package    com.yusys.bione.plugin.engine.entity;  
import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;


/**
 * The persistent class for the RPT_ENGINE_BATCH database table.
 * 
 */
@Entity
@Table(name="RPT_ENGINE_BATCH")
public class RptEngineBatch implements Serializable {
	private static final long serialVersionUID = 1L;

	@EmbeddedId
	private RptEngineBatchPK id;

	@Column(name="BATCH_TYPE")
	private String batchType;

	@Column(name="END_TIME")
	private Timestamp endTime;

	@Column(name="ERROR_LOG")
	private String errorLog;

	@Column(name="EXE_STS")
	private String exeSts;

	@Column(name="INDEX_NUM")
	private BigDecimal indexNum;

	@Column(name="RPT_NUM")
	private BigDecimal rptNum;

	@Column(name="START_TIME")
	private Timestamp startTime;

    public RptEngineBatch() {
    }

	public RptEngineBatchPK getId() {
		return this.id;
	}

	public void setId(RptEngineBatchPK id) {
		this.id = id;
	}
	
	public String getBatchType() {
		return this.batchType;
	}

	public void setBatchType(String batchType) {
		this.batchType = batchType;
	}

	public Timestamp getEndTime() {
		return this.endTime;
	}

	public void setEndTime(Timestamp endTime) {
		this.endTime = endTime;
	}

	public String getErrorLog() {
		return this.errorLog;
	}

	public void setErrorLog(String errorLog) {
		this.errorLog = errorLog;
	}

	public String getExeSts() {
		return this.exeSts;
	}

	public void setExeSts(String exeSts) {
		this.exeSts = exeSts;
	}

	public BigDecimal getIndexNum() {
		return this.indexNum;
	}

	public void setIndexNum(BigDecimal indexNum) {
		this.indexNum = indexNum;
	}

	public BigDecimal getRptNum() {
		return this.rptNum;
	}

	public void setRptNum(BigDecimal rptNum) {
		this.rptNum = rptNum;
	}

	public Timestamp getStartTime() {
		return this.startTime;
	}

	public void setStartTime(Timestamp startTime) {
		this.startTime = startTime;
	}

}