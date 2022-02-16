package com.yusys.bione.plugin.engine.web;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.yusys.bione.comp.entity.page.Pager;
import com.yusys.bione.comp.repository.jpa.SearchResult;
import com.yusys.bione.comp.utils.StringUtils2;
import com.yusys.bione.plugin.base.utils.CommandRemote;
import com.yusys.bione.plugin.base.utils.CommandRemote.CommandRemoteType;
import com.yusys.bione.plugin.engine.entity.RptEngineNodeInfo;
import com.yusys.bione.plugin.engine.service.EngineNodeInfoBS;
import com.yusys.bione.plugin.engine.web.vo.EngineNodeInfo;

@Controller
@RequestMapping("/report/frame/engine/log/node")
public class EngineNodeInfoController {
	
	@Autowired
	private EngineNodeInfoBS nodeInfoBs;
	private String offLine = "N";
	private String ENGINE_SUCCESS = "0000";
	private String ENGINE_ERROR   = "9000";
	private static Logger logger = LoggerFactory.getLogger( EngineNodeInfoController.class );
	
	
	@RequestMapping(method = RequestMethod.GET)
	public String index () {
		return "/plugin/engine/engine-node-index";
	}
	
	@RequestMapping(value = "/edit" , method = RequestMethod.GET)
	public ModelAndView edit (String nodeId) {
		nodeId = StringUtils2.javaScriptEncode(nodeId);
		return new ModelAndView("/plugin/engine/engine-node-new", "nodeId", nodeId);
	}
	
	@RequestMapping(value = "/new" , method = RequestMethod.GET)
	public ModelAndView newNode () {
		return new ModelAndView("/plugin/engine/engine-node-news");
	}
	
	@RequestMapping(value = "/multiple_edits" , method = RequestMethod.GET)
	public ModelAndView multipleEdits (String nodeIds) {
		nodeIds = StringUtils2.javaScriptEncode(nodeIds);
		return new ModelAndView("/plugin/engine/engine-node-edits", "nodeIds", nodeIds);
	}
	
	@RequestMapping(value = "/view" , method = RequestMethod.GET)
	public ModelAndView view (String nodeId) {
		nodeId = StringUtils2.javaScriptEncode(nodeId);
		return new ModelAndView("/plugin/engine/engine-node-view", "nodeId", nodeId);
	}
	
	@RequestMapping(value = "/page")
	@ResponseBody
	public Map<String, Object> getEngineNodeInfo ( Pager pager ) {
		Map<String, Object> result = Maps.newHashMap();
		SearchResult<RptEngineNodeInfo> nodeInfo = nodeInfoBs.getRptEngineNodeInfo( pager.getPageFirstIndex(), pager.getPagesize(), 
				pager.getSortname(), pager.getSortorder(), pager.getSearchCondition() );
		result.put( "Total", nodeInfo.getTotalCount() );
		result.put( "Rows",  nodeInfo.getResult() );
		
		return result;
	}
	
	@RequestMapping(value = "/getNode/{id}", method = RequestMethod.GET)
	@ResponseBody
	public EngineNodeInfo getNodeInfo( @PathVariable("id") String id ) {
		RptEngineNodeInfo rptEngineNodeInfo =  this.nodeInfoBs.getNodeInfo(id);
		
		EngineNodeInfo engineNodeInfo = new EngineNodeInfo();
		engineNodeInfo.setNodeNm( rptEngineNodeInfo.getNodeNm() );
		engineNodeInfo.setNodeType( rptEngineNodeInfo.getNodeType() );
		engineNodeInfo.setRemark( rptEngineNodeInfo.getRemark() );
		engineNodeInfo.setIpAddress( rptEngineNodeInfo.getIpAddress() );
		engineNodeInfo.setPort( rptEngineNodeInfo.getPort() );
		engineNodeInfo.setMaxThread( rptEngineNodeInfo.getMaxThread() );
		engineNodeInfo.setNodeSts( rptEngineNodeInfo.getNodeSts() );
		engineNodeInfo.setTimeoutTime( rptEngineNodeInfo.getTimeoutTime() );
		Map<String,Object> map = new HashMap<String, Object>();
		map.put("QueryNodeInfo", id);
		JSONObject msg = new JSONObject();
		try {
			Object ret = CommandRemote.sendSync ( JSON.toJSONString(map), CommandRemoteType.INDEX );
			msg = JSON.parseObject(ret.toString());
		} catch (Throwable e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			logger.error( "连接节点失败" );
			msg.put("Code", ENGINE_ERROR);
		}
		boolean flag = false;
		if ( msg.containsKey("Code") && msg.containsKey("Msg") && ENGINE_SUCCESS.equals( msg.get("Code") ) ) {
			JSONObject threadCount = msg.getJSONObject("Msg");
			if ( threadCount.containsKey( "ActiveCount" ) && threadCount.containsKey( "FreeCount" ) ) {
				engineNodeInfo.setActiveCount(threadCount.getBigDecimal("ActiveCount"));
				engineNodeInfo.setFreeCount(threadCount.getBigDecimal("FreeCount"));
				flag = true;
			}
		}
		if ( ! flag ) {
			engineNodeInfo.setActiveCount( new BigDecimal( 0 ) );
			engineNodeInfo.setFreeCount( rptEngineNodeInfo.getMaxThread() );
			engineNodeInfo.setNodeSts(offLine);
		}
		return engineNodeInfo;
	}
	
	@RequestMapping(value = "/checkNodeNm")
	@ResponseBody
	public boolean checkNodeNm( String nodeNm ) {
		return this.nodeInfoBs.checkIsNodeNmNoExist( nodeNm );
	}
	@RequestMapping(value = "/checkNum")
	@ResponseBody
	public boolean checkNum( String nodeNm ,String nodeTypeBox) {
		return this.nodeInfoBs.IsNodeNmNoExist(nodeNm ,nodeTypeBox );
	}
	
    @RequestMapping(method = RequestMethod.POST)
	public void create(RptEngineNodeInfo model ,String nodeTypeBox ) {
    	if ( null == model.getTimeoutTime() || ( new BigDecimal( 0 ) ).equals( model.getTimeoutTime() ) ) {
    		model.setTimeoutTime( new BigDecimal( 1000 ) );
    	}
    	model.setNodeType(nodeTypeBox);
    	model.setNodeId( model.getNodeNm() );
    	model.setNodeSts("N");
    	nodeInfoBs.saveOrUpdateEntity( model );
    	Map<String,Object> map = new HashMap<String, Object>();
		map.put("RefreshNodeInfo", "");
		try {
			CommandRemote.sendSync(JSON.toJSONString(map), CommandRemoteType.INDEX);
		} catch (Throwable e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	@RequestMapping(value = "/create", method = RequestMethod.GET)
	@ResponseBody
    public Map<String,Object> createNode(RptEngineNodeInfo model ,String nodeTypeBox ) {
    	if ( null == model.getTimeoutTime() || ( new BigDecimal( 0 ) ).equals( model.getTimeoutTime() ) ) {
    		model.setTimeoutTime( new BigDecimal( 1000 ) );
    	}
    	model.setNodeSts("N");
    	String nodeNm = model.getNodeNm();
    	String[] nodeTypes = StringUtils.split(nodeTypeBox, ';');
    	for(String nodeType:nodeTypes){
    		model.setNodeNm(nodeNm+"_"+nodeType);
    		model.setNodeType(nodeType);
    		model.setNodeId( model.getNodeNm() );
    		nodeInfoBs.saveOrUpdateEntity( model );
    	}
    	Map<String,Object> map = new HashMap<String, Object>();
    	map.put("RefreshNodeInfo", "");
    	try {
    		CommandRemote.sendSync(JSON.toJSONString(map), CommandRemoteType.INDEX);
    	} catch (Throwable e) {
    		// TODO Auto-generated catch block
    		e.printStackTrace();
    	}
    	return map;
    }
    
    @RequestMapping(value = "/save")
	@ResponseBody
    public Map<String,Object> update(String ipAddress ,String port,String nodeIds,String maxThread,String remark) {
    	nodeInfoBs.update(ipAddress, port, nodeIds, maxThread,remark);
    	Map<String,Object> map = new HashMap<String, Object>();
    	map.put("RefreshNodeInfo", "");
    	try {
    		CommandRemote.sendSync(JSON.toJSONString(map), CommandRemoteType.INDEX);
    	} catch (Throwable e) {
    		// TODO Auto-generated catch block
    		e.printStackTrace();
    	}
    	return map;
    }
	
    
	@RequestMapping(value = "/{id}", method = RequestMethod.GET)
	@ResponseBody
	public RptEngineNodeInfo show( @PathVariable("id") String id ) {
		return nodeInfoBs.getEntityById( id );
	}
	
	@RequestMapping(value = "/show", method = RequestMethod.GET)
	@ResponseBody
	public RptEngineNodeInfo showList( String nodeIds ) {
		RptEngineNodeInfo rptEngineNodeInfo = new RptEngineNodeInfo();
		Set<String> ipAddress = Sets.newHashSet();
		Set<String> remark = Sets.newHashSet();
		Set<BigDecimal> port =  Sets.newHashSet();
		Set<BigDecimal> maxThread = Sets.newHashSet();
		List<String> ids = Arrays.asList(StringUtils.split(nodeIds, ','));
 		List<RptEngineNodeInfo> rptEngineNodeInfos = nodeInfoBs.getEntityListByProperty(RptEngineNodeInfo.class, "nodeId",ids);
		for(RptEngineNodeInfo nodeInfo :rptEngineNodeInfos){
			ipAddress.add(nodeInfo.getIpAddress());
			port.add(nodeInfo.getPort());
			maxThread.add(nodeInfo.getMaxThread());
			remark.add(nodeInfo.getRemark());
		}
		if(ipAddress.size()==1){
			rptEngineNodeInfo.setIpAddress(rptEngineNodeInfos.get(0).getIpAddress());
		}
		if(port.size()==1){
			rptEngineNodeInfo.setPort(rptEngineNodeInfos.get(0).getPort());
		}
		if(maxThread.size()==1){
			rptEngineNodeInfo.setMaxThread(rptEngineNodeInfos.get(0).getMaxThread());
		}
		if(remark.size()==1){
			rptEngineNodeInfo.setRemark(rptEngineNodeInfos.get(0).getRemark());
		}
		return rptEngineNodeInfo;
	}
	
	@RequestMapping(value = "/{id}", method = RequestMethod.POST)
	@ResponseBody
	public void delete( @PathVariable("id") String id ) {
		String ids[] = StringUtils.split(id,",");
		for(int i = 0 ;i<ids.length ; i++){
			nodeInfoBs.removeEntityById( ids[i] );
		}
		Map<String,Object> map = new HashMap<String, Object>();
		map.put("RefreshNodeInfo", "");
		try {
			CommandRemote.sendSync(JSON.toJSONString(map), CommandRemoteType.INDEX);
		} catch (Throwable e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
}