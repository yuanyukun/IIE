package com.horem.parachute.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

/**
 * Created by user on 2016/3/18.
 */
public abstract class CommonAdapter<T extends RecyclerView.ViewHolder> extends RecyclerView.Adapter<T> {

    protected final Context mContext;
    protected List lists;

    private OnRecyclerViewItemClickListener mOnItemClickListener = null;

    public void setOnItemClickListener(OnRecyclerViewItemClickListener listener) {
        mOnItemClickListener = listener;
    }

    public CommonAdapter(Context context,  List lists) {
        this.mContext = context;
        this.lists = lists;
    }

    public CommonAdapter(Context mContext) {
        this.mContext = mContext;
    }


@Override
    public abstract T onCreateViewHolder(ViewGroup parent, int viewType);

    @Override
    public void onBindViewHolder(T holder, int position) {
        holder.itemView.setOnClickListener(new OnClickListener(position, mOnItemClickListener));
    }

    @Override
    public int getItemCount() {
        return lists.size();
    }

    public static class OnClickListener implements View.OnClickListener{
        final int mPosition;
        final OnRecyclerViewItemClickListener mListener;

        public OnClickListener(int i, OnRecyclerViewItemClickListener listener) {
            mPosition = i;
            mListener = listener;
        }

        @Override
        public void onClick(View v) {
            if (mListener != null) {
                mListener.onItemClick(v, mPosition);
            }
        }
    }

    // 用来设置每个item的接听
    public static interface OnRecyclerViewItemClickListener {
        void onItemClick(View view, int position);
    }


}
