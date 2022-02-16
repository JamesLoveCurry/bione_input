//初始化界面,菜单配置路径是【/frs/yufreejs?js=/yujs/crrs/xxx/xxx.js】
function AfterInit(){
	JSPFree.createBillList("d1","/biapp-crrs/freexml/crrs/rule/crrs_result_summary_CODE2.xml",null,{list_btns:"$VIEW2;[icon-p69]导出xls/onExport_1;",autoquery:"N"});
	JSPFree.billListBindCustQueryEvent(d1_BillList, onErrorSummary);
}

function onErrorSummary(_condition){
	var _sql ="select s.reportdate,c.varify_num, "+
				" count(s.rid) sure_count,"+
				" count(s.rid)/count(c.rid)+count(s.rid)+count(w.rid)+count(rw.rid) sure_rate,"+
				" count(w.rid) warn_count,"+
				" count(w.rid)/count(c.rid)+count(s.rid)+count(w.rid)+count(rw.rid) warn_rate,"+
				" count(c.rid) consistency_count,"+
				" count(c.rid)/count(c.rid)+count(s.rid)+count(w.rid)+count(rw.rid) consistency_rate,"+
				" count(rw.rid) nwarn_count,"+
				" count(rw.rid)/count(c.rid)+count(s.rid)+count(rw.rid)+count(rw.rid) nwarn_rate,"+
				" '' nconsistency_count,"+
				" '' nconsistency_rate,"+
				" count(s.rid)+count(c.rid)+count(w.rid)+count(rw.rid) error_count,"+
				" ROUND((count(s.rid)+count(c.rid)+count(w.rid)+count(rw.rid))/(select (select count(rid) from CRRS_ENT_BOND) +(select count(rid) from CRRS_ENT_CREDIT) +(select count(rid) from CRRS_ENT_EQUITYSTAKE) +(select count(rid) from CRRS_ENT_GROUP_CLIENT) +(select count(rid) from CRRS_ENT_GUARANTEED) +(select count(rid) from CRRS_ENT_LOAN) +(select count(rid) from CRRS_ENT_OFFBALANCE_SA) +(select count(rid) from CRRS_ENT_TRADE_CUSTOMERS) +(select count(rid) from CRRS_ENT_TRADE_INFO) +(select count(rid) from CRRS_GROUP_ACTUALCONTROLLER) +(select count(rid) from CRRS_GROUP_EXECUTIVES) +(select count(rid) from CRRS_GROUP_FFILIATED_GROUPS) +(select count(rid) from CRRS_GROUP_GROUP_CLIENT) +(select count(rid) from CRRS_GROUP_MEMBERS) +(select count(rid) from CRRS_PERSON_ENT_GUARANTEED) +(select count(rid) from CRRS_PERSON_JOINT_DEBTOR) +(select count(rid) from CRRS_PERSON_PERSONAL) +(select count(rid) from CRRS_PERSON_PERSONAL_LOAN) +(select count(rid) from CRRS_PERSON_STUDENT_LOAN) +(select count(rid) from CRRS_SINGLE_CORPORATION) +(select count(rid) from CRRS_SINGLE_EXECUTIVES) +(select count(rid) from CRRS_SINGLE_SHAREHOLDER_EP) count_num from dual),2) error_rate "+
 				" from crrs_result_sure s"+
				" left join crrs_result_consistency c on c.customercode=s.customercode"+
				" left join crrs_result_warn w on w.customercode=s.customercode and w.isdoubtdata='N'"+
				" left join crrs_result_warn rw on rw.customercode=s.customercode and rw.isdoubtdata='Y'";
				
	if(_condition!=""){
		_condition = _condition.replace(/reportdate/g,"s.reportdate");
		_sql += " and "+_condition;
	}
	_sql += " group by c.varify_num,s.reportdate ";
	JSPFree.queryDataBySQL(d1_BillList, _sql);
}

function onExport_1(){
//	var str_sql = d1_BillList.CurrSQL;
//	JSPFree.downloadExcelBySQL("数据差错分析-跨期.xls", str_sql, "数据差错分析-跨期");
}