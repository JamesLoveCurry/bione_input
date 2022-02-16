/**
 * 
 */
package com.yusys.bione.plugin.base.common;

import com.yusys.bione.frame.base.common.GlobalConstants4frame;

import java.io.File;
import java.util.HashMap;
import java.util.Map;


/**
 * <pre>
 * Title:统一报表框架部分的通用静态常量
 * Description: 统一报表框架部分的通用静态常量
 * </pre>
 * 
 * @author caiqy caiqy@yuchengtech.com
 * @version 1.00.00
 * 
 *          <pre>
 * 修改记录
 *    修改后版本:     修改人：  修改日期:     修改内容:
 * </pre>
 */
public class GlobalConstants4plugin {

	// 状态标识
	public static final String STS_ON = "1"; // 启用/完成
	public static final String STS_OFF = "0"; // 停用/未完成

	// 状态标识
	public static final String STS_ON_Y = "Y"; // 启用/完成
	public static final String STS_OFF_N = "N"; // 停用/未完成
	// 设置扩展机构类型参数
	public static final String TREE_PARAM_TYPE = GlobalConstants4frame.TREE_PARAM_TYPE;
	// 报表升级概况参数
	public static final String RPT_UPGRADE_TYPE = "rptUpgradeType";
	// 监管机构类型参数
	public static final String RPT_ORG_CALSS = "rptOrgClass";
	// 通用布尔
	public static final String COMMON_BOOLEAN_YES = "Y"; // 是
	public static final String COMMON_BOOLEAN_NO = "N"; // 否
	// 生成报文 导出大数据csv excel等jdbc fetchsize配置
	public static final Integer FETCH_SIZE = 10000;
	/**
	 * 通用 冒号字符 :
	 */
	public static final String COMMON_STR_COLON = ":";
	/**
	 * 通用 逗号字符 ,
	 */
	public static final String COMMON_STR_COMMA = ",";
	/**
	 * 通用 加号字符 +
	 */
	public static final String COMMON_STR_PLUS_SIGN = "+";
	/**
	 * 通用 减号字符 -
	 */
	public static final String COMMON_STR_MINUS_SIGN = "-";
	/**
	 * 通用 左方括号 字符 [
	 */
	public static final String COMMON_STR_BRACKETS_LEFT = "[";
	/**
	 * 通用 右方括号 字符 ]
	 */
	public static final String COMMON_STR_BRACKETS_RIGHT = "]";
	/**
	 * 通用 左圆括号 字符 (
	 */
	public static final String COMMON_STR_PARENTHESES_LEFT = "(";
	/**
	 * 通用 右圆括号 字符 )
	 */
	public static final String COMMON_STR_PARENTHESES_RIGHT = ")";
	/**
	 * 通用 左大括号 字符 {
	 */
	public static final String COMMON_STR_BRACES_LEFT = "{";
	/**
	 * 通用 右大括号 字符 }
	 */
	public static final String COMMON_STR_BRACES_RIGHT = "}";
	
	/**
	 * 通用 字符 n
	 */
	public static final String COMMON_STR_N = "N";
	// 报表类型
	public static final String RPT_TYPE_OUTER = "01"; // 外部报表
	public static final String RPT_TYPE_DESIGN = "02"; // 报表设计器
	public static final String RPT_TYPE_DETAIL = "03"; // 报表模板

	// 报表扩展类型
	public static final String RPT_EXT_TYPE_BANK = "01"; // 外部报表
	public static final String RPT_EXT_TYPE_FRS = "02"; // 平台报表
	public static final String RPT_EXT_TYPE_EAST = "04"; // EAST报表

	// 监管业务类型(机构类型)
	public static final String RPT_FRS_BUSI_PUBLIC = "00"; // 公共
	public static final String RPT_FRS_BUSI_BANK = "01"; // 平台
	public static final String RPT_FRS_BUSI_1104 = "02"; // 银监会1104
	public static final String RPT_FRS_BUSI_PBC = "03"; // 人行大集中
	public static final String RPT_FRS_BUSI_EAST = "04"; // EAST明细

	// 报表模板类型
	public static final String RPT_TMP_TYPE_DETAIL = "01"; // 明细类
	public static final String RPT_TMP_TYPE_CELL = "02"; // 单元格类
	public static final String RPT_TMP_TYPE_COM = "03";// 综合类
	public static final String RPT_TMP_TYPE_IDXTAB_V = "04";// 指标列表类（纵）
	public static final String RPT_TMP_TYPE_IDXTAB_H = "05";// 指标列表类（横）
	public static final String RPT_TMP_TYPE_CROSS = "06";// 交叉列表
	
	
//
	// 报表资源标识 常量 -add by zhongqh
	public static final String AUTH_ROLE = "AUTH_OBJ_ROLE"; // 报表填报权限资源标识
	public static final String RPT_FILL = "AUTH_RES_RPT_FILL"; // 报表填报权限资源标识
	public static final String RPT_VIEW = "AUTH_RES_RPT_VIEW"; // 报表查看权限资源标识
	public static final String DETAIL_VIEW = "AUTH_RES_DETAIL";//明细模板查看权限资源标识
//
	// 平台内置变量
	public static String RPT_INNER_SYS_VAR_ORG = "org";
	public static String RPT_INNER_SYS_VAR_DATE = "date";
	public static String RPT_INNER_SYS_VAR_CURRENCY = "currency";
	public static String RPT_INNER_SYS_VAR_UNIT = "tempUnit";
	// 系统变量信息
	// 变量类型
	public static String BIONE_SYS_VAR_TYPE_CONSTANT = "01"; // 常量
	public static String BIONE_SYS_VAR_TYPE_SQL = "02"; // SQL语句
	public static String BIONE_SYS_VAR_TYPE_SYSTEM = "03"; // 平台型
	// 数据集来源扩展模式
	public static final String EXT_MODE_INSERT = "01";// 插入
	public static final String EXT_MODE_OVER = "02";// 覆盖
	// 数据集来源扩展方向
	public static final String EXT_DIRECTION_V = "01";// 纵向扩展
	public static final String EXT_DIRECTION_H = "02";// 横向扩展
	public static final String EXT_DIRECTION_HV = "03";// 横纵向扩展

	// 数据集字段类型
	public static final String COL_TYPE_NORMAL = "00";
	public static final String COL_TYPE_MEASURE = "01";
	public static final String COL_TYPE_DIM = "02";


	public static final String DESIGN_EXPORT_PATH = "/export/design";
	public static final String DESIGN_EXPORT_ZIP_PATH = "/export/zip";

	
	/**
	 * 报表目录树 -根节点图标 路径
	 */
	public static String TREE_ICON_ROOT = "/" + GlobalConstants4frame.ICON_URL + "/house.png";

	/**
	 * 报表目录树 -目录图标 路径
	 */
	public static String REPORT_CATALOG_ICON = "/" + GlobalConstants4frame.ICON_URL
			+ "/folder_page.png";  

	/**
	 * 报表目录树 -报表图标 路径
	 */
	public static String REPORT_ICON = "/" + GlobalConstants4frame.ICON_URL + "/report.png";

	/**
	 * 报表目录树 -文档图标 路径
	 */
	public static String REPORT_DOCUMENT_ICON = "/" + GlobalConstants4frame.ICON_URL + "/page.png";

	/**
	 * 报表目录树 -标签对象 路径
	 */
	public static String REPORT_LABELOBJ_ICON = "/" + GlobalConstants4frame.ICON_URL + "/tag_red.png";

	/**
	 * 参数模版树 参数模版目录 图标路径
	 */
	public static String PARAMTMP_CATALOG_ICON = "/" + GlobalConstants4frame.ICON_URL
			+ "/folder_page.png";

	/**
	 * 参数模版树 参数模版 图标路径
	 */
	public static String PARAMTMP_ICON = "/" + GlobalConstants4frame.ICON_URL + "/html.png";

	/**
	 * 读取引擎用
	 */
	public static String REPORT_ENGINE_READ_REPORT = "import" + File.separator
			+ "report" + File.separator + "reportdata";

	/**
	 * 我的查询, 实例类型
	 */
	public static String INST_TYPE_QUERYS = "01"; // 报表批量查询
	public static String INST_TYPE_RPTIDX = "02"; // 报表指标查询
	public static String INST_TYPE_IDX = "03"; // 指标数据查询
	public static String INST_TYPE_RPT = "04"; // 报表查询

	/**
	 * 报表类型-外部集成报表
	 */
	public static String REPORT_TYPE_INTEGRATED = "01";
	/**
	 * 报表类型-平台设计器报表
	 */
	public static String REPORT_TYPE_PLATFORM = "02";

	/**
	 * 平台变量定义类型
	 */
	public static String SYS_DEF_TYPE_INNER = "01"; // 内置
	public static String SYS_DEF_TYPE_CUSTOM = "02"; // 自定义
	/**
	 * 维度类别来源类型
	 */
	public static String DIM_TYPE_SRC_INNER = "01"; // 内置
	public static String DIM_TYPE_SRC_CUSTOM = "02"; // 自定义

	/** 指标类型 **/
	public static final String ROOT_INDEX = "01"; // 根指标
	public static final String COMPOSITE_INDEX = "02"; // 组合指标
	public static final String DERIVE_INDEX = "03"; // 派生指标
	public static final String GENERIC_INDEX = "04"; // 泛化指标
	public static final String SUM_ACCOUNT_INDEX = "05"; // 总账指标
	// 报表指标类型 现在应该没用了
	public static final String REPORT_INDEX = "06"; // 报表指标
	public static final String EMPTY_INDEX = "06"; // 空指标

	public static final String ADD_RECORD_INDEX = "07"; // 补录指标
	public static final String EXCEL_INDEX = "09"; // excel公式指标

	/** 数据类型 **/
	public static final String DATA_TYPE_NULL = "00"; // 原格式
	public static final String DATA_TYPE_MONEY = "01"; // 金额
	public static final String DATA_TYPE_VALUE = "03"; // 数值
	public static final String DATA_TYPE_PERCENT = "02"; // 比例

	/** 汇总方式 **/
	public static final String DATA_SUM_NO = "00"; // 不汇总
	public static final String DATA_SUM_SUM = "01"; // 求和
	public static final String DATA_SUM_AVG = "02"; // 均值
	public static final String DATA_SUM_MAX = "03"; // 最大
	public static final String DATA_SUM_MIN = "04"; // 最小

	/** 生成周期 **/
	public static final String CALC_CYCLE_DAY = "01"; // 日
	public static final String CALC_CYCLE_MONTH = "02"; // 月
	public static final String CALC_CYCLE_SEASON = "03"; // 季
	public static final String CALC_CYCLE_YEAR = "04"; // 年
	public static final String CALC_CYCLE_WEEK = "10"; // 周
	public static final String CALC_CYCLE_ONE_THIRD_MONTH = "11"; // 旬
	public static final String CALC_CYCLE_HALF_YEAR = "12"; // 半年

	/** 数据单位 **/
	public static final String DATA_UNIT_YUAN = "01"; // 元/个
	public static final String DATA_UNIT_HUNDRED = "02"; // 百
	public static final String DATA_UNIT_THOUSAND = "03"; // 千
	public static final String DATA_UNIT_TEN_THOUSAND = "04"; // 万
	public static final String DATA_UNIT_HUNDRED_MILLION = "05"; // 亿
	public static final String DATA_UNIT_PERCENT = "06"; // % 百分号
	public static final String DATA_UNIT_EMPTY = "00";// 无单位
	/** 明细错误表名 **/
	public static final String DETAIL_FULL_NAME="F"; // 拼接全表名 如Z_BFD201DBWXX
	public static final String DETAIL_ORG_NAME="O";// 拼接日期和机构 如Z210101_BFD201DBWXX_701100
	public static final String DETAIL_LAST_NAME="N";// 拼日期和最后的部分 如Z210101_DBWXX
	public static final String DETAIL_FULL_NAME_DATE="D";// 拼日期和全表名 如Z210101_BFD201DBWXX
	/** 指标状态 **/
	public static final String INDEX_STS_START = "Y";// 启用
	public static final String INDEX_STS_STOP = "N";// 停用

	/** 指标是否汇总 **/
	public static final String IS_SUM_TRUE = "Y";// 启用
	public static final String IS_SUM_FALSE = "N";// 停用

	/** 指标是否发布 **/
	public static final String IS_PUBLISH_TRUE = "Y";// 启用
	public static final String IS_PUBLISH_FALSE = "N";// 停用

	/** 指标是否具有信息权限 **/
	public static final String HAS_INFORIGHT_TRUE = "Y";// 具有
	public static final String HAS_INFORIGHT_FALSE = "N";// 不具有

	/** 维度类型的类型 **/
	public static final String DIM_TYPE_BUSI = "00";// 业务维度
	public static final String DIM_TYPE_DATE = "01";// 日期维度
	public static final String DIM_TYPE_ORG = "02";// 机构维度
	public static final String DIM_TYPE_CURRENCY = "03";// 币种维度
	public static final String DIM_TYPE_LINE = "04";// 条线维度
	public static final String DIM_TYPE_INDEXNO = "04"; // 指标维度

	public static final String DIM_TYPE_DATE_NAME = "DATE";
	public static final String DIM_TYPE_ORG_NAME = "ORG";
	public static final String DIM_TYPE_CURRENCY_NAME = "CURRENCY";
	public static final String DIM_TYPE_INDEXNO_NAME = "INDEXNO";

	public static final String COMMON_DIM_TYPE = DIM_TYPE_DATE_NAME + ","
			+ DIM_TYPE_ORG_NAME + "," + DIM_TYPE_CURRENCY_NAME + ","
			+ DIM_TYPE_INDEXNO_NAME; // 通用指标维度

	public static final String COMMON_RPTIDX_DIM_TYPE = DIM_TYPE_DATE_NAME + ","
			+ DIM_TYPE_ORG_NAME + "," + DIM_TYPE_INDEXNO_NAME; // 报表指标通用维度
	
	/** 指标统计类型 **/
	public static final String IDX_STAT_TYPE_COMMON = "01"; // 时点类
	public static final String IDX_STAT_TYPE_TIMEPOINT = "02"; // 时点类
	public static final String IDX_STAT_TYPE_STAGE = "03"; // 时期类

	/* 数据模型字段类型 */
	public static final String DATASET_COL_TYPE_COMMON = "00";// 一般字段
	public static final String DATASET_COL_TYPE_MEASURE = "01";// 度量字段
	public static final String DATASET_COL_TYPE_DIM = "02";// 维度字段

	/* 维度结构类型 */
	public static final String DIM_TYPE_STRUCT_LIST = "01";// 列表
	public static final String DIM_TYPE_STRUCT_TREE = "02";// 树形

	/* 指标公式类型 */
	public static final String FORMULA_TYPE_FILTER = "01";// 过滤公式
	public static final String FORMULA_TYPE_CALC = "02";// 计算公式
	public static final String FORMULA_TYPE_CUSTOM_SQL = "03";// 自定义sql语句
	// 面板启动/停用
	public static final String PANEL_STS_START = "1";// 启用
	public static final String PANEL_STS_STOP = "0";// 停用
	/**
	 * 监管机构汇总类型
	 */
	public static String FRS_ORG_SUN_TYPE_INPUT = "01";// 基础行(填报行)
	public static String FRS_ORG_SUN_TYPE_SUM = "02";// 汇总行
	/**
	 * 报表计算状态01：等待运行02：运行中03：成功04：失败
	 */
	public static String RPT_ENGINE_REPORT_STS_WAIT = "01";// 运行中
	public static String RPT_ENGINE_REPORT_STS_RUN = "02";// 运行中
	public static String RPT_ENGINE_REPORT_STS_SUCCESS = "03";// 成功
	public static String RPT_ENGINE_REPORT_STS_FAILED = "04";// 失败

	/**
	 * 指标过滤类型
	 */
	public static String RPT_IDX_FILTER_MODE_IN = "01";// 包含（in）
	public static String RPT_IDX_FILTER_MODE_NOT_IN = "02";// 不包含（not in）
	public static String RPT_IDX_FILTER_MODE_EQUAL = "03";// 等于（=）
	public static String RPT_IDX_FILTER_MODE_NOT_EQUAL = "04";// 不等于（<>）
	public static String RPT_IDX_FILTER_MODE_GT = "05";// 大于（>）
	public static String RPT_IDX_FILTER_MODE_GT_EQUAL = "06";// 大于等于（>=）
	public static String RPT_IDX_FILTER_MODE_LT = "07";// 小于（<）
	public static String RPT_IDX_FILTER_MODE_LT_EQUAL = "08";// 小于等于（<=）
//
	public static String RPT_CELL_SOURCE_COMMON = "01";// 一般单元格
	public static String RPT_CELL_SOURCE_MODULE = "02";// 数据集字段单元格
	public static String RPT_CELL_SOURCE_IDX = "03";// 指标单元格
	public static String RPT_CELL_SOURCE_FORMULA = "04";// excel公式单元格
	public static String RPT_CELL_SOURCE_RPTCALC = "05";// 表间计算单元格
	public static String RPT_CELL_SOURCE_STATICTEXT = "06";// 文本常量单元格
	public static String RPT_CELL_SOURCE_IDXTAB = "07";// 指标列表单元格
	public static String RPT_CELL_SOURCE_DIMTAB = "08";// 维度列表单元格
	public static String RPT_CELL_SOURCE_COMMON_EDIT = "09";// 可编辑单元格
	/**
	 * 数据模型类型
	 */
	public static String SET_TYPE_DETAIL = "00";// 明细模型
	public static String SET_TYPE_MUTI_DIM = "01";// 多维模型
	public static String SET_TYPE_GENERIC = "02";// 泛化模型
	public static String SET_TYPE_SUM = "03";// 总账模型
	public static String SET_TYPE_REPORT = "04";// 报表模型
//
	/**
	 * 度量类型
	 */
	public static String MEASURE_TYPE_STEADY = "01";// 固定度量
	public static String MEASURE_TYPE_EXTEND = "02";// 扩展度量
	public static String MEASURE_TYPE_SUM = "03";// 总账度量
//		// 上年同期
//
	// 单元格显示格式
	public static String CELL_DISPLAY_FORMAT_ORG = "01";// 金额
	public static String CELL_DISPLAY_FORMAT_PER = "02";// 百分比
	public static String CELL_DISPLAY_FORMAT_COUNT = "03";// 数值
	public static String CELL_DISPLAY_FORMAT_TEXT = "04";// 文本
	public static String CELL_DISPLAY_FORMAT_PER2 = "08";// 百分比(无%)
	
	public static String CELL_DISPLAY_FORMAT_YMD = "01";// 年月日
	public static String CELL_DISPLAY_FORMAT_YM = "02";// 年月
	public static String CELL_DISPLAY_FORMAT_M = "03";// 月
	public static String CELL_DISPLAY_FORMAT_D = "04";// 日

	/**
	 * 引擎指标运行任务状态
	 */
	public static String ENGINE_RUN_STATUS_WAITING = "01"; // 等待运行
	public static String ENGINE_RUN_STATUS_RUNNING = "02"; // 运行中
	public static String ENGINE_RUN_STATUS_OK = "03"; // 成功
	public static String ENGINE_RUN_STATUS_FAIL = "04"; // 失败
	public static String ENGINE_RUN_STATUS_STOP = "07"; // 手动停止
	public static String ENGINE_RUN_STATUS_TIME_OUT = "08"; // 超时停止

	/**
	 * 
	 */
	public static String RPT_ENGINE_TASK_TYPE_RPTID = "RptId"; // 报表类型
	public static String RPT_ENGINE_TASK_TYPE_RPTTMP = "RptTmpId"; // 模板类型
	public static String RPT_ENGINE_TASK_TYPE_IDX = "IndexNo"; // 指标类型
	public static String RPT_ENGINE_TASK_TYPE_RPTIDX = "RptIndexNo"; // 报表指标类型
	public static String RPT_ENGINE_TASK_TYPE_RPTIG ="RptIdGroup"; //任务组报表类型
	public static String RPT_ENGINE_TASK_TYPE_IDXNG="IndexNoGroup"; //任务组指标类型

	/**
	 * 引擎启动类型标识
	 */
	public static String ENGINE_RUN_TYPE_CALC = "01";// 计算或其它
	public static String ENGINE_RUN_TYPE_INFLU = "02";// 影响

	// 校验公式类型
	public static String VALID_FUNC_TYPE_RPT = "03";// 报表函数
	public static String VALID_FUNC_TYPE_MATH = "02";// 数学函数
	public static String VALID_FUNC_TYPE_LOGIC = "01";// 逻辑函数

	// 自定义指标标识和维度标识代码前缀
	public static String COSTOMED_INDEX_ROOT_PREFIX = "BI";// 根指标
	public static String COSTOMED_INDEX_DERIVE_PREFIX = "DI";// 派生指标
	public static String COSTOMED_INDEX_COMPOSITE_PREFIX = "II";// 组合指标
	// public static String COSTOMED_INDEX_SUM_PREFIX ="";//总账指标
	public static String COSTOMED_INDEX_GENERIC_PREFIX = "GI";// 泛化指标
	public static String COSTOMED_DIM_PREFIX = "DM";// 维度

	// 模板导出路径
	public static String EXPORT_DIM_TEMPLATE_PATH = "template" + File.separator
			+ "01-dimInfo.xls";
	public static String EXPORT_MODEL_TEMPLATE_PATH = "template"
			+ File.separator + "02-modelInfo.xls";
	public static String EXPORT_INDEX_TEMPLATE_PATH = "template"
			+ File.separator + "03-idxInfo.xls";
	public static String EXPORT_RPT_TEMPLATE_PATH = "template" + File.separator
			+ "04-rptInfo.xls";
	public static String EXPORT_RPTREL_TEMPLATE_PATH = "template"
			+ File.separator + "05-rptsetInfo.xls";
	public static String EXPORT_ORG_TEMPLATE_PATH = "template" + File.separator
			+ "06-orgInfo.xls";
	public static String EXPORT_BIONEORG_TEMPLATE_PATH = "template"
			+ File.separator + "07-bioneorgInfo.xls";
	public static String EXPORT_USER_TEMPLATE_PATH = "template"
			+ File.separator + "08-userInfo.xls";
	public static String EXPORT_SIMILAR_TEMPLATE_PATH = "template"
			+ File.separator + "09-similarInfo.xls";
	public static String EXPORT_COMP_TEMPLATE_PATH = "template"
			+ File.separator + "10-compInfo.xls";
	public static String EXPORT_IDXPLANVAL_TEMPLATE_PATH = "template"
			+ File.separator + "11-idxplanvalInfo.xls";
	public static String EXPORT_LABEL_TEMPLATE_PATH = "template"
			+ File.separator + "12-labelInfo.xls";
	public static String EXPORT_INTER_BANK_PATH = "template"
			+ File.separator + "13-interBankInfo.xls";
	public static String EXPORT_RPT_BANK_PATH = "template"
			+ File.separator + "14-rptBankInfo.xls";
	public static String EXPORT_IDXCHECK_TEMPLATE_PATH = "template"
			+ File.separator + "15-idxcheckInfo.xls";
	public static String EXPORT_TRANS_MODEL_TEMPLATE_PATH = "template"
			+ File.separator + "16-transModelInfo.xls";
	public static String EXPORT_GENERALIZE_TEMPLATE_PATH = "template"
			+ File.separator + "17-GeneralizeInfo.xls";
	public static String EXPORT_VALIDLOGIC_TEMPLATE_PATH = "template"
			+ File.separator + "18-ValidLogicInfo.xls";
	public static String EXPORT_VALIDWARN_TEMPLATE_PATH = "template"
			+ File.separator + "18-ValidWarnInfo.xls";
	public static String EXPORT_VALIDORGMERGE_TEMPLATE_PATH = "template"
			+ File.separator + "18-ValidOrgmergeInfo.xls";
	public static String EXPORT_DETAIL_VALIDLOGIC_TEMPLATE_PATH = "template"
			+ File.separator + "18-DetailValidLogicInfo.xls";
	public static String EXPORT_WARN_FAIL_TEMPLATE_PATH = "template"
			+ File.separator + "18-WarnFailInfo.xls";
	public static String EXPORT_EASTCRRULE_TEMPLATE_PATH = "template"
			+ File.separator + "19-EastCrRuleInfo.xls";

	public static String EXPORT_EASTCRLOG_TEMPLATE_PATH = "template"
			+ File.separator + "20-EastCrLog.xls";
	
	public static String EXPORT_CRRS_TEMPLATE_PATH = "template"
			+ File.separator;

	public static String EXPORT_PBCIJCFG_TEMPLATE_PATH = "template"
			+ File.separator + "21-PbcIjCfg.xls";
	public static String EXPORT_PISA_IDX_TEMPLATE_PATH = "template"
			+ File.separator + "22-PisaIdxCfg.xls";
	public static String EXPORT_PISA_INOUT_TEMPLATE_PATH = "template"
			+ File.separator + "23-PisaInOutCfg.xls";
	public static String EXPORT_CRRS_CROSS_BANK_CONSISTENCY = "template"
			+ File.separator + "24-CrossBankConsistencyInfo.xls";
	public static String MONEY_UNIT_PARAMS = "moneyUnit";
	public static String EXPORT_EAST_ALL_SUBJECT = "template"
			+ File.separator + "27-EastAllSubject.xlsx";
	public static String EXPORT_PSCSCRRULE_TEMPLATE_PATH = "template"
			+ File.separator + "30-PscsCrRuleInfo.xls";
	public static String EXPORT_SAFECRRULE_TEMPLATE_PATH = "template"
			+ File.separator + "31-SafeCrRuleInfo.xls";
	public static String EXPORT_RPT_STD_CODE_INFO_TEMPLATE_PATH = "template"
			+ File.separator + "32-RptStdCodeInfo.xls";
	public static String EXPORT_SAFE_REPORT_ANALYSIS_TEMPLATE_PATH = "template"
			+ File.separator + "33-SafeReportAnalysis.xlsx";
	public static String EXPORT_SAFE_HANDLE_ANALYSIS_TEMPLATE_PATH = "template"
			+ File.separator + "34-SafeHandleAnalysis.xlsx";
	public static String EXPORT_BFDCRRULE_TEMPLATE_PATH = "template"
			+ File.separator + "35-BfdCrRuleInfo.xls";
	public static String EXPORT_BFD_RPT_STD_CODE_INFO_TEMPLATE_PATH = "template"
			+ File.separator + "36-BfdRptStdCodeInfo.xls";
	public static String EXPORT_REPORT_APPROVAL_INFO_TEMPLATE_PATH = "template"
			+ File.separator + "37-ReportApprovalInfo.xlsx";
	public static String EXPORT_REPORT_REVIEW_INFO_TEMPLATE_PATH = "template"
			+ File.separator + "38-ReportReviewInfo.xlsx";
	public static String EXPORT_IMAS_CRRULE_TEMPLATE_PATH = "template"
			+ File.separator + "39-ImasCrRuleInfo.xls";
	public static String EXPORT_IMAS_RPT_STD_CODE_INFO_TEMPLATE_PATH = "template"
			+ File.separator + "40-ImasRptStdCodeInfo.xls";
	public static String EXPORT_EAST_RPT_STD_CODE_INFO_TEMPLATE_PATH = "template"
			+ File.separator + "41-EastRptStdCodeInfo.xls";
	
	// 客户风险导出模板路径--数据采集
	public static String EXPORT_CRRS_SINGLE_INFO_TEMPLATE_PATH = "template"
			+ File.separator + "21-SingleCorporationInfo.xls"; // 单一法人
	public static String EXPORT_CRRS_GROUP_INFO_TEMPLATE_PATH = "template"
			+ File.separator + "22-GroupInfo.xls"; // 集团
	public static String EXPORT_CRRS_SUPPLY_INFO_TEMPLATE_PATH = "template"
			+ File.separator + "23-SupplyChainFinanceInfo.xls"; // 供应链
	public static String EXPORT_CRRS_INTERBANK_INFO_TEMPLATE_PATH = "template"
			+ File.separator + "24-InterbankInfo.xls"; // 同业
	public static String EXPORT_CRRS_PERSIONL_INFO_TEMPLATE_PATH = "template"
			+ File.separator + "25-PersionalInfo.xls"; // 个人
	

	// 客户风险导出模板路径--报表查看
	public static String EXPORT_CRRS_FXYJ01_TEMPLATE_PATH = "template"
			+ File.separator + "51_fxyj01.xls"; // 对公及同业客户统计表
	public static String EXPORT_CRRS_FXYJ02_TEMPLATE_PATH = "template"
			+ File.separator + "52_fxyj02.xls"; // 集团客户、供应链融资统计表
	public static String EXPORT_CRRS_FXYJ03_TEMPLATE_PATH = "template"
			+ File.separator + "53_fxyj03.xls"; // 单一法人客户统计表
	public static String EXPORT_CRRS_FXYJ04_TEMPLATE_PATH = "template"
			+ File.separator + "54_fxyj04.xls"; // 对公客户担保情况统计表
	public static String EXPORT_CRRS_FXYJ05_TEMPLATE_PATH = "template"
			+ File.separator + "55_fxyj05.xls"; // 个人贷款违约情况统计表
	public static String EXPORT_CRRS_FXYJ06_TEMPLATE_PATH = "template"
			+ File.separator + "56_fxyj06.xls"; // 个人违约贷款担保情况统计表
	public static String EXPORT_TABLE_TEMPLATE_PATH = "template"
			+ File.separator + "57-tableInfo.xls";
	
	// 数字单位
	public static String NUMBER_UNIT_PARAMS = "numberUnit";

	// 指标查询时间跨度
	public static String INDEX_QUERY_SPAN = "indexQuerySpan";
	
	// 金融基础，指标类，生成报文模板
	public static String BFD_REPORT_FILE_201_TEMPLATE = "template" + File.separator + "bfd-201.xls"; // 20201_20201金融机构（法人）基本情况统计表
	public static String BFD_REPORT_FILE_202_TEMPLATE = "template" + File.separator + "bfd-202.xls"; // 20202_20202金融机构（法人）资产负债及风险统计表
	public static String BFD_REPORT_FILE_203_TEMPLATE = "template" + File.separator + "bfd-203.xls"; // 20203_20203金融机构（法人）利润及资本统计表
	
	// 授权资源标识
	public static String IDX_RES_NO = "AUTH_RES_IDX"; // 指标管理
	public static String RPTVIEW_RES_NO = "AUTH_RES_RPT_VIEW"; // 报表查询
	public static String RPTFILL_RES_NO = "AUTH_RES_RPT_FILL"; // 报表填报
	public static String DETAIL_RES_NO = "AUTH_RES_DETAIL"; // 明细模板
	public static String RPTORG_RES_NO = "AUTH_RES_RPTORG"; // 机构权限
	public static String MODEL_RES_NO = "AUTH_RES_MODEL"; // 明细模型
	public static String IDXTHEME_RES_NO = "AUTH_RES_IDX_THEME"; // 主题库权限
	public static String RPTAUDT_RES_NO = "AUTH_RES_RPT_AUDT"; //报表审核
	public static String RPTCHECK_RES_NO = "AUTH_RES_RPT_CHECK"; //报表复核
	public static final String LOCAL_AUTH_OBJ_DEF_ID_ROLE = "LOCAL_AUTH_OBJ_ROLE";// 本地角色
	public static String BFD_RES_NO = "AUTH_RES_BFD_REPORT";// 金融基础报表
	public static String EAST_RES_NO = "AUTH_RES_EAST_REPORT";// 金融基础报表
	public static String IMAS_RES_NO = "AUTH_RES_IMAS_REPORT";
	// 报表模板-批量过滤类型
	public static String RPT_FILTER_MODEL_ROW = "01"; // 行过滤
	public static String RPT_FILTER_MODEL_COL = "02"; // 列过滤

	// 游离机构根节点ID
	public static final String TREE_FREE_ROOT_NO = " ";

	// 指标定义来源
	public static String INDEX_DEF_SRC_LIB = "01";// 01 全行级指标(指标库)
	public static String INDEX_DEF_SRC_ORG = "02";// 02 机构级指标
	public static String INDEX_DEF_SRC_USER = "03";// 03 用户级指标

	// 报表定义来源
	public static String RPT_DEF_SRC_LIB = "01";// 01 全行级报表
	public static String RPT_DEF_SRC_ORG = "02";// 02 机构级报表
	public static String RPT_DEF_SRC_USER = "03";// 03 用户级报表

	// 模板定义来源
	public static String DETAIL_DEF_SRC_LIB = "01";// 01 全行级指标(指标库)
	public static String DETAIL_DEF_SRC_ORG = "02";// 02 机构级指标
	public static String DETAIL_DEF_SRC_USER = "03";// 03 用户级指标

	public final static String FORMULA_TYPE_LEFT = "L";
	public final static String FORMULA_TYPE_RIGHT = "R";

	public final static String IDXTAB_DETAILS = "01";
	public final static String IDXTAB_SUBTOTAL = "02";
	public final static String IDXTAB_TOTAL = "03";

	public final static String SUM_TYPE_SUB = "01";
	public final static String SUM_TYPE_TOTAL = "02";

	public final static String FORMULA_RANGE_EXT = "01";//范围扩展(纵)
	public final static String FORMULA_ADD_EXT = "02"; //自增扩展(纵)
	public final static String FORMULA_RANGE_EXT_H = "03";//范围扩展(横)
	public final static String FORMULA_ADD_EXT_H = "04"; //自增扩展(横)
	
	public final static String APPLY_STS_COMMIT = "1"; // 待审批
	public final static String APPLY_STS_COLLATE = "2";// 审批通过
	public final static String APPLY_STS_REJECT = "3";// 驳回

	public final static String OBJ_SHARE_RPT = "1";// 分享报表
	public final static String OBJ_SHARE_IDX = "2";// 分析指标

	public final static String SHARE_STS_ON = "1";// 分享
	public final static String SHARE_STS_OFF = "2";// 取消分享
	public final static String SHARE_STS_PUBLIC = "3";// 公共报表
	public final static String SHARE_STS_DELETE = "4";// 已删除

	public static String RESULT_SUCCESS = "0000"; // 成功
	public static String RESULT_DATABASE_ERROR = "1001"; // 数据库错误
	public static String RESULT_TEST_NOPASS = "1002";
	public static String RESULT_INDEX_ERROR = "2002"; // 指标配置错误
	public static String RESULT_DIM_ERROR = "2003"; // 维度配置错误
	public static String RESULT_CACHE_ERROR = "2004"; // cache出错
	public static String RESULT_COMPUTE_ERROR = "2005"; // 计算出错 —机构汇总、过滤等
	public static String RESULT_CHECK_ERROR = "2006"; // 检核配置错误
	public static String RESULT_ORG_ERROR = "2007"; // 机构配置错误
	public static String RESULT_NO_TASK_ERROR = "2008"; // 没有找到任务
	public static String RESULT_NODE_ERROR = "3001"; // 节点配置错误
	public static String RESULT_JSON_ERROR = "4001"; // JSON错误 报文错误
	public static String RESULT_FTP_ERROR = "5001"; // 文件传输错误
	public static String RESULT_KILL_ERROR = "5002"; // 杀死任务失败
	public static String RESULT_REBOOT_ERROR = "5003"; // 引擎重启导致任务失败
	public static String RESULT_SYS_KILL = "5004"; // 手动杀死
	public static String RESULT_NETWORK_ERROR = "6001"; // 网络错误
	public static String RESULT_PART_SUCCESS = "0001"; // 部分成功
	public static String RESULT_UNKNOWN_ERROR = "9001"; // 未知错误
	public static String RESULT_UNREALIZED_ERROR = "9002"; // 未实现的类
	public static String RESULT_SYS_JAR_ERROR = "9003"; // 缺少jar
	public static String RESULT_SYS_UNDEF_INTERFACE = "9004"; // 未定义的计算器
	public static String RESULT_SYS_TIMEOUT = "9005"; // 执行超时
	public static String RESULT_PARAMETER_ERROR = "9006"; // 参数错误
	public static String RESULT_UNDEF_METHOD_ERROR = "9007"; // 未实现的方法

	public static final String LOGIC_DATA_TYPE_TEXT = "01";// 文本old--text
	public static final String LOGIC_DATA_TYPE_NUMBER = "02";// 数字old--number
	public static final String LOGIC_DATA_TYPE_DATE = "03";// 日期old--date
	public static final String LOGIC_DATA_TYPE_TIME = "04";// 时间old--time
	public static final String LOGIC_DATA_TYPE_OBJECT = "05";// 对象old--object

	public static final String SUM_COL_TYPE = "01";// 汇总字段
	public static final String VAL_COL_TYPE = "02";// 求值字段
	
	public static final String DRAW_IDX = "01";// 指标翻牌对象
	public static final String DRAW_RPT = "02";// 报表翻牌对象

	public static final String BACK_RPT_CTL_NODE_ID = "backRptCtlNodeId";// 打回报表目录ID

	public static final String MSG_TYPE_SHARE = "01";// 打回分享
	public static final String MSG_TYPE_PUBLISH = "02";// 打回发布
	
	//数据集树图标
	public static final String DATA_TREE_NODE_ICON_ROOT="/images/classics/icons/house.png";//根结点
	
	public static final String RPT_REPORT_RESULT="RPT_REPORT_RESULT";//报表事实表
	public static final String RPT_IDX_RESULT="RPT_IDX_RESULT";//指标事实表
	
	public static final String RULE_TYPE_COMMON = "01";
	public static final String RULE_TYPE_PERIOD = "02";
	public static final String RULE_TYPE_TIME = "03";
	
	/** 指标分析通用参数 **/
	//管驾公式
	/** 时点公式 **/
	public static final String POINT_FA = "01";
	/** 累计公式 **/
	public static final String CUMULATIVE_FA = "02";
	/** 日均公式 **/
	public static final String AVERAGE_FA = "03";
	/** 组合公式 **/
	public static final String GROUP_FA = "04";
	/** 增幅公式 **/
	public static final String INCREASE_FA = "05";
	/** 特殊公式 **/
	public static final String SPECIAL_FA = "06";
	
	//图表类型
	/** 指标概要图 **/
	public static final String OUTLINE = "01";
	/** 机构信息图 **/
	public static final String ORG = "02";
	/** 趋势分析图 **/
	public static final String TREND = "03";
	/** 结构解析图 **/
	public static final String RELATIONSHIP = "04";
	/** 关系解析图 **/
	public static final String STRUCTURE= "05";
	
	
	//图表展现类型
	/** 双轴柱折图 **/
	public static final String BIAXIAL_BAR = "01";
	/** 饼图 **/
	public static final String PIE = "02";
	/** 杜邦图 **/
	public static final String DUPONT = "03";
	/** 单轴柱折图 **/
	public static final String BAR = "04";
	/** 矩形树图 **/
	public static final String TREE_MAP = "05";
	/** 指标分析使用参数 **/
	public static final String INDEX_ANALYSIS_DATA_KEY = "index_analysis_data_key";
	/** 全部指标 **/
	public static final String INDEX_ANALYSIS_ALL_IDX = "index_analysis_all_idx";
	/** 全部指标目录 **/
	public static final String INDEX_ANALYSIS_ALL_IDXLOG = "index_analysis_all_idxlog";
	/** 全部度量 **/
	public static final String INDEX_ANALYSIS_ALL_MEASURE = "index_analysis_all_measure";
	/** 报文缓存 **/
	public static final String INDEX_ANALYSIS_MESSAGE = "index_analysis_message";
	/** 报文key缓存 **/
	public static final String INDEX_ANALYSIS_MESSAGE_KEY = "index_analysis_message_key";
	
	/**
	 * mstr管理员服务参数配置
	 */
	public static final String MSTR_ROOT_NODE_ID = "mstr.rootId";
	public static final String MSTR_SERVER_NAME = "mstr.serverName";
	public static final String MSTR_T_VIEW_MEDIA = "mstr.tViewMedia";
	public static final String MSTR_WEB_SERVER_TYPE = "mstr.serverType";
	public static final String MSTR_WEB_SERVER_TYPE_ASP = "1";
	public static final String MSTR_WEB_SERVER_TYPE_JSP = "2";

	public static final String MSTR_HISTORY_PAGESIZE = "mstr.historyPageSize";

	public static final String MSTR_OBJECT_TYPE_FOLDER = "folder";
	public static final String MSTR_OBJECT_TYPE_REPORT = "report";
	public static final String MSTR_OBJECT_TYPE_DOCUMENT = "document";

	public static final String MSTR_FOLDER_PUBLIC = "共享报表";
	public static final String MSTR_FOLDER_PRIVATE = "我的报表";

	/**
	 * 报表缓存方式
	 */
	public static String REPORT_SAVE_IMPL="fileReportListSaver";//当前为文件缓存

	/**
	 * 报表属性参数
	 */
	public static String REPORT_INFO = "reportInfo";// 报表路径
	public static String REPORT_SERVER_HOST = "reptServerHost";// 报表服务器地址
	public static String REPORT_SERVER_PORT = "reptServerPort";// 报表服务器端口
	public static String REPORT_SERVER_URL = "reptServerUrl";// 报表服务器url
	public static String REPORT_NAMESPACE = "reportNamespace";// 命名空间
	public static String REPORT_USER_NAME = "reportUsername";// 报表服务器用户名
	public static String REPORT_PASSWORD = "reportPassword";// 报表服务器密码
	public static String REPORT_PROJECT_NAME = "reportProjectName";// 报表服务器工程
	public static String REPORT_WEBPORT = "reportWebPort";// 页面端口
	public static String REPORT_WEB_TYPE = "reportWebType";// web服务器类型

	/**
	 * 报表认证类型
	 */
	public static final String RPT_SERVER_AUTH_TYPE_ANONYMOUS = "0";// 匿名认证
	public static final String RPT_SERVER_AUTH_TYPE_MAPPING = "1";// 映射认证

	public static final String COGNOS_GATEWAY = "cognos.gateway";
	/**
	 * 登录报表服务器的类型标识属性名
	 */
	public static final String REPORT_SERVER_LOGIN_TYPE = "logintype";
	
	/**
	 * cognos服务器登陆信息对应的session属性名
	 */
	public static final String COGNOS_SESSION_ATTR = "cognos.hosturl";
	public static final String COGNOS_SESSION_EXPIREDTIME = "Cognos.session.expiredTime";
	/**
	 * mstr服务器登陆信息对应的cache属性名
	 */
	public static final String MSTR_CACHE_ATTR_ADMIN = "mstr.factory.admin";


	public static final String MSTR_PROPERTIES_PATH = "rpt-frame/extension/report-server.properties";
	
	public static final String EXCEL_OVER_TYPE = "01";// 覆盖
	public static final String EXCEL_INSERT_TYPE = "02";// 插入

	public static final String EXT_V = "01";//垂直
	public static final String EXT_H = "02";//水平
	
	public static final String BIONE_DRIVER_ORACLE_RAC = "0";//oracle数据库
	public static final String BIONE_DRIVER_ORACLE = "1";//oracle数据库
	public static final String BIONE_DRIVER_DB2 = "2";//db2数据库
	public static final String BIONE_DRIVER_H2 = "3";//H2数据库
	public static final String BIONE_DRIVER_MYSQL = "4";//Mysql数据库
	public static final String BIONE_DRIVER_HIVE = "6";//Hive数据库
	public static final String BIONE_DRIVER_POSTGRESQL = "7";//Postgresql数据库
	public static final String BIONE_DRIVER_INCEPTOR = "8";//Inceptor数据库
	public static final String BIONE_DRIVER_GBASE = "11"; //数据源为Gbase标识
	
	// 规则类型
	public static final String EAST_CR_RULE_EMPTY = "空值";
	public static final String EAST_CR_RULE_RANGE = "取值范围";
	public static final String EAST_CR_RULE_FORMAT = "格式";
	public static final String EAST_CR_RULE_LOGICIN = "表内逻辑";
	public static final String EAST_CR_RULE_LOGICBTW = "表间逻辑";
	public static final String EAST_CR_RULE_SUM = "总分核对";
	public static final String EAST_CR_RULE_EQUAL = "跨监管一致性";
	public static final String EAST_CR_RULE_DSS = "脱敏";
	public static final String EAST_CR_RULE_RECORD = "记录数";
	
	// 规则类型
	public static final String PSCS_CR_RULE_EMPTY = "空值";
	public static final String PSCS_CR_RULE_RANGE = "取值范围";
	public static final String PSCS_CR_RULE_FORMAT = "格式";
	public static final String PSCS_CR_RULE_LOGICIN = "表内逻辑";
	public static final String PSCS_CR_RULE_LOGICBTW = "表间逻辑";
	public static final String PSCS_CR_RULE_RECORD = "记录数";
	
	// 规则类型
	public static final String SAFE_CR_RULE_EMPTY = "空值";
	public static final String SAFE_CR_RULE_RANGE = "取值范围";
	public static final String SAFE_CR_RULE_FORMAT = "格式";
	public static final String SAFE_CR_RULE_LOGICIN = "表内逻辑";
	public static final String SAFE_CR_RULE_LOGICBTW = "表间逻辑";
	public static final String SAFE_CR_RULE_RECORD = "记录数";
	// 规则类型
	public static final String BFD_CR_RULE_EMPTY = "空值";
	public static final String BFD_CR_RULE_RANGE = "取值范围";
	public static final String BFD_CR_RULE_LOGICIN = "表内逻辑";
	public static final String BFD_CR_RULE_LOGICBTW = "表间逻辑";
	public static final String BFD_CR_RULE_EQUAL = "跨监管一致性";
	public static final String BFD_CR_RULE_RECORD = "记录数";

	public static final String RES_TYPE_ROOT = "00";
	public static final String RES_TYPE_CATA = "01";
	public static final String RES_TYPE_NODE = "02";
	public static final String RES_TYPE_OPER = "03";
	
	// 明细报表 数据校验 运算符
	public static final String FRS_OPER_TYPE_EQUAL = "==";
	public static final String FRS_OPER_TYPE_NOT_EQUAL = "!=";
	public static final String FRS_OPER_TYPE_GREATER_OR_EQUAL = ">=";
	public static final String FRS_OPER_TYPE_LESS_OR_EQUAL = "<=";
	public static final String FRS_OPER_TYPE_GREATER = ">";
	public static final String FRS_OPER_TYPE_LESS = "<";
	
	/**
	 * 纯数字类型
	 */
	public static final String FRS_DETAIL_EXPRESSION_TYPE_NUMBER = "0";
	
	/**
	 * 计算公式
	 */
	public static final String FRS_DETAIL_EXPRESSION_TYPE_CALCULATION_FORMULA = "1";
	
	public static String RPT_CHECK_TYPE_LOGIC_DETAIL = "99";// 明细逻辑校验
	
	/**
	 * 引擎指标运行任务状态
	 */
	public static String RPT_ENGINE_IDX_STS_WAIT = "01";// 等待运行
	public static String RPT_ENGINE_IDX_STS_DOING = "02";// 运行中
	public static String RPT_ENGINE_IDX_STS_FINISH = "03";// 执行成功
	public static String RPT_ENGINE_IDX_STS_FAIL = "04";// 执行失败
	public static String CHECK_INDEX_PLAN = "05";//计划值

	/**
	 * 数据模型初始化表后缀
	 */
	public static String RPT_SYS_MODULE_TABLE_SUFFIX = "_init";

	/**
	 * 不同模块使用的配置文件
	 */
	public static String RPT_ENGINE_BFD_PROPERTIES = "biapp-bfd/exception/bfdMessage.properties";
	public static String RPT_ENGINE_EAST_PROPERTIES = "biapp-east/exception/eastMessage.properties";
	public static String RPT_ENGINE_CRRS_PROPERTIES = "biapp-crrs/exception/crrsMessage.properties";
	public static String RPT_ENGINE_FSRS_PROPERTIES = "biapp-fsrs/exception/fsrsMessage.properties";
	public static String RPT_ENGINE_SAFE_PROPERTIES = "biapp-safe/exception/safeMessage.properties";
	public static String RPT_ENGINE_IMAS_PROPERTIES = "biapp-imas/exception/imasMessage.properties";
	public static String RPT_ENGINE_CR_PROPERTIES = "biapp-cr/exception/crMessage.properties";
	public static String RPT_ENGINE_PSCS_PROPERTIES = "biapp-pscs/exception/pscsMessage.properties";
	
	
	/**
	 * 升级概况-新增
	 */
	public static String RPT_UPGRADE_ADD = "01";
	/**
	 * 升级概况-修改
	 */
	public static String RPT_UPGRADE_UPDATE = "02";
	/**
	 * 升级概况-停用
	 */
	public static String RPT_UPGRADE_DELETE = "03";
	/**
	 * 升级概况-无变化
	 */
	public static String RPT_UPGRADE_UNCHANGE = "04";

	/**
	 * 报表校验公式导入方式
	 */
	public static String RPT_VALID_IMPORT_TYPE_1 = "1";//根据指标编号导入
	public static String RPT_VALID_IMPORT_TYPE_2 = "2";//根据单元格名称导入
	public static String RPT_VALID_IMPORT_TYPE_3 = "3";//根据报表编号.单元格编号导入
	
	/**
	 * 数据类型-归档数据
	 */
	public static final String ARCHIVE_TYPE_SUBMIT_DATA= "01";
	/**
	 * 数据类型-回灌数据
	 */
	public static final String ARCHIVE_TYPE_IMPORT_DATA= "02";
	/**
	 * 数据类型-初始数据
	 */
	public static final String ARCHIVE_TYPE_INIT_DATA= "03";
	/**
	 * 数据类型-填报数据
	 */
	public static final String ARCHIVE_TYPE_FILL_DATA= "04";
	/**
	 * 数据类型-初始数据（元）
	 */
	public static final String ARCHIVE_TYPE_INIT_DATA_YUAN= "05";


	// 明细类模块简称和对应的配置文件枚举
	public enum DetailProperties {
		EAST("EAST", RPT_ENGINE_EAST_PROPERTIES),
		BFD("BFD", RPT_ENGINE_BFD_PROPERTIES),
		IMAS("IMAS", RPT_ENGINE_IMAS_PROPERTIES)
		;
		private String code;
		private String name;
		DetailProperties(String code, String name) {
			this.code = code;
			this.name = name;
		}

		public String getCode() {
			return code;
		}

		public void setCode(String code) {
			this.code = code;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}
		/**
		 * code-value映射map
		 */
		private static Map<String, String> map;

		static {
			map = new HashMap<>();
			for (DetailProperties detailProperties: DetailProperties.values()) {
				map.put(detailProperties.getCode(), detailProperties.getName());
			}
		}
		/**
		 * 根据code返回对应值
		 *
		 * @param code 返回码
		 * @return 返回值
		 */
		public static String getValue(String code) {
			return map.get(code);
		}
	}

	// 明细类模块简称和对应的字段配置表
	public enum DetailFieldTab {
		EAST("EAST", "EAST_CR_COL"),
		BFD("BFD", "BFD_CR_COL"),
		IMAS("IMAS", "IMAS_CR_COL")
		;
		private String code;
		private String name;
		DetailFieldTab(String code, String name) {
			this.code = code;
			this.name = name;
		}

		public String getCode() {
			return code;
		}

		public void setCode(String code) {
			this.code = code;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}
		/**
		 * code-value映射map
		 */
		private static Map<String, String> map;

		static {
			map = new HashMap<>();
			for (DetailFieldTab detailProperties: DetailFieldTab.values()) {
				map.put(detailProperties.getCode(), detailProperties.getName());
			}
		}
		/**
		 * 根据code返回对应值
		 *
		 * @param code 返回码
		 * @return 返回值
		 */
		public static String getValue(String code) {
			return map.get(code);
		}
	}


	// 明细类模块简称和对应的配置表
	public enum DetailTab {
		EAST("EAST", "EAST_CR_TAB"),
		BFD("BFD", "BFD_CR_TAB"),
		IMAS("IMAS", "IMAS_CR_TAB")
		;
		private String code;
		private String name;
		DetailTab(String code, String name) {
			this.code = code;
			this.name = name;
		}

		public String getCode() {
			return code;
		}

		public void setCode(String code) {
			this.code = code;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}
		/**
		 * code-value映射map
		 */
		private static Map<String, String> map;

		static {
			map = new HashMap<>();
			for (DetailTab detailProperties: DetailTab.values()) {
				map.put(detailProperties.getCode(), detailProperties.getName());
			}
		}
		/**
		 * 根据code返回对应值
		 *
		 * @param code 返回码
		 * @return 返回值
		 */
		public static String getValue(String code) {
			return map.get(code);
		}
	}

	// 明细类模块简称和对应的配置表
	public enum DetailRuleTab {
		EAST("EAST", "EAST_CR_RULE"),
		BFD("BFD", "BFD_CR_RULE"),
		IMAS("IMAS", "IMAS_CR_RULE")
		;
		private String code;
		private String name;
		DetailRuleTab(String code, String name) {
			this.code = code;
			this.name = name;
		}

		public String getCode() {
			return code;
		}

		public void setCode(String code) {
			this.code = code;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}
		/**
		 * code-value映射map
		 */
		private static Map<String, String> map;

		static {
			map = new HashMap<>();
			for (DetailRuleTab detailProperties: DetailRuleTab.values()) {
				map.put(detailProperties.getCode(), detailProperties.getName());
			}
		}
		/**
		 * 根据code返回对应值
		 *
		 * @param code 返回码
		 * @return 返回值
		 */
		public static String getValue(String code) {
			return map.get(code);
		}
	}

	// 明细类模块简称和对应的配置表
	public enum DetailOrgType {
		EAST("EAST", "04"),
		BFD("BFD", "BFD"),
		IMAS("IMAS", "IMAS")
		;
		private String code;
		private String name;
		DetailOrgType(String code, String name) {
			this.code = code;
			this.name = name;
		}

		public String getCode() {
			return code;
		}

		public void setCode(String code) {
			this.code = code;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}
		/**
		 * code-value映射map
		 */
		private static Map<String, String> map;

		static {
			map = new HashMap<>();
			for (DetailOrgType detailProperties: DetailOrgType.values()) {
				map.put(detailProperties.getCode(), detailProperties.getName());
			}
		}
		/**
		 * 根据code返回对应值
		 *
		 * @param code 返回码
		 * @return 返回值
		 */
		public static String getValue(String code) {
			return map.get(code);
		}
	}
}
