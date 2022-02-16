package com.yusys.biapp.input.template.web;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.yusys.biapp.input.common.InputConstants;
import com.yusys.biapp.input.data.entity.RptInputListDataFile;
import com.yusys.biapp.input.data.service.DataFileBS;
import com.yusys.biapp.input.dict.entity.RptInputListDataDictInfo;
import com.yusys.biapp.input.dict.utils.UdipConstants;
import com.yusys.biapp.input.inputTable.entity.RptInputListTableConstraint;
import com.yusys.biapp.input.inputTable.entity.RptInputListTableFieldInf;
import com.yusys.biapp.input.inputTable.entity.RptInputListTableInfo;
import com.yusys.biapp.input.inputTable.service.InputTableBS;
import com.yusys.biapp.input.inputTable.vo.InputTableSql;
import com.yusys.biapp.input.rule.entity.RptInputListDataRuleInfo;
import com.yusys.biapp.input.rule.entity.RptInputListRuleItemInfo;
import com.yusys.biapp.input.rule.service.DataRuleBS;
import com.yusys.biapp.input.rule.service.RuleItemBS;
import com.yusys.biapp.input.template.entity.*;
import com.yusys.biapp.input.template.service.*;
import com.yusys.biapp.input.template.utils.io.BOReader;
import com.yusys.biapp.input.template.utils.object.BOList;
import com.yusys.biapp.input.template.vo.RptInputLstTempleInfoVO;
import com.yusys.biapp.input.template.web.vo.TemplateRewriteTableVO;
import com.yusys.bione.comp.common.CommonComboBoxNode;
import com.yusys.bione.comp.common.CommonTreeNode;
import com.yusys.bione.comp.entity.page.Pager;
import com.yusys.bione.comp.entity.upload.Uploader;
import com.yusys.bione.comp.repository.jpa.SearchResult;
import com.yusys.bione.comp.utils.*;
import com.yusys.bione.frame.base.common.GlobalConstants4frame;
import com.yusys.bione.frame.base.web.BaseController;
import com.yusys.bione.frame.mtool.entity.BioneDsInfo;
import com.yusys.bione.frame.security.BioneSecurityUtils;
import com.yusys.bione.frame.security.BioneUser;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/rpt/input/temple")
public class TempleController extends BaseController {
	private final Log log = LogFactory.getLog(TempleController.class);
	// @Autowired
	// private DataLoadBS dataLoadBS;
//	@Autowired
//	private LogBS logBS;
	@Autowired
	private TempleBS templeBS;
	@Autowired
	private TempleRuleInfoBS templeRulesBS;
	@Autowired
	private DataRuleBS dataRuleBS;
	@Autowired
	private RuleItemBS ruleItemBS;
	@Autowired
	private TempleConstraintBS templeConstraintBS;
	@Autowired
	private TempleFieldBS templeFieldBS;
	@Autowired
	private TempleFileBS templeFileBS;
	@Autowired
	private DataLoadBS dataLoadBS;
	@Autowired
	private TempleRewriteBS templeRewriteBS;

	@Autowired
	private DataFileBS dataFileBS;

	@Autowired
	private InputTableBS tableBS;

	/**
	 * 执行修改前的页面跳转
	 * 
	 * @return String 用于匹配结果页面
	 */
	@RequestMapping(value = "/templeCatalog/{dirType}", method = RequestMethod.GET)
	public ModelAndView dirEdit(@PathVariable("dirType") String type) {
		type = StringUtils2.javaScriptEncode(type);
		return new ModelAndView(
				"/input/temple/temple-catalog-tree",
				"catalogType", type);
	}

	/**
	 * 查找数据表界面
	 * 
	 * @return
	 */
	@RequestMapping(value = "/tableListForTemple/{id}")
	public ModelAndView tableListForTemple(@PathVariable("id") String id) {
		id = StringUtils2.javaScriptEncode(id);
		return new ModelAndView(
				"/input/temple/table-list", "id", id);
	}

	// 跳转欢迎页面
	@RequestMapping(method = RequestMethod.GET)
	public String welcome() {
		return "/input/temple/temple-index";
	}

	//
	// /**
	// * 执行添加前页面跳转
	// * @return String 用于匹配结果页面
	// */
	@RequestMapping("/edit")
	public ModelAndView edit(String id, String lookType) {
		ModelMap mm = new ModelMap();
		mm.addAttribute("id", StringUtils2.javaScriptEncode(id));
		mm.addAttribute("lookType", StringUtils2.javaScriptEncode(lookType));
		return new ModelAndView(
				"/input/temple/temple-tab", mm);
	}

	/**
	 * 导入补录模版数据
	 * 
	 * @param dirId
	 * @return
	 */
	@RequestMapping("/impTempleData")
	public ModelAndView uploadTempleData(String dirId) {
		dirId = StringUtils2.javaScriptEncode(dirId);
		return new ModelAndView("/input/smb/file-imp-templedata", "dirId", dirId);
	}

	/**
	 * 补录文件导入
	 * @param templeId
	 * @param caseId
	 * @return
	 */
	@RequestMapping("/uploadTempleData")
	public ModelAndView uploadTempleData(String templeId, String caseId,String taskNodeInstanceId) {
		ModelMap mm = new ModelMap();
		mm.addAttribute("templeId", StringUtils2.javaScriptEncode(templeId));
		mm.addAttribute("caseId", StringUtils2.javaScriptEncode(caseId));
		mm.addAttribute("taskNodeInstanceId", StringUtils2.javaScriptEncode(taskNodeInstanceId));
		return new ModelAndView("/input/smb/file-upload-templedata", mm);
	}

	/**
	 * 模板导入
	 * 
	 * @param uploader
	 * @param response
	 * @param dirId
	 * @return
	 */
	@RequestMapping("/startImpTempleData")
	@ResponseBody
	public String startImpTempleData(Uploader uploader, HttpServletResponse response, String dirId) {
		String udipTempless = null;
		InputStream is = null;
		File file = null;
		String fileName = uploader.getUpload().getFileItem().getName();
		try {
			List<String>tempNms=Lists.newArrayList();
			file = this.uploadFile(uploader, GlobalConstants4frame.FRS_RPT_FILL_IMPORT_PATH, false);
			// is = uploader.getUpload().getInputStream();
			if (uploader.getChunk() == uploader.getChunks() - 1) {
				is = new FileInputStream(file);
				if (file != null) {
					file.delete();
				}
				List<BOList> tempList = BOReader.readList(is);
				for (BOList boList : tempList) {
					String classNames = boList.getClassName();
					classNames = classNames.substring(classNames.lastIndexOf(".") + 1);
					if ("RptInputLstTempleInfo".equals(classNames)) {
						RptInputLstTempleInfo udipTemples = (RptInputLstTempleInfo) boList.get(0);
						tempNms.add(udipTemples.getTempleName());
						List<RptInputLstTempleInfo> list = templeBS.findEntityListByProperty("templeName", udipTemples.getTempleName());
						if (list != null && list.size() > 0) {
							if (udipTempless == null) {
								udipTempless = udipTemples.getTempleName();
							} else {
								udipTempless = udipTempless + "和" + udipTemples.getTempleName();
							}
						}
					}
				}
				if (udipTempless != null && !"".equals(udipTempless)) {
					return udipTempless;
				} else {
					templeBS.fileImpTemp(tempList, dirId);
					

//					BioneUser user = BioneSecurityUtils.getCurrentUserInfo();
//					StringBuilder buff = new StringBuilder();
//					buff.append("用户[").append(user.getLoginName()).append("]执行了导入模板操作,模板的名称为:");
//					boolean isFirst=true;
//					for(String nm:tempNms)
//					{
//						if(isFirst)
//							isFirst=false;
//						else
//							buff.append(",");
//						buff.append(nm);
//					}
//					templeBS.saveLog("02", "明细补录模板", buff.toString(), user.getUserId(), user.getLoginName());
				}
			}
		} catch (Exception e) {
			udipTempless = e.getMessage();
			logger.info("文件导入出现异常", e);
			return udipTempless;
		} finally {
			if (file != null) {
				file.delete();
			}
			if (is != null) {
				try {
					is.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		logger.info("文件[" + fileName + "]导入完成");
		return udipTempless;
	}
	
	@RequestMapping("/startUploadTempleData")
	@ResponseBody
	public Map<String, Object> startUploadTempleData(Uploader uploader, HttpServletResponse response, String templeId, String caseId,String taskNodeInstanceId) {
		Map<String, Object> result = Maps.newHashMap();
		try {
			File file = this.uploadFile(uploader, GlobalConstants4frame.FRS_RPT_FILL_IMPORT_PATH, false);
			List<String>rs = this.dataFileBS.saveDataFileById(file, templeId, caseId,taskNodeInstanceId);
			if(rs!=null&&!rs.isEmpty())
			{
				if(rs.get(0).equalsIgnoreCase("cannt"))
					result.put("error", rs.get(1));
				else
					result.put("OK",rs );
			}else
				result.put("OK",rs );
		} catch (Exception e) {
			result.put("error", Lists.newArrayList(e));
		}
		return result;
	}

	// /**
	// * 补录任务选择补录模板时的页面
	// * @param id
	// * @return
	// */
	// @RequestMapping(value = "/getTempleList/{id}", method =
	// RequestMethod.GET)
	// public ModelAndView chackDS(@PathVariable("id") String id) {
	// return new ModelAndView("/udip/temple/temple-index", "id", id);
	// }

	/**
	 * 执行删除操作，可进行指删除
	 */
	@RequestMapping(value = "/{id}", method = RequestMethod.POST)
	@ResponseBody
	public String destroy(@PathVariable("id") String id) {
		String[] ids = id.split(",");
		//删除前校验是否有任务关联模板 20190605
		boolean flag = this.templeBS.checkTaskByTemplateId(ids);
		System.err.println(flag);
		if(!flag) {
			return "check";
		}
//		String logicSysNo = BioneSecurityUtils.getCurrentUserInfo().getCurrentLogicSysNo();
		List<String>tgNms = Lists.newArrayList();
		for (int i = 0; i < ids.length; i++) {
			RptInputLstTempleInfo tempObj = this.templeBS.getEntityById(ids[i]);
			tgNms.add(tempObj.getTempleName());
			// 删除对应的补录字段
			this.templeFieldBS.removeEntityByProperty("templeId", ids[i]);
			// 删除补录字段主键
			this.templeConstraintBS.removeEntityByProperty("templeId", ids[i]);
			// 查找所有补录模板的excel文件
			List<RptInputLstTempleFile> listFile = this.templeFileBS.findEntityListByProperty("templeId", ids[i]);
			for (int j = 0; j < listFile.size(); j++) {
				// 删除远程文件
				File file = new File(listFile.get(j).getFilePath());
				file.delete();
				// 删除对应的templeFile记录
				this.templeFileBS.removeEntity(listFile.get(j));
			}
			// 查找模板规则记录
			List<RptInputLstTempleRule> listRules = this.templeRulesBS.findEntityListByProperty("templeId", ids[i]);
			// 根据模板规则记录查找对应的补录规则并删除
			for (int k = 0; k < listRules.size(); k++) {
				this.dataRuleBS.removeEntityByRuleId(listRules.get(k).getRuleId());
				// 删除规则子项
				this.ruleItemBS.removeEntityByProperty("ruleId", listRules.get(k).getRuleId());
			}
			// 删除模板规则
			this.templeRulesBS.removeEntityByProperty("templeId", ids[i]);

			// 删除模板
//			RptInputLstTempleInfo udipTemple = this.templeBS.getEntityByProperty(RptInputLstTempleInfo.class, "templeId", ids[i]);
			this.templeBS.removeEntityByProperty("templeId", ids[i]);
//			this.authObjBS.removeAuthObjResRel(logicSysNo,
//					GlobalConstants.UA_AUTH_OBJ_DEF_ID_ROLE,
//					TempResImpl.RES_OBJ_DEF_NO, ids[i]);// 删除授权关系
//			this.authObjBS.removeAuthObjResRel(logicSysNo,
//					GlobalConstants.LOCAL_AUTH_OBJ_DEF_ID_ROLE,
//					TempResImpl.RES_OBJ_DEF_NO, ids[i]);// 删除授权关系

			/*logBS.addLog(this.getRequest().getRemoteAddr(), logicSysNo,
					BioneSecurityUtils.getCurrentUserInfo().getUserId(),
					"删除补录模版【" + udipTemple.getTempleName() + "】");*/
		}
		
	/*	for(String idss : ids){
			//userList.add((BioneUserInfo) this.userBS.getEntityById(id));
			//LstTempList.addAll(templeBS.getEntityById(idss));
			//userList.add(userBS.getEntityById(BioneUserInfo.class , BioneSecurityUtils
				//	.getCurrentUserId()));
			LstTempList.add(templeBS.g)
		}*/
//		BioneUser user = BioneSecurityUtils.getCurrentUserInfo();
//		StringBuilder buff = new StringBuilder();
//		buff.append("用户[").append(user.getLoginName()).append("]删除了").append(tgNms.size()).append("个模板,模板的名称为:");
//		boolean isFirst=true;
//		for(String tg:tgNms)
//		{
//			if(isFirst)
//				isFirst=false;
//			else
//				buff.append(",");
//			buff.append(tg);
//		}
//		templeBS.saveLog("02", "明细补录模板", buff.toString(), user.getUserId(), user.getLoginName());
		
		return "true";
	}

	/**
	 * 修改。加载页面
	 */
	@RequestMapping("/findTempleInfo")
	@ResponseBody
	public RptInputLstTempleInfoVO show(String templeId) {
		RptInputLstTempleInfoVO model = templeBS.getTempInfoById(templeId);
		if (model == null) {
			model = new RptInputLstTempleInfoVO();
			model.setTempleId(templeId);
		} /*
		 * else { if (StringUtils.isNotBlank(model.getTempleId())) { BioneDsInfo
		 * info = dataSourceBS.getEntityById(model.getDsId()); if (model != null
		 * && info != null) { model.setLogicSysNo(info.getDsName()); } } else {
		 * model.setLogicSysNo(""); } }
		 */
		return model;
	}

	/**
	 * 获取用于加载grid的数据
	 */
	@RequestMapping("/list.*")
	@ResponseBody
	public Map<String, Object> list(Pager rf, String realId, @RequestParam(defaultValue = "false") boolean toTask) {
		SearchResult<RptInputLstTempleInfo> searchResult = this.templeBS
				.getTempleList(rf.getPageFirstIndex(), rf.getPagesize(),
						rf.getSortname(), rf.getSortorder(), rf.getSearchCondition(), realId, toTask);
		Map<String, Object> objDefMap = Maps.newHashMap();
		objDefMap.put("Rows", searchResult.getResult());
		objDefMap.put("Total", searchResult.getTotalCount());
		

		/*****添加日志记录 *****/
//		BioneUser user = BioneSecurityUtils.getCurrentUserInfo();
//		StringBuilder buff = new StringBuilder();
//		buff.append("用户[").append(user.getLoginName()).append("]").append("对明细补录模板进行了一次查询，查询条件为：");
//		Map<String, Object> cd = rf.getSearchCondition();
//		Map<String,String> filedInfo =  (Map<String, String>) cd.get("fieldValues");
//		Map<String,String>nmMap=Maps.newHashMap();
//		nmMap.put("temple.templeName", "模板名称");
//		nmMap.put("temple.tableEnName", "表名");
//		nmMap.put("temple.templeSts", "状态");
//		Map<String,Map<String,String>>exMap=Maps.newHashMap();
//		Map<String,String>tmpMap=Maps.newHashMap();
//		tmpMap.put("1", "启用");
//		tmpMap.put("0", "停用");
//		exMap.put("temple.templeSts", tmpMap);
//		templeBS.saveQueryLog("明细补录模板",  user.getUserId(), user.getLoginName(),nmMap,filedInfo,exMap);
		
		return objDefMap;
	}

	//
	// /**
	// * 获取用于加载补录数据源的数据
	// */
	// @RequestMapping("/listColumns.*")
	// @ResponseBody
	// public Map<String, Object> listColumns(Pager rf) {
	// SearchResult<UdipTempleColumns> searchResult =
	// this.templeColumnsBS.getTempleColumnsList(rf.getPageFirstIndex(),
	// rf.getPagesize(), rf.getSortname(), rf.getSortorder(),
	// rf.getSearchCondition());
	// Map<String, Object> objDefMap = Maps.newHashMap();
	// objDefMap.put("Rows", searchResult.getResult());
	// objDefMap.put("Total", searchResult.getTotalCount());
	// return objDefMap;
	// }
	//
	/**
	 * 保存数据表中的各个字段
	 */
	@RequestMapping("/saveTempleInfo")
	public String saveTempleInfo(RptInputLstTempleInfo templeInfo, String paramStr) {
		// String saveMark = "03";//修改
		// 保存基本信息
		RptInputLstTempleInfo udip = this.templeBS.getEntityById(templeInfo.getTempleId());
		
		/*************添加日志记录*********/
		
		/*RptInputLstTempleInfo TemInfo = this.templeBS.getEntityById(RptInputLstTempleInfo.class , BioneSecurityUtils
				.getCurrentUserId());*/
				
			
		// BioneUser users = BioneSecurityUtils.getCurrentUserInfo();
		
		
		
		if (udip == null) {
			//默认发送短信
			templeInfo.setIsSendNotify("1");
			templeInfo.setCreateDate(DateUtils.formatDate(new Date(), "yyyy-MM-dd"));
			templeInfo.setCreateUser(BioneSecurityUtils.getCurrentUserInfo().getLoginName());
			templeInfo.setLogicSysNo(BioneSecurityUtils.getCurrentUserInfo().getCurrentLogicSysNo());
			//BioneUser user = BioneSecurityUtils.getCurrentUserInfo();
			//templeInfo.setDefOrg(user.getOrgNo());
			String userId = templeInfo.getDefUser();
			// saveMark = "01";
			if(StringUtils.isEmpty(userId))
			{
				BioneUser user = BioneSecurityUtils.getCurrentUserInfo();
				userId = ","+user.getUserId()+",";
			}else
				userId+=",";
			templeInfo.setDefUser(userId);
			//英文表名全部存储大写表名，mysql库存在小写情况，后面存在通过关联英文表名查询的情况，会导致关联不上。 20191028
			templeInfo.setTableEnName(templeInfo.getTableEnName() != null ? templeInfo.getTableEnName().toUpperCase() : "");
			this.templeBS.saveOrUpdateEntity(templeInfo);
		} else {
			udip.setTempleName(templeInfo.getTempleName());
			udip.setCatalogId(templeInfo.getCatalogId());
			udip.setTempleSts(templeInfo.getTempleSts());
			udip.setRemark(templeInfo.getRemark());
			udip.setIsCheck(templeInfo.getIsCheck());
			udip.setDutyUser(templeInfo.getDutyUser());
			udip.setDutyUserDept(templeInfo.getDutyUserDept());
			udip.setAllowAdd(templeInfo.getAllowAdd());
			udip.setAllowDelete(templeInfo.getAllowDelete());
			udip.setAllowUpdate(templeInfo.getAllowUpdate());
			udip.setAllowInputHist(templeInfo.getAllowInputHist());
			udip.setAllowInputLower(templeInfo.getAllowInputLower());
			udip.setDefOrg(templeInfo.getDefOrg());
			udip.setDefSrc(templeInfo.getDefSrc());
			udip.setDefOrgNm(templeInfo.getDefOrgNm());
			String userId = templeInfo.getDefUser();
			if(StringUtils.isEmpty(userId))
			{
				BioneUser user = BioneSecurityUtils.getCurrentUserInfo();
				userId = ","+user.getUserId()+",";
			}else
				userId+=",";
			udip.setDefUser(userId);

			if (StringUtils.isNotBlank(templeInfo.getDsId())) {
				udip.setDsId(templeInfo.getDsId());
				
			}
			if (StringUtils.isNotBlank(templeInfo.getTableEnName())) {
				if (templeInfo.getTableEnName().equalsIgnoreCase(udip.getTableEnName())) {
					//英文表名全部存储大写表名，mysql库存在小写情况，后面存在通过关联英文表名查询的情况，会导致关联不上。 20191028
					udip.setTableEnName(templeInfo.getTableEnName().toUpperCase());
				} else {
					this.templeConstraintBS.removeEntityByProperty("templeId", udip.getTempleId());
					List<RptInputLstTempleRule> list = this.templeRulesBS.findEntityListByProperty("templeId", udip.getTempleId());
					for (int i = 0; i < list.size(); i++) {
						this.dataRuleBS.removeEntityById(list.get(i).getRuleId());
						this.ruleItemBS.removeEntityByProperty("ruleId", list.get(i).getRuleId());
						this.templeRulesBS.removeEntityByProperty("ruleId", list.get(i).getRuleId());
					}
					//英文表名全部存储大写表名，mysql库存在小写情况，后面存在通过关联英文表名查询的情况，会导致关联不上。 20191028
					udip.setTableEnName(templeInfo.getTableEnName().toUpperCase());
				}

			}
			if (StringUtils.isNotBlank(templeInfo.getOrgColumn())) {
				udip.setOrgColumn(templeInfo.getOrgColumn());
			} else {
				udip.setOrgColumn("");
			}
			

			
			this.templeBS.saveOrUpdateEntity(udip);
//			logBS.addLog(this.getRequest().getRemoteAddr(), BioneSecurityUtils
//					.getCurrentUserInfo().getCurrentLogicSysNo(),
//					BioneSecurityUtils.getCurrentUserInfo().getUserId(),
//					"保存补录模版【" + udip.getTempleName() + "】");
		}
		// 保存该模板中的字段
		if (StringUtils.isNotBlank(paramStr)) {
			templeInfo.setCreateDate(DateUtils.formatDate(new Date(),
					"yyyy-MM-dd"));
			// templeInfo.setCreator(BioneSecurityUtils.getCurrentUserInfo()
			// .getLoginName());
			templeInfo.setLogicSysNo(BioneSecurityUtils.getCurrentUserInfo()
					.getCurrentLogicSysNo());
			// 保存字段时先删除该模板中的字段
			this.templeFieldBS.removeEntityByProperty("templeId",
					templeInfo.getTempleId());

			String[] templeColumnsList = paramStr.split(";;");
			RptInputLstTempleField templeField = new RptInputLstTempleField();
			templeField.setTempleId(templeInfo.getTempleId());
			templeField.setLogicSysNo(BioneSecurityUtils.getCurrentUserInfo()
					.getCurrentLogicSysNo());
			for (int i = 0; i < templeColumnsList.length; i++) {
				String[] templeColumns = templeColumnsList[i].split("#@#", 14);
				// templeField.setTempleColumnId(RandomUtils.uuid2());
				templeField.setTempleFieldId(RandomUtils.uuid2());
				// templeField.setColumnName(templeColumns[0]);
				templeField.setFieldEnName(templeColumns[0]);
				// templeField.setColumnType(templeColumns[1]);
				templeField.setFieldType(templeColumns[1]);
				// templeField.setColumnLength(Integer.parseInt(templeColumns[2]));
				templeField.setFieldLength(templeColumns[2]);
				// templeField.setpNumber(Integer.parseInt(templeColumns[3]));
				templeField.setDecimalLength(StringUtils
						.isEmpty(templeColumns[3])
						|| "null".equals(templeColumns[3]) ? "0"
						: templeColumns[3]);
				// templeField.setColumnComment(templeColumns[4]);
				templeField.setFieldCnName(templeColumns[4]);
				// templeField.setNullable(templeColumns[5]);
				templeField.setAllowNull(templeColumns[5]);
				templeField.setIsDesen(templeColumns[6]);
				// templeField.setSelectable(templeColumns[6]);
				templeField.setAllowSift(templeColumns[7]);
				// templeField.setDataZidian(templeColumns[7]);
				templeField.setDictId(templeColumns[8]);
				// templeField.setColumnDetail(templeColumns[8]);
				templeField.setFieldDetail(templeColumns[9]);
				// templeField.setSeqNo(Integer.parseInt(templeColumns[9]));
				templeField.setOrderNo(Integer.parseInt(templeColumns[10],11) );
				// templeField.setWritable(templeColumns[10]);
				templeField.setAllowInput(templeColumns[11]);
				//默认值
				templeField.setDefaultValue(templeColumns[12]);
				//新增查询类型字段
				templeField.setSearchType(templeColumns[13]);
				this.templeFieldBS.saveEntity(templeField);
			}
		}
		// 保存补录规则
		/********添加日志记录**********/
//		StringBuilder buff = new StringBuilder();
//		buff.append("用户[").append(users.getLoginName()).append("]").append(saveMark.equals("03")?"修改":"新增").append("模板名称为:")
//		.append(templeInfo.getTempleName());
//		templeBS.saveLog(saveMark, "明细补录模板  ", buff.toString(), users.getUserId(), users.getLoginName());
		return "true";
	}

	/**
	 * 导入补录模版文件
	 * 
	 * @param response
	 * @param tempId
	 */
	@RequestMapping(value = "/fileDownTemp")
	public void fileDownTemp(HttpServletResponse response, String tempId) {
		if (StringUtils.isNotBlank(tempId)) {
			String[] tempIds = tempId.split(",");
			List<BOList> dataList = Lists.newArrayList();
			FileInputStream is = null;
			File localFile = null;
			List<String>tempNms=Lists.newArrayList();
			try {
				for (int i = 0; i < tempIds.length; i++) {
					// 补录模板
					RptInputLstTempleInfo udip = this.templeBS.getEntityById(tempIds[i]);
					tempNms.add(udip.getTempleName());
					BOList tempBoList = new BOList(RptInputLstTempleInfo.class);
					tempBoList.add(udip);
					dataList.add(tempBoList);
					// 获取该补录模版对应的数据源
					List<BioneDsInfo> dsInfoList = Lists.newArrayList();
					BioneDsInfo dsInfo = this.templeBS.getEntityByProperty(BioneDsInfo.class, "dsId", udip.getDsId());
					if (dsInfo != null) {
						dsInfoList.add(dsInfo);
					}
					// 模板字段
					List<RptInputLstTempleField> colLists = this.templeFieldBS.getEntityListByProperty(RptInputLstTempleField.class, "templeId", udip.getTempleId());
					BOList colBoList = new BOList(RptInputLstTempleField.class);
					for (RptInputLstTempleField udipTempleColumns : colLists) {
						colBoList.add(udipTempleColumns);
					}
					dataList.add(colBoList);
					// 模版字段对应的数据字典
					List<RptInputListDataDictInfo> dataLibList = Lists.newArrayList();
					if (colLists != null && colLists.size() > 0) {
						for (RptInputLstTempleField udipTempleColumns : colLists) {
							if (!"".equals(udipTempleColumns.getDictId())) {
								RptInputListDataDictInfo udipDataLibInfo = this.templeBS.getEntityByProperty(RptInputListDataDictInfo.class, "dictId", udipTempleColumns.getDictId());
								if (udipDataLibInfo != null && !dataLibList.contains(udipDataLibInfo)) {
									dataLibList.add(udipDataLibInfo);
								}
							}
						}
					}
					if (CollectionUtils.isNotEmpty(dataLibList)) {
						BOList dataLib = new BOList(RptInputListDataDictInfo.class);
						for (RptInputListDataDictInfo udipDataLibInfo : dataLibList) {
							dataLib.add(udipDataLibInfo);
							if (udipDataLibInfo != null && !"".equals(udipDataLibInfo.getDsId())) {// 获取数据字典中对应的数据源
								BioneDsInfo dsInfos = this.templeBS.getEntityByProperty(BioneDsInfo.class, "dsId", udipDataLibInfo.getDsId());
								if (dsInfos != null && !dsInfoList.contains(dsInfos)) {// 如果数据源已经存在数据源列表中，就不需要重复添加
									dsInfoList.add(dsInfos);
								}
							}
						}
						dataList.add(dataLib);
					}

					// 保存补录模版对应的表的信息
					List<RptInputListTableInfo> tableInfoList = tableBS.getTableInfoByDsIdAndTableName(udip.getDsId(), udip.getTableEnName());
					if (tableInfoList != null && tableInfoList.size() > 0) {
						RptInputListTableInfo udipTableInfo = tableInfoList.get(0);
						BOList tableBoList = new BOList(RptInputListTableInfo.class);// 保存补录表信息
						tableBoList.add(udipTableInfo);
						dataList.add(tableBoList);

						List<RptInputListTableFieldInf> udipTableColList = tableBS.getEntityListByProperty(RptInputListTableFieldInf.class, "tableId", udipTableInfo.getTableId());
						BOList tableColList = new BOList(RptInputListTableFieldInf.class);// 保存补录表字段信息
						for (RptInputListTableFieldInf udipTableColInfo : udipTableColList) {
							tableColList.add(udipTableColInfo);
						}
						dataList.add(tableColList);

						List<RptInputListTableConstraint> udipPriIndexList = tableBS.getEntityListByProperty(RptInputListTableConstraint.class, "tableId", udipTableInfo.getTableId());
						BOList priIndexList = new BOList(RptInputListTableConstraint.class);// 保存补录表主键索引信息
						for (RptInputListTableConstraint udipPriIndexInfo : udipPriIndexList) {
							priIndexList.add(udipPriIndexInfo);
						}
						dataList.add(priIndexList);

						if (udipTableInfo != null && !"".equals(udipTableInfo.getDsId())) {// 获取补录表对应的数据源信息
							BioneDsInfo dsInfos = this.templeBS.getEntityByProperty(BioneDsInfo.class, "dsId", udipTableInfo.getDsId());
							if (dsInfos != null && !dsInfoList.contains(dsInfos)) {
								dsInfoList.add(dsInfos);
							}
						}
					}

					if (dsInfoList != null && dsInfoList.size() > 0) {// 保存补录模板，数据字典，补录表对应的数据源数据
						BOList dsBoList = new BOList(BioneDsInfo.class);
						for (int k = 0; k < dsInfoList.size(); k++) {
							dsBoList.add(dsInfoList.get(k));
						}
						dataList.add(dsBoList);
					}

					// 模板主键
					List<RptInputLstTempleConst> priLists = this.templeConstraintBS.getEntityListByProperty(RptInputLstTempleConst.class, "templeId", udip.getTempleId());
					if (priLists != null && priLists.size() > 0) {
						BOList priBoList = new BOList(RptInputLstTempleConst.class);
						for (RptInputLstTempleConst udipTemplePrimary : priLists) {
							priBoList.add(udipTemplePrimary);
						}
						dataList.add(priBoList);
					}
					// 模板文件
					// List<UdipTempleFile> fileLists =
					this.templeFileBS.getEntityListByProperty(RptInputLstTempleFile.class, "templeId", udip.getTempleId());
					// if (fileLists != null && fileLists.size() > 0) {
					// BOList fileBoList = new BOList(UdipTempleFile.class);
					// for (UdipTempleFile udipTempleFile : fileLists) {
					// fileBoList.add(udipTempleFile);
					// }
					// dataList.add(fileBoList);
					// }
					BOList fileBoList = new BOList(RptInputLstTempleFile.class);
					RptInputLstTempleFile udipTempleFile = new RptInputLstTempleFile();
					udipTempleFile.setTempleId(udip.getTempleId());
					fileBoList.add(udipTempleFile);
					dataList.add(fileBoList);
					// 模板规则
					List<RptInputLstTempleRule> rulList = this.templeRulesBS.getEntityListByProperty(RptInputLstTempleRule.class, "templeId", udip.getTempleId());
					if (CollectionUtils.isNotEmpty(rulList)) {
						BOList ruleBoList = new BOList(RptInputLstTempleRule.class);
						for (RptInputLstTempleRule udipTempleRules : rulList) {
							ruleBoList.add(udipTempleRules);
						}
						dataList.add(ruleBoList);
					}
					List<String> ruleIdList = Lists.newArrayList();
					// 规则定义
					List<RptInputListDataRuleInfo> dataRulList = null;
					if (CollectionUtils.isNotEmpty(rulList)) {
						for (RptInputLstTempleRule rule : rulList) {
							ruleIdList.add(rule.getRuleId());
						}
						dataRulList = this.dataRuleBS.findByIdList(ruleIdList);
					}
					BOList udipRuleItemsInfoList = new BOList(RptInputListRuleItemInfo.class);
					if (CollectionUtils.isNotEmpty(dataRulList)) {
						BOList dataRulsList = new BOList(RptInputListDataRuleInfo.class);
						for (RptInputListDataRuleInfo udipDataRulesInfo : dataRulList) {
							dataRulsList.add(udipDataRulesInfo);
							// 规则子项
							List<RptInputListRuleItemInfo> ruleItemsInfoList = this.dataRuleBS.getEntityListByProperty(RptInputListRuleItemInfo.class, "ruleId", udipDataRulesInfo.getRuleId());
							if (CollectionUtils.isNotEmpty(ruleItemsInfoList)) {
								for (RptInputListRuleItemInfo ruleItemsInfo : ruleItemsInfoList) {
									udipRuleItemsInfoList.add(ruleItemsInfo);
								}
							}
						}
						dataList.add(dataRulsList);
						if (udipRuleItemsInfoList != null && udipRuleItemsInfoList.size() > 0) {
							dataList.add(udipRuleItemsInfoList);
						}
					}

					// 保存补录模版对应的表的建表sql
					String tableSql = this.templeBS.getTableSqlByTableName(udip.getDsId(), udip.getTableEnName());
					if (!"".equals(tableSql)) {
						BOList sqlBoList = new BOList(InputTableSql.class);
						InputTableSql udipTableSql = new InputTableSql();
						udipTableSql.setTableSql(tableSql.replaceAll("\n", ""));
						sqlBoList.add(udipTableSql);
						dataList.add(sqlBoList);
					}
				}
				String times = DateUtils.formatDate(new Date(), "yyyy-MM-dd HH-mm-ss");
				String defaultFileName = "补录模板导出文件_" + times + ".mdx";// 生成默认文件名
				localFile = BOReader.write(dataList, String.valueOf(System.currentTimeMillis()) + ".mdx");
				

//				BioneUser user = BioneSecurityUtils.getCurrentUserInfo();
//				StringBuilder buff = new StringBuilder();
//				buff.append("用户[").append(user.getLoginName()).append("]执行导出操作，模板名称为:");
//				boolean isFirst = true;
//				for(String nm:tempNms){
//					if(isFirst)
//						isFirst= false;
//					else
//						buff.append(",");
//					buff.append(nm);
//				}
//				templeBS.saveLog("04", "明细补录模板", buff.toString(), user.getUserId(), user.getLoginName());
				
				DownloadUtils.download(response, localFile, defaultFileName);
			} catch (Exception e) {
				log.error(e);
			} finally {
				if (localFile != null) {
					localFile.delete();
				}
				try {
					if (is != null)
						is.close();
				} catch (IOException e) {
					log.error("关闭文件输入流失败", e);
				}
			}
		}
	}

	/**
	 * 执行添加前的数据加载
	 * 
	 * @return String 用于匹配结果页面
	 */
	@RequestMapping(value = "/new", method = RequestMethod.GET)
	public ModelAndView editNew(String catalogId,String catalogName) {
		Map<String, Object> map = new HashMap<>();
		map.put("catalogId", catalogId);
		String id = RandomUtils.uuid2();
		map.put("id", id);
		map.put("catalogName",catalogName);
		return new ModelAndView(
				"/input/temple/temple-tab", map);
	}

	/**
	 * 第一步
	 * 
	 * @return
	 */
	@RequestMapping(value = "/tab1/{id}")
	public ModelAndView tab1(@PathVariable("id") String id,String catalogId,String catalogName) {
		id = StringUtils2.javaScriptEncode(id);
		Map<String, Object> map = new HashMap<>();
		map.put("id", id);
		map.put("catalogId", catalogId);
		map.put("catalogName", catalogName);
		return new ModelAndView(
				"/input/temple/temple-tab1", map);

	}

	/**
	 * 检出模版名称是否重复
	 * 
	 * @param model
	 * @param id
	 * @return
	 */
	@RequestMapping("/checkTempleName")
	@ResponseBody
	public boolean checkTempleName(RptInputLstTempleInfo model, String id) {

		List<RptInputLstTempleInfo> list = templeBS.findEntityListByProperty(
				"templeName", model.getTempleName());
		if (list == null || list.isEmpty()) {
			return true;
		} else {
			if (null != id) {
				RptInputLstTempleInfo info = list.get(0);
				if (list.size() > 1 || !info.getTempleId().equals(id))
					return false;
				else
					return true;
			}
			return false;
		}
	}

	/**
	 * 第二步
	 * 
	 * @return
	 */
	@RequestMapping(value = "/tab2/{id}")
	public ModelAndView tab2(@PathVariable("id") String id) {
		id = StringUtils2.javaScriptEncode(id);
		return new ModelAndView(
				"/input/temple/temple-tab2", "id", id);
	}

	//
	// /**
	// * 查找数据源界面
	// * @return
	// */
	// @RequestMapping(value = "/dsListForTemple")
	// public String dsListForTemple() {
	// return "/udip/temple/ds-list";
	// }

	/**
	 * 主键编辑界面跳转
	 * 
	 * @param id
	 * @return
	 */
	@RequestMapping("/templeKey")
	public ModelAndView templeKey(String id, String lookType) {
		ModelMap mm = new ModelMap();
		mm.addAttribute("id", StringUtils2.javaScriptEncode(id));
		mm.addAttribute("lookType", StringUtils2.javaScriptEncode(lookType));
		return new ModelAndView(
				"/input/temple/create-temple-key", mm);
	}

	/**
	 * 数据预加载界面跳转
	 * 
	 * @param id
	 * @return
	 */
	@RequestMapping("/templeDataLoad")
	public ModelAndView templeDataLoadKey(String id, String lookType) {
		ModelMap mm = new ModelMap();
		mm.addAttribute("id", StringUtils2.javaScriptEncode(id));
		mm.addAttribute("lookType", StringUtils2.javaScriptEncode(lookType));
		return new ModelAndView(
				"/input/temple/create-temple-data-load",
				mm);
	}

	/**
	 * 数据预加载数据值下拉框
	 * 
	 * @return
	 */
	@RequestMapping("/getTempleDataLoadList")
	@ResponseBody
	public List<Map<String, String>> getTempleDataLoadList() {
		return UdipConstants.DataLoadList;
	}

	/**
	 * 查找主键信息，用于回显修改
	 * 
	 * @param id
	 * @return
	 */
	@RequestMapping(value = "/findTempleKey/{id}", method = RequestMethod.GET)
	@ResponseBody
	public List<RptInputLstTempleConst> findTempleKey(
			@PathVariable("id") String id) {
		List<RptInputLstTempleConst> listRuleItem = templeConstraintBS
				.findEntityListByProperty("templeId", id);
		return listRuleItem;
	}

	@RequestMapping(value = "/findTempleDataLoad/{id}", method = RequestMethod.GET)
	@ResponseBody
	public List<RptInputLstDataLoadInfo> findTempleDataLoad(
			@PathVariable("id") String id) {
		List<RptInputLstDataLoadInfo> listRuleItem = dataLoadBS
				.findEntityListByProperty("templeId", id);
		return listRuleItem;
	}

	/**
	 * 保存用户自定义模板主键
	 * 
	 * @param paramStr
	 * @param templeId
	 * @return
	 */
	@RequestMapping("/savaTempleKey")
	public String savaTempleKey(String paramStr, String templeId) {
		if (StringUtils.isNotBlank(paramStr)) {
			String[] templePrimaryList = paramStr.split(";;");
			// 保存字段时先删除该模板中的字段
			this.templeConstraintBS.removeEntityByProperty("templeId", templeId);
			for (int i = 0; i < templePrimaryList.length; i++) {
				this.templeConstraintBS.saveTempleKey(templePrimaryList[i]);
			}
		} else {
			this.templeConstraintBS.removeEntityByProperty("templeId", templeId);
		}
		return "true";
	}

	/**
	 * 
	 * 
	 * @param dsId
	 * @param tableName
	 * @param templeId
	 * @return
	 */
	@RequestMapping("/savaTempleKeyIndex")
	public String savaTempleKeyIndex(String dsId, String tableName, String templeId) {
		this.templeConstraintBS.setTempleKeyIndex(dsId, tableName, templeId);
		return "true";
	}

	@RequestMapping("/savaTempleDataLoad")
	public String savaTempleDataLoad(String paramStr, String templeId) {
		if (StringUtils.isNotBlank(paramStr)) {
			// 保存字段时先删除该模板中的字段
			String[] templePrimaryList = paramStr.split(";;");
			this.dataLoadBS.removeEntityByProperty("templeId", templeId);
			for (int i = 0; i < templePrimaryList.length; i++) {
				this.dataLoadBS.saveDataload(templePrimaryList[i], templeId);
			}
		} else {
			this.dataLoadBS.removeEntityByProperty("templeId", templeId);
		}
		return "true";
	}
	
	@RequestMapping(value = "/saveReWrite"  ,method = RequestMethod.POST)
	public void saveReWrite(@RequestBody TemplateRewriteTableVO templateRewriteTableVO){
		RptInputLstTempleInfo templeInfo = templeBS.getEntityById(templateRewriteTableVO.getTempleId());
		templateRewriteTableVO.setTempleNm(templeInfo.getTempleName());
		this.templeFieldBS.saveReWrite( templateRewriteTableVO);
	}
	
	@RequestMapping(value = "/getRewriteByTempleId", method = RequestMethod.POST)
	@ResponseBody
	public RptInputRewriteTempleInfo getRewriteByTempleId(String templeId) {
		return templeRewriteBS
				.getRptInputRewriteTempleInfoByTempleId(templeId);
	}
	
	/**
	 * 第三步
	 * 
	 * @return
	 */
	@RequestMapping(value = "/tab3/{id}")
	public ModelAndView tab3(@PathVariable("id") String id) {
		id = StringUtils2.javaScriptEncode(id);
		return new ModelAndView(
				"/input/temple/temple-tab3", "id", id);
	}

	/**
	 * 获取该ID下的补录excel模板
	 */
	@RequestMapping("/listFile.*")
	@ResponseBody
	public Map<String, Object> listFile(Pager pager, String id) {

		SearchResult<RptInputLstTempleFile> searchResult = this.templeFileBS
				.getTempleFileList(pager.getPageFirstIndex(),
						pager.getPagesize(), pager.getSortname(),
						pager.getSortorder(), pager.getSearchCondition(), id);

		Map<String, Object> objDefMap = Maps.newHashMap();
		objDefMap.put("Rows", searchResult.getResult());
		objDefMap.put("Total", searchResult.getTotalCount());
		return objDefMap;
	}

	/**
	 * 获取该ID下的补录excel数据
	 */
	@RequestMapping("/listDataFile.*")
	@ResponseBody
	public Map<String, Object> listDataFile(Pager pager, String id,
			String caseId) {
		String user = BioneSecurityUtils.getCurrentUserInfo().getLoginName();
		SearchResult<RptInputListDataFile> searchResult = this.dataFileBS
				.getDataFileList(pager.getPageFirstIndex(),
						pager.getPagesize(), pager.getSortname(),
						pager.getSortorder(), pager.getSearchCondition(), id,
						user, caseId);

		Map<String, Object> objDefMap = Maps.newHashMap();
		objDefMap.put("Rows", searchResult.getResult());
		return objDefMap;
	}
	
	/**
     * 导入补录模版数据
     * 
     * @param dirId
     * @return
     */
    @RequestMapping(value = "/tmpExcel/{id}", method = RequestMethod.GET)
    public ModelAndView userUploadTmpExcel(@PathVariable("id")String tmpId) {
    	tmpId = StringUtils2.javaScriptEncode(tmpId);
        return new ModelAndView("/input/temple/temple-tab3-UserUploadExcel", "tmpId", tmpId);
    }
    
    /**
     * 导入补录模版数据
     * 
     * @param dirId
     * @return
     */
    @RequestMapping(value = "/tmpExcel/upload", method = RequestMethod.POST)
    @ResponseBody
    private Map<String,Object> uploadTmpExcel(Uploader uploader, String tmpId) {
        File file = null;
        try {
            file = this.uploadFile(uploader, GlobalConstants4frame.LOGIC_SYS_IMPORT_PATH + File.separatorChar, false);
        } catch (Exception e) {
            logger.info("文件上传出现异常", e);
            e.printStackTrace();
        }
        if (file != null) {
            Map<String,Object> resMap = Maps.newHashMap();
//            FileInputStream is = null;
//            HSSFWorkbook workbook = null;
            try {
//                is = new FileInputStream(file); //文件流  
//                workbook = new HSSFWorkbook(is);      
//                HSSFSheet sheet = workbook.getSheetAt(0);
                
//                if(!sheet.getProtect() || 
//                        PasswordRecord.hashPassword(tmpId) != sheet.getPassword()){
//                    resMap.put("msg", "模板校验失败,请使用系统生成模板编辑并上传.");
//                    resMap.put("status", false);
//                    return resMap;
//                }
                
            } catch (Exception e) {
                e.printStackTrace();
            }finally{
//                if(is != null){
//                    try {
//                        is.close();
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    }
//                }
            }
            String templeUrl = null;
            try {
                templeUrl = templeBS.getRemoteUrl() + File.separator;
            } catch (Exception e) {
                logger.info("文件上传出现异常", e);
                e.printStackTrace();
            }
            String fileName = file.getName().replaceAll(" ", "").substring(0,file.getName().lastIndexOf("."));
            String fileType = "." + FilesUtils.getFileExt(file.getName());
            
            //防止文件重名
            File existsFile = new File(templeUrl + fileName + fileType);
            int i = 1;
            while(existsFile.exists()){
                existsFile = new File(templeUrl + fileName + "(" + i++ + ")" +fileType);
            }
            log.info("上传模板路径：" + templeUrl);
            try {
				FilesUtils.copyFile(file, existsFile);
				RptInputLstTempleFile templeFile = new RptInputLstTempleFile();
                templeFile.setTempleId(tmpId);
                templeFile.setCreateDate(DateUtils.formatDate(new Date(), "yyyy-MM-dd"));
                templeFile.setFileName(existsFile.getName());
                templeFile.setFilePath(existsFile.getPath());
                templeFile.setFileId(RandomUtils.uuid2());
                templeBS.setTempleFile(templeFile);
                logger.info("文件[" + file.getName() + "]上传完成");
                resMap.put("status", true);
                resMap.put("msg", "导入成功!");
				
			} catch (IOException e) {
				resMap.put("status", false);
				resMap.put("msg", "导入失败!");
				e.printStackTrace();
			}
        }
        return null;
    }
    

	/**
	 * 生成excel文件
	 * 
	 * @param id
	 */
	@RequestMapping(value = "/excel_upload/{id}", method = RequestMethod.POST)
	@ResponseBody
	public void excelUpload(@PathVariable("id") String id) {
		this.templeBS.excelUps(id);
	}

	/**
	 * 模板定制初始页中的excel模板下载
	 * 
	 * @param response
	 * @param id
	 *            模板Id
	 */
	@RequestMapping(value = "/excel_down_temp_index/{id}")
	public void excelDown2(HttpServletResponse response,
			@PathVariable("id") String id) {
		try {
			List<RptInputLstTempleFile> templeFile = this.templeFileBS
					.findEntityListByProperty("templeId", id);
			for (RptInputLstTempleFile tempFile : templeFile) {
				if (UdipConstants.TASK_STATE_USING.equals(tempFile.getSts())) {
					excelDown(response, tempFile.getFileId());
					continue;
				}
			}
			// RptInputLstTempleInfo tempInfo = templeBS.getEntityById(id);

//			BioneUser user = BioneSecurityUtils.getCurrentUserInfo();
//			StringBuilder buff = new StringBuilder();
//			buff.append("用户[").append(user.getLoginName()).append("]下载了模板:").append(tempInfo.getTempleName());
//			templeBS.saveLog("02", "明细补录模板", buff.toString(), user.getUserId(), user.getLoginName());
		} catch (Exception e) {
			log.error(e);
		}
	}

	/**
	 * 下载excel文件
	 * 
	 * @param response
	 * @param id
	 * @throws Exception
	 */
	@RequestMapping(value = "/excel_down/{id}")
	public void excelDown(HttpServletResponse response,
			@PathVariable("id") String id) {
		File file = null;
		try {
			RptInputLstTempleFile templeFile = this.templeFileBS
					.getEntityById(id);
			file = new File(templeFile.getFilePath());
			DownloadUtils.download(response, file, templeFile.getFileName()); //
			// 提供下载
//			logBS.addLog(this.getRequest().getRemoteAddr(), BioneSecurityUtils
//					.getCurrentUserInfo().getCurrentLogicSysNo(),
//					BioneSecurityUtils.getCurrentUserInfo().getUserId(),
//					"下载补录模板【" + templeFile.getFileName() + "】文件");
		} catch (Exception e) {
			log.error(e);
		} finally {
//			if (file != null) {
//				file.delete();
//			}
		}
	}

	/**
	 * 下载excel文件
	 * 
	 * @param response
	 * @param id
	 * @throws Exception
	 */
	@RequestMapping(value = "/excel_down_data/{id}")
	public void excel_down_data(HttpServletResponse response,
			@PathVariable("id") String id) {
		File file = null;
		try {
			String templeNames = "";
			int n = 1;
			List<RptInputLstTempleFile> templeFileList = this.templeFileBS
					.findEntityListByProperty("templeId", id);
			for (RptInputLstTempleFile templeFile : templeFileList) {
				if (templeFile.getSts().equals("1")) {
					file = new File(templeFile.getFilePath()
							+ templeFile.getFileName());
					// file = smbFileUpAndDown.getFileByPath(
					// templeFile.getFilePath(),
					// templeFile.getFileName());
					DownloadUtils.download(response, file,
							templeFile.getFileName());
					if (n == 1) {
						templeNames = templeFile.getFileName();
					} else {
						templeNames = templeNames + ","
								+ templeFile.getFileName();
					}
					n++;
				}
			}
//			logBS.addLog(this.getRequest().getRemoteAddr(), BioneSecurityUtils
//					.getCurrentUserInfo().getCurrentLogicSysNo(),
//					BioneSecurityUtils.getCurrentUserInfo().getUserId(),
//					"下载补录模板【" + templeNames + "】信息");
		} catch (Exception e) {
			log.error(e);
		} finally {
			if (file != null) {
				file.delete();
			}
		}

	}

	//
	// /**
	// * 获取补录模板的远程文件连接路径
	// * @param smbType
	// * @return
	// * @throws Exception
	// */
	// public static String getRemoteUrl(String smbType) throws Exception {
	// SmbServerBS smbBS = (SmbServerBS)
	// SpringContextHolder.getBean("smbServerBS");
	// SearchResult<UdipSmbSrvInfo> result = smbBS.getByTypeAndLogicNo(smbType,
	// BiOneSecurityUtils.getCurrentUserInfo().getCurrentLogicSysNo());
	//
	// if (result != null) {
	// UdipSmbSrvInfo info = result.getResult().get(0);
	// return SambaUtil.getPath(info);
	// } else {
	// throw new Exception("没有文件服务器!");
	// }
	// }
	//
	// /**
	// * 删除excel文件与记录
	// * @param id
	// * @return
	// */
	// @RequestMapping(value = "/templeFile/{id}", method =
	// RequestMethod.DELETE)
	// @ResponseBody
	// public String deleteFile(@PathVariable("id") String id) {
	// try {
	// String[] ids = id.split(",");
	//
	// for (int i = 0; i < ids.length; i++) {
	// UdipTempleFile templeFile = this.templeFileBS.getEntityById(ids[i]);
	//
	// smbFileUpAndDown.deleteFile(templeFile.getFilePath());
	// this.templeFileBS.removeEntityById(ids[i]);
	// logBS.addLog(this.getRequest().getRemoteAddr(),
	// BiOneSecurityUtils.getCurrentUserInfo().getCurrentLogicSysNo(),
	// BiOneSecurityUtils.getCurrentUserInfo().getUserId(), "删除补录模版【" +
	// templeFile.getTempleName() + "】");
	// }
	// } catch (Exception e) {
	// return "删除失败！";
	// }
	// return "删除成功！";
	// }
	//

	/**
	 * 删除excel文件与记录
	 * 
	 * @param id
	 * @return
	 */
	@RequestMapping(value = "/templeFile/{id}", method = RequestMethod.POST,produces = "text/html;charset=UTF-8")
	@ResponseBody
	public String deleteFile(@PathVariable("id") String id) {
		try {
			String[] ids = id.split(",");

			for (int i = 0; i < ids.length; i++) {
				RptInputLstTempleFile templeFile = this.templeFileBS
						.getEntityById(ids[i]);
				// 删除文件
				File fileTmp = new File(templeFile.getFilePath());
				fileTmp.delete();
				this.templeFileBS.removeEntityById(ids[i]);
//				logBS.addLog(this.getRequest().getRemoteAddr(),
//						BioneSecurityUtils.getCurrentUserInfo()
//								.getCurrentLogicSysNo(), BioneSecurityUtils
//								.getCurrentUserInfo().getUserId(), "删除补录模版【"
//								+ templeFile.getFileName() + "】");
			}
		} catch (Exception e) {
			return "删除失败！";
		}
		return "删除成功！";
	}

	/**
	 * 删除excel文件与记录
	 * 
	 * @param id
	 * @return
	 */
	@RequestMapping(value = "/deleteDataFile/{id}", method = RequestMethod.POST)
	@ResponseBody
	public String deleteDataFile(@PathVariable("id") String id) {
		try {
			String[] ids = id.split(",");

			for (int i = 0; i < ids.length; i++) {
				RptInputListDataFile templeFile = this.dataFileBS
						.getEntityById(ids[i]);
				File file = new File(templeFile.getFilePath()
						+ templeFile.getFileName());
				file.delete();
				// smbFileUpAndDown.deleteFile(templeFile.getFilePath() +
				// templeFile.getFileName());
				this.dataFileBS.removeEntityById(ids[i]);
//				logBS.addLog(this.getRequest().getRemoteAddr(),
//						BioneSecurityUtils.getCurrentUserInfo()
//								.getCurrentLogicSysNo(), BioneSecurityUtils
//								.getCurrentUserInfo().getUserId(), "文件【"
//								+ templeFile.getFileName() + "】删除成功");
			}
		} catch (Exception e) {
			return "删除失败！";
		}
		return "删除成功！";
	}

	/**
	 * 更改模板状态
	 * 
	 * @param pager
	 * @param id
	 * @return
	 */
	@RequestMapping(value = "/chackTempleState/{id}")
	@ResponseBody
	public String chackTempleState(Pager pager, @PathVariable("id") String id) {
		try {
			RptInputLstTempleInfo inputTask = this.templeBS.getEntityById(id);
			
			if (inputTask.getTempleSts().equals(UdipConstants.TASK_STATE_USING)) {
				inputTask.setTempleSts(UdipConstants.TASK_STATE_STOP);
				this.templeBS.saveOrUpdateEntity(inputTask);
			} else if (inputTask.getTempleSts().equals(
					UdipConstants.TASK_STATE_STOP)) {
				inputTask.setTempleSts(UdipConstants.TASK_STATE_USING);
				this.templeBS.saveOrUpdateEntity(inputTask);
			}
			
//			BioneUser user = BioneSecurityUtils.getCurrentUserInfo();
//			StringBuilder buff = new StringBuilder();
//			buff.append("用户[").append(user.getLoginName()).append("]修改了模板:").append(inputTask.getTempleName()).append("的状态为");
//			buff.append(inputTask.getTempleSts().equalsIgnoreCase(UdipConstants.TASK_STATE_USING)?"启用":"停用");
//			templeBS.saveLog("02", "明细补录模板", buff.toString(), user.getUserId(), user.getLoginName());
			
		} catch (Exception e) {
			log.error(e);
		}
//		logBS.addLog(this.getRequest().getRemoteAddr(), BioneSecurityUtils
//				.getCurrentUserInfo().getCurrentLogicSysNo(),
//				BioneSecurityUtils.getCurrentUserInfo().getUserId(),
//				"更改补录模版启用停用状态");
		return "更改状态成功";
	}

	/**
	 * 检查模板excel状态并生成excel模板
	 * 
	 * @param pager
	 * @param id
	 * @return
	 */
	@RequestMapping(value = "/chackFileState/{id}",produces = "text/html;charset=UTF-8")
	@ResponseBody
	public String chackState(Pager pager, @PathVariable("id") String id) {
		try {
			RptInputLstTempleFile templeFile = this.templeFileBS
					.getEntityById(id);
			templeFileBS.setFileDisable(templeFile.getTempleId());
			if (templeFile.getSts().equals(InputConstants.TASK_STATE_USING)) {
				templeFile.setSts(InputConstants.TASK_STATE_STOP);
				this.templeFileBS.saveOrUpdateEntity(templeFile);
			} else if (templeFile.getSts().equals(
					InputConstants.TASK_STATE_STOP)) {
				templeFile.setSts(InputConstants.TASK_STATE_USING);
				this.templeFileBS.saveOrUpdateEntity(templeFile);
			}
		} catch (Exception e) {
			log.error(e);
		}
//		logBS.addLog(this.getRequest().getRemoteAddr(), BioneSecurityUtils
//				.getCurrentUserInfo().getCurrentLogicSysNo(),
//				BioneSecurityUtils.getCurrentUserInfo().getUserId(),
//				"更改补录模版启用停用状态");
		return "更改状态成功";
	}

	/**
	 * 判断模版是否存在excel模板
	 * 
	 * @param id
	 * @return
	 */
	@RequestMapping(value = "/templeFileExit/{id}")
	@ResponseBody
	public int templeFileExit(@PathVariable("id") String id) {
		try {
			int i = 0;
			List<RptInputLstTempleFile> templeFile = this.templeFileBS
					.findEntityListByProperty("templeId", id);
			for (RptInputLstTempleFile file : templeFile) {
				if ("1".equals(file.getSts())) {
					i = i + 1;
				}
			}
			return i;
		} catch (Exception e) {
			log.error(e);
			return 0;
		}
	}
	
	@RequestMapping("/task/temple")
	@ResponseBody
	public String getTempleByTask(String taskId) {
		if (StringUtils.isNotEmpty(taskId)) {
		    try {
		        Object[] result = (Object[])templeBS.getTemplePathByTaskId(taskId);
		        return String.valueOf(result[0]);
            } catch (Exception e) {
                return "";
            }
		}
		return "";
	}
	

	/**
	 * 第四步
	 * 
	 * @return
	 */
	@RequestMapping(value = "/tab4/{id}")
	public ModelAndView tab4(@PathVariable("id") String id) {
		id = StringUtils2.javaScriptEncode(id);
		return new ModelAndView(
				"/input/temple/temple-tab4", "id", id);
	}
	
	/**
	 * 第四步
	 * 
	 * @return
	 */
	@RequestMapping(value = "/setReWrite")
	public ModelAndView setReWrite(String templeId) {
		templeId = StringUtils2.javaScriptEncode(templeId);
		return new ModelAndView("/input/temple/temple-rewrite", "templateId", templeId);
	}
	
	@RequestMapping(value = "/getDsList.*", method = RequestMethod.POST)
	@ResponseBody
	public List<CommonComboBoxNode> getDsList() {
		return templeBS.getDsList();
	}
	

	/**
	 * 展示列信息界面cl
	 * @param dsId
	 * @param tableNm
	 * @return
	 */
	@RequestMapping(value = "/editRewritecolumn", method = RequestMethod.GET)
	public ModelAndView editRewritecolumn(String dsId,String tableNm,String templateId){
		ModelMap mm = new ModelMap();
		mm.put("dsId", StringUtils2.javaScriptEncode(dsId));
		mm.put("tableNm", StringUtils2.javaScriptEncode(tableNm));
		mm.put("templateId", StringUtils2.javaScriptEncode(templateId));
		return new ModelAndView("/input/temple/template-column-rewrite",mm);
	}

	//
	// /**
	// * 获取补录ID的所有rules
	// * @param rf
	// * @param id
	// * @return
	// */
	// @RequestMapping("/listRule")
	// @ResponseBody
	// public Map<String, Object> listRule(Pager rf, String id) {
	// List<UdipTempleRules> listRules =
	// this.templeRulesBS.findEntityListByProperty("templeId", id);
	// List<UdipDataRulesInfo> dataRulesList = Lists.newArrayList();
	// for (UdipTempleRules list : listRules) {
	// UdipDataRulesInfo data = new UdipDataRulesInfo();
	// data = this.dataRulesBS.getEntityById(list.getRuleId());
	// dataRulesList.add(data);
	// }
	//
	// Map<String, Object> objDefMap = Maps.newHashMap();
	// objDefMap.put("Rows", dataRulesList);
	// objDefMap.put("Total", dataRulesList.size());
	// return objDefMap;
	// }
	//
	// /**
	// * 获取所有模板数据
	// */
	// @RequestMapping("/getAlltempleInfo.*")
	// @ResponseBody
	// public Map<String, Object> getAlltempleInfo(Pager rf) {
	// SearchResult<UdipTemple> searchResult =
	// this.templeBS.getAllTempleList(rf.getPageFirstIndex(), rf.getPagesize(),
	// rf.getSortname(), rf.getSortorder(), rf.getSearchCondition());
	// Map<String, Object> objDefMap = Maps.newHashMap();
	// objDefMap.put("Rows", searchResult.getResult());
	// objDefMap.put("Total", searchResult.getTotalCount());
	// return objDefMap;
	// }
	//
	// /**
	// * 补录任务中查找模板有邮箱
	// */
	// @RequestMapping(value = "/findTempleAndName/{id}")
	// @ResponseBody
	// public UdipTemple findDirIdAndName(@PathVariable("id") String id) {
	// List<UdipTaskTempleInfo> taskTample =
	// udipTaskTempleBS.findEntityListByProperty("taskId", id);
	// UdipTemple temple = new UdipTemple();
	// StringBuilder templeName = new StringBuilder();
	// StringBuilder templeId = new StringBuilder();
	// for (UdipTaskTempleInfo list : taskTample) {
	// templeId.append(",");
	// templeId.append(list.getTempleId());
	// templeName.append(",");
	// UdipTemple temp = templeBS.getEntityById(list.getTempleId());
	// if(temp!=null)
	// templeName.append(temp.getTempleName());
	// }
	// temple.setTempleName(templeName.deleteCharAt(0).toString());
	// temple.setTempleId(templeId.deleteCharAt(0).toString());
	//
	// UdipInputTaskInfo task= udipTaskBS.findUniqueEntityByProperty("taskId",
	// id);
	// if(task != null){
	// UdipMailGroupInfo group = mailGroupBS.getEntityById(task.getMailId());
	// // 用来暂时存放邮件Id
	// temple.setCreator(task.getMailId());
	// // 用来暂时存放邮件名称
	// temple.setCreateDate(group==null?"":group.getGroupName());
	// }
	// return temple;
	// }
	//
	/**
	 * 补录模板的树
	 * 
	 * @return
	 */
	@RequestMapping("/templeTree.json")
	@ResponseBody
	public List<CommonTreeNode> templeTree() {
		String logic = BioneSecurityUtils.getCurrentUserInfo()
				.getCurrentLogicSysNo();
		// List<String> list = BioneSecurityUtils.getCurrentUserInfo();
		List<CommonTreeNode> tempList = templeBS.buildTempleTree(logic, null);
		return tempList;
	}

	//
	// /**
	// * 根据taskID查找补录表的下拉框
	// * @param rf
	// * @param id
	// * @return
	// */
	// @RequestMapping(value = "/getTempleComboxList/{id}")
	// @ResponseBody
	// public List<Map<String, String>> getColumnList(Pager rf,
	// @PathVariable("id") String id) {
	// List<Map<String, String>> columnList = Lists.newArrayList();
	//
	// SearchResult<UdipTaskTempleInfo> searchResult =
	// this.udipTaskTempleBS.getAllTempleIdList(rf.getPageFirstIndex(),
	// rf.getPagesize(), rf.getSortname(), rf.getSortorder(),
	// rf.getSearchCondition(), id);
	// List<UdipTaskTempleInfo> list = searchResult.getResult();
	// for (int i = 0; i < list.size(); i++) {
	// UdipTemple temple =
	// this.templeBS.getEntityById(list.get(i).getTempleId());
	// Map<String, String> m = Maps.newHashMap();
	// m.put("text", temple.getTempleName());
	// m.put("id", temple.getTempleId());
	// columnList.add(i, m);
	// }
	// return columnList;
	// }
	//
	/**
	 * 检出模版是否有启用excel文件
	 * 
	 * @param templeId
	 * @return
	 */
	@RequestMapping(value = "/findTempleFile")
	@ResponseBody
	public Boolean findTempleFile(String templeId) {
		Boolean chack = false;
		if (StringUtils.isNotBlank(templeId)) {
			String[] tempId = templeId.split(",");
			for (String id : tempId) {
				List<RptInputLstTempleFile> udipAuthRecord = templeFileBS
						.findEntityListByProperty("templeId", id);
				if (udipAuthRecord.isEmpty()) {
					chack = false;
				} else {
					if (templeBS.getEntityById(id).getTempleSts()
							.equals(UdipConstants.TASK_STATE_USING)) {
						for (int i = 0; i < udipAuthRecord.size(); i++) {
							if (udipAuthRecord.get(i).getSts()
									.equals(UdipConstants.TASK_STATE_USING)) {
								chack = true;
								break;
							} else {
								chack = false;
							}
						}

					}
				}
			}

		} else {
			chack = true;
		}
		return chack;

	}

	/**
	 * 检查
	 * 
	 * @param templeId
	 * @return
	 */
	@RequestMapping(value = "/chackTempleTask")
	@ResponseBody
	public String chackTempleTask(String templeId) {

		// try {
		// if (StringUtils.isNotBlank(templeId)) {
		// String[] ids = templeId.split(",");
		// for (int i = 0; i < ids.length; i++) {
		// List<Rptinputlistta> taskTemple = this.udipTaskTempleBS
		// .findEntityListByProperty("templeId", ids[i]);
		// if (!taskTemple.isEmpty()) {
		// for (UdipTaskTempleInfo tasktemp : taskTemple) {
		// UdipInputTaskInfo inputTask = udipTaskBS
		// .getEntityById(tasktemp.getTaskId());
		// if (inputTask != null
		// && inputTask.getTaskState().equals(
		// UdipConstants.TASK_STATE_USING)) {
		// return "fail";
		// }
		// }
		// }
		// }
		// }
		//
		// } catch (Exception e) {
		// return "fail";
		// }
		return "ok";
	}

	// @RequestMapping("/templeLock")
	// public ModelAndView templeLock(String caseId){
	// return new ModelAndView("/udip/temple/task-temple-lock", "caseId",
	// caseId);
	//
	// }
	// @RequestMapping(value = "/setTaskTempleLock")
	// @ResponseBody
	// public String setTaskTempleLock(String caseId,String templeId){
	// try {
	// templeBS.setTaskTempLock(caseId,templeId);
	// logBS.addLog(this.getRequest().getRemoteAddr(),
	// BiOneSecurityUtils.getCurrentUserInfo().getCurrentLogicSysNo(),
	// BiOneSecurityUtils.getCurrentUserInfo().getUserId(), "设置实例【" + caseId +
	// "】对应的锁表");
	// } catch (Exception e) {
	// logger.error(e.getMessage());
	// return "设置失败！";
	// }
	// return "ok";
	// }
	// @RequestMapping(value = "/getTaskTempleLock")
	// @ResponseBody
	// public List<String> getTaskTempleLock(String caseId){
	// try {
	// return templeBS.getTaskTempLock(caseId);
	// } catch (Exception e) {
	// logger.error(e.getMessage());
	// return null;
	// }
	// }
	
	/**
	 * 查询模板是否已包含约束信息
	 * 
	 * @param templeId
	 * @return
	 */
	@RequestMapping(value = "/chackPrimaryKey")
	@ResponseBody
	public String chackPrimaryKey(String templeId) {
		List<RptInputLstTempleConst> list = templeConstraintBS.findEntityListByProperty("templeId", templeId);
		if (list.isEmpty()) {
			return "";
		}
		return templeId;
	}
	

	
	@RequestMapping(value = "/changeIsStart", method = RequestMethod.POST)
	@ResponseBody
	public String changeIsStart(String templeId,String isStart) {
		String value = "1";
		if(StringUtils.isNotEmpty(isStart)&&(isStart.equals("1")||isStart.equals("undefined")))
			value = "2";
		templeBS.updateIsStart(templeId,value);
		// RptInputLstTempleInfo tempInfo = this.templeBS.getEntityById(templeId);
//		BioneUser user = BioneSecurityUtils.getCurrentUserInfo();
//		StringBuilder buff = new StringBuilder();
//		buff.append("用户[").append(user.getLoginName()).append("]修改了模板:").append(tempInfo.getTempleName()).append(" 的回写状态为")
//		.append(value.equals("1")?"启动回写":"取消回写");
//		templeBS.saveLog("03", "明细补录模板", buff.toString(), user.getUserId(), user.getLoginName());
		return "succ";
	}

	
	@RequestMapping(value = "/changeIsSendNotify", method = RequestMethod.POST)
	@ResponseBody
	public String changeIsSendNotify(String templeId,String isSendNotify) {
		String value = "2";
		if(StringUtils.isNotEmpty(isSendNotify)&&(isSendNotify.equals("2")||isSendNotify.equals("undefined")))
			value = "1";
		templeBS.updateIsSendNotify(templeId,value);
		// RptInputLstTempleInfo tempInfo = this.templeBS.getEntityById(templeId);
//		BioneUser user = BioneSecurityUtils.getCurrentUserInfo();
//		StringBuilder buff = new StringBuilder();
//		buff.append("用户[").append(user.getLoginName()).append("]修改了模板:").append(tempInfo.getTempleName()).append(" 的短信发送状态为")
//		.append(value.equals("1")?"发送短信":"不发送短信");
//		templeBS.saveLog("03", "明细补录模板", buff.toString(), user.getUserId(), user.getLoginName());
		return "succ";
	}

	/**
	 * 跳转到查看关联的补录模版页面
	 * 
	 * @param id
	 * @return
	 */
	@RequestMapping(value = "/selectField", method = RequestMethod.GET)
	public ModelAndView selectField(String templateId,String dataType) {
		ModelMap mm = new ModelMap();
		mm.put("templateId", StringUtils2.javaScriptEncode(templateId));
		mm.put("dataType", StringUtils2.javaScriptEncode(dataType));
		return new ModelAndView("/input/temple/temple-selectfield", mm);
	}
	
	@RequestMapping(value = "/getFiledList", method = RequestMethod.POST)
	public Map<String, Object> getFiledList(String templateId,String dataType) {
		List<RptInputLstTempleField> fieldList = templeFieldBS.getTempleColumns(templateId);
		Map<String, Object> rsMap = Maps.newHashMap();
		//用if(field.getFieldType().equalsIgnoreCase(dataType))做判断，不同库的字段类型不同，如varchar和varchar2 20191028
//		List<RptInputLstTempleField> filterList = Lists.newArrayList();
//		for(RptInputLstTempleField field:fieldList){
//			if(field.getFieldType().equalsIgnoreCase(dataType))
//			{
//				filterList.add(field);
//			}
//		}
//		rsMap.put("Rows", filterList);
//		rsMap.put("Total", fieldList.size());
		
		rsMap.put("Rows", fieldList);
		rsMap.put("Total", fieldList.size());
		return rsMap;
	}
	/**
	 * 部门显示视树，初始方法
	 * 
	 * @return 
	 * 		RestFul的增强结果类型
	 */
	@RequestMapping("/asyncOrgTree")
	public ModelAndView asyncOrgTree(String multi) {
		 ModelMap mm = new ModelMap();
		 mm.put("objDefNo", "AUTH_OBJ_ORG");
		 if(StringUtils.isEmpty(multi))
			 multi = "0";
		 mm.put("multi", StringUtils2.javaScriptEncode(multi));
		return new ModelAndView("/bione/org/org-asyuc-tree",mm);
	}
	@RequestMapping("/asyncUserTree")
	public ModelAndView asyncUserTree(String multi,String isyg) {
		 ModelMap mm = new ModelMap();
		 if(StringUtils.isNotEmpty(isyg))
			 mm.put("isyg", StringUtils2.javaScriptEncode(isyg));
		 mm.put("objDefNo", "AUTH_OBJ_USER");
		 if(StringUtils.isEmpty(multi))
			 multi = "0";
		 mm.put("multi", StringUtils2.javaScriptEncode(multi));
		return new ModelAndView("/bione/user/user-asyuc-tree",mm);
	}
	
	@RequestMapping(value = "/templeSelUser")
	public ModelAndView dirEdit() {
		return new ModelAndView("/input/temple/temple-sel-user");
	}
	// /**
	// * 执行查看前页面跳转
	// */
	// @RequestMapping("/viewtemple")
	// public ModelAndView viewtemple(String id, String lookType) {
	// ModelMap mm = new ModelMap();
	// mm.addAttribute("id", id);
	// mm.addAttribute("lookType", lookType);
	// return new ModelAndView("/udip/temple/temple-view-tab", mm);
	// }
	//
	// /**
	// * 第一步
	// * @return
	// */
	// @RequestMapping(value = "/view-tab1/{id}")
	// public ModelAndView viewtab1(@PathVariable("id") String id) {
	// return new ModelAndView("/udip/temple/temple-view-tab1", "id", id);
	//
	// }
	//
	// /**
	// * 第二步
	// * @return
	// */
	// @RequestMapping(value = "/view-tab2/{id}")
	// public ModelAndView viewtab2(@PathVariable("id") String id) {
	// return new ModelAndView("/udip/temple/temple-view-tab2", "id", id);
	// }
	//
	// /**
	// * 第三步
	// * @return
	// */
	// @RequestMapping(value = "/view-tab3/{id}")
	// public ModelAndView viewtab3(@PathVariable("id") String id) {
	// return new ModelAndView("/udip/temple/temple-view-tab3", "id", id);
	// }
	//
	// /**
	// * 第四步
	// * @return
	// */
	// @RequestMapping(value = "/view-tab4/{id}")
	// public ModelAndView viewtab4(@PathVariable("id") String id) {
	// return new ModelAndView("/udip/temple/temple-view-tab4", "id", id);
	// }
}
