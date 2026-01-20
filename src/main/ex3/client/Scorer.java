package ex3.client;

import ex3.model.Cell;
import ex3.model.DangerMap;
import ex3.model.Map;
import ex3.model.Map2D;
import ex3.model.PacMan;
import exe.ex3.game.GhostCL;

import static ex3.client.AlgoWeights.*;
import static ex3.util.GameConstants.*;

public class Scorer {
    private Map map;
    private DangerMap dangerMap;
    private PacMan pacMan;
    private int mapWidth;
    private int mapHeight;

    private GhostCL[] ghosts;
    private boolean powered;
    private boolean panic;
    private int lastDirLocal = STAY;

    private int[][] visitCount;

    public Scorer() {
    }

    public void updateEnv(Map map, DangerMap dangerMap, PacMan pacMan, int mapWidth, int mapHeight) {
        this.map = map;
        this.dangerMap = dangerMap;
        this.pacMan = pacMan;
        this.mapWidth = mapWidth;
        this.mapHeight = mapHeight;
        ensureVisitArray();
    }

    public void updateState(GhostCL[] ghosts, boolean powered, boolean panic, int lastDir) {
        this.ghosts = ghosts;
        this.powered = powered;
        this.panic = panic;
        this.lastDirLocal = lastDir;
        ensureVisitArray();
    }

    public void addToMemory(Cell from, int dir) {
        ensureVisitArray();
        if (from != null && map != null && map.isInside(from)) {
            visitCount[from.getX()][from.getY()]++;
        }
        lastDirLocal = dir;
    }

    public double score(Cell tile, int dir) {
        if (map == null || dangerMap == null || pacMan == null) return Double.NEGATIVE_INFINITY;
        if (isIllegal(tile)) return Double.NEGATIVE_INFINITY;
        return safetyScore(tile) + trapScore(tile) + foodScore(tile) + routeScore(tile, dir);
    }

    private void ensureVisitArray() {
        if (mapWidth <= 0 || mapHeight <= 0) return;
        if (visitCount == null || visitCount.length != mapWidth || visitCount[0].length != mapHeight) {
            visitCount = new int[mapWidth][mapHeight];
        }
    }

    private boolean isIllegal(Cell tile) {
        if (map == null) return true;
        if (!map.isInside(tile)) return true;
        return map.getPixel(tile) == WALL;
    }

    private int dangerAt(Cell c) {
        return dangerMap.getDanger(c.getX(), c.getY());
    }

    private double safetyScore(Cell tile) {
        int curD = dangerAt(pacMan.getPos());
        int nxtD = dangerAt(tile);

        if (curD < 0) curD = 999;
        if (nxtD < 0) nxtD = 999;

        if (!powered && nxtD == 0) return -1e9;

        int delta = nxtD - curD;
        double proximityBoost = 1.0 / (curD + 1.0);

        double modeMult = 1.0;
        if (panic && !powered) modeMult *= PANIC_MULT;
        if (powered) modeMult *= POWERED_MULT;

        double signed = powered ? -delta : delta;
        return SAFETY_WEIGHT * proximityBoost * modeMult * signed;
    }

    private double trapScore(Cell tile) {
        int area = floodOpenArea(tile, TRAP_FLOOD_LIMIT);
        int diff = area - TRAP_OK_AREA;
        return TRAP_WEIGHT * diff;
    }


    private int floodOpenArea(Cell start, int limit) {
        if (isIllegal(start)) return 0;

        Map2D dist = map.allDistance(start, WALL);
        int count = 0;

        for (int x = 0; x < mapWidth; x++) {
            for (int y = 0; y < mapHeight; y++) {
                int d = dist.getPixel(x, y);
                if (d >= 0 && d <= limit) count++;

            }
        }
        return count;
    }

    private double foodScore(Cell tile) {
        if (powered) {
            int cur = nearestReachableEdibleGhostDist(pacMan.getPos());
            int nxt = nearestReachableEdibleGhostDist(tile);
            if (cur < Integer.MAX_VALUE && nxt < Integer.MAX_VALUE) {
                return EDIBLE_GHOST_WEIGHT * (cur - nxt);
            }
        }

        if (!powered && panic) {
            int cur = distToNearest(pacMan.getPos(), POWERUP);
            int nxt = distToNearest(tile, POWERUP);
            if (cur < Integer.MAX_VALUE && nxt < Integer.MAX_VALUE) {
                return POWERUP_WEIGHT * (cur - nxt);
            }
        }

        int cur = distToNearest(pacMan.getPos(), DOT);
        int nxt = distToNearest(tile, DOT);
        if (cur < Integer.MAX_VALUE && nxt < Integer.MAX_VALUE) {
            return DOT_WEIGHT * (cur - nxt);
        }
        return 0.0;
    }

    private int nearestReachableEdibleGhostDist(Cell from) {
        if (ghosts == null || ghosts.length == 0) return Integer.MAX_VALUE;

        int best = Integer.MAX_VALUE;
        Map2D dist = map.allDistance(from, WALL);

        for (GhostCL g : ghosts) {
            double time = g.remainTimeAsEatable(0);
            if (time <= 0) continue;

            Cell gp = parsePos(g.getPos(0));
            int d = dist.getPixel(gp.getX(), gp.getY());

            if (d < 0) continue;
            double limit = time - EATABLE_TIME_BUFFER;
            if (d <= limit) best = Math.min(best, d);
        }

        return best;
    }

    private int distToNearest(Cell from, int targetPixel) {
        if (isIllegal(from)) return Integer.MAX_VALUE;

        Map2D dist = map.allDistance(from, WALL);
        int best = Integer.MAX_VALUE;

        for (int x = 0; x < mapWidth; x++) {
            for (int y = 0; y < mapHeight; y++) {
                if (map.getPixel(x, y) != targetPixel) continue;
                int d = dist.getPixel(x, y);
                if (d >= 0 && d < best) best = d;
            }
        }

        return best;
    }

    private double routeScore(Cell tile, int dir) {
        ensureVisitArray();

        double s = 0.0;
        s -= VISIT_PENALTY * visitCount[tile.getX()][tile.getY()];

        if (dir != STAY && lastDirLocal != STAY && dir == opposite(lastDirLocal)) {
            s -= BACKTRACK_PENALTY;
        }
        return s;
    }

    private int opposite(int dir) {
        return switch (dir) {
            case UP -> DOWN;
            case DOWN -> UP;
            case LEFT -> RIGHT;
            case RIGHT -> LEFT;
            default -> STAY;
        };
    }

    private Cell parsePos(String pos) {
        if (pos == null) throw new IllegalArgumentException("pos is null");

        String[] split = pos.split(",");
        if (split.length != 2) {
            throw new IllegalArgumentException("Invalid position: " + pos);
        }

        try {
            int x = Integer.parseInt(split[0].trim());
            int y = Integer.parseInt(split[1].trim());
            return new Cell(x, y);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid numeric position: " + pos, e);
        }
    }
}