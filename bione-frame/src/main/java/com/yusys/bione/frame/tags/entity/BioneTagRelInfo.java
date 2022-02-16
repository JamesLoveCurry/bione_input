package com.yusys.bione.frame.tags.entity;

import java.io.Serializable;

import javax.persistence.*;

import java.sql.Timestamp;
import java.util.Date;


/**
 * The persistent class for the BIONE_TAG_REL_INFO database table.
 * 
 */
@Entity
@Table(name="BIONE_TAG_REL_INFO")
@NamedQuery(name="BioneTagRelInfo.findAll", query="SELECT b FROM BioneTagRelInfo b")
public class BioneTagRelInfo implements Serializable {
	private static final long serialVersionUID = 1L;

	@EmbeddedId
	private BioneTagRelInfoPK id;

	@Column(name="CREATE_TIME")
	private Timestamp createTime;

	public BioneTagRelInfo() {
	}

	public BioneTagRelInfoPK getId() {
		return this.id;
	}

	public void setId(BioneTagRelInfoPK id) {
		this.id = id;
	}

	public Date getCreateTime() {
		return this.createTime;
	}

	public void setCreateTime(Timestamp createTime) {
		this.createTime = createTime;
	}

}