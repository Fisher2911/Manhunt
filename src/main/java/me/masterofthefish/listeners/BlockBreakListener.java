package me.masterofthefish.listeners;

import me.masterofthefish.Manhunt;
import me.masterofthefish.game.Game;
import me.masterofthefish.user.ManhuntUser;
import me.masterofthefish.user.UserManager;
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
