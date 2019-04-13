package com.example.biao.multifunction.db.util;

import android.content.Context;

import com.litesuits.orm.LiteOrm;
import com.litesuits.orm.db.assit.QueryBuilder;

import java.util.List;

/**
 * Created by benxiang on 2019/4/12.
 */

public class DataBaseManager {

    private static DataBaseManager dataBaseManager = null;
    private LiteOrm liteOrm = null;
    private static final String DATABASE_NAME = "weather.db";

    private DataBaseManager(Context context) {
        if (null == liteOrm) {
            liteOrm = LiteOrm.newSingleInstance(context, DATABASE_NAME);
        }
    }

    public static DataBaseManager getInstance(Context context) {
        if (null == dataBaseManager) {
            synchronized (DataBaseManager.class) {
                dataBaseManager = new DataBaseManager(context);
            }
        }
        return dataBaseManager;
    }

    /**
     * 插入一条记录
     */
    public <T> long insert(T t) {
        return liteOrm.save(t);
    }

    /**
     * 插入所有记录
     */
    public <T> void insertAll(List<T> list) {
        liteOrm.save(list);
    }

    /**
     * 根据对象查询数据
     */
    public <T> int update(T t) {
        return liteOrm.update(t);
    }

    /**
     * 查询所有数据
     */
    public <T> int updateAll(List<T> list) {
        return liteOrm.update(list);
    }

    /**
     * 根据id查询
     */
    public <T> T query(long id, Class<T> clazz) {
        return liteOrm.queryById(id, clazz);
    }

    /**
     * 查询所有
     */
    public <T> List<T> queryAll(Class<T> cla) {
        return liteOrm.query(cla);
    }

    /**
     * 查询  某字段 等于 Value的值
     */
    @SuppressWarnings("unchecked")
    public <T> List<T> queryByWhere(Class<T> cla, String field, String[] value) {
        return liteOrm.query(new QueryBuilder(cla).where(field + "=?", (Object[]) value));
    }

    /**
     * 查询  某字段 等于 Value的值  可以指定从1-20，就是分页
     */
    @SuppressWarnings("unchecked")
    public <T> List<T> queryByWhereLength(Class<T> cla, String field, String[] value, int start, int length) {
        return liteOrm.query(new QueryBuilder(cla).where(field + "=?", (Object[]) value).limit(start, length));
    }

    /**
     * 删除一个数据
     */
    public <T> void delete(T t) {
        liteOrm.delete(t);
    }

    /**
     * 删除所有数据
     */
    public <T> void deleteAll(Class<T> clazz) {
        liteOrm.deleteAll(clazz);
    }

    /**
     * 删除集合中的数据
     */
    public <T> void deleteList(List<T> list) {
        liteOrm.delete(list);
    }

    /**
     * 删除数据库
     */
    public void deleteDatabase() {
        liteOrm.deleteDatabase();
    }

}
