package com.bruce.chatui.adapter;

import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;

import com.bruce.chatui.Emoji;
import com.bruce.chatui.SmileyItemFragment;
import com.bruce.chatui.SmileyItemFragment.OnDeleteSmileyListener;
import com.bruce.chatui.SmileyItemFragment.OnSelectSmileyListener;
import com.bruce.chatui.SmileyFragment;

import java.util.List;

/**
 * Created by N1007 on 2015/1/21.
 * 每一页fragment中对应的viewpager的adapter
 * viewPager的每页都是fragment，每个fragment用GridView显示出emoji
 */
public class SmileyItemPageAdapter extends FragmentStatePagerAdapter {

    private List<Emoji> emojies;
    private int mPageNum;
    private OnSelectSmileyListener mSelectListener;
    private OnDeleteSmileyListener mDeleteListener;

    /**
     * @param fm
     * @param emojies  emoji的集合
     * @param pageNum  viewPager的页数
     */
    public SmileyItemPageAdapter(FragmentManager fm, List<Emoji> emojies, int pageNum) {
        super(fm);
        this.emojies = emojies;
        this.mPageNum = pageNum;
    }

    @Override
    public SmileyFragment getItem(int position) {
        SmileyFragment fragment = null;
       //分别把emoji的集合填补到各个fragment
        if(position == mPageNum-1){ //最后一页
            fragment = new SmileyFragment(emojies.subList(position* SmileyItemFragment.SMILEYPAGE_SIZE,emojies.size()));
        }else{ //非最后一页
            fragment = new SmileyFragment(emojies.subList(position*SmileyItemFragment.SMILEYPAGE_SIZE,(position+1)*SmileyItemFragment.SMILEYPAGE_SIZE));
        }
        fragment.setmDeleteListener(mDeleteListener);
        fragment.setSelectListener(mSelectListener);
        return fragment;
    }

    @Override
    public int getCount() {
        return mPageNum;
    }

    @Override
    public int getItemPosition(Object object) {
        return PagerAdapter.POSITION_NONE;
    }


    public void setmSelectListener(OnSelectSmileyListener mSelectListener) {
        this.mSelectListener = mSelectListener;
    }

    public void setmDeleteListener(OnDeleteSmileyListener mDeleteListener) {
        this.mDeleteListener = mDeleteListener;
    }
}
