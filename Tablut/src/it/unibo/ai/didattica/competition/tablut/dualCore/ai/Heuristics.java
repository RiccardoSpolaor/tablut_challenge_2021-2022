package it.unibo.ai.didattica.competition.tablut.dualCore.ai;

import java.util.HashMap;
import java.util.Map;
import it.unibo.ai.didattica.competition.tablut.domain.State.Pawn;
import it.unibo.ai.didattica.competition.tablut.dualCore.game.GameHandler;

public class Heuristics {

    public static Map<String, Float> WHITE_WEIGHTS = Heuristics.initializeWhite();
    public static Map<String, Float> BLACK_WEIGHTS = Heuristics.initializeBlack();

    private static Map<String, Float> initializeWhite() {
        Map<String, Float> WHITE_WEIGHTS = new HashMap<String, Float>();
        WHITE_WEIGHTS.put("king_position", 0.2f);
        WHITE_WEIGHTS.put("open_paths_to_victory", 0.3f);
        WHITE_WEIGHTS.put("black_captured_proportion", 0.5f);
        return WHITE_WEIGHTS;
    }

    private static Map<String, Float> initializeBlack() {
        Map<String, Float> BLACK_WEIGHTS = new HashMap<String, Float>();
        BLACK_WEIGHTS.put("black_near_king", 0.14f);
        BLACK_WEIGHTS.put("white_captured_proportion", 0.8f);
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

        Float openPathToVictory = GameHandler.getKingOpenPathsToGoalProportion(board);

        float blackCaptured = 0.0f;
        if (GameHandler.arePawnsCaptured(board, previousBoard, -1)) {
            blackCaptured = 1.0f;
        }

        heuristics.put("king_position", GameHandler.getKingThroneProximityValue(board));
        heuristics.put("open_paths_to_victory", openPathToVictory);
        heuristics.put("black_captured_proportion", blackCaptured);

        return normalizedHeuristicValue(heuristics, WHITE_WEIGHTS);
    }

    public static Float getBlackHeuristicValue(Pawn[][] board, Pawn[][] previousBoard) {
        Map<String, Float> heuristics = new HashMap<String, Float>();

        float whiteCaptured = 0.0f;
        if (GameHandler.arePawnsCaptured(board, previousBoard, 1)) {
            whiteCaptured = 1.0f;
        }
        Float blockedPathToVictory = (GameHandler.getKingOpenPathsToGoalProportion(board) == 0) ? 1f : 0f;
        Float blackNearKing = GameHandler.blackNearKing(board);
        heuristics.put("black_near_king", blackNearKing);
        heuristics.put("white_captured_proportion", whiteCaptured);
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
