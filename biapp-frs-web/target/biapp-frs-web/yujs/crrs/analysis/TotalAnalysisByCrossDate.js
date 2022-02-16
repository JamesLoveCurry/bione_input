//初始化界面,菜单配置路径是【/frs/yufreejs?js=/yujs/crrs/xxx/xxx.js】
function AfterInit(){
JSPFree.createTabb("d1", [ "单一法人客户", "集团客户", "供应链融资客户", "同业客户", "个人客户" ]);

	JSPFree.createBillList("d1_1","/biapp-crrs/freexml/crrs/summary/crrs_single_summary_CODE2.xml",null,{list_btns:"$VIEW2;[icon-p69]导出/onExport_1;",autoquery:"N",isSwitchQuery:"N"});
	JSPFree.billListBindCustQueryEvent(d1_1_BillList, onSingleSummary);

	JSPFree.createBillList("d1_2","/biapp-crrs/freexml/crrs/summary/crrs_group_summary_CODE2.xml",null,{list_btns:"$VIEW2;[icon-p69]导出/onExport_2;",autoquery:"N",isSwitchQuery:"N"});
	JSPFree.billListBindCustQueryEvent(d1_2_BillList, onGroupSummary);

	JSPFree.createBillList("d1_3","/biapp-crrs/freexml/crrs/summary/crrs_supplychain_summary_CODE2.xml",null,{list_btns:"$VIEW2;[icon-p69]导出/onExport_3;",autoquery:"N",isSwitchQuery:"N"});
	JSPFree.billListBindCustQueryEvent(d1_3_BillList, onUpplychainSummary);

	JSPFree.createBillList("d1_4","/biapp-crrs/freexml/crrs/summary/crrs_tradeinfo_summary_CODE2.xml",null,{list_btns:"$VIEW2;[icon-p69]导出/onExport_4;",autoquery:"N",isSwitchQuery:"N"});
	JSPFree.billListBindCustQueryEvent(d1_4_BillList, onTradeinfoSummary);

	JSPFree.createBillList("d1_5","/biapp-crrs/freexml/crrs/summary/crrs_personal_summary_CODE2.xml",null,{list_btns:"$VIEW2;[icon-p69]导出/onExport_5;",autoquery:"N",isSwitchQuery:"N"});
	JSPFree.billListBindCustQueryEvent(d1_5_BillList, onPersonalSummary);
}
//单一法人客户
function onSingleSummary(_condition){
	var _sql = "select summary_name, data_dt, sum(base_count) base_count, sum(financial_count) financial_count,sum(executives_count) executives_count,sum(shareholder_ep_count) shareholder_ep_count, sum(credit_count) credit_count,sum(loan_sum) loan_sum,sum(no_credit_count) no_credit_count,sum(loan_detail_count) loan_detail_count,sum(enterprise_bond_count) enterprise_bond_count, sum(bond_account_sum) bond_account_sum,sum(enterprise_equitystake_count) enterprise_equitystake_count,sum(equitystake_account_sum) equitystake_account_sum,sum(offbalance_sa_count) offbalance_sa_count,sum(collateral_price) collateral_price,sum(guaranteed_count) guaranteed_count from crrs_single_summary where summary_type='批次'";
	if(_condition!=""){
		_sql += " and "+_condition;
	}
	_sql += " group by summary_name, data_dt order by summary_name, data_dt"
	JSPFree.queryDataBySQL(d1_1_BillList, _sql);
}
//集团客户
function onGroupSummary(_condition){
	var _sql ="select summary_name, data_dt,sum(base_count) base_count,sum(executives_count) executives_count,sum(members_count) members_count,sum(actualcontroller_count) actualcontroller_count,sum(ffiliated_groups_count) ffiliated_groups_count, sum(credit_count) credit_count, sum(loan_sum) loan_sum, sum(group_client_count) group_client_count from crrs_group_summary where summary_type='批次'";
	if(_condition!=""){
		_sql += " and "+_condition;
	}
	_sql += " group by summary_name, data_dt order by summary_name, data_dt"
	JSPFree.queryDataBySQL(d1_2_BillList, _sql);
}
//供应链融资客户
function onUpplychainSummary(_condition){
	var _up_sql  = "select summary_name, data_dt, sum(base_count) base_count, sum(members_count) members_count, sum(credit_count) credit_count, sum(loan_sum) loan_sum, sum(group_client_count) group_client_count from crrs_supplychain_summary where summary_type = '批次' ";
	var _up_sql_where = "group by summary_name, data_dt order by summary_name, data_dt";
	
	if (_condition != null && _condition != "") {
		JSPFree.queryDataBySQL(d1_3_BillList, _up_sql + " and " + _condition + _up_sql_where);
	} else {
		JSPFree.queryDataBySQL(d1_3_BillList, _up_sql + _up_sql_where);
	}
}
//同业客户
function onTradeinfoSummary(_condition){
	var _tr_sql  = "select summary_name, data_dt, sum(base_count) base_count, sum(credit_count) credit_count, sum(bank_deposit_count) bank_deposit_count, sum(buying_back_count) buying_back_count, sum(buy_out_turn_count) buy_out_turn_count, sum(selling_back_count) selling_back_count, sum(business_detail_count) business_detail_count from crrs_tradeinfo_summary where summary_type = '批次' ";
	var _tr_sql_where = "group by summary_name, data_dt order by summary_name, data_dt";
	
	if (_condition != null && _condition != "") {
		JSPFree.queryDataBySQL(d1_4_BillList, _tr_sql + " and " + _condition + _tr_sql_where);
	} else {
		JSPFree.queryDataBySQL(d1_4_BillList, _tr_sql + _tr_sql_where);
	}
}
//个人客户
function onPersonalSummary(_condition){
	var _pe_sql  = "select summary_name, data_dt, sum(base_count) base_count, sum(personal_loan_count) personal_loan_count, sum(loan_sum) loan_sum, sum(joint_debtor_count) joint_debtor_count, sum(student_loan_count) student_loan_count, sum(guaranteed_count) guaranteed_count, sum(collateral_price) collateral_price from crrs_personal_summary where summary_type = '批次' ";
	var _pe_sql_where = "group by summary_name, data_dt order by summary_name, data_dt";
	
	if (_condition != null && _condition != "") {
		JSPFree.queryDataBySQL(d1_5_BillList, _pe_sql + " and " + _condition + _pe_sql_where);
	} else {
		JSPFree.queryDataBySQL(d1_5_BillList, _pe_sql + _pe_sql_where);
	}
}

function onExport_1(){
	var str_sql = d1_1_BillList.CurrSQL;
	if (str_sql == null) {
		JSPFree.alert("当前无记录！");
		return;
	}
	JSPFree.downloadExcelBySQL("数据总量分析-跨期（单一法人客户）.xls", str_sql, "单一法人客户","批次,数据日期,基本信息条数,财务信息条数,高管及重要联系人信息条数,股东及重要关联企业信息条数,授信信息条数,贷款余额,未纳入授信业务条数,贷款明细条数,持有企业债券明细信息条数,股权账面余额,表外业务明细信息条数,押品评估价值,担保信息条数");
}

function onExport_2(){
	var str_sql = d1_2_BillList.CurrSQL;
	if (str_sql == null) {
		JSPFree.alert("当前无记录！");
		return;
	}
	JSPFree.downloadExcelBySQL("数据总量分析-跨期（集团客户）.xls", str_sql, "集团客户","批次,数据日期,基本信息条数,高管及重要联系人信息条数,实际控制人信息条数,成员名单信息条数,关联集团信息条数,授信信息条数,贷款余额,授信拆分信息条数");
}

function onExport_3(){
	var str_sql = d1_3_BillList.CurrSQL;
	if (str_sql == null) {
		JSPFree.alert("当前无记录！");
		return;
	}
	JSPFree.downloadExcelBySQL("数据总量分析-跨期（供应链融资客户）.xls", str_sql, "供应链融资客户","批次,数据日期,基本信息条数,成员名单信息条数,授信信息条数,贷款余额,授信拆分信息条数");
}

function onExport_4(){
	var str_sql = d1_4_BillList.CurrSQL;
	if (str_sql == null) {
		JSPFree.alert("当前无记录！");
		return;
	}
	JSPFree.downloadExcelBySQL("数据总量分析-跨期（同业客户）.xls", str_sql, "同业客户","批次,数据日期,基本信息条数,授信信息条数,存放同业,买入返售资产,买断式转贴现,卖出回购资产,业务明细信息条数");
}

function onExport_5(){
	var str_sql = d1_5_BillList.CurrSQL;
	if (str_sql == null) {
		JSPFree.alert("当前无记录！");
		return;
	}
	JSPFree.downloadExcelBySQL("数据总量分析-跨期（个人客户）.xls", str_sql, "个人客户","批次,数据日期,基本信息条数,业务明细信息条数,贷款余额,共同债务人信息条数,助学贷款专项指标信息条数,担保信息条数,押品评估价值");
}