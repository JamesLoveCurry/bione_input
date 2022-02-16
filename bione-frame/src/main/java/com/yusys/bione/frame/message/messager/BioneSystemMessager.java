/**
 * 
 */
package com.yusys.bione.frame.message.messager;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.springframework.transaction.annotation.Transactional;

import com.yusys.bione.comp.utils.SpringContextHolder;
import com.yusys.bione.frame.message.entity.BioneMsgAttachInfo;
import com.yusys.bione.frame.message.entity.BioneMsgAttachRel;
import com.yusys.bione.frame.message.entity.BioneMsgAttachRelPK;
import com.yusys.bione.frame.message.entity.BioneMsgLog;
import com.yusys.bione.frame.message.entity.BioneMsgUserRel;
import com.yusys.bione.frame.message.entity.BioneMsgUserRelPK;
import com.yusys.bione.frame.message.entity.BioneMsgUserState;
import com.yusys.bione.frame.message.entity.BioneMsgUserStatePK;
import com.yusys.bione.frame.message.service.MsgAttachRelBS;
import com.yusys.bione.frame.message.service.MsgNoticeLogBS;
import com.yusys.bione.frame.message.service.MsgUserRelBS;
import com.yusys.bione.frame.message.service.MsgUserStateBS;

/**
 * @author	tanxu
 * @email	tanxu@yuchengtech.com
 * @date	2014-2-27
 */
public class BioneSystemMessager implements IMessager<BioneMsgLog, BioneMsgAttachInfo> {

	
	@Override
	public boolean send(BioneMsgLog msg) throws BioneMessageException {
		
		MsgNoticeLogBS logBs = SpringContextHolder.getBean("msgNoticeLogBS");
		MsgUserRelBS userRelBs = SpringContextHolder.getBean("msgUserRelBS");
		MsgUserStateBS stateBs = SpringContextHolder.getBean("msgUserStateBS");
		
		msg.setViewSts("1");
		logBs.saveEntity(msg);
		
		//保存用户与平台消息关系
		BioneMsgUserRelPK relPk = new BioneMsgUserRelPK();
		relPk.setLogicSysNo(msg.getLogicSysNo());
		relPk.setMsgId(msg.getMsgId());
		relPk.setUserId(msg.getReceiveUser());
		
		BioneMsgUserRel rel = new BioneMsgUserRel();
		rel.setId(relPk);
		userRelBs.saveOrUpdateEntity(rel);

		//保存用户与平台消息状态
		BioneMsgUserStatePK statePk = new BioneMsgUserStatePK();
		statePk.setUserId(msg.getReceiveUser());
		statePk.setLogicSysNo(msg.getLogicSysNo());
		
		BioneMsgUserState state = new BioneMsgUserState();
		state.setId(statePk);
		if(msg.getMsgMode().equals("01")||msg.getMsgMode().equals("02")){
			state.setPushSts("1");
		}else{
			state.setMsgSts("1");
		}
		
		
		stateBs.saveOrUpdateEntity(state);
		
		return true;
	}

	@Override
	@Transactional(readOnly = false)
	public boolean send(BioneMsgLog msg, Iterable<BioneMsgAttachInfo> attaches)
			throws BioneMessageException {
		this.send(msg);
		
		MsgAttachRelBS attachRelBs = SpringContextHolder.getBean("msgAttachRelBS");
		//保存消息与附件关系
		List<BioneMsgAttachRel> rels = new ArrayList<BioneMsgAttachRel>();
		for(Iterator<BioneMsgAttachInfo> it = attaches.iterator(); it.hasNext();) {
			BioneMsgAttachInfo attach = it.next();
			
			BioneMsgAttachRel rel = new BioneMsgAttachRel();
			BioneMsgAttachRelPK id = new BioneMsgAttachRelPK();
			id.setAttachId(attach.getAttachId());
			id.setMsgId(msg.getMsgId());
			rel.setId(id);
			rels.add(rel);
		}
		attachRelBs.saveBatch(rels);
		
		return true;
	}

}
