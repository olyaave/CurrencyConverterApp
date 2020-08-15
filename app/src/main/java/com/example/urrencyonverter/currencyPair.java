package com.example.urrencyonverter;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class currencyPair {

        public currencyPair(String fromToName, Double value){
                id = 0;
                this.fromToName = fromToName;
                this.value = value;
                time = 0;
        }
        @PrimaryKey
        public int id;

        @ColumnInfo(name = "pairsName")
        public String fromToName;

        @ColumnInfo(name = "value")
        public Double value;

        @ColumnInfo(name = "time")
        public long time;

}
