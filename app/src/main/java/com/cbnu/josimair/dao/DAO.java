package com.cbnu.josimair.dao;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;

import androidx.room.*;

import com.cbnu.josimair.Model.IndoorAir;

import java.util.Date;
import java.util.List;

@Dao
public interface DAO {
    @Query("SELECT * FROM indoorair")
    List<IndoorAir> getAll();

    @Query("SELECT * FROM indoorair WHERE time BETWEEN :from AND :to")
    List<IndoorAir> loadAllBetweenDates(Date from, Date to);

    @Query("DELETE FROM indoorair WHERE time BETWEEN :from AND :to")
    void deleteAllBetweenDates(Date from, Date to);

    @Insert
    void insertAll(IndoorAir... indoorAir);

    @Delete
    void delete(IndoorAir indoorAir);
}





