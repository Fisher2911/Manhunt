package me.masterofthefish.manhunt.game;

public final class GameFullException extends RuntimeException {

    public GameFullException() {
        super();
    }

    public GameFullException(String message) {
        super(message);
    }
}
