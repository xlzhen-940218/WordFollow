package com.xlzhen.wordfollow.utils;

import android.content.Context;

import java.io.File;

public class FileUidUtils {
    public static String getFileName(Context context,String uid){
        return "uid_" + uid + ".json";
    }
    public static File getFile(Context context, String uid) {
        return StorageUtils.getExternalFileByKey(context, getFileName(context,uid));
    }
}
