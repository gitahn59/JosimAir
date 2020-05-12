package com.cbnu.josimair.ui;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.text.Editable;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

import com.cbnu.josimair.Model.AppDatabase;
import com.cbnu.josimair.R;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class ManagementActivity extends AppCompatActivity {

    Calendar start;
    Calendar end;
    SimpleDateFormat sdfDate;
    SimpleDateFormat sdfTime;
    EditText startDateText;
    EditText endDateText;
    Button deleteBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_management);

        startDateText = (EditText) findViewById(R.id.startDateText);
        startDateText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showStartDatePicker();
            }
        });

        endDateText = (EditText) findViewById(R.id.endDateText);
        endDateText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showEndDatePicker();
            }
        });

        deleteBtn = (Button) findViewById(R.id.deleteBtn);

        sdfDate = new SimpleDateFormat("YY.MM.dd");
        sdfTime = new SimpleDateFormat("a hh:mm");

        start =  Calendar.getInstance();
        startDateText.setText(calendarToString(start));
        end = Calendar.getInstance();
        endDateText.setText(calendarToString(end));

        deleteBtn.setOnClickListener(new Button.OnClickListener(){
            public void onClick(View v){
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        AppDatabase.getInstance(getApplicationContext()).indoorAirDao().deleteAllBetweenDates(start.getTime(),end.getTime());
                    }
                }).start();
            }
        });

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                super.onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    String calendarToString(Calendar calendar){
        String result="";
        result = result + sdfDate.format(calendar.getTime()) +" "+ sdfTime.format(calendar.getTime());
        return result;
    }

    void showStartDatePicker() {
        DatePickerDialog datePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                Calendar temp = (Calendar)start.clone();
                temp.set(Calendar.YEAR,year);
                temp.set(Calendar.MONTH,month);
                temp.set(Calendar.DAY_OF_MONTH,dayOfMonth);
                if(temp.compareTo(end) > 0){
                    return;
                }
                start = (Calendar)temp.clone();
                showStartTimePicker();
                startDateText.setText(calendarToString(start));
            };

        },start.get(Calendar.YEAR), start.get(Calendar.MONTH), start.get(Calendar.DATE));
        datePickerDialog.show();
    }

    void showStartTimePicker(){
        TimePickerDialog timePickerDialog = new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                Calendar temp = (Calendar)start.clone();
                temp.set(Calendar.HOUR_OF_DAY,hourOfDay);
                temp.set(Calendar.MINUTE,minute);
                if(temp.compareTo(end) > 0){
                    return;
                }
                start = (Calendar)temp.clone();
                startDateText.setText(calendarToString(start));
            }
        },start.get(Calendar.HOUR_OF_DAY),start.get(Calendar.MINUTE),false);
        timePickerDialog.show();
    }


    void showEndDatePicker() {
        DatePickerDialog datePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                Calendar temp = (Calendar)end.clone();
                temp.set(Calendar.YEAR,year);
                temp.set(Calendar.MONTH,month);
                temp.set(Calendar.DAY_OF_MONTH,dayOfMonth);
                if(temp.compareTo(start) < 0){
                    return;
                }
                Calendar now = Calendar.getInstance();
                if(temp.compareTo(now) > 0){
                    return;
                }
                end = (Calendar)temp.clone();
                endDateText.setText(calendarToString(end));
                showEndTimePicker();
            };

        },end.get(Calendar.YEAR), end.get(Calendar.MONTH), end.get(Calendar.DATE));
        datePickerDialog.show();
    }

    void showEndTimePicker(){
        TimePickerDialog timePickerDialog = new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                Calendar temp = (Calendar)end.clone();
                temp.set(Calendar.HOUR_OF_DAY,hourOfDay);
                temp.set(Calendar.MINUTE,minute);
                if(temp.compareTo(end) > 0){
                    return;
                }
                end = (Calendar)temp.clone();
                endDateText.setText(calendarToString(end));
            }
        }, end.get(Calendar.HOUR_OF_DAY), end.get(Calendar.MINUTE),false);
        timePickerDialog.show();
    }
}
