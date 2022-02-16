/**
 * 
 */
package com.yusys.bione.frame.search;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.yusys.bione.frame.base.common.GlobalConstants4frame;


/**
 * <pre>
 * Title:不同系统模块的检索配置和接口实现类
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
public enum SearchModule {
	//DEMO案例，实际开发时候请删除
//	DEMO("案例", "demo");
	REPORT("报表模块", GlobalConstants4frame.RPT_LABEL_OBJ_NO);
	
	private static final Logger log = LoggerFactory.getLogger(SearchModule.class);
	
	private final String moduleId;
	
	private final String moduleName;
	
	private volatile ILuceneWriterAndSearcherBuilder builder;
	
	private static final Map<String, SearchModule> stringToEnum = new HashMap<String, SearchModule>();
	
	/**
	 * 加载所有的枚举对象
	 */
	static {
		for(SearchModule module : values()){
			stringToEnum.put(module.toString(), module);
		}
	}
	
	/**
	 * 构造方法，系统会根据参数path，在指定的index文件夹下生成模块文件夹
	 * @param moduleName	模块名称
	 * @param path			模块的相对路径		
	 */
	SearchModule(String moduleName, String moduleId){
		this.moduleName = moduleName;
		this.moduleId = moduleId;
	}
	
	public String getModuleName(){
		return moduleName;
	}
	
	public String getModuleId(){
		return moduleId;
	}
	
	/**
	 * 根据模块路径，返回对应的ILuceneWriterAndSearcherBuilder生成器实现
	 * @return 				ILuceneWriterAndSearcherBuilder生成器实现
	 */
	public ILuceneWriterAndSearcherBuilder getWriterAndSearcherBuilder(){
		log.debug("返回模块名为（" + moduleName + "），相对路径为（" + moduleId + "）的对象生成器。");
		if(builder == null){
			synchronized (this) {
				if(builder == null){
					builder = new LuceneWriterAndSearcherBuilder(moduleId);
				}
			}
		}
		return builder;
	}
	
//	/**
//	 * 系统启动的时候会加载该方法，与数据源同步并重新初始化索引文件
//	 */
//	public void load(){
//		try {
//			CacheUtil.relaodCache(getModuleId());
//			LuceneIndexManager indexManager = new LuceneIndexManager(getWriterAndSearcherBuilder());
//			indexManager.create(IndexDocumentUtil.createIndexDocument(getEntities(), CacheUtil.getConfig(getModuleId())));
//		} catch (SearchException e) {
//			e.printStackTrace();
//		}
//	}
//	
//	/**
//	 * 返回该模块要创建索引文件的对象集合
//	 */
//	public abstract Iterable<?> getEntities();
	
	/**
	 * 根据SearchModule的字符串表示返回对应的枚举对象，找不到则返回null
	 * @param symbol	字符串参数
	 * @return			SearchModule的实例	
	 */
	public static SearchModule formString(String symbol){
		return stringToEnum.get(symbol);
	}
	
	@Override
	public String toString() {
		return this.getModuleId();
	}
	
}
