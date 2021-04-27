package me.masterofthefish;

import com.onarandombox.MultiverseCore.MultiverseCore;
import me.masterofthefish.commands.ManhuntCommand;
import me.masterofthefish.config.GuiConfig;
import me.masterofthefish.config.Lang;
import me.masterofthefish.config.Settings;
import me.masterofthefish.game.GameManager;
import me.masterofthefish.listeners.BlockBreakListener;
import me.masterofthefish.listeners.EntityDeathListener;
import me.masterofthefish.listeners.ItemClickListener;
import me.masterofthefish.listeners.PlayerJoinListener;
import me.masterofthefish.saving.Database;
import me.masterofthefish.saving.SQLiteDatabase;
import me.masterofthefish.user.UserManager;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;

public class Manhunt extends JavaPlugin {

    private Database database;
    private Settings settings;
    private UserManager userManager;
    private GameManager gameManager;
    private GuiConfig guiConfig;
    private MultiverseCore multiverseCore;

    @Override
    public void onEnable() {
        init();
    }

    @Override
    public void onDisable() {
        shutdown();
    }

    public void shutdown() {
        database.saveAll();
        database.close();
    }

    // Order Matters
    private void init() {
        this.userManager = new UserManager(this);
        database = new SQLiteDatabase(this);
        this.settings = new Settings(this);
        this.guiConfig = new GuiConfig(this);
        this.gameManager = new GameManager(this);
        this.multiverseCore = (MultiverseCore) Bukkit.getServer().getPluginManager().getPlugin("Multiverse-Core");
        Lang.load(this);
        registerCommands();
        registerListeners();
        database.loadOnlinePlayers();
    }

    private void registerCommands() {
        getCommand("manhunt").setExecutor(new ManhuntCommand(this));
    }

    private void registerListeners() {
        List.of(new PlayerJoinListener(this), new ItemClickListener(this),
                new EntityDeathListener(this), new BlockBreakListener(this)).
                forEach(listener -> getServer().getPluginManager().registerEvents(listener, this));
    }

    public Settings getSettings() {
        return this.settings;
    }

    public UserManager getUserManager() {
        return this.userManager;
    }

    public GuiConfig getGuiConfig() {
        return this.guiConfig;
    }

    public GameManager getGameManager() {
        return this.gameManager;
    }

    public Database getDatabase() {
        return database;
    }

    public MultiverseCore getMultiverseCore() {
        return this.multiverseCore;
    }
}