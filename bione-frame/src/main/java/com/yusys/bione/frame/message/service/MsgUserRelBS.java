package com.yusys.bione.frame.message.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.yusys.bione.comp.service.BaseBS;
import com.yusys.bione.frame.message.entity.BioneMsgUserRel;

/**
 * <pre>
 * Title: 消息模块-附件服务
 * Description: 消息模块-附件服务
 * </pre>
 * 
 * @author fangjuan@yuchengtech.com
 * @version 1.00.00
 * 
 *          <pre>
 * 修改记录
 *    修改后版本:     修改人：  修改日期:     修改内容:
 * </pre>
 */
@Service
@Transactional(readOnly = true)
public class MsgUserRelBS extends BaseBS<BioneMsgUserRel> {


}
