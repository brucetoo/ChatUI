package com.bruce.chatui.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.bruce.chatui.R;
import com.bruce.chatui.utils.album.ImageBucket;
import com.squareup.picasso.Picasso;

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
        holder.setText(R.id.name,mList.get(position).bucketName);
        holder.setText(R.id.count,mList.get(position).count);
       // holder.setImageResource(,R.id.image);
        //mList.get(position).imageList.get(0).getImageId()
        Picasso.with(mContext).load(mList.get(position).imageList.get(0).getThumbnailPath())
                .placeholder(R.drawable.default_album_image)
                .centerInside()
                .into((ImageView)holder.getView(R.id.image));
        return holder.getView();
    }

}
