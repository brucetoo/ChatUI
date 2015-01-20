package com.bruce.chatui;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bruce.chatui.thirdView.CirclePageIndicator;

import java.util.List;

/**
 * Created by N1007 on 2015/1/20.
 * 不同类型Emoji 的fragment
 * 涉及到了 viewpager中嵌套fragment，然后再在fragment中嵌套viewpager的问题
 */
public class SmileyItemFragment extends Fragment {

    private static int SMILEYPAGE_SIZE = 21; //表情每页的个数
    private ViewPager mViewPager;
    private CirclePageIndicator mIndicator;
    private List<Emoji> emojies; //该fragment中emoji的集合

    public SmileyItemFragment(){}

    public SmileyItemFragment(List<Emoji> _emojies) {
        this.emojies = _emojies;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view =inflater.inflate(R.layout.fragment_smiley_item,null);
        mViewPager = (ViewPager) view.findViewById(R.id.item_viewpager);
        mIndicator = (CirclePageIndicator) view.findViewById(R.id.page_indicator);

        //根据emoji的数量算出viewpager的页数
        int smileySize = emojies.size();
        int pageNum = smileySize % SMILEYPAGE_SIZE == 0 ?
                smileySize/SMILEYPAGE_SIZE
                :smileySize/SMILEYPAGE_SIZE + 1;


        return view;
    }

}
