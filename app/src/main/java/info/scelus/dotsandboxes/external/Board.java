package info.scelus.dotsandboxes.external;

/**
 * Created by SceLus on 11/10/2014.
 */
public class Board {
    private int rows;
    private int columns;
    private Game game;

    public class Box {
        public boolean top;
        public boolean right;
        public boolean bottom;
        public boolean left;
        public Game.Player player;

        public Box() {
            top = false;
            right = false;
            bottom = false;
            left = false;
        }

        public Box(byte b) {
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

    private enum Line {
        LEFT, RIGHT, TOP, BOTTOM;
    }

    private byte[][] boxes;

    public Board() {
        rows = 0;
        columns = 0;

        boxes = new byte[rows][];
        for (int i = 0; i < rows; i++)
            boxes[i] = new byte[columns];

        game = new Game();
    }

    public Board(int rows, int columns) {
        this.rows = rows;
        this.columns = columns;

        boxes = new byte[rows][];
        for (int i = 0; i < rows; i++)
            boxes[i] = new byte[columns];

        game = new Game();
    }

    public Box getBoxAt(int row, int column) {
        return new Box(boxes[row][column]);
    }

    public int getRows() {
        return rows;
    }

    public int getColumns() {
        return columns;
    }

    public void setGameListener(Game.GameListener listener) {
        game.registerListener(listener);
    }

    public void setLineAtBox(int row, int column, Line line) {
        switch (line){
            case LEFT:
                boxes[row][column] |= 8;
                break;
            case RIGHT:
                boxes[row][column] |= 2;
                break;
            case TOP:
                boxes[row][column] |= 1;
                break;
            case BOTTOM:
                boxes[row][column] |= 4;
                break;
        }

        // the box is complete
        if ((boxes[row][column] & 15) == 15) {
            // set the player who made id
            if (game.getNext() == Game.Player.PLAYER1) {
                boxes[row][column] |= 16;
            }
            else {
                boxes[row][column] |= 32;
            }
        }
    }

    public void setLine(int x1, int y1, int x2, int y2) {
        if (x1 < 0 && x1 > columns ||
            x2 < 0 && x2 > columns ||
            y1 < 0 && y1 > rows ||
            y2 < 0 && y2 > rows)
        return;

        int row = y1 > y2 ? y2 : y1;
        int column = x1 > x2 ? x2 : x1;
        boolean linePlaced = false;

        // horizontal
        if (y1 == y2) {
            if (row > 0 && (boxes[row-1][column] & 4) != 4) {
                setLineAtBox(row - 1, column, Line.BOTTOM);
                linePlaced = true;
            }

            if (row < rows && (boxes[row][column] & 1) != 1) {
                setLineAtBox(row, column, Line.TOP);
                linePlaced = true;
            }
        }
        // vertical
        else if (x1 == x2) {
            if (column > 0 && (boxes[row][column-1] & 2) != 2) {
                setLineAtBox(row, column-1, Line.RIGHT);
                linePlaced = true;
            }

            if (column < columns && (boxes[row][column] & 8) != 8) {
                setLineAtBox(row, column, Line.LEFT);
                linePlaced = true;
            }
        }

        if (linePlaced)
            game.setScore(getScore(Game.Player.PLAYER1), getScore(Game.Player.PLAYER2));
    }

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
}
