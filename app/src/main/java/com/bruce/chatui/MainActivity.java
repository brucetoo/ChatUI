package com.bruce.chatui;

import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.SpannableString;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.bruce.chatui.adapter.PhraseAdapter;
import com.bruce.chatui.adapter.SmileyPagerAdapter;

import roboguice.activity.RoboFragmentActivity;
import roboguice.inject.ContentView;
import roboguice.inject.InjectView;

/**
 * Created by N1007 on 2015/1/20.
 */
@ContentView(R.layout.main_activity)
public class MainActivity extends RoboFragmentActivity {
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
    private View mCamera; //相机面板
    private ViewPager mViewPager;
    private ListView mSmileyListView;
    private TextView mDefalutSmiley;
    private TextView mGameSmiley;
    private TextView mPharseSmiley;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initSmileyPanel();
        initCameraPanel();

        initMainPanel();
    }

    /**
     * 主界面的操作设置
     */
    private void initMainPanel() {

    }

    /**
     * 相机面板
     */
    private void initCameraPanel() {
        mCamera = View.inflate(this,R.layout.view_camera,null);
        ImageView btnPhoto = (ImageView) mCamera.findViewById(R.id.btn_phone);
        ImageView btnCamera = (ImageView) mCamera.findViewById(R.id.btn_camera);

        btnPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        btnCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
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

        SmileyPagerAdapter adapter = new SmileyPagerAdapter(getSupportFragmentManager(),this);
        adapter.setmSelectListener(new SmileyItemFragment.OnSelectSmileyListener() {
            @Override
            public void onSelected(SpannableString spannableString) {
                //在edittext框中添加选中的表情
                mEditText.getText().insert(mEditText.getSelectionStart(),spannableString);
            }
        });
        adapter.setmDeleteListener(new SmileyItemFragment.OnDeleteSmileyListener() {
            @Override
            public void onDelete() {
                //调用系统的删除键
                mEditText.dispatchKeyEvent(new KeyEvent(KeyEvent.ACTION_MULTIPLE,KeyEvent.KEYCODE_DEL));
            }
        });
        mViewPager.setAdapter(adapter);
        mViewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                /**因为只有两页用了viewpager
                 * 如果现在是第一页，保持第二页中数据的在其数据的第一页
                 * 如果是第二页，保持第一页数据最后一页
                 */
                if(position == 0){
                    ((SmileyPagerAdapter)mViewPager.getAdapter()).instantiateItem(mViewPager,1).setItemFirst();
                }else if(position == 1){
                    ((SmileyPagerAdapter)mViewPager.getAdapter()).instantiateItem(mViewPager,0).setItemLast();
                }

                mDefalutSmiley.setSelected(false);
                mGameSmiley.setSelected(false);
                mPharseSmiley.setSelected(false);
                switch (position){
                    case 0:
                        mDefalutSmiley.setSelected(true);
                        break;
                    case 1:
                        mGameSmiley.setSelected(true);
                        break;
                }
            }

            @Override
            public void onPageSelected(int position) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        final PhraseAdapter phraseAdapter = new PhraseAdapter(this);
        mSmileyListView.setAdapter(phraseAdapter);
        mSmileyListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mEditText.getText().insert(mEditText.getSelectionStart(),phraseAdapter.phrases[position]);
            }
        });

        //最初设置第一个面板选中
        mDefalutSmiley.setSelected(true);
        //默认表情面板
        mDefalutSmiley.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //listview隐藏掉
                mSmileyListView.setVisibility(View.GONE);
                mViewPager.setVisibility(View.VISIBLE);
                //第一个pager中第一个fragment选中
                mViewPager.setCurrentItem(0);
                ((SmileyPagerAdapter)mViewPager.getAdapter()).instantiateItem(mViewPager,0).setItemFirst();
            }
        });
        //游戏表情面板
        mGameSmiley.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //listview隐藏掉
                mSmileyListView.setVisibility(View.GONE);
                mViewPager.setVisibility(View.VISIBLE);
                //第二个pager中第一个fragment选中
                mViewPager.setCurrentItem(1);
                ((SmileyPagerAdapter)mViewPager.getAdapter()).instantiateItem(mViewPager,1).setItemFirst();
            }
        });

        //短语面板
        mPharseSmiley.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDefalutSmiley.setSelected(false);
                mGameSmiley.setSelected(false);
                mPharseSmiley.setSelected(true);
                mSmileyListView.setVisibility(View.VISIBLE);
                mViewPager.setVisibility(View.GONE);
            }
        });
    }
}




























