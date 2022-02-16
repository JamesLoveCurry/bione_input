package com.yusys.bione.frame.message.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.yusys.bione.comp.entity.page.Pager;
import com.yusys.bione.frame.auth.service.AuthBS;
import com.yusys.bione.frame.message.entity.BioneMsgAuthObjRel;
import com.yusys.bione.frame.message.entity.BioneMsgAuthObjRelPK;
import com.yusys.bione.frame.security.BioneUser;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.yusys.bione.comp.repository.jpa.SearchResult;
import com.yusys.bione.comp.service.BaseBS;
import com.yusys.bione.frame.base.common.GlobalConstants4frame;
import com.yusys.bione.frame.message.entity.BioneMsgNoticeInfo;
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
public class MsgNoticeInfoBS extends BaseBS<BioneMsgNoticeInfo> {

	@Autowired
	private AuthBS authBS;

	// public SearchResult<BioneMsgNoticeInfo> getAll(int pageFirstIndex,
	// int pagesize, String sortname, String sortorder,

	// Map<String, Object> Conditionmap) {
	// StringBuilder jql = new StringBuilder("");
	// jql.append("select notice from BioneMsgNoticeInfo notice where 1=1 ");
	// if (!Conditionmap.get("jql").equals("")) {
	// jql.append(" and " + Conditionmap.get("jql"));
	// }
	// if (!StringUtils.isEmpty(sortname)) {
	// jql.append(" order by notice." + sortname + " " + sortorder);
	// }
	// Map<String, Object> values = (Map<String, Object>) Conditionmap
	// .get("params");
	// SearchResult<BioneMsgNoticeInfo> msgSendTypeList = this.baseDAO
	// .findPageWithNameParam(pageFirstIndex, pagesize,
	// jql.toString(), values);
	//
	// return msgSendTypeList;
	// }

	// public boolean checkTypeName(String id, String sendTypeName) {
	// String jql =
	// "select type from BioneMsgNoticeInfo type where type.sendTypeName = ?0 ";
	// List<BioneMsgNoticeInfo> list = new ArrayList<BioneMsgNoticeInfo>();
	// if (id == null || id.equals("")) {
	// list = this.baseDAO.findWithIndexParam(jql, sendTypeName);
	// } else {
	// jql = jql + " and type.sendTypeNo <> ?1";
	// list = this.baseDAO.findWithIndexParam(jql, sendTypeName, id);
	// }
	// if (list == null || list.size() == 0) {
	// return true;
	// } else {
	// return false;
	// }
	// }

	/**
	 * 获取用于加载grid的数据，查看公告时
	 */
	@SuppressWarnings("unchecked")
	public SearchResult<BioneMsgNoticeInfo> getMsgListWithPage(
			String logicSysNo, String userId, int firstResult, int pageSize,
			String orderBy, String orderType, Map<String, Object> conditionMap) {
		Map<String, Object> values = (Map<String, Object>) conditionMap
				.get("params");
		StringBuilder jql = new StringBuilder();
		jql.append("select info from BioneMsgNoticeInfo info where 1=1 and info.logicSysNo = :logicSysNo ");
		if (!conditionMap.get("jql").equals("")) {
			jql.append(" and " + conditionMap.get("jql"));
		}
		if(!BioneSecurityUtils.getCurrentUserInfo().isSuperUser()){
			// 不是逻辑系统管理员，只能管理当前机构下公告
			jql.append(" and info.createUser in (select userId from BioneUserInfo usr where usr.orgNo = :orgNo)");
			values.put("orgNo", BioneSecurityUtils.getCurrentUserInfo().getOrgNo());
		}
		if (!StringUtils.isEmpty(orderBy)) {
			jql.append(" order by info.announcementSts desc, " + orderBy + " "
					+ orderType);
		}
		values.put("logicSysNo", logicSysNo);
		SearchResult<BioneMsgNoticeInfo> msgAnnoList = this.baseDAO
				.findPageWithNameParam(firstResult, pageSize, jql.toString(),
						values);
		return msgAnnoList;
	}

	/**
	 * 获取用于加载grid的数据（公告显示时查看所有）
	 */
	@SuppressWarnings("unchecked")
	public SearchResult<BioneMsgNoticeInfo> getMsgListWithPageForView(
			String logicSysNo, String userId, int firstResult, int pageSize,
			String orderBy, String orderType, Map<String, Object> conditionMap) {
		StringBuilder jql = new StringBuilder();
		jql.append("SELECT msgAnno FROM BioneMsgNoticeInfo msgAnno, BioneUserInfo u WHERE msgAnno.createUser=u.userId "
				+ " AND msgAnno.logicSysNo = :logicSysNo AND msgAnno.announcementSts = :announcementSts AND msgAnno.announcementType != :announcementType");
		if (!conditionMap.get("jql").equals("")) {
			jql.append(" and " + conditionMap.get("jql"));
		}
		if (StringUtils.isNotBlank(orderBy)) {
			jql.append(" order by msgAnno.announcementSts desc, " + orderBy + " " + orderType);
		}else {
			jql.append(" order by msgAnno.announcementSts desc, msgAnno.lastUpdateTime desc");
		}
		Map<String, Object> values = (Map<String, Object>) conditionMap
				.get("params");
		values.put("logicSysNo", logicSysNo);
		values.put("announcementSts",
				GlobalConstants4frame.MESSAGE_ANNOUNCEMENT_STATUS_PUBLISHED);
		values.put("announcementType","00");
		SearchResult<BioneMsgNoticeInfo> msgAnnoList = this.baseDAO
				.findPageWithNameParam(firstResult, pageSize, jql.toString(),
						values);
		return msgAnnoList;
	}

	@Transactional(readOnly = false)
	public void publish(String id) {
		String[] ids = StringUtils.split(id, ',');
		if (ids != null && ids.length > 0) {

			for (String tmpId : ids) {
				BioneMsgNoticeInfo info = this.getEntityById(tmpId);
				info.setAnnouncementSts(GlobalConstants4frame.MESSAGE_ANNOUNCEMENT_STATUS_PUBLISHED);

				this.saveOrUpdateEntity(info);
			}
		}
	}

	@Transactional(readOnly = false)
	public void unPublish(String id) {
		String[] ids = StringUtils.split(id, ',');
		if (ids != null && ids.length > 0) {

			for (String tmpId : ids) {
				BioneMsgNoticeInfo info = this.getEntityById(tmpId);
				info.setAnnouncementSts(GlobalConstants4frame.MESSAGE_ANNOUNCEMENT_STATUS_DELETED);

				this.saveOrUpdateEntity(info);
			}
		}
	}

	/**
	 * 获取需要做交集权限判断的公告id
	 * 
	 * @param ids
	 * @return
	 */
	public List<String> getAuthNoticeIds(List<String> ids) {
		List<String> returnList = new ArrayList<String>();
		if (ids == null || ids.size() <= 0) {
			return returnList;
		}
		String sql = "select announcement_id                                                                                                     "
				+ "from (                                                                                                                               "
				+ "SELECT                                                                                                                             "
				+ "    max(obj_id) ,  announcement_id , obj_def_no                                                                 "
				+ "FROM                                                                                                                               "
				+ "    bione_msg_auth_obj_rel                                                                                                "
				+ "WHERE                                                                                                                             "
				+ "    bione_msg_auth_obj_rel.ANNOUNCEMENT_ID in ?0   "
				+ "  and   obj_def_no IN (?1 ,?2) "
				+ "  group by announcement_id , obj_def_no "
				+ ") group by announcement_id "
				+"   having count(obj_def_no) = 2 ";
		List<Object[]> objs = this.baseDAO.findByNativeSQLWithIndexParam(sql,
				ids, GlobalConstants4frame.AUTH_OBJ_DEF_ID_ORG,
				GlobalConstants4frame.AUTH_OBJ_DEF_ID_ROLE);
		if (objs != null) {
			for (Object objTmp : objs) {
				String tmp = (String)objTmp;
				if (!returnList.contains(tmp)) {
					returnList.add(tmp);
				}
			}
		}
		return returnList;
	}

	// /**
	// * 保存公告信息
	// * @param entity
	// */
	// @Transactional(readOnly = false)
	// public void saveMsg(BioneMsgNoticeInfo entity) {
	// this.saveEntity(entity);
	// }
	//
	//
	// public BioneMsgNoticeInfo updateMsg(BioneMsgNoticeInfo entity) {
	// return this.updateEntity(entity);
	// }
	//
	// @Transactional(readOnly = false)
	// public void deleteMsg(String id) {
	// this.removeEntityById(id);
	// }
	//
	// @Transactional(readOnly = false)
	// public void deleteBatch(String[] ids) {
	// if(ids!=null && ids.length>0) {
	// for(String id : ids) {
	// this.removeEntityById(id);
	// }
	// }
	// }

	/**
	 * 查询公告信息
	 * @param pager
	 * @param maxRows
	 * @return
	 */
	public SearchResult<BioneMsgNoticeInfo> getNoticeInfo(Pager pager, String maxRows) {
		if(StringUtils.isNotBlank(maxRows)){
			pager.setPagesize(Integer.parseInt(maxRows));
			pager.setSortname("lastUpdateTime desc");
		}
		BioneUser userObj = BioneSecurityUtils.getCurrentUserInfo();
		SearchResult<BioneMsgNoticeInfo> searchResult = null;
		if (userObj.isSuperUser()) {
			searchResult = getMsgListWithPageForView(
					userObj.getCurrentLogicSysNo(), userObj.getUserId(),
					pager.getPageFirstIndex(), pager.getPagesize(),
					pager.getSortname(), pager.getSortorder(),
					pager.getSearchCondition());
		} else {
			// 由通用的平台授权方式改为自定义授权方式，自定义的公告授权采用的是交集授权的方式。
			List<String> ids = this.authBS.getNoticesByUserRel();
			searchResult = findNoticeInfos(
					userObj.getCurrentLogicSysNo(), userObj.getUserId(),
					pager.getPageFirstIndex(), pager.getPagesize(),
					pager.getSortname(), pager.getSortorder(),
					pager.getSearchCondition(), ids);
		}
		return searchResult;
	}

	public SearchResult<BioneMsgNoticeInfo> findNoticeInfos(String logicSysNo,
															String userId, int firstResult, int pageSize, String orderBy,
															String orderType, Map<String, Object> conditionMap, List<String> ids) {
		String jql = (String) conditionMap.get("jql");
		@SuppressWarnings("unchecked")
		Map<String, Object> values = (Map<String, Object>) conditionMap
				.get("params");
		if (StringUtils.isNotEmpty(jql)) {
			jql += " and msgAnno.announcementType != :announcementType";
			conditionMap.put("announcementType", "00");
		}
		if (ids.size() > 0) {
			if (StringUtils.isNotEmpty(jql)) {
				jql = jql + " and (";
			}
			else{
				jql = jql + " (";
			}
			List<List<String>> idLists = this.change(ids);
			for(int i = 0; i < idLists.size(); i++) {
				if(i == 0) {
					jql = jql + "msgAnno.announcementId in (:ids" + i + ") ";
				}else {
					jql = jql + "or msgAnno.announcementId in (:ids" + i + ") ";
				}
				values.put("ids" + i, idLists.get(i));
			}
			jql = jql + ") ";
			conditionMap.put("params", values);
		} else {
			jql = jql + " 1=0 ";
		}
		conditionMap.put("jql", jql);
		return getMsgListWithPageForView(logicSysNo, userId,
				firstResult, pageSize, orderBy, orderType, conditionMap);
	}
	public List<List<String>> change(List<String> rptList){
		List<List<String>> rptIdsParam = new ArrayList<List<String>>();
		int count = rptList.size()/1000 + (rptList.size() % 1000 == 0 ? 0 : 1);//rptList以1000为单位分割
		for(int i=0;i<count;i++){
			rptIdsParam.add(rptList.subList(i * 1000, ((i+1) * 1000 > rptList.size() ? rptList.size() : (i + 1) * 1000)));
		}
		return rptIdsParam;
	}

	public BioneMsgNoticeInfo getNotice(String id){
		String sql = " select rpt from BioneMsgNoticeInfo rpt where rpt.announcementId = ?0";
		BioneMsgNoticeInfo info = this.baseDAO.findUniqueWithIndexParam(sql, id);
		return info;
	}
	public List<BioneMsgAuthObjRel> getNoticeList (String id){
		List<BioneMsgAuthObjRel> msgList = new ArrayList<BioneMsgAuthObjRel>();
		String sql = "select * from BIONE_MSG_AUTH_OBJ_REL where announcement_id = ?0";
		List<Object[]> noticeList = this.baseDAO.findByNativeSQLWithIndexParam(sql, id);
		if(noticeList.size()>0){
			for(Object[] obj:noticeList){
				BioneMsgAuthObjRel info = new BioneMsgAuthObjRel();
				BioneMsgAuthObjRelPK infoPK = new BioneMsgAuthObjRelPK();
				infoPK.setAnnouncementId(String.valueOf(obj[2]));
				infoPK.setLogicSysNo(String.valueOf(obj[1]));
				infoPK.setObjId(String.valueOf(obj[0]));
				info.setId(infoPK);
				info.setObjDefNo(String.valueOf(obj[3]));
				msgList.add(info);
			}
		}
		return msgList;
	}

}
