package com.yusys.biapp.input.task.entity;

import java.io.Serializable;
import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;


/**
 * The persistent class for the rpt_fltsk_ins_generate_sts database table.
 * 
 */
@Entity
@Table(name="rpt_tsk_ins_generate_sts")
public class RptTskInsGenerateSts implements Serializable {
	private static final long serialVersionUID = 1L;

	@EmbeddedId
	private RptTskInsGenerateStsPK id;

	@Column(name="PRE_STS")
	private String preSts;

	@Column(name="GENERATE_STS")
	private String generateSts;

	@Column(name="GENERATE_TIME")
	private Timestamp generateTime;

	
    public RptTskInsGenerateSts() {
    }


	public RptTskInsGenerateStsPK getId() {
		return id;
	}


	public void setId(RptTskInsGenerateStsPK id) {
		this.id = id;
	}


	public String getPreSts() {
		return preSts;
	}


	public void setPreSts(String preSts) {
		this.preSts = preSts;
	}


	public String getGenerateSts() {
		return generateSts;
	}


	public void setGenerateSts(String generateSts) {
		this.generateSts = generateSts;
	}


	public Timestamp getGenerateTime() {
		return generateTime;
	}


	public void setGenerateTime(Timestamp generateTime) {
		this.generateTime = generateTime;
	}

	
}