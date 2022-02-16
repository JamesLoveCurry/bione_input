package com.yusys.bione.plugin.valid.check;


/**
 * <pre>
 * Title:自定义异常类
 * Description: 程序功能的描述
 * </pre>
 * @author mengzx  
 * @version 1.00.00
 * <pre>
 * 修改记录
 *    修改后版：     修改人：  修改日期:     修改内容: 
 * </pre>
 */
public class BizException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8145349205567805184L;
	private String indexNo;//指标编码
	
	public BizException(String info){
		
		super(info);
	}

	/**
	 * @return 返回 indexNo
	 */
	public String getIndexNo() {
		return indexNo;
	}

	/**
	 * @param indexNo 设置 indexNo
	 */
	public void setIndexNo(String indexNo) {
		this.indexNo = indexNo;
	}
}
