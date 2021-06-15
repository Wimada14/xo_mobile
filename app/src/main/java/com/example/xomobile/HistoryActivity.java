package com.example.xomobile;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.xomobile.Adapter.MyRecyclerViewAdapter;
import com.example.xomobile.Class.dataHistoryGame;
import com.example.xomobile.Class.stateHistory;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import static com.example.xomobile.MainActivity.dbHistoryWinnerHelper;

public class HistoryActivity  extends  AppCompatActivity implements MyRecyclerViewAdapter.ItemClickListener {
    MyRecyclerViewAdapter adapter;
    ArrayList<dataHistoryGame> arrDataHistoryGame;
    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);
        TextView txt_no_history = findViewById(R.id.txt_no_history);

        ImageView img_back = findViewById(R.id.img_back);
        img_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        RecyclerView recyclerViewHistory = findViewById(R.id.recyclerViewHistory);
        arrDataHistoryGame = new     ArrayList<dataHistoryGame>();
        Map<String, dataHistoryGame> mapHistoreGame = dbHistoryWinnerHelper.getHistoryAll();
        for(Map.Entry<String, dataHistoryGame> entry : mapHistoreGame.entrySet()) {
            String key = entry.getKey();
            dataHistoryGame value = entry.getValue();
            arrDataHistoryGame.add(value);
        }
        Collections.sort(arrDataHistoryGame, new Comparator<dataHistoryGame>() {
            @Override
            public int compare(dataHistoryGame o1, dataHistoryGame o2) {
                return o2.play_timestamp.compareTo(o1.play_timestamp);
            }
        });

        if(arrDataHistoryGame.size()>0) {
            txt_no_history.setVisibility(View.GONE);
            recyclerViewHistory.setVisibility(View.VISIBLE);
            // set up the RecyclerView
            recyclerViewHistory.setLayoutManager(new LinearLayoutManager(this));
            adapter = new MyRecyclerViewAdapter(this, arrDataHistoryGame);
            adapter.setClickListener(this);
            recyclerViewHistory.setAdapter(adapter);
        }else{
            txt_no_history.setVisibility(View.VISIBLE);
            recyclerViewHistory.setVisibility(View.GONE);
        }
    }

    @Override
    public void onItemClick(View view, int position) {
        Gson gson = new Gson();
//        Toast.makeText(this, "You clicked " + adapter.getItem(position) + " on row number " + position, Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(getApplicationContext(), ReplayActivity.class);
        intent.putExtra("item_history" , gson.toJson(arrDataHistoryGame.get(position)));
        startActivity(intent);
    }
}
