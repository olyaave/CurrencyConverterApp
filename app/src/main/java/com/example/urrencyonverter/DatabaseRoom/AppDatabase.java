package com.example.urrencyonverter.DatabaseRoom;


import androidx.room.Database;
import androidx.room.RoomDatabase;
import com.example.urrencyonverter.currencyPair;

@Database(entities = {currencyPair.class}, version = 1)
public abstract class AppDatabase extends RoomDatabase {
    public abstract CurrencyDao currencyDao();

}