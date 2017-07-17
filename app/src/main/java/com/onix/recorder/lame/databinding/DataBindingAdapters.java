package com.onix.recorder.lame.databinding;

import android.databinding.BindingAdapter;
import android.databinding.BindingConversion;
import android.databinding.InverseBindingAdapter;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

public class DataBindingAdapters {

    @BindingAdapter("android:src")
    public static void setImageUri(ImageView view, String imageUri) {
        if (imageUri == null) {
            view.setImageURI(null);
        } else {
            view.setImageURI(Uri.parse(imageUri));
        }
    }

    @BindingAdapter("android:src")
    public static void setImageUri(ImageView view, Uri imageUri) {
        view.setImageURI(imageUri);
    }

    @BindingAdapter("android:src")
    public static void setImageDrawable(ImageView view, Drawable drawable) {
        view.setImageDrawable(drawable);
    }

    @BindingAdapter("android:src")
    public static void setImageResource(ImageView imageView, int resource) {
        imageView.setImageResource(resource);
    }

    @BindingAdapter("android:background")
    public static void setBackgroundResource(ImageView imageView, int resource) {
        imageView.setBackgroundResource(resource);
    }

    @BindingAdapter("android:background")
    public static void setBackgroundColor(ImageView imageView, int color) {
        imageView.setBackgroundColor(color);
    }

    @BindingConversion
    public static ColorDrawable convertColorToDrawable(int color) {
        return new ColorDrawable(color);
    }

    @BindingAdapter({"android:text"})
    public static void setText(TextView view, float value) {
        view.setText(String.valueOf(value));
    }

    @BindingAdapter({"android:text"})
    public static void setText(TextView view, int value) {
        view.setText(String.valueOf(value));
    }

    @BindingAdapter("android:layout_marginTop")
    public static void setMarginTop(View view, float topMargin) {
        ViewGroup.MarginLayoutParams layoutParams = (ViewGroup.MarginLayoutParams) view.getLayoutParams();
        layoutParams.setMargins(layoutParams.leftMargin, Math.round(topMargin),
                layoutParams.rightMargin, layoutParams.bottomMargin);
        view.setLayoutParams(layoutParams);
    }

    @InverseBindingAdapter(attribute = "android:text")
    public static int getIntFromText(TextView view) {
        String text = view.getText().toString();
        if (!TextUtils.isEmpty(text)) {
            return Integer.parseInt(text);
        }

        return 0;
    }

    @BindingAdapter("android:text")
    public static void setText(TextView view, long value) {
        view.setText(String.valueOf(value));
    }

    @InverseBindingAdapter(attribute = "android:text")
    public static long getLongFromText(TextView view) {
        String text = view.getText().toString();
        if (!TextUtils.isEmpty(text)) {
            return Long.parseLong(text);
        }

        return 0;
    }
}