package com.yusys.bione.plugin.valid.web.vo;

import java.io.Serializable;

/**
 * <pre>
 * Title: 明细报表校验 表达式处理
 * Description: 表达式中 批量处理 [1-10]F>=[1-10]O  [1-9]O>=[2-10]O
 * </pre>
 * @author lizy6 
 * @version 1.00.00
 */
public class ExpressionBatchVO implements Serializable {

	private static final long serialVersionUID = 990238185110966735L;

	/**
	 * 批量处理[1-10]F+G 公式  F+G
	 */
	private String batchFormula;
	
	/**
	 * 批量处理[1-10]F+G 索引字符 1-10
	 */
	private String batchIndexStr;
	
	/**
	 * 批量处理[1-10]F+G 开始索引 1
	 */
	private int batchBeginIndex;
	
	/**
	 * 批量处理[1-10]F+G 结束索引 10
	 */
	private int batchEndIndex;
	
	/**
	 * 批量处理[1-10]F+G 批量处理次数 10 次
	 */
	private int batchCount;
	
	/**
	 * 批量处理[1-n]F+G 是否包含n 包含则代表不固定行
	 */
	private boolean isIncludeN;
	
	/**
	 * 是否为批量 处理
	 */
	private boolean isBatch;
	
	public ExpressionBatchVO() {}
	
	public ExpressionBatchVO(String batchFormula, String batchIndexStr, int batchBeginIndex, int batchEndIndex,
			int batchCount, boolean isIncludeN, boolean isBatch) {
		this.batchFormula = batchFormula;
		this.batchIndexStr = batchIndexStr;
		this.batchBeginIndex = batchBeginIndex;
		this.batchEndIndex = batchEndIndex;
		this.batchCount = batchCount;
		this.isIncludeN = isIncludeN;
		this.isBatch = isBatch;
	}

	public String getBatchFormula() {
		return batchFormula;
	}

	public void setBatchFormula(String batchFormula) {
		this.batchFormula = batchFormula;
	}

	public int getBatchBeginIndex() {
		return batchBeginIndex;
	}

	public void setBatchBeginIndex(int batchBeginIndex) {
		this.batchBeginIndex = batchBeginIndex;
	}

	public int getBatchEndIndex() {
		return batchEndIndex;
	}

	public void setBatchEndIndex(int batchEndIndex) {
		this.batchEndIndex = batchEndIndex;
	}

	public boolean isIncludeN() {
		return isIncludeN;
	}

	public void setIncludeN(boolean isIncludeN) {
		this.isIncludeN = isIncludeN;
	}

	public String getBatchIndexStr() {
		return batchIndexStr;
	}

	public void setBatchIndexStr(String batchIndexStr) {
		this.batchIndexStr = batchIndexStr;
	}

	public int getBatchCount() {
		return batchCount;
	}

	public void setBatchCount(int batchCount) {
		this.batchCount = batchCount;
	}
	
	public boolean isBatch() {
		return isBatch;
	}

	public void setBatch(boolean isBatch) {
		this.isBatch = isBatch;
	}

	@Override
	public String toString() {
		String toStr = 	this.batchFormula + " | " +
						this.batchIndexStr + " | " +
						this.batchBeginIndex + " | " +
						this.batchEndIndex + " | " +
						this.batchCount + " | " +
						this.isIncludeN;
		return toStr;
	}
}
