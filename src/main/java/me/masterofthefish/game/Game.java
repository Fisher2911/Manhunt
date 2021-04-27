package me.masterofthefish.game;

import com.onarandombox.MultiverseCore.MultiverseCore;
import com.onarandombox.MultiverseCore.api.MVWorldManager;
import me.masterofthefish.Manhunt;
import me.masterofthefish.config.Lang;
import me.masterofthefish.config.Settings;
import me.masterofthefish.user.ManhuntUser;
import me.masterofthefish.user.Role;
import me.mattstudios.mfgui.gui.components.ItemBuilder;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.*;

public class Game {

    private final Manhunt plugin;
    private final Settings settings;
    private final List<ManhuntUser> users = new ArrayList<>();
    final Map<ManhuntUser, Location> usersStartLocation = new HashMap<>();
    private Location speedRunnerStartLocation;
    private String world;
    private boolean active;
    private boolean countdownActive;

    private BukkitTask playerChecker;
    private Role winner;

    public Game(final Manhunt plugin, final String world, final boolean active) {
        this.plugin = plugin;
        this.settings = plugin.getSettings();
        this.world = world;
        this.active = active;
        setPlayerChecker();
    }

    private void setPlayerChecker() {
        this.playerChecker = new BukkitRunnable() {
            double countdownTimeLeft = settings.getCountdownTime();
            @Override
            public void run() {
                if(!active) {
                    countdownTimeLeft = settings.getCountdownTime();
                    return;
                }
                if(countdownTimeLeft > 0) {
                    for(final ManhuntUser user : users) {
                        if(!user.isOnline() || user.getRole() == Role.SPEED_RUNNER) {
                            continue;
                        }
                        final Player player = user.getPlayer();
                        final Location startLocation = usersStartLocation.get(user);
                        if(compareLocations(startLocation,
                                player.getLocation()) >= 1) {
                            player.teleport(startLocation);
                        }
                        Lang.getCountdownTitle().sendTitle(player, "%time%", Math.round(countdownTimeLeft));
                    }
                    countdownTimeLeft-=0.25;
                    return;
                }
                countdownActive = false;
            }
        }.runTaskTimer(plugin, 5, 5);
    }

    public void start() {
        this.active = true;
        this.countdownActive = true;
        final Random random = new Random();

        final ManhuntUser speedRunner = users.get(random.nextInt(users.size()));
        speedRunner.setRole(Role.SPEED_RUNNER);
        speedRunner.getPlayer().sendMessage(Lang.sendRole(Role.SPEED_RUNNER.toString().
                toLowerCase().replace('_', ' ')));
        speedRunnerStartLocation = getFromCircle(0);
        teleportPlayer(speedRunner, speedRunnerStartLocation);

        int userNum = 1;
        for(ManhuntUser manhuntUser : users) {
            if(manhuntUser.equals(speedRunner)) {
                continue;
            }
            manhuntUser.setRole(Role.HUNTER);
            final Player player = manhuntUser.getPlayer();
            if(!manhuntUser.isOnline()) {
                continue;
            }
            player.sendMessage(Lang.sendRole(Role.HUNTER.toString().
                    toLowerCase().replace('_', ' ')));
            teleportPlayer(manhuntUser, getFromCircle(userNum));
            userNum++;
            final ItemStack compassItem = ItemBuilder.from(Material.COMPASS).
                    setName(Lang.trackerName()).build();
            final ItemMeta itemMeta = compassItem.getItemMeta();
            final NamespacedKey key = new NamespacedKey(plugin, "compass");
            itemMeta.getPersistentDataContainer().set(key, PersistentDataType.BYTE, (byte) 1);
            compassItem.setItemMeta(itemMeta);
            player.getInventory().addItem(compassItem);
            player.setCompassTarget(speedRunnerStartLocation);
        }
    }

    private Location getFromCircle(final int num) {
            final double angle = Math.toRadians(((double) num /5) * 360d);
            final int radius = 10;
            final double x = Math.cos(angle) * radius;
            final double z = Math.sin(angle) * radius;
            final World world = Bukkit.getWorld(this.world);
            if(world == null) {
                return null;
            }
            return new Location(world, x, world.getHighestBlockYAt((int) x, (int) z), z);
    }

    private void teleportPlayer(final ManhuntUser user, final Location location) {
        usersStartLocation.put(user, location);
        if(user.isOnline()) {
            final Player player = user.getPlayer();
           player.teleport(location);
           player.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 200, 255));
        }
    }

    private double compareLocations(final Location loc1, final Location loc2) {
        final Location clone1 = loc1.clone();
        clone1.setY(0);
        final Location clone2 = loc2.clone();
        clone2.setY(0);
        return clone1.distance(clone2);
    }

    public void kill(final ManhuntUser user) {
        if(user.getRole() == Role.SPEED_RUNNER) {
            end(Role.HUNTER);
        }
    }

    private void sendPlayerToSpawn(final ManhuntUser user) {
        if(!user.isOnline()) {
            return;
        }
        user.getPlayer().teleport(settings.getSpawnLocation());
    }

    public void end(final Role winner) {
        this.winner = winner;
        users.forEach(user -> {
            if(!user.isOnline()) {
                return;
            }
            final Role role = user.getRole();
            final Player player = user.getPlayer();
            if(Game.this.winner == role) {
                Lang.getWinTitle().sendTitle(player);
                user.addWin();
            } else {
                Lang.getLoseTitle().sendTitle(player);
                user.addLoss();
            }
            sendPlayerToSpawn(user);
            user.setCurrentGame(null);
        });
        final List<ManhuntUser> previousUsers = new ArrayList<>(this.users);
        this.users.clear();
        final MultiverseCore core = plugin.getMultiverseCore();
        final MVWorldManager worldManager = core.getMVWorldManager();
        worldManager.regenWorld(world, false, true, null);
        worldManager.deleteWorld(world + "_nether");
        worldManager.deleteWorld(world + "_the_end");
        this.active = false;
        plugin.getGameManager().onGameEnd(this, previousUsers);
    }

    public void addPlayer(final ManhuntUser user) {
        if(this.users.size() <= 4) {
            this.users.add(user);
            user.setCurrentGame(this);
            if(this.users.size() == 5) {
                start();
            }
            return;
        }
        throw new GameFullException("Error: Game full");
    }

    public boolean isActive() {
        return this.active;
    }

    public int getPlayersWaiting() {
        return this.users.size();
    }

    public boolean isFull() {
        return this.users.size() >= 5;
    }

    public World getWorld() {
        return Bukkit.getWorld(world);
    }

    public void removeUser(final ManhuntUser user) {
        if(!isActive()) {
            this.users.remove(user);
        } else {
            throw new IllegalStateException("Cannot remove a player while they are in a game!");
        }
    }

    public Location getSpeedRunnerStartLocation() {
        return speedRunnerStartLocation;
    }

    public boolean isCountdownActive() {
        return this.countdownActive;
    }

    public ManhuntUser getSpeedRunner() {
        for(final ManhuntUser user : this.users) {
            if(user.getRole() == Role.SPEED_RUNNER) {
                return user;
            }
        }
        return null;
    }

    @Override
    public String toString() {
        return "Game{" +
                "plugin=" + this.plugin +
                ", settings=" + this.settings +
                ", users=" + this.users +
                ", world=" + this.world +
                ", active=" + this.active +
                ", winner=" + this.winner +
                '}';
    }
}