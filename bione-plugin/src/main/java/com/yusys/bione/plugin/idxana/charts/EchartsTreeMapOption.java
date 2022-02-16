package com.yusys.bione.plugin.idxana.charts;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EchartsTreeMapOption extends EchartsOption {
	
	private List<Map<String,Object>> series = new ArrayList<Map<String,Object>>(); //矩形树图系列
	
	@SuppressWarnings("unchecked")
	public EchartsTreeMapOption(Map<String, Object> option) {
		super(option);
		series = this.giveSeries((List<Map<String, Object>>) option.get("series"));
	}

	/**
	 * 矩形树图系列赋值
	 * @param chSeries
	 * @return
	 */
	private List<Map<String,Object>> giveSeries(List<Map<String,Object>> chSeriesList){
		List<Map<String,Object>> series= new ArrayList<Map<String,Object>>();
		for(Map<String,Object> chSeries : chSeriesList){
			Map<String,Object> breadcrumb= new HashMap<String, Object>();
			breadcrumb.put("show", false);//是否显示面包屑路径
			chSeries.put("breadcrumb", breadcrumb);
			chSeries.put("nodeClick", false);//节点点击效果
			chSeries.put("roam", false);//是否开启拖拽和放缩
			chSeries.put("type", "treemap");
			series.add(chSeries);
		}
		return series;
	}

	public List<Map<String, Object>> getSeries() {
		return series;
	}

	public void setSeries(List<Map<String, Object>> series) {
		this.series = series;
	}
	
	
}
