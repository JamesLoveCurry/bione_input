package com.yusys.biapp.input.dict.utils;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.yusys.biapp.input.inputTable.entity.RptInputListTableFieldInf;
import com.yusys.bione.frame.base.common.GlobalConstants4frame;

import java.util.List;
import java.util.Map;

public class UdipConstants {
	public static final String System = "system";
	public static final String TEXT = "text";
	public static final String ID = "id";
	public static final String SUBJECT_INPUT = "【补录通知】";
	public final static String SMB_TYPE_FILE = "1";// 常用文件
	public final static String SMB_TYPE_TEMP = "2";// 补录模板
	public final static String SMB_TYPE_TEMPDATA = "3";// 补录模板数据
	public final static String SMB_TYPE_PROCLAMATION = "4";// 公告文件

	public static Map<String, String> SmbMap = Maps.newHashMap();
	public static Map<String, String> DirType = Maps.newHashMap();
	public static Map<String, String> roleType = Maps.newHashMap();
	public static final List<Map<String, String>> SmbTypeList = Lists.newArrayList();
	public static final List<Map<String, String>> roleTypeList = Lists.newArrayList();

	public static Map<String, String> TaskSequenceType = Maps.newHashMap();
	public static final List<Map<String, String>> taskSequenceList = Lists.newArrayList();// 任务频度

	public final static String DIR_ROOT = "0";// 根目录
	public final static String DIR_TYPE_TEMP = "1";// 目录类型，补录模板
	public final static String DIR_TYPE_LIB = "2";// 目录类型，数据字典
	public final static String DIR_TYPE_TASK = "3";// 目录类型，补录任务
	public final static String DIR_TYPE_FILE= "4";// 目录类型，文件管理

	public final static String TASK_CASE = "case";// 实例标识
	public final static String TASK_TASK = "task";// 任务标识
	public final static String CASE_TEMPLE = "temple";// 模板标识
	public final static String TASK_LIB = "lib";// 字典标识
	public final static String TASK_TABLE = "table";// 表标识
	public final static String SYS_FILE = "file";// 文件标识
	
	public final static String ROLE_TYPE_MANAGE = "1";// 管理员
	public final static String ROLE_TYPE_INPUT = "2";// 补录员
	public final static String ROLE_TYPE_AUTH = "3"; // 审核员
	public final static String ROLE_TYPE_VIEW = "4"; // 审核员

	public final static String TASK_SEQUENCE_1 = "1";// 任务频度日
	public final static String TASK_SEQUENCE_2 = "2";// 任务频度周
	public final static String TASK_SEQUENCE_3 = "3";// 任务频度旬
	public final static String TASK_SEQUENCE_4 = "4";// 任务频度月
	public final static String TASK_SEQUENCE_5 = "5";// 任务频度季
	public final static String TASK_SEQUENCE_6 = "6";// 任务频度年

	/** 补录规则1——数据值范围 */
	public final static String DATA_RULES_1 = "1";
	/** 补录规则2——正则表达式 */
	public final static String DATA_RULES_2 = "2";
	/** 补录规则3——表内横向校验 */
	public final static String DATA_RULES_3 = "3";
	/** 补录规则4——表内纵向校验 */
	public final static String DATA_RULES_4 = "4";
	/** 补录规则5——表间一致校验 */
	public final static String DATA_RULES_5 = "5";
	public static Map<String, String> dataRulesType = Maps.newHashMap();

	/** 计算函数:取子串 */
	public final static String DATA_RULES_SUBSTR = "substr";
	/** 计算函数:取长度 */
	public final static String DATA_RULES_LEN = "len";
	/** 计算函数:去空格 */
	public final static String DATA_RULES_TRIM = "trim";
	/** 计算函数:三元表达 */
	public final static String DATA_RULES_IF = "if";
	public static final List<Map<String, String>> dataRulesFuncList = Lists.newArrayList();// 计算函数

	/** 聚合函数:求和 */
	public final static String DATA_RULES_SUM = "sum";
	/** 聚合函数:统计 */
	public final static String DATA_RULES_COUNT = "count";
	/** 聚合函数:平均值 */
	public final static String DATA_RULES_AVG = "avg";
	/** 聚合函数:最大值 */
	public final static String DATA_RULES_MAX = "max";
	/** 聚合函数:最小值 */
	public final static String DATA_RULES_MIN = "min";
	/** 聚合函数:绝对值 */
	public final static String DATA_RULES_ABS = "abs";
	public static final List<Map<String, String>> dataRulesFuncList2 = Lists.newArrayList();// 计算函数

	/** 任务或者数据状态 */
	public final static String TASK_STATE_VALIDATE = "validate";// 补录数据校验状态
	public final static String TASK_STATE_DISPATCH = "dispatch";// 补录数据下发状态
	public final static String TASK_STATE_SAVE = "save";// 补录数据保存状态
	public final static String TASK_STATE_SUBMIT = "submit";// 补录数据提交状态
	public final static String TASK_STATE_SUCESS = "success";// 补录数据通过状态
	public final static String TASK_STATE_REFUSE = "refuse";// 补录数据回退状态
	public final static String TASK_STATE_REJECT = "reject";// 补录数据驳回状态

	public static final List<Map<String, String>> dataRulesList = Lists.newArrayList();// 补录规则

	public final static String TASK_STATE_USING = "1";// 任務状态启用
	public final static String TASK_STATE_STOP = "0";// 任務状态停用

	public final static String CASE_STATE_START = "start";	// 实例状态,开始
	public final static String CASE_STATE_INPUT = "0";	// 实例未提交状态
	public final static String CASE_STATE_SUBMIT = "1";	// 实例已提交状态
	public final static String CASE_STATE_END = "2";		// 实例状态, 结束(填报完成)

	public final static String ORG_AUTH_AUTHED = "auth";// 机构互申
	public final static String ORG_INPUT_INPUTED = "input";// 机构互补
	public static Map<String, String> OrgedType = Maps.newHashMap();
	public static final List<Map<String, String>> OrgedList = Lists.newArrayList();

	public static Map<String, String> stateType = Maps.newHashMap();
	public static final List<Map<String, String>> stateTypeList = Lists.newArrayList();// 启用、停用状态列表

	public final static String LIB_LIB_TYPE_CONSTANT = "1";// 数据字典，常量类型
	public final static String LIB_LIB_TYPE_DIS = "2";// 数据字典，数据库表类型
	
	public final static String LIB_SHOW_TYPE_TREE = "01";	// 数据树
	public final static String LIB_SHOW_TYPE_CMBX = "02";	// 下拉框

	public static List<Map<String, String>> colType = Lists.newArrayList();// 保存数据库表字段的类型参数
	/** 补录主键字段 **/
	public final static String DATAINPUT_ID = "DATAINPUT_ID";
	/** 操作日期字段 **/
	public final static String TAB_OPER_DATE = "SYS_OPER_DATE";
	/** 数据状态字段 **/
	public final static String TAB_DATA_STATE = "SYS_DATA_STATE";
	/** 补录人员字段 **/
	public final static String TAB_OPERATOR = "SYS_OPERATOR";
	/** 补录机构 **/
	public final static String TAB_OPER_ORG = "SYS_OPER_ORG";
	/** 数据日期字段 **/
	public final static String TAB_DATA_DATE = "SYS_DATA_DATE";
	/** 任务实例ID **/
	public final static String TAB_DATA_CASE = "SYS_DATA_CASE";
	/** 排序字段 **/
	public final static String TAB_ORDER_NO = "SYS_ORDER_NO";
	
	/** 保存指定系统字段，在添加补录表的主键索引时，需要显示的字段 */
	public static List<Map<String, String>> colPriType = Lists.newArrayList();

	public final static String TAB_PRIMARY = "primary";// 表字段主键约束
	public final static String TAB_UNIQUE = "unique";// 表字段唯一约束
	public final static String TAB_INDEX = "index";// 表字段索引约束
	public final static String TAB_CHAR = "CHAR";// 表字段固定字符类型
	public final static String TAB_DECIMAL = "DECIMAL";// 表字段固定字符类型
	public final static String TAB_NUMBER = "NUMBER";// 表字段数字类型
	public final static String TAB_NUMERIC = "NUMERIC";// 表字段数字类型
	public final static String TAB_VARCHAR = "VARCHAR";// 表字段不固定字符类型
	public final static String TAB_VARCHAR2 = "VARCHAR2";// 表字段不固定字符类型
	public final static String TAB_TIMESTAMP = "TIMESTAMP";// 表字段timestamp类型
	public final static String TAB_DATE = "DATE";// 表字段date类型
	public final static String TAB_INTEGER = "INTEGER";// 表字段integer类型
	public static Map<String, Map<String, RptInputListTableFieldInf>> colMaps = Maps.newLinkedHashMap();// 数据库表添加固定常量字段.注释
	public static Map<String, RptInputListTableFieldInf> colOracle = Maps.newLinkedHashMap();// 数据库表添加固定常量字段.注释
	public static Map<String, RptInputListTableFieldInf> colDB2 = Maps.newLinkedHashMap();// 数据库表添加固定常量字段.注释
	public static Map<String, RptInputListTableFieldInf> colMYSQL = Maps.newLinkedHashMap();// 数据库表添加固定常量字段.注释
	public static Map<String, RptInputListTableFieldInf> colPostgresql = Maps.newLinkedHashMap();// 数据库表添加固定常量字段.注释
	public static String TAB_BEFORE = "before";
	public static List<Map<String, String>> keyType = Lists.newArrayList();// 保存数据库表字段的约束类型
	
	/** 定义表时，保存表类型(1：补录表，2：数据字典，3：其他表) */
	public static List<Map<String, String>> tableType = Lists.newArrayList();
	public final static String TAB_INPUT = "1";// 表类型为补录表
	public final static String TAB_LIB = "2";// 表类型为数据字典
	public final static String TAB_OTHER = "3";// 表类型为其他表

	public final static String DATA_FLAG = "#DATA_FLAG#";// 补录数据操作类型，标识
	public final static String DATA_FLAG_SAVE = "0";// 补录数据操作类型，新增
	public final static String DATA_FLAG_UPATE = "1";// 补录数据操作类型，更新
	
	/**导入补录模板临时文件路径 */
	public static String TEMPLE_SYS_IMPORT_PATH ="/templeImport";

	public static Map<String, String> DataLoadMap = Maps.newHashMap();
	public static List<Map<String, String>> DataLoadList = Lists.newArrayList();// 预加载过滤值列表
	public final static String DATA_LOAD_DATA_DATE = "data_date";// 数据日期

	public final static String DATA_RANGE_DAY = "01";// 时间范围————上一日
	public final static String DATA_RANGE_MONTH = "02";// 时间范围————上一月
	public final static String DATA_RANGE_YEAR = "03";// 时间范围————上一年
	public final static String DATA_RANGE_THIS_DAY = "04";// 时间范围————本日
	public final static String DATA_RANGE_THIS_MONTH = "05";// 时间范围————本月
	public final static String DATA_RANGE_THIS_YEAR = "06";// 时间范围————本年
	public static Map<String, String> dataRangeType = Maps.newHashMap();
	public static final List<Map<String, String>> dataRangeList = Lists.newArrayList();// 时间范围下拉列表

	public final static String STATE_YES = "yes";// 状态肯定标识
	public final static String STATE_NO = "no";// 状态否定标识

//	public final static String ICON_FOLDER = GlobalConstants.APP_CONTEXT_PATH+"/images/udip/icons/folder.png";// 文件夹图标
//	public final static String ICON_TEMPLE = GlobalConstants.APP_CONTEXT_PATH+"/images/udip/icons/temp.png";//模板图标
//	public final static String ICON_RULE = GlobalConstants.APP_CONTEXT_PATH+"/images/udip/icons/rule.png";//规则图标
//	public final static String ICON_TASK = GlobalConstants.APP_CONTEXT_PATH+"/images/udip/icons/task.png";//任务图标
//	public final static String ICON_CASE = GlobalConstants.APP_CONTEXT_PATH+"/images/udip/icons/case.png";//任务实例
//	public final static String ICON_LIB = GlobalConstants.APP_CONTEXT_PATH+"/images/udip/icons/lib.png";//角色图标
//	public final static String ICON_USER = GlobalConstants.APP_CONTEXT_PATH+"/images/udip/icons/user.png";//用户图标
//	public final static String ICON_ORG = GlobalConstants.APP_CONTEXT_PATH+"/images/udip/icons/org.png";//机构图标
//	public final static String ICON_ROLE = GlobalConstants.APP_CONTEXT_PATH+"/images/udip/icons/role.png";//角色图标
	
	public final static String PARENT_ORG_CODE="-1";//用户机构管理表中根节点的上级机构代码默认为-1
	public final static String ORG_LEVEL="0";//用户机构管理表中根节点的机构等级代码默认为0
	
	/** excel表格颜色 */
	public final static String HSSF_COLOR_RED = "RED";
	public final static String HSSF_COLOR_BLUE = "BLUE";
	public final static String HSSF_COLOR_BLACK = "BLACK";
	public final static String HSSF_COLOR_BROWN = "BROWN";
	public final static String HSSF_COLOR_DARK_RED = "DARK_RED";
	public final static String HSSF_COLOR_DARK_GREEN = "DARK_GREEN";
	
	static {
		SmbMap.put(SMB_TYPE_FILE, "常用文件");
		SmbMap.put(SMB_TYPE_TEMP, "补录模板");
		SmbMap.put(SMB_TYPE_TEMPDATA, "数据文件");
		SmbMap.put(SMB_TYPE_PROCLAMATION, "公告附件");

		Map<String, String> m1 = Maps.newHashMap();
		m1.put(TEXT, "常用文件");
		m1.put(ID, SMB_TYPE_FILE);
		SmbTypeList.add(0, m1);
		Map<String, String> m2 = Maps.newHashMap();
		m2.put(TEXT, "补录模板");
		m2.put(ID, SMB_TYPE_TEMP);
		SmbTypeList.add(1, m2);
		Map<String, String> m3 = Maps.newHashMap();
		m3.put(TEXT, "数据文件");
		m3.put(ID, SMB_TYPE_TEMPDATA);
		SmbTypeList.add(2, m3);
		Map<String, String> m4 = Maps.newHashMap();
		m4.put(TEXT, "公告附件");
		m4.put(ID, SMB_TYPE_PROCLAMATION);
		SmbTypeList.add(3, m4);

		/****/
		DirType.put(DIR_TYPE_TEMP, "补录模板目录");//补录模板目录=补录规则目录
		DirType.put(DIR_TYPE_LIB, "补录字典目录");
		DirType.put(DIR_TYPE_TASK, "补录任务目录");
		DirType.put(DIR_TYPE_FILE, "文件管理目录");
	}

	static {
		roleType.put(ROLE_TYPE_MANAGE, "管理角色");
		roleType.put(ROLE_TYPE_INPUT, "补录角色");
		roleType.put(ROLE_TYPE_AUTH, "审核角色");
		roleType.put(ROLE_TYPE_VIEW, "查询角色");

		Map<String, String> m1 = Maps.newHashMap();
		m1.put(TEXT, roleType.get(ROLE_TYPE_MANAGE));
		m1.put(ID, ROLE_TYPE_MANAGE);
		roleTypeList.add(0, m1);

		Map<String, String> m2 = Maps.newHashMap();
		m2.put(TEXT, roleType.get(ROLE_TYPE_INPUT));
		m2.put(ID, ROLE_TYPE_INPUT);
		roleTypeList.add(1, m2);

		Map<String, String> m3 = Maps.newHashMap();
		m3.put(TEXT, roleType.get(ROLE_TYPE_AUTH));
		m3.put(ID, ROLE_TYPE_AUTH);

		roleTypeList.add(2, m3);

		Map<String, String> m4 = Maps.newHashMap();
		m4.put(TEXT, roleType.get(ROLE_TYPE_VIEW));
		m4.put(ID, ROLE_TYPE_VIEW);

		roleTypeList.add(3, m4);

		TaskSequenceType.put(TASK_SEQUENCE_1, "日");
		TaskSequenceType.put(TASK_SEQUENCE_2, "周");
		TaskSequenceType.put(TASK_SEQUENCE_3, "旬");
		TaskSequenceType.put(TASK_SEQUENCE_4, "月");
		TaskSequenceType.put(TASK_SEQUENCE_5, "季");
		TaskSequenceType.put(TASK_SEQUENCE_6, "年");
		Map<String, String> t1 = Maps.newHashMap();
		t1.put(TEXT, TaskSequenceType.get(TASK_SEQUENCE_1));
		t1.put(ID, TASK_SEQUENCE_1);
		taskSequenceList.add(0, t1);

		Map<String, String> t2 = Maps.newHashMap();
		t2.put(TEXT, TaskSequenceType.get(TASK_SEQUENCE_2));
		t2.put(ID, TASK_SEQUENCE_2);
		taskSequenceList.add(1, t2);

		Map<String, String> t3 = Maps.newHashMap();
		t3.put(TEXT, TaskSequenceType.get(TASK_SEQUENCE_3));
		t3.put(ID, TASK_SEQUENCE_3);
		taskSequenceList.add(2, t3);

		Map<String, String> t4 = Maps.newHashMap();
		t4.put(TEXT, TaskSequenceType.get(TASK_SEQUENCE_4));
		t4.put(ID, TASK_SEQUENCE_4);
		taskSequenceList.add(3, t4);

		Map<String, String> t5 = Maps.newHashMap();
		t5.put(TEXT, TaskSequenceType.get(TASK_SEQUENCE_5));
		t5.put(ID, TASK_SEQUENCE_5);
		taskSequenceList.add(4, t5);

		Map<String, String> t6 = Maps.newHashMap();
		t6.put(TEXT, TaskSequenceType.get(TASK_SEQUENCE_6));
		t6.put(ID, TASK_SEQUENCE_6);
		taskSequenceList.add(5, t6);
		
		colMaps.put("ORACLE", colOracle);
		colMaps.put("DB2", colDB2);
		colMaps.put("MYSQL", colMYSQL);
		colMaps.put("POSTGRESQL", colPostgresql);

		RptInputListTableFieldInf udipTableColInfo = new RptInputListTableFieldInf();

		// 字段类型 begin
		// oracle begin
//		udipTableColInfo = new RptInputListTableFieldInf();
//		udipTableColInfo.setFieldEnName(DATAINPUT_ID);
//		udipTableColInfo.setFieldType(TAB_VARCHAR2);
//		udipTableColInfo.setFieldCnName("主键字段");
//		udipTableColInfo.setFieldLength("32");
//		udipTableColInfo.setAllowNull("true");	// 非空
//		colOracle.put(DATAINPUT_ID, udipTableColInfo);

		
		udipTableColInfo = new RptInputListTableFieldInf();
		udipTableColInfo.setFieldEnName(TAB_DATA_DATE);
		udipTableColInfo.setFieldType(TAB_VARCHAR2);
		udipTableColInfo.setFieldCnName("数据日期");
		udipTableColInfo.setFieldLength("20");
		udipTableColInfo.setAllowNull("false");	// 非空
		colOracle.put(TAB_DATA_DATE, udipTableColInfo);

		udipTableColInfo = new RptInputListTableFieldInf();
		udipTableColInfo.setFieldEnName(TAB_DATA_CASE);
		udipTableColInfo.setFieldType(TAB_VARCHAR2);
		udipTableColInfo.setFieldCnName("任务实例ID");
		udipTableColInfo.setFieldLength("32");
		udipTableColInfo.setAllowNull("false");	// 非空
		colOracle.put(TAB_DATA_CASE, udipTableColInfo);
		
		udipTableColInfo = new RptInputListTableFieldInf();
		udipTableColInfo.setFieldEnName(TAB_OPERATOR);
		udipTableColInfo.setFieldType(TAB_VARCHAR2);
		udipTableColInfo.setFieldCnName("补录人员");
		udipTableColInfo.setFieldLength("256");
		colOracle.put(TAB_OPERATOR, udipTableColInfo);
		
		udipTableColInfo = new RptInputListTableFieldInf();
		udipTableColInfo.setFieldEnName(TAB_OPER_ORG);
		udipTableColInfo.setFieldType(TAB_VARCHAR2);
		udipTableColInfo.setFieldCnName("补录机构");
		udipTableColInfo.setFieldLength("32");
		udipTableColInfo.setAllowNull("false");	// 非空
		colOracle.put(TAB_OPER_ORG, udipTableColInfo);
		
		udipTableColInfo = new RptInputListTableFieldInf();
		udipTableColInfo.setFieldEnName(TAB_OPER_DATE);
		udipTableColInfo.setFieldType(TAB_VARCHAR2);
		udipTableColInfo.setFieldCnName("操作日期");
		udipTableColInfo.setFieldLength("20");
		colOracle.put(TAB_OPER_DATE, udipTableColInfo);

		udipTableColInfo = new RptInputListTableFieldInf();
		udipTableColInfo.setFieldEnName(TAB_DATA_STATE);
		udipTableColInfo.setFieldType(TAB_VARCHAR2);
		udipTableColInfo.setFieldCnName("数据状态:save(已保存),submit(已提交),reject(驳回),sucess(审核通过),refuse(已回退)");
		udipTableColInfo.setFieldLength("10");
		colOracle.put(TAB_DATA_STATE, udipTableColInfo);
		
		udipTableColInfo = new RptInputListTableFieldInf();
		udipTableColInfo.setFieldEnName(TAB_ORDER_NO);
		udipTableColInfo.setFieldType(TAB_NUMBER);
		udipTableColInfo.setFieldCnName("序号");
		udipTableColInfo.setFieldLength("19");
		udipTableColInfo.setDecimalLength("0");
		colOracle.put(TAB_ORDER_NO, udipTableColInfo);
		// oracle end
		
		// DB2 begin
//		udipTableColInfo = new RptInputListTableFieldInf();
//		udipTableColInfo.setFieldEnName(DATAINPUT_ID);
//		udipTableColInfo.setFieldType(TAB_VARCHAR);
//		udipTableColInfo.setFieldCnName("主键字段");
//		udipTableColInfo.setFieldLength("32");
//		udipTableColInfo.setAllowNull("true");	// 非空
//		colDB2.put(DATAINPUT_ID, udipTableColInfo);
		
		udipTableColInfo = new RptInputListTableFieldInf();
		udipTableColInfo.setFieldEnName(TAB_DATA_DATE);
		udipTableColInfo.setFieldType(TAB_VARCHAR);
		udipTableColInfo.setFieldCnName("数据日期");
		udipTableColInfo.setFieldLength("20");
		udipTableColInfo.setAllowNull("false");	// 非空
		colDB2.put(TAB_DATA_DATE, udipTableColInfo);

		udipTableColInfo = new RptInputListTableFieldInf();
		udipTableColInfo.setFieldEnName(TAB_DATA_CASE);
		udipTableColInfo.setFieldType(TAB_VARCHAR);
		udipTableColInfo.setFieldCnName("任务实例ID");
		udipTableColInfo.setFieldLength("32");
		udipTableColInfo.setAllowNull("false");	// 非空
		colDB2.put(TAB_DATA_CASE, udipTableColInfo);
		
		udipTableColInfo = new RptInputListTableFieldInf();
		udipTableColInfo.setFieldEnName(TAB_OPERATOR);
		udipTableColInfo.setFieldType(TAB_VARCHAR);
		udipTableColInfo.setFieldCnName("补录人员");
		udipTableColInfo.setFieldLength("256");
		colDB2.put(TAB_OPERATOR, udipTableColInfo);
		
		udipTableColInfo = new RptInputListTableFieldInf();
		udipTableColInfo.setFieldEnName(TAB_OPER_ORG);
		udipTableColInfo.setFieldType(TAB_VARCHAR);
		udipTableColInfo.setFieldCnName("补录机构");
		udipTableColInfo.setFieldLength("32");
		udipTableColInfo.setAllowNull("false");	// 非空
		colDB2.put(TAB_OPER_ORG, udipTableColInfo);
		
		udipTableColInfo = new RptInputListTableFieldInf();
		udipTableColInfo.setFieldEnName(TAB_OPER_DATE);
		udipTableColInfo.setFieldType(TAB_VARCHAR);
		udipTableColInfo.setFieldCnName("操作日期");
		udipTableColInfo.setFieldLength("20");
		colDB2.put(TAB_OPER_DATE, udipTableColInfo);

		udipTableColInfo = new RptInputListTableFieldInf();
		udipTableColInfo.setFieldEnName(TAB_DATA_STATE);
		udipTableColInfo.setFieldType(TAB_VARCHAR);
		udipTableColInfo.setFieldCnName("数据状态:save(已保存),submit(已提交),reject(驳回),sucess(审核通过),refuse(已回退)");
		udipTableColInfo.setFieldLength("10");
		colDB2.put(TAB_DATA_STATE, udipTableColInfo);
		
		udipTableColInfo = new RptInputListTableFieldInf();
		udipTableColInfo.setFieldEnName(TAB_ORDER_NO);
		udipTableColInfo.setFieldType(TAB_NUMERIC);
		udipTableColInfo.setFieldCnName("序号");
		udipTableColInfo.setFieldLength("19");
		udipTableColInfo.setDecimalLength("0");
		udipTableColInfo.setAllowNull("false");	// 非空
		colDB2.put(TAB_ORDER_NO, udipTableColInfo);
		// db2 end
		
		//mysql start
		udipTableColInfo = new RptInputListTableFieldInf();
		udipTableColInfo.setFieldEnName(TAB_DATA_DATE);
		udipTableColInfo.setFieldType(TAB_VARCHAR);
		udipTableColInfo.setFieldCnName("数据日期");
		udipTableColInfo.setFieldLength("20");
		udipTableColInfo.setAllowNull("false");	// 非空
		colMYSQL.put(TAB_DATA_DATE, udipTableColInfo);

		udipTableColInfo = new RptInputListTableFieldInf();
		udipTableColInfo.setFieldEnName(TAB_DATA_CASE);
		udipTableColInfo.setFieldType(TAB_VARCHAR);
		udipTableColInfo.setFieldCnName("任务实例ID");
		udipTableColInfo.setFieldLength("32");
		udipTableColInfo.setAllowNull("false");	// 非空
		colMYSQL.put(TAB_DATA_CASE, udipTableColInfo);
		
		udipTableColInfo = new RptInputListTableFieldInf();
		udipTableColInfo.setFieldEnName(TAB_OPERATOR);
		udipTableColInfo.setFieldType(TAB_VARCHAR);
		udipTableColInfo.setFieldCnName("补录人员");
		udipTableColInfo.setFieldLength("256");
		colMYSQL.put(TAB_OPERATOR, udipTableColInfo);
		
		udipTableColInfo = new RptInputListTableFieldInf();
		udipTableColInfo.setFieldEnName(TAB_OPER_ORG);
		udipTableColInfo.setFieldType(TAB_VARCHAR);
		udipTableColInfo.setFieldCnName("补录机构");
		udipTableColInfo.setFieldLength("32");
		udipTableColInfo.setAllowNull("false");	// 非空
		colMYSQL.put(TAB_OPER_ORG, udipTableColInfo);
		
		udipTableColInfo = new RptInputListTableFieldInf();
		udipTableColInfo.setFieldEnName(TAB_OPER_DATE);
		udipTableColInfo.setFieldType(TAB_VARCHAR);
		udipTableColInfo.setFieldCnName("操作日期");
		udipTableColInfo.setFieldLength("20");
		colMYSQL.put(TAB_OPER_DATE, udipTableColInfo);

		udipTableColInfo = new RptInputListTableFieldInf();
		udipTableColInfo.setFieldEnName(TAB_DATA_STATE);
		udipTableColInfo.setFieldType(TAB_VARCHAR);
		udipTableColInfo.setFieldCnName("数据状态:save(已保存),submit(已提交),reject(驳回),sucess(审核通过),refuse(已回退)");
		udipTableColInfo.setFieldLength("10");
		colMYSQL.put(TAB_DATA_STATE, udipTableColInfo);
		
		udipTableColInfo = new RptInputListTableFieldInf();
		udipTableColInfo.setFieldEnName(TAB_ORDER_NO);
		udipTableColInfo.setFieldType(TAB_DECIMAL);
		udipTableColInfo.setFieldCnName("序号");
		udipTableColInfo.setFieldLength("19");
		udipTableColInfo.setDecimalLength("0");
		colMYSQL.put(TAB_ORDER_NO, udipTableColInfo);
		//mysql end
		// 字段类型 end

		//gaussdb start
		udipTableColInfo = new RptInputListTableFieldInf();
		udipTableColInfo.setFieldEnName(TAB_DATA_DATE);
		udipTableColInfo.setFieldType(TAB_VARCHAR);
		udipTableColInfo.setFieldCnName("数据日期");
		udipTableColInfo.setFieldLength("20");
		udipTableColInfo.setAllowNull("false");	// 非空
		colPostgresql.put(TAB_DATA_DATE, udipTableColInfo);

		udipTableColInfo = new RptInputListTableFieldInf();
		udipTableColInfo.setFieldEnName(TAB_DATA_CASE);
		udipTableColInfo.setFieldType(TAB_VARCHAR);
		udipTableColInfo.setFieldCnName("任务实例ID");
		udipTableColInfo.setFieldLength("32");
		udipTableColInfo.setAllowNull("false");	// 非空
		colPostgresql.put(TAB_DATA_CASE, udipTableColInfo);

		udipTableColInfo = new RptInputListTableFieldInf();
		udipTableColInfo.setFieldEnName(TAB_OPERATOR);
		udipTableColInfo.setFieldType(TAB_VARCHAR);
		udipTableColInfo.setFieldCnName("补录人员");
		udipTableColInfo.setFieldLength("256");
		colPostgresql.put(TAB_OPERATOR, udipTableColInfo);

		udipTableColInfo = new RptInputListTableFieldInf();
		udipTableColInfo.setFieldEnName(TAB_OPER_ORG);
		udipTableColInfo.setFieldType(TAB_VARCHAR);
		udipTableColInfo.setFieldCnName("补录机构");
		udipTableColInfo.setFieldLength("32");
		udipTableColInfo.setAllowNull("false");	// 非空
		colPostgresql.put(TAB_OPER_ORG, udipTableColInfo);

		udipTableColInfo = new RptInputListTableFieldInf();
		udipTableColInfo.setFieldEnName(TAB_OPER_DATE);
		udipTableColInfo.setFieldType(TAB_VARCHAR);
		udipTableColInfo.setFieldCnName("操作日期");
		udipTableColInfo.setFieldLength("20");
		colPostgresql.put(TAB_OPER_DATE, udipTableColInfo);

		udipTableColInfo = new RptInputListTableFieldInf();
		udipTableColInfo.setFieldEnName(TAB_DATA_STATE);
		udipTableColInfo.setFieldType(TAB_VARCHAR);
		udipTableColInfo.setFieldCnName("数据状态:save(已保存),submit(已提交),reject(驳回),sucess(审核通过),refuse(已回退)");
		udipTableColInfo.setFieldLength("10");
		colPostgresql.put(TAB_DATA_STATE, udipTableColInfo);

		udipTableColInfo = new RptInputListTableFieldInf();
		udipTableColInfo.setFieldEnName(TAB_ORDER_NO);
		udipTableColInfo.setFieldType(TAB_NUMERIC);
		udipTableColInfo.setFieldCnName("序号");
		udipTableColInfo.setFieldLength("19");
		udipTableColInfo.setDecimalLength("0");
		colPostgresql.put(TAB_ORDER_NO, udipTableColInfo);
		//gaussdb end
		// 字段类型 end
		
		Map<String, String> colMapss = Maps.newHashMap();
		colMapss.put(ID, TAB_DATA_CASE);
		colMapss.put(TEXT, "多批次支持");
		colPriType.add(colMapss);

		dataRulesType.put(DATA_RULES_1, "数据值范围");
		dataRulesType.put(DATA_RULES_2, "正则表达式");
		dataRulesType.put(DATA_RULES_3, "表内横向校验");
		dataRulesType.put(DATA_RULES_4, "表内纵向校验");
		dataRulesType.put(DATA_RULES_5, "表间一致校验");
		Map<String, String> dataRules1 = Maps.newHashMap();
		dataRules1.put(TEXT, dataRulesType.get(DATA_RULES_1));
		dataRules1.put(ID, DATA_RULES_1);
		dataRulesList.add(0, dataRules1);

		Map<String, String> dataRules2 = Maps.newHashMap();
		dataRules2.put(TEXT, dataRulesType.get(DATA_RULES_2));
		dataRules2.put(ID, DATA_RULES_2);
		dataRulesList.add(1, dataRules2);
		
		/* 20190521 三种校验操作方式极其复杂
		Map<String, String> dataRules3 = Maps.newHashMap();
		dataRules3.put(TEXT, dataRulesType.get(DATA_RULES_3));
		dataRules3.put(ID, DATA_RULES_3);
		dataRulesList.add(2, dataRules3);

		Map<String, String> dataRules4 = Maps.newHashMap();
		dataRules4.put(TEXT, dataRulesType.get(DATA_RULES_4));
		dataRules4.put(ID, DATA_RULES_4);
		dataRulesList.add(3, dataRules4);

		Map<String, String> dataRules5 = Maps.newHashMap();
		dataRules5.put(TEXT, dataRulesType.get(DATA_RULES_5));
		dataRules5.put(ID, DATA_RULES_5);
		dataRulesList.add(4, dataRules5);
		*/
		
		Map<String, String> dataRulesFunc = Maps.newHashMap();
		dataRulesFunc.put(ID, DATA_RULES_SUBSTR);
		dataRulesFunc.put(TEXT, "取子串（SUBSTR）");
		dataRulesFuncList.add(dataRulesFunc);

		dataRulesFunc = Maps.newHashMap();
		dataRulesFunc.put(ID, DATA_RULES_LEN);
		dataRulesFunc.put(TEXT, "取长度（LEN）");
		dataRulesFuncList.add(dataRulesFunc);

		dataRulesFunc = Maps.newHashMap();
		dataRulesFunc.put(ID, DATA_RULES_TRIM);
		dataRulesFunc.put(TEXT, "去空格（TRIM）");
		dataRulesFuncList.add(dataRulesFunc);

		dataRulesFunc = Maps.newHashMap();
		dataRulesFunc.put(ID, DATA_RULES_IF);
		dataRulesFunc.put(TEXT, "三元表达（IF）");
		dataRulesFuncList.add(dataRulesFunc);

		Map<String, String> dataRulesFunc2 = Maps.newHashMap();
		dataRulesFunc2.put(ID, DATA_RULES_SUM);
		dataRulesFunc2.put(TEXT, "求和（SUM）");
		dataRulesFuncList2.add(dataRulesFunc2);

		dataRulesFunc2 = Maps.newHashMap();
		dataRulesFunc2.put(ID, DATA_RULES_COUNT);
		dataRulesFunc2.put(TEXT, "统计（COUNT）");
		dataRulesFuncList2.add(dataRulesFunc2);

		dataRulesFunc2 = Maps.newHashMap();
		dataRulesFunc2.put(ID, DATA_RULES_AVG);
		dataRulesFunc2.put(TEXT, "平均值（AVG）");
		dataRulesFuncList2.add(dataRulesFunc2);

		dataRulesFunc2 = Maps.newHashMap();
		dataRulesFunc2.put(ID, DATA_RULES_MAX);
		dataRulesFunc2.put(TEXT, "最大值（MAX）");
		dataRulesFuncList2.add(dataRulesFunc2);

		dataRulesFunc2 = Maps.newHashMap();
		dataRulesFunc2.put(ID, DATA_RULES_MIN);
		dataRulesFunc2.put(TEXT, "最小值（MIN）");
		dataRulesFuncList2.add(dataRulesFunc2);

		dataRulesFunc2 = Maps.newHashMap();
		dataRulesFunc2.put(ID, DATA_RULES_ABS);
		dataRulesFunc2.put(TEXT, "绝对值（ABS）");
		dataRulesFuncList2.add(dataRulesFunc2);

		stateType.put(TASK_STATE_USING, "启用");
		stateType.put(TASK_STATE_STOP, "停用");
		Map<String, String> stateType1 = Maps.newHashMap();
		stateType1.put(TEXT, stateType.get(TASK_STATE_USING));
		stateType1.put(ID, TASK_STATE_USING);
		stateTypeList.add(0, stateType1);

		Map<String, String> stateType2 = Maps.newHashMap();
		stateType2.put(TEXT, dataRulesType.get(TASK_STATE_STOP));
		stateType2.put(ID, TASK_STATE_STOP);
		stateTypeList.add(1, stateType2);

		Map<String, String> colTypeMap = Maps.newHashMap();
		colTypeMap.put(ID, TAB_CHAR);
		colTypeMap.put(TEXT, TAB_CHAR);
		colType.add(colTypeMap);
		colTypeMap = Maps.newHashMap();
		colTypeMap.put(ID, TAB_NUMBER);
		colTypeMap.put(TEXT, TAB_NUMBER);
		colType.add(colTypeMap);
		colTypeMap = Maps.newHashMap();
		colTypeMap.put(ID, TAB_VARCHAR2);
		colTypeMap.put(TEXT, TAB_VARCHAR2);
		colType.add(colTypeMap);
		colTypeMap = Maps.newHashMap();
		colTypeMap.put(ID, TAB_TIMESTAMP);
		colTypeMap.put(TEXT, TAB_TIMESTAMP);
		colType.add(colTypeMap);
		colTypeMap = Maps.newHashMap();
		colTypeMap.put(ID, TAB_DATE);
		colTypeMap.put(TEXT, TAB_DATE);
		colType.add(colTypeMap);
		colTypeMap = Maps.newHashMap();
		colTypeMap.put(ID, TAB_INTEGER);
		colTypeMap.put(TEXT, TAB_INTEGER);
		colType.add(colTypeMap);

		Map<String, String> keyTypeMap = Maps.newHashMap();
		keyTypeMap.put(ID, TAB_PRIMARY);
		keyTypeMap.put(TEXT, "主键(" + TAB_PRIMARY + ")");
		keyTypeMap.put(TAB_BEFORE, "PK_");
		keyType.add(keyTypeMap);
		keyTypeMap = Maps.newHashMap();
		keyTypeMap.put(ID, TAB_UNIQUE);
		keyTypeMap.put(TEXT, "唯一性(" + TAB_UNIQUE + ")");
		keyTypeMap.put(TAB_BEFORE, "UQ_");
		keyType.add(keyTypeMap);
		keyTypeMap = Maps.newHashMap();
		keyTypeMap.put(ID, TAB_INDEX);
		keyTypeMap.put(TEXT, "索引(" + TAB_INDEX + ")");
		keyTypeMap.put(TAB_BEFORE, "INDEX_");
		keyType.add(keyTypeMap);

		Map<String, String> tableMap = Maps.newHashMap();
		tableMap.put(ID, TAB_INPUT);
		tableMap.put(TEXT, "补录表");
		tableType.add(tableMap);
		tableMap = Maps.newHashMap();
		tableMap.put(ID, TAB_LIB);
		tableMap.put(TEXT, "数据字典");
		tableType.add(tableMap);
		tableMap = Maps.newHashMap();
		tableMap.put(ID, TAB_OTHER);
		tableMap.put(TEXT, "其他表");
		tableType.add(tableMap);
	}

	static {
		OrgedType.put(ORG_AUTH_AUTHED, "设置互审");
		OrgedType.put(ORG_INPUT_INPUTED, "设置互补");

		Map<String, String> m1 = Maps.newHashMap();
		m1.put(TEXT, OrgedType.get(ORG_AUTH_AUTHED));
		m1.put(ID, ORG_AUTH_AUTHED);
		OrgedList.add(0, m1);

		Map<String, String> m2 = Maps.newHashMap();
		m2.put(TEXT, OrgedType.get(ORG_INPUT_INPUTED));
		m2.put(ID, ORG_INPUT_INPUTED);
		// OrgedList.add(1, m2);

		DataLoadMap.put(DATA_LOAD_DATA_DATE, "数据日期");
		Map<String, String> data1 = Maps.newHashMap();
		data1.put(TEXT, DataLoadMap.get(DATA_LOAD_DATA_DATE));
		data1.put(ID, DATA_LOAD_DATA_DATE);
		DataLoadList.add(0, data1);
	}

	static {
		dataRangeType.put(DATA_RANGE_DAY, "上一日");
		dataRangeType.put(DATA_RANGE_MONTH, "上一月");
		dataRangeType.put(DATA_RANGE_YEAR, "上一年");
		dataRangeType.put(DATA_RANGE_THIS_DAY, "本日");
		dataRangeType.put(DATA_RANGE_THIS_MONTH, "本月");
		dataRangeType.put(DATA_RANGE_THIS_YEAR, "本年");
		
		Map<String, String> dr1 = Maps.newHashMap();
		dr1.put(TEXT, dataRangeType.get(DATA_RANGE_DAY));
		dr1.put(ID, DATA_RANGE_DAY);
		dataRangeList.add(0, dr1);
		Map<String, String> dr2 = Maps.newHashMap();
		dr2.put(TEXT, dataRangeType.get(DATA_RANGE_MONTH));
		dr2.put(ID, DATA_RANGE_MONTH);
		dataRangeList.add(1, dr2);
		Map<String, String> dr3 = Maps.newHashMap();
		dr3.put(TEXT, dataRangeType.get(DATA_RANGE_YEAR));
		dr3.put(ID, DATA_RANGE_YEAR);
		dataRangeList.add(2, dr3);
		Map<String, String> dr4 = Maps.newHashMap();
		dr4.put(TEXT, dataRangeType.get(DATA_RANGE_THIS_DAY));
		dr4.put(ID, DATA_RANGE_THIS_DAY);
		dataRangeList.add(3, dr4);
		Map<String, String> dr5 = Maps.newHashMap();
		dr5.put(TEXT, dataRangeType.get(DATA_RANGE_THIS_MONTH));
		dr5.put(ID, DATA_RANGE_THIS_MONTH);
		dataRangeList.add(4, dr5);
		Map<String, String> dr6 = Maps.newHashMap();
		dr6.put(TEXT, dataRangeType.get(DATA_RANGE_THIS_YEAR));
		dr6.put(ID, DATA_RANGE_THIS_YEAR);
		dataRangeList.add(5, dr6);
	}
	
	// ICONS
	private final static String ICON_PATH = GlobalConstants4frame.APP_CONTEXT_PATH
			+ "/images/classics/udip";
	public final static String ICON_FOLDER = ICON_PATH + "/folder.png";// 文件夹图标
	public final static String ICON_TEMPLE = ICON_PATH + "/temp.png";// 模板图标
	public final static String ICON_RULE = ICON_PATH + "/rule.png";// 规则图标
	public final static String ICON_TASK = ICON_PATH + "/task.png";// 任务图标
	public final static String ICON_CASE = ICON_PATH + "/case.png";// 任务实例
	public final static String ICON_LIB = ICON_PATH + "/lib.png";// 角色图标
	public final static String ICON_USER = ICON_PATH + "/user.png";// 用户图标
	public final static String ICON_ORG = ICON_PATH + "/org.png";// 机构图标
	public final static String ICON_ROLE = ICON_PATH + "/role.png";// 角色图标

	
}
