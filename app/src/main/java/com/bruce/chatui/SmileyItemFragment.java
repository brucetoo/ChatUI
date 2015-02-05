package com.bruce.chatui;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.text.SpannableString;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bruce.chatui.adapter.SmileyItemPageAdapter;
import com.bruce.chatui.thirdparty.CirclePageIndicator;

import java.util.List;

/**
 * Created by N1007 on 2015/1/20.
 * 不同类型Emoji 的fragment
 * 涉及到了 viewpager中嵌套fragment，然后再在fragment中嵌套viewpager的问题
 */
public class SmileyItemFragment extends Fragment {

    private static int PAGE_ID = 1;
    public static int SMILEYPAGE_SIZE = 21; //表情每页的个数
    private ViewPager mViewPager;
    private CirclePageIndicator mIndicator;
    private List<Emoji> emojies; //该fragment中emoji的集合
    private SmileyItemPageAdapter mAdapter;

    /**
     * 每个emoji表情的点击事件回调都会传递很多层到达最初的处理界面
     */
    private OnSelectSmileyListener mSelectListener;
    private OnDeleteSmileyListener mDeleteListener;
    public SmileyItemFragment(){}

    public void setmSelectListener(OnSelectSmileyListener mSelectListener) {
        this.mSelectListener = mSelectListener;
    }

    public void setmDeleteListener(OnDeleteSmileyListener mDeleteListener) {
        this.mDeleteListener = mDeleteListener;
    }

    public SmileyItemFragment(List<Emoji> _emojies) {
        this.emojies = _emojies;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view =inflater.inflate(R.layout.fragment_smiley_item,null);
        mViewPager = (ViewPager) view.findViewById(R.id.item_viewpager);
        mIndicator = (CirclePageIndicator) view.findViewById(R.id.page_indicator);

        //根据emoji的数量算出viewpager的页数 pageNum
        int smileySize = emojies.size();
        int pageNum = smileySize % SMILEYPAGE_SIZE == 0 ?
                smileySize/SMILEYPAGE_SIZE
                :smileySize/SMILEYPAGE_SIZE + 1;

        mAdapter = new SmileyItemPageAdapter(getActivity().getSupportFragmentManager(),emojies,pageNum);
        mAdapter.setmDeleteListener(mDeleteListener);
        mAdapter.setmSelectListener(mSelectListener);
        mViewPager.setAdapter(mAdapter);
        /**
         * 在不同地方用到同一套UI中的viewPager，有时候会导致第二次在使用的时候ViewPager数据加载不出来。
         * 这样就必须对ViewPager设置不同的 ID（可以先注释下方代码对不效果）
         */
        mViewPager.setId(PAGE_ID);
        PAGE_ID++;
        mIndicator.setViewPager(mViewPager);
        return view;
    }


    public void setItemFirst(){
        mViewPager.setCurrentItem(0,false);
    }

    public void setItemLast(){
        mViewPager.setCurrentItem(mAdapter.getCount()-1,false);
    }


    /**
     * 选中单个emoji表情的接口
     */
    public interface  OnSelectSmileyListener{
        void onSelected(SpannableString spannableString);
    }

    /**
     * 删除某个emoji表情的接口
     */
    public interface  OnDeleteSmileyListener{
        void onDelete();
    }

}
