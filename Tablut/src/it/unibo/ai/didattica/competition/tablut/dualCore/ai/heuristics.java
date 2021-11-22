package it.unibo.ai.didattica.competition.tablut.dualCore.ai;

import java.util.HashMap;
import java.util.Map;

public class heuristics {
    
    Integer BLACK_TOTAL = 16;
    Integer WHITE_TOTAL = 9;

    Map<String, Float> WHITE_WEIGHTS = new HashMap<String, Float>();
    Map<String, Float> BLACK_WEIGHTS = new HashMap<String, Float>();
    
    public heuristics() {
        // EURISTICHE BLACK
        this.BLACK_WEIGHTS.put("black_near_king", 0.1f);
        this.BLACK_WEIGHTS.put("black_around_king", 0.4f);
        this.BLACK_WEIGHTS.put("white_captured_proportion", 0.5f);
        // EURISTICHE WHITE
        this.WHITE_WEIGHTS.put("king_position", 0.1f);
        this.WHITE_WEIGHTS.put("open_paths_to_victory", 0.4f);
        this.WHITE_WEIGHTS.put("black_captured_proportion", 0.5f);
    }

    public Float normalizedHeuristicValue(){
        return null;
    }

    public Float getWhiteHeuristicValue(){
        return null;
    }

    public Float getBlackHeuristicValue(){
        return null;
    }

    public Float getHeuristicValue(){
        return null;
    }
    
}
