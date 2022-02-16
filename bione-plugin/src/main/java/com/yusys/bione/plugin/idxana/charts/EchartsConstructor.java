package com.yusys.bione.plugin.idxana.charts;

import java.util.Map;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.yusys.bione.plugin.base.common.GlobalConstants4plugin;

/**
 * <pre>
 * Title:Echart图表构造器
 * Description: 
 * </pre>
 * 
 * @author yangyf
 * @version 1.00.00
 * 
 *          <pre>
 * 修改记录
 *    修改后版本:     修改人：  修改日期:     修改内容:
 * </pre>
 */

public class EchartsConstructor {
	
	/**
	 * 简单的图表构造器
	 * @param chartType
	 * @return
	 */
	public static JSONObject getChartsOption(String showType ,Map<String,Object> chartOption ,Map<String,Object> chartCfg){
		JSONObject option = null;
		if(GlobalConstants4plugin.BIAXIAL_BAR.equals(showType)){
			option = (JSONObject)JSON.toJSON(new EchartsBarOption(chartOption));
		}else if(GlobalConstants4plugin.PIE.equals(showType)){
			option = (JSONObject)JSON.toJSON(new EchartsPieOption(chartOption));
			option.remove("xAxis");
			option.remove("yAxis");
		}else if(GlobalConstants4plugin.BAR.equals(showType)){
			option = (JSONObject)JSON.toJSON(new EchartsBarOption(chartOption));
		}else if(GlobalConstants4plugin.TREE_MAP.equals(showType)){
			option = (JSONObject)JSON.toJSON(new EchartsTreeMapOption(chartOption));
			option.remove("xAxis");
			option.remove("yAxis");
		}
		if(chartCfg != null){
			option.putAll(chartCfg);
		}
		return option;
	}
}
