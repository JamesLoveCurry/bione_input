/**
 * 
 */
package com.yusys.bione.frame.search.index;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.lucene.document.Document;

import com.yusys.bione.frame.base.common.GlobalConstants4frame;

/**
 * <pre>
 * Title:对于索引和检索结果的包装类
 * Description:类似数据库的一条记录或文件系统中的一个文件
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
public class IndexDocument {
	
	private Map<String, IndexField> fields = new HashMap<String, IndexField>();
	
	/**
	 * 构造方法
	 * @param id				文档的唯一标识，存到索引文件中
	 */
	public IndexDocument(String id){
		if(StringUtils.isNotEmpty(id)){
			add(GlobalConstants4frame.INDEX_DOCUMENT_ID, id, true, false);
		}
	}
	
	/**
	 * 保存数据的方法
	 * @param key		数据的键名
	 * @param value		数据的内容
	 * @return			
	 */
	public IndexDocument store(String key, String value) {
		add(key, value, true, false);
		return this;
	}
	
	/**
	 * 返回文档中所有字段的名字，检索的时候不会返回searchContent，被检索的字符串
	 * @return			Iterable遍历器
	 */
	public Iterable<String> keys(){
		return fields.keySet();
	}
	
	/**
	 * 根据参数键返回文档中的值
	 * @param key		文档中的键
	 * @return			文档中键对应的值
	 */
	public String get(String key){
		IndexField filed = fields.get(key);
		return filed != null ? filed.getValue() : null;
	}
	
	/**
	 * 删除文档中指定键对应的字段
	 * @param key		文档中的键
	 * @return			
	 */
	public IndexDocument remove(String key) {
		fields.remove(key);
		return this;
	}
	
	/**
	 * 为文档添加字段
	 * @param key			字段的键
	 * @param value			字段的值
	 * @param isStore		是否在索引文件中保存该字段
	 * @param isAnalyzed	是否对该字段进行分词
	 * @return			
	 */
	public IndexDocument add(String key, String value, boolean isStore, boolean isAnalyzed) {
		fields.put(key, new IndexField(key, value, isStore, isAnalyzed));
		return this;
	}
	
	/**
	 * 返回文档的唯一标识
	 * @return				文档的唯一标识	
	 */
	public String getId(){
		return get(GlobalConstants4frame.INDEX_DOCUMENT_ID);
	}
	
	/**
	 * 把当前对象转换为lucene的Document对象
	 * @return				lucene的Document对象	
	 */
	protected Document convert2Document() {
		Document doc = new Document();
		for(String key : fields.keySet()){
			doc.add(fields.get(key).convert2Field());
		}
		return doc;
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("IndexDocument {\n");
		for(String key : fields.keySet()){
			sb.append(fields.get(key).toString());
			sb.append("\n");
		}
		sb.append("}");
		return sb.toString();
	}

}
