package main.java.impl;

// Player is a player within the game.
// A player has an associated colour and side and is used to enable two player logic
public class Player {

    private String name;
    // Is the player human or AI
    private boolean isHuman;

    // What side of the board is the player starting from
    private Side side;

    public Player(String name, boolean isHuman, Side side) {
        this.name = name;
        this.isHuman = isHuman;
        this.side = side;
    }

    public String getName() {
        return name;
    }
    // returns true if the player is a human user
    // false if the player is an AI
    public boolean isHuman() {
        return isHuman;
    }

    // changes the player to a human or an AI
    public void setIsHuman(boolean isHuman) {
        this.isHuman = isHuman;
    }

    // returns the side that the player is affiliated with
    public Side getSide() {
        return side;
    }

    @Override
    public String toString() {
        return side.toString();
    }
}
