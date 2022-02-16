package com.yusys.bione.frame.tags.entity;

import java.io.Serializable;
import javax.persistence.*;


/**
 * The persistent class for the BIONE_TAG_INFO database table.
 * 
 */
@Entity
@Table(name="BIONE_TAG_INFO")
@NamedQuery(name="BioneTagInfo.findAll", query="SELECT b FROM BioneTagInfo b")
public class BioneTagInfo implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name="TAG_ID")
	private String tagId;

	private String remark;

	@Column(name="TAG_NAME")
	private String tagName;

	public BioneTagInfo() {
	}

	public String getTagId() {
		return this.tagId;
	}

	public void setTagId(String tagId) {
		this.tagId = tagId;
	}

	public String getRemark() {
		return this.remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public String getTagName() {
		return this.tagName;
	}

	public void setTagName(String tagName) {
		this.tagName = tagName;
	}

}