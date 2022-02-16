package com.yusys.bione.plugin.idxanacfg.web;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.yusys.bione.comp.common.CommonTreeNode;
import com.yusys.bione.frame.base.web.BaseController;
import com.yusys.bione.plugin.idxanacfg.service.RptAnaTmpRelBS;

/**
 * <pre>
 * Title:指标分析指标映射模版配置
 * Description: 
 * </pre>
 * 
 * @author yangyf 
 * @version 1.00.00
 * 
 *          <pre>
 * 修改记录
 *    修改后版本:   修改人：    修改日期:     修改内容:
 * </pre>
 */
@Controller
@RequestMapping("/idx/tmp/config")
public class RptIdxTmpController extends BaseController {
	
	@Autowired 
	private RptAnaTmpRelBS rptAnaTmpBS;
	
	//指标映射模版页面
	@RequestMapping(method = RequestMethod.GET)
	public String index() {
		return "/plugin/idxanacfg/idx-analysis-tmp-config";
	}
	
	/**
	 * 遍历右侧模版树
	 * @return
	 */
	@RequestMapping("/getMenuToTree")
	public Map<String, Object> getMenuToTree() {
		//创建新Map,新list
		Map<String, Object> resultMap = new HashMap<String, Object>();
		List<CommonTreeNode> resultList = new ArrayList<CommonTreeNode>();
			resultList.addAll(rptAnaTmpBS.funcToTree());//list中放置funcToTree查到的数
			resultMap.put("nodes", resultList);//Map中放置resultList的
		return resultMap;
	}
	
	/**
	 * 保存右侧页面数据
	 * @param params
	 * 		  jsp传回页面数据
	 */
	@RequestMapping("/saveMenu")
	//params是从页面传回右侧树的id，名称等，用“；”隔开
	public void saveMenu(String paramIdx,String paramTid) {
		this.rptAnaTmpBS.saveTree(paramIdx,paramTid);
	}	
	
}
