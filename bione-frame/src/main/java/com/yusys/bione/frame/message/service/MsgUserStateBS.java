package com.yusys.bione.frame.message.service;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.yusys.bione.comp.service.BaseBS;
import com.yusys.bione.frame.message.entity.BioneMsgUserState;
import com.yusys.bione.frame.security.BioneSecurityUtils;

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
public class MsgUserStateBS extends BaseBS<BioneMsgUserState> {

	/**
	 * 查看用户是否有新消息
	 * @return
	 */
	public Map<String, Object> showMsgState() {
		String jql = "select state from BioneMsgUserState state where state.id.userId = ?0 " +
				"and state.id.logicSysNo=?1 ";
		BioneMsgUserState state = this.baseDAO.findUniqueWithIndexParam(jql,
				BioneSecurityUtils.getCurrentUserId(), BioneSecurityUtils
						.getCurrentUserInfo().getCurrentLogicSysNo());
		BioneMsgUserState undefinedState = this.baseDAO.findUniqueWithIndexParam(jql, BioneSecurityUtils.getCurrentUserId(), "undefined");
		Map<String, Object> map = new HashMap<String, Object>();
		if (state != null) {
			map.put("msgSts", "1".equals(state.getMsgSts())?true:false);
			map.put("msgNum", this.getUnReadMsgCount());
			if(undefinedState != null){
				if("1".equals(undefinedState.getMsgSts())){
					map.put("msgSts", true);
				}
			}
			return map;
		}else{
			map.put("msgSts", false);
			return map;
		}
		
	}
	/**
	 * 获取未读信息记录数
	 */
	public Long getUnReadMsgCount() {
		String jql= "select count(log) from BioneMsgLog log where log.receiveUser =:receiveUser "
				+ " and (log.logicSysNo =:logicSysNo or log.logicSysNo is null) and log.viewSts = :viewSts";
		Map<String, Object> values = new HashMap<String, Object>();
		values.put("receiveUser", BioneSecurityUtils.getCurrentUserId());
		values.put("logicSysNo", BioneSecurityUtils.getCurrentUserInfo().getCurrentLogicSysNo());
		values.put("viewSts", "1");
		return this.baseDAO.findUniqueWithNameParam(jql, values);
	}
}
