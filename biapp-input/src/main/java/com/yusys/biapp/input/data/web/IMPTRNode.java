package com.yusys.biapp.input.data.web;

import java.math.BigDecimal;

public interface IMPTRNode {
	public String getNodeId();
	public String getUpNodeId();
	public BigDecimal getLft();
	public BigDecimal getRgt();
	
	public void setLft(BigDecimal left);
	public void setRgt(BigDecimal right);
}
