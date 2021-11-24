package it.unibo.ai.didattica.competition.tablut.dualCore.ai;

import java.util.SortedMap;
import java.util.TreeMap;

public class TraspositionTable {
    SortedMap<Integer,TableEntry> dictionary; // initializzare
    Integer size;

    boolean isAdding = false, isGetting = false;

    public TraspositionTable(Integer size) {
        this.dictionary = new TreeMap<Integer,TableEntry>();
        this.size = size;
    }
    
    public synchronized TableEntry getItem(Integer key) {
        while (isAdding || isGetting) {
            try {
                wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        isGetting = true;
        TableEntry val;
        try {
            val = this.dictionary.get(key);
            isGetting = false;
            notifyAll();
            return val;
        } catch (Exception e) {
            val = null;
            isGetting = false;
            notifyAll();
            return val;
        }
    }

    public synchronized void setItem(Integer key, TableEntry value) {
        while (isAdding || isGetting) {
            try {
                wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        isAdding = true;

        this.dictionary.put(key, value);
        if (this.dictionary.size() > size){
            this.dictionary.remove(this.dictionary.firstKey());
        }

        isAdding = false;
        notifyAll();
    }

}
