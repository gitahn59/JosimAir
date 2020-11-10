package com.cbnu.josimair.dao;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

import com.cbnu.josimair.Model.util.Converters;
import com.cbnu.josimair.Model.entity.IndoorAir;
import com.cbnu.josimair.Model.entity.Timetable;

/**
 * Sqlite Database를 관리하는 Class
 *
 */
@Database(entities = {IndoorAir.class, Timetable.class}, version = 1)
@TypeConverters({Converters.class})
public abstract class AppDatabase extends RoomDatabase {
    public abstract DAO getDao();

    private static Object lock = new Object();
    private static AppDatabase instance;

    public static AppDatabase getInstance(Context context) {
        synchronized (lock) {
            if (instance == null) {
                instance = Room.databaseBuilder(context.getApplicationContext(), AppDatabase.class, "josimAirTest3").build();
            }
            return instance;
        }
    }
}