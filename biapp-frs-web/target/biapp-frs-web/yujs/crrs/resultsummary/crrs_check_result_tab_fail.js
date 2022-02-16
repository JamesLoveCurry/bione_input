/**
 *
 * <pre>
 * Title: 检核结果查询
 * Description:【按数据表查询】--查看错误明细
 * </pre>
 * @author wangxy31
 * @version 1.00.00
 @date 2020年8月12日
 */

var tabName = "";
var problemcode = "";
var tabNameEn = "";
var dataDt = "";
var orgNo = "";

function AfterInit() {
	problemcode = jso_OpenPars.problemcode;
    tabName = jso_OpenPars.tabName;
    tabNameEn = jso_OpenPars.tabNameEn;
    dataDt = jso_OpenPars.dataDt;
    orgNo = jso_OpenPars.orgNo;

    var str_className = "";
    var parJson = {tabNameEn: tabNameEn}
    var jsoResult = JSPFree.doClassMethodCall("com.yusys.crrs.result.template.CrrsTabFailDetailTemplet", "getTemplet", parJson);
    if (jsoResult.status) {
        str_className = jsoResult.template;
    } else {
        $.messager.alert("提示", "缺少该表的模板，请联系管理员！", "warning");
        return;
    }
    var condition = getQueryCondition();
    JSPFree.createBillList("d1", str_className, null, {
        isSwitchQuery: "N",
        autoquery: "N",
        ishavebillquery: "N",
        list_ispagebar: "Y",
        querycontion: "data_dt='" + dataDt + "' and " + condition,
        autocondition: "data_dt='" + dataDt + "' and " + condition,
        isafterloadsetcolor: "Y",
        AfterLoadClass: "com.yusys.crrs.result.service.CrrsTabFailDetailClassLoader"
    });
    d1_BillList.pagerType = "2"; //第二种分页类型
    JSPFree.queryDataBySQL(d1_BillList, getTabTemplateSql());
}

function getQueryCondition(){
	var jso_rt = JSPFree.doClassMethodCall("com.yusys.crrs.customer.service.ValidateQueryIssuedNoCondition","getQueryCondition",{"_reportOrgNo" : orgNo});
	var condition = "";
	if(jso_rt.msg == "ok"){
		condition = jso_rt.condition;
	}

	return condition;
}

/**
 * 【查看错误明细数据】，拼接sql
 * @param str_data_dt
 * @returns
 */
function getTabTemplateSql(str_data_dt) {
    var defvalue = {"tabName": tabName, "tabNameEn": tabNameEn, "dataDt": dataDt, "orgNo": orgNo, "problemcode": problemcode};
    var jso_sql = JSPFree.doClassMethodCall("com.yusys.crrs.result.template.CrrsTabFailDetailTemplet", "getTabTemplateSql", defvalue);
    var _sql = jso_sql.sql;

    return _sql;
}
