package com.bruce.chatui.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bruce.chatui.R;
import com.bruce.chatui.utils.Logger;
import com.bruce.chatui.utils.album.ImageBucket;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;

import java.util.List;

/**
 * Created by N1007 on 2015/1/30.
 */
public class ImageBucketAdapter extends BaseAdapter {

    private Context mContext;
    private List<ImageBucket> mList;

    public ImageBucketAdapter(Context context, List<ImageBucket> list) {
        this.mContext = context;
        this.mList = list;
    }

    @Override
    public int getCount() {
        return mList.size();
    }

    @Override
    public Object getItem(int position) {
        return mList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder holder = ViewHolder.newInstance(mContext, convertView, parent, R.layout.list_view_album);
//        holder.setText(R.id.name,mList.get(position).bucketName);
//        holder.setText(R.id.count,mList.get(position).count);
        ( (TextView) holder.getView(R.id.count)).setText("(" + mList.get(position).count + ")");
        ( (TextView) holder.getView(R.id.name)).setText(mList.get(position).bucketName);
       // holder.setImageResource(,R.id.image);
        Logger.debug("getView-ImagePath:",mList.get(position).imageList.get(0).getImagePath());
        DisplayImageOptions displayOptions = new DisplayImageOptions.Builder()
                .showImageForEmptyUri(R.drawable.default_album_image)
                .imageScaleType(ImageScaleType.IN_SAMPLE_INT)
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .bitmapConfig(Bitmap.Config.RGB_565).build();
        ImageLoader.getInstance().displayImage("file://"+mList.get(position).imageList.get(0).getImagePath(),((ImageView) holder.getView(R.id.image)),
                displayOptions);

        return holder.getView();
    }

}
