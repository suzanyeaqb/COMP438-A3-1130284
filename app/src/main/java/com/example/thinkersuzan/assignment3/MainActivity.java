package com.example.thinkersuzan.assignment3;

;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Handler;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.InputType;
import android.view.Gravity;
import android.view.View;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.TextView;
import android.widget.Toast;

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

                AlertDialog alertDialog = new AlertDialog.Builder(this).create();

                alertDialog.setTitle("The Timer" + timeLabel.getText());
                alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        mediaPlayer.stop();
                        grid = Sudoku.GRID_9X9;
                        int[][] puzzle = sudoku.getNewPuzzle(grid, gameMode);
                        createBoard(puzzle);
                        startTime = SystemClock.uptimeMillis();
                        myHandler.postDelayed(updateTimerMethod, 0);


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
}
