/**
 * 
 */
package com.yusys.bione.frame.search.index;

import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;

/**
 * <pre>
 * Title:
 * Description:
 * </pre>
 * 
 * @author tanxu@yuchengtech.com
 * @version 
 * 
 * <pre>
 * 修改记录
 *    修改后版本:	修改人：		修改日期:	修改内容:
 * </pre>
 */
public class IndexField {

	private String name;
	
	private String value;
	
	private boolean isStore;
	
	private boolean isAnalyzed;
	
	/**
	 * 构造方法
	 * @param name				字段的名称，在文档中唯一
	 * @param value				字段的值
	 * @param isStore			是否在索引文件中保存该字段
	 * @param isAnalyzed		是否对该字段进行分词
	 */
	public IndexField(String name, String value, boolean isStore, boolean isAnalyzed){
		this.name = name;
		this.value = value;
		this.isStore = isStore;
		this.isAnalyzed = isAnalyzed;
	}
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public boolean isStore() {
		return isStore;
	}

	public void setStore(boolean isStore) {
		this.isStore = isStore;
	}

	public boolean isAnalyzed() {
		return isAnalyzed;
	}

	public void setAnalyzed(boolean isAnalyzed) {
		this.isAnalyzed = isAnalyzed;
	}

	protected Field convert2Field() {
		return isAnalyzed ? new TextField(name, value, isStore ? Field.Store.YES : Field.Store.NO) : new StringField(name, value, isStore ? Field.Store.YES : Field.Store.NO);
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("Field ( name : ").append(name);
		sb.append(", value : ").append(value);
		sb.append(", isStore : ").append(isStore);
		sb.append(", isAnalyzed : ").append(isAnalyzed).append(" )");
		return sb.toString();
	}
}
