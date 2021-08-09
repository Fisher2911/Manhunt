package me.masterofthefish.manhunt.listeners;

import me.masterofthefish.manhunt.Manhunt;
import me.masterofthefish.manhunt.game.Game;
import me.masterofthefish.manhunt.user.UserManager;
import me.masterofthefish.manhunt.user.ManhuntUser;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

public class BlockBreakListener implements Listener {

    private final Manhunt plugin;
    private final UserManager userManager;

    public BlockBreakListener(final Manhunt plugin) {
        this.plugin = plugin;
        this.userManager = plugin.getUserManager();
    }

    @EventHandler
    public void onBlockBreak(final BlockBreakEvent event) {
        final Player player = event.getPlayer();
        final ManhuntUser user = userManager.getUser(player.getUniqueId());
        if(user == null) {
            return;
        }
        final Game game = user.getCurrentGame();
        if(game == null) {
            return;
        }
        if(game.isCountdownActive()) {
            event.setCancelled(true);
        }
    }
}
