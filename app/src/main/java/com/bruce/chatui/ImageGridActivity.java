package com.bruce.chatui;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bruce.chatui.adapter.ImageGridAdapter;
import com.bruce.chatui.utils.Const;
import com.bruce.chatui.utils.album.ImageItem;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import roboguice.activity.RoboActivity;
import roboguice.inject.ContentView;
import roboguice.inject.InjectView;

/**
 * Created by N1007 on 2015/2/2.
 */
@ContentView(R.layout.activity_image_grid)
public class ImageGridActivity extends RoboActivity {

    //UI
    @InjectView(R.id.gridview)
    private GridView mGridView;
    @InjectView(R.id.base_fragment_title)
    private TextView mTitle;
    @InjectView(R.id.back_btn)
    private ImageView mLeftBtn;
    @InjectView(R.id.cancel)
    private TextView mCancle;
    @InjectView(R.id.finish)
    private TextView mSend;
    @InjectView(R.id.preview)
    private TextView mPreview;

    private List<ImageItem> mList;
    private String mAblumName;
    private ImageGridAdapter mAdapter;

    private Handler mHander = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    Toast.makeText(ImageGridActivity.this, "选择不能超过" + Const.MAX_SELECT_IMAGE_COUNT + "个", Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mList = (List<ImageItem>) getIntent().getSerializableExtra("image_list");
        mAblumName = getIntent().getStringExtra("ablum_name");
        initView();
    }

    private void initView() {
        mGridView.setSelector(new ColorDrawable(Color.TRANSPARENT));
        mAdapter = new ImageGridAdapter(this, mList, mHander);
        mGridView.setAdapter(mAdapter);
        mAdapter.setTextCallBack(new ImageGridAdapter.TextCallBack() {
            @Override
            public void onListen(int count) {
                if (count == 0) {
                    mSend.setText("发送");
                } else {
                    mSend.setText("发送" + "(" + count + ")");
                }
            }
        });

        mLeftBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ImageGridActivity.this,
                        PickPictureActivity.class);
                startActivity(intent);
                ImageGridActivity.this.finish();
            }
        });

        mCancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                setResult(RESULT_CANCELED, null);
                ImageGridActivity.this.finish();
            }
        });

        mSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mAdapter.getSelectedMap().size() > 0) {
                    //发送消息~~
                    Intent intent = new Intent();
                    ArrayList<String> paths = new ArrayList<String>();
                    for(Map.Entry entry:mAdapter.getSelectedMap().entrySet()){
                       paths.add("file://"+((ImageItem)entry.getValue()).getImagePath());
                    }
                    intent.putExtra("paths",paths);
                    setResult(RESULT_OK,intent);
                    ImageGridActivity.this.finish();
                } else {
                    Toast.makeText(ImageGridActivity.this, "你还没选中任何图片~", Toast.LENGTH_SHORT).show();
                }
            }
        });

        mPreview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mAdapter.getSelectedMap().size() > 0) {
                    Toast.makeText(ImageGridActivity.this, "此功能不想做~~", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(ImageGridActivity.this, "你还没选中任何图片~", Toast.LENGTH_SHORT).show();
                }
            }
        });
        mTitle.setText(mAblumName);

    }
}
