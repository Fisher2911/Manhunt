package me.masterofthefish.game;

import me.masterofthefish.Manhunt;
import me.masterofthefish.config.GuiConfig;
import me.masterofthefish.config.Lang;
import me.masterofthefish.config.Settings;
import me.masterofthefish.user.ManhuntUser;
import me.masterofthefish.user.UserManager;
import me.mattstudios.mfgui.gui.components.ItemBuilder;
import me.mattstudios.mfgui.gui.guis.BaseGui;
import me.mattstudios.mfgui.gui.guis.Gui;
import me.mattstudios.mfgui.gui.guis.GuiItem;
import me.mattstudios.mfgui.gui.guis.PaginatedGui;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.HumanEntity;
import org.bukkit.inventory.ItemStack;

import java.util.*;

public class GameManager {

    private final Manhunt plugin;
    private final List<Game> games = new LinkedList<>();
    private final Map<Game, LinkedList<ManhuntUser>> queuedUsers = new HashMap<>();
    private final UserManager userManager;
    private final Map<GuiItem, Game> itemGameMap = new HashMap<>();
    private final Map<ManhuntUser, BaseGui> userGuis = new HashMap<>();

    public GameManager(final Manhunt plugin) {
        this.plugin = plugin;
        this.userManager = plugin.getUserManager();
        Bukkit.getScheduler().runTaskLater(plugin, this::setupGames, 20);
    }

    private void setupGames() {
        final Settings settings = plugin.getSettings();
        for(final String worldName : settings.getWorlds()) {
            final Game game = new Game(plugin, worldName, false);
            this.games.add(game);
            this.queuedUsers.put(game, new LinkedList<>());
        }
    }


    public BaseGui getGui(final ManhuntUser manhuntUser) {
        if(!manhuntUser.isOnline()) {
            return new Gui("Not Online");
        }
        final BaseGui playerGui = userGuis.get(manhuntUser);
        if(playerGui != null) {
            for(final Map.Entry<GuiItem, Game> entry : itemGameMap.entrySet()) {
                entry.getKey().setItemStack(getDisplayItem(entry.getValue(), manhuntUser));
            }
            playerGui.update();
            playerGui.open(manhuntUser.getPlayer());
            return playerGui;
        }
        final GuiConfig guiConfig = plugin.getGuiConfig();
        final BaseGui gui = new PaginatedGui(3, guiConfig.getTitle());
        gui.setDefaultClickAction(event -> event.setCancelled(true));
        if(guiConfig.fillBorder()) {
            gui.getFiller().fillBorder(ItemBuilder.from(guiConfig.getBorderType()).setName(" ").asGuiItem());
        }
        for(final Map.Entry<Integer, Material> entry : guiConfig.getItems().entrySet()) {
            final int slot = entry.getKey();
            final Material material = entry.getValue();
            gui.setItem(slot, ItemBuilder.from(material).setName(" ").asGuiItem());
        }
        for(final Game game : this.games) {
            final GuiItem guiItem = ItemBuilder.from(getDisplayItem(game, manhuntUser)).asGuiItem();
            guiItem.setAction(event -> {
                final HumanEntity clicker = event.getWhoClicked();
                final UUID uuid = clicker.getUniqueId();
                final ManhuntUser user = userManager.getUser(uuid);
                if (user == null) {
                    return;
                }
                if (user.isInGame()) {
                    user.setCurrentGame(null);
                    removeFromQueue(user, game);
//                    clicker.sendMessage(Lang.alreadyInQueue());
                    updateMenu(gui, guiItem, game, manhuntUser);
                    return;
                }
                addToQueue(user, game);
                updateMenu(gui, guiItem, game, manhuntUser);
            });
            gui.addItem(guiItem);
            itemGameMap.put(guiItem, game);
        }
        return gui;
    }

    private void updateMenu(final BaseGui gui, final GuiItem guiItem, final Game game, final ManhuntUser user) {
        guiItem.setItemStack(getDisplayItem(game, user));
        gui.update();
    }

    private ItemStack getDisplayItem(final Game game, final ManhuntUser user) {
        final GuiConfig config = plugin.getGuiConfig();
        final Material activeMaterial = config.getGameDisplayMaterialActive();
        final Material waitingMaterial = config.getGameDisplayMaterialWaiting();
        final boolean isActive = game.isActive();
        return ItemBuilder.from(isActive ? activeMaterial : waitingMaterial).
                setName(isActive ? Lang.gameStarted() : Lang.gameWaiting()).
                setLore("", isActive ? ChatColor.RED + "In Game" :
                        Lang.totalPlayersWaiting(game.getPlayersWaiting()),
                        "",
                        game.equals(user.getCurrentGame()) ? ChatColor.AQUA + "Waiting" :
                        queuedUsers.get(game).contains(user) ? ChatColor.AQUA +
                        "In Queue" : "").build();
    }

    public Game getGameFromWorld(final World world) {
        for(final Game game : games) {
            if(game.getWorld().equals(world) ||
                    world.getName().
                            replace("_the_end", "").
                            replace("_nether", "").
                            equals(game.getWorld().getName())) {
                return game;
            }
        }
        return null;
    }

    public void onGameEnd(final Game game, final List<ManhuntUser> previousUsers) {
        final LinkedList<ManhuntUser> users = queuedUsers.get(game);
        for(final ManhuntUser user : previousUsers) {
            users.remove(user);
        }
        Collections.sort(users);
        int i = 0;
        while(i < 5) {
            final ManhuntUser user = users.poll();
            if(user == null) {
                break;
            }
            game.addPlayer(user);
            i++;
        }
    }

    /**
     * @return false if player was not added to the queue
     */
    public boolean addToQueue(final ManhuntUser user, final Game game) {
            if(game.isActive() || game.isFull()) {
                queuedUsers.get(game).add(user);
                return false;
            }
            game.addPlayer(user);
            return true;
    }

    public void removeFromQueue(final ManhuntUser user, final Game game) {
        queuedUsers.get(game).remove(user);
        game.removeUser(user);
    }

//    public void checkQueue() {
//        if(queuedUsers.isEmpty()) {
//            return;
//        }
//        for(final Game game : games) {
//            while(!game.isActive() && !game.isFull() && !queuedUsers.isEmpty()) {
//                final ManhuntUser user = queuedUsers.poll();
//                if(user == null) {
//                    continue;
//                }
//                game.addPlayer(user);
//            }
//        }
//    }
}