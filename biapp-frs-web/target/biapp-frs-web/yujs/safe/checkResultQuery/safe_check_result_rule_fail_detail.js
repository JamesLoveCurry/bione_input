var tabName;
var tabNameEn;
var dataDt;
var orgNo;
var reportType;
var ruleRid;

function AfterInit() {
    tabName = jso_OpenPars.tabName;
    tabNameEn = jso_OpenPars.tabNameEn;
    dataDt = jso_OpenPars.dataDt;
    orgNo = jso_OpenPars.orgNo;
    reportType = jso_OpenPars.reportType;
    ruleRid = jso_OpenPars.ruleRid;
    
    var str_className = "Class:com.yusys.safe.checkresultquery.service.SafeRuleBuilderTemplate.getTemplet(" +
        "'" + tabName + "','" + tabNameEn +
        "','" + reportType +
        "','" + dataDt +
        "','" + orgNo +
        "','" + ruleRid + "')";
    JSPFree.createBillList("d1", str_className);
    d2_BillList.pagerType = "1"; //第一种分页类型
}

