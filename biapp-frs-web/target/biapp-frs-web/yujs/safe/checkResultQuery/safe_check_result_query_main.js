/**
 * 检核结果查看
 * @author baifk
 *
 * @type {string}
 */
var reportType = "";
var report_type = "";

function AfterInit() {
    if (jso_OpenPars != '') {
        if (jso_OpenPars.type != null) {
            reportType = jso_OpenPars.type;
            report_type = jso_OpenPars.type;
        }
    }
    var org_class = SafeFreeUtil.getOrgClass(str_LoginUserOrgNo, reportType); //获取机构层级
    
    if (SafeFreeUtil.getSafeOrgClass().zh === org_class) {
        var _templetCode = "/biapp-safe/freexml/common/safe_check_result_query_tab_zh.xml";
    } else {
        var _templetCode = "/biapp-safe/freexml/common/safe_check_result_query_tab.xml";
    }
    // 按数据表查询
    JSPFree.createBillList("d1", _templetCode, null, {
        list_btns: "[icon-p68]导出/tableExportToExcel;",
        isSwitchQuery: "N",
        autoquery: "N",
        list_ispagebar: "Y"
    });
    d1_BillList.pagerType = "2"; //第二种分页类型
    JSPFree.queryDataBySQL(d1_BillList, getQueryDataTabSQl() + " order by reportdate desc ");
    JSPFree.billListBindCustQueryEvent(d1_BillList, onDataTableQuery);
}

/**
 * 页面加载结束后
 * @returns
 */
function AfterBodyLoad() {
    
    var result = JSPFree.doClassMethodCall("com.yusys.safe.checkresultquery.service.SafeCheckResultQueryBS",
        "getTabNameInfoByReportType", {reportType: reportType, isParentTable: false});
    if ($("#tablename") && $("#tablename").length > 0) {
        $('#tablename').combobox({
            panelHeight: 'auto',
            panelMaxHeight: 150,
            valueField: 'id',
            textField: 'name',
            data: JSON.parse(JSON.stringify(result.value))
        });
    }
    var result = JSPFree.doClassMethodCall("com.yusys.safe.checkresultquery.service.SafeCheckResultQueryBS",
        "getTabNameInfoByReportType", {reportType: reportType, isParentTable: true});
    if ($("#main_tablename") && $("#main_tablename").length > 0) {
        $('#main_tablename').combobox({
            panelHeight: 'auto',
            panelMaxHeight: 150,
            valueField: 'id',
            textField: 'name',
            data: JSON.parse(JSON.stringify(result.value))
        });
    }
    
}


function getOrgClass() {
    var orgClassVO = JSPFree.getHashVOs("select trim(ORG_CLASS) as org_class from rpt_org_info where ORG_TYPE = 'BOP' and MGR_ORG_NO = '" + str_LoginUserOrgNo + "'");
    if (orgClassVO && orgClassVO.length > 0) {
        var orgClass = orgClassVO[0].org_class;
        return orgClass;
    }
    return "";
}
/**
 * 获取按数据表查询的sql
 * @returns {string}
 */
function getQueryDataTabSQl() {
    return getSqlSelectPartForTab()
}


/**
 * 按数据表查询
 * 查询按钮
 * @param _condition
 */
function onDataTableQuery(_condition) {
    var org_class = SafeFreeUtil.getOrgClass(str_LoginUserOrgNo, reportType); //获取机构层级
    
    if (SafeFreeUtil.getSafeOrgClass().zh === org_class && $('#org_no').val()) {
        var sql = " select * from  v_safe_check_result_query_tab " + "  where  report_type = '" + reportType + "'";
        if (_condition) {
            sql += " and " + _condition
        }
        JSPFree.queryDataBySQL(d1_BillList, sql + " order by reportdate desc ");
        FreeUtil.resetToFirstPage(d1_BillList);
    } else {
        var sql = getSqlSelectPartForTab();
        if (_condition) {
            sql += " and " + _condition
        }
        JSPFree.queryDataBySQL(d1_BillList, sql + " order by reportdate desc ");
        FreeUtil.resetToFirstPage(d1_BillList);
    }
   
}

function getOrgClassAndRptOrgNo() {
    var orgClassVO = JSPFree.getHashVOs("select trim(ORG_CLASS) as org_class,org_no from rpt_org_info where ORG_TYPE = '"+report_type+"' and MGR_ORG_NO = '" + str_LoginUserOrgNo + "'");
    var array = new Array();
    if (orgClassVO && orgClassVO.length > 0) {
        var orgClass = orgClassVO[0].org_class;
        var rptOrgNo = orgClassVO[0].org_no;
        array.push(orgClass);
        array.push(rptOrgNo);
        return array;
    }
    return "";
}

/**
 * 按数据表查询
 * @returns {string}
 */
function getSqlSelectPartForTab() {
    var sql = " select * from ";
    var org_class = SafeFreeUtil.getOrgClass(str_LoginUserOrgNo, reportType); //获取机构层级
    
    if (SafeFreeUtil.getSafeOrgClass().zh === org_class) {
        sql += " v_safe_check_result_query_zh"
    } else {
        sql += " v_safe_check_result_query_tab";
    }
    sql += "  where  report_type = '" + reportType + "'";
    var array = getOrgClassAndRptOrgNo();
    if ("总行" != array[0]) {
        sql += " and org_no = '" + array[1] + "'";
    }
    return sql;
}

/**
 * 按数据表查询导出excel
 */
function tableExportToExcel() {
    if (d1_BillList.CurrSQL3 == null || "undefined" == d1_BillList.CurrSQL3) {
        JSPFree.alert("当前无记录！");
        return;
    }
    JSPFree.downloadBillListCurrSQL3AsExcel(null, d1_BillList);
}

/**
 * 查看明细
 * @param _btn
 */
function viewFailDetailByTab(_btn) {
    
    var dataset = _btn.dataset;
    var index = dataset.rowindex;
    var rows = d1_BillList.datagrid("getRows");
    var row = rows[index];
    
    if ("0" == row.fail_count) {
        JSPFree.alert("当前错误记录数为0，无法查看错误明细数据！");
        return;
    }
    var org_class = SafeFreeUtil.getOrgClass(str_LoginUserOrgNo, reportType); //获取机构层级
    var jso_OpenPars = {
        tabName: row.tablename,
        tabNameEn: row.tablename_en,
        dataDt: row.reportdate,
        orgNo: row.org_no,
        reportType: reportType,
        orgClass: org_class
    };
    JSPFree.openDialog("错误明细", "/yujs/safe/checkResultQuery/safe_check_result_tab_fail_detail.js", 950, 500, jso_OpenPars, function (_rtdata) {
    });
    
}

function viewFailDetailByRule(_btn) {
    debugger
    var dataset = _btn.dataset;
    var index = dataset.rowindex;
    var rows = d1_2_BillList.datagrid("getRows");
    var row = rows[index];
    
    if ("0" == row.fail_count) {
        JSPFree.alert("当前错误记录数为0，无法查看错误明细数据！");
        return;
    }
    
    var jso_OpenPars = {
        tabName: row.tablename_rule,
        tabNameEn: row.tablename_en,
        dataDt: row.reportdate_rule,
        orgNo: row.org_no_rule,
        reportType: reportType,
        ruleRid: row.rule_rid
    };
    JSPFree.openDialog("错误明细", "/yujs/safe/checkResultQuery/safe_check_result_rule_fail_detail.js", 950, 500, jso_OpenPars, function (_rtdata) {
    });
    
}

/**
 * 校验
 * @returns
 */
function validateTask() {
    var pars = {reportType: reportType};
    JSPFree.openDialog("选择日期", "/yujs/safe/reportCheck/report_check_date.js", 700, 560, pars, function (_rtdata) {
        if (_rtdata != null && _rtdata.code == "finish") {
            JSPFree.alert("校验完成！");
        }
    });
}