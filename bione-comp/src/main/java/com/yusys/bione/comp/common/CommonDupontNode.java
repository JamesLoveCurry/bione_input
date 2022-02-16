package com.yusys.bione.comp.common;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.google.common.collect.Maps;

/**
 * Title : 通用的杜邦图节点类
 * Description : 程序功能的描述
 * @author yangyf
 * @version 1.0
 */
@SuppressWarnings("serial")
public class CommonDupontNode implements java.io.Serializable {

	/**
	 * 
	 */

	private String id ; // 节点id
	private String upId ; // 上级节点Id
	private String text ; // 节点名称
	private String value ;//数据
	private String percent ;//百分比
	private String unit ;//单位
	private String icon ;//图标
	private String color;
	private boolean isExpand = true; // 当前节点是否展开
	private Object data ; // 节点对应的数据对象
		
	/* 子节点集合 */
	private List<CommonDupontNode> children = null;
	
	/* 参数 */
	private Map<String, String> params = Maps.newHashMap();
	
	/* 默认父节点ID */
	public static String ROOT_ID = "0";

	/**
	 * 添加子节点
	 * 
	 * @param childNode
	 */
	public void addChildNode(CommonDupontNode childNode) {

		if (this.children == null)
			this.children = new ArrayList<CommonDupontNode>();

		this.children.add(childNode);
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getUpId() {
		return upId;
	}

	public void setUpId(String upId) {
		this.upId = upId;
	}

	public Object getData() {
		return data;
	}

	public void setData(Object data) {
		this.data = data;
	}

	public List<CommonDupontNode> getChildren() {
		return children;
	}

	public void setChildren(List<CommonDupontNode> children) {
		this.children = children;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public Map<String, String> getParams() {
		return params;
	}

	public void setParams(Map<String, String> params) {
		this.params = params;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public String getPercent() {
		return percent;
	}

	public void setPercent(String percent) {
		this.percent = percent;
	}

	public boolean getIsExpand() {
		return isExpand;
	}

	public void setIsExpand(boolean isExpand) {
		this.isExpand = isExpand;
	}

	public String getUnit() {
		return unit;
	}

	public void setUnit(String unit) {
		this.unit = unit;
	}

	public String getIcon() {
		return icon;
	}

	public void setIcon(String icon) {
		this.icon = icon;
	}

	public String getColor() {
		return color;
	}

	public void setColor(String color) {
		this.color = color;
	}

	
}
