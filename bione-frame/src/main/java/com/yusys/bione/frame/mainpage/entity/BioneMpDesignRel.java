package com.yusys.bione.frame.mainpage.entity;

import java.io.Serializable;
import javax.persistence.*;


/**
 * The persistent class for the BIONE_MP_DESIGN_REL database table.
 * 
 */
@Entity
@Table(name="BIONE_MP_DESIGN_REL")
@NamedQuery(name="BioneMpDesignRel.findAll", query="SELECT b FROM BioneMpDesignRel b")
public class BioneMpDesignRel  implements Serializable{
	private static final long serialVersionUID = 1L;

	@EmbeddedId
	private BioneMpDesignRelPK id;

	public BioneMpDesignRel() {
	}

	public BioneMpDesignRelPK getId() {
		return this.id;
	}

	public void setId(BioneMpDesignRelPK id) {
		this.id = id;
	}

}