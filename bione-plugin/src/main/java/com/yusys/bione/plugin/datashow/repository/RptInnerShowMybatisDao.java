package com.yusys.bione.plugin.datashow.repository;

import java.util.List;
import java.util.Map;

import com.yusys.bione.comp.repository.mybatis.MyBatisRepository;

/**
 * <pre>
 * Title: RptInnerShowMybatisDao
 * Description: 
 * </pre>
 * 
 * @author weijx weijx@yuchengtech.com
 * @version 1.00.00
 */

@MyBatisRepository
public interface RptInnerShowMybatisDao {
	/**
	 * 获取报表的定义来源信息
	 * @param map
	 * @return
	 */
	public List<String> getRptDefSrc(Map<String, Object> map);
	
	/**
	 * 获取自定义报表的参数模板Id
	 * @param map
	 * @return
	 */
	public String getRptParamInfo(Map<String, Object> map);
	
	public List<String> getCheckOrgInfo(Map<String, Object> map);
	
	public List<String> getRptType(Map<String, Object> map);
}
