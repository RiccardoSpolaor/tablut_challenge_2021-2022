package it.unibo.ai.didattica.competition.tablut.dualCore.ai;

import it.unibo.ai.didattica.competition.tablut.domain.Action;
import it.unibo.ai.didattica.competition.tablut.domain.State;
import it.unibo.ai.didattica.competition.tablut.dualCore.game.GameHandler;

public class Node {
    private State state;
    private State parentState;

    private Action parentOperation;
    private int Id;

    private Float heuristicValue;

    public Node(State state, State parentState, Action parentOperation) {
        this.state = state;
        this.parentState = parentState;
        this.parentOperation = parentOperation;
        this.Id = this.hashCode();
    }

    public State getState() {
        return state;
    }

    public void setState(State state) {
        this.state = state;
    }

    public State getParentState() {
        return parentState;
    }

    public void setParentState(State parentState) {
        this.parentState = parentState;
    }

    public Action getParentOperation() {
        return parentOperation;
    }

    public void setParentOperation(Action parentOperation) {
        this.parentOperation = parentOperation;
    }

    public int getId() {
        return Id;
    }

    public void setId(int id) {
        Id = id;
    }

    public Float getHeuristicValue() {
        return heuristicValue;
    }

    public void setHeuristicValue(Float heuristicValue) {
        this.heuristicValue = heuristicValue;
    }

    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + this.state.hashCode();
        result = prime * result + this.parentState.hashCode();
        return result;
    }

    public Integer isTerminal() {
        return GameHandler.checkVictory(this.state.getBoard());
    }
}
