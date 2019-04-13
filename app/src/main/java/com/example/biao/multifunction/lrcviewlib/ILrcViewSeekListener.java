package com.example.biao.multifunction.lrcviewlib;

/**
 * created by ： bifan-wei
 *  歌词拖动监听
 */

public interface ILrcViewSeekListener {
    void onSeek(LrcRow currentLrcRow, long CurrentSelectedRowTime);
}
