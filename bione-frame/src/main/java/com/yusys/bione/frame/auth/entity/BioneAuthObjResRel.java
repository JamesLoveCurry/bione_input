package com.yusys.bione.frame.auth.entity;

import java.io.Serializable;
import javax.persistence.*;


/**
 * The persistent class for the BIONE_AUTH_OBJ_RES_REL database table.
 * 
 */
@Entity
@Table(name="BIONE_AUTH_OBJ_RES_REL")
public class BioneAuthObjResRel implements Serializable {
	private static final long serialVersionUID = 1L;

	@EmbeddedId
	private BioneAuthObjResRelPK id;
	
	@Column(name="RES_TYPE") 
    private String resType;

    public BioneAuthObjResRel() {
    }

	public BioneAuthObjResRelPK getId() {
		return this.id;
	}

	public void setId(BioneAuthObjResRelPK id) {
		this.id = id;
	}

	public String getResType() {
        return resType;
    }

    public void setResType(String resType) {
        this.resType = resType;
    }
	
    @Override
    public boolean equals(Object other) {
    	if (this == other) {
			return true;
		}
		if (!(other instanceof BioneAuthObjResRel)) {
			return false;
		}
		BioneAuthObjResRel castOther = (BioneAuthObjResRel)other;
		
		boolean isEquals = this.id.getLogicSysNo().equals(castOther.id.getLogicSysNo())
				&& this.id.getObjDefNo().equals(castOther.id.getObjDefNo())
				&& this.id.getResDefNo().equals(castOther.id.getResDefNo())
				&& this.id.getPermissionType().equals(castOther.id.getPermissionType())
				&& this.id.getObjId().equals(castOther.id.getObjId())
				&& this.id.getResId().equals(castOther.id.getResId())
				&& this.id.getPermissionId().equals(castOther.id.getPermissionId());
		return isEquals;
    }
}