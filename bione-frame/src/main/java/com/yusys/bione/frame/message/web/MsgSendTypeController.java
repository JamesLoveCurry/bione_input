package com.yusys.bione.frame.message.web;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.yusys.bione.comp.entity.page.Pager;
import com.yusys.bione.comp.repository.jpa.SearchResult;
import com.yusys.bione.comp.utils.RandomUtils;
import com.yusys.bione.comp.utils.StringUtils2;
import com.yusys.bione.frame.base.web.BaseController;
import com.yusys.bione.frame.message.entity.BioneMsgSendType;
import com.yusys.bione.frame.message.messager.IMessager;
import com.yusys.bione.frame.message.service.MsgSendTypeBS;

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
@RequestMapping("/bione/msgSendType")
public class MsgSendTypeController extends BaseController {

	@Autowired
	private MsgSendTypeBS typeBs;

	/**
	 * 分页显示所有的消息类型
	 * @param pager
	 * @return
	 */
	@RequestMapping(value = "/list.*", method = RequestMethod.POST)
	@ResponseBody
	public Map<String, Object> getAll(Pager pager) {
		SearchResult<BioneMsgSendType> result = typeBs.getAll(
				pager.getPageFirstIndex(), pager.getPagesize(),
				pager.getSortname(), pager.getSortorder(),
				pager.getSearchCondition());

		Map<String, Object> map = new HashMap<String, Object>();
		map.put("Rows", result.getResult());
		map.put("Total", result.getTotalCount());
		return map;
	}


	/**
	 * 显示所有的消息类型
	 * @return
	 */
	@RequestMapping(value = "/listAll.*", method = RequestMethod.POST)
	@ResponseBody
	public Map<String, Object> listAll() {
		Map<String, Object> map = new HashMap<String, Object>();
		List<BioneMsgSendType> result = typeBs.listAll();
		map.put("Rows", result);
		map.put("Total", result.size());
		return map;
	}
	
	/**
	 * 跳转到编辑消息页面
	 * @param id
	 * @return
	 */
	@RequestMapping(value = "/edit", method = RequestMethod.GET)
	public ModelAndView edit(String id) {
		id = StringUtils2.javaScriptEncode(id);
		return new ModelAndView("/frame/message/msg-send-type-edit", "id", id);
	}

	/**
	 * 保存消息
	 * @param entity
	 */
	@RequestMapping(method = RequestMethod.POST)
	public void create(BioneMsgSendType entity) {
		if (entity != null) {
			if (entity.getSendTypeNo() == null
					|| entity.getSendTypeNo().equals("")) {
				entity.setSendTypeNo(RandomUtils.uuid2());

			}
			this.typeBs.saveOrUpdateEntity(entity);
		}
	}

	/**
	 * 跳转到首页
	 * @return
	 */
	@RequestMapping(method = RequestMethod.GET)
	public ModelAndView index() {
		return new ModelAndView("/frame/message/msg-send-type-index");
	}
	
	/**
	 * 根据ID得到消息类型
	 * @param id
	 * @return
	 */
	@RequestMapping(value = "/getInfo.*", method = RequestMethod.GET)
	@ResponseBody
	public BioneMsgSendType getInfo(String id){
		return this.typeBs.getEntityById(id);
	}
	
	/**
	 * 批量删除消息类型
	 * @param ids
	 */
	@RequestMapping(value = "/deMsgSend",method = RequestMethod.POST)
	@ResponseBody
	public void delete(String ids){
		String[] id = StringUtils.split(ids, ',');
		for(int i=0;i<id.length;i++){
			this.typeBs.removeEntityById(id[i]);
		}
	}
	
	/**
	 * 新增时，检查消息名称是否存在
	 * @param id
	 * @param sendTypeName
	 * @return
	 */
	@RequestMapping(value = "/checkTypeName", method = RequestMethod.GET)
	@ResponseBody
	public boolean checkTypeName(String id, String sendTypeName){
		return this.typeBs.checkTypeName(id, sendTypeName);
	}
	
	@RequestMapping(value = "/checkTypeNo", method = RequestMethod.GET)
	@ResponseBody
	public boolean checkTypeNo(String sendTypeNo){
		return this.typeBs.checkTypeNo(sendTypeNo);
	}
	
	
	/**
	 * 新增时，检查消息名称是否存在
	 * @param id
	 * @param sendTypeName
	 * @return
	 */
	@RequestMapping(value = "/checkClassName")
	@ResponseBody
	public boolean checkClassName(String beanName) {
		try {
			Class<?> clazz = Class.forName(beanName);
			for (Class<?> c : clazz.getInterfaces()) {
				if (c == IMessager.class) {
					return true;
				}
			}
			return false;
		} catch (ClassNotFoundException e) {
			return false;
		}
	}

}
