package com.yusys.bione.frame.validtype.web;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.yusys.bione.comp.entity.page.Pager;
import com.yusys.bione.comp.repository.jpa.SearchResult;
import com.yusys.bione.comp.utils.StringUtils2;
import com.yusys.bione.frame.auth.entity.BioneAuthResDef;
import com.yusys.bione.frame.validtype.entity.BioneValidTypeDef;
import com.yusys.bione.frame.validtype.service.ValidTypeDefBS;

/**
 * <pre>
 * Title:CRUD操作演示
 * Description: 完成用户信息表的CRUD操作 
 * </pre>
 * @author hubing1@yusys.com.cm
 * @version 1.00.00
 * <pre>
 * 修改记录
 *    修改后版本:     修改人：胡兵		  修改日期:     修改内容: 
 * </pre>
 */
@Controller
@RequestMapping("/bione/admin/validTypeDef")
public class ValidTypeDefController {
	@Autowired
	private ValidTypeDefBS validTypeDefBS;
	
	/**
	 * 首页
	 */
	@RequestMapping(method = RequestMethod.GET)
	public String index(){
		return "/frame/validType/valid-type-def-index";
	}
	/**
	 * grid
	 */
	@RequestMapping("/list.*")
	@ResponseBody
	public Map<String,Object> list(Pager pager){
		Map<String,Object> rMap = new HashMap<String,Object>();
		SearchResult<BioneAuthResDef> searchResult = this.validTypeDefBS.getValidTypeDefList(pager.getPageFirstIndex(),
				pager.getPagesize(), pager.getSortname(), pager.getSortorder(), pager.getSearchCondition());
		rMap.put("Rows", searchResult.getResult());
		rMap.put("Total", searchResult.getTotalCount());
		return rMap;
	}
	/**
	 * 新增
	 */
	@RequestMapping(value = "/new", method = RequestMethod.GET)
	public String editNew() {
		return "/frame/validType/valid-type-def-edit";
	}
	/**
	 * 修改
	 * @param id
	 * @return
	 */
	@RequestMapping(value = "/{id}/edit", method = RequestMethod.GET)
	public ModelAndView edit(@PathVariable("id") String id) {
		id = StringUtils2.javaScriptEncode(id);
		return new ModelAndView("/frame/validType/valid-type-def-edit", "id", id);
	}
	
	/**
	 * 根据ID，加载数据
	 */
	@RequestMapping(value = "/{id}", method = RequestMethod.GET)
	@ResponseBody
	public BioneValidTypeDef show(@PathVariable("id") String id) {
		BioneValidTypeDef model = this.validTypeDefBS.getEntityById(id);
		return model;
	}
	/**
	 * 表单验证中的后台验证，验证模块标识是否已存在
	 */
	@RequestMapping(value = "objDefNoValid")
	@ResponseBody
	public boolean resDefNoValid(String objDefNo) {
		BioneValidTypeDef model = this.validTypeDefBS.findUniqueEntityByProperty("objDefNo", objDefNo);
		if (model != null)
			return false;
		else
			return true;
	}
	
	/**
	 * 用于添加，或修改时的保存对象
	 */
	@RequestMapping(method = RequestMethod.POST)
	public void create(BioneValidTypeDef model) {
		this.validTypeDefBS.saveOrUpdateEntity(model);
	}
	/**
	 * 执行删除操作，可进行指删除
	 */
	@RequestMapping(value = "/{ids}", method = RequestMethod.POST)
	@ResponseBody
	public void destroy(@PathVariable("ids") String ids) {
		this.validTypeDefBS.delValidTypeDef(ids);
	}
}
