package ex3.util;

import exe.ex3.game.Game;

import java.awt.Color;

public final class GameConstants {
    public static final int CODE = 0;

    public static final int EMPTY;
    public static final int WALL;
    public static final int DOT;
    public static final int POWERUP;
    public static final int STAY;
    public static final int UP, LEFT, DOWN, RIGHT;

    static {
        EMPTY = Game.getIntColor(Color.BLACK, CODE);
        WALL = Game.getIntColor(Color.BLUE, CODE);
        DOT = Game.getIntColor(Color.PINK, CODE);
        POWERUP = Game.getIntColor(Color.GREEN, CODE);
        STAY = Game.STAY;
        UP = Game.UP;
        LEFT = Game.LEFT;
        DOWN = Game.DOWN;
        RIGHT = Game.RIGHT;
    }

    public static final int[] DIRS = {UP, LEFT, DOWN, RIGHT};

    private GameConstants() {
    }
}
