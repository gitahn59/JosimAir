package com.cbnu.josimair.dao;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;

import androidx.room.*;

import com.cbnu.josimair.Model.IndoorAir;
import com.cbnu.josimair.Model.IndoorAirGroup;

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

    @Query("SELECT strftime('%m.%d',time) as time, avg(value) as value " +
            "FROM indoorair " +
            "WHERE time <= :to " +
            "GROUP BY strftime('%m%d',time) " +
            "ORDER BY strftime('%m%d',time)" +
            "LIMIT 7 ")
    List<IndoorAirGroup> getGroupByDayBetweenDates(Date to);

    @Query("SELECT strftime('%d%H',time) as time, avg(value) as value " +
            "FROM indoorair " +
            "WHERE time <= :to " +
            "GROUP BY strftime('%m%d%H',time) " +
            "ORDER BY strftime('%m%d%H',time) " +
            "LIMIT 24 ")
    List<IndoorAirGroup> getGroupByHourBetweenDates(Date to);

    @Query("SELECT strftime('%M',time) as time, avg(value) as value " +
            "FROM indoorair " +
            "WHERE time <= :to " +
            "GROUP BY strftime('%W',time) " +
            "ORDER BY strftime('%W',time) " +
            "LIMIT 24 ")
    List<IndoorAirGroup> getGroupByWeekBetweenDates(Date to);
}





