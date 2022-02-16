package com.yusys.bione.frame.message.web;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.google.common.collect.Maps;
import com.yusys.bione.comp.entity.page.Pager;
import com.yusys.bione.comp.repository.jpa.SearchResult;
import com.yusys.bione.frame.auth.service.AuthBS;
import com.yusys.bione.frame.base.common.GlobalConstants4frame;
import com.yusys.bione.frame.base.web.BaseController;
import com.yusys.bione.frame.message.entity.BioneMsgNoticeInfo;
import com.yusys.bione.frame.message.service.MsgNoticeInfoBS;
import com.yusys.bione.frame.security.BioneSecurityUtils;

/**
 * <pre>
 * Title: 公告查看
 * Description:
 * </pre>
 * 
 * @author kanglg@yuchengtech.com
 * @version 1.00.00
 * 
 *          <pre>
 * 修改记录
 *    修改后版本:     修改人：  修改日期:     修改内容:
 * </pre>
 */
@Controller
@RequestMapping("/bione/msg/bulletin")
public class MsgNoticeViewController extends BaseController {

	@Autowired
	private MsgNoticeInfoBS infoBS;

	@Autowired
	private AuthBS authBS;

	@RequestMapping(method = RequestMethod.GET)
	public String index() {
		return "/frame/message/msg-bulletin-index";
	}

	@RequestMapping("list.json")
	@ResponseBody
	public Map<String, Object> list(Pager pager,String maxRows) {
		SearchResult<BioneMsgNoticeInfo> searchResult = infoBS.getNoticeInfo(pager, maxRows);
		Map<String, Object> msgAnnoMap = Maps.newHashMap();
		msgAnnoMap.put("Rows", searchResult.getResult());
		msgAnnoMap.put("Total", searchResult.getTotalCount());
		return msgAnnoMap;
	}

	/** Private method begin **/

	/**
	 * 获取当前用户权限内的公告id （目前，只有机构和角色两个授权对象。两者之间不同于平台统一权限，此处是权限是交集处理，
	 * 即：机构A-公告1,2；角色1-公告2,3,；则机构A的角色1只能看见公告2。此处和平台权限不一致，需要注意）
	 * 
	 * @return
	 */
	@SuppressWarnings({ "unchecked", "unused" })
	private List<String> getNoticesByUser() {
		List<String> noticeIds = new ArrayList<String>();
		String logicSysNo = BioneSecurityUtils.getCurrentUserInfo()
				.getCurrentLogicSysNo();
		// 当前用户对应的授权对象集合
		Map<String, List<String>> userAuthObjMap = BioneSecurityUtils
				.getCurrentUserInfo().getAuthObjMap();
		List<String> orgNoticeIds = this.authBS.findAuthMsgIdListByType(
				logicSysNo, GlobalConstants4frame.AUTH_OBJ_DEF_ID_ORG,
				userAuthObjMap.get(GlobalConstants4frame.AUTH_OBJ_DEF_ID_ORG));
		List<String> roleNoticeIds = this.authBS.findAuthMsgIdListByType(
				logicSysNo, GlobalConstants4frame.AUTH_OBJ_DEF_ID_ROLE,
				userAuthObjMap.get(GlobalConstants4frame.AUTH_OBJ_DEF_ID_ROLE));
		// 分析权限内公告哪些是同时分配有机构和角色权限的
		List<String> allNoticeIds = (List<String>) CollectionUtils.union(
				orgNoticeIds, roleNoticeIds);
		List<String> needAnalyseIds = this.infoBS
				.getAuthNoticeIds(allNoticeIds);
		// 分析机构和角色的公告权限
		if (orgNoticeIds.size() <= 0 && roleNoticeIds.size() <= 0) {
			return noticeIds;
		} else if (orgNoticeIds.size() > 0 && roleNoticeIds.size() > 0) {
			// 取机构权限和角色权限的权限交集
			for (String orgNoticeId : orgNoticeIds) {
				if (noticeIds.contains(orgNoticeId)) {
					continue;
				}
				if (needAnalyseIds.contains(orgNoticeId)) {
					if (roleNoticeIds.contains(orgNoticeId)) {
						noticeIds.add(orgNoticeId);
					}
				} else {
					noticeIds.add(orgNoticeId);
				}
			}
		} else {
			List<String> tmps = orgNoticeIds.size() <= 0 ? roleNoticeIds
					: orgNoticeIds;
			for (String tmp : tmps) {
				if (!needAnalyseIds.contains(tmp) && !noticeIds.contains(tmp)) {
					noticeIds.add(tmp);
				}
			}
		}
		return noticeIds;
	}

	/** Private method end **/
}
