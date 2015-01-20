package com.bruce.chatui;

import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import roboguice.activity.RoboActivity;
import roboguice.inject.ContentView;
import roboguice.inject.InjectView;

/**
 * Created by N1007 on 2015/1/20.
 */
@ContentView(R.layout.main_activity)
public class MainActivity extends RoboActivity {
    /**
     * 主聊天面板
     */
    @InjectView(R.id.layout_swipe)
    SwipeRefreshLayout mSwipeLayout;
    @InjectView(R.id.list_view)
    ListView mListView;
    @InjectView(R.id.chat_record)
    ImageView mChat;
    @InjectView(R.id.chat_smile)
    ImageView mSmile;
    @InjectView(R.id.chat_item)
    ImageView mItem;
    @InjectView(R.id.edit_text)
    ClipEditText mEditText;
    @InjectView(R.id.layout_bottom)
    LinearLayout mBottomLayout;

    /**
     * 表情面板
     */
    private View mSmiley; //表情面板
    private ViewPager mViewPager;
    private ListView mSmileyListView;
    private TextView mDefalutSmiley;
    private TextView mGameSmiley;
    private TextView mPharseSmiley;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initSmileyPanel();
    }

    /**
     * 初始化表情面板
     */
    private void initSmileyPanel() {
        mSmiley = View.inflate(this, R.layout.view_smiley, null);
        mViewPager = (ViewPager) mSmiley.findViewById(R.id.view_pager_smiley);
        mSmileyListView = (ListView) mSmiley.findViewById(R.id.list_view_smiley);
        mDefalutSmiley = (TextView) mSmiley.findViewById(R.id.smiley_default);
        mGameSmiley = (TextView) mSmiley.findViewById(R.id.smiley_game);
        mPharseSmiley = (TextView) mSmiley.findViewById(R.id.smiley_phrase);

    }
}




























