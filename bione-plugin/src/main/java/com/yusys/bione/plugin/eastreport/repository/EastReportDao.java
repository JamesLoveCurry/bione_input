package com.yusys.bione.plugin.eastreport.repository;

import com.yusys.bione.comp.repository.mybatis.MyBatisRepository;
import com.yusys.bione.plugin.eastreport.entity.EastReport;

import java.util.List;
import java.util.Map;

/**
 * @项目名称：金融基础报表权限dao层
 * @类名称： EastReportDao
 * @类描述:
 * @功能描述:
 * @创建人: miaokx@yusys.com.cn
 * @创建时间: 2021年03月24日 14:35
 * @修改备注:
 * @修改记录: 修改时间  修改人员  修改原因
 * ---------------------------------------------------------
 * @Version 1.0.0
 * @Copyright (c) 宇信科技-版权所有
 */
@MyBatisRepository
public interface EastReportDao {
    public List<EastReport> getAllEastReport(Map<String, Object> param);
}
