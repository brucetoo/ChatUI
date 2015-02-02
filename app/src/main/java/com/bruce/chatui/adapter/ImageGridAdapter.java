package com.bruce.chatui.adapter;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Handler;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.bruce.chatui.R;
import com.bruce.chatui.utils.Const;
import com.bruce.chatui.utils.Logger;
import com.bruce.chatui.utils.album.ImageItem;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * Created by N1007 on 2015/2/2.
 */
public class ImageGridAdapter extends BaseAdapter {

    private Activity mContext;
    private List<ImageItem> mList;
    private Handler mHander;
    private int mSelectedCount;
    private TextCallBack textCallBack = null;//选择数木的回调
    private Map<Integer, ImageItem> selectedMap = new TreeMap<Integer, ImageItem>();

    public ImageGridAdapter(Activity context, List<ImageItem> list, Handler mHander) {
        this.mContext = context;
        this.mList = list;
        this.mHander = mHander;
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
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder holder = ViewHolder.newInstance(mContext, convertView, parent, R.layout.grid_view_item);
        final ImageView isSelected = (ImageView) holder.getView(R.id.isSelected);
        final ImageView image = (ImageView) holder.getView(R.id.image);
        final ImageItem item = mList.get(position);
        if (item.isSelected()) {
            isSelected.setImageResource(R.drawable.album_img_selected);
        } else {
            isSelected.setImageResource(R.drawable.album_img_select_nor);
        }
        Logger.info("getView-imagePath", item.getImagePath());

        DisplayImageOptions displayOptions = new DisplayImageOptions.Builder()
                .showImageForEmptyUri(R.drawable.default_album_image)
                .imageScaleType(ImageScaleType.IN_SAMPLE_INT)
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .bitmapConfig(Bitmap.Config.RGB_565).build();
        ImageLoader.getInstance().displayImage("file://" + item.getImagePath(),image,
                displayOptions);

        image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mSelectedCount < Const.MAX_SELECT_IMAGE_COUNT) {
                    item.setSelected(!item.isSelected());
                    if (item.isSelected()) {
                        mSelectedCount++;
                        selectedMap.put(position,item);
                    }else{
                        mSelectedCount--;
                        selectedMap.remove(position);
                    }
                    if(textCallBack != null){
                        textCallBack.onListen(mSelectedCount);
                    }
                } else { //超过最大选择
                    if(item.isSelected()){
                        item.setSelected(false);
                        mSelectedCount--;
                        selectedMap.remove(position);
                    }else {
                        mHander.sendEmptyMessage(0);
                    }
                }
                notifyDataSetChanged();
            }
        });

        return holder.getView();
    }

    public Map<Integer, ImageItem> getSelectedMap() {
        return selectedMap;
    }

    public void setTextCallBack(TextCallBack back) {
        textCallBack = back;
    }

    public static interface TextCallBack {
        void onListen(int count);
    }

}
