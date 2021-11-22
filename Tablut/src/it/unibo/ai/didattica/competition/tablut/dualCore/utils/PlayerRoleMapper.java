package it.unibo.ai.didattica.competition.tablut.dualCore.utils;

import it.unibo.ai.didattica.competition.tablut.domain.State;

public class PlayerRoleMapper {
    public static int getColor(State.Turn role) throws IllegalArgumentException {
        return switch (role) {
            case WHITE -> 1;
            case BLACK -> -1;
            default -> throw new IllegalArgumentException("The player color should be white or black.");
        };
    }
}
