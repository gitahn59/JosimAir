package com.cbnu.josimair;

import android.content.Context;
import android.location.Location;

import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner;
import androidx.test.platform.app.InstrumentationRegistry;

import com.cbnu.josimair.Model.entity.OutdoorAir;
import com.cbnu.josimair.Model.service.RestAPIService;

import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertEquals;

@RunWith(AndroidJUnit4ClassRunner.class)
public class RestAPIServiceUnitTest {
    @Test
    public void GPS_To_TMLocation_변환_테스트() {
        Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
        RestAPIService service = RestAPIService.getInstance(appContext,null);
        Location last = new Location("송파구청");
        last.setLatitude(37.4980326);
        last.setLongitude(127.1151262);
        RestAPIService.TMLocation actual = service.getTMLocation(last);
        RestAPIService.TMLocation expected = new RestAPIService.TMLocation(210110.6843246899,443986.46683131345);
        assertEquals(expected.getX(), actual.getX(),0.001);
        assertEquals(expected.getY(), actual.getY(),0.001);
    }

    @Test
    public void 최근접_관측소_정보_수신_테스트(){
        RestAPIService.TMLocation tm = new RestAPIService.TMLocation(210110.6843246899,443986.46683131345);
        Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
        RestAPIService service = RestAPIService.getInstance(appContext,null);

        String actual = service.getNearByStationInfo(tm);
        assertEquals("송파구",actual);
    }

    @Test
    public void 실외_대기정보_수신_테스트() {
        Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
        RestAPIService service = RestAPIService.getInstance(appContext,null);
        OutdoorAir actual = service.getOutdoorAir("송파구");
        assertEquals("송파구", actual.getStationName());
    }
}
