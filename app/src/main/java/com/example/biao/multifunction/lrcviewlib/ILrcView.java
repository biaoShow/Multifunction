package com.example.biao.multifunction.lrcviewlib;


import java.util.List;

public interface ILrcView {

    void setLrcData(List<LrcRow> lrcRows);//设置lrc数据

    void setLrcViewSeekListener(ILrcViewSeekListener seekListener);//滑动监听

    void setLrcViewMessage(String messageText);//设置无数据显示文字

    void smoothScrollToTime(long time,boolean isResume);//滑动到指定时间位置

}
