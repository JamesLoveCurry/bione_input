package com.yusys.bione.comp.bigdata.utils;

import com.yusys.bione.comp.utils.StrDesUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sun.misc.BASE64Decoder;


/**
 * @项目名称： bash64解密
 * @类名称： BashUtil
 * @类描述:
 * @功能描述:
 * @创建人: miaokx@yusys.com.cn
 * @创建时间: 2021年03月04日 15:23
 * @修改备注:
 * @修改记录: 修改时间  修改人员  修改原因
 * ---------------------------------------------------------
 * @Version 1.0.0
 * @Copyright (c) 宇信科技-版权所有
 */
public class BashUtil {
    private static Logger log = LoggerFactory.getLogger(BashUtil.class);
    /***
     * @方法描述: bash64密码解密
     * @创建人: miaokx@yusys.com.cn
     * @创建时间: 2021/3/4 15:22
     * @Param: cipher
     * @return: java.lang.String
     */
    public static String getPassword(String cipher){
        log.debug("BioneDbcpBasicDataSource start setPassword！...."+cipher);
        try {
            log.debug("BioneDbcpBasicDataSource ConfigTools decrypt password！....start");
            BASE64Decoder decoder = new BASE64Decoder();
            byte[] bytes = decoder.decodeBuffer(cipher);
            log.debug("BioneDbcpBasicDataSource ConfigTools decrypt password！....end");
            cipher = new String(bytes);
            log.debug("BioneDbcpBasicDataSource end setPassword！...."+cipher);
        } catch (Exception e) {
            e.printStackTrace();
            log.debug("BioneDbcpBasicDataSource decrypt Password failure！...."+cipher);
        }
        return cipher;
    }


}
