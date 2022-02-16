/**
 * 
 */
package com.yusys.biapp.input.common;

/**
 * <pre>
 * Title:补录用静态变量
 * Description: 补录用静态变量 
 * </pre>
 * @author caiqy  caiqy@yuchengtech.com
 * @version 1.00.00
 * <pre>
 * 修改记录
 *    修改后版本:     修改人：  修改日期:     修改内容: 
 * </pre>
 */
public class InputConstants {

	public static final String FILE_IMP_PATH = "/import/input/datainput";
	public static final String FILE_IMP_TEMP_PATH = "/import/input/templateInfo";
	public static final String FILE_EXP_PATH = "/export/input";//填报-导出Excel路径
	
	public final static String TASK_STATE_USING = "1";// 任務状态启用
	public final static String TASK_STATE_STOP = "0";// 任務状态停用


	// 应用的上下文路径
	public static String APP_CONTEXT_PATH;
	// 应用部署的物理路径
	public static String APP_REAL_PATH;
	
	// 通用树根节点标识
	public static final String DEFAULT_TREE_ROOT_NO = "#ytec_root#";
	
	// 通用的状态标识 1:有效，0：无效
	public static final String COMMON_STATUS_VALID = "1";
	public static final String COMMON_STATUS_INVALID = "0";
	
	// 认证方式: 01本地认证-超级系统认证  02本地认证-逻辑系统认证   
	//其他为外部认证  03UA认证 04LDAP认证
	public static final String AUTH_TYPE_LOCAL_SUPER = "01";
	public static final String AUTH_TYPE_LOCAL_SYS = "02";
	public static final String AUTH_TYPE_UA = "03";
	public static final String AUTH_TYPE_LDAP = "04";
	public static final String AUTH_TYPE_LOCAL_CAS = "05";
	// 通用状态：是/否
	public static final String COMMON_YES = "1";
	public static final String COMMON_NO  = "0";
	
	// 默认每月的天数
	public static int COMMON_MONTH_DAYS = 30;
		
	// 通用树根节点ID
	public static final String TREE_ROOT_NO = "0";
	
	// 密码Hash散列的salt值,目前是固定值，可以考虑在User表中增加一个字段动态设置该值
	public static final String CREDENTIALS_SALT = "YTEC_BIONE";

	// 授权对象定义的标识符
	public static final String AUTH_OBJ_DEF_ID_POSI = "AUTH_OBJ_POSI";// 岗位
	public static final String AUTH_OBJ_DEF_ID_USER = "AUTH_OBJ_USER";// 用户
	public static final String AUTH_OBJ_DEF_ID_ROLE = "AUTH_OBJ_ROLE";// 角色
	public static final String AUTH_OBJ_DEF_ID_ORG = "AUTH_OBJ_ORG";// 机构
	public static final String AUTH_OBJ_DEF_ID_DEPT = "AUTH_OBJ_DEPT";// 部门
	public static final String AUTH_OBJ_DEF_ID_GROUP = "AUTH_OBJ_GROUP";// 授权对象组

	// 授权资源定义的标识符
	public static final String AUTH_RES_DEF_ID_MENU = "AUTH_RES_MENU";// 菜单资源
	public static final String AUTH_RES_DEF_ID_MSG = "AUTH_RES_MSG"; // 公告资源
	   public static final String AUTH_RES_SUP_TASK = "AUTH_RES_SUP_TASK";// 补录表查询权限
	    public static final String AUTH_RES_SUPPLEMENT = "AUTH_RES_SUPPLEMENT";//补录任务权限


	// 资源定义数据来源 1、实现类方式 2、SQL语句方式
	public static final String RES_DATA_SRC_TYPE_BEAN = "1";
	public static final String RES_DATA_SRC_TYPE_SQL = "2";

	// 资源许可类型 1、操作授权 2、数据授权
	public static final String RES_PERMISSION_TYPE_OPER = "1";
	public static final String RES_PERMISSION_TYPE_DATA = "2";

	// 全部授权
	public static final String PERMISSION_ALL = "*";
	public static final String PERMISSION_NONE = "-";
	public static final String PERMISSION_SEPARATOR = ":";// 权限许可串的分割符

	// 授权许可字符串前缀 OPER：操作权限 DATA:数据权限,URL:菜单URL
	public static final String PERMISSION_PREFIX_OPER = "OPER";
	public static final String PERMISSION_PREFIX_DATA = "DATA";
	public static final String PERMISSION_PREFIX_URL = "URL";

	// 超级管理员系统标识
	public static String SUPER_LOGIC_SYSTEM = "BIONE";

	// 超级管理员用户标志(页面缺省填写)
	public static final String SUPER_USER_NO = "";
	// 超级管理员用户密码(页面缺省填写)
	public static final String SUPER_USER_PWD = "";

	// 主题
	public static final String THEME = "classics";

	// 图标存放地址
	public static final String ICON_URL = "images/" + THEME + "/icons";

	// 逻辑系统 配置管理员图标
	public static String LOGIC_ADMIN_ICON = "/" + ICON_URL + "/user.png";
	// 逻辑系统 配置功能点 功能默认图标
	public static String LOGIC_FUNC_ICON = "/" + ICON_URL + "/application.png";
	// 逻辑系统 配置功能点 模块默认图标
	public static String LOGIC_MODULE_ICON = "/" + ICON_URL + "/f1.gif";
	// 逻辑系统 授权资源 默认图标
	public static String LOGIC_AUTH_RES_ICON = "/" + ICON_URL
			+ "/application_double.png";
	// 逻辑系统 授权对象 默认图标
	public static String LOGIC_AUTH_OBJ_ICON = "/" + ICON_URL + "/rsgl2.gif";
	
	//add 董龙婷  application_view
	// 逻辑系统 配置报表图标
	public static String LOGIC_VIEW_ICON = "/" + ICON_URL + "/application_view_list.png";

	//机构图标
	public static String LOGIC_ORG_ICON="/"+ICON_URL+"/application_view_list.png";
	public static String LOGIC_ALL_ICON="/"+ICON_URL+"/folder.png";
	//部门图标
	public static String LOGIC_DEPT_ICON=ICON_URL+"/grid.png";//角色图标 add by zhongqh
	public static String LOGIC_USER_ICON = "/"+ICON_URL+"/user_green.png";
	public static String LOGIC_ORG_ICON_NEW=ICON_URL+"/organ.gif";
	public static String LOGIC_DEPT_ICON_NEW="/"+ICON_URL+"/report_user.png";
	public static String ORG_LEVEL_ICON="/"+ICON_URL+"/column.png";
	
	//报表权限编码 --add by zhongqh
	public static String RPT_READ="1"; //报表 查看 权限
	public static String RPT_WRITE="2";//报表 填报 权限
	public static String RPT_ALL="3";//报表 所有 权限
	public static String RPT_EXTRA_ADD="4";//报表 特别添加 权限
	public static String RPT_EXTRA_FORBIDE="5";//报表 特别禁止 权限
	//指标权限
	public static String INDEX_ALL="6";//指标 所有 权限
	
	// 菜单节点类型
	public static String MENU_TYPE_MODULE = "M"; // 模块
	public static String MENU_TYPE_FUNCTION = "F";// 功能

	// 任务类型
	public static String TASK_TYPE_SYSTASK = "01";// 系统任务
	public static String TASK_TYPE_EXPANDTASK = "02";// 扩展任务
	// 任务状态
	public static String TASK_STS_NORMAL = "01";// 正常
	public static String TASK_STS_STOP = "02";// 挂起
	// 导出目录
	public static String EXPORT_PATH = "/export/bione";
	// 导出元数据实例
	public static String LOGIC_SYS_EXPORT_PATH = EXPORT_PATH
			+ "/logicsys_export";
	// 导入目录
	public static String IMPORT_PATH = "/import/bione";
	// 导出文件临时路径
	public static String LOGIC_SYS_IMPORT_PATH = IMPORT_PATH
			+ "/logicsys_import";

	// 系统变量信息
	// 变量类型
	public static String BIONE_SYS_VAR_TYPE_CONSTANT = "01"; // 常量
	public static String BIONE_SYS_VAR_TYPE_SQL = "02"; // SQL语句
	public static String BIONE_SYS_VAR_TYPE_SYSTEM = "03"; // 平台型
	
	// 平台型系统变量标识
	public static String BIONE_SYSTEM_VAR_CURRENT_ORG = "currentOrg"; // 当前用户所属机构
	public static String BIONE_SYSTEM_VAR_NOW_DATE = "nowDate"; // 当前时间(Date)
	public static String BIONE_SYSTEM_VAR_NOW_TIMESTAMP = "nowTimestamp"; // 当前时间(Timestamp)
	public static String BIONE_SYSTEM_VAR_YESTODAYNOW_DATE = "yestodayNowDate"; // 当前时间(Timestamp)
	public static String BIONE_SYSTEM_VAR_YESTODAYNOW_TIMESTAMP = "yestodayNowTimestamp"; // 当前时间(Timestamp)

	// 属性组件类型
	public static String BIONE_ATTR_ELEMENT_TYPE_TEXT = "01"; // 文本框
	public static String BIONE_ATTR_ELEMENT_TYPE_SELECT = "02"; // 下拉框
	public static String BIONE_ATTR_ELEMENT_TYPE_DATE = "03"; // 日期框
	public static String BIONE_ATTR_ELEMENT_TYPE_HIDDEN = "04"; // 隐藏域
	public static String BIONE_ATTR_ELEMENT_TYPE_PASSWORD = "05"; // 密码框
	public static String BIONE_ATTR_ELEMENT_TYPE_TEXTAREA = "06"; // 多行文本输入域
	
	//数据集树图标
	public static final String DATA_TREE_NODE_ICON_ROOT="/images/classics/icons/house.png";//根结点
	public static final String DATA_TREE_NODE_ICON_CATALOG="/images/classics/icons/Catalog.gif";//目录
	public static final String DATA_TREE_NODE_ICON_REPORT="/images/classics/icons/report.png";//模板
	
	// 平台逻辑字段类型标识
		public static final String LOGIC_DATA_TYPE_TEXT = "01";// 文本old--text
		public static final String LOGIC_DATA_TYPE_NUMBER = "02";// 数字old--number
		public static final String LOGIC_DATA_TYPE_DATE = "03";// 日期old--date
		public static final String LOGIC_DATA_TYPE_TIME = "04";// 时间old--time
		public static final String LOGIC_DATA_TYPE_OBJECT = "05";// 对象old--object

	// 系统密码安全配置数据ID默认“1”
	public static String BIONE_PWD_SECURITY_INFO_ID = "1";
	/**
	 * 报表目录树  -根节点图标 路径
	 */
	public static String TREE_ICON_ROOT = "/"+ICON_URL+"/house.png";
	/**
	 * 	报表目录树  -目录图标 路径
	 */
	public static String REPORT_CATALOG_ICON = "/"+ICON_URL+"/folder_page.png";
	/**
	 * 	报表目录树  -报表图标 路径
	 */
	public static String REPORT_ICON = "/"+ICON_URL+"/report.png";

	/**
	 * 读取引擎用
	 */
	public static String REPORT_ENGINE_READ_REPORT = "reportdata";
	
	/**
	 * 缓存报表信息
	 */
	public static String REPORT_SAVE_OBJECT_DIR="objectdata";
	/**
	 * 报表缓存方式
	 */
	public static String REPORT_SAVE_IMPL="fileReportListSaver";//当前为文件缓存
	
	/**
	 * 参数模板属性表：参数类型
	 */
	public static final String RPT_PARAMTMP_ATTRS_PARAM_TYPE_TEXT = "01";//文本
	public static final String RPT_PARAMTMP_ATTRS_PARAM_TYPE_SELECT = "02";//下拉框
	public static final String RPT_PARAMTMP_ATTRS_PARAM_TYPE_DATE = "03";//日期
	public static final String RPT_PARAMTMP_ATTRS_PARAM_TYPE_TREE = "04";//数据树
	public static final String RPT_PARAMTMP_ATTRS_PARAM_TYPE_HIDDEN = "05";//隐藏域
	/**
	 * 参数模板属性表：值类型
	 */
	public static final String RPT_PARAMTMP_ATTRS_VAL_TYPE_VALUE = "01";//定值
	public static final String RPT_PARAMTMP_ATTRS_VAL_TYPE_SQL = "02";//sql语句
	public static final String RPT_PARAMTMP_ATTRS_VAL_TYPE_SYSPARAM = "03";//系统参数（下拉框时可用）
	public static final String RPT_PARAMTMP_ATTRS_VAL_TYPE_DATASET = "04";//数据集（数据树类型时可用）
	// 数据源类型
	public static final String LOGIC_SOURCE_TYPE_TABLE = "02";// 数据库表
	public static final String LOGIC_SOURCE_TYPE_SQL = "01";// 标准SQL
	public static final String ORACLE_DATA_SOURCE = "1";// 数据源为Oracle标识
	public static final String DB2_DATA_SOURCE = "2";// 数据源为DB2标识

	/**
	 * sql语句类型
	 */
	public static final String RPT_SQL_TYPE_COMB = "0";//下拉框sql
	public static final String RPT_SQL_TYPE_TREE = "1";//数据树sql
	
	/**
	 * lucene默认索引的域
	 */
	public static final String INDEX_DOCUMENT_ID = "id";//文档的唯一标识
	public static final String INDEX_DOCUMENT_MODULE_ID = "moduleId";//检索模块的唯一标识
	public static final String INDEX_DOCUMENT_MODULE_NAME = "moduleName";//检索模块的名称
	
	/**
	 * lucene默认创建索引的文件夹
	 */
	public static final String INDEX_PATH  = "/export/bione/index/";
	
	/**
	 *  对象动作缓存名
	 */
	public static final String CACHE_OBJECT_ACTION = "OBJECT_ACTION";

	
	/**
	 * 报表 标签对象标识
	 */
	public static final String RPT_LABEL_OBJ_NO = "report";
	
	/**
	 * 密码安全策略中，锁定类型
	 */
	public static final String PWD_SECURITY_LOCK_TYPE_TIMELIMIT = "01";//限时锁定
	public static final String PWD_SECURITY_LOCK_TYPE_EVER = "02";//永久锁定
	
	/**
	 * 密码安全校验实现类名称
	 */
	public static final String USER_LOCK_VALIDATOR_NAME = "userLockDbValidator";
	
	/** 消息状态 - 草稿 */
	public static final String MESSAGE_ANNOUNCEMENT_STATUS_DRAFT = "1";

	/** 消息状态 - 已发布 */
	public static final String MESSAGE_ANNOUNCEMENT_STATUS_PUBLISHED = "2";

	/** 消息状态 - 已过期 */
	public static final String MESSAGE_ANNOUNCEMENT_STATUS_EXPIRED = "3";

	/** 消息状态 - 已删除 */
	public static final String MESSAGE_ANNOUNCEMENT_STATUS_DELETED = "0";

	
	/** 消息状态 - 已读 */
	public static final String MESSAGE_COMMON_STATUS_READ = "2";

	/** 消息状态 - 未读 */
	public static final String MESSAGE_COMMON_STATUS_UNREAD = "3";
	
	/**  */
	public static final String PUSH_JOIN_LISTEN_ID = "BIONE_NOTICE";
	
	/**  */
	public static final String PUSH_MESSAGE_KEY = "BIONE_MSG_KEY";
	
	/** 展示方式 - 无 */
	public static final String SHOW_TYPE_NULL = "_null";
	
	/** 展示方式 - 在当前页展示 */
	public static final String SHOW_TYPE_IN_SELF = "_self";
	
	/** 展示方式 - 在新的弹出框中展示 */
	public static final String SHOW_TYPE_IN_BLANK = "_blank";
	
	/** 分层认证方式 **/
	public static final String AUTH_LEVEL_TYPE_NEXT = "01"; //拥有本级机构和下一级机构资源的授权权限
	public static final String AUTH_LEVEL_TYPE_ALL = "02";    //拥有本级机构和所有子机构资源的授权权限
	
	
	/** 资产负债机构层级 **/
	public static final String RPT_ORG_LEL_SUB = "3"; //支行层级
	public static final String RPT_ORG_LEL_TWO = "2"; //二级分行层级
	public static final String RPT_ORG_LEL_ONE = "1"; //一级分行层级
	public static final String RPT_ORG_LEL_TOTAL = "0"; //全行层级
	public static final String RPT_ORG_ID = "701100"; //全行机构编号
	
	public static final String DESIGN_EXPORT_PATH = "/export/design";
	
	public static final String RES_TYPE_ROOT = "00";
	public static final String RES_TYPE_CATA = "01";
	public static final String RES_TYPE_NODE = "02";
	public static final String RES_TYPE_OPER = "03";
	
	public static final String AUTH_LOG_TYPE_RES = "01";
	public static final String AUTH_LOG_TYPE_OBJ = "02";
	
	public static final String EXCEL_EXPORT_TYPE_V = "01";// 纵向导出
	public static final String EXCEL_EXPORT_TYPE_H = "02";// 横向导出
	
	public static final String EXCEL_OVER_TYPE = "01";// 覆盖
	public static final String EXCEL_INSERT_TYPE = "02";// 插入

	public static final String EXT_V = "01";//垂直
	public static final String EXT_H = "02";//水平

}
