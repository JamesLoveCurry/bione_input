package com.yusys.bione.plugin.businessline.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.Lists;
import com.yusys.bione.comp.common.CommonComboBoxNode;
import com.yusys.bione.comp.repository.jpa.SearchResult;
import com.yusys.bione.comp.service.BaseBS;
import com.yusys.bione.plugin.businesslib.entity.RptMgrBusiLibInfo;
import com.yusys.bione.plugin.businessline.entity.RptMgrBusiCfg;
import com.yusys.bione.plugin.businessline.entity.RptMgrBusiLine;
import com.yusys.bione.plugin.businessline.web.vo.RptMgrBusiCfgVO;
import com.yusys.bione.plugin.datamodel.entity.RptSysModuleCol;
import com.yusys.bione.plugin.datamodel.entity.RptSysModuleInfo;

@Service
@Transactional(readOnly = true)
public class RptBusinessLineBS extends BaseBS<Object>{

	/**
	 * grid查询数据
	 * @param firstResult
	 * @param pageSize
	 * @param orderBy
	 * @param orderType
	 * @param conditionMap
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public SearchResult<RptMgrBusiLine> getBusinessLine(int firstResult,
			int pageSize, String orderBy, String orderType,
			Map<String, Object> conditionMap) {
		String jql = " select info from RptMgrBusiLine info where 1=1";
		if (!conditionMap.get("jql").equals("")) {
			jql += " and " + conditionMap.get("jql") + " ";
		}

		if (!StringUtils.isEmpty(orderBy)) {
			jql += "order by " + orderBy + " " + orderType;
		}

		Map<String, Object> values = (Map<String, Object>) conditionMap
				.get("params");
		SearchResult<RptMgrBusiLine> lineList = this.baseDAO
				.findPageWithNameParam(firstResult, pageSize, jql, values);
		return lineList;
	}
		
	/**
	 * 删除业务条线
	 */
	public void delBusinessLine(String[] ids) {
		String delSql = " delete from RptMgrBusiLine line where line.lineId in (?0) ";
		List<String> idsList = Arrays.asList(ids);
		this.baseDAO.batchExecuteWithIndexParam(delSql, idsList);//删除
	}
	
	public List<String> addColCheck(String setId,List<String> enNms) {
		List<String> rlist = Lists.newArrayList();
		List<RptSysModuleCol> colist = Lists.newArrayList();
		if(null != enNms && enNms.size() > 0){
			String jql = "select col from RptSysModuleCol col where col.setId = ?0 and col.enNm not in (?1)";
			colist = this.baseDAO.findWithIndexParam(jql, setId, enNms);
		}
		if(null != colist && colist.size() > 0){
			for(RptSysModuleCol col : colist){
				rlist.add(col.getEnNm());
			}
		}
		return rlist;
	}

	/**
	 * 获取业务条线数据
	 * @return
	 */
	public List<CommonComboBoxNode> getLineInfo(){
		List<RptMgrBusiLine> lines = this.getAllEntityList(RptMgrBusiLine.class, "rankOrder", false);
		List<CommonComboBoxNode> nodes = new ArrayList<CommonComboBoxNode>();
		if(lines != null && lines.size() > 0){
			for(RptMgrBusiLine line : lines){
				CommonComboBoxNode node = new CommonComboBoxNode();
				node.setId(line.getLineId());
				node.setText(line.getLineNm());
				nodes.add(node);
			}
		}
		return nodes;
		
	}
	
	
	/**
	 * 获取业务条线信息
	 * @param lineId 条线ID
	 * @return 业务条线信息
	 */
	public RptMgrBusiLine getLineModify(String lineId) {
		String jql = " select info from RptMgrBusiLine info where info.lineId=:lineId";		
		Map<String, Object> values = new HashMap<String, Object>();
		values.put("lineId", lineId);
		RptMgrBusiLine line = this.baseDAO.findUniqueWithNameParam(jql, values);
		return line;
	}
	
	public Map<String,String> lineCheck(String lineId) {
		Map<String,String> rMap = new HashMap<String, String>();
		RptMgrBusiLine line = this.getEntityById(RptMgrBusiLine.class, lineId);
		if(null != line){
			rMap.put("msg", "1");
		}else{
			rMap.put("msg", "0");
		}
		return rMap;
	}
	
	public RptMgrBusiCfgVO getLineCfg(String lineId) {
		RptMgrBusiCfgVO vo = new RptMgrBusiCfgVO();
		RptMgrBusiLine line = this.getEntityById(RptMgrBusiLine.class, lineId);
		if(line != null){
			vo.setLineId(line.getLineId());
			vo.setLineNm(line.getLineNm());
			RptMgrBusiCfg cfg =  this.getEntityById(RptMgrBusiCfg.class, lineId);
			if(cfg != null){
				RptSysModuleInfo rptsys = this.getEntityById(RptSysModuleInfo.class, cfg.getRptSetId());
				RptSysModuleInfo idxsys = this.getEntityById(RptSysModuleInfo.class, cfg.getIdxSetId());
				if(rptsys != null){
					vo.setRptSourceId(rptsys.getSourceId());
					vo.setRptSetId(rptsys.getTableEnNm());
				}
				if(idxsys != null){
					vo.setIdxSourceId(idxsys.getSourceId());
					vo.setIdxSetId(idxsys.getTableEnNm());
				}
			}
		}
		return vo;
	}
	
	public String getSetId(String lineId,String type) {
		if(StringUtils.isNotBlank(lineId)){
			RptMgrBusiCfg cfg = this.getEntityById(RptMgrBusiCfg.class, lineId);
			if(cfg != null){
				if(type.equals("idx")){
					return cfg.getIdxSetId();
				}
				else{
					return cfg.getRptSetId();
				}
			}
			else{
				return "";
			}
		}
		return "";
	}

	/**
	 * 根据 业务分库 ID 
	 * 获取 setId
	 * @param busiLibId
	 * @return
	 */
	public String getSetIdByBusiLibId(String busiLibId) {
		String setId = "";
		if(StringUtils.isNotBlank(busiLibId)) {
			RptMgrBusiLibInfo blInfo = this.getEntityById(RptMgrBusiLibInfo.class, busiLibId);
			if(blInfo != null) {
				setId = blInfo.getSetId();
			}
		}
		return setId;
	}
}

