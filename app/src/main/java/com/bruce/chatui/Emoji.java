package com.bruce.chatui;

/**
 * Emoji Modle
 */
public class Emoji {

	public final static int EMOJI_GAME = 1; //emoji类型
	public final static int EMOJI_SMILEY = 2;
	public final static int EMOJI_VIP = 3;
	
	public int resourceid; //id
	public String value;
	public String tag;
	public boolean hide;
	public int sort; //加入的位置
	public int type;
	
	public static Emoji fromResource(int icon,String value,boolean hide,int sort,String tag,int type) {
        Emoji emoji = new Emoji();
        emoji.resourceid = icon;
        emoji.value = value;
        emoji.hide = hide;
        emoji.sort = sort;
        emoji.tag = tag;
        emoji.type = type;
        return emoji;
    }
}
