package com.yusys.bione.comp.repository.mybatis;

import java.util.ArrayList;
import java.util.List;

public class PageMyBatis<E> extends ArrayList<E> {

	private static final long serialVersionUID = 1L;

	private long totalCount;

	public List<E> getResult() {
		return this;
	}

	public long getTotalCount() {
		return totalCount;
	}

	public void setTotalCount(long totalCount) {
		this.totalCount = totalCount;
	}
}
