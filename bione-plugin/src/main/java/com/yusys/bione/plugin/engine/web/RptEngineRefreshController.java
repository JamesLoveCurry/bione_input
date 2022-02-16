package com.yusys.bione.plugin.engine.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import com.yusys.bione.comp.utils.SpringContextHolder;
import com.yusys.bione.plugin.engine.service.RptEngineBS;
import com.yusys.bione.plugin.engine.util.EngineUtils;


@Controller
@RequestMapping("/report/frame/engineRefresh")
public class RptEngineRefreshController {
	
	private RptEngineBS rptEngineBS = SpringContextHolder.getBean("rptEngineBS");
	/**
	 * 引擎辅助功能
	 * @return
	 */
	@RequestMapping(method = RequestMethod.GET)
	public ModelAndView index() {
		return new ModelAndView("/plugin/engine/engine-refresh-index");
	}
	@RequestMapping("/refresh")
	public void engineRefresh(String cfgType,String nodeType,String dsType,String modelType,String indexType){
		if(cfgType!=null&&nodeType!=null&&dsType!=null&&modelType!=null&&indexType!=null){
			EngineUtils.refreshAllCache();
		}else{
			if(cfgType!=null){
				EngineUtils.refreshIdxCfgCache();
			}
			if(nodeType!=null){
				EngineUtils.refreshNodeCfgCache();		
			}
			if(dsType!=null){
				EngineUtils.refreshDsCfgCache();
			}
			/*if(modelType!=null){
				EngineUtils.refreshModelCfgCache();
			}*/
			if(indexType!=null){
				EngineUtils.refreshIndexCfgCache();
			}
		}
		
	}
	
	@RequestMapping("/force")
	public void forchSuccess(){
		this.rptEngineBS.stopTask("");
	}
}
