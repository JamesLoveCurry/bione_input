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
var ruleId = "";
var tabNameEn = "";
var dataDt = "";
var orgNo = "";
var orgType = "";

function AfterInit() {
	tabName = jso_OpenPars.tabName;
	ruleId = jso_OpenPars.ruleId;
	tabNameEn = jso_OpenPars.tabNameEn;
	dataDt = jso_OpenPars.dataDt;
	orgNo = jso_OpenPars.orgNo;
	orgType = jso_OpenPars.orgType;
	var parJson = {tabName: tabName, tabNameEn: tabNameEn, dataDt: dataDt, orgNo: orgNo, orgType: orgType}
	var jsoResult = JSPFree.doClassMethodCall("com.yusys.bfd.result.service.BfdResultSummaryBS","getErrorCountByTx", parJson);
	if (jsoResult.notSuperUser == 'Y') {
		JSPFree.createSplit("d1","上下", 50);
		document.getElementById("d1_A").innerHTML = "<div style=\"width:100%;text-align:left; font-size: 15px\">该用户所属条线检核记录数：" + jsoResult.all_count + "; &nbsp; &nbsp;检核失败记录数:" + jsoResult.fail_count + "</div>";
		var str_className = "Class:com.yusys.bfd.result.template.BfdTabFailDetailTemplet.getTemplet('"+tabName+"','"+tabNameEn+"','"+dataDt+"','"+orgNo +"','"+orgType +"')";
		// 判断当前是不是admin登录 如果是其他条线登录则展示条线内条数。
		JSPFree.createBillList("d1_B", str_className);
		d1_B_BillList.pagerType="2"; //第二种分页类型
		JSPFree.queryDataBySQL(d1_B_BillList, getTabTemplateSql());
	} else {
		var str_className = "Class:com.yusys.bfd.result.template.BfdTabFailDetailTemplet.getTemplet('"+tabName+"','"+tabNameEn+"','"+dataDt+"','"+orgNo +"','"+orgType +"')";
		// 判断当前是不是admin登录 如果是其他条线登录则展示条线内条数。
		JSPFree.createBillList("d1", str_className);
		d1_BillList.pagerType="2"; //第二种分页类型
		JSPFree.queryDataBySQL(d1_BillList, getTabTemplateSql());
	}
}

/**
 * 【查看错误明细数据】，拼接sql
 * @param str_data_dt
 * @returns
 */
function getTabTemplateSql(str_data_dt) {
	var _sql = "";
	var defvalue = {"tabName":tabName, "tabNameEn":tabNameEn, "dataDt" : dataDt, "orgNo" : orgNo, "orgType" : orgType};
	var jso_sql = JSPFree.doClassMethodCall("com.yusys.bfd.result.template.BfdTabFailDetailTemplet","getTabTemplateSql", defvalue);
	if (jso_sql.result == "OK") {
		_sql = jso_sql._sql;
	}
	
	return _sql;
}
