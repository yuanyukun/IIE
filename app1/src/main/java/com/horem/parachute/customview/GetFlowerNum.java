package com.horem.parachute.customview;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.horem.parachute.R;
import com.horem.parachute.util.Utils;

/**
 * Created by yuanyukun on 2016/6/27.
 */
public class GetFlowerNum extends LinearLayout{
    private TextView minus;
    private TextView plus;
    private EditText flowers;
    private String currentNumStr;
    private TextView flowersPrice;
    private boolean isHasFocus = false;

    private OnEditTextChangedListener listener;
    public interface OnEditTextChangedListener{
        void onEditNumChanged(String value);
    }
    public void setOnEditTextChanged(OnEditTextChangedListener listener){
        this.listener = listener;
    }



    public GetFlowerNum(Context context) {
        super(context);

    }
    public GetFlowerNum(Context context, AttributeSet attrs) {
        super(context, attrs);
         LayoutInflater.from(context).inflate(R.layout.get_flowers,this);
        minus = (TextView) findViewById(R.id.minus);
        plus = (TextView) findViewById(R.id.plus);
        flowers = (EditText) findViewById(R.id.edit_flowers);
        flowersPrice = (TextView) findViewById(R.id.flower_price);

        flowers.setText("10");
        flowers.setCursorVisible(false);
        flowers.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                flowers.setCursorVisible(true);
            }
        });

        flowersPrice.setText("朵鲜花");
        flowers.setSelection(flowers.getText().toString().length());
        //
        minus.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                onMinusClicked();
            }
        });
        //
        plus.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                onPlusClicked();
            }
        });
        //
        flowers.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                flowers.setSelection(flowers.getText().toString().length());
                listener.onEditNumChanged(getCurrentNum());
            }
        });
    }

    public GetFlowerNum(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }
    public String  getCurrentNum(){

        currentNumStr = flowers.getText().toString();
        return currentNumStr;
    }
    public void onMinusClicked(){
        if(!getCurrentNum().equals("")){

           if( Integer.valueOf(currentNumStr) > 1){
               int value = Integer.valueOf(currentNumStr);
               flowers.setText(String.valueOf(value -1));
           } else{
               flowers.setText(String.valueOf(1));
           }
            flowers.setSelection(flowers.getText().toString().length());
        }else{
            flowers.setText("");
        }
    }
    public void onPlusClicked(){
        if(!getCurrentNum().equals("")){
            if(Integer.valueOf(currentNumStr) >= 9999){
                flowers.setText(9999+"");
            }else {
                int value = Integer.valueOf(getCurrentNum());
                flowers.setText(String.valueOf(value+1));
            }
            flowers.setSelection(flowers.getText().toString().length());
        }else{
            flowers.setText("1");
        }
    }

}
