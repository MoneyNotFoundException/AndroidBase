package cn.lianshi.library.mvvmbase.widget.indexbar;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import cn.lianshi.library.mvvmbase.utils.ui.DensityUtils;


/**
 * Created by yx on 2016/7/22.
 * 快速索引控件
 */
public class QuickIndexBar extends View {

    private String[] LETTERS;
    private Paint mPaint;
    private Canvas mCanvas;
    private float mCellHeight;//单元格高
    private int mCellWidth;//单元格宽
    Rect mBounds = new Rect();
    private int mCurrentIndex = -1;//当前索引

    public QuickIndexBar(Context context) {
        this(context, null);
    }

    public QuickIndexBar(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public QuickIndexBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setColor(Color.rgb(85,136,255));
        mPaint.setTextSize(DensityUtils.dip2px(context,12));
        mPaint.setTypeface(Typeface.DEFAULT_BOLD);
    }

    /**
     * 必须设置绘制的letters
     * @param letters
     */
    public void setLetters(String[] letters) {
        LETTERS = letters;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        this.mCanvas = canvas;
        for (int i = 0; i < LETTERS.length; i++) {
            String letter = LETTERS[i];
            float x = mCellWidth * 0.5f - mPaint.measureText(letter) * 0.5f;
            //获取文本高度
            mPaint.getTextBounds(letter, 0, letter.length(), mBounds);
            mPaint.setColor(i == mCurrentIndex ? Color.WHITE
                    : Color.rgb(85,136,255));
            int textHeight = mBounds.height();
            float y = textHeight * 0.5f + mCellHeight * 0.5f + mCellHeight * i;
            canvas.drawText(letter,x,y,mPaint);
        }

    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int index = -1;
        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:
                index = (int) (event.getY() / mCellHeight);
                if (index >= 0 && index < LETTERS.length) {
                    if (index != mCurrentIndex) {
                        if (mOnLetterUpdateListener != null) {
                            mOnLetterUpdateListener.onLetterUpdate(LETTERS[index]);
                        }
                        mCurrentIndex = index;
                    }
                }
                break;
            case MotionEvent.ACTION_MOVE:
                index = (int) (event.getY() / mCellHeight);
                if (index >= 0 && index < LETTERS.length) {
                    if (index != mCurrentIndex) {
                        if (mOnLetterUpdateListener != null) {
                            mOnLetterUpdateListener.onLetterUpdate(LETTERS[index]);
                        }
                        mCurrentIndex = index;
                    }
                }
                break;
            case MotionEvent.ACTION_UP:
                mCurrentIndex = -1;
                break;
        }
        return true;
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        int height = getMeasuredHeight();//控件高度
        //增加精确度
        mCellHeight = height * 1.0f / LETTERS.length;
        mCellWidth = getMeasuredWidth();
    }

    //接口回调
    private OnLetterUpdateListener mOnLetterUpdateListener;

    public void setOnLetterUpdateListener(OnLetterUpdateListener onLetterUpdateListener) {
        this.mOnLetterUpdateListener = onLetterUpdateListener;
    }

    public interface OnLetterUpdateListener {
        void onLetterUpdate(String letter);
    }
}
