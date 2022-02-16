/**
 * 
 */
package com.yusys.biapp.input.template.utils.object;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * <pre>
 * Title:实现序列化接口，可将导出对象序列化到文件
 * Description:
 * </pre>
 * @author guojiangping
 * @version 1.00.00
 * 
 * <pre>
 * 修改记录
 *    修改后版本:     修改人：  修改日期:     修改内容:
 * </pre>
 */
public class BOList extends ArrayList<Object> implements Serializable {

	private static final long serialVersionUID = 1175845558331676390L;

	private String className;

	public BOList(Class<?> clazz) {
		this.className = clazz.getName();
	}

	public BOList(String className){
		this.className=className;
	}
	
	public BOList() {
	}
	
	public String getClassName() {
		return className;
	}

	public void setClassName(String className) {
		this.className = className;
	}
}
