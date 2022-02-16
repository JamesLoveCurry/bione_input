package com.yusys.bione.plugin.frsorg.repository;

import java.util.List;
import java.util.Map;

import com.yusys.bione.comp.repository.mybatis.MyBatisRepository;
import com.yusys.bione.frame.authobj.entity.BioneOrgInfo;
import com.yusys.bione.plugin.frsorg.entity.RptFimAddrInfo;
import com.yusys.bione.plugin.frsorg.web.vo.RptMgrFrsOrgVo;
import com.yusys.bione.plugin.rptorg.entity.RptOrgInfo;
import com.yusys.bione.plugin.rptorg.web.vo.RptMgrFrsOrgSource;
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
public interface RptMgrFrsOrgMybatisDao {

	List<RptOrgInfo> findOrg(Map<String,Object> mp); //RptMgrFrsOrg全部信息
	
	List<RptOrgInfo> findOrgThen(Map<String,Object> mp); //过滤后的机构树

	RptMgrFrsOrgVo findOrgNm(Map<String,Object> map);  //RptMgrFrsOrg和管理机构

	String findMgrNms(Map<String,Object> map);  //MgrOrgNo对应的MgrOrgNm

	RptMgrFrsOrgVo getOrg(Map<String, Object> map); //去重OrgNm
	
	RptMgrFrsOrgVo getOrgAndAddr(Map<String, Object> map); //去重OrgNm edit by lxp 20160613 修改获取机构的

	void saveOrg(RptMgrFrsOrgVo vo);  //新增

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
	
	List<String> getConOrg(Map<String, Object> map);

	List<RptOrgInfo> isMaxOrg(String orgNo);
	/*----------  add by cl 2016-5-7---------------*/
	List<String> checkBandTask(Map<String, Object> queryObj);

	List<String> getConNmOrg(Map<String, Object> parme);

	List<RptOrgInfo> getSendMsgOrgTree();
	List<RptOrgInfo> getSendMsgOrgTreeLevel(Map<String,String> map);

	List<RptOrgInfo> getZFHSendMsgOrgTree();
	//修改RPT_FIM_ADDR_INFO add by lxp 2017 0608
	void updateAddrInfo(RptFimAddrInfo addInfo);
	//add by lxp 2017 0609 新增插入rpt_fim_addr_info
	void saveAddrOrgInfo(RptFimAddrInfo addInfo);
	//edit by lxp 0609 删除数据RPT_FIM_ADDR_INFO
	void deleteArrOrg(Map<String, Object> map);

	List<RptOrgInfo> findOrgThenBySumRel(Map<String, Object> map);

	List<RptOrgInfo> findOrgUserThenBySumRel(Map<String, Object> mapNo);
}
