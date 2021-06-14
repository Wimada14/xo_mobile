package com.example.xomobile;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

import com.example.xomobile.Helper.DatabaseHistoryWinnerHelper;

public class MainActivity extends AppCompatActivity {
    String dbHistoryHelper;
    public static DatabaseHistoryWinnerHelper dbHistoryWinnerHelper;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        LinearLayout btn_play = findViewById(R.id.btn_play);
        dbHistoryWinnerHelper = new DatabaseHistoryWinnerHelper(this);

        btn_play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), GameActivity.class);
                startActivity(intent);
            }
        });
    }
}