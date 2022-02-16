package com.yusys.bione.plugin.idxanacfg.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.yusys.bione.comp.service.BaseBS;
import com.yusys.bione.plugin.idxanacfg.entity.RptAnaChartsOrgRel;
import com.yusys.bione.plugin.rptorg.entity.RptOrgInfo;

/**
 * <pre>
 * Title:指标分析图表机构关系BS
 * Description: 
 * </pre>
 * 
 * @author yangyf
 * @version 1.00.00
 * 
 *          <pre>
 * 修改记录
 *    修改后版本:     修改人：  修改日期:     修改内容:
 * </pre>
 */

@Service
@Transactional(readOnly = true)
public class RptAnaChartsOrgRelBS extends BaseBS<RptAnaChartsOrgRel>{
	
	/**
	 * 获取图表勾选的机构
	 * @param chartId
	 * @return
	 */
	public Map<String, String> getOrgRel(String chartId){
		Map<String, String> orgRelMap = new HashMap<String, String>();
		RptAnaChartsOrgRel chartOrg = this.getEntityById(chartId);
		if(chartOrg != null){
			String orgNo = chartOrg.getShowOrg();
			orgRelMap.put("orgNm", this.getOrgNm(orgNo));
			orgRelMap.put("orgNo", orgNo);
		}
		return orgRelMap;
	}
	
	/**
	 * 保存图表显示的机构
	 * @param chartId
	 * @param orgNo
	 * @return
	 */
	@Transactional(readOnly = false)
	public RptAnaChartsOrgRel saveOrgRel(String chartId,String orgNo){
		RptAnaChartsOrgRel anaChartsOrgInfo = new RptAnaChartsOrgRel();
		anaChartsOrgInfo.setChartId(chartId);
		anaChartsOrgInfo.setShowOrg(orgNo);;
		return this.saveOrUpdateEntity(anaChartsOrgInfo);
	}
	
	/**
	 * 获取机构名称
	 * @param orgNo 机构号分号分割字符串
	 * @return
	 */
	private String getOrgNm(String orgNo){
		String orgNm = "";
		if(orgNo != null && orgNo.length() > 0){
			String[] orgNolist = StringUtils.split(orgNo, ";");
			if(orgNolist != null && orgNolist.length > 0){
				List<String> orgList =new ArrayList<String>();
				orgList = Arrays.asList(orgNolist);
				String jql = "select org from RptOrgInfo org where org.id.orgNo in ?0";
				List<RptOrgInfo> rptOrglist = this.baseDAO.findWithIndexParam(jql,orgList);
				if(rptOrglist != null && rptOrglist.size() > 0){
					for(RptOrgInfo org : rptOrglist){
						if(org != null){
							orgNm += org.getOrgNm() + ";";
						}
					}
				}
			}
		}
		return orgNm;
	}
	
	/**
	 * 复制图表选择的机构
	 * @param charsId
	 * @param newCharsId
	 */
	@Transactional(readOnly = false)
	public void copyCharts(String charsId, String newCharsId){
		RptAnaChartsOrgRel oldCharts = this.getEntityById(charsId);
		if(oldCharts != null){
			RptAnaChartsOrgRel newCharts = new RptAnaChartsOrgRel();
			newCharts.setChartId(newCharsId);
			newCharts.setRemark(oldCharts.getRemark());
			newCharts.setShowOrg(oldCharts.getShowOrg());
			newCharts.setShowType(oldCharts.getShowType());
			this.saveEntity(newCharts);
		}
	}
}
