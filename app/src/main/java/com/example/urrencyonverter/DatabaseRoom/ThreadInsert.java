package com.example.urrencyonverter.DatabaseRoom;

import android.database.sqlite.SQLiteConstraintException;
import android.util.Log;
import com.example.urrencyonverter.currencyPair;

import java.util.Date;

public class ThreadInsert extends RoomThread {

    public ThreadInsert(AppDatabase database) {
        super(database);
    }

    @Override
    public synchronized void run() {
        int size = db.currencyDao().getAll().size();
        try {
            for (currencyPair pair : pairs) {
                pair.id = size;
                pair.time = new Date().getTime();
                if (checkCollision(pair))
                    db.currencyDao().update(pair);
                else {
                    db.currencyDao().insert(pair);
                    size++;
                }
            }
        } catch (SQLiteConstraintException e) {
            Log.w(TAG_DB_insert, "Не удалось записать данные в базу данных.");
        }
        printDatabase();
        System.out.println("Thread " + threadName + " exiting.");
    }

    private boolean checkCollision(currencyPair pair){
        currencyPair otherPair;
        return (otherPair = db.currencyDao().findByName(pair.fromToName)) != null;
    }

    private static final String TAG_DB_insert = "DatabaseINFO: ";
}