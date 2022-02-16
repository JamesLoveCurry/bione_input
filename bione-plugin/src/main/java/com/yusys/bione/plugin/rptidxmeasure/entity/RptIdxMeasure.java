package com.yusys.bione.plugin.rptidxmeasure.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;

/**
 * 实体类
 */


@Entity
@Table(name="RPT_IDX_MEASURE_INFO")
public class RptIdxMeasure implements Serializable {
    private static final long serialVersionUID=1L;

    @Id
    @Column(name="MEASURE_NO",unique = true,nullable = false)
    private String measureNo;

    @Column(name="MEASURE_TYPE")
    private String measureType;

    @Column(name="MEASURE_NM")
    private String measureNm;

    @Column(name="CALC_FORMULA")
    private String calcFormula;

    @Column
    private String remark;

    public String getMeasureNo() {
        return measureNo;
    }

    public void setMeasureNo(String measureNo) {
        this.measureNo = measureNo;
    }

    public String getMeasureType() {
        return measureType;
    }

    public void setMeasureType(String measureType) {
        this.measureType = measureType;
    }

    public String getMeasureNm() {
        return measureNm;
    }

    public void setMeasureNm(String measureNm) {
        this.measureNm = measureNm;
    }

    public String getCalcFormula() {
        return calcFormula;
    }

    public void setCalcFormula(String calcFormula) {
        this.calcFormula = calcFormula;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }
}
