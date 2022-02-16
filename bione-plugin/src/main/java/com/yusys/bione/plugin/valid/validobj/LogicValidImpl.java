package com.yusys.bione.plugin.valid.validobj;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.yusys.bione.comp.common.CommonTreeNode;
import com.yusys.bione.plugin.rptidx.entity.RptIdxCatalog;
import com.yusys.bione.plugin.rptidx.entity.RptIdxInfo;
import com.yusys.bione.plugin.rptvalid.service.RptValidLogicBS;
import com.yusys.bione.plugin.valid.IValidTypeImpl;
import com.yusys.bione.plugin.valid.service.RptValidGroupBS;

/**
 * <pre>
 * Title:程序的中文名称
 * Description: 程序功能的描述
 * </pre>
 * 
 * @author hubing1@yusys.com.cn
 * @version 1.00.00
 * 
 *          <pre>
 * 修改记录
 *    修改后版本:     修改人：  修改日期:     修改内容:
 * </pre>
 */
@Component
public class LogicValidImpl implements IValidTypeImpl {

	@Autowired
	private RptValidLogicBS rptValidLogicBS;
	
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
		
		rstNodes.addAll(this.rptValidLogicBS.getValidIdxCtls(parentNode, basePath, validType));//目录节点
		rstNodes.addAll(this.rptValidLogicBS.getValidIdxs(parentNode, basePath, validType));//指标节点
		if("idxInfo".equals(parentNode.getParams().get("nodeType")) && 
				"05".equals(parentNode.getParams().get("idxType"))){//如果父节点是总账指标还要显示度量节点
			rstNodes.addAll(this.rptValidLogicBS.getIdxMeasure(parentNode, basePath, validType));//总账的度量节点
		}else{
			rstNodes.addAll(this.rptValidLogicBS.getIdxLogicValid(parentNode, basePath, validType));//逻辑校验节点
		}
		return rstNodes;
	}

	@Override
	public Map<String, String> doGetAllValidTreeNode(String basePath, String groupId, String grpType,String validType) {
		Map<String, String> rstMap = new HashMap<String, String>();
		Map<String,Set<String>> logic = this.rptValidLogicBS.getLogicValidIdxAndGlMeasure();
		List<RptIdxInfo> logicIdxs = this.rptValidGroupBS.getValidIdxByNos(logic.get("allLocIdx"));
		if(null != logicIdxs && logicIdxs.size() > 0){
			List<String> oneLvlCtlNos = new ArrayList<String>();
			List<RptIdxCatalog> catalogs = new ArrayList<RptIdxCatalog>();
			for(RptIdxInfo idx : logicIdxs){//指标节点
				rstMap.put(validType + "@" + idx.getId().getIndexNo(), validType + "@" + idx.getIndexCatalogNo());
				oneLvlCtlNos.add(idx.getIndexCatalogNo());
			}
			this.rptValidGroupBS.getAllUpCtlNos(oneLvlCtlNos, catalogs);
			if(null != catalogs && catalogs.size() > 0){//目录节点
				for(RptIdxCatalog ctl : catalogs){
					rstMap.put(validType + "@" + ctl.getIndexCatalogNo(), validType + "@" + ctl.getUpNo());
				}
			}
			Set<String> glMeasure = logic.get("glMeasure");//总账度量节点
			if(null != glMeasure && glMeasure.size() > 0){
				for(String mse : glMeasure){
					rstMap.put(validType + "@" + mse, validType + "@" + StringUtils.split(mse, ".")[0]);
				}
			}
			Set<String> idxChecks = logic.get("idxChecks");//校验节点
			if(null != idxChecks && idxChecks.size() > 0){
				for(String check : idxChecks){
					rstMap.put(validType + "@" + StringUtils.split(check, ";")[0], validType + "@" + StringUtils.split(check, ";")[1]);
				}
			}
		}
		return rstMap;
	}

	@Override
	public List<CommonTreeNode> doGetSearchSyncTreeNode(String basePath, String groupId,
			String grpType, String validType, String searchNm) {
		List<CommonTreeNode> rstNodes = new ArrayList<CommonTreeNode>();
		List<CommonTreeNode> nodes = this.rptValidLogicBS.findSyncTreeByKeyWord(basePath, searchNm, validType);
		if(null != nodes && nodes.size() > 0){
			for(CommonTreeNode node : nodes) {
				node.setId(validType + "@" + node.getId());
				node.setUpId(validType + "@" + node.getUpId());
				rstNodes.add(node);
			}
		}
		return rstNodes;
	}
}
