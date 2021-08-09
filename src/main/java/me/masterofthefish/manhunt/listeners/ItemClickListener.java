package me.masterofthefish.manhunt.listeners;

import me.masterofthefish.manhunt.Manhunt;
import me.masterofthefish.manhunt.game.Game;
import me.masterofthefish.manhunt.user.UserManager;
import me.masterofthefish.manhunt.user.ManhuntUser;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

public class ItemClickListener implements Listener {

    private final Manhunt plugin;
    private final UserManager userManager;

    public ItemClickListener(final Manhunt plugin) {
        this.plugin = plugin;
        this.userManager = plugin.getUserManager();
    }

    @EventHandler
    public void onItemHold(final PlayerItemHeldEvent event) {
        final Player player = event.getPlayer();
        final ManhuntUser user = userManager.getUser(player.getUniqueId());
        if(user == null) {
            return;
        }
        final Game currentGame = user.getCurrentGame();
        if(currentGame == null) {
            return;
        }
        final ItemStack swappedTo = player.getInventory().getItem(event.getNewSlot());
        if(swappedTo == null) {
            return;
        }
        final ItemMeta itemMeta = swappedTo.getItemMeta();
        if(itemMeta == null) {
            return;
        }
        final NamespacedKey key = new NamespacedKey(plugin, "compass");
        if(!itemMeta.getPersistentDataContainer().has(key, PersistentDataType.BYTE)) {
            return;
        }
        player.setCompassTarget(currentGame.getSpeedRunner().getPlayer().getLocation());
    }

}
