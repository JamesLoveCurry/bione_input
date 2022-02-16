package com.yusys.bione.frame.message.web;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.yusys.bione.comp.entity.page.Pager;
import com.yusys.bione.comp.repository.jpa.SearchResult;
import com.yusys.bione.comp.utils.StringUtils2;
import com.yusys.bione.frame.base.web.BaseController;
import com.yusys.bione.frame.message.entity.BioneMsgLog;
import com.yusys.bione.frame.message.entity.BioneMsgUserState;
import com.yusys.bione.frame.message.entity.BioneMsgUserStatePK;
import com.yusys.bione.frame.message.service.MsgNoticeLogBS;
import com.yusys.bione.frame.message.service.MsgUserStateBS;
import com.yusys.bione.frame.message.web.vo.BioneMsgLogVO;
import com.yusys.bione.frame.message.web.vo.MsgLogVO;
import com.yusys.bione.frame.security.BioneSecurityUtils;
import com.yusys.bione.frame.user.entity.BioneUserInfo;

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
@RequestMapping("/bione/msgNoticeLog")
public class MsgNoticeLogController extends BaseController {

	@Autowired
	private MsgNoticeLogBS logBs;
	
	@Autowired
	private MsgUserStateBS stateBs;
	
	private AtomicLong count = new AtomicLong(0);
	
	
	/**
	 * 跳转到公告-消息 tab页中的我的消息的页面
	 * @return
	 */
	@RequestMapping(method = RequestMethod.GET)
	public ModelAndView  announceInfo(){
		ModelMap mm = new ModelMap();
		BioneMsgUserStatePK pk = new BioneMsgUserStatePK();
		pk.setLogicSysNo(BioneSecurityUtils.getCurrentUserInfo()
				.getCurrentLogicSysNo());
		pk.setUserId(BioneSecurityUtils.getCurrentUserId());
		BioneMsgUserState state = this.stateBs.getEntityById(pk);
		String msgSts = "0";
		if(state != null){
			msgSts = state.getMsgSts();
		}
		mm.put("msgNum", this.stateBs.getUnReadMsgCount());
		mm.put("msgSts", StringUtils2.javaScriptEncode(msgSts));
		return new ModelAndView("/frame/message/msg-notice-log-viewMore",mm);
	}
	
	
	/**
	 * 展示所有的消息
	 * @param pager
	 * @return
	 */
	@RequestMapping(value = "/list.*", method = RequestMethod.POST)
	@ResponseBody
	public Map<String, Object> getAll(Pager pager) {
		SearchResult<BioneMsgLogVO> result = this.logBs.getAll(pager.getPageFirstIndex(), pager.getPagesize(), 
				pager.getSortname(), pager.getSortorder(), pager.getSearchCondition());
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("Total", result.getTotalCount());
		map.put("Rows", result.getResult());
		return map;
	}


	/**
	 * 批量删除消息
	 * @param ids
	 */
	@RequestMapping(method = RequestMethod.POST)
	@ResponseBody
	public void delete(String ids) {
		String[] id = StringUtils.split(ids, ',');
		for (int i = 0; i < id.length; i++) {
			this.logBs.removeEntityById(id[i]);
		}
	}

	/**
	 * 批量删除消息
	 * @param ids
	 */
	@RequestMapping(value = "/viewedDelete", method = RequestMethod.POST)
	@ResponseBody
	public void viewedDelete() {
		this.logBs.viewedDelete();
	}
	/**
	 * 查看消息信息
	 * @param id
	 * @return
	 */
	@RequestMapping(value = "/{id}/view", method = RequestMethod.GET)
	public ModelAndView view(@PathVariable("id") String id) {
		ModelMap mm = new ModelMap();
		mm.put("msgState", StringUtils2.javaScriptEncode(this.logBs.resetState(id)));
		mm.put("msgNum", this.stateBs.getUnReadMsgCount());
		mm.put("id", StringUtils2.javaScriptEncode(id));
		return new ModelAndView("/frame/message/msg-notice-log-view", mm);
	}
	
	/**
	 * 根据消息ID得到消息实体
	 * @param id
	 * @return
	 */
	@RequestMapping(value = "/getInfo.*")
	@ResponseBody
	public BioneMsgLog getInfo(String id){
		BioneMsgLog msglog = logBs.getEntityById(id);
		if(null != msglog){
			BioneUserInfo user = logBs.getEntityById(BioneUserInfo.class, msglog.getSendUser());
			msglog.setSendUser(user.getUserName());
		}
		return msglog;
	}
	

	/**
	 * 按一定时间查看该用户是否有新消息
	 * @return
	 */
	@RequestMapping("/showMsgState")
	@ResponseBody
	public Map<String, Object> showMsgState(){
		count.incrementAndGet();
		Map<String, Object> map = this.logBs.showMsgState();
		if(map.get("msgSts").equals(true)){
			List<MsgLogVO> msg = this.logBs.getMsgs();
			map.put("msgs", msg);
		}
		map.put("count", count.decrementAndGet());
		return map;
	}
	
	/**
	 * 全部消息标记为已读
	 */
	@RequestMapping(value = "/changeViewSts", method = RequestMethod.GET)
	@ResponseBody
	public void changeViewSts(){
		this.logBs.changeViewSts();
	}
	
	/**
	 * 得到消息类型的名称
	 * @param id
	 * @return
	 */
	@RequestMapping(value = "/getMsgTypeName", method = RequestMethod.GET)
	@ResponseBody
	public String getMsgTypeName(String id){
		return this.logBs.getMsgTypeName(id);
	}
	/**
	 * 登陆系统初始化是否有新消息
	 * @param id
	 * @return
	 */
	@RequestMapping(value = "/initMsgTip.*")
	@ResponseBody
	public Map<String,Object> initMsgTip(String id){
		return this.stateBs.showMsgState();
	}
}
