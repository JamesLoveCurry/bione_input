/**
 * 保存并创建引擎任务
 *
 */

var reportType = null;
var operateType = null;
var taskId = null;
var dataDt = null;
var tabName = null;
var orgNo = null;
function AfterInit() {
    reportType = jso_OpenPars.reportType;
    operateType = jso_OpenPars.operateType;
    taskId = jso_OpenPars.taskId;
    dataDt = jso_OpenPars.dataDt;
    orgNo = jso_OpenPars.orgNo;
    // 任务选择的报表名称
    tabName = jso_OpenPars.tabName
    // 获取【检核日志】常量类
    tab_name = SafeFreeUtil.getTableNames().SAFE_ENGINE_LOG;
    // 获取英文表名
    var jso_data = JSPFree.doClassMethodCall(
        "com.yusys.safe.base.common.service.SafeCommonBS",
        "getTabNameByEn", {tab_name: tab_name});
    var tab_name_en = jso_data.tab_name_en;
    JSPFree.createSplitByBtn("d1", "上下", 130, ["保存/onConfirm","保存并启动/onSaveAndStart", "取消/onCancel"], false);
    
    var str_className = "Class:com.yusys.safe.base.common.template.CommonBuilderTemplate.getTemplet('引擎任务表','SAFE_ENGINE_TASK','" + reportType + "')";
    JSPFree.createBillCard("d1_A", str_className, null, null);
    
    $("#d1_A_form").append("<div style=\"text-indent:2em;width:100%;margin-top:-180px;\">执行数据校验，会清空校验结果数据，如果需要历史数据，请先导出校验结果。</div>");
    
    var tab_name_className = "Class:com.yusys.safe.base.common.template.TabBuilderTemplate.getTempletOnlyShowCode('" + reportType + "')";
    JSPFree.createBillList("d1_B", tab_name_className, null, {
        isSwitchQuery: "N",
        ishavebillquery: "N",
        onlyItems: "tab_name;tab_name_en;report_code"
    });
    if ("modify" === operateType) {
        JSPFree.queryBillCardData(d1_A_BillCard, "task_id = '" + taskId + "'");
    }
}

function AfterBodyLoad() {
    document.getElementById("d1_A_form").style.overflowY = 'hidden';
    if ("modify" === operateType) {
        FreeUtil.setBillQueryItemEditable("data_dt_start","日历",false);
        $("#data_dt_start").datebox('setValue',dataDt);
        FreeUtil.setBillQueryItemEditable("data_dt_end","日历",false);
        $("#data_dt_end").datebox('setValue',dataDt);
        // 修改的时候，默认将表名勾选上
        var tabDatas = JSPFree.getBillListAllDatas(d1_B_BillList);
        for (var i = 0; i < tabDatas.length; i++) {
            var tab_name = tabDatas[i].tab_name;
            if (tabName.indexOf(tab_name) != -1) {
                d1_B_BillList.datagrid('selectRow', i);
            }
        }
    }
    
}

/**
 * 确定
 * @returns
 */
function onConfirm() {
    var result = saveAndStartBeforeCheck(false);
    result.isStartTask = false;
    JSPFree.closeDialog(result);
}

/**
 * 保存并启动
 */
function onSaveAndStart() {
    var result = saveAndStartBeforeCheck(true);
    result.isStartTask = true;
    JSPFree.closeDialog(result);
}

function saveAndStartBeforeCheck(isStartTask) {
    var jso_cardData = JSPFree.getBillCardFormValue(d1_A_BillCard);
    var task_name = jso_cardData.task_name;
    if (!task_name) {
        $.messager.alert('提示', '必须填写任务名称!', 'info');
        return;
    }
    var data_dt_start = jso_cardData.data_dt_start;
    if (!jso_cardData.data_dt_start) {
        $.messager.alert('提示', '必须选择数据开始日期!', 'info');
        return;
    }
    var data_dt_end = jso_cardData.data_dt_end;
    if (!jso_cardData.data_dt_end) {
        $.messager.alert('提示', '必须选择数据结束日期!', 'info');
        return;
    }
    if (data_dt_start > data_dt_end) {
        $.messager.alert('提示', '结束时间不能小于开始时间!', 'info');
        return;
    }
    
    var jsy_datas = JSPFree.getBillListSelectDatas(d1_B_BillList);
    if (!jsy_datas || jsy_datas.length == 0) {
        JSPFree.alert("请选择报表!");
        return;
    }
    var tabNames = "";
    for (var i = 0; i < jsy_datas.length; i++) {
        tabNames += jsy_datas[i].tab_name + ",";
    }
    tabNames = tabNames.substring(0, tabNames.lastIndexOf(","));
    var result =  null;
    if ("add" === operateType) {
        result = JSPFree.doClassMethodCall("com.yusys.safe.reportCheck.service.SafeEngineBS", "saveEngineTask", {
            data_dt_start: jso_cardData.data_dt_start,
            data_dt_end: jso_cardData.data_dt_end,
            task_name: task_name,
            report_type: reportType,
            type: SafeFreeUtil.getSafeEngineType().MANUAL,
            tab_name: tabNames,
            user_org_no: str_LoginUserOrgNo,
            isStartTask: isStartTask
        });
    } else if ("modify" === operateType) {
        result = JSPFree.doClassMethodCall("com.yusys.safe.reportCheck.service.SafeEngineBS", "modifyEngineTask", {
            task_name: task_name,
            data_dt: jso_cardData.data_dt_start,
            report_type: reportType,
            tab_name: tabNames,
            task_id: taskId,
            org_no: orgNo,
            isStartTask: isStartTask
        });
    }
    return result;
}

/**
 * 取消
 * @returns
 */
function onCancel() {
    JSPFree.closeDialog({status: "close"});
}