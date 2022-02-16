package com.yusys.bione.plugin.engine.service;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.Lists;
import org.apache.commons.lang3.StringUtils;
import com.yusys.bione.comp.repository.jpa.SearchResult;
import com.yusys.bione.comp.service.BaseBS;
import com.yusys.bione.plugin.engine.entity.RptEngineNodeInfo;

@Service
@Transactional(readOnly = true)
public class EngineNodeInfoBS extends BaseBS<RptEngineNodeInfo> {
	
	public SearchResult<RptEngineNodeInfo> getRptEngineNodeInfo (  int pageStart, int pageSize, String sortName, String sortOrder, Map<String, Object> conditons  ) {
		StringBuilder sqlSb = new StringBuilder();
		
		sqlSb.append( "select node from RptEngineNodeInfo node where 1 = 1 " );
		
		if ( StringUtils.isNotEmpty( conditons.get( "jql" ).toString() ) ) {
			sqlSb.append( " and " ).append( conditons.get( "jql" ) ).append( " " );
		}
		
		if ( StringUtils.isNotEmpty( sortName ) ) {
			sqlSb.append( " order by node. " ).append( sortName );
			sqlSb.append( " " ).append( sortOrder );
		} else {
			sqlSb.append( " order by node.nodeId " );
		}
		
		@SuppressWarnings("unchecked")
		Map<String, Object> values = ( Map<String, Object> ) conditons.get( "params" );
		return this.baseDAO.findPageWithNameParam( pageStart, pageSize, sqlSb.toString(), values );
	}
	
	public RptEngineNodeInfo getNodeInfo ( String nodeNm ) {
		List<RptEngineNodeInfo> list = Lists.newArrayList();
		String jql = "select node from RptEngineNodeInfo node where nodeNm=?0 ";
		list = this.baseDAO.findWithIndexParam( jql, nodeNm );
		return list.get(0);
	}
	
	public void update(String ipAddress ,String port,String nodeIds,String maxThread, String remark){
		List<String> ids = Arrays.asList(StringUtils.split(nodeIds, ','));
		String sql = "UPDATE RPT_ENGINE_NODE_INFO SET IP_ADDRESS =?0,port=?1,MAX_THREAD=?2, remark=?3 WHERE NODE_ID in ?4";
		this.baseDAO.createNativeQueryWithIndexParam(sql, ipAddress,Integer.parseInt(port),Integer.parseInt(maxThread),remark,ids).executeUpdate();
	}
	
	public boolean checkIsNodeNmNoExist( String nodeNm ) {
		List<RptEngineNodeInfo> list = Lists.newArrayList();
		String jql = "select node from RptEngineNodeInfo node where nodeNm=?0 ";
		list = this.baseDAO.findWithIndexParam( jql, nodeNm );
		return null == list || 0 == list.size();
	}

	public boolean IsNodeNmNoExist(String nodeNm, String nodeTypeBox) {
		// TODO Auto-generated method stub
		List<RptEngineNodeInfo> list = Lists.newArrayList();
		List<String> ids = Lists.newArrayList();
		String [] nodeTypes = StringUtils.split(nodeTypeBox, ';');
		for(String nodeType : nodeTypes){
			ids.add(nodeNm+"_"+nodeType);
		}
		String jql = "select node from RptEngineNodeInfo node where nodeNm in?0 ";
		list = this.baseDAO.findWithIndexParam( jql, ids );
		return null == list || 0 == list.size();
	}
}