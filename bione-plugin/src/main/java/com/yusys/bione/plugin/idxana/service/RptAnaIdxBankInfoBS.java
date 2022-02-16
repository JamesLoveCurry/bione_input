package com.yusys.bione.plugin.idxana.service;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.yusys.bione.comp.service.BaseBS;
import com.yusys.bione.plugin.rptbank.entity.RptIdxBankInfo;

@Service
@Transactional(readOnly = true)
public class RptAnaIdxBankInfoBS extends BaseBS<RptIdxBankInfo>{
	
	/**
	 * 获取满足条件的对象
	 * @param idxno
	 * @param themeId
	 * @return
	 */
	public RptIdxBankInfo getMainIdx(String idxno ,String themeId){
		RptIdxBankInfo idxBankInfo = new RptIdxBankInfo();
		if((idxno != null && idxno.length() > 0) && (themeId != null && themeId.length() > 0)){
			String jql = "select main from RptIdxBankInfo main where main.id.indexId = ?0 and main.id.themeId = ?1";
			idxBankInfo= this.baseDAO.findUniqueWithIndexParam(jql, idxno, themeId);
		}
		return idxBankInfo;
	}
	
	/**
	 * 获取满足条件的对象
	 * @param idxno
	 * @param themeId
	 * @param currency
	 * @return
	 */
	public RptIdxBankInfo getMainIdx(String idxno ,String themeId ,String currency){
		RptIdxBankInfo peculiarIdxBankInfo = new RptIdxBankInfo();
		if((idxno != null && idxno.length() > 0) && (themeId != null && themeId.length() > 0)){
			String jql = "select main from RptIdxBankInfo main where main.mainNo = ?0 and main.id.themeId = ?1";
			if(StringUtils.isNotEmpty(currency)){
				jql += " and main.currency = ?2";
				peculiarIdxBankInfo= this.baseDAO.findUniqueWithIndexParam(jql, idxno, themeId, currency);	
			}else{
				peculiarIdxBankInfo= this.baseDAO.findUniqueWithIndexParam(jql, idxno, themeId);	
			}
		}
		return peculiarIdxBankInfo;
	}
	
	/**
	 * 获取满足条件的对象集合
	 * @param idxno
	 * @param themeId
	 * @return
	 */
	public List<RptIdxBankInfo> getIdxlist(String idxno ,String themeId){
		List<RptIdxBankInfo> rptlist = new ArrayList<RptIdxBankInfo>();
		if((idxno != null && idxno.length() > 0) && (themeId != null && themeId.length() > 0)){
			String jql = "select main from RptIdxBankInfo main where main.upNo = ?0 and main.id.themeId = ?1 order by main.orderNum";
			rptlist = this.baseDAO.findWithIndexParam(jql, idxno, themeId);
		}
		return rptlist;
	}

}
