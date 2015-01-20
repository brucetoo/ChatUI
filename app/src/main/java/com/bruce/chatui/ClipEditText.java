package com.bruce.chatui;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.text.ClipboardManager;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.style.ImageSpan;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.widget.EditText;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 添加表情复制粘贴处理的输入框
 */
public class ClipEditText extends EditText {
	private final static Pattern SMILEY_PATTERN = Pattern.compile(
			"\\[([\\s\\S]*?)\\]|#[0-9]{3}", Pattern.CASE_INSENSITIVE);
	private final static Pattern FACE_PATTERN = Pattern.compile(
			"\\[emts\\]([\\s\\S]*?)\\[\\/emts\\]", Pattern.CASE_INSENSITIVE);

	public ClipEditText(Context context) {
		super(context);
	}

	public ClipEditText(Context context, AttributeSet attr) {
		super(context, attr);
	}

	public ClipEditText(Context context, AttributeSet attr, int defStyle) {
		super(context, attr, defStyle);
	}

	@Override
	public boolean onTextContextMenuItem(int id) {
		int min = 0;
		int max = getText().length();
		if (isFocused()) {
			final int selStart = getSelectionStart();
			final int selEnd = getSelectionEnd();
			min = Math.max(0, Math.min(selStart, selEnd));
			max = Math.max(0, Math.max(selStart, selEnd));
		}
		ClipboardManager clip = (ClipboardManager) getContext()
				.getSystemService(Context.CLIPBOARD_SERVICE);
		Editable edit = getEditableText();
		switch (id) {
		case android.R.id.paste:
			String value = clip.getText().toString();
			if (min != max) {
				ImageSpan[] os = edit.getSpans(min, max, ImageSpan.class);
				max = getMaxIndex(max);
				for (ImageSpan o : os) {
					edit.removeSpan(o);
				}
			}
			value = filterFace(value);
			if (min == max) {
				edit.insert(min, span(value));
			} else {
				edit.replace(min, max, span(value));
			}
			setSelection(min + value.length());
			onKeyDown(KeyEvent.KEYCODE_BACK, new KeyEvent(
					KeyEvent.ACTION_MULTIPLE, KeyEvent.KEYCODE_BACK));
			return true;
		case android.R.id.copy:
			CharSequence text = "";
			max = getMaxIndex(max);
			text = ((CharSequence) edit).subSequence(min, max).toString();
			clip.setText(text);
			onKeyDown(KeyEvent.KEYCODE_BACK, new KeyEvent(
					KeyEvent.ACTION_MULTIPLE, KeyEvent.KEYCODE_BACK));
			return true;
		case android.R.id.cut:
			CharSequence text2 = "";
			max = getMaxIndex(max);
			text2 = ((CharSequence) edit).subSequence(min, max).toString();
			edit.delete(min, max);
			clip.setText(text2);
			onKeyDown(KeyEvent.KEYCODE_BACK, new KeyEvent(
					KeyEvent.ACTION_MULTIPLE, KeyEvent.KEYCODE_BACK));
			return true;
		default:
			break;
		}
		return super.onTextContextMenuItem(id);
	}

	private int getMaxIndex(int max) {
		ImageSpan[] iss = getEditableText().getSpans(max, max, ImageSpan.class);
		if (iss.length > 0) {
			max = getEditableText().getSpanEnd(iss[0]);
		}
		return max;
	}

	private SpannableStringBuilder span(String content) {
		SpannableStringBuilder spannable = new SpannableStringBuilder(content);
		Matcher matcher = SMILEY_PATTERN.matcher(content);
		while (matcher.find()) {
			String group = matcher.group();
			if (group.contains("[") && group.contains("]")) {
				group = group.substring(1, group.length() - 1);
			}
			Emoji emoji = EmojiSmiley.getEmoji(group);
			if (emoji == null) {
				continue;
			}
			SpannableString item = new SpannableString(emoji.tag);
			Drawable d = getResources().getDrawable(emoji.resourceid);
			d.setBounds(
					0,
					0,
					(int) getResources().getDimension(
							R.dimen.channel_chatsmily_height),
					(int) getResources().getDimension(
							R.dimen.channel_chatsmily_height));
			ImageSpan span = new ImageSpan(d, ImageSpan.ALIGN_BOTTOM);
			spannable.setSpan(span, matcher.start(), matcher.end(),
					Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
		}
		return spannable;
	}

	private String filterFace(String content) {
		Matcher matcher = FACE_PATTERN.matcher(content);
		while (matcher.find()) {
			String key = matcher.group(1);
			String[] keys = key.split(" ");
			key = keys[keys.length - 1];
			if (EmojiSmiley.smileyMap().containsKey(key)) {
				Emoji emoji = EmojiSmiley.smileyMap().get(key);
				content = content.replace(matcher.group(), emoji.tag);
			}
		}
		return content;
	}
}
