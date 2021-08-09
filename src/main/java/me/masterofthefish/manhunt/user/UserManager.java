package me.masterofthefish.manhunt.user;

import me.masterofthefish.manhunt.Manhunt;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class UserManager {

    private final Manhunt plugin;

    private final Map<UUID, ManhuntUser> users = new HashMap<>();

    public UserManager(Manhunt plugin) {
        this.plugin = plugin;
    }

    public ManhuntUser getUser(final UUID uuid) {
        return users.get(uuid);
    }

    public void setUser(final UUID uuid, final ManhuntUser user) {
        this.users.put(uuid, user);
    }
}
