package com.yusys.biapp.input.index.repository;

import java.util.List;
import java.util.Map;

import com.yusys.biapp.input.index.entity.RptInputCatalog;
import com.yusys.biapp.input.index.entity.RptInputIdxCfgDetail;
import com.yusys.biapp.input.index.entity.RptInputIdxCfgOrg;
import com.yusys.biapp.input.index.entity.RptInputIdxCfgValidate;
import com.yusys.biapp.input.index.entity.RptInputIdxDimFilter;
import com.yusys.biapp.input.index.entity.RptInputIdxTemplate;
import com.yusys.biapp.input.index.entity.RptInputIdxUpdlog;
import com.yusys.biapp.input.index.entity.RptInputIdxValidate;
import com.yusys.biapp.input.index.util.vo.DsInfoVO;
import com.yusys.biapp.input.index.web.vo.DataInputTemplateVO;
import com.yusys.biapp.input.index.web.vo.IdxCfgDetailVO;
import com.yusys.bione.comp.repository.mybatis.MyBatisRepository;
import com.yusys.bione.plugin.rptdim.entity.RptDimItemInfo;


@MyBatisRepository
public interface IdxDataInputDao  {
	
	/**
	 * 创建目录
	 * @param catalog
	 */
	public void saveDataInputCatalog(RptInputCatalog catalog);
	
	/**
	 * 更新目录
	 * @param catalog
	 */
	public void updateDataInputCatalog(RptInputCatalog catalog);	
	
	/**
	 * 删除目录
	 * @param catalog
	 */
	public void deleteDataInputCatalog(List<String> catalogList);
	
	/**
	 * 得到所有的目录信息
	 * @return
	 */
	public List<RptInputCatalog>getDataInputCatalog(Map<String, Object> map);
	
	/**
	 * 根据条件查询目录下的任务信息
	 * @param catalogId
	 * @return
	 */
	public List<RptInputIdxTemplate>getTemplateInfos(Map<String,Object>map);
	
	
	
	public String getSysNo(Map<String,Object> params);
	/**
	 * 根据条件查询目录下的任务信息
	 * @param catalogId
	 * @return
	 */
	public List<DataInputTemplateVO>getShowTemplateInfos(Map<String,Object>map);
	
	/**
	 * 通过指标获取对应的维度关系
	 * @param indexNo
	 */
	public List<Map<String,String>> getRptIdxDimRelDefaultMaxVersion(String indexNo);
	/**
	 * 获取
	 * @param map
	 * @return
	 */
	public  List<RptDimItemInfo>getDataInputDim(Map<String,Object>map);
	
	
	/**
	 * 指标补录配置明细
	 * @param templateId
	 * @return
	 */
	public List<IdxCfgDetailVO>getRptInputIdxCfgDetailByTemplateId(String templateId);
	
	/**
	 * 指标补录配置维度过滤
	 * @param templateId
	 * @return
	 */
	public List<RptInputIdxDimFilter> getRptInputIdxDimFilterByCfgId(String templateId);
	
	/**
	 * 查询指标补录配置机构
	 * @param cfgId
	 * @return
	 */
	public List<RptInputIdxCfgOrg>getRptInputIdxCfgOrgByCfgId(String cfgId);
	
	/**
	 * 检测目录名称是否可以创建
	 * @return
	 */
	public Integer testSameIndexCatalogNm(Map<String, Object> map);
	/**
	 * 检测指标补录模板名称是否可以创建
	 * @return
	 */
	public Integer testTemplateNm(Map<String, Object> map);
	
	/**
	 * 得到目录下的模板数
	 * @param params
	 * @return
	 */
	public Integer getTemplateCount(Map<String, Object> params);
	
	/**
	 * 保存指标补录模板配置
	 */
	public void saveRptInputIdxTemplate(RptInputIdxTemplate template);
	
	/**
	 * 更新指标补录模板配置
	 */
	public void updateRptInputIdxTemplate(RptInputIdxTemplate template);
	/**
	 * 删除指标下的模板信息
	 * @param catalog
	 */
	public void deleteRptInputIdxCfgDetailByTemplateId(String templateId);
	
	/**
	 * 删除指标补录配置机构
	 * @param templateId
	 */
	public void deleteRptInputIdxCfgOrgByTemplateId(String templateId);
	/**
	 * 删除模板
	 * @param templateId
	 */
	public void deleteRptInputIdxTemplateByTemplateId(String templateId);
	
	/**
	 * 删除指标补录配置维度过滤信息
	 * @param templateId
	 */
	public void deleteRptInputIdxDimFilterByTemplateId(String templateId);
	/**
	 * 保存指标补录配置明细
	 */
	public void saveRptInputIdxCfgDetail(RptInputIdxCfgDetail detail);
	
	/**
	 * 保存指标补录配置校验规则关系
	 * @param val
	 */
	public void saveRptInputIdxCfgValidate(RptInputIdxCfgValidate val);
	
	/**
	 * 保存指标补录配置机构
	 * @param cfgOrg
	 */
	public void saveRptInputIdxCfgOrg(RptInputIdxCfgOrg cfgOrg);
	/**
	 * 保存指标补录配置维度过滤
	 * @param filter
	 */
	public void saveRptInputIdxDimFilter(RptInputIdxDimFilter filter);
	
	/**
	 * 保存任务实例下的补录临时表
	 * @param params
	 */
	public void saveRptInputIdxTempResult(Map<String, Object> params);
	/**
	 * 保存任务实例下的补录计划临时表
	 * @param params
	 */
	public void saveRptInputIdxPlanTempResult(Map<String, Object> params);
	/**
	 * 保存任务实例下的补录临时表
	 * @param params
	 */
	public List<Map<String,Object>> getRptInputIdxTempResult(Map<String,Object>params);
	/**
	 * 保存任务实例下的补录计划临时表
	 * @param params
	 */
	public List<Map<String,Object>> getRptInputIdxPlanTempResult(Map<String,Object>params);
	/**
	 * 删除任务实例下的补录计划临时表
	 * @param taskInstanceId
	 */
	public void deleteRptInputIdxPlanTempResultByTaskInstanceId(String taskInstanceId);
	
	/**
	 * 删除任务实例下的补录临时表
	 * @param taskInstanceId
	 */
	public void deleteRptInputIdxTempResultByTaskInstanceId(String taskInstanceId);
	/**
	 * 查询币种名称
	 * @param taskInstanceId
	 */
	public String queryCurNm(String curNo);
	
	/**
	 * 通过任务信息查询指标补录临时结果表
	 * @param taskInstanceId
	 * @return
	 */
	public List<Map<String,Object>> getRptInputIdxTempResultByTaskInstanceId(String taskInstanceId);
	
	/**
	 * 通过任务信息查询指标补录计划临时结果表
	 * @param taskInstanceId
	 * @return
	 */
	public List<Map<String,Object>> getRptInputIdxPlanTempResultByTaskInstanceId(String taskInstanceId);
	
	/**
	 * 判断指标结果是否存在记录
	 * @param params
	 * @return
	 */
	public Integer getRptIdxResultByParam(Map<String,Object>params);
	
	/**
	 * 判断指标计划结果是否存在记录
	 * @param params
	 * @return
	 */
	public Integer getRptIdxPlanResultByParam(Map<String,Object>params);
	/**
	 * 保存指标结果
	 * @param params
	 */
	//public void saveRptIdxResult(RptIdxResult result);

	/**
	 * 更新指标结果
	 * @param params
	 */
	//public void updateRptIdxResult(RptIdxResult result);
	
	/**
	 * 保存指标计划结果
	 * @param params
	 */
	//public void saveRptIdxPlanResult(RptIdxPlanResult result);

	/**
	 * 更新指标计划结果
	 * @param params
	 */
	//public void updateRptIdxPlanResult(RptIdxPlanResult result);
	
	/**
	 * 保存指标补录更新日志表
	 * @param log
	 */
	public void saveRptInputIdxUpdlog(RptInputIdxUpdlog log);

	/**
	 * 更新指标补录更新日志表
	 * @param log
	 */
	public void updateRptInputIdxUpdlog(RptInputIdxUpdlog log);
	/**
	 * 查询符合条件的日志数量
	 * @param params
	 * @return
	 */
	public Integer getRptInputIdxUpdlogCnt(Map<String,Object>params);
	/**
	 * 得到关联的任务数
	 * @param templateId
	 * @return
	 */
	public int getRalatedTaskCount(String templateId);
	
	/**
	 * 根据指标查询规则
	 * @param indexNo
	 * @return
	 */
	public List<RptInputIdxValidate> getRuleByIndexNo(Map<String,Object>params);
	
	/**
	 * 查询配置下的规则信息
	 * @param cfgId
	 * @return
	 */
	public RptInputIdxValidate getRuleByCfg(String cfgId);
	
	
	public DsInfoVO getDataSourceById(String dsId);
	/**
	 * 保存规则信息
	 * @param rptInputIdxValidate
	 */
	public void saveRptInputIdxValidate(RptInputIdxValidate rptInputIdxValidate);
	
	/**
	 * 更新规则信息
	 * @param rptInputIdxValidate
	 */
	public void updateRptInputIdxValidate(RptInputIdxValidate rptInputIdxValidate);
	
	/**
	 * 更新规则信息
	 * @param rptInputIdxValidate
	 */
	public void deleteRptInputIdxValidate(String ruleId);
	
}
