package com.example.biao.multifunction.util;

import android.content.Context;
import android.database.Cursor;
import android.provider.MediaStore;

import com.example.biao.multifunction.model.Song;

import java.util.ArrayList;
import java.util.List;

/**
 * 音乐工具类,
 * 扫描系统里面的音频文件，返回一个list集合
 */

public class MusicUtils {

    public static List<Song> list = new ArrayList<Song>();
    public static List<Song> getMusicData(Context context) {
        // 媒体库查询语句（写一个工具类MusicUtils）
        Cursor cursor = context.getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, null, null, null, MediaStore.Audio.AudioColumns.IS_MUSIC);
        if (cursor != null) {
            while (cursor.moveToNext()) {
                Song song = new Song();
                song.setSong(cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DISPLAY_NAME)));
                song.setSinger(cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST)));
                song.setPath(cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA)));
                song.setDuration(cursor.getInt(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION)));
                song.setSize(cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.SIZE)));
                if (song.getSize() > 1000 * 800) {
                    // 注释部分是切割标题，分离出歌曲名和歌手 （本地媒体库读取的歌曲信息不规范）
                    if (song.getSong().contains("-")) {
                        String[] str = song.getSong().split("-");
                        song.setSinger(str[0]);
                        song.setSong(str[1]);
                    }
                    list.add(song);
                }
            }
            // 释放资源
            cursor.close();
        }

        return list;
    }

    /**
     * 定义一个方法用来格式化获取到的时间
     */
    public static String formatTime(int time) {
        if (time / 1000 % 60 < 10) {
            return time / 1000 / 60 + ":0" + time / 1000 % 60;

        } else {
            return time / 1000 / 60 + ":" + time / 1000 % 60;
        }

    }

    public static String formatSize(long size) {
        float i = size / (float) (1024 * 1024);
        String str = String.valueOf((float) (Math.round(i * 100)) / 100);
        return str;
    }

    //截取歌曲路径
    public static String fromatPath(String pathandname) {

        int end = pathandname.lastIndexOf("/");
        if (end != -1) {
            return pathandname.substring(0, end);
        } else {
            return null;
        }

    }

}
