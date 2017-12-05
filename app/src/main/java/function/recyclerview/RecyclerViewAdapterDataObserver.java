package function.recyclerview;

import android.support.v7.widget.RecyclerView;

/**
 * Created by tanyang on 2017/12/5.
 */

class RecyclerViewAdapterDataObserver extends RecyclerView.AdapterDataObserver {


    private OnAdapterDataChangedListener mListener;

    public RecyclerViewAdapterDataObserver(OnAdapterDataChangedListener listener) {
        mListener = listener;
    }

    @Override
    public void onChanged() {
        super.onChanged();
        dispatchChange();
    }

    @Override
    public void onItemRangeChanged(int positionStart, int itemCount) {
        super.onItemRangeChanged(positionStart, itemCount);
        dispatchChange();
    }

    @Override
    public void onItemRangeChanged(int positionStart, int itemCount, Object payload) {
        super.onItemRangeChanged(positionStart, itemCount, payload);
        dispatchChange();
    }

    @Override
    public void onItemRangeInserted(int positionStart, int itemCount) {
        super.onItemRangeInserted(positionStart, itemCount);
        dispatchChange();
    }

    @Override
    public void onItemRangeRemoved(int positionStart, int itemCount) {
        super.onItemRangeRemoved(positionStart, itemCount);
        dispatchChange();
    }

    private void dispatchChange() {
        if (mListener != null) {
            mListener.onChanged();
        }
    }

    interface OnAdapterDataChangedListener {
        void onChanged();
    }
}
