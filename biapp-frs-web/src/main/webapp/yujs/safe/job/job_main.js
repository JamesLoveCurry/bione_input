/**
 *
 * <pre>
 * Title:【任务触发器】
 * Description: 任务触发器主页面
 * </pre>
 * @author miaokx
 * @version 1.00.00
 * @date 2020/5/27 10:04
 */

var tab_name = "";
var tab_name_en = "";
var str_className = "";
var str_subfix = "";

var tab_name1 = "";
var tab_name_en1= "";
var str_className1 = "";
function AfterInit() {
    // 获取路径参数
    if (jso_OpenPars != '') {
        if(jso_OpenPars.type != null) {
            str_subfix = jso_OpenPars.type;
        }
    }
    JSPFree.createSplit("d1", "上下", 400); // 页签先上下分割
    // 获取【自动任务表】常量类
    tab_name = SafeFreeUtil.getTableNames().SAFE_JOB;
    // 获取英文表名
    var jso_data = JSPFree.doClassMethodCall(
        "com.yusys.safe.base.common.service.SafeCommonBS",
        "getTabNameByEn", {tab_name:tab_name});
    tab_name_en = jso_data.tab_name_en;
    str_className = "Class:com.yusys.safe.job.template.JobBuilderTemplate.getJobTaskTemplet('" + tab_name + "','" + tab_name_en + "','" + str_subfix +"')";
    JSPFree.createBillList("d1_A", str_className, null, {list_btns:"[icon-p99]新增/onInsert;[icon-p99]修改/onUpdate;$DELETE;[icon-p59]启动/startJob;[icon-p61]停止/endJob;", isSwitchQuery:"N"});
    JSPFree.setBillCardValues("d1_A")
    // 获取【自动任务日志表】常量类
    tab_name1 = SafeFreeUtil.getTableNames().SAFE_JOB_LOG;
    // 获取英文表名
    var jso_data = JSPFree.doClassMethodCall(
        "com.yusys.safe.base.common.service.SafeCommonBS",
        "getTabNameByEn", {tab_name:tab_name1, report_type:str_subfix});
    tab_name_en1 = jso_data.tab_name_en;

    str_className1 = "Class:com.yusys.safe.job.template.JobBuilderTemplate.getJobTaskTemplet('" + tab_name1 + "','" + tab_name_en1 + "','" + str_subfix + "')";
    JSPFree.createBillList("d1_B", str_className1, null, {autoquery:"N", isSwitchQuery:"N", ishavebillquery:"N"});

    // 绑定表格选择事件,d1_A_BillList会根据命名规则已创建
    JSPFree.bindSelectEvent(d1_A_BillList, function(rowIndex, rowData) {
        var rid = rowData.rid; // 取得选中记录中的id值
        var str_sqlWhere = "jobid='" + rid + "'"; // 拼SQL条件
        JSPFree.queryDataByConditon(d1_B_BillList, str_sqlWhere); // 锁定规则表查询数据
    });
}

/**
 * 新增
 * @return {[type]} [description]
 */
function onInsert() {
    var defaultVal = {templetcode:str_className, tabname:tab_name, tabnameen:tab_name_en, reporttype:str_subfix};
    JSPFree.openDialog("新增", "/yujs/safe/job/job_add.js", 900, 560, defaultVal, function(_rtdata) {
        if (_rtdata == "OK") {
            JSPFree.alert("保存成功!");
        }

        JSPFree.queryDataByConditon(d1_A_BillList);
    }, true);
}

/**
 * 修改
 * @return {[type]} [description]
 */
function onUpdate() {
	var billList = d1_A_BillList.datagrid('getSelected');
	if (billList == null) {
		$.messager.alert('提示', '必须选择一条记录进行操作', 'warning');
		return;
	}
	
    var defaultVal = {templetcode:str_className, tabname:tab_name, tabnameen:tab_name_en, reporttype:str_subfix, rid:billList.rid};
    JSPFree.openDialog("修改", "/yujs/safe/job/job_edit.js", 900, 560, defaultVal, function(_rtdata) {
        if (_rtdata == "OK") {
            JSPFree.alert("修改成功!");
        }

        JSPFree.queryDataByConditon(d1_A_BillList);
    }, true);
}

/**
 * 启动
 * @returns
 */
function startJob(){
    var selectDatas = d1_A_BillList.datagrid('getSelections');
    if (selectDatas.length == 0) {
        $.messager.alert('提示', '至少选择一条记录进行操作', 'warning');
        return;
    } else if (selectDatas[0].activeflag == "N") {
        $.messager.alert('提示', '请先激活！', 'warning');
        return;
    } else if (selectDatas[0].jobstat == "启动") {
        $.messager.alert('提示', '任务已启动无需再启动', 'warning');
        return;
    }
    
    var jso_par = {jobName:selectDatas[0].name};
    var jso_data = JSPFree.doClassMethodCall("com.yusys.safe.job.service.JobBS", "startJob", jso_par);
    if(jso_data.code == "success") {
        $.messager.alert('提示', '启动成功');
    } else {
        $.messager.alert('提示', jso_data.msg, 'warning');
    }
    
    JSPFree.refreshBillListCurrRow(d1_A_BillList);
}

/**
 * 停止
 * @returns
 */
function endJob(){
    var selectDatas = d1_A_BillList.datagrid('getSelections');
    if (selectDatas.length == 0) {
        $.messager.alert('提示', '至少选择一条记录进行操作', 'warning');
        return;
    } else if (selectDatas[0].jobstat == "停止") {
        $.messager.alert('提示', '任务已经是停止状态', 'warning');
        return;
    }

    var jso_par = {jobName:selectDatas[0].name};
    var jso_data = JSPFree.doClassMethodCall("com.yusys.safe.job.service.JobBS", "stopJob", jso_par);
    if(jso_data.code == "success"){
        $.messager.alert('提示', '停止成功');
    } else {
        $.messager.alert('提示', jso_data.msg, 'warning');
    }
    
    JSPFree.refreshBillListCurrRow(d1_A_BillList);
}