package ex3.model;

public class PacMan {
    private Cell pos;

    public PacMan(Cell pos) {
        if (pos == null) throw new IllegalArgumentException("Position must not be null");
        this.pos = new Cell(pos);
    }

    public PacMan(String pos) {
        if (pos == null) throw new IllegalArgumentException("pos is null");

        String[] split = pos.split(",");
        if (split.length != 2) throw new IllegalArgumentException("Invalid position");

        int x, y;
        try {
            x = Integer.parseInt(split[0].trim());
            y = Integer.parseInt(split[1].trim());

        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid numeric position: " + pos, e);
        }

        this(new Cell(x, y));
    }

    public Cell getPos() {
        return new Cell(pos);
    }

    private void setPos(Cell pos) {
        if (pos == null) throw new IllegalArgumentException("pos is null");
        this.pos = new Cell(pos);
    }

    public void moveTo(Cell pos) {
        if (pos == null) throw new IllegalArgumentException("pos is null");
        setPos(pos);
    }
}