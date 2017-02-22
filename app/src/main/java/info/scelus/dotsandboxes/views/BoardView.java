package info.scelus.dotsandboxes.views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import info.blackbear.scelus.dotsandboxes.R;
import info.scelus.dotsandboxes.external.Board;
import info.scelus.dotsandboxes.external.Game;

/**
 * Created by SceLus on 11/10/2014.
 */
public class BoardView extends View {
    private Board board;
    private int horizontalOffset;
    private int verticalOffset;
    private int boxSide = 80;
    private int snapLength = 16;
    private int dotRadius = 4; // the radius of the dots

    private Paint linePaint;
    private Paint lineTempPaint;
    private Paint dotPaint;
    private Paint boxPaint;

    private float touchWidth;
    private float touchHeight;

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

    private void init () {
        linePaint = new Paint();
        linePaint.setAntiAlias(true);
        linePaint.setStrokeWidth(4f);
        linePaint.setColor(getResources().getColor(R.color.line));

        lineTempPaint = new Paint();
        lineTempPaint.setAntiAlias(true);
        lineTempPaint.setStrokeWidth(4f);
        lineTempPaint.setColor(getResources().getColor(R.color.line_temp));

        dotPaint = new Paint();
        dotPaint.setAntiAlias(true);

        boxPaint = new Paint();
        boxPaint.setAntiAlias(true);
    }

    @Override
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (board == null)
            return;

        // draw the lines
        int x1, y1, x2, y2;
        for (int i = 0; i < board.getRows(); i++)
            for (int j = 0; j < board.getColumns(); j++) {
                Board.Box box = board.getBoxAt(i, j);

                // draw the box
                if (box.player == Game.Player.PLAYER1)
                    boxPaint.setColor(getResources().getColor(R.color.boxPlayer1));
                else
                    boxPaint.setColor(getResources().getColor(R.color.boxPlayer2));

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
                canvas.drawCircle(x1, y1, dotRadius,linePaint);
            }
    }

    public void setBoard(Board board) {
        this.board = board;
        this.touchWidth = board.getColumns() * boxSide + snapLength;
        this.touchHeight = board.getRows() * boxSide + snapLength;
    }

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
            size = width - getPaddingTop() - getPaddingBottom();
        }

        if (board == null)
            return;

        if (width < height) {
            boxSide = size / board.getRows();
        }
        else {
            boxSide = size / board.getColumns();
        }

        horizontalOffset = (width - size) / 2;
        verticalOffset = (height - size) / 2;

        setBoard(board);
    }

    @Override
    public boolean onTouchEvent(MotionEvent motionEvent) {
        if (board == null)
            return false;

        switch(motionEvent.getAction()) {
            case MotionEvent.ACTION_DOWN:
            case MotionEvent.ACTION_MOVE: {
                float touchX = motionEvent.getX() - horizontalOffset;
                float touchY = motionEvent.getY() - verticalOffset;

                if (touchX < -snapLength ||
                    touchX > touchWidth ||
                    touchY < -snapLength ||
                    touchY > touchHeight)
                    return false;

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
                board.setLine((int) x1temp,(int) y1temp,(int) x2temp,(int) y2temp);
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
