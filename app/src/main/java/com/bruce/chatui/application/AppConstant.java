package com.bruce.chatui.application;

import android.app.Application;
import android.content.Context;

import com.bruce.chatui.Emoji;
import com.bruce.chatui.EmojiSmiley;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.cache.memory.impl.UsingFreqLimitedMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.nostra13.universalimageloader.core.download.BaseImageDownloader;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

/**
 * Created by N1007 on 2015/1/20.
 */
public class AppConstant extends Application {

    //默认表情
    private List<Emoji> defaultEmoji;

    //游戏表情
    private List<Emoji> gameEmoji;

    @Override
    public void onCreate() {
        super.onCreate();

        //加载表情
        defaultEmoji = new ArrayList<>();
        gameEmoji = new ArrayList<>();
        Iterator<Emoji> iterator = EmojiSmiley.smileyMap().values().iterator();
        addEmoji(iterator, defaultEmoji, gameEmoji);
        ConfigImageLoader();
    }

    private void ConfigImageLoader() {
        ImageLoaderConfiguration configuration = new ImageLoaderConfiguration.Builder(this)
                .memoryCacheExtraOptions(480, 800) // max width, max height，即保存的每个缓存文件的最大长宽
                .threadPoolSize(3)//线程池内加载的数量
                .threadPriority(Thread.NORM_PRIORITY - 2)
                .denyCacheImageMultipleSizesInMemory()
                .memoryCache(new UsingFreqLimitedMemoryCache(2 * 1024 * 1024)) // You can pass your own memory cache implementation/你可以通过自己的内存缓存实现
                .memoryCacheSize(2 * 1024 * 1024)
                .discCacheSize(50 * 1024 * 1024)
                .discCacheFileNameGenerator(new Md5FileNameGenerator())//将保存的时候的URI名称用MD5 加密
                .tasksProcessingOrder(QueueProcessingType.LIFO)
                .discCacheFileCount(100) //缓存的文件数量
                .defaultDisplayImageOptions(DisplayImageOptions.createSimple())
                .imageDownloader(new BaseImageDownloader(this, 5 * 1000, 30 * 1000)) // connectTimeout (5 s), readTimeout (30 s)超时时间
                .writeDebugLogs() // Remove for release app
                .build();//开始构建
        ImageLoader.getInstance().init(configuration);
    }

    /**
     * 分类添加emoji表情
     *
     * @param iterator
     * @param defaultEmoji
     * @param gameEmoji
     */
    private void addEmoji(Iterator<Emoji> iterator, List<Emoji> defaultEmoji, List<Emoji> gameEmoji) {
        while (iterator.hasNext()) {
            Emoji emoji = iterator.next();
            if (!emoji.hide) { //表情是否是隐藏
                if (emoji.type == Emoji.EMOJI_SMILEY) {
                    defaultEmoji.add(emoji);
                } else if (emoji.type == Emoji.EMOJI_GAME) {
                    gameEmoji.add(emoji);
                }
            }
        }

        sortEmoji(defaultEmoji);
        sortEmoji(gameEmoji);
    }

    /**
     * 对emoji排序 （可不要）
     *
     * @param Emojis
     */
    private void sortEmoji(List<Emoji> Emojis) {
        Collections.sort(Emojis, new Comparator<Emoji>() {

            @Override
            public int compare(Emoji lhs, Emoji rhs) {
                if (lhs.sort > rhs.sort) {
                    return 1;
                } else {
                    return -1;
                }
            }
        });
    }

    public List<Emoji> getGameEmoji() {
        return gameEmoji;
    }

    public List<Emoji> getDefaultEmoji() {
        return defaultEmoji;
    }

    public static AppConstant getApplition(Context context) {
        return (AppConstant) context.getApplicationContext();
    }

}
