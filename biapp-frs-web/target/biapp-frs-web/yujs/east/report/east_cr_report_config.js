//初始化界面,菜单配置路径是【/frs/yufreejs?js=/yujs/east/east_cr_report_config.js】
var org_no = '';
var addr = '';
function AfterInit(){

	var jso_report_org = JSPFree.doClassMethodCall("com.yusys.east.checkrule.rulesummary.service.ValidateQueryCondition","getReportOrgNo",{_loginUserOrgNo:str_LoginUserOrgNo,report_type:"04"});
	if(jso_report_org.msg=="ok"){
		org_no = jso_report_org.data;
		addr = jso_report_org.addr;
	}

	JSPFree.createBillList("d1","/biapp-east/freexml/east/report/east_cr_report_config_CODE1.xml",null,{isSwitchQuery:"N"});
	JSPFree.queryDataByConditon2(d1_BillList, getCondition());
	JSPFree.billListBindCustQueryEvent(d1_BillList, queryCondition);
}

function queryCondition(_condition){
	var sql = "select * from east_cr_report_config where 1=1 ";
	var whereSql = getCondition();

	if(_condition){
		sql = sql+" and "+_condition;
	}else{
		sql = sql+" and "+whereSql;
	}
	d1_BillList.pagerType = "2";
	JSPFree.queryDataBySQL(d1_BillList,sql);
	FreeUtil.resetToFirstPage(d1_BillList);
}

function getCondition() {
	var condition = "";
	var jso_rt = JSPFree.doClassMethodCall("com.yusys.east.checkrule.rulesummary.service.ValidateQueryCondition","getQueryCondition",{"_loginUserOrgNo" : str_LoginUserOrgNo});
	if(jso_rt.msg == "ok"){
		condition = jso_rt.condition;
	}
	// var whereSql = '';
	// var jsn_result = JSPFree.doClassMethodCall("com.yusys.east.common.service.EastGetReportBS", "getReportList", null);
	// if (jsn_result.code == 'success') {
	// 	whereSql = jsn_result.data;
	// }
	// condition += " and " + whereSql;

	var jso_rt = JSPFree.doClassMethodCall("com.yusys.east.checkrule.rulesummary.service.ValidateQueryCondition","getAddrCondition",{"_loginUserOrgNo" : str_LoginUserOrgNo});
	if(jso_rt.code == "success"){
		condition = condition + jso_rt.data;
	}

	return condition;
}

function onBatchInsert(){
	JSPFree.openDialog("批量新增","/yujs/east/report/east_cr_report_config_inserts.js",580,500,null,function(_rtData){
		if (_rtData != null) {
			if ("dirclose" == _rtData.type) {
				return;
			}
			var str_sql = _rtData.whereSQL;  //返回的主键拼成的SQL
		       JSPFree.queryDataByConditon(d1_BillList,str_sql);
		       JSPFree.alert("批量新增数据成功!<br>当前页面数据是查询的新增数据!");
		}
	});
}