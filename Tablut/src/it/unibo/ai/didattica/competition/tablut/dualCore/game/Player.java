package it.unibo.ai.didattica.competition.tablut.dualCore.game;

import it.unibo.ai.didattica.competition.tablut.domain.Action;
import it.unibo.ai.didattica.competition.tablut.domain.State;
import it.unibo.ai.didattica.competition.tablut.dualCore.ai.Negamax;
import it.unibo.ai.didattica.competition.tablut.dualCore.utils.PlayerRoleMapper;

public final class Player {
    private final Negamax negamax;

    public Player(State.Turn turn, int timeout) {
        int color = PlayerRoleMapper.getColor(turn);
        negamax = new Negamax(color, timeout);
    }

    public Action chooseMove(State state) {
        return negamax.findBestMove(state);
    }

    public void saveState(State state) {
        GameHandler.updatePreviousStates(state);
    }
}
