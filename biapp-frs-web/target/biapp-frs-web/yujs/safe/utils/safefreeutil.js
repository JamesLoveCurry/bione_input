/**
 * 
 * <pre>
 * Title: 【外汇局报送】
 * Description: 常量类定义
 * </pre>
 * @author wangxy31 
 * @version 1.00.00
   @date 2020年5月12日
 */

(function($) {
	window['SafeFreeUtil'] = {};
	
	/**
	 * 自动：任务触发器，类名
	 */
	var scheduledClassName = 'com.yusys.safe.scheduled.ScheduledSafeDayTask';
	SafeFreeUtil.getScheduledClassName = function() {
		return scheduledClassName;
	};
	
	/**
	 * 自动下载申报文件信息反馈：任务触发器，类名
	 */
	var scheduledDownloadClassName = 'com.yusys.safe.scheduled.ScheduledSafeDownloadTask';
	SafeFreeUtil.scheduledDownloadClassName = function() {
		return scheduledDownloadClassName;
	};
	
	/**
	 * 引擎任务rid
	 * 后缀需要补充-报送类型
	 */
	var engineTaskId = '88888888-8888-8888-8888-888888888888';
	SafeFreeUtil.getEngineTaskId = function() {
		return engineTaskId;
	};
	
	/**
	 * 单位基本情况表
	 */
	var tabNameEn = 'SAFE_CORP_INFO';
	SafeFreeUtil.getTabNameEn = function() {
		return tabNameEn;
	};
	
	/**
	 * 任务触发器，类名
	 */
	var tab_names = {
		RPT_STD_CODE_INFO : '字典表',
		SAFE_RESULT_DATA : '错误补录表',
		SAFE_RECEIPT_DATA : '回执补录表',
		SAFE_CR_REPORT_CONFIG : '报文策略表',
		SAFE_CR_REPORT : '报文任务表',
		SAFE_CR_REPORT_LOG : '报文任务日志表',
		SAFE_CR_TAB : '检核数据表',
		SAFE_CR_COL : '检核字段表',
		SAFE_JOB : '自动任务表',
		SAFE_JOB_LOG : '自动任务日志表',
		SAFE_MTS_CONFIG: 'MTS配置',
		SAFE_ENGINE_LOG: '检核日志',
        SAFE_CR_RULE: '规则总表',
        SAFE_FILLING_PROCESS: '填报流程任务主表',
        SAFE_FILLING_PROCESS_CHILD: '填报流程任务子表',
        SAFE_FILLING_REASON: '填报流程任务表原因记录',
		SAFE_CR_RECEIPT: '回执信息表',
		SAFE_CR_STATUS_CONFIG: '状态表'
	};
	SafeFreeUtil.getTableNames = function() {
		return tab_names;
	};
	
	/**
	 * 【检核数据表定义】--表归属
	 */
	var safe_tab_belongto = {
		business : '业务表',
		virtual : '虚拟表',
		code : '字典表'
	};
	SafeFreeUtil.getSafeTabBelongto = function() {
		return safe_tab_belongto;
	};
	
	/**
	 * 【任务触发器】--任务类型
	 */
	var safe_job_type = {
		type1 : '1', // 自动任务
		type2 : '2' // 自动下载申报文件信息反馈
	};
	SafeFreeUtil.getSafeJobType = function() {
		return safe_job_type;
	};
	
	/**
	 * 【监控与统计】中文表名
	 */
	var safe_analysis_tab = {
		report : '报送情况统计表',
		revise : '补录监控统计表',
		handle : '报表处理统计表',
		approval : '审核情况统计表',
		review : '复核情况统计表',
	};
	SafeFreeUtil.getSafeAnalysisTab = function() {
		return safe_analysis_tab;
	};
	/**
	 * 【错误补录表】--状态
	 */
	var safe_result_data_status = {
		status_1 : '待处理',
		status_2 : '完成'
	};
	SafeFreeUtil.getSafeResultDataStatus = function() {
		return safe_result_data_status;
	};
	
	/**
	 * 申报文件反馈处理状态
	 * bfk
	 */
	var safe_receipt_processing_status = {
		status_1: '0',// 未处理
		status_2: '1',// 已处理
		status_3: '2',// 已重新报送
		status_4: '3' // 报送成功
	};
	SafeFreeUtil.getSafeReceiptProcessingStatus = function () {
		return safe_receipt_processing_status;
	};
	
	/**
	 * 【调用引擎】--状态
	 */
	var safe_engine_type = {
		MANUAL: '手动',
		AUTOMATIC : '自动'
	};
	SafeFreeUtil.getSafeEngineType = function() {
		return safe_engine_type;
	};
	
	/**
	 * 【报表处理】【报表下发】--状态
	 * 0初始化 1处理中 2撤回 3完成 4解锁
	 */
	var process_status = {
		INITIALIZATION : '0',
		PROCESSING : '1',
		WITHDRAW : '2',
		COMPLETE : '3',
		UNLOCK : '4'
	};
	SafeFreeUtil.getProcessStatus = function() {
		return process_status;
	};
	
	/**
	 * 【报表处理】【报表复核】--状态
	 * 0下发 1待复核 2退回 3完成
	 */
	var process_child_status = {
		DOWN : '0', 
		REVIEWED : '1',
		RETURN : '2', 
		COMPLETE : '3'
	};
	SafeFreeUtil.getProcessChildStatus = function() {
		return process_child_status;
	};
	/**
	 *是否生成报文--状态
	 * 0未生成，1已生成
	 */
	var bw_status = {
		NO_GENERATE : '0',
		GENERATE : '1'
	};
	SafeFreeUtil.getBwStatus = function() {
		return bw_status;
	};
	/**
	 *报表任务流程 日志状态
	 */
	var filling_reason_type = {
		SUBMIT : '提交',
		BACK : '退回',
		APPROVED: '复核通过'
	};

	SafeFreeUtil.getFillingReasonType = function () {
		return filling_reason_type;
	}
	/**
	 * 报文任务 状态
	 * 0已创建 1生成报文中 2生成报文失败 3完成
	 */
	var process_report_status = {
		INITIALIZATION : '0',
		PROCESSING : '1',
		FAILURE : '2',
		COMPLETE : '3'
	};
	SafeFreeUtil.getProcessReportStatus = function () {
		return process_report_status;
	}
	
	/**
	 * 明细数据-下发状态
	 * 0未处理 1已处理
	 */
	var xf_status = {
		NO_DOWN : '0',
		DOWN : '1'
	};
	SafeFreeUtil.getXfStatus = function() {
		return xf_status;
	};
	
	/**
	 * 明细数据-校验状态
	 * 0校验失败 1校验成功
	 */
	var check_status = {
		NO_CHECK : '0',
		CHECK : '1'
	};
	SafeFreeUtil.getCheckStatus = function() {
		return check_status;
	};

	/**
	 * 报表处理统计-处理状态
	 * 0未处理 1已处理
	 */
	var handled_status ={
		handled_count:'1',
		nohandled_count : '0',
	};
	SafeFreeUtil.getHandledStatus = function() {
		return handled_status;
	};

	/**
	 * 0 未提交
	 * 1 已提交
	 * 2 已审核
	 * 3 审核不通过
	 * @type {{UNAPPROVED: string, APPROVED: string}}
	 */
	var approval_status ={
		UNSUBMITTED:'0',
		SUBMITTED:'1',
		APPROVED:'2',
		UNAPPROVED : '3',
	};
	SafeFreeUtil.getApprovalStatus = function() {
		return approval_status;
	};
	var safe_org_class = {
		zh : '总行',
		fh : '分行',
		zhh : '支行'
	};
	SafeFreeUtil.getSafeOrgClass = function() {
		return safe_org_class;
	};
	/**
	 * 获取机构层级，通过当前登录人机构号获取
	 */
	SafeFreeUtil.getOrgClass = function (orgNo, reportType) {
		var org_class = "";
		var jso_tr = JSPFree.getHashVOs("select org_class from rpt_org_info where mgr_org_no='" + orgNo + "' and org_type='" + reportType + "'");
		if (jso_tr != null && jso_tr.length > 0) {
			org_class = jso_tr[0].org_class;
		}
		return org_class;
	};
	
	var report_type = {
		BOP : 'BOP',
		ACC : 'ACC',
		JSH : 'JSH',
		CFA : 'CFA',
		FAL : 'FAL',
		CWD : 'CWD',
		EXD : 'EXD',
		CRD : 'CRD',
		CRX : 'CRX'
	};
	
	SafeFreeUtil.getReportType = function () {
		return report_type;
	}
	
	SafeFreeUtil.isSafeReportType = function (reportTypeCode) {
		var result = SafeFreeUtil.getReportType()[reportTypeCode];
		return result;
	}
	
})(jQuery);

