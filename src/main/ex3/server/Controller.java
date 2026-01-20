package ex3.server;

import ex3.util.StdDraw;

import static ex3.util.GameConstants.*;

import java.awt.event.KeyEvent;

public class Controller {
    private boolean prevSpace;
    private boolean prevC;
    private boolean prevR;
    private boolean prevQ;

    public Actions poll() {
        Actions a = new Actions();

        boolean space = StdDraw.isKeyPressed(KeyEvent.VK_SPACE);
        boolean c = StdDraw.isKeyPressed(KeyEvent.VK_C);
        boolean r = StdDraw.isKeyPressed(KeyEvent.VK_R);
        boolean q = StdDraw.isKeyPressed(KeyEvent.VK_Q);

        if (space && !prevSpace) a.togglePause = true;
        if (c && !prevC) a.toggleCyclic = true;
        if (r && !prevR) a.restart = true;
        if (q && !prevQ) a.quit = true;

        prevSpace = space;
        prevC = c;
        prevR = r;
        prevQ = q;

        int dir = STAY;
        if (StdDraw.isKeyPressed(KeyEvent.VK_UP) || StdDraw.isKeyPressed(KeyEvent.VK_W)) {
            dir = UP;
        } else if (StdDraw.isKeyPressed(KeyEvent.VK_LEFT) || StdDraw.isKeyPressed(KeyEvent.VK_A)) {
            dir = LEFT;
        } else if (StdDraw.isKeyPressed(KeyEvent.VK_DOWN) || StdDraw.isKeyPressed(KeyEvent.VK_S)) {
            dir = DOWN;
        } else if (StdDraw.isKeyPressed(KeyEvent.VK_RIGHT) || StdDraw.isKeyPressed(KeyEvent.VK_D)) {
            dir = RIGHT;
        }

        a.dir = dir;
        return a;
    }
}