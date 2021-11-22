package it.unibo.ai.didattica.competition.tablut.dualCore.ai;

import it.unibo.ai.didattica.competition.tablut.domain.Action;
import it.unibo.ai.didattica.competition.tablut.domain.State;
import java.util.ArrayList;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.RecursiveTask;

public final class Negamax {
    private static final int TABLE_SIZE = 1_000_000;

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

    public class NegamaxResult {
        private Action action;
        private Float value;

        public NegamaxResult() {
        };

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

    public final Action findBestMove(State state, ArrayList<Integer> previousStates) {

        ExecutorService executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());

        ArrayList<Node> childNodes = new ArrayList<Node>();
        ArrayList<Future<NegamaxResult>> futureResults = new ArrayList<>(childNodes.size());

        try {
            for (Node child : childNodes) {
                Future<NegamaxResult> result = executor.submit(new Callable<NegamaxResult>() {
                    @Override
                    public NegamaxResult call() {
                        NegamaxResult value = parallelNegamax();
                        return value;
                    }
                });
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

    private Float negamax(Node node, Float started, int color, Float alpha, Float beta, int depth,
            ArrayList<Integer> previousStates) {
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
        if (previousStates.contains(node.getId()))
            return 0f;

        int victory = node.isTerminal();

        if (Math.abs(victory) == 1)
            return color * victory * Math.max(depth, 1f);

        if (depth == 0)
            return color * node.getHeuristicValue();

        List<Action> availableActions = GameHandler.get_available_actions(node.state.board, color);
        List<Node> childNodes = new ArrayList<>(availableActions.size());

        for (Action action : availableActions) {
            Node n = new Node(GameHandler.applyOperation(node.getState(), node.getParentOperation()), node, action);
            Float heuristic = heuristicsTable.getItem(n.getId());
            if (heuristic == null) {
                heuristic = Heuristics.getWhiteHeuristicValue(n.getState().getBoard(), n.getParentState().getBoard());
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
                return color;
            } else {
                return -color;
            }
        });

        float bestValue = Float.MIN_VALUE;
        for (Node child : childNodes) {
            if (Instant.now().getEpochSecond() - started >= timeout) {
                if (bestValue == Float.MIN_VALUE)
                    return Float.MIN_VALUE * this.color * color;
                break;
            }
            float val = -negamax(child, started, -color, -beta, -alpha, depth - 1, previousStates);
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
