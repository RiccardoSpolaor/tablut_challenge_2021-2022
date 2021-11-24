package it.unibo.ai.didattica.competition.tablut.dualCore.ai;

import java.util.SortedMap;
import java.util.TreeMap;

public class HeuristicsTable {

    SortedMap<Integer, Float> dictionary; // initializzare
    Integer size;

    boolean isAdding = false, isGetting = false;

    public HeuristicsTable(Integer size) {
        this.dictionary = new TreeMap<Integer, Float>();
        this.size = size;
    }

    public synchronized Float getItem(Integer key) {
        while (isAdding || isGetting) {
            try {
                wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        isGetting = true;
        Float val;

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

    public synchronized void setItem(Integer key, Float value) {
        while (isAdding || isGetting) {
            try {
                wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        isAdding = true;

        this.dictionary.put(key, value);
        if (this.dictionary.size() > size) {
            System.out.println("T size " + dictionary.size());
            this.dictionary.remove(this.dictionary.firstKey());
        }

        isAdding = false;
        notifyAll();
    }

}