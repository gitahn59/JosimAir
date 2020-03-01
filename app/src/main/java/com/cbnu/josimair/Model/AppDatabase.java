package com.cbnu.josimair.Model;

import androidx.room.Database;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

import com.cbnu.josimair.dao.DAO;

@Database(entities = {IndoorAir.class}, version = 1)
@TypeConverters({Converters.class})
public abstract class AppDatabase extends RoomDatabase {
    public abstract DAO indoorAirDao();
}