package ex3.server;

import static ex3.util.GameConstants.*;

public final class Dir {
    private Dir() {
    }

    public static boolean isDir(int d) {
        return d == UP || d == DOWN || d == LEFT || d == RIGHT;
    }

    public static int opposite(int d) {
        return switch (d) {
            case UP -> DOWN;
            case DOWN -> UP;
            case LEFT -> RIGHT;
            case RIGHT -> LEFT;
            default -> STAY;
        };
    }
}