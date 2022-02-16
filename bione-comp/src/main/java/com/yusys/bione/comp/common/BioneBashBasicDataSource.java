package com.yusys.bione.comp.common;

import com.yusys.bione.comp.bigdata.utils.BashUtil;
import org.apache.commons.dbcp.BasicDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * @项目名称： 数据库密码加密bash64方式
 * @类名称： BioneBashBasicDataSource
 * @类描述:
 * @功能描述:
 * @创建人: miaokx@yusys.com.cn
 * @创建时间: 2021年03月04日 15:15
 * @修改备注:
 * @修改记录: 修改时间  修改人员  修改原因
 * ---------------------------------------------------------
 * @Version 1.0.0
 * @Copyright (c) 宇信科技-版权所有
 */
public class BioneBashBasicDataSource extends BasicDataSource {

    private static Logger log = LoggerFactory.getLogger(BioneBashBasicDataSource.class);
    /**
     * @方法描述: 密码解密
     * @创建人: miaokx@yusys.com.cn
     * @创建时间: 2021/3/4 15:17
     * @Param: password
     * @return: void
     */
    public void setPassword(String password) {
        log.debug("BioneDbcpBasicDataSource start setPassword！...."+password);
        try {
            log.debug("BioneDbcpBasicDataSource ConfigTools decrypt password！....start");
            password = BashUtil.getPassword(password);
            log.debug("BioneDbcpBasicDataSource ConfigTools decrypt password！....end");
        } catch (Exception e) {
            e.printStackTrace();
            log.debug("BioneDbcpBasicDataSource decrypt Password failure！...."+password);
        }
        log.debug("BioneDbcpBasicDataSource end setPassword！...."+password);
        this.password = password;
    }

}
