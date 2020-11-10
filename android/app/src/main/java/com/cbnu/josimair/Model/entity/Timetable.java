package com.cbnu.josimair.Model.entity;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

import com.cbnu.josimair.Model.util.Converters;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

@Entity
public class Timetable {
    @PrimaryKey
    @TypeConverters({Converters.class})
    @ColumnInfo(name = "time")
    @NonNull
    public Date time;
    public Date getTime() {
        return time;
    }
    public void setTime(Date time) {
        this.time = time;
    }

    public static List<Timetable> makeTimetalbes(){
        List<Timetable> dates = new ArrayList<>();
        Calendar from = Calendar.getInstance();
        from.add(Calendar.MONTH, -50);
        Calendar to = Calendar.getInstance();
        to.add(Calendar.MONTH, 50);

        while(true){
            Timetable t = new Timetable();
            t.setTime(from.getTime());
            dates.add(t);
            from.add(Calendar.DATE,1);
            if(to.compareTo(from) < 0){
                break;
            }
        }

        return dates;
    }


}
