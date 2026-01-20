package ex3.util;

import exe.ex3.game.Game;

import java.awt.Color;

public final class GameConstants {
    public static final int CODE = 0;

    public static final int EMPTY = Game.getIntColor(Color.BLACK, CODE);
    public static final int WALL = Game.getIntColor(Color.BLUE, CODE);
    public static final int DOT = Game.getIntColor(Color.PINK, CODE);
    public static final int POWERUP = Game.getIntColor(Color.GREEN, CODE);

    public static final int STAY = Game.STAY;
    public static final int UP = Game.UP, LEFT = Game.LEFT, DOWN = Game.DOWN, RIGHT = Game.RIGHT;

    public static final int[] DIRS = {UP, LEFT, DOWN, RIGHT};

    private GameConstants() {
    }
}
