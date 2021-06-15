package com.example.xomobile.Helper;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.ContactsContract;
import android.util.Log;

import androidx.annotation.Nullable;

import com.example.xomobile.Class.dataHistoryGame;
import com.example.xomobile.Class.stateHistory;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class DatabaseHistoryWinnerHelper extends SQLiteOpenHelper {
    public static final int DATABASE_VERSION = 1;
    public DatabaseHistoryWinnerHelper(@Nullable Context context) {
        super(context,context.getApplicationInfo().dataDir + "/databases/History", null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        try {
            String CREATE_TABLE_WINNER_GAME = "CREATE TABLE tbl_winner_game (id_winner_game INTEGER PRIMARY KEY NOT NULL DEFAULT 0, play_timestamp DATETIME DEFAULT CURRENT_TIMESTAMP , status_game TEXT DEFAULT '', player TEXT DEFAULT '',play_columns INTEGER NOT NULL DEFAULT 0)";
            sqLiteDatabase.execSQL(CREATE_TABLE_WINNER_GAME);
            String CREATE_TABLE_HISTORY = "CREATE TABLE tbl_history_game (id_history_game INTEGER PRIMARY KEY  NOT NULL DEFAULT 0 , id_winner_game INTEGER NOT NULL DEFAULT 0, rows INTEGER NOT NULL DEFAULT 0, columns INTEGER NOT NULL DEFAULT 0, level INTEGER NOT NULL DEFAULT 0, value TEXT DEFAULT '')";
            sqLiteDatabase.execSQL(CREATE_TABLE_HISTORY);
        }catch (Exception e){
            Log.d("Error",e.getMessage());
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }

    public Map<String, dataHistoryGame> getHistoryAll()
    {
        String strQuery = "SELECT * FROM tbl_winner_game as wg ";
        strQuery += "INNER JOIN tbl_history_game as hg ON wg.id_winner_game = hg.id_winner_game ";
        strQuery += "ORDER BY wg.play_timestamp  DESC";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(strQuery, null);
        cursor.moveToFirst();
        Map<String, dataHistoryGame> mapHistory = new HashMap<String, dataHistoryGame>();
        dataHistoryGame dataHistoryGame = new dataHistoryGame();
        ArrayList<stateHistory> listHistory  = new ArrayList<stateHistory>();
        stateHistory objHistory = new stateHistory();
        int i = 0;
        while (!cursor.isAfterLast()) {
            //tbl_history
            String id_history_game =cursor.getString(cursor.getColumnIndex("id_history_game"));
            String rows =cursor.getString(cursor.getColumnIndex("rows"));
            String columns =cursor.getString(cursor.getColumnIndex("columns"));
            String level =cursor.getString(cursor.getColumnIndex("level"));
            String value =cursor.getString(cursor.getColumnIndex("value"));
            objHistory = new stateHistory();
            objHistory.id_history_game = id_history_game;
            objHistory.rows = rows;
            objHistory.columns = columns;
            if(!level.equals("")){
                objHistory.level = Integer.parseInt(level);
            }
            objHistory.value = value;
            //tbl_winner
            String id_winner_game =cursor.getString(cursor.getColumnIndex("id_winner_game"));
            String play_timestamp =cursor.getString(cursor.getColumnIndex("play_timestamp"));
            String player =cursor.getString(cursor.getColumnIndex("player"));
            String play_columns =cursor.getString(cursor.getColumnIndex("play_columns"));

            if (mapHistory.containsKey(id_winner_game)) {
                listHistory.add(objHistory);
                dataHistoryGame.listHistory = listHistory;
                mapHistory.put(id_winner_game,dataHistoryGame);
            } else {
                listHistory  = new ArrayList<stateHistory>();
                listHistory.add(objHistory);
                dataHistoryGame = new dataHistoryGame();
                dataHistoryGame.id_winner_game = id_winner_game;
                dataHistoryGame.play_timestamp = play_timestamp;
                dataHistoryGame.player = player;
                dataHistoryGame.play_columns = play_columns;
                dataHistoryGame.listHistory = listHistory;

                mapHistory.put(id_winner_game,dataHistoryGame);
            }
            cursor.moveToNext();
            i++;
        }
        return mapHistory;
    }
    public String checkDefault()
    {
        String countQuery = "SELECT  * FROM tbl_winner_game where id_winner_game = 1";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        String primarykey = "";
        if (cursor.moveToFirst())
        {
            primarykey = cursor.getString(cursor.getColumnIndex("id_winner_game"));
            cursor.close();
        }
        return primarykey;
    }
    public Integer MaxWinner()
    {
        String countQuery = "SELECT ifnull(max(id_winner_game),0) as id_winner_game FROM tbl_winner_game";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        int primarykey = 0;
        if (cursor.moveToFirst())
        {
            primarykey = Integer.parseInt(cursor.getString(cursor.getColumnIndex("id_winner_game")));
            primarykey = primarykey + 1;
            cursor.close();
        }
        return primarykey;
    }
    public static String getCurrentTimeUsingDate() {
        Date date = new Date();
        String strDateFormat = "dd/MM/yyyy HH:mm:ss";
        DateFormat dateFormat = new SimpleDateFormat(strDateFormat, new Locale("th"));
        return dateFormat.format(date);
    }
    // Insert Data
    public long InsertWinnerData(String strStatusGame, String strPlayer,Integer strPlay_columns) {
        // TODO Auto-generated method stub
        String TABLE_WINNER= "tbl_winner_game";
        try {
            SQLiteDatabase db;
            db = this.getWritableDatabase(); // Write Data
            int id = MaxWinner();
            ContentValues Val = new ContentValues();
            Val.put("id_winner_game", id);
            Val.put("status_game", strStatusGame);
            Val.put("player", strPlayer);
            Val.put("play_columns", strPlay_columns);
            Val.put("play_timestamp", getCurrentTimeUsingDate());

            long rows = db.insert(TABLE_WINNER, null, Val);

            return rows; // return rows inserted.

        } catch (Exception e) {
            return -1;
        }

    }
    public Integer MaxHistory()
    {
        String countQuery = "SELECT ifnull(max(id_history_game),0) as id_history_game FROM tbl_history_game";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        int primarykey = 0;
        if (cursor.moveToFirst())
        {
            primarykey = Integer.parseInt(cursor.getString(cursor.getColumnIndex("id_history_game")));
            primarykey = primarykey + 1;
            cursor.close();
        }
        return primarykey;
    }

    public void InsertHistoryData(long idWinner, int i, int j, String val,String Level) {
        String TABLE_HISTORY= "tbl_history_game";
        try {
            SQLiteDatabase db;
            db = this.getWritableDatabase(); // Write Data
            int id = MaxHistory();
            ContentValues Val = new ContentValues();
            Val.put("id_history_game", id);
            Val.put("id_winner_game", idWinner);
            Val.put("rows", i);
            Val.put("columns", j);
            Val.put("level", Level);
            Val.put("value", val);

            db.insert(TABLE_HISTORY, null, Val);

        } catch (Exception e) {
            Log.d("Error",e.getMessage());
        }

    }

}
