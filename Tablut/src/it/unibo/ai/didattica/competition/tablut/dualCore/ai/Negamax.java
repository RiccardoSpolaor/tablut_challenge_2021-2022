package it.unibo.ai.didattica.competition.tablut.dualCore.ai;

import it.unibo.ai.didattica.competition.tablut.domain.Action;
import it.unibo.ai.didattica.competition.tablut.domain.State;
import it.unibo.ai.didattica.competition.tablut.dualCore.game.GameHandler;

import java.util.*;
import java.util.concurrent.*;

public final class Negamax {

    private static final int TABLE_SIZE = 1_000_000;
    private static final int DEPTH = 4;

    private final int color;
    private final int timeout;
    private final HeuristicsTable heuristicsTable;
    private final TraspositionTable traspositionTable;
    private final ExecutorService executorService = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
    private final ConcurrentCounter counter = new ConcurrentCounter();

    public Negamax(int color, int timeout) {
        this.color = color;
        this.timeout = timeout - 1;
        traspositionTable = new TraspositionTable(TABLE_SIZE);
        heuristicsTable = new HeuristicsTable(TABLE_SIZE);
    }

    private static class ConcurrentCounter {
        private int count = 0;
        private boolean waiting = false;

        public synchronized void increase() {
            while (waiting) {
                try {
                    wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            waiting = true;
            count++;
            waiting = false;
            notifyAll();
        }
        public void reset() {
            count = 0;
        }
        public int getCount(){
            return count;
        }
    }

    private class NegamaxThread implements Callable<NegamaxResult> {
        private final Node node;
        private final Long started;

        private NegamaxThread(Node node, Long started) {
            super();
            this.node = node;
            this.started = started;
        }

        @Override
        public NegamaxResult call() {
            return parallelNegamax(node, started);
        }
    }

    private static class NegamaxResult {
        private final Action action;
        private final Float value;

        public NegamaxResult(Action action, Float value) {
            this.action = action;
            this.value = value;
        }

        public Action getAction() {
            return action;
        }

        public Float getValue() {
            return value;
        }
    }

    public Action findBestMove(State state) {
        traspositionTable.clear();
        counter.reset();
        Node root = new Node(state, state, null);

        // ExecutorService executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());

        ArrayList<Action> availableActions = GameHandler.getAvailableActions(root.getState().getBoard(),
                root.getState());
        ArrayList<Node> childNodes = getSortedChildNodes(root, availableActions, color);

        ArrayList<Future<NegamaxResult>> futureResults = new ArrayList<>(childNodes.size());

        long started = System.currentTimeMillis();

        for (Node child : childNodes) {
            futureResults.add(executorService.submit(new NegamaxThread(child, started)));
        }

        float bestValue = Float.NEGATIVE_INFINITY;

        NegamaxResult bestResult = null;
        List<NegamaxResult> results = new ArrayList<NegamaxResult>();
        for (int i = 0; i < childNodes.size(); i++) {
            try {
                if (((System.currentTimeMillis() - started) / 1000) >= timeout) {
                    break;
                }
                NegamaxResult actualResult = futureResults.get(i).get(timeout - (System.currentTimeMillis() - started) / 1000, TimeUnit.SECONDS );
                results.add(actualResult);
                float actualValue = actualResult.getValue();
                if (actualValue > bestValue) {
                    bestResult = actualResult;
                    bestValue = actualValue;
                    
                }
            } catch (Exception ignored) {}
        }

        if (bestResult == null) {
            Random random = new Random();
            return availableActions.get(random.nextInt(availableActions.size()));
        }
        // executorService.shutdownNow();
        return bestResult.getAction();
    }

    private NegamaxResult parallelNegamax(Node node, Long started) {
        if (Thread.interrupted()) {
            Thread.currentThread().interrupt();
            return new NegamaxResult(node.getParentOperation(), Float.NEGATIVE_INFINITY);
        }
        return new NegamaxResult(node.getParentOperation(),
                -this.negamax(node, started, -color, Float.NEGATIVE_INFINITY, Float.POSITIVE_INFINITY, DEPTH));
    }

    private ArrayList<Node> getSortedChildNodes(Node root, ArrayList<Action> availableActions, int current_color) {
        ArrayList<Node> childNodes = new ArrayList<>(availableActions.size());

        for (Action action : availableActions) {
            Node n = new Node(GameHandler.applyOperation(root.getState(), action), root.getState(), action);
            Float heuristic = heuristicsTable.getItem(n.getId());
            if (heuristic == null) {
                heuristic = Heuristics.getHeuristicValue(n.getState().getBoard(), n.getParentState().getBoard(), current_color);
                heuristicsTable.setItem(n.getId(), heuristic);
            }
            n.setHeuristicValue(heuristic);
            childNodes.add(n);
        }

        childNodes.sort((o1, o2) -> {
            if (Objects.equals(o1.getHeuristicValue(), o2.getHeuristicValue()))
                return 0;
            if (o1.getHeuristicValue() > o2.getHeuristicValue()) {
                return -current_color;
            } else {
                return current_color;
            }
        });

        return childNodes;
    }

    private Float negamax(Node node, Long started, int current_color, Float alpha, Float beta, int depth) {
        
        if (Thread.interrupted()) {
            Thread.currentThread().interrupt();
            return Float.NEGATIVE_INFINITY * this.color * current_color;
        }
        Float alphaOrig = alpha;

        TableEntry tableEntry = traspositionTable.getItem(node.getId());

        if (tableEntry != null && tableEntry.depth >= depth) {
            if (tableEntry.flag.equals(TableEntry.FlagValue.EXACT)) {
                return tableEntry.value;
            } else if (tableEntry.flag.equals(TableEntry.FlagValue.LOWER_BOUND))
                alpha = Math.max(alpha, tableEntry.value);

            else if (tableEntry.flag.equals(TableEntry.FlagValue.UPPER_BOUND))
                beta = Math.min(beta, tableEntry.value);

            if (alpha >= beta)
                return tableEntry.value;
        }

        /* Draw */
        if (GameHandler.getPreviousStates().contains(node.getId())) {
            return 0f;
        }

        int victory = node.isTerminal();

        if (Math.abs(victory) == 1) {
            return current_color * victory * Math.max(depth, 1f);
        }

        if (depth == 0) {
            counter.increase();
            return current_color * node.getHeuristicValue();
        }

        ArrayList<Action> availableActions = GameHandler.getAvailableActions(node.getState().getBoard(),
                node.getState());
        ArrayList<Node> childNodes = getSortedChildNodes(node, availableActions, current_color);
        
        float bestValue = Float.NEGATIVE_INFINITY;

        for (Node child : childNodes) {
            if (((System.currentTimeMillis() - started) / 1000) >= timeout) {
                if (bestValue == Float.NEGATIVE_INFINITY)
                    return Float.NEGATIVE_INFINITY * this.color * current_color;
                break;
            }
            float val = -negamax(child, started, -current_color, -beta, -alpha, depth - 1);
            
            bestValue = Math.max(bestValue, val);
            alpha = Math.max(alpha, bestValue);
            if (alpha >= beta) {
                break;
            }
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
