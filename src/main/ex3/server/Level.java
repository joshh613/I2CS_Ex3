package ex3.server;

public final class Level {
    public final int[][] board;
    public final int pacX;
    public final int pacY;

    public final int[][] ghostSpawns;
    public final long[] ghostReleaseDelaysMs;

    public final int doorX;
    public final int doorY;

    public final int foodLeft;

    public Level(int[][] board, int pacX, int pacY, int[][] ghostSpawns, long[] ghostReleaseDelaysMs, int doorX, int doorY, int foodLeft) {
        this.board = board;
        this.pacX = pacX;
        this.pacY = pacY;
        this.ghostSpawns = ghostSpawns;
        this.ghostReleaseDelaysMs = ghostReleaseDelaysMs;
        this.doorX = doorX;
        this.doorY = doorY;
        this.foodLeft = foodLeft;
    }
}