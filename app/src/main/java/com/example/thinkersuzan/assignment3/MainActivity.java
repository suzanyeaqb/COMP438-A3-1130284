package com.example.thinkersuzan.assignment3;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Environment;
import android.os.Handler;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.InputType;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MainActivity extends AppCompatActivity {
    public int[][] solver = new int[9][9];
    private EditText[][] numbers;
    private Sudoku sudoku;
    private int gameMode;
    private int grid;
    TextView timeLabel;
    private long startTime = 0L;
    private Handler myHandler = new Handler();
    long timeInMillies = 0L;
    long timeSwap = 0L;
    long finalTime = 0L;
    MediaPlayer mediaPlayer;
    String result;
    List<String> strings1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {
            sudoku = new Sudoku();
            gameMode = Sudoku.GAME_MODE_MEDIUM;
            grid = Sudoku.GRID_9X9;

            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_main);
            timeLabel = (TextView) findViewById(R.id.timeLabel);

            initialize();
            startTime();


        } catch (Exception e) {

        }
    }

    private void startTime() {
        startTime = SystemClock.uptimeMillis();
        myHandler.postDelayed(updateTimerMethod, 0);
    }

    private void initialize() {
        int[][] puzzle = sudoku.getNewPuzzle(grid, gameMode);
        createBoard(puzzle);
    }

    private Runnable updateTimerMethod = new Runnable() {

        public void run() {
            timeInMillies = SystemClock.uptimeMillis() - startTime;
            finalTime = timeSwap + timeInMillies;

            int seconds = (int) (finalTime / 1000);
            int minutes = seconds / 60;
            seconds = seconds % 60;
            int milliseconds = (int) (finalTime % 1000);
            timeLabel.setText("" + minutes + ":"
                    + String.format("%02d", seconds) + ":"
                    + String.format("%03d", milliseconds));
            myHandler.postDelayed(this, 0);
        }

    };

    private void createBoard(int[][] puzzle) {
        grid = puzzle.length;
        int rowsInGrid = grid == 9 ? 3 : 2;
        GridLayout gridLayout = (GridLayout) findViewById(R.id.tableGrid);
        numbers = new EditText[grid][grid];
        gridLayout.removeAllViews();
        int column = 9;
        int row = 9;
        gridLayout.setColumnCount(column);
        gridLayout.setRowCount(row);
        for (int c = 0, r = 0; c < grid; c++) {
            for (int j = 0; j < grid; j++) {
                numbers[c][j] = new EditText(this);
                numbers[c][j].setClickable(true);
                numbers[c][j].setInputType(InputType.TYPE_CLASS_NUMBER);


                numbers[c][j].setFilters(new InputFilter[]{new InputFilter.LengthFilter(1)});

                String text = "";
                if (0 < puzzle[c][j] && puzzle[c][j] <= grid) {

                    text += puzzle[c][j];
                    numbers[c][j].setOnKeyListener(null);
                    numbers[c][j].setTextColor(Color.BLACK);
                    numbers[c][j].setEnabled(false);
                }
                numbers[c][j].setText(text);
                numbers[c][j].setWidth(105);
                numbers[c][j].setHeight(105);
                numbers[c][j].setTextAlignment(View.TEXT_ALIGNMENT_CENTER);

                if (((0 <= c && c < rowsInGrid) || (rowsInGrid * 2 <= c && c < grid)) && (3 <= j && j < 6)) {
                    numbers[c][j].setBackgroundColor(Color.rgb(255, 153, 153));
                } else if ((rowsInGrid <= c && c < rowsInGrid * 2) && ((0 <= j && j < 3) || (6 <= j && j < 9))) {
                    numbers[c][j].setBackgroundColor(Color.rgb(255, 153, 153));

                } else {
                    numbers[c][j].setBackgroundColor(Color.rgb(255, 204, 204));

                }
                GridLayout.LayoutParams param = new GridLayout.LayoutParams();
                param.rightMargin = 5;
                param.topMargin = 5;
                param.setGravity(Gravity.CENTER);
                param.columnSpec = GridLayout.spec(c);
                param.rowSpec = GridLayout.spec(j);
                numbers[c][j].setLayoutParams(param);
                gridLayout.addView(numbers[c][j]);
            }
        }

    }

    public void setInput(String ans, EditText inputButtton) {
        inputButtton.setText(ans);
    }

    private int[][] getAns() {

        int[][] ans = new int[grid][grid];

        for (int i = 0; i < grid; i++) {
            for (int j = 0; j < grid; j++) {
                try {
                    ans[i][j] = Integer.parseInt(numbers[i][j].getText() + "");
                } catch (NumberFormatException e) {
                    ans[i][j] = 0;
                }
            }
        }

        return ans;
    }

    private boolean isAnsComplete() {

        boolean isAnsComplete = true;
        for (int i = 0; i < grid; i++) {
            for (int j = 0; j < grid; j++) {
                try {
                    Integer.parseInt(numbers[i][j].getText() + "");
                } catch (NumberFormatException e) {
                    isAnsComplete = false;
                    break;
                }
            }
        }
        return isAnsComplete;
    }

    private void showMessage(String message) {
        try {

            this.setVisible(true);
        } catch (Exception e) {
            AlertDialog alertDialog = new AlertDialog.Builder(this).create();
            alertDialog.setTitle(message);
            alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                }
            });
            alertDialog.setIcon(R.drawable.winner);
            alertDialog.show();

        }
    }

    public void print(int[][] board) {
        for (int i = 0; i < board.length; i++) {
            for (int j = 0; j < board[0].length; j++) {
                if (numbers[i][j].getText().length() == 0) {
                    setInput(board[i][j] + "", numbers[i][j]);
                }
            }

        }

    }

    public void Solver(View view) {

        try {
            for (int i = 0; i < 9; i++) {
                for (int j = 0; j < 9; j++) {
                    solver[i][j] = sudoku.puzzle[i][j];
                }
            }
            if (sudoku.solve(solver)) {

                print(solver);
            } else {
//            JOptionPane.showMessageDialog(null, "No Solution ");

            }
        } catch (Exception ex) {
            Logger.getLogger(MainActivity.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void Check(View view) {

        if (!isAnsComplete()) {
            AlertDialog alertDialog = new AlertDialog.Builder(this).create();
            alertDialog.setTitle("The Fields not complete Please Complete");
            alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
// here you can add functions
                }
            });
            alertDialog.show();
        } else {
            boolean isAnsCorrect = sudoku.check(getAns());
            String messageStr = "";
            if (isAnsCorrect) {

                this.stop();


                mediaPlayer = MediaPlayer.create(this, R.raw.winner);//
                mediaPlayer.start();
                WriteBtn();

                AlertDialog alertDialog = new AlertDialog.Builder(this).create();
                alertDialog.setMessage(readFromFile()+"\n"+"1-"+strings1.get(0)+"\n"+"2-"+strings1.get(1)
                        +"\n"+"3-"+strings1.get(2)+"\n"+"4-"+strings1.get(3)+"\n"+"5-"+strings1.get(4));
                alertDialog.setTitle("Th Timer" + timeLabel.getText());
                alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        mediaPlayer.stop();
                        grid = Sudoku.GRID_9X9;
                        int[][] puzzle = sudoku.getNewPuzzle(grid, gameMode);
                        createBoard(puzzle);
                        startTime = 0L;
                        timeInMillies = 0L;
                        timeSwap = 0L;
                        finalTime = 0L;
                        startTime();
                    }
                });
                alertDialog.setIcon(R.drawable.iconwinner);
                alertDialog.show();

            } else {
                messageStr = "Sorry You have failed. ";
            }
            showMessage(messageStr);
        }
    }

    public void Pause(View view) {
        timeSwap += timeInMillies;
        myHandler.removeCallbacks(updateTimerMethod);
    }

    public void exit(View view) {
        this.finish();//finish application running
    }

    private void stop() {

        timeSwap += timeInMillies;
        myHandler.removeCallbacks(updateTimerMethod);
    }

    public void newGame(View view) {
        grid = Sudoku.GRID_9X9;
        int[][] puzzle = sudoku.getNewPuzzle(grid, gameMode);
        createBoard(puzzle);
        startTime = 0L;
        timeInMillies = 0L;
        timeSwap = 0L;
        finalTime = 0L;
        startTime();

    }

    public void start(View view) {
        startTime = SystemClock.uptimeMillis();
        myHandler.postDelayed(updateTimerMethod, 0);
        Runnable updateTimerMethod = new Runnable() {

            public void run() {
                timeInMillies = SystemClock.uptimeMillis() - timeSwap;
                finalTime = timeSwap + timeInMillies;

                int seconds = (int) (finalTime / 1000);
                int minutes = seconds / 60;
                seconds = seconds % 60;
                int milliseconds = (int) (finalTime % 1000);
                timeLabel.setText("" + minutes + ":"
                        + String.format("%02d", seconds) + ":"
                        + String.format("%03d", milliseconds));
                myHandler.postDelayed(this, 0);
            }

        };
    }

    private String readFromFile() {
        StringBuffer stringBuffer = new StringBuffer();
        String message = "";

        try {
            BufferedReader inputReader = new BufferedReader(new InputStreamReader(
                    openFileInput("config.txt")));

            String inputString;
            ArrayList<String> strings = new ArrayList<>();
            while ((inputString = inputReader.readLine()) != null) {
                stringBuffer.append(inputString + "\n");
                strings.add(inputString);
            }
Collections.sort(strings);
         strings1 = strings.subList(0, 5);
            String min=strings1.get(0);
                if(min.compareTo(result)== -1){
                    message="It doesn`t from any top five scores";

                }else {
                   strings1.indexOf(result) ;
                    message="the score"+strings1.indexOf(result);

                }


            Toast.makeText(getBaseContext(), strings1.toString(),
                    Toast.LENGTH_LONG).show();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return message;
    }

    public void WriteBtn() {
        try {

            FileOutputStream fos = openFileOutput("config.txt",
                    Context.MODE_APPEND | Context.MODE_WORLD_READABLE);


            fos.write(timeLabel.getText().toString().getBytes());
            fos.write("\n".getBytes());
            fos.close();

            String storageState = Environment.getExternalStorageState();
            if (storageState.equals(Environment.MEDIA_MOUNTED)) {
                File file = new File(getExternalFilesDir(null),
                        "config.txt");
                String dir = getFilesDir().getAbsolutePath();

                FileOutputStream fos2 = new FileOutputStream(file);
                fos2.write(timeLabel.getText().toString().getBytes());
                result=timeLabel.getText().toString();
                fos.write("\n".getBytes());
                fos2.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
