package function.animator;
import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * Created by tanyang on 2017/12/2.
 * scale动画
 */

public class ScaleInAnimationAdapter extends AnimationAdapter {

    private static final float DEFAULT_SCALE_FROM = .5f;
    private final float mFrom;

    public ScaleInAnimationAdapter(RecyclerView.Adapter adapter) {
        this(adapter, DEFAULT_SCALE_FROM);
    }

    public ScaleInAnimationAdapter(RecyclerView.Adapter adapter, float from) {
        super(adapter);
        mFrom = from;
    }

    @Override
    protected Animator[] getAnimators(View view) {
        ObjectAnimator scaleX = ObjectAnimator.ofFloat(view, "scaleX", DEFAULT_SCALE_FROM, 1f);
        ObjectAnimator scaleY = ObjectAnimator.ofFloat(view, "scaleY", DEFAULT_SCALE_FROM, 1f);
        return new ObjectAnimator[]{scaleX, scaleY};
    }
}
