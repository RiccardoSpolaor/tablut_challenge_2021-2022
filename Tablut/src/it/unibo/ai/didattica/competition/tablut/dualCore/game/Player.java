package it.unibo.ai.didattica.competition.tablut.dualCore.game;

import it.unibo.ai.didattica.competition.tablut.domain.Action;
import it.unibo.ai.didattica.competition.tablut.domain.State;
import it.unibo.ai.didattica.competition.tablut.dualCore.utils.PlayerRoleMapper;
import java.io.IOException;
import java.util.ArrayList;

public class Player {
    private final ArrayList<Integer> previousStates = new ArrayList<>();
    private final State.Turn turn;
    private final int color;
    // private final Negamax

    // Todo initialize negamax
    public Player(State.Turn turn) {
        this.turn = turn;
        this.color = PlayerRoleMapper.getColor(turn);
    }

    // Todo: implement negamax return value
    public final Action chooseMove(State stateType) {
        // bestMove = self.negamax.find_best_move(Node(state))
        // return MoveHasher.format_move(best_move)
        Action action = null;
        try {
            action = new Action("a4", "a5", turn);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return action;
    }

    public final void saveState(State state) {
        this.previousStates.add(state.hashCode());
    }
}
