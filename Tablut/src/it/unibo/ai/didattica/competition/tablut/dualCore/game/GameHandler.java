package it.unibo.ai.didattica.competition.tablut.dualCore.game;

import java.io.IOException;
import java.util.ArrayList;

import it.unibo.ai.didattica.competition.tablut.domain.Action;
import it.unibo.ai.didattica.competition.tablut.domain.State;
import it.unibo.ai.didattica.competition.tablut.domain.State.Pawn;

public class GameHandler {

    private final static ArrayList<Integer> previousStates = new ArrayList<>();

    public static ArrayList<Integer> getPreviousStates() {
        return previousStates;
    }

    public static void updatePreviousStates(State state) {
       previousStates.add(state.hashCode());
    }

    private final static int[][] CAMPS = {
            {0, 3}, {0, 4}, {0, 5}, {1, 4},
            {3, 0}, {3, 8}, {4, 0}, {4, 1},
            {4, 7}, {4, 8}, {5, 0}, {5, 8},
            {7, 4}, {8, 3}, {8, 4}, {8, 5}
    };

    private final static int[][] ESCAPES = {
        {0, 1}, {0, 2}, {0, 6}, {0, 7},
        {1, 0}, {1, 8}, {2, 0}, {2, 8},
        {6, 0}, {6, 8}, {7, 0}, {7, 8},
        {8, 1}, {8, 2}, {8, 6}, {8, 7}
    };

/*    private final static int[][] ESCAPE_BLOCKS = {
        {1, 2}, {1, 6}, {2, 1}, {2, 2}, {2, 6}, {2, 7},
        {7, 2}, {7, 6}, {6, 1}, {6, 2}, {6, 6}, {6, 7}
    };
*/

    private final static int[] THRONE = {4, 4};

    public static Float getKingOpenPathsToGoalProportion(Pawn[][] board) {
        int[] kingPosition = findKingPosition(board);
        if (kingPosition == null)
            return 0f;

        int kingRow = kingPosition[0], kingCol = kingPosition[1];
        int available_paths = 4;

        for (int c = kingCol + 1; c < board.length; c++) {
            if (board[kingRow][c] != Pawn.EMPTY || isPawnInBoardPositions(kingRow, c, CAMPS)) {
                available_paths -= 1;
                break;
            }
        }

        for (int c = 0; c < kingCol; c++) {
            if (board[kingRow][c] != Pawn.EMPTY || isPawnInBoardPositions(kingRow, c, CAMPS)) {
                available_paths -= 1;
                break;
            }
        }

        for (int r = kingRow + 1; r < board.length; r++) {
            if (board[r][kingCol] != Pawn.EMPTY || isPawnInBoardPositions(r, kingCol, CAMPS)) {
                available_paths -= 1;
                break;
            }
        }

        for (int r = 0; r < kingRow; r++) {
            if (board[r][kingCol] != Pawn.EMPTY || isPawnInBoardPositions(r, kingCol, CAMPS)) {
                available_paths -= 1;
                break;
            }
        }

        return Math.min(available_paths, 2f) / 2f;
    }

    private static int getPawnsLeft (Pawn[][] board, int color) {
        ArrayList<Pawn> pawn_values_to_check = new ArrayList<>();
        if (color == 1) {
            pawn_values_to_check.add(Pawn.WHITE);
            pawn_values_to_check.add(Pawn.KING);
        } else
            pawn_values_to_check.add(Pawn.BLACK);

        int found = 0;

        for (Pawn[] pawns : board) {
            for (int column = 0; column < board.length; column++) {
                if (pawn_values_to_check.contains(pawns[column])) {
                    found += 1;
                }
            }
        }

        return found;
    }
    public static boolean arePawnsCaptured(Pawn[][] board, Pawn[][] previousBoard, int color) {
        return getPawnsLeft(board, color) != getPawnsLeft(previousBoard, color);
    }

    public static Float getKingThroneProximityValue(Pawn[][] board) {
        if (board[4][4] == Pawn.KING)
            return 1f;
        if (board[3][4] == Pawn.KING || board[5][4] == Pawn.KING || board[4][3] == Pawn.KING || board[4][5] == Pawn.KING)
            return .5f;
        return 0f;
    }

    public static Float blackAroundKing(Pawn[][] board) {
        int[] kingPosition = findKingPosition(board);
        if (kingPosition == null) {
            return 0f;
        }

        int kingRow = kingPosition[0], kingCol = kingPosition[1];

        int pawnsNearKingNumber = 0;


        for (int c = kingCol + 1; c < board.length; c++) {
            if (board[kingRow][c] == Pawn.BLACK) {
                pawnsNearKingNumber += 1;
                break;
            } else if (board[kingRow][c] == Pawn.EMPTY) {
                break;
            }
        }

        for (int c = kingCol - 1; c >= 0; c--) {
            if (board[kingRow][c] == Pawn.BLACK) {
                pawnsNearKingNumber += 1;
                break;
            } else if (board[kingRow][c] == Pawn.EMPTY) {
                break;
            }
        }

        for (int r = kingRow + 1; r < board.length; r++) {
            if (board[r][kingCol] == Pawn.BLACK) {
                pawnsNearKingNumber += 1;
                break;
            } else if (board[r][kingCol] == Pawn.EMPTY) {
                break;
            }
        }

        for (int r = kingRow - 1; r >= 0; r--) {
            if (board[r][kingCol] == Pawn.BLACK) {
                pawnsNearKingNumber += 1;
                break;
            } else if (board[r][kingCol] == Pawn.EMPTY) {
                break;
            }
        }

        return pawnsNearKingNumber / 4f;
    }

    public static Float blackNearKing(Pawn[][] board) {
        int[] kingPosition = findKingPosition(board);
        if (kingPosition == null) {
            return 0f;
        }
        int kingRow = kingPosition[0], kingCol = kingPosition[1];

        int number = 0;

        if (kingRow - 1 >= 0 && board[kingRow - 1][kingCol] == Pawn.BLACK)
            number ++;
        if (kingRow + 1 < board.length && board[kingRow+1][kingCol] == Pawn.BLACK)
            number++;
        if (kingCol - 1 >= 0 && board[kingRow][kingCol - 1] == Pawn.BLACK)
            number ++;
        if (kingCol + 1 < board.length && board[kingRow][kingCol + 1] == Pawn.BLACK)
            number++;

        float king_pos_value = getKingThroneProximityValue(board);
        if (king_pos_value == .5f)
            return number / 3f;
        return number / 4f;
    }

    private static int[] findKingPosition(Pawn[][] board) {
        for (int row = 0; row < board.length; row++) {
            for (int column = 0; column < board.length; column++) {
                if (board[row][column] == Pawn.KING) {
                    return new int[]{row, column};
                }
            }
        }
        return null;
    }

    public static Integer checkVictory(Pawn[][] board) {
        for (int[] cell : ESCAPES) {
            if (board[cell[0]][cell[1]] == Pawn.KING) {
                return 1;
            }
        }

        boolean found = false;

        for (Pawn[] pawns : board) {
            if (found)
                break;
            for (int column = 0; column < board.length; column++) {
                if (pawns[column] == Pawn.BLACK) {
                    found = true;
                    break;
                }
            }
        }

        if (!found) {
            return 1;
        }

        if (findKingPosition(board) == null)
            return -1;

        return 0;
    }

    private static boolean canPlayerMoveInCell(Pawn[][] board, State.Turn turn, int start_row, int start_column, int end_row, int end_column) {
        if (turn == State.Turn.WHITE)
            return canWhiteMoveInCell(board, end_row, end_column);
        else
            return canBlackMoveInCell(board, start_row, start_column, end_row, end_column);
    }

    private static boolean isPawnInBoardPositions (int row, int column, int[][] positionsToCheck) {
        for (int[] c : positionsToCheck) {
            if (c[0] == row && c[1] == column)
                return false;
        }
        return true;
    }

    private static boolean canWhiteMoveInCell(Pawn[][] board, int row, int column) {
        if (board[row][column] == Pawn.EMPTY) {
            return isPawnInBoardPositions(row, column, CAMPS);
        } else
            return false;
    }

    private static boolean canBlackMoveInCell(Pawn[][] board, int start_row, int start_column, int end_row, int end_column) {
        if (board[end_row][end_column] == Pawn.EMPTY) {
            boolean isEndPositionInCamps = isPawnInBoardPositions(start_row, start_column, CAMPS);
            if (isEndPositionInCamps) {
                return isPawnInBoardPositions(end_row, end_column, CAMPS);
            } else
                return true;
        } else
            return false;
    }

    private static Action getFormattedAction(State state, int startRow, int startColumn, int endRow, int endColumn) {
        try {
            return new Action(state.getBox(startRow, startColumn), state.getBox(endRow, endColumn), state.getTurn());
        } catch (IOException e) {
            return null;
        }
    }

    //
    public static ArrayList<Action> getAvailableActions(Pawn[][] board, State state) {
        ArrayList<Action> actionsList = new ArrayList<>();

        ArrayList<Pawn> usableCellValues = new ArrayList<>();
        if (state.getTurn() == State.Turn.WHITE) {
            usableCellValues.add(Pawn.WHITE);
            usableCellValues.add(Pawn.KING);
        }
        else
            usableCellValues.add(Pawn.BLACK);

        for (int row = 0; row < board.length; row++) {
            for (int column = 0; column < board.length; column++) {
                if (usableCellValues.contains(board[row][column])){
                    for (int r = row + 1; r < board.length; r++) {
                        if (canPlayerMoveInCell(board, state.getTurn(), row, column, r, column)) {
                            Action action = getFormattedAction(state, row, column, r, column);
                            if (action != null)
                                actionsList.add(action);
                        }
                        else
                            break;
                    }

                    for (int r = row - 1; r >= 0; r--) {
                        if (canPlayerMoveInCell(board, state.getTurn(), row, column, r, column)) {
                            Action action = getFormattedAction(state, row, column, r, column);
                            if (action != null)
                                actionsList.add(action);
                        }
                        else
                            break;
                    }

                    for (int c = column + 1; c < board.length; c++) {
                        if (canPlayerMoveInCell(board, state.getTurn(), row, column, row, c)) {
                            Action action = getFormattedAction(state, row, column, row, c);
                            if (action != null)
                                actionsList.add(action);
                        }
                        else
                            break;
                    }

                    for (int c = column - 1; c >= 0; c--) {
                        if (canPlayerMoveInCell(board, state.getTurn(), row, column, row, c)) {
                            Action action = getFormattedAction(state, row, column, row, c);
                            if (action != null)
                                actionsList.add(action);
                        } else
                            break;
                    }
                }
            }
        }
        return actionsList;
    }

    private static boolean isEnemyPawnCaptured(State state, int enemyRow, int enemyColumn) {
        Pawn[][] board = state.getBoard();
        State.Turn turn = state.getTurn();
        ArrayList<Pawn> playerValues = new ArrayList<>();
        ArrayList<Pawn> enemyValues = new ArrayList<>();
        if (turn == State.Turn.WHITE) {
            playerValues.add(Pawn.KING);
            playerValues.add(Pawn.WHITE);
            enemyValues.add(Pawn.BLACK);
        }   else {
            playerValues.add(Pawn.BLACK);
            enemyValues.add(Pawn.KING);
            enemyValues.add(Pawn.WHITE);
        }

        if (enemyRow < 0 || enemyRow >= board.length || enemyColumn < 0 || enemyColumn >= board.length)
            return false;

        Pawn enemy = board[enemyRow][enemyColumn];

        if (!enemyValues.contains(enemy))
            return false;

        boolean isEnemyInCamp = isPawnInBoardPositions(enemyRow, enemyColumn, CAMPS);

        int leftCol = enemyColumn - 1;
        int rightCol = enemyColumn + 1;
        int topRow = enemyRow - 1;
        int bottomRow =  enemyRow + 1;

        boolean leftCapture = leftCol >= 0 && (
                board[enemyRow][leftCol] == Pawn.THRONE
                || (isPawnInBoardPositions(enemyRow, leftCol, CAMPS) && !isEnemyInCamp)
                || playerValues.contains(board[enemyRow][leftCol])
        );

        boolean rightCapture = rightCol < board.length&& (
                board[enemyRow][rightCol] == Pawn.THRONE
                || (isPawnInBoardPositions(enemyRow, rightCol, CAMPS) && !isEnemyInCamp)
                || playerValues.contains(board[enemyRow][rightCol])
        );

        boolean topCapture = topRow >= 0 && (
                board[topRow][enemyColumn] == Pawn.THRONE
                || (isPawnInBoardPositions(topRow, enemyColumn, CAMPS) && !isEnemyInCamp)
                || playerValues.contains(board[topRow][enemyColumn])
        );

        boolean bottomCapture = bottomRow < board.length && (
                board[bottomRow][enemyColumn] == Pawn.THRONE
                || (isPawnInBoardPositions(bottomRow, enemyColumn, CAMPS) && !isEnemyInCamp)
                || playerValues.contains(board[bottomRow][enemyColumn])
        );

        if (enemy == Pawn.KING){
            if (getKingThroneProximityValue(board) != 0)
                return topCapture && bottomCapture && leftCapture && rightCapture;
        }

        return (topCapture && bottomCapture) || (leftCapture && rightCapture);
    }

    // ToDo: check victory aggiungere qui?
    public static State applyOperation(State state, Action parentOperation) {
        // state = start_state.copy()

        Pawn[][] board = state.getBoard();

        int start_row = parentOperation.getRowFrom();
        int start_col = parentOperation.getColumnFrom();

        int end_row = parentOperation.getRowTo();
        int end_col = parentOperation.getColumnTo();

        Pawn playerValue = board[start_row][start_col];

        if (start_row == THRONE[0] && start_col == THRONE[1])
            board[start_row][start_col] = Pawn.THRONE;
        else
            board[start_row][start_col] = Pawn.EMPTY;
        board[end_row][end_col] = playerValue;

        if (isEnemyPawnCaptured(state, end_row, end_col - 1))
            board[end_row][end_col - 1] = Pawn.EMPTY;

        if (isEnemyPawnCaptured(state, end_row, end_col + 1))
            board[end_row][end_col + 1] = Pawn.EMPTY;

        if (isEnemyPawnCaptured(state, end_row + 1, end_col))
            board[end_row + 1][end_col] = Pawn.EMPTY;

        if (isEnemyPawnCaptured(state, end_row - 1, end_col))
            board[end_row - 1][end_col] = Pawn.EMPTY;

        state.setBoard(board);

        if (state.getTurn() == State.Turn.WHITE)
            state.setTurn(State.Turn.BLACK);
        else
            state.setTurn(State.Turn.WHITE);

        return state;
    }

}
