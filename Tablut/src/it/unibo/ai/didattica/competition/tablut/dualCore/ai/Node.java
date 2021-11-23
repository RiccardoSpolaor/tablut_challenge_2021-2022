package it.unibo.ai.didattica.competition.tablut.dualCore.ai;

import it.unibo.ai.didattica.competition.tablut.domain.Action;
import it.unibo.ai.didattica.competition.tablut.domain.State;
import it.unibo.ai.didattica.competition.tablut.dualCore.game.GameHandler;

import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.security.*;

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
        String toHash = this.state.toString() + this.parentState.toString();

        try {
            byte[] bytesOfMessage = toHash.getBytes("UTF-8");
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] theMD5digest = md.digest(bytesOfMessage);
            return ByteBuffer.wrap(theMD5digest).getInt();
        } catch (UnsupportedEncodingException | NoSuchAlgorithmException e) {
            return (int) System.currentTimeMillis();
        }
    }

    public Integer isTerminal() {
        return GameHandler.checkVictory(this.state.getBoard());
    }
}
