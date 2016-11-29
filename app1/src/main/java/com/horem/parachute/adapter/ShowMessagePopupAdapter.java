package com.horem.parachute.adapter;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.model.GlideUrl;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.horem.parachute.R;
import com.horem.parachute.common.CustomLoading;
import com.horem.parachute.mine.bean.PhotoListBean;

import java.util.List;

import uk.co.senab.photoview.PhotoView;

/**
 * Created by yuanyukun on 2016/5/26.
 */
public class ShowMessagePopupAdapter extends PagerAdapter{

    private List mLists;
    private Context context;
    private CustomLoading customLoadingDialog;
    public ShowMessagePopupAdapter(List<String> list, Context context) {
        this.mLists = list;
        this.context = context;
    }

    @Override
    public int getCount() {
        return mLists.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {

        PhotoView photoView = new PhotoView(container.getContext());
        final ObjectAnimator anim = ObjectAnimator.ofInt(photoView, "ImageLevel", 0, 100);
        anim.setRepeatCount(ObjectAnimator.INFINITE);
        anim.start();
//
//// your loading code from above
        startLoading("");
        Glide.with(context)
                .load(mLists.get(position))
                .centerCrop()
                .placeholder(R.drawable.frame_loading)
                .crossFade()
                // new listener to stop unnecessary animation when the placeholder is not visible any more
                .listener(new RequestListener<Object, GlideDrawable>() {
                    @Override
                    public boolean onException(Exception e, Object model, Target<GlideDrawable> target, boolean isFirstResource) {
                       anim.cancel();
                        stopLoading();
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(GlideDrawable resource, Object model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                       anim.cancel();
                        stopLoading();
                        return false;
                    }
                })
                .into(photoView);

//        Glide.with(context).load(mLists.get(position))
//                .centerCrop()
//                .crossFade()
//                .placeholder(R.mipmap.background).into(photoView);

        photoView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Log.i("PoPupViewPager","长按事件触发");
                return true;
            }
        });
        // Now just add PhotoView to ViewPager and return it
        container.addView(photoView, ViewPager.LayoutParams.MATCH_PARENT, ViewPager.LayoutParams.MATCH_PARENT);

        return photoView;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
//        super.destroyItem(container, position, object);
        container.removeView((View) object);
    }
    /**
     * 进度加载动画
     */
    public void startLoading(String msg){
        if(customLoadingDialog == null){
            customLoadingDialog = CustomLoading.CreateLoadingDialog(context);
            customLoadingDialog.setMessage(msg);
            customLoadingDialog.setCanceledOnTouchOutside(false);//不允许点击取消

            Window wd = customLoadingDialog.getWindow();
            WindowManager.LayoutParams lp = wd.getAttributes();
            lp.alpha = 0.5f;
            wd.setAttributes(lp);
        }
        customLoadingDialog.show();
    }
    /**
     * 关闭加载动画
     */
    public void stopLoading(){
        if(customLoadingDialog != null){
            customLoadingDialog.dismiss();
            customLoadingDialog.dismiss();
        }
    }
}
