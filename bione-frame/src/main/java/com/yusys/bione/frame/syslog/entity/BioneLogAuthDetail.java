package com.yusys.bione.frame.syslog.entity;

import java.io.Serializable;
import javax.persistence.*;


/**
 * The persistent class for the BIONE_LOG_AUTH_DETAIL database table.
 * 
 */
@Entity
@Table(name="BIONE_LOG_AUTH_DETAIL")
public class BioneLogAuthDetail implements Serializable {
	private static final long serialVersionUID = 1L;

	@EmbeddedId
	private BioneLogAuthDetailPK id;

    public BioneLogAuthDetail() {
    }
    
    public BioneLogAuthDetail(String logId, String resDefNo, String resId){
    	BioneLogAuthDetailPK pk = new BioneLogAuthDetailPK();
    	pk.setLogId(logId);
    	pk.setResDefNo(resDefNo);
    	pk.setResId(resId);
    	this.id = pk;
    }

	public BioneLogAuthDetailPK getId() {
		return this.id;
	}

	public void setId(BioneLogAuthDetailPK id) {
		this.id = id;
	}
	
}