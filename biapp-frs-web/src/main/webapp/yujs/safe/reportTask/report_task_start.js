/**
 *
 * <pre>
 * Title: 【报送管理】-【报文生成】
 * Description: 报文生成：启动页面
 * </pre>
 * @author miaokx
 * @version 1.00.00
 * @date 2020年6月17日
 */

var ary_allTaskIds = [];  // 所有要启动的对象
var data_dt = [];  // 日期
var ary_allTableNames = [];
var report_type = null;
// 是否生成空报文
var is_create_empty_report = null;
var data_dt_start = null;
var data_dt_end = null;
var org_no = null;

// 是否全部生成
var isAllConfirm = null;
function AfterInit() {
	ary_allTaskIds = jso_OpenPars2.allTaskIds;
	data_dt = jso_OpenPars2.data_dt;
	ary_allTableNames = jso_OpenPars2.allTableNames;
	report_type = jso_OpenPars2.report_type;
	is_create_empty_report = jso_OpenPars2.is_create_empty_report;
	isAllConfirm = jso_OpenPars2.isAllConfirm;
	data_dt_start = jso_OpenPars2.data_dt_start;
	data_dt_end = jso_OpenPars2.data_dt_end;
	org_no = jso_OpenPars2.org_no;
	JSPFree.createSpanByBtn("d1",["启动/onStart","刷新/onRefresh","取消/onCancel"]);
}

/**
 * 页面初始化后
 * @returns
 */
function AfterBodyLoad() {
	doRefreshData();
	
	// 每5秒钟刷新一次
	self.setInterval(doRefreshData,5000);
}

/**
 * 刷新数据
 * @returns
 */
function doRefreshData() {
	// 加载数据
	var jso_statu = JSPFree.doClassMethodCall("com.yusys.safe.reporttask.service.SafeCrReportBS", "checkCreateReportFileStatu");
	document.getElementById("d1_A").innerHTML = jso_statu.msg;

	if ("OK" == jso_statu.status) {
		$('#d1_onStart').linkbutton('disable');
	} else {
		$('#d1_onStart').linkbutton('enable');
	}
}

/**
 * 【启动】按钮点击逻辑
 */
function onStart() {
	// 选择生成时，如果不创建空报文，则只生成有数据修改的报文
	var currentTaskIds = ary_allTaskIds;
	var currentTableNames = ary_allTableNames;
	if (!is_create_empty_report) {
		var jso_par = {
			taskids: ary_allTaskIds,
			tasktables: ary_allTableNames,
			report_type: report_type,
			data_dt_start: data_dt_start,
			data_dt_end: data_dt_end,
			org_no: org_no
		}
		// 判断那些报表存在修改的数据,可以生成报文的数据
		var checkResult = JSPFree.doClassMethodCall(
			"com.yusys.safe.reporttask.service.SafeCrReportBS", "getHaveModifyDataTableNamesByBwStatus", jso_par);
		if (checkResult.status === 'ok') {
			ary_allTaskIds = checkResult.taskRids;
			ary_allTableNames = checkResult.tableNames;
		} else {
			$.messager.alert('提示', '启动报文任务失败，请重新启动！', 'info');
			return;
		}
		// 此标识代表为 true 时，代表当前模块第一次生成报文，并且此模块当前日期下无数据，需要生成一个控制文件，表示报文已生成
		var isOnlyCreateControlFile = false;
		// 选择的数据表当前批次日期下无数据，则需要判断当前模块下是否存在数据，如果不存在则需要生成【控制文件】
		if (ary_allTaskIds.length == 0) {
			// 1. 如果当前选择的报表当前日期下无数据，并且，
			// 2. 当前模块当前日期下第一次生成报文，则判断整个模块是否存在数据，如果不存在数据，则需要生成 控制文件
			// 判断当前模块下数据表是否存在数据，如果不存在数据，则生成空报文
			var checkIsFirstCreateReport = JSPFree.doClassMethodCall(
				"com.yusys.safe.reporttask.service.SafeCrReportBS", "checkCurrentModuleIsExistsData", jso_par);
			if (checkIsFirstCreateReport.status === 'ok') {
				// 第一次创建报文
				var isFirstCreateReport = checkIsFirstCreateReport.isFirstCreateReport;
				var tableNames = checkIsFirstCreateReport.tableNames;
				// 当前模块下也不存在数据
				if (isFirstCreateReport && tableNames.length == 0) {
					ary_allTaskIds = currentTaskIds;
					ary_allTableNames = currentTableNames;
					// 需要创建一个控制文件，以表示当前报文已生成
					isOnlyCreateControlFile = true;
				}
			} else {
				$.messager.alert('提示', '启动报文任务失败，请重新启动！', 'info');
				return;
			}
		}
	}
	var jso_rt = null;
	jso_rt = JSPFree.doClassMethodCall("com.yusys.safe.reporttask.service.SafeCrReportBS",
		"startReportTask", {
			taskids: ary_allTaskIds,
			data_dt: data_dt,
			tasktables: ary_allTableNames,
			report_type: report_type,
			is_all_confirm: isAllConfirm,
			is_only_create_control_file: isOnlyCreateControlFile
		});
	JSPFree.alert(jso_rt.msg);

	// 按钮置灰,不要重复启动!
	$('#d1_onStart').linkbutton('disable');

	// 一秒后刷新一下状态!
	self.setTimeout(doRefreshData, 200);
}

/**
 * 【刷新】按钮点击逻辑
 */
function onRefresh() {
	doRefreshData();
	JSPFree.alert("人工刷新完成!");
}

/**
 * 关闭窗口
 */
function onCancel() {
	JSPFree.closeDialog();
}
