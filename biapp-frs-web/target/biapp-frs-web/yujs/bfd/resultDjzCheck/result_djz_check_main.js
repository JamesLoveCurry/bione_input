/**
 *
 * <pre>
 * Title:金综与大集中校验主页面
 * Description
 * </pre>
 * @author miaokx
 * @version 1.00.00
 * @date 2020/8/7 10:21
 */
var org_no = "";
var org_class = "";
var data_dt = "";
function AfterInit(){
	JSPFree.createBillList("d1","/biapp-bfd/freexml/resultDjzCheck/bfd_check_result_djz_code.xml",null,{isSwitchQuery:"N",autoquery:"N"});
	d1_BillList.pagerType="1";
	var _sql = getSql();
	JSPFree.queryDataBySQL(d1_BillList, _sql);
	JSPFree.billListBindCustQueryEvent(d1_BillList, onErrorSummary);
}
var str_data_dt = "";
/*
 * 获取sql
 */
function getSql() {
	var condition = "";
	var jso_rt = JSPFree.doClassMethodCall("com.yusys.bfd.common.service.BfdValidateQueryConditionBS","getQueryCondition",{"_loginUserOrgNo" : str_LoginUserOrgNo});
	if(jso_rt.msg == "ok"){
		condition = jso_rt.condition;
	}

	// 获取最新一期的日期
	var jso_data = JSPFree.getHashVOs("SELECT max(data_dt) data_dt FROM bfd_cr_uprr_rslt where " + condition);
	if(jso_data != null && jso_data.length > 0){
		str_data_dt = jso_data[0].data_dt;
	}

	FreeUtil.loadBillQueryData(d1_BillList, {data_dt:str_data_dt});

	var _sql ="select rule_name,sum(rpt_item_val) rpt_item_val,sum(bfd_sum_val) bfd_sum_val,sum(item_diff_val) item_diff_val,curr_info,data_dt,curr_cd,rule_id,sys_id,sys_name,rpt_name,ledr_src_tab from ("+
		" SELECT s.data_dt,s.sys_id,s.sys_name,s.rpt_name,s.rule_id,r.rule_name,s.ledr_src_tab,i.curr_info,s.curr_cd,s.org_no,s.busi_type,sum(s.rpt_item_val) rpt_item_val,"+
		" sum(s.bfd_sum_val) bfd_sum_val,sum(s.item_diff_val) item_diff_val"+
		" FROM bfd_cr_uprr_rslt s LEFT JOIN bfd_cr_rule r ON s.rule_id = r.id"+
		" LEFT JOIN rpt_std_curr_info i on s.curr_cd = i.curr_cd" +
		" group by s.data_dt,s.sys_id,s.sys_name,s.rpt_name,s.ledr_src_tab,i.curr_info,s.rule_id,r.rule_name,s.curr_cd,s.org_no,s.busi_type)"+
		" where 1=1 ";
	_sql += " and " + condition;
	_sql += " and data_dt = '"+str_data_dt+"'";
	_sql += " group by data_dt,sys_id,sys_name,rpt_name,rule_id,ledr_src_tab,rule_name,curr_cd,curr_info ";
	_sql += " order by data_dt desc,sys_id,sys_name,rpt_name,rule_id,ledr_src_tab,rule_name,curr_cd,curr_info ";

	return _sql;
}

/**
 * 点击查询功能
 * @param _condition
 */
function onErrorSummary(_condition){
	var condition = "";
	var jso_rt = JSPFree.doClassMethodCall("com.yusys.east.checkrule.rulesummary.service.ValidateQueryCondition","getQueryCondition",{"_loginUserOrgNo" : str_LoginUserOrgNo});
	if(jso_rt.msg == "ok"){
		condition = jso_rt.condition;
	}

	var _sql ="select rule_name,sum(rpt_item_val) rpt_item_val,sum(bfd_sum_val) bfd_sum_val,sum(item_diff_val) item_diff_val,curr_info,data_dt,curr_cd,rule_id,sys_id,sys_name,ledr_src_tab,rpt_name from ("+
		" SELECT s.data_dt,s.sys_id,s.sys_name,s.rpt_name,s.rule_id,r.rule_name,i.curr_info,s.curr_cd,s.org_no,s.busi_type,s.ledr_src_tab,sum(s.rpt_item_val) rpt_item_val,"+
		" sum(s.bfd_sum_val) bfd_sum_val,sum(s.item_diff_val) item_diff_val"+
		" FROM bfd_cr_uprr_rslt s LEFT JOIN bfd_cr_rule r ON s.rule_id = r.id"+
		" LEFT JOIN rpt_std_curr_info i on s.curr_cd = i.curr_cd" +
		" group by s.data_dt,s.sys_id,s.sys_name,s.rpt_name,s.ledr_src_tab,i.curr_info,s.rule_id,r.rule_name,s.curr_cd,s.org_no,s.busi_type)"+
		" where 1=1 ";
	_sql += " and " + condition;

	if (_condition != null && _condition != "") {
		_sql += " and " +_condition
	}
	_sql += " group by data_dt,sys_id,sys_name,rpt_name,ledr_src_tab,rule_id,rule_name,curr_cd,curr_info ";
	_sql += " order by data_dt desc,sys_id,sys_name,rpt_name,ledr_src_tab,rule_id,rule_name,curr_cd,curr_info ";

	JSPFree.queryDataBySQL(d1_BillList, _sql);
	FreeUtil.resetToFirstPage(d1_BillList); //手工跳转到第一页
}
/**
 * 导出
 */
function tabDownload() {
	var dataDt = JSPFree.getBillQueryFormValue(d1_BillQuery).data_dt;
	var fileName = "金综与大集中校验-"+dataDt;
	JSPFree.downloadBillListDataAsExcel1(d1_BillList,fileName,"金综与大集中校验");
}

/**
 * 查看详情
 * @param _btn
 */
function viewDetail(_btn) {
	// 数据都在这个map中
	var dataset = _btn.dataset;
	var index = dataset.rowindex;
	var rows = d1_BillList.datagrid("getRows");
	// index为行号
	var row = rows[index];
	var jso_OpenPars = {};
	jso_OpenPars.data_dt = row.data_dt;
	jso_OpenPars.curr_cd = row.curr_cd;
	jso_OpenPars.sys_id = row.sys_id;
	jso_OpenPars.rule_id = row.rule_id;
	jso_OpenPars.rpt_name = row.rpt_name;
	JSPFree.openDialog("查看详情","/yujs/bfd/resultDjzCheck/result_djz_check_detail.js", 1100, 780, jso_OpenPars,function(_rtdata){});
}
/*
 * 导入大集中数据
 */
function importFrsData() {
	JSPFree.openDialog("文件上传","/yujs/bfd/resultDjzCheck/djz_import.js", 500, 350, null,function(_rtdata){
		if (_rtdata == "success") {
			JSPFree.alert("导入成功！");
		}
	});
}