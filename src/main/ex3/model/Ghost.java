package ex3.model;

import static ex3.util.GameConstants.STAY;

public class Ghost {
    public int x;
    public int y;
    public int dir = STAY;

    public boolean released = false;
    public long releaseAtMs = 0;
    public long eatableUntilMs = 0;
    public long lastMoveMs = 0;

    public boolean isEatable(long nowMs) {
        return nowMs < eatableUntilMs;
    }
}