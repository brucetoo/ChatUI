package com.bruce.chatui.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.AnimationDrawable;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bruce.chatui.MessageInfo;
import com.bruce.chatui.R;
import com.bruce.chatui.thirdparty.BubbleImageHelper;
import com.bruce.chatui.thirdparty.CircleImageView;
import com.bruce.chatui.thirdparty.RichTextView;
import com.mogujie.tt.support.audio.AudioPlayerHandler;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by N1007 on 2015/2/4.
 */
public class MessageAdapter extends BaseAdapter {

    public static final int MESSAGE_TYPE_SEND_TEXT = 0;
    public static final int MESSAGE_TYPE_SEND_IMAGE = 1;
    public static final int MESSAGE_TYPE_RECEIVE_TEXT = 2;
    public static final int MESSAGE_TYPE_RECEIVE_IMAGE = 3;
    public static final int MESSAGE_TYPE_SEND_AUDIO = 4;
    public static final int MESSAGE_TYPE_RECEIVE_AUDIO = 5;

    public static final int VIEW_TYPE_COUNT = 6;

    private ArrayList<MessageInfo> messageList;
    private Context context;
    private boolean isAudioPlaying = false; //是否在播放
    private Map<String,AnimationDrawable> anims= new HashMap<String,AnimationDrawable>(); //所有正在播放的动画

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
                case MESSAGE_TYPE_SEND_AUDIO:
                    convertView = View.inflate(context, R.layout.list_item_send_audio, null);
                    holder = new AudioMessageHolder(convertView);
                    break;
                case MESSAGE_TYPE_RECEIVE_AUDIO:
                    convertView = View.inflate(context,R.layout.list_item_receive_audio,null);
                    holder = new AudioMessageHolder(convertView);
                    break;
            }
            convertView.setTag(holder);
        } else {
            holder = (MessageBaseHolder) convertView.getTag();
        }
        holder.handleData(info);
        return convertView;
    }

    /**
     * 基类holder 公共的部分: icon,时间,发送状态
     */
    class MessageBaseHolder {
        CircleImageView user_icon; //icon
        ImageView msg_tip; //发送成功与否？
        TextView time_top; //发送时间

        public MessageBaseHolder(View convertView) {
            user_icon = (CircleImageView) convertView.findViewById(R.id.user_icon);
            msg_tip = (ImageView) convertView.findViewById(R.id.msg_tip);
            time_top = (TextView) convertView.findViewById(R.id.msg_text);
        }

        public void handleData(MessageInfo info) {
            //处理头像显示
            //顶部时间是否显示....
            time_top.setText(info.time);
        }
    }

    /**
     * 语音状态的viewHolder
     */
    class AudioMessageHolder extends MessageBaseHolder {

        TextView audio_length;
        LinearLayout playLayout;
        View anim;

        public AudioMessageHolder(View convertView) {
            super(convertView);
            audio_length = (TextView) convertView.findViewById(R.id.audio_length);
            playLayout = (LinearLayout) convertView.findViewById(R.id.send_audio);
            anim = convertView.findViewById(R.id.anim);
        }

        @Override
        public void handleData(final MessageInfo info) {
            super.handleData(info);
            Log.i("AudioMessageHolder:", info.audioPath);
            audio_length.setText(info.audioLen + "''");
            final AnimationDrawable animationDrawable = (AnimationDrawable) anim.getBackground();
            playLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (TextUtils.isEmpty(info.audioPath)) {
                        Toast.makeText(context, "语音文件跪了~~~", Toast.LENGTH_SHORT).show();
                    } else {
                        if(AudioPlayerHandler.getInstance().isPlaying()){ //如果正在播放就停止
                            AudioPlayerHandler.getInstance().stopPlayer();
                            animationDrawable.stop();
                        }
                        AudioPlayerHandler.getInstance().startPlay(info.audioPath);
                        animationDrawable.start();
                        anims.put(info.audioPath,animationDrawable);
                    }

                }
            });
        }
    }

    /**
     * 停止对应路径中正在播放的动画
     * @param path
     */
    public void stopAnimaiton(String path){
        if(path != null){
            if(anims.containsKey(path)){
                AnimationDrawable drawable = anims.get(path);
                if(drawable.isRunning() && drawable!=null){
                    drawable.stop();
                    drawable.selectDrawable(0);
                    anims.remove(path);
                }
            }
        }
    }

    /**
     * 文本状态的viewHolder
     */
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

    /**
     * 图片状态的viewHolder
     */
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
            ImageLoader.getInstance().loadImage(info.imagePath, new ImageLoadingListener() {
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
                    if (info.isSend) {
                        bmp = BubbleImageHelper.getInstance(context).getBubbleImageBitmap(bitmap, R.drawable.send_image_default_bk);
                    } else {
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
