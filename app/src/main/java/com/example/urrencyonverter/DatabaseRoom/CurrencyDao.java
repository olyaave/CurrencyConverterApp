package com.example.urrencyonverter.DatabaseRoom;

import androidx.room.*;
import com.example.urrencyonverter.currencyPair;

import java.util.ArrayList;
import java.util.List;

@Dao
public interface CurrencyDao {
    @Query("SELECT * FROM currencyPair")
    List<currencyPair> getAll();

    @Query("SELECT * FROM currencyPair WHERE pairsName LIKE :first LIMIT 1")
    currencyPair findByName(String first);

    @Insert
    void insertAll(ArrayList<currencyPair> pairs);

    @Insert
    void insert(currencyPair pair);

    @Update
    void update(currencyPair pair);

    @Delete
    void delete(currencyPair pair);


}
