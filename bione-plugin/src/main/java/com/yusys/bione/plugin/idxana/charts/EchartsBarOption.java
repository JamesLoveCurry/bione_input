package com.yusys.bione.plugin.idxana.charts;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class EchartsBarOption extends EchartsOption {
	
	private List<Map<String,Object>> series = new ArrayList<Map<String,Object>>(); //柱折图系列
	
	@SuppressWarnings("unchecked")
	public EchartsBarOption(Map<String, Object> option) {
		super(option);
		series = this.giveSeries((List<Map<String, Object>>) option.get("series"));
	}

	/**
	 * 柱折图系列赋值
	 * @param chSeries
	 * @return
	 */
	private List<Map<String, Object>> giveSeries(List<Map<String, Object>> chSeries){
		List<Map<String, Object>> series= new ArrayList<Map<String,Object>>();
		if(chSeries != null){
			series = chSeries;
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
