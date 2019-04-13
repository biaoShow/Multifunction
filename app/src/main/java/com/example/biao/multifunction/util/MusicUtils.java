package com.example.biao.multifunction.util;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.ArrayMap;

import com.example.biao.multifunction.R;
import com.example.biao.multifunction.model.Song;

import net.sourceforge.pinyin4j.PinyinHelper;

import java.text.Collator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * 音乐工具类
 * 扫描系统里面的音频文件，返回一个list集合
 */

public class MusicUtils {
    private static final List<Song> list = new ArrayList<>();
    private static Map<String, Integer> letterMap = new ArrayMap<>();
    private static Context utilContext;

    public static List<Song> getMusicData(Context context) {
        utilContext = context;
        synchronized (list) {
            if (list.size() <= 0) {
                // 媒体库查询语句（写一个工具类MusicUtils）
                Cursor cursor = context.getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, null, null,
                        null, MediaStore.Audio.AudioColumns.IS_MUSIC);
                if (cursor != null) {
                    while (cursor.moveToNext()) {
                        Song song = new Song();
                        String singer = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST));
                        if (singer.contains("<unknown>")) {
                            singer = "未知";
                        }
                        song.setSong(cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DISPLAY_NAME)));
                        song.setSinger(singer);
                        song.setPath(cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA)));
                        song.setDuration(cursor.getInt(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION)));
                        song.setSize(cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.SIZE)));
                        //获取专辑ID
                        int albumId = cursor.getInt(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM_ID));
//                        //根据专辑ID获取到专辑封面图
                        song.setAlbumID(albumId);
                        if (song.getSize() > 1000 * 800) {
                            // 注释部分是切割标题，分离出歌曲名和歌手 （本地媒体库读取的歌曲信息不规范）
                            if (song.getSong().contains("-")) {
                                String[] str = song.getSong().split("-");
                                if (str[1].trim().contains(".")) {
                                    song.setSong(str[1].trim().substring(0, str[1].indexOf(".") - 1));
                                } else {
                                    song.setSong(str[1].trim());
                                }
                                song.setSinger(str[0]);

                            }
                            list.add(song);
                        }
                    }
                    // 释放资源
                    cursor.close();
                }
            }
            Collections.sort(list, new Comparator<Song>() {
                @Override
                public int compare(Song o1, Song o2) {
                    //获取中文环境
                    Comparator<Object> com = Collator.getInstance();
                    return com.compare(convertToHanYuPinYinString(o1.getSinger()), convertToHanYuPinYinString(o2.getSinger()));
                }
            });
            return list;
        }
    }

    public static Map<String, Integer> getLetterMap(Context context) {
        if (letterMap.size() <= 0) {
            synchronized (list) {
                String pinyin = "";
                String letter = "";
                String temporary = "";
                if (list.size() <= 0) {
                    getMusicData(context);
                }
                for (int i = 0; i < list.size(); i++) {
                    pinyin = convertToHanYuPinYinString(list.get(i).getSinger());
                    if (pinyin.length() > 0) {
                        letter = pinyin.substring(0, 1).toUpperCase();
                        if (!temporary.equals(letter)) {
                            temporary = letter;
                            switch (letter) {
                                case "A":
                                    letterMap.put("A", i);
                                    break;
                                case "B":
                                    letterMap.put("B", i);
                                    break;
                                case "C":
                                    letterMap.put("C", i);
                                    break;
                                case "D":
                                    letterMap.put("D", i);
                                    break;
                                case "E":
                                    letterMap.put("E", i);
                                    break;
                                case "F":
                                    letterMap.put("F", i);
                                    break;
                                case "G":
                                    letterMap.put("G", i);
                                    break;
                                case "H":
                                    letterMap.put("H", i);
                                    break;
                                case "I":
                                    letterMap.put("I", i);
                                    break;
                                case "J":
                                    letterMap.put("J", i);
                                    break;
                                case "K":
                                    letterMap.put("K", i);
                                    break;
                                case "L":
                                    letterMap.put("L", i);
                                    break;
                                case "M":
                                    letterMap.put("M", i);
                                    break;
                                case "N":
                                    letterMap.put("N", i);
                                    break;
                                case "O":
                                    letterMap.put("O", i);
                                    break;
                                case "P":
                                    letterMap.put("P", i);
                                    break;
                                case "Q":
                                    letterMap.put("Q", i);
                                    break;
                                case "R":
                                    letterMap.put("R", i);
                                    break;
                                case "S":
                                    letterMap.put("S", i);
                                    break;
                                case "T":
                                    letterMap.put("T", i);
                                    break;
                                case "U":
                                    letterMap.put("U", i);
                                    break;
                                case "V":
                                    letterMap.put("V", i);
                                    break;
                                case "W":
                                    letterMap.put("W", i);
                                    break;
                                case "X":
                                    letterMap.put("X", i);
                                    break;
                                case "Y":
                                    letterMap.put("Y", i);
                                    break;
                                case "Z":
                                    letterMap.put("Z", i);
                                    break;
                                default:
                                    letterMap.put("#", i);
                                    break;
                            }
                        }
                    }
                }
            }
        }
        return letterMap;
    }

    /**
     * 中文转拼音
     *
     * @param str
     * @return
     */
    private static String convertToHanYuPinYinString(String str) {
        StringBuilder sb = new StringBuilder();
        String[] arr = null;
        for (int i = 0; i < str.length(); i++) {
            arr = PinyinHelper.toHanyuPinyinStringArray(str.charAt(i));
            if (arr != null && arr.length > 0) {
                for (String string : arr) {
                    sb.append(string);
                }
            } else {
                sb.append(str.charAt(i));
            }
        }
        return sb.toString();
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

    /**
     * 定义一个方法用来格式化获取到的歌曲大小
     *
     * @param size 歌曲的大小
     * @return 返回格式化后的字符
     */
    public static String formatSize(long size) {
        float i = size / (float) (1024 * 1024);
        return String.valueOf((float) (Math.round(i * 100)) / 100);
    }

    //截取歌曲路径
    public static String getFilePath(String pathandname) {

        int end = pathandname.lastIndexOf("/");
        if (end != -1) {
            return pathandname.substring(0, end);
        } else {
            return null;
        }
    }

    /**
     * 根据专辑ID获取专辑封面图
     *
     * @param album_id 专辑ID
     * @return
     */
    public static Bitmap getAlbumArt(int album_id) {
        String mUriAlbums = "content://media/external/audio/albums";
        String[] projection = new String[]{"album_art"};
        Cursor cur = utilContext.getContentResolver().query(Uri.parse(mUriAlbums + "/" + Integer.toString(album_id)), projection, null, null, null);
        String album_art = null;
        if (cur.getCount() > 0 && cur.getColumnCount() > 0) {
            cur.moveToNext();
            album_art = cur.getString(0);
        }
        cur.close();
        Bitmap bm = null;
        if (album_art != null) {
            bm = BitmapFactory.decodeFile(album_art);
        } else {
            bm = BitmapFactory.decodeResource(utilContext.getResources(), R.mipmap.music_logo);
        }
        return bm;
    }

}
