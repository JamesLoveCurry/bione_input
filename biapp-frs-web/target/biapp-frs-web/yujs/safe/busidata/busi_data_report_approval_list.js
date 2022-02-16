/**
 *
 * <pre>
 * Title:【报表处理】【报表数据审核】
 * Description:各个报表列表
 * </pre>
 * @author baifk
 * @date 2021-02-28
 */

var tab_name = "";
var tab_name_en = "";
var report_type = "";
var str_ds = "";
var str_className = null;
var isRptNo;

function AfterInit() {
    tab_name = jso_OpenPars.tab_name;
    tab_name_en = jso_OpenPars.tab_name_en;
    report_type = jso_OpenPars.report_type;
    str_ds = jso_OpenPars.ds;

    // 此处要对【单位基本情况表】进行特殊处理
    str_className = "Class:com.yusys.safe.base.common.template.CommonBuilderTemplate.getTemplet('" + tab_name + "','" + tab_name_en + "','" + report_type + "','" + str_LoginUserOrgNo + "')";
    // 获取子表查询按钮
    // var childBtnStr = JSPFree.getChildTabBtn(tab_name_en, report_type);
    JSPFree.createBillList("d1", str_className, null, {
        list_btns: "[icon-p81]查看/view1;[icon-ok1]审核通过/onApprovalPassed;[icon-reset2]审核不通过/onApprovalNotPassed;",
        isSwitchQuery: "N",
        autoquery: "N",
        list_ischeckstyle: "Y",
        list_ismultisel: "Y",
        refWhereSQL: "approval_status != '0'",
    });

}

/**
 * 页面初始化后
 * @returns
 */
function AfterBodyLoad() {
    isRptNo = d1_BillList.templetVO.templet_option.isrptno;
}


/**
 * 查看
 * @returns
 */
function view1() {
    var json_rowdata = d1_BillList.datagrid('getSelected'); // 先得到数据

    if (json_rowdata == null || json_rowdata == undefined) {
        $.messager.alert('提示', '至少选择一条记录进行操作', 'warning');
        return;
    }

    var defaultVal = {
        type: "View",
        tabname: tab_name,
        tabnameen: tab_name_en,
        str_ds: str_ds,
        report_type: report_type
    };
    defaultVal["_BillCardData"] = json_rowdata; // 列表数据,这样设置就是从前端取数

    JSPFree.openDialog3("查看", "/yujs/safe/busidata/safe_check_data_edit.js", null, null, defaultVal, function (_rtdata) {

    }, true);
}

/**
 * 审核通过
 */
function onApprovalPassed() {
    var currentSelections = d1_BillList.datagrid('getSelections');
    if (currentSelections.length == 0) {
        $.messager.alert('提示', '必须选择一条记录进行操作', 'warning');
        return;
    }
    if (currentSelections.length == 1 && "2" === currentSelections[0].approval_status) {
        $.messager.alert('提示', '选中记录已审核通过，请重新选择！', 'warning');
        return;
    }
    var selectedRidArray = [];
    getCurrentSelectedRidArray(currentSelections, selectedRidArray);
    if (selectedRidArray.length == 0) {
        $.messager.alert('提示', '所选记录中不存在需要审核的记录，请重新选择！', 'warning');
        return;
    }
    JSPFree.confirm("提示", "你真的要审核选中的记录吗?", function (_isOK) {
        if (_isOK) {
            doApprovalReportData(selectedRidArray, "", 2);
        }
    });
}

/**
 * 审核不通过
 */
function onApprovalNotPassed() {
    var currentSelections = d1_BillList.datagrid('getSelections');
    if (currentSelections.length == 0) {
        $.messager.alert('提示', '必须选择一条记录进行操作', 'warning');
        return;
    }
    var selectedRidArray = [];
    // 获取rid
    getCurrentSelectedRidArray(currentSelections, selectedRidArray);
    if (selectedRidArray.length == 0) {
        $.messager.alert('提示', '所选记录中不存在可以审核不通过的记录，请重新选择！', 'warning');
        return;
    }
    JSPFree.confirm("提示", "你真的要提交选中的记录吗?", function (_isOK) {
        if (_isOK) {
            JSPFree.openDialog("填写审核不通过原因","/yujs/safe/fillingProcess/safe_distribute_detail_fillin_reason.js",600,300,{templetcode:"/biapp-cams/freexml/cams/cams_data_log_remark.xml"},function(_rtdata){
                if (typeof(_rtdata) == "undefined")//点取消报错，且无法关闭弹窗解决
                    return ;
                if (_rtdata.reason) {
                    doApprovalReportData(selectedRidArray, _rtdata.reason,3);
                }
            });
        }
    });
}

/**
 *  执行报表数据审核不通过
 * 
 * @param group
 */
function doApprovalReportData(selectedRidArray,reason,status) {
    var jso_rt = JSPFree.doClassMethodCall("com.yusys.safe.reportapproval.service.SafeReportApprovalBS", "safeApprovalReportData", {
        rids: selectedRidArray,
        tableNameEN: tab_name_en,
        status: status,
        reason: reason // 审核不通过原因
    });
    if (jso_rt.code == "success") {
        // 刷新当前页
        JSPFree.refreshBillListCurrPage(d1_BillList);
        $.messager.show({title: '消息提示', msg: '操作成功', showType: 'show'});
    } else {
        JSPFree.alert(jso_rt.msg);
    }
}

/**
 * 获取需要操作的数据rid
 * 
 * @param currentSelections
 * @param selectedRidArray
 */
function getCurrentSelectedRidArray(currentSelections, selectedRidArray) {
    for (var i = 0; i < currentSelections.length; i++) {
        var temp = currentSelections[i];
        var approvalStatus = temp.approval_status;
        if ("2" !=approvalStatus) {
            // 1. 审核通过时,已审核的不需要再次审核
            // 2. 审核不通过时,已审核通过的不可以审核不通过
            selectedRidArray.push(temp.rid);
        }
    }
}

/**
 * 查看子表信息
 * @param tabNmEn 字表表名
 */
function childView(childTabNmEn, childTabNm, btnNm, typeFlag) {
    var json_rowdata = d1_BillList.datagrid('getSelected'); // 先得到数据

    if (json_rowdata == null || json_rowdata == undefined) {
        $.messager.alert('提示', '至少选择一条记录进行操作', 'warning');
        return;
    }
    var defaultVal = {tabname:childTabNm,tabnameen:childTabNmEn,typeFlag:typeFlag,str_ds:str_ds,report_type:report_type,parentTabNmEn:tab_name_en,parentRid:json_rowdata.rid,data_dt:json_rowdata.data_dt,org_no:json_rowdata.org_no};
    JSPFree.openDialog3("查看"+btnNm,"/yujs/safe/busidata/safe_childtab_info_list.js",null,null,defaultVal,function(_rtdata) {},true);

}