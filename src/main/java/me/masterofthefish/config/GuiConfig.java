package me.masterofthefish.config;

import me.masterofthefish.Manhunt;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

public class GuiConfig {

    private final Manhunt plugin;
    private Material gameDisplayMaterialWaiting;
    private Material gameDisplayMaterialActive;
    private String title;
    private boolean fillBorder;
    private Material borderType;
    private final Map<Integer, Material> items = new HashMap<>();

    public GuiConfig(final Manhunt plugin) {
        this.plugin = plugin;
        this.plugin.saveResource("menu.yml", false);
        load();
    }

    public void load() {
        final File file = Paths.get(plugin.getDataFolder().getPath(), "menu.yml").toFile();
        final YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
        this.title = config.getString("title");
        this.fillBorder = config.getBoolean("fill-border");
        final String borderString = config.getString("border-type");
        final String gameWaitingString = config.getString("game-waiting-material");
        final String gameActiveString = config.getString("game-active-material");
        try {
            this.borderType = Material.valueOf(borderString);
            this.gameDisplayMaterialWaiting = Material.valueOf(gameWaitingString);
            this.gameDisplayMaterialActive = Material.valueOf(gameActiveString);
        } catch (final IllegalArgumentException exception) {
            if(this.borderType == null) {
                this.borderType = Material.BLACK_STAINED_GLASS_PANE;
            }
            if(this.gameDisplayMaterialWaiting == null) {
                this.gameDisplayMaterialWaiting = Material.EMERALD_BLOCK;
            }
            if(this.gameDisplayMaterialActive == null) {
                this.gameDisplayMaterialActive = Material.REDSTONE_BLOCK;
            }
        }
        if(title != null) {
            this.title = ChatColor.translateAlternateColorCodes('&', title);
        } else {
            this.title = "Games";
        }
        final ConfigurationSection itemSection = config.getConfigurationSection("items");
        if(itemSection != null) {
            for (String key : itemSection.getKeys(false)) {
                try {
                    final int value = Integer.parseInt(key);
                    String materialValue = itemSection.getString(key);
                    if(materialValue == null) {
                        items.put(value, Material.AIR);
                        continue;
                    }
                    try {
                        items.put(value, Material.valueOf(materialValue));
                    } catch (final IllegalArgumentException exception) {
                        items.put(value, Material.AIR);
                    }
                } catch (final NumberFormatException ignored) {

                }
            }
        }
    }

    public String getTitle() {
        return title;
    }

    public boolean fillBorder() {
        return fillBorder;
    }

    public Material getBorderType() {
        return borderType;
    }

    public Map<Integer, Material> getItems() {
        return items;
    }

    public Material getGameDisplayMaterialWaiting() {
        return gameDisplayMaterialWaiting;
    }

    public Material getGameDisplayMaterialActive() {
        return gameDisplayMaterialActive;
    }
}