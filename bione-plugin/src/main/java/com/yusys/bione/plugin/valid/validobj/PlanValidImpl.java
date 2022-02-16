package com.yusys.bione.plugin.valid.validobj;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.yusys.bione.comp.common.CommonTreeNode;
import com.yusys.bione.plugin.idxplan.entity.RptIdxPlanvalResult;
import com.yusys.bione.plugin.idxplan.service.IdxPlanvalInfoBS;
import com.yusys.bione.plugin.rptidx.entity.RptIdxCatalog;
import com.yusys.bione.plugin.rptidx.entity.RptIdxInfo;
import com.yusys.bione.plugin.valid.IValidTypeImpl;
import com.yusys.bione.plugin.valid.service.RptValidGroupBS;

/**
 * <pre>
 * Title:程序的中文名称
 * Description: 程序功能的描述
 * </pre>
 * 
 * @author caiqy caiqy@yuchengtech.com
 * @version 1.00.00
 * 
 *          <pre>
 * 修改记录
 *    修改后版本:     修改人：  修改日期:     修改内容:
 * </pre>
 */
@Component
public class PlanValidImpl implements IValidTypeImpl {
	
	@Autowired
	private IdxPlanvalInfoBS idxPlanvalInfoBS;
	
	@Autowired
	private RptValidGroupBS rptValidGroupBS;

	@Override
	public String getAuthObjDefNo() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<CommonTreeNode> doGetValidIdxAsync(String groupId, String grpType, String validType, CommonTreeNode parentNode,
			String basePath, String indexVerId, String... searchText) {
		List<CommonTreeNode> rstNodes = new ArrayList<CommonTreeNode>();
		
		rstNodes.addAll(this.idxPlanvalInfoBS.getValidIdxCtls(parentNode, basePath, validType));//目录节点
		rstNodes.addAll(this.idxPlanvalInfoBS.getValidIdxs(parentNode, basePath, validType));//指标节点
		if("idxInfo".equals(parentNode.getParams().get("nodeType")) && 
				"05".equals(parentNode.getParams().get("idxType"))){//如果父节点是总账指标还要显示度量节点
			rstNodes.addAll(this.idxPlanvalInfoBS.getIdxMeasure(parentNode, basePath, validType));//总账的度量节点
		}
		return rstNodes;
	}

	@Override
	public Map<String, String> doGetAllValidTreeNode(String basePath, String groupId, String grpType,String validType) {
		Map<String, String> rstMap = new HashMap<String, String>();
		List<RptIdxPlanvalResult> planList = this.idxPlanvalInfoBS.getEntityList(RptIdxPlanvalResult.class);
		List<String> idxNos = new ArrayList<String>();
		if(null != planList && planList.size() > 0){
			for(RptIdxPlanvalResult plan : planList){
				if(StringUtils.contains(plan.getId().getIndexNo(), ".")){
					idxNos.add(StringUtils.split(plan.getId().getIndexNo(), ".")[0]);
					rstMap.put(validType + "@" + plan.getId().getIndexNo(), 
							validType + "@" + StringUtils.split(plan.getId().getIndexNo(), ".")[0]);
				}else{
					idxNos.add(plan.getId().getIndexNo());
				}
			}
			if(null != idxNos && idxNos.size() > 0){
				List<String> oneLvlCtlNos = new ArrayList<String>();
				List<RptIdxCatalog> catalogs = new ArrayList<RptIdxCatalog>();
				List<RptIdxInfo> planIdxs = this.idxPlanvalInfoBS.getPlanIdxInfo(idxNos);
				for(RptIdxInfo idx : planIdxs){
					rstMap.put(validType + "@" + idx.getId().getIndexNo(), 
							validType + "@" + idx.getIndexCatalogNo());
					oneLvlCtlNos.add(idx.getIndexCatalogNo());
				}
				this.rptValidGroupBS.getAllUpCtlNos(oneLvlCtlNos, catalogs);
				if(null != catalogs && catalogs.size() > 0){//目录节点
					for(RptIdxCatalog ctl : catalogs){
						rstMap.put(validType + "@" + ctl.getIndexCatalogNo(), 
								validType + "@" + ctl.getUpNo());
					}
				}
			}
		}
		return rstMap;
	}

	@Override
	public List<CommonTreeNode> doGetSearchSyncTreeNode(String basePath, String groupId,
			String grpType, String validType, String searchNm) {
		List<CommonTreeNode> rstNodes = new ArrayList<CommonTreeNode>();
		List<CommonTreeNode> nodes = this.idxPlanvalInfoBS.findSyncTreeByKeyWord(basePath, searchNm, validType);
		if(null != nodes && nodes.size() > 0) {
			for(CommonTreeNode node : nodes) {
				node.setId(validType + "@" + node.getId());
				node.setUpId(validType + "@" + node.getUpId());
				rstNodes.add(node);
			}
		}
		return rstNodes;
	}
}
