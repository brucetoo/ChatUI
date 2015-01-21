package com.bruce.chatui.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.bruce.chatui.R;

/**
 * Created by N1007 on 2015/1/21.
 */
public class PhraseAdapter extends BaseAdapter {

    private Context mCtx;
    public String[] phrases = {
             "single prhase 1",
             "single prhase 2",
             "single prhase 3",
             "single prhase 4",
             "single prhase 5",
             "single prhase 6",
             "single prhase 7",
             "single prhase 8",
             "single prhase 9",
             "single prhase 10",
             "single prhase 11",
             "single prhase 12",
    };


    public PhraseAdapter(Context context) {
        this.mCtx = context;
    }

    @Override
    public int getCount() {
        return phrases.length;
    }

    @Override
    public Object getItem(int position) {
        return phrases[position];
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = ViewHolder.newInstance(mCtx,convertView,parent, R.layout.list_view_chat);
        viewHolder.setText(R.id.text_phrase,phrases[position]);
        return viewHolder.getView();
    }
}
