package it.unibo.ai.didattica.competition.tablut.dualCore.ai;

import java.util.HashMap;
import java.util.Map;
import it.unibo.ai.didattica.competition.tablut.domain.State.Pawn;
import it.unibo.ai.didattica.competition.tablut.dualCore.game.GameHandler;

public class Heuristics {

    Integer BLACK_TOTAL = 16;
    Integer WHITE_TOTAL = 9;

    Map<String, Float> WHITE_WEIGHTS = new HashMap<String, Float>();
    Map<String, Float> BLACK_WEIGHTS = new HashMap<String, Float>();

    public Heuristics() {
        this.BLACK_WEIGHTS.put("black_near_king", 0.1f);
        this.BLACK_WEIGHTS.put("black_around_king", 0.4f);
        this.BLACK_WEIGHTS.put("white_captured_proportion", 0.5f);

        this.WHITE_WEIGHTS.put("king_position", 0.1f);
        this.WHITE_WEIGHTS.put("open_paths_to_victory", 0.4f);
        this.WHITE_WEIGHTS.put("black_captured_proportion", 0.5f);
    }

    public Float normalizedHeuristicValue(Map<String, Float> heuristics, Map<String, Float> weights) {
        Float sum = 0.0f;
        for (String key : heuristics.keySet()) {
            sum += heuristics.get(key) * weights.get(key);
        }
        return sum;
    }

    public Float getWhiteHeuristicValue(Pawn[][] board, Pawn[][] previousBoard) {
        Map<String, Float> heuristics = new HashMap<String, Float>();

        Float openPathToVictory = GameHandler.getKingOpenPathToGoal(board); // utilizzare min() [vedi python]
        Float blackCaptured = 0.0f;
        if (GameHandler.getPawnDifference(board, previousBoard, -1)) { // ritorna boolean se è diverso il numero
            blackCaptured = 1.0f;
        }

        heuristics.put("king_position", GameHandler.isKingInThrone(board)); // TODO ritorna 1 se king è in trono, 0.5 se
                                                                            // è vicino e 0 se non vicino
        heuristics.put("open_paths_to_victory", openPathToVictory);
        heuristics.put("black_captured_proportion", blackCaptured);

        return this.normalizedHeuristicValue(heuristics, this.WHITE_WEIGHTS);
    }

    public Float getBlackHeuristicValue(Pawn[][] board, Pawn[][] previousBoard) {
        Map<String, Float> heuristics = new HashMap<String, Float>();

        Float openPathToVictory = GameHandler.getKingOpenPathToGoal(board); // utilizzare min() [vedi python]
        Float whiteCaptured = 0.0f;
        if (GameHandler.getPawnDifference(board, previousBoard, 1)) {
            whiteCaptured = 1.0f;
        }

        Float blackAroundKing = GameHandler.blackAroundKing(board);
        Float blackNearKing = GameHandler.blackNearKing(board);

        heuristics.put("black_around_king", blackAroundKing);
        heuristics.put("black_near_king", blackNearKing);
        heuristics.put("white_captured_proportion", whiteCaptured);

        return this.normalizedHeuristicValue(heuristics, this.BLACK_WEIGHTS);
    }

    public Float getHeuristicValue(Pawn[][] board, Pawn[][] previousBoard, Integer color) {
        if (color == 10) {
            return this.getWhiteHeuristicValue(board, previousBoard);
        } else {
            return this.getBlackHeuristicValue(board, previousBoard);
        }
    }
}
