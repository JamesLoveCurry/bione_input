package com.yusys.bione.plugin.imasreport.repository;

import com.yusys.bione.comp.repository.mybatis.MyBatisRepository;
import com.yusys.bione.plugin.imasreport.entity.ImasReport;

import java.util.List;
import java.util.Map;

/**
 * @项目名称：利率报备报表权限dao层
 * @类名称： ImasReportDao
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
public interface ImasReportDao {
    public List<ImasReport> getAllImasReport(Map<String, Object> param);
}
