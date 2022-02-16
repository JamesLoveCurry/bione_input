package    com.yusys.bione.plugin.engine.entity;
import java.io.Serializable;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;


/**
 * The persistent class for the RPT_ENGINE_IDX_REL database table.
 * 
 */
@Entity
@Table(name="RPT_ENGINE_IDX_REL")
public class RptEngineIdxRel implements Serializable {
	private static final long serialVersionUID = 1L;

	@EmbeddedId
	private RptEngineIdxRelPK id;

    public RptEngineIdxRel() {
    }

	public RptEngineIdxRelPK getId() {
		return this.id;
	}

	public void setId(RptEngineIdxRelPK id) {
		this.id = id;
	}
	
}