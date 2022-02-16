package com.yusys.bione.plugin.idxanacfg.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.yusys.bione.comp.common.CommonTreeNode;
import com.yusys.bione.comp.service.BaseBS;
import com.yusys.bione.plugin.idxanacfg.entity.RptAnaTmpIdxRel;
import com.yusys.bione.plugin.idxanacfg.entity.RptAnaTmpInfo;
import com.yusys.bione.plugin.rptidx.entity.RptIdxInfo;


/**
 * <pre>
 * Title:指标模板关系BS
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
public class RptAnaTmpRelBS extends BaseBS<RptAnaTmpIdxRel> {
	
	/**
	 * 左侧目录树的节点
	 */
	private String folderTreeType = "idxCatalog";
	private String idxTreeType = "idxInfo";
	
	/**
	 * 构造指标模板树
	 * @return
	 */
	public List<CommonTreeNode> funcToTree(){
		List<CommonTreeNode> treeNodes = new ArrayList<CommonTreeNode>();
		List<RptAnaTmpInfo> anaTmpList = this.getEntityList(RptAnaTmpInfo.class);//原表已有全部元素
		for(RptAnaTmpInfo anaTmpInfo : anaTmpList){
			CommonTreeNode treeNode = new CommonTreeNode();//实例化CommonTreeNode
			treeNode.setId(anaTmpInfo.getTemplateId());
			treeNode.setText(anaTmpInfo.getTemplateNm());
			treeNode.setUpId("0");
			treeNode.setIcon(this.getContextPath() +"/images/classics/icons/folder.gif");
			Map<String, String> paramMap = new HashMap<String, String>();
			paramMap.put("realId",anaTmpInfo.getTemplateId());
			paramMap.put("nodeType",folderTreeType);
			treeNode.setParams(paramMap);
			List<RptAnaTmpIdxRel> anaRptlist = this.baseDAO.findWithIndexParam("select rpt from RptAnaTmpIdxRel rpt where rpt.templateId = ?0",anaTmpInfo.getTemplateId());//原表已有全部元素
			if(anaRptlist.size() !=0){
			    treeNode.setIsParent(true);
			    treeNodes.add(treeNode);
				for(RptAnaTmpIdxRel anaRptTmpRel : anaRptlist){
					CommonTreeNode treeNode1 = new CommonTreeNode();
					treeNode1.setId(anaRptTmpRel.getIndexNo());
					treeNode1.setText(anaRptTmpRel.getIndexNm());
					treeNode1.setUpId(anaTmpInfo.getTemplateId());
					treeNode1.setIcon(this.getContextPath() +"/images/classics/icons/application_view_list.png");
					treeNode1.setData(anaRptTmpRel);
					Map<String,String> paramkid = new HashMap<String,String>();
					paramkid.put("realId",anaRptTmpRel.getIndexNo());
					paramkid.put("nodeType", idxTreeType);
					treeNode1.setParams(paramkid);
					treeNodes.add(treeNode1);
				}				
			}else{
				treeNode.setIsParent(true);
				treeNodes.add(treeNode);
			}
		}
		return treeNodes;
	}
	
	// 获取上下文路径
	private String getContextPath() {
		return com.yusys.bione.frame.base.common.GlobalConstants4frame.APP_CONTEXT_PATH;
	}
	
	/**
	 * 保存指标映射模版
	 *
	 */
	@Transactional(readOnly = false)
	public void saveTree(String paramIdx,String paramTid) {
		List<RptAnaTmpIdxRel> tmpoldList= this.getAllEntityList();
		this.removeEntity(tmpoldList);
		String[] idxId = StringUtils.split(paramIdx, ";");
		List<RptAnaTmpIdxRel> tmpList = new ArrayList<RptAnaTmpIdxRel>();
		for(String indexNos : idxId){
			if(!"null".equals(indexNos)){
				String[] indexNoArr = StringUtils.split(indexNos, ':');
				String templateId = indexNoArr[0];
				String indexNo = indexNoArr[1];
				RptIdxInfo anaRptTmpRel = this.baseDAO.findUniqueWithIndexParam("select idx from RptIdxInfo idx where idx.endDate= ?0 and idx.id.indexNo = ?1 and idx.indexSts = ?2 and idx.isRptIndex = ?3","29991231",indexNo,"Y","N");
				if(anaRptTmpRel != null){
					RptAnaTmpIdxRel tmpRel = new RptAnaTmpIdxRel();//创建新的对象，分割的东西放到新对象中
					tmpRel.setIndexNm(anaRptTmpRel.getIndexNm());
					tmpRel.setTemplateId(templateId);
					tmpRel.setIndexNo(indexNo);
					tmpList.add(tmpRel);
				}
			}
		}
		for (RptAnaTmpIdxRel rptdelete : tmpList) {// 保存数据
			this.saveOrUpdateEntity(rptdelete);
		}
	}
}
