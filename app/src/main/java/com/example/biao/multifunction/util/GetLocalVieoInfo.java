package com.example.biao.multifunction.util;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
import android.provider.MediaStore;

import com.example.biao.multifunction.model.VideoInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * 获取本地视屏工具类
 * Created by ZeQiang Fang on 2018/6/8.
 */

public class GetLocalVieoInfo {

    public static List<VideoInfo> getVideoFromSDCard(Context context) {
        List<VideoInfo> list = new ArrayList<>();
        String[] projection = new String[]{MediaStore.Video.Media.DATA, MediaStore.Video.Media.DURATION,
                MediaStore.Video.Media.DISPLAY_NAME, MediaStore.Video.Thumbnails.DATA};
        Cursor cursor = context.getContentResolver().query(MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
                projection, null, null, null);
        assert cursor != null;
        while (cursor.moveToNext()) {
            VideoInfo videoInfo = new VideoInfo();
            videoInfo.setPath(cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DATA)));
            videoInfo.setTime(cursor.getInt(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DURATION)));
            videoInfo.setName(cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DISPLAY_NAME)));
            videoInfo.setImagePath(cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Video.Thumbnails.DATA)));
            list.add(videoInfo);
        }
        cursor.close();
        return list;
    }

    /**
     * 获取视频缩略图
     *
     * @param filePath 缩略图地址
     * @return 返回bitmap图片
     */
    private static MediaMetadataRetriever retriever = new MediaMetadataRetriever();

    public static Bitmap getVideoThumbnail(String filePath) {
        Bitmap b = null;
        try {
            retriever.setDataSource(filePath);
            b = retriever.getFrameAtTime();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (RuntimeException e) {
            e.printStackTrace();

        } finally {
            try {
                retriever.release();
            } catch (RuntimeException e) {
                e.printStackTrace();
            }
        }
        return b;
    }

}
