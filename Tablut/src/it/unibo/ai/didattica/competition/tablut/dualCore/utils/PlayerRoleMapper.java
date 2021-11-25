package it.unibo.ai.didattica.competition.tablut.dualCore.utils;

import it.unibo.ai.didattica.competition.tablut.domain.State;

public class PlayerRoleMapper {
    public static int getColor(State.Turn role) throws IllegalArgumentException {
        if (role.equals(State.Turn.WHITE))
            return 1;
        else if (role.equals(State.Turn.BLACK))
            return  -1;

        throw new IllegalArgumentException("The player color should be white or black.");
    }
}
