package com.horem.parachute.customview;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.bumptech.glide.DrawableTypeRequest;
import com.bumptech.glide.Glide;
import com.horem.parachute.R;
import com.horem.parachute.menu.Screen;
import com.horem.parachute.mine.AppUserInfo;
import com.horem.parachute.util.ScreenBean;
import com.horem.parachute.util.Utils;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by yuanyukun on 2016/7/9.
 */
public class CustomHeadView extends RelativeLayout {

    private static final  int AUTH_NONE = 0;            //无认证
    private static final  int AUTH_ORGANIZATION = 10;   //机构认证
    private static final  int AUTH_PERSONAL = 20;       //个人认证
    private Context context;
//    DrawableTypeRequest<String> drawableTypeRequest;

    private CircleImageView originHead;
    private ImageView userTypeMark;
    public CustomHeadView(Context context) {
        super(context);
        init(context);
    }

    public CustomHeadView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        init(context);
    }

    private void init(Context context) {
        LayoutInflater.from(context).inflate(R.layout.custom_head_view,this);
        RelativeLayout.LayoutParams lp = new LayoutParams(ScreenBean.getScreenWidth(), ScreenBean.getScreenWidth());
        this.setLayoutParams(lp);
        originHead  = (CircleImageView) findViewById(R.id.custom_head_view_head);
        userTypeMark = (ImageView) findViewById(R.id.custom_head_view_mark);
    }
    public void showHeadView(String headName, int authType, final long memberId,boolean clickable){
        switch (authType){
            case AUTH_NONE:
                userTypeMark.setVisibility(View.INVISIBLE);
                break;
            case AUTH_ORGANIZATION:
                userTypeMark.setVisibility(View.VISIBLE);
                userTypeMark.setImageResource(R.mipmap.organization_auth_32);
                break;
            case AUTH_PERSONAL:
                userTypeMark.setVisibility(View.VISIBLE);
                userTypeMark.setImageResource(R.mipmap.personal_auth_32);
                break;
        }
//         drawableTypeRequest = Glide.with(context).load(Utils.getHeadeUrl(headName));
        Glide.with(getContext()).load(Utils.getHeadeUrl(headName))
//                .placeholder(R.mipmap.circle_dark)
                .into(originHead);
        if(clickable) {
            originHead.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, AppUserInfo.class);
                    intent.putExtra("memberId", Long.valueOf(memberId));
                    context.startActivity(intent);
                }
            });
        }
    }
    public CustomHeadView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }
}
