package com.yusys.biapp.input.data.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.yusys.biapp.input.data.entity.RptInputLstAuthRecord;
import com.yusys.bione.comp.service.BaseBS;
import com.yusys.bione.comp.utils.DateUtils;
import com.yusys.bione.comp.utils.RandomUtils;
import com.yusys.bione.frame.security.BioneSecurityUtils;

/**
 * <pre>
 * Title: 程序名称
 * Description: 功能描述
 * </pre>
 * 
 * @author xuguangyuan xugy@yuchengtech.com
 * @version 1.00.00
 * 
 *          <pre>
 * 修改记录
 *    修改后版本:       修改人:       修改日期:       修改内容:
 * </pre>
 */
@Service
@Transactional(readOnly = true)
public class AuthRecordBS extends BaseBS<RptInputLstAuthRecord> {

	/**
	 * 新增一条操作记录
	 * 
	 * @param templeId
	 *            模版Id
	 * @param caseId
	 *            实例Id
	 * @param dataType
	 *            状态
	 */
	@Transactional(readOnly = false)
	public void saveAuthRecord(String templeId, String caseId, String dataType) {
		RptInputLstAuthRecord udipAuthRecordInfo = new RptInputLstAuthRecord();
		udipAuthRecordInfo.setCaseId(caseId);
		udipAuthRecordInfo.setOperTime(DateUtils.getYYYY_MM_DD_HH_mm_ss());
		udipAuthRecordInfo.setOperType(dataType);
		udipAuthRecordInfo.setOperUser(BioneSecurityUtils.getCurrentUserInfo().getLoginName());
		udipAuthRecordInfo.setOrgNo(BioneSecurityUtils.getCurrentUserInfo().getOrgNo());
		udipAuthRecordInfo.setRecordId(RandomUtils.uuid2());
		udipAuthRecordInfo.setOperRemark("");
		udipAuthRecordInfo.setTempleId(templeId);
		this.saveOrUpdateEntity(udipAuthRecordInfo);
	}
}
