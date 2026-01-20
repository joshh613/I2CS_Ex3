package ex3.server;

import ex3.model.Ghost;

import java.util.Random;

import static ex3.server.Dir.*;
import static ex3.util.GameConstants.*;

public class Server {
    private boolean cyclic = true;
    private boolean paused = true;

    private final long powerDurationMs = 5200;
    private final long ghostStepMs = 180;

    private int[][] board;
    private int w, h;

    private int pacX, pacY;
    private int pacDir = RIGHT;

    private Ghost[] ghosts;
    private int[][] ghostSpawn;
    private long[] ghostReleaseDelays;
    private int doorX, doorY;

    private boolean done = false;
    private boolean won = false;

    private long startMs = 0;
    private int score = 0;
    private int steps = 0;
    private int kills = 0;
    private int foodLeft = 0;

    private Random rnd = new Random(1);

    public void init(int scenario, boolean cyclic, long seed) {
        Level lvl = LevelBuilder.build(scenario);

        this.cyclic = cyclic;
        this.rnd = new Random(seed);

        this.board = lvl.board;
        this.w = board.length;
        this.h = board[0].length;

        this.pacX = lvl.pacX;
        this.pacY = lvl.pacY;
        this.pacDir = RIGHT;

        this.ghostSpawn = lvl.ghostSpawns;
        this.ghostReleaseDelays = lvl.ghostReleaseDelaysMs;
        this.doorX = lvl.doorX;
        this.doorY = lvl.doorY;

        this.ghosts = new Ghost[ghostSpawn.length];
        long now = System.currentTimeMillis();
        for (int i = 0; i < ghosts.length; i++) {
            Ghost g = new Ghost();
            g.x = ghostSpawn[i][0];
            g.y = ghostSpawn[i][1];
            g.released = false;
            g.releaseAtMs = now + ghostReleaseDelays[i];
            g.lastMoveMs = 0;
            g.eatableUntilMs = 0;
            ghosts[i] = g;
        }

        this.foodLeft = lvl.foodLeft;

        this.score = 0;
        this.steps = 0;
        this.kills = 0;
        this.won = false;
        this.done = false;

        this.paused = true;
        this.startMs = System.currentTimeMillis();
    }

    public void togglePause() {
        paused = !paused;
    }

    public void toggleCyclic() {
        cyclic = !cyclic;
    }

    public boolean isCyclic() {
        return cyclic;
    }

    public boolean isPaused() {
        return paused;
    }

    public boolean isDone() {
        return done;
    }

    public boolean isWon() {
        return won;
    }

    public int getPacX() {
        return pacX;
    }

    public int getPacY() {
        return pacY;
    }

    public int getPacDir() {
        return pacDir;
    }

    public Ghost[] getGhosts() {
        return ghosts;
    }

    public int[][] getBoardSnapshot() {
        int[][] copy = new int[w][h];
        for (int x = 0; x < w; x++) System.arraycopy(board[x], 0, copy[x], 0, h);
        return copy;
    }

    public String getHudLine() {
        long t = System.currentTimeMillis() - startMs;
        String state = done ? (won ? "WIN" : "LOSE") : (paused ? "PAUSED" : "RUN");
        return "State=" + state + " | Time=" + (t / 1000.0) + " | Score=" + score + " | Steps=" + steps + " | Kills=" + kills + " | FoodLeft=" + foodLeft + " | Cyclic=" + cyclic;
    }

    public void step(int dir) {
        if (paused || done) return;

        long now = System.currentTimeMillis();
        movePac(dir);
        updateGhosts(now);
        resolveCollisions(now);

        steps++;

        if (foodLeft <= 0) {
            won = true;
            done = true;
        }
    }

    private void movePac(int dir) {
        if (!isDir(dir)) return;
        pacDir = dir;

        int nx = pacX;
        int ny = pacY;

        if (dir == UP) ny++;
        else if (dir == DOWN) ny--;
        else if (dir == LEFT) nx--;
        else if (dir == RIGHT) nx++;

        int[] wxy = wrap(nx, ny);
        if (wxy == null) return;

        nx = wxy[0];
        ny = wxy[1];

        if (board[nx][ny] == WALL) return;

        int cell = board[nx][ny];
        if (cell == DOT) {
            score += 1;
            foodLeft--;
            board[nx][ny] = EMPTY;
        } else if (cell == POWERUP) {
            score += 5;
            board[nx][ny] = EMPTY;
            activatePower(nowMs());
        }

        pacX = nx;
        pacY = ny;
    }

    private void activatePower(long now) {
        long until = now + powerDurationMs;
        if (ghosts == null) return;
        for (Ghost g : ghosts) g.eatableUntilMs = until;
    }

    private void updateGhosts(long now) {
        if (ghosts == null) return;

        for (Ghost g : ghosts) {
            if (!g.released) {
                if (now < g.releaseAtMs) continue;
                stepGhostRelease(g, now);
                continue;
            }
            stepGhostRandomWalk(g, now);
        }
    }

    private void stepGhostRandomWalk(Ghost g, long now) {
        if (now - g.lastMoveMs < ghostStepMs) return;
        g.lastMoveMs = now;

        int[] dirs = {UP, DOWN, LEFT, RIGHT};
        shuffle(dirs);

        boolean hasNonReverse = hasNonReverseOption(g);

        for (int d : dirs) {
            if (hasNonReverse && d == opposite(g.dir)) continue;

            int nx = g.x;
            int ny = g.y;

            if (d == UP) ny++;
            else if (d == DOWN) ny--;
            else if (d == LEFT) nx--;
            else if (d == RIGHT) nx++;

            int[] wxy = wrap(nx, ny);
            if (wxy == null) continue;

            nx = wxy[0];
            ny = wxy[1];

            if (board[nx][ny] == WALL) continue;

            g.dir = d;
            g.x = nx;
            g.y = ny;
            return;
        }
    }

    private boolean hasNonReverseOption(Ghost g) {
        for (int d : DIRS) {
            if (d == opposite(g.dir)) continue;

            int nx = g.x;
            int ny = g.y;

            if (d == UP) ny++;
            else if (d == DOWN) ny--;
            else if (d == LEFT) nx--;
            else if (d == RIGHT) nx++;

            int[] wxy = wrap(nx, ny);
            if (wxy == null) continue;

            nx = wxy[0];
            ny = wxy[1];

            if (board[nx][ny] != WALL) return true;
        }
        return false;
    }

    private void resolveCollisions(long now) {
        if (ghosts == null) return;

        for (int i = 0; i < ghosts.length; i++) {
            Ghost g = ghosts[i];
            if (!g.released) continue;

            if (g.x == pacX && g.y == pacY) {
                if (g.isEatable(now)) {
                    eatGhost(i, now);
                } else {
                    won = false;
                    done = true;
                }
                return;
            }
        }
    }

    private void eatGhost(int idx, long now) {
        score += 200;
        kills++;

        Ghost g = ghosts[idx];
        g.released = false;
        g.releaseAtMs = now + 2000;
        g.lastMoveMs = 0;
        g.eatableUntilMs = 0;

        g.x = ghostSpawn[idx][0];
        g.y = ghostSpawn[idx][1];
    }

    private int[] wrap(int x, int y) {
        if (cyclic) {
            x = (x + w) % w;
            y = (y + h) % h;
            return new int[]{x, y};
        }
        if (x < 0 || y < 0 || x >= w || y >= h) return null;
        return new int[]{x, y};
    }

    private void shuffle(int[] a) {
        for (int i = a.length - 1; i > 0; i--) {
            int j = rnd.nextInt(i + 1);
            int t = a[i];
            a[i] = a[j];
            a[j] = t;
        }
    }

    private long nowMs() {
        return System.currentTimeMillis();
    }

    private void stepGhostRelease(Ghost g, long now) {
        if (g.x == doorX && g.y == doorY) {
            g.released = true;
            g.dir = DOWN;
            g.lastMoveMs = now;
            return;
        }

        int nx = g.x;
        int ny = g.y;

        if (g.x < doorX) nx = g.x + 1;
        else if (g.x > doorX) nx = g.x - 1;
        else {
            if (g.y < doorY) ny = g.y + 1;
            else ny = g.y - 1;
        }

        int[] wxy = wrap(nx, ny);
        if (wxy == null) return;
        nx = wxy[0];
        ny = wxy[1];

        if (board[nx][ny] == WALL) return;

        g.x = nx;
        g.y = ny;
    }
}