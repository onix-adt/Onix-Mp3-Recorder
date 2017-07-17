package com.onix.recorder.lame.data.helpers;

import android.content.Context;
import android.support.annotation.MenuRes;
import android.support.v7.widget.PopupMenu;
import android.view.View;

public class ContextMenuHelper {

    public static void showMenu(final View anchorView, Context context, @MenuRes int resMenu, PopupMenu.OnMenuItemClickListener menuItemClickListener) {
        PopupMenu popup = new PopupMenu(context, anchorView);
        popup.getMenuInflater().inflate(resMenu, popup.getMenu());
        popup.setOnMenuItemClickListener(menuItemClickListener);
        popup.show();
    }
}
