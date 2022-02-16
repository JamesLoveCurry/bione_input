package com.yusys.bione.plugin.rptorggrp.repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.yusys.bione.comp.repository.mybatis.MyBatisRepository;
import com.yusys.bione.plugin.rptorggrp.entity.RptGrpOrgRel;
import com.yusys.bione.plugin.rptorggrp.entity.RptOrgGrp;
import com.yusys.bione.plugin.rptorggrp.web.vo.RptGrpVO;
import com.yusys.bione.plugin.rptorggrp.web.vo.RptOrgGrpVO;

@MyBatisRepository
public interface RptOrgGrpMybatisDao {
	public List<RptGrpVO> getCol(Map<String, Object> params);

	public void saveGroup(RptOrgGrp rpt);

	public void editGroup(RptOrgGrp rpt);

	public void delOrg(HashMap<String, Object> params);

	public RptOrgGrp getRptById(String groupId);

	public void delObj(String collectionId);

	public void saveObj(RptGrpOrgRel rpt);

	public ArrayList<String> searchbyId(String COLLECTION_ID);

	public void delObjs(HashMap<String, Object> params);

	public ArrayList<RptOrgGrp> checkName(Map<String, String> params);

	public List<RptOrgGrpVO> getOrgGrp(Map<String, Object> params);
	
	public List<RptOrgGrpVO> getUserOrgGrp(Map<String, String> params);
	
	public List<String> getMgrOrgNo(Map<String, String> params);
}
