import java.util.Arrays;

public class PacMan {
    private Index2D pos;
    private final Index2D[] history;

    public PacMan(Index2D pos, int memory) {
        if (memory <= 0) throw new IllegalArgumentException("Memory must be positive");
        if (pos == null) throw new IllegalArgumentException("Position must not be null");

        this.pos = new Index2D(pos);
        this.history = new Index2D[memory];
    }

    public PacMan(Index2D pos) {
        this(pos, 8);
    }

    public PacMan(int x, int y, int memory) {
        this(new Index2D(x, y), memory);
    }

    public PacMan(int x, int y) {
        this(x, y, 8);
    }

    public PacMan(String pos, int memory) {
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

        this(new Index2D(x, y), memory);
    }

    public PacMan(String pos) {
        this(pos, 8);
    }

    public Index2D getPos() {
        return new Index2D(pos);
    }

    public Index2D[] getHistory() {
        return history.clone();
    }

    public Index2D getPrevPos() {
        Index2D prev = history[history.length - 1];
        return prev == null ? null : new Index2D(prev);
    }

    private void setPos(Index2D pos) {
        if (pos == null) throw new IllegalArgumentException("pos is null");
        this.pos = new Index2D(pos);
    }

    public void moveTo(Index2D pos) {
        if (pos == null) throw new IllegalArgumentException("pos is null");

        addToHistory(new Index2D(getPos()));
        setPos(pos);
    }

    private void addToHistory(Index2D pos) {
        for (int i = 1; i < history.length; i++) {
            history[i - 1] = history[i];
        }

        history[history.length - 1] = new Index2D(pos);
    }

    @Override
    public String toString() {
        return "pos=" + pos + ", history=" + Arrays.toString(history);
    }
}