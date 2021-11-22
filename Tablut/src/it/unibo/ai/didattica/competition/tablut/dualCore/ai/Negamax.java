package it.unibo.ai.didattica.competition.tablut.dualCore.ai;

import it.unibo.ai.didattica.competition.tablut.domain.Action;
import it.unibo.ai.didattica.competition.tablut.domain.State;
import it.unibo.ai.didattica.competition.tablut.dualCore.game.GameHandler;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public final class Negamax {
    private static final int TABLE_SIZE = 1_000_000;
    private static final int DEPTH = 4;

    private final int color;
    private final int timeout;
    private final HeuristicsTable heuristicsTable;
    private final TraspositionTable traspositionTable;

    public Negamax(int color, int timeout) {
        this.color = color;
        this.timeout = timeout - 1;
        traspositionTable = new TraspositionTable(TABLE_SIZE);
        heuristicsTable = new HeuristicsTable(TABLE_SIZE);
    }

    public static class NegamaxResult {
        private Action action;
        private Float value;

        public NegamaxResult(Action action, Float value) {
            this.action = action;
            this.value = value;
        }

        public Action getAction() {
            return action;
        }

        public void setAction(Action action) {
            this.action = action;
        }

        public Float getValue() {
            return value;
        }

        public void setValue(Float value) {
            this.value = value;
        }

    }

    public Action findBestMove(State state) {

        Node root = new Node(state, state, null);

        ExecutorService executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());

        ArrayList<Action> availableActions = GameHandler.getAvailableActions(root.getState().getBoard(), color);
        ArrayList<Node> childNodes = getSortedChildNodes(root, availableActions, color);
        ArrayList<Future<NegamaxResult>> futureResults = new ArrayList<>(childNodes.size());

        Long started = Instant.now().getEpochSecond();

        try {
            for (Node child : childNodes) {
                Future<NegamaxResult> result = executor.submit(() -> parallelNegamax(child, started));
                futureResults.add(result);
            }
        } finally {
            executor.shutdown();
        }

        Float bestValue = Float.NEGATIVE_INFINITY;
        Float actualValue = Float.NEGATIVE_INFINITY;

        NegamaxResult bestResult = null;
        NegamaxResult actualresult = null;

        for (Future<NegamaxResult> future : futureResults) {
            try {
                actualresult = future.get();
                actualValue = actualresult.value;
            } catch (Exception e) {
                actualValue = Float.NEGATIVE_INFINITY;
                continue;
            }
            if (actualValue > bestValue) {
                bestResult = actualresult;
            }
        }
        return bestResult.getAction();
    }

    private NegamaxResult parallelNegamax(Node node, Long started) {
        return new NegamaxResult(
                node.getParentOperation(),
                -this.negamax(node, started, -color, Float.POSITIVE_INFINITY, Float.NEGATIVE_INFINITY, DEPTH)
        );
    }

    private ArrayList<Node> getSortedChildNodes (Node root, ArrayList<Action> availableActions, int current_color) {
        ArrayList<Node> childNodes = new ArrayList<>(availableActions.size());

        for (Action action : availableActions) {
            Node n = new Node(GameHandler.applyOperation(root.getState(), root.getParentOperation()), root.getState(),
                    action);
            Float heuristic = heuristicsTable.getItem(n.getId());
            if (heuristic == null) {
                heuristic = Heuristics.getHeuristicValue(n.getState().getBoard(), n.getParentState().getBoard(), current_color);
                heuristicsTable.setItem(n.getId(), heuristic);
            }
            n.setHeuristicValue(heuristic);
            childNodes.add(n);
        }

        // ToDo check if the sorting is correct;
        childNodes.sort((o1, o2) -> {
            if (Objects.equals(o1.getHeuristicValue(), o2.getHeuristicValue()))
                return 0;
            if (o1.getHeuristicValue() > o2.getHeuristicValue()) {
                return current_color;
            } else {
                return -current_color;
            }
        });

        return childNodes;
    }

    private Float negamax(Node node, Long started, int current_color, Float alpha, Float beta, int depth) {
        Float alphaOrig = alpha;

        TableEntry tableEntry = traspositionTable.getItem(node.getId());

        if (tableEntry != null && tableEntry.depth >= depth) {
            if (tableEntry.flag.equals(TableEntry.FlagValue.EXACT))
                return tableEntry.value;

            else if (tableEntry.flag.equals(TableEntry.FlagValue.LOWER_BOUND))
                alpha = Math.max(alpha, tableEntry.value);

            else if (tableEntry.flag.equals(TableEntry.FlagValue.UPPER_BOUND))
                beta = Math.min(beta, tableEntry.value);

            if (alpha >= beta)
                return tableEntry.value;
        }

        /* Draw */
        if (GameHandler.getPreviousStates().contains(node.getId()))
            return 0f;

        int victory = node.isTerminal();

        if (Math.abs(victory) == 1)
            return current_color * victory * Math.max(depth, 1f);

        if (depth == 0)
            return current_color * node.getHeuristicValue();

        ArrayList<Action> availableActions = GameHandler.getAvailableActions(node.getState().getBoard(), current_color);
        ArrayList<Node> childNodes = getSortedChildNodes(node, availableActions, current_color);

        float bestValue = Float.NEGATIVE_INFINITY;
        for (Node child : childNodes) {
            if (Instant.now().getEpochSecond() - started >= timeout) {
                if (bestValue == Float.NEGATIVE_INFINITY)
                    return Float.NEGATIVE_INFINITY * this.color * current_color;
                break;
            }
            float val = -negamax(child, started, -current_color, -beta, -alpha, depth - 1);
            bestValue = Math.max(bestValue, val);
            alpha = Math.max(alpha, bestValue);
            if (alpha >= beta)
                break;
        }

        TableEntry.FlagValue tableEntryFlag;

        if (bestValue <= alphaOrig)
            tableEntryFlag = TableEntry.FlagValue.UPPER_BOUND;
        else if (bestValue >= beta)
            tableEntryFlag = TableEntry.FlagValue.LOWER_BOUND;
        else
            tableEntryFlag = TableEntry.FlagValue.EXACT;

        tableEntry = new TableEntry(bestValue, tableEntryFlag, depth);

        traspositionTable.setItem(node.getId(), tableEntry);
        return bestValue;
    }
}
