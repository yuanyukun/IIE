package com.horem.parachute.common;

import android.app.Dialog;
import android.content.Context;

/**
 * Created by user on 2016/3/25.
 */
public class PopupDialog extends Dialog {

    public PopupDialog(Context context) {
        super(context);
    }

    public PopupDialog(Context context, int themeResId) {
        super(context, themeResId);
    }

    protected PopupDialog(Context context, boolean cancelable, OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
    }

}
