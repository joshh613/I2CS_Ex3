package ex3.client;

import ex3.model.Cell;
import ex3.model.DangerMap;
import ex3.model.Map;
import ex3.model.PacMan;
import exe.ex3.game.GhostCL;
import exe.ex3.game.PacManAlgo;
import exe.ex3.game.PacmanGame;

import static ex3.util.GameConstants.*;

public class Ex3Algo implements PacManAlgo {
    private int count;

    private PacMan pacMan;

    private Map map;
    private int mapWidth;
    private int mapHeight;

    private DangerMap dangerMap;

    private Scorer scorer;

    private int lastDir = STAY;

    public Ex3Algo() {
        count = 0;
    }

    @Override
    public String getInfo() {
        return "Pac-man AI using danger map + mode + direction scoring.";
    }

    @Override
    public int move(PacmanGame game) {
        if (count == 0) init(game);
        else update(game);

        if (count % 60 == 0) log(game);
        count++;

        double bestScore = Double.NEGATIVE_INFINITY;
        int bestDir = STAY;

        for (int dir : DIRS) {
            Cell tile = calcTile(dir);
            double score = scorer.score(tile, dir);

            if (score > bestScore) {
                bestScore = score;
                bestDir = dir;
            }
        }

        if (bestScore < AlgoWeights.MOVE_THRESHOLD) {
            lastDir = STAY;
            return STAY;
        }

        scorer.addToMemory(pacMan.getPos(), bestDir);
        pacMan.moveTo(calcTile(bestDir));
        lastDir = bestDir;
        return bestDir;
    }

    private void init(PacmanGame game) {
        pacMan = new PacMan(game.getPos(CODE));

        int[][] board = game.getGame(CODE);
        map = new Map(board);
        map.setCyclic(game.isCyclic());
        mapWidth = board.length;
        mapHeight = board[0].length;

        GhostCL[] ghosts = game.getGhosts(CODE);
        dangerMap = new DangerMap(map, ghosts);

        scorer = new Scorer();
        update(game);
    }

    private void update(PacmanGame game) {
        pacMan = new PacMan(game.getPos(CODE));

        int[][] board = game.getGame(CODE);
        map = new Map(board);
        map.setCyclic(game.isCyclic());
        mapWidth = board.length;
        mapHeight = board[0].length;

        GhostCL[] ghosts = game.getGhosts(CODE);
        dangerMap.update(ghosts);

        scorer.updateEnv(map, dangerMap, pacMan, mapWidth, mapHeight);

        int safetyLevel = dangerMap.getDanger(pacMan.getPos());
        boolean powered = isPowered(ghosts);
        boolean panic = isPanic(safetyLevel, powered);

        scorer.updateState(ghosts, powered, panic, lastDir);
    }

    private boolean isPowered(GhostCL[] ghosts) {
        if (ghosts == null) return false;
        for (GhostCL g : ghosts) {
            if (g.remainTimeAsEatable(0) > 0) return true;
        }
        return false;
    }

    private boolean isPanic(int safetyLevel, boolean powered) {
        if (powered) return false;
        if (safetyLevel < 0) return false;
        return safetyLevel <= AlgoWeights.PANIC_DIST;
    }

    private Cell calcTile(int dir) {
        Cell pos = pacMan.getPos();
        int x = pos.getX();
        int y = pos.getY();

        switch (dir) {
            case UP:
                y++;
                break;
            case LEFT:
                x--;
                break;
            case DOWN:
                y--;
                break;
            case RIGHT:
                x++;
                break;
            case STAY:
            default:
                break;
        }

        if (map.isCyclic()) {
            x = wrapX(x);
            y = wrapY(y);
        }

        return new Cell(x, y);
    }

    private void log(PacmanGame game) {
        int[][] board = game.getGame(CODE);
        for (int y = 0; y < board[0].length; y++) {
            for (int[] row : board) {
                int v = row[y];
                System.out.print(v + "\t");
            }
            System.out.println();
        }

        System.out.println("Empty=" + EMPTY + ", Wall=" + WALL + ", DOT=" + DOT + ", POWERUP=" + POWERUP);
        System.out.println("Pacman coordinate: " + pacMan);

        GhostCL[] ghosts = game.getGhosts(CODE);
        for (int i = 0; i < ghosts.length; i++) {
            GhostCL g = ghosts[i];
            System.out.println(i + ") status: " + g.getStatus() + ",  type: " + g.getType() + ",  pos: " + g.getPos(0) + ",  time: " + g.remainTimeAsEatable(0));
        }
    }

    private int wrapX(int x) {
        return (x + mapWidth) % mapWidth;
    }

    private int wrapY(int y) {
        return (y + mapHeight) % mapHeight;
    }
}