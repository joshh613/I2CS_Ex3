package ex3.model;

import exe.ex3.game.GhostCL;

import static ex3.util.GameConstants.*;


public class DangerMap {
    private final Map map;
    private final int width, height;
    private final int[][] danger;

    public DangerMap(Map map, GhostCL[] ghosts) {
        this.map = new Map(map.getMap());
        this.map.setCyclic(map.isCyclic());
        this.width = map.getWidth();
        this.height = map.getHeight();

        this.danger = new int[width][height];
        initDanger();
        update(ghosts);
    }


    public void update(GhostCL[] ghosts) {
        initDanger();
        if (ghosts == null) return;

        for (GhostCL g : ghosts) {
            Pixel2D gp = posToPix(g.getPos(CODE));
            Map2D d = map.allDistance(gp, WALL);

            for (int x = 0; x < width; x++) {
                for (int y = 0; y < height; y++) {
                    int val = d.getPixel(x, y);
                    if (val >= 0 && val < danger[x][y]) {
                        danger[x][y] = val;
                    }
                }
            }
        }

        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                if (danger[x][y] == Integer.MAX_VALUE) danger[x][y] = -1;
            }
        }
    }

    public int getDanger(int x, int y) {
        return danger[x][y];
    }

    public int getDanger(Pixel2D p) {
        return getDanger(p.getX(), p.getY());
    }

    // HELPER METHODS

    private void initDanger() {
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                danger[x][y] = Integer.MAX_VALUE;
            }
        }
    }

    private Pixel2D posToPix(String pos) {
        if (pos == null) throw new IllegalArgumentException("pos is null");

        String[] split = pos.split(",");
        if (split.length != 2) throw new IllegalArgumentException("Invalid position");

        int x, y;
        try {
            x = Integer.parseInt(split[0].trim());
            y = Integer.parseInt(split[1].trim());

        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid numeric position: " + pos, e);
        }

        return new Cell(x, y);
    }
}
