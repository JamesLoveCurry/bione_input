package com.yusys.biapp.input.template.service;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.Maps;
import com.yusys.biapp.input.template.entity.RptInputRewriteTempleInfo;
import com.yusys.biapp.input.template.entity.RptInputRewriteTempleInfoPK;
import com.yusys.biapp.input.template.vo.RptInputLstTempleInfoVO;
import com.yusys.bione.comp.service.BaseBS;
@Service
@Transactional
public class TempleRewriteBS  extends BaseBS<RptInputRewriteTempleInfo> {
	
	@Autowired
	private TempleBS templeBS;
	
	public RptInputRewriteTempleInfo getRptInputRewriteTempleInfoByTempleId(String tempId){

		StringBuilder jql = new StringBuilder("");
		// Map<String,Object>conditionMap=Maps.newHashMap();
		Map<String, Object> values = Maps.newHashMap();
		
		jql.append("select temple from RptInputRewriteTempleInfo temple where temple.id.templeId=:templeId");
		values.put("templeId", tempId);

//		SearchResult<RptInputLstTempleFile> authObjDefList = this.baseDAO
//				.findPageWithNameParam(firstResult, -1, jql.toString(), values);
		List<RptInputRewriteTempleInfo> tempList = this.baseDAO.findWithNameParm(jql.toString(), values);
		if(tempList!=null&&!tempList.isEmpty())
			return tempList.get(0);
		RptInputLstTempleInfoVO vo = templeBS.getTempInfoById(tempId);
		
		RptInputRewriteTempleInfo info = new RptInputRewriteTempleInfo();
		RptInputRewriteTempleInfoPK pk = new RptInputRewriteTempleInfoPK();
		pk.setDsId(vo.getDsId());
		info.setTableName("");
		info.setId(pk);
		return info;
	}
	

}
