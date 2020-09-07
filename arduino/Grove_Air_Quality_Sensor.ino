/*
    Grove_Air_Quality_Sensor.ino
    Demo for Grove - Air Quality Sensor.

    Copyright (c) 2019 seeed technology inc.
    Author    : Lets Blu
    Created Time : Jan 2019
    Modified Time:

    The MIT License (MIT)

    Permission is hereby granted, free of charge, to any person obtaining a copy
    of this software and associated documentation files (the "Software"), to deal
    in the Software without restriction, including without limitation the rights
    to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
    copies of the Software, and to permit persons to whom the Software is
    furnished to do so, subject to the following conditions:

    The above copyright notice and this permission notice shall be included in
    all copies or substantial portions of the Software.

    THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
    IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
    FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
    AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
    LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
    OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
    THE SOFTWARE.
*/
#include <SoftwareSerial.h> //시리얼통신 라이브러리 호출

#include "Air_Quality_Sensor.h" 
int LedRed = 13;
int LedYellow = 9;
int LedG = 11;
int i, value=0;
char air[100];
int blueTx=2;   //Tx (보내는핀 설정)
int blueRx=3;   //Rx (받는핀 설정)
SoftwareSerial mySerial(blueTx, blueRx);  //시리얼 통신을 위한 객체선언
 
AirQualitySensor sensor(8); 

void setup(void) {
  pinMode(LedRed,OUTPUT);
  pinMode(LedYellow,OUTPUT);
  pinMode(LedG,OUTPUT); 
    Serial.begin(9600);
mySerial.begin(9600); //블루투스 시리얼
    while (!Serial);

    Serial.println("Waiting sensor to init...");
    delay(2000);

    if (sensor.init()) {
        Serial.println("Sensor ready.");
    } else {
        Serial.println("Sensor ERROR!");
    }
}

void loop(void) {
    int quality = sensor.slope();
    //digitalWrite(LedRed,HIGH);
    //delay(1000);
    //digitalWrite(LedRed,LOW);
    Serial.print("Sensor value: ");
    Serial.println(sensor.getValue()/10);

    if (quality == AirQualitySensor::FORCE_SIGNAL) {
        Serial.println("High pollution! Force signal active.");
        digitalWrite(LedG,HIGH);
        delay(1000);
        digitalWrite(LedG,LOW);
    } else if (quality == AirQualitySensor::HIGH_POLLUTION) {
        Serial.println("High pollution!");
        digitalWrite(LedYellow,HIGH);
        delay(1000);
        digitalWrite(LedYellow,LOW);
    } else if (quality == AirQualitySensor::LOW_POLLUTION) {
        Serial.println("Low pollution!");
    } else if (quality == AirQualitySensor::FRESH_AIR) {
        Serial.println("Fresh air.");
        digitalWrite(LedG,HIGH);
        delay(1000);
        digitalWrite(LedG,LOW);
    }
     if (mySerial.available()) {       
    Serial.write(mySerial.read());  //블루투스측 내용을 시리얼모니터에 출력
  }
  if (Serial.available()) {         
    mySerial.write(Serial.read());  //시리얼 모니터 내용을 블루추스 측에 WRITE
  } 
  //mySerial.print("vlaue = ";); 
  value = sensor.getValue();
  
   sprintf(air, "%d", value);
   
    Serial.print(air);
  
  mySerial.println(air);
    delay(500);
}
