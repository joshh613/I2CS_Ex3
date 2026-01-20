package ex3.model;

/**
 * An implementation of {@link Pixel2D}, representing an (integer, immutable) 2D coordinate.
 */
public class Cell implements Pixel2D {
    /**
     * The x-coordinate, representing the column index.
     */
    private final int x;

    /**
     * The y-coordinate, representing the row index.
     */
    private final int y;

    /**
     * Constructs a pixel at {@code (0,0)}.
     */
    public Cell() {
        this(0, 0);
    }

    /**
     * Constructs a pixel with the given coordinates.
     *
     * @param x the x-coordinate
     * @param y the y-coordinate
     * @throws IllegalArgumentException if {@code x} or {@code y} are less than zero
     */
    public Cell(int x, int y) {
        if (x < 0 || y < 0) throw new IllegalArgumentException("x or y should not be negative");
        this.x = x;
        this.y = y;
    }

    /**
     * Copy constructor.
     *
     * @param other another {@link Pixel2D}
     */
    public Cell(Pixel2D other) {
        this(other.getX(), other.getY());
    }

    @Override
    public int getX() {
        return x;
    }

    @Override
    public int getY() {
        return y;
    }

    /**
     * Represents the pixel as a string.
     *
     * @return a string in the format {@code "x,y"}
     */
    @Override
    public String toString() {
        return x + "," + y;
    }

    /**
     * Compares this pixel to a given object, testing for equality (i.e. they have the same {@code (x,y)} coordinates
     *
     * @param obj the object with which to compare
     * @return {@code true} if and only if the object represent the same coordinate
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof Pixel2D other)) return false;
        return x == other.getX() && y == other.getY();
    }

    /**
     * Returns a hash code consistent with our {@link #equals(Object)}.
     *
     * @return a hash code
     */
    @Override
    public int hashCode() {
        return 31 * x + y;
    }
}
