package info.scelus.dotsandboxes.views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import info.blackbear.scelus.dotsandboxes.R;
import info.scelus.dotsandboxes.game.models.Board;
import info.scelus.dotsandboxes.game.controllers.Game;

/**
 * Class responsible for displaying and interacting with the board
 */
public class BoardView extends View {

    private Game game;              // The game which is being played
    private int horizontalOffset;   // Offset at the left and right to display the dots grid
    private int verticalOffset;     // Offset at the top and bottom to display the dots grid

    // TODO: load them from XML resource
    private int boxSide = 80;       // default size for the box
    private int snapLength = 16;    // default distance for a touch to "snap" to a line
    private int dotRadius = 4;      // the radius of the dots

    private Paint linePaint;
    private Paint lineTempPaint;
    private Paint dotPaint;
    private Paint boxPaint;

    // the bounds of the touch area that can beused for placing lines
    // it is bound by -snapLength and touchWidth for the orizontal dimension
    // and by -snapLength and touchHeight for the vertical dimension
    private float touchWidth;
    private float touchHeight;

    // colors of the player boxes
    private int colorPlayer1;
    private int colorPlayer2;

    // temp coordinates for calculations
    private float x1temp, y1temp, x2temp, y2temp;
    private boolean drawTemp = false;

    public BoardView(Context context) {
        super(context);
        init();
    }

    public BoardView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public BoardView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    /**
     * Initialize all paints required to draw on a {@link Canvas}
     */
    private void init () {
        colorPlayer1 = ContextCompat.getColor(getContext(), R.color.boxPlayer1);
        colorPlayer2 = ContextCompat.getColor(getContext(), R.color.boxPlayer2);

        linePaint = new Paint();
        linePaint.setAntiAlias(true);
        linePaint.setStrokeWidth(4f);
        linePaint.setColor(ContextCompat.getColor(getContext(), R.color.line));

        lineTempPaint = new Paint();
        lineTempPaint.setAntiAlias(true);
        lineTempPaint.setStrokeWidth(4f);
        lineTempPaint.setColor(ContextCompat.getColor(getContext(), R.color.line_temp));

        dotPaint = new Paint();
        dotPaint.setAntiAlias(true);

        boxPaint = new Paint();
        boxPaint.setAntiAlias(true);
    }

    /**
     * Draws everything in a specific order
     * 1. boxes
     * 2. lines
     * 3. temp lines
     * 4. dots
     * @param canvas the canvas to draw on
     */
    @Override
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (game == null)
            return;

        Board board = game.getBoard();

        int x1, y1, x2, y2;
        for (int i = 0; i < board.getRows(); i++)
            for (int j = 0; j < board.getColumns(); j++) {
                Board.Box box = board.getBoxAt(i, j);

                // draw the box
                if (box.player == Game.Player.PLAYER1)
                    boxPaint.setColor(colorPlayer1);
                else
                    boxPaint.setColor(colorPlayer2);

                if (box.left && box.right && box.bottom && box.top) {
                    x1 = horizontalOffset + j * boxSide;
                    y1 = verticalOffset + i * boxSide;
                    x2 = horizontalOffset + (j + 1) * boxSide;
                    y2 = verticalOffset + (i + 1) * boxSide;

                    canvas.drawRect(x1, y1, x2, y2, boxPaint);
                }

                if (box.top) {
                    x1 = horizontalOffset + j * boxSide;
                    x2 = horizontalOffset + (j+1) * boxSide;
                    y1 = verticalOffset + i * boxSide;
                    y2 = verticalOffset + i * boxSide;

                    canvas.drawLine(x1, y1, x2, y2, linePaint);
                }

                if (box.left) {
                    x1 = horizontalOffset + j * boxSide;
                    x2 = horizontalOffset + j * boxSide;
                    y1 = verticalOffset + i * boxSide;
                    y2 = verticalOffset + (i+1) * boxSide;

                    canvas.drawLine(x1, y1, x2, y2, linePaint);
                }

                if (box.right && j == board.getColumns() - 1) {
                    x1 = horizontalOffset + (j+1) * boxSide;
                    x2 = horizontalOffset + (j+1) * boxSide;
                    y1 = verticalOffset + i * boxSide;
                    y2 = verticalOffset + (i+1) * boxSide;

                    canvas.drawLine(x1, y1, x2, y2, linePaint);
                }

                if (box.bottom && i == board.getRows() - 1) {
                    x1 = horizontalOffset + j * boxSide;
                    x2 = horizontalOffset + (j+1) * boxSide;
                    y1 = verticalOffset + (i+1) * boxSide;
                    y2 = verticalOffset + (i+1) * boxSide;

                    canvas.drawLine(x1, y1, x2, y2, linePaint);
                }
            }

        // draw the temp line
        if (drawTemp && (x1temp > 0 && x1temp <= board.getColumns() &&
                        x2temp > 0 && x2temp <= board.getColumns() &&
                        y1temp > 0 && y1temp <= board.getRows() &&
                        y2temp > 0 && y2temp <= board.getRows())) {

            canvas.drawLine(x1temp*boxSide + horizontalOffset,
                            y1temp*boxSide + verticalOffset,
                            x2temp*boxSide + horizontalOffset,
                            y2temp*boxSide + verticalOffset, lineTempPaint);
        }

        // draw the dots
        for (int i = 0; i <= board.getRows(); i++)
            for (int j = 0; j <= board.getColumns(); j++) {
                x1 = horizontalOffset + j * boxSide;
                y1 = verticalOffset + i * boxSide;
                canvas.drawCircle(x1, y1, dotRadius, dotPaint);
            }
    }

    /**
     * Sets the game object and calculates the width and height of the touch area
     * @param game
     */
    public void setGame(Game game) {
        this.game = game;
        requestLayout();
    }

    /**
     * Overriden in order to maintain a square aspect ratio of the view.
     * Also used to calculate board specific measures
     * @param widthMeasureSpec
     * @param heightMeasureSpec
     */
    @SuppressWarnings("SuspiciousNameCombination")
    @Override
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec){
        int width = MeasureSpec.getSize(widthMeasureSpec);
        int height = MeasureSpec.getSize(heightMeasureSpec);

        int size;
        if (width < height) {
            super.onMeasure(widthMeasureSpec, widthMeasureSpec);
            height = width;
            size = width - getPaddingLeft() - getPaddingRight();
        }
        else {
            super.onMeasure(heightMeasureSpec, heightMeasureSpec);
            width = height;
            size = height - getPaddingTop() - getPaddingBottom();
        }

        if (game == null)
            return;

        if (game.getBoard().getColumns() < game.getBoard().getRows()) {
            boxSide = size / game.getBoard().getRows();
        }
        else {
            boxSide = size / game.getBoard().getColumns();
        }

        this.horizontalOffset = (width - size) / 2;
        this.verticalOffset = (height - size) / 2;

        this.touchWidth = game.getBoard().getColumns() * boxSide + snapLength;
        this.touchHeight = game.getBoard().getRows() * boxSide + snapLength;
    }

    /**
     * Using the native {@link View} method to service the touch events
     * @param motionEvent the motion event to be processed
     * @return returns whether the MotionEvent was consumed
     */
    @Override
    public boolean onTouchEvent(MotionEvent motionEvent) {
        if (game == null)
            return false;

        Board board = game.getBoard();
        switch(motionEvent.getAction()) {
            case MotionEvent.ACTION_DOWN:
            case MotionEvent.ACTION_MOVE: {
                // calculate where on the view did the motion event occur
                float touchX = motionEvent.getX() - horizontalOffset;
                float touchY = motionEvent.getY() - verticalOffset;

                // check if the touch event was within the touch area
                if (touchX < -snapLength ||
                    touchX > touchWidth ||
                    touchY < -snapLength ||
                    touchY > touchHeight)
                    return false;

                // calculate on which box did the touch happen
                float rowY = Math.abs(touchY) / boxSide;
                float columnX = Math.abs(touchX) / boxSide;

                double deltaXLeft, deltaYDown, deltaXRight, deltaYUp, deltaX, deltaY;

                deltaXLeft = columnX - Math.floor(columnX);
                deltaXRight = Math.ceil(columnX) - columnX;
                deltaYDown = rowY - Math.floor(rowY);
                deltaYUp = Math.ceil(rowY) - rowY;

                deltaX = deltaXLeft > deltaXRight ? deltaXRight : deltaXLeft;
                deltaY = deltaYDown > deltaYUp ? deltaYUp : deltaYDown;

                if (deltaX < deltaY) {
                    if (deltaX < snapLength) {
                        x1temp = (float) Math.floor(columnX);
                        x2temp = x1temp;
                        y1temp = (float) Math.floor(rowY);
                        y2temp = y1temp + 1;

                        if (deltaX == deltaXRight) {
                            x1temp += 1;
                            x2temp += 1;
                        }
                        drawTemp = true;
                    }
                    else {
                        x1temp = 0;
                        x2temp = 0;
                        y1temp = 0;
                        y2temp = 0;
                        drawTemp = false;
                    }
                }
                else if (deltaX > deltaY){
                    if (deltaY < snapLength) {
                        x1temp = (float) Math.floor(columnX);
                        x2temp = x1temp + 1;
                        y1temp = (float) Math.floor(rowY);
                        y2temp = y1temp;

                        if (deltaY == deltaYUp) {
                            y1temp += 1;
                            y2temp += 1;
                        }
                        drawTemp = true;
                    }
                    else {
                        x1temp = 0;
                        x2temp = 0;
                        y1temp = 0;
                        y2temp = 0;
                        drawTemp = false;
                    }
                }

                invalidate();
                return true;
            }

            case MotionEvent.ACTION_UP:
                int numberDotStart = ((int) y1temp) * (board.getColumns() + 1) + (int) x1temp;
                int numberDotEnd = ((int) y2temp) * (board.getRows() + 1) + (int) x2temp;
                game.makeAMove(numberDotStart, numberDotEnd);
                invalidate();

            case MotionEvent.ACTION_CANCEL: {
                drawTemp = false;
                x1temp = 0;
                x2temp = 0;
                y1temp = 0;
                y2temp = 0;
                invalidate();
                return true;
            }
        }
        return false;
    }
}
