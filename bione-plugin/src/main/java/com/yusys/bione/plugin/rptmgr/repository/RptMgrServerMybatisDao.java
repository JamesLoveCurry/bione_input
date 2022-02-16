package com.yusys.bione.plugin.rptmgr.repository;

import java.util.List;
import java.util.Map;

import com.yusys.bione.comp.repository.mybatis.MyBatisRepository;
import com.yusys.bione.plugin.rptmgr.entity.RptMgrAdapter;
import com.yusys.bione.plugin.rptmgr.entity.RptMgrAdapterParam;
import com.yusys.bione.plugin.rptmgr.entity.RptMgrAuthUserInfo;
import com.yusys.bione.plugin.rptmgr.entity.RptMgrParam;
import com.yusys.bione.plugin.rptmgr.entity.RptMgrServerInfo;
import com.yusys.bione.plugin.rptmgr.web.vo.ServerListVO;
import com.yusys.bione.plugin.rptmgr.web.vo.ServerParamVO;


/**
 * 通过@MapperScannerConfigurer扫描目录中的所有接口, 动态在Spring Context中生成实现.
 * 方法名称必须与Mapper.xml中保持一致.
 * 
 * @author calvin
 */
@MyBatisRepository
public interface RptMgrServerMybatisDao {
	public List<ServerListVO> serverlist();
	public List<RptMgrParam> getParamInfoList(Map<String,Object> map);
	public List<RptMgrAuthUserInfo> authUserlist(Map<String, Object> map);
	public void deleteAuthUserInfo(Map<String, Object> map);
	public void deleteAuthUserRelInfo(Map<String, Object> map);
	public List<RptMgrAdapterParam>  adapterParamlist(Map<String,Object> map);
	public List<RptMgrAdapter> getAdapterInfo(Map<String,Object> map);
	public List<String> findUsedServerName(Map<String,Object> map);
	public void deleteRptMgrParam(Map<String,Object> map);
	public void deleteRptAuthUserRel(Map<String,Object> map);
	public void deleteRptAuthUserInfo(Map<String,Object> map);
	public void deleteRptMgrServerInfo(Map<String,Object> map);
	public void saveAuthUser(RptMgrAuthUserInfo info);
	public void updateAuthUser(RptMgrAuthUserInfo info);
	public void saveServer(RptMgrServerInfo info);
	public void updateServer(RptMgrServerInfo info);
	public void saveParam(RptMgrParam info);
	public void updateParam(RptMgrParam info);
	public RptMgrServerInfo getServerInfoById(String serverId);
	
	public List<ServerParamVO> paramList(String serverId);
	
	
}
