package ex3.client;

public class AlgoWeights {
    public static final int PANIC_THRESHOLD = 4;
    public static final int TRAP_CHECK_DEPTH = 20;
    public static final int POWER_SEEK_DISTANCE = 12;
    public static final double MOVE_THRESHOLD = 100.0;

    public static final double W_SAFETY_BASE = 500.0;
    public static final double W_SAFETY_PANIC = 2000.0;
    public static final double W_TRAP = 300.0;
    public static final double W_FOOD = 150.0;
    public static final double W_POWER = 400.0;
    public static final double W_HUNT = 600.0;
    public static final double PENALTY_VISITED = -50.0;
    public static final double PENALTY_REVERSE = -100.0;
}
