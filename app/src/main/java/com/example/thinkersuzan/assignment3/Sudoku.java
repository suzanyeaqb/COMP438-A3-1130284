package com.example.thinkersuzan.assignment3;

import java.util.Arrays;
import java.util.Random;

/**
 * Created by Thinker Suzan on 5/20/2016.
 */
public class Sudoku {
    public static final int[][] VALID_BOARD_9X9 = {
            {4, 3, 5, 8, 7, 6, 1, 2, 9},
            {8, 7, 6, 2, 1, 9, 3, 4, 5},
            {2, 1, 9, 4, 3, 5, 7, 8, 6},
            {5, 2, 3, 6, 4, 7, 8, 9, 1},
            {9, 8, 1, 5, 2, 3, 4, 6, 7},
            {6, 4, 7, 9, 8, 1, 2, 5, 3},
            {7, 5, 4, 1, 6, 8, 9, 3, 2},
            {3, 9, 2, 7, 5, 4, 6, 1, 8},
            {1, 6, 8, 3, 9, 2, 5, 7, 4}};

    public static final int GRID_9X9 = 9;
    public static final int GAME_MODE_EXPART = 75;
    public static final int GAME_MODE_MEDIUM = 60;
    public static final int GAME_MODE_EASY = 50;
    public static final int GAME_MODE_EASYER = 4;
    public static final int DEFAULT_TOLERANCE = 5;
    public static final String SET_VALUE_9X9 = "123456789";
    public static int[][] puzzle;
    private Random random = new Random();

    // Create a copy of 2D array.
    private int[][] copyOf(int[][] original) {
        int[][] copy = new int[original.length][];
        for (int i = 0; i < original.length; i++) {
            copy[i] = Arrays.copyOf(original[i], original[i].length);
        }
        return copy;
    }

    // Swap rows of an 2D array.
    private int[][] swapRows(int[][] board, int row1, int row2) {
        for (int j = 0; j < board.length; j++) {
            int temp = board[row1][j];
            board[row1][j] = board[row2][j];
            board[row2][j] = temp;
        }
        return board;
    }

    // Swap columns of an 2D array.
    private int[][] swapCols(int[][] board, int col1, int col2) {
        for (int i = 0; i < board.length; i++) {
            int temp = board[i][col1];
            board[i][col1] = board[i][col2];
            board[i][col2] = temp;
        }
        return board;
    }

    // This method swaps rows and columns of an valid board.
    // Swaping process for rows must be done same horizontal grid and
    // also for column swaping process must be in vertical grid.
    private int[][] swapRowsAndCols(int[][] board) {

        int range = board.length == GRID_9X9 ? 7 : 5;
        // define number of rows per horizontal group.
        int rowsInGrid = board.length == GRID_9X9 ? 3 : 2;
        // For both 9X9 and and 9X6 number of columns in vertical grid is 3.
        int colsInGrid = 3;

        for (int a = 0; a < range; a += rowsInGrid) {
            int row[] = getTwoRanNum(a, rowsInGrid);
            swapRows(board, row[0], row[1]);
        }

        for (int a = 0; a < range; a += colsInGrid) {
            int[] col = getTwoRanNum(a, colsInGrid);
            swapCols(board, col[0], col[1]);
        }
        return board;
    }

    // Swap only horizontal groups.
    private int[][] swapGrids(int[][] board) {
        int firstgrid = 1 + random.nextInt(3);
        int secondgrid = 1 + random.nextInt(3);
        int numRowsInGrid = board.length == GRID_9X9 ? 3 : 2;

        if ((firstgrid == 1 && secondgrid == 2) || (firstgrid == 2 && secondgrid == 1)) {
            for (int i = 0; i < numRowsInGrid; i++) {
                swapRows(board, i, i + numRowsInGrid);
            }
        } else if ((firstgrid == 2 && secondgrid == 3) || (firstgrid == 3 && secondgrid == 2)) {
            for (int i = numRowsInGrid; i < numRowsInGrid * 2; i++) {
                swapRows(board, i, i + numRowsInGrid);
            }
        } else if ((firstgrid == 1 && secondgrid == 3) || (firstgrid == 3 && secondgrid == 1)) {
            for (int i = 0; i < numRowsInGrid; i++) {
                swapRows(board, i, i + (numRowsInGrid * 2));
            }
        }
        return board;
    }

    // swap numbers for each rows.
    private int[][] swapNums(int[][] board) {
        int[] num = getTwoRanNum(1, board.length);
        for (int i = 0; i < board.length; i++) {
            for (int j = 0; j < board.length; j++) {
                if (board[i][j] == num[0]) {
                    board[i][j] = num[1];
                } else if (board[i][j] == num[1]) {
                    board[i][j] = num[0];
                }
            }
        }
        return board;
    }

    // provide two random number as an array with length two.
    private int[] getTwoRanNum(int min, int tolerance) {
        int a[] = new int[2];
        a[0] = min + random.nextInt(tolerance);
        a[1] = min + random.nextInt(tolerance);
        return a;
    }

    // Create an validsudoku board.
    private int[][] createBoard(int[][] board) {
        for (int i = 0; i < 10; i++) {
            swapRowsAndCols(board);
            swapGrids(board);
            swapNums(board);
        }
        return board;
    }

    // Hide some numbers to create puzzle.
    private int[][] createPuzzle(int[][] board, int mode) {
        this.puzzle = copyOf(board);
        int numOfEmptyBlock = getNumberOfEmptyBlock(board, mode);
        for (int i = 0; i < numOfEmptyBlock; i++) {
            int[] rowcol = getTwoRanNum(0, board.length);
            this.puzzle[rowcol[0]][rowcol[1]] = 0;
        }
        return copyOf(this.puzzle);
    }

    // Define number of empty blocks according to game mode.
    private int getNumberOfEmptyBlock(int[][] board, int mode) {
        int numOfEmptyBlock = 0;
        int numOfBlock = board.length * board[0].length;

        if (GAME_MODE_EASYER <= mode && mode <= GAME_MODE_EXPART) {
            numOfEmptyBlock = (int) Math.floor((mode * numOfBlock) / 100);
        } else {
            numOfEmptyBlock = (int) Math.floor((GAME_MODE_MEDIUM * numOfBlock) / 100);
        }
        int tolerance = (int) Math.floor(((numOfBlock - numOfEmptyBlock) * 5) / 100);
        numOfEmptyBlock += random.nextInt(tolerance + 1); // to avoid negetive

        return numOfEmptyBlock;
    }

    // Check Is the sollution correct or Incorrect.
    public boolean check(int[][] board) {
        boolean isCorrect = true;
        int numOfRowsInGrid = board.length == 9 ? 3 : 2;
        final String setValues = SET_VALUE_9X9;
        // check rows
        for (int i = 0; i < board.length; i++) {
            String set = setValues;
            for (int j = 0; j < board.length; j++) {
                set = set.replace("" + board[i][j], "");
            }
            if (!set.isEmpty()) {
                isCorrect = false;
                return isCorrect;
            }
        }

        // check columns
        for (int j = 0; j < board.length; j++) {
            String set = setValues;
            for (int i = 0; i < board.length; i++) {
                set = set.replace("" + board[i][j], "");
            }
            if (!set.isEmpty()) {
                isCorrect = false;
                return isCorrect;
            }
        }

        //check Horizontal and vertical grids
        for (int hg = 0; hg < board.length; hg += numOfRowsInGrid) {
            for (int vg = 0; vg < board[0].length; vg += 3) {
                String set = setValues;
                for (int i = hg; i < (hg + numOfRowsInGrid); i++) {
                    for (int j = vg; j < vg + 3; j++) {
                        set = set.replace("" + board[i][j], "");
                    }
                }
                if (!set.isEmpty()) {
                    isCorrect = false;
                    return isCorrect;
                }
            }
        }

        return isCorrect;
    }

    public boolean solve(int[][] board) {
        int[][] array = new int[board.length][board[0].length];
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                array[i][j] = board[i][j] > 0 ? 2 : 0;

            }
        }
        return solve(board, array, 0, 0);
    }

    public boolean solve(int[][] board, int[][] array, int x, int y) {
        if (x == 9) {
            int count = 0;
            for (int i = 0; i < 9; i++) {
                for (int j = 0; j < 9; j++) {
                    count += board[i][j] > 0 ? 1 : 0;

                }
            }
            if (count == 81) {
                return true;

            } else {
                return false;
            }
        }
        if (array[x][y] >= 1) {
            int nextx = x;
            int nexty = y + 1;
            if (nexty == 9) {
                nextx = x + 1;
                nexty = 0;
            }
            return solve(board, array, nextx, nexty);
        } else {
            boolean[] valied = new boolean[9];
            for (int i = 0; i < 9; i++) {
                if (array[x][i] >= 1) {
                    valied[board[x][i] - 1] = true;
                }
            }
            for (int i = 0; i < 9; i++) {
                if (array[i][y] >= 1) {
                    valied[board[i][y] - 1] = true;
                }
            }
            for (int i = x - (x % 3); i < x - (x % 3) + 3; i++) {
                for (int j = y - (y % 3); j < y - (y % 3) + 3; j++) {
                    if (array[i][j] >= 1) {
                        valied[board[i][j] - 1] = true;
                    }
                }

            }
            for (int i = 0; i < valied.length; i++) {
                if (!valied[i]) {
                    array[x][y] = 1;
                    board[x][y] = i + 1;
                    int nextx = x;
                    int nexty = y + 1;
                    if (nexty == 9) {
                        nextx = x + 1;
                        nexty = 0;
                    }
                    if (solve(board, array, nextx, nexty)) {
                        return true;
                    }
                    for (int j = 0; j < 9; j++) {
                        for (int k = 0; k < 9; k++) {
                            if (j > x || (j == x && k >= y)) {
                                if (array[j][k] == 1) {
                                    array[j][k] = 0;
                                    board[j][k] = 0;

                                }
                            }
                        }
                    }
                }

            }
        }
        return false;
    }

    public int[][] getNewPuzzle(int grid, int gameMode) {

        return createPuzzle(createBoard(VALID_BOARD_9X9), gameMode);
    }

    public int[][] resetPuzzle() {
        return puzzle;
    }

    private void printArray(int[][] a) {
        for (int i = 0; i < a.length; i++) {
            for (int j = 0; j < a[i].length; j++) {
                System.out.print(a[i][j] + "\t");
            }
            System.out.println();
        }
        System.out.println();
    }
}
