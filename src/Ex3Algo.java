import exe.ex3.game.Game;
import exe.ex3.game.GhostCL;
import exe.ex3.game.PacManAlgo;
import exe.ex3.game.PacmanGame;

import java.awt.*;

/**
 * This is the major algorithmic class for Ex3 - the PacMan game:
 * This code is a very simple example (random-walk algorithm).
 * Your task is to implement (here) your PacMan algorithm.
 */
public class Ex3Algo implements PacManAlgo {
    private static final int CODE = 0;

    private static final int BLUE = Game.getIntColor(Color.BLUE, CODE);
    private static final int PINK = Game.getIntColor(Color.PINK, CODE);
    private static final int BLACK = Game.getIntColor(Color.BLACK, CODE);
    private static final int GREEN = Game.getIntColor(Color.GREEN, CODE);

    private static final int[] DIRS = {Game.UP, Game.LEFT, Game.DOWN, Game.RIGHT};
    private static final int STAY = Game.STAY;

    private int count;
    private PacMan pacMan;

    private Map map;
    private int width;
    private int height;

    private DangerMap dangerMap;

    private int safetyLevel;
    private boolean isPowered;
    private boolean isPanic;

    public Ex3Algo() {
        count = 0;
    }

    @Override
    /**
     *  Add a short description for the algorithm as a String.
     */ public String getInfo() {
        return "Pac-man AI using a heatmap, direction scoring, and travel modes.";
    }

    @Override
    /**
     * This is the main method - that you should design, implement and test.
     */ public int move(PacmanGame game) {
        if (count == 0) {
            init(game);
        } else {
            update(game);
        }

        if (count++ % 60 == 0) {
            log(game);
        }

        safetyLevel = dangerMap.getSafetyLevel(pacMan);
        isPowered = isPowered(game);
        isPanic = isPanic(safetyLevel, isPowered);

        double bestScore = Double.NEGATIVE_INFINITY;
        int bestDir = STAY;
        Index2D tile;
        for (int dir : DIRS) {
            tile = calcTile(dir);
            double score = calcScore(tile);
            if (score > bestScore) {
                bestScore = score;
                bestDir = dir;
            }
        }

        if (bestScore < AlgoWeights.MOVE_THRESHOLD) {
            return STAY;
        }

        tile = calcTile(bestDir);
        pacMan.moveTo(tile);
        return bestDir;
    }

    private void init(PacmanGame game) {
        String pos = game.getPos(CODE);
        pacMan = new PacMan(pos);

        int[][] board = game.getGame(CODE);
        map = new Map(board);
        map.setCyclic(game.isCyclic());
        width = board.length;
        height = board[0].length;
        GhostCL[] ghosts = game.getGhosts(CODE);
        dangerMap = new DangerMap(map, ghosts);
    }

    private void update(PacmanGame game) {
        int[][] board = game.getGame(CODE);
        map = new Map(board);
        GhostCL[] ghosts = game.getGhosts(CODE);
        dangerMap.update(map, ghosts);
    }

    private Index2D calcTile(int dir) {
        Index2D pos = pacMan.getPos();
        int x = pos.getX();
        int y = pos.getY();
        switch (dir) {
            case Game.UP:
                y--;
                break;
            case Game.LEFT:
                x--;
                break;
            case Game.DOWN:
                y++;
                break;
            case Game.RIGHT:
                x++;
                break;
        }
        if (map.isCyclic()) {
            x = (x + width) % width;
            y = (y + height) % height;
        }
        return new Index2D(x, y);
    }

    private double calcScore(Index2D tile) {
        if (isIllegal(tile)) return Double.NEGATIVE_INFINITY;
        return safetyScore(tile) + trapScore(tile) + foodScore(tile) + routeScore(tile);
    }

    private boolean isIllegal(Index2D tile) {
        return !map.isInside(tile) || map.getPixel(tile) == BLUE;
    }

    private void log(PacmanGame game) {
        int[][] board = game.getGame(CODE);
        for (int y = 0; y < board[0].length; y++) {
            for (int x = 0; x < board.length; x++) {
                int v = board[x][y];
                System.out.print(v + "\t");
            }
            System.out.println();
        }

        System.out.println("Blue=" + BLUE + ", Pink=" + PINK + ", Black=" + BLACK + ", Green=" + GREEN);
        System.out.println("Pacman coordinate: " + pacMan.toString());

        GhostCL[] ghosts = game.getGhosts(CODE);
        for (int i = 0; i < ghosts.length; i++) {
            GhostCL g = ghosts[i];
            System.out.println(i + ") status: " + g.getStatus() + ",  type: " + g.getType() + ",  pos: " + g.getPos(0) + ",  time: " + g.remainTimeAsEatable(0));
        }
    }
}