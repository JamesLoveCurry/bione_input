package com.yusys.biapp.input.template.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;


/**
 * The persistent class for the RPT_INPUT_REWRITE_TEMPLE_INFO database table.
 * 
 */
@Entity
@Table(name="RPT_INPUT_REWRITE_TEMPLE_INFO")
public class RptInputRewriteTempleInfo implements Serializable {
	private static final long serialVersionUID = 1L;

	@EmbeddedId
	private RptInputRewriteTempleInfoPK id;

	@Column(name="TABLE_NAME")
	private String tableName;

	@Column(name="UPDATE_TYPE")
	private String updateType;
	
	@Column(name="AUTO_REWRITE")
	private String autoRewrite;
	
	@Transient
	private String isStart;

    public RptInputRewriteTempleInfo() {
    }

	public String getAutoRewrite() {
        return autoRewrite;
    }

    public void setAutoRewrite(String autoRewrite) {
        this.autoRewrite = autoRewrite;
    }

    public RptInputRewriteTempleInfoPK getId() {
		return this.id;
	}

	public void setId(RptInputRewriteTempleInfoPK id) {
		this.id = id;
	}
	
	public String getTableName() {
		return this.tableName;
	}

	public void setTableName(String tableName) {
		this.tableName = tableName;
	}

	public String getUpdateType() {
		return updateType;
	}

	public void setUpdateType(String updateType) {
		this.updateType = updateType;
	}

	public String getIsStart() {
		return isStart;
	}

	public void setIsStart(String isStart) {
		this.isStart = isStart;
	}

}