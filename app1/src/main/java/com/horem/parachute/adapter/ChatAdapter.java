/*
 * Copyright (c) 2015, 张涛.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.horem.parachute.adapter;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.horem.parachute.R;
import com.horem.parachute.mine.ChatActivity;
import com.horem.parachute.util.MyTimeUtil;

import org.kymjs.chat.UrlUtils;
import org.kymjs.chat.bean.Message;
import org.kymjs.kjframe.KJBitmap;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * @author kymjs (http://www.kymjs.com/) on 6/8/15.
 */
public class ChatAdapter extends BaseAdapter {

    private final Context context;
    private List<Message> datas = null;
    private KJBitmap kjb;
    private ChatActivity.OnChatItemClickListener listener;
    private static final int MINE = 1;
    private static final int OTHER = 0;

    public ChatAdapter(Context cxt, List<Message> datas, ChatActivity.OnChatItemClickListener listener) {
        this.context = cxt;
       /* if (datas == null) {
            datas = new ArrayList<Message>(0);
        }*/
        this.datas = datas;
        kjb = new KJBitmap();
        this.listener = listener;
    }

    public void refresh(List<Message> datas) {
       /* if (datas == null) {
            datas = new ArrayList<>(0);
        }*/
        this.datas = datas;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return datas.size();
    }

    @Override
    public Object getItem(int position) {
        return datas.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        return datas.get(position).getIsSend() ? MINE : OTHER;
    }

    @Override
    public int getViewTypeCount() {
        return 2;
    }

    public static String getDataTime(String format,Date date) {
        SimpleDateFormat df = new SimpleDateFormat(format);
        return df.format(date);
    }
    @Override
    public View getView(final int position, View v, ViewGroup parent) {
        final ViewHolder holder;
        final Message data = datas.get(position);
        Log.d("消息列表adapter",new Gson().toJson(data));
        if (v == null) {
            holder = new ViewHolder();
            switch (getItemViewType(position)){
                case MINE:
                    v = View.inflate(context, R.layout.chat_item_list_right, null);
                    break;
                case OTHER:
                    v = View.inflate(context, R.layout.chat_item_list_left, null);
                    break;
            }

            holder.layout_content = (RelativeLayout) v.findViewById(R.id.chat_item_layout_content);//message content
            holder.img_avatar = (CircleImageView) v.findViewById(R.id.chat_item_avatar);//用户头像
            holder.img_chatimage = (ImageView) v.findViewById(R.id.chat_item_content_image);//聊天中的图片，默认隐藏
            holder.img_sendfail = (ImageView) v.findViewById(R.id.chat_item_fail);//发送失败图片，默认隐藏
            holder.progress = (ProgressBar) v.findViewById(R.id.chat_item_progress);//发送状态progress
            holder.tv_chatcontent = (TextView) v.findViewById(R.id.chat_item_content_text);//消息内容
            holder.tv_date = (TextView) v.findViewById(R.id.chat_item_date);//日期
            holder.user_name = (TextView) v.findViewById(R.id.chat_item_username);//用户名
            v.setTag(holder);

        } else {
            holder = (ViewHolder) v.getTag();
        }

        holder.user_name.setText(data.getFromUserName());
//        Glide.with(context).load("").into(holder.img_avatar);
        holder.tv_date.setText(MyTimeUtil.friendlyTime(getDataTime("yyyy-MM-dd HH:mm:ss",data.getTime())));
        holder.tv_date.setVisibility(View.GONE);
        if(position == 0){
            holder.tv_date.setVisibility(View.VISIBLE);
        }else {
            //TODO is same day ?
            Message pmsg = datas.get(position - 1);
            if (inSameDay(pmsg.getTime(), data.getTime())) {
                holder.tv_date.setVisibility(View.GONE);
            } else {
                holder.tv_date.setVisibility(View.VISIBLE);
            }
        }

        //如果是文本类型，则隐藏图片，如果是图片则隐藏文本
        if (data.getType() == Message.MSG_TYPE_TEXT) {
            holder.img_chatimage.setVisibility(View.GONE);
            holder.tv_chatcontent.setVisibility(View.VISIBLE);
            //如果文本中含有网址，高亮显示
            if (data.getContent().contains("href")) {
                holder.tv_chatcontent = UrlUtils.handleHtmlText(holder.tv_chatcontent, data
                        .getContent());
            } else {
                holder.tv_chatcontent = UrlUtils.handleText(holder.tv_chatcontent, data
                        .getContent());
            }
        } else {//图片，暂时未用到
            holder.tv_chatcontent.setVisibility(View.GONE);
            holder.img_chatimage.setVisibility(View.VISIBLE);

            //如果内存缓存中有要显示的图片，且要显示的图片不是holder复用的图片，则什么也不做，否则显示一张加载中的图片
            if (kjb.getMemoryCache(data.getContent()) != null && data.getContent() != null &&
                    data.getContent().equals(holder.img_chatimage.getTag())) {
            } else {
                holder.img_chatimage.setImageResource(R.drawable.default_head);
            }
            kjb.display(holder.img_chatimage, data.getContent(), 300, 300);
        }

        //如果是表情或图片，则不显示气泡，如果是图片则显示气泡
        if (data.getType() != Message.MSG_TYPE_TEXT) {
            holder.layout_content.setBackgroundResource(android.R.color.transparent);
        } else {
            if (data.getIsSend()) {
                holder.layout_content.setBackgroundResource(R.drawable.chat_to_bg_selector);
            } else {
                holder.layout_content.setBackgroundResource(R.drawable.chat_from_bg_selector);
            }
        }

        //显示头像
        if (data.getIsSend()) {
            Glide.with(context).load(data.getFromUserAvatar())
                    .into(holder.img_avatar);
        } else {
            Glide.with(context).load(data.getToUserAvatar())
                    .into(holder.img_avatar);
        }

        if (listener != null) {
            holder.tv_chatcontent.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onTextClick(position);
                }
            });
            holder.img_chatimage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    switch (data.getType()) {
                        case Message.MSG_TYPE_PHOTO:
                            listener.onPhotoClick(position);
                            break;
                        case Message.MSG_TYPE_FACE:
                            listener.onFaceClick(position);
                            break;
                    }
                }
            });
        }

        //消息发送的状态
        switch (data.getState()) {
            case Message.MSG_STATE_FAIL:
                holder.progress.setVisibility(View.GONE);
                holder.img_sendfail.setVisibility(View.VISIBLE);
                break;
            case Message.MSG_STATE_SUCCESS:
                holder.progress.setVisibility(View.GONE);
                holder.img_sendfail.setVisibility(View.GONE);
                break;
            case Message.MSG_STATE_SENDING:
                holder.progress.setVisibility(View.VISIBLE);
                holder.img_sendfail.setVisibility(View.GONE);
                break;
        }
        return v;
    }
    public static boolean inSameDay(Date date1, Date Date2) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date1);
        int year1 = calendar.get(Calendar.YEAR);
        int day1 = calendar.get(Calendar.DAY_OF_YEAR);

        calendar.setTime(Date2);
        int year2 = calendar.get(Calendar.YEAR);
        int day2 = calendar.get(Calendar.DAY_OF_YEAR);

        if ((year1 == year2) && (day1 == day2)) {
            return true;
        }
        return false;
    }

    static class ViewHolder {
        TextView tv_date;
        CircleImageView img_avatar;
        TextView tv_chatcontent;
        ImageView img_chatimage;
        ImageView img_sendfail;
        ProgressBar progress;
        RelativeLayout layout_content;
        TextView user_name;
    }
}
