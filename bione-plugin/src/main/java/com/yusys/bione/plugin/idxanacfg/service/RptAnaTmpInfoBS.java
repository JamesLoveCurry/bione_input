package com.yusys.bione.plugin.idxanacfg.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.yusys.bione.comp.common.CommonTreeNode;
import com.yusys.bione.comp.service.BaseBS;
import com.yusys.bione.comp.utils.RandomUtils;
import com.yusys.bione.plugin.idxanacfg.entity.RptAnaTmpInfo;
/**
 * <pre>
 * Title:指标分析模版BS
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
public class RptAnaTmpInfoBS extends BaseBS<RptAnaTmpInfo>{
	
	private String treeRootIcon = "/images/classics/menuicons/folder_table.png";
	
	/**
	 * 获取上下文路径
	 * @return
	 */
	private String getContextPath() {
		return com.yusys.bione.frame.base.common.GlobalConstants4frame.APP_CONTEXT_PATH;
	}
	
	/**
	 * 构造模版菜单树
	 * @return
	 */	
	public List<CommonTreeNode> getTmpTree(){
		List<CommonTreeNode> treeNodes = new ArrayList<CommonTreeNode>();
		CommonTreeNode treeNode = new CommonTreeNode();
		treeNode.setId("0");
		treeNode.setText("全部模板");
		treeNode.setIcon("/rpt-web/images/classics/menuicons/house.png");//feed.png
		treeNodes.add(treeNode);
		List<RptAnaTmpInfo> rptTmpList = this.baseDAO.findWithIndexParam("select rpt from RptAnaTmpInfo rpt order by rpt.isDefault desc");//原表已有全部模板
		for(RptAnaTmpInfo rptcabintmpinfo : rptTmpList){
			CommonTreeNode tmpNode = new CommonTreeNode();
			tmpNode.setId(rptcabintmpinfo.getTemplateId());//元素id
			tmpNode.setText(rptcabintmpinfo.getTemplateNm());
			tmpNode.setData(rptcabintmpinfo);
			tmpNode.setIcon(this.getContextPath() + treeRootIcon);
			tmpNode.setUpId("0");
			tmpNode.setData(rptcabintmpinfo);
			treeNodes.add(tmpNode);
		}
		return treeNodes;		
	}
	
	/**
	 * 设置默认模版
	 * @param tempId
	 * @return
	 */
	@Transactional(readOnly = false)
	public RptAnaTmpInfo defaultTemp(String templateId){
		String jql = "Update RptAnaTmpInfo set isDefault = ?0";
		this.baseDAO.batchExecuteWithIndexParam(jql,"N");
		RptAnaTmpInfo rptAnaTmpInfo = this.getEntityById(templateId);
		if(rptAnaTmpInfo != null){
			rptAnaTmpInfo.setIsDefault("Y");
			rptAnaTmpInfo = this.saveOrUpdateEntity(rptAnaTmpInfo);
		}
		return rptAnaTmpInfo;
	 }
	
	/**
	 * 保存模板详细信息
	 * @param templateId
	 * @param dataFormat
	 * @param dataUnit
	 * @param dataPrecision
	 * @return
	 */
	@Transactional(readOnly = false)
	public RptAnaTmpInfo saveTmpDetail(String templateId,String dataFormat,String dataUnit,String dataPrecision){
		RptAnaTmpInfo tmpInfo = new RptAnaTmpInfo();
		tmpInfo = this.getEntityById(RptAnaTmpInfo.class,templateId);
		if(tmpInfo != null){
			tmpInfo.setDataFormat(dataFormat);
			tmpInfo.setDataUnit(dataUnit);
			BigDecimal Precision = new BigDecimal(dataPrecision);
			tmpInfo.setDataPrecision(Precision);
		}
		return this.saveOrUpdateEntity(tmpInfo);
	}
	
	/**
	 * 复制模板
	 * @param templateId 原模板Id
	 * @return
	 */
	@Transactional(readOnly = false)
	public String copyTemp(String templateId){
		String newTmpId = RandomUtils.uuid2();
		RptAnaTmpInfo tmpInfo = this.getEntityById(templateId);
		if(tmpInfo != null){
			RptAnaTmpInfo newTmpinfo = new RptAnaTmpInfo();
			newTmpinfo.setTemplateId(newTmpId);
			newTmpinfo.setTemplateNm(tmpInfo.getTemplateNm() + "(复制)");
			newTmpinfo.setTemplateFreq(tmpInfo.getTemplateFreq());
			newTmpinfo.setRemark(tmpInfo.getRemark());
			newTmpinfo.setDataFormat(tmpInfo.getDataFormat());
			newTmpinfo.setDataPrecision(tmpInfo.getDataPrecision());
			newTmpinfo.setDataUnit(tmpInfo.getDataUnit());
			newTmpinfo.setIsDefault("N");
			this.saveEntity(newTmpinfo);
		}
		return newTmpId;
	}
}
