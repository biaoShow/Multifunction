package com.example.biao.multifunction.util;

/**
 * Created by benxiang on 2019/4/1.
 */

public interface OnChooseLetterChangedListener {
    /**
     * 滑动时
     *
     * @param s
     */
    void onChooseLetter(String s);

    /**
     * 手指离开
     */
    void onNoChooseLetter();
}
