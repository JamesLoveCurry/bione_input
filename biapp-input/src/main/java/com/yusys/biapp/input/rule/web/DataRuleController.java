package com.yusys.biapp.input.rule.web;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.yusys.biapp.input.dict.utils.UdipConstants;
import com.yusys.biapp.input.rule.entity.RptInputListDataRuleInfo;
import com.yusys.biapp.input.rule.entity.RptInputListRuleItemInfo;
import com.yusys.biapp.input.rule.service.DataRuleBS;
import com.yusys.biapp.input.rule.service.RuleItemBS;
import com.yusys.biapp.input.template.entity.RptInputLstTempleRule;
import com.yusys.biapp.input.template.service.TempleRuleInfoBS;
import com.yusys.biapp.input.utils.DateUtils;
import com.yusys.biapp.input.utils.SysFunction;
import com.yusys.bione.comp.entity.page.Pager;
import com.yusys.bione.comp.repository.jpa.SearchResult;
import com.yusys.bione.comp.utils.RandomUtils;
import com.yusys.bione.comp.utils.StringUtils2;
import com.yusys.bione.frame.base.web.BaseController;
import com.yusys.bione.frame.security.BioneSecurityUtils;
import com.yusys.bione.frame.security.BioneUser;

@Controller
@RequestMapping("/rpt/input/rule")
public class DataRuleController extends BaseController {
//	@Autowired
//	private LogBS logBS;
	@Autowired
	private DataRuleBS dataRulesBS;
	@Autowired
	private TempleRuleInfoBS templeRuleBS;
	@Autowired
	private RuleItemBS ruleItemBS;
	
	// 跳转欢迎页面
	@RequestMapping(method = RequestMethod.GET)
	public String welcome() {
		return "/input/rule/data-rule-index";
	}

	@RequestMapping("/getSysParams")
	@ResponseBody
	public List<String> getSysParams() {
		return SysFunction.sysParams;
	}

	@RequestMapping("/dataRulesCombox")
	@ResponseBody
	public List<Map<String, String>> dataRulesCombox() {
		return UdipConstants.dataRulesList;
	}

	@RequestMapping("/dataRulesFuncCombox")
	@ResponseBody
	public List<Map<String, String>> dataRulesFuncCombox() {
		return UdipConstants.dataRulesFuncList;
	}

	@RequestMapping("/dataRulesFuncCombox2")
	@ResponseBody
	public List<Map<String, String>> dataRulesFuncCombox2() {
		return UdipConstants.dataRulesFuncList2;
	}

	/**
	 * 获取用于加载grid的数据
	 */
	@RequestMapping("/list.*")
	@ResponseBody
	public Map<String, Object> list(Pager rf, String templeId,String catalogId) {
		if(rf.getPagesize()==2)
			rf.setPage(-1);
		SearchResult<RptInputListDataRuleInfo> searchResult = this.dataRulesBS.getRuleForTemplelist(rf.getPageFirstIndex(), rf.getPagesize(), rf.getSortname(), rf.getSortorder(), rf.getSearchCondition(), templeId,catalogId);
		Map<String, Object> objDefMap = Maps.newHashMap();
		objDefMap.put("Rows", searchResult.getResult());
		objDefMap.put("Total", searchResult.getTotalCount());
		return objDefMap;
	}
	/**
	 * 补录规则添加
	 */
	@RequestMapping("/rule")
	public ModelAndView rule(String id) {
		ModelMap mm = new ModelMap();
		mm.addAttribute("id", StringUtils2.javaScriptEncode(id));
		return new ModelAndView("/input/rule/data-rule", mm);
	}
	
	/**
	 * 规则1:数据值范围界面
	 * @return
	 */
	@RequestMapping("/rule1")
	public ModelAndView rule1(String id, String ruleId,String lookType) {
		ModelMap mm = new ModelMap();
		mm.addAttribute("ruleId", StringUtils2.javaScriptEncode(ruleId));
		mm.addAttribute("id", StringUtils2.javaScriptEncode(id));
		mm.addAttribute("lookType", StringUtils2.javaScriptEncode(lookType));
		return new ModelAndView("/input/rule/data-rule1", mm);
	}

	/**
	 * 规则1保存操作
	 * @param udipDataRules
	 * @param templeId
	 * @param paramStr
	 * @return
	 */
	@RequestMapping(value = "/rule1-save", method = RequestMethod.POST)
	@ResponseBody
	public Map<String,Object> rule1Save(RptInputListDataRuleInfo udipDataRules, String templeId, String value2, String value4) {
		// String saveMark = "03"; // 修改
		Map<String,Object> map = new HashMap<String, Object>();
		if (StringUtils.isNotBlank(udipDataRules.getRuleId())) {
			udipDataRules.setRuleId(udipDataRules.getRuleId());
		} else {
			udipDataRules.setRuleId(RandomUtils.uuid2());
			// saveMark = "01";//新增
			RptInputLstTempleRule templeRules = new RptInputLstTempleRule();
			templeRules.setId(RandomUtils.uuid2());
			templeRules.setRuleId(udipDataRules.getRuleId());
			templeRules.setTempleId(templeId);
			templeRuleBS.saveEntity(templeRules);
		}
		if (StringUtils.isNotBlank(udipDataRules.getMinVal())) {
			if (value2.equals("{")) {
				udipDataRules.setMinVal("{" + udipDataRules.getMinVal() + "}");
			} else if (value2.equals("[")) {
				udipDataRules.setMinVal("[" + udipDataRules.getMinVal() + "]");
			}
		}
		if (StringUtils.isNotBlank(udipDataRules.getMaxVal())) {
			if (value4.equals("{")) {
				udipDataRules.setMaxVal("{" + udipDataRules.getMaxVal() + "}");
			} else if (value4.equals("[")) {
				udipDataRules.setMaxVal("[" + udipDataRules.getMaxVal() + "]");
			}
		}
		if (StringUtils.isBlank(udipDataRules.getValueSet()) || udipDataRules.getValueSet().equals("例如：1,2,3")) {
			udipDataRules.setValueSet("");
		}
		
		udipDataRules.setRuleType("1");
		udipDataRules.setCreateDate(DateUtils.getYYYY_MM_DD_HH_mm_ss());
		BioneUser user = BioneSecurityUtils.getCurrentUserInfo();
		udipDataRules.setCreateUser(user.getLoginName());
		udipDataRules.setLogicSysNo(user.getCurrentLogicSysNo());
		/*********添加日志记录***********/
//		StringBuilder buff = new StringBuilder();
//		buff.append("用户[").append(user.getLoginName()).append("]").append(saveMark.equals("01")?"新增":"修改").append("了一个数据值范围的补录规则,规则的名称为:")
//		.append(udipDataRules.getRuleNm());
//		dataRulesBS.saveLog(saveMark, "补录规则维护-数据值范围  ", buff.toString(), user.getUserId(), user.getLoginName());
		dataRulesBS.saveOrUpdateEntity(udipDataRules);
//		logBS.addLog(this.getRequest().getRemoteAddr(), user.getCurrentLogicSysNo(), user.getUserId(), "保存数据值范围的补录规则【" + udipDataRules.getRuleName() + "】");
		map.put("msg", "success");
		return map;
	}

	/**
	 * 规则2:正则表达式界面
	 * @return
	 */
	@RequestMapping("/rule2")
	public ModelAndView rule2(String id, String ruleId,String lookType) {
		ModelMap mm = new ModelMap();
		mm.addAttribute("ruleId", StringUtils2.javaScriptEncode(ruleId));
		mm.addAttribute("id", StringUtils2.javaScriptEncode(id));
		mm.addAttribute("lookType", StringUtils2.javaScriptEncode(lookType));
		return new ModelAndView("/input/rule/data-rule2", mm);
	}
	/**
	 * 规则2保存操作
	 * @param datarule
	 * @param templeId
	 * @param paramStr
	 * @return
	 */
	@RequestMapping(value = "/rule2-save", method = RequestMethod.POST)
	@ResponseBody
	public Map<String,Object> rule2Save(RptInputListDataRuleInfo datarule, String templeId) {
		// String saveMark = "03";//  修改
		Map<String,Object> map = new HashMap<String, Object>();
		if (StringUtils.isNotBlank(datarule.getRuleId())) {
			datarule.setRuleId(datarule.getRuleId());
		} else {
			datarule.setRuleId(RandomUtils.uuid2());
			// saveMark = "01"; // 新增
			RptInputLstTempleRule templeRules = new RptInputLstTempleRule();
			templeRules.setId(RandomUtils.uuid2());
			templeRules.setRuleId(datarule.getRuleId());
			templeRules.setTempleId(templeId);
			templeRuleBS.saveEntity(templeRules);
		}
		datarule.setRuleType("2");
		datarule.setCreateDate(DateUtils.getYYYY_MM_DD_HH_mm_ss());
		BioneUser user = BioneSecurityUtils.getCurrentUserInfo();
		datarule.setCreateUser(user.getLoginName());
		datarule.setLogicSysNo(user.getCurrentLogicSysNo());
		/*********添加日志记录***********/
//		StringBuilder buff = new StringBuilder();
//		RptInputLstTempleInfo tmpInfo = templeBS.getEntityById(templeId);
//		buff.append("用户[").append(user.getLoginName()).append("]").append(saveMark.equals("01")?"新增":"修改").append("了一个正则表达式的补录规则,规则的名称为:")
//		.append(datarule.getRuleNm());
//		dataRulesBS.saveLog(saveMark, "补录规则维护-正则表达式  ", buff.toString(), user.getUserId(), user.getLoginName());
		dataRulesBS.saveOrUpdateEntity(datarule);
		map.put("msg", "success");
		return map;
//		logBS.addLog(this.getRequest().getRemoteAddr(), user.getCurrentLogicSysNo(), user.getUserId(), "保存正则表达式的补录规则【" + datarule.getRuleName() + "】");
	}

	/**
	 * 规则3:表内横向校验界面
	 * @return
	 */
	@RequestMapping("/rule3")
	public ModelAndView rule3(String id, String ruleId,String lookType) {
		ModelMap mm = new ModelMap();
		mm.addAttribute("ruleId", StringUtils2.javaScriptEncode(ruleId));
		mm.addAttribute("id", StringUtils2.javaScriptEncode(id));
		mm.addAttribute("lookType", StringUtils2.javaScriptEncode(lookType));
		return new ModelAndView("/input/rule/data-rule3", mm);
	}

	/**
	 * 规则3保存操作
	 * @param dataRule
	 * @param templeId
	 * @param paramStr
	 * @return
	 */
	@RequestMapping("/rule3-save")
	public Map<String,Object> rule3Save(RptInputListDataRuleInfo dataRule, String templeId, String paramStr) {
		// 保存UDIP_DATA_RULES表
		// String saveMark = "03";// 修改
		Map<String,Object> map = new HashMap<String, Object>();
		if (StringUtils.isNotBlank(dataRule.getRuleId())) {
			dataRule.setRuleId(dataRule.getRuleId());
		} else {
			dataRule.setRuleId(RandomUtils.uuid2());
			// saveMark = "01";// 新增
			// 保存templeRule表
			RptInputLstTempleRule templeRules = new RptInputLstTempleRule();
			templeRules.setId(RandomUtils.uuid2());
			templeRules.setRuleId(dataRule.getRuleId());
			templeRules.setTempleId(templeId);
			templeRuleBS.saveEntity(templeRules);
		}

		dataRule.setRuleType("3");
		dataRule.setCreateDate(DateUtils.getYYYY_MM_DD_HH_mm_ss());
//		logBS.addLog(this.getRequest().getRemoteAddr(), BioneSecurityUtils.getCurrentUserInfo().getCurrentLogicSysNo(), BioneSecurityUtils.getCurrentUserInfo().getUserId(), "保存表内横向校验的补录规则【" + udipDataRules.getRuleName() + "】");
		BioneUser user = BioneSecurityUtils.getCurrentUserInfo();
		dataRule.setCreateUser(user.getLoginName());
		dataRule.setLogicSysNo(user.getCurrentLogicSysNo());
		/*********添加日志记录***********/
//		StringBuilder buff = new StringBuilder();
//		RptInputLstTempleInfo tmpInfo = templeBS.getEntityById(templeId);
//		buff.append("用户[").append(user.getLoginName()).append("]对模板").append(saveMark.equals("01")?"新增":"修改").append("了一个表内横向校验的补录规则,规则的名称为:")
//		.append(dataRule.getRuleNm());
//		dataRulesBS.saveLog(saveMark, "补录规则维护-表内横向校验  ", buff.toString(), user.getUserId(), user.getLoginName());
		
		dataRulesBS.saveOrUpdateEntity(dataRule);
//		logBS.addLog(this.getRequest().getRemoteAddr(), user.getCurrentLogicSysNo(), user.getUserId(), "保存正则表达式的补录规则【" + dataRule.getRuleName() + "】");

		// 保存UDIP_RULE_ITEMS表
		if (StringUtils.isNotBlank(paramStr)) {
			// 保存字段时先删除该模板中的字段
			this.ruleItemBS.removeEntityByProperty("ruleId", dataRule.getRuleId());
			String[] templeColumnsList = paramStr.split(";;");
			for (int i = 0; i < templeColumnsList.length; i++) {
				String[] templeColumns = templeColumnsList[i].split("@#@", 7);
				RptInputListRuleItemInfo ruleItem = new RptInputListRuleItemInfo();
				ruleItem.setRuleId(dataRule.getRuleId());
				if (StringUtils.isBlank(templeColumns[3])) {
					ruleItem.setItemId(RandomUtils.uuid2());
					ruleItem.setCalCode(templeColumns[0]);
					ruleItem.setFieldName(templeColumns[1]);
					ruleItem.setAggregateFunc(templeColumns[2]);
					ruleItem.setLeftOrRight(templeColumns[4]);
					ruleItem.setDsId(templeColumns[5]);
					ruleItem.setTableEnName(templeColumns[6]);
				} else {
					ruleItem.setItemId(RandomUtils.uuid2());
					ruleItem.setCalCode(templeColumns[0]);
					ruleItem.setFieldName(templeColumns[1]);
					ruleItem.setAggregateFunc(templeColumns[2]);
					ruleItem.setFilterCondition(templeColumns[3]);
					ruleItem.setLeftOrRight(templeColumns[4]);
					ruleItem.setDsId(templeColumns[5]);
					ruleItem.setTableEnName(templeColumns[6]);
				}
				this.ruleItemBS.saveEntity(ruleItem);
			}
		}
		map.put("msg", "success");
		return map;
	}

	/**
	 * 规则4:表内纵向校验界面
	 * @return
	 */
	@RequestMapping("/rule4")
	public ModelAndView rule4(String id, String ruleId,String lookType) {
		ModelMap mm = new ModelMap();
		mm.addAttribute("ruleId", StringUtils2.javaScriptEncode(ruleId));
		mm.addAttribute("id", StringUtils2.javaScriptEncode(id));
		mm.addAttribute("lookType", StringUtils2.javaScriptEncode(lookType));
		return new ModelAndView("/input/rule/data-rule4", mm);
	}

	/**
	 * 规则4保存操作
	 * @param dataRule
	 * @param templeId
	 * @param paramStr
	 * @return
	 */
	@RequestMapping("/rule4-save")
	public Map<String,Object> rule4Save(RptInputListDataRuleInfo dataRule, String templeId, String paramStr) {
		// String saveMark = "03"; // 修改
		Map<String,Object> map = new HashMap<String, Object>();
		// 保存UDIP_DATA_RULES表
		if (StringUtils.isNotBlank(dataRule.getRuleId())) {
			dataRule.setRuleId(dataRule.getRuleId());
		} else {
			dataRule.setRuleId(RandomUtils.uuid2());
			// saveMark = "01"; // 新增
			// 保存templeRule表
			RptInputLstTempleRule templeRules = new RptInputLstTempleRule();
//			templeRules.setIndexId(RandomUtils.uuid2());
			templeRules.setId(RandomUtils.uuid2());
			templeRules.setRuleId(dataRule.getRuleId());
			templeRules.setTempleId(templeId);
			templeRuleBS.saveEntity(templeRules);

		}

		dataRule.setRuleType("4");
		dataRule.setCreateDate(DateUtils.getYYYY_MM_DD_HH_mm_ss());
//		dataRule.setCreator(BioneSecurityUtils.getCurrentUserInfo().getLoginName());
//		dataRule.setLogicSysNo(BioneSecurityUtils.getCurrentUserInfo().getCurrentLogicSysNo());
//		dataRulesBS.saveOrUpdateEntity(dataRule);
//		logBS.addLog(this.getRequest().getRemoteAddr(), BioneSecurityUtils.getCurrentUserInfo().getCurrentLogicSysNo(), BioneSecurityUtils.getCurrentUserInfo().getUserId(), "保存表内纵向校验的补录规则【" + dataRule.getRuleName() + "】");
		BioneUser user = BioneSecurityUtils.getCurrentUserInfo();
		dataRule.setCreateUser(user.getLoginName());
		dataRule.setLogicSysNo(user.getCurrentLogicSysNo());
		/***********添加日志记录*********/
//		StringBuilder buff = new StringBuilder();
//		RptInputLstTempleInfo tmpInfo = templeBS.getEntityById(templeId);
//		buff.append("用户[").append(user.getLoginName()).append("]").append(saveMark.equals("01")?"新增":"修改").append("了一个表内纵向校验界面 的补录规则,参数的名称为:")
//		.append(dataRule.getRuleNm());
//		dataRulesBS.saveLog(saveMark, "补录规则维护-表内纵向校验界面  ", buff.toString(), user.getUserId(), user.getLoginName());
		
		dataRulesBS.saveOrUpdateEntity(dataRule);
//		logBS.addLog(this.getRequest().getRemoteAddr(), user.getCurrentLogicSysNo(), user.getUserId(), "保存正则表达式的补录规则【" + dataRule.getRuleName() + "】");
		// 保存UDIP_RULE_ITEMS表
		if (StringUtils.isNotBlank(paramStr)) {
			// 保存字段时先删除该模板中的字段
			this.ruleItemBS.removeEntityByProperty("ruleId", dataRule.getRuleId());
			String[] templeColumnsList = paramStr.split(";;");
			for (int i = 0; i < templeColumnsList.length; i++) {
				String[] templeColumns = templeColumnsList[i].split("@#@",8);

				RptInputListRuleItemInfo ruleItem = new RptInputListRuleItemInfo();
				ruleItem.setRuleId(dataRule.getRuleId());
				if (!StringUtils.isNotBlank(templeColumns[3])) {
					ruleItem.setItemId(RandomUtils.uuid2());
					ruleItem.setCalCode(templeColumns[0]);
//					ruleItem.setColumnName(templeColumns[1]);
					ruleItem.setFieldName(templeColumns[1]);
//					ruleItem.setLeftOrRigh(templeColumns[5]);
					ruleItem.setLeftOrRight(templeColumns[5]);
				} else {
					ruleItem.setItemId(RandomUtils.uuid2());
					ruleItem.setCalCode(templeColumns[0]);
//					ruleItem.setColumnName(templeColumns[1]); 
					ruleItem.setFieldName(templeColumns[1]);
//					ruleItem.setGroupBy(templeColumns[2]);
					ruleItem.setGroupField(templeColumns[2]);
//					ruleItem.setFunctionName(templeColumns[3]);
					ruleItem.setAggregateFunc(templeColumns[3]);
//					ruleItem.setQueryCondition(templeColumns[4]);
					ruleItem.setFilterCondition(templeColumns[4]);
//					ruleItem.setLeftOrRigh(templeColumns[5]);
					ruleItem.setLeftOrRight(templeColumns[5]);
					ruleItem.setDsId(templeColumns[6]);
//					ruleItem.setTableName(templeColumns[7]);
					ruleItem.setTableEnName(templeColumns[7]);
				}
				
				this.ruleItemBS.saveEntity(ruleItem);
			}
		}
		map.put("msg", "success");
		return map;
	}

	/**
	 * 规则5:表间一致校验界面
	 * @return
	 */
	@RequestMapping("/rule5")
	public ModelAndView rule5(String id, String ruleId,String lookType) {
		ModelMap mm = new ModelMap();
		mm.addAttribute("ruleId", StringUtils2.javaScriptEncode(ruleId));
		mm.addAttribute("id", StringUtils2.javaScriptEncode(id));
		mm.addAttribute("lookType", StringUtils2.javaScriptEncode(lookType));
		return new ModelAndView("/input/rule/data-rule5", mm);
	}

	/**
	 * 规则5保存操作
	 * @param dataRule
	 * @param templeId
	 * @param paramStr
	 * @return
	 */
	@RequestMapping("/rule5-save")
	public Map<String,Object> rule5Save(RptInputListDataRuleInfo dataRule, String templeId, String paramStr) {
		// String saveMark = "03"; // 修改
		// 保存UDIP_DATA_RULES表
		Map<String,Object> map = new HashMap<String, Object>();
		if (StringUtils.isNotBlank(dataRule.getRuleId())) {
			dataRule.setRuleId(dataRule.getRuleId());
		} else {
			dataRule.setRuleId(RandomUtils.uuid2());
			// saveMark = "01"; // 新增
			// 保存templeRule表
			RptInputLstTempleRule templeRules = new RptInputLstTempleRule();
//			templeRules.setIndexId(RandomUtils.uuid2());
			templeRules.setId(RandomUtils.uuid2());
			templeRules.setRuleId(dataRule.getRuleId());
			templeRules.setTempleId(templeId);
		
		}

		dataRule.setRuleType("5");
		dataRule.setCreateDate(DateUtils.getYYYY_MM_DD_HH_mm_ss());
//		dataRule.setCreator(BioneSecurityUtils.getCurrentUserInfo().getLoginName());
//		dataRule.setLogicSysNo(BioneSecurityUtils.getCurrentUserInfo().getCurrentLogicSysNo());
//		dataRulesBS.saveOrUpdateEntity(dataRule);
//		logBS.addLog(this.getRequest().getRemoteAddr(), BioneSecurityUtils.getCurrentUserInfo().getCurrentLogicSysNo(), BioneSecurityUtils.getCurrentUserInfo().getUserId(), "保存表间一致校验的补录规则【" + dataRule.getRuleName() + "】");
		BioneUser user = BioneSecurityUtils.getCurrentUserInfo();
		dataRule.setCreateUser(user.getLoginName());
		dataRule.setLogicSysNo(user.getCurrentLogicSysNo());
		/***********添加日志记录*********/
//		StringBuilder buff = new StringBuilder();
//		RptInputLstTempleInfo tmpInfo = templeBS.getEntityById(templeId);
//		buff.append("用户[").append(user.getLoginName()).append("]").append(saveMark.equals("01")?"新增":"修改").append("了一个表间一致校验的补录规则,规则的名称为:")
//		.append(dataRule.getRuleNm());
//		dataRulesBS.saveLog(saveMark, "补录规则维护-表间一致校验  ", buff.toString(), user.getUserId(), user.getLoginName());
		dataRulesBS.saveOrUpdateEntity(dataRule);
//		logBS.addLog(this.getRequest().getRemoteAddr(), user.getCurrentLogicSysNo(), user.getUserId(), "保存正则表达式的补录规则【" + dataRule.getRuleName() + "】");

		// 保存UDIP_RULE_ITEMS表
		RptInputListRuleItemInfo ruleItem = new RptInputListRuleItemInfo();
		ruleItem.setRuleId(dataRule.getRuleId());
		if (StringUtils.isNotBlank(paramStr)) {
			// 保存字段时先删除该模板中的字段
			this.ruleItemBS.removeEntityByProperty("ruleId", dataRule.getRuleId());
			String[] templeColumnsList = paramStr.split(";;");
			for (int i = 0; i < templeColumnsList.length; i++) {
				String[] templeColumns = templeColumnsList[i].split("@#@");
				ruleItem.setItemId(RandomUtils.uuid2());
				ruleItem.setCalCode(templeColumns[0]);
//				ruleItem.setColumnName(templeColumns[1]);
				ruleItem.setFieldName(templeColumns[1]);
//				ruleItem.setGroupBy(templeColumns[2]);
				ruleItem.setGroupField(templeColumns[2]);
//				ruleItem.setFunctionName(templeColumns[3]);
				ruleItem.setAggregateFunc(templeColumns[3]);
//				ruleItem.setQueryCondition(templeColumns[4]);
				ruleItem.setFilterCondition(templeColumns[4]);
//				ruleItem.setLeftOrRigh(templeColumns[5]);
				ruleItem.setLeftOrRight(templeColumns[5]);
				ruleItem.setDsId(templeColumns[6]);
				ruleItem.setTableEnName(templeColumns[7]);
				this.ruleItemBS.saveEntity(ruleItem);
			}
		}
		map.put("msg", "success");
		return map;
	}

	/**
	 *  查找规则信息，用于修改
	 * @param ruleId
	 * @return
	 */
	@RequestMapping("/findRuleInfo")
	@ResponseBody
	public RptInputListDataRuleInfo show( String ruleId) {
		//RptInputListDataRuleInfo model = dataRulesBS.getEntityById(RptInputListDataRuleInfo.class, ruleId);
		
		return dataRulesBS.getRptInputListDataRuleInfoByRuleId(ruleId);
	}

	/**
	 * 获取该ID下的ruleItems
	 */
	@RequestMapping(value = "/listRuleItem/{ruleId}", method = RequestMethod.GET)
	@ResponseBody
	public List<RptInputListRuleItemInfo> listRuleItem(@PathVariable("ruleId") String ruleId) {
		List<RptInputListRuleItemInfo> listRuleItem = ruleItemBS.findEntityListByProperty("ruleId", ruleId);
		return listRuleItem;
	}

	/**
	 * 检查规则名称
	 * @param model
	 * @param ruleId
	 * @param templeId
	 * @return
	 */ 
	@RequestMapping("/checkRuleName")
	@ResponseBody
	public boolean checkRuleName(RptInputListDataRuleInfo model, String ruleId,String templeId) {

		return dataRulesBS.canAddRule(ruleId,model.getRuleNm(),templeId);
		/*
		if (list.isEmpty()) {
			return true;
		} else {
			boolean canAdd = true;
			//RptInputListDataRuleInfo info = list.get(0);
			for(RptInputListDataRuleInfo info : list){
				
				RptInputLstTempleRule tempRule = templeRuleBS.findUniqueEntityByProperty("ruleId", model.getRuleId());
				if(tempRule == null)
					continue;
				if (StringUtils.isNotBlank(ruleId)) {
					if (!info.getRuleId().equals(ruleId) && templeId.equals(tempRule.getTempleId())&&info.getRuleNm().equals(model.getRuleNm()))
					{
						canAdd =  false;
						break;
					}
				}
			}
			return canAdd;
		}
		*/
	}

	/**
	 * 根据规则Id删除所有关联的信息
	 * @param id
	 * @return
	 */
	@RequestMapping(value = "/deleteRule/{id}", method = RequestMethod.POST,produces = "text/html;charset=UTF-8")
	@ResponseBody
	public String deleteFile(@PathVariable("id") String id) {
		List<RptInputListDataRuleInfo> ruleList = Lists.newArrayList();
		try {
			String[] ids = id.split(",");
			for (int i = 0; i < ids.length; i++) {
				// RptInputListDataRuleInfo udipDataRulesInfo = this.dataRulesBS.getEntityByProperty(RptInputListDataRuleInfo.class, "ruleId", ids[i]);
				ruleList.add(dataRulesBS.getEntityById(ids[i]));
				this.dataRulesBS.removeEntityById(ids[i]);
				this.ruleItemBS.removeEntityByProperty("ruleId", ids[i]);
				this.templeRuleBS.removeEntityByProperty("ruleId", ids[i]);
//				logBS.addLog(this.getRequest().getRemoteAddr(), BioneSecurityUtils.getCurrentUserInfo().getCurrentLogicSysNo(), BioneSecurityUtils.getCurrentUserInfo().getUserId(), "删除补录规则" + "【" + udipDataRulesInfo.getRuleName() + "】");
			}
			
//			BioneUser user = BioneSecurityUtils.getCurrentUserInfo();
//			StringBuilder buff = new StringBuilder();
//			buff.append("用户[").append(user.getLoginName()).append("]删除了").append(ruleList.size()).append("个规则,规则的名称和类型分别为:");
//			boolean isFirst=true;
//			for(RptInputListDataRuleInfo role:ruleList)
//			{
//				if(isFirst)
//					isFirst=false;
//				else
//					buff.append(",");
//				String ruleType  = role.getRuleType();
//				String ruleTypeText ;
//				if(ruleType.equals("1"))
//					ruleTypeText = "数据值范围";
//				else if(ruleType.equals("2"))
//					ruleTypeText="正则表达式";
//				else if(ruleType.equals("3"))
//					ruleTypeText="表内横向校验";
//				else if(ruleType.equals("4"))
//					ruleTypeText="表内纵向校验";
//				else if(ruleType.equals("5"))
//					ruleTypeText="表间一致校验";
//				else
//					ruleTypeText="";
//				buff.append("[").append(role.getRuleNm()).append(",").append(ruleTypeText).append("]");
//			}
//			dataRulesBS.saveLog("02", "补录规则维护 ", buff.toString(), user.getUserId(), user.getLoginName());
		} catch (Exception e) {
			return "删除失败！";
		}
		return "删除成功！";
	}
	
	/**
	 * 获取时间区间下拉
	 * @return
	 */
	@RequestMapping("/getDataRangerSelection")
	@ResponseBody
	public List<Map<String, String>> getDataRangerSelection() {
		return UdipConstants.dataRangeList;
	}
	
	
	@RequestMapping("/lookDataRules")
	public ModelAndView lookDataRules(String lookType,String templeId) {
		ModelMap mm = new ModelMap();
		mm.addAttribute("lookType", StringUtils2.javaScriptEncode(lookType));
		mm.addAttribute("id", StringUtils2.javaScriptEncode(templeId));
		return new ModelAndView("/input/temple/temple-view-tab4",mm);
	}
}
