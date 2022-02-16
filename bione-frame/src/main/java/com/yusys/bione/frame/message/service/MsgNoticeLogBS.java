package com.yusys.bione.frame.message.service;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.Set;

import com.google.common.collect.Maps;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.Lists;
import com.yusys.bione.comp.repository.jpa.SearchResult;
import com.yusys.bione.comp.service.BaseBS;
import com.yusys.bione.comp.utils.RandomUtils;
import com.yusys.bione.frame.excel.ExcelBS;
import com.yusys.bione.frame.message.entity.BioneMsgLog;
import com.yusys.bione.frame.message.entity.BioneMsgUserRel;
import com.yusys.bione.frame.message.entity.BioneMsgUserRelPK;
import com.yusys.bione.frame.message.entity.BioneMsgUserState;
import com.yusys.bione.frame.message.entity.BioneMsgUserStatePK;
import com.yusys.bione.frame.message.web.vo.BioneMsgLogVO;
import com.yusys.bione.frame.message.web.vo.MsgLogVO;
import com.yusys.bione.frame.security.BioneSecurityUtils;

/**
 * <pre>
 * Title: 消息模块-消息类型定义服务
 * Description: 消息模块-消息类型定义服务
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
public class MsgNoticeLogBS extends BaseBS<BioneMsgLog> {

	@Autowired
	private MsgUserRelBS relBs;

	@Autowired
	private MsgUserStateBS stateBs;
	
	@Autowired
	private ExcelBS excelBS;

	/**
	 * 分页得到所有的消息
	 * 
	 * @param pageFirstIndex
	 * @param pagesize
	 * @param sortname
	 * @param sortorder
	 * @param Conditionmap
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public SearchResult<BioneMsgLogVO> getAll(int pageFirstIndex, int pagesize,
			String sortname, String sortorder, Map<String, Object> Conditionmap) {

		StringBuilder jql = new StringBuilder("");
		jql.append("select new com.yusys.bione.frame.message.web.vo.BioneMsgLogVO(log,usr.userName) from BioneMsgLog log,BioneUserInfo usr "
				+ " where 1=1 and log.receiveUser =:receiveUser "
				+ " and (log.logicSysNo =:logicSysNo or log.logicSysNo = 'undefined') "
				+ " and log.sendUser = usr.id.userId ");
		if (!Conditionmap.get("jql").equals("")) {
			jql.append(" and " + Conditionmap.get("jql"));
		}
		if (!StringUtils.isEmpty(sortname)) {
			jql.append(" order by log.viewSts desc, log." + sortname + " " + sortorder);
		}
		Map<String, Object> values = (Map<String, Object>) Conditionmap
				.get("params");
		values.put("receiveUser", BioneSecurityUtils.getCurrentUserId());
		values.put("logicSysNo", BioneSecurityUtils.getCurrentUserInfo()
				.getCurrentLogicSysNo());
		SearchResult<BioneMsgLogVO> msgSendTypeList = this.baseDAO
				.findPageWithNameParam(pageFirstIndex, pagesize,
						jql.toString(), values);

		return msgSendTypeList;
	}

	/**
	 * 查看我的消息时，显示消息
	 * 
	 * @param logicSysNo
	 * @param userId
	 * @param firstResult
	 * @param pageSize
	 * @param orderBy
	 * @param orderType
	 * @param conditionMap
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public SearchResult<BioneMsgLog> getMsgListWithPageForView(
			String logicSysNo, String userId, int firstResult, int pageSize,
			String orderBy, String orderType, Map<String, Object> conditionMap) {
		StringBuilder jql = new StringBuilder();
		jql.append("SELECT log FROM BioneMsgLog log WHERE 1=1 AND " +
				" (log.logicSysNo = :logicSysNo or log.logicSysNo = 'undefined') and log.receiveUser =:receiveUser ");
		if (!conditionMap.get("jql").equals("")) {
			jql.append(" and " + conditionMap.get("jql"));
		}
		if (!StringUtils.isEmpty(orderBy)) {
			jql.append(" order by log." + orderBy + " " + orderType);
		}
		Map<String, Object> values = (Map<String, Object>) conditionMap
				.get("params");
		values.put("logicSysNo", logicSysNo);
		values.put("receiveUser", BioneSecurityUtils.getCurrentUserId());
		SearchResult<BioneMsgLog> logList = this.baseDAO.findPageWithNameParam(
				firstResult, pageSize, jql.toString(), values);
		return logList;
	}

	/**
	 * 改变消息的状态
	 * @param id
	 */
	@Transactional(readOnly = false)
	public void changeRel(String id) {
		// 写表BioneMsgUserRel
		BioneMsgUserRelPK relPk = new BioneMsgUserRelPK();
		relPk.setMsgId(id);
		relPk.setUserId(BioneSecurityUtils.getCurrentUserId());
		relPk.setLogicSysNo(BioneSecurityUtils.getCurrentUserInfo()
				.getCurrentLogicSysNo());

		if (this.relBs.getEntityById(relPk) == null) {
			BioneMsgUserRel rel = new BioneMsgUserRel();
			rel.setId(relPk);
			this.relBs.saveEntity(rel);
		}
		// 写表BioneMsgUserState
		BioneMsgUserStatePK statePk = new BioneMsgUserStatePK();
		statePk.setLogicSysNo(BioneSecurityUtils.getCurrentUserInfo()
				.getCurrentLogicSysNo());
		statePk.setUserId(BioneSecurityUtils.getCurrentUserId());

		BioneMsgUserState state = new BioneMsgUserState();
		if (this.stateBs.getEntityById(statePk) == null) {
			state.setId(statePk);
			state.setMsgSts("0");
			state.setPushSts("0");
		} else {
			state.setMsgSts("1");
			state.setPushSts("1");
		}
		state.setId(statePk);
		this.stateBs.saveOrUpdateEntity(state);

	}


	/** 
	 * 查看消息时，重置该消息的阅读状态，并重置该用户的阅读状态
	 * @param id
	 */
	@Transactional(readOnly = false)
	public String resetState(String id) {
		String msgSts = "1";
		BioneMsgLog log = this.findUniqueEntityByProperty("msgId", id);
		if (log.getViewSts().equals("1")) {
			log.setViewSts("0");
			log.setViewTime(new Timestamp(System.currentTimeMillis()));
			this.saveOrUpdateEntity(log);

			String jql = "select log from BioneMsgLog log "
					+ " where log.viewSts = ?0 and log.receiveUser = ?1 "
					+ " and (log.logicSysNo = ?2 or log.logicSysNo is null) ";
			List<BioneMsgLog> list = this.baseDAO.findWithIndexParam(jql, "1",
					BioneSecurityUtils.getCurrentUserId(),BioneSecurityUtils.getCurrentUserInfo()
					.getCurrentLogicSysNo());
			BioneMsgUserStatePK pk = new BioneMsgUserStatePK();
			pk.setLogicSysNo(BioneSecurityUtils.getCurrentUserInfo()
					.getCurrentLogicSysNo());
			pk.setUserId(BioneSecurityUtils.getCurrentUserId());

			BioneMsgUserState state = this.stateBs.getEntityById(pk);
			if (list != null && list.size() > 0) {
				state.setMsgSts("1");
				msgSts = "1";
			} else {
				state.setMsgSts("0");
				msgSts = "0";
			}
			this.stateBs.saveOrUpdateEntity(state);
		}
		else{
			BioneMsgUserStatePK pk = new BioneMsgUserStatePK();
			pk.setLogicSysNo(BioneSecurityUtils.getCurrentUserInfo()
					.getCurrentLogicSysNo());
			pk.setUserId(BioneSecurityUtils.getCurrentUserId());
			BioneMsgUserState state = this.stateBs.getEntityById(pk);
			if(state!=null){
				msgSts = state.getMsgSts();
			}
		}
		return msgSts;
	}

	/**
	 * 得到用户消息列表
	 * @return
	 */
	@Transactional(readOnly = false)
	public List<MsgLogVO> getMsgs() {
		String jql = "select log from BioneMsgLog log, BioneMsgUserRel rel"
				+ " where rel.id.msgId = log.msgId "
				+ " and rel.id.userId =?0 and (rel.id.logicSysNo =?1 or rel.id.logicSysNo = 'undefined' ) " 
				+ " and (log.msgMode = '01' or log.msgMode = '02') and log.viewSts = ?2";
		List<BioneMsgLog> list = this.baseDAO.findWithIndexParam(jql,
				BioneSecurityUtils.getCurrentUserId(), BioneSecurityUtils
						.getCurrentUserInfo().getCurrentLogicSysNo(), "1");
		List<MsgLogVO> vo = new ArrayList<MsgLogVO>();
		for (int i = 0; i < list.size(); i++) {
			MsgLogVO tmp = new MsgLogVO();
			tmp.setId(list.get(i).getMsgId());
			tmp.setMode(list.get(i).getMsgMode());
			tmp.setAlarmTitle((list.get(i).getMsgMode().equals("02") ? "[响铃]"
					: "") + list.get(i).getMsgTitle());
			
			tmp.setAlarmText(list.get(i).getMsgDetail());
			vo.add(tmp);
			BioneMsgLog log = list.get(i);
			log.setViewSts("0");
			log.setViewTime(new Timestamp(System.currentTimeMillis()));
			this.saveOrUpdateEntity(log);
		}

		BioneMsgUserStatePK pk = new BioneMsgUserStatePK();
		pk.setLogicSysNo(BioneSecurityUtils.getCurrentUserInfo()
				.getCurrentLogicSysNo());
		pk.setUserId(BioneSecurityUtils.getCurrentUserId());

		BioneMsgUserState state = this.stateBs.getEntityById(pk);
		if (state != null) {
			state.setPushSts("0");
			this.stateBs.saveOrUpdateEntity(state);
		}

		return vo;
	}

	/**
	 * 全部消息标记为已读
	 */
	@Transactional(readOnly = false)
	public void changeViewSts() {
		String jql = "select log from BioneMsgLog log where log.receiveUser =?0 and (log.logicSysNo =?1 or log.logicSysNo ='undefined') ";
		List<BioneMsgLog> log = this.baseDAO.findWithIndexParam(jql,
				BioneSecurityUtils.getCurrentUserId(), BioneSecurityUtils.getCurrentUserInfo().getCurrentLogicSysNo());
		for (BioneMsgLog tmp : log) {
			tmp.setViewSts("0");
			tmp.setViewTime(new Timestamp(System.currentTimeMillis()));
			this.saveOrUpdateEntity(tmp);
		}
		BioneMsgUserStatePK id = new BioneMsgUserStatePK();
		id.setLogicSysNo(BioneSecurityUtils.getCurrentUserInfo()
				.getCurrentLogicSysNo());
		id.setUserId(BioneSecurityUtils.getCurrentUserId());

		BioneMsgUserState state = new BioneMsgUserState();
		state.setId(id);
		state.setMsgSts("0");
		state.setPushSts("0");
		this.stateBs.saveOrUpdateEntity(state);
	}

	/**
	 * 得到消息类型的名称
	 * @param id
	 * @return
	 */
	public String getMsgTypeName(String id) {
		String jql = "select type.sendTypeName from BioneMsgSendType type, BioneMsgLog log where log.sendTypeNo = type.sendTypeNo "
				+ " and log.msgId =?0";
		String tmp = this.baseDAO.findUniqueWithIndexParam(jql, id);
		return tmp;
	}

	/**
	 * 查看用户是否有新消息
	 * @return
	 */
	public Map<String, Object> showMsgState() {
		return this.stateBs.showMsgState();
	}

	@Transactional(readOnly = false)
	public void viewedDelete() {
		String jql = "select log from BioneMsgLog log where (log.logicSysNo =?0 or logicSysNo = 'undefined') " +
				" and log.receiveUser =?1" +
				" and log.viewSts =?2";
		List<BioneMsgLog> list = this.baseDAO.findWithIndexParam(jql,
				BioneSecurityUtils.getCurrentUserInfo().getCurrentLogicSysNo(), 
				BioneSecurityUtils.getCurrentUserId(), "0");
		this.removeEntity(list);
		
	}
	/**
	 * 平台消息批量保存
	 * @param usrIds 消息提醒用户ID
	 * @param msgTitle 消息主题
	 * @param msgDetail 消息内容
	 * @param msgType 消息类型  01分享  02打回
	 * @return Map
	 */
	public Map<String, Object> savePlatMsgs(String[] usrIds,String msgTitle,String msgDetail,String msgType){
		Map<String, Object> map = new HashMap<String, Object>();
		if(null != usrIds && usrIds.length > 0){
			List<BioneMsgLog> msgs = Lists.newArrayList();
			List<BioneMsgUserState> msgStas = Lists.newArrayList();
			for(String usrId : usrIds){
				BioneMsgLog msg = new BioneMsgLog();
				msg.setMsgId(RandomUtils.uuid2());
				msg.setSendTypeNo("f39e865d7b214ea5afb0f2f9bb9a5cbe");
				msg.setSendUser(BioneSecurityUtils.getCurrentUserId());
				msg.setReceiveUser(usrId);
				msg.setMsgTitle(msgTitle);
				msg.setMsgDetail(msgDetail);
				msg.setMsgType(msgType);
				msg.setLogicSysNo(BioneSecurityUtils.getCurrentUserInfo().getCurrentLogicSysNo());
				msg.setSendTime(new Timestamp(System.currentTimeMillis()));
				msg.setViewSts("1");//未读
				msgs.add(msg);
				//用户平台消息状态
				BioneMsgUserState msgSta = new BioneMsgUserState();
				BioneMsgUserStatePK pk = new BioneMsgUserStatePK();
				pk.setUserId(usrId);
				pk.setLogicSysNo(BioneSecurityUtils.getCurrentUserInfo().getCurrentLogicSysNo());
				msgSta.setId(pk);
				msgSta.setMsgSts("1");
				msgStas.add(msgSta);
			}
			excelBS.saveEntityJdbcBatch(msgs);
			List<String> idFeild =new ArrayList<String>();
			idFeild.add("id.userId");
			idFeild.add("id.logicSysNo");
			excelBS.deleteEntityJdbcBatch(msgStas, idFeild);
			excelBS.saveEntityJdbcBatch(msgStas);
		}
		map.put("mag", "success");
		return map;
	}
	/**
	 * 平台消息批量保存
	 */
	public Map<String, Object> savePlatMsgs(Set<String> usrIds,String msgTitle,String msgDetail,String msgType){
		Map<String, Object> map = new HashMap<String, Object>();
		if(null != usrIds && usrIds.size() > 0){
			List<BioneMsgLog> msgs = Lists.newArrayList();
			List<BioneMsgUserState> msgStas = Lists.newArrayList();
			for(String usrId : usrIds){
				BioneMsgLog msg = new BioneMsgLog();
				msg.setMsgId(RandomUtils.uuid2());
				msg.setSendTypeNo("f39e865d7b214ea5afb0f2f9bb9a5cbe");
				msg.setSendUser(BioneSecurityUtils.getCurrentUserId());
				msg.setReceiveUser(usrId);
				msg.setMsgTitle(msgTitle);
				msg.setMsgDetail(msgDetail);
				msg.setMsgType(msgType);
				msg.setLogicSysNo(BioneSecurityUtils.getCurrentUserInfo().getCurrentLogicSysNo());
				msg.setSendTime(new Timestamp(System.currentTimeMillis()));
				msg.setViewSts("1");//未读
				msgs.add(msg);
				//用户平台消息状态
				BioneMsgUserState msgSta = new BioneMsgUserState();
				BioneMsgUserStatePK pk = new BioneMsgUserStatePK();
				pk.setUserId(usrId);
				pk.setLogicSysNo(BioneSecurityUtils.getCurrentUserInfo().getCurrentLogicSysNo());
				msgSta.setId(pk);
				msgSta.setMsgSts("1");
				msgStas.add(msgSta);
			}
			excelBS.saveEntityJdbcBatch(msgs);
			List<String> idFeild =new ArrayList<String>();
			idFeild.add("id.userId");
			idFeild.add("id.logicSysNo");
			excelBS.deleteEntityJdbcBatch(msgStas, idFeild);
			excelBS.saveEntityJdbcBatch(msgStas);
		}
		map.put("mag", "success");
		return map;
	}
	/**
	 * 平台消息单个保存
	 * @param usrIds 消息提醒用户ID
	 * @param msgTitle 消息主题
	 * @param msgDetail 消息内容
	 * @param msgType 消息类型  01分享  02打回
	 * @return Map
	 */
	public Map<String, Object> savePlatMsg(String usrId,String msgTitle,String msgDetail,String msgType){
		Map<String, Object> map = new HashMap<String, Object>();
		List<BioneMsgLog> msgs = Lists.newArrayList();
		List<BioneMsgUserState> msgStas = Lists.newArrayList();
		
		BioneMsgLog msg = new BioneMsgLog();
		msg.setMsgId(RandomUtils.uuid2());
		msg.setSendTypeNo("f39e865d7b214ea5afb0f2f9bb9a5cbe");
		msg.setSendUser(BioneSecurityUtils.getCurrentUserId());
		msg.setReceiveUser(usrId);
		msg.setMsgTitle(msgTitle);
		msg.setMsgDetail(msgDetail);
		msg.setMsgType(msgType);
		msg.setLogicSysNo(BioneSecurityUtils.getCurrentUserInfo().getCurrentLogicSysNo());
		msg.setSendTime(new Timestamp(System.currentTimeMillis()));
		msg.setViewSts("1");//未读
		msgs.add(msg);
		//用户平台消息状态
		BioneMsgUserState msgSta = new BioneMsgUserState();
		BioneMsgUserStatePK pk = new BioneMsgUserStatePK();
		pk.setUserId(usrId);
		pk.setLogicSysNo(BioneSecurityUtils.getCurrentUserInfo().getCurrentLogicSysNo());
		msgSta.setId(pk);
		msgSta.setMsgSts("1");
		msgStas.add(msgSta);
			
		excelBS.saveEntityJdbcBatch(msgs);
		List<String> idFeild =new ArrayList<String>();
		idFeild.add("id.userId");
		idFeild.add("id.logicSysNo");
		excelBS.deleteEntityJdbcBatch(msgStas, idFeild);
		excelBS.saveEntityJdbcBatch(msgStas);
			
		map.put("mag", "success");
		return map;
	}
}
