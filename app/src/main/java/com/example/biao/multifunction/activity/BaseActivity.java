package com.example.biao.multifunction.activity;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.example.biao.multifunction.R;
import com.example.biao.multifunction.util.ActivityCollecter;

/**
 * 基础类
 * Created by biao on 2018/5/15.
 */

public class BaseActivity extends AppCompatActivity {

    private Dialog loadingDialog = null;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityCollecter.addActivity(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ActivityCollecter.removeActivity(this);
    }

    /**
     * 显示loadingDialog
     */
    public void showLoadingDialog() {
        creatLoadingDialog().show();
    }

    /**
     * 隐藏loadingDialog
     */
    public void hideLoadingDialog() {
        if (loadingDialog != null) {
            loadingDialog.dismiss();
        }
    }

    /**
     * 创建loadingdialog
     *
     * @return
     */
    private Dialog creatLoadingDialog() {
        if (null == loadingDialog) {
            loadingDialog = new Dialog(this, R.style.loadingDialog);
            loadingDialog.setCanceledOnTouchOutside(false);
            loadingDialog.setContentView(R.layout.loading_dialog);
        }
        return loadingDialog;
    }
}
