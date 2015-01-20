package com.bruce.chatui;

import android.app.Activity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.view.ViewGroup;

import com.bruce.chatui.application.AppConstant;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by N1007 on 2015/1/20.
 * 表情外部viewpager的适配器-对应三个类型的page
 */
public class SmileyPagerAdapter extends FragmentStatePagerAdapter {


    private Activity mActivity;

    public SmileyPagerAdapter(FragmentManager fm,Activity mActivity) {
        super(fm);
        this.mActivity = mActivity;
    }

    @Override
    public SmileyItemFragment instantiateItem(ViewGroup container, int position) {
        return (SmileyItemFragment)super.instantiateItem(container, position);
    }

    /**
     * 改方法的重载是为了防止 notifyDataSetChanged() 不更新的问题
     * 特别是覆盖了 instantiateItem（）这个方法后必须重载
     * @param object
     * @return
     */
    @Override
    public int getItemPosition(Object object) {
        return PagerAdapter.POSITION_NONE;
    }

    @Override
    public SmileyItemFragment getItem(int position) {
        SmileyItemFragment fragment = null;
        List<Emoji> emojis = new ArrayList<>();
        if (position == 0){ //第一页加载默认emoji图片
             emojis.addAll(AppConstant.getApplition(mActivity).getDefaultEmoji());
        }else{ //第二页加载game emoji图片
            emojis.addAll(AppConstant.getApplition(mActivity).getGameEmoji());
        }

        //加载完所有的emoji表情后，需要在每页的最后一个地方加上点击就能删除的按钮，
        // 也就是第21的位置（posiont =20）
        if(emojis != null){
            int i = 20; //第一页第一个加入删除键的位置，第二页加载第二个删除键的位置= 20 + 21;
            while(i<=emojis.size()){
                Emoji emoji = new Emoji();
                emojis.add(i,emoji);
                i += 21;
            }
            fragment = new SmileyItemFragment(emojis);
        }
        return fragment;
    }


    /**
     * 只有两个fragment放不同的emoji，另外一个只是放Listview的View
     * @return
     */
    @Override
    public int getCount() {
        return 2;
    }
}
