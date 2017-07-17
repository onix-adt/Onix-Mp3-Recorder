package com.onix.recorder.lame.data.utils;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.Context;

import java.util.LinkedList;
import java.util.List;

public class AccountUtils {

    public static String getDeviceUserName(Context context) {
        AccountManager manager = AccountManager.get(context);
        Account[] accounts = manager.getAccountsByType("com.google");
        List<String> possibleEmails = new LinkedList<>();

        for (Account account : accounts) {
            possibleEmails.add(account.name);
        }

        if (!possibleEmails.isEmpty() && possibleEmails.get(0) != null) {
            String email = possibleEmails.get(0);
            String[] parts = email.split("@");

            if (parts.length > 1)
                return parts[0];
        }

        return null;
    }
}
