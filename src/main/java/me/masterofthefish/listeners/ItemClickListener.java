package me.masterofthefish.listeners;

import me.masterofthefish.Manhunt;
import me.masterofthefish.game.Game;
import me.masterofthefish.user.ManhuntUser;
import me.masterofthefish.user.UserManager;
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
