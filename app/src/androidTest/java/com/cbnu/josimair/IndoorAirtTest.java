package com.cbnu.josimair;

import android.content.Context;

import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner;
import androidx.test.platform.app.InstrumentationRegistry;

import com.cbnu.josimair.Model.IndoorAir;
import com.cbnu.josimair.Model.StatisticsService;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Calendar;
import java.util.Date;

import static org.junit.Assert.assertEquals;

@RunWith(AndroidJUnit4ClassRunner.class)
public class IndoorAirtTest {
    @Test
    public void 나쁜_실내공기_감지_테스트() {

        // 나쁜 실내공기 15개 추가
        for(int i=0; i<15; i++){
            IndoorAir air = new IndoorAir(750);
            IndoorAir.setLastKnownIndoorAir(air);
        }

        // 좋은 실내공기 5개 추가
        for(int i=0; i<5; i++){
            IndoorAir air = new IndoorAir(100);
            IndoorAir.setLastKnownIndoorAir(air);
        }

        // badCount 15 - 5 = 10 확인
        assertEquals(0, IndoorAir.getBadCount());
    }
}
