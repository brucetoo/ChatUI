package com.bruce.chatui.thirdparty;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.FontMetrics;
import android.graphics.drawable.Drawable;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ImageSpan;
import android.text.style.URLSpan;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TextView;

import com.bruce.chatui.Emoji;
import com.bruce.chatui.EmojiSmiley;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RichTextView extends TextView {

    private final static Pattern SMILEY_PATTERN = Pattern.compile(
            "\\[([\\s\\S]*?)\\]|#[0-9]{3}", Pattern.CASE_INSENSITIVE);
	// 是否自动调整表情大小
	private boolean mAdjustFaceBounds = true;
	private OnLinkClickListener mLinkClickListener = new OnLinkClickListener() {
		@Override
		public void onLinkClick(String value) {

		}
	};

	public RichTextView(Context context) {
		super(context);
	}

	public RichTextView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public RichTextView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public OnLinkClickListener getLinkClickListener() {
		return mLinkClickListener;
	}

	public void setLinkClickListener(OnLinkClickListener linkClickListener) {
		this.mLinkClickListener = linkClickListener;
	}

	public void setChatText(String chatText) {
		if (chatText == null) {
			setText(chatText);
			return;
		}
		Spanned span = Html.fromHtml(chatText);
		setText(span);
		setMovementMethod(LinkMovementMethod.getInstance());
		parseText(span);
	}

	public void parseText(Spanned spanhtml) {
		CharSequence text = getText();
		if (text instanceof Spannable) {
			int end = text.length();
			Spannable sp = (Spannable) getText();
			URLSpan[] urls = sp.getSpans(0, end, URLSpan.class);

			URLSpan[] htmlurls = spanhtml != null ? spanhtml.getSpans(0, end,
					URLSpan.class) : new URLSpan[] {};

			if (urls.length == 0 && htmlurls.length == 0) {
				SpannableStringBuilder style = new SpannableStringBuilder(text);
				filterFace(style);
				setText(style);
				return;
			}

			SpannableStringBuilder style = new SpannableStringBuilder(text);
			for (URLSpan url : urls) {
				if (!isNormalUrl(url)) {
					style.removeSpan(url);// 只需要移除之前的URL样式，再重新设置
					NoLinkSpan span = new NoLinkSpan(url.getURL());
					style.setSpan(span, sp.getSpanStart(url),
							sp.getSpanEnd(url),
							Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
					continue;
				}
				style.removeSpan(url);// 只需要移除之前的URL样式，再重新设置
				MyURLSpan myURLSpan = new MyURLSpan(url.getURL());
				style.setSpan(myURLSpan, sp.getSpanStart(url),
						sp.getSpanEnd(url), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
			}
			for (URLSpan url : htmlurls) {
				style.removeSpan(url);// 只需要移除之前的URL样式，再重新设置
				MyURLSpan myURLSpan = new MyURLSpan(url.getURL());
				style.setSpan(myURLSpan, spanhtml.getSpanStart(url),
						spanhtml.getSpanEnd(url),
						Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
			}
			filterFace(style);
			setText(style);
		}
	}

	private void filterFace(SpannableStringBuilder content) {
		Matcher matcher = SMILEY_PATTERN.matcher(content);
		while (matcher.find()) {
			String key = matcher.group(1);
			String[] keys = key.split(" ");
			key = keys[keys.length - 1];
			Drawable drawable = EmojiSmiley
					.getSmileyDrawable(getContext(), key);
			if (drawable == null) {
				continue;
			}
			Emoji emoji = EmojiSmiley.getEmoji(key);
			if (isAdjustFaceBounds() && emoji.type != Emoji.EMOJI_VIP) {
				int fontHeight = getFontHeight();
				drawable.setBounds(0, 0, fontHeight, fontHeight);
			} else {
				drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
			}
			ImageSpan span = new ImageSpan(drawable, ImageSpan.ALIGN_BOTTOM);
			content.setSpan(span, matcher.start(), matcher.end(),
					Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
		}
	}


	private int getFontHeight() {
		Paint paint = new Paint();
		paint.setTextSize(getTextSize());
		FontMetrics fm = paint.getFontMetrics();
		return (int) Math.ceil(fm.descent - fm.top) + 2;
	}

	public boolean isAdjustFaceBounds() {
		return mAdjustFaceBounds;
	}

	public void setAdjustFaceBounds(boolean adjustFaceBounds) {
		this.mAdjustFaceBounds = adjustFaceBounds;
	}

	public class MyURLSpan extends ClickableSpan {
		private String mUrl;

		public MyURLSpan(String url) {
			mUrl = url;
		}

		@Override
		public void onClick(View widget) {
			mLinkClickListener.onLinkClick(mUrl);
		}
	}

	/**
	 * 无响应的ClickableSpan
	 * 
	 * 
	 */
	public class NoLinkSpan extends ClickableSpan {
		private String text;

		public NoLinkSpan(String text) {
			super();
			this.text = text;
		}

		@Override
		public void updateDrawState(TextPaint ds) {
			ds.setColor(Color.BLACK);
			ds.setUnderlineText(false); // 去掉下划线
		}

		@Override
		public void onClick(View widget) {
			// doNothing...
		}

	}

	public interface OnLinkClickListener {
		void onLinkClick(String value);
	}

	/**
	 * 过滤掉一些不正常的链接
	 * 
	 * @param url
	 * @return
	 */
	public boolean isNormalUrl(URLSpan url) {
		String urlStr = url.getURL();
		if (urlStr.endsWith(".sh")) {
			return false;
		}
		return true;
	}
}
