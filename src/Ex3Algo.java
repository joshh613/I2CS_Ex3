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
    private static final int code = 0;
    private static final int blue = Game.getIntColor(Color.BLUE, code);
    private static final int pink = Game.getIntColor(Color.PINK, code);
    private static final int black = Game.getIntColor(Color.BLACK, code);
    private static final int green = Game.getIntColor(Color.GREEN, code);
    private static final int up = Game.UP, left = Game.LEFT, down = Game.DOWN, right = Game.RIGHT;

    private int _count;

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
        int[][] board = game.getGame(0);
        String[] pos = game.getPos(code).split(",");
        Index2D pacman = new Index2D(Integer.parseInt(pos[0]), Integer.parseInt(pos[1]));
        GhostCL[] ghosts = game.getGhosts(code);

        if (_count % 60 == 0) {
            printBoard(board);
            System.out.println("Blue=" + blue + ", Pink=" + pink + ", Black=" + black + ", Green=" + green);
            System.out.println("Pacman coordinate: " + Arrays.toString(pos));
            printGhosts(ghosts);
        }

        Map map = new Map(board);
        map.setCyclic(true);
        Map2D dist = map.allDistance(pacman, black);

        _count++;
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
}