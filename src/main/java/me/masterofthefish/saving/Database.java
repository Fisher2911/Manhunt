package me.masterofthefish.saving;

import me.masterofthefish.user.ManhuntUser;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.UUID;

public interface Database {

    void save(final ManhuntUser user);
    ManhuntUser load(final UUID uuid);
    ManhuntUser load(final Player player);
    List<ManhuntUser> getTopTen();
    void saveAll();
    void loadOnlinePlayers();
    void close();

}