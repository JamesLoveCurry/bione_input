package com.yusys.bione.plugin.businesslib.service;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.yusys.bione.comp.common.CommonComboBoxNode;
import com.yusys.bione.comp.repository.jpa.SearchResult;
import com.yusys.bione.comp.service.BaseBS;
import com.yusys.bione.frame.mtool.entity.BioneDsInfo;
import com.yusys.bione.plugin.businesslib.entity.RptMgrBusiLibInfo;
import com.yusys.bione.plugin.businessline.entity.RptMgrBusiLine;

@Service
@Transactional(readOnly = true)
public class RptBusinessLibBS  extends BaseBS<Object>{

	@SuppressWarnings("unchecked")
	public Map<String, Object> getBusinessLibList(int firstResult,
			int pageSize, String orderBy, String orderType,
			Map<String, Object> conditionMap) {
		
		String jql = " select r from RptMgrBusiLibInfo r where 1=1";
		if (!conditionMap.get("jql").equals("")) {
			jql += " and " + conditionMap.get("jql") + " ";
		}
		if (!StringUtils.isEmpty(orderBy)) {
			jql += "order by " + orderBy + " " + orderType;
		}
		Map<String, Object> values = (Map<String, Object>) conditionMap.get("params");
		SearchResult<RptMgrBusiLine> rsList = this.baseDAO.findPageWithNameParam(firstResult, pageSize, jql, values);
		Map<String, Object> rsMap = Maps.newHashMap();
		rsMap.put("Rows", rsList.getResult());
		rsMap.put("Total", rsList.getTotalCount());
		return rsMap;
	}

	public void batchRemoveEntitys(String ids) {
		String[] busiLibIds = StringUtils.split(ids, ',');
		List<String> busiLibList = Arrays.asList(busiLibIds);
		
		Map<String,Object> params = Maps.newHashMap();
		params.put("busiLibIds", busiLibList);
		String jql = new String("delete from RptMgrBusiLibInfo r where r.busiLibId in :busiLibIds");
		this.baseDAO.batchExecuteWithNameParam(jql, params);
	}

	public List<CommonComboBoxNode> getComboxInfoByTypeNo(String typeNo) {
		List<CommonComboBoxNode> nodeList = Lists.newArrayList();
		//数据源下拉框
		if(StringUtils.equals("dsIdBox", typeNo)) {
			List<BioneDsInfo> dsList = this.getAllEntityList(BioneDsInfo.class);
			for(BioneDsInfo ds : dsList) {
				CommonComboBoxNode node = new CommonComboBoxNode();
				node.setId(ds.getDsId());
				node.setText(ds.getDsName());
				nodeList.add(node);
			}
		}else if(StringUtils.equals("busiLib", typeNo)) {
			List<RptMgrBusiLibInfo> bsList = this.getAllEntityList(RptMgrBusiLibInfo.class);
			for(RptMgrBusiLibInfo bs : bsList) {
				CommonComboBoxNode node = new CommonComboBoxNode();
				node.setId(bs.getBusiLibId());
				node.setText(bs.getBusiLibNm());
				nodeList.add(node);
			}
		}
		return nodeList;
	}

	public boolean validateValue(String id, String busiLibNo, String busiLibNm) {
		boolean ret = false;
		if(StringUtils.isNotBlank(busiLibNo)) {
			RptMgrBusiLibInfo obj = this.getEntityByProperty(RptMgrBusiLibInfo.class, "busiLibNo", busiLibNo);
			ret = this.validateById(id,obj);
			
		}else if(StringUtils.isNotBlank(busiLibNm)) {
			RptMgrBusiLibInfo obj = this.getEntityByProperty(RptMgrBusiLibInfo.class, "busiLibNm", busiLibNm);
			ret = this.validateById(id,obj);
		}
		return ret;
	}

	private boolean validateById(String id, RptMgrBusiLibInfo obj) {
		if(obj != null){
			//可以查询出结果  判断ID是否存在
			if(StringUtils.isNotBlank(id)){
				//id存在  修改情况  编号与原来相同
				 if(!id.equals(obj.getBusiLibId())){
					 return false;
				 }
			}else{
				//id不存在  新增情况
				return false;
			}
		}
		return true;
	}

	public Map<String, Object> getDsData() {
		Map<String, Object> dsMap = Maps.newHashMap();

		List<BioneDsInfo> dsList = this.getAllEntityList(BioneDsInfo.class);
		if(dsList !=null && !dsList.isEmpty()) {
			for(BioneDsInfo ds : dsList) {
				dsMap.put(ds.getDsId(), ds.getDsName());
			}
		}
		return dsMap;
	}

	/**
	 * 根据数据源获取模型
	 * @param dsId
	 * @param firstResult
	 * @param pageSize
	 * @param orderBy
	 * @param orderType
	 * @param conditionMap
	 * @return
	 */
	public Map<String, Object> getRptSysModuleInfoList(String dsId, int firstResult,
			int pageSize, String orderBy, String orderType,	Map<String, Object> conditionMap) {
		StringBuffer jqlBuf = new StringBuffer(" select r from RptSysModuleInfo r where 1=1 ");
		if(StringUtils.isNotEmpty(dsId)) {
			jqlBuf.append(" and r.sourceId='" + dsId + "' ") ;
		}
		if (!conditionMap.get("jql").equals("")) {
			jqlBuf.append(" and "+ conditionMap.get("jql") ) ;
		}
		if (!StringUtils.isEmpty(orderBy)) {
			jqlBuf.append(" order by " + orderBy + " " + orderType) ;;
		}
		@SuppressWarnings("unchecked")
		Map<String, Object> values = (Map<String, Object>) conditionMap.get("params");
		SearchResult<RptMgrBusiLine> rsList = this.baseDAO.findPageWithNameParam(firstResult, pageSize, jqlBuf.toString(), values);
		Map<String, Object> rsMap = Maps.newHashMap();
		rsMap.put("Rows", rsList.getResult());
		rsMap.put("Total", rsList.getTotalCount());
		return rsMap;
	}

}
