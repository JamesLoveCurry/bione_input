package com.yusys.bione.plugin.rptorg.web;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.yusys.bione.comp.common.CommonTreeNode;
import com.yusys.bione.comp.utils.SpringContextHolder;
import com.yusys.bione.comp.utils.StringUtils2;
import com.yusys.bione.frame.base.web.BaseController;
import com.yusys.bione.plugin.rptorg.entity.RptOrgInfo;
import com.yusys.bione.plugin.rptorg.entity.RptOrgInfoPK;
import com.yusys.bione.plugin.rptorg.service.RptOrgInfoBS;
import com.yusys.bione.plugin.rptorg.web.vo.RptOrgInfoVo;
import com.yusys.bione.plugin.wizard.service.IWizardRequire;
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

@Controller
@RequestMapping("/rpt/frame/rptorg")
public class RptOrgInfoController extends BaseController{
	
	@Autowired
	private RptOrgInfoBS rptOrgInfoBS;
	
	@RequestMapping(method = RequestMethod.GET)
	public String index() {
		return "/plugin/rptorg/rptorg-tree-index";
	}
	
	@RequestMapping(value="/getTree.*",method=RequestMethod.POST)
	@ResponseBody
	public List<CommonTreeNode> getTree(String orgType,String upOrgNo,String orgNo,String mgrOrgNo,String state){
		return this.rptOrgInfoBS.getDataTrees(orgType,upOrgNo,orgNo,getContextPath(),mgrOrgNo,state);
	}
	
	/**
	 * 获取机构树
	 * @孙玉明
	 */
	@RequestMapping(value = "/grid")
	public ModelAndView get(String orgNo,String orgType,String upOrgNo,String mgrOrgNo){
		ModelMap model = new ModelMap();
		model.put("orgNo", StringUtils2.javaScriptEncode(orgNo));
		model.put("orgType", StringUtils2.javaScriptEncode(orgType));
		model.put("upOrgNo", StringUtils2.javaScriptEncode(upOrgNo));
		model.put("mgrOrgNo", StringUtils2.javaScriptEncode(mgrOrgNo));
		return new ModelAndView("/plugin/rptorg/rptorg-temp-grid",model);
	}
	
	@RequestMapping(value="/getInfo")
	@ResponseBody
	public RptOrgInfoVo getInfo(String orgNo,String orgType,String upOrgNo ,String mgrOrgNo){
		RptOrgInfoVo list = this.rptOrgInfoBS.showAll(orgNo,orgType,upOrgNo,mgrOrgNo);
		return list;
	}
	
	//add
	@RequestMapping(value="addOrg")
	public ModelAndView addOrg(String orgType,String upOrgNo,String orgNo){
		ModelMap model = new ModelMap();
		model.put("orgType", StringUtils2.javaScriptEncode(orgType));
		model.put("upOrgNo", StringUtils2.javaScriptEncode(upOrgNo));
		model.put("orgNo", StringUtils2.javaScriptEncode(orgNo));
		return new ModelAndView("/plugin/rptorg/rptorg-temp-grid",model);
	}
	
	//以下3个是新增时候查询唯一性
	@RequestMapping(value="getOrgNm" )
	@ResponseBody
	public boolean getOrgNm(String orgType,String orgNm,String sourceOrgNo){
		return this.rptOrgInfoBS.getOrgNm(orgType,orgNm,sourceOrgNo);
	}
	
	@RequestMapping(value="getOrgNo")
	@ResponseBody
	public boolean getOrgNo(String orgType,String orgNo,String sourceOrgNo){
		return this.rptOrgInfoBS.getOrgNo(orgType,orgNo,sourceOrgNo);
	}
	@RequestMapping(value="getFinanceOrgNo" )
	@ResponseBody
	public boolean getFinanceOrgNo(String orgType,String financeOrgNo,String sourceOrgNo){
		return this.rptOrgInfoBS.getFinanceOrgNo(orgType,financeOrgNo,sourceOrgNo);
	}
	
	@RequestMapping(value="come")
	public ModelAndView come(String orgType,String upOrgNo){
		ModelMap model = new ModelMap();
		model.put("orgType", StringUtils2.javaScriptEncode(orgType));
		model.put("upOrgNo", StringUtils2.javaScriptEncode(upOrgNo));
		return new ModelAndView("/plugin/rptorg/rptorg-tree-come",model);
	}
	
	//新增数据
	@RequestMapping(value="addInfo")
	@ResponseBody
	public Map<String,Object> addInfo(RptOrgInfoVo vo,String orgNo,String sourceOrgNo,String type, String lastUpOrgNo){
		 Map<String,Object> result = new HashMap<String, Object>();
		vo.setMgrOrgNo(orgNo);
		vo.setIsVirtualOrg("N");
		if(!StringUtils.isEmpty(sourceOrgNo)){
			RptOrgInfoPK pk = new RptOrgInfoPK();   //修改
			pk.setOrgNo(orgNo);
			pk.setOrgType(type);
			vo.setId(pk);
			if(vo.getUpOrgNo().equals(lastUpOrgNo)){//未修改上级机构
				this.rptOrgInfoBS.updateOrg(vo,orgNo,sourceOrgNo);
			}else{
				this.rptOrgInfoBS.updateOrgChangeUpNo(vo, orgNo, sourceOrgNo, lastUpOrgNo);
			}
		}else{
			RptOrgInfoPK pk = new RptOrgInfoPK();   //新增
			pk.setOrgNo(orgNo);
			pk.setOrgType(type);
			vo.setId(pk);
			this.rptOrgInfoBS.addOrg(vo);
		}
		return result;
	}
	
	@RequestMapping(value="/deleteOrg")
	@ResponseBody
	public String deleteOrg(String orgType,String orgNo,String upOrgNo){
		List<RptOrgInfo> list = this.rptOrgInfoBS.getListOrg(orgType,orgNo);
		if( list.size() == 0 || list == null){
			this.rptOrgInfoBS.deleteOrg(orgType,orgNo);
			return ""; //删除数据并返回空
		}else{
			return "false";
		}
	}
	
	@RequestMapping(value="/getOrgInfo")
	@ResponseBody
	public RptOrgInfoVo getOrgInfo(String orgNo,String orgType){
		RptOrgInfoVo org = this.rptOrgInfoBS.getOrgInfo(orgNo,orgType);
		return org;
	}
	
	@RequestMapping(value="/searchTree.*" , method = RequestMethod.POST)
	@ResponseBody
	public List<CommonTreeNode> searchTree(String orgNm){
		return this.rptOrgInfoBS.searchTree(orgNm,getContextPath());
	}
	
	//权限过滤
	@RequestMapping(value = "/getOrgTree")
	@ResponseBody
	public List<CommonTreeNode> getOrgTree(String id){
		return this.rptOrgInfoBS.getOrgTree(id,getContextPath());
	}
	
	@RequestMapping(value = "/getOrgPage")
	public ModelAndView getOrgPage(){
		return new ModelAndView("/plugin/rptorg/rptorg-choose-org");
	}
	
	@RequestMapping(value = "/chooseUpOrgNo")
	public ModelAndView chooseUpOrgNo(String orgType){
		orgType = StringUtils2.javaScriptEncode(orgType);
		return new ModelAndView("/plugin/rptorg/choose-up-org", "orgType", orgType);
	}
	/**
	 * 导出机构
	 * @param ids
	 * @param type
	 * @param response
	 * @return
	 */
	@RequestMapping("/download")
	@ResponseBody
	public Map<String,Object> download(String orgType,String type,HttpServletResponse response){
		Map<String,Object> map=new HashMap<String, Object>();
		IWizardRequire require;
		type=type.substring(0, 1).toLowerCase()+type.substring(1, type.length());
		require = SpringContextHolder.getBean(type+"WizardRequire");
		String fileName = require.export(orgType);
		map.put("fileName", fileName);
		return map;
	}
	
	/**
	 * 获取机构树
	 * @return
	 */
	@RequestMapping("/getAllOrgTree")
	@ResponseBody
	public Map<String,Object> getAllOrgTree(String orgType,String id,String nodeType,String isSum){
		/*this.rptMgrBS.getRptTreeSync(GlobalConstants.RPT_EXT_TYPE_FRS,
				GlobalConstants.RPT_TYPE_DESIGN, "", true, true);*/
		Map<String,Object> map=new HashMap<String, Object>();
		map.put("treeInfo", this.rptOrgInfoBS.getAllOrgTree(orgType,nodeType,isSum,false));
		map.put("treeData", this.rptOrgInfoBS.getAllOrgData(orgType));
		return map;
	}
}
