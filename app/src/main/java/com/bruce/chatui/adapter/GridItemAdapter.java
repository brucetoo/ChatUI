package com.bruce.chatui.adapter;

import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import com.bruce.chatui.Emoji;
import com.bruce.chatui.R;
import com.bruce.chatui.SmileyFragment;

import java.util.List;

/**
 * Created by N1007 on 2015/1/21.
 * 表情最终的选中区域的adapter
 */
public class GridItemAdapter extends BaseAdapter {
    private List<Emoji> emojis;
    private SmileyFragment fragment;
    private GridView view;

    public GridItemAdapter(List<Emoji> emojis, SmileyFragment smileyFragment, GridView view) {
        this.emojis = emojis;
        this.fragment = smileyFragment;
        this.view = view;
    }


    @Override
    public int getCount() {
        return 21;
    }

    @Override
    public Object getItem(int position) {
        return emojis.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ImageView imageView;
        if (convertView == null) {
            imageView = new ImageView(fragment.getActivity());
            imageView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
        } else {
            imageView = (ImageView) convertView;
        }

        //将gridView平分
        AbsListView.LayoutParams param = new AbsListView.LayoutParams(
                view.getWidth() / 7, view.getHeight() / 3);
        imageView.setLayoutParams(param);
        imageView.setScaleX(0.7f);
        imageView.setScaleY(0.7f);
        if (position < emojis.size()) {
            imageView.setVisibility(View.VISIBLE);
            Emoji emoji = emojis.get(position);
            imageView.setImageResource(emoji.resourceid);
            imageView.setTag(emoji.resourceid);
        } else {
            imageView.setVisibility(View.INVISIBLE);
        }

        //删除键
        if (position == getCount() - 1) {
            imageView.setVisibility(View.VISIBLE);
            imageView.setImageResource(R.drawable.ic_label_delete);
//           imageView.setTag(R.drawable.ic_label_delete);
        }

        return imageView;
    }
}
