package com.yusys.biapp.input.dict.repository;

import java.util.List;
import java.util.Map;

import com.yusys.biapp.input.dict.entity.RptInputListDataDictInfo;
import com.yusys.bione.comp.repository.mybatis.MyBatisRepository;

@MyBatisRepository
public interface RptInputListDataDictInfoDao {
	/**
	 * 根据数据字典Id，删除数据字典
	 * @param dictId 数据字典ID
	 */
	public void deleteDictInfoById(String templateId);
	/**
	 * 向数据字典表中插入数据字典信息
	 * @param dictInfo 数据字典信息
	 */
	public void saveDataInputDict(RptInputListDataDictInfo dictInfo);
	/**
	 * 获取数据字典表中的该用户有权限的数据字典的信息
	 * @return 数据字典的信息
	 */
	public List<RptInputListDataDictInfo> getDictDataByIds(List<String> dictIds);
	
	/**
	 * 获取数据字典表中的数据字典的信息
	 * @return 数据字典的信息
	 */
	public void deleteDictResRel(Map<String, String> params);
}
