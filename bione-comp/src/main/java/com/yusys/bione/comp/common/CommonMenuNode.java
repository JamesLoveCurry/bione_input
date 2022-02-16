package com.yusys.bione.comp.common;

import java.util.ArrayList;
import java.util.List;

public class CommonMenuNode implements java.io.Serializable {
	private static final long serialVersionUID = 706621550112134159L;
	
	private String id ; // 节点id
	private String upId ; // 上级节点Id
	private String text ; // 节点名称
	private String title ; // 鼠标经过显示
	private String sort ; // 菜单展示顺序
	private String url ; // 菜单连接url
	private String type ; // 菜单类型 横向或竖向
	private Object data ; // 节点对应的数据对象
	private List<CommonMenuNode> children = null;
	
	/**
	 * 添加子节点
	 * 
	 * @param childNode
	 */
	public void addChildNode(CommonMenuNode childNode) {

		if (this.children == null)
			this.children = new ArrayList<CommonMenuNode>();

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

	public List<CommonMenuNode> getChildren() {
		return children;
	}

	public void setChildren(List<CommonMenuNode> children) {
		this.children = children;
	}
	
	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}
	
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getSort() {
		return sort;
	}
	public void setSort(String sort) {
		this.sort = sort;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
}
