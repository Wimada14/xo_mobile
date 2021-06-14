package com.example.xomobile;

import android.content.res.ColorStateList;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.content.res.AppCompatResources;

import com.example.xomobile.Class.dataHistoryGame;
import com.example.xomobile.Class.stateHistory;
import com.google.gson.Gson;
import org.json.JSONObject;
import java.util.Collections;
import java.util.Comparator;


public class ReplayActivity extends AppCompatActivity {
    public int numColumns, numRows;
    public int cellWidth, cellHeight;
    public int h_all;
    private int grid_size;
    TableLayout gameBoard;
    TextView txt_turn;
    char[][] my_board;
    char turn;
    JSONObject objLevel = new JSONObject();
    int levelClick = 0;
    dataHistoryGame dictHistory = new dataHistoryGame();
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_replay);
        Gson gson = new Gson();
        String strDict = "";

        Bundle bundle = getIntent().getExtras();
        strDict = bundle.getString("item_history");
        dictHistory = gson.fromJson(strDict, dataHistoryGame.class);
        numColumns = Integer.parseInt(dictHistory.play_columns);
        numRows = Integer.parseInt(dictHistory.play_columns);
        grid_size = numColumns;
        my_board = new char[grid_size][grid_size];

        txt_turn = findViewById(R.id.turn);

        resetBoard();

        TextView txt_column = findViewById(R.id.txt_column);
        txt_column.setText(String.valueOf(numColumns));
        LinearLayout li_head_setting = findViewById(R.id.li_head_setting);
        LinearLayout li_btn_reset = findViewById(R.id.li_btn_reset);
        li_head_setting.post(new Runnable() {
            @Override
            public void run() {
                h_all += li_head_setting.getHeight();
            }
        });
        li_btn_reset.post(new Runnable() {
            @Override
            public void run() {
                h_all += li_btn_reset.getHeight();
                h_all += 120;
                DrawTableXO();
            }
        });
        ImageView img_back = findViewById(R.id.img_back);
        img_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        ImageView img_bottom_back = findViewById(R.id.img_bottom_back);
        img_bottom_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        ImageView img_bottom_replay = findViewById(R.id.img_bottom_replay);
        img_bottom_replay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                resetBoard();
                DrawTableXO();
            }
        });
    }
    public class FishNameComparator implements Comparator<stateHistory>
    {
        @Override
        public int compare(stateHistory stateHistory, stateHistory t1) {
            return stateHistory.level.compareTo(t1.level);
        }
    }
    public void replay(){
        int timeCount=1000;
        Collections.sort(dictHistory.listHistory, new FishNameComparator());
        for(int index=0;index<dictHistory.listHistory.size();index++){
            if(!dictHistory.listHistory.get(index).level.equals(0)){
                int j = Integer.parseInt(dictHistory.listHistory.get(index).columns);
                int i = Integer.parseInt(dictHistory.listHistory.get(index).rows);
                    TableRow row = (TableRow) gameBoard.getChildAt(i);
                        int finalJ = j;
                        int finalI = i;
                        new Handler().postDelayed(new Runnable() {
                            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
                            @Override
                            public void run() {
                                RelativeLayout rv = (RelativeLayout) row.getChildAt(finalJ);
                                ImageView image = (ImageView) rv.getChildAt(0);
                                reMove(finalI, finalJ,image);
                            }
                        }, timeCount+=500);
                }
            }

    }
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void DrawTableXO() {
        gameBoard = findViewById(R.id.mainBoard);
        gameBoard.removeAllViews();
        TableRow tableRow;
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int height = displayMetrics.heightPixels;
        int width = displayMetrics.widthPixels;

        cellWidth = width / numColumns;
        cellHeight = height / numRows;

        int h_header = (height - h_all) / numColumns;

        RelativeLayout rv;
        ImageView image;
        for (int i = 0; i < numRows; i++) {
            tableRow = new TableRow(getApplicationContext());
            tableRow.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
            for (int j = 0; j < numColumns; j++) {
                rv = new RelativeLayout(this);
                image = new ImageView(this);
                image.setImageResource(R.drawable.icon_x);
                ColorStateList cslSelect = AppCompatResources.getColorStateList(this, R.color.white);
                image.setImageTintList(cslSelect);
                image.setBackgroundResource(R.drawable.border_table);
//                image.setPadding(30, 30, 30, 30);
                RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                        RelativeLayout.LayoutParams.WRAP_CONTENT,
                        h_header
                );
                image.setLayoutParams(params);
//                image.setOnClickListener(Move(i, j, image));
                rv.addView(image);
                tableRow.addView(rv);
            }
            gameBoard.addView(tableRow);
        }
        replay();
    }
     @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
     public void reMove(final int r, final int c, final ImageView image) {
        ColorStateList cslSelect = AppCompatResources.getColorStateList(getApplicationContext(), R.color.black);
        image.setImageTintList(cslSelect);
        if (!Cell_Set(r, c)) {
            my_board[r][c] = turn;
            if (turn == 'X') {
                image.setImageDrawable(getDrawable(R.drawable.icon_x));
                turn = 'O';
            } else if (turn == 'O') {
                image.setImageDrawable(getDrawable(R.drawable.icon_o));
                turn = 'X';
            }
            if (gameStatus() == 0) {
                txt_turn.setText("Turn: Player " + turn);
            } else if (gameStatus() == -1) {
                txt_turn.setText("This is a Draw match");
                stopMatch();
            } else {
                String strWin = "";
                if(turn=='O'){
                    strWin ="X";
                }else if(turn=='X'){
                    strWin ="O";
                }
                txt_turn.setText("The Winner is Player "+strWin);
                stopMatch();
            }
        }
    }
    protected void resetBoard() {
        turn = 'X';
        txt_turn.setText("Turn : Player " + turn);
        for (int i = 0; i < grid_size; i++) {
            for (int j = 0; j < grid_size; j++) {
                my_board[i][j] = ' ';
            }
        }
    }

    protected int gameStatus() {

        //0 Continue
        //1 X Wins
        //2 O Wins
        //-1 Draw

        int rowX = 0, colX = 0, rowO = 0, colO = 0;
        for (int i = 0; i < grid_size; i++) {
            if (check_Row_Equality(i, 'X'))
                return 1;
            if (check_Column_Equality(i, 'X'))
                return 1;
            if (check_Row_Equality(i, 'O'))
                return 2;
            if (check_Column_Equality(i, 'O'))
                return 2;
            if (check_Diagonal('X'))
                return 1;
            if (check_Diagonal('O'))
                return 2;
        }

        boolean boardFull = true;
        for (int i = 0; i < grid_size; i++) {
            for (int j = 0; j < grid_size; j++) {
                if (my_board[i][j] == ' ')
                    boardFull = false;
            }
        }
        if (boardFull)
            return -1;
        else return 0;
    }

    protected boolean check_Diagonal(char player) {
        int count_Equal1 = 0, count_Equal2 = 0;
        for (int i = 0; i < grid_size; i++)
            if (my_board[i][i] == player)
                count_Equal1++;
        for (int i = 0; i < grid_size; i++)
            if (my_board[i][grid_size - 1 - i] == player)
                count_Equal2++;
        if (count_Equal1 == grid_size || count_Equal2 == grid_size)
            return true;
        else return false;
    }

    protected boolean check_Row_Equality(int r, char player) {
        int count_Equal = 0;
        for (int i = 0; i < grid_size; i++) {
            if (my_board[r][i] == player)
                count_Equal++;
        }

        if (count_Equal == grid_size)
            return true;
        else
            return false;
    }

    protected boolean check_Column_Equality(int c, char player) {
        int count_Equal = 0;
        for (int i = 0; i < grid_size; i++) {
            if (my_board[i][c] == player)
                count_Equal++;
        }

        if (count_Equal == grid_size)
            return true;
        else
            return false;
    }

    protected boolean Cell_Set(int r, int c) {
        return !(my_board[r][c] == ' ');
    }

    protected void stopMatch(){
        for(int i = 0; i< gameBoard.getChildCount(); i++){
            TableRow row = (TableRow) gameBoard.getChildAt(i);
            for(int j = 0; j<row.getChildCount(); j++){
                RelativeLayout rv = (RelativeLayout) row.getChildAt(j);
                rv.setOnClickListener(null);
            }
        }
    }
}