package ex3.model;

import java.util.LinkedList;

/**
 * This class represents a 2D map as a "screen" or a raster matrix or maze over integers.
 *
 * @author joshh613
 *
 */
public class Map implements Map2D {
    private int[][] _map;
    private boolean _cyclicFlag = true;
    private int _width, _height;

    private static final int[][] DIRS = {{1, 0}, {-1, 0}, {0, 1}, {0, -1}};


    /**
     * Constructs a w*h 2D raster map with an init value v.
     *
     * @param w width
     * @param h height
     * @param v default value
     */
    public Map(int w, int h, int v) {
        init(w, h, v);
    }

    /**
     * Constructs a square map (size*size) with a default value v=0.
     *
     * @param size width and height
     */
    public Map(int size) {
        this(size, size, 0);
    }

    /**
     * Constructs a map from a given 2D array.
     *
     * @param data map array
     */
    public Map(int[][] data) {
        init(data);
    }

    @Override
    public void init(int w, int h, int v) {
        if (w <= 0 || h <= 0) {
            throw new IllegalArgumentException("invalid dimensions: h=" + h + ", w=" + w);
        }

        _width = w;
        _height = h;
        _map = new int[_width][_height];
        for (int i = 0; i < _width; i++) {
            for (int j = 0; j < _height; j++) {
                _map[i][j] = v;
            }
        }
    }

    @Override
    public void init(int[][] arr) {
        if (arr == null || arr.length == 0 || arr[0].length == 0) {
            throw new IllegalArgumentException("null/empty array");
        }

        _width = arr.length;
        _height = arr[0].length;

        for (int i = 0; i < _width; i++) {
            if (arr[i].length != _height) {
                throw new IllegalArgumentException("ragged array");
            }
        }

        _map = new int[_width][_height];
        for (int i = 0; i < _width; i++) {
            System.arraycopy(arr[i], 0, _map[i], 0, _height);
        }
    }

    @Override
    public int[][] getMap() {
        int[][] newMap = new int[_width][_height];
        for (int i = 0; i < _width; i++) {
            System.arraycopy(_map[i], 0, newMap[i], 0, _height);
        }
        return newMap;
    }

    @Override
    public int getWidth() {
        return _width;
    }

    @Override
    public int getHeight() {
        return _height;
    }

    @Override
    public int getPixel(int x, int y) {
        if (x < 0 || y < 0 || x >= _width || y >= _height) {
            throw new IndexOutOfBoundsException("x/y out of bounds");
        }
        return _map[x][y];
    }

    @Override
    public int getPixel(Pixel2D p) {
        if (p == null) {
            throw new NullPointerException("null pixel");
        }
        return getPixel(p.getX(), p.getY());
    }

    @Override
    public void setPixel(int x, int y, int v) {
        if (!isInside(x, y)) {
            throw new IndexOutOfBoundsException("x/y out of bounds");
        }
        _map[x][y] = v;
    }

    @Override
    public void setPixel(Pixel2D p, int v) {
        if (p == null) {
            throw new NullPointerException("null pixel");
        }
        setPixel(p.getX(), p.getY(), v);
    }

    @Override
    /**
     * Fills this map with the new color (new_v) starting from p.
     * https://en.wikipedia.org/wiki/Flood_fill
     */
    public int fill(Pixel2D xy, int new_v) {
        if (xy == null || !isInside(xy)) {
            return 0;
        }

        int old_v = getPixel(xy);
        if (old_v == new_v) {
            return 0;
        }

        boolean[][] visited = new boolean[_width][_height];
        LinkedList<Pixel2D> pixels = new LinkedList<>();

        visited[xy.getX()][xy.getY()] = true;
        pixels.add(xy);
        int count = 0;

        while (!pixels.isEmpty()) {
            Pixel2D curr = pixels.remove();
            int x = curr.getX(), y = curr.getY();
            setPixel(x, y, new_v);
            count++;

            for (int[] dir : DIRS) {
                int newX = x + dir[0];
                int newY = y + dir[1];

                if (isCyclic()) {
                    newX = (newX + _width) % _width; //newX+width handles newX=-1
                    newY = (newY + _height) % _height;
                } else {
                    if (!isInside(newX, newY)) {
                        continue;
                    }
                }

                if (!visited[newX][newY] && getPixel(newX, newY) == old_v) {
                    visited[newX][newY] = true;
                    pixels.add(new Index2D(newX, newY));
                }
            }
        }
        return count;
    }

    @Override
    /**
     * BFS like shortest the computation based on iterative raster implementation of BFS, see:
     * https://en.wikipedia.org/wiki/Breadth-first_search
     */
    public Pixel2D[] shortestPath(Pixel2D p1, Pixel2D p2, int obsColor) {
        if (p1 == null || p2 == null || !isInside(p1) || !isInside(p2)) {
            return null;
        }
        if (getPixel(p1) == obsColor || getPixel(p2) == obsColor) {
            return null;
        }

        if (p1.equals(p2)) {
            return new Pixel2D[]{p1};
        }

        boolean[][] visited = new boolean[_width][_height];
        Pixel2D[][] prev = new Pixel2D[_width][_height];
        LinkedList<Pixel2D> pixels = new LinkedList<>();

        int x1 = p1.getX(), x2 = p2.getX(), y1 = p1.getY(), y2 = p2.getY();
        visited[x1][y1] = true;
        pixels.add(new Index2D(x1, y1));

        while (!pixels.isEmpty()) {
            Pixel2D curr = pixels.remove();
            int x = curr.getX(), y = curr.getY();

            if (x == x2 && y == y2) {
                break;
            }

            for (int[] dir : DIRS) {
                int newX = x + dir[0];
                int newY = y + dir[1];

                if (isCyclic()) {
                    newX = (newX + _width) % _width; //newX+width handles newX=-1
                    newY = (newY + _height) % _height;
                } else {
                    if (!isInside(newX, newY)) {
                        continue;
                    }
                }

                if (visited[newX][newY] || getPixel(newX, newY) == obsColor) {
                    continue;
                }

                visited[newX][newY] = true;
                prev[newX][newY] = curr;
                pixels.add(new Index2D(newX, newY));
            }
        }

        if (!visited[x2][y2]) {
            return null;
        }
        return finalPath(prev, x2, y2);
    }

    @Override
    public boolean isInside(Pixel2D p) {
        return p != null && isInside(p.getX(), p.getY());
    }

    @Override
    public boolean isCyclic() {
        return _cyclicFlag;
    }

    @Override
    public void setCyclic(boolean cy) {
        _cyclicFlag = cy;
    }

    @Override
    public Map2D allDistance(Pixel2D start, int obsColor) {
        if (start == null || !isInside(start)) {
            return new Map(_width, _height, -1);
        }

        int x1 = start.getX(), y1 = start.getY();
        if (getPixel(x1, y1) == obsColor) {
            return new Map(_width, _height, -1);
        }

        boolean[][] visited = new boolean[_width][_height];
        visited[x1][y1] = true;

        int[][] dist = new int[_width][_height];
        for (int x = 0; x < _width; x++) {
            for (int y = 0; y < _height; y++) {
                dist[x][y] = -1;
            }
        }
        dist[x1][y1] = 0;

        LinkedList<Pixel2D> pixels = new LinkedList<>();
        pixels.add(new Index2D(x1, y1));

        while (!pixels.isEmpty()) {
            Pixel2D curr = pixels.remove();
            int x = curr.getX(), y = curr.getY();

            for (int[] dir : DIRS) {
                int newX = x + dir[0];
                int newY = y + dir[1];

                if (isCyclic()) {
                    newX = (newX + _width) % _width; //newX+width handles newX=-1
                    newY = (newY + _height) % _height;
                } else {
                    if (!isInside(newX, newY)) {
                        continue;
                    }
                }

                if (visited[newX][newY] || getPixel(newX, newY) == obsColor) {
                    continue;
                }

                visited[newX][newY] = true;
                dist[newX][newY] = dist[x][y] + 1;
                pixels.add(new Index2D(newX, newY));
            }
        }
        return new Map(dist);
    }

    public void update(int[][] arr) {
        if (arr.length != _width || arr[0].length != _height) throw new IllegalArgumentException("wrong dimensions");
        for (int x = 0; x < _width; x++) {
            for (int y = 0; y < _height; y++) {
                setPixel(x, y, arr[x][y]);
            }
        }
    }

    // HELPER METHODS

    private boolean isInside(int x, int y) {
        return x >= 0 && x < _width && y >= 0 && y < _height;
    }

    private Pixel2D[] finalPath(Pixel2D[][] prev, int x2, int y2) {
        int len = 0;
        Pixel2D pixel = new Index2D(x2, y2);
        while (pixel != null) {
            len++;
            pixel = prev[pixel.getX()][pixel.getY()];
        }

        Pixel2D[] path = new Pixel2D[len];
        pixel = new Index2D(x2, y2);
        for (int i = len - 1; i >= 0; i--) {
            path[i] = pixel;
            pixel = prev[pixel.getX()][pixel.getY()];
        }
        return path;
    }

}
