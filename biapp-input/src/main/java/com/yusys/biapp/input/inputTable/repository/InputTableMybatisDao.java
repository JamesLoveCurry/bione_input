package com.yusys.biapp.input.inputTable.repository;

import com.yusys.biapp.input.inputTable.entity.RptListDataloadDataInfo;
import com.yusys.biapp.input.inputTable.entity.RptListDataloadSqlInfo;
import com.yusys.biapp.input.inputTable.entity.RptListDataloadType;
import com.yusys.biapp.input.inputTable.vo.InputTableVO;
import com.yusys.biapp.input.task.vo.DataLoadInfoVO;
import com.yusys.biapp.input.template.entity.RptInputRewriteFieldInfo;
import com.yusys.biapp.input.template.entity.RptInputRewriteTempleInfo;
import com.yusys.bione.comp.repository.mybatis.MyBatisRepository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@MyBatisRepository
public interface InputTableMybatisDao {

	public List<InputTableVO> getTableInfoById(Map<String,Object> map);
	
	/**
	 * 根据模板ID查询回写表信息
	 * @param templeId
	 * @return
	 */
	public RptInputRewriteTempleInfo getRptInputRewriteTempleInfoByTempId(String templeId);
	
	/**
	 * 通过模板ID得到补录表信息
	 * @param templeId
	 * @return
	 */
	public HashMap<String,Object> getRptInputListTableInfoByTempId(String templeId);
	
	/**
	 * 根据模板ID查询回写字段ID
	 * @param templeId
	 * @return
	 */
	public List<RptInputRewriteFieldInfo>getRptInputRewriteFieldInfo(String templeId);
	
	
	/**
	 * 通过模型ID查询对应的表信息
	 * @param templeId
	 * @return
	 */
	public List<Map<String,Object>>getTableInfoByTempleId(String templeId);
	
	
	/**
	 * 通过补录表ID查询配置ID
	 * @param tableId
	 * @return
	 */
	public RptListDataloadType getRptListDataloadTypeByTableId(String tableId);
	

	
	/**
	 * 通过补录表ID查询配置ID
	 * @param tableId
	 * @return
	 */
	public DataLoadInfoVO getRptListDataloadTypeByTempId(String tempId);
	
	/**
	 * 保存自定义类型预增数据
	 * @param rptListDataloadDataInfo
	 * @return
	 */
	public void saveRptListDataloadDataInfo(RptListDataloadDataInfo rptListDataloadDataInfo);

	
	/**
	 * 保存sql类型预增数据
	 * @param rptListDataloadDataInfo
	 * @return
	 */
	public void saveRptListDataloadSqlInfo(RptListDataloadSqlInfo rptListDataloadSqlInfo);
	
	/**
	 * 保存预增数据类型
	 * @param rptListDataloadType
	 * @return
	 */
	public void saveRptListDataloadType(RptListDataloadType rptListDataloadType);
	
	
	/**
	 * 更新预增数据类型
	 * @param rptListDataloadType
	 * @return
	 */
	public void updateRptListDataloadType(RptListDataloadType rptListDataloadType);
	
	/**
	 * 更新预增数据中的sql设置
	 * @param rptListDataloadTypeSql
	 * @return
	 */
	public void updateRptListDataloadSqlInfo(RptListDataloadSqlInfo rptListDataloadTypeSql);
	
	/**
	 * 更新预增数据中的自定义数据设置
	 * @param rptListDataloadDataInfo
	 */
	public void updateRptListDataloadDataInfo(RptListDataloadDataInfo rptListDataloadDataInfo);
	
	/**
	 * 通过配置ID查询自定义类型预增数据
	 * @param cfgId
	 * @return
	 */
	public RptListDataloadDataInfo getRptListDataloadDataInfoById(String cfgId);
	
	/**
	 * 通过配置ID查询自定义类型预增数据
	 * @param cfgId
	 * @return
	 */
	public RptListDataloadSqlInfo getRptListDataloadSqlInfoById(String cfgId);
	public List<RptListDataloadSqlInfo> getRptListDataloadSqlInfoByTaskId(String taskId);
	
	/**
     * 通过配置ID查询自定义类型预增数据
     * @param cfgId
     * @return
     */
	public HashMap<String,Object> getRptListDataloadSqlInfoVOById(String cfgId);
	
	/**
	 * 删除预增sql信息
	 * @param params
	 */
	public void deleteRptListDataloadSqlInfo(Map<String,Object>params);
	
	/**
	 * 删除预增数据信息
	 * @param params
	 */
	public void deleteRptListDataloadDataInfo(Map<String,Object>params);

	List<Map<String, Object>> queryInputTable(Map<String,Object>params);
}
