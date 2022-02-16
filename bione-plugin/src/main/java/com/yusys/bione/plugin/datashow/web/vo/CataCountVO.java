package com.yusys.bione.plugin.datashow.web.vo;

public class CataCountVO {
	private String indexCatalogNo;
	private long counts;
	private boolean isParent;

	public CataCountVO() {
		super();
	}

	public CataCountVO(String indexCatalogNo) {
		this.indexCatalogNo = indexCatalogNo;
	}

	public String getIndexCatalogNo() {
		return indexCatalogNo;
	}

	public void setIndexCatalogNo(String indexCatalogNo) {
		this.indexCatalogNo = indexCatalogNo;
	}

	public long getCounts() {
		return counts;
	}

	public void setCounts(long counts) {
		this.counts = counts;
		this.isParent = counts > 0;
	}

	public CataCountVO(long counts) {
		super();
		this.counts = counts;
	}

	public boolean isParent() {
		return isParent;
	}

	public void setParent(boolean isParent) {
		this.isParent = isParent;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((indexCatalogNo == null) ? 0 : indexCatalogNo.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		CataCountVO other = (CataCountVO) obj;
		if (indexCatalogNo == null) {
			if (other.indexCatalogNo != null)
				return false;
		} else if (!indexCatalogNo.equals(other.indexCatalogNo))
			return false;
		return true;
	}
}
