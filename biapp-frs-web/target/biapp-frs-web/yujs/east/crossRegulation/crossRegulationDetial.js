//初始化界面,菜单配置路径是【/frs/yufreejs?js=/yujs/east/58table.js】
var data_dt = "";
var rpt_name = "";
var rule_id = "";
var curr_cd = "";
function AfterInit(){
	data_dt = jso_OpenPars.data_dt;
//	rpt_name=jso_OpenPars.rpt_name;
	rule_id = jso_OpenPars.rule_id;
	curr_cd = jso_OpenPars.curr_cd;
	
	JSPFree.createBillList("d1","/biapp-east/freexml/east/crossRegulation/east_cr_uprr_rslt_CODE3.xml",null,{isSwitchQuery:"N",ishavebillquery:"N",autoquery:"N"});
	d1_BillList.pagerType="2"; //第二种分页类型
	var _sql = getSql();
	JSPFree.queryDataBySQL(d1_BillList, _sql);
}

function getSql() {
	var condition = "";
	var jso_rt = JSPFree.doClassMethodCall("com.yusys.east.checkrule.rulesummary.service.ValidateQueryCondition","getQueryCondition",{"_loginUserOrgNo" : str_LoginUserOrgNo});
	if(jso_rt.msg == "ok"){
		condition = jso_rt.condition;
	}
	
	var _sql ="select org_no,data_dt,rpt_name,rule_id,rule_name,curr_cd,sum(rpt_item_val) rpt_item_val,sum(east_sum_val) east_sum_val,sum(item_diff_val) item_diff_val from ("+
				" SELECT s.data_dt,s.rpt_name,s.rule_id,r.rule_name,s.curr_cd,s.org_no,s.busi_type,sum(s.rpt_item_val) rpt_item_val,"+
				" sum(s.east_sum_val) east_sum_val,sum(s.item_diff_val) item_diff_val"+
				" FROM east_cr_uprr_rslt s LEFT JOIN east_cr_rule r ON s.rule_id = r.id"+
				" group by s.data_dt,s.rpt_name,s.rule_id,r.rule_name,s.curr_cd,s.org_no,s.busi_type)"+
				" where busi_type like '1_%'";
	_sql += " and " + condition;
	_sql += " and data_dt='"+data_dt+"' and rule_id='"+rule_id+"' and curr_cd='"+curr_cd+"'";
	_sql += " group by data_dt,rpt_name,rule_id,rule_name,curr_cd,org_no ";
	
	return _sql;
}