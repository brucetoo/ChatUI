package com.bruce.chatui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.Editable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.bruce.chatui.adapter.PhraseAdapter;
import com.bruce.chatui.adapter.SmileyPagerAdapter;
import com.bruce.chatui.utils.Logger;

/**
 * Created by N1007 on 2015/1/20.
 */
public class MainActivity extends FragmentActivity implements SwipeRefreshLayout.OnRefreshListener {
    /**
     * 主聊天面板
     */
    private SwipeRefreshLayout mSwipeLayout;
    private ListView mListView;
    private ImageView mRecord;
    private ImageView mSmile;
    private ImageView mSend;
    private ClipEditText mEditText;
    private LinearLayout mBottomLayout;

    /**
     * 表情面板
     */
    private View mSmiley; //表情面板
    private View mCamera; //相机面板
    private View mVoice; //录音面板
    private ViewPager mViewPager;
    private ListView mSmileyListView;
    private TextView mDefalutSmiley;
    private TextView mGameSmiley;
    private TextView mPharseSmiley;
    private boolean isSend;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);
        initView();
        initSmileyPanel();
        initCameraPanel();
        initRecordPanel();
        initMainPanel();
    }


    private void initView() {
        mSwipeLayout = (SwipeRefreshLayout) findViewById(R.id.layout_swipe);
        mListView = (ListView) findViewById(R.id.list_view);
        mRecord = (ImageView) findViewById(R.id.chat_record);
        mSmile = (ImageView) findViewById(R.id.chat_smile);
        mSend = (ImageView) findViewById(R.id.chat_item);
        mEditText = (ClipEditText) findViewById(R.id.edit_text);
        mBottomLayout = (LinearLayout) findViewById(R.id.layout_bottom);
    }

    /**
     * 录制语音面板初始化
     */
    private void initRecordPanel() {
        mVoice = View.inflate(this, R.layout.view_record, null);

    }

    /**
     * 主界面的操作设置
     */
    private void initMainPanel() {
        mListView.setOnTouchListener(mListTouchListener);
        mSmile.setOnClickListener(mSmileClickListener);
        mRecord.setOnClickListener(mRecordClickListener);
        mSend.setOnClickListener(mSendClickListener);
        mEditText.setOnClickListener(mEditClickListener);
        mEditText.addTextChangedListener(mWatcher);
        mEditText.setOnFocusChangeListener(mFocusChangeListener);
        mSwipeLayout.setSize(SwipeRefreshLayout.DEFAULT);
        mSwipeLayout.setOnRefreshListener(this);
        mSwipeLayout.setColorSchemeResources(R.color.material_700, R.color.material_500);
    }

    private View.OnFocusChangeListener mFocusChangeListener = new View.OnFocusChangeListener() {

        @Override
        public void onFocusChange(View v, boolean hasFocus) {
            if (hasFocus) {
                removeBottomView();
            }
        }
    };
    /**
     * 输入框的输入监听事件
     */
    private TextWatcher mWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            Logger.debug("---------------------", "afterTextChanged" + s.toString());
            if (!TextUtils.isEmpty(s.toString())) {
                isSend = true;
                mSend.setBackgroundResource(R.drawable.selector_btn_send);
            } else {
                isSend = false;
                mSend.setBackgroundResource(R.drawable.selector_btn_chat_item);
            }
        }
    };


    /**
     * 聊天界面的触摸处理
     */
    private View.OnTouchListener mListTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            //软键盘消失
            InputMethodManager im = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            im.hideSoftInputFromWindow(mEditText.getWindowToken(), 0);
            removeBottomView();
            return false;
        }
    };


    /**
     * 表情按钮点击
     */
    private View.OnClickListener mSmileClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            changePanel(mSmiley);
        }
    };


    /**
     * 录音点击按钮
     */
    private View.OnClickListener mRecordClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            changePanel(mVoice);
        }
    };
    /**
     * 发送点击按钮（分为发送和更多按钮需要判断）
     */
    private View.OnClickListener mSendClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (isSend) { //发送逻辑处理

            } else { //更多逻辑处理
                changePanel(mCamera);
            }
        }
    };
    /**
     * 输入框点击
     */
    private View.OnClickListener mEditClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            removeBottomView();
        }
    };

    /**
     * 相机面板
     */
    private void initCameraPanel() {
        mCamera = View.inflate(this, R.layout.view_camera, null);
        ImageView btnPhoto = (ImageView) mCamera.findViewById(R.id.btn_phone);
        ImageView btnCamera = (ImageView) mCamera.findViewById(R.id.btn_camera);

        btnPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this,PickPictureActivity.class);
                startActivityForResult(intent,1);
                overridePendingTransition(R.anim.album_enter,R.anim.album_exit);
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

        SmileyPagerAdapter adapter = new SmileyPagerAdapter(getSupportFragmentManager(), this);
        adapter.setmSelectListener(new SmileyItemFragment.OnSelectSmileyListener() {
            @Override
            public void onSelected(SpannableString spannableString) {
                //在edittext框中添加选中的表情
                mEditText.getText().insert(mEditText.getSelectionStart(), spannableString);
            }
        });
        adapter.setmDeleteListener(new SmileyItemFragment.OnDeleteSmileyListener() {
            @Override
            public void onDelete() {
                //调用系统的删除键
                mEditText.dispatchKeyEvent(new KeyEvent(KeyEvent.ACTION_MULTIPLE, KeyEvent.KEYCODE_DEL));
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
//                if (position == 0) {
//                    ((SmileyPagerAdapter) mViewPager.getAdapter()).instantiateItem(mViewPager, 1).setItemFirst();
//                } else if (position == 1) {
//                    ((SmileyPagerAdapter) mViewPager.getAdapter()).instantiateItem(mViewPager, 0).setItemLast();
//                }

                mDefalutSmiley.setSelected(false);
                mGameSmiley.setSelected(false);
                mPharseSmiley.setSelected(false);
                switch (position) {
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
                mEditText.getText().insert(mEditText.getSelectionStart(), phraseAdapter.phrases[position]);
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
                ((SmileyPagerAdapter) mViewPager.getAdapter()).instantiateItem(mViewPager, 0).setItemFirst();
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
                ((SmileyPagerAdapter) mViewPager.getAdapter()).instantiateItem(mViewPager, 1).setItemFirst();
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

    /**
     * 移除底部的view
     */
    private void removeBottomView() {
        Logger.debug("removeBottomView", "count-" + mBottomLayout.getChildCount());
        //如果底部视图包含了表情面板或者录音面板，则移除它
        if (mBottomLayout.getChildCount() == 3) {
            mBottomLayout.removeViewAt(2);
        }
    }

    /**
     * 改变面板
     *
     * @param view
     */
    private void changePanel(View view) {
        InputMethodManager im = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        im.hideSoftInputFromWindow(mEditText.getWindowToken(), 0);
        /**
         * 首先判断该视图是否存在，存在就删除，不存在就添加（添加有个条件就是，要除去第三个位置的视图）
         */
        Logger.debug("changePanel", "count-" + mBottomLayout.getChildCount());
        if (mBottomLayout.indexOfChild(view) != -1) {
            mBottomLayout.removeView(view);
        } else {
            if (mBottomLayout.getChildCount() == 3) {
                mBottomLayout.removeViewAt(2);
            }
            mBottomLayout.addView(view, 2);
            Logger.debug("changePanel", "count-" + mBottomLayout.getChildCount());
        }
    }

    @Override
    public void onRefresh() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                mSwipeLayout.setRefreshing(false);
            }
        }, 5000);
    }
}




























