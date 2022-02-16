package com.yusys.bione.frame.activiti.entity;

import java.util.List;
import java.util.Map;

import com.google.common.collect.Maps;

public class ActFlowInfoVO {
	private String activityId;
	private String height;
	//private String properties;
	private String width;
	private String xpiont;
	private String ypoint;
	
	private String flow;
	private String flowSour;
	private String flowTarg;
	private String id;
	
	private String name;
	private List<String> xPointArray;
	private List<String> yPointArray;
	
	
	private String startEvent;
	private String endEvent;
	private String sequenceFlow;
	
	private String seqId;
	private String flowId;
	private String sourceId;
	private String targetId;
	
	
	/* 参数 */
	private Map<String, String> properties = Maps.newHashMap();
	
	public String getFlowSour() {
		return flowSour;
	}

	public void setFlowSour(String flowSour) {
		this.flowSour = flowSour;
	}
	
	public String getFlowTarg() {
		return flowTarg;
	}

	public void setFlowTarg(String flowTarg) {
		this.flowTarg = flowTarg;
	}
	
	public String getSouceId() {
		return sourceId;
	}

	public void setSouceId(String sourceId) {
		this.sourceId = sourceId;
	}
	
	public String getTargetId() {
		return targetId;
	}

	public void setTargetId(String targetId) {
		this.targetId = targetId;
	}
	
	public String getFlowId() {
		return flowId;
	}

	public void setFlowId(String flowId) {
		this.flowId = flowId;
	}
	
	public String getSeqId() {
		return seqId;
	}

	public void setSeqId(String seqId) {
		this.seqId = seqId;
	}
	
	
	public String getStartEvent() {
		return startEvent;
	}

	public void setStartEvent(String startEvent) {
		this.startEvent = startEvent;
	}
	
	public String getEndEvent() {
		return endEvent;
	}

	public void setEndEvent(String endEvent) {
		this.endEvent = endEvent;
	}
	
	public String getSequenceFlow() {
		return sequenceFlow;
	}

	public void setSequenceFlow(String sequenceFlow) {
		this.sequenceFlow = sequenceFlow;
	}
	
	
	public String getActivityId() {
		return activityId;
	}

	public void setActivityId(String activityId) {
		this.activityId = activityId;
	}
	
	public String getHeight() {
		return height;
	}

	public void setHeight(String height) {
		this.height = height;
	}
	
	
	public String getWidth() {
		return width;
	}

	public void setWidth(String width) {
		this.width = width;
	}
	public String getXpiont() {
		return xpiont;
	}

	public void setXpiont(String xpiont) {
		this.xpiont = xpiont;
	}
	
	public String getYpoint() {
		return ypoint;
	}

	public void setYpoint(String ypoint) {
		this.ypoint = ypoint;
	}
	
	public String getFlow() {
		return flow;
	}

	public void setFlow(String flow) {
		this.flow = flow;
	}
	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	public List<String> getXPointArray() {
		return xPointArray;
	}

	public void setXPointArray(List<String> xPointArray) {
		this.xPointArray = xPointArray;
	}
	
	public List<String> getYPointArray() {
		return yPointArray;
	}

	public void setYPointArray(List<String> yPointArray) {
		this.yPointArray = yPointArray;
	}
	
	public Map<String, String> getProperties() {
		return properties;
	}

	public void setProperties(Map<String, String> properties) {
		this.properties = properties;
	}

}

