package it.unibo.ai.didattica.competition.tablut.dualCore.ai;

import java.util.SortedMap;
import java.util.TreeMap;

public class HeuristicsTable {

    SortedMap<Integer, Float> dictionary; // initializzare
    Integer size;

    public HeuristicsTable(Integer size) {
        this.dictionary = new TreeMap<Integer, Float>();
        this.size = size;
    }

    public Float getItem(Integer key) {
        try {
            return this.dictionary.get(key);
        } catch (Exception e) {
            return null;
        }
    }

    public void setItem(Integer key, Float value) {
        this.dictionary.put(key, value);
        if (this.dictionary.size() > size) {
            this.dictionary.remove(this.dictionary.firstKey());
        }
    }

}