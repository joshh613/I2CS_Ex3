import ex3.model.Cell;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class CellTest {
    double EPS = 1e-6;

    @Test
    void constructors() {
        Cell cell = new Cell();
        assertEquals(0, cell.getX());
        assertEquals(0, cell.getY());

        cell = new Cell(4, 2);
        assertEquals(4, cell.getX());
        assertEquals(2, cell.getY());

        Cell other = new Cell(cell);
        assertEquals(cell.getX(), other.getX());
        assertEquals(cell.getY(), other.getY());
    }

    @Test
    void equalsAndHashCode() {
        Cell cell1 = new Cell(4, 2);
        Cell cell2 = new Cell(4, 2);
        Cell cell3 = new Cell(2, 4);


        assertTrue(cell1.equals(cell1));
        assertTrue(cell1.equals(cell2));
        assertTrue(cell2.equals(cell1));
        assertTrue(cell1.equals(new Cell(cell1)));

        assertFalse(cell1.equals(null));
        assertFalse(cell1.equals(cell3));

        assertEquals(cell1.hashCode(), cell2.hashCode());
        assertNotEquals(cell1.hashCode(), cell3.hashCode());
    }

    @Test
    void distance() {
        Cell cell1 = new Cell(4, 2);
        Cell cell2 = new Cell(4, 2);
        Cell cell3 = new Cell(2, 4);

        assertEquals(0.0, cell1.distance2D(cell2), EPS);
        assertEquals(Math.sqrt(8), cell1.distance2D(cell3), EPS);
        assertEquals(Math.sqrt(8), cell3.distance2D(cell1), EPS);
    }
}
