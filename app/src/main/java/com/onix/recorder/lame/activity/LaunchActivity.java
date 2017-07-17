package com.onix.recorder.lame.activity;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.onix.recorder.lame.R;
import com.onix.recorder.lame.databinding.ActivityLaunchBinding;
import com.onix.recorder.lame.views.splash_animation.ItemView;
import com.onix.recorder.lame.views.splash_animation.LaunchAnimationListener;

public class LaunchActivity extends AppCompatActivity {

    public static final int COUNT_ITEMS = 4;
    public static final int COUNT_DIVIDE = 6;
    public static final int DELAY_MILLIS_START_MAIN_ACTIVITY = 2100;

    private Handler mHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        }

        ActivityLaunchBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_launch);

        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        int widthPixels = displayMetrics.widthPixels;
        float size = widthPixels / COUNT_DIVIDE;

        Bitmap bitmapFront = BitmapFactory.decodeResource(getResources(), R.drawable.onix_logo);
        int width = bitmapFront.getWidth();
        int height = bitmapFront.getHeight();

        float tempWidth = (float) width / COUNT_ITEMS;
        float start = 0;

        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams((int) size, (int) size);
        Bitmap bitmapBack = BitmapFactory.decodeResource(getResources(), R.drawable.circle);
        for (int i = 0; i < COUNT_ITEMS; i++) {
            ItemView itemView = new ItemView(this);

            itemView.setFrontColor(Color.WHITE);
            itemView.setFrontTextureImage(bitmapFront, start, 0, start + tempWidth, height, size, size, ImageView.ScaleType.FIT_XY);

            itemView.setBackColor(Color.WHITE);
            itemView.setBackImage(bitmapBack);
            itemView.showBackImage();

            itemView.setLayoutParams(layoutParams);
            binding.container.addView(itemView);

            int id = getResources().getIdentifier("logo_animation_" + i, "anim", getPackageName());
            Animation animation = AnimationUtils.loadAnimation(this, id);
            animation.setAnimationListener(new LaunchAnimationListener(itemView));
            itemView.startAnimation(animation);

            start += tempWidth;
        }

        mHandler = new Handler();
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                startActivity(new Intent(LaunchActivity.this, MainActivity.class));
                finish();
            }
        }, DELAY_MILLIS_START_MAIN_ACTIVITY);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mHandler != null) {
            mHandler.removeCallbacksAndMessages(null);
        }
    }
}
