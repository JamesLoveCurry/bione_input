package com.yusys.biapp.input.inputTable.web;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.yusys.biapp.input.data.service.DataFileBS;
import com.yusys.biapp.input.dict.entity.RptInputListDataDictInfo;
import com.yusys.biapp.input.dict.service.InputDataDictBS;
import com.yusys.biapp.input.dict.utils.ColumnType;
import com.yusys.biapp.input.dict.utils.DataUtils;
import com.yusys.biapp.input.dict.utils.UdipConstants;
import com.yusys.biapp.input.input.web.TaskCaseController;
import com.yusys.biapp.input.inputTable.entity.RptInputListTableConstraint;
import com.yusys.biapp.input.inputTable.entity.RptInputListTableFieldInf;
import com.yusys.biapp.input.inputTable.entity.RptInputListTableInfo;
import com.yusys.biapp.input.inputTable.entity.RptListDataloadType;
import com.yusys.biapp.input.inputTable.service.InputTableBS;
import com.yusys.biapp.input.inputTable.service.TableConstrainBS;
import com.yusys.biapp.input.inputTable.service.TableFieldBS;
import com.yusys.biapp.input.inputTable.vo.InputTableVO;
import com.yusys.biapp.input.inputTable.vo.TableDataVO;
import com.yusys.biapp.input.inputTable.vo.TableSqlVO;
import com.yusys.biapp.input.task.entity.RptTskIns;
import com.yusys.biapp.input.task.service.DeployTaskBS;
import com.yusys.biapp.input.template.entity.RptInputLstTempleInfo;
import com.yusys.biapp.input.template.service.TempleBS;
import com.yusys.biapp.input.template.service.TempleFieldBS;
import com.yusys.bione.comp.common.CommonTreeNode;
import com.yusys.bione.comp.entity.page.Pager;
import com.yusys.bione.comp.entity.upload.Uploader;
import com.yusys.bione.comp.repository.jpa.SearchResult;
import com.yusys.bione.comp.utils.*;
import com.yusys.bione.frame.base.common.GlobalConstants4frame;
import com.yusys.bione.frame.base.web.BaseController;
import com.yusys.bione.frame.mtool.entity.BioneDriverInfo;
import com.yusys.bione.frame.mtool.entity.BioneDsInfo;
import com.yusys.bione.frame.mtool.service.DataSourceBS;
import com.yusys.bione.frame.mtool.service.DriverInfoBS;
import com.yusys.bione.frame.mtool.util.MtoolUtils;
import com.yusys.bione.frame.security.BioneSecurityUtils;
import com.yusys.bione.frame.security.BioneUser;
import com.yusys.bione.plugin.base.common.GlobalConstants4plugin;
import com.yusys.bione.plugin.wizard.web.vo.InputTableImportVO;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.*;

@Controller
@RequestMapping("/rpt/input/table")
public class InputTableController extends BaseController {

	private final Log log = LogFactory.getLog(InputTableController.class);
	
	private final String PATH = "/input/table/";

	@Autowired
	private InputTableBS tableBS;

	@Autowired
	private TableFieldBS tableColBS;

	@Autowired
	private TableConstrainBS priIndexBS;

	@Autowired
	private DataSourceBS dataSourceBS;

	@Autowired
	DriverInfoBS driverInfoBS;
	
	@Autowired
	TempleBS templeBS;
	
	@Autowired
	private InputDataDictBS udipDataLibBS;

	@Autowired
	private TempleFieldBS templeFieldBS;

	@Autowired
	private DeployTaskBS deployTaskBS;

	@Autowired
	private DataFileBS dataFileBS;

	@Autowired
	private TaskCaseController taskCaseController;

	private String fuhao = ";;";

	private String douhao = ",,";

	@RequestMapping
	public String index() {
		return PATH + "table-index";
	}

	/**
	 * ?????????????????????????????????????????????????????????
	 * 
	 * @param rf
	 * @return
	 */
	@RequestMapping("/list.*")
	@ResponseBody
	public Map<String, Object> list(Pager rf) {
		SearchResult<RptInputListTableInfo> searchResult = tableBS.getSearch(
				rf.getPageFirstIndex(), rf.getPagesize(), rf.getSortname(),
				rf.getSortorder(), rf.getSearchCondition());

		// BioneUser user = BioneSecurityUtils.getCurrentUserInfo();
		// Map<String, Object> cd = rf.getSearchCondition();
		// Map<String,String> filedInfo =  (Map<String, String>) cd.get("fieldValues");
		Map<String,String>nmMap=Maps.newHashMap();
		nmMap.put("tableEnName", "??????");
		nmMap.put("tableCnName", "????????????");
		nmMap.put("dsId", "?????????");
		Map<String,Map<String,String>>exMap=Maps.newHashMap();
		Map<String,String>tmpMap=Maps.newHashMap();
		List<BioneDsInfo>dsInfos=dataSourceBS.getAllEntityList();
		for(BioneDsInfo ds:dsInfos){
			tmpMap.put(ds.getDsId(), ds.getDsName());
		}
		exMap.put("dsId", tmpMap);
//		tableBS.saveQueryLog("???????????????",  user.getUserId(), user.getLoginName(),nmMap,filedInfo,exMap);
		
		Map<String, Object> objDefMap = Maps.newHashMap();
		objDefMap.put("Rows", searchResult.getResult());
		objDefMap.put("Total", searchResult.getTotalCount());
		return objDefMap;
	}

	@RequestMapping(value = "/getDataLoadInfo.*", method = RequestMethod.POST)
	@ResponseBody
	public RptListDataloadType getRptListDataloadTypeByTableId(String tableId){
		return tableBS.getRptListDataloadTypeByTableId(tableId);
	}
	
	/**
	 * ?????????????????????????????????????????????????????????
	 * 
	 * @param rf
	 * @return
	 */
	@RequestMapping(value = "/dataload", method = RequestMethod.GET)
	public ModelAndView dataload( String id) {
		id = StringUtils2.javaScriptEncode(id);
		return new ModelAndView(PATH + "table-dataload", "tableId", id);
	}

	/**
	 * ?????????????????????????????????????????????????????????
	 * 
	 * @param rf
	 * @return
	 */
	@RequestMapping(value = "/toEditTypeData", method = RequestMethod.GET)
	public ModelAndView toEditTypeData( String type,String cfgId,String tableId) {
		String src = "table-dataload-sql";
		ModelMap map = new ModelMap();
		map.put("cfgId", StringUtils2.javaScriptEncode(cfgId));
		map.put("tableId", StringUtils2.javaScriptEncode(tableId));
		//1:???????????????          2:sql??????
		if(type.equals("1"))
		{
			src = "table-dataload-datalist";
		}
		return new ModelAndView(PATH + src, map);
	}
	
	/**
	 * ????????????
	 * 
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/newTable", method = RequestMethod.GET)
	public String newTable() {
		return PATH + "table-add";
	}
	
	/**
	 * ????????????
	 * 
	 * @param tableId
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/updateTable", method = RequestMethod.GET)
	public ModelAndView updateTable(String tableId) {
		tableId = StringUtils2.javaScriptEncode(tableId);
		return new ModelAndView(PATH + "table-add", "tableId", tableId);
	}

	@RequestMapping(value = "/selectSchemaTable", method = RequestMethod.GET)
	public ModelAndView selectSchemaTable(String dsId,String dsName,String operType,String templateId,String tableNm) {
		ModelMap mm = new ModelMap();
//		if(!StringUtils.isEmpty(templateId)){
//			RptInputLstTempleInfo templeInfo = templeBS.getEntityById(templateId);
//			mm.put("dsId", templeInfo.getDsId());
//		}else
		mm.put("dsId", StringUtils2.javaScriptEncode(dsId));
		mm.put("dsName", StringUtils2.javaScriptEncode(dsName));
		mm.put("operType", StringUtils2.javaScriptEncode(operType));
		mm.put("templateId", StringUtils2.javaScriptEncode(templateId));
		mm.put("tableNm", StringUtils2.javaScriptEncode(tableNm));
		return new ModelAndView("/input/table/table-selectschema",mm);
	}

	@RequestMapping(value = "/uploadSchemaData")
	public ModelAndView selectSchemaTable() {
		ModelMap mm = new ModelMap();
		return new ModelAndView("/input/table/table-index-upload",mm);
	}

	@RequestMapping(value = "/startUploadSchemaData", method = RequestMethod.POST)
	@ResponseBody
	public List<InputTableImportVO> startUploadSchemaData(Uploader uploader) {
		File file = null;
		try {
			file = this.uploadFile(uploader, "/export/input", false);
		} catch (Exception e) {
			logger.info("????????????????????????", e);
		}
		return tableBS.upload(file);
	}

	@RequestMapping(value = "/export", method = RequestMethod.POST)
	@ResponseBody
	public Map<String, Object> export() throws UnsupportedEncodingException {
		Map<String, Object> res = new HashMap<String, Object>();
		String fileName = this.getRealPath() + GlobalConstants4plugin.DESIGN_EXPORT_PATH
				+"/" +GlobalConstants4plugin.EXPORT_TABLE_TEMPLATE_PATH;
		res.put("fileName", URLEncoder.encode(fileName,"UTF-8"));
		return res;
	}

	/**
	 * ????????????
	 * @param response ????????????Id
	 * @param filepath ????????????
	 * @return ??????????????????
	 */
	@RequestMapping("/download")
	public void download(HttpServletResponse response, String filepath) throws IOException {
		if(FilepathValidateUtils.validateFilepath(filepath)) {
			File file = new File(filepath);
			DownloadUtils.download(response, file);
		}
	}

	/**
	 * ??????dsId?????????????????????cl
	 * @param dsId
	 * @return
	 */
	@RequestMapping(value = "/getTableTree.*", method = RequestMethod.POST)
	@ResponseBody
	public List<CommonTreeNode> getTableTree(String dsId, String searchNm) {
		return tableBS.getTableTreeByDsId(dsId, searchNm);
	}

	/**
	 * ??????tableId???????????????
	 * 
	 * @param tableId
	 * @return
	 */
	@RequestMapping("/getTableInfoById")
	@ResponseBody
	public InputTableVO getTableInfoById(String tableId) {
		return tableBS.getTableInfoById(tableId);
	}
	
	/**
	 * ?????????????????????cl
	 * @param dsId
	 * @param tableNm
	 * @return
	 */
	@RequestMapping(value = "/showcolumn", method = RequestMethod.GET)
	public ModelAndView showcolumn(String dsId,String tableNm){
		ModelMap mm = new ModelMap();
		mm.put("dsId", StringUtils2.javaScriptEncode(dsId));
		mm.put("tableNm", StringUtils2.javaScriptEncode(tableNm));
		return new ModelAndView("/input/table/table-showcolumn",mm);
	}

	/**
	 * ???????????????cl
	 * 
	 * @param tableId
	 * @return
	 */
	@RequestMapping(value = "/getColumnInfoByDsAndTable.*", method = RequestMethod.POST)
	@ResponseBody
	public Map<String, Object> getColumnInfoByDsAndTable(String dsId,String tableNm,String templeId) {
		return tableBS.getColumnInfoByDsAndTable(dsId,tableNm,templeId);
	}
	/**
	 * ?????????????????????
	 * @return
	 */
	@RequestMapping(value = "/getColumnNumberType.*")
	@ResponseBody
	public List<String> getColumnNumberType() {
		List<String> numberType = Lists.newArrayList();
		List<String> numberList = ColumnType.NumberType;
		if (numberList != null && numberList.size() > 0) {
			for (int i = 0; i < numberList.size(); i++) {
				numberType.add(numberList.get(i).toLowerCase());
			}
		}
		return numberType;
	}

	/**
	 * ?????????????????????
	 * @return
	 */
	@RequestMapping(value = "/getColumnIntType.*")
	@ResponseBody
	public List<String> getColumnIntType() {
		List<String> numberType = Lists.newArrayList();
		List<String> intList = ColumnType.IntType;
		if (intList != null && intList.size() > 0) {
			for (int i = 0; i < intList.size(); i++) {
				numberType.add(intList.get(i).toLowerCase());
			}
		}
		return numberType;
	}
	
	/**
	 * ????????????????????????
	 * @return
	 */
	@RequestMapping(value = "/getColumnType.*")
	@ResponseBody
	public List<Map<String, String>> getColumnType(String dsId) {
		//return UdipConstants.colType;
		return tableBS.getColumnType(dsId);
	}
	
	/**
	 * ????????????????????????????????????Id???????????????
	 * 
	 * @param dsId
	 * @return
	 */
	@RequestMapping(value = "/getDatSourceList.*")
	@ResponseBody
	public List<Map<String, String>> getDatSourceList(String dsId) {
		List<Map<String, String>> columnList = Lists.newArrayList();
		String sys = BioneSecurityUtils.getCurrentUserInfo()
				.getCurrentLogicSysNo();
		try {
			List<BioneDsInfo> dsList = dataSourceBS.getEntityListByProperty(BioneDsInfo.class, "logicSysNo", sys);
			Map<String, String> m = null;
			for (int i = 0; i < dsList.size(); i++) {
				m = Maps.newHashMap();
				m.put("text", dsList.get(i).getDsName());
				m.put("id", dsList.get(i).getDsId());
				if (StringUtils.isNotBlank(dsId)) {
					if (dsId.equals(dsList.get(i).getDsId())) {
						columnList = Lists.newArrayList();
						columnList.add(0, m);
						return columnList;
					}
				} else {
					columnList.add(i, m);
				}
			}
		} catch (Exception e) {
			log.error(e);
		}

		return columnList;
	}

	/**
	 * ????????????????????????
	 * 
	 * @param dsId
	 *            ????????????Id
	 * @param tableId
	 *            ???Id
	 * @param delete
	 *            ??????????????????????????????????????????????????????true????????????false???????????????
	 * @throws SQLException
	 */
	@RequestMapping("/updateDsId")
	public void updateDsId(String dsId, String tableId, boolean delete)
			throws SQLException {
		if (StringUtils.isNotBlank(dsId) && StringUtils.isNotBlank(tableId)) {
			RptInputListTableInfo tableInfo = this.tableBS
					.getEntityById(tableId);
			// ???????????????????????????????????????????????????????????????,??????delete???true?????????
			if (delete) {
				// ??????????????????????????????,??????????????????????????????????????????????????????
				BioneDsInfo dsInfo = dataSourceBS.getEntityById(tableInfo.getDsId());
				if (dsInfo != null) {
					BioneDriverInfo bsModel = driverInfoBS.getEntityByProperty(
							BioneDriverInfo.class, "driverId", dsInfo.getDriverId());
					boolean flag = MtoolUtils.testConnection(bsModel.getDriverName(), dsInfo.getConnUrl(),
							dsInfo.getConnUser(), dsInfo.getConnPwd());
					if (flag) {
						// ??????????????????????????????????????????????????????????????????
						String templeTableName = "MID_"
								+ tableInfo.getTableEnName().trim();
						List<String> list = tableBS.getTableByDsIdAndTableName(
								tableInfo.getDsId(), templeTableName);
						if (list != null && list.size() > 0) {
							String createTempleTableSql = "drop table "
									+ templeTableName;
							tableBS.createTableBySql(createTempleTableSql,
									tableInfo.getDsId());
						}
						// ???????????????
						String createTableSql = "drop table "
								+ tableInfo.getTableEnName();
						tableBS.createTableBySql(createTableSql,
								tableInfo.getDsId());
					}
				}
			}
			if (tableInfo != null) {
				tableInfo.setDsId(dsId);
				
				/***********??????????????????**********//*
				BioneAuthObjDef objDef = this.authBS.getEntityById(BioneAuthObjDef.class , BioneSecurityUtils
						.getCurrentUserId());
						String saveMark = "03";//??????
						if(tableInfo.getDsId() !=null || "".equals(tableInfo.getDsId())){
							tableInfo.setDsId(RandomUtils.uuid2());
							saveMark = "01"; // ??????
						}
						BioneUser user = BioneSecurityUtils.getCurrentUserInfo();
						StringBuilder buff = new StringBuilder();
						buff.append("??????[").append(user.getLoginName()).append("]").append("????????????????????????????????????").append("???????????????,??????????????????:")
						.append(tableInfo.getTableCnName()).append(",????????????:").append(tableInfo.getTableType());
						tableBS.saveLog(saveMark, "????????????????????????", buff.toString(), user.getUserId(), user.getLoginName());*/
				this.tableBS.saveOrUpdateEntity(tableInfo);
			}
			
			
		}
	}

	/**
	 * ???????????????
	 * 
	 * @return
	 */
	@RequestMapping(value = "/tab1")
	public String tab1() {
		return PATH + "table-add1";
	}

	/**
	 * ????????????
	 * 
	 * @return
	 */
	@RequestMapping(value = "/tab2")
	public String tab2() {
		return PATH + "table-add2";
	}

	/**
	 * ????????????
	 * 
	 * @return
	 */
	@RequestMapping(value = "/tab3")
	public String tab3() {
		return PATH + "table-add3";
	}

	/**
	 * ?????????
	 * 
	 * @return
	 */
	@RequestMapping(value = "/tab4")
	public String tab4() {
		return PATH + "table-sql";
	}

	/**
	 * ???????????????????????????
	 * 
	 * @param result
	 *            ???????????????????????????
	 * @return
	 */
	@RequestMapping("/tableUpdateError")
	public ModelAndView tableUpdateError(String result) {
		ModelMap mm = new ModelMap();
		try {
			String con = URLDecoder.decode(result, "utf-8");
			mm.addAttribute("result", StringUtils2.javaScriptEncode(StringUtils.remove(con, '\n')));
		} catch (UnsupportedEncodingException e) {
			log.error(e);
		}
		return new ModelAndView(PATH + "table-update-error", mm);
	}

	/**
	 * ?????????Id?????????????????????????????????????????????
	 * 
	 * @param tableId
	 * @return
	 */
	@RequestMapping("/checkTempListByTableName")
	@ResponseBody
	public List<String> checkTempListByTableName(String tableId) {
		List<String> templeNameList = Lists.newArrayList();
		if (StringUtils.isNotBlank(tableId)) {
			String[] tableIds = tableId.split(",");
			for (int i = 0; i < tableIds.length; i++) {
				RptInputListTableInfo tableInfo = this.tableBS
						.getEntityByProperty(RptInputListTableInfo.class,
								"tableId", tableIds[i]);
				//???????????????????????????????????????????????? 20190613
				if (tableInfo != null && StringUtils.isNotBlank(tableInfo.getTableEnName())) {
					 List<RptInputLstTempleInfo> templeList =
							 templeBS.getUdipTempleByDsIdAndTableName(tableInfo.getDsId(),tableInfo.getTableEnName());
					 if(CollectionUtils.isNotEmpty(templeList)) {
						 for(RptInputLstTempleInfo temple : templeList) {
							 templeNameList.add(temple.getTempleName());
						 }
					 }
				}
			}
		}
		return templeNameList;
	}

	/**
	 * ?????????Id???????????????????????????????????????????????????????????????
	 * 
	 * @param tableId
	 * @param delete
	 */
	@RequestMapping("/delete")
	@ResponseBody
	public String deleteTableInfo(String tableId, boolean delete) {
		String str = StringUtils.EMPTY;
		try {
			String[] tableIds = tableId.split(",");
			String tableNames = StringUtils.EMPTY;
			List<RptInputListTableInfo> userList = Lists.newArrayList();
			for(String id : tableIds){
				userList.add(tableBS.getEntityById(id));
			}
			for (int i = 0; i < tableIds.length; i++) {
				RptInputListTableInfo tableInfos = tableBS.getEntityByProperty(
						RptInputListTableInfo.class, "tableId", tableIds[i]);
				if (i == 0) {
					tableNames = tableInfos.getTableEnName();
					str = tableInfos.getDsId() + douhao + "MID_"
							+ tableInfos.getTableEnName().trim();
				} else {
					tableNames = tableNames + "," + tableInfos.getTableEnName();
					str = str + fuhao + tableInfos.getDsId() + douhao
							+ "MID_" + tableInfos.getTableEnName().trim();
				}
				// ??????????????????????????????,??????????????????????????????????????????????????????

				if (delete) {
					BioneDsInfo dsInfo = dataSourceBS.getEntityById(tableInfos.getDsId());
					if (dsInfo != null) {
						BioneDriverInfo bsModel = driverInfoBS
								.getEntityByProperty(BioneDriverInfo.class,
										"driverId", dsInfo.getDriverId());
						boolean flag = MtoolUtils.testConnection(
								bsModel.getDriverName(), dsInfo.getConnUrl(),
								dsInfo.getConnUser(), dsInfo.getConnPwd());
						if (flag) {

							String schema = this.dataSourceBS.getSchemaByDsInfo(dsInfo);
							String createTableSql = "drop table "+
									schema+"."+ tableInfos.getTableEnName();
							tableBS.createTableBySql(createTableSql,
									tableInfos.getDsId());
							dropWeiHaiTable(createTableSql,schema+"."+ tableInfos.getTableEnName() , dsInfo.getDsId());
						}
					}
				}

				
				// ????????????????????????
				priIndexBS.removeEntityByProperty("tableId", tableIds[i]);
				// ??????????????????
				tableColBS.removeEntityByProperty("tableId", tableIds[i]);
				// ?????????
				tableBS.removeEntityById(tableIds[i]);
				
				
			}
			/**********??????????????????**********/
			
			BioneUser user = BioneSecurityUtils.getCurrentUserInfo();
			StringBuilder buff = new StringBuilder();
			buff.append("??????[").append(user.getLoginName()).append("]?????????").append(userList.size()).append("??????,??????????????????????????????:");
			boolean isFirst=true;
			for(RptInputListTableInfo users:userList)
			{
				if(isFirst)
					isFirst=false;
				else
					buff.append(",");
				buff.append("[").append(users.getTableCnName()).append(",").append(users.getTableEnName()).append("]");
			}
//			tableBS.saveLog("02", "???????????????", buff.toString(), user.getUserId(), user.getLoginName());
//			logBS.addLog(this.getRequest().getRemoteAddr(), BioneSecurityUtils.getCurrentUserInfo().getCurrentLogicSysNo(),
//					BioneSecurityUtils.getCurrentUserInfo().getUserId(), "??????????????????" + tableNames + "?????????");
		} catch (Exception e) {
			log.error(e);
		}
		return str;
	}
	
	/*
	private void deleteWeiHaiTable(){
		
	}
	*/

	/**
	 * ???????????????Id??????????????????????????????????????????
	 * 
	 * @param result
	 */
	@RequestMapping("/deleteTempleTable")
	@ResponseBody
	public void deleteTempleTable(String result) {
		try {
			if (StringUtils.isNotBlank(result)) {
				String[] tableNames = result.split(fuhao);
				String tableName = StringUtils.EMPTY;
				for (int i = 0; i < tableNames.length; i++) {
					String[] tables = tableNames[i].split(douhao,10);
					if (i == 0) {
						tableName = tables[1];
					} else {
						tableName = tableName + "," + tables[1];
					}

					List<String> tableList = tableBS
							.getTableByDsIdAndTableName(tables[0], tables[1]);
					if (StringUtils.isNotBlank(tables[1]) && tableList != null
							&& tableList.size() > 0) {
						String createTableSql = "drop table " + tables[1];
						tableBS.createTableBySql(createTableSql, tables[0]);
					}

				}
//				logBS.addLog(THIS.GETREQUEST().GETREMOTEADDR(), BIONESECURITYUTILS.GETCURRENTUSERINFO().GETCURRENTLOGICSYSNO(), BIONESECURITYUTILS
//							.getCurrentUserInfo().getUserId(), "??????????????????" + tableName + "?????????");
			}
		} catch (Exception e) {
			log.error(e);
		}
	}

	/**
	 * ???????????????
	 * 
	 * @return
	 */
	@RequestMapping(value = "/getTableType.*")
	@ResponseBody
	public List<Map<String, String>> getTableType() {
		Map<String, String> m = Maps.newHashMap();
		m.put("1", GlobalConstants4frame.TAB_INPUT);
		GlobalConstants4frame.tableType.add(m);
		return GlobalConstants4frame.tableType;
	}

	/**
	 * ??????????????????????????????????????????????????????
	 * 
	 * @param mode
	 * @param tableId
	 * @return
	 */
	@RequestMapping("/checkTableName")
	@ResponseBody
	public boolean checkTableName(RptInputListTableInfo mode, String tableId) {
		/*
		List<String> list = this.tableBS.getTableByDsIdAndTableName(mode.getDsId(), mode.getTableEnName());
		if (CollectionUtils.isEmpty(list)) {
			return true;
		} else {
			if (StringUtils.isNotBlank(tableId)) {
				RptInputListTableInfo tableInfo = this.tableBS.getEntityByProperty(RptInputListTableInfo.class, "tableId", tableId);
				if (tableInfo != null && !list.get(0).equalsIgnoreCase(tableInfo.getTableEnName()))
					return false;
				else
					return true;
			}
			return false;
		}
		*/
		//20190729 ???????????????????????????????????????????????????
		List<String> list = this.tableBS.getTableByDsIdAndTableName(mode.getDsId(), mode.getTableEnName());
		if (CollectionUtils.isEmpty(list)) {
			return true;
		} else {
			//tableId????????????????????????
			if (StringUtils.isBlank(tableId)) {
				if(mode.getTableEnName() != null) {
					for(String tablename : list) {
						if(tablename.equalsIgnoreCase(mode.getTableEnName())) {
							return false;
						}
					}
					return true;
				}
			}
			return true;
		}
		
	}

	/**
	 * ??????SQL????????????
	 * @param tableInfo
	 * @param tableColInfo
	 * @param tableIndexInfo
	 * @return
	 */
	@RequestMapping("/getTableInfoSql")
	@ResponseBody
	public Map<String, String> getTableInfoSql(RptInputListTableInfo tableInfo, String tableColInfo, String tableIndexInfo) {
		//??????????????????????????????
		String[] tableColInfos = tableColInfo.split(fuhao);
		Map<String, RptInputListTableFieldInf> tableColMap = Maps.newLinkedHashMap();
		List<RptInputListTableFieldInf> newTableColList = new ArrayList<RptInputListTableFieldInf>();
		for (int i = 0; i < tableColInfos.length; i++) {
			String[] colInfos = tableColInfos[i].split(douhao,10);
			RptInputListTableFieldInf inputTableColInfo = new RptInputListTableFieldInf();
			inputTableColInfo.setFieldEnName(colInfos[0]);
			inputTableColInfo.setFieldType(colInfos[1]);
			inputTableColInfo.setAllowNull(colInfos[2]);
			inputTableColInfo.setFieldCnName(colInfos[3]);
			inputTableColInfo.setFieldLength(colInfos[6]);
			inputTableColInfo.setDecimalLength(nullTranslateStr(colInfos[7]));
			String defaultValue = nullTranslateStr(colInfos[9]);
			if(defaultValue!=null&&!defaultValue.trim().equals("")&&!defaultValue.equalsIgnoreCase("undefined"))
				inputTableColInfo.setDefaultValue(defaultValue);
			newTableColList.add(inputTableColInfo);
			tableColMap.put(inputTableColInfo.getFieldEnName(), inputTableColInfo);
		}
		
		//????????????????????????????????????
		String[] tableIndexInfos = tableIndexInfo.split(fuhao);
		List<RptInputListTableConstraint> priIndexList = Lists.newArrayList();
		if (ArrayUtils.isNotEmpty(tableIndexInfos) && !StringUtils.EMPTY.equals(tableIndexInfos[0])) {
			for (int i = 0; i < tableIndexInfos.length; i++) {
				RptInputListTableConstraint udipPriIndexInfo = new RptInputListTableConstraint();
				String[] indexInfos = tableIndexInfos[i].split(douhao,10);
				String keyName = StringUtils.EMPTY;
				if (UdipConstants.TAB_PRIMARY.equals(indexInfos[0])) {
					keyName = "PK_" + i + tableInfo.getTableEnName();
				} else if (UdipConstants.TAB_UNIQUE.equals(indexInfos[0])) {
					keyName = "UNQ_" + i + tableInfo.getTableEnName();
				} else if (UdipConstants.TAB_INDEX.equals(indexInfos[0])) {
					keyName = "INDEX_" + i + tableInfo.getTableEnName();
				}
				udipPriIndexInfo.setKeyName(keyName);
				udipPriIndexInfo.setKeyType(indexInfos[0]);
				
				// ??????????????????????????????????????????
				String indexColumn = indexInfos[1].toUpperCase();
				if ("1".equals(tableInfo.getTableType())) {
					indexColumn = indexColumn += ";" + UdipConstants.TAB_DATA_DATE;
					indexColumn = indexColumn += ";" + UdipConstants.TAB_DATA_CASE;
					indexColumn = indexColumn += ";" + UdipConstants.TAB_OPER_ORG;
					indexColumn = indexColumn += ";" + UdipConstants.TAB_ORDER_NO;
				}
				udipPriIndexInfo.setKeyColumn(indexColumn);
				udipPriIndexInfo.setTableId(tableInfo.getTableId());
				udipPriIndexInfo.setId(RandomUtils.uuid2());
				// priIndexBS.saveEntity(udipPriIndexInfo);
				priIndexList.add(udipPriIndexInfo);
			}
		}
		
		// ??????????????????????????????????????????
		if (UdipConstants.TAB_INPUT.equals(tableInfo.getTableType())) {
			tableColMap.putAll(UdipConstants.colMaps.get(this.dataSourceBS.getDataSourceType(tableInfo.getDsId()).toUpperCase()));
		}
		//??????????????????
		String createTabSql = this.tableBS.getTableInfoSql(tableInfo, tableColMap, priIndexList);
		
		//????????????????????????
		StringBuilder colUpdateSql = new StringBuilder();	
		Map<String, RptInputListTableFieldInf> oldTableColMap = Maps.newLinkedHashMap();
		List<RptInputListTableFieldInf> oldTableColList = this.tableColBS.getEntityListByProperty(RptInputListTableFieldInf.class, "tableId", tableInfo.getTableId());
		String updateSql = "";
		if(oldTableColList != null && oldTableColList.size() > 0){
			//?????????????????????????????????????????????????????????????????????????????????
			for(RptInputListTableFieldInf oldTableCol : oldTableColList){
				//????????????????????????map
				oldTableColMap.put(oldTableCol.getFieldEnName(), oldTableCol);	
				RptInputListTableFieldInf newTableCol = tableColMap.get(oldTableCol.getFieldEnName());
				if(newTableCol != null){//????????????sql
					updateSql = this.tableBS.getUpdateColSql(newTableCol, oldTableCol, tableInfo);
					colUpdateSql.append(updateSql);
				}else{//????????????sql
					updateSql = this.tableBS.getDropColSql(oldTableCol.getFieldEnName(), tableInfo);
					colUpdateSql.append(updateSql);
				}
			}
			//??????????????????????????????????????????????????????
			for(RptInputListTableFieldInf newTableCol : newTableColList){
				RptInputListTableFieldInf oldTableCol = oldTableColMap.get(newTableCol.getFieldEnName());
				if(oldTableCol == null){//????????????sql
					updateSql = this.tableBS.getAddColSql(newTableCol, tableInfo);
					colUpdateSql.append(updateSql);
				}
			}
		}
		
		//???????????????????????????????????????????????????????????????
		/*List<RptInputListTableConstraint> oldPriIndexList = this.tableColBS.getEntityListByProperty(RptInputListTableConstraint.class, "tableId", tableInfo.getTableId());
		List<RptInputListTableConstraint> oldPriIndexList2 = new ArrayList<RptInputListTableConstraint>();
		for(RptInputListTableConstraint oldPriIndex : oldPriIndexList){
			// ??????????????????????????????????????????
			String indexColumn = oldPriIndex.getKeyColumn().toUpperCase();
			if ("1".equals(tableInfo.getTableType())) {
				indexColumn = indexColumn += ";" + UdipConstants.TAB_DATA_DATE;
				indexColumn = indexColumn += ";" + UdipConstants.TAB_DATA_CASE;
				indexColumn = indexColumn += ";" + UdipConstants.TAB_OPER_ORG;
				indexColumn = indexColumn += ";" + UdipConstants.TAB_ORDER_NO;
			}
			oldPriIndex.setKeyColumn(indexColumn);
			oldPriIndexList2.add(oldPriIndex);
			if(!priIndexList.contains(oldPriIndex)){
				//?????????????????????
				updateSql = this.tableBS.getDeletePriIndexSql(oldPriIndex, tableInfo);
				colUpdateSql.append(updateSql);
			}
		}
		
		for(RptInputListTableConstraint priIndex : priIndexList){
			if(!oldPriIndexList.contains(priIndex)){
				//????????????????????????
				updateSql = this.tableBS.getAddPriIndexSql(priIndex, tableInfo);
				colUpdateSql.append(updateSql);
			}
		}*/
		
		Map<String, String> sqlMap = Maps.newHashMap();
		sqlMap.put("createTabSql", createTabSql);
		sqlMap.put("colUpdateSql", colUpdateSql.toString());
		return sqlMap;
	}


	/**
	 * ????????????sql?????????sql???????????????
	 * 
	 * @param createTempTableInfo
	 * @param tableId
	 * @throws SQLException
	 */
	@RequestMapping("/createTempTableInfo")
	@ResponseBody
	public Map<String, String> createTempTableInfo(
			RptInputListTableInfo tableInfo, String tableColInfo,
			String tableIndexInfo, String tableId) throws SQLException {
		String[] tableColInfos = tableColInfo.split(fuhao);

		String[] colInfos = null;
		Map<String, RptInputListTableFieldInf> tableColMap = Maps.newHashMap();
		for (int i = 0; i < tableColInfos.length; i++) {
			RptInputListTableFieldInf udipTableColInfo = new RptInputListTableFieldInf();
			colInfos = tableColInfos[i].split(douhao,10);
			udipTableColInfo.setFieldEnName(colInfos[0]);
			udipTableColInfo.setFieldType(colInfos[1]);
			udipTableColInfo.setAllowNull(colInfos[2]);
			udipTableColInfo.setFieldLength(colInfos[6]);
			udipTableColInfo.setDecimalLength(colInfos[7]);
			if (colInfos.length <= 3) {
				udipTableColInfo.setFieldCnName(StringUtils.EMPTY);
			} else {
				udipTableColInfo.setFieldCnName(colInfos[3]);
			}
			tableColMap
					.put(udipTableColInfo.getFieldEnName(), udipTableColInfo);
			udipTableColInfo.setDefaultValue(colInfos[8]);
		}

		String[] tableIndexInfos = tableIndexInfo.split(fuhao);
		List<RptInputListTableConstraint> priList = Lists.newArrayList();
		if (tableIndexInfos != null && tableIndexInfos.length > 0 && !StringUtils.EMPTY.equals(tableIndexInfos[0])) {
			List<RptInputListTableInfo> tableList = Lists.newArrayList();
			if (StringUtils.isNotBlank(tableId)) {
				RptInputListTableInfo inputTableInfo = (RptInputListTableInfo) this.tableBS.getEntityById(tableId);
				if (inputTableInfo != null)
					tableList.add(inputTableInfo);
			} else {
				tableList = this.tableBS.getTableInfoByDsIdAndTableName(tableInfo.getDsId(), tableInfo.getTableEnName());
			}
			if (tableList != null && tableList.size() > 0) {
				priList = this.priIndexBS.getEntityListByProperty(RptInputListTableConstraint.class, "tableId", tableList.get(0).getTableId());
				if (priList != null && priList.size() > 0) {
					for (int i = 0; i < priList.size(); i++) {

					}
				}
			}
		}
		tableInfo.setTableEnName("MID_" + tableInfo.getTableEnName().toUpperCase());
		String sql = tableBS.getTableInfoSql(tableInfo, tableColMap, priList);
		List<String> tableList = tableBS.getTableByDsIdAndTableName(tableInfo.getDsId(), tableInfo.getTableEnName());
		if (tableList != null && tableList.size() > 0) {
			String createTableSqls = "drop table " + tableInfo.getTableEnName();
			tableBS.createTableBySql(createTableSqls, tableInfo.getDsId());
		}
		dropWeiHaiTable(sql,tableInfo.getTableEnName() , tableInfo.getDsId());
		createWeiHaiTable(sql,tableInfo.getTableEnName() , tableInfo.getDsId());
		Map<String, String> resultMap = Maps.newHashMap();
		resultMap.put("result", tableBS.createTableBySql(sql, tableInfo.getDsId()));
//		logBS.addLog(this.getRequest().getRemoteAddr(), BioneSecurityUtils.getCurrentUserInfo().getCurrentLogicSysNo(),
//				BioneSecurityUtils.getCurrentUserInfo().getUserId(), "??????????????????" + tableInfo.getTableEnName() + "????????????sql");
		
/*
				*//***********??????????????????**********//*
		BioneAuthObjDef objDef = this.tableBS.getEntityById(BioneAuthObjDef.class , BioneSecurityUtils
				.getCurrentUserId());
				String saveMark = "03";//??????
		if(tableInfo.getTableId() !=null || "".equals(tableInfo.getTableId())){
			tableInfo.setTableId(RandomUtils.uuid2());
			saveMark = "01"; // ??????
		}
		BioneUser user = BioneSecurityUtils.getCurrentUserInfo();
		StringBuilder buff = new StringBuilder();
		buff.append("??????[").append(user.getLoginName()).append("]").append(saveMark.equals("03") ? "??????" : "??????").append("???????????????,??????????????????:")
		.append(tableInfo.getCreateUser()).append(",????????????:").append(tableInfo.getTableCnName());
		tableBS.saveLog(saveMark, "???????????????", buff.toString(), user.getUserId(), user.getLoginName());*/
		
		return resultMap;
	}
	
	private void dropWeiHaiTable(String sql,String tableNm,String dsId) throws SQLException{
		String dropDeleteTableSql = "drop table  " + tableNm+"_DELETE";
		tableBS.createTableBySql(dropDeleteTableSql, dsId);
		String dropValidateTableSql = "drop table  " + tableNm+"_VALIDATE";
		tableBS.createTableBySql(dropValidateTableSql, dsId);
	}
	
	private void createWeiHaiTable(String sql,String tableNm,String dsId) throws SQLException{
		sql = sql.substring(0,sql.indexOf("alter table"));
		String deleteTableSql = sql.replaceAll(tableNm, tableNm+"_DELETE");
		tableBS.createTableBySql(deleteTableSql, dsId);
		
		
		String validateTableSql = sql.replaceAll(tableNm, tableNm+"_VALIDATE");
		tableBS.createTableBySql(validateTableSql, dsId);
	}

	/**
	 * ????????????sql?????????sql???????????????
	 * 
	 * @param createTableSql
	 * @param dsId
	 * @param tableId
	 * @throws SQLException
	 */
	@RequestMapping("/createTableInfo")
	@ResponseBody
	public Map<String, String> createTableInfo(String createTableSql, String dsId, String tableId, String tableName) throws SQLException {
		RptInputListTableInfo mode = new RptInputListTableInfo(dsId, tableName);
		if (StringUtils.isNotBlank(tableId)) {
			// ????????????????????????
			RptInputListTableInfo udipTableInfo = this.tableBS.getEntityByProperty(RptInputListTableInfo.class, "tableId", tableId);
			
			String schema = this.dataSourceBS.getSchemaByDsId(dsId);
			
			String createTableSqls = "drop table " + schema+"."+udipTableInfo.getTableEnName();
			this.tableBS.createTableBySql(createTableSqls, dsId);
		} else {
			boolean flag = checkTableName(mode, tableId);
			if (flag == false) { // ?????????????????????drop table
				String createTableSqls = "drop table " + tableName;
				this.tableBS.createTableBySql(createTableSqls, dsId);
			}
		}
		dropWeiHaiTable(createTableSql,tableName , dsId);
		createWeiHaiTable(createTableSql,tableName , dsId);
		Map<String, String> resultMap = Maps.newHashMap();
		resultMap.put("result", this.tableBS.createTableBySql(createTableSql, dsId));
//		logBS.addLog(this.getRequest().getRemoteAddr(), BioneSecurityUtils.getCurrentUserInfo().getCurrentLogicSysNo(),
//				BioneSecurityUtils.getCurrentUserInfo().getUserId(), "??????????????????" + tableName + "????????????sql");
		return resultMap;
	}
	
	/**
	 * ??????????????????sql
	 * @param createTableSql
	 * @param dsId
	 * @param tableId
	 * @param tableName
	 * @return
	 * @throws SQLException
	 */
	@RequestMapping("/colUpdateSql")
	@ResponseBody
	public Map<String, String> colUpdateSql(String colUpdateSql, String dsId, String tableId, String tableName) throws SQLException {
		Map<String, String> resultMap = Maps.newHashMap();
		resultMap.put("result", this.tableBS.createTableBySql(colUpdateSql, dsId));
		return resultMap;
	}

	private String nullTranslateStr(String str){
		if(StringUtils.isBlank(str)){
			return "";
		}else{
			if("null".equals(str) || "undefined".equals(str)){
				return "";
			}
			return str;
		}
	}
	/**
	 * ??????????????????????????????????????????????????????
	 * @param udipTableInfo
	 * @param tableColInfo
	 * @param tableIndexInfo
	 * @return 
	 */
	@SuppressWarnings("rawtypes")
	@RequestMapping("/saveTableInfo")
	@ResponseBody
	public String saveTableInfo(RptInputListTableInfo udipTableInfo, String tableColInfo, String tableIndexInfo) {
		// String saveMark = "03";// ??????
		if(udipTableInfo.getTableId() == null || "".equals(udipTableInfo.getTableId())){
			// saveMark = "01";
		}
		// ????????????????????????
		/*List<RptInputListTableInfo> udipTableList = tableBS.getTableInfoByDsIdAndTableName(udipTableInfo.getDsId(), udipTableInfo.getTableEnName());
		RptInputListTableInfo tableInfo = new RptInputListTableInfo();
		if (CollectionUtils.isNotEmpty(udipTableList)) {
			tableInfo = udipTableList.get(0);
		}*/
		RptInputListTableInfo tableInfo;
		if(StringUtils.isEmpty(udipTableInfo.getTableId()))
			tableInfo = new RptInputListTableInfo();
		else
			tableInfo = tableBS.getEntityById(udipTableInfo.getTableId());
		if (tableInfo != null && tableInfo.getTableId() != null && !StringUtils.EMPTY.equals(tableInfo.getTableId())) {
			tableInfo.setDsId(udipTableInfo.getDsId());
			tableInfo.setTableType(udipTableInfo.getTableType());
			tableInfo.setTableEnName(udipTableInfo.getTableEnName().toUpperCase());
			tableInfo.setTableCnName(udipTableInfo.getTableCnName());
			tableInfo.setTableSpace(udipTableInfo.getTableSpace());
			this.tableBS.updateEntity(tableInfo);
		} else {
			
			String tableId = RandomUtils.uuid2();
			tableInfo.setDsId(udipTableInfo.getDsId());
			tableInfo.setTableType(udipTableInfo.getTableType());
			tableInfo.setTableEnName(udipTableInfo.getTableEnName().toUpperCase());
			tableInfo.setTableCnName(udipTableInfo.getTableCnName());
			tableInfo.setTableId(tableId);
			tableInfo.setCreateUser(BioneSecurityUtils.getCurrentUserInfo().getUserId());
			tableInfo.setCreateDate(DateUtils.getYYYY_MM_DD_HH_mm_ss());
			tableInfo.setLogicSysNo(BioneSecurityUtils.getCurrentUserInfo().getCurrentLogicSysNo());
			tableInfo.setTableSpace(udipTableInfo.getTableSpace());
			this.tableBS.saveEntity(tableInfo);
		}

		String[] tableColInfos = tableColInfo.split(fuhao); // ????????????????????????
		RptInputListTableFieldInf udipTableColInfo = new RptInputListTableFieldInf();
		List<RptInputListTableFieldInf> tableColInfoList = Lists.newArrayList();
		this.tableColBS.removeEntityByProperty("tableId", tableInfo.getTableId());
		for (int i = 0; i < tableColInfos.length; i++) {
			String[] colInfos = tableColInfos[i].split(douhao,10);
			udipTableColInfo.setFieldEnName(colInfos[0].toUpperCase());
			udipTableColInfo.setFieldType(colInfos[1].toUpperCase());
			udipTableColInfo.setAllowNull(colInfos[2]);
			if (colInfos.length <= 3 || StringUtils.isBlank(colInfos[3])) {
				udipTableColInfo.setFieldCnName(StringUtils.EMPTY);
			} else {
				udipTableColInfo.setFieldCnName(colInfos[3]);
			}
			if (StringUtils.isNotBlank(colInfos[4])) {
				udipTableColInfo.setOrderNo(Integer.parseInt(colInfos[4], 10));
			}
			udipTableColInfo.setTableId(tableInfo.getTableId());
			udipTableColInfo.setId(RandomUtils.uuid2());
			udipTableColInfo.setFieldLength(colInfos[6]);
			
			udipTableColInfo.setDecimalLength(nullTranslateStr(colInfos[7]));
			
			udipTableColInfo.setDefaultValue(nullTranslateStr(colInfos[9]));
			tableColInfoList.add(udipTableColInfo);
			this.tableColBS.saveOrUpdateEntity(udipTableColInfo);
		}
		if (UdipConstants.TAB_INPUT.equals(udipTableInfo.getTableType())) {
			Map<String, RptInputListTableFieldInf> tableColMap = UdipConstants.colMaps.get(this.dataSourceBS.getDataSourceType(tableInfo.getDsId()).toUpperCase());
			Iterator iterator = tableColMap.keySet().iterator();
			while (iterator.hasNext()) {
				String colName = (String) iterator.next();
				RptInputListTableFieldInf udipTableColInfo1 = tableColMap.get(colName);
				udipTableColInfo =  new RptInputListTableFieldInf();
				udipTableColInfo.setOrderNo(udipTableColInfo.getOrderNo() + 1);
				udipTableColInfo.setFieldEnName(colName.toUpperCase());
				udipTableColInfo.setFieldType(udipTableColInfo1.getFieldType());
				udipTableColInfo.setAllowNull("true");
				udipTableColInfo.setFieldCnName(udipTableColInfo1.getFieldCnName());
				udipTableColInfo.setTableId(tableInfo.getTableId());
				udipTableColInfo.setId(RandomUtils.uuid2());
				udipTableColInfo.setFieldLength(udipTableColInfo1.getFieldLength());
				udipTableColInfo.setDecimalLength(nullTranslateStr(udipTableColInfo1.getDecimalLength()));
				this.tableColBS.saveOrUpdateEntity(udipTableColInfo);
			}
		}

		this.priIndexBS.removeEntityByProperty("tableId", tableInfo.getTableId());
		if (StringUtils.isNotEmpty(tableIndexInfo)) { // ????????????????????????
			String[] tableIndexInfos = tableIndexInfo.split(fuhao);
			RptInputListTableConstraint udipPriIndexInfo = new RptInputListTableConstraint();
			for (int i = 0; i < tableIndexInfos.length; i++) {
				String[] indexInfos = tableIndexInfos[i].split(douhao,10);
				List<Map<String, String>> keyType = UdipConstants.keyType;
				for (int k = 0; k < keyType.size(); k++) {
					Map<String, String> keyMap = keyType.get(k);
					if (keyMap.get(UdipConstants.ID).equals(indexInfos[0])) {
						String priName = keyMap.get(UdipConstants.TAB_BEFORE) + i + udipTableInfo.getTableEnName().toUpperCase();
						// ???????????????30??????????????????????????????????????????
						int leng = 30 - DataUtils.INPUT_TEMP_TABLE.trim().length();
						if (priName.length() > leng) {
							udipPriIndexInfo.setKeyName(priName.substring(0, leng));
						} else {
							udipPriIndexInfo.setKeyName(priName);
						}
						udipPriIndexInfo.setKeyType(keyMap.get(UdipConstants.ID));
						if (UdipConstants.TAB_PRIMARY.equals(indexInfos[0])) {
							indexInfos[1] += douhao + UdipConstants.TAB_DATA_DATE + douhao + UdipConstants.TAB_DATA_CASE + douhao + UdipConstants.TAB_OPER_ORG + douhao + UdipConstants.TAB_ORDER_NO;
							String[] columns = indexInfos[1].split(";");
							for (int n = 0; n < columns.length; n++) {
								List<RptInputListTableFieldInf> udipTableColInfoList = tableBS.getTableColInfoByIdAndCol(tableInfo.getTableId(), "'" + columns[n] + "'");
								if (CollectionUtils.isNotEmpty(udipTableColInfoList)) {
									udipTableColInfoList.get(0).setAllowNull("false");
									this.tableColBS.saveOrUpdateEntity(udipTableColInfoList.get(0));// ????????????????????????????????????????????????
								}
							}
						}
					}
				}
				
				udipPriIndexInfo.setKeyColumn( indexInfos[1].toUpperCase());
				udipPriIndexInfo.setTableId(tableInfo.getTableId());
				udipPriIndexInfo.setId(RandomUtils.uuid2());
				this.priIndexBS.saveEntity(udipPriIndexInfo);
				
				
			}
		}
//		BioneUser user = BioneSecurityUtils.getCurrentUserInfo();
//		StringBuilder buff  = new StringBuilder();
//		buff.append("??????[").append(user.getLoginName()).append("]").append(saveMark.equals("01") ? "??????" : "??????").append("??????????????????,???????????????????????????:")
//		.append(udipTableInfo.getTableCnName()).append(",???????????????:").append(udipTableInfo.getTableEnName());
//		tableColBS.saveLog(saveMark, "???????????????", buff.toString(), user.getUserId(), user.getLoginName());
//		logBS.addLog(this.getRequest().getRemoteAddr(), BioneSecurityUtils.getCurrentUserInfo().getCurrentLogicSysNo(), BioneSecurityUtils.getCurrentUserInfo().getUserId(), "??????????????????" + udipTableInfo.getTableEnName() + "?????????");
		return tableInfo.getTableId();
	}


	@RequestMapping(value = "/saveDataLoadSql" ,method = RequestMethod.POST,produces = "text/html;charset=UTF-8")
	@ResponseBody
	public String saveDataLoadSql(@RequestBody TableSqlVO tableSqlVO){
		return tableBS.saveDataLoadInfo(tableSqlVO,"1");
	}


	@RequestMapping("/saveDataLoadData")
	@ResponseBody
	public String saveDataLoadData(@RequestBody TableDataVO tableDataVO){
		String rs  = tableBS.saveDataLoadInfo(tableDataVO,"2");
		return rs;
	}
	
	/**
	 * ??????????????????????????????????????????????????????
	 * @param udipTableInfo ???????????????
	 * @param tableColInfo ?????????????????????
	 * @param tableIndexInfo ????????????????????????
	 * @param update ??????????????????????????????????????????
	 */
	@RequestMapping("/updateTableInfo")
	@ResponseBody
	public Map<String, String> updateTableInfo(RptInputListTableInfo udipTableInfo, String tableColInfo, String tableIndexInfo, String update) {

		// ??????????????????
		Map<String, String> resultMap = this.tableBS.updateTableSql(udipTableInfo, tableColInfo, tableIndexInfo, update);
//		logBS.addLog(this.getRequest().getRemoteAddr(), BioneSecurityUtils.getCurrentUserInfo().getCurrentLogicSysNo(),
//				BioneSecurityUtils.getCurrentUserInfo().getUserId(), "??????????????????" + udipTableInfo.getTableEnName() + "?????????");
		return resultMap;
	}
	
	/**
	 * ??????tableId?????????????????????
	 * 
	 * @param tableId
	 * @return
	 */
	@RequestMapping("/getTableColInfoById")
	@ResponseBody
	public List<RptInputListTableFieldInf> getTableColInfoById(String tableId) {
		List<RptInputListTableFieldInf> lists = Lists.newArrayList();
		if (StringUtils.isNotBlank(tableId)) {
			RptInputListTableInfo tableInfo = this.tableBS.getEntityById(tableId);
			StringBuilder colNames = new StringBuilder();
			// ??????????????????????????????????????????
			if (tableInfo != null && "1".equals(tableInfo.getTableType())) {
				Map<String, RptInputListTableFieldInf> colMaps = UdipConstants.colMaps.get(this.dataSourceBS.getDataSourceType(tableInfo.getDsId()).toUpperCase());
				@SuppressWarnings("rawtypes")
				Iterator iterator1 = colMaps.keySet().iterator();
				int n = 0;
				while (iterator1.hasNext()) {
					String colName = (String) iterator1.next();
					n++;
					if (n == colMaps.size()) {
						colNames.append("'").append(colName).append("'");
					} else {
						colNames.append("'").append(colName).append("',");
					}
				}

			}
			lists = tableBS.getTableColInfoById(tableId, colNames.toString());
		}
		return lists;
	}

	/**
	 * ???????????????
	 * @param dsId
	 * @return
	 */
	@RequestMapping(value = "/showTableSpace", method = RequestMethod.GET)
	public ModelAndView showTableSpace(String dsId) {
		dsId = StringUtils2.javaScriptEncode(dsId);
		return new ModelAndView("/input/table/table-showtablespace", "dsId", dsId);
	}
	
	/**
	 * ???????????????
	 * @param dsId
	 * @param rf
	 * @return
	 */
	@RequestMapping(value = "/getTableSpace.*", method = RequestMethod.POST)
	@ResponseBody
	public Map<String, Object> getTableSpace(String dsId,Pager rf) {
		Map<String, Object> map = Maps.newHashMap();
		List<Map<String,String>>l = this.tableBS.getTableSpace(dsId,rf);
		if(l!=null){
			map.put("Rows", l);
			map.put("Total", l.size());
		}
		return  map;
	}
	
	/**
	 * ?????????????????????????????????????????????????????????
	 * 
	 * @return
	 */
	@RequestMapping(value = "/getColumnForPri.*")
	@ResponseBody
	public List<Map<String, String>> getColumnForPri() {
		List<Map<String, String>> colPriType = Lists.newArrayList();
		Map<String, String> colMapss = Maps.newHashMap();
		colMapss.put("id", "SYS_DATA_CASE");
		colMapss.put("text", "???????????????");
		colPriType.add(colMapss);
		return colPriType;
	}

	/**
	 * ??????tableId???????????????????????????
	 * 
	 * @param tableId
	 * @return
	 */
	@RequestMapping("/getPriIndexInfoById")
	@ResponseBody
	public List<RptInputListTableConstraint> getPriIndexInfoById(String tableId) {
		List<RptInputListTableConstraint> priList = Lists.newArrayList();
		if (StringUtils.isNotBlank(tableId)) {
			List<RptInputListTableConstraint> priList2 = this.priIndexBS.getEntityListByProperty(RptInputListTableConstraint.class, "tableId", tableId);
			RptInputListTableInfo tableInfo = this.tableBS.getEntityById(tableId);
			Map<String, RptInputListTableFieldInf> colMaps = UdipConstants.colMaps.get(this.dataSourceBS.getDataSourceType(tableInfo.getDsId()).toUpperCase());
			for (RptInputListTableConstraint pri : priList2) {
				if ("primary".equalsIgnoreCase(pri.getKeyType())) {
					String[] keyColumns = StringUtils.split(pri.getKeyColumn(), ',');
					List<String> pList = Lists.newArrayList();
					for (String keyColumn : keyColumns) {
						if (!colMaps.containsKey(keyColumn.toUpperCase())) {
							pList.add(keyColumn);
						}
					}
					pri.setKeyColumn(StringUtils.join(pList, ','));
					priList.add(0, pri);
				} else {
					priList.add(pri);
				}
			}
		}
		return priList;
	}

	/**
	 * ??????????????????????????????????????????
	 * 
	 * @param id
	 * @return
	 */
	@RequestMapping(value = "/{id}/queryTemp", method = RequestMethod.GET)
	public ModelAndView chack(@PathVariable("id") String id) {
		id = StringUtils2.javaScriptEncode(id);
		return new ModelAndView(PATH + "table-temp-list", "id", id);
	}

	/**
	 * ?????????????????????????????????????????????????????????????????????
	 * 
	 * @param tableId
	 * @return
	 */
	@RequestMapping("/getTempListByTableName")
	@ResponseBody
	public Map<String, Object> getTempListByTableName(String tableId) {
		List<RptInputLstTempleInfo> tempList = Lists.newArrayList();
		if (StringUtils.isNotBlank(tableId)) {
			RptInputListTableInfo udipTableInfo = this.tableBS
					.getEntityById(tableId);
			if (udipTableInfo != null
					&& StringUtils.isNotBlank(udipTableInfo.getTableEnName())) {
				 List<RptInputLstTempleInfo> udipTempleList =
				 templeBS.getUdipTempleByDsIdAndTableName(udipTableInfo.getDsId(),
				 udipTableInfo.getTableEnName());
				if (udipTempleList != null && udipTempleList.size() > 0) {
					tempList = udipTempleList;
				}
			}
		}
		Map<String, Object> objDefMap = Maps.newHashMap();
		objDefMap.put("Rows", tempList);
		return objDefMap;
	}
	

	/**
	 * ???????????????????????????Id?????????????????????????????????????????????
	 * @param tableEnName
	 * @param dsId
	 * @return
	 */
	@RequestMapping("/checkTempleTableName")
	@ResponseBody
	public boolean checkTempleTableName(String tableEnName, String dsId, String tableId) {
		String templeTableName = DataUtils.INPUT_TEMP_TABLE + tableEnName.trim();
		if (StringUtils.isNotBlank(tableId)) {
			RptInputListTableInfo tableInfo = tableBS.getEntityById(tableId);
			// ?????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????
			if (tableInfo != null && !tableInfo.getTableEnName().equalsIgnoreCase(tableEnName)) {
				templeTableName = DataUtils.INPUT_TEMP_TABLE + tableInfo.getTableEnName().trim();
			}
		}
		List<String> list = tableBS.getTableByDsIdAndTableName(dsId, templeTableName);
		return CollectionUtils.isEmpty(list);
	}
	
	/**
	 * ?????????????????????????????????????????????????????????????????????????????????
	 * @param tableId
	 * @param dsId
	 * @param tableName
	 * @param tableType
	 * @return
	 */
	@RequestMapping("/checkTempAndDataLibByTableName")
	@ResponseBody
	public String checkTempAndDataLibByTableName(String tableId, String dsId, String tableName, String tableType) {
		StringBuilder message = new StringBuilder();
		if (StringUtils.isNotBlank(tableId) && StringUtils.isBlank(tableName)) {
			RptInputListTableInfo tableInfo = this.tableBS.getEntityById(tableId);
			tableName = tableInfo.getTableEnName();
			dsId = tableInfo.getDsId();
		}
		if (StringUtils.isNotBlank(dsId) && StringUtils.isNotBlank(tableName)) {
			if (StringUtils.isNotBlank(tableType) && UdipConstants.TAB_INPUT.equals(tableType)) {
				List<RptInputLstTempleInfo> udipTempleList = this.templeBS.getUdipTempleByDsIdAndTableName(dsId, tableName);
				if (udipTempleList != null && udipTempleList.size() > 0) {
					message.append("????????????,???????????????????????????????????????");
					for (int i = 0; i < udipTempleList.size(); i++) {
						RptInputLstTempleInfo temple = udipTempleList.get(i);
						if (i == udipTempleList.size() - 1) {
							message.append(temple.getTempleName());
						} else {
							message.append(temple.getTempleName()).append(",");
						}
					}
					message.append("???,????????????????????????????????????????????????????????????????????????????????????");
					return message.toString();
				}
			} else if (StringUtils.isNotBlank(tableType) && UdipConstants.TAB_LIB.equals(tableType)) {
				List<RptInputListDataDictInfo> dataLibList = udipDataLibBS.getUdipDataLibByDsIdAndTableName(dsId, tableName);
				if (dataLibList != null && dataLibList.size() > 0) {
					message.append("????????????,???????????????????????????????????????");
					for (int i = 0; i < dataLibList.size(); i++) {
						RptInputListDataDictInfo dataLib = dataLibList.get(i);
						if (i == dataLibList.size() - 1) {
							message.append(dataLib.getDictName());
						} else {
							message.append(dataLib.getDictName()).append(",");
						}
					}
					message.append("???,???????????????????????????????????????????????????????????????????????????????????????????????????");
					return message.toString();
				}
			}
		}
		return message.toString();
	}
	
	/**
	 * ???????????????????????????
	 * 
	 * @param tableName
	 * @return
	 */
	@RequestMapping("/tableNameValid")
	@ResponseBody
	public boolean tableNameValid(String dsId, String tableEnName, String oldTableEnName) {
		if (StringUtils.isNotBlank(oldTableEnName)) {
			if (tableEnName.equals(oldTableEnName)) {
				return true;
			}
		}
		List<RptInputListTableInfo> tableList = this.tableBS.findByPropertys(
				RptInputListTableInfo.class, new String[] { "dsId", "tableEnName" }, new String[] { dsId, tableEnName });
		return CollectionUtils.isEmpty(tableList);
	}
	

	@RequestMapping(value="/getSqlInfo", method = RequestMethod.GET)
	@ResponseBody
	public Map<String, Object> getSqlInfo(String cfgId){
		return tableBS.getSqlInfoVO(cfgId);
	}

	@RequestMapping("/getDataInfo")
	@ResponseBody
	public String getDataInfo(String cfgId){
		return  tableBS.getDataInfo(cfgId);
	}


	@RequestMapping("/getTableColumnInfo")
	@ResponseBody
	public List<RptInputListTableFieldInf>getTableColumnInfo(String tableId,String cfgId){
		List<RptInputListTableFieldInf> lists = Lists.newArrayList();
		if (StringUtils.isNotBlank(tableId)) {
			RptInputListTableInfo tableInfo = this.tableBS.getEntityById(tableId);
			StringBuilder colNames = new StringBuilder();
			// ??????????????????????????????????????????
			if (tableInfo != null && "1".equals(tableInfo.getTableType())) {
				Map<String, RptInputListTableFieldInf> colMaps = UdipConstants.colMaps.get(this.dataSourceBS.getDataSourceType(tableInfo.getDsId()).toUpperCase());
				@SuppressWarnings("rawtypes")
				Iterator iterator1 = colMaps.keySet().iterator();
				int n = 0;
				while (iterator1.hasNext()) {
					String colName = (String) iterator1.next();
					n++;
					if (n == colMaps.size()) {
						colNames.append("'").append(colName).append("'");
					} else {
						colNames.append("'").append(colName).append("',");
					}
				}

			}
			lists = tableBS.getTableColInfoById(tableId, colNames.toString());
		}
		return lists;
	}

	// ???????????????Id???????????????????????????????????????????????????????????????
	@RequestMapping(value = "/dataTableList")
	@ResponseBody
	public Map<String, Object> dataTableList(String id) {
		List<RptInputListTableInfo> tableList = Lists.newArrayList();
		try {
			tableList = tableBS.getDataTableList(id);
		} catch (Exception e) {
			logger.error(e.toString());
		}
		Map<String, Object> objDefMap = Maps.newHashMap();
		objDefMap.put("Rows", tableList);
		return objDefMap;
	}

	@RequestMapping("/queryData")
	public ModelAndView queryData(){
		ModelAndView modelAndView = new ModelAndView(PATH + "table-input-query-index");
		return modelAndView;
	}

	@RequestMapping("/queryDataList")
	public ModelAndView queryDataList(String templateId){
		ModelAndView modelAndView = new ModelAndView(PATH + "table-input-query-list");
		modelAndView.addObject("templateId", templateId);
		return modelAndView;
	}

	/**
	 * @????????????: ?????????????????????
	 * @?????????: huzq1 
	 * @????????????: 2021/9/10 17:10
	  * @param pager
	 * @param templateId
	 * @return
	 **/
	@ResponseBody
	@RequestMapping("/queryInputTable")
	public Map<String, Object> queryInputTable(Pager pager, String templateId){
		return tableBS.queryInputTable(pager, templateId, false);
	}

	/**
	 * @????????????: ??????????????????
	 * @?????????: huzq1 
	 * @????????????: 2021/9/10 17:11
	  * @param tableName
	 * @return
	 **/
	@ResponseBody
	@RequestMapping("/listtables")
	public List<CommonTreeNode> listtables(String tableName){
		return tableBS.listtables(tableName);
	}

	/**
	 * @????????????: ????????????????????????????????????
	 * @?????????: huzq1
	 * @????????????: 2021/10/14 16:24
	  * @param
	 * @return
	 **/
	@RequestMapping("/inputQueryEditPage")
	public ModelAndView inputQueryEditPage(String paramStr, String templateId){
		ModelMap mm = new ModelMap();
		RptInputLstTempleInfo temp = templeBS.getEntityById(templateId);
		if (temp != null && StringUtils.isNotBlank(temp.getOrgColumn()) && "no".equals(temp.getAllowInputLower())) {
			mm.addAttribute("orgOwn", StringUtils2.javaScriptEncode(BioneSecurityUtils .getCurrentUserInfo().getOrgNo()));
			mm.addAttribute("orgColumn", StringUtils2.javaScriptEncode(temp.getOrgColumn()));
		} else if (temp != null && StringUtils.isNotBlank(temp.getOrgColumn()) && StringUtils.isNotBlank(temp.getAllowInputLower())) {
			mm.addAttribute("orgOwn", StringUtils2.javaScriptEncode(BioneSecurityUtils .getCurrentUserInfo().getOrgNo()));
		}
		mm.addAttribute("paramStr", StringUtils2.javaScriptEncode(EncodeUtils.urlDecode(paramStr)));
		mm.addAttribute("templeId", StringUtils2.javaScriptEncode(templateId));

		mm.addAttribute("caseId", "");
		mm.addAttribute("rowindex", "");
		return new ModelAndView(PATH + "table-input-query-edit", mm);
	}

	/**
	 * @????????????: ????????????
	 * @?????????: huzq1 
	 * @????????????: 2021/10/15 16:43
	 * @param templateId
	 * @param param
	 * @param paramStrAdd
	 * @param paramStrDel
	 * @param paramStrUdp
	 * @param logList
	 * @return
	 **/
	@ResponseBody
	@RequestMapping(value = "/updateQueryData", method = RequestMethod.POST)
	public Map<String, Object> updateQueryData(String templateId, String param, String paramStrAdd, String paramStrDel, String paramStrUdp, String logList){
		Map<String, Object> result = new HashMap<>();
		result.put("status", "fail");
		if(paramStrAdd == null) {
			paramStrAdd = "";
		}
		if(paramStrUdp == null) {
			paramStrUdp = "";
		}
		Connection conn = null;
		Statement stmt = null ;
		ResultSet rs = null;
		try {

			RptInputLstTempleInfo temp = templeBS.getEntityById(templateId);
			conn = this.dataSourceBS.getConnection(temp.getDsId());
			stmt = conn.createStatement();
			paramStrAdd = URLDecoder.decode(paramStrAdd,"UTF-8");
			paramStrUdp = URLDecoder.decode(paramStrUdp,"UTF-8");

			String[] paramCol = param.split(",");
			List<Map<String, Object>> dataList = tableBS.getDataList(paramStrUdp, paramCol);
			Map<String, Object> map = dataList.get(0);
			String datainputId = map.get("DATAINPUT_ID").toString();
			String sql = "select SYS_DATA_CASE,FLOW_NODE_ID from " + temp.getTableEnName() + " where DATAINPUT_ID = '" + datainputId + "'";
			rs = stmt.executeQuery(sql);
			String taskInstanceId = "";
			String flowNodeId = "";

			while(rs.next()){
				taskInstanceId = rs.getString(1);
				flowNodeId = rs.getString(2);
			}
			RptTskIns rptTskIns = deployTaskBS.getRptTskInsById(taskInstanceId);
			taskCaseController.saveTaskcaseTempleInfo(taskInstanceId,rptTskIns.getTaskId(),templateId,paramStrAdd,paramStrDel,paramStrUdp,param,taskInstanceId,logList);
			result.put("status", "success");
		} catch (Exception e){
			e.printStackTrace();
			result.put("msg", e.getMessage());
		} finally {
			this.dataSourceBS.releaseConnection(rs,stmt,conn);
		}
		return result;
	}

	@RequestMapping("/expInputData")
	public void expInputData(Pager pager, String templateId, HttpServletResponse response){
		tableBS.expInputData(pager, templateId,this.getRealPath(), response);
	}

	/**
	 * @????????????: ?????????????????????-??????
	 * @?????????: huzq1 
	 * @????????????: 2021/11/5 17:06
	  * @param uploader
	 * @param response
	 * @param templeId
	 * @return
	 **/
	@RequestMapping("/startUploadTableInputData")
	@ResponseBody
	public Map<String, Object> startUploadTableInputData(Uploader uploader, HttpServletResponse response, String templeId) {
		Map<String, Object> result = Maps.newHashMap();
		result.put("status", "success");
		try {
			File file = this.uploadFile(uploader, GlobalConstants4frame.FRS_RPT_FILL_IMPORT_PATH, false);
			RptInputLstTempleInfo temp = this.templeBS.getEntityById(templeId);
			this.dataFileBS.getUpdateData(file, templeId, getMaxOrderNo(temp));
		} catch (Exception e) {
			e.printStackTrace();
			result.put("msg", e.getMessage());
			result.put("status", "fail");
		}
		return result;
	}

	private Long getMaxOrderNo(RptInputLstTempleInfo temp) {
		Connection conn = null;
		Statement stmt = null;
		ResultSet rs = null;
		try {
			conn = this.dataSourceBS.getConnection(temp.getDsId());
			stmt = conn.createStatement();
			String sql = "select max(" + UdipConstants.TAB_ORDER_NO + ") from " + temp.getTableEnName();
			rs = stmt.executeQuery(sql);
			Long orderNo = 0l;
			while (rs.next()) {
				orderNo = rs.getLong(1);
				break;
			}
			return orderNo;
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			this.dataSourceBS.releaseConnection(rs, stmt, conn);
		}
		return 0l;
	}

	/**
	 * ?????????????????????
	 * @param templeId
	 * @return
	 */
	@RequestMapping("/uploadInputData")
	public ModelAndView uploadInputData(String templeId) {
		ModelMap mm = new ModelMap();
		mm.addAttribute("templeId", StringUtils2.javaScriptEncode(templeId));
		return new ModelAndView("/input/table/table-input-upload-templedata", mm);
	}

}
