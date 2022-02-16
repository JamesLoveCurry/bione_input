package    com.yusys.bione.plugin.engine.entity;
import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;

/**
 * The primary key class for the RPT_ENGINE_RPT_REL database table.
 * 
 */
@Embeddable
public class RptEngineRptRelPK implements Serializable {
	//default serial version id, required for serializable classes.
	private static final long serialVersionUID = 1L;

	@Column(name="BATCH_NUM")
	private long batchNum;

	@Column(name="DATA_DATE")
	private String dataDate;

	@Column(name="RPT_ID")
	private String rptId;

    public RptEngineRptRelPK() {
    }
	public long getBatchNum() {
		return this.batchNum;
	}
	public void setBatchNum(long batchNum) {
		this.batchNum = batchNum;
	}
	public String getDataDate() {
		return this.dataDate;
	}
	public void setDataDate(String dataDate) {
		this.dataDate = dataDate;
	}
	public String getRptId() {
		return this.rptId;
	}
	public void setRptId(String rptId) {
		this.rptId = rptId;
	}

	public boolean equals(Object other) {
		if (this == other) {
			return true;
		}
		if (!(other instanceof RptEngineRptRelPK)) {
			return false;
		}
		RptEngineRptRelPK castOther = (RptEngineRptRelPK)other;
		return 
			(this.batchNum == castOther.batchNum)
			&& this.dataDate.equals(castOther.dataDate)
			&& this.rptId.equals(castOther.rptId);

    }
    
	public int hashCode() {
		final int prime = 31;
		int hash = 17;
		hash = hash * prime + ((int) (this.batchNum ^ (this.batchNum >>> 32)));
		hash = hash * prime + this.dataDate.hashCode();
		hash = hash * prime + this.rptId.hashCode();
		
		return hash;
    }
}