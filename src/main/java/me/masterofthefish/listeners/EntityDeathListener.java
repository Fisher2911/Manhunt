package me.masterofthefish.listeners;

import me.masterofthefish.Manhunt;
import me.masterofthefish.game.Game;
import me.masterofthefish.game.GameManager;
import me.masterofthefish.user.ManhuntUser;
import me.masterofthefish.user.Role;
import me.masterofthefish.user.UserManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;

import java.util.UUID;

public class EntityDeathListener implements Listener {

    private final Manhunt plugin;
    private final UserManager userManager;
    private final GameManager gameManager;

    public EntityDeathListener(final Manhunt plugin) {
        this.plugin = plugin;
        this.userManager = plugin.getUserManager();
        this.gameManager = plugin.getGameManager();
    }

    @EventHandler
    public void onEntityDeath(final EntityDeathEvent event) {
        final Entity entity = event.getEntity();
        final Game game = gameManager.getGameFromWorld(entity.getWorld());
        if(game == null) {
            return;
        }
        if(entity instanceof Player) {
            final UUID uuid = entity.getUniqueId();
            final Player player = (Player) entity;
            player.spigot().respawn();
            final ManhuntUser user = userManager.getUser(uuid);
            if(user == null) {
                return;
            }
            game.kill(user);
            return;
        }
        if (entity.getType() == EntityType.ENDER_DRAGON) {
            game.end(Role.SPEED_RUNNER);
        }
    }
}