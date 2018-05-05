package com.example.biao.multifunction.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.TextView;

import com.example.biao.multifunction.R;
import com.example.biao.multifunction.model.Song;
import com.example.biao.multifunction.util.MusicUtils;
import java.util.List;

/**
 * 音乐详情dialog
 * Created by biao on 2018/5/5.
 */

public class MusicCodeDialog extends Dialog{

    private List<Song> list;
    private Context context;
    private int position;
    private TextView tv_dialog_song,tv_dialog_singer,tv_dialog_duration,
            tv_dialog_size,tv_dialog_path,tv_dialog_close;

    public MusicCodeDialog(@NonNull Context context,int position) {
        super(context);
        this.context = context;
        this.position = position;
        this.list = MusicUtils.list;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.music_item_dialog);

        //初始化控件
        tv_dialog_song = findViewById(R.id.tv_dialog_song);
        tv_dialog_singer = findViewById(R.id.tv_dialog_singer);
        tv_dialog_duration = findViewById(R.id.tv_dialog_duration);
        tv_dialog_size = findViewById(R.id.tv_dialog_size);
        tv_dialog_path = findViewById(R.id.tv_dialog_path);
        tv_dialog_close = findViewById(R.id.tv_dialog_close);

        setCodeItem(position);//设置控件参数

        tv_dialog_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

    }

    /**
     * 设置控件参数
     * @param position
     */

    private void setCodeItem(int position) {
        tv_dialog_song.setText(list.get(position).getSong());
        tv_dialog_singer.setText(list.get(position).getSinger());
        tv_dialog_duration.setText(MusicUtils.formatTime(list.get(position).getDuration()));
        tv_dialog_size.setText(MusicUtils.formatSize(list.get(position).getSize()));
        tv_dialog_path.setText(MusicUtils.fromatPath(list.get(position).getPath()));
    }
}
