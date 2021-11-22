package it.unibo.ai.didattica.competition.tablut.dualCore.ai;
import it.unibo.ai.didattica.competition.tablut.domain.Action;
import it.unibo.ai.didattica.competition.tablut.domain.State;
import java.util.ArrayList;

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

    public final Action findBestMove(State state, ArrayList<Integer> previousStates) {

    }
}
