package com.yusys.bione.plugin.valid.web;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.google.common.collect.Maps;
import com.yusys.bione.comp.common.CommonTreeNode;
import com.yusys.bione.comp.entity.page.Pager;
import com.yusys.bione.comp.utils.RandomUtils;
import com.yusys.bione.comp.utils.SpringContextHolder;
import com.yusys.bione.comp.utils.StringUtils2;
import com.yusys.bione.frame.base.web.BaseController;
import com.yusys.bione.frame.validtype.entity.BioneValidTypeDef;
import com.yusys.bione.plugin.base.utils.CommandRemote;
import com.yusys.bione.plugin.base.utils.CommandRemote.CommandRemoteType;
import com.yusys.bione.plugin.valid.IValidTypeImpl;
import com.yusys.bione.plugin.valid.entitiy.RptValidGroupMain;
import com.yusys.bione.plugin.valid.service.RptValidGroupBS;
/**
 * 
 * <pre>
 * Title:
 * Description:
 * </pre>
 * 
 * @author hubing1@yusys.com.cn
 * @version 1.00.00
 * 
 *          <pre>
 * 修改记录
 *    修改后版本:     修改人：  修改日期:     修改内容:
 * </pre>
 */
@Controller
@RequestMapping("/report/frame/validgroup")
public class RptValidGroupController extends BaseController{
	@Autowired
	private RptValidGroupBS rptValidGroupBS;
	
	/**
	 * 首页
	 */
	@RequestMapping(method = RequestMethod.GET)
	public String index() {
		return "/plugin/valid/valid-group-index";
	}
	
	/**
	 * 新增or修改
	 */
	@RequestMapping(value = "/new",method = RequestMethod.GET)
	public ModelAndView create(String groupId,String grpType){
		Map<String, String> map = new HashMap<String, String>();
		map.put("groupId", StringUtils2.javaScriptEncode(groupId));
		map.put("grpType", StringUtils2.javaScriptEncode(grpType));
		return new ModelAndView("/plugin/valid/valid-group-new", map);
	}
	/**
	 * 校验
	 */
	@RequestMapping(value = "/check",method = RequestMethod.GET)
	public ModelAndView check(String groupId,String grpType){
		Map<String, String> map = new HashMap<String, String>();
		map.put("groupId", StringUtils2.javaScriptEncode(groupId));
		map.put("grpType", StringUtils2.javaScriptEncode(grpType));
		return new ModelAndView("/plugin/valid/valid-group-check", map);
	}
	/**
	 * 机构选择
	 */
	@RequestMapping(value = "/selectOrg",method = RequestMethod.GET)
	public ModelAndView selectOrg(){
		return new ModelAndView("/plugin/valid/valid-group-select-org");
	}
	/**
	 * grid
	 */
	@RequestMapping(value = "/loadOrgTree.*")
	@ResponseBody
	public List<CommonTreeNode> loadOrgTree(String searchNm) {
		return this.rptValidGroupBS.loadOrgTree(this.getContextPath(), searchNm);
	}
	/**
	 * grid
	 */
	@RequestMapping(value = "/list.*", method = RequestMethod.POST)
	@ResponseBody
	public Map<String, Object> list(Pager pager) {
		return this.rptValidGroupBS.list(pager);
	}
	/**
	 * 通过id获取实体
	 */
	@RequestMapping(value = "/getValidGroupById",method = RequestMethod.GET)
	public Map<String, RptValidGroupMain> getValidGroupById(String groupId) {
		return this.rptValidGroupBS.getValidGroupById(groupId);
	}
	/**
	 * 新增及修改保存
	 */
	@RequestMapping(method = RequestMethod.POST)
	public void save(RptValidGroupMain model) {
		if(StringUtils.isEmpty(model.getGroupId())){
			model.setGroupId(RandomUtils.uuid2());
		}
		this.rptValidGroupBS.saveOrUpdateEntity(model);
	}
	/**
	 * 校验
	 * @return 
	 */
	@RequestMapping(value = "/checking", method = RequestMethod.POST)
	public Map<String, Object> checking(String groupId, String validTypes, String validDate, String validOrgs) {
		Map<String, Object> params = Maps.newHashMap();
		boolean pingFlag = true;
		try {
			pingFlag = CommandRemote.testConnection(CommandRemoteType.INDEX);
		} catch (Throwable e1) {
			pingFlag = false;
			e1.printStackTrace();
		}
		if (!pingFlag) {
			logger.info("引擎连接失败！");
			params.put("msg", "引擎连接失败！");
			return params;
		}
		String info = this.rptValidGroupBS.checking(groupId, StringUtils.split(validTypes, ";"), validDate, StringUtils.split(validOrgs, ";"));
		params.put("msg", info);
		return params;
	}
	/**
	 * 批量删除
	 */
	@RequestMapping(value = "/delValidGrp",method = RequestMethod.POST)
	public Map<String,String> delValidGrp(String ids) {
		Map<String,String> rMap = new HashMap<String, String>();
		this.rptValidGroupBS.delValidGrp(ids);
		rMap.put("msg", "ok");
		return rMap;
	}
	/**
	 * 校验配置
	 */
	@RequestMapping(value = "/config",method = RequestMethod.GET)
	public ModelAndView config(String groupId,String grpType){
		Map<String, String> map = new HashMap<String, String>();
		map.put("groupId", StringUtils2.javaScriptEncode(groupId));
		map.put("grpType", StringUtils2.javaScriptEncode(grpType));
		return new ModelAndView("/plugin/valid/valid-group-config", map);
	}
	
	/**
	 * 查tab页的授权项
	 * @return
	 */
	@RequestMapping(value = "/getValidTypeTabs.*")
	@ResponseBody
	public Map<String,Object> getValidTypeTabs(){
		Map<String,Object> validRel = new HashMap<String, Object>();
		List<BioneValidTypeDef> defList = this.rptValidGroupBS.getValidTypeDef();
		if (defList != null && defList.size() > 0) {
			List<Map<String,String>> subList = new ArrayList<Map<String,String>>();
			for (BioneValidTypeDef obj : defList) {
				Map<String, String> resMap = new HashMap<String, String>();
				resMap.put("objDefNo", obj.getObjDefNo());
				resMap.put("objDefName", obj.getObjDefName());
				resMap.put("beanName", obj.getBeanName());
				subList.add(resMap);
			}
			validRel.put("data", subList);
		}
		return validRel;
	}
	
	/**
	 * 查各个tab页的指标树
	 * @return
	 */
	@RequestMapping(value = "/getIdxAsyncTree.*")
	@ResponseBody
	public List<CommonTreeNode> getIdxAsyncTree(String groupId, String grpType,String validType, String beanName, String indexVerId, CommonTreeNode node){
		List<CommonTreeNode> idxTreeNodes = new ArrayList<CommonTreeNode>();
		IValidTypeImpl valid = SpringContextHolder.getBean(beanName);
		idxTreeNodes = valid.doGetValidIdxAsync(groupId, grpType, validType, node, this.getContextPath(), indexVerId);
		return idxTreeNodes;
	}
	
	/**
	 * 保存配置校验
	 */
	@RequestMapping(value = "/saveValidRel",method = RequestMethod.POST)
	public void saveValidRel(String groupId,String grpType,String saveData){
		if(StringUtils.isNotBlank(saveData)){
			this.rptValidGroupBS.saveValidGroupInfo(groupId, grpType, saveData);
		}else{//没有保存项，就直接删除该指标组所有校验配置项
			this.rptValidGroupBS.delValidGroupById(groupId, grpType);
		}
	}
	/**
	 * 查找已勾选的树节点信息
	 * @param groupId
	 * @param grpType
	 */
	@RequestMapping(value = "/findSelectNodeObj",method = RequestMethod.GET)
	public Map<String, Object> findSelectNodeObj(String groupId, String grpType){
		return this.rptValidGroupBS.findSelectNodeObj(groupId, grpType);
	}
	/**
	 * 查找展现的树节点信息
	 * @param groupId
	 * @param grpType
	 */
	@RequestMapping(value = "/findAllTreeNodeObj",method = RequestMethod.GET)
	public Map<String, Map<String, String>> findAllTreeNodeObj(String groupId, String grpType, String beanName, String validType){
		Map<String, Map<String, String>> rstMap = new HashMap<String, Map<String, String>>();
		IValidTypeImpl valid = SpringContextHolder.getBean(beanName);
		Map<String, String> vNodes = valid.doGetAllValidTreeNode(this.getContextPath(), groupId, grpType, validType);
		rstMap.put("allTreeNodes", vNodes);
		return rstMap;
	}
	/**
	 * 加载搜索同步树
	 */
	@RequestMapping(value = "/loadSyncTree.*")
	@ResponseBody
	public List<CommonTreeNode> loadSyncTree(String groupId, String grpType,String validType, String beanName, String searchNm) {
		List<CommonTreeNode> rstNodes = new ArrayList<CommonTreeNode>();
		CommonTreeNode rootNode = new CommonTreeNode();
		rootNode.setId(validType + "@0");
		rootNode.setText("授权校验树");
		rootNode.setTitle("授权校验树");
		rootNode.setOpen(true);
		rootNode.setIcon(this.getContextPath() + "/images/classics/icons/house.png");
		rstNodes.add(rootNode);
		IValidTypeImpl valid = SpringContextHolder.getBean(beanName);
		List<CommonTreeNode> nodes = valid.doGetSearchSyncTreeNode(this.getContextPath(), groupId, grpType, validType, searchNm);
		if(null != nodes && nodes.size() > 0){
			rstNodes.addAll(nodes);
		}
		return rstNodes;
	}
}
