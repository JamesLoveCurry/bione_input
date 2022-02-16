package com.yusys.bione.comp.common;

import org.apache.commons.dbcp.BasicDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.yusys.bione.comp.utils.StrDesUtils;


/**
 * 
 * <pre>
 * Title: 使用Dbcp 内部数据源连接池   spring 加载 数据库连接信息前      针对database.properties 中 jdbc.password 进行解密。
 * Description: jdbc.password 加密/解密  
 *   			注意:database.properties 中 jdbc.password 需要进行加密
 * </pre>
 * 
 * @author lcy  lizy6@yusys.com.cn
 * @version 1.00.00
 * @since	2018-02-27
 *
 *          <pre>
 * 修改记录
 *    修改后版本:     修改人：  修改日期:     修改内容:
 * </pre>
 */
public class BioneDbcpBasicDataSource extends BasicDataSource{

	private static Logger log = LoggerFactory.getLogger(BioneDbcpBasicDataSource.class);	

	public void setPassword(String password) {
		log.debug("BioneDbcpBasicDataSource start setPassword！...."+password);
		try {
			if(password.length() > 30){
				log.debug("BioneDbcpBasicDataSource ConfigTools decrypt password！....start");
				//druid-1.0.31.jar 解密方法
				//password = ConfigTools.decrypt(password);
				//平台-解密方法
				password = StrDesUtils.appStrDecryptByDefaultEncoding(password, 82);
				log.debug("BioneDbcpBasicDataSource ConfigTools decrypt password！....end");
			}
				
		} catch (Exception e) {
			e.printStackTrace();
			log.debug("BioneDbcpBasicDataSource decrypt Password failure！...."+password);
		}
		log.debug("BioneDbcpBasicDataSource end setPassword！...."+password);
        this.password = password;
        
    }
}
