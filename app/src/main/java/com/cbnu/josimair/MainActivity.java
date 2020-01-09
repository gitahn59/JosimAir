package com.cbnu.josimair;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button btn = (Button)findViewById(R.id.button);
        btn.setOnClickListener(
            new Button.OnClickListener(){
                public void onClick(View v){
                    Intent intent = new Intent(MainActivity.this,AirInfoActivity.class);
                    startActivity(intent);
                }
            }
        );
    }
}
