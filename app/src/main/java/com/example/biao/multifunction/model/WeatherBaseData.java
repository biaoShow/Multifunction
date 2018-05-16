package com.example.biao.multifunction.model;

/**
 * 获取now天气json数据实体类
 * Created by biao on 2018/5/16.
 */

public class WeatherBaseData {
    private String basic;
    private String update;
    private String status;
    private String now;

    public String getBasic() {
        return basic;
    }

    public void setBasic(String basic) {
        this.basic = basic;
    }

    public String getUpdate() {
        return update;
    }

    public void setUpdate(String update) {
        this.update = update;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getNow() {
        return now;
    }

    public void setNow(String now) {
        this.now = now;
    }
}
