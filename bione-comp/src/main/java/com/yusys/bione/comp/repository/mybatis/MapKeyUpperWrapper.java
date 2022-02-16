package com.yusys.bione.comp.repository.mybatis;

import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.reflection.wrapper.MapWrapper;

import java.util.Map;

/**
 * @项目名称： 统一监管报送
 * @类名称： MapKeyUpperWrapper
 * @类描述: mybatis中返回map 将key值转为大写
 * @功能描述:
 * @创建人: huzq1
 * @创建时间: 2021/08/18 14:04
 * @修改备注:
 * @修改记录: 修改时间  修改人员  修改原因
 * ---------------------------------------------------------
 * @Version 1.0.0
 * @Copyright (c) 宇信科技-版权所有
 */
public class MapKeyUpperWrapper extends MapWrapper {

    public MapKeyUpperWrapper(MetaObject metaObject, Map<String, Object> map) {
        super(metaObject, map);
    }

    @Override
    public String findProperty(String name, boolean useCamelCaseMapping) {
        return name == null ? "" : name.toUpperCase();
    }
}
