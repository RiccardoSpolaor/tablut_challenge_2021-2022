package it.unibo.ai.didattica.competition.tablut.dualCore.ai;

import java.util.HashMap;
import java.util.Map;
import it.unibo.ai.didattica.competition.tablut.domain.State.Pawn;
import it.unibo.ai.didattica.competition.tablut.dualCore.game.GameHandler;

public class Heuristics {

    public static Integer BLACK_TOTAL = 16;
    public static Integer WHITE_TOTAL = 9;

    public static Map<String, Float> WHITE_WEIGHTS = Heuristics.initializeWhite();
    public static Map<String, Float> BLACK_WEIGHTS = Heuristics.initializeBlack();

    private static Map<String, Float> initializeWhite() {
        Map<String, Float> WHITE_WEIGHTS = new HashMap<String, Float>();
        WHITE_WEIGHTS.put("king_position", 0.2f);
        WHITE_WEIGHTS.put("open_paths_to_victory", 0.3f);
        WHITE_WEIGHTS.put("black_captured_proportion", 0.5f);

        // WHITE_WEIGHTS.put("black_around_king", -0.3f);
        //WHITE_WEIGHTS.put("black_near_king", -0.05f);
        return WHITE_WEIGHTS;
    }

    private static Map<String, Float> initializeBlack() {
        Map<String, Float> BLACK_WEIGHTS = new HashMap<String, Float>();
        BLACK_WEIGHTS.put("black_near_king", 0.14f);
        // BLACK_WEIGHTS.put("black_around_king", 0.3f);
        BLACK_WEIGHTS.put("white_captured_proportion", 0.8f);
        // BLACK_WEIGHTS.put("block_escapes", 0.1f);
        BLACK_WEIGHTS.put("block_paths_to_victory", 0.06f);
        return BLACK_WEIGHTS;
    }

    public static Float normalizedHeuristicValue(Map<String, Float> heuristics, Map<String, Float> weights) {
        float sum = 0.0f;
        for (String key : heuristics.keySet()) {
            sum += heuristics.get(key) * weights.get(key);
        }
        return sum;
    }

    public static Float getWhiteHeuristicValue(Pawn[][] board, Pawn[][] previousBoard) {
        Map<String, Float> heuristics = new HashMap<String, Float>();

        Float openPathToVictory = GameHandler.getKingOpenPathsToGoalProportion(board); // utilizzare min() [vedi python]

        float blackCaptured = 0.0f;
        if (GameHandler.arePawnsCaptured(board, previousBoard, -1)) { // ritorna boolean se è diverso il numero
            blackCaptured = 1.0f;
        }

        heuristics.put("king_position", GameHandler.getKingThroneProximityValue(board)); // TODO ritorna 1 se king è in trono, 0.5 se
                                                                            // è vicino e 0 se non vicino
        heuristics.put("open_paths_to_victory", openPathToVictory);
        heuristics.put("black_captured_proportion", blackCaptured);

        // black heuristics

        // Float blackAroundKing = GameHandler.blackAroundKing(board);
        //Float blackNearKing = GameHandler.blackNearKing(board);
        // heuristics.put("black_around_king", blackAroundKing);
        //heuristics.put("black_near_king", blackNearKing);

        return normalizedHeuristicValue(heuristics, WHITE_WEIGHTS);
    }

    public static Float getBlackHeuristicValue(Pawn[][] board, Pawn[][] previousBoard) {
        Map<String, Float> heuristics = new HashMap<String, Float>();

        float whiteCaptured = 0.0f;
        if (GameHandler.arePawnsCaptured(board, previousBoard, 1)) {
            whiteCaptured = 1.0f;
        }
        Float blockedPathToVictory = (GameHandler.getKingOpenPathsToGoalProportion(board) == 0) ? 1f : 0f;
        // Float blackAroundKing = GameHandler.blackAroundKing(board);
        Float blackNearKing = GameHandler.blackNearKing(board);
        // Float escapedBlocked = GameHandler.countEscapeDifference(board, previousBoard);
        // heuristics.put("black_around_king", blackAroundKing);
        heuristics.put("black_near_king", blackNearKing);
        heuristics.put("white_captured_proportion", whiteCaptured);
        // heuristics.put("block_escapes", escapedBlocked);
        heuristics.put("block_paths_to_victory", blockedPathToVictory);

        return normalizedHeuristicValue(heuristics, BLACK_WEIGHTS);
    }

    public static Float getHeuristicValue(Pawn[][] board, Pawn[][] previousBoard, Integer color) {
        if (color == 1) {
            return getWhiteHeuristicValue(board, previousBoard);
        } else {
            return -getBlackHeuristicValue(board, previousBoard);
        }
    }
}
