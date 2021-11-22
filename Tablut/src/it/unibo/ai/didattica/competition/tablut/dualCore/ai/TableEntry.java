package it.unibo.ai.didattica.competition.tablut.dualCore.ai;

public class TableEntry {

    public enum FlagValue {
        EXACT,
        LOWER_BOUND,
        UPPER_BOUND,
    }

    Float value;
    FlagValue flag;
    Integer depth;
    
    public TableEntry(Float value, FlagValue flag, Integer depth) {
        this.value = value;
        this.flag = flag;
        this.depth = depth;
    }
    
}
