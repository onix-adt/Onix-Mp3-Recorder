package com.onix.recorder.lame.views.splash_animation;

import android.view.animation.Animation;

public class LaunchAnimationListener implements Animation.AnimationListener {

    private ItemView mItemView;

    public LaunchAnimationListener(ItemView itemView) {
        this.mItemView = itemView;
    }

    @Override
    public void onAnimationStart(Animation animation) {

    }

    @Override
    public void onAnimationEnd(Animation animation) {
        mItemView.open();
    }

    @Override
    public void onAnimationRepeat(Animation animation) {

    }
}
