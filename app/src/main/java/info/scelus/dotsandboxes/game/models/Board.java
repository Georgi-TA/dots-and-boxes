package info.scelus.dotsandboxes.game.models;

import info.scelus.dotsandboxes.game.controllers.Game;

/**
 * Class responsible for the presentation of the board in a given state.
 * This board itself is presented as numbered dots from 0 to n^2-1.
 * Each node is connected to another via an {@link Edge}.
 */
public class Board {

    private int rows;        // the rows of the board
    private int columns;     // the columns of the board
    private int minWinScore; // the minimum score needed to win

    boolean isFinished() {
        return getScore(Game.Player.PLAYER1) > minWinScore || getScore(Game.Player.PLAYER2) > minWinScore;
    }

    /**
     * Wrapper class for the box byte model.
     * It consists of four booleans for each line
     * and a {@link #player info.scelus.dotsandboxes.game.controllers.Game.Player} enum object
     * indicating the owner of the box.
     */
    public class Box {
        public boolean top;
        public boolean right;
        public boolean bottom;
        public boolean left;
        public Game.Player player;

        Box(byte b) {
            top = ((b & 1) == 1);
            right = ((b & 2) == 2);
            bottom = ((b & 4) == 4);
            left = ((b & 8) == 8);

            if ((b & 16) == 16)
                player = Game.Player.PLAYER1;
            else if ((b & 32) == 32)
                player = Game.Player.PLAYER2;
        }
    }

    /**
     * Enumeration for the type of lines placed on a box
     */
    private enum Line {
        LEFT, RIGHT, TOP, BOTTOM
    }

    /**
     * Internal representation of the board an array of bytes
     * Each byte's bits are a mask for the state of a box.
     * 0 - top line placed
     * 1 - right line placed
     * 2 - bottom line placed
     * 3 - left line placed
     * 4 - player one owns this box
     * 5 - player two owns this box
     *
     * Note that 4 and 5 are exclusive
     */
    private byte[][] boxes;

    /**
     * Constructor for the board
     * @param rows the rows of the board
     * @param columns the columns of the board
     */
    public Board(int rows, int columns) {
        this.rows = rows;
        this.columns = columns;
        this.minWinScore = (rows * columns / 2) + (rows * columns % 2);

        boxes = new byte[rows][];
        for (int i = 0; i < rows; i++)
            boxes[i] = new byte[columns];
    }

    /**
     * Wraps a byte representing a box into a {@link Box} object.
     * @param row row of the box
     * @param column column of the box
     * @return {@link Box} object
     */
    public Box getBoxAt(int row, int column) {
        return new Box(boxes[row][column]);
    }

    /**
     * Getter fot the rows
     * @return the number of rows
     */
    public int getRows() {
        return rows;
    }

    /**
     * Getter fot the columns
     * @return number of columns
     */
    public int getColumns() {
        return columns;
    }

    /**
     * Sets a line for a couple of dots. The dots are numbered from 0 to n^2-1
     * @param dotStart the dot that is with a number less than the one for dotEnd
     * @param dotEnd the dot that is with a number greater than the one for dotStart
     * @param player the player that is placing the line
     *
     * @return if the move completed a square
     */
    public boolean setLineForDots(int dotStart, int dotEnd, Game.Player player) {
        int rowStart = dotStart / (rows + 1);
        int rowEnd = dotEnd / (columns + 1);
        int columnStart = dotStart % (rows + 1);
        int columnEnd = dotEnd % (columns + 1);

        // sanity check
        return  rowStart <= rowEnd
                && columnStart <= columnEnd
                && toggleLine(true, rowStart, columnStart, rowEnd, columnEnd, player);
    }

    /**
     * Sets a line for a couple of dots. The dots are numbered from
     * @param dotStart the dot that is with a number less than the one for dotEnd
     * @param dotEnd the dot that is with a number greater than the one for dotStart
     */
     boolean removeLineForDots(int dotStart, int dotEnd) {
         int rowStart = dotStart / (rows + 1);
         int rowEnd = dotEnd / (columns + 1);
         int columnStart = dotStart % (rows + 1);
         int columnEnd = dotEnd % (columns + 1);

        // sanity check
        if (rowStart <= rowEnd && columnStart <= columnEnd) {
            toggleLine(false, rowStart, rowEnd, columnStart, columnEnd, Game.Player.NONE);
            return true;
        }

        return false;
    }

    /**
     * Sets a line at for a given box based on a row and column. It takes into an account the adjacent box.
     * @param row the row of the box
     * @param column the column of the box
     * @param line the type of line being placed
     * @param player the player that is placing the line
     *
     * @return if the line completed a box
     */
    private boolean setLineAtBox(int row, int column, Line line, Game.Player player) {
        switch (line){
            case TOP:
                boxes[row][column] |= 1;
                break;
            case RIGHT:
                boxes[row][column] |= 2;
                break;
            case BOTTOM:
                boxes[row][column] |= 4;
                break;
            case LEFT:
                boxes[row][column] |= 8;
                break;
        }

        // the box is complete
        if ((boxes[row][column] & 15) == 15) {
            // set the player who made it
            if (player == Game.Player.PLAYER1) {
                boxes[row][column] |= 16;
                boxes[row][column] &= ~32;
            } else if (player == Game.Player.PLAYER2){
                boxes[row][column] |= 32;
                boxes[row][column] &= ~16;
            }
            // the box belongs to no one
            else {
                boxes[row][column] &= ~16;
                boxes[row][column] &= ~32;
            }

            return true;
        }

        return false;
    }

    /**
     * Unsets a line at for a given box based on a row and column.
     * @param row the row of the box
     * @param column the column of the box
     * @param line the type of line being placed
     */
    private void unsetLineAtBox(int row, int column, Line line) {
        switch (line){
            case LEFT:
                boxes[row][column] &= ~8;
                break;
            case RIGHT:
                boxes[row][column] &= ~2;
                break;
            case TOP:
                boxes[row][column] &= ~1;
                break;
            case BOTTOM:
                boxes[row][column] &= ~4;
                break;
        }

        boxes[row][column] &= ~16;
        boxes[row][column] &= ~32;
    }

    /**
     * Method for setting a line between two dots based on their row and column. It completes two boxes if possible.
     * @param columnStart column of starting dot
     * @param rowStart row of starting dot
     * @param columnEnd column of ending dot
     * @param rowEnd row of ending dot
     * @param player the player placing the line
     */
    private boolean toggleLine(boolean set, int rowStart, int columnStart, int rowEnd, int columnEnd, Game.Player player) {
        if (columnStart < 0 || columnStart > columns ||
            columnEnd < 0 || columnEnd > columns ||
            rowStart < 0 || rowStart > rows ||
            rowEnd < 0 || rowEnd > rows)
        return false;

        // get the starting coordinates (row, column)
        int row = rowStart < rowEnd ? rowStart : rowEnd;
        int column = columnStart < columnEnd ? columnStart : columnEnd;

        // horizontal
        if (rowStart == rowEnd) {
            // top row
            if (row == 0) {
                if (set)
                    return setLineAtBox(row, column, Line.TOP, player);
                else
                    unsetLineAtBox(row, column, Line.TOP);
            }
            // bottom row
            else if (row == rows) {
                if (set)
                    return setLineAtBox(row - 1, column, Line.BOTTOM, player);
                else
                    unsetLineAtBox(row - 1, column, Line.BOTTOM);
            }
            // middle rows
            else {
                if (set) {
                    return setLineAtBox(row, column, Line.TOP, player) |
                           setLineAtBox(row - 1, column, Line.BOTTOM, player);
                }
                else {
                    unsetLineAtBox(row, column, Line.TOP);
                    unsetLineAtBox(row - 1, column, Line.BOTTOM);
                }
            }
        }
        // vertical
        else if (columnStart == columnEnd) {
            // leftmost column
            if (column == 0) {
                if (set)
                    return setLineAtBox(row, column, Line.LEFT, player);
                else
                    unsetLineAtBox(row, column, Line.LEFT);
            }
            // rightmost column
            else if (column == columns) {
                if (set)
                    return setLineAtBox(row, column - 1, Line.RIGHT, player);
                else
                    unsetLineAtBox(row, column - 1, Line.RIGHT);
            }
            // middle columns
            else {
                if (set) {
                    return setLineAtBox(row, column - 1, Line.RIGHT, player) |
                           setLineAtBox(row, column, Line.LEFT, player);
                }
                else {
                    unsetLineAtBox(row, column - 1, Line.RIGHT);
                    unsetLineAtBox(row, column, Line.LEFT);
                }
            }
        }

        return false;
    }

    /**
     * Gives an overview of the current points for a certain player
     * @param player the player that is going to be checked
     * @return the score in the current board state
     */
    public int getScore (Game.Player player) {
        int score = 0;
        for (int i = 0; i < rows; i++)
            for (int j = 0; j < columns; j++)
                if (player == Game.Player.PLAYER1 && (boxes[i][j] & 16) == 16)
                    score++;
                else if (player == Game.Player.PLAYER2 && (boxes[i][j] & 32) == 32)
                    score++;

        return score;
    }

    /**
     *   Method used for serialization during a save state
     */
    @Override
    public String toString() {
        StringBuilder s = new StringBuilder();
        for (int i = 0; i < getRows(); i++) {
            for (int j = 0; j < getColumns(); j++) {
                s.append(Integer.toString(boxes[i][j]));
                s.append(",");
            }
        }
        return s.toString();
    }

    /**
     *   Loads the board from a serialized string following the convention
     *   of {@link #toString }
     */
    public void loadBoard(String boardValues) {

        String[] values = boardValues.split(",");
        int countValues = 0;
        for (int i = 0; i < getRows(); i++) {
            for (int j = 0; j < getColumns(); j++) {
                boxes[i][j] = Byte.valueOf(values[countValues]);
                countValues++;
            }
        }
    }
}
