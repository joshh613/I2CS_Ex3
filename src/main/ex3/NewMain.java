package ex3;

import ex3.server.Actions;
import ex3.server.Controller;
import ex3.server.GUI;
import ex3.util.GameAdapter;
import ex3.server.Server;
import ex3.util.GameInfo;
import ex3.util.StdDraw;
import exe.ex3.game.PacManAlgo;

public class NewMain {
    private static final int CELL_PX = 28;
    private static final int HUD_PX = 40;

    static void main() {
        Server server = new Server();
        server.init(GameInfo.CASE_SCENARIO, GameInfo.CYCLIC_MODE, GameInfo.RANDOM_SEED);
        GameAdapter view = new GameAdapter(server);
        GUI ui = new GUI(CELL_PX, HUD_PX);
        ui.initCanvas(server.getBoardSnapshot().length, server.getBoardSnapshot()[0].length);
        Controller input = new Controller();
        PacManAlgo algo = GameInfo.ALGO;

        while (!server.isDone()) {
            Actions a = input.poll();
            if (a.quit) return;
            if (a.togglePause) server.togglePause();
            if (a.toggleCyclic) server.toggleCyclic();
            if (a.restart) server.init(GameInfo.CASE_SCENARIO, server.isCyclic(), GameInfo.RANDOM_SEED);

            if (!server.isPaused()) {
                int dir = algo.move(view);
                server.step(dir);
            }

            ui.draw(server.getBoardSnapshot(), server.getPacX(), server.getPacY(), server.getPacDir(), server.getGhosts(), server.getHudLine());
            StdDraw.pause(GameInfo.DT);
        }

        ui.drawEndScreen(server.isWon(), server.getHudLine());
    }
}