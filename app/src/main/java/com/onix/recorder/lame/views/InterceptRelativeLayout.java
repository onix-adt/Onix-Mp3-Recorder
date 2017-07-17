package com.onix.recorder.lame.views;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.os.Build;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.RelativeLayout;

import com.onix.recorder.lame.R;

public class InterceptRelativeLayout extends RelativeLayout {

    private boolean mIsIntercept;

    public InterceptRelativeLayout(Context context) {
        this(context, null);
    }

    public InterceptRelativeLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public InterceptRelativeLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        initAttrs(context, attrs);
    }

    private void initAttrs(Context context, AttributeSet attrs) {
        if (!this.isInEditMode()) {
            TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.InterceptRelativeLayout);
            mIsIntercept = ta.getBoolean(R.styleable.InterceptRelativeLayout_isIntercept, false);
            ta.recycle();
        }
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public InterceptRelativeLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);

        initAttrs(context, attrs);
    }

    public void setIntercept(boolean intercept) {
        mIsIntercept = intercept;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return mIsIntercept;
    }
}