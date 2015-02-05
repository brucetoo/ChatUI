
package com.bruce.chatui.thirdparty;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.util.DisplayMetrics;

/**
 * 采用遮罩的方式，实现气泡  http://blog.csdn.net/xyz_lmn/article/details/22745997
 */
public class BubbleImageHelper {
    private Context context = null;
    private static BubbleImageHelper instance = null;

    public static synchronized BubbleImageHelper getInstance(Context c) {
        if (null == instance) {
            instance = new BubbleImageHelper(c);
        }
        return instance;
    }

    private BubbleImageHelper(Context c) {
        context = c;
    }

    private Bitmap getScaleImage(Bitmap bitmap, float width, float height) {
        if (null == bitmap || width < 0.0f || height < 0.0f) {
            return null;
        }
        Matrix matrix = new Matrix();
        float scaleWidth = width / bitmap.getWidth();
        float scaleHeight = height / bitmap.getHeight();
        matrix.postScale(scaleWidth, scaleHeight);
        Bitmap resizeBmp = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(),
                bitmap.getHeight(), matrix, true);
        return resizeBmp;
    }

    public Bitmap getBubbleImageBitmap(Bitmap srcBitmap,
            int backgroundResourceID) {
        if (null == srcBitmap) {
            return null;
        }
        Bitmap background = null;
        background = BitmapFactory.decodeResource(context.getResources(),
                backgroundResourceID);
        if (null == background) {
            return null;
        }

        Bitmap mask = null;
        Bitmap newBitmap = null;
        mask = srcBitmap;

        float srcWidth = (float) srcBitmap.getWidth();
        float srcHeight = (float) srcBitmap.getHeight();
        if (srcWidth < (float) getImageMessageItemMinWidth(context)
                && srcHeight < (float) getImageMessageItemMinHeight(context)) {
            srcWidth = getImageMessageItemMinWidth(context);
            srcHeight = (float)getImageMessageItemMinHeight(context);
            Bitmap tmp = getScaleImage(background, srcWidth, srcHeight);
            if (null != tmp) {
                background = tmp;
            } else {
                tmp = getScaleImage(srcBitmap,
                        (float) getImageMessageItemDefaultWidth(context),
                        (float) getImageMessageItemDefaultHeight(context));
                if (null != tmp) {
                    mask = tmp;
                }
            }
        }

        Config config = background.getConfig();
        if (null == config) {
            config = Config.ARGB_8888;
        }

        newBitmap = Bitmap.createBitmap(background.getWidth(),
                background.getHeight(), config);
        Canvas newCanvas = new Canvas(newBitmap);

        newCanvas.drawBitmap(background, 0, 0, null);

        Paint paint = new Paint();

        //取下层图像非交集部门与上层图像交集部门
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_ATOP));

        int left = 0;
        int top = 0;
        int right = mask.getWidth();
        int bottom = mask.getHeight();
        if (mask.getWidth() > background.getWidth()) {
            left = (mask.getWidth() - background.getWidth()) / 2;
            right = mask.getWidth() - left;
        }

        if (mask.getHeight() > background.getHeight()) {
            top = (mask.getHeight() - background.getHeight()) / 2;
            bottom = mask.getHeight() - top;
        }

        newCanvas.drawBitmap(mask, new Rect(left, top, right, bottom),
                new Rect(0, 0, background.getWidth(), background.getHeight()),
                paint);

        return newBitmap;
    }

    public int getElementSize(Context context) {
        if (context != null) {
            DisplayMetrics dm = context.getResources().getDisplayMetrics();
            int screenHeight = px2dip(dm.heightPixels, context);
            int screenWidth = px2dip(dm.widthPixels, context);
            int size = screenWidth / 6;
            if (screenWidth >= 800) {
                size = 60;
            } else if (screenWidth >= 650) {
                size = 55;
            } else if (screenWidth >= 600) {
                size = 50;
            } else if (screenHeight <= 400) {
                size = 20;
            } else if (screenHeight <= 480) {
                size = 25;
            } else if (screenHeight <= 520) {
                size = 30;
            } else if (screenHeight <= 570) {
                size = 35;
            } else if (screenHeight <= 640) {
                if (dm.heightPixels <= 960) {
                    size = 35;
                } else if (dm.heightPixels <= 1000) {
                    size = 45;
                }
            }
            return size;
        }
        return 40;
    }

    private  int px2dip(float pxValue, Context context) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }

    public  int getImageMessageItemDefaultWidth(Context context) {
        return getElementSize(context) * 5;
    }

    public  int getImageMessageItemDefaultHeight(Context context) {
        return getElementSize(context) * 7;
    }

    public  int getImageMessageItemMinWidth(Context context) {
        return getElementSize(context) * 3;
    }

    public  int getImageMessageItemMinHeight(Context context) {
        return getElementSize(context) * 3;
    }
}
