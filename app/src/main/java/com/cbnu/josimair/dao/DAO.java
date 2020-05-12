package com.cbnu.josimair.dao;

import androidx.room.*;

import com.cbnu.josimair.Model.IndoorAir;
import com.cbnu.josimair.Model.IndoorAirGroup;
import com.cbnu.josimair.Model.Timetable;

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

    @Insert
    void insertAll(Timetable... timetables);

    @Query("SELECT count(*) FROM " +
            "Timetable "
    )
    int isRaady();

    @Delete
    void delete(IndoorAir indoorAir);

    @Query(" SELECT strftime('%H',time) as time, avg(value) as value " +
            "FROM indoorair " +
            "WHERE time Between :from and :to " +
            "GROUP BY strftime('%m%d%H',time) " +
            "ORDER BY strftime('%m%d%H',time) DESC " +
            "LIMIT 24  ")
    List<IndoorAirGroup> getGroupByHourBetweenDates(Date from, Date to);

    @Query("SELECT timetable.h as time, origin.v as value " +
            "FROM " +
            "   (SELECT 0 as h " +
            "    union select 1 union select 6 union select 11 union select 16 union select 21 " +
            "    union select 2 union select 7 union select 12 union select 17 union select 22 " +
            "    union select 3 union select 8 union select 13 union select 18 union select 23 " +
            "    union select 4 union select 9 union select 14 union select 19 " +
            "    union select 5 union select 10 union select 15 union select 20 " +
            "   ) as timetable " +
            "LEFT OUTER JOIN" +
            "   (SELECT cast(strftime('%H',time) as integer) as h, avg(value) as v " +
            "    FROM indoorair " +
            "    WHERE time Between :from and :to " +
            "    GROUP BY strftime('%m%d%H',time) " +
            "    ORDER BY strftime('%m%d%H',time) DESC " +
            "    LIMIT 24 " +
            "   ) as origin " +
            "ON timetable.h = origin.h")
    List<IndoorAirGroup> getGroupByHourBetweenDatesWithTimeTable(Date from, Date to);

    @Query("SELECT * FROM " +
            "   (SELECT strftime('%m.%d',time) as time, avg(value) as value " +
            "    FROM indoorair " +
            "    WHERE time <= :to " +
            "    GROUP BY strftime('%m%d',time) " +
            "    ORDER BY strftime('%m%d',time) DESC " +
            "    LIMIT 7) " +
            "ORDER BY time ASC"
    )
    List<IndoorAirGroup> getGroupByDayBetweenDates(Date to);

    @Query("SELECT t.time as time, origin.v as value " +
            "FROM " +
            "   (SELECT strftime('%m.%d',time) as time " +
            "    FROM timetable " +
                "WHERE time Between :from and :to " +
            "    GROUP BY strftime('%m.%d',time) " +
            "    ORDER BY time ASC " +
            "    LIMIT 7 " +
            ") as t " +
            "LEFT OUTER JOIN" +
            "   (SELECT strftime('%m.%d',time) as time, avg(value) as v " +
            "    FROM IndoorAir " +
            "    GROUP BY strftime('%m.%d',time)" +
            "    LIMIT 7 " +
            ") as origin " +
            "ON t.time = origin.time "
    )
    List<IndoorAirGroup> getGroupByDayBetweenDatesWithTimeTable(Date from, Date to);



    @Query("SELECT * FROM " +
            "(SELECT strftime('%m%d',time) as time, avg(value) as value " +
            "FROM indoorair " +
            "WHERE time <= :to " +
            "GROUP BY strftime('%W',time) " +
            "ORDER BY strftime('%W',time) DESC " +
            "LIMIT 7 ) " +
            "ORDER BY time ASC ")
    List<IndoorAirGroup> getGroupByWeekBetweenDates(Date to);

    @Query("SELECT t.time as time, origin.v as value " +
            "FROM " +
            "   (SELECT strftime('%m.%d',time) as time " +
            "    FROM timetable " +
            "WHERE time Between :from and :to " +
            "    GROUP BY strftime('%W',time) " +
            "    ORDER BY time ASC " +
            "    LIMIT 7 " +
            ") as t " +
            "LEFT OUTER JOIN" +
            "   (SELECT strftime('%m.%d',time) as time, avg(value) as v " +
            "    FROM IndoorAir " +
            "    WHERE time Between :from and :to " +
            "    GROUP BY strftime('%W',time)" +
            ") as origin " +
            "ON t.time = origin.time "
    )
    List<IndoorAirGroup> getGroupByDayBetweenWeeksWithTimeTable(Date from, Date to);

}





