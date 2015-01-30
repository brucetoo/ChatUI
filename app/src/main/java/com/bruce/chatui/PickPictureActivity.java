package com.bruce.chatui;

import android.os.Bundle;
import android.widget.ListView;

import com.bruce.chatui.adapter.ImageBucketAdapter;
import com.bruce.chatui.utils.album.AlbumTool;
import com.bruce.chatui.utils.album.ImageBucket;

import java.util.List;

import roboguice.activity.RoboActivity;
import roboguice.inject.ContentView;
import roboguice.inject.InjectView;

/**
 * Created by N1007 on 2015/1/30.
 * 选择相册
 */
@ContentView(R.layout.pick_photo_activity)
public class PickPictureActivity extends RoboActivity {

    @InjectView(R.id.pick_list)
    private ListView pick_list;

    private List<ImageBucket> abList;
    private AlbumTool albumTool;
    private ImageBucketAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initData();
        mAdapter = new ImageBucketAdapter(this,abList);
        pick_list.setAdapter(mAdapter);
    }

    /**
     * 获取相册数据
     */
    private void initData() {
        albumTool = AlbumTool.getHelper(this);
        abList = albumTool.getImagesBucketList(true);
    }

}
