var tabName;
var tabNameEn;
var dataDt;
var orgNo;
var reportType;
var orgClass;
function AfterInit() {
    tabName = jso_OpenPars.tabName;
    tabNameEn = jso_OpenPars.tabNameEn;
    dataDt = jso_OpenPars.dataDt;
    orgNo = jso_OpenPars.orgNo;
    reportType = jso_OpenPars.reportType;
    orgClass = jso_OpenPars.orgClass;
    var result = FreeUtil.doClassMethodCall("com.yusys.safe.checkresultquery.service.SafeCheckResultQueryBS", "isPrimaryTable", {
        tabNameEn: tabNameEn
    });
    var str_className = "Class:com.yusys.safe.checkresultquery.service.SafeTabBuilderTemplate.getTemplet('" + tabName + "','" + tabNameEn + "','" + reportType + "','" + dataDt + "','" + orgClass + "','" + orgNo + "')";
    if (result.status){
        JSPFree.createBillList("d1", str_className,null,{list_btns:"[icon-p09]查看主表信息/showPrimaryTable;"});
    } else {
        JSPFree.createBillList("d1", str_className);
    }
    d1_BillList.pagerType = "1"; //第一种分页类型
}

function showPrimaryTable(){
    var jso_OpenPars = d1_BillList.datagrid("getSelected");
    if (jso_OpenPars == null) {
        $.messager.alert('提示', '必须选择一条记录进行操作', 'warning');
        return;
    }
   
    var result = FreeUtil.doClassMethodCall("com.yusys.safe.checkresultquery.service.SafeCheckResultQueryBS", "primaryTableData", {
        tabName: tabName,
        tabNameEn: tabNameEn,
        reportType: reportType,
        rid: jso_OpenPars.rid
    });
    var parentTabnmEn = result.parentTabnmEn;
    var parentTabName = result.parentTabName;
    var par = {
        parentTabnmEn: parentTabnmEn,
        parentTabName: parentTabName,
        reportType: reportType,
        parentData: result.parentData
    }
    JSPFree.openDialog(parentTabName, "/yujs/safe/checkResultQuery/safe_check_result_tab_fail_primary_detail.js", 1100, 700, par, function (){
    });
}
