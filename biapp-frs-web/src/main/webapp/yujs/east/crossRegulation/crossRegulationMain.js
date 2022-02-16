//初始化界面,菜单配置路径是【/frs/yufreejs?js=/yujs/east/58table.js】
function AfterInit(){
	JSPFree.createBillList("d1","/biapp-east/freexml/east/crossRegulation/east_cr_uprr_rslt_CODE2.xml",null,{isSwitchQuery:"N",autoquery:"N"});
	d1_BillList.pagerType="2"; //第二种分页类型
	var _sql = getSql();
	JSPFree.queryDataBySQL(d1_BillList, _sql);
	JSPFree.billListBindCustQueryEvent(d1_BillList, onErrorSummary);
}

var str_data_dt = "";
function getSql() {
	var condition = "";
	var jso_rt = JSPFree.doClassMethodCall("com.yusys.east.checkrule.rulesummary.service.ValidateQueryCondition","getQueryCondition",{"_loginUserOrgNo" : str_LoginUserOrgNo});
	if(jso_rt.msg == "ok"){
		condition = jso_rt.condition;
	}
	
	// 获取最新一期的日期
	var jso_data = JSPFree.getHashVOs("SELECT max(data_dt) data_dt FROM east_cr_uprr_rslt where " + condition);
	if(jso_data != null && jso_data.length > 0){
		str_data_dt = jso_data[0].data_dt;
	}
	
	FreeUtil.loadBillQueryData(d1_BillList, {data_dt:str_data_dt});
	
	var _sql ="select rule_name,sum(rpt_item_val) rpt_item_val,sum(east_sum_val) east_sum_val,sum(item_diff_val) item_diff_val,curr_info,data_dt,curr_cd,rule_id from ("+
				" SELECT s.data_dt,s.rpt_name,s.rule_id,r.rule_name,i.curr_info,s.curr_cd,s.org_no,s.busi_type,sum(s.rpt_item_val) rpt_item_val,"+
				" sum(s.east_sum_val) east_sum_val,sum(s.item_diff_val) item_diff_val"+
				" FROM east_cr_uprr_rslt s LEFT JOIN east_cr_rule r ON s.rule_id = r.id"+
				" LEFT JOIN rpt_std_curr_info i on s.curr_cd = i.curr_cd" +
				" group by s.data_dt,s.rpt_name,i.curr_info,s.rule_id,r.rule_name,s.curr_cd,s.org_no,s.busi_type)"+
				" where busi_type like '1_%'";
	_sql += " and " + condition;
	_sql += " and data_dt = '"+str_data_dt+"'";
	_sql += " group by data_dt,rpt_name,rule_id,rule_name,curr_cd,curr_info ";
	_sql += " order by data_dt desc,rpt_name,rule_id,rule_name,curr_cd,curr_info ";
	
	return _sql;
}

function onErrorSummary(_condition){
	var condition = "";
	var jso_rt = JSPFree.doClassMethodCall("com.yusys.east.checkrule.rulesummary.service.ValidateQueryCondition","getQueryCondition",{"_loginUserOrgNo" : str_LoginUserOrgNo});
	if(jso_rt.msg == "ok"){
		condition = jso_rt.condition;
	}
	
	var _sql ="select rule_name,sum(rpt_item_val) rpt_item_val,sum(east_sum_val) east_sum_val,sum(item_diff_val) item_diff_val,curr_info,data_dt,curr_cd,rule_id from ("+
			" SELECT s.data_dt,s.rpt_name,s.rule_id,r.rule_name,i.curr_info,s.curr_cd,s.org_no,s.busi_type,sum(s.rpt_item_val) rpt_item_val,"+
			" sum(s.east_sum_val) east_sum_val,sum(s.item_diff_val) item_diff_val"+
			" FROM east_cr_uprr_rslt s LEFT JOIN east_cr_rule r ON s.rule_id = r.id"+
			" LEFT JOIN rpt_std_curr_info i on s.curr_cd = i.curr_cd" +
			" group by s.data_dt,s.rpt_name,i.curr_info,s.rule_id,r.rule_name,s.curr_cd,s.org_no,s.busi_type)"+
			" where busi_type like '1_%'";
	_sql += " and " + condition;
	
	if (_condition != null && _condition != "") {
		_sql += " and " +_condition
	}
	_sql += " group by data_dt,rpt_name,rule_id,rule_name,curr_cd,curr_info ";
	_sql += " order by data_dt desc,rpt_name,rule_id,rule_name,curr_cd,curr_info ";
	
	JSPFree.queryDataBySQL(d1_BillList, _sql);
	FreeUtil.resetToFirstPage(d1_BillList); //手工跳转到第一页
}

// 导出
function tabDownload() {
	if (d1_BillList.CurrSQL3 == null || "undefined" == d1_BillList.CurrSQL3) {
		JSPFree.alert("当前无记录！");
		return;
	}
	
	var _str_data = JSPFree.getBillQueryFormValue(d1_BillQuery).data_dt;
	var data = _str_data.replace(/-/g, '');
	
	var fileName = "跨监管一致性-1104与EAST校验-"+data+".xls";
//	JSPFree.downloadExcelBySQL(fileName, d1_BillList.CurrSQL3, "跨监管一致性-1104与EAST校验","规则名称,1104指标值,EAST汇总值,差值,币种,数据日期");
	JSPFree.downloadBillListDataAsExcel1(d1_BillList,fileName,"跨监管一致性-1104与EAST校验");
}

/**
 * 导入
 *
 * @returns
 */
function tabImport() {
	JSPFree.openDialog("文件上传", "/yujs/east/crossRegulation/inputData.js", 500, 240, null,
	function(_rtdata) {
		if (_rtdata != null && _rtdata != "undefined") {
			if(_rtdata.type == "dirclose"){ //直接关闭窗口

			}else{
				JSPFree.alert(_rtdata.msg);
				// JSPFree.queryDataByConditon(d1_BillList, _rtdata.whereSQL); // 立即查询刷新数据
			}
		}
	});
}

// 查看详情
function viewDetail(_btn) {
	var dataset = _btn.dataset;  //数据都在这个map中
	var index = dataset.rowindex;
	var rows = d1_BillList.datagrid("getRows");
	var row = rows[index];//index为行号

	var jso_OpenPars = {};
	jso_OpenPars.data_dt = row.data_dt;
	jso_OpenPars.rpt_name = row.rpt_name;
	jso_OpenPars.rule_id = row.rule_id;
	jso_OpenPars.curr_cd = row.curr_cd;

	JSPFree.openDialog("查看详情","/yujs/east/crossRegulation/crossRegulationDetial.js", 900, 600, jso_OpenPars,function(_rtdata){});
}