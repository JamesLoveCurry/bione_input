package com.yusys.bione.plugin.frsorg.web;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.google.common.collect.Maps;
import com.yusys.bione.comp.common.CommonComboBoxNode;
import com.yusys.bione.comp.common.CommonTreeNode;
import com.yusys.bione.comp.utils.DownloadUtils;
import com.yusys.bione.comp.utils.FilepathValidateUtils;
import com.yusys.bione.comp.utils.SpringContextHolder;
import com.yusys.bione.comp.utils.StringUtils2;
import com.yusys.bione.frame.base.web.BaseController;
import com.yusys.bione.plugin.frsorg.service.RptMgrFrsOrgBS;
import com.yusys.bione.plugin.frsorg.web.vo.RptMgrFrsOrgVo;
import com.yusys.bione.plugin.rptorg.entity.RptOrgInfo;
import com.yusys.bione.plugin.rptorg.entity.RptOrgInfoPK;
import com.yusys.bione.plugin.wizard.service.IWizardRequire;

/**
 * <pre>
 * Title:监管机构管理
 * Description:
 * </pre>
 * @author xuxin
 * @version 1.00.00
 */
@Controller
@RequestMapping("/frs/systemmanage/orgmanage")
public class RptMgrFrsOrgController extends BaseController{
	@Autowired
	private RptMgrFrsOrgBS rptMgrFrsOrgBS;
	
	@RequestMapping(method = RequestMethod.GET)
	public String index() {
		return "/frs/monitororgtree/monitororg-tree-index";
	}
	
	/**
	 * 点击树弹出右侧面板
	 * @param orgNo
	 * @param orgType
	 * @param upOrgNo
	 * @param mgrOrgNo
	 * @return
	 */
	@RequestMapping(value="/orgFrame", method = RequestMethod.GET)
	public ModelAndView orgFrame(String orgNo, String orgType, String upOrgNo, String mgrOrgNo, String orgLevel, String orgClass){
		ModelMap mm = new ModelMap();
		mm.put("orgNo", StringUtils2.javaScriptEncode(orgNo));
		mm.put("orgType", StringUtils2.javaScriptEncode(orgType));
		mm.put("upOrgNo", StringUtils2.javaScriptEncode(upOrgNo));
		mm.put("mgrOrgNo", StringUtils2.javaScriptEncode(mgrOrgNo));
		mm.put("orgLevel", StringUtils2.javaScriptEncode(orgLevel));
		mm.put("orgClass", StringUtils2.javaScriptEncode(orgClass));
		return new ModelAndView("/frs/monitororgtree/monitororg-temp-grid",mm);
	}
	
	/**
	 * 获得机构树
	 * @param orgNm
	 * @param orgType
	 * @param orgNo
	 * @param upOrgNo
	 * @param mgrOrgNo
	 * @param state
	 * @return
	 */
	@RequestMapping(value="/getTree", method = RequestMethod.GET)
	@ResponseBody
	public List<CommonTreeNode> getTree(String orgNm, String orgType, String orgNo, String upOrgNo, String mgrOrgNo, String state, String isOrgReport){		
		return this.rptMgrFrsOrgBS.getDataTrees(orgType,upOrgNo,orgNo,this.getContextPath(),mgrOrgNo,state, isOrgReport);
	}
	
	/**
	 * 按条件加载机构树
	 * @param orgType
	 * @param orgNm
	 * @return 
	 */
	@RequestMapping(value="/searchOrgTree", method = RequestMethod.POST)
	@ResponseBody
	public List<CommonTreeNode> searchOrgTree(String orgType, String orgNm, String isOrgReport){
		return this.rptMgrFrsOrgBS.searchOrgTree(orgType, orgNm, getContextPath(), isOrgReport);
	}
	
	
	/**
	 * 删除机构
	 * @param orgType
	 * @param orgNo
	 * @param upOrgNo
	 * @return 删除结果
	 */
	@RequestMapping(value="/deleteOrg", method = RequestMethod.POST)
	@ResponseBody
	public String deleteOrg(String orgType, String orgNo, String upOrgNo){
		String flag="";
		List<RptOrgInfo> list = this.rptMgrFrsOrgBS.getListOrg(orgType,orgNo);
		if( list.isEmpty() || list == null){
			
			String bandTaskInfo = rptMgrFrsOrgBS.getBandTask(orgType,orgNo);
			if(StringUtils.isNotEmpty(bandTaskInfo)){
				return bandTaskInfo;
			}
			this.rptMgrFrsOrgBS.deleteOrg(orgType,orgNo);
			return flag;
		}else{
			flag="false";
			return flag;
		}
	}
	
	/**
	 * 修改或新增机构
	 * @param orgType
	 * @param upOrgNo
	 * @param orgNo
	 * @return
	 */
	@RequestMapping(value="/addOrg", method = RequestMethod.GET)
	@ResponseBody
	public Map<String,String> addOrg(String orgType, String upOrgNo, String orgNo){
		Map<String,String> map = Maps.newHashMap();
		map.put("orgType", orgType);
		map.put("upOrgNo", upOrgNo);
		map.put("orgNo", orgNo);
		return map;
	}
	
	/**
	 * 下载机构信息
	 * @param response
	 * @param orgType
	 * @param type
	 * @return
	 */
	@RequestMapping(value="/download", method = RequestMethod.POST)
	@ResponseBody
	public Map<String,Object> download(HttpServletResponse response, String orgType, String type){
		Map<String,Object> map=new HashMap<String, Object>();
		IWizardRequire require;
		type=type.substring(0, 1).toLowerCase()+type.substring(1, type.length());
		require = SpringContextHolder.getBean(type+"WizardRequire");
		String fileName=require.export(orgType);
		map.put("fileName", fileName);
		return map;
	}
	
	/**
	 * 导出文件
	 * @param response
	 * @param type
	 * @param fileName
	 * @throws UnsupportedEncodingException
	 */
	@RequestMapping(value="/export", method = RequestMethod.GET)
	public void export(HttpServletRequest request, HttpServletResponse response, String type, String fileName) throws UnsupportedEncodingException {
		if(FilepathValidateUtils.validateFilepath(fileName)) {
			try {
				File file=new File(fileName);
				
				if (request.getHeader("User-Agent").toLowerCase().indexOf("firefox") > 0) {
					fileName = new String(fileName.getBytes("UTF-8"), "UTF-8"); // firefox浏览器
				} else if (request.getHeader("User-Agent").toLowerCase().indexOf("msie") > 0 
						|| request.getHeader("User-Agent").toLowerCase().indexOf("trident") > 0 
						|| request.getHeader("User-Agent").toLowerCase().indexOf("edge")> 0) {
					fileName = URLEncoder.encode(fileName, "UTF-8");// IE浏览器
				} else if (request.getHeader("User-Agent").toLowerCase().indexOf("chrome") > 0) {
					fileName = new String(fileName.getBytes("UTF-8"), "UTF-8");// 谷歌
				}
				
				DownloadUtils.download(response, file,type+"info.xls");
				file.delete();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * 判断机构名是否唯一
	 * @param orgType
	 * @param orgNm
	 * @param sourceOrgNo
	 * @return
	 */
	@RequestMapping(value="getOrgNm", method = RequestMethod.POST)
	@ResponseBody
	public boolean getOrgNm(String orgType,String orgNm,String sourceOrgNo){
		return this.rptMgrFrsOrgBS.getOrgNm(orgType,orgNm,sourceOrgNo);
	}
	
	/**
	 * 判断机构编号是否唯一
	 * @param orgType
	 * @param orgNo
	 * @param sourceOrgNo
	 * @return
	 */
	@RequestMapping(value="getOrgNo", method = RequestMethod.POST)
	@ResponseBody
	public boolean getOrgNo(String orgType,String orgNo,String sourceOrgNo){
		return this.rptMgrFrsOrgBS.getOrgNo(orgType,orgNo,sourceOrgNo);
	}
	
	/**
	 * 上级机构
	 * @param orgType
	 * @return
	 */
	@RequestMapping(value="/chooseUpOrg", method = RequestMethod.GET)
	public ModelAndView chooseUpOrg(String orgType){
		orgType = StringUtils2.javaScriptEncode(orgType);
		return new ModelAndView("/frs/monitororgtree/choose-up-org", "orgType", orgType);
	}
	
	/**
	 * 行内机构
	 * @param orgType
	 * @return
	 */
	@RequestMapping(value="chooseInterOrg", method = RequestMethod.GET)
	public ModelAndView chooseInterOrg(String orgType){
		orgType = StringUtils2.javaScriptEncode(orgType);
		return new ModelAndView("/frs/monitororgtree/monitororg-choose-org", "orgType", orgType);
	}
	
	/**
	 * 人行机构
	 * @param orgNo
	 * @param orgType
	 * @param flag
	 */
	@RequestMapping(value="getBlankOrgInfo", method = RequestMethod.GET)
	@ResponseBody
	public RptMgrFrsOrgVo getBlankOrgInfo(String orgNo, String orgType, String flag){
		return rptMgrFrsOrgBS.getOrgInfo1(orgNo,orgType,flag);
	}
	
	/**
	 * 保存
	 * @param vo
	 * @param orgNo
	 * @param sourceOrgNo
	 * @param type
	 * @param lastUpOrgNo
	 */
	@RequestMapping(value="/saveInfo", method = RequestMethod.GET)
	public void saveInfo(RptMgrFrsOrgVo vo,String orgNo,String sourceOrgNo,String type, String lastUpOrgNo){
		if(!StringUtils.isEmpty(sourceOrgNo)){
			RptOrgInfoPK pk = new RptOrgInfoPK();   //修改
			pk.setOrgNo(orgNo);
			pk.setOrgType(type);
			vo.setId(pk);
			if(vo.getUpOrgNo().equals(lastUpOrgNo)){//未修改上级机构
				this.rptMgrFrsOrgBS.updateOrg(vo,orgNo,sourceOrgNo);
			}else{
				this.rptMgrFrsOrgBS.updateOrgChangeUpNo(vo, orgNo, sourceOrgNo, lastUpOrgNo);
			}
		}else{
			RptOrgInfoPK pk = new RptOrgInfoPK();   //新增
			pk.setOrgNo(orgNo);
			pk.setOrgType(type);
			vo.setId(pk);
			this.rptMgrFrsOrgBS.addOrg(vo);
		}
	}
	
	/**
	 * 通过id获取机构树
	 * @param id
	 * @return
	 */
	@RequestMapping(value = "/getOrgTree", method = RequestMethod.GET)
	@ResponseBody
	public List<CommonTreeNode> getOrgTree(String id){
		return this.rptMgrFrsOrgBS.getOrgTree(id,getContextPath());
	}
	
	/**
	 * 获取全部机构类型
	 * @return
	 */
	@RequestMapping(value = "/orgClassList.*", method = RequestMethod.POST)
	@ResponseBody
	public List<CommonComboBoxNode> orgClassList() {
		return this.rptMgrFrsOrgBS.getRptClassList(null, null);
	}
}
