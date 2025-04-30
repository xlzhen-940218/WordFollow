package com.xlzhen.wordfollow.utils;


import android.content.Context;

import com.alibaba.fastjson2.JSON;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

public class StorageUtils {
    public static <T> void saveData(Context context, String key, T data) {
        File dataDir = context.getDir("data", Context.MODE_PRIVATE);
        File dataFile = new File(String.format("%s/%s", dataDir.getAbsolutePath(), key));
        if (!dataFile.exists()) {
            try {
                dataFile.createNewFile();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        String json = JSON.toJSONString(data);
        try {
            FileOutputStream stream = new FileOutputStream(dataFile);
            stream.write(json.getBytes(StandardCharsets.UTF_8));
            stream.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static <T> void saveExternalFilesData(Context context, String key, T data) {
        File dataDir = context.getExternalFilesDir("file");
        if (!dataDir.exists()) {
            dataDir.mkdirs();
        }
        File dataFile = new File(String.format("%s/%s", dataDir.getAbsolutePath(), key));
        if (!dataFile.exists()) {
            try {
                dataFile.createNewFile();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        String json = JSON.toJSONString(data);
        try {
            FileOutputStream stream = new FileOutputStream(dataFile);
            stream.write(json.getBytes(StandardCharsets.UTF_8));
            stream.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static <T> void saveExternalFilesData(Context context, String key, byte[] data) {
        File dataDir = context.getExternalFilesDir("file");
        if (!dataDir.exists()) {
            dataDir.mkdirs();
        }
        File dataFile = new File(String.format("%s/%s", dataDir.getAbsolutePath(), key));
        if (!dataFile.exists()) {
            try {
                dataFile.createNewFile();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        try {
            FileOutputStream stream = new FileOutputStream(dataFile);
            stream.write(data);
            stream.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static <T> T getData(Context context, String key, Class<T> cls) {
        File dataDir = context.getDir("data", Context.MODE_PRIVATE);
        File dataFile = new File(String.format("%s/%s", dataDir.getAbsolutePath(), key));
        if (!dataFile.exists()) {
            return null;
        }
        StringBuilder stringBuilder = new StringBuilder();
        try {
            // 打开文件输入流
            FileInputStream fis = new FileInputStream(dataFile.getAbsolutePath());
            // 使用缓冲读取器
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(fis, StandardCharsets.UTF_8));
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                stringBuilder.append(line).append("\n");
            }
            // 关闭资源
            fis.close();
            bufferedReader.close();
            return JSON.parseObject(stringBuilder.toString(), cls);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }

    }

    public static <T> T getExternalFilesData(Context context, String key, Class<T> cls) {
        File dataDir = context.getExternalFilesDir("file");
        File dataFile = new File(String.format("%s/%s", dataDir.getAbsolutePath(), key));
        if (!dataFile.exists()) {
            return null;
        }
        StringBuilder stringBuilder = new StringBuilder();
        try {
            // 打开文件输入流
            FileInputStream fis = new FileInputStream(dataFile.getAbsolutePath());
            // 使用缓冲读取器
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(fis, StandardCharsets.UTF_8));
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                stringBuilder.append(line).append("\n");
            }
            // 关闭资源
            fis.close();
            bufferedReader.close();
            return JSON.parseObject(stringBuilder.toString(), cls);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }

    }

    public static byte[] getExternalFilesData(Context context, String key) {
        File dataDir = context.getExternalFilesDir("file");
        File dataFile = new File(String.format("%s/%s", dataDir.getAbsolutePath(), key));
        if (!dataFile.exists()) {
            return null;
        }
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        try {
            FileInputStream stream = new FileInputStream(dataFile);
            while (stream.read(buffer) != -1) {
                byteArrayOutputStream.write(buffer);
            }
            return byteArrayOutputStream.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }

    }

    public static boolean removeData(Context context, String key) {
        File dataDir = context.getDir("data", Context.MODE_PRIVATE);
        File dataFile = new File(String.format("%s/%s", dataDir.getAbsolutePath(), key));
        if (!dataFile.exists()) {
            return false;
        }
        return dataFile.delete();
    }

    public static String getKeyExternalFilesPath(Context context, String key) {
        File dataDir = context.getExternalFilesDir("file");
        File dataFile = new File(String.format("%s/%s", dataDir.getAbsolutePath(), key));
        if (!dataFile.exists()) {
            return null;
        }
        return dataFile.getAbsolutePath();
    }

    public static String generateVoiceFilePath(Context context) {
        File dataDir = context.getExternalFilesDir("voice");
        File dataFile = new File(String.format("%s/%s", dataDir.getAbsolutePath(), UUID.randomUUID().toString()));
        return dataFile.getAbsolutePath();
    }

    public static File[] getExternalFiles(Context context) {
        File dataDir = context.getExternalFilesDir("file");
        File dataFile = new File(String.format("%s", dataDir.getAbsolutePath()));
        if (!dataFile.exists()) {
            return null;
        }
        return dataFile.listFiles();
    }

    public static File getExternalFileByKey(Context context, String key) {
        File dataDir = context.getExternalFilesDir("file");
        return new File(String.format("%s/%s", dataDir.getAbsolutePath(), key));
    }
}
