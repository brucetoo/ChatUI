package com.bruce.chatui.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bruce.chatui.MessageInfo;
import com.bruce.chatui.R;
import com.bruce.chatui.thirdparty.BubbleImageHelper;
import com.bruce.chatui.thirdparty.CircleImageView;
import com.bruce.chatui.thirdparty.RichTextView;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

import java.util.ArrayList;

/**
 * Created by N1007 on 2015/2/4.
 */
public class MessageAdapter extends BaseAdapter {

    public static final int MESSAGE_TYPE_SEND_TEXT = 0;
    public static final int MESSAGE_TYPE_SEND_IMAGE = 1;
    public static final int MESSAGE_TYPE_RECEIVE_TEXT = 2;
    public static final int MESSAGE_TYPE_RECEIVE_IMAGE = 3;
    public static final int MESSAGE_TYPE_RECEIVE_AUDIO = 5;
    public static final int MESSAGE_TYPE_SEND_AUDIO = 4;

    public static final int VIEW_TYPE_COUNT = 4;

    private ArrayList<MessageInfo> messageList ;
    private Context context;

    public MessageAdapter(Context context) {
        this.context = context;
        messageList = new ArrayList<MessageInfo>();
    }

    public void addMessage(MessageInfo o, final ListView mListView) {
        messageList.add(o);
        notifyDataSetChanged();
     //  mListView.smoothScrollToPosition(0);
        mListView.post(new Runnable() {
            @Override
            public void run() {
                mListView.setSelection(getCount() - 1);
            }
        });
    }

    public void refreshList(ArrayList<MessageInfo> list) {
        messageList = list;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return messageList.size();
    }

    @Override
    public MessageInfo getItem(int position) {
        return messageList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        return messageList.get(position).msgType;
    }

    @Override
    public int getViewTypeCount() {
        return VIEW_TYPE_COUNT;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        int msgType = getItemViewType(position);
        MessageBaseHolder holder = null;
        MessageInfo info = messageList.get(position);
        if (convertView == null) {
            switch (msgType) {
                case MESSAGE_TYPE_SEND_TEXT:
                    convertView = View.inflate(context, R.layout.list_item_send_text, null);
                    holder = new TextMessageHolder(convertView);
                    break;
                case MESSAGE_TYPE_SEND_IMAGE:
                    convertView = View.inflate(context, R.layout.list_item_send_image, null);
                    holder = new ImageMessageHolder(convertView);
                    break;
                case MESSAGE_TYPE_RECEIVE_TEXT:
                    convertView = View.inflate(context, R.layout.list_item_receive_text, null);
                    holder = new TextMessageHolder(convertView);
                    break;
                case MESSAGE_TYPE_RECEIVE_IMAGE:
                    convertView = View.inflate(context, R.layout.list_item_receive_image, null);
                    holder = new ImageMessageHolder(convertView);
                    break;
            }
            convertView.setTag(holder);
        } else {
            holder = (MessageBaseHolder) convertView.getTag();
        }
        holder.handleData(info);
        return convertView;
    }

    class MessageBaseHolder {
        CircleImageView user_icon;
        ImageView msg_tip; //发送成功与否？
        TextView time_top;

        public MessageBaseHolder(View convertView) {
            user_icon = (CircleImageView) convertView.findViewById(R.id.user_icon);
            msg_tip = (ImageView) convertView.findViewById(R.id.msg_tip);
            time_top = (TextView) convertView.findViewById(R.id.msg_text);
        }

        public void handleData(MessageInfo info) {
            //处理头像~~~
           time_top.setText(info.time);
        }
    }

    class TextMessageHolder extends MessageBaseHolder {

        RichTextView text_content;
        ProgressBar msg_progress; //文本进度

        public TextMessageHolder(View convertView) {
            super(convertView);
            text_content = (RichTextView) convertView.findViewById(R.id.rich_text);
        }

        @Override
        public void handleData(MessageInfo info) {
            super.handleData(info);
            text_content.setChatText(info.contentText);
        }
    }

    class ImageMessageHolder extends MessageBaseHolder {
        ProgressBar image_loading;//图片进度
        ImageView msg_image;

        public ImageMessageHolder(View convertView) {
            super(convertView);
            msg_image = (ImageView) convertView.findViewById(R.id.msg_image);
            image_loading = (ProgressBar) convertView.findViewById(R.id.image_loading);
        }

        @Override
        public void handleData(final MessageInfo info) {
            super.handleData(info);
            DisplayImageOptions displayOptions = new DisplayImageOptions.Builder()
                    .showImageForEmptyUri(R.drawable.default_album_image)
                    .imageScaleType(ImageScaleType.IN_SAMPLE_INT)
                    .cacheInMemory(true)
                    .cacheOnDisk(true)
                    .bitmapConfig(Bitmap.Config.RGB_565).build();
         //   ImageLoader.getInstance().displayImage(info.imagePath, msg_image, displayOptions);
            ImageLoader.getInstance().loadImage(info.imagePath,new ImageLoadingListener() {
                @Override
                public void onLoadingStarted(String s, View view) {
                     image_loading.setVisibility(View.VISIBLE);
                }

                @Override
                public void onLoadingFailed(String s, View view, FailReason failReason) {

                }

                @Override
                public void onLoadingComplete(String s, View view, Bitmap bitmap) {
                    Bitmap bmp = null;
                    image_loading.setVisibility(View.GONE);
                    if(info.isSend) {
                        bmp = BubbleImageHelper.getInstance(context).getBubbleImageBitmap(bitmap, R.drawable.send_image_default_bk);
                    }else {
                        bmp = BubbleImageHelper.getInstance(context).getBubbleImageBitmap(bitmap, R.drawable.receive_image_default_bk);
                    }
                        msg_image.setImageBitmap(bmp);
                    }

                @Override
                public void onLoadingCancelled(String s, View view) {

                }
            });
        }
    }

}
