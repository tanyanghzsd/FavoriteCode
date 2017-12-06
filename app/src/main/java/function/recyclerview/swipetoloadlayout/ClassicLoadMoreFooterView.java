package function.recyclerview.swipetoloadlayout;

/**
 * Created by tanyang on 2017/12/5.
 */


import android.content.Context;
import android.util.AttributeSet;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.tanyang.favoritecode.R;


/**
 * Created by Aspsine on 2015/9/2.
 */
public class ClassicLoadMoreFooterView extends SwipeLoadMoreFooterLayout {
    private TextView tvLoadMore;
    private ImageView ivSuccess;
    private ProgressBar progressBar;
    private ImageView ivProgressBar;
    private Animation rotate;

    private int mFooterHeight;

    public ClassicLoadMoreFooterView(Context context) {
        this(context, null);
    }

    public ClassicLoadMoreFooterView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ClassicLoadMoreFooterView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mFooterHeight = getResources().getDimensionPixelOffset(R.dimen.load_more_footer_height_classic);
        rotate = AnimationUtils.loadAnimation(context, R.anim.rotate_forever);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        tvLoadMore = (TextView) findViewById(R.id.tvLoadMore);
        ivSuccess = (ImageView) findViewById(R.id.ivSuccess);
        progressBar = (ProgressBar) findViewById(R.id.progressbar);
        ivProgressBar = (ImageView) findViewById(R.id.progressbar_image);
        ivProgressBar.startAnimation(rotate);
    }

    @Override
    public void onPrepare() {
        ivSuccess.setVisibility(GONE);
    }

    @Override
    public void onMove(int y, boolean isComplete, boolean automatic) {
        if (!isComplete) {
            ivSuccess.setVisibility(GONE);
            progressBar.setVisibility(GONE);
            if (-y >= mFooterHeight) {
                tvLoadMore.setText(getContext().getString(R.string.pull_to_refresh_release_label));
            } else {
                tvLoadMore.setText(getContext().getString(R.string.pull_to_refresh_pull_label));
            }
        }
    }

    @Override
    public void onLoadMore() {
        tvLoadMore.setText(getContext().getString(R.string.pull_to_refresh_refreshing_label));
        progressBar.setVisibility(GONE);
    }

    @Override
    public void onRelease() {

    }

    @Override
    public void onComplete() {
        progressBar.setVisibility(GONE);
        ivSuccess.setVisibility(GONE);
    }

    @Override
    public void onReset() {
        ivSuccess.setVisibility(GONE);
    }
}

