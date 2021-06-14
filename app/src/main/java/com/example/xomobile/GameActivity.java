package com.example.xomobile;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
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

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;
import static com.example.xomobile.MainActivity.dbHistoryWinnerHelper;

public class GameActivity extends AppCompatActivity {
    public int numColumns, numRows;
    public int cellWidth, cellHeight;
    public int h_all;
    private int grid_size;
    TableLayout gameBoard;
    TextView txt_turn;
    char [][] my_board;
    char turn;
    JSONObject objLevel = new JSONObject();
    int levelClick =0;
    ImageView img_reset;
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        numColumns = 3;
        numRows = 3;
        grid_size = numColumns;
        my_board = new char [grid_size][grid_size];

        txt_turn = findViewById(R.id.turn);

        resetBoard();

        EditText editColomnNumber = findViewById(R.id.editColomnNumber);
        editColomnNumber.setOnKeyListener(new View.OnKeyListener() {
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if ((event.getAction() == KeyEvent.ACTION_DOWN) &&(keyCode == KeyEvent.KEYCODE_ENTER)) {
                    resetBoard();
//                    Toast.makeText(GameActivity.this, editColomnNumber.getText(), Toast.LENGTH_SHORT).show();
                    int num = Integer.parseInt(String.valueOf(editColomnNumber.getText()));
                    if(num>0){
                        numColumns = num;
                        numRows = num;
                        grid_size = numColumns;
                        my_board = new char [grid_size][grid_size];
                        resetBoard();
                        DrawTableXO();
                    }
                    return true;
                }
                return false;
            }
        });

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
        img_reset = findViewById(R.id.img_reset);
        img_reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                resetBoard();
                DrawTableXO();
            }
        });
        TextView txt_history = findViewById(R.id.txt_history);
        txt_history.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), HistoryActivity.class);
                startActivity(intent);
            }
        });

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
                image.setOnClickListener(Move(i, j, image));
                rv.addView(image);
                tableRow.addView(rv);

            }
            gameBoard.addView(tableRow);
        }
    }

    protected void resetBoard(){
        turn = 'X';
        levelClick =0;
        objLevel = new JSONObject();
        txt_turn.setText("Turn : Player "+turn);
        for(int i = 0; i< grid_size; i++){
            for(int j = 0; j< grid_size; j++){
                my_board[i][j] = ' ';
            }
        }
    }

    protected int gameStatus(){

        //0 Continue
        //1 X Wins
        //2 O Wins
        //-1 Draw

        int rowX = 0, colX = 0, rowO = 0, colO = 0;
        for(int i = 0; i< grid_size; i++){
            if(check_Row_Equality(i,'X'))
                return 1;
            if(check_Column_Equality(i, 'X'))
                return 1;
            if(check_Row_Equality(i,'O'))
                return 2;
            if(check_Column_Equality(i,'O'))
                return 2;
            if(check_Diagonal('X'))
                return 1;
            if(check_Diagonal('O'))
                return 2;
        }

        boolean boardFull = true;
        for(int i = 0; i< grid_size; i++){
            for(int j = 0; j< grid_size; j++){
                if(my_board[i][j]==' ')
                    boardFull = false;
            }
        }
        if(boardFull)
            return -1;
        else return 0;
    }

    protected boolean check_Diagonal(char player){
        int count_Equal1 = 0,count_Equal2 = 0;
        for(int i = 0; i< grid_size; i++)
            if(my_board[i][i]==player)
                count_Equal1++;
        for(int i = 0; i< grid_size; i++)
            if(my_board[i][grid_size -1-i]==player)
                count_Equal2++;
        if(count_Equal1== grid_size || count_Equal2== grid_size)
            return true;
        else return false;
    }

    protected boolean check_Row_Equality(int r, char player){
        int count_Equal=0;
        for(int i = 0; i< grid_size; i++){
            if(my_board[r][i]==player)
                count_Equal++;
        }

        if(count_Equal== grid_size)
            return true;
        else
            return false;
    }

    protected boolean check_Column_Equality(int c, char player){
        int count_Equal=0;
        for(int i = 0; i< grid_size; i++){
            if(my_board[i][c]==player)
                count_Equal++;
        }

        if(count_Equal== grid_size)
            return true;
        else
            return false;
    }

    protected boolean Cell_Set(int r, int c){
        return !(my_board[r][c]==' ');
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

    View.OnClickListener Move(final int r, final int c, final ImageView image){

        return new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onClick(View v) {
                ColorStateList cslSelect = AppCompatResources.getColorStateList(getApplicationContext(), R.color.black);
                image.setImageTintList(cslSelect);
                if(!Cell_Set(r,c)) {
                    levelClick +=1;
                    try {
                        objLevel.put(r + "_" + c, String.valueOf(levelClick));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
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
                    }
                    else if(gameStatus() == -1){
                        txt_turn.setText("This is a Draw match");
                        stopMatch();
                        AlertNotOpenVideoCall();
                    }
                    else{
                        String strWin = "";
                        if(turn=='O'){
                            strWin ="X";
                        }else if(turn=='X'){
                            strWin ="O";
                        }
                        txt_turn.setText("The Winner is Player "+strWin);
                        stopMatch();
                        AlertNotOpenVideoCall();
                    }
                }
            }
        };
    }


    private void AlertNotOpenVideoCall(){

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if(!isFinishing()){
                    try {
                        final Dialog dDialog = new Dialog(GameActivity.this, R.style.CustomAlertDialog);
                        if (dDialog.isShowing()) {
                            dDialog.dismiss();
                        }
                        dDialog.setCancelable(true);
                        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                        @SuppressLint("dDialog") final View dialogView = inflater.inflate(R.layout.custom_alert_dialog, null);

                        Button btn_replay = dialogView.findViewById(R.id.btn_replay);
                        Button btn_exit = dialogView.findViewById(R.id.btn_exit);
                        TextView txtTitleAlert = dialogView.findViewById(R.id.txtTitleAlert);
                        String txtTitle = "The Winner is Player " +turn;
                        String playWin = "";
                        String statusGame = "";
                        if (gameStatus() == 0) {
//                            txtTitleAlert.setText("Turn: Player " + turn);
                        }
                        else if(gameStatus() == -1){
                            txtTitleAlert.setText("This is a Draw match");
                            stopMatch();
                            playWin ="";
                            statusGame = "Draw";
                        }
                        else{
                         if(turn=='X'){
                             txtTitleAlert.setText("The Winner is Player O");
                             playWin = "O";
                         }else{
                             txtTitleAlert.setText("The Winner is Player  X");
                             playWin = "X";
                         }
                            stopMatch();
                            statusGame = "Win";
                        }
                        String finalPlayWin = playWin;
                        String finalStatusGame = statusGame;
                        btn_replay.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                dDialog.dismiss();
                                long idReturn;
                                idReturn = dbHistoryWinnerHelper.InsertWinnerData(finalStatusGame, finalPlayWin,grid_size);
                                if(idReturn > 0){
                                    for(int i = 0; i< grid_size; i++){
                                        for(int j = 0; j< grid_size; j++){
                                            String val = String.valueOf(my_board[i][j]);
                                            String Level = "";
                                            try {
                                                Level = objLevel.getString(i+"_"+j);
                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                            }

                                            dbHistoryWinnerHelper.InsertHistoryData(idReturn, i,j,val,Level);
                                        }
                                    }

                                }
                                img_reset.performClick();
                            }
                        });

                        btn_exit.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                dDialog.dismiss();
                               System.exit(0);
                            }
                        });

                        dDialog.setContentView(dialogView);
                        dDialog.show();
                    }
                    catch (WindowManager.BadTokenException ex) {
                        ex.printStackTrace();
                    }
                }
            }
        });
    }
}
