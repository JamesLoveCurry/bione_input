package com.yusys.bione.plugin.rptorg.service;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.yusys.bione.comp.common.CommonTreeNode;
import com.yusys.bione.comp.service.BaseBS;
import com.yusys.bione.comp.utils.ArrayUtils;
import com.yusys.bione.comp.utils.EhcacheUtils;
import com.yusys.bione.comp.utils.SqlValidateUtils;
import com.yusys.bione.frame.authobj.entity.BioneOrgInfo;
import com.yusys.bione.frame.authobj.entity.BioneOrgInfoRel;
import com.yusys.bione.frame.base.common.GlobalConstants4frame;
import com.yusys.bione.frame.security.BioneSecurityUtils;
import com.yusys.bione.plugin.base.common.GlobalConstants4plugin;
import com.yusys.bione.plugin.base.utils.ReBuildParam;
import com.yusys.bione.plugin.frsorg.entity.RptFimAddrInfo;
import com.yusys.bione.plugin.rptmgr.util.SplitUtils;
import com.yusys.bione.plugin.rptorg.entity.RptOrgInfo;
import com.yusys.bione.plugin.rptorg.entity.RptOrgInfoPK;
import com.yusys.bione.plugin.rptorg.entity.UprOrgTypeInfo;
import com.yusys.bione.plugin.rptorg.repository.RptOrgInfoMybatisDao;
import com.yusys.bione.plugin.rptorg.web.vo.RptMgrFrsOrgSource;
import com.yusys.bione.plugin.rptorg.web.vo.RptOrgInfoVo;
import com.yusys.bione.plugin.wizard.web.vo.OrgImportVO;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;


@Service
@Transactional(readOnly = true)
public class RptOrgInfoBS extends BaseBS<Object>{

    @Autowired
	private  RptOrgInfoMybatisDao rptOrgInfoMybatisDao;
	
    private final String RPT_ORG_CACHE = "rptFrsOrgCache";
    
    private final String RPT_ORG_ALLCHILD_CACHE = "rptFrsOrgAllChildCache";
    
    private final String RPT_ORGREPORT_ALLCHILD_CACHE = "rptFrsOrgReportAllChildCache";
    
	public List<CommonTreeNode> getDataTree(String orgType, String upOrgNo,String orgNo,String path,String mgrOrgNo){
		return getDataTrees(orgType,upOrgNo,orgNo,path,mgrOrgNo,null);
	}

	public List<CommonTreeNode> getInnerOrgTree(String path,List<String> orgIds){
		List<CommonTreeNode> nodes = new ArrayList<CommonTreeNode>();
		nodes.add(generateRootNode(path));
		String jql = "select org from RptOrgInfo org where org.id.orgType = ?0 and org.id.orgNo in ?1 ";
		if(orgIds != null && orgIds.size() > 0){
			List<RptOrgInfo> orgInfos = this.baseDAO.findWithIndexParam(jql, GlobalConstants4plugin.RPT_FRS_BUSI_BANK,orgIds);
			if(orgInfos != null && orgInfos.size() > 0){
				for(RptOrgInfo info : orgInfos){
					nodes.add(generateOrgNode(path, info, GlobalConstants4plugin.RPT_FRS_BUSI_BANK,true));
				}
			}
		}
		return nodes;
	}
	
	public List<CommonTreeNode> getInnerOrgTree(String path, String orgType){
		List<CommonTreeNode> nodes = new ArrayList<CommonTreeNode>();
		nodes.add(mGenerateRootNode(path));
		if(StringUtils.isBlank(orgType)){
			orgType = GlobalConstants4plugin.RPT_FRS_BUSI_BANK;
		}
		if(BioneSecurityUtils.getCurrentUserInfo().isSuperUser()){
			String jql = "select org from RptOrgInfo org where org.id.orgType = ?0";
			List<RptOrgInfo> orgInfos = this.baseDAO.findWithIndexParam(jql, orgType);
			if(orgInfos != null && orgInfos.size() > 0){
				for(RptOrgInfo info : orgInfos){
					nodes.add(generateOrgNode(path, info, orgType,true));
				}
			}
		}
		else{
			List<String> orgNos = BioneSecurityUtils.getResIdListOfUser("AUTH_RES_RPTORG");
			List<String> upOrgNos = new ArrayList<String>();
			orgNos.add(BioneSecurityUtils.getCurrentUserInfo().getOrgNo());
			upOrgNos.add(BioneSecurityUtils.getCurrentUserInfo().getOrgNo());
			this.getChildOrg(upOrgNos, orgType, orgNos,new ArrayList<String>());
			String jql = "select org from RptOrgInfo org where org.id.orgType = :orgType ";
			Map<String,Object> params = new HashMap<String, Object>();
			params.put("orgType", orgType);
			List<List<?>> orgNoLists = ReBuildParam.splitLists(orgNos);
			jql += " and ( ";
			int i = 0;
			for(List<?> indexNoList : orgNoLists){
				jql += " org.id.orgNo in (:orgNoList"+i+") ";
				params.put("orgNoList"+i, indexNoList);
				if(i < orgNoLists.size() - 1){
					jql += " or ";
				}
				else{
					jql += " ) ";
				}
				i++;
			}
			List<RptOrgInfo> orgInfos = this.baseDAO.findWithNameParm(jql, params);
			if(orgInfos != null && orgInfos.size() > 0){
				for(RptOrgInfo info : orgInfos){
					nodes.add(generateOrgNode(path, info, orgType,orgNos.contains(info.getUpOrgNo())));
				}
			}
		}
		return nodes;
	}
	
	/**
	 * ????????????????????????
	 * @param orgNo ????????????
	 * @param orgType ????????????
	 * @param isAllChild ????????????????????????
	 * @param isContainFa ??????????????????
	 * @return
	 */
	public List<RptOrgInfo> findOrgChild(String orgNo, String orgType, Boolean isAllChild, Boolean isContainFa){
		List<RptOrgInfo> org = new ArrayList<RptOrgInfo>();
		List<String> upOrg = new ArrayList<String>();
		upOrg.add(orgNo);
		if(StringUtils.isBlank(orgType)){
			orgType = GlobalConstants4plugin.RPT_FRS_BUSI_BANK;
		}
		RptOrgInfoPK id = new RptOrgInfoPK();
		id.setOrgNo(orgNo);
		id.setOrgType(orgType);
		RptOrgInfo rootorg = this.getEntityById(RptOrgInfo.class, id);
		if(rootorg != null && isContainFa){
			org.add(rootorg);
		}
		while(upOrg!=null && upOrg.size()>0){
			//????????????????????????????????????????????????????????????????????????
			String paramjql = "select param.paramValue from BioneParamInfo param where param.paramTypeNo = 'OrgTreeType'";
			List<String> paramValues = this.baseDAO.findWithIndexParam(paramjql);
			List<RptOrgInfo> upOrgInfos = new ArrayList<RptOrgInfo>();
			if(paramValues != null && paramValues.size() > 0){
				if("Y".equals(paramValues.get(0))){
					Map<String,Object> params = new HashMap<String, Object>();
					params.put("orgType",orgType);
					params.put("upOrgNo",upOrg);
					upOrgInfos = this.rptOrgInfoMybatisDao.getChildOrgBySumRel(params);
				}else{
					List<List<String>> orgnoss = splitStringBy1000(upOrg);
					String jql = "select org from RptOrgInfo org where org.id.orgType = :orgType";
					Map<String,Object> params = new HashMap<String, Object>();
					params.put("orgType",orgType);
					jql+= " and ( ";
					for(int i=0; i < orgnoss.size();i++){
						jql +=" org.upOrgNo in (:orgNo"+i+")";
						if(i < orgnoss.size()-1){
							jql += " or ";
						}
						else{
							jql += " ) ";
						}
						params.put("orgNo"+i, orgnoss.get(i));
					}
					upOrgInfos = this.baseDAO.findWithNameParm(jql, params);
				}
			}else {
				List<List<String>> orgnoss = splitStringBy1000(upOrg);
				String jql = "select org from RptOrgInfo org where org.id.orgType = :orgType";
				Map<String,Object> params = new HashMap<String, Object>();
				params.put("orgType",orgType);
				jql+= " and ( ";
				for(int i=0; i < orgnoss.size();i++){
					jql +=" org.upOrgNo in (:orgNo"+i+")";
					if(i < orgnoss.size()-1){
						jql += " or ";
					}
					else{
						jql += " ) ";
					}
					params.put("orgNo"+i, orgnoss.get(i));
				}
				upOrgInfos = this.baseDAO.findWithNameParm(jql, params);
			}
			upOrg = new ArrayList<String>();
			if(upOrgInfos!=null && upOrgInfos.size()>0){
				org.addAll(upOrgInfos);
				if(isAllChild){
					for(RptOrgInfo upOrgInfo : upOrgInfos){
						upOrg.add(upOrgInfo.getId().getOrgNo());
					}
				}
			}
			else{
				break;
			}
		}
		return org;
	}
	
	/**
	 * ?????????????????????1000?????????list???
	 * @param orgList
	 * @return
	 */
	public static List<List<String>> splitStringBy1000(List<String> orgList){
		List<List<String>> orgNosParam = new ArrayList<List<String>>();
		int count = orgList.size()/1000 + (orgList.size() % 1000 == 0 ? 0 : 1);
		
		for(int i=0;i<count;i++){
			orgNosParam.add(orgList.subList(i * 1000, ((i+1) * 1000 > orgList.size() ? orgList.size() : (i + 1) * 1000)));
		}
		return orgNosParam;
	}
	
	/**
	 * ???????????????
	 * @param path
	 * @return
	 */
	private CommonTreeNode mGenerateRootNode(String path){
		Map<String,String> oneParams=new HashMap<String, String>();
		CommonTreeNode oneNode = new CommonTreeNode();
		oneNode.setId(GlobalConstants4frame.TREE_ROOT_NO);
		oneNode.setText("?????????");
		oneNode.setIcon(path+"/"+GlobalConstants4frame.ICON_URL+"/house.png");
		oneNode.setIsParent(true);
		oneParams.put("orgNo",GlobalConstants4frame.TREE_ROOT_NO);
		oneNode.setParams(oneParams);
		return oneNode;
	}
	
	public List<CommonTreeNode> getInnerOrgTree(String path){
		List<CommonTreeNode> nodes = new ArrayList<CommonTreeNode>();
		nodes.add(generateRootNode(path));
		if(BioneSecurityUtils.getCurrentUserInfo().isSuperUser()){
			String jql = "select org from RptOrgInfo org where org.id.orgType = ?0";
			List<RptOrgInfo> orgInfos = this.baseDAO.findWithIndexParam(jql, GlobalConstants4plugin.RPT_FRS_BUSI_BANK);
			if(orgInfos != null && orgInfos.size() > 0){
				for(RptOrgInfo info : orgInfos){
					nodes.add(generateOrgNode(path, info, GlobalConstants4plugin.RPT_FRS_BUSI_BANK,true));
				}
			}
		}
		else{
			List<String> orgNos = BioneSecurityUtils.getResIdListOfUser("AUTH_RES_RPTORG");
			List<String> upOrgNos = new ArrayList<String>();
			orgNos.add(BioneSecurityUtils.getCurrentUserInfo().getOrgNo());
			upOrgNos.add(BioneSecurityUtils.getCurrentUserInfo().getOrgNo());
			this.getChildOrg(upOrgNos, GlobalConstants4plugin.RPT_FRS_BUSI_BANK, orgNos,new ArrayList<String>());
			String jql = "select org from RptOrgInfo org where org.id.orgType = :orgType ";
			Map<String,Object> params = new HashMap<String, Object>();
			params.put("orgType", GlobalConstants4plugin.RPT_FRS_BUSI_BANK);
			List<List<?>> orgNoLists = ReBuildParam.splitLists(orgNos);
			jql += " and ( ";
			int i = 0;
			for(List<?> indexNoList : orgNoLists){
				jql += " org.id.orgNo in (:orgNoList"+i+") ";
				params.put("orgNoList"+i, indexNoList);
				if(i < orgNoLists.size() - 1){
					jql += " or ";
				}
				else{
					jql += " ) ";
				}
				i++;
			}
			List<RptOrgInfo> orgInfos = this.baseDAO.findWithNameParm(jql, params);
			if(orgInfos != null && orgInfos.size() > 0){
				for(RptOrgInfo info : orgInfos){
					nodes.add(generateOrgNode(path, info, GlobalConstants4plugin.RPT_FRS_BUSI_BANK,orgNos.contains(info.getUpOrgNo())));
				}
			}
		}
		return nodes;
	}
	
	public Map<String,List<String>> getValidateOrgInfo(){
		 Map<String,List<String>> validateMap = new HashMap<String, List<String>>();
		 List<String> vorgNos = BioneSecurityUtils.getResIdListOfUser("AUTH_RES_RPTORG");
		 List<String> orgNos = BioneSecurityUtils.getResIdListOfUser("AUTH_RES_RPTORG");
		 List<String> upOrgNos = new ArrayList<String>();
		 List<String> curorgId = BioneSecurityUtils.getCurrentUserInfo().getAuthObjMap().get(GlobalConstants4frame.AUTH_OBJ_DEF_ID_ORG);
		 List<String> curorg = new ArrayList<String>();
		 if(curorgId !=null && curorgId.size() >0){
			List<List<String>> curorgIds = splitStringBy1000(curorgId);
			String jql = "select org.orgNo from BioneOrgInfo org where 1=1 ";
			Map<String,Object> params = new HashMap<String, Object>();
			jql+= " and ( ";
			for(int i=0; i < curorgIds.size();i++){
				jql +=" org.orgId in (:orgNo"+i+")";
				if(i < curorgIds.size()-1){
					jql += " or ";
				}
				else{
					jql += " ) ";
				}
				params.put("orgNo"+i, curorgIds.get(i));
			 }
			 curorg = this.baseDAO.findWithNameParm(jql, params);
		 }
		 if(curorg != null && curorg.size() > 0){
			 upOrgNos.addAll(curorg);
		 }
		 orgNos.addAll(curorg);
		 vorgNos.removeAll(curorg);
		 validateMap.put("other", vorgNos);
		 validateMap.put("validate", orgNos);
		 return validateMap;
	}
	
	public Map<String,List<String>> getValidateOrgInfo(String orgType,String date){
		 Map<String,List<String>> validateMap = new HashMap<String, List<String>>();
		 List<String> vorgNos = BioneSecurityUtils.getResIdListOfUser("AUTH_RES_RPTORG");
		 List<String> orgNos = BioneSecurityUtils.getResIdListOfUser("AUTH_RES_RPTORG");
		 List<String> upOrgNos = new ArrayList<String>();
		 List<String> curorgId = BioneSecurityUtils.getCurrentUserInfo().getAuthObjMap().get(GlobalConstants4frame.AUTH_OBJ_DEF_ID_ORG);
		 if(curorgId == null){
			 curorgId = new ArrayList<String>();
		 }
		 curorgId.add(BioneSecurityUtils.getCurrentUserInfo().getOrgNo());
		 String jql = "select org.orgNo from BioneOrgInfo org where org.orgId in ?0";
		 List<String> curorg = new ArrayList<String>();
		 List<BioneOrgInfoRel> rels = this.getAllEntityList(BioneOrgInfoRel.class, "id.orgNo", false);
		 Map<String,String> orgRelMap = new HashMap<String, String>();
		 if(rels != null && rels.size() > 0){
			 for(BioneOrgInfoRel rel : rels){
				 orgRelMap.put(rel.getId().getOrgNo(), rel.getId().getRelOrgNo());
			 }
		 }
		 List<String> rorgIds  = new ArrayList<String>();
		 if(curorgId !=null && curorgId.size() >0){
			 for(String corgId : curorgId){
				 if(orgRelMap.get(corgId)!=null){
					 rorgIds.add(orgRelMap.get(corgId));
				 }
			 }
			 curorg = this.baseDAO.findWithIndexParam(jql, curorgId);
			 curorg.addAll(rorgIds);
		 }
		 if(curorg != null && curorg.size() > 0){
			 upOrgNos.addAll(curorg);
			 orgNos.addAll(curorg);
		 }
		 vorgNos.removeAll(curorg);
		 vorgNos.addAll(rorgIds);
		 if(StringUtils.isNotBlank(orgType)){
			 orgType = "01";
		 }
		 this.getChildOrg(upOrgNos, orgType, orgNos,vorgNos,date);
		 validateMap.put("other", vorgNos);
		 validateMap.put("validate", orgNos);
		 return validateMap;
	}
	
	private void getChildOrg(List<String> upOrgNos,String orgType,List<String> orgNos,List<String> vorgNos,String date){
		if(upOrgNos != null && upOrgNos.size()>0){
			Map<String,Object> params = new HashMap<String, Object>();
			String jql = "select org.id.orgNo from UprOrgInfo org where org.id.orgType = :orgType ";
			params.put("orgType", orgType);
			BigDecimal versionId = null;
			if(StringUtils.isNotBlank(date)){
				String jql1 = "select type.id.versionId from UprOrgTypeInfo type where type.startDt <= ?0 and type.endDt > ?0";
				List<BigDecimal> versionIds = this.baseDAO.findWithIndexParam(jql1, date);
				if(versionIds != null && versionIds.size()>0){
					versionId = versionIds.get(0);
				}
			}
			if(date == null){
				String jql1 = "select type.id.verId from UprOrgTypeInfo type where type.endDt = ?0 ";
				List<BigDecimal> versionIds = this.baseDAO.findWithIndexParam(jql1, "29991231");
				if(versionIds != null && versionIds.size()>0){
					versionId = versionIds.get(0);
				}
			}
			if(versionId == null){
				versionId = new BigDecimal(1);
			}
			
			jql += " and org.id.versionId = :versionId";
			params.put("versionId", versionId);
			List<List<String>> nos = SplitUtils.toDbList(upOrgNos);
			jql += " and (";
			for(int i =0 ;i < nos.size();i++){
				jql += " org.upOrgNo in (:upOrgNos"+i+") ";
				params.put("upOrgNos"+i, nos.get(i));
				if(i < nos.size()-1){
					jql += " or ";
				}
				else{
					jql += ")";
				}
			}
			upOrgNos = this.baseDAO.findWithNameParm(jql, params);
			if(upOrgNos != null && upOrgNos.size()>0){
				orgNos.addAll(upOrgNos);
				for(String uporgNo : upOrgNos){
					vorgNos.remove(uporgNo);
				}
				getChildOrg(upOrgNos, orgType, orgNos,vorgNos,date);
			}
		}
	}
	
	private void getChildOrg(List<String> upOrgNos,String orgType,List<String> orgNos,List<String> vorgNos){
		if(upOrgNos != null && upOrgNos.size()>0){
			
			Map<String,Object> params = new HashMap<String, Object>();
			String jql = "select org.id.orgNo from RptOrgInfo org where org.id.orgType = :orgType and org.upOrgNo in (:upOrgNos)";
			params.put("orgType", GlobalConstants4plugin.RPT_FRS_BUSI_BANK);
			params.put("upOrgNos", upOrgNos);
			
			upOrgNos = this.baseDAO.findWithNameParm(jql, params);
			if(upOrgNos != null && upOrgNos.size()>0){
				orgNos.addAll(upOrgNos);
				for(String uporgNo : upOrgNos){
					vorgNos.remove(uporgNo);
				}
				getChildOrg(upOrgNos, orgType, orgNos,vorgNos);
			}
		}
		
	}
	
	/**
	 * ???????????????
	 * @param path
	 * @return
	 */
	private CommonTreeNode generateRootNode(String path){
		Map<String,String> oneParams=new HashMap<String, String>();
		CommonTreeNode oneNode = new CommonTreeNode();
		oneNode.setId(GlobalConstants4frame.TREE_ROOT_NO);
		oneNode.setText("?????????");
		oneNode.setIcon(path+"/"+GlobalConstants4frame.ICON_URL+"/house.png");
		oneNode.setIsParent(true);
		oneParams.put("orgType",GlobalConstants4plugin.RPT_FRS_BUSI_BANK);
		oneParams.put("orgNo",GlobalConstants4frame.TREE_ROOT_NO);
		oneNode.setParams(oneParams);
		return oneNode;
	}
	
	private CommonTreeNode generateOrgNode(String path,RptOrgInfo info,String orgType,boolean flag){
		CommonTreeNode treeNode = new CommonTreeNode();
		treeNode.setIcon(path+"/"+GlobalConstants4frame.ICON_URL+"/application_view_detail.png");
		treeNode.setId(info.getId().getOrgNo());
		treeNode.setText(info.getOrgNm());
		if(flag)
			treeNode.setUpId(info.getUpOrgNo());
		else
			treeNode.setUpId("0");
		Map<String, String> params = new HashMap<String, String>();
		params.put("orgNo", info.getId().getOrgNo());
		params.put("upOrgNo", info.getUpOrgNo());
		params.put("orgNm", info.getOrgNm());
		params.put("orgType",orgType);
		params.put("id",info.getId().getOrgNo());
		params.put("resDefNo",
				com.yusys.bione.plugin.base.common.GlobalConstants4plugin.RPTORG_RES_NO);
		treeNode.setParams(params);
		return treeNode;
	}
	
	//?????????
	public List<CommonTreeNode> getDataTrees(String orgType, String upOrgNo,String orgNo,String path,String mgrOrgNo,String state) {
		//String userOrgNo = BioneSecurityUtils.getCurrentUserInfo().getOrgNo();
		Map<String,Object> maps =new HashMap<String,Object>();
		maps.put("paramTypeNo", GlobalConstants4plugin.TREE_PARAM_TYPE);
		List<CommonTreeNode> node = Lists.newArrayList();
		if(orgNo == null){
			Map<String,String> oneParams=new HashMap<String, String>();
			CommonTreeNode oneNode = new CommonTreeNode();
			oneNode.setId(GlobalConstants4frame.TREE_ROOT_NO);
			oneNode.setText("?????????");
			oneNode.setIcon(path+"/"+GlobalConstants4frame.ICON_URL+"/house.png");
			oneNode.setIsParent(true);
			oneParams.put("orgType",GlobalConstants4plugin.RPT_FRS_BUSI_BANK);
			oneParams.put("orgNo",GlobalConstants4frame.TREE_ROOT_NO);
			oneNode.setParams(oneParams);
			node.add(oneNode);
		}
		else{
			boolean isSuper = BioneSecurityUtils.getCurrentUserInfo().isSuperUser(); //true????????????;false???????????????
			if(isSuper == true){
				Map<String,Object> map = new HashMap<String,Object>();
				map.put("upOrgNo",orgNo);
				map.put("orgType", orgType);
				
				List<RptOrgInfo> list = Lists.newArrayList();
 				if(state != null && !state.equals("")){
					if(state.equals("1")){
						list = this.rptOrgInfoMybatisDao.findOrg(map);
						list = getNotFree(list,state);
					}else{
						list = this.rptOrgInfoMybatisDao.findOrgThen(map);
						list = getNotFree(list,state);
					}
				}else{
					list = this.rptOrgInfoMybatisDao.findOrgThen(map);
					list = getNotFree(list,state);
				}
				for(int i = 0;i<list.size();i++){
					CommonTreeNode treeNode = new CommonTreeNode();
					if(list.get(i).getId().getOrgType().equals(orgType)){
						treeNode.setIcon(path+"/"+GlobalConstants4frame.ICON_URL+"/application_view_detail.png");
						treeNode.setId(list.get(i).getId().getOrgNo());
						treeNode.setText(list.get(i).getOrgNm());
						treeNode.setUpId(list.get(i).getFinanceOrgNo());
						Map<String, String> params = new HashMap<String, String>();
						params.put("orgNo", list.get(i).getId().getOrgNo());
						params.put("upOrgNo", list.get(i).getUpOrgNo());
						params.put("orgNm", list.get(i).getOrgNm());
						params.put("orgType",orgType);
						params.put("mgrOrgNo", list.get(i).getMgrOrgNo());
						params.put("isVirtualOrg", list.get(i).getIsVirtualOrg());
						treeNode.setParams(params);
						treeNode.setIsParent(true);
						node.add(treeNode);
					}
				}
			}else{//???????????????
				if(orgNo.equals("0")){ //????????? ?????????????????????
					String userMgrNo = BioneSecurityUtils.getCurrentUserInfo().getOrgNo();
					Map<String ,Object> mapMgr = new HashMap<String,Object>();
					mapMgr.put("mgrOrgNo", userMgrNo);
					mapMgr.put("orgType", orgType);
					List<String> orgNoUser = this.rptOrgInfoMybatisDao.getOrgNoUser(mapMgr); //????????????orgNo
					//???????????????????????????
					Map<String,Object> mapUpNo = new HashMap<String,Object>();
					mapUpNo.put("orgType", orgType);
					List<RptOrgInfo> userUpNo = this.rptOrgInfoMybatisDao.findOrg(mapUpNo); //??????????????????
					Map<String, RptOrgInfo> map = new HashMap<String, RptOrgInfo>();
					for(RptOrgInfo org : userUpNo){
						map.put(org.getId().getOrgNo(), org);
					}
					Map<String,String> MapNo = new HashMap<String,String>(); //???????????????Map
					
					for(int i = 0;i<orgNoUser.size();i++){
						String orgNos = ""; //??????
						RptOrgInfo org = map.get(orgNoUser.get(i));
						orgNos += org.getId().getOrgNo();
						while(map.get(org.getUpOrgNo()) != null){ 
							org = map.get(org.getUpOrgNo());
							orgNos += "," +org.getId().getOrgNo() ;
						}
						if(!org.getUpOrgNo().equals(" ")){
							MapNo.put(orgNoUser.get(i), orgNos+",");
						}
					}
					List<String> listNos=new ArrayList<String>();
					List<String> clistNos=new ArrayList<String>();
					for(String qorgNo:MapNo.keySet()){
						clistNos.clear();
						if(listNos.size()>0){
							boolean flag=true;
							for(String corgNo:listNos){
								if(MapNo.get(corgNo).indexOf(MapNo.get(qorgNo))>=0){
									clistNos.add(corgNo);
								}
								if(MapNo.get(qorgNo).indexOf(MapNo.get(corgNo))>=0){
									flag=false;
									break;
								}
							}
							if(flag){
								listNos.add(qorgNo);
							}
							if(clistNos.size()>0){
								for(String corg:clistNos){
									listNos.remove(corg);
								}
							}
						}
						else{
							listNos.add(qorgNo);
						}
					}
					if(userMgrNo != null){
						Map<String,Object> mapNo = new HashMap<String,Object>();
						mapNo.put("orgNo", ReBuildParam.toDbList(listNos));
						mapNo.put("orgType", orgType);
						List<RptOrgInfo> list = Lists.newArrayList();
						if (listNos != null && listNos.size() > 0) {
							if(state != null && !state.equals("")){
								if(state.equals("1")){
									list = this.rptOrgInfoMybatisDao.findOrgUser(mapNo);
								}else{
									list = this.rptOrgInfoMybatisDao.findOrgUserThen(mapNo);
									list = getNotFree(list,state);
								}
							}else{
								list = this.rptOrgInfoMybatisDao.findOrgUserThen(mapNo);
								list = getNotFree(list,state);
							}
						} else{
							list = Lists.newArrayList();
						}
						for(int i = 0;i<list.size();i++){
							CommonTreeNode treeNode = new CommonTreeNode();
							if(list.get(i).getId().getOrgType().equals(orgType)){
								treeNode.setIcon(path+"/"+GlobalConstants4frame.ICON_URL+"/application_view_detail.png");
								treeNode.setId(list.get(i).getId().getOrgNo());
								treeNode.setText(list.get(i).getOrgNm());
								treeNode.setUpId(list.get(i).getFinanceOrgNo());
								Map<String, String> params = new HashMap<String, String>();
								params.put("orgNo", list.get(i).getId().getOrgNo());
								params.put("upOrgNo", list.get(i).getUpOrgNo());
								params.put("orgNm", list.get(i).getOrgNm());
								params.put("orgType",orgType);
								params.put("mgrOrgNo", list.get(i).getMgrOrgNo());
								params.put("isVirtualOrg", list.get(i).getIsVirtualOrg());
								treeNode.setParams(params);
								treeNode.setIsParent(true);
								node.add(treeNode);
							}
						}
					}
				}else{
					Map<String,Object> map = new HashMap<String,Object>();
					map.put("upOrgNo",orgNo);
					map.put("orgType", orgType);
					List<RptOrgInfo> list = Lists.newArrayList();
					if(state != null && !state.equals("")){
						if(state.equals("1")){
							list = this.rptOrgInfoMybatisDao.findOrg(map);
						}else{
							list = this.rptOrgInfoMybatisDao.findOrgThen(map);
						}
					}else{
						list = this.rptOrgInfoMybatisDao.findOrgThen(map);
					}
					for(int i = 0;i<list.size();i++){
						CommonTreeNode treeNode = new CommonTreeNode();
						if(list.get(i).getId().getOrgType().equals(orgType)){
							treeNode.setIcon(path+"/"+GlobalConstants4frame.ICON_URL+"/application_view_detail.png");
							treeNode.setId(list.get(i).getId().getOrgNo());
							treeNode.setText(list.get(i).getOrgNm());
							treeNode.setUpId(list.get(i).getFinanceOrgNo());
							Map<String, String> params = new HashMap<String, String>();
							params.put("orgNo", list.get(i).getId().getOrgNo());
							params.put("upOrgNo", list.get(i).getUpOrgNo());
							params.put("orgNm", list.get(i).getOrgNm());
							params.put("orgType",orgType);
							params.put("mgrOrgNo", list.get(i).getMgrOrgNo());
							params.put("isVirtualOrg", list.get(i).getIsVirtualOrg());
							treeNode.setParams(params);
							treeNode.setIsParent(true);
							node.add(treeNode);
						}
					}
				}
				
			}
		}
		return node;
	}
	
	//??????????????????
	public List<RptOrgInfo> getNotFree(List<RptOrgInfo> list,String state){
		if(state == null){
			for(int i = 0;i<list.size();i++){
				if(list.get(i).getUpOrgNo().equals(" ")){
					list.remove(i);
				}
			}
		}
		return list;
	}
	
	//????????????????????????????????????

	//?????????????????????
	public RptOrgInfoVo showAll(String orgNo,String orgType,String upOrgNo,String mgrOrgNo) {
		// TODO Auto-generated method stub
		//?????????????????????????????????Map
		Map<String, Object> map = new HashMap<String , Object>();
		RptOrgInfoVo vo = new RptOrgInfoVo();
		map.put("orgNo", orgNo);
		map.put("orgType",orgType);
		map.put("upOrgNo", upOrgNo);
		//???????????????????????????Map
		Map<String,Object> maps = new HashMap<String,Object>();
		maps.put("orgNo", mgrOrgNo);
		String name = this.rptOrgInfoMybatisDao.findMgrNms(maps);
		RptOrgInfoVo v = this.rptOrgInfoMybatisDao.findOrgNm(map);
		vo.setFinanceOrgNo(v.getFinanceOrgNo());
		vo.setId(v.getId());
		vo.setMgrOrgNm(name);
		vo.setMgrOrgNo(v.getMgrOrgNo());
		vo.setOrgNm(v.getOrgNm());
		vo.setOrgSumType(v.getOrgSumType());
		vo.setUpOrgNo(v.getUpOrgNo());
		return vo;
	}

	//??????
	public boolean getOrgNm(String orgType, String orgNm,String sourceOrgNo) {
		Map<String ,Object> map = new HashMap<String, Object>();
		if(!StringUtils.isEmpty(sourceOrgNo)){
			map.put("sourceOrgNo", sourceOrgNo);
		}
		map.put("orgType",orgType);
		map.put("orgNm",orgNm);
		RptOrgInfo org = this.rptOrgInfoMybatisDao.getOrg(map);
		if(org != null){
			return false;
		}else{
			return true;
		}
		
	}

	//??????
	public boolean getOrgNo(String orgType, String orgNo,String sourceOrgNo) {
		Map<String ,Object> map = new HashMap<String, Object>();
		if(!StringUtils.isEmpty(sourceOrgNo)){
			map.put("sourceOrgNo", sourceOrgNo);
		}
		map.put("orgType",orgType);
		map.put("orgNo", orgNo);
		RptOrgInfo org = this.rptOrgInfoMybatisDao.getOrg(map);
		if(org != null){
			return false;
		}else{
			return true;
		}
			
	}

	//??????
	public boolean getFinanceOrgNo(String orgType, String financeOrgNo,String sourceOrgNo) {
		// TODO Auto-generated method stub
		Map<String ,Object> map = new HashMap<String, Object>();
		if(!StringUtils.isEmpty(sourceOrgNo)){
			map.put("sourceOrgNo", sourceOrgNo);
		}
		map.put("financeOrgNo", financeOrgNo);
		RptOrgInfo org = this.rptOrgInfoMybatisDao.getOrg(map);
		if(org != null){
			return false;
		}else{
			return true;
		}
	}

	public void addOrg(RptOrgInfoVo vo) {
		String orgNo = vo.getUpOrgNo();
		String orgType = vo.getId().getOrgType();
		Map<String,Object> map = new HashMap<String,Object>();
		map.put("orgNo", orgNo);
		map.put("orgType", orgType);
		List<RptOrgInfo> list = this.rptOrgInfoMybatisDao.findOrg(map);
		if(list != null && list.size() !=0){
			String namespace = list.get(0).getNamespace() + vo.getId().getOrgNo() + "/";
			vo.setNamespace(namespace);
		}
		this.rptOrgInfoMybatisDao.saveOrg(vo);
		
	}

	public void deleteOrg(String orgType,String orgNo) {
		// TODO Auto-generated method stub
		Map<String,Object> map = new HashMap<String,Object>();
		RptOrgInfoPK pk = new RptOrgInfoPK();
		pk.setOrgNo(orgNo);
		pk.setOrgType(orgType);	
		map.put("id", pk);
		this.rptOrgInfoMybatisDao.deleteOrg(map);
	}

	public List<RptOrgInfo> getListOrg(String orgType,String orgNo) {
		Map<String ,Object> map = new HashMap<String, Object>();
		map.put("orgType",orgType);
		map.put("upOrgNo",orgNo);
		List<RptOrgInfo> list = this.rptOrgInfoMybatisDao.findOrg(map);
		return list;
	}

	public RptOrgInfoVo getOrgInfo(String orgNo,String orgType) {
		// TODO Auto-generated method stub
		Map<String , Object> map = new HashMap<String , Object>();
		map.put("orgNo", orgNo);
		map.put("orgType", orgType);
		RptOrgInfoVo org = this.rptOrgInfoMybatisDao.getOrg(map);
		RptOrgInfoPK pk = new RptOrgInfoPK();
		Map<String , Object> maps = new HashMap<String , Object>();
		maps.put("orgType", orgType);
		maps.put("orgNo", org.getMgrOrgNo());
		String name = this.rptOrgInfoMybatisDao.findMgrNms(maps);
		pk.setOrgNo(orgNo);
		pk.setOrgType(orgType);
		org.setMgrOrgNm(name);
		org.setId(pk);
		
		Map<String, Object> condition = new HashMap<String, Object>();
		condition.put("orgNo", org.getUpOrgNo());
		condition.put("orgType", orgType);
		List<RptOrgInfo> list = this.rptOrgInfoMybatisDao.findOrg(condition);
		if(list == null || list.size() == 0){
			org.setUpOrgNm("???");
			
		}
		if(list != null && list.size() == 1){
			org.setUpOrgNm(list.get(0).getOrgNm());
		}
		return org;
	}

	public void updateOrg(RptOrgInfoVo vo, String orgNo, String sourceOrgNo) {
		// TODO Auto-generated method stub
		RptMgrFrsOrgSource source = new RptMgrFrsOrgSource();
		source.setFinanceOrgNo(vo.getFinanceOrgNo());
		String type = vo.getId().getOrgType();
		RptOrgInfoPK pk = new RptOrgInfoPK();
		pk.setOrgNo(orgNo);
		pk.setOrgType(type);
		source.setId(pk);
		source.setMgrOrgNo(vo.getMgrOrgNo());
		source.setOrgNm(vo.getOrgNm());
		source.setOrgSumType(vo.getOrgSumType());
		source.setUpOrgNo(vo.getUpOrgNo());
		source.setSourceOrgNo(sourceOrgNo);
		source.setIsVirtualOrg(vo.getIsVirtualOrg());
		this.rptOrgInfoMybatisDao.updateOrg(source);
	}
	
	/**
	 * ?????????????????????????????????????????????????????????
	 * @param vo
	 * @param orgNo
	 * @param sourceOrgNo
	 * @param laseUpOrgNo
	 */
	public void updateOrgChangeUpNo(RptOrgInfoVo vo, String orgNo, String sourceOrgNo, String laseUpOrgNo){
		RptOrgInfo newOrg = new RptOrgInfo();
		RptOrgInfoPK pk = new RptOrgInfoPK();
		pk.setOrgNo(orgNo);
		pk.setOrgType(vo.getId().getOrgType());
		newOrg.setFinanceOrgNo(vo.getFinanceOrgNo());
		newOrg.setId(pk);
		newOrg.setMgrOrgNo(vo.getMgrOrgNo());
		newOrg.setOrgNm(vo.getOrgNm());
		newOrg.setOrgSumType(vo.getOrgSumType());
		newOrg.setUpOrgNo(vo.getUpOrgNo());
		newOrg.setIsVirtualOrg(vo.getIsVirtualOrg());
		
		
		Map<String, Object> condition = new HashMap<String, Object>();
		condition.put("orgNo", vo.getUpOrgNo());
		condition.put("orgType", vo.getId().getOrgType());
		RptOrgInfo upOrg = new RptOrgInfo();
		if(!vo.getUpOrgNo().equals("0")){
			upOrg = this.rptOrgInfoMybatisDao.findOrg(condition).get(0);//??????????????????
		}
		
		condition.clear();
		condition.put("orgNo", "%/" + orgNo + "/%");
		condition.put("orgType", vo.getId().getOrgType());
		List<RptOrgInfo> childOrgList = this.rptOrgInfoMybatisDao.findChildOrgs(condition);
		
		Map<String, RptOrgInfo> map = new HashMap<String, RptOrgInfo>();
		for(RptOrgInfo tmp : childOrgList){
			map.put(tmp.getId().getOrgNo(), tmp);
		}
		for(int i=0;i<childOrgList.size();i++){
			RptOrgInfo org = childOrgList.get(i);
			List<String> namespaceList = new ArrayList<String>();
			namespaceList.add(org.getId().getOrgNo());
			while(map.get(org.getUpOrgNo()) != null){
				org = map.get(org.getUpOrgNo());
				namespaceList.add(org.getId().getOrgNo());
			}
			String namespace = "";
			for(int j=namespaceList.size(); j>0 ; j--){
				namespace += namespaceList.get(j-1) + "/";
			}
			
			org = childOrgList.get(i);
			org.setNamespace(upOrg.getNamespace() + namespace);
			if(!org.getId().getOrgNo().equals(newOrg.getId().getOrgNo())){
				this.rptOrgInfoMybatisDao.updateOrg2(org);
			}
		}
		newOrg.setNamespace(upOrg.getNamespace() + newOrg.getId().getOrgNo() + "/");
		this.rptOrgInfoMybatisDao.updateOrg2(newOrg);
	}
	//????????????????????????  created by wangyp
	public List<CommonTreeNode> getSetTree(String orgType){  //1:????????? (??????),0:?????????
	    List<CommonTreeNode>list=Lists.newArrayList();
		CommonTreeNode treeNode=new CommonTreeNode();
	    treeNode.setId(GlobalConstants4frame.TREE_ROOT_NO);
		if(orgType.equals(GlobalConstants4plugin.RPT_FRS_BUSI_PBC)){
			treeNode.setText("????????????");
		}
	    else{
	    	treeNode.setText("????????????");
	    }
		list.add(treeNode);
		this.getAllTree(list, orgType, GlobalConstants4frame.TREE_ROOT_NO, treeNode);
		return list;
	}
	
	//also created by wangyp
	public void getAllTree(List<CommonTreeNode>list,String orgType,String upOrgNo,CommonTreeNode treeNode){
		Map<String,Object> map = new HashMap<String,Object>();
		map.put("upOrgNo",upOrgNo);
		map.put("orgType", orgType);
		List<RptOrgInfo> lists = this.rptOrgInfoMybatisDao.findOrg(map);
		if(lists.size()>0){
			treeNode.setIsParent(true);
			for(int i=0;i<lists.size();i++){
				RptOrgInfo rpt=lists.get(i);
				CommonTreeNode ct=new CommonTreeNode();
				ct.setId(rpt.getId().getOrgNo());
				ct.setUpId(upOrgNo);
				ct.setText(rpt.getOrgNm());
				list.add(ct);
				this.getAllTree(list, rpt.getId().getOrgType(), rpt.getId().getOrgNo(), ct);
			}
		}
		else{
			treeNode.setIsParent(false);
		}
	}

	//?????????
	public List<CommonTreeNode> searchTree(String orgNm,String path) {
		Map<String , Object> map = new HashMap<String,Object>();
		String orgNms = "%"+orgNm+"%";
		map.put("orgNm", orgNms);
		List<CommonTreeNode> nodes = Lists.newArrayList();
		List<RptOrgInfo> list = this.rptOrgInfoMybatisDao.searchTree(map);
		if(list != null){
			for(int i = 0;i<list.size();i++){
				CommonTreeNode treeNode = new CommonTreeNode();
				if(list.get(i).getId().getOrgType().equals(GlobalConstants4plugin.RPT_FRS_BUSI_PBC)){
					treeNode.setIcon(path+"/"+GlobalConstants4frame.ICON_URL+"/application_view_detail.png");
					treeNode.setId(list.get(i).getId().getOrgNo());
					treeNode.setText(list.get(i).getOrgNm());
					treeNode.setUpId(list.get(i).getUpOrgNo());
					Map<String, String> params = new HashMap<String, String>();
					params.put("orgNo", list.get(i).getId().getOrgNo());
					params.put("upOrgNo", list.get(i).getUpOrgNo());
					params.put("orgNm", list.get(i).getOrgNm());
					params.put("orgType",list.get(i).getId().getOrgType());
					treeNode.setParams(params);
					treeNode.setIsParent(true);
					nodes.add(treeNode);
				}
				if(list.get(i).getId().getOrgType().equals(GlobalConstants4plugin.RPT_FRS_BUSI_1104)){
					treeNode.setIcon(path+"/"+GlobalConstants4frame.ICON_URL+"/application_view_detail.png");
					treeNode.setId(list.get(i).getId().getOrgNo());
					treeNode.setText(list.get(i).getOrgNm());
					treeNode.setUpId(list.get(i).getFinanceOrgNo());
					Map<String, String> params = new HashMap<String, String>();
					params.put("orgNo", list.get(i).getId().getOrgNo());
					params.put("upOrgNo", list.get(i).getUpOrgNo());
					params.put("orgNm", list.get(i).getOrgNm());
					params.put("orgType",list.get(i).getId().getOrgType());
					treeNode.setParams(params);
					treeNode.setIsParent(true);
					nodes.add(treeNode);
				}
				if(list.get(i).getId().getOrgType().equals(GlobalConstants4plugin.RPT_FRS_BUSI_BANK)){
					treeNode.setIcon(path+"/"+GlobalConstants4frame.ICON_URL+"/application_view_detail.png");
					treeNode.setId(list.get(i).getId().getOrgNo());
					treeNode.setText(list.get(i).getOrgNm());
					treeNode.setUpId(list.get(i).getUpOrgNo());
					Map<String, String> params = new HashMap<String, String>();
					params.put("orgNo", list.get(i).getId().getOrgNo());
					params.put("upOrgNo", list.get(i).getUpOrgNo());
					params.put("orgNm", list.get(i).getOrgNm());
					params.put("orgType",list.get(i).getId().getOrgType());
					treeNode.setParams(params);
					treeNode.setIsParent(false);
					nodes.add(treeNode);
				}
			}
		}
		return nodes;
	}

	public List<CommonTreeNode> getOrgTree(String id,String path) {
		Map<String, Object> condition = new HashMap<String, Object>();
		String userMgrNo = BioneSecurityUtils.getCurrentUserInfo().getOrgNo();
		Map<String ,Object> mapMgr = new HashMap<String,Object>();
		mapMgr.put("mgrOrgNo", userMgrNo);
		List<String> orgNoUser = this.rptOrgInfoMybatisDao.getOrgNoUser(mapMgr);
		List<CommonTreeNode> result = new ArrayList<CommonTreeNode>();
		if(orgNoUser != null
				&& orgNoUser.size() > 0){
			if(id == null || id.equals("")){
				if(orgNoUser!=null&&orgNoUser.size()>0){
					condition.put("orgNo", ReBuildParam.toDbList(orgNoUser));
				}
				List<BioneOrgInfo> list = this.rptOrgInfoMybatisDao.list(condition);
				for(BioneOrgInfo org : list){
					CommonTreeNode node = new CommonTreeNode();
					node.setId(org.getOrgNo());
					node.setIsParent(true);
					node.setText(org.getOrgName());
					node.setUpId(org.getUpNo());
					node.setIcon(path+"/"+GlobalConstants4frame.ICON_URL+"/application_view_detail.png");
					result.add(node);
				}
			}else{
				List<String> idList = new ArrayList<String>();
				idList.add(id);
				condition.put("upNo", ReBuildParam.toDbList(idList));
				List<BioneOrgInfo> list = this.rptOrgInfoMybatisDao.list(condition);
				for(BioneOrgInfo org : list){
					CommonTreeNode node = new CommonTreeNode();
					node.setId(org.getOrgNo());
					node.setIsParent(true);
					node.setText(org.getOrgName());
					node.setUpId(org.getUpNo());
					node.setIcon(path+"/"+GlobalConstants4frame.ICON_URL+"/application_view_detail.png");
					result.add(node);
				}
			}
		}
		return result;
	}
	
	public List<String> getManageOrg(String mgrOrgNo,String orgType){
		List<String> orgNos=new ArrayList<String>();
		List<RptOrgInfo> mgrorgNos=new ArrayList<RptOrgInfo>();
		Map<String ,Object> mapMgr = new HashMap<String,Object>();
		mapMgr.put("mgrOrgNo", mgrOrgNo);
		mapMgr.put("orgType", orgType);
		List<RptOrgInfo> orgs=this.rptOrgInfoMybatisDao.getOrgNoInfoUser(mapMgr); //????????????orgNo
		if(orgs!=null&&orgs.size()>0){
			if(orgs.size()==1){
				orgNos.add(orgs.get(0).getId().getOrgNo());
			}
			else {
				mgrorgNos.add(orgs.get(0));
				for(RptOrgInfo org:orgs){
					boolean flag=true;
					List<RptOrgInfo> deletOrg=new ArrayList<RptOrgInfo>();
					for(RptOrgInfo  morg:mgrorgNos){
						if(org.getNamespace().indexOf(morg.getNamespace())>=0){
							flag=false;
							break;
						}
						if(morg.getNamespace().indexOf(org.getNamespace())>=0){
							deletOrg.add(morg);
						}
					}
					if(flag){
						mgrorgNos.add(org);
					}
					if(deletOrg.size()>0){
						for(RptOrgInfo dorg:deletOrg){
							mgrorgNos.remove(dorg);
						}
					}
				}
				for(RptOrgInfo morg:mgrorgNos){
					orgNos.add(morg.getId().getOrgNo());
				}
			}
		}
		return orgNos;
	}
	
	/**
	 * ????????????????????????
	 * @return
	 */
	public List<OrgImportVO> getImportOrg(Map<String,Object> map){
		List<OrgImportVO> vos = Lists.newArrayList();
		vos=  this.rptOrgInfoMybatisDao.getExportInfo(map);
		return vos;
	}

	public Map<String,String> findOrgInfo(String orgType){
		Map<String,String> orgMap=new HashMap<String, String>();
		Map<String,Object> params=new HashMap<String, Object>();
		params.put("orgType", orgType);
		List<RptOrgInfo> orgs=this.rptOrgInfoMybatisDao.findOrg(params);
		if(orgs!=null&&orgs.size()>0){
			for(RptOrgInfo org:orgs){
				orgMap.put(org.getId().getOrgNo(), org.getOrgNm());
			}
		}
		return orgMap;
	}
	
	public List<RptOrgInfo> findOrgChild(String orgNo , String orgType){
		List<RptOrgInfo> org = new ArrayList<RptOrgInfo>();
		List<String> upOrg = new ArrayList<String>();
		upOrg.add(orgNo);
		RptOrgInfoPK id = new RptOrgInfoPK();
		id.setOrgNo(orgNo);
		id.setOrgType(orgType);
		RptOrgInfo rootorg = this.getEntityById(RptOrgInfo.class, id);
		if(rootorg != null){
			org.add(rootorg);
		}
		while(upOrg!=null && upOrg.size()>0){
			String jql = "select org from RptOrgInfo org where org.upOrgNo in ?0 and org.id.orgType = ?1";
			List<RptOrgInfo> upOrgInfos = this.baseDAO.findWithIndexParam(jql, upOrg , orgType);
			upOrg = new ArrayList<String>();
			if(upOrgInfos!=null && upOrgInfos.size()>0){
				org.addAll(upOrgInfos);
				for(RptOrgInfo upOrgInfo : upOrgInfos){
					upOrg.add(upOrgInfo.getId().getOrgNo());
				}
			}
			else{
				break;
			}
		}
		return org;
	}
	
	/**
	 * ???????????????
	 *
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public Map<String,List<CommonTreeNode>> getAllOrgTree(String orgType, String nodeType,String isSum,boolean isParent) {
		EhcacheUtils.remove("orgMap", orgType+"-"+isParent);
		Map<String,List<CommonTreeNode>> common = (Map<String, List<CommonTreeNode>>) EhcacheUtils.get("orgMap", orgType+"-"+isParent);
		if(common!= null){
			return common;
		}
		else{
			common = makeMap(orgType,isParent);
			EhcacheUtils.put("orgMap", orgType+"-"+isParent, common);
			return common;
		}
	}
	
	/**
	 * ???????????????
	 *
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public Map<String,String> getAllOrgData(String orgType) {
		Map<String,String> orgInfo = (Map<String, String>) EhcacheUtils.get("orgData", orgType);
		if(orgInfo != null){
			return orgInfo;
		}
		else{
			orgInfo = new HashMap<String, String>();
			Map<String,Object> map = new HashMap<String, Object>();
			map.put("orgType", orgType);
			String jql = "select org from RptOrgInfo org where org.id.orgType = :orgType";
			List<RptOrgInfo> orgs = this.baseDAO.findWithNameParm(jql, map);
			if(orgs !=null && orgs.size()>0){
				for(RptOrgInfo org : orgs){
					orgInfo.put(org.getId().getOrgNo(), org.getOrgNm());
				}
			}
			EhcacheUtils.put("orgData", orgType, orgInfo);
			return orgInfo;
		}
	}
	
	private  Map<String,List<CommonTreeNode>> makeMap(String orgType,boolean isParent){
		Map<String,List<CommonTreeNode>> map=new HashMap<String, List<CommonTreeNode>>();
		Map<String, Object> params_cc = Maps.newHashMap();
		//params_cc.put("orgSumType", GlobalConstants4plugin.FRS_ORG_SUN_TYPE_INPUT);
		params_cc.put("orgType", orgType);
		List<RptOrgInfo> childfrsOrgs = this.rptOrgInfoMybatisDao.getChildOrg(params_cc);
		if(childfrsOrgs!=null&&childfrsOrgs.size()>0){
			for(RptOrgInfo org:childfrsOrgs){
				if(map.get(org.getUpOrgNo())==null){
					List<CommonTreeNode> orgs=new ArrayList<CommonTreeNode>();
					CommonTreeNode childNode = new CommonTreeNode();
					childNode.setId(org.getId().getOrgNo());
					childNode.setText(org.getOrgNm());
					//childNode.setUpId(upId);
					childNode.setUpId(org.getUpOrgNo());
					Map<String, String> nodeParams_c = Maps.newHashMap();
					nodeParams_c.put("type", "org");
					nodeParams_c.put("orgType", orgType);
					nodeParams_c.put("realId", org.getId().getOrgNo());
					nodeParams_c.put("isParent", "true");
					childNode.setParams(nodeParams_c);
					childNode.setData(org);
					if(!isParent || checkOrgParent(org.getId().getOrgNo(),orgType)){
						orgs.add(childNode);
						map.put(org.getUpOrgNo(), orgs);
					}
				}
				else{
					CommonTreeNode childNode = new CommonTreeNode();
					childNode.setId(org.getId().getOrgNo());
					childNode.setText(org.getOrgNm());
					//childNode.setUpId(upId);
					childNode.setUpId(org.getUpOrgNo());
					Map<String, String> nodeParams_c = Maps.newHashMap();
					nodeParams_c.put("type", "org");
					nodeParams_c.put("orgType", orgType);
					nodeParams_c.put("realId", org.getId().getOrgNo());
					nodeParams_c.put("isParent", "true");
					childNode.setParams(nodeParams_c);
					childNode.setData(org);
					if(!isParent || checkOrgParent(org.getId().getOrgNo(),orgType)){
						map.get(org.getUpOrgNo()).add(childNode);
					}
				}
			}
		}
		return map;
	}
	
	public Boolean checkOrgParent(String orgNo,String orgType){
		if(orgType.equals("03")){
			String jql =" select org from RptOrgInfo org where org.upOrgNo = ?0 and org.id.orgType = ?1";
			List<RptOrgInfo> orgs= this.baseDAO.findWithIndexParam(jql, orgNo,orgType);
			return orgs.size()>0;
		}
		return true;
	}
	
	public List<String> getOrgNoBySysCode(){
		Map<String,Object> params = new HashMap<String, Object>();
		String jql = "select org.id.orgNo from RptOrgInfo org where org.id.orgType = :orgType ";
		params.put("orgType", GlobalConstants4plugin.RPT_FRS_BUSI_BANK);
		List<String> orgNos = this.baseDAO.findWithNameParm(jql, params);
		return orgNos;
	}
	
	/**
	 * ??????????????????
	 * @param orgNo ??????????????????????????????
	 * @return
	 */
	public String getAnaOrg(String orgNo){
		String displayRangeNm = "";
		if(orgNo != null && orgNo.length() > 0){
			String[] orgNolist = StringUtils.split(orgNo, ";");
			if(orgNolist != null && orgNolist.length > 0){
				for(String org : orgNolist){
					String jql = "select org from RptOrgInfo org where org.id.orgNo = ?0 and org.id.orgType = ?1";
					RptOrgInfo rptOrgInfo = this.baseDAO.findUniqueWithIndexParam(jql,org,"01");
					if(rptOrgInfo != null){
						displayRangeNm += rptOrgInfo.getOrgNm() + ";";
					}
				}
			}
		}
		return displayRangeNm;
	}
	
	public List<RptOrgInfo> getOrgByNos(String orgNo){
		List<RptOrgInfo> orgs  = new ArrayList<RptOrgInfo>();
		if(StringUtils.isNotBlank(orgNo)){
			List<String> orgList = ArrayUtils.asList(orgNo, ",");
			String jql = "select org from RptOrgInfo org where org.id.orgNo in ?0";
			orgs  = this.baseDAO.findWithIndexParam(jql, orgList);
		}
		return orgs;
	}
	
	public List<RptOrgInfo> getOrgByNos(String orgNo,String seq){
		List<RptOrgInfo> orgs  = new ArrayList<RptOrgInfo>();
		if(StringUtils.isNotBlank(orgNo)){
			List<String> orgList = ArrayUtils.asList(orgNo, seq);
			String jql = "select org from RptOrgInfo org where org.id.orgNo in ?0";
			orgs  = this.baseDAO.findWithIndexParam(jql, orgList);
		}
		return orgs;
	}
	
	public List<CommonTreeNode> getAuthOrgTree(String upNo,String searchName,String orgType) {
		if(!BioneSecurityUtils.getCurrentUserInfo().isSuperUser()){
			Map<String,List<String>> validateMap = new HashMap<String, List<String>>();
			validateMap = this.getValidateOrgInfo();
			List<String> orgNos=validateMap.get("validate");
			if(orgNos == null || orgNos.size() <= 0){
				return new ArrayList<CommonTreeNode>();
			}
		}
		
		Map<String, Object> params = Maps.newHashMap();
		if(!StringUtils.isEmpty(upNo)){
			params.put("upOrgNo", upNo);
		}else{
			if(StringUtils.isNotBlank(searchName)){
				//2020 lcy ??????????????????sql?????? ????????????
				if(SqlValidateUtils.validateStr(searchName)) {
					searchName = SqlValidateUtils.replaceValue(searchName);
				}
				params.put("orgNmLike",  "%"+searchName+"%");
				if(!BioneSecurityUtils.getCurrentUserInfo().isSuperUser()){
					Map<String,List<String>> validateMap = new HashMap<String, List<String>>();
					validateMap = this.getValidateOrgInfo();
					List<String> orgNos=validateMap.get("validate");
					params.put("orgNos",  ReBuildParam.splitLists(orgNos));
				}
			}
			else{
				if(!BioneSecurityUtils.getCurrentUserInfo().isSuperUser()){
					Map<String,List<String>> validateMap = new HashMap<String, List<String>>();
					validateMap = this.getValidateOrgInfo();
					List<String> vorgNos=validateMap.get("validate");
					vorgNos.add(BioneSecurityUtils.getCurrentUserInfo().getOrgNo());
					params.put("orgNos", ReBuildParam.splitLists(vorgNos));
				}
				else{
					params.put("upOrgNo", "0");
				}
			}
		}
		if(StringUtils.isNotBlank(orgType)){
		    params.put("orgType", orgType);
		}else{
		    params.put("orgType", GlobalConstants4plugin.RPT_FRS_BUSI_BANK);
		}
		List<CommonTreeNode> list = rptOrgInfoMybatisDao.findOrgNode(params);
		for(CommonTreeNode node:list){
			//202011 ??????MyBatis??????-???????????? ?????????31
			String iconpath = GlobalConstants4frame.APP_CONTEXT_PATH + "/" + GlobalConstants4frame.ICON_URL + "/mnuTree.gif";
			node.setIcon(iconpath);
			if(StringUtils.isNotBlank(searchName)){
				node.setText(node.getText()+"("+node.getId()+")");
				node.setIsParent(false);
			}
			else{
				node.setText(node.getText()+"("+node.getId()+")");
				node.setIsParent(true);
				node.setChildren(new ArrayList<CommonTreeNode>());
			}
		}
		return list;
	}
	
	public List<String> getAllChildOrg(String orgNo, String orgType, String date) {
		String jql = "select org from UprOrgInfo org where org.id.orgType =?0 and org.id.orgNo = ?1 and org.startDt <= ?2 and org.endDt > ?2";
		RptOrgInfo org = this.baseDAO.findUniqueWithIndexParam(jql, orgType, orgNo, date);
		if (org != null) {
			//Map<String, Object> params = new HashMap<String, Object>();
			List<String> orgNos = this.getValidateOrgByNo(orgType, Collections.singletonList(orgNo), date);
			/*StringBuilder jqlStrBu = new StringBuilder("select org.id.orgNo from UprOrgInfo org where ");
			SplitUtils.processSplitList("org.id.orgNo", "orgNo", SplitUtils.splitList(orgNos, SplitUtils.ORACLE_SPLIT_SIZE), jqlStrBu, params);
			jqlStrBu.append(" and org.id.orgType = :orgType"
					+ " and org.startDt <= :date and org.endDt > :date"
					+ " and NOT EXISTS("
					+ " select 1 from UprOrgInfo org1"
					+ " where org.id.orgNo = org1.upOrgNo"
					+ " and org.id.orgType = org1.id.orgType"
					+ " and org1.id.versionId = org.id.versionId) ");
			params.put("orgType", orgType);
			params.put("date", date);
			List<String> allOrgNos = this.baseDAO.findWithNameParm(jqlStrBu.toString(), params);
			return allOrgNos;*/
			return orgNos;
		} else {
			return Collections.singletonList(orgNo);
		}

	}
	
	private List<String> getValidateOrgByNo(String orgType,List<String> orgNo,String date){
		 List<String> orgNos = new ArrayList<String>();
		 orgNos.addAll(orgNo);
		 this.getChildOrg(orgNos, orgNos, orgType, date);
		 return orgNos;
	}
	
	private void getChildOrg(List<String> upOrgNos,List<String> orgNos,String orgType,String date){
		if(upOrgNos != null && upOrgNos.size()>0){
			Map<String,Object> params = new HashMap<String, Object>();
			String jql = "select org.id.orgNo from UprOrgInfo org where org.id.orgType = :orgType and org.upOrgNo in (:upOrgNos) and org.startDt <=:date and org.endDt > :date";
			params.put("orgType", orgType);
			params.put("upOrgNos", upOrgNos);
			params.put("date", date);
			upOrgNos = this.baseDAO.findWithNameParm(jql, params);
			if(upOrgNos != null && upOrgNos.size()>0){
				orgNos.addAll(upOrgNos);
				getChildOrg(upOrgNos, orgNos,orgType,date);
			}
		}
	}
	
	public List<CommonTreeNode> getAuthTree(String basePath,String orgType,String date,String searchNm){
		List<CommonTreeNode> nodes = new ArrayList<CommonTreeNode>();
		if(StringUtils.isEmpty(orgType)){
			orgType = "01";
		}
		//nodes.add(generateRootNode(basePath, orgType));
		Map<String,Object> params = new HashMap<String, Object>();
		String jql = "select typ from UprOrgTypeInfo typ where  typ.id.orgType = :orgType";
		if(StringUtils.isNotBlank(date) && !"undefined".equals(date)){
			jql += " and typ.startDt <= :date and typ.endDt > :date";
			params.put("date", date);
		}
		else{
			jql += " and typ.endDt = :date";
			params.put("date", "29991231");
		}
		params.put("orgType", orgType);
		UprOrgTypeInfo type = this.baseDAO.findUniqueWithNameParam(jql, params);
		BigDecimal versionId = type.getId().getVersionId();
		jql = "select org from UprOrgInfo org where org.id.orgType= :orgType and org.id.versionId = :versionId";
		params.clear();
		params.put("orgType", orgType);
		params.put("versionId", versionId);
		if(!BioneSecurityUtils.getCurrentUserInfo().isSuperUser()){
			List<String> orgNos = this.getValidateOrgInfo(orgType, date).get("validate");
			jql += " and "+SplitUtils.splitListSql(orgNos, params, "org.id.orgNo", "orgNo");
		}
		if(StringUtils.isNotBlank(searchNm)){
			jql += " and (org.orgName like :searchNm or org.id.orgNo like :searchNm)";
			//2020 lcy ??????????????????sql?????? ????????????
			if(SqlValidateUtils.validateStr(searchNm)) {
				searchNm = SqlValidateUtils.replaceValue(searchNm);
			}
			params.put("searchNm", "%"+searchNm+"%");
		}
		List<RptOrgInfo> orgs = this.baseDAO.findWithNameParm(jql, params);
		if(orgs != null && orgs.size()>0){
			for(RptOrgInfo org : orgs){
				nodes.add(generateOrgNode(basePath, org, null, true));
			}
		}
		return nodes;
	}
	
	public List<String> getRootOrg(String orgNo, String orgType, String date) {
		String jql = "select org from UprOrgInfo org where org.id.orgType =?0 and org.id.orgNo = ?1 and org.startDt <= ?2 and org.endDt > ?2";
		RptOrgInfo org = this.baseDAO.findUniqueWithIndexParam(jql, orgType, orgNo, date);
		if (org != null) {
			Map<String, Object> params = new HashMap<String, Object>();
			List<String> orgNos = this.getValidateOrgByNo(orgType, Collections.singletonList(orgNo), date);
			StringBuilder jqlStrBu = new StringBuilder("select org.id.orgNo from UprOrgInfo org where ");
			SplitUtils.processSplitList("org.id.orgNo", "orgNo", SplitUtils.splitList(orgNos, SplitUtils.ORACLE_SPLIT_SIZE), jqlStrBu, params);
			jqlStrBu.append(" and org.id.orgType = :orgType"
					+ " and org.startDt <= :date and org.endDt > :date"
					+ " and NOT EXISTS("
					+ " select 1 from UprOrgInfo org1"
					+ " where org.id.orgNo = org1.upOrgNo"
					+ " and org.id.orgType = org1.id.orgType"
					+ " and org1.id.versionId = org.id.versionId) ");
			params.put("orgType", orgType);
			params.put("date", date);
			List<String> allOrgNos = this.baseDAO.findWithNameParm(jqlStrBu.toString(), params);
			return allOrgNos;
		} else {
			return Collections.singletonList(orgNo);
		}

	}

	//???????????????
	public List<CommonTreeNode> getOrgTreeByList(String orgType,List<String> orgIds){
		List<CommonTreeNode> nodes = new ArrayList<CommonTreeNode>();
		if(StringUtils.isNotBlank(orgType) && (null != orgIds) && orgIds.size() > 0) {
			String jql = "select org from RptOrgInfo org where org.id.orgType = ?0 and org.id.orgNo in ?1 ";
			if(orgIds != null && orgIds.size() > 0){
				List<RptOrgInfo> orgInfos = this.baseDAO.findWithIndexParam(jql, orgType,orgIds);
				if(orgInfos != null && orgInfos.size() > 0){
					for(RptOrgInfo info : orgInfos){
						CommonTreeNode node = new CommonTreeNode();
						node.setId(info.getId().getOrgNo());
						node.setText(info.getOrgNm());
						node.setUpId(info.getUpOrgNo());
						node.setData(info);
						Map<String, String> nodeParams_c = Maps.newHashMap();
						nodeParams_c.put("type", "org");
						nodeParams_c.put("orgType", orgType);
						nodeParams_c.put("isParent", "true");
						nodeParams_c.put("realId", info.getId().getOrgNo());
						node.setParams(nodeParams_c);
						nodes.add(node);
					}
				}
			}
		}
		return nodes;
	}
	
	/**
	 * ????????????????????????????????????
	 * ???????????????????????????????????????????????????????????????????????????
	 * @return
	 */
	public List<String> getIssuedNo(String orgType, boolean isParent) {
		HashSet<String> orgNoSet = new HashSet<String>();
		// ????????????????????????????????????????????????
		String jql = "select org from RptOrgInfo org where org.id.orgType =?0";
		List<RptOrgInfo> rptOrgInfoList = this.baseDAO.findWithIndexParam(jql, orgType);
		//??????????????????Map<?????????????????? ???????????????>
		Map<String, String> rptOrgMap = new HashMap<String, String>();
		for(int i = 0;i < rptOrgInfoList.size(); i++) {
			rptOrgMap.put(rptOrgInfoList.get(i).getMgrOrgNo(), rptOrgInfoList.get(i).getId().getOrgNo());
		}
		
		// ?????????????????????????????????(??????),????????????????????????????????????
		List<String> innerOrgNos = BioneSecurityUtils.getCurrentUserInfo().getAuthObjMap().get(GlobalConstants4frame.AUTH_OBJ_DEF_ID_ORG);
		if (innerOrgNos != null) {
			for (String orgNo :  innerOrgNos) {
				if(StringUtils.isNotBlank(rptOrgMap.get(orgNo))) {
					orgNoSet.add(rptOrgMap.get(orgNo));
				}
			}
		}
		//????????????????????????????????????????????????
		List<String> userOrg = this.getAllChildOrgNo(BioneSecurityUtils.getCurrentUserInfo().getOrgNo(), orgType, true);
		for(int i = 0; i < userOrg.size(); i++) {
			orgNoSet.add(userOrg.get(i));
		}
		List<String> orgNos = new ArrayList<String>(orgNoSet);
		return orgNos;
	}
	
	/**
	 * ?????????????????????????????????????????????????????????
	 * ?????????????????????????????????????????????????????????
	 * @return
	 */
	public List<String> getRptOrgNo(String orgType, boolean isParent) {
		HashSet<String> orgNoSet = new HashSet<String>();
		// ????????????????????????????????????????????????
		String jql = "select org from RptOrgInfo org where org.id.orgType =?0 and org.isOrgReport =?1";
		List<RptOrgInfo> rptOrgInfoList = this.baseDAO.findWithIndexParam(jql, orgType, GlobalConstants4plugin.STS_ON_Y);
		//??????????????????Map<?????????????????? ???????????????>
		Map<String, String> rptOrgMap = new HashMap<String, String>();
		for(int i = 0;i < rptOrgInfoList.size(); i++) {
			rptOrgMap.put(rptOrgInfoList.get(i).getMgrOrgNo(), rptOrgInfoList.get(i).getId().getOrgNo());
		}
		
		// ?????????????????????????????????(??????),????????????????????????????????????
		List<String> innerOrgNos = BioneSecurityUtils.getCurrentUserInfo().getAuthObjMap().get(GlobalConstants4frame.AUTH_OBJ_DEF_ID_ORG);
		if (innerOrgNos != null) {
			for (String orgNo :  innerOrgNos) {
				if(StringUtils.isNotBlank(rptOrgMap.get(orgNo))) {
					orgNoSet.add(rptOrgMap.get(orgNo));
				}
			}
		}
		// ??????????????????????????????????????????????????????????????????
		List<String> userOrg = this.getAllChildRptOrgNo(BioneSecurityUtils.getCurrentUserInfo().getOrgNo(), orgType, true);
		for(int i = 0; i < userOrg.size(); i++) {
			orgNoSet.add(userOrg.get(i));
		}
		List<String> orgNos = new ArrayList<String>(orgNoSet);
		return orgNos;
	}
	
	/**
	 * ???????????????????????????????????????????????????
	 * @param orgNo ????????????
	 * @param orgType ????????????
	 * @param isParent ??????????????????
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<String> getAllChildOrgNo(String orgNo, String orgType, boolean isParent){
		if(StringUtils.isNotBlank(orgNo) && StringUtils.isNotBlank(orgType)){
			//orgNo??????????????????????????????????????????????????????rpt???????????????????????????????????????????????????
			String jql = "select org.id.orgNo from RptOrgInfo org where org.id.orgType =?0 and org.mgrOrgNo = ?1";
			String rptOrgNo = this.baseDAO.findUniqueWithIndexParam(jql, orgType, orgNo);
			if(StringUtils.isNotBlank(rptOrgNo)) {
				orgNo = rptOrgNo;
			}
			//????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????
			List<String> orgNos = (List<String>) EhcacheUtils.get(this.RPT_ORG_ALLCHILD_CACHE, orgNo + "_" + orgType);
			if(null == orgNos || orgNos.isEmpty()) {
				orgNos = getChildOrgNo(orgNo, orgType, GlobalConstants4plugin.STS_OFF_N);
				EhcacheUtils.put(this.RPT_ORG_ALLCHILD_CACHE, orgNo + "_" +orgType, orgNos);
			}
			//???????????????????????????????????????????????????????????????????????????????????????
			if(isParent) {
				List<String> orgNoAddParent = new ArrayList<String>();
				orgNoAddParent.addAll(orgNos);
				orgNoAddParent.add(orgNo);
				return orgNoAddParent;
			}
			return orgNos;
		}
		return null;
	}
	/**
	 * ???????????????????????????????????????????????????(?????????????????????)
	 * @param orgNo ????????????
	 * @param orgType ????????????
	 * @param isParent ??????????????????
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<String> getAllChildRptOrgNo(String orgNo, String orgType, boolean isParent){
		if(StringUtils.isNotBlank(orgNo) && StringUtils.isNotBlank(orgType)){
			//orgNo??????????????????????????????????????????????????????rpt???????????????????????????????????????????????????
			String jql = "select org.id.orgNo from RptOrgInfo org where  org.id.orgType =?0 and org.mgrOrgNo = ?1 and org.isOrgReport = ?2";
			String rptOrgNo = this.baseDAO.findUniqueWithIndexParam(jql, orgType, orgNo, GlobalConstants4plugin.STS_ON_Y);
			if(StringUtils.isNotBlank(rptOrgNo)) {
				orgNo = rptOrgNo;
			}
			List<String> orgNos = (List<String>) EhcacheUtils.get(this.RPT_ORGREPORT_ALLCHILD_CACHE, orgNo + "_" +orgType);
			if(null == orgNos) {
				orgNos = getChildOrgNo(orgNo, orgType, GlobalConstants4plugin.STS_ON_Y);
				EhcacheUtils.put(this.RPT_ORGREPORT_ALLCHILD_CACHE, orgNo + "_" +orgType, orgNos);
			}
			if(isParent) {
				List<String> orgNoAddParent = new ArrayList<String>();
				orgNoAddParent.addAll(orgNos);
				orgNoAddParent.add(orgNo);
				return orgNoAddParent;
			}
			return orgNos;
		}
		return null;
	}
	
	/**
	 * ?????????????????????????????????????????????
	 * @param orgNo ???????????????
	 * @param orgType ????????????
	 * @param isOrgReport ???????????????????????????
	 * @return
	 */
	private List<String> getChildOrgNo(String orgNo, String orgType, String isOrgReport){
		List<String> orgNos = new ArrayList<String>();
		List<String> childOrgNos = this.getNextlevelOrgNo(orgNo, orgType, isOrgReport);
		if(null != childOrgNos) {
			orgNos.addAll(childOrgNos);
			for(String childOrgNo : childOrgNos) {
				orgNos.addAll(this.getChildOrgNo(childOrgNo, orgType, isOrgReport));
			}
		}
		return orgNos;
	}
	
	/**
	 * ???????????????????????????????????????????????????
	 * @param orgNo ???????????????
	 * @param orgType ????????????
	 * @param isOrgReport ???????????????????????????
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private List<String> getNextlevelOrgNo(String orgNo, String orgType, String isOrgReport){
		List<String> orgNos = new ArrayList<String>();
		//????????????????????????????????????????????????????????????????????????????????????????????????????????????
		Map<String, List<String>>orgNosMap = (Map<String, List<String>>) EhcacheUtils.get(this.RPT_ORG_CACHE, orgType + isOrgReport);
		if(null == orgNosMap) {
			//?????????????????????????????????List<????????????????????????????????????>
			orgNosMap = new HashMap<String, List<String>>();
			Map<String,Object> params=new HashMap<String, Object>();
			params.put("orgType", orgType);
			//?????????????????????????????????????????????????????????
			if(GlobalConstants4plugin.STS_ON_Y.equals(isOrgReport)) {
				params.put("isOrgReport", isOrgReport);
			}
			List<RptOrgInfo> orgs = new ArrayList<RptOrgInfo>();
			//????????????????????????????????????????????????????????????????????????
			String jql = "select param.paramValue from BioneParamInfo param where param.paramTypeNo = 'OrgTreeType'";
			List<String> paramValues = this.baseDAO.findWithIndexParam(jql);
			if(paramValues != null && paramValues.size() > 0){
				if("Y".equals(paramValues.get(0))){
					orgs = this.rptOrgInfoMybatisDao.findOrgBySumRel(params);
				}else{
					orgs = this.rptOrgInfoMybatisDao.findOrg(params);
				}
			}else {
				orgs = this.rptOrgInfoMybatisDao.findOrg(params);
			}		
			if(null != orgs) {
				for(RptOrgInfo org : orgs) {
					List<String> childOrgNo = orgNosMap.get(org.getUpOrgNo());
					if(null == childOrgNo) {
						childOrgNo = new ArrayList<String>();
					}
					childOrgNo.add(org.getId().getOrgNo());
					orgNosMap.put(org.getUpOrgNo(), childOrgNo);
				}
				EhcacheUtils.put(this.RPT_ORG_CACHE, orgType + isOrgReport, orgNosMap);
			}
		}
		orgNos = orgNosMap.get(orgNo);
		return orgNos;
	}

	public List<String> getSameLevelOrgNo(String orgNo, String orgType) {
		Map<String, Object> param = new HashMap<>();
		param.put("orgNo", orgNo);
		param.put("orgType", orgType);
		List<RptOrgInfo> list = rptOrgInfoMybatisDao.getSameLevelOrgNo(param);
		List<String> collect = list.stream().map(item -> item.getId().getOrgNo()).collect(Collectors.toList());
		return collect;
	}

	//???????????????????????????
	public List<String> getBioneOrgList(){
		String jql = "select org.orgNo from  BioneOrgInfo org where 1=1";
		return this.baseDAO.findWithIndexParam(jql);
	}

	//???????????????????????????
	public List<String> getOrgTypeList(){
		String jql = "select param.paramValue from BioneParamInfo param where param.paramTypeNo ='reportorgtype'";
		return this.baseDAO.findWithIndexParam(jql);
	}

	//???????????????????????????
	public List<String> getOrgClassList() {
		String jql = "select param.paramValue from BioneParamInfo param where param.paramTypeNo = 'rptOrgClass'";
		return this.baseDAO.findWithIndexParam(jql);
	}

	//??????????????????????????????
	public List<String> getaddrList(){
		String jql = "SELECT rsci.item FROM RptStdCodeInfo rsci WHERE rsci.rptType='04' AND rsci.codeType='EAST_ADDR'";
		return this.baseDAO.findWithIndexParam(jql);
	}

	//???????????????????????????????????????
	public Map<String, String> getrhOrgNoMap(){
		String jql = "select rfai from RptFimAddrInfo rfai where 1=1";
		List<RptFimAddrInfo> list =new ArrayList<>();
		list=this.baseDAO.findWithIndexParam(jql);
		Map<String, String> map = new HashMap<>();
		for(RptFimAddrInfo rfai:list){
			map.put(rfai.getAddrNo(), rfai.getOrgNo());
		}
		return map;
	}

}


