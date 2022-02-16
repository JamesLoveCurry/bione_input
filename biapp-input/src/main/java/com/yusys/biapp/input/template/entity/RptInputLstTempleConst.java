package com.yusys.biapp.input.template.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;


/**
 * The persistent class for the RPT_INPUT_LST_TEMPLE_CONST database table.
 * 
 */
@Entity
@Table(name="RPT_INPUT_LST_TEMPLE_CONST")
public class RptInputLstTempleConst implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name="KEY_ID", unique=true, nullable=false, length=32)
	private String keyId;

	@Column(name="KEY_COLUMN", length=500)
	private String keyColumn;

	@Column(name="KEY_NAME", length=100)
	private String keyName;

	@Column(name="KEY_TYPE", length=10)
	private String keyType;

	@Column(name="TEMPLE_ID", nullable=false, length=32)
	private String templeId;

    public RptInputLstTempleConst() {
    }

	public String getKeyId() {
		return this.keyId;
	}

	public void setKeyId(String keyId) {
		this.keyId = keyId;
	}

	public String getKeyColumn() {
		return this.keyColumn;
	}

	public void setKeyColumn(String keyColumn) {
		this.keyColumn = keyColumn;
	}

	public String getKeyName() {
		return this.keyName;
	}

	public void setKeyName(String keyName) {
		this.keyName = keyName;
	}

	public String getKeyType() {
		return this.keyType;
	}

	public void setKeyType(String keyType) {
		this.keyType = keyType;
	}

	public String getTempleId() {
		return this.templeId;
	}

	public void setTempleId(String templeId) {
		this.templeId = templeId;
	}

}