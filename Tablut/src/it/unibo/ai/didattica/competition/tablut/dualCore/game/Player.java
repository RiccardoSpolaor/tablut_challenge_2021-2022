package it.unibo.ai.didattica.competition.tablut.dualCore.game;

import it.unibo.ai.didattica.competition.tablut.domain.Action;
import it.unibo.ai.didattica.competition.tablut.domain.State;
import it.unibo.ai.didattica.competition.tablut.dualCore.ai.Negamax;
import it.unibo.ai.didattica.competition.tablut.dualCore.utils.PlayerRoleMapper;
import java.io.IOException;
import java.util.ArrayList;

public final class Player {
    // private final State.Turn turn;
    // private final int color;
    // private final int timeout;
    private final Negamax negamax;

    // Todo initialize negamax
    public Player(State.Turn turn, int timeout) {
        // this.turn = turn;
        int color = PlayerRoleMapper.getColor(turn);
        negamax = new Negamax(color, timeout);
    }

    // Todo: implement negamax return value pass to findBestMove
    public Action chooseMove(State state) {
        return negamax.findBestMove(state);
    }

    public void saveState(State state) {
        GameHandler.updatePreviousStates(state);
    }
}
