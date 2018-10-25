package com.yuan7.lockscreen.constant;

/**
 * Created by Administrator on 2017/8/11.
 */

public class DBConstant {
    //表名
    public static final String USER_TABLE_NAME = "users_table";//用户表
    public static final String LABEL_TABLE_NAME = "label_table";

    //SQL语句
    public static final String FIND_USER_ALL = "SELECT * FROM " + USER_TABLE_NAME;//查询所有用户表数据
    public static final String FIND_LABEL_BY_TYPE_ALL = "SELECT * FROM " + LABEL_TABLE_NAME + " WHERE type = :type LIMIT 15 OFFSET :offset";
    public static final String FIND_LABEL_BY_HIAPKID = "SELECT * FROM " + LABEL_TABLE_NAME + " WHERE hiapkId = :hiapkId";
    public static final String FIND_LABEL_BY_HIAPKID_COUNT = "SELECT COUNT(*) FROM " + LABEL_TABLE_NAME + " WHERE hiapkId = :hiapkId";

}
