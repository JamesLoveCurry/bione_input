package com.yusys.bione.comp.common;

import com.yusys.bione.comp.utils.RSAEncryptUtils;
import org.apache.commons.dbcp.BasicDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @Author: WM
 * @Date: 2021\6\4 0004 16:54
 * @Description: 连接数据源时，使用RSA方式进行加密/解密
 */
public class BioneRSADataSource extends BasicDataSource {

    private static Logger log = LoggerFactory.getLogger(BioneDbcpBasicDataSource.class);

    @Override
    public void setPassword(String password) {
        log.debug("BioneRSADataSource start setPassword！...."+password);
        try {
            if(password.length() > 30){
                log.debug("BioneRSADataSource ConfigTools decrypt password！....start");
                password = RSAEncryptUtils.dataSourceDecrypt(password);
                log.debug("BioneRSADataSource ConfigTools decrypt password！....end");
            }

        } catch (Exception e) {
            e.printStackTrace();
            log.debug("BioneDbcpBasicDataSource decrypt Password failure！...."+password);
        }
        log.debug("BioneDbcpBasicDataSource end setPassword！...."+password);
        this.password = password;

    }
}
