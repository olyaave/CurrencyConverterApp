package com.example.urrencyonverter.DatabaseRoom;

import android.database.sqlite.SQLiteConstraintException;
import android.util.Log;
import com.example.urrencyonverter.currencyPair;

public class ThreadDeleting extends RoomThread {
    public ThreadDeleting(AppDatabase database) {
        super(database);
    }
    @Override
    public synchronized void run() {
        try {
            for (currencyPair o : db.currencyDao().getAll()) {
                db.currencyDao().delete(o);
            }
        } catch (SQLiteConstraintException e) {
            Log.w(TAG_DB_delete, "Не удалось очистить базу данных.");
        }

        System.out.println("Wrote pair:" + db.currencyDao().getAll());
        System.out.println("Thread " + threadName + " exiting.");
    }

    private static final String TAG_DB_delete = "Database_delete_INFO: ";

}
