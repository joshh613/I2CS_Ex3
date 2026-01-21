package ex3.util;

import ex3.model.Ghost;
import ex3.server.Server;
import exe.ex3.game.GhostCL;
import exe.ex3.game.PacmanGame;

import static ex3.util.GameConstants.*;

public class GameAdapter implements PacmanGame {
    private final Server server;

    public GameAdapter(Server server) {
        this.server = server;
    }

    @Override
    public String init(int level, String mapStr, boolean cyclic, long seed, double ghostSpeed, int dt, int something) {
        server.init(level, cyclic, seed);
        return "OK";
    }

    @Override
    public void play() {
        server.togglePause();
    }

    @Override
    public String move(int dir) {
        server.step(dir);
        return getData(0);
    }

    @Override
    public String end(int code) {
        return "DONE";
    }

    @Override
    public int[][] getGame(int code) {
        int[][] raw = server.getBoardSnapshot();
        int w = raw.length;
        int h = raw[0].length;

        int[][] out = new int[w][h];
        for (int x = 0; x < w; x++) {
            for (int y = 0; y < h; y++) {
                out[x][y] = translateTile(raw[x][y]);
            }
        }
        return out;
    }

    @Override
    public String getPos(int code) {
        return server.getPacX() + "," + server.getPacY();
    }

    @Override
    public GhostCL[] getGhosts(int code) {
        Ghost[] gs = server.getGhosts();
        if (gs == null) return new GhostCL[0];

        GhostCL[] out = new GhostCL[gs.length];
        for (int i = 0; i < gs.length; i++) {
            out[i] = new GhostAdapter(gs[i]);
        }
        return out;
    }

    @Override
    public String getData(int code) {
        return server.getHudLine();
    }

    @Override
    public int getStatus() {
        if (server.isDone()) return DONE;
        return PLAY;
    }

    @Override
    public boolean isCyclic() {
        return server.isCyclic();
    }

    @Override
    public Character getKeyChar() {
        return null;
    }

    private int translateTile(int v) {
        return switch (v) {
            case 0 -> EMPTY;
            case 1 -> WALL;
            case 3 -> DOT;
            case 5 -> POWERUP;
            default -> EMPTY;
        };
    }
}