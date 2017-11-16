package main.java.utils;

import main.java.gui.Tile;
import main.java.impl.Position;

public class GameUtils {

    private static GameUtils instance = new GameUtils();

    private GameUtils() {
    }

    public static GameUtils getInstance() {
        return instance;
    }

    public Position convertToBoardPosition(double pixelX, double pixelY) {
        int x = (int)(pixelX + Tile.WIDTH / 2) / Tile.WIDTH;
        int y = (int)(pixelY + Tile.WIDTH / 2) / Tile.WIDTH;
        return new Position(x, y);
    }
}
