package me.masterofthefish.manhunt;

import com.onarandombox.MultiverseCore.MultiverseCore;
import me.masterofthefish.manhunt.commands.ManhuntCommand;
import me.masterofthefish.manhunt.config.GuiConfig;
import me.masterofthefish.manhunt.config.Lang;
import me.masterofthefish.manhunt.config.Settings;
import me.masterofthefish.manhunt.game.GameManager;
import me.masterofthefish.manhunt.listeners.BlockBreakListener;
import me.masterofthefish.manhunt.listeners.EntityDeathListener;
import me.masterofthefish.manhunt.listeners.ItemClickListener;
import me.masterofthefish.manhunt.listeners.PlayerJoinListener;
import me.masterofthefish.manhunt.saving.Database;
import me.masterofthefish.manhunt.saving.SQLiteDatabase;
import me.masterofthefish.manhunt.user.UserManager;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;

import java.util.List;

public class Manhunt extends JavaPlugin {

    private Database database;
    private Settings settings;
    private UserManager userManager;
    private GameManager gameManager;
    private GuiConfig guiConfig;
    private MultiverseCore multiverseCore;
    private BukkitTask saveTask;

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
        this.settings = new Settings(this);
        database = new SQLiteDatabase(this);
        this.guiConfig = new GuiConfig(this);
        this.gameManager = new GameManager(this);
        this.multiverseCore = (MultiverseCore) Bukkit.getServer().getPluginManager().getPlugin("Multiverse-Core");
        Lang.load(this);
        registerCommands();
        registerListeners();
        database.loadOnlinePlayers();
        this.saveTask = Bukkit.getScheduler().runTaskTimerAsynchronously(this, () -> {
            database.saveAll();
        }, 6_000, 6_000);
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