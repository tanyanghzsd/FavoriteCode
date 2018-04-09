package function.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.HorizontalScrollView;

/**
 * 水平滑动view
 * Created by kingyang on 2017/3/7.
 */

public class HorSlideView extends HorizontalScrollView{

    private int mCheckViewDx;

    public HorSlideView(Context context) {
        this(context, null);
    }

    public HorSlideView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setHorizontalScrollBarEnabled(false);
        setVerticalScrollBarEnabled(false);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        int action = ev.getAction();
        if (action == MotionEvent.ACTION_DOWN) {
            mCheckViewDx = 0;
        }
        if (action == MotionEvent.ACTION_MOVE && ignoreTouchEvent()) {
            return false;
        }
        return super.dispatchTouchEvent(ev);
    }

    public boolean canScroll(int dx) {
        mCheckViewDx = dx;
        if (ignoreTouchEvent()) {
            return false;
        }
        return true;
    }

    private boolean ignoreTouchEvent() {
        View child = getChildAt(0);
        if (child == null ||
                child.getWidth() <= getWidth() - getPaddingLeft() - getPaddingRight() ||
                getScrollX() == 0 && mCheckViewDx > 0 ||
                getScrollX() ==
                        child.getWidth() - getWidth() + getPaddingLeft() + getPaddingRight() &&
                        mCheckViewDx < 0) {
            return true;
        }
        return false;
    }
}
