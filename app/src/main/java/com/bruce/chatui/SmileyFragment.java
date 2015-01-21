package com.bruce.chatui;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ImageSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

import com.bruce.chatui.SmileyItemFragment.OnDeleteSmileyListener;
import com.bruce.chatui.SmileyItemFragment.OnSelectSmileyListener;
import com.bruce.chatui.adapter.GridItemAdapter;

import java.util.List;

/**
 * Created by N1007 on 2015/1/21.
 * 包含GridView用于显示emoji的fragment
 */
public class SmileyFragment extends Fragment {

    private List<Emoji> emojis; //每页emoji集合
    private OnDeleteSmileyListener mDeleteListener;
    private OnSelectSmileyListener mSelectListener;

    public void setmDeleteListener(OnDeleteSmileyListener mDeleteListener) {
        this.mDeleteListener = mDeleteListener;
    }

    public void setSelectListener(OnSelectSmileyListener selectListener) {
        this.mSelectListener = selectListener;
    }

    public SmileyFragment(List<Emoji> emojis) {
        this.emojis = emojis;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, final Bundle savedInstanceState) {
        GridView view = new GridView(getActivity());
        GridItemAdapter adapter = new GridItemAdapter(emojis, SmileyFragment.this, view);
        view.setAdapter(adapter);
        view.setNumColumns(7); //每行7个
        //item点击回调
        view.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if ((position + 1) % 21 == 0) { //点击是删除表情
                    if (mDeleteListener != null)
                        mDeleteListener.onDelete();
                } else {//点击普通表情
                    //获取到点击的表情对象
                    Emoji emoji = (Emoji) parent.getAdapter().getItem(position);
                    if(emoji.tag == null) //一般tag不是Null 表示表情的标签
                        return;
                    //通过SpannableString 和 ImageSpan来显示表情
                    SpannableString spannableString = new SpannableString(emoji.tag);
                    Drawable drawable = getActivity().getResources().getDrawable(emoji.resourceid);
                    drawable.setBounds(0,0,(int)getActivity().getResources().getDimension(R.dimen.channel_chatsmily_height),
                            (int)getActivity().getResources().getDimension(R.dimen.channel_chatsmily_height));
                    ImageSpan span = new ImageSpan(drawable, ImageSpan.ALIGN_BOTTOM);
                    spannableString.setSpan(span,0,emoji.tag.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    if(mSelectListener != null){
                        mSelectListener.onSelected(spannableString);
                    }
                }
            }
        });
        return view;
    }
}
