package com.horem.parachute.menu;

import android.app.Activity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.PopupWindow;

import com.horem.parachute.R;
import com.horem.parachute.adapter.PopupBalloonRecyclerViewAdapter;
import com.horem.parachute.adapter.PopupTaskRecyclerViewAdapter;
import com.horem.parachute.login.bean.SubTaskBean;
import com.horem.parachute.task.bean.BalloonBean;
import com.horem.parachute.util.Utils;

import java.util.ArrayList;

/**
 * Created by user on 2016/4/15.
 */
public class PopupBalloonInfo extends PopupWindow{


    private Activity activity;
    private ArrayList<BalloonBean> lists;
    private int position;
    private LayoutInflater mInflater;

    public PopupBalloonInfo(Activity activity, ArrayList<BalloonBean> lists, int position) {
        this.activity = activity;
        this.lists = lists;
        this.position = position;

        init();
    }

    private void init() {
        mInflater = LayoutInflater.from(activity);
        final View contentView = mInflater.inflate(R.layout.main_popup_show_view,null);
        RecyclerView recyclerView = (RecyclerView) contentView.findViewById(R.id.popup_task_options);
        LinearLayoutManager manager = new LinearLayoutManager(activity){
            @Override
            public RecyclerView.LayoutParams generateDefaultLayoutParams() {
                // 这里要复写一下，因为默认宽高都是wrap_content
                // 这个不复写，你点击的背景色就只充满你的内容
                return new RecyclerView.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT);
            }

        };
        manager.setOrientation(LinearLayoutManager.HORIZONTAL);
        manager.scrollToPositionWithOffset(position, Utils.dp2px(activity,50));
        recyclerView.setLayoutManager(manager);
        PopupBalloonRecyclerViewAdapter adapter = new PopupBalloonRecyclerViewAdapter(activity,lists,activity);
        recyclerView.setAdapter(adapter);
        contentView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {

                int height = contentView.findViewById(R.id.popup_task_options).getTop();//弹框高度
                int bottom = contentView.findViewById(R.id.popup_task_options).getBottom();//弹框底部高度
                int y = (int) motionEvent.getY();//触摸高度

                if(motionEvent.getAction() == motionEvent.ACTION_DOWN){
                    if(y < height || y > bottom){
                        dismiss();
                    }
                }
                return true;
            }
        });


        setContentView(contentView);
        setWidth(LinearLayout.LayoutParams.MATCH_PARENT);
        setHeight(LinearLayout.LayoutParams.MATCH_PARENT);
        setAnimationStyle(R.style.AnimBottom);
        setFocusable(true);
        backgroundAlpha(0.5f,0.5f);
        this.setOnDismissListener(new OnDismissListener() {
            @Override
            public void onDismiss() {
                backgroundAlpha(0f,1f);
            }
        });
    }


    /**
     * 设置添加屏幕的背景透明度
     * @param bg
     * @param alpha
     */
    private  void backgroundAlpha(float bg,float alpha)
    {
        WindowManager.LayoutParams lp = activity.getWindow().getAttributes();
        lp.alpha = alpha;
        lp.dimAmount = bg;
        activity.getWindow().setAttributes(lp);
        activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
    }
}
