package com.cbnu.josimair;

import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner;

import com.cbnu.josimair.Model.entity.IndoorAir;

import org.junit.Test;
import org.junit.runner.RunWith;

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
        assertEquals(10, IndoorAir.getBadCount());
    }
}
