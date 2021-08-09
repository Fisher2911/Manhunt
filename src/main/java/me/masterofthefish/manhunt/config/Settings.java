package me.masterofthefish.manhunt.config;

import me.masterofthefish.manhunt.Manhunt;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Settings {

    private final Manhunt plugin;

    // For releasing of hunters/speedrunners
    private int teleportDelay;
    private int distanceBetweenPlayers;
    private Location spawnLocation;
    private int minXTeleport;
    private int minZTeleport;
    private int maxXTeleport;
    private int maxZTeleport;
    private int logoutTimeLimit;
    private int countdownTime;

    private final List<String> worlds = new ArrayList<>();


    private final FileConfiguration config;

    public Settings(final Manhunt plugin) {
        this.plugin = plugin;
        this.config = plugin.getConfig();
        this.plugin.saveDefaultConfig();
        Bukkit.getScheduler().runTaskLater(plugin, this::load, 1);
    }

    public void load() {
        final String settings = "settings";
        this.teleportDelay = config.getInt(settings + "teleport-delay");
        this.minXTeleport = config.getInt(settings + "min-x-teleport");
        this.minZTeleport = config.getInt(settings + "min-z-teleport");
        this.maxXTeleport = config.getInt(settings + "max-x-teleport");
        this.maxZTeleport = config.getInt(settings + "max-z-teleport");
        this.logoutTimeLimit = config.getInt(settings + "logout-time-limit");
        this.countdownTime = config.getInt(settings + "countdown-time");
        final String location = "location";
        final String worldName = config.getString(location + "." + "world");
        final int x = config.getInt(location + "." + "x");
        final int y = config.getInt(location + "." + "y");
        final int z = config.getInt(location + "." + "z");
        worlds.addAll(config.getStringList("worlds"));
        if(worldName == null) {
            plugin.getLogger().severe("World name is null for spawn location: Shutting Down");
            Bukkit.getPluginManager().disablePlugin(plugin);
            return;
        }
        this.spawnLocation = new Location(Bukkit.getWorld(worldName), x, y, z);
    }

    public int getTeleportDelay() {
        return this.teleportDelay;
    }

    public int getMinXTeleport() {
        return this.minXTeleport;
    }

    public int getMinZTeleport() {
        return this.minZTeleport;
    }

    public int getMaxXTeleport() {
        return this.maxXTeleport;
    }

    public int getMaxZTeleport() {
        return this.maxZTeleport;
    }

    public int getLogoutTimeLimit() { return this.logoutTimeLimit; }

    public int getCountdownTime() {
        return this.countdownTime;
    }

    public Location getSpawnLocation() {
        return this.spawnLocation;
    }

    public List<String> getWorlds() {
        return Collections.unmodifiableList(this.worlds);
    }
}
