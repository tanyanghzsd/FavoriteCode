package widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;

import com.tanyang.favoritecode.R;


/**
 * 类描述: 实现宽度和高度成一定比率的FrameLayout
 *
 * @author  dengxiaoming
 * @date  [2014年1月19日]
 */
public class ProportionFrameLayout extends FrameLayout {
	/**
	 * 宽跟高的比例
	 */
	public float mProportion = 1f;
	/**
	 * 以宽为标准 true:以宽为标准，计算高  false:以高为标准，计算宽
	 */
	private boolean mWidthStandard = true;
	
	private boolean mEnableProportion = true;
	
	public ProportionFrameLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
		TypedArray typeArray = context.obtainStyledAttributes(attrs, R.styleable.GoPlayProportionLayout);
		mProportion = typeArray.getFloat(R.styleable.GoPlayProportionLayout_proportion, 1f);
		typeArray.recycle();
	}

	public ProportionFrameLayout(Context context) {
		this(context, null);
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		if (!mEnableProportion) {
			super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		} else {
			if (mWidthStandard) {
				int heightMode = View.MeasureSpec.getMode(heightMeasureSpec);
				if (heightMode == View.MeasureSpec.EXACTLY) {
					super.onMeasure(widthMeasureSpec, heightMeasureSpec);
				} else {
					int width = View.MeasureSpec.getSize(widthMeasureSpec);
					int height = (int) (width * mProportion);
					setMeasuredDimension(width, height);
					super.onMeasure(widthMeasureSpec, View.MeasureSpec.makeMeasureSpec(height, View.MeasureSpec.EXACTLY));
				}
			} else {
				int widthMode = View.MeasureSpec.getMode(widthMeasureSpec);
				if (widthMode == View.MeasureSpec.EXACTLY) {
					super.onMeasure(widthMeasureSpec, heightMeasureSpec);
				} else {
					int height = View.MeasureSpec.getSize(heightMeasureSpec);
					int width = (int) (height / mProportion);
					setMeasuredDimension(width, height);
					super.onMeasure(View.MeasureSpec.makeMeasureSpec(width, View.MeasureSpec.EXACTLY), heightMeasureSpec);
				}
			}
		}
	}

	/**
	 * 功能简述: 设置宽跟高的比例
	 * @param proportion
	 */
	public void setProportion(float proportion) {
		mProportion = proportion;
	}

	/**
	 * 设置是否以宽为标准计算高宽
	 * @param standard
	 */
	public void setWidthStandard(boolean standard) {
		mWidthStandard = standard;
	}

	/**
	 * 设置是否开启比例缩放功能
	 * @param enable
	 */
	public void setEnableProportion(boolean enable) {
		mEnableProportion = enable;
	}
}
