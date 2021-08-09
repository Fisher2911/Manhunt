package me.masterofthefish.manhunt.saving;

import me.masterofthefish.manhunt.Manhunt;
import me.masterofthefish.manhunt.user.ManhuntUser;
import me.masterofthefish.manhunt.user.UserManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.io.File;
import java.sql.*;
import java.util.List;
import java.util.UUID;

// todo
public class SQLiteDatabase implements Database {

    private final Manhunt plugin;
    private final UserManager userManager;
    private Connection conn;
    private final String TABLE_NAME = "user";
    private final String COLUMN_UUID = "uuid";
    public final String COLUMN_WINS = "wins";
    public final String COLUMN_LOSSES = "losses";

    public SQLiteDatabase(final Manhunt plugin) {
        this.plugin = plugin;
        this.userManager = plugin.getUserManager();
        setupTables();
    }

    private Connection getConnection() {
        if(this.conn != null) {
            return this.conn;
        }
        try {
            this.conn = DriverManager.getConnection("jdbc:sqlite:" + new File(plugin.getDataFolder(), "users.db"));
            return conn;
        } catch(SQLException e) {
            plugin.getLogger().warning("Error accessing sqlite connection: " + e.getMessage());
            plugin.getLogger().warning("Shutting down...");
            e.printStackTrace();
            Bukkit.getPluginManager().disablePlugin(plugin);
        }
        return null;
    }

    private void setupTables() {
        try(final PreparedStatement statement =
                    getConnection().prepareStatement("CREATE TABLE IF NOT EXISTS " +
                            TABLE_NAME +
                            " (" + COLUMN_UUID + " TEXT, " +
                            COLUMN_WINS + " INTEGER, " +
                            COLUMN_LOSSES + " INTEGER, " +
                            "UNIQUE(uuid))")) {
            statement.execute();
        } catch(SQLException exception) {
            plugin.getLogger().warning("Error setting up database tables");
            exception.printStackTrace();
        }
    }

    @Override
    public void save(final ManhuntUser user) {
        final UUID uuid = user.getUuid();
        final int wins = user.getWins();
        final int losses = user.getLosses();
        try(final PreparedStatement statement =
                    getConnection().prepareStatement("INSERT INTO " +
                            TABLE_NAME + "(" +
                            COLUMN_UUID + ", " +
                            COLUMN_WINS + ", " +
                            COLUMN_LOSSES + ") " +
                            "VALUES ('" +
                            uuid + "', '" +
                            wins + "', '" +
                            losses + "') " +
                            "ON CONFLICT (" +
                            COLUMN_UUID + ") DO UPDATE SET " +
                            COLUMN_WINS + "='" + wins + "', " +
                            COLUMN_LOSSES + "='" + losses + "'")) {
            statement.executeUpdate();
        } catch(final SQLException e) {
            plugin.getLogger().warning("Error saving user with uuid : " + user.getUuid());
            e.printStackTrace();
        }
    }

    @Override
    public ManhuntUser load(final UUID uuid) {
        ManhuntUser user;
        try(final PreparedStatement statement = getConnection().prepareStatement(
                "SELECT * FROM " + TABLE_NAME + " WHERE " +
                        COLUMN_UUID + "='" + uuid + "'");
            final ResultSet results = statement.executeQuery()) {
            if(!results.next()) {
                user = new ManhuntUser(uuid, 0, 0);
            } else {
                final int wins = results.getInt(COLUMN_WINS);
                final int losses = results.getInt(COLUMN_LOSSES);
                user = new ManhuntUser(uuid, wins, losses);
            }
        } catch(final SQLException  exception) {
            plugin.getLogger().warning("Error loading user with uuid : " + uuid);
            exception.printStackTrace();
            user = new ManhuntUser(uuid, 0, 0);
        }
        userManager.setUser(uuid, user);
        return user;
    }

    @Override
    public ManhuntUser load(final Player player) {
        return load(player.getUniqueId());
    }

    @Override
    public List<ManhuntUser> getTopTen() {
        return null;
    }

    @Override
    public void saveAll() {
        for(final Player player : Bukkit.getOnlinePlayers()) {
            final ManhuntUser user = userManager.getUser(player.getUniqueId());
            if(user == null) {
                continue;
            }
            save(user);
        }
    }

    @Override
    public void loadOnlinePlayers() {
        for(final Player player : Bukkit.getOnlinePlayers()) {
            load(player);
        }
    }

    @Override
    public void close() {
        try {
            conn.close();
        } catch(final SQLException exception) {
            plugin.getLogger().warning("Error shutting down database");
        }
    }
}
