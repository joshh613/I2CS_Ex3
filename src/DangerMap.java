import exe.ex3.game.GhostCL;

public class DangerMap {
    Map map;

    public DangerMap(int[][] board, boolean isCyclic, GhostCL[] ghosts) {
        map = new Map(board);
        map.setCyclic(isCyclic);
    }
}
