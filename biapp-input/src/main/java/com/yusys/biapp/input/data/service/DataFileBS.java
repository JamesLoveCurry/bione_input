/**
 * 
 */
package com.yusys.biapp.input.data.service;

import com.google.common.collect.Lists;
import com.yusys.biapp.input.data.entity.RptInputListDataFile;
import com.yusys.biapp.input.dict.utils.DataUtils;
import com.yusys.biapp.input.dict.utils.UdipConstants;
import com.yusys.biapp.input.input.service.TaskCaseBS;
import com.yusys.biapp.input.logs.entity.RptInputLstValidateLog;
import com.yusys.biapp.input.logs.service.ValidateLogBS;
import com.yusys.biapp.input.rule.common.DataFormatValidate;
import com.yusys.biapp.input.task.entity.RptTskIns;
import com.yusys.biapp.input.template.entity.RptInputLstTempleConst;
import com.yusys.biapp.input.template.entity.RptInputLstTempleField;
import com.yusys.biapp.input.template.entity.RptInputLstTempleInfo;
import com.yusys.biapp.input.template.entity.RptInputLstTempleRule;
import com.yusys.biapp.input.utils.ExcelConstants;
import com.yusys.bione.comp.repository.jpa.SearchResult;
import com.yusys.bione.comp.service.BaseBS;
import com.yusys.bione.comp.utils.FormatUtils;
import com.yusys.bione.frame.mtool.service.DataSourceBS;
import com.yusys.bione.frame.security.BioneSecurityUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author ex_licd
 * 
 */
@Service(value = "dataFileBS")
@Transactional(readOnly = false)
public class DataFileBS extends BaseBS<RptInputListDataFile> {
	private final Log logger = LogFactory.getLog(DataFileBS.class);
	@Autowired
	private ValidateLogBS validateLogBS;
	@Autowired
	private DataFormatValidate dataFormatValidate;
	@Autowired
	private ExcelConstants excelConstants;
	@Autowired
	private DataUtils dataUtils;
	@Autowired
	private DataStateBS dataStateBS;
	@Autowired
	private AuthRecordBS authRecordBS;
	@Autowired
	private TaskCaseBS taskCaseBS;
	@Autowired
	private DataSourceBS dataSourceBS;

	public SearchResult<RptInputListDataFile> getDataFileList(int firstResult, int pageSize, String orderBy, String orderType,
			Map<String, Object> conditionMap, String id, String user, String caseId) {
		StringBuilder jql = new StringBuilder("");
		@SuppressWarnings("unchecked")
		Map<String, Object> values = (Map<String, Object>) conditionMap.get("params");
		if (StringUtils.isNotBlank(id)) {
			jql.append("select temple from RptInputListDataFile temple where temple.templeId=:templeId and temple.caseId=:caseId and temple.importMan=:importMan");
			values.put("templeId", id);
			values.put("caseId", caseId);
			values.put("importMan", user);
		} else {
			jql.append("select temple from RptInputListDataFile temple where 1=1");
		}
		SearchResult<RptInputListDataFile> authObjDefList = this.baseDAO.findPageWithNameParam(firstResult, 100000, jql.toString(), values);
		return authObjDefList;
	}

	private String checkAddData(List<Map<String, Object>> data,RptInputLstTempleInfo temp, List<String> keyList,String flowNodeId) throws Exception{
		
		List<String>msgs=null;
		if(keyList!=null&&!keyList.isEmpty()&&!(keyList.size()==1&&keyList.get(0).equalsIgnoreCase("SYS_ORDER_NO")))
			msgs=this.dataUtils.checkDateOfUpload( data, temp,  keyList,flowNodeId);
		if(msgs==null||msgs.isEmpty())
			return null;
		StringBuilder buff = new StringBuilder(1000);
		buff.append("主键值:");
		boolean isFirst=true;
		for(String key : keyList){
			if(key.equalsIgnoreCase("SYS_ORDER_NO"))
				continue;
			if(isFirst)
				isFirst=false;
			else
				buff.append(",");
			buff.append(key);
		}
		buff.append("为");
		isFirst=true;
		for(String msg:msgs){
			if(isFirst)
				isFirst=false;
			else
				buff.append(",");
			buff.append("[").append(msg).append("]");
		}
		buff.append("的数据已经被别的用户添加");
		return buff.toString();
	}
	
	/**
	 * 补录导入数据保存
	 * 
	 * @param file
	 * @param templeId
	 * @param caseId
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<String> saveDataFileById(File file, String templeId, String caseId,String taskFlowNodeId) {
		logger.debug("补录导入数据保存-------start-----0----saveDataFileById"+FormatUtils.format(System.currentTimeMillis(), "yyyy-MM-dd HH:mm:ss.SSS"));
		List<String> list = Lists.newArrayList();
		List<Map<String, Object>> dataAdd = Lists.newArrayList();
		List<Map<String, Object>> dataOld = Lists.newArrayList();
		List<Map<String, Object>> dataUdp = Lists.newArrayList();

		List<String> dataState = Lists.newArrayList();
		dataState.add(UdipConstants.TASK_STATE_DISPATCH);
		dataState.add(UdipConstants.TASK_STATE_SAVE);
		dataState.add(UdipConstants.TASK_STATE_VALIDATE);
		dataState.add(UdipConstants.TASK_STATE_REFUSE);
		dataState.add(UdipConstants.TASK_STATE_SUBMIT);
		dataState.add(UdipConstants.TASK_STATE_SUCESS);
		dataState.add(UdipConstants.TASK_STATE_REJECT);
		
		// 统一获取信息
		Map<String, Object> getMap = this.taskCaseBS.getMap(templeId, caseId,false);
		
		RptInputLstTempleInfo temp = (RptInputLstTempleInfo) getMap.get("temp");

		Map<String, Object> mapList = this.excelConstants.getList(file, getMaxOrderNo(temp));

		try {
			if (mapList.isEmpty()) {
				list.add(list.size(), "无数据");
			} else if (mapList.get("typeWrrong") != null) {
				list.add(list.size(), "读取文件出错，请检查模板是否正确。");
			} else if (mapList.get("file") != null) {
				list.add(list.size(), "目标文件数据不存在，可能已上传，可导入同名文件进行更新。");
			} else if (mapList.get("wrrong") != null) {
				list.add(list.size(), "读取文件出错。");
			} else {
				if ("1".equals(temp.getAllowAdd())) {
					dataAdd = (List<Map<String, Object>>) mapList.get("addList");
				}
				/*if(dataAdd.size() > 0){
				    String checkInfo = checkAddData(dataAdd, temp,  (List<String>) getMap.get("keyCols"),taskFlowNodeId);
				    if(StringUtils.isNotEmpty(checkInfo)){
				        list.add("cannt");
				        list.add(list.size(), checkInfo);
				    }
				}*/
				if ("1".equals(temp.getAllowUpdate())) {
					dataUdp = (List<Map<String, Object>>) mapList.get("udpList");
				}
				if ("1".equals(temp.getAllowDelete())) {
					dataOld = (List<Map<String, Object>>) mapList.get("delList");
				}
			}
			if (!list.isEmpty()) {
				return list;
			}
			List<String> org = this.taskCaseBS.getOrgList((RptInputLstTempleInfo) getMap.get("temp"), null, caseId);
			List<RptInputLstValidateLog> listlog = Lists.newArrayList();
			List<RptInputLstValidateLog> listlog2 = Lists.newArrayList();
			// 日志实体
			RptInputLstValidateLog RptInputLstValidateLog = this.validateLogBS.getUdipValidateLog(caseId, "",
						(String) getMap.get("oper"), "", templeId, (String) getMap.get("data"));
			try {
				// 判断模板是否有主键
				if (((List<RptInputLstTempleConst>) getMap.get("keyList")).isEmpty()) {
					list.add("模板未创建主键，无法导入，请联系管理人员。");
					return list;
				}
				Map<String, Object> result = null;
				Boolean chack = false;
				if (CollectionUtils.isNotEmpty(dataAdd))
				{
					logger.debug("补录导入数据保存-----before----1----validateAddData"+FormatUtils.format(System.currentTimeMillis(), "yyyy-MM-dd HH:mm:ss.SSS"));
					String msg=dataUtils.validateAddData(dataAdd , (RptInputLstTempleInfo) getMap.get("temp"),  caseId, taskFlowNodeId,(List<RptInputLstTempleConst>) getMap.get("keyList"),(Map<String, RptInputLstTempleField>) getMap.get("colMap"));
					if(StringUtils.isNotEmpty(msg))
					{
						list.add("cannt");
						list.add(msg);
						return list;
					}
					logger.debug("补录导入数据保存-----before----2----listlog"+FormatUtils.format(System.currentTimeMillis(), "yyyy-MM-dd HH:mm:ss.SSS"));
					listlog = this.dataFormatValidate.validate(2,
							(RptTskIns) getMap.get("taskCase"), dataAdd,
							(RptInputLstTempleInfo) getMap.get("temp"),
							(Map<String, RptInputLstTempleField>) getMap.get("colMap"),
							(List<RptInputLstTempleConst>) getMap.get("keyList"),
							(List<RptInputLstTempleRule>) getMap.get("ruleList"),
							(Map<String, Map<String, Object>>) getMap.get("libList"),
							UdipConstants.DATA_FLAG_SAVE,taskFlowNodeId);
				}
				if (CollectionUtils.isNotEmpty(dataUdp)) {
					logger.debug("补录导入数据保存-----before----3----listlog2"+FormatUtils.format(System.currentTimeMillis(), "yyyy-MM-dd HH:mm:ss.SSS"));
					listlog2 = this.dataFormatValidate.validate(1,
							(RptTskIns) getMap.get("taskCase"), dataUdp,
							(RptInputLstTempleInfo) getMap.get("temp"),
							(Map<String, RptInputLstTempleField>) getMap.get("colMap"),
							(List<RptInputLstTempleConst>) getMap.get("keyList"),
							(List<RptInputLstTempleRule>) getMap.get("ruleList"),
							(Map<String, Map<String, Object>>) getMap.get("libList"),
							UdipConstants.DATA_FLAG_UPATE,taskFlowNodeId);
				}
				listlog.addAll(listlog2);
				logger.debug("补录导入数据保存-----before----4----deleteUdipValidateLog"+FormatUtils.format(System.currentTimeMillis(), "yyyy-MM-dd HH:mm:ss.SSS"));
				this.validateLogBS.deleteUdipValidateLog(caseId, templeId, RptInputLstValidateLog.getOrgNo());
				if (listlog.isEmpty()) {
					RptInputLstValidateLog log2 = new RptInputLstValidateLog();
					log2.setValidateResult("数据合法性校验成功");
					logger.debug("补录导入数据保存----5---数据合法性校验成功"+FormatUtils.format(System.currentTimeMillis(), "yyyy-MM-dd HH:mm:ss.SSS"));
					listlog.add(log2);
					result = this.dataUtils.deleteAndAddData(taskFlowNodeId,1, dataAdd,
							dataOld, dataUdp, (RptInputLstTempleInfo) getMap.get("temp"), (List<String>) getMap.get("keyCols"),
							(Map<String, RptInputLstTempleField>) getMap.get("colMap"),
							(Map<String, Map<String, Object>>) getMap.get("libList"),(String) getMap
									.get("casedata"), dataState, getMap.get("data"), getMap.get("oper"), caseId, org);
					chack = (boolean)result.get("chack");
				} else {
					list.add("数据合法性校验失败：");
					for (RptInputLstValidateLog log : listlog) {
						list.add(log.getValidateResult());
					}
				}
				if (chack) {
					if (dataAdd.size() + dataUdp.size() + dataOld.size() > 0) {
						logger.debug("补录导入数据保存----6---更改dataState状态"+FormatUtils.format(System.currentTimeMillis(), "yyyy-MM-dd HH:mm:ss.SSS"));
						// 更改dataState状态
						this.dataStateBS.changerDataState(templeId, caseId,
								org, BioneSecurityUtils.getCurrentUserInfo().getCurrentLogicSysNo(),
								UdipConstants.TASK_STATE_SAVE);
						// 新增一条数据操作记录
						this.authRecordBS.saveAuthRecord(templeId, caseId,
								UdipConstants.TASK_STATE_SAVE);
					}
					// 保存成功后删除服务器文件
					file.delete();
				} else {
					list.add("数据插入数据库失败");
				}
				logger.debug("补录导入数据保存-------before----7-----saveUdipValidateLog"+FormatUtils.format(System.currentTimeMillis(), "yyyy-MM-dd HH:mm:ss.SSS"));
				this.validateLogBS.saveUdipValidateLog(listlog, RptInputLstValidateLog);
				if (list.isEmpty()) {
					list.add("操作成功:");
					list.add("新增数据：" + dataAdd.size() + "条.");
					list.add("更新数据：" + dataUdp.size() + "条.");
					list.add("删除数据：" + (result == null ? "0" : result.get("deleteTotal").toString())  + "条.");
				}
				logger.debug("补录导入数据保存-------end----8-----saveDataFileById"+FormatUtils.format(System.currentTimeMillis(), "yyyy-MM-dd HH:mm:ss.SSS"));
				return list;
			} catch (Exception e) {
				e.printStackTrace();
				RptInputLstValidateLog log2 = new RptInputLstValidateLog();
				log2.setValidateResult("数据插入数据库失败：" + e.getMessage());
				listlog.add(log2);
				this.validateLogBS.saveUdipValidateLog(listlog, RptInputLstValidateLog);
				list.add(e.getMessage());
				return list;
			} finally {
				file.delete();
			}
			
			
		} catch (Exception e1) {
		    e1.printStackTrace();
			list.add(e1.getMessage());
			return list;
		}
	}
	
	private Long getMaxOrderNo(RptInputLstTempleInfo temp) {
		Connection conn = null;
		Statement stmt = null;
		ResultSet rs = null;
		try {
			conn = this.dataSourceBS.getConnection(temp.getDsId());
			stmt = conn.createStatement();
			String sql = "select max(" + UdipConstants.TAB_ORDER_NO + ") from " + temp.getTableEnName();
			rs = stmt.executeQuery(sql);
			Long orderNo = 0l;
			while (rs.next()) {
				orderNo = rs.getLong(1);
				break;
			}
			return orderNo;
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			this.dataSourceBS.releaseConnection(rs, stmt, conn);
		}
		return 0l;
	}

	/**
	 * @方法描述: 补录表数据查询-导入
	 * @创建人: huzq1 
	 * @创建时间: 2021/11/5 17:07
	  * @param file
	 * @param templateId
	 * @param orderNo
	 * @return
	 **/
	public Map<String, Object> getUpdateData(File file, String templateId, long orderNo){
		Map<String, Object> mapList = this.excelConstants.getAllList(file, orderNo);
		List<Map<String, Object>> list  = (List<Map<String, Object>>) mapList.get("list");
		Map<String, List<Map<String, Object>>> taskInsMap = list.stream().collect(Collectors.groupingBy(item -> (String)item.get("SYS_DATA_CASE")));
		for (String taskId : taskInsMap.keySet()) {
			List<Map<String, Object>> nodeInsList = taskInsMap.get(taskId);
			Map<String, List<Map<String, Object>>> nodeInsMap = nodeInsList.stream().collect(Collectors.groupingBy(item -> (String) item.get("FLOW_NODE_ID")));
			for (String nodeInsId : nodeInsMap.keySet()) {
				List<Map<String, Object>> newUdpList = nodeInsMap.get(nodeInsId);
				for (Map<String, Object> map : newUdpList) {
					map.remove("SYS_DATA_CASE");
					map.remove("FLOW_NODE_ID");
				}
				Map<String, Object> map = new HashMap<>();
				map.put("addList", new ArrayList<>());
				map.put("udpList", newUdpList);
				map.put("delList", new ArrayList<>());
				List<String> strings = updateDataFileById(map, templateId, taskId, nodeInsId);
				System.out.println(strings);
			}
		}
		return mapList;
	}

	/**
	 * 补录导入数据保存
	 *
	 * @param mapList
	 * @param templeId
	 * @param caseId
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<String> updateDataFileById(Map<String, Object> mapList, String templeId, String caseId,String taskFlowNodeId) {
		logger.debug("补录导入数据保存-------start-----0----saveDataFileById"+FormatUtils.format(System.currentTimeMillis(), "yyyy-MM-dd HH:mm:ss.SSS"));
		List<String> list = Lists.newArrayList();
		List<Map<String, Object>> dataAdd = Lists.newArrayList();
		List<Map<String, Object>> dataOld = Lists.newArrayList();
		List<Map<String, Object>> dataUdp = Lists.newArrayList();

		List<String> dataState = Lists.newArrayList();
		dataState.add(UdipConstants.TASK_STATE_DISPATCH);
		dataState.add(UdipConstants.TASK_STATE_SAVE);
		dataState.add(UdipConstants.TASK_STATE_VALIDATE);
		dataState.add(UdipConstants.TASK_STATE_REFUSE);
		dataState.add(UdipConstants.TASK_STATE_SUBMIT);
		dataState.add(UdipConstants.TASK_STATE_SUCESS);
		dataState.add(UdipConstants.TASK_STATE_REJECT);

		// 统一获取信息
		Map<String, Object> getMap = this.taskCaseBS.getMap(templeId, caseId,false);
		RptInputLstTempleInfo temp = (RptInputLstTempleInfo) getMap.get("temp");

		try {
			if (mapList.isEmpty()) {
				list.add(list.size(), "无数据");
			} else if (mapList.get("typeWrrong") != null) {
				list.add(list.size(), "读取文件出错，请检查模板是否正确。");
			} else if (mapList.get("file") != null) {
				list.add(list.size(), "目标文件数据不存在，可能已上传，可导入同名文件进行更新。");
			} else if (mapList.get("wrrong") != null) {
				list.add(list.size(), "读取文件出错。");
			} else {
				if ("1".equals(temp.getAllowUpdate())) {
					dataUdp = (List<Map<String, Object>>) mapList.get("udpList");
				}
				if ("1".equals(temp.getAllowDelete())) {
					dataOld = (List<Map<String, Object>>) mapList.get("delList");
				}
			}
			if (!list.isEmpty()) {
				return list;
			}
			List<String> org = this.taskCaseBS.getOrgList((RptInputLstTempleInfo) getMap.get("temp"), null, caseId);
			List<RptInputLstValidateLog> listlog = Lists.newArrayList();
			List<RptInputLstValidateLog> listlog2 = Lists.newArrayList();
			// 日志实体
			RptInputLstValidateLog RptInputLstValidateLog = this.validateLogBS.getUdipValidateLog(caseId, "",
					(String) getMap.get("oper"), "", templeId, (String) getMap.get("data"));
			try {
				// 判断模板是否有主键
				if (((List<RptInputLstTempleConst>) getMap.get("keyList")).isEmpty()) {
					list.add("模板未创建主键，无法导入，请联系管理人员。");
					return list;
				}
				Map<String, Object> result = null;
				Boolean chack = false;
				if (CollectionUtils.isNotEmpty(dataUdp)) {
					logger.debug("补录导入数据保存-----before----3----listlog2"+FormatUtils.format(System.currentTimeMillis(), "yyyy-MM-dd HH:mm:ss.SSS"));
					listlog2 = this.dataFormatValidate.validate(1,
							(RptTskIns) getMap.get("taskCase"), dataUdp,
							(RptInputLstTempleInfo) getMap.get("temp"),
							(Map<String, RptInputLstTempleField>) getMap.get("colMap"),
							(List<RptInputLstTempleConst>) getMap.get("keyList"),
							(List<RptInputLstTempleRule>) getMap.get("ruleList"),
							(Map<String, Map<String, Object>>) getMap.get("libList"),
							UdipConstants.DATA_FLAG_UPATE,taskFlowNodeId);
				}
				listlog.addAll(listlog2);
				logger.debug("补录导入数据保存-----before----4----deleteUdipValidateLog"+FormatUtils.format(System.currentTimeMillis(), "yyyy-MM-dd HH:mm:ss.SSS"));
				this.validateLogBS.deleteUdipValidateLog(caseId, templeId, RptInputLstValidateLog.getOrgNo());
				if (listlog.isEmpty()) {
					RptInputLstValidateLog log2 = new RptInputLstValidateLog();
					log2.setValidateResult("数据合法性校验成功");
					logger.debug("补录导入数据保存----5---数据合法性校验成功"+FormatUtils.format(System.currentTimeMillis(), "yyyy-MM-dd HH:mm:ss.SSS"));
					listlog.add(log2);
					result = this.dataUtils.deleteAndAddData(taskFlowNodeId,1, dataAdd,
							dataOld, dataUdp, (RptInputLstTempleInfo) getMap.get("temp"), (List<String>) getMap.get("keyCols"),
							(Map<String, RptInputLstTempleField>) getMap.get("colMap"),
							(Map<String, Map<String, Object>>) getMap.get("libList"),(String) getMap
									.get("casedata"), dataState, getMap.get("data"), getMap.get("oper"), caseId, org);
					chack = (boolean)result.get("chack");
				} else {
					list.add("数据合法性校验失败：");
					for (RptInputLstValidateLog log : listlog) {
						list.add(log.getValidateResult());
					}
				}
				if (chack) {
					if (dataAdd.size() + dataUdp.size() + dataOld.size() > 0) {
						logger.debug("补录导入数据保存----6---更改dataState状态"+FormatUtils.format(System.currentTimeMillis(), "yyyy-MM-dd HH:mm:ss.SSS"));
						// 更改dataState状态
						this.dataStateBS.changerDataState(templeId, caseId,
								org, BioneSecurityUtils.getCurrentUserInfo().getCurrentLogicSysNo(),
								UdipConstants.TASK_STATE_SAVE);
						// 新增一条数据操作记录
						this.authRecordBS.saveAuthRecord(templeId, caseId,
								UdipConstants.TASK_STATE_SAVE);
					}
				} else {
					list.add("数据插入数据库失败");
				}
				logger.debug("补录导入数据保存-------before----7-----saveUdipValidateLog"+FormatUtils.format(System.currentTimeMillis(), "yyyy-MM-dd HH:mm:ss.SSS"));
				this.validateLogBS.saveUdipValidateLog(listlog, RptInputLstValidateLog);
				if (list.isEmpty()) {
					list.add("操作成功:");
					list.add("新增数据：" + dataAdd.size() + "条.");
					list.add("更新数据：" + dataUdp.size() + "条.");
					list.add("删除数据：" + (result == null ? "0" : result.get("deleteTotal").toString())  + "条.");
				}
				logger.debug("补录导入数据保存-------end----8-----saveDataFileById"+FormatUtils.format(System.currentTimeMillis(), "yyyy-MM-dd HH:mm:ss.SSS"));
				return list;
			} catch (Exception e) {
				e.printStackTrace();
				RptInputLstValidateLog log2 = new RptInputLstValidateLog();
				log2.setValidateResult("数据插入数据库失败：" + e.getMessage());
				listlog.add(log2);
				this.validateLogBS.saveUdipValidateLog(listlog, RptInputLstValidateLog);
				list.add(e.getMessage());
				return list;
			}
		} catch (Exception e1) {
			e1.printStackTrace();
			list.add(e1.getMessage());
			return list;
		}
	}

}
