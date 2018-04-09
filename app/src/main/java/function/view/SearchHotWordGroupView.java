package function.view;

import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.tanyang.favoritecode.R;

import java.util.List;

import utils.DisplayUtil;

/**
 * Created by tanyang on 2018/3/20.
 */

public class SearchHotWordGroupView extends LinearLayout {

    private Context context;

    public SearchHotWordGroupView(Context context) {
        super(context);
        this.context = context;
        setOrientation(VERTICAL);//设置方向
    }

    public SearchHotWordGroupView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        setOrientation(VERTICAL);//设置方向
    }

    public SearchHotWordGroupView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        setOrientation(VERTICAL);//设置方向
    }


    /**
     * 外部接口调用
     *
     * @param items
     * @param onItemClick
     */
    public void initViews(String items[], final OnSearchHotWordsItemClick onItemClick) {
        int length = 0;//一行加载item 的宽度

        LinearLayout layout = null;

        LayoutParams layoutLp = null;

        boolean isNewLine = true;//是否换行

        int screenWidth = getScreenWidth() - DisplayUtil.dip2px(36);//屏幕的宽度 - marginLeft - marginRight

        int size = items.length;
        for (int i = 0; i < size; i++) {//便利items
            if (isNewLine) {//是否开启新的一行
                layout = new LinearLayout(context);
                layout.setOrientation(HORIZONTAL);
                layoutLp = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                layoutLp.bottomMargin = DisplayUtil.dip2px(12);
            }

            View view = LayoutInflater.from(context).inflate(R.layout.search_hot_tip_item, null);
            TextView itemView = (TextView) view.findViewById(R.id.text);
//            if (i < 3) {
//                itemView.setBackground(context.getResources().getDrawable(R.drawable.bg_hot_word_origan));
//                itemView.setTextColor(context.getResources().getColor(R.color.orange_ff6101));
//            }
            itemView.setText(items[i]);

            final int j = i;
            itemView.setOnClickListener(new OnClickListener() {//给每个item设置点击事件
                @Override
                public void onClick(View v) {
                    if (null != onItemClick) {
                        onItemClick.onClick(j);
                    }
                }
            });

            //设置item的参数
            LayoutParams itemLp = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            itemLp.rightMargin = DisplayUtil.dip2px(10);

            //得到当前行的长度
            length += DisplayUtil.dip2px(10) + getViewWidth(itemView);
            if (length > screenWidth) {//当前行的长度大于屏幕宽度则换行
                length = 0;
                isNewLine = true;
                addView(layout, layoutLp);
                i--;
            } else {//否则添加到当前行
                isNewLine = false;
                layout.addView(view, itemLp);
            }
        }
        addView(layout, layoutLp);
    }

    /**
     * @param items
     * @param onItemClick
     */
    public void initViews(List<String> items, OnSearchHotWordsItemClick onItemClick) {
        if (items != null && items.size() > 0) {
            int size = items.size();
            String[] titles = new String[size];
            for (int i = 0; i < size; i++) {
                titles[i] = items.get(i);
            }
            initViews(titles, onItemClick);
        }
    }

//    public void init(List<HotWordBean> items, OnSearchHotWordsItemClick onItemClick) {
//        List<String> results = new ArrayList<>();
//        for (int i = 0; i < items.size(); i++) {
//            HotWordBean hotWordBean = items.get(i);
//            if (hotWordBean != null) {
//                String word = hotWordBean.getWord();
//                if (!TextUtils.isEmpty(word)) {
//                    results.add(word);
//                }
//            }
//        }
//        //只取前10个
//        if (results.size() > 10) {
//            results = results.subList(0, 10);
//        }
//        initViews(results, onItemClick);
//    }

    /**
     * 得到手机屏幕的宽度
     *
     * @return
     */
    private int getScreenWidth() {
        DisplayMetrics dm = new DisplayMetrics();
        ((Activity) context).getWindowManager().getDefaultDisplay().getMetrics(dm);
        return dm.widthPixels;
    }

    /**
     * 得到view控件的宽度
     *
     * @param view
     * @return
     */
    private int getViewWidth(View view) {
        int w = MeasureSpec.makeMeasureSpec(0,
                MeasureSpec.UNSPECIFIED);
        int h = MeasureSpec.makeMeasureSpec(0,
                MeasureSpec.UNSPECIFIED);
        view.measure(w, h);
        return view.getMeasuredWidth();
    }
}

