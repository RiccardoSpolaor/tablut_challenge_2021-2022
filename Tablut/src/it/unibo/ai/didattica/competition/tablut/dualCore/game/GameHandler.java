package it.unibo.ai.didattica.competition.tablut.dualCore.game;

import java.util.ArrayList;
import java.util.List;

import it.unibo.ai.didattica.competition.tablut.domain.Action;
import it.unibo.ai.didattica.competition.tablut.domain.State;
import it.unibo.ai.didattica.competition.tablut.domain.State.Pawn;

public class GameHandler {

    private final int[][] camps = {
        {0,3}, {0,4}, {0,5}, {1,4},
        {3,0}, {3,8}, {4,0}, {4,1},
        {4,7}, {4,8}, {5,0}, {5,8},
        {7,4}, {8,3}, {8,4}, {8,5}
    };

    private final int[][] escapes = {
        {0,1}, {0,2}, {0,6}, {0,7},
        {1,0}, {1,8}, {2,0}, {2,8},
        {6,0}, {6,8}, {7,0}, {7,8},
        {8,1}, {8,2}, {8,6}, {8,7}
    };

    private final int[][] escape_block = {
        {1,2}, {1,6}, {2,1}, {2,2}, {2,6}, {2,7},
        {6,1}, {6,7}, {7,2}, {7,6}, {6,2}, {6,6}
    };

    private final Integer boardLenght = 9;
    private final int[][] throne = {{4,4}};

    private final static ArrayList<Integer> previousStates = new ArrayList<>();

    public static ArrayList<Integer> getPreviousStates() {
        return previousStates;
    }

    public static void updatePreviousStates(State state) {
       previousStates.add(state.hashCode());
    }

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

    public static ArrayList<Action> getAvailableActions(Pawn[][] board, int color) {
        return null;
    }

    public static State applyOperation(State state, Action parentOperation) {
        return null;
    }

}
