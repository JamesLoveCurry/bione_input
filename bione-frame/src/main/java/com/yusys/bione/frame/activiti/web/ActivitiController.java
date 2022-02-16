package com.yusys.bione.frame.activiti.web;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.activiti.engine.repository.Model;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.google.common.collect.Maps;
import com.yusys.bione.comp.entity.page.Pager;
import com.yusys.bione.comp.repository.jpa.SearchResult;
import com.yusys.bione.frame.activiti.entity.ActIdGroup;
import com.yusys.bione.frame.activiti.entity.ActReModel;
import com.yusys.bione.frame.activiti.service.ActivitiBS;
import com.yusys.bione.frame.base.web.BaseController;

@Controller
@RequestMapping("/frs/activiti")
public class ActivitiController extends BaseController {

	@Autowired
	private ActivitiBS activitiBS;

	@RequestMapping(method = RequestMethod.GET)
	public String index() {
		return "/frame/activiti/activiti-index";
	}

	@RequestMapping(value = "/new", method = RequestMethod.GET)
	public String editNew() {
		return "/frame/activiti/activiti-new";
	}

	/**
	 * 
	 * @Title: list
	 * @Description: 加载流程模型列表
	 * @param rf
	 * @return Map<String,Object>
	 * @throws
	 */
	@RequestMapping("/list.*")
	@ResponseBody
	public Map<String, Object> list(Pager rf) {
		SearchResult<ActReModel> searchResult = activitiBS.getModuleList(
				rf.getPageFirstIndex(), rf.getPagesize(), rf.getSortname(),
				rf.getSortorder(), rf.getSearchCondition());
		Map<String, Object> moduleMap = Maps.newHashMap();
		moduleMap.put("Rows", searchResult.getResult());
		moduleMap.put("Total", searchResult.getTotalCount());
		return moduleMap;
	}

	/**
	 * @throws IOException 
	 * 
	 * @Title: create
	 * @Description: 创建流程图新模型
	 * @param request
	 * @param response
	 * @throws Exception
	 *             void
	 * @throws
	 */
	@RequestMapping(value = "/create", method = RequestMethod.POST)
	@ResponseBody
	public void create(HttpServletRequest request, HttpServletResponse response) throws IOException{
		String name = request.getParameter("name");
		String description = request.getParameter("description");
		Model modelData = activitiBS.createModel(name, description);
		response.sendRedirect(request.getContextPath()
				+ "/process-edit/modeler.html?modelId=" + modelData.getId());
	}

	/**
	 * 
	 * @Title: deploy
	 * @Description: 根据Model部署流程
	 * @param Id
	 * @return
	 * @throws Exception
	 *             String
	 * @throws
	 */
	@RequestMapping(value = "/deploy/{id}", method = RequestMethod.POST)
	@ResponseBody
	public void deploy(@PathVariable("id") String Id) throws Exception {
		String[] modleID = StringUtils.split(Id, ',');
		activitiBS.deployModel(modleID);
	}

	/**
	 * 
	 * @Title: delete
	 * @Description: 删除流程模型以及部署的流程定义
	 * @param Id
	 * @return
	 * @throws Exception
	 *             String
	 * @throws
	 */
	@RequestMapping(value = "/delete/{id}", method = RequestMethod.POST)
	@ResponseBody
	public void delete(@PathVariable("id") String Id) throws Exception {
		String[] modleID = StringUtils.split(Id, ',');
		activitiBS.deleteModel(modleID);
	}

	/**
	 * 
	 * @Title: validate
	 * @Description: 增加已经发起任务的流程不能再次部署的校验
	 * @param Id
	 * @return
	 * @throws Exception
	 *             String
	 * @throws
	 */
	@RequestMapping(value = "/validate/{id}", method = RequestMethod.POST)
	@ResponseBody
	public String validate(@PathVariable("id") String Id) throws Exception {
		String[] modleID = StringUtils.split(Id, ',');
		String validate = activitiBS.validate(modleID);
		return validate;
	}

	/**
	 * @throws UnsupportedEncodingException
	 * 
	 * @Title: getRoleName
	 * @Description: 获取系统角色列表
	 * @return String
	 * @throws
	 */
	@RequestMapping(value = "/getRoleName", method = RequestMethod.GET)
	@ResponseBody
	public String getRoleName(HttpServletResponse response)
			throws UnsupportedEncodingException {
		StringBuilder stringBuilder = new StringBuilder();
		List<ActIdGroup> roleList = activitiBS.getRoleName();
		for (int i = 0; i < roleList.size(); i++) {
			stringBuilder.append(String.valueOf(roleList.get(i)).trim() + ",");
		}
		String RoleName = stringBuilder.toString();
		RoleName = RoleName.substring(0, RoleName.lastIndexOf(","));
		// 手动设置响应头为UTF-8后浏览器不生效，于是转为编码解码方式避免中文乱码
		String string = URLEncoder.encode(RoleName, "UTF-8");
		return string;
	}
}
