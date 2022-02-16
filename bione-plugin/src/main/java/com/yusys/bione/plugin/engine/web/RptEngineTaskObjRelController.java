package com.yusys.bione.plugin.engine.web;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.yusys.bione.comp.common.CommonTreeNode;
import com.yusys.bione.comp.entity.page.Pager;
import com.yusys.bione.comp.utils.StringUtils2;
import com.yusys.bione.frame.base.web.BaseController;
import com.yusys.bione.frame.excel.ExcelBS;
import com.yusys.bione.plugin.engine.entity.RptEngineTsk;
import com.yusys.bione.plugin.engine.entity.RptEngineTskobjRel;
import com.yusys.bione.plugin.engine.entity.RptEngineTskobjRelPK;
import com.yusys.bione.plugin.engine.service.RptEngineTaskObjRelBS;
import com.yusys.bione.plugin.rptidx.service.IdxInfoBS;

@Controller
@RequestMapping("/report/frame/taskobjrel")
public class RptEngineTaskObjRelController extends BaseController{

	@Autowired
	private RptEngineTaskObjRelBS relBS;
	@Autowired
	private IdxInfoBS idxBS;
	@Autowired
	private ExcelBS excelBS;
	/*
	 * 任务组配置页面跳转
	 */
	@RequestMapping(method = RequestMethod.GET)
	public ModelAndView index() {
		return new ModelAndView("/plugin/taskobjrel/taskobjrel-index");
	}
	
	@RequestMapping(value="list.*",method = RequestMethod.POST)
	@ResponseBody
	public Map<String,Object>  search(Pager page){
		return relBS.getTaskObjRel(page);
	}
	
	@RequestMapping(value="edit",method = RequestMethod.GET)
	public ModelAndView edit(String id,String type){
		ModelMap mm = new ModelMap();
		mm.put("id", StringUtils2.javaScriptEncode(id));
		mm.put("type", StringUtils2.javaScriptEncode(type));
		return new ModelAndView("/plugin/taskobjrel/taskobjrel-index-edit", mm);
	}
	
	@RequestMapping(value="create",method = RequestMethod.POST)
	@ResponseBody
	public Map<String,Object>  create(String ids,String taskNm,String type,String id,String taskNo){
		Map<String,Object> result=new HashMap<String, Object>();
		if(StringUtils.isNotBlank(ids)){
			String idinfos[]=StringUtils.split(ids,",");
			if(StringUtils.isNotBlank(id)){
				this.relBS.removeTskRel(id);
			}
			RptEngineTsk tsk = new RptEngineTsk();
			tsk.setTaskNm(taskNm);
			tsk.setTaskNo(taskNo);
			tsk.setObjType(type);
			this.relBS.saveOrUpdateEntity(tsk);
			List<RptEngineTskobjRel> rels = new ArrayList<RptEngineTskobjRel>();
			for(int i=0;i<idinfos.length;i++){
				RptEngineTskobjRelPK pk=new RptEngineTskobjRelPK();
				RptEngineTskobjRel rel=new RptEngineTskobjRel();
				pk.setTaskNo(taskNo);
				pk.setObjType(type);
				pk.setObjNo(idinfos[i]);
				rel.setId(pk);
				rels.add(rel);
			}
			this.excelBS.saveEntityJdbcBatch(rels);
		}
		return result;
	}
	
	@RequestMapping(value="getInfo",method = RequestMethod.POST)
	@ResponseBody
	public Map<String,Object>  getInfo(String type,String taskNo){
		Map<String,Object> result=new HashMap<String, Object>();
		Map<String,String> checkMap=new HashMap<String, String>();
		String taskNm="";
		RptEngineTsk tsk = this.relBS.getEntityById(RptEngineTsk.class, taskNo);
		if(tsk!=null){
			taskNm = tsk.getTaskNm();
		}
		List<RptEngineTskobjRel> rels=this.relBS.getEntityListByProperty(RptEngineTskobjRel.class, "id.taskNo", taskNo);
		if(rels!=null&&rels.size()>0){
			for(RptEngineTskobjRel rel : rels){
				checkMap.put(rel.getId().getObjNo(), rel.getId().getObjNo());
			}
		}
		result.put("taskNm", taskNm);
		result.put("checkMap", checkMap);
		return result;
	}
	
	@RequestMapping(value="checkValidate")
	@ResponseBody
	public boolean  checkValidate(String taskNo){
		List<RptEngineTskobjRel> rels=this.relBS.getEntityListByProperty(RptEngineTskobjRel.class, "id.taskNo", taskNo);
		if(rels!=null&&rels.size()>0){
			return false;
		}
		return true;
	}
	
	@RequestMapping(value="getTreeInfo",method = RequestMethod.POST)
	@ResponseBody
	public Map<String,Object>  getTreeInfo(){
		Map<String,Object> result=new HashMap<String, Object>();
		List<CommonTreeNode> nodes= idxBS.getTree(this.getContextPath(), null, "1", "0", null, null, null, null, "1", null, "1", null);
		Map<String,List<CommonTreeNode>> treeInfo=new HashMap<String, List<CommonTreeNode>>();
		setTreeInfo(treeInfo, nodes);
		result.put("treeInfo", treeInfo);
		return result;
	}
	
	@RequestMapping(value="remove",method = RequestMethod.POST)
	@ResponseBody
	public Map<String,Object> remove(String ids){
		Map<String,Object> result=new HashMap<String, Object>();
		String taskNo[]=StringUtils.split(ids,",");
		for(int i=0;i<taskNo.length;i++){
			this.relBS.removeTsk(taskNo[i]);
			this.relBS.removeTskRel(taskNo[i]);
		}
			
		return result;
	}
	
	private void setTreeInfo(Map<String,List<CommonTreeNode>> treeInfo,List<CommonTreeNode> nodes){
		if(nodes!=null&&nodes.size()>0){
			for(CommonTreeNode node: nodes){
				if(treeInfo.get(node.getUpId())==null){
					List<CommonTreeNode> nlist=new ArrayList<CommonTreeNode>();
					nlist.add(node);
					treeInfo.put(node.getUpId(), nlist);
				}
				else{
					treeInfo.get(node.getUpId()).add(node);
				}
				setTreeInfo(treeInfo, node.getChildren());
			}
		}
	}
}
