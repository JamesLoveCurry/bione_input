package com.yusys.bione.plugin.rptidx.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.yusys.bione.comp.common.CommonComboBoxNode;
import com.yusys.bione.comp.service.BaseBS;
import com.yusys.bione.frame.base.common.GlobalConstants4frame;
import com.yusys.bione.plugin.base.common.GlobalConstants4plugin;
import com.yusys.bione.plugin.rptdim.entity.RptDimItemInfo;

@Service
@Transactional(readOnly = true)
public class IdxResultAnlyBS extends BaseBS<Object> {

	/**
	 * 获取当前机构的所有下级机构
	 * @param orgNo
	 * @return
	 */
	public List<String> getChildOrg(String orgNo) {
		String jql = "select org.id.orgNo from RptOrgInfo org where org.id.orgType =?0 and org.upOrgNo =?1";
		List<String> list = this.baseDAO.findWithIndexParam(jql, GlobalConstants4plugin.RPT_FRS_BUSI_BANK, orgNo);
		return list;
	}

	public String getOrgNm(String orgNo) {
		String jql = "select org.orgNm from  RptOrgInfo org where org.id.orgType =?0 and org.id.orgNo =?1";
		return this.baseDAO.findUniqueWithIndexParam(jql, GlobalConstants4plugin.RPT_FRS_BUSI_BANK, orgNo);
	}

	public List<CommonComboBoxNode> getDimOfIndex(String indexNo,
			String indexVerId) {
		String jql = "select new com.yusys.bione.comp.common.CommonComboBoxNode(dim.dimTypeNo, dim.dimTypeNm) from RptIdxInfo idx, RptIdxDimRel rel, RptDimTypeInfo dim"
				+ " where idx.id.indexNo = rel.id.indexNo and idx.id.indexVerId = rel.id.indexVerId and rel.id.dimNo = dim.dimTypeNo "
				+ " and rel.id.indexNo =?0 and rel.id.indexVerId =?1 and dim.dimType = ?2";
		List<CommonComboBoxNode> list = this.baseDAO.findWithIndexParam(jql, indexNo, new Long(indexVerId), GlobalConstants4plugin.DIM_TYPE_BUSI);
		return list;
	}//用这个

	public List<RptDimItemInfo> getDimItems(String dimTypeNo) {
		String jql = "select item from RptDimItemInfo item where item.id.dimTypeNo =?0";//新写一个方法加这个条件
		List<RptDimItemInfo> list = this.baseDAO.findWithIndexParam(jql, dimTypeNo);
		return list;
	}
	
	public List<RptDimItemInfo> getDimSum(String dimTypeNo){
		String jql="select item from RptDimItemInfo item where item.id.dimTypeNo =?0 and upNo =?1";
		List<RptDimItemInfo> list = this.baseDAO.findWithIndexParam(jql, dimTypeNo,GlobalConstants4frame.DEFAULT_TREE_ROOT_NO);
		return list;
	}

}
