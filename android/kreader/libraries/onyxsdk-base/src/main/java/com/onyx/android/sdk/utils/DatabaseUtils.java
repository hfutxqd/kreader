package com.onyx.android.sdk.utils;

import android.database.sqlite.SQLiteDatabase;

/**
 * Created by ming on 2017/7/21.
 */

public class DatabaseUtils {

    public static int getDatabase(String databasePath) {
        SQLiteDatabase database = SQLiteDatabase.openDatabase(databasePath, null,SQLiteDatabase.OPEN_READONLY);
        int version = database.getVersion();
        database.close();
        return version;
    }

    public static boolean canRestoreDB(final String src, final String dst) {
        int srcDBVersion = DatabaseUtils.getDatabase(src);
        int dstDBVersion = DatabaseUtils.getDatabase(dst);
        return dstDBVersion >= srcDBVersion;
    }
}
