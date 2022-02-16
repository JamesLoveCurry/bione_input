/**
 * 
 */
package com.yusys.bione.frame.auth.web.vo;

import java.io.Serializable;

/**
 * <pre>
 * Title:程序的中文名称
 * Description: 程序功能的描述 
 * </pre>
 * @author caiqy  caiqy@yuchengtech.com
 * @version 1.00.00
 * <pre>
 * 修改记录
 *    修改后版本:     修改人：  修改日期:     修改内容: 
 * </pre>
 */
public class AuthTreeNode implements Serializable{

	private static final long serialVersionUID = -5480661081490332644L;

	private String id;
	private String upId;
	private String realId;
	private String text;
	private String nodeType;
	private String permissionId;
	private String icon;
	private String isParent;
	
	/**
	 * 
	 */
	public AuthTreeNode() {
		super();
	}
	
	/**
	 * @return 返回 id。
	 */
	public String getId() {
		return id;
	}
	/**
	 * @param id 设置 id。
	 */
	public void setId(String id) {
		this.id = id;
	}
	/**
	 * @return 返回 upId。
	 */
	public String getUpId() {
		return upId;
	}
	/**
	 * @param upId 设置 upId。
	 */
	public void setUpId(String upId) {
		this.upId = upId;
	}
	/**
	 * @return 返回 realId。
	 */
	public String getRealId() {
		return realId;
	}
	/**
	 * @param realId 设置 realId。
	 */
	public void setRealId(String realId) {
		this.realId = realId;
	}
	/**
	 * @return 返回 text。
	 */
	public String getText() {
		return text;
	}
	/**
	 * @param text 设置 text。
	 */
	public void setText(String text) {
		this.text = text;
	}
	/**
	 * @return 返回 nodeType。
	 */
	public String getNodeType() {
		return nodeType;
	}
	/**
	 * @param nodeType 设置 nodeType。
	 */
	public void setNodeType(String nodeType) {
		this.nodeType = nodeType;
	}
	/**
	 * @return 返回 permissionId。
	 */
	public String getPermissionId() {
		return permissionId;
	}
	/**
	 * @param permissionId 设置 permissionId。
	 */
	public void setPermissionId(String permissionId) {
		this.permissionId = permissionId;
	}
	/**
	 * @return 返回 icon。
	 */
	public String getIcon() {
		return icon;
	}
	/**
	 * @param icon 设置 icon。
	 */
	public void setIcon(String icon) {
		this.icon = icon;
	}
	/**
	 * @return 返回 isParent。
	 */
	public String getIsParent() {
		return isParent;
	}
	/**
	 * @param isParent 设置 isParent。
	 */
	public void setIsParent(String isParent) {
		this.isParent = isParent;
	}
	
	
	
}
