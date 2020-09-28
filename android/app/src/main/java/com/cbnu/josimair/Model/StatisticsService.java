package com.cbnu.josimair.Model;

import android.content.Context;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class StatisticsService {
    private AppDatabase db;

    public StatisticsService(Context context) {
        this.db = AppDatabase.getInstance(context);
    }

    /**
     * 현재 시간(H)을 기준으로 정렬한다
     *
     * @param src 정렬 대상 List
     * @param now 현재 시간(H)
     */
    private ArrayList<IndoorAirGroup> sortOrderbyTime(List<IndoorAirGroup> src, int now){
        ArrayList<IndoorAirGroup> li = new ArrayList<IndoorAirGroup>();
        ArrayList<IndoorAirGroup> temp = new ArrayList<IndoorAirGroup>();
        for(IndoorAirGroup i : src){
            int h = Integer.parseInt(i.getDay());
            if(h<=now) temp.add(i);
            else li.add(i);
        }

        for(IndoorAirGroup i : temp) {
            li.add(i);
        }
        return li;
    }

    /**
     * 현재 시간을 기준으로 24시간의 실내 대기정보 통계 데이터를 가져온다.
     *
     */
    public List<IndoorAirGroup> getHourStatisticsData(){
        Calendar to = Calendar.getInstance();
        Calendar from = Calendar.getInstance();
        from.add(Calendar.HOUR, -24);
        List<IndoorAirGroup> li = db.indoorAirDao().getGroupByHourBetweenDatesWithTimeTable(from.getTime(),to.getTime());
        return sortOrderbyTime(li, from.get(Calendar.HOUR_OF_DAY));
    }

    /**
     * 현재 시간을 기준으로 7일간의 실내 대기정보 통계 데이터를 가져온다.
     *
     */
    public List<IndoorAirGroup> getDayStatisticsData(){
        Calendar from = Calendar.getInstance();
        from.add(Calendar.DATE,-7);
        from.set(Calendar.HOUR_OF_DAY,0);
        from.set(Calendar.MINUTE,0);
        from.set(Calendar.SECOND,0);

        Calendar to = Calendar.getInstance();
        to.add(Calendar.DATE,-1);
        to.set(Calendar.HOUR_OF_DAY,23);
        to.set(Calendar.MINUTE,59);
        to.set(Calendar.SECOND,59);

        return db.indoorAirDao().getGroupByDayBetweenDatesWithTimeTable(from.getTime(), to.getTime());
    }

    /**
     * 현재 시간을 기준으로 7주간의 실내 대기정보 통계 데이터를 가져온다.
     *
     */
    public List<IndoorAirGroup> getWeekStatisticsData(){
        Calendar from = Calendar.getInstance();
        from.add(Calendar.WEEK_OF_YEAR,-7);
        from.set(Calendar.HOUR_OF_DAY,0);
        from.set(Calendar.MINUTE,0);
        from.set(Calendar.SECOND,0);

        Calendar to = Calendar.getInstance();

        return db.indoorAirDao().getGroupByDayBetweenWeeksWithTimeTable(from.getTime(), to.getTime());
    }
}
