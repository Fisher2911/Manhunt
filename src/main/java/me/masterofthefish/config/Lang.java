package me.masterofthefish.config;

import me.masterofthefish.Manhunt;
import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.nio.file.Paths;

public class Lang {

    private static TitleSettings winTitle = new TitleSettings("Winner", "Winner", 1, 200, 1);
    private static TitleSettings loseTitle = new TitleSettings("Loser", "Loser", 1, 200, 1);;
    private static TitleSettings countdownTitle = new TitleSettings("Starting in %time%", "Starting in %time%", 1, 40, 1);

    private static String alreadyInGame = "&aAlready In Game";
    private static String gameWaiting = "&aGame Waiting";
    private static String gameStarted = "&aGame Started";
    private static String totalPlayersWaiting = "&aTotal Players Waiting %waiting%";
    private static String alreadyInQueue = "&aPlayer Already in Queue";
    private static String sendRole = "&aYour role is %role%";
    private static String trackerName = "&aTracker Name";

    public static void load(final Manhunt plugin) {
        final File file = Paths.get(plugin.getDataFolder().getPath(), "lang.yml").toFile();
        final YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
        final String Titles = "titles";
        final ConfigurationSection section = config.getConfigurationSection(Titles);
        if(section != null) {
            final String winner = "winner";
            winTitle = getTitleFromConfig(section, winner);
            final String loser = "loser";
            loseTitle = getTitleFromConfig(section, loser);
            final String countdown = "countdown";
            countdownTitle = getTitleFromConfig(section, countdown);
        }
        alreadyInGame = getNotNull(alreadyInGame, config.getString("already-in-game"));
        gameWaiting = getNotNull(gameWaiting, config.getString("game-waiting"));
        gameStarted = getNotNull(gameStarted, config.getString("game-started"));
        totalPlayersWaiting = getNotNull(totalPlayersWaiting, config.getString("total-players-waiting"));
        alreadyInQueue = getNotNull(alreadyInQueue, config.getString("already-in-queue"));
        sendRole = getNotNull(sendRole, config.getString("send-role"));
        trackerName = getNotNull(trackerName, config.getString("tracker-name"));
    }

    private static TitleSettings getTitleFromConfig(final ConfigurationSection section, String key) {
        String title = section.getString(key + ".title");
        String subTitle = section.getString(key + ".sub-title");
        final int fadeIn = section.getInt(key + ".fade-in");
        final int stay = section.getInt(key + ".stay");
        final int fadeOut = section.getInt(key + ".fade-out");
        if(title != null) {
            title = color(title);
        }
        if(subTitle != null) {
            subTitle = color(subTitle);
        }
        return new TitleSettings(title, subTitle, fadeIn, stay, fadeOut);
    }

    public static class TitleSettings {
        public final String title;
        public final String subTitle;
        public final int fadeIn;
        public final int stay;
        public final int fadeOut;

        public TitleSettings(final String title, final String subTitle, final int fadeIn, final int stay, final int fadeOut) {
            this.title = title;
            this.subTitle = subTitle;
            this.fadeIn = fadeIn;
            this.stay = stay;
            this.fadeOut = fadeOut;
        }

        public void sendTitle(final Player player) {
            sendTitle(player, "", "");
        }

        public void sendTitle(final Player player, final String replace, final Object object) {
            player.sendTitle(title.replace(replace, object.toString()), subTitle.replace(replace, object.toString()),
                    fadeIn, stay, fadeOut);
        }
    }

    public static TitleSettings getWinTitle() {
        return winTitle;
    }
    public static TitleSettings getLoseTitle() {
        return loseTitle;
    }

    public static TitleSettings getCountdownTitle() {
        return countdownTitle;
    }

    public static String alreadyInGame() {
        return color(alreadyInGame);
    }
    public static String gameWaiting() {
        return color(gameWaiting);
    }
    public static String gameStarted() {
        return color(gameStarted);
    }
    public static String totalPlayersWaiting(final int waiting) {
        return color(totalPlayersWaiting.replace("%waiting%", String.valueOf(waiting)));
    }
    public static String alreadyInQueue() {
        return alreadyInQueue;
    }
    public static String sendRole(final String string) {
        return color(sendRole.replace("%role%", string));
    }
    public static String trackerName() {
        return color(trackerName);
    }
    private static String color(final String string) {
        return ChatColor.translateAlternateColorCodes('&', string);
    }

    private static String getNotNull(final String string, final String setTo) {
        return setTo != null ? setTo : string;
    }
}