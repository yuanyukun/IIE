package com.horem.parachute.menu;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.PopupWindow;

import com.horem.parachute.R;

/**
 * Created by user on 2016/5/12.
 */
public class IntroduceMenu extends PopupWindow {

    private View contentView;
    private ImageView Image;
    private Activity activity;

    public IntroduceMenu(View contentView, int width, int height) {
        super(contentView, width, height);
    }

    public IntroduceMenu(Activity activity, Drawable img) {
        super(activity);
        this.activity = activity;

        LayoutInflater mInflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        contentView = mInflater.inflate(R.layout.main_introduce_popup,null);
        Image  = (ImageView) contentView.findViewById(R.id.main_introduce_image);
        Image.setBackground(img);
        Image.setAlpha(0.3f);
        Image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        Screen screen = new Screen(activity);
        this.setContentView(contentView);
        this.setWidth(screen.getWidth());
        this.setHeight(screen.getHeight());
        this.setAnimationStyle(R.style.AnimBottom);
        this.setFocusable(true);

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
