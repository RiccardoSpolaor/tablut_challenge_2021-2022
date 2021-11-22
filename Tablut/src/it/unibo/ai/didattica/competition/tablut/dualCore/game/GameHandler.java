package it.unibo.ai.didattica.competition.tablut.dualCore.game;

import java.util.List;

import it.unibo.ai.didattica.competition.tablut.domain.Action;
import it.unibo.ai.didattica.competition.tablut.domain.State;
import it.unibo.ai.didattica.competition.tablut.domain.State.Pawn;

public class GameHandler {

    private final int[][] camps;

    public static Float getKingOpenPathToGoal(Pawn[][] board) {
        return null;
    }

    public static boolean getPawnDifference(Pawn[][] board, Pawn[][] previousBoard, int i) {
        return false;
    }

    public static Float isKingInThrone(Pawn[][] board) {
        return null;
    }

    public static Float blackAroundKing(Pawn[][] board) {
        return null;
    }

    public static Float blackNearKing(Pawn[][] board) {
        return null;
    }

    public static Integer checkVictory(Pawn[][] board) {

        return null;
    }

    public static List<Action> getAvailableActions(Pawn[][] board, int color) {
        return null;
    }

    public static State applyOperation(State state, Action parentOperation) {
        return null;
    }

}
