package com.yusys.biapp.input.dict.web;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.yusys.bione.comp.utils.*;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
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
import com.yusys.biapp.input.dict.entity.RptInputListDataDictInfo;
import com.yusys.biapp.input.dict.service.InputDataDictBS;
import com.yusys.biapp.input.dict.utils.UdipConstants;
import com.yusys.biapp.input.dict.vo.InputListDataDictInfoVO;
import com.yusys.biapp.input.input.service.TaskCaseBS;
import com.yusys.biapp.input.task.entity.RptTskIns;
import com.yusys.biapp.input.template.entity.RptInputLstTempleField;
import com.yusys.biapp.input.template.entity.RptInputLstTempleInfo;
import com.yusys.biapp.input.template.service.TempleBS;
import com.yusys.biapp.input.template.service.TempleFieldBS;
import com.yusys.biapp.input.utils.ExcelConstants;
import com.yusys.bione.comp.common.CommonTreeNode;
import com.yusys.bione.comp.entity.page.Pager;
import com.yusys.bione.comp.entity.upload.Uploader;
import com.yusys.bione.comp.repository.jdbc.entity.BioneColumnMetaData;
import com.yusys.bione.comp.repository.jpa.SearchResult;
import com.yusys.bione.frame.base.web.BaseController;
import com.yusys.bione.frame.mtool.service.DataSourceBS;
import com.yusys.bione.frame.security.BioneSecurityUtils;
import com.yusys.bione.frame.security.BioneUser;
import com.yusys.bione.frame.user.entity.BioneUserInfo;

@Controller
@RequestMapping("/rpt/input/library")
public class DataListDictController extends BaseController{
	private final Log log = LogFactory.getLog(DataListDictController.class);
	private final String fileNameInfo = "data_dict.xls";
	private final String SPLITCHAR = ";;";
	private final String dictPath = File.separator + "export" + File.separator + "input" + File.separator + "dict" + File.separator;
	private final String dictImportPath = dictPath + "import";
	private final String dictTemplatePath = dictPath + fileNameInfo;
//	@Autowired
//	private LogBS logBS;
	@Autowired
	private TaskCaseBS taskCaseBS;
	@Autowired
	private TempleFieldBS templateFieldBS;
	@Autowired
	private ExcelConstants excelConstants;
	@Autowired
	private InputDataDictBS inputDataDictBS;
	@Autowired
	private DataSourceBS dsBS;
	@Autowired
	private TempleBS templeBS;
	@Autowired
	private DataSourceBS dataSource;
	
	@RequestMapping(method = RequestMethod.GET)
	public String index() {
		return "/input/library/lib-index";
	}

	/**
	 * ????????????????????????
	 * @param realId
	 * @param type
	 * @return
	 */
	@RequestMapping("/libTree.json")
	@ResponseBody
	public List<CommonTreeNode> templelistTree(String nodeId) {
		String logic = BioneSecurityUtils.getCurrentUserInfo().getCurrentLogicSysNo();
		return inputDataDictBS.buildLibTreeForDir(nodeId,logic);
	}

	/**
	 * ??????????????????grid?????????
	 */
	@RequestMapping("/list.*")
	@ResponseBody
	public Map<String, Object> list(Pager rf, String dirId,String dsId) {
		SearchResult<RptInputListDataDictInfo> searchResult = inputDataDictBS.getSearch(rf.getPageFirstIndex(), rf.getPagesize(), rf.getSortname(), rf.getSortorder(), rf.getSearchCondition(), dirId,true,dsId);
		Map<String, Object> objDefMap = Maps.newHashMap();
		List<RptInputListDataDictInfo> showDictInfos = searchResult.getResult();
		if(showDictInfos != null && showDictInfos.size() > 0){
			for (RptInputListDataDictInfo dictInfo : showDictInfos){
				BioneUserInfo userInfo  = inputDataDictBS.getEntityByProperty(BioneUserInfo.class, "userId", dictInfo.getCreateUser());
				if(userInfo!=null)
				dictInfo.setCreateUser(userInfo.getUserName());
				if(dictInfo.getDictType().equals("1") && StringUtils.isNotBlank(dictInfo.getStaticContent())){
					dictInfo.setSqlText(dictInfo.getStaticContent());
				}
			}
		}
		
		/*****?????????????????? *****/
		BioneUser user = BioneSecurityUtils.getCurrentUserInfo();
		StringBuilder buff = new StringBuilder();
		buff.append("??????[").append(user.getLoginName()).append("]").append("?????????????????????????????????????????????????????????");
		// Map<String, Object> cd = rf.getSearchCondition();
		// Map<String,String> filedInfo =  (Map<String, String>) cd.get("fieldValues");
		Map<String,String>nmMap=Maps.newHashMap();
		nmMap.put("dsId", "?????????");
		nmMap.put("dictName", "????????????");
		nmMap.put("dictType", "????????????");
		Map<String,Map<String,String>>exMap=Maps.newHashMap();
		
		Map<String,String>tmpMap=Maps.newHashMap();
		tmpMap.put("1", "??????");
		tmpMap.put("2", "????????????");
		exMap.put("dictType", tmpMap);
		
		objDefMap.put("Rows", searchResult.getResult());
		objDefMap.put("Total", searchResult.getTotalCount());
		return objDefMap;
	}
	
	@RequestMapping(value = "/newLibrary", method = RequestMethod.GET)
	public ModelAndView saveLibrary() {
		return new ModelAndView("/input/library/lib-add", "dirTypeRule", UdipConstants.DIR_TYPE_LIB);
	}
	
	@RequestMapping(value = "/update/{dictId}", method = RequestMethod.GET)
	public ModelAndView updateLibrary(@PathVariable("dictId") String dictId) {
		dictId = StringUtils2.javaScriptEncode(dictId);
		return new ModelAndView("/input/library/lib-edit", "dictId", dictId);
	}
	
	@RequestMapping(value = "/delete", method = RequestMethod.GET)
	public void deleteLibrary(String dictId) {
		if (dictId != null && !"".equals(dictId)) {
			String logicSysNo= BioneSecurityUtils.getCurrentUserInfo().getCurrentLogicSysNo();
			String[] dictIds = dictId.split(",");
			List<RptInputListDataDictInfo> dataDictInfoList = Lists.newArrayList();
			String libNames = "";
			for (int i = 0; i < dictIds.length; i++) {
				RptInputListDataDictInfo dataLib = inputDataDictBS.getEntityByProperty(RptInputListDataDictInfo.class, "dictId", dictIds[i]);
				dataDictInfoList.add(inputDataDictBS.getEntityById(dictId));
				if (i == 0) {
					libNames = dataLib.getDictName();
				} else {
					libNames = libNames + "," + dataLib.getDictName();
				}
			}
			
			/*****************??????????????????***********/
//			BioneUser user = BioneSecurityUtils.getCurrentUserInfo();
//			StringBuilder buff = new StringBuilder();
//			buff.append("??????[").append(user.getLoginName()).append("]?????????").append(dataDictInfoList.size()).append("???????????????,??????????????????:");
//			boolean isFirst=true;
//			for(RptInputListDataDictInfo dataDictInfo:dataDictInfoList)
//			{
//				if(isFirst)
//					isFirst=false;
//				else
//					buff.append(",");
//				buff.append("[").append(dataDictInfo.getDictName()).append("]");
//			}
//			inputDataDictBS.saveLog("02", "???????????? ", buff.toString(), user.getUserId(), user.getLoginName());
			inputDataDictBS.deleteDictData(logicSysNo, dictIds);
//			logBS.addLog(this.getRequest().getRemoteAddr(), logicSysNo, BioneSecurityUtils.getCurrentUserInfo().getUserId(), "?????????????????????" + libNames + "???");
		}
	}

	/**
	 * ??????????????????????????????????????????
	 * @param id
	 * @return
	 */
	@RequestMapping(value = "/{id}/queryTemp", method = RequestMethod.GET)
	public ModelAndView chack(@PathVariable("id") String id) {
		id = StringUtils2.javaScriptEncode(id);
		return new ModelAndView("/input/library/lib-temp-list", "id", id);
	}

	/**
	 * ???????????????????????????????????????
	 * @return
	 */
	@RequestMapping("/implibData")
	public ModelAndView implibData(String dictId) {
		dictId = StringUtils2.javaScriptEncode(dictId);
		return new ModelAndView("/input/library/file-imp-libdata", "dictId", dictId);
	}
	
	@RequestMapping("/libContent")
	public ModelAndView libContent(String content) {
		return new ModelAndView("/input/library/lib-addContent");

	}
	/**
	 * ??????????????????????????????????????????????????????????????????
	 * @param mode
	 * @return
	 */
	@RequestMapping("/checkLibName")
	@ResponseBody
	public boolean checkLibName(RptInputListDataDictInfo mode,String dictId) {
		List<RptInputListDataDictInfo> list = inputDataDictBS.findEntityListByProperty("dictName", mode.getDictName());
		if (list == null || list.isEmpty()) {
			return true;
		} else {
			if (StringUtils.isNotBlank(dictId)) {
				RptInputListDataDictInfo libInfo = list.get(0);
				RptInputListDataDictInfo udipLibInfo = this.inputDataDictBS.getEntityById(dictId);
				if (udipLibInfo != null && !udipLibInfo.getDictName().equalsIgnoreCase(libInfo.getDictName()))
					return false;
				else
					return true;
			}
			return false;
		}
	}
	/**
	 * ???????????????ID???????????????????????????????????????????????????
	 * @param dsId
	 * @param tableName
	 * @return
	 */

	@RequestMapping(value = "libHis", method = RequestMethod.GET)
	public String indexHis() {
		return "/input/library/lib-his-index";
	}

	/**
	 * ??????????????????grid?????????
	 */
	@RequestMapping("/all.*")
	@ResponseBody
	public Map<String, Object> all(Pager rf, String dirId,String dsId) {
		SearchResult<RptInputListDataDictInfo> searchResult = inputDataDictBS.getSearch(rf.getPageFirstIndex(), rf.getPagesize(), rf.getSortname(), rf.getSortorder(), rf.getSearchCondition(), dirId,true,dsId);
		Map<String, Object> objDefMap = Maps.newHashMap();
		objDefMap.put("Rows", searchResult.getResult());
		objDefMap.put("Total", searchResult.getTotalCount());
		return objDefMap;
	}
	
	/**
	 * ?????????????????????????????????ID
	 */
	@RequestMapping(value = "/{id}", method = RequestMethod.POST)
	@ResponseBody
	public void destroy(@PathVariable("id") String id) {
		inputDataDictBS.removeEntityById(id);
//		logBS.addLog(this.getRequest().getRemoteAddr(), BioneSecurityUtils.getCurrentUserInfo().getCurrentLogicSysNo(), BioneSecurityUtils.getCurrentUserInfo().getUserId(), "?????????????????????" + inputDataDictBS.getEntityByProperty(RptInputListDataDictInfo.class, "libId", id) + "???");
	}

	/**
	 * ??????dirId???dirName
	 */
	@RequestMapping(value = "/{id}/edit")
	@ResponseBody
	public RptInputListDataDictInfo findDirIdAndName(@PathVariable("id") String id) {
		RptInputListDataDictInfo model = inputDataDictBS.getEntityById(id);
		return model;
	}
	
	/**
	 * ??????libId?????????????????????
	 */
	@RequestMapping(value = "/findUdipDataById")
	@ResponseBody
	public InputListDataDictInfoVO findUdipDataById(String dictId) {
		InputListDataDictInfoVO model = this.inputDataDictBS.findUdipDataById(dictId);
		return model;
	}

	/**
	 * ??????ID???????????????
	 */
	@RequestMapping(value = "/{id}/new", method = RequestMethod.GET)
	@ResponseBody
	public RptInputListDataDictInfo show(@PathVariable("id") String id) {
		RptInputListDataDictInfo info = new RptInputListDataDictInfo();
		return info;
	}

	@RequestMapping("/libTreeByLibId.json")
	@ResponseBody
	public List<CommonTreeNode> templelistTreeByLibId(String libId, String dataDate,String orgId) {
		String dataDates = DateUtils.formatDate(FormatUtils.parseShortDate(dataDate), "yyyyMMdd");
		if(StringUtils.isNotBlank(orgId)){
			return inputDataDictBS.getNodeListById(libId, dataDates,orgId);
		}else{
			return inputDataDictBS.buildLibTreeByLibId(libId, dataDates);
		}
	}

	@RequestMapping("/libListByLibId.json")
	@ResponseBody
	public Map<String, Object> templelistByLibId(String libId, String dataDate, Pager rf) {
		Map<String, Object> objDefMap = Maps.newHashMap();
		String dataDates = "";
		if(StringUtils.isNotBlank(dataDate)){
			dataDates = DateUtils.formatDate(DateUtils.getDateStart(dataDate), "yyyyMMdd");
		}
		List<CommonTreeNode> dataLib = inputDataDictBS.buildLibTreeByLibId(libId, dataDates);
		int first = rf.getPageFirstIndex();
		int pageSize = rf.getPagesize();
		//????????????
		List<CommonTreeNode> dataLibList = Lists.newArrayList();
		if (first >= 0 && pageSize > 0 && !dataLib.isEmpty()) {
			if(first > dataLib.size())
				first = 0;
			if ((pageSize + first) > dataLib.size()) {
				dataLibList = dataLib.subList(first, dataLib.size());
			} else {
				dataLibList = dataLib.subList(first, pageSize + first);
			}

		} else {
			dataLibList = dataLib;
		}
		objDefMap.put("Rows", dataLibList);
		objDefMap.put("Total", dataLib.size());
		return objDefMap;
	}
	
	@RequestMapping("/libMapbyTempleId.json")
	@ResponseBody
	public Map<String, Map<String, String>> libMapbyTempleId(String templeId, String caseId) {
		// ????????????????????????
//		UdipTaskCaseInfo taskCase = this.udipTaskCaseBS.getEntityById(caseId);
//		List<UdipTempleColumns> listCol = this.templeColumnsBS.findEntityListByProperty("templeId", templeId);
//		Map<String, Map<String, String>> mapList = Maps.newHashMap();
//		for (UdipTempleColumns col : listCol) {
//			if (StringUtils.isNotBlank(col.getDataZidian())) {
//				List<CommonTreeNode> common = udipDataLibBS.buildLibTreeByLibId(col.getDataZidian(), taskCase.getEtlDate().replaceAll("-", ""));
//				Map<String, String> map = Maps.newHashMap();
//				for (CommonTreeNode comm : common) {
//					map.put(comm.getId(), comm.getText());
//				}
//				mapList.put(col.getDataZidian(), map);
//			}
//
//		}
		Map<String, Map<String, String>> mapList = Maps.newHashMap();
		return mapList;
	}

	/**
	 * ????????????????????????
	 * @param templeId
	 * @param caseId
	 * @return
	 */
	@RequestMapping("/libMapbyTempleId2.json")
	@ResponseBody
	public Map<String, List<Map<String, String>>> libMapbyTempleId2(String templeId, String caseId) {
		// ????????????????????????
		RptTskIns taskCase = null;
		if(StringUtils.isNotBlank(caseId)){
			taskCase = this.taskCaseBS.getEntityById(caseId);
		}
		List<RptInputLstTempleField> listCol = this.templateFieldBS.findEntityListByProperty("templeId", templeId);
		Map<String, List<Map<String, String>>> mapList = Maps.newHashMap();
		for (RptInputLstTempleField col : listCol) {
			if (StringUtils.isNotBlank(col.getDictId())) {
				List<CommonTreeNode> common = null;
				if (taskCase != null) {
					common = inputDataDictBS.buildLibTreeByLibId(col.getDictId(), taskCase.getDataDate().replaceAll("-", ""));
				} else {
					common = inputDataDictBS.buildLibTreeByLibId(col.getDictId(), "");
				}

				List<Map<String, String>> listmap = Lists.newArrayList();
				for (CommonTreeNode comm : common) {
					Map<String, String> map = Maps.newHashMap();
					map.put("id", comm.getId());
					map.put("text", comm.getText());
					map.put("upid", comm.getUpId());
					map.put(comm.getId(), comm.getText());
					listmap.add(map);
				}
				mapList.put(col.getDictId(), listmap);
			}
		}
		return mapList;
	}

	@RequestMapping(value = "/treeChange", method = RequestMethod.GET)
	public String getlistTrees() {
		return "/input/library/lib-dir-tree";
	}

	@RequestMapping(value = "/dataSource", method = RequestMethod.GET)
	public String getdataSource() {
		return "/input/library/lib-source";
	}
	private String [] splitSqlContent(String sql){
		// ???SQL?????????","?????????????????????
		BioneUser user = BioneSecurityUtils.getCurrentUserInfo();
		sql = sql.replaceAll("@USERORG", "'"+user.getOrgNo()+"'").replaceAll("@USERID", " '"+user.getUserId()+"' ");
		String [] column = sql.split(",");
		// column[0]???select +???Id?????????,??????id??????
		String id = column[0].split(" ")[1];
		String text = "";
		String upId = "";
		// ??????SQL??????????????????
		String [] fromStrings = column[1].split(" ");
		if(fromStrings.length > 4)	{
			//if(column[1].split(" ")[1].equalsIgnoreCase("from") && column[1].split(" ")[3].equalsIgnoreCase("from")){
				// ???????????????????????????SQL?????????????????????
				text = column[1].split(" ")[0];
				return new String []{id,text};
			//}
		}else if(fromStrings.length > 2){
			if(column[1].split(" ")[1].equalsIgnoreCase("from")){
				// ???????????????????????????SQL?????????????????????
				text = column[1].split(" ")[0];
				return new String []{id,text};
			}
		}
		// ??????SQL???????????????
		text = column[1].split(" ")[0];
		upId = column[2].split(" ")[0];
		return new String [] {id,text,upId};
	}
	
	/**
	 * ??????????????????
	 * 
	 * @param dictId
	 * @return
	 */
	@RequestMapping(value = "/query/{dictId}", method = RequestMethod.GET)
	public ModelAndView queryLibrary(@PathVariable("dictId") String dictId) {
		RptInputListDataDictInfo dataInputDictInfo = inputDataDictBS.getEntityByProperty(RptInputListDataDictInfo.class, "dictId", dictId);
		StringBuilder libId_date = new StringBuilder();
		libId_date.append(dictId);
		libId_date.append(SPLITCHAR).append("query");
		try {
			if (dataInputDictInfo.getDsId() != null && !"".equals(dataInputDictInfo.getDsId()) && dataInputDictInfo.getStaticContent() == null) {
				String[] sqlColumns = splitSqlContent(dataInputDictInfo.getSqlText());
				for(String column : sqlColumns){
					libId_date.append(SPLITCHAR).append(column);
				}
			} else if (dataInputDictInfo.getDsId() == null || "".equals(dataInputDictInfo.getDsId())) {
				libId_date.append(SPLITCHAR).append("??????");
				libId_date.append(SPLITCHAR).append("??????");
			}

			/********??????????????????**********/
//			BioneUser users = BioneSecurityUtils.getCurrentUserInfo();
//			StringBuilder buff = new StringBuilder();
//			buff.append("??????[").append(users.getLoginName()).append("]").append("?????????????????????,").append("???????????????:")
//			.append(dataInputDictInfo.getDictName());
//			templeBS.saveLog("01", "????????????", buff.toString(), users.getUserId(), users.getLoginName());
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e);
		}

//		logBS.addLog(this.getRequest().getRemoteAddr(), BioneSecurityUtils.getCurrentUserInfo().getCurrentLogicSysNo(), BioneSecurityUtils.getCurrentUserInfo().getUserId(), "?????????????????????" + dataInputDictInfo.getDictName() + "???");
		String libId = StringUtils2.javaScriptEncode(libId_date.toString());
		if (dataInputDictInfo.getSqlText() != null && !"".equals(dataInputDictInfo.getSqlText())) {
			return new ModelAndView("/input/library/lib-tree", "libId", libId);
		} else {
			return new ModelAndView("/input/library/lib-list", "libId", libId);
		}
		
	}
//
//	/**
//	 * ????????????????????????????????????
//	 * @param libId
//	 * @param dataDate
//	 * @return
//	 * @throws Exception
//	 */
//	@RequestMapping(value = "/queryHis", method = RequestMethod.GET)
//	public ModelAndView queryHisLibrary(String libId, String dataDate) {
//		UdipDataLibInfo udipDataLibInfo = udipDataLibBS.getEntityByProperty(UdipDataLibInfo.class, "libId", libId);
//		StringBuilder libId_date = new StringBuilder();
//		libId_date.append(libId);
//		libId_date.append(SPLIT_cha).append(dataDate);
//		try {
//			if (udipDataLibInfo.getDsId() != null && !"".equals(udipDataLibInfo.getDsId()) && udipDataLibInfo.getParentCol() == null) {
//				List<MtoolDbTableProBO> list = rdbConnectionManagerBS.getTableMoreList(udipDataLibInfo.getDsId(), udipDataLibInfo.getTableName());
//				int k = 0;
//				for (int i = 0; i < list.size(); i++) {
//					MtoolDbTableProBO mb = list.get(i);
//					if (mb.getTableName().equals(udipDataLibInfo.getCodeCol())) {
//						k = i;
//					} else if (mb.getTableName().equals(udipDataLibInfo.getNameCol())) {
//						if (!"".equals(mb.getTableRemarks())) {
//							libId_date.append(SPLIT_cha).append(mb.getTableRemarks());
//						} else {
//							libId_date.append(SPLIT_cha).append(mb.getTableName());
//						}
//
//					}
//				}
//				MtoolDbTableProBO mb = list.get(k);
//				if (!"".equals(mb.getTableRemarks())) {
//					libId_date.append(SPLIT_cha).append(mb.getTableRemarks());
//				} else {
//					libId_date.append(SPLIT_cha).append(mb.getTableName());
//				}
//			} else if (udipDataLibInfo.getDsId() == null || "".equals(udipDataLibInfo.getDsId())) {
//				libId_date.append(SPLIT_cha).append("??????");
//				libId_date.append(SPLIT_cha).append("??????");
//			}
//
//		} catch (Exception e) {
//			log.error(e);
//		}
//		logBS.addLog(this.getRequest().getRemoteAddr(), BiOneSecurityUtils.getCurrentUserInfo().getCurrentLogicSysNo(), BiOneSecurityUtils.getCurrentUserInfo().getUserId(), "?????????????????????" + udipDataLibInfo.getLibName() + "???");
//		if (udipDataLibInfo.getParentCol() != null && !"".equals(udipDataLibInfo.getParentCol())) {
//			return new ModelAndView("/udip/library/lib-tree", "libId", libId_date.toString());
//		} else {
//			return new ModelAndView("/udip/library/lib-list", "libId", libId_date.toString());
//		}
//
//	}
//
	@RequestMapping(value = "/add", method = RequestMethod.POST)
	@ResponseBody
	public Map<String,String> editNew(RptInputListDataDictInfo dictInfo, String filterCondition, String catalogId) {
		String libId = RandomUtils.uuid2();
		dictInfo.setDictId(libId);
		dictInfo.setCatalogId(catalogId);
		dictInfo.setCreateUser(BioneSecurityUtils.getCurrentUserInfo().getUserId());
		dictInfo.setCreateTime(DateUtils.getYYYY_MM_DD_HH_mm_ss());
		dictInfo.setLogicSysNo(BioneSecurityUtils.getCurrentUserInfo().getCurrentLogicSysNo());
		inputDataDictBS.saveDictInfo(dictInfo);
		
		/********??????????????????**********/
//		BioneUser users = BioneSecurityUtils.getCurrentUserInfo();
//		StringBuilder buff = new StringBuilder();
//		buff.append("??????[").append(users.getLoginName()).append("]").append("?????????????????????,").append("???????????????:")
//		.append(dictInfo.getDictName());
//		templeBS.saveLog("01", "????????????", buff.toString(), users.getUserId(), users.getLoginName());
		return new HashMap<String,String>();
	}

	@RequestMapping(value = "/updateMode.*")
	public void updateMode(RptInputListDataDictInfo mode, String catalogId) {
		mode.setCatalogId(catalogId);
		inputDataDictBS.saveOrUpdateEntity(mode);
		

		/********??????????????????**********/
//		BioneUser users = BioneSecurityUtils.getCurrentUserInfo();
//		StringBuilder buff = new StringBuilder();
//		buff.append("??????[").append(users.getLoginName()).append("]").append("?????????????????????,").append("???????????????:")
//		.append(mode.getDictName());
//		templeBS.saveLog("03", "????????????", buff.toString(), users.getUserId(), users.getLoginName());
	}
//
//	/**
//	 * ??????????????????????????????????????????????????????????????????
//	 * @param mode
//	 * @return
//	 */
//	@RequestMapping("/checkLibName")
//	@ResponseBody
//	public boolean checkLibName(UdipDataLibInfo mode,String libId) {
//		List<UdipDataLibInfo> list = udipDataLibBS.findEntityListByProperty("libName", mode.getLibName());
//		if (list == null || list.isEmpty()) {
//			return true;
//		} else {
//			if (StringUtils.isNotBlank(libId)) {
//				UdipDataLibInfo libInfo = list.get(0);
//				UdipDataLibInfo udipLibInfo = this.udipDataLibBS.getEntityById(libId);
//				if (udipLibInfo != null && !udipLibInfo.getLibName().equalsIgnoreCase(libInfo.getLibName()))
//					return false;
//				else
//					return true;
//			}
//			return false;
//		}
//	}

	@RequestMapping(value = "/excel_down")
	public void excel_down(HttpServletRequest request, HttpServletResponse response, String id, String dataDate) {
		List<CommonTreeNode> listRowColumns = Lists.newArrayList();
		RptInputListDataDictInfo udipDataLibInfo = inputDataDictBS.getEntityByProperty( // ??????????????????
				RptInputListDataDictInfo.class, "dictId", id);
		CommonTreeNode comm = new CommonTreeNode();
		List<CommonTreeNode> lists = null;
		File file = null;
		try {
			if (udipDataLibInfo.getDsId() != null && !"".equals(udipDataLibInfo.getDsId())) {
				if(StringUtils.isNotBlank(udipDataLibInfo.getSqlText())){
					String[] sqlContent = splitSqlContent(udipDataLibInfo.getSqlText());
					comm.setId(sqlContent[0]);
					comm.setText(sqlContent[1]);
					if(sqlContent.length >2){
						comm.setUpId(sqlContent[2]);
					}
				}
					
			} else {
				comm.setText("??????");
				comm.setId("??????");
			}
			listRowColumns.add(comm);
			String dataDates = DateUtils.formatDate(DateUtils.getDateStart(dataDate), "yyyyMMdd");
			lists = inputDataDictBS.buildLibTreeByLibId(id, dataDates);
			listRowColumns.addAll(lists);

			file = fileNetUpload(listRowColumns, String.valueOf(System.currentTimeMillis()));
			String fileName = udipDataLibInfo.getDictName() + ".xls";
			
			if (request.getHeader("User-Agent").toLowerCase().indexOf("firefox") > 0) {
				fileName = new String(fileName.getBytes("UTF-8"), "UTF-8"); // firefox?????????
			} else if (request.getHeader("User-Agent").toLowerCase().indexOf("msie") > 0 
					|| request.getHeader("User-Agent").toLowerCase().indexOf("trident") > 0 
					|| request.getHeader("User-Agent").toLowerCase().indexOf("edge")> 0) {
				fileName = URLEncoder.encode(fileName, "UTF-8");// IE?????????
			} else if (request.getHeader("User-Agent").toLowerCase().indexOf("chrome") > 0) {
				fileName = new String(fileName.getBytes("UTF-8"), "UTF-8");// ??????
			}
			
			DownloadUtils.download(response, file, fileName); // ????????????

//			logBS.addLog(this.getRequest().getRemoteAddr(), BioneSecurityUtils.getCurrentUserInfo().getCurrentLogicSysNo(), BioneSecurityUtils.getCurrentUserInfo().getUserId(), "?????????????????????" + udipDataLibInfo.getDictName() + "???");
		} catch (Exception e) {
			log.error(e);
		} finally {
			if (file != null) {
				file.delete();
			}
		}

	}
	/*
	 * (non-Javadoc)
	 * @see com.yuchengtech.datainput.smb.utils.FileUpAndDown#fileNetUpload(java. util.List, java.lang.String)
	 */
	public File fileNetUpload(List<CommonTreeNode> listColumns, String libName) {
		File localFile = null;
		String xlsFile = libName + ".xls";
		if (FilepathValidateUtils.validateFilepath(xlsFile)) {
			localFile = new File(xlsFile);
			OutputStream out = null;
			HSSFWorkbook workBook = excelConstants.getWorkBookForLib(listColumns);
			try {
				FileOutputStream fOut = new FileOutputStream(localFile);
				out = new BufferedOutputStream(fOut);
				workBook.write(fOut);
				fOut.flush();
				fOut.close();
			} catch (Exception e) {
				e.printStackTrace();
				log.error(e);
			} finally {
				try {
					if (out != null)
						out.close();
				} catch (IOException e) {
					e.printStackTrace();
					log.error(e);
				}
			}
		}
		return localFile;
	}

//
//	/**
//	 * ??????????????????????????????????????????
//	 * @param id
//	 * @param rowid
//	 * @return
//	 */
	@RequestMapping("/getDataZidian")
	public ModelAndView getDataZidian(String id, String rowid,String dsId) {
		ModelMap mm = new ModelMap();
		mm.addAttribute("rowid", StringUtils2.javaScriptEncode(rowid));
		mm.addAttribute("id", StringUtils2.javaScriptEncode(id));
		mm.addAttribute("dsId", StringUtils2.javaScriptEncode(dsId));
		return new ModelAndView("/input/library/lib-index", mm);
	}

	/**
	 * ??????????????????????????????????????????
	 * 
	 * @param id
	 * @param rowid
	 * @return
	 */
	@RequestMapping("/getFormTypeName")
	public ModelAndView getDataZidian(String orgId, String libId, String templeId, String caseId, String formType, String formName) {
		RptInputListDataDictInfo dataDictInfo = inputDataDictBS.getEntityByProperty(RptInputListDataDictInfo.class, "dictId", libId);
		StringBuilder libId_date = new StringBuilder();
		libId_date.append(libId);
		if (StringUtils.isNotBlank(caseId)) {
			RptTskIns taskCase = this.taskCaseBS.getEntityById(caseId);
			libId_date.append(SPLITCHAR).append(taskCase.getDataDate());
		} else {
			libId_date.append(SPLITCHAR).append("");
		}
		try {
			if (StringUtils.isNotBlank(dataDictInfo.getDsId()) && StringUtils.isBlank(dataDictInfo.getShowType())) {
//				List<MtoolDbTableProBO> list = rdbConnectionManagerBS.getTableMoreList(dataDictInfo.getDsId(), dataDictInfo.getTableEnName());
//				int k = 0;
//				for (int i = 0; i < list.size(); i++) {
//					MtoolDbTableProBO mb = list.get(i);
//					if (mb.getTableName().equals(dataDictInfo.getCodeCol())) {
//						k = i;
//					} else if (mb.getTableName().equals(dataDictInfo.getNameCol())) {
//						if (!"".equals(mb.getTableRemarks())) {
//							libId_date.append(SPLITCHAR).append(mb.getTableRemarks());
//						} else {
//							libId_date.append(SPLITCHAR).append(mb.getTableName());
//						}
//					}
//				}
//				MtoolDbTableProBO mb = list.get(k);
//				if (!"".equals(mb.getTableRemarks())) {
//					libId_date.append(SPLITCHAR).append(mb.getTableRemarks());
//				} else {
//					libId_date.append(SPLITCHAR).append(mb.getTableName());
//				}
			} else if (dataDictInfo.getDsId() == null || "".equals(dataDictInfo.getDsId())) {
				libId_date.append(SPLITCHAR).append("??????");
				libId_date.append(SPLITCHAR).append("??????");
			}

//			logBS.addLog(this.getRequest().getRemoteAddr(), BioneSecurityUtils.getCurrentUserInfo().getCurrentLogicSysNo(),
//					BioneSecurityUtils.getCurrentUserInfo().getUserId(), "??????????????????????????????????????????");
			ModelMap mm = new ModelMap();
			mm.addAttribute("libId", StringUtils2.javaScriptEncode(libId_date.toString()));
			if (StringUtils.isNotBlank(orgId)) {
				mm.addAttribute("orgId", StringUtils2.javaScriptEncode(orgId));
			}
			mm.addAttribute("formType", StringUtils2.javaScriptEncode(EncodeUtils.urlDecode(formType)));
			mm.addAttribute("formName", StringUtils2.javaScriptEncode(EncodeUtils.urlDecode(formName)));
			if (UdipConstants.LIB_SHOW_TYPE_TREE.equals(dataDictInfo.getShowType())) {
				return new ModelAndView("/input/library/lib-tree", mm);
			} else {
				return new ModelAndView("/input/library/lib-list", mm);
			}
		} catch (Exception e) {
			log.error(e);
		}
		return new ModelAndView("/input/library/lib-list");
	}

	/**
	 * ??????????????????ID????????????ID?????????????????????????????????
	 * @param id
	 * @return
	 */
	@RequestMapping("/getTempListById")
	@ResponseBody
	public Map<String, Object> getTempListById(String dictId) {
		List<RptInputLstTempleInfo> tempList = Lists.newArrayList();
		if (StringUtils.isNotBlank(dictId)) {
			List<RptInputLstTempleField> taskList = templateFieldBS.getEntityListByProperty(RptInputLstTempleField.class, "dictId", dictId);
			if (taskList != null && taskList.size() > 0) {
				for (RptInputLstTempleField dataInputTemplateField : taskList) {
					List<RptInputLstTempleInfo> udipTempleList = templeBS.getEntityListByProperty(RptInputLstTempleInfo.class, "templeId", dataInputTemplateField.getTempleId());
					if (udipTempleList != null && udipTempleList.size() > 0) {
						RptInputLstTempleInfo dataInputTemplateInfo = udipTempleList.get(0);
						dataInputTemplateInfo.setOrgColumn(dataInputTemplateField.getFieldCnName());// ???????????????????????????????????????
						tempList.add(dataInputTemplateInfo);
					}
				}
			}
		}
		Map<String, Object> objDefMap = Maps.newHashMap();
		objDefMap.put("Rows", tempList);
		return objDefMap;
	}

	/**
	 * ??????????????????ID????????????ID?????????????????????????????????
	 * @param id
	 * @return
	 */
	@RequestMapping("/checkTempListById")
	@ResponseBody
	public List<String> checkTempListById(String dictId) {
		List<String> objDefList = Lists.newArrayList();
		if (StringUtils.isNotBlank(dictId)) {
			String[] libIds = dictId.split(",");
			for (int i = 0; i < libIds.length; i++) {
				List<RptInputLstTempleField> taskList = templateFieldBS.getEntityListByProperty(RptInputLstTempleField.class, "dictId", libIds[i]);
				if (taskList != null && taskList.size() > 0) {
					for (RptInputLstTempleField udipTempleColumns : taskList) {
						List<RptInputLstTempleInfo> dataInputTemplates = templateFieldBS.getEntityListByProperty(RptInputLstTempleInfo.class, "templeId", udipTempleColumns.getTempleId());
						if (dataInputTemplates != null && dataInputTemplates.size() > 0) {
							RptInputLstTempleInfo dataInputTemplate = dataInputTemplates.get(0);
							objDefList.add(dataInputTemplate.getTempleName());
						}
					}
				}
			}
		}
		return objDefList;
	}
//	
//	/**
//	 * 
//	 * @param templeId
//	 * @return
//	 */
//	@RequestMapping("/libMapsbyTempleId.json")
//	@ResponseBody
//	public Map<String, Map<String, String>> libMapsbyTempleId(String templeId) {
//		List<UdipTempleColumns> listCol = this.templeColumnsBS.findEntityListByProperty("templeId", templeId);
//		Map<String, Map<String, String>> mapList = Maps.newHashMap();
//		for (UdipTempleColumns col : listCol) {
//			if (StringUtils.isNotBlank(col.getDataZidian())) {
//				List<CommonTreeNode> common = udipDataLibBS.buildLibTreeByLibId(col.getDataZidian(), "");
//				Map<String, String> map = Maps.newHashMap();
//				for (CommonTreeNode comm : common) {
//					map.put(comm.getId(), comm.getText());
//				}
//				mapList.put(col.getDataZidian(), map);
//			}
//
//		}
//		return mapList;
//	}
//	
	/**
	 * ??????????????????????????????????????????????????????
	 * @param mode
	 */
	@RequestMapping(value = "/testDataLib.*")
	@ResponseBody
	public boolean testDataLib(RptInputListDataDictInfo mode){
		boolean flag=true;
		if(mode!=null && StringUtils.isNotBlank(mode.getSqlText())){
			flag = this.inputDataDictBS.testDataLib(mode);
		}
		return flag;
	}
	
	@RequestMapping(value = "/testLibId")
	@ResponseBody
	public String testLibId(String catalogId, String dictName) {
		String result = this.inputDataDictBS.getLibId(catalogId, dictName);
		return result;
	}

	/**
	 * ????????????????????????
	 * @param response
	 */
	@RequestMapping("/excelDownLoalDataLibInfo")
	public void excelDownLoalDataLibInfo(HttpServletRequest request, HttpServletResponse response, String dictId) {
		String filePath = this.getRealPath() + dictTemplatePath;
		RptInputListDataDictInfo dictInfo = this.inputDataDictBS.getEntityById(dictId);
		if (FilepathValidateUtils.validateFilepath(filePath)) {
			Workbook workBook;
			OutputStream output = null;
			FileInputStream fis = null;;
			try {
				
				fis = new FileInputStream(filePath);
				workBook = new HSSFWorkbook(fis);
				Sheet sheet = workBook.getSheet("Sheet1");
				
				if("1".equals(dictInfo.getDictType())){
					String[] value = StringUtils.split(dictInfo.getStaticContent(), SPLITCHAR);
					if (ArrayUtils.isNotEmpty(value)) {
						for (int i = 0, len = value.length; i < len; i++) {
							String[] vals = StringUtils.split(value[i], ':');
							Row row = sheet.createRow(i + 2);
							Cell cell0 = row.createCell(0);
							cell0.setCellType(CellType.STRING);
							cell0.setCellValue(vals[0]);
							// cell0.setCellStyle(cellStyleContent);
							
							Cell cell1 = row.createCell(1);
							cell1.setCellType(CellType.STRING);
							cell1.setCellValue(vals[1]);
							// cell1.setCellStyle(cellStyleContent);
						}
					}
					
				}else{
					Connection conn = null;
					Statement stat = null;
					ResultSet rs = null;
					try {
						conn = dataSource.getConnection(dictInfo.getDsId());
						stat = conn.createStatement();
						rs = stat.executeQuery(dictInfo.getSqlText());
						int i = 0;
						while(rs.next()){
							Row row = sheet.createRow(i++ + 2);
							Cell cell0 = row.createCell(0);
							cell0.setCellType(CellType.STRING);
							cell0.setCellValue(rs.getString(1));
							
							Cell cell1 = row.createCell(1);
							cell1.setCellType(CellType.STRING);
							cell1.setCellValue(rs.getString(2));
						}
						
					} catch (Exception e) {
						e.printStackTrace();
					}finally{
						dataSource.releaseConnection(rs, stat, conn);
					}
				}
				String fileName = dictInfo.getDictName() + "??????.xls";
				
				if (request.getHeader("User-Agent").toLowerCase().indexOf("firefox") > 0) {
					fileName = new String(fileName.getBytes("UTF-8"), "UTF-8"); // firefox?????????
				} else if (request.getHeader("User-Agent").toLowerCase().indexOf("msie") > 0 
						|| request.getHeader("User-Agent").toLowerCase().indexOf("trident") > 0 
						|| request.getHeader("User-Agent").toLowerCase().indexOf("edge")> 0) {
					fileName = URLEncoder.encode(fileName, "UTF-8");// IE?????????
				} else if (request.getHeader("User-Agent").toLowerCase().indexOf("chrome") > 0) {
					fileName = new String(fileName.getBytes("UTF-8"), "UTF-8");// ??????
				}
				
				response.setHeader("Content-Disposition", "inline;filename=" + new String(fileName.getBytes("GB18030"), "ISO8859-1"));
				response.setContentType("application/-excel;charset=UTF-8");
				output = new BufferedOutputStream(response.getOutputStream());
				// output = new BufferedOutputStream(new FileOutputStream(new File(filePath)));
				workBook.write(output);
				output.flush();
				output.close();
			} catch (FileNotFoundException e1) {
				e1.printStackTrace();
			} catch (IOException e1) {
				e1.printStackTrace();
			} finally {
				try {
					if(fis != null){
						fis.close();
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
				try {
					if (output != null)
						output.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		/************??????????????????**********/
//		BioneUser user = BioneSecurityUtils.getCurrentUserInfo();
//		StringBuilder buff  = new StringBuilder();
//		buff.append("??????[").append(user.getLoginName()).append("]").append("????????????????????????,???????????????:")
//		.append(dictInfo.getDictName());
//		inputDataDictBS.saveLog("04", "????????????", buff.toString(), user.getUserId(), user.getLoginName());

	}

	/**
	 * ??????????????????????????????
	 * @param uploader
	 * @param response
	 * @return
	 */
	@RequestMapping(value = "/startImpLibData",produces = "text/html;charset=UTF-8")
	@ResponseBody
	public String startImpLibData(Uploader uploader, HttpServletResponse response, String dictId) {
		String fileName = uploader.getUpload().getFileItem().getName();
		StringBuilder dataDictMessage = new StringBuilder();
		// String dictNm = "";
		// ????????????????????????????????????????????????????????????
		if (StringUtils.isNotBlank(dictId)) {
			File file = null;
			try {
				//?????????????????????????????????data_dict.xls 20190516 wf
//				if (!fileNameInfo.equals(fileName)) {
//					return "????????????????????????????????????????????????????????????,";
//				}
				file = this.uploadFile(uploader, dictImportPath, false);
				if (uploader.getChunk() == uploader.getChunks() - 1) {
					if (file == null) {
						return "?????????????????????????????????,";
					}
					// ????????????????????????????????????????????????
					RptInputListDataDictInfo dataLibInfo = this.inputDataDictBS.getEntityById(dictId);
					// dictNm = dataLibInfo.getDictName();
					dataLibInfo.setStaticContent(StringUtils.EMPTY);
					this.inputDataDictBS.saveOrUpdateEntity(dataLibInfo);
					
					// ??????????????????????????????List??????
					List<Map<String, Object>> mapList = excelConstants.getRoleList(file);
					if (file != null) {
						file.delete();
					}
					// ????????????????????????????????????
					List<String> nameList = Lists.newArrayList();
					// ????????????????????????????????????
					List<String> codeList = Lists.newArrayList();
					Map<String, String> commList = this.inputDataDictBS.buildLibMapById(dictId);
					// ??????????????????????????????list??????
					if (commList != null && commList.size() > 0) {
						@SuppressWarnings("rawtypes")
						Iterator iterator = commList.keySet().iterator();
						while (iterator.hasNext()) {
							String cols = (String) iterator.next();
							String colValue = (String) commList.get(cols);
							nameList.add(colValue);
							codeList.add(cols);
						}
					}
					if (dataLibInfo != null && mapList != null && mapList.size() > 0) {
						// ????????????????????????????????????,??????????????????????????????????????????
						if (UdipConstants.LIB_LIB_TYPE_CONSTANT.equals(dataLibInfo.getDictType())) {
							validateLibData(mapList, nameList, codeList, dataDictMessage, false, false);
							if (StringUtils.isNotBlank(dataDictMessage.toString())) {
								return dataDictMessage.toString();
							} else {
								StringBuilder content = new StringBuilder();
								content.append(dataLibInfo.getStaticContent());
								if (StringUtils.isNotBlank(dataLibInfo.getStaticContent())) {
									Map<String, Object> colMap = mapList.get(0);
									content.append(";;").append(colMap.get("0")).append(":").append(colMap.get("1"));
								} else {
									Map<String, Object> colMap = mapList.get(0);
									content.append(colMap.get("0")).append(":").append(colMap.get("1"));
								}
								for (int i = 1; i < mapList.size(); i++) {
									Map<String, Object> colMap = mapList.get(i);
									content.append(";;").append(colMap.get("0")).append(":").append(colMap.get("1"));
								}
								dataLibInfo.setStaticContent(content.toString());
								this.inputDataDictBS.saveOrUpdateEntity(dataLibInfo);
							}
						}
					} else {
						dataDictMessage.append("?????????????????????????????????????????????????????????!");
						return dataDictMessage.toString();
					}
//					BioneUser user = BioneSecurityUtils.getCurrentUserInfo();
//					StringBuilder buff  = new StringBuilder();
//					buff.append("??????[").append(user.getLoginName()).append("]").append("???????????????????????????????????????,????????????????????????:").append(dictNm);
//					inputDataDictBS.saveLog("04", "????????????", buff.toString(), user.getUserId(), user.getLoginName());
				}
			} catch (Exception e) {
				if (file != null) {
					file.delete();
				}
				dataDictMessage.append("???").append(e.getMessage()).append("???");
				logger.info("????????????????????????", e);
				return dataDictMessage.toString();
			} finally {
				if (file != null) {
					file.delete();
				}
			}
		}
		return null;
	}
	
	/**
	 * ????????????????????????
	 * @param mapList ?????????????????????????????????
	 * @param nameList ???????????????????????????
	 * @param codeList ???????????????????????????
	 * @param udipLibMessage ??????????????????
	 * @param isParent ???????????????????????????true?????????false?????????
	 * @param isDate   ???????????????????????????true?????????false?????????
	 */
	private String validateLibData(List<Map<String, Object>> mapList,List<String> nameList,List<String> codeList,StringBuilder udipLibMessage,boolean isParent,boolean isDate){
		for (int i = 0; i < mapList.size(); i++) {
			Map<String, Object> colMap = mapList.get(i);
			if (colMap != null && colMap.size() > 0) {
				@SuppressWarnings("rawtypes")
				Iterator iterator = colMap.keySet().iterator();
				String colValue = "";
				String cols = "";
				if(StringUtils.isNotBlank(udipLibMessage.toString()) && udipLibMessage.toString().length()>60){
					return "";
				}
				while (iterator.hasNext()) {
					cols = (String) iterator.next();
					colValue = (String) colMap.get(cols);
					// ??????????????????????????????
					if ("0".equals(cols)) {
						if (StringUtils.isNotBlank(colValue)) {
							if (nameList.contains(colValue)) {
								udipLibMessage.append("??????").append((i + 3)).append("??????????????????????????????????????????????????????????????????,");
							}
							nameList.add(colValue);
						}else{
							udipLibMessage.append("??????").append((i + 3)).append("?????????????????????????????????,");
						}
						continue;
					}else if ("1".equals(cols)) {// ??????????????????????????????
						if (StringUtils.isNotBlank(colValue)) {
							if (codeList.contains(colValue)) {
								udipLibMessage.append("??????").append((i + 3)).append("??????????????????????????????????????????????????????????????????,");
							}
							codeList.add(colValue);
						}else{
							udipLibMessage.append("??????").append((i + 3)).append("?????????????????????????????????,");
						}
						continue;
					}
				}
			}
		}

		
		return "";
	}
	
	/**
	 * ???????????????ID???????????????????????????????????????????????????
	 * @param dsId
	 * @param tableName
	 * @return
	 */
	@RequestMapping(value = "/getColumnList.*")
	@ResponseBody
	public List<Map<String, String>> getColumnList(String dsId, String tableName) {
		List<Map<String, String>> columnList = Lists.newArrayList();
		List<BioneColumnMetaData> list = this.dsBS.getColumnMetaData(dsId, tableName);
		if (CollectionUtils.isNotEmpty(list)) {
			for (BioneColumnMetaData meta : list) {
				Map<String, String> m = Maps.newHashMap();
				/*if (!"".equals(meta.getComments())) {
					m.put("text", meta.getComments() + "(" + meta.getColumnName() + ")");
				} else {*/
					m.put("text", meta.getColumnName());
				/*}*/
				m.put("id", meta.getColumnName());
				columnList.add(m);
			}
		}
		return columnList;
	}

}
