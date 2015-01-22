package com.bruce.chatui.application;

import android.app.Application;
import android.content.Context;

import com.bruce.chatui.Emoji;
import com.bruce.chatui.EmojiSmiley;

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
       Iterator<Emoji> iterator =  EmojiSmiley.smileyMap().values().iterator();
       addEmoji(iterator,defaultEmoji,gameEmoji);
    }
    /**
     * 分类添加emoji表情
     * @param iterator
     * @param defaultEmoji
     * @param gameEmoji
     */
    private void addEmoji(Iterator<Emoji> iterator, List<Emoji> defaultEmoji, List<Emoji> gameEmoji) {
        while(iterator.hasNext()){
           Emoji emoji = iterator.next();
           if(!emoji.hide){ //表情是否是隐藏
               if(emoji.type == Emoji.EMOJI_SMILEY){
                   defaultEmoji.add(emoji);
               }else if(emoji.type == Emoji.EMOJI_GAME){
                   gameEmoji.add(emoji);
               }
           }
        }

        sortEmoji(defaultEmoji);
        sortEmoji(gameEmoji);
    }

    /**
     * 对emoji排序 （可不要）
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
