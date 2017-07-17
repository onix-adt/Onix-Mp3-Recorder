package com.onix.recorder.lame.views.splash_animation;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Rect;
import android.support.v7.widget.AppCompatImageView;
import android.util.AttributeSet;

public class TextureImageView extends AppCompatImageView {

    private Bitmap mBitmap;

    private Rect mSrc;
    private Rect mDst;

    public TextureImageView(Context context) {
        super(context);
    }

    public TextureImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public TextureImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setImage(Bitmap bitmap, float start, float top, float end, float bottom, float itemWidth, float itemHeight, ScaleType scaleType) {
        this.mBitmap = bitmap;

        float bitmapWidth = end - start;
        float bitmapHeight = bottom - top;

        float bitmapRatio = bitmapWidth / bitmapHeight;
        float itemRatio = itemWidth / itemHeight;

        switch (scaleType) {
            case FIT_CENTER:
                if (bitmapRatio >= itemRatio) {
                    float scale = bitmapWidth / itemWidth;
                    float tempHeight = bitmapHeight / scale;
                    float offset = (itemHeight - tempHeight) / 2;
                    mDst = new Rect(0, (int) offset, (int) itemWidth, (int) (offset + tempHeight));
                } else {
                    float scale = bitmapHeight / itemHeight;
                    float tempWidth = (bitmapWidth / scale);
                    float offset = (itemWidth - tempWidth) / 2;
                    mDst = new Rect((int) offset, 0, (int) (offset + tempWidth), (int) itemHeight);
                }
                mSrc = new Rect((int) start, (int) top, (int) end, (int) bottom);
                break;

            case CENTER_CROP:
                if (bitmapRatio >= itemRatio) {
                    float scale = itemHeight / bitmapHeight;
                    float tempWidth = (itemWidth / scale);
                    float offset = (bitmapWidth - tempWidth) / 2;
                    mSrc = new Rect((int) (start + offset), (int) top, (int) (start + offset + tempWidth), (int) bottom);
                } else {
                    float scale = itemWidth / bitmapWidth;
                    float tempHeight = (itemHeight / scale);
                    float offset = (bitmapHeight - tempHeight) / 2;
                    mSrc = new Rect((int) start, (int) (top + offset), (int) end, (int) (top + offset + tempHeight));
                }
                mDst = new Rect(0, 0, (int) itemWidth, (int) itemHeight);
                break;

            default:
                mSrc = new Rect((int) start, (int) top, (int) end, (int) bottom);
                mDst = new Rect(0, 0, (int) itemWidth, (int) itemHeight);
                break;
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (mBitmap != null && mSrc != null && mDst != null) {
            canvas.drawColor(Color.TRANSPARENT);
            canvas.drawBitmap(mBitmap, mSrc, mDst, null);
        }
        super.onDraw(canvas);
    }
}
