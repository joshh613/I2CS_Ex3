package ex3.server;

import static ex3.util.GameConstants.*;

public final class LevelBuilder {
    private LevelBuilder() {
    }

    public static Level build(int scenario) {
        int w = 19 + scenario * 2;
        int h = 15 + scenario * 2;
        int[][] b = new int[w][h];

        for (int x = 0; x < w; x++) {
            for (int y = 0; y < h; y++) {
                b[x][y] = WALL;
            }
        }

        int cx = w / 2;
        int cy = h / 2;

        for (int x = 1; x < w - 1; x++) {
            b[x][1] = EMPTY;
            b[x][h - 2] = EMPTY;
        }
        for (int y = 1; y < h - 1; y++) {
            b[1][y] = EMPTY;
            b[w - 2][y] = EMPTY;
        }

        openRow(b, cy);
        openRow(b, cy - 2);
        openRow(b, cy + 2);

        openCol(b, cx);
        openCol(b, cx - 3);
        openCol(b, cx + 3);

        b[0][cy] = EMPTY;
        b[1][cy] = EMPTY;
        b[w - 1][cy] = EMPTY;
        b[w - 2][cy] = EMPTY;

        int houseW = scenario >= 3 ? 9 : 7;
        int houseH = scenario >= 3 ? 5 : 3;

        int hx0 = cx - houseW / 2;
        int hx1 = cx + houseW / 2;
        int hy0 = cy - houseH / 2;
        int hy1 = cy + houseH / 2;

        for (int x = hx0; x <= hx1; x++) {
            for (int y = hy0; y <= hy1; y++) {
                boolean border =
                        x == hx0 || x == hx1 || y == hy0 || y == hy1;
                b[x][y] = border ? WALL : EMPTY;
            }
        }

        int doorX = cx;
        int doorY = hy1 + 1;
        b[cx][hy1] = EMPTY;
        b[cx][doorY] = EMPTY;

        for (int x = 0; x < w; x++) {
            for (int y = 0; y < h; y++) {
                if (b[x][y] == EMPTY) {
                    b[x][y] = DOT;
                }
            }
        }

        for (int x = hx0 + 1; x <= hx1 - 1; x++) {
            for (int y = hy0 + 1; y <= hy1 - 1; y++) {
                b[x][y] = EMPTY;
            }
        }

        b[2][2] = POWERUP;
        b[w - 3][2] = POWERUP;
        b[2][h - 3] = POWERUP;
        b[w - 3][h - 3] = POWERUP;

        int pacX = cx;
        int pacY = Math.min(h - 2, hy0 - 2);
        b[pacX][pacY] = EMPTY;

        int[][] ghostSpawns = {
                {cx - 1, cy},
                {cx, cy},
                {cx + 1, cy}
        };

        for (int[] g : ghostSpawns) {
            b[g[0]][g[1]] = EMPTY;
        }

        long[] delays = {0, 2500, 5000};

        int foodLeft = 0;
        for (int x = 0; x < w; x++) {
            for (int y = 0; y < h; y++) {
                if (b[x][y] == DOT || b[x][y] == POWERUP) foodLeft++;
            }
        }

        return new Level(b, pacX, pacY, ghostSpawns, delays, doorX, doorY, foodLeft);
    }

    // HELPER METHODS

    private static void openRow(int[][] b, int y) {
        for (int x = 2; x < b.length - 2; x++) b[x][y] = EMPTY;
    }

    private static void openCol(int[][] b, int x) {
        for (int y = 2; y < b[0].length - 2; y++) b[x][y] = EMPTY;
    }
}