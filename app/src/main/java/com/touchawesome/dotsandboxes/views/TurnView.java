package com.touchawesome.dotsandboxes.views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import com.touchawesome.dotsandboxes.R;

import static android.content.ContentValues.TAG;

/**
 * Created by scelus on 20.03.17
 */

public class TurnView extends View {
    private Paint mPaint;

    private Rect leftRect;
    private Rect rightRect;
    private int colorPlayer1;
    private int colorPlayer2;

    public TurnView(Context context) {
        super(context);
        init();
    }

    public TurnView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public TurnView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }
    
    private void init() {
        leftRect = new Rect();
        rightRect = new Rect();


        colorPlayer1 = ContextCompat.getColor(getContext(), R.color.boxPlayer1);
        colorPlayer2 = ContextCompat.getColor(getContext(), R.color.boxPlayer2);

        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setStrokeWidth(30);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        mPaint.setColor(colorPlayer1);
        canvas.drawRect(leftRect, mPaint);

        mPaint.setColor(colorPlayer2);
        canvas.drawRect(rightRect, mPaint);
    }

    @Override
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width = MeasureSpec.getSize(widthMeasureSpec);
        int height = MeasureSpec.getSize(widthMeasureSpec);

        leftRect.set(getPaddingLeft(), getPaddingTop(), width/2, height - getPaddingBottom());
        rightRect.set(width/2, getPaddingTop(), width - getPaddingRight(), height - getPaddingBottom());

        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }
}
