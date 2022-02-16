/**
 * 
 */
package com.yusys.bione.frame.message.messager;

import com.yusys.bione.frame.message.entity.BioneMsgAttachInfo;
import com.yusys.bione.frame.message.entity.BioneMsgLog;

/**
 * @author tanxu
 * @email tanxu@yuchengtech.com
 * @date 2013-12-12
 */
public class EmailMessager implements IMessager<BioneMsgLog, BioneMsgAttachInfo> {

	@Override
	public boolean send(BioneMsgLog msg) throws BioneMessageException {
		return false;
	}

	@Override
	public boolean send(BioneMsgLog useAlarmMsg, Iterable<BioneMsgAttachInfo> attaches) throws BioneMessageException {
		return true;
	}
}
