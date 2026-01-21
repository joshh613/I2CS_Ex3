package ex3.util;

import ex3.model.Ghost;
import exe.ex3.game.GhostCL;

public class GhostAdapter implements GhostCL {
    private final Ghost g;

    GhostAdapter(Ghost g) {
        this.g = g;
    }

    @Override
    public int getType() {
        return RANDOM_WALK1;
    }

    @Override
    public String getPos(int code) {
        return g.x + "," + g.y;
    }

    @Override
    public String getInfo() {
        return "G";
    }

    @Override
    public double remainTimeAsEatable(int code) {
        long ms = g.eatableUntilMs - System.currentTimeMillis();
        return ms / 1000.0;
    }

    @Override
    public int getStatus() {
        return System.currentTimeMillis() < g.eatableUntilMs ? 1 : 0;
    }
}