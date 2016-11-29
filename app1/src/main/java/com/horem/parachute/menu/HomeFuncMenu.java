package com.horem.parachute.menu;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.horem.parachute.R;

/**
 * Created by user on 2016/3/19.
 */
public class HomeFuncMenu extends PopupWindow {

    private  View contentView;
    private TextView photoBtn;
    private TextView videoBtn;
    private TextView cancelBtn;
    private Activity activity;

    private OnMenuClickListener listener;
    public interface  OnMenuClickListener {

         void  firstLevelClick();
         void  secondLevelClick();
    }
    public void setOnMenuClickListener(OnMenuClickListener listener){
        this.listener = listener;
    }

    public HomeFuncMenu(Activity activity,String[] names) {
        super(activity);
        this.activity = activity;
        LayoutInflater mInflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        contentView = mInflater.inflate(R.layout.main_popup_func_view,null);

        photoBtn = (TextView) contentView.findViewById(R.id.popup_func_photo);
        photoBtn.setText(names[0]);
        videoBtn = (TextView) contentView.findViewById(R.id.popup_func_video);
        videoBtn.setText(names[1]);
        cancelBtn = (TextView) contentView.findViewById(R.id.popup_func_cancel);
        cancelBtn.setText(names[2]);
        photoBtn.setOnClickListener(itemsOnclick);
        videoBtn.setOnClickListener(itemsOnclick);
        cancelBtn.setOnClickListener(itemsOnclick);

        this.setContentView(contentView);
        this.setWidth(LinearLayout.LayoutParams.MATCH_PARENT);
        this.setHeight(LinearLayout.LayoutParams.WRAP_CONTENT);
        this.setAnimationStyle(R.style.AnimBottom);
        this.setFocusable(true);
        backgroundAlpha(0.7f);
        contentView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {

                int height = contentView.findViewById(R.id.top_level).getTop();//弹框高度
                int y = (int) motionEvent.getY();//触摸高度
                if(motionEvent.getAction() == motionEvent.ACTION_UP){
                    if(y < height){
                        dismiss();
                    }
                }
                return false;
            }
        });
    }
    View.OnClickListener itemsOnclick = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            dismiss();
            switch (view.getId()) {
                case R.id.popup_func_photo:
                    listener.firstLevelClick();
                    dismiss();
                    break;
                case R.id.popup_func_video:
                    listener.secondLevelClick();
                    dismiss();
                    break;
            }
        }
    };

    @Override
    public void dismiss() {
        super.dismiss();
        backgroundAlpha(1f);
    }
    /**
     * 设置添加屏幕的背景透明度
     * @param bgAlpha
     */
    private  void backgroundAlpha(float bgAlpha)
    {
        WindowManager.LayoutParams lp = activity.getWindow().getAttributes();
        lp.alpha = bgAlpha;
        lp.dimAmount = bgAlpha;
        activity.getWindow().setAttributes(lp);
        activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
    }

}
