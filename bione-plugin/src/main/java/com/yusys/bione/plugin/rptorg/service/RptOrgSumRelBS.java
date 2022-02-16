package com.yusys.bione.plugin.rptorg.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.yusys.bione.comp.utils.ArrayUtils;
import com.yusys.bione.frame.base.common.GlobalConstants4frame;
import com.yusys.bione.plugin.base.utils.ReBuildParam;
import com.yusys.bione.plugin.rptorg.entity.RptOrgSumRel;
import com.yusys.bione.plugin.rptorg.entity.RptOrgSumRelPK;
import com.yusys.bione.plugin.rptorg.repository.RptOrgSumRelMybatisDao;
/**
 * <pre>
 * Description: 功能描述
 * </pre>
 * @author sunyuming  sunym@yuchengtech.com
 * @version 1.00.00
 * <pre>
 * 修改记录
 *    修改后版本:       修改人:       修改日期:       修改内容: 
 * </pre>
 */

@Service
@Transactional(readOnly = true)
public class RptOrgSumRelBS {

	@Autowired
	private RptOrgSumRelMybatisDao rptMgrFrdOrgCollectMybatisDao;

	public Map<String, Object> findCheck(String orgType, String orgNo) {
		Map<String, Object> params = new HashMap<String, Object>();
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("orgType", orgType);
		map.put("upOrgNo", orgNo);
		List<RptOrgSumRel> list = this.rptMgrFrdOrgCollectMybatisDao
				.findCheck(map);
		List<String> orgNos = new ArrayList<String>();
		List<String> upOrgNos = new ArrayList<String>();
		if (list != null && list.size() > 0) {
			for (RptOrgSumRel collect : list) {
				orgNos.add(collect.getId().getOrgNo());
			}
			getUpOrgNo(upOrgNos, orgNos);
		}
		params.put("orgNos", orgNos);
		params.put("upOrgNos", upOrgNos);
		return params;
	}

	public void getUpOrgNo(List<String> upOrgNos, List<String> orgNos) {
		List<String> orgUps = this.rptMgrFrdOrgCollectMybatisDao
				.findUpNode(ReBuildParam.toDbList(orgNos));
		if (orgUps != null && orgUps.size() > 0) {
			if (!orgUps.get(0).equals(GlobalConstants4frame.TREE_ROOT_NO)) {
				upOrgNos.addAll(orgUps);
				getUpOrgNo(upOrgNos, orgUps);
			}

		}
	}
	public void getUpOrgNos(List<String>upOrgNos,List<String>orgNos,String orgNo){
		List<String>list=rptMgrFrdOrgCollectMybatisDao.findUpNode(ReBuildParam.toDbList(orgNos));
		if(list.size()>0){
			for(int i=0;i<list.size();i++){
				if(list.get(i).equals(orgNo)){
					list.remove(i);
					i--;
				}
			}
		}
		if(list.size()>0){
			upOrgNos.addAll(list);
			getUpOrgNos(upOrgNos, list, orgNo);
		}
	}

	public void getCheckedChildren(List<String> downOrgNos, List<String> orgNos) {
		List<String> orgDowns = this.rptMgrFrdOrgCollectMybatisDao
				.findChildNode(ReBuildParam.toDbList(orgNos));
		if (orgDowns != null && orgDowns.size() > 0) {
			downOrgNos.addAll(orgDowns);
			getCheckedChildren(downOrgNos, orgDowns);
		}
	}

	public String saveInfo(String checkedAll, String orgNo, String orgType,
			boolean cascade) {
		List<String> upOrgNoList = new ArrayList<String>();
		if(StringUtils.isNotBlank(checkedAll)) {
			String[] checkeds = checkedAll.split(",");
			for(String checked : checkeds) {
				upOrgNoList.add(checked);
			}
		}
		if (cascade) {
			List<String> checkedOrg = new ArrayList<String>();
			if(upOrgNoList.size() > 0) {
				getCheckedChildren(checkedOrg, upOrgNoList);
				upOrgNoList.addAll(checkedOrg);
			}
		}
		Map<String, Object> params = new HashMap<String, Object>();
		RptOrgSumRelPK id = new RptOrgSumRelPK();
		id.setUpOrgNo(orgNo);
		id.setOrgType(orgType);
		params.put("id", id);
		this.rptMgrFrdOrgCollectMybatisDao.deleteInfo(params);
		for (int i = 0; i < upOrgNoList.size(); i++) {
			Map<String, Object> map = new HashMap<String, Object>();
			RptOrgSumRelPK pk = new RptOrgSumRelPK();
			pk.setUpOrgNo(orgNo);
			pk.setOrgType(orgType);
			pk.setOrgNo(upOrgNoList.get(i));
			map.put("id", pk);
			map.put("sumRelType", "0");
			this.rptMgrFrdOrgCollectMybatisDao.saveInfo(map);
		}
		return "true";
	}
}
