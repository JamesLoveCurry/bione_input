package com.yusys.bione.frame.base.common;

import com.google.common.collect.Lists;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <pre>
 * Title:全局常量定义类
 * Description: 统一维护相关的常量 ,代码中涉及到的常量，统一在此定义，避免到处分散无法维护
 * </pre>
 * 
 * @author mengzx
 * @version 1.00.00
 * 
 *          <pre>
 * 修改记录
 *    修改后版本:     修改人：  修改日期:     修改内容:
 * </pre>
 */
public class GlobalConstants4frame {
    
    /**
     * 节点类型
     */
    public static final String RES_TYPE_ROOT = "00";//根节点
    public static final String RES_TYPE_CATA = "01";//目录
    public static final String RES_TYPE_NODE = "02";//节点
    public static final String RES_TYPE_OPER = "03";//权限
    
    /**
     * 监管报送数据填报导入数据路径
     */
    public static String FRS_RPT_FILL_IMPORT_PATH = "import\\rpt\\fill\\";
    
    /** 定义表时，保存表类型(1：补录表，2：数据字典，3：其他表) --add by zhongqh */
    public static List<Map<String, String>> tableType = Lists.newArrayList();
    public final static String TAB_INPUT = "1";// 表类型为补录表
    public final static String TAB_LIB = "2";// 表类型为数据字典
    public final static String TAB_OTHER = "3";// 表类型为其他表

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
	
	// 默认的PD
	public static String DEFAULT_PD = "123456";
		
	// 通用树根节点ID
	public static final String TREE_ROOT_NO = "0";
	
	// 密码Hash散列的salt值,目前是固定值，可以考虑在User表中增加一个字段动态设置该值
	public static final String CREDENTIALS_SALT = "YTEC_BIONE";

	// 授权对象定义的标识符
	public static final String AUTH_OBJ_DEF = "AUTH_OBJ_DEF";//授权对象
	public static final String AUTH_OBJ_DEF_ID_POSI = "AUTH_OBJ_POSI";// 岗位
	public static final String AUTH_OBJ_DEF_ID_USER = "AUTH_OBJ_USER";// 用户
	public static final String AUTH_OBJ_DEF_ID_ROLE = "AUTH_OBJ_ROLE";// 角色
	public static final String AUTH_OBJ_DEF_ID_ORG = "AUTH_OBJ_ORG";// 机构
	public static final String AUTH_OBJ_DEF_ID_DEPT = "AUTH_OBJ_DEPT";// 部门
	public static final String AUTH_OBJ_DEF_ID_GROUP = "AUTH_OBJ_GROUP";// 授权对象组

	// 授权资源定义的标识符
	public static final String AUTH_RES_DEF = "AUTH_RES_DEF";//授权资源
	public static final String AUTH_RES_DEF_ID_MENU = "AUTH_RES_MENU";// 菜单资源
	public static final String AUTH_RES_DEF_ID_MSG = "AUTH_RES_MSG"; // 公告资源
	public static final String AUTH_RES_RPT_FILL = "AUTH_RES_RPT_FILL";// 填报报表资源
	public static final String AUTH_RES_RPTORG = "AUTH_RES_RPTORG";//机构数据资源

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
	// 监管报送逻辑系统标识
	public static String FRS_LOGIC_SYSTEM = "FRS";
	
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

	//机构图标
	public static String LOGIC_ORG_ICON="/"+ICON_URL+"/application_view_list.png";
	public static String LOGIC_ALL_ICON="/"+ICON_URL+"/folder.png";
	//部门图标
	public static String LOGIC_DEPT_ICON="/"+ICON_URL+"/application.png";
	//角色图标 add by zhongqh
	public static String LOGIC_USER_ICON = "/"+ICON_URL+"/user_green.png";
	public static String LOGIC_ORG_ICON_NEW="/"+ICON_URL+"/connect.png";
	public static String LOGIC_DEPT_ICON_NEW="/"+ICON_URL+"/report_user.png";
	public static String ORG_LEVEL_ICON="/"+ICON_URL+"/column.png";
	
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
	// 导入文件临时路径
	public static String LOGIC_SYS_IMPORT_PATH = IMPORT_PATH
			+ "/logicsys_import";
	// 导入文件临时路径
	public static String WARN_SYS_IMPORT_PATH = IMPORT_PATH
			+ "/warnsys_import";
	// 导入文件临时路径
	public static String ORGMERGE_SYS_IMPORT_PATH = IMPORT_PATH
			+ "/orgmergesys_import";

	// 系统变量信息
	// 变量类型
	public static String BIONE_SYS_VAR_TYPE_CONSTANT = "01"; // 常量
	public static String BIONE_SYS_VAR_TYPE_SQL = "02"; // SQL语句
	public static String BIONE_SYS_VAR_TYPE_SYSTEM = "03"; // 平台型
	
	// 平台型系统变量标识
	public static String BIONE_SYSTEM_VAR_CURRENT_ORG = "currentOrg"; // 当前用户所属机构
	public static String BIONE_SYSTEM_VAR_CURRENT_DEPT = "currentDept"; // 当前用户所属部门
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

	/**
	 * lucene默认索引的域
	 */
	public static final String INDEX_DOCUMENT_ID = "id";//文档的唯一标识
	public static final String INDEX_DOCUMENT_MODULE_ID = "moduleId";//检索模块的唯一标识
	public static final String INDEX_DOCUMENT_MODULE_NAME = "moduleName";//检索模块的名称
	
	/**
	 * lucene默认创建索引的文件夹
	 */
	public static final String INDEX_PATH  = "/export/frame/index/";
	
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

	/** 分层认证方式 **/
	public static final String AUTH_LEVEL_TYPE_NEXT = "01"; //拥有本级机构和下一级机构资源的授权权限
	public static final String AUTH_LEVEL_TYPE_ALL = "02";    //拥有本级机构和所有子机构资源的授权权限

	/** 工作流业务类型：监管报表审核*/
	public static final String BUZ_TYPE_RPT_AUDIT = "processDefinitionKey";
	
	public static final String TASK_TYPE_NO = "taskType";
	public static final String NODE_TYPE_NO = "nodeType";
	
	/** 任务实例ID **/
	public final static String TAB_DATA_CASE = "SYS_DATA_CASE";
	
	// 设置机构类型参数
	public static final String TREE_PARAM_TYPE = "reportorgtype";
	
	/**
	 * 指标类报送任务类型参数标识-idxTaskType
	 */
	public static String IDX_TASK_TYPE_PARAM_TYPE_NO = "idxTaskType";

	/**
	 * 数据源标识
	 **/
	public static final String ORACLE_RAC_DATA_SOURCE = "0";// 数据源为Oracle集群标识

	public static final String ORACLE_DATA_SOURCE = "1";// 数据源为Oracle标识

	public static final String DB2_DATA_SOURCE = "2";// 数据源为DB2标识

	public static final String H2_DATA_SOURCE = "3";// 数据源为H2标识

	public static final String MYSQL_DATA_SOURCE = "4";// 数据源为MYSQL标识

	public static final String ODPS_DATA_SOURCE = "5";// 数据源为ODPS标识

	public static final String HIVE_DATA_SOURCE = "6";// 数据源为HIVE标识

	public static final String ELK_DATA_SOURCE = "7";// 数据源为ELK标识

	public static final String INCEPTOR_DATA_SOURCE = "8";// 数据源为Inceptor标识

	public static final String GBASE_DATA_SOURCE = "11"; //数据源为Gbase标识

	public static final String POSTGRESQL_DATA_SOURCE = "12"; //数据源为高斯标识

	/**
	 * 用户授权对象标识
	 **/
	public final static Map<String, String> map = new HashMap<String, String>(16);
	static {
		map.put("AUTH_OBJ_ROLE", "角色");
		map.put("AUTH_OBJ_ORG", "机构");
		map.put("AUTH_OBJ_DEPT", "条线");
		map.put("AUTH_OBJ_USER", "用户");
	}
}
