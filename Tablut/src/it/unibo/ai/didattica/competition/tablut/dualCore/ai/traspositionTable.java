package it.unibo.ai.didattica.competition.tablut.dualCore.ai;

import java.util.SortedMap;
import java.util.TreeMap;

public class TraspositionTable {
    SortedMap<Integer,TableEntry> dictionary; // initializzare
    Integer size;

    public TraspositionTable(Integer size) {
        this.dictionary = new TreeMap<Integer,TableEntry>();
        this.size = size;
    }
    
    public TableEntry getItem(Integer key) {
        try {
            return this.dictionary.get(key);
        } catch (Exception e) {
            return null;
        }
    }

    public void setItem(Integer key, TableEntry value) {
        this.dictionary.put(key, value);
        if (this.dictionary.size() > size){
            this.dictionary.remove(this.dictionary.firstKey());
        }
    }

}
