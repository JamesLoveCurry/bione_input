var orgNo = "";
var orgType = "";
var addr = '';
function AfterInit(){
	var type = jso_OpenPars.type; // 区分是：常规，总分，监管一致性（1，2，3）
	
	var tabName = jso_OpenPars.tabName;
	var tabNameEn = jso_OpenPars.tabNameEn;
	var dataDt = jso_OpenPars.dataDt;
	var ruleId = jso_OpenPars.ruleId;
	orgNo = jso_OpenPars.orgNo;
	orgType = jso_OpenPars.org_type;
	addr = jso_OpenPars.addr;
	if (type == "1") {
		var str_className = "Class:com.yusys.east.checkresult.summary.service.FailDetailTempletBuilderByRule.getTemplet('"+tabName+"','"+tabNameEn+"','"+dataDt+"','"+ruleId+"','"+orgNo+"','"+orgType+"','"+addr+"')";
		JSPFree.createBillList("d1", str_className);
	} else if (type == "2") {
		JSPFree.createBillList("d1", "/biapp-east/freexml/east/result/east_cr_gl_rslt_CODE1.xml",null,{isSwitchQuery:"N",autoquery:"N"});
		JSPFree.queryDataByConditon2(d1_BillList, getQueryCondition(ruleId, dataDt));
		JSPFree.setBillListForceSQLWhere(d1_BillList, getQueryCondition(ruleId, dataDt));
	} else if (type == "3") {
		JSPFree.createBillList("d1", "/biapp-east/freexml/east/result/east_cr_uprr_rslt_CODE1.xml",null,{isSwitchQuery:"N",autoquery:"N"});
		JSPFree.queryDataByConditon2(d1_BillList, getQueryCondition(ruleId, dataDt));
		JSPFree.setBillListForceSQLWhere(d1_BillList, getQueryCondition(ruleId, dataDt));
	}
}

function getQueryCondition(_ruleId, _dataDt){
	// 组装busi_type字段
	var busi_type = "";
	if (orgType == "Y") {
		var org_class = "";
		var jso_org = JSPFree.getHashVOs("SELECT org_class FROM rpt_org_info where org_type='04' and is_org_report='Y' and org_no = '"+orgNo+"'");
		if(jso_org != null && jso_org.length > 0){
			org_class = jso_org[0].org_class;
		}
		if (org_class == "总行") {
			busi_type = "1_" + orgNo;
		} else {
			busi_type = "2_" + orgNo;
		}
	} else {
		busi_type = "1_";
	}
	var condition = " rule_id = '"+_ruleId+"' and data_dt = '"+_dataDt+"' and busi_type like '"+busi_type+"%'";
	
	var jso_rt = JSPFree.doClassMethodCall("com.yusys.east.checkrule.rulesummary.service.ValidateQueryCondition","getQueryCondition",{"_loginUserOrgNo" : orgNo});
	if(jso_rt.msg == "ok"){
		condition = condition + " and " + jso_rt.condition;
	}
	//获取查看明细展示的数据量
	var jso_rt1 = JSPFree.doClassMethodCall("com.yusys.east.checkresult.summary.service.FailDetailTempletBuilderByRule","getFailNumForJS",{"fail_failNum":1});
	if (jso_rt1.msg=="ok"){
		condition = condition+" and rownum<="+jso_rt1.failNum;
	}
	return condition;
}