package com.yusys.bione.plugin.rptfav.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.yusys.bione.comp.entity.page.Pager;
import com.yusys.bione.comp.repository.mybatis.PageHelper;
import com.yusys.bione.comp.repository.mybatis.PageMyBatis;
import com.yusys.bione.frame.base.common.GlobalConstants4frame;
import com.yusys.bione.frame.security.BioneSecurityUtils;
import com.yusys.bione.plugin.rptfav.repository.FavFolderInsRelMybatisDao;
import com.yusys.bione.plugin.rptfav.repository.FavFolderMybatisDao;
import com.yusys.bione.plugin.rptfav.repository.FavIdxDetailMybatisDao;
import com.yusys.bione.plugin.rptfav.repository.FavIdxDimFilterMybatisDao;
import com.yusys.bione.plugin.rptfav.repository.FavIdxDimMybatisDao;
import com.yusys.bione.plugin.rptfav.repository.FavQueryinsMybatisDao;
import com.yusys.bione.plugin.rptfav.web.vo.FolderNameVo;
import com.yusys.bione.plugin.rptfav.web.vo.NameIdVo;
import com.yusys.bione.plugin.rptfav.web.vo.RptFavQueryinsVo;

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
public class RptQueryReportBS {
	
	@Autowired
	private FavFolderMybatisDao favFolderMybatisDao;
	
	//my God
	@Autowired
	private FavFolderInsRelMybatisDao favFolderInsRelMybatisDao;
	@Autowired
	private FavQueryinsMybatisDao favQueryinsMybatisDao;
	@Autowired
	private FavIdxDetailMybatisDao favIdxDetailMybatisDao;
	@Autowired
	private FavIdxDimMybatisDao favIdxDimMybatisDao;
	@Autowired
	private FavIdxDimFilterMybatisDao favIdxDimFilterMybatisDao;
	
	

	public Map<String, Object> reportInfo(Pager pager,String folderId,String instanceType) {
		// TODO Auto-generated method stub
		Map<String,Object> map = new HashMap<String,Object>();
		if(folderId == null || folderId.equals(GlobalConstants4frame.TREE_ROOT_NO)){
			folderId = null;
			map.put("folderId", folderId);
		}else{
			if(folderId.equals("")){
				folderId = null;
			}
			map.put("folderId", folderId);
		}
		map.put("instanceType", instanceType);
		if(!BioneSecurityUtils.getCurrentUserInfo().isSuperUser()){
			map.put("userId", BioneSecurityUtils.getCurrentUserId());
		}
		PageHelper.startPage(pager);
		PageMyBatis<RptFavQueryinsVo> list = null;
		if("03".equals(instanceType)){//指标
			list = this.favFolderMybatisDao.reportInfo(map);
		}else{//报表
			list = this.favFolderMybatisDao.reportInfo1(map);
		}
		PageMyBatis<FolderNameVo> vo = new PageMyBatis<FolderNameVo>();
		Map<String,Object> parames = new HashMap<String,Object>();
		parames.put("instanceId", folderId);
		if(!BioneSecurityUtils.getCurrentUserInfo().isSuperUser()){
			map.put("userId", BioneSecurityUtils.getCurrentUserId());
		}
		List<NameIdVo> nameVo = this.favQueryinsMybatisDao.findFolderNm(parames);
		if(list !=null){
			for(int i = 0;i<list.size();i++){
				FolderNameVo v = new FolderNameVo();
				v.setCreateTime(list.get(i).getCreateTime());
				v.setCreateUser(list.get(i).getCreateUser());
				v.setInstanceId(list.get(i).getInstanceId());
				v.setQueryNm(list.get(i).getQueryNm());
				v.setRemark(list.get(i).getRemark());
				v.setRptNum(list.get(i).getRptNum());
				if(nameVo !=null && nameVo.size() > 0){
					v.setFolderId(nameVo.get(0).getFolderId());
					v.setFolderNm(nameVo.get(0).getFolderNm());
				}
				v.setUserName(list.get(i).getUserName());
				vo.add(v);
			}
		}
		Map<String,Object> params = new HashMap<String,Object>();
		params.put("Rows", vo.getResult());
		params.put("Total", list.getTotalCount());
		return params;
	}
	
	//delete报表
	public String del(String instanceId,String folderId) {
		// TODO Auto-generated method stub
		Map<String,Object> map = new HashMap<String,Object>();
		map.put("instanceId", instanceId);
		map.put("userId", BioneSecurityUtils.getCurrentUserId());
		map.put("folderId", folderId);
		this.favFolderMybatisDao.del(map);
		this.favFolderInsRelMybatisDao.delete(map);
		return "";
	}
	
	//delete指标
	@Transactional(readOnly = false)
	public String delindex(String instanceId,String folderId) {
		// TODO Auto-generated method stub
		Map<String,Object> map = new HashMap<String,Object>();
		String userId = BioneSecurityUtils.getCurrentUserId();
		map.put("instanceId", instanceId);
		map.put("folderId", folderId);
		map.put("userId", userId);
		this.favFolderInsRelMybatisDao.delete(map);
		this.favQueryinsMybatisDao.delete(map);
		this.favIdxDetailMybatisDao.delete(map);
		this.favIdxDimMybatisDao.delete(map);
		this.favIdxDimFilterMybatisDao.deletes(map);
		return "";
	}

	public Map<String, Object> reportInfoIdx(Pager pager, String folderId,String instanceType) {  //folderId是instanceId 
		Map<String,Object> map = new HashMap<String,Object>();
		if(folderId == null || folderId.equals(GlobalConstants4frame.TREE_ROOT_NO)){
			folderId = null;
			map.put("instanceId", folderId);
		}else{
			if(folderId.equals("")){
				folderId = null;
			}
			map.put("instanceId", folderId);
		}
		map.put("instanceType", instanceType);
		map.put("userId", BioneSecurityUtils.getCurrentUserId());
		PageHelper.startPage(pager);
		PageMyBatis<RptFavQueryinsVo> list = (PageMyBatis<RptFavQueryinsVo>) this.favQueryinsMybatisDao.listInfo(map);
		PageMyBatis<FolderNameVo> vo = new PageMyBatis<FolderNameVo>();
		Map<String,Object> parames = new HashMap<String,Object>();
		parames.put("folderId", folderId);
		parames.put("userId", BioneSecurityUtils.getCurrentUserId());
		List<NameIdVo> nameVo = this.favQueryinsMybatisDao.findFolderNm(parames);
		if(list !=null){
			for(int i = 0;i<list.size();i++){
				FolderNameVo v = new FolderNameVo();
				v.setCreateTime(list.get(i).getCreateTime());
				v.setCreateUser(list.get(i).getCreateUser());
				v.setInstanceId(list.get(i).getInstanceId());
				v.setQueryNm(list.get(i).getQueryNm());
				v.setRemark(list.get(i).getRemark());
				if(nameVo !=null &&nameVo.size()>0){
					v.setFolderId(nameVo.get(0).getFolderId());
					v.setFolderNm(nameVo.get(0).getFolderNm());
				}
				v.setUserName(list.get(i).getUserName());
				vo.add(v);
			}
		}
		
		Map<String,Object> params = new HashMap<String,Object>();
		params.put("Rows", vo.getResult());
		params.put("Total", vo.getTotalCount());
		return params;
	}
}
