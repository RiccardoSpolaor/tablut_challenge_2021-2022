package it.unibo.ai.didattica.competition.tablut.dualCore.ai;

import java.util.SortedMap;
import java.util.TreeMap;

public class traspositionTable {
    SortedMap<Integer,tableEntry> dictionary; // initializzare
    Integer size;

    public traspositionTable(Integer size) {
        this.dictionary = new TreeMap<Integer,tableEntry>();
        this.size = size;
    }
    
    public tableEntry getItem(Integer key) {
        try {
            return this.dictionary.get(key);
        } catch (Exception e) {
            return null;
        }
    }

    public void setItem(Integer key, tableEntry value) {
        this.dictionary.put(key, value);
        if (this.dictionary.size() > size){
            this.dictionary.remove(this.dictionary.firstKey());
        }
    }

}
