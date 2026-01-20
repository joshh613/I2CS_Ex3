package ex3.model;

import java.util.ArrayDeque;
import java.util.Deque;

/**
 * This class represents a 2D map as a "screen" (where {@code map[x][y]} represents a cell with {@code 0<=x<width} and {@code 0<=y<height}).
 *
 * @author joshh613
 */
public class Map implements Map2D {
    private int[][] map;
    private boolean cyclicFlag = true;
    private int width, height;

    private static final int[][] DIRS = {{1, 0}, {-1, 0}, {0, 1}, {0, -1}};

    /**
     * Constructs a {@code w*h} map, giving each cell an initial value of {@code v}.
     *
     * @param w map width
     * @param h map height
     * @param v map initial value
     */
    public Map(int w, int h, int v) {
        init(w, h, v);
    }

    /**
     * Constructs a square map with length {@code size}, giving each cell an inital value of {@code 0}.
     *
     * @param size length (i.e. width and height)
     */
    public Map(int size) {
        this(size, size, 0);
    }

    /**
     * Constructs a map from a given 2D array.
     *
     * @param data 2D array
     */
    public Map(int[][] data) {
        init(data);
    }

    @Override
    public void init(int w, int h, int v) {
        if (w <= 0 || h <= 0) {
            throw new IllegalArgumentException("w and h must be greater than zero");
        }

        width = w;
        height = h;
        map = new int[width][height];
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                map[i][j] = v;
            }
        }
    }

    @Override
    public void init(int[][] arr) {
        checkValidArray(arr);

        width = arr.length;
        height = arr[0].length;
        map = new int[width][height];
        for (int i = 0; i < width; i++) {
            System.arraycopy(arr[i], 0, map[i], 0, height);
        }
    }

    @Override
    public int[][] getMap() {
        int[][] newMap = new int[width][height];
        for (int i = 0; i < width; i++) {
            System.arraycopy(map[i], 0, newMap[i], 0, height);
        }
        return newMap;
    }

    @Override
    public int getWidth() {
        return width;
    }

    @Override
    public int getHeight() {
        return height;
    }

    @Override
    public int getPixel(int x, int y) {
        if (!isInside(x, y)) throw new IndexOutOfBoundsException("x/y out of bounds");
        return map[x][y];
    }

    @Override
    public int getPixel(Pixel2D p) {
        if (p == null) throw new NullPointerException("null pixel");
        return getPixel(p.getX(), p.getY());
    }

    @Override
    public void setPixel(int x, int y, int v) {
        if (!isInside(x, y)) throw new IndexOutOfBoundsException("x/y out of bounds");
        map[x][y] = v;
    }

    @Override
    public void setPixel(Pixel2D p, int v) {
        if (p == null) throw new NullPointerException("null pixel");
        setPixel(p.getX(), p.getY(), v);
    }

    /**
     * Flood fill the pixels connected (up/down/left/right) to {@code xy} with {@code new_v}.
     *
     * @param xy    starting pixel
     * @param new_v new value to be filled in
     * @return number of pixels updated
     */
    @Override
    public int fill(Pixel2D xy, int new_v) {
        if (xy == null || !isInside(xy)) return 0;

        int old_v = getPixel(xy);
        if (old_v == new_v) return 0;

        boolean[][] visited = new boolean[width][height];
        Deque<Pixel2D> pixels = new ArrayDeque<>();

        visited[xy.getX()][xy.getY()] = true;
        pixels.addLast(new Cell(xy));

        int count = 0;
        while (!pixels.isEmpty()) {
            Pixel2D curr = pixels.removeFirst();
            int x = curr.getX();
            int y = curr.getY();
            setPixel(x, y, new_v);
            count++;

            for (int[] dir : DIRS) {
                int newX = x + dir[0];
                int newY = y + dir[1];

                if (isCyclic()) {
                    newX = wrapX(newX);
                    newY = wrapY(newY);
                } else if (!isInside(newX, newY)) continue;

                if (!visited[newX][newY] && getPixel(newX, newY) == old_v) {
                    visited[newX][newY] = true;
                    pixels.addLast(new Cell(newX, newY));
                }
            }
        }
        return count;
    }

    /**
     * Shortest path (using BFS, up/down/left/right) avoiding {@code obsColor}.
     *
     * @param p1       start point
     * @param p2       end point
     * @param obsColor the value treated as an obstacle.
     * @return array of pixels from {@code p1} to {@code p2} (inclusive)
     */
    @Override
    public Pixel2D[] shortestPath(Pixel2D p1, Pixel2D p2, int obsColor) {
        if (p1 == null || p2 == null || !isInside(p1) || !isInside(p2)) return null;
        if (getPixel(p1) == obsColor || getPixel(p2) == obsColor) return null;
        if (p1.equals(p2)) return new Pixel2D[]{p1};

        boolean[][] visited = new boolean[width][height];
        Pixel2D[][] prev = new Pixel2D[width][height];
        Deque<Pixel2D> pixels = new ArrayDeque<>();

        int x1 = p1.getX(), x2 = p2.getX(), y1 = p1.getY(), y2 = p2.getY();
        visited[x1][y1] = true;
        pixels.addLast(new Cell(x1, y1));

        while (!pixels.isEmpty()) {
            Pixel2D curr = pixels.removeFirst();
            if (curr.equals(p2)) break;

            int x = curr.getX(), y = curr.getY();
            for (int[] dir : DIRS) {
                int newX = x + dir[0];
                int newY = y + dir[1];

                if (isCyclic()) {
                    newX = wrapX(newX);
                    newY = wrapY(newY);
                } else if (!isInside(newX, newY)) continue;

                if (visited[newX][newY] || getPixel(newX, newY) == obsColor) continue;

                visited[newX][newY] = true;
                prev[newX][newY] = curr;
                pixels.addLast(new Cell(newX, newY));
            }
        }
        return visited[x2][y2] ? buildPath(prev, x2, y2) : null;
    }

    @Override
    public boolean isInside(Pixel2D p) {
        return p != null && isInside(p.getX(), p.getY());
    }

    @Override
    public boolean isCyclic() {
        return cyclicFlag;
    }

    @Override
    public void setCyclic(boolean cy) {
        cyclicFlag = cy;
    }

    /**
     * Returns a new map where each pixel's value is its shortest distance to {@code start}.
     *
     * @param start    starting point
     * @param obsColor the value representing obstacles
     * @return map of shortest distances
     */
    @Override
    public Map2D allDistance(Pixel2D start, int obsColor) {
        if (start == null || !isInside(start)) throw new IllegalArgumentException("start isn't inside");

        int[][] dist = new int[width][height];
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                dist[x][y] = -1;
            }
        }

        if (getPixel(start) == obsColor) {
            Map res = new Map(dist);
            res.setCyclic(this.isCyclic());
            return res;
        }

        int x1 = start.getX(), y1 = start.getY();
        boolean[][] visited = new boolean[width][height];
        visited[x1][y1] = true;
        dist[x1][y1] = 0;

        Deque<Pixel2D> pixels = new ArrayDeque<>();
        pixels.addLast(new Cell(start));

        while (!pixels.isEmpty()) {
            Pixel2D curr = pixels.removeFirst();
            int x = curr.getX(), y = curr.getY();

            for (int[] dir : DIRS) {
                int newX = x + dir[0];
                int newY = y + dir[1];

                if (isCyclic()) {
                    newX = wrapX(newX);
                    newY = wrapY(newY);
                } else if (!isInside(newX, newY)) continue;

                if (visited[newX][newY] || getPixel(newX, newY) == obsColor) continue;

                visited[newX][newY] = true;
                dist[newX][newY] = dist[x][y] + 1;
                pixels.add(new Cell(newX, newY));
            }
        }
        Map res = new Map(dist);
        res.setCyclic(this.isCyclic());
        return res;
    }

    // HELPER METHODS

    private boolean isInside(int x, int y) {
        return x >= 0 && x < width && y >= 0 && y < height;
    }

    private int wrapX(int x) {
        return (x + width) % width;
    }

    private int wrapY(int y) {
        return (y + height) % height;
    }

    public void checkValidArray(int[][] arr) {
        if (arr == null || arr.length == 0 || arr[0] == null || arr[0].length == 0) {
            throw new IllegalArgumentException("null/empty array");
        }
        int h = arr[0].length;
        for (int[] row : arr) {
            if (row == null || row.length != h) {
                throw new IllegalArgumentException("ragged array");
            }
        }
    }

    private Pixel2D[] buildPath(Pixel2D[][] prev, int x2, int y2) {
        int len = 0;
        Pixel2D pixel = new Cell(x2, y2);
        while (pixel != null) {
            len++;
            pixel = prev[pixel.getX()][pixel.getY()];
        }

        Pixel2D[] path = new Pixel2D[len];
        pixel = new Cell(x2, y2);
        for (int i = len - 1; i >= 0; i--) {
            path[i] = pixel;
            pixel = prev[pixel.getX()][pixel.getY()];
        }
        return path;
    }
}
