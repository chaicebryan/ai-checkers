package main.java.utils;

import main.java.gui.Tile;
import main.java.impl.Position;

// A utility class that is used to convert pixel positions to board positions
public class GameUtils {

    private static final GameUtils instance = new GameUtils();

    private GameUtils() {
    }

    // This class is using the singleton design pattern as this
    // does not require multiple objects
    public static GameUtils getInstance() {
        return instance;
    }

    // converts pixel positions to board positions
    public Position convertToBoardPosition(double pixelX, double pixelY) {
        int x = (int)(pixelX + Tile.WIDTH / 2) / Tile.WIDTH;
        int y = (int)(pixelY + Tile.WIDTH / 2) / Tile.WIDTH;
        return new Position(x, y);
    }
}
