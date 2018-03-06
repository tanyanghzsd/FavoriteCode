package widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

/**
 * 联系人字母侧边栏
 * @author linzewu
 * @date 2017/8/9
 */

public class LetterSidebar extends View {
    /* 供选择的字母 */
    private static final String[] LETTER = {
        "A", "B", "C", "D", "E", "F", "G",
        "H", "I", "J", "K", "L", "M", "N",
        "O", "P", "Q", "R", "S", "T",
        "U", "V", "W", "X", "Y", "Z",
        "*"
    };
    
    private static final int COLOR_DEFAULT = 0xFF666666;
    private static final int COLOR_SELECTED = 0xFFFFFFFF;
    
    /* 当前的字母选择 */
    private int mCurrentSelect = -1;
    /* 字母侧边栏上边距 */
    private int mLetterMarginTop = 30;
    /* 字母侧边栏下边距 */
    private int mLetterMarginBottom = 30;
    
    private Paint mPaint = new Paint();
    
    
    public LetterSidebar(Context context) {
        super(context);
    }

    public LetterSidebar(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public LetterSidebar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        
        float width = getWidth();
        float height = getHeight();
        
        float perHeight = (height - mLetterMarginTop - mLetterMarginBottom) / LETTER.length;
        
        for (int i = 0 ; i < LETTER.length ; i++) {
            if (i == mCurrentSelect) {
                mPaint.setColor(COLOR_SELECTED);
            } else {
                mPaint.setColor(COLOR_DEFAULT);
            }
            mPaint.setAntiAlias(true);
            mPaint.setTextSize(30);

            /* 字符串左边在坐标上的位置 */
            float xPos = width / 2 - mPaint.measureText(LETTER[i]) / 2;
            /* 字符串baseline在坐标上的位置 */
            float yPos = perHeight * i + perHeight + mLetterMarginTop;
            canvas.drawText(LETTER[i], xPos, yPos, mPaint);
        }
        
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                if (mLetterSideBarListener != null) {
                    mCurrentSelect = -1;
                    mLetterSideBarListener.onSelectFinish();
                }
                invalidate();
                break;
            default:    
                if (mLetterSideBarListener != null) {
                    /* 每个字母占用的高度 */
                    int perHeight = (getHeight() - mLetterMarginTop - mLetterMarginBottom) / 
                            LETTER.length;
                    if (event.getY() < mLetterMarginTop
                            || event.getY() > (getHeight() - mLetterMarginBottom)) {
                        /* 如果不在字母范围内,跳出 */
                        mCurrentSelect = -1;
                        invalidate();
                        break;
                    }
                    
                    /* 选中字母,触发回调 */
                    int select = ((int) event.getY() - mLetterMarginTop) / perHeight;
                    if (select <= 0) {
                        select = 0;
                    } else if (select >= LETTER.length) {
                        select = LETTER.length - 1;
                    }
                    if (select != mCurrentSelect) {
                        mCurrentSelect = select;
                        //点击的位置Y轴
                        long positionY = perHeight * mCurrentSelect + perHeight + mLetterMarginTop;
                        mLetterSideBarListener.onSelectHeight(positionY);
                        mLetterSideBarListener.onSelect(LETTER[mCurrentSelect]);
                        invalidate();
                    }
                }
                break;
        }
        return true;
    }
    


    public interface LetterSideBarListener {
        void onSelect(String letter);
        void onSelectFinish();
        void onSelectHeight(long positionY);
    }
    
    private LetterSideBarListener mLetterSideBarListener;

    public LetterSideBarListener getLetterSideBarListener() {
        return mLetterSideBarListener;
    }

    public void setLetterSideBarListener(LetterSideBarListener letterSideBarListener) {
        mLetterSideBarListener = letterSideBarListener;
    }
}
