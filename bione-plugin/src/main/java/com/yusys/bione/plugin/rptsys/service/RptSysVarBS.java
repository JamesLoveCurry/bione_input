package com.yusys.bione.plugin.rptsys.service;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.Maps;
import com.yusys.bione.comp.entity.page.Pager;
import com.yusys.bione.comp.repository.mybatis.PageHelper;
import com.yusys.bione.comp.repository.mybatis.PageMyBatis;
import com.yusys.bione.comp.service.BaseBS;
import com.yusys.bione.comp.utils.RandomUtils;
import com.yusys.bione.frame.mtool.entity.BioneDriverInfo;
import com.yusys.bione.frame.mtool.entity.BioneDsInfo;
import com.yusys.bione.plugin.base.common.GlobalConstants4plugin;
import com.yusys.bione.plugin.datamodel.repository.mybatis.RptDataSetDao;
import com.yusys.bione.plugin.rptsys.entity.RptSysVarInfo;
import com.yusys.bione.plugin.rptsys.repository.RptSysVarMybatisDao;
import com.yusys.bione.plugin.rptsys.web.vo.RptSysVarInfoVO;
@Service
@Transactional(readOnly = true)
public class RptSysVarBS extends BaseBS<Object> {
	@Autowired
	private RptSysVarMybatisDao rptSysDao;
	@Autowired
	private RptDataSetDao rptDsDao;
	
	public Map<String, Object> getSysVarList(Pager pager,String type) {
		Map<String,Object> params=new HashMap<String, Object>();
		if(type.equals("inner")){
			params.put("defType", GlobalConstants4plugin.SYS_DEF_TYPE_INNER);
		}
		else{
			params.put("defType", GlobalConstants4plugin.SYS_DEF_TYPE_CUSTOM);
		}
		PageHelper.startPage(pager);
		PageMyBatis<RptSysVarInfo> page = (PageMyBatis<RptSysVarInfo>) rptSysDao.getSysVarByParams(params);
		Map<String, Object> shareMap = Maps.newHashMap();
		shareMap.put("Rows", page.getResult());
		shareMap.put("Total", page.getTotalCount());
		return shareMap;
	}
	
	public RptSysVarInfo getSysVarInfoById(String varId) {
		Map<String,Object> params=new HashMap<String, Object>();
		params.put("defType", GlobalConstants4plugin.SYS_DEF_TYPE_CUSTOM);
		params.put("varId", varId);
		List<RptSysVarInfo> sysInfos=rptSysDao.getSysVarByParams(params);
		if(sysInfos!=null&&sysInfos.size()>0){
			return sysInfos.get(0);
		}
		return null;
	}
	
	public List<RptSysVarInfo> getSysVarInfosByNo(String varNo,String defType) {
		Map<String,Object> params=new HashMap<String, Object>();
		params.put("defType", defType);
		params.put("varNo", varNo);
		return rptSysDao.getSysVarByParams(params);
	}
	@Transactional(readOnly = false)
	public void saveSysVarInfo(RptSysVarInfo sysInfo) {
		sysInfo.setDefType(GlobalConstants4plugin.DIM_TYPE_SRC_CUSTOM);
		if (sysInfo.getVarId() == null || "".equals(sysInfo.getVarId())) {
			// 若是新增操作
			sysInfo.setVarId(RandomUtils.uuid2());
			this.rptSysDao.saveSysVarInfo(sysInfo);
		}
		else{
			this.rptSysDao.updateSysVarInfo(sysInfo);
		}
	}
	
	public BioneDsInfo findDataSourceById(String sourceId){
		return this.rptDsDao.findDataSourceById(sourceId);
	}
	
	public BioneDriverInfo findDriverInfoById(String driveId){
		return this.rptDsDao.findDriverInfoById(driveId);
	}
	@Transactional(readOnly = false)
	public void deleteSysVarInfo(List<String>  varIds){
		this.rptSysDao.deleteSysVarInfo(varIds);
	}
	
	public boolean checkVarNo(String varNo){
		Map<String,Object> params=new HashMap<String, Object>();
		params.put("defType", GlobalConstants4plugin.SYS_DEF_TYPE_CUSTOM);
		params.put("varNo", varNo);
		List<RptSysVarInfo> sysInfos=rptSysDao.getSysVarByParams(params);
		if(sysInfos!=null&&sysInfos.size()>0){
			return false;
		}
		return true;
	}
	
	/**
	 * 根据id得到系统信息变量表信息
	 * 
	 * @param varId
	 * @return
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	public RptSysVarInfoVO getSysVarInfoByVarId(String varId) {
		Map<String,Object> params=new HashMap<String, Object>();
		params.put("defType", GlobalConstants4plugin.SYS_DEF_TYPE_CUSTOM);
		params.put("varId", varId);
		List<RptSysVarInfoVO> sysInfos=rptSysDao.getSysVarVoByParams(params);
		if(sysInfos!=null&&sysInfos.size()>0){
			return sysInfos.get(0);
		}
		return null;
	}
	
	
}
