package com.yusys.bione.plugin.drivercfg.utils;

import com.yusys.bione.comp.utils.PropertiesUtils;

import java.util.Arrays;

/**
 * @Author: WM
 * @Date: 2021\5\8 0008 11:43
 * @Description: 数据库管理工具类
 */
public class DriverCfgUtils {



    /**
     * 获取当前数据库类型
     * @return
     */
    public static String getDriverType() {
        PropertiesUtils pUtils = PropertiesUtils.get("database.properties");
        String dialectType = pUtils.getProperty("database.type");
        return dialectType;
    }

    /**
     * 获取mysql关键字集合，当关键字作为字段时，需要加上`字段`才能实现新增，修改等操作
     * @return
     */
    public static String[] getMysqlKeyword() {
        String[] str = {"status","explain"};
        return str;
    }

    public static void main(String[] args) {
        System.out.println(Arrays.asList(getMysqlKeyword()).contains("explain2"));
    }
}
