package me.masterofthefish.listeners;

import me.masterofthefish.Manhunt;
import me.masterofthefish.game.Game;
import me.masterofthefish.saving.Database;
import me.masterofthefish.user.ManhuntUser;
import me.masterofthefish.user.Role;
import me.masterofthefish.user.UserManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.UUID;

public class PlayerLeaveListener implements Listener {

    private final Manhunt plugin;
    private final Database database;
    private final UserManager userManager;

    private PlayerLeaveListener(final Manhunt plugin) {
        this.plugin = plugin;
        this.database = plugin.getDatabase();
        this.userManager = plugin.getUserManager();
    }

    @EventHandler
    public void onPlayerLeave(final PlayerQuitEvent event) {
        final Player player = event.getPlayer();
        final UUID uuid = player.getUniqueId();
        final ManhuntUser user = userManager.getUser(uuid);
        if(user == null) {
            return;
        }
        final Role role = user.getRole();
        if(user.getCurrentGame() == null) {
            return;
        }
        final Game game = user.getCurrentGame();
        new BukkitRunnable() {
            int timePassed = 0;
            final int timeAllowed = plugin.getSettings().getLogoutTimeLimit();
            @Override
            public void run() {
                timePassed++;
                if(timePassed > timeAllowed) {
                    if(role == Role.SPEED_RUNNER) {
                        game.end(Role.SPEED_RUNNER);
                        cancel();
                        return;
                    }
                }
                if(!game.isActive()) {
                    cancel();
                }
            }
        }.runTaskTimer(plugin,20, 20);
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () ->database.save(user));
    }
}
