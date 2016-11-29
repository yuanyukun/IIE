package cn.papaxiong.iie.ui.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import cn.papaxiong.iie.R;
import cn.papaxiong.iie.utils.DimenUtils;
import cn.papaxiong.iie.utils.L;

import static android.widget.RelativeLayout.TRUE;

/**
 * Created by Administrator on 2016/11/29.
 */

public class Switcher extends LinearLayout{
    
    private RelativeLayout leftLayout,rightLayout;
    private View divdualLine;
    
    private TextView leftTitle,rightTitle;

    private float leftLayoutColor;
    private float rightLayotuColor;

    private float leftTextSize;
    private String leftText;
    private int leftTextColor;

    private float rightTextSize;
    private String rightText;
    private int rightTextColor;
    
    private RelativeLayout.LayoutParams leftParams,rightParams;
    private LayoutParams leftLayoutParams;
    private LayoutParams rightLayoutParams;

    public Switcher(Context context, AttributeSet attrs) {
        super(context, attrs);

        TypedArray ta = context.obtainStyledAttributes(attrs,R.styleable.Switcher);

        leftText = ta.getString(R.styleable.Switcher_leftText);
        leftTextColor = ta.getColor(R.styleable.Switcher_leftTextColor,0);
        leftTextSize = ta.getDimension(R.styleable.Switcher_leftTextSize,0);

        rightText = ta.getString(R.styleable.Switcher_rightText);
        rightTextColor = ta.getColor(R.styleable.Switcher_rightTextColor,0);
        rightTextSize = ta.getDimension(R.styleable.Switcher_rightTextSize,0);

        leftLayoutColor = ta.getColor(R.styleable.Switcher_leftLayoutColor,0);
        rightLayotuColor = ta.getColor(R.styleable.Switcher_rightLayoutColor,0);

        ta.recycle();
        
        leftTitle = new TextView(context);
        rightTitle = new TextView(context);
        
        leftTitle.setText(leftText);
        leftTitle.setTextColor(leftTextColor);
        leftTitle.setTextSize(leftTextSize);
        
        rightTitle.setText(rightText);
        rightTitle.setTextColor(rightTextColor);
        rightTitle.setTextSize(rightTextSize);

        leftLayout = new RelativeLayout(context);
        leftParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        leftParams.addRule(RelativeLayout.CENTER_IN_PARENT,TRUE);
        leftLayout.addView(leftTitle,leftParams);

        rightLayout = new RelativeLayout(context);
        rightParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        rightParams.addRule(RelativeLayout.CENTER_IN_PARENT,TRUE);
        rightLayout.addView(rightTitle,rightParams);

        leftLayoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        leftLayoutParams.weight = 1;

        divdualLine = new View(context);
        LinearLayout.LayoutParams divdualParams = new LinearLayout.LayoutParams(DimenUtils.dp2px(context,1), ViewGroup.LayoutParams.MATCH_PARENT);

        rightLayoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        rightLayoutParams.weight = 1;

        addView(leftLayout,leftLayoutParams);
        addView(divdualLine);
        addView(rightLayout,rightLayoutParams);

        leftLayout.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                L.i("leftButtonClicked");
            }
        });

        rightLayout.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                L.i("rightButtonClicked");
            }
        });

    }
}
