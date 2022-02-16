package com.yusys.bione.frame.message.web;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.yusys.bione.comp.common.CommonTreeNode;
import com.yusys.bione.comp.entity.page.Pager;
import com.yusys.bione.comp.repository.jpa.SearchResult;
import com.yusys.bione.comp.utils.RandomUtils;
import com.yusys.bione.comp.utils.SpringContextHolder;
import com.yusys.bione.comp.utils.StringUtils2;
import com.yusys.bione.frame.auth.entity.BioneAuthObjDef;
import com.yusys.bione.frame.auth.service.AuthBS;
import com.yusys.bione.frame.base.common.GlobalConstants4frame;
import com.yusys.bione.frame.base.web.BaseController;
import com.yusys.bione.frame.message.entity.*;
import com.yusys.bione.frame.message.service.MsgAttachRelBS;
import com.yusys.bione.frame.message.service.MsgAttachmentBS;
import com.yusys.bione.frame.message.service.MsgAuthObjRelBS;
import com.yusys.bione.frame.message.service.MsgNoticeInfoBS;
import com.yusys.bione.frame.security.BioneSecurityUtils;
import com.yusys.bione.frame.security.BioneUser;
import com.yusys.bione.frame.security.IAuthObject;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <pre>
 * Title: 消息模块-消息类型定义控制器
 * Description: 消息模块-消息类型定义控制器
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
@Controller
@RequestMapping("/bione/msg/announcement")
public class MsgNoticeInfoController extends BaseController {
	
	@Autowired
	private MsgNoticeInfoBS frsInfoBs;
	
	@Autowired
	private MsgNoticeInfoBS infoBs;

	@Autowired
	private MsgAttachmentBS msgAttachBS;
	
//	@Autowired
//	private RptUserBS rptBS;
	
	@Autowired
	private MsgAttachRelBS attachRelBs;

	@Autowired
	private AuthBS authBS;

	@Autowired
	private MsgAuthObjRelBS AuthRelBS;
	
	//MsgAuthObjRelBS

	/**
	 * 跳转到首页
	 * 
	 * @return
	 */
	@RequestMapping(method = RequestMethod.GET)
	public String index() {
		return "/frame/message/msg-announcement-index";
	}

	/**
	 * 展示公告时，弹出“更多”的页面
	 * 
	 * @return
	 */
	@RequestMapping(value = "/viewIdx", method = RequestMethod.GET)
	public String viewIdx() {
		return "/frame/message/msg-announcement-viewIdx";
	}
	
	/**
	 * 展示公告时，弹出“更多”的页面
	 * 
	 * @return
	 */
	@RequestMapping(value = "/viewInfo", method = RequestMethod.GET)
	public String viewInfo() {
		return "/frame/message/msg-announcement-viewIdx";
	}

	/**
	 * 获取用于加载grid的数据，查看公告时
	 */
	@RequestMapping("/list.*")
	@ResponseBody
	public Map<String, Object> list(Pager pager) {
		BioneUser userObj = BioneSecurityUtils.getCurrentUserInfo();
		SearchResult<BioneMsgNoticeInfo> searchResult = frsInfoBs
				.getMsgListWithPage(userObj.getCurrentLogicSysNo(),
						userObj.getUserId(), pager.getPageFirstIndex(),
						pager.getPagesize(), pager.getSortname(),
						pager.getSortorder(), pager.getSearchCondition());
		Map<String, Object> msgAnnoMap = Maps.newHashMap();
		msgAnnoMap.put("Rows", searchResult.getResult());
		msgAnnoMap.put("Total", searchResult.getTotalCount());
		return msgAnnoMap;
	}

	/**
	 * 获取用于加载grid的数据（公告显示时查看所有）
	 */
	@RequestMapping("/listView.*")
	@ResponseBody
	public Map<String, Object> listView(Pager pager) {
		BioneUser userObj = BioneSecurityUtils.getCurrentUserInfo();
		SearchResult<BioneMsgNoticeInfo> searchResult = frsInfoBs.getMsgListWithPageForView(userObj.getCurrentLogicSysNo(),
						userObj.getUserId(), pager.getPageFirstIndex(),
						pager.getPagesize(), pager.getSortname(),
						pager.getSortorder(), pager.getSearchCondition());
		Map<String, Object> msgAnnoMap = Maps.newHashMap();
		msgAnnoMap.put("Rows", searchResult.getResult());
		msgAnnoMap.put("Total", searchResult.getTotalCount());
		return msgAnnoMap;
	}

	/**
	 * 公告展示时，用户查看公告
	 * 
	 * @param maxRows
	 *            页面上显示的公告条数
	 * @return
	 */
	@RequestMapping("/{maxRows}/portalListView")
	@ResponseBody
	public Map<String, Object> portalListView(
			@PathVariable("maxRows") String maxRows) {
		int iMaxRows = 6;
		if (maxRows != null && maxRows.trim().length() > 0) {
			try {
				iMaxRows = Integer.parseInt(maxRows);
			} catch (Exception ex) {
				logger.error("MessageAnnouncementController#portalListView maxRows转换出错！");
				ex.printStackTrace();
			}
		}
		Map<String, Object> conditionMap = new HashMap<String, Object>();
		conditionMap.put("jql", "");
		conditionMap.put("params", new HashMap<String, Object>());
		//
		BioneUser userObj = BioneSecurityUtils.getCurrentUserInfo();
		SearchResult<BioneMsgNoticeInfo> searchResult = frsInfoBs
				.getMsgListWithPageForView(userObj.getCurrentLogicSysNo(),
						userObj.getUserId(), 0, iMaxRows, "lastUpdateTime",
						" desc ", conditionMap);
		Map<String, Object> msgAnnoMap = Maps.newHashMap();
		msgAnnoMap.put("Rows", searchResult.getResult());
		msgAnnoMap.put("Total", searchResult.getTotalCount());
		return msgAnnoMap;
	}

	/**
	 * 跳转新增公告页面
	 */
	@RequestMapping(value = "/new", method = RequestMethod.GET)
	public String editNew() {
		return "/frame/message/msg-announcement-add";
	}
	
	/**
	 * 防止XSS 攻击漏洞
	 * 
	 * @param announcementTitle
	 * @return boolean
	 * @Date 2021/11/22 10:56       
	 * @author baifk
	 **/
	@RequestMapping(value = "/announcementTitleValid")
	@ResponseBody
	public boolean announcementTitleValid(String announcementTitle) {
		String xssUserName = StringUtils2.striptXSS(announcementTitle);
		if (!announcementTitle.equals(xssUserName)) {
			return false;
		} else {
			return true;
		}
	}

	@RequestMapping("auth")
	public ModelAndView auth() {
		List<BioneAuthObjDef> authObjDefList = this.authBS
				.getAllAuthObjDefBySys(BioneSecurityUtils.getCurrentUserInfo()
						.getCurrentLogicSysNo());
//		List<BioneAuthObjDef> returnDefs = new ArrayList<BioneAuthObjDef>();
//		for(BioneAuthObjDef defTmp : authObjDefList){
//			if(GlobalConstants4frame.AUTH_OBJ_DEF_ID_ORG.equals(defTmp.getObjDefNo())
//					|| GlobalConstants4frame.AUTH_OBJ_DEF_ID_ROLE.equals(defTmp.getObjDefNo())){
//				// 目前公告管理只支持（机构、角色）
//				returnDefs.add(defTmp);
//			}
//		}
		// 将授权对象定义传至页面
		return new ModelAndView("/frame/message/msg-announcement-auth",
				"authObjDefs", authObjDefList);
	}

	/**
	 * 跳转编辑公告页面
	 */
	@RequestMapping(value = "/{id}/edit", method = RequestMethod.GET)
	public ModelAndView edit(@PathVariable("id") String id) {
		id = StringUtils2.javaScriptEncode(id);
		return new ModelAndView("/frame/message/msg-announcement-add", "id", id);
	}

	/**
	 * 跳转预览公告页面
	 */
	@RequestMapping(value = "/{id}/view", method = RequestMethod.GET)
	public ModelAndView view(@PathVariable("id") String id) {
		id = StringUtils2.javaScriptEncode(id);
		return new ModelAndView("/frame/message/msg-announcement-view", "id",
				id);
	}

	/**
	 * 公告管理 获取指定授权对象树（目前公告管理只可以分配给机构、角色）
	 * @param objDefNo
	 * @param node
	 * @return
	 */
	@RequestMapping("/getAuthObjTree.*")
	@ResponseBody
	public List<CommonTreeNode> getAuthObjTree(String objDefNo,
			CommonTreeNode node) {
		List<CommonTreeNode> pageShowTree = new ArrayList<CommonTreeNode>();
		if (objDefNo == null || "".equals(objDefNo)) {
			return pageShowTree;
		}
		if (objDefNo != null && !"".equals(objDefNo)) {
			// 获取实现类
			List<String> beanNames = this.authBS.findAuthObjBeanNameByType(objDefNo);
			if (beanNames != null && beanNames.size() > 0) {
				// 存在至少一个授权对象实现类申明
				String beanName = beanNames.get(0);
				IAuthObject authObj = SpringContextHolder.getBean(beanName);
				if (authObj != null) {
					// pageShowTree = authObj.doGetAuthObjectInfo();
					pageShowTree = authObj.doGetAuthObjectInfoAsync(node);
					if (pageShowTree != null) {
						for (int i = 0; i < pageShowTree.size(); i++) {
							(pageShowTree.get(i)).setIcon(this.getRequest()
									.getContextPath()
									+ "/"
									+ (pageShowTree.get(i)).getIcon());
						}
					}
				}
			}
		}
		return pageShowTree;
	}
	
	/**
	 * 根据公告id得到该条公告的信息
	 * 
	 * @param id
	 *            公告的主键
	 * @return
	 */
	@RequestMapping(value = "/{id}", method = RequestMethod.GET)
	@ResponseBody
	public BioneMsgNoticeInfo show(@PathVariable("id") String id) {
		BioneMsgNoticeInfo  info = new BioneMsgNoticeInfo();
		if(!StringUtils.isEmpty(id)){
			info = frsInfoBs.getNotice(id);
		}
		return info;
		//return this.frsInfoBs.getEntityById(id);
	}

	@RequestMapping(value = "/auth/{id}")
	@ResponseBody
	public List<BioneMsgAuthObjRel> authShow(@PathVariable("id") String id) {
		List<BioneMsgAuthObjRel>  info = new ArrayList<BioneMsgAuthObjRel>();
		if(!StringUtils.isEmpty(id)){
			info = frsInfoBs.getNoticeList(id);
		}
		return info;
	//	return this.AuthRelBS.findEntityListByProperty("id.announcementId", id);
	}

	/**
	 * 用于保存添加或修改时的对象
	 */
	@RequestMapping(method = RequestMethod.POST)
	@Transactional
	public void create(BioneMsgNoticeInfo entity, String addAttachs,
			String delAttachs, String authItem) {
		BioneUser userObj = BioneSecurityUtils.getCurrentUserInfo();
		Timestamp now = new Timestamp(System.currentTimeMillis());
		String globeMsgId = "";
		if (entity != null) {
			globeMsgId = entity.getAnnouncementId();
			BioneMsgNoticeInfo eold = frsInfoBs.getEntityById(globeMsgId);
			if (eold == null) {
				globeMsgId = RandomUtils.uuid2();
				entity.setEffectiveDate(now);
				entity.setLastUpdateUser(userObj.getUserName());
				entity.setLogicSysNo(userObj.getCurrentLogicSysNo() == null ? ""
						: userObj.getCurrentLogicSysNo());
			} else {
				entity.setEffectiveDate(eold.getEffectiveDate());
				entity.setLastUpdateUser(eold.getLastUpdateUser());
				entity.setLogicSysNo(eold.getLogicSysNo());
				// entity.setAnnouncementDetail(eold.getAnnouncementDetail());
			}
			entity.setAnnouncementId(globeMsgId);
			entity.setAnnouncementSts(GlobalConstants4frame.MESSAGE_ANNOUNCEMENT_STATUS_DRAFT);
			entity.setLastUpdateTime(now);
			entity.setLastUpdateUser(userObj.getUserName());
			entity.setCreateUser(BioneSecurityUtils.getCurrentUserId());
			// =====================================================
			// 保存主体信息
			// =====================================================
			frsInfoBs.updateEntity(entity);
			// =====================================================
			// 保存新附件
			// =====================================================
			if (addAttachs == null || addAttachs.equals("")
					|| delAttachs == null || delAttachs.equals("")) {
				throw new NullPointerException("附件保存失败！");
			}
			// adds
			List<BioneMsgAttachInfo> adds = new ArrayList<BioneMsgAttachInfo>();
			List<BioneMsgAttachRel> rels = new ArrayList<BioneMsgAttachRel>();

			JSONArray addArray = JSON.parseArray(addAttachs);
			if (addArray != null && !addArray.isEmpty()) {
				for (int i = 0; i < addArray.size(); i ++) {
					BioneMsgAttachInfo attach = addArray.getObject(i, BioneMsgAttachInfo.class);
					// attach.setLastUpdateTime(now);
					// attach.setMsgId(globeMsgId);
					//attach.setRemark("fuefowejfoj");
					adds.add(attach);

					BioneMsgAttachRel rel = new BioneMsgAttachRel();
					BioneMsgAttachRelPK id = new BioneMsgAttachRelPK();
					id.setAttachId(attach.getAttachId());
					id.setMsgId(globeMsgId);
					rel.setId(id);
					rels.add(rel);
				}
			}
//			//add donglt
//			RptUserNotice user = new RptUserNotice();
//			user.setUserId(userObj.getUserId());
//			user.setIsRead("1");
//			user.setAnnouncementId(globeMsgId);
//			this.rptBS.saveEntity(user);
			this.attachRelBs.saveBatch(rels);
			this.msgAttachBS.saveBatch(adds);
			
			
			// =====================================================
			// 删除附件
			// =====================================================
			// deletes
			String[] deletes = new String[] {};
			JSONArray delArray = JSON.parseArray(delAttachs);
			if (delArray != null && !delArray.isEmpty()) {
				deletes = new String[delArray.size()];
				for (int i = 0; i < delArray.size(); i ++) {
					deletes[i] = delArray.getString(i);
				}
			}
			this.msgAttachBS.deleteBatch(deletes);
		}

		// 授权对象保存
		AuthRelBS.removeEntityByProperty("id.announcementId", globeMsgId);
		JSONArray ja = JSON.parseArray(authItem);
		List<BioneMsgAuthObjRel> rels = Lists.newArrayList();
		for (int i = 0; i < ja.size(); i ++) {
			JSONObject jo = ja.getJSONObject(i);
			if(null != jo.get("objId")){
				BioneMsgAuthObjRel rel = new BioneMsgAuthObjRel();
				BioneMsgAuthObjRelPK relPk = new BioneMsgAuthObjRelPK();
				relPk.setAnnouncementId(globeMsgId);
				relPk.setObjId(jo.getString("objId"));
				relPk.setLogicSysNo(BioneSecurityUtils.getCurrentUserInfo().getCurrentLogicSysNo());
				rel.setId(relPk);
				rel.setObjDefNo(jo.getString("objDefNo"));
				rels.add(rel);
			}
		}
		AuthRelBS.batchSave(rels);
	}

	/**
	 * 发布公告
	 */
	@RequestMapping("/{id}/publish")
	@ResponseBody
	public void publish(@PathVariable("id") String id) {

		this.infoBs.publish(id);
	}


	/**
	 * 取消发布公告
	 */
	@RequestMapping("/{id}/unPublish")
	@ResponseBody
	public void unPublish(@PathVariable("id") String id) {
		this.infoBs.unPublish(id);
	}
	
	/**
	 * 批量删除公告
	 */
	@RequestMapping(value = "/{id}", method = RequestMethod.POST)
	@ResponseBody
	@Transactional(readOnly = false)
	public void destroy(@PathVariable("id") String id) {
		String[] ids = StringUtils.split(id, ',');
		if (ids != null && ids.length > 0) {
			for (String tmpId : ids) {
				// delete message
				frsInfoBs.removeEntityById(tmpId);
				// delete attachments

				List<BioneMsgAttachRel> list = this.attachRelBs.findEntityListByProperty("id.msgId", tmpId);
/*				String attachId[] = new String[list.size()];
				for (int i = 0; i < list.size(); i++) {
					attachId[i] = new String(list.get(i).getId().getAttachId());
				}*/
				List<String> attachIdList = Lists.newArrayList();
				for (int i = 0; i < list.size(); i++) {
					attachIdList.add(list.get(i).getId().getAttachId());
				}
				msgAttachBS.deleteBatch(attachIdList);
				this.attachRelBs.removeEntityByProperty("id.msgId", tmpId);

			}
		}
	}
	
	/**
	 * 跳转到公告-消息 tab页
	 * 
	 * @return
	 */
	@RequestMapping("/showMsg")
	public ModelAndView showMsg() {
		return new ModelAndView("/frame/message/msg-display-index");
	}
}
