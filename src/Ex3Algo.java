import exe.ex3.game.Game;
import exe.ex3.game.GhostCL;
import exe.ex3.game.PacManAlgo;
import exe.ex3.game.PacmanGame;

import java.awt.*;
import java.util.Arrays;

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

    private static final int UP = Game.UP, LEFT = Game.LEFT, DOWN = Game.DOWN, RIGHT = Game.RIGHT;

    private int _count;
    private int[][] _board;
    private Map _map;
    private Index2D _pacman;
    private GhostCL[] _ghosts;

    public Ex3Algo() {
        _count = 0;
    }

    @Override
    /**
     *  Add a short description for the algorithm as a String.
     */
    public String getInfo() {
        return null;
    }

    @Override
    /**
     * This is the main method - that you should design, implement and test.
     */
    public int move(PacmanGame game) {
        _board = game.getGame(CODE);
        _map = new Map(_board);
        _map.setCyclic(game.isCyclic());
        _pacman = getPacman(game);
        _ghosts = game.getGhosts(CODE);

        if (_count++ % 60 == 0) {
            log();
        }

        return randomDir();
    }

    private static void printBoard(int[][] b) {
        for (int y = 0; y < b[0].length; y++) {
            for (int x = 0; x < b.length; x++) {
                int v = b[x][y];
                System.out.print(v + "\t");
            }
            System.out.println();
        }
    }

    private static void printGhosts(GhostCL[] gs) {
        for (int i = 0; i < gs.length; i++) {
            GhostCL g = gs[i];
            System.out.println(i + ") status: " + g.getStatus() + ",  type: " + g.getType() + ",  pos: " + g.getPos(0) + ",  time: " + g.remainTimeAsEatable(0));
        }
    }

    private static int randomDir() {
        int[] dirs = {Game.UP, Game.LEFT, Game.DOWN, Game.RIGHT};
        int ind = (int) (Math.random() * dirs.length);
        return dirs[ind];
    }

    private Index2D getPacman(PacmanGame game) {
        String[] pos = game.getPos(CODE).split(",");
        return new Index2D(Integer.parseInt(pos[0]), Integer.parseInt(pos[1]));
    }

    private void log() {
        printBoard(_board);
        System.out.println("Blue=" + BLUE + ", Pink=" + PINK + ", Black=" + BLACK + ", Green=" + GREEN);
        System.out.println("Pacman coordinate: " + _pacman.toString());
        printGhosts(_ghosts);
    }
}