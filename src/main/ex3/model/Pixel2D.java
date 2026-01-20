package ex3.model;

/**
 * Represents an {@code (x,y)} coordinate in a 2D grid. The coordinate is (1) integer and (2) immutable.
 */
public interface Pixel2D {
    /**
     *
     * @return the x-coordinate (column index)
     */
    int getX();

    /**
     *
     * @return the y-coordinate (row index)
     */
    int getY();

    /**
     * Computes the Euclidean distance between this pixel and another pixel (using the formula {@code sqrt(dx*dx + dy*dy)}.
     *
     * @param other the other pixel
     * @return the Euclidean distance between the pixels
     * @throws NullPointerException if {@code other} is {@code null}
     */
    default double distance2D(Pixel2D other) {
        if (other == null) throw new NullPointerException("other pixel is null");
        int dx = other.getX() - getX();
        int dy = other.getY() - getY();
        return Math.hypot(dx, dy);
    }
}
