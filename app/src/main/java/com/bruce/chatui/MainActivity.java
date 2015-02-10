package com.bruce.chatui;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.Editable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.bruce.chatui.adapter.MessageAdapter;
import com.bruce.chatui.adapter.PhraseAdapter;
import com.bruce.chatui.adapter.SmileyPagerAdapter;
import com.bruce.chatui.utils.Const;
import com.bruce.chatui.utils.Logger;
import com.mogujie.tt.support.audio.AudioPlayerHandler;
import com.mogujie.tt.support.audio.AudioRecordHandler;
import com.mogujie.tt.support.audio.SpeexEncoder;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by N1007 on 2015/1/20.
 */
public class MainActivity extends FragmentActivity implements SwipeRefreshLayout.OnRefreshListener {
    private static final int MSG_PIC_PHOTO = 0;
    private static final int MSG_TAKE_PICTURE = 1;
    private static String TAG = "MainActivity";
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
    private ImageButton mReVoice;
    private TextView mReVoiceHint;
    private ViewPager mViewPager;
    private ListView mSmileyListView;
    private TextView mDefalutSmiley;
    private TextView mGameSmiley;
    private TextView mPharseSmiley;
    private boolean isSend;
    private String takePhotoSavePath;
    private ArrayList<MessageInfo> messageList = new ArrayList<MessageInfo>();
    private MessageAdapter adapter;

    private Dialog mVolumeDlg; //音量dialog
    private LinearLayout mVolumeBg; //背景
    private ImageView mVolumeImg; //图片

    private AudioRecordHandler mAudioRecorder;
    private Thread mAudioRecordThread;
    private boolean mIsAlready = false; //录音是否完成
    private static Handler mHandler = null;
    private String mAudioPath = null;

    public static Handler getmHandler() {
        return mHandler;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);
        initHander();
        initData();
        initView();
        initSmileyPanel();
        initCameraPanel();
        initRecordPanel();
        initVolumeDialog();
        initMainPanel();
    }

    private void initHander() {
        mHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                switch (msg.what) {
                    case Const.RECEIVE_MAX_VOLUME:
                        Log.i("receivevoice-------", "");
                        onReceiveMaxVolume((Integer) msg.obj);
                        break;
                    case Const.RECORD_TOO_LONG:
                        Toast.makeText(MainActivity.this, "录音时间太长", Toast.LENGTH_SHORT).show();
                        break;
                    case Const.RECORD_MSG_OVER:
                        onRecordOver((Float) msg.obj);
                        break;
                    case Const.RECORD_STOP_PLAY:
                        adapter.stopAnimaiton((String) msg.obj);
                        break;
                }
            }
        };
    }


    /**
     * 音量提示的dialog
     */
    private void initVolumeDialog() {
        mVolumeDlg = new Dialog(this, R.style.SoundVolumeStyle);
        mVolumeDlg.requestWindowFeature(Window.FEATURE_NO_TITLE);
        mVolumeDlg.setContentView(R.layout.view_volume_dialog);
        mVolumeDlg.setCanceledOnTouchOutside(true);
        mVolumeBg = (LinearLayout) mVolumeDlg.findViewById(R.id.volume_bg);
        mVolumeImg = (ImageView) mVolumeDlg.findViewById(R.id.volume_img);
    }

    private void initData() {

        for (int i = 0; i < 10; i++) {
            MessageInfo info = new MessageInfo();
            info.msgType = (int) (Math.random() * 6);
            info.contentText = "msg-" + i;
            info.time = "11:1" + i;
            if (info.msgType == MessageAdapter.MESSAGE_TYPE_RECEIVE_IMAGE)
                info.isSend = false;
            else
                info.isSend = true;
            if (info.msgType == MessageAdapter.MESSAGE_TYPE_SEND_AUDIO || info.msgType == MessageAdapter.MESSAGE_TYPE_RECEIVE_AUDIO) {
                info.audioPath = Environment.getExternalStorageDirectory()
                        .getAbsolutePath()
                        + File.separator
                        + "Brucetoo"
                        + File.separator
                        + "audio"
                        + File.separator
                        +"1423555852706"+".spx";
                info.audioLen = "4";
            }
            info.imagePath = "drawable://" + R.drawable.pic1;
            messageList.add(info);
        }
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
        mReVoice = (ImageButton) mVoice.findViewById(R.id.voice_view);
        mReVoiceHint = (TextView) mVoice.findViewById(R.id.voice_hint);
        mReVoice.setOnTouchListener(mVoiceTouchListener);
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
        adapter = new MessageAdapter(this);
        adapter.refreshList(messageList);
        mListView.setAdapter(adapter);
    }

    private View.OnFocusChangeListener mFocusChangeListener = new View.OnFocusChangeListener() {

        @Override
        public void onFocusChange(View v, boolean hasFocus) {
            if (hasFocus) {
                removeBottomView();
            }
        }
    };


    float yDown = 0;
    float yUp = 0;

    private View.OnTouchListener mVoiceTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            //滑动ListView到底部
            mListView.post(new Runnable() {
                @Override
                public void run() {
                    mListView.setSelection(adapter.getCount() - 1);
                }
            });

            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    yDown = event.getY(); //记录按下时的Y位置
                    //判断是否正在播放音频
                    if (AudioPlayerHandler.getInstance().isPlaying())
                        AudioPlayerHandler.getInstance().stopPlayer();
                    mReVoiceHint.setVisibility(View.VISIBLE);
                    mVolumeDlg.show();
                    mAudioPath = getAudioPath(String.valueOf(System.currentTimeMillis()) + ".spx");
                    mAudioRecorder = new AudioRecordHandler(mAudioPath, new SpeexEncoder.TaskCallback() {
                        @Override
                        public void callback(Object result) {
                            Logger.info(TAG, "send audio message");
                            if (mIsAlready) {
                                //此处发送消息 用Handler处理
                                mHandler.obtainMessage(Const.RECORD_MSG_OVER, mAudioRecorder.getRecordTime()).sendToTarget();
                            }
                        }
                    });

                    mAudioRecordThread = new Thread(mAudioRecorder);
                    mIsAlready = false; // 设置未开始录音
                    mAudioRecorder.setRecording(true);
                    Logger.info(TAG, "audio start record thread");
                    mAudioRecordThread.start(); //开启录制音频线程
                    break;

                case MotionEvent.ACTION_MOVE:
                    yUp = event.getY(); //移动起来的Y位置
                    if (yDown - yUp > 50) {
                        mVolumeBg.setBackgroundResource(R.drawable.sound_volume_cancel_bk);
                    } else {
                        mVolumeBg.setBackgroundResource(R.drawable.sound_volume_default_bk);
                    }
                    break;

                case MotionEvent.ACTION_UP:
                    mReVoiceHint.setVisibility(View.GONE);
                    //停止录音，隐藏dialog
                    if (mAudioRecorder.isRecording()) {
                        mAudioRecorder.setRecording(false);
                    }

                    if (mVolumeDlg.isShowing()) {
                        mVolumeDlg.dismiss();
                    }
                    if (yDown - yUp <= 50) { //上滑动停止录音
                        if (mAudioRecorder.getRecordTime() >= 0.5 && mAudioRecorder.getRecordTime() < Const.MAX_SOUND_RECORD_TIME) {
                            mIsAlready = true;
                        }
                    }

                    if (mAudioRecorder.getRecordTime() <= 0.5) {
                        Toast.makeText(MainActivity.this, "录音时间太短", Toast.LENGTH_SHORT).show();
                    }
                    break;
                default:
                    break;
            }

            return false;
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
                MessageInfo in = new MessageInfo();
                in.isSend = true;
                in.msgType = MessageAdapter.MESSAGE_TYPE_SEND_TEXT;
                in.time = "22:22";
                in.contentText = mEditText.getText().toString();
                adapter.addMessage(in, mListView);
                mEditText.setText("");
            } else { //更多逻辑处理s
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
            mListView.post(new Runnable() {
                @Override
                public void run() {
                    mListView.setSelection(adapter.getCount() - 1);
                }
            });
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
                removeBottomView();
                Intent intent = new Intent(MainActivity.this, PickPictureActivity.class);
                startActivityForResult(intent, MSG_PIC_PHOTO);
                overridePendingTransition(R.anim.album_enter, R.anim.album_exit);
            }
        });

        btnCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                removeBottomView();
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                takePhotoSavePath = getImageSavePath(String.valueOf(System.currentTimeMillis()) + ".jpg");
                intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(new File(takePhotoSavePath)));
                startActivityForResult(intent, MSG_TAKE_PICTURE);
            }
        });
    }

    /**
     * 获取图片的保存路径
     *
     * @param fileName
     * @return
     */
    public static String getImageSavePath(String fileName) {

        if (TextUtils.isEmpty(fileName)) {
            return null;
        }

        final File folder = new File(Environment.getExternalStorageDirectory()
                .getAbsolutePath()
                + File.separator
                + "Brucetoo" //文件夹名
                + File.separator
                + "images");
        if (!folder.exists()) {
            folder.mkdirs();
        }

        return folder.getAbsolutePath() + File.separator + fileName;
    }

    /**
     * 语音保存路径
     *
     * @return
     */
    private String getAudioPath(String fileName) {
        final File folder = new File(Environment.getExternalStorageDirectory()
                .getAbsolutePath()
                + File.separator
                + "Brucetoo"
                + File.separator
                + "audio");
        if (!folder.exists()) {
            folder.mkdirs();
        }
        //+File.separator+fileName+".spx"
        return folder.getAbsolutePath() + File.separator + fileName;
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
                Logger.info("selecet SpannableString:", spannableString.toString());
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
        messageList.clear();
        initData();
        Logger.info("onRefresh:", "" + messageList.size());
        adapter.refreshList(messageList);
        mSwipeLayout.setRefreshing(false);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == MSG_PIC_PHOTO) {
                handPickPhotoData(data);
            } else if (requestCode == MSG_TAKE_PICTURE) {
                handTakePhotoData(data);
            }
        }
    }

    /**
     * 选择照片处理
     *
     * @param data
     */
    private void handPickPhotoData(Intent data) {
        ArrayList<String> paths = data.getStringArrayListExtra("paths");
        for (String path : paths) {
            MessageInfo info = new MessageInfo();
            info.time = "33:33";
            info.msgType = MessageAdapter.MESSAGE_TYPE_SEND_IMAGE;
            info.isSend = true;
            info.imagePath = path;
            Log.d(TAG, "handPickPhotoData:" + path);
            adapter.addMessage(info, mListView);
        }

    }

    /**
     * 处理拍照后的数据
     *
     * @param data
     */
    private void handTakePhotoData(Intent data) {
        if (data != null) {
            Bundle bundle = data.getExtras();
            Bitmap bitmap = (Bitmap) bundle.get("data");
            Logger.info("bitmap-info:", bitmap.toString());

        } else {
            //从takePhotoSavePath 中获取bitmap
            Logger.info("bitmap-info:", takePhotoSavePath);
        }
        MessageInfo info = new MessageInfo();
        info.isSend = true;
        info.imagePath = "file://" + takePhotoSavePath;
        info.msgType = MessageAdapter.MESSAGE_TYPE_SEND_IMAGE;
        info.time = "44:44";
        adapter.addMessage(info, mListView);
        //压缩处理.
        //上传至服务器
        //加入消息队列.....
    }


    /**
     * 调整录音显示界面
     *
     * @param voiceValue
     */
    private void onReceiveMaxVolume(int voiceValue) {
        Log.i("onReceiveMaxVolume--", voiceValue + "");
        if (voiceValue < 200.0) {
            mVolumeImg.setImageResource(R.drawable.sound_volume_01);
        } else if (voiceValue > 200.0 && voiceValue < 600) {
            mVolumeImg.setImageResource(R.drawable.sound_volume_02);
        } else if (voiceValue > 600.0 && voiceValue < 1200) {
            mVolumeImg.setImageResource(R.drawable.sound_volume_03);
        } else if (voiceValue > 1200.0 && voiceValue < 2400) {
            mVolumeImg.setImageResource(R.drawable.sound_volume_04);
        } else if (voiceValue > 2400.0 && voiceValue < 10000) {
            mVolumeImg.setImageResource(R.drawable.sound_volume_05);
        } else if (voiceValue > 10000.0 && voiceValue < 28000.0) {
            mVolumeImg.setImageResource(R.drawable.sound_volume_06);
        } else if (voiceValue > 28000.0) {
            mVolumeImg.setImageResource(R.drawable.sound_volume_07);
        }
    }

    /**
     * 录音结束处理
     *
     * @param voiceLen 录音时长
     */
    private void onRecordOver(Float voiceLen) {
        Log.i(TAG, "record over");

        int tLen = (int) (voiceLen + 0.5);
        tLen = tLen < 1 ? 1 : tLen;
        if (tLen < voiceLen) {
            ++tLen;
        }

        MessageInfo info = new MessageInfo();
        info.isSend = true;
        info.msgType = MessageAdapter.MESSAGE_TYPE_SEND_AUDIO;
        info.audioLen = tLen + "";
        info.audioPath = mAudioPath;
        info.time = "44:44";
        adapter.addMessage(info, mListView);
    }
}




























