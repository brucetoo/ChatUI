package com.bruce.chatui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.bruce.chatui.adapter.ImageBucketAdapter;
import com.bruce.chatui.utils.album.AlbumTool;
import com.bruce.chatui.utils.album.ImageBucket;

import java.io.Serializable;
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
        pick_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(PickPictureActivity.this,ImageGridActivity.class);
                intent.putExtra("image_list",(Serializable)abList.get(position).imageList);
                intent.putExtra("ablum_name",abList.get(position).bucketName);
                startActivityForResult(intent,1);
            }
        });
    }

    /**
     * 获取相册数据
     */
    private void initData() {
        albumTool = AlbumTool.getHelper(this);
        abList = albumTool.getImagesBucketList(true);
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.album_enter,R.anim.album_exit);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 1){
            if(resultCode == RESULT_OK) {
                setResult(RESULT_OK, data);
            }else {
                setResult(RESULT_CANCELED,null);
            }
            PickPictureActivity.this.finish();
        }
    }
}
