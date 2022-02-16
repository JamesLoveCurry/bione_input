package com.yusys.bione.plugin.rptorg.repository;

import com.yusys.bione.comp.common.CommonTreeNode;
import com.yusys.bione.comp.repository.mybatis.MyBatisRepository;
import com.yusys.bione.frame.authobj.entity.BioneOrgInfo;
import com.yusys.bione.plugin.rptorg.entity.RptOrgInfo;
import com.yusys.bione.plugin.rptorg.web.vo.RptMgrFrsOrgSource;
import com.yusys.bione.plugin.rptorg.web.vo.RptOrgInfoVo;
import com.yusys.bione.plugin.wizard.web.vo.OrgImportVO;

import java.util.List;
import java.util.Map;
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
@MyBatisRepository
public interface RptOrgInfoMybatisDao {

	List<RptOrgInfo> findOrg(Map<String,Object> mp); //RptMgrFrsOrg全部信息
	
	List<RptOrgInfo> findOrgThen(Map<String,Object> mp); //过滤后的机构树

	RptOrgInfoVo findOrgNm(Map<String,Object> map);  //RptMgrFrsOrg和管理机构

	String findMgrNms(Map<String,Object> map);  //MgrOrgNo对应的MgrOrgNm

	RptOrgInfoVo getOrg(Map<String, Object> map); //去重OrgNm
	
	List<RptOrgInfoVo> getOrgList(Map<String, Object> map); //去重OrgNm

	void saveOrg(RptOrgInfoVo vo);  //新增

	void deleteOrg(Map<String, Object> model); //删除

	void updateOrg(RptMgrFrsOrgSource source);

	List<RptOrgInfo> searchTree(Map<String, Object> map);

	List<RptOrgInfo> findOrgUser(Map<String, Object> mapNo);

	List<RptOrgInfo> findOrgUserThen(Map<String, Object> mapNo);

	List<String> getOrgNoUser(Map<String, Object> mapMgr);
	
	List<RptOrgInfo> getOrgNoInfoUser(Map<String, Object> mapMgr);

	List<BioneOrgInfo> list(Map<String, Object> condition);

	List<String> findOrgNo(Map<String, Object> mapMgr);
	
	void updateOrg2(RptOrgInfo org);
	List<RptOrgInfo> findChildOrgs(Map<String, Object> map);

	List<RptOrgInfo> findFreeOrg(Map<String, Object> map);
	
	List<RptOrgInfo> getFrsOrgByNamespace(Map<String, Object> map);
	
	public List<RptOrgInfo> getChildOrg(Map<String,Object> params);
	
	public List<CommonTreeNode> findOrgNode(Map<String, Object> params);

	List<RptOrgInfo> findOrgBySumRel(Map<String, Object> params);

	List<RptOrgInfo> getSameLevelOrgNo(Map<String, Object> param);

	List<OrgImportVO> getExportInfo(Map<String, Object> map);

	List<RptOrgInfo> getChildOrgBySumRel(Map<String, Object> params);

}
