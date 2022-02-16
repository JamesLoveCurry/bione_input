package com.yusys.bione.plugin.idxana.charts;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class EchartsOption {
	
	private List<String> color = new ArrayList<String>(); //图表颜色
	private List<Map<String,Object>> yAxis = new ArrayList<Map<String,Object>>(); //y轴
	private List<Map<String,Object>> xAxis = new ArrayList<Map<String,Object>>(); //x轴
	private Map<String,Object> tooltip = new HashMap<String, Object>(); //提示框
	private Map<String,Object> legend = new HashMap<String, Object>(); //图例
	private Map<String,Object> toolbox = new HashMap<String, Object>(); //工具栏

	@SuppressWarnings("unchecked")
	protected EchartsOption(Map<String, Object> option){
		color = this.giveColor(option.get("color"));
		toolbox = this.giveToolbox((Map<String, Object>) option.get("toolbox"));
		tooltip = this.giveTooltip((Map<String, Object>) option.get("tooltip"));
		legend = this.giveLegend((Map<String, Object>) option.get("legend"));
		xAxis = this.givexAxis(option.get("xAxis"));
		yAxis = this.giveyAxis(option.get("yAxis"));
	}
	
	/**
	 * 图表颜色赋值
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private List<String> giveColor(Object chColor){
		List<String> color = Arrays.asList("#00d1bc","#d9ca00","#9767b8","#cd895c","#99cc56","#c23531","#2f4554","#61a0a8","#d48265","#91c7ae","#749f83");
		if(chColor != null ){
			color = (List<String>) chColor;//用户修改color
		}
		return color;
	}
	
	/**
	 * 提示框赋值
	 * @return
	 */
	private Map<String,Object> giveTooltip(Map<String,Object> chTooltip){
		Map<String,Object> tooltip= new HashMap<String, Object>();
		Map<String,Object> axisPointer = new HashMap<String, Object>();
		axisPointer.put("type", "shadow");
		tooltip.put("trigger", "axis");
		tooltip.put("axisPointer", axisPointer);
		if(chTooltip != null){
			tooltip.putAll(chTooltip);
		}
		return tooltip;
	}
	
    /**
     * 图例赋值
     * @return
     */
	private Map<String,Object> giveLegend(Map<String,Object> chLegend){
		Map<String,Object> legend= new HashMap<String, Object>();
		legend.put("orient", "horizontal");
		legend.put("left", "center");
		legend.put("top", "top");
		if(chLegend != null){
			legend.putAll (chLegend);
		}
		return legend;
	}
	
	/**
	 * 工具栏赋值
	 * @return
	 */
	private Map<String,Object> giveToolbox(Map<String,Object> chToolbox){
		Map<String,Object> toolbox= new HashMap<String, Object>();
		Map<String,Object> feature= new HashMap<String, Object>();
		Map<String,Object> data= new HashMap<String, Object>();
		List<String> type = Arrays.asList("line","bar");
		data.put("readOnly", false);
		feature.put("dataView", this.giveFeature(data));
		feature.put("restore", this.giveFeature(null));
		feature.put("saveAsImage", this.giveFeature(null));
		data = new HashMap<String, Object>();
		data.put("type", type);
		feature.put("magicType", this.giveFeature(data));
		toolbox.put("feature", feature);
		if(chToolbox != null){
			toolbox.putAll(chToolbox);
		}
		return toolbox;
	}
	
	/**
	 * 工具栏各个配置项赋值
	 * @return
	 */
	private Map<String,Object> giveFeature(Map<String,Object> data){
		Map<String,Object> returnMap = new HashMap<String, Object>();
		returnMap.put("show", true);
		if(data != null){
			returnMap.putAll(data);
		}
		return returnMap;
	}
	
	/**
	 * y轴赋值
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private List<Map<String,Object>> giveyAxis(Object chyAxis){
		List<Map<String,Object>> yAxis= new ArrayList<Map<String,Object>>();
		if(chyAxis != null){
			yAxis = (List<Map<String, Object>>) chyAxis;
		}
		return yAxis;
	}
	
	/**
	 * x轴赋值
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private List<Map<String,Object>> givexAxis(Object chxAxis){
		List<Map<String,Object>> xAxis= new ArrayList<Map<String,Object>>();
		if(chxAxis != null){
			xAxis = (List<Map<String, Object>>) chxAxis;
		}
		return xAxis;
	}
	
	public List<String> getColor() {
		return color;
	}

	public void setColor(List<String> color) {
		this.color = color;
	}

	public List<Map<String,Object>> getyAxis() {
		return yAxis;
	}

	public void setyAxis(List<Map<String,Object>> yAxis) {
		this.yAxis = yAxis;
	}

	public List<Map<String,Object>> getxAxis() {
		return xAxis;
	}

	public void setxAxis(List<Map<String,Object>> xAxis) {
		this.xAxis = xAxis;
	}

	public Map<String, Object> getTooltip() {
		return tooltip;
	}

	public void setTooltip(Map<String, Object> tooltip) {
		this.tooltip = tooltip;
	}

	public Map<String, Object> getLegend() {
		return legend;
	}

	public void setLegend(Map<String, Object> legend) {
		this.legend = legend;
	}

	public Map<String, Object> getToolbox() {
		return toolbox;
	}

	public void setToolbox(Map<String, Object> toolbox) {
		this.toolbox = toolbox;
	}
}
