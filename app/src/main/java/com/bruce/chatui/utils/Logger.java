package com.bruce.chatui.utils;

import android.util.Log;

/**
 * Logger
 *
 */

public class Logger {

	public static void debug(String tag, String value) {

		if (Const.IS_DEBUG) {
			Log.d(tag, value);
		}
	}

	public static void info(String tag, String value) {

		if (Const.IS_DEBUG) {
			Log.i(tag, value);
		}
	}

	public static void error(String tag, String value, Exception e) {

		if (Const.IS_DEBUG) {
			Log.e(tag, value, e);
		}
	}

	public static void error(String tag, String value) {

		if (Const.IS_DEBUG) {
			Log.e(tag, value);
		}
	}

	public static void error(String tag, Exception e) {

		if (Const.IS_DEBUG) {
			Log.e(tag, "", e);
		}
	}

	public static void warning(String tag, String value) {

		if (Const.IS_DEBUG) {
			Log.w(tag, value);
		}
	}

	public static void view(String tag, String value) {

		if (Const.IS_DEBUG) {
			Log.v(tag, value);
		}
	}

}
