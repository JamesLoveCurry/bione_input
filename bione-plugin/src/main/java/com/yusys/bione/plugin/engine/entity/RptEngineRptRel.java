package    com.yusys.bione.plugin.engine.entity;
import java.io.Serializable;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;


/**
 * The persistent class for the RPT_ENGINE_RPT_REL database table.
 * 
 */
@Entity
@Table(name="RPT_ENGINE_RPT_REL")
public class RptEngineRptRel implements Serializable {
	private static final long serialVersionUID = 1L;

	@EmbeddedId
	private RptEngineRptRelPK id;

    public RptEngineRptRel() {
    }

	public RptEngineRptRelPK getId() {
		return this.id;
	}

	public void setId(RptEngineRptRelPK id) {
		this.id = id;
	}
	
}