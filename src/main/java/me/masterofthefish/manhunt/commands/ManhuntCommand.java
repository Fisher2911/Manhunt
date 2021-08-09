package me.masterofthefish.manhunt.commands;

import me.masterofthefish.manhunt.Manhunt;
import me.masterofthefish.manhunt.config.Lang;
import me.masterofthefish.manhunt.game.GameManager;
import me.masterofthefish.manhunt.user.UserManager;
import me.masterofthefish.manhunt.user.ManhuntUser;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class ManhuntCommand implements CommandExecutor {

    private final Manhunt plugin;
    private final UserManager userManager;
    private final GameManager gameManager;

    public ManhuntCommand(Manhunt plugin) {
        this.plugin = plugin;
        this.userManager = plugin.getUserManager();
        this.gameManager = plugin.getGameManager();
    }

    @Override
    public boolean onCommand(@NotNull final CommandSender sender, @NotNull final Command command, @NotNull final String label, @NotNull final String[] args) {
        if(!(sender instanceof Player)) {
            return true;
        }
        final Player player = (Player) sender;
        final UUID uuid = player.getUniqueId();
        final ManhuntUser user = userManager.getUser(uuid);
        if(user == null) {
            return true;
        }
        if(user.getCurrentGame() != null) {
            player.sendMessage(Lang.alreadyInGame());
        }
        gameManager.getGui(user).open(player);
        return true;
    }
}