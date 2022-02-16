package com.yusys.bione.plugin.idxana.charts;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class EchartsPieOption extends EchartsOption {

	private List<Map<String,Object>> series = new ArrayList<Map<String,Object>>(); //饼图系列

	@SuppressWarnings("unchecked")
	public EchartsPieOption(Map<String, Object> option) {
		super(option);
		series = this.giveSeries((List<Map<String, Object>>) option.get("series"));
	}
	
	/**
	 * 饼图系列赋值
	 * @param chSeries
	 * @return
	 */
	private List<Map<String,Object>> giveSeries(List<Map<String,Object>> chSeriesList){
		List<Map<String,Object>> series= new ArrayList<Map<String,Object>>();
		for(Map<String,Object> chSeries : chSeriesList){
			List<String> radius = Arrays.asList("0","55%");//半径
			chSeries.put("type", "pie");
			if(chSeries.get("radius") == null)
				chSeries.put("radius", radius);
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
