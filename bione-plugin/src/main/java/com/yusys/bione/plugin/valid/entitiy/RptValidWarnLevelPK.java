package com.yusys.bione.plugin.valid.entitiy;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;

/**
 * The primary key class for the RPT_VALID_WARN_LEVEL database table.
 * 
 */
@Embeddable
public class RptValidWarnLevelPK implements Serializable {
	//default serial version id, required for serializable classes.
	private static final long serialVersionUID = 1L;

	@Column(name="LEVEL_NUM")
	private String levelNum;

	@Column(name="CHECK_ID")
	private String checkId;

    public RptValidWarnLevelPK() {
    }
	public String getLevelNum() {
		return this.levelNum;
	}
	public void setLevelNum(String levelNum) {
		this.levelNum = levelNum;
	}

	public String getCheckId() {
		return checkId;
	}
	public void setCheckId(String checkId) {
		this.checkId = checkId;
	}
	public boolean equals(Object other) {
		if (this == other) {
			return true;
		}
		if (!(other instanceof RptValidWarnLevelPK)) {
			return false;
		}
		RptValidWarnLevelPK castOther = (RptValidWarnLevelPK)other;
		return 
			this.levelNum.equals(castOther.levelNum)
			&& this.checkId.equals(castOther.checkId);

    }
    
	public int hashCode() {
		final int prime = 31;
		int hash = 17;
		hash = hash * prime + this.levelNum.hashCode();
		hash = hash * prime + this.checkId.hashCode();
		
		return hash;
    }
}