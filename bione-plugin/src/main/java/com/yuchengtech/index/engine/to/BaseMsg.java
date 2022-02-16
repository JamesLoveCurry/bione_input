package com.yuchengtech.index.engine.to;

import java.io.Serializable;

public abstract class BaseMsg implements Serializable {

	private static final long serialVersionUID = 1L;
	private int type;

	public BaseMsg() {
	}

	public int getType() {
		return type;
	}

	public void setType(int _type) {
		this.type = _type;
	}

}

