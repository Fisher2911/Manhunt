package me.masterofthefish.listeners;

import me.masterofthefish.Manhunt;
import me.masterofthefish.saving.Database;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class PlayerJoinListener implements Listener {

    private final Manhunt plugin;
    private final Database database;

    public PlayerJoinListener(final Manhunt plugin) {
        this.plugin = plugin;
        this.database = plugin.getDatabase();
    }

    @EventHandler
    public void onJoin(final PlayerJoinEvent event) {
        final Player player = event.getPlayer();
        database.load(player);
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> database.load(player));
    }
}