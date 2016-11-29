package cn.papaxiong.iie.common;

import android.app.Dialog;
import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import cn.papaxiong.iie.R;

/**
 * Created by user on 2016/3/20.
 */
public class CustomAlertDialog extends Dialog {

    public CustomAlertDialog(Context context) {
        super(context);
    }

    public CustomAlertDialog(Context context, int theme) {
        super(context, theme);
    }

    protected CustomAlertDialog(Context context, boolean cancelable, OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
    }

    public static class Builder implements View.OnClickListener{


        private CustomAlertDialog Dialog;
        private Context context;
        private TextView mTvContent;
        private TextView mTvTitle;
        private View mLayoutTitle;
        private View mViewDivider,mViewBtnTopDivider;
        private Button mBtnConfirm;
        private Button mBtnCancel;

        private OnButtonOnClickListener mOnButtonClickListener;
        public  interface OnButtonOnClickListener{
             void OnConfirmed();
             void OnCanceled();
        }
        public Builder setOnButtonOnClickListener(OnButtonOnClickListener listener){
            this.mOnButtonClickListener = listener;
            return this;
        }

        public Builder(Context context){

            this.context = context;
            LayoutInflater mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            Dialog = new CustomAlertDialog(context, R.style.CustomProgressDialog);
            View layout = mInflater.inflate(R.layout.custom_alert_dialog,null);
            Dialog.setContentView(layout,new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

            mTvContent = (TextView)layout.findViewById(R.id.tv_content);
            mTvTitle = (TextView)layout.findViewById(R.id.tv_title);
            mLayoutTitle = layout.findViewById(R.id.layout_title);

            mBtnConfirm = (Button)layout.findViewById(R.id.btn_confirm);
            mBtnConfirm.setOnClickListener(this);

            mBtnCancel = (Button)layout.findViewById(R.id.btn_cancel);
            mBtnCancel.setOnClickListener(this);

            mViewDivider = layout.findViewById(R.id.view_divider);
            mViewBtnTopDivider = layout.findViewById(R.id.view_btn_top_divider);

        }
        public Builder setBtnBg(){

            mBtnConfirm.setBackgroundColor(context.getResources().getColor(R.color.color_e64));
            mBtnConfirm.setTextColor(context.getResources().getColor(R.color.white));
            mBtnCancel.setBackgroundColor(context.getResources().getColor(R.color.color_999));
            mBtnCancel.setTextColor(context.getResources().getColor(R.color.color_666));
            return this;
        }
        public Builder setTitle(String title) {
            if (!TextUtils.isEmpty(title)) {
                mTvTitle.setText(title);
                mLayoutTitle.setVisibility(View.VISIBLE);
            }
            return this;
        }

        public Builder setContent(String text) {
            mTvContent.setText(text);
            return this;
        }
        //更换显示内容布局
        public Builder setContentView(View view) {
            ViewGroup parent = (ViewGroup)mTvContent.getParent();
            int index = parent.indexOfChild(mTvContent);
            parent.removeViewAt(index);
            parent.addView(view, index);
            return this;
        }
        //确认键文字
        public Builder setConfirmText(String text) {
            mBtnConfirm.setText(text);
            return this;
        }
        //取消键文字
        public Builder setCancelText(String text) {
            mBtnCancel.setText(text);
            return this;
        }
        //隐藏取消键
        public Builder hideCancelButton() {
            mBtnCancel.setVisibility(View.GONE);
            mViewDivider.setVisibility(View.GONE);//确认和取消键之间的分隔符
            return this;
        }
        //隐藏所有的按键
        public Builder hideButtons() {
            hideCancelButton();

            mBtnConfirm.setVisibility(View.GONE);
            mViewBtnTopDivider.setVisibility(View.GONE);
            return this;
        }

        //在对话框外部点击事件的处理
        public Builder setCancelOnTouchOutside(boolean flag) {
            Dialog.setCanceledOnTouchOutside(flag);
            return this;
        }

        public Builder setCancelable(boolean flag) {
            Dialog.setCancelable(flag);
            return this;
        }

        public Builder setConfirmButtonTextColor(int colorId) {
            mBtnConfirm.setTextColor(colorId);
            return this;
        }


        @Override
        public void onClick(View view) {
            switch (view.getId()){

                case R.id.btn_confirm:
                    if (mOnButtonClickListener != null) {
                        dismiss();
                        mOnButtonClickListener.OnConfirmed();
                    }
                    break;
                case R.id.btn_cancel:
                    if (mOnButtonClickListener != null) {
                        dismiss();
                        mOnButtonClickListener.OnCanceled();
                    }
                    break;
            }
        }
        public void dismiss(){
            Dialog.dismiss();
        }
        public void show() {
            Dialog.show();
        }
        public CustomAlertDialog getAlertDialog() {
            return Dialog;
        }

    }
}
