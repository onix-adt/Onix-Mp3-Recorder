package com.onix.recorder.lame.views.splash_animation;

import android.animation.AnimatorInflater;
import android.animation.AnimatorSet;
import android.content.Context;
import android.databinding.DataBindingUtil;
import android.databinding.Observable;
import android.databinding.ObservableBoolean;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.AttrRes;
import android.support.annotation.ColorInt;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.annotation.StyleRes;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView.ScaleType;

import com.onix.recorder.lame.R;
import com.onix.recorder.lame.databinding.LayoutItemViewBinding;

public class ItemView extends FrameLayout implements View.OnClickListener {

    public static final int DISTANCE = 8000;

    private ObservableBoolean fieldFrontVisible = new ObservableBoolean(false);

    private int indexId;
    private boolean mFinishedOpen;
    private LayoutItemViewBinding mBinding;
    private OnItemViewClickListener mListener;

    public ItemView(@NonNull Context context) {
        super(context);
        init(context);
    }

    public ItemView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public ItemView(@NonNull Context context, @Nullable AttributeSet attrs, @AttrRes int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    @SuppressWarnings("unused")
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public ItemView(@NonNull Context context, @Nullable AttributeSet attrs, @AttrRes int defStyleAttr, @StyleRes int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context);
    }

    public void addListener(OnItemViewClickListener listener) {
        this.mListener = listener;
    }

    private void init(Context context) {
        LayoutInflater inflater = LayoutInflater.from(context);
        mBinding = DataBindingUtil.inflate(inflater, R.layout.layout_item_view, this, true);
        mBinding.setListener(this);


        final AnimatorSet setRightOut = (AnimatorSet) AnimatorInflater.loadAnimator(context, R.animator.out_animation);
        final AnimatorSet setLeftIn = (AnimatorSet) AnimatorInflater.loadAnimator(context, R.animator.in_animation);

        float scale = getResources().getDisplayMetrics().density * DISTANCE;
        mBinding.front.setCameraDistance(scale);
        mBinding.back.setCameraDistance(scale);

        fieldFrontVisible.addOnPropertyChangedCallback(new Observable.OnPropertyChangedCallback() {
            @Override
            public void onPropertyChanged(Observable observable, int i) {
                if (((ObservableBoolean) observable).get()) {
                    setRightOut.setTarget(mBinding.back);
                    setLeftIn.setTarget(mBinding.front);
                    setRightOut.start();
                    setLeftIn.start();
                } else {
                    setRightOut.setTarget(mBinding.front);
                    setLeftIn.setTarget(mBinding.back);
                    setRightOut.start();
                    setLeftIn.start();
                }
            }
        });
    }

    @Override
    public void onClick(View v) {
        if (!mFinishedOpen) {
            fieldFrontVisible.set(!fieldFrontVisible.get());
            if (mListener != null) {
                mListener.clickItem(this);
            }
        }
    }

    public void setIndex(int index) {
        setIndex(index, false);
    }

    public void setIndex(int index, boolean showId) {
        indexId = index;
        if (showId) {
            mBinding.frontTitle.setText(String.valueOf(indexId));
        }
    }

    public int getIndexId() {
        return indexId;
    }

    public void close() {
        fieldFrontVisible.set(false);
    }

    public void finishClose() {
        fieldFrontVisible.set(false);
        mFinishedOpen = true;
    }

    public void open() {
        fieldFrontVisible.set(true);
        mFinishedOpen = true;
    }

    public boolean isFinishedOpen() {
        return mFinishedOpen;
    }

    public void setFrontColor(@ColorInt int color) {
        mBinding.front.setBackgroundColor(color);
    }

    public void setFrontImage(Bitmap bitmap) {
        mBinding.frontImage.setImageBitmap(bitmap);
    }

    public void setFrontTextureImage(Bitmap bitmap, int start, int top, int end, int bottom, int itemWidth, int itemHeight, ScaleType scaleType) {
        setFrontTextureImage(bitmap, (float) start, (float) top, (float) end, (float) bottom, (float) itemWidth, (float) itemHeight, scaleType);
    }

    public void setFrontTextureImage(Bitmap bitmap, float start, float top, float end, float bottom, float itemWidth, float itemHeight, ScaleType scaleType) {
        mBinding.frontImage.setImage(bitmap, start, top, end, bottom, itemWidth, itemHeight, scaleType);
    }

    public void setBackColor(@ColorInt int color) {
        mBinding.back.setBackgroundColor(color);
    }

    public void setBackBackground(Drawable drawable) {
        mBinding.back.setBackground(drawable);
    }

    public void setBackImage(Bitmap bitmap) {
        mBinding.backImage.setImageBitmap(bitmap);
    }

    public void setBackTextureImage(Bitmap bitmap, float start, float top, float end, float bottom, float itemWidth, float itemHeight, ScaleType scaleType) {
        mBinding.backImage.setImage(bitmap, start, top, end, bottom, itemWidth, itemHeight, scaleType);
    }

    public void showBackImage() {
        mBinding.backImage.setVisibility(VISIBLE);
    }

    @Override
    public String toString() {
        return "ItemView{" +
                "indexId=" + indexId +
                ", fieldFrontVisible=" + fieldFrontVisible +
                ", mFinishedOpen=" + mFinishedOpen +
                "} " + super.toString();
    }
}
