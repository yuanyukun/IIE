package com.horem.parachute.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.amap.api.maps.model.LatLng;
import com.amap.api.services.core.PoiItem;
import com.horem.parachute.R;

import java.util.List;

/**
 * Created by yuanyukun on 2016/5/23.
 */
public class SimpleRecylerAdapter extends RecyclerView.Adapter<SimpleRecylerAdapter.ViewHolder> implements View.OnClickListener{

    private Context context;
    private List<PoiItem> mLists;
    private int mPosition;
    private OnItemClicked itemClicked;

    @Override
    public void onClick(View v) {
        itemClicked.subItemClicked(v,new LatLng(mLists.get(mPosition).getLatLonPoint().getLatitude(),
                mLists.get(mPosition).getLatLonPoint().getLongitude()));
    }

    public interface OnItemClicked{
        void subItemClicked(View view, LatLng date);
    }

    public void setOnItemClicked(OnItemClicked itemClicked){
        this.itemClicked = itemClicked;
    }


    public SimpleRecylerAdapter(Context context,List<PoiItem> mLists) {
        super();
        this.context = context;
        this.mLists = mLists;
    }

    @Override
    public void onBindViewHolder(SimpleRecylerAdapter.ViewHolder holder, int position) {
        this.mPosition = position;
        holder.PoiItemDescribe.setText(mLists.get(position).getTitle());
        holder.PoiItemDetails.setText(mLists.get(position).getCityName()+" | "+mLists.get(position).getSnippet());
    }

    public void setData(List<PoiItem> lists){
        this.mLists = lists;
    }
    @Override
    public int getItemCount() {
        return mLists.size();
    }

    @Override
    public SimpleRecylerAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View layout = LayoutInflater.from(parent.getContext()).inflate(R.layout.poikey_search_item_view, parent, false);
        layout.setOnClickListener(this);
        return new ViewHolder(layout);
    }
    public static class ViewHolder extends RecyclerView.ViewHolder {

        private TextView PoiItemDescribe;
        private TextView PoiItemDetails;

        public ViewHolder(View itemView) {
            super(itemView);
            PoiItemDescribe = (TextView) itemView.findViewById(R.id.poi_key_search_item_txt_title);
            PoiItemDetails = (TextView) itemView.findViewById(R.id.poi_key_search_item_txt_details);
        }
    }
}
