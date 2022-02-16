/**
 * 
 */
package com.yusys.bione.frame.message.messager;

import java.util.Map;

import com.google.common.collect.Maps;
import com.yusys.bione.comp.utils.SpringContextHolder;
import com.yusys.bione.frame.message.entity.BioneMsgAttachInfo;
import com.yusys.bione.frame.message.entity.BioneMsgLog;
import com.yusys.bione.frame.message.entity.BioneMsgSendType;
import com.yusys.bione.frame.message.service.MsgSendTypeBS;

/**
 * @author	tanxu
 * @email	tanxu@yuchengtech.com
 * @date	2014-1-20
 */
public class DBMessagerFactory implements IMessagerFactory<BioneMsgLog, BioneMsgAttachInfo> {
	
	private static DBMessagerFactory factory = new DBMessagerFactory();

	private static Map<String, IMessager<BioneMsgLog, BioneMsgAttachInfo>> messagers = Maps.newHashMap();
	
	@SuppressWarnings("unchecked")
	private DBMessagerFactory(){
		MsgSendTypeBS msgSendTypeBS = SpringContextHolder.getBean(MsgSendTypeBS.class);
		for (BioneMsgSendType mst :  msgSendTypeBS.getAllEntityList()) {
			try {
				Class<? extends IMessager<BioneMsgLog, BioneMsgAttachInfo>> clazz = (Class<? extends IMessager<BioneMsgLog, BioneMsgAttachInfo>>)Class.forName(mst.getBeanName());
				messagers.put(mst.getSendTypeNo(), clazz.newInstance());
			} catch (Exception e) {
				throw new IllegalArgumentException("Could not instantiate " + mst.getBeanName(), e);
			}
		}
	}
	
	public static IMessagerFactory<BioneMsgLog, BioneMsgAttachInfo> getInstance(){
		return factory;
	}
	
	@Override
	public IMessager<BioneMsgLog, BioneMsgAttachInfo> getMessager(final String type) {
		return messagers.get(type);
	}
	
}
