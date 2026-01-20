package ex3.client;

public class AlgoWeights {
    /**
     * I ended up sending my algorithm to an LLM and these are the final tuned weights.
     */
    public static final double MOVE_THRESHOLD = -1e9;
    public static final int PANIC_DIST = 3;
    public static final int EATABLE_TIME_BUFFER = 3;
    public static final double SAFETY_WEIGHT = 7.5;
    public static final double PANIC_MULT = 3.0;
    public static final double POWERED_MULT = 1.15;
    public static final double TRAP_WEIGHT = 0.08;
    public static final int TRAP_FLOOD_LIMIT = 40;
    public static final int TRAP_OK_AREA = 28;
    public static final double DOT_WEIGHT = 2.0;
    public static final double POWERUP_WEIGHT = 3.2;
    public static final double EDIBLE_GHOST_WEIGHT = 0.9;
    public static final double VISIT_PENALTY = 0.35;
    public static final double BACKTRACK_PENALTY = 1.1;
}