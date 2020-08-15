package com.example.urrencyonverter.DatabaseRoom;

import com.example.urrencyonverter.currencyPair;

import java.util.ArrayList;
import java.util.Date;

public class RoomThread extends Thread {
    private static final String TAG_DB = "DatabaseINFO: ";
    private static final long RELEVANT_TIME = 3600000;

    public RoomThread(AppDatabase database) {
        threadName = "DB_write_thread";
        db = database;
    }

    @Override
    public synchronized void start() {
        if (thread == null) {
            System.out.println("Thread " + threadName + " starting.");
            thread = new Thread(this, threadName);
            thread.start();
        }
    }

    public void addList(ArrayList<currencyPair> pairs) {
        this.pairs = pairs;
    }

    public boolean isRelevant(String fromToName){
        currencyPair pair = db.currencyDao().findByName(fromToName);
        return (new Date().getTime()) - pair.time < RELEVANT_TIME;
    }

    protected void printDatabase(){
        for (currencyPair pair : db.currencyDao().getAll()) {
            System.out.println( "\n" + pair.id + " " + pair.fromToName + " " + pair.value + " time: " + pair.time);
        }
    }

    protected ArrayList<currencyPair> pairs;
    protected Thread thread;
    protected final String threadName;
    protected final AppDatabase db;
}
// сделать функцию с использованием isReleted