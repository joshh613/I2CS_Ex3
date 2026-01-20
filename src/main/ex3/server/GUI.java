package ex3.server;

import ex3.model.Ghost;
import ex3.util.StdDraw;

import static ex3.util.GameConstants.*;

import java.awt.Color;
import java.awt.Font;

public class GUI {
    private final int cellPx;
    private final int hudPx;

    private int canvasW;
    private int canvasH;

    public GUI(int cellPx, int hudPx) {
        this.cellPx = cellPx;
        this.hudPx = hudPx;
    }

    public void initCanvas(int boardW, int boardH) {
        canvasW = boardW * cellPx;
        canvasH = boardH * cellPx + hudPx;

        StdDraw.setCanvasSize(canvasW, canvasH);
        StdDraw.setXscale(0, canvasW);
        StdDraw.setYscale(0, canvasH);
        StdDraw.enableDoubleBuffering();
    }

    public void draw(int[][] board, int pacX, int pacY, int pacDir, Ghost[] ghosts, String hudLine) {
        int w = board.length;
        int h = board[0].length;

        StdDraw.clear(Color.BLACK);
        StdDraw.setPenColor(Color.WHITE);
        StdDraw.setFont(new Font("Arial", Font.PLAIN, 14));
        StdDraw.text(canvasW * 0.5, h * cellPx + hudPx * 0.55, hudLine == null ? "" : hudLine);

        for (int x = 0; x < w; x++) {
            for (int y = 0; y < h; y++) {
                int v = board[x][y];

                double cx = x * cellPx + cellPx * 0.5;
                double cy = y * cellPx + cellPx * 0.5;

                if (v == WALL) {
                    StdDraw.setPenColor(new Color(0, 140, 255));
                    StdDraw.filledSquare(cx, cy, cellPx * 0.5);
                } else if (v == DOT) {
                    StdDraw.setPenColor(Color.PINK);
                    StdDraw.filledCircle(cx, cy, cellPx * 0.08);
                } else if (v == POWERUP) {
                    StdDraw.setPenColor(Color.GREEN);
                    StdDraw.filledCircle(cx, cy, cellPx * 0.14);
                }
            }
        }

        if (ghosts != null) {
            long now = System.currentTimeMillis();

            for (Ghost g : ghosts) {
                if (!g.released) continue;

                double gx = g.x * cellPx + cellPx * 0.5;
                double gy = g.y * cellPx + cellPx * 0.5;

                boolean edible = g.isEatable(now);
                StdDraw.setPenColor(edible ? Color.CYAN : Color.WHITE);
                double scale = edible ? 0.55 : 0.85;
                StdDraw.filledCircle(gx, gy, cellPx * 0.5 * scale);
            }
        }

        double px = pacX * cellPx + cellPx * 0.5;
        double py = pacY * cellPx + cellPx * 0.5;

        StdDraw.setPenColor(Color.YELLOW);
        StdDraw.filledCircle(px, py, cellPx * 0.35);

        double dx = 0, dy = 0;
        if (pacDir == 1) dy = cellPx * 0.18;        // UP
        else if (pacDir == 3) dy = -cellPx * 0.18;  // DOWN
        else if (pacDir == 2) dx = -cellPx * 0.18;  // LEFT
        else if (pacDir == 4) dx = cellPx * 0.18;   // RIGHT

        StdDraw.setPenColor(Color.BLACK);
        StdDraw.filledCircle(px + dx, py + dy, cellPx * 0.06);

        StdDraw.show();
    }

    public void drawEndScreen(boolean won, String hudLine) {
        StdDraw.clear(Color.BLACK);

        StdDraw.setPenColor(Color.WHITE);
        StdDraw.setFont(new Font("Arial", Font.BOLD, 36));
        StdDraw.text(canvasW * 0.5, canvasH * 0.55, won ? "YOU WIN!" : "GAME OVER");

        StdDraw.setFont(new Font("Arial", Font.PLAIN, 16));
        StdDraw.text(canvasW * 0.5, canvasH * 0.45, hudLine == null ? "" : hudLine);

        StdDraw.setFont(new Font("Arial", Font.PLAIN, 16));
        StdDraw.text(canvasW * 0.5, canvasH * 0.38, "Press R to restart, Q to quit");

        StdDraw.show();
    }
}