package ex3.client;

import ex3.model.Index2D;

public class AlgoScoring {
    private double calcScore(Index2D tile) {
        if (isIllegal(tile)) return Double.NEGATIVE_INFINITY;
        return safetyScore(tile) + trapScore(tile) + foodScore(tile) + routeScore(tile);
    }

    private boolean isIllegal(Index2D tile) {
        return !map.isInside(tile) || map.getPixel(tile) == BLUE;
    }
}
