package me.masterofthefish.manhunt.user;

import me.masterofthefish.manhunt.game.Game;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.UUID;

public class ManhuntUser implements Comparable<ManhuntUser> {

    private final UUID uuid;
    private Game currentGame;
    private Role role;
    private int wins;
    private int losses;

    public ManhuntUser(final UUID uuid, final int wins, final int losses) {
        this.uuid = uuid;
        this.wins = wins;
        this.losses = losses;
    }

    public Player getPlayer() {
        return Bukkit.getPlayer(uuid);
    }

    public boolean isOnline() {
        return getPlayer().isOnline();
    }

    public void setRole(final Role role) {
        this.role = role;
    }

    public Game getCurrentGame() {
        return currentGame;
    }

    public UUID getUuid() {
        return uuid;
    }

    public Role getRole() {
        return role;
    }

    public void setCurrentGame(final Game currentGame) {
        this.currentGame = currentGame;
    }

    public boolean isInGame() {
        return this.currentGame != null;
    }

    public void addWin() {
        this.wins++;
    }

    public void addLoss() {
        this.losses++;
    }

    public int getWins() {
        return wins;
    }

    public int getLosses() {
        return losses;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ManhuntUser that = (ManhuntUser) o;
        return Objects.equals(uuid, that.uuid);
    }

    @Override
    public int hashCode() {
        return Objects.hash(uuid);
    }

    @Override
    public int compareTo(final @NotNull ManhuntUser user) {
        if(!this.isOnline()) {
            return -1;
        }
        if(!user.isOnline()) {
            return 1;
        }
        final Player firstPlayer = this.getPlayer();
        final Player secondPlayer = user.getPlayer();
        final String permission = "manhunt.priority";
        if(firstPlayer.hasPermission(permission) && !secondPlayer.hasPermission(permission)) {
            return -1;
        } else if(secondPlayer.hasPermission(permission) && !firstPlayer.hasPermission(permission)) {
            return 1;
        }
        return 0;
    }
}
