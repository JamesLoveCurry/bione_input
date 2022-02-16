//初始化界面,菜单配置路径是【/frs/yufreejs?js=/yujs/east/checkquery.js】
var org_no = "";
var org_class = "";
var bank_flag = "Y";
var addr = '';
var dataDt = "";
function AfterInit() {
	var jsy_btns = ["导出/exportData"];
	JSPFree.createTabbByBtn("d1", [ "字段校验", "总分核对" ], jsy_btns, false);

	var tabName = "";
	var orgOt = "";
	if(jso_OpenPars != '') {
		if(jso_OpenPars.data_dt!=null || jso_OpenPars.tab_name!=null || jso_OpenPars.org_ot!=null){
			tabName = jso_OpenPars.tab_name;
			dataDt = jso_OpenPars.data_dt;
			orgOt = jso_OpenPars.org_ot;
		}
	}
	
	if(orgOt==null || orgOt=="") {
		//通过当前登录人所属内部机构获取报送机构号
		var jso_report_org = JSPFree.doClassMethodCall("com.yusys.east.checkrule.rulesummary.service.ValidateQueryCondition","getReportOrgNo",{_loginUserOrgNo:str_LoginUserOrgNo,report_type:"04"});
		if(jso_report_org.msg=="ok"){
			orgOt = jso_report_org.data;
		}
	}
	// 判断是否是总行，如果是总行则不禁用，如果是其他则禁用
	var jso_org = JSPFree.getHashVOs("SELECT org_no,org_class,addr FROM rpt_org_info where org_type='04' and is_org_report='Y' and mgr_org_no = '"+str_LoginUserOrgNo+"'");
	if(jso_org != null && jso_org.length > 0){
		org_no = jso_org[0].org_no;
		org_class = jso_org[0].org_class;
		addr = jso_org[0].addr;
	}
	if (addr!=null && addr!="") {
		// 海峡特殊化：需要增加导出按钮，[icon-p67]导出/download1
		JSPFree.createBillList("d1_1","/biapp-east/freexml/east/result/east_cr_result_check_CODE1_addr.xml",null,{isSwitchQuery:"N",autoquery:"N",list_ispagebar:"Y",list_btns:"[icon-edit]编辑/update1"});//;[icon-p67]导出/download1
		d1_1_BillList.pagerType="2"; //第二种分页类型
		FreeUtil.loadBillQueryData(d1_1_BillList, {addr_ot:addr});
		var _sql = getSql(tabName, dataDt);
		JSPFree.queryDataBySQL(d1_1_BillList, _sql);
		JSPFree.billListBindCustQueryEvent(d1_1_BillList, onErrorSummary);

		// 海峡特殊化：需要增加导出按钮，[icon-p67]导出/download2
		JSPFree.createBillList("d1_2","/biapp-east/freexml/east/result/east_cr_result_check_CODE2_addr.xml",null,{isSwitchQuery:"N",autoquery:"N",list_ispagebar:"Y",list_btns:"[icon-edit]编辑/update2;[icon-p68]导出全部科目/onDownloadAllSubject"});//;[icon-p67]导出/download2
		d1_2_BillList.pagerType="2"; //第二种分页类型
		FreeUtil.loadBillQueryData(d1_2_BillList, {addr_ot1:addr});
		var _sql1 = getSql1(tabName, dataDt);
		JSPFree.queryDataBySQL(d1_2_BillList, _sql1);
		JSPFree.billListBindCustQueryEvent(d1_2_BillList, onErrorSummary1);
	}
	var isNoBank = JSPFree.doClassMethodCall("com.yusys.east.business.report.service.ExportRuleResultCheckDMO", "getBankFlag", {bank_flag:""});
	bank_flag = isNoBank.flag;
}

//页面加载结束后
function AfterBodyLoad(){
}

function getSql(_tabName, _dataDt) {	
	var condition = "";
	var jso_rt = JSPFree.doClassMethodCall("com.yusys.east.checkrule.rulesummary.service.ValidateQueryCondition","getQueryCondition",{"_loginUserOrgNo" : str_LoginUserOrgNo});
	if(jso_rt.msg == "ok"){
		condition = jso_rt.condition;
	}
	
	// 获取最新一期的日期
	var str_data_dt = "";
	if (_dataDt == "" || _dataDt == null) {
		var jso_data = JSPFree.getHashVOs("SELECT max(data_dt) data_dt FROM east_check_result_col");
		if(jso_data != null && jso_data.length > 0){
			str_data_dt = jso_data[0].data_dt;
		}
	} else {
		str_data_dt = _dataDt;
	}
	
	if (_tabName != "" && _tabName != null) {
		FreeUtil.loadBillQueryData(d1_1_BillList, {tab_name:_tabName});
		condition = condition + " and tab_name = '"+_tabName+"'";
	}
	
	FreeUtil.loadBillQueryData(d1_1_BillList, {data_dt:str_data_dt});
	
	// 如果是总行取busi_type是1_org开头的辖内
	// 如果是报送分行取busi_type是2_org开头的辖内
	// 如果是其他的取busi_type是1开头的辖内
	var _sql = "select s.*,case when fail_count <> 0 then  explain  else '' end as fail_reason from " +
		"(select t.check_no,t.data_dt,t.check_type,t.tab_name,t.col_name,t.check_rule,sum(t.fail_count) fail_count,sum(t.total_count) total_count,t.fail_type,t.fail_source,t.addr, "
		+" t.data_source,t.busi_type,  r.explain from east_check_result_col t "
		+" left join east_check_result_explain r on t.check_no || '_' ||" +str_data_dt.replace(/-/g,"")+ "|| '_'|| " + addr+"=r.rid where 1=1 and data_dt = '"+str_data_dt+"' ";

	_sql = _sql + " and addr = '"+addr+"'";
	_sql = _sql + " group by t.check_no,t.data_dt,t.check_type,t.tab_name,t.col_name,t.check_rule,t.fail_type,t.fail_source,t.addr,t.data_source,t.busi_type,r.explain) s";
	_sql += " order by fail_count desc";
	console.log("getSql:" + _sql);
	return _sql;
}

function getSql1(_tabName, _dataDt) {
	var condition = "";
	var jso_rt = JSPFree.doClassMethodCall("com.yusys.east.checkrule.rulesummary.service.ValidateQueryCondition","getQueryCondition",{"_loginUserOrgNo" : str_LoginUserOrgNo});
	if(jso_rt.msg == "ok"){
		condition = jso_rt.condition;
	}
	
	// 获取最新一期的日期
	var str_data_dt = "";
	if (_dataDt == "" || _dataDt == null) {
		var jso_data = JSPFree.getHashVOs("SELECT max(data_dt) data_dt FROM east_check_result_sum");
		if(jso_data != null && jso_data.length > 0){
			str_data_dt = jso_data[0].data_dt;
		}
	} else {
		str_data_dt = _dataDt;
	}
	
	if (_tabName != "" && _tabName != null) {
		FreeUtil.loadBillQueryData(d1_2_BillList, {tab_name:_tabName});
	}

	FreeUtil.loadBillQueryData(d1_2_BillList, {data_dt:str_data_dt});
	
	// 如果是总行取busi_type是1_org开头的辖内
	// 如果是报送分行取busi_type是2_org开头的辖内
	// 如果是其他的取busi_type是1开头的辖内
	var _sql = "select s.* ," + "case when (case when total_sum is not null then (case when ledger_sum is not null then total_sum-ledger_sum else total_sum-0 end) else " +
		"(case when ledger_sum is not null then 0-ledger_sum else 0 end) end)<> 0 then explain  else '' end as differ_desc " +
		" from ("+
		"select t.data_dt,t.check_rule,t.tab_name,t.check_key,t.subject_name,t.subject_no,sum(t.ledger_sum) ledger_sum,sum(t.total_sum) total_sum,sum(t.differ_sum)differ_sum,t.curr_cd,t.addr,t.busi_type,r.explain "
		+ " from east_check_result_sum t "
		+" left join east_check_result_explain r on t.subject_no || '_' ||t.curr_cd||'_'||" +str_data_dt.replace(/-/g,"")+ "|| '_'|| "+addr+"=r.rid where 1=1 and data_dt='"+str_data_dt+"'";
	_sql = _sql + " and addr = '"+addr+"'";
	_sql = _sql + " group by t.data_dt,t.check_rule,t.tab_name,t.check_key,t.subject_name,t.subject_no,t.curr_cd,t.addr,t.busi_type, r.explain )s";
	_sql += " order by tab_name, subject_name";
	console.log("getSql1:"+ _sql);
	return _sql;
}

function onErrorSummary(_condition) {
	var data = JSPFree.getBillQueryFormValue(d1_1_BillQuery);
	var _sql = "select s.*,case when fail_count <> 0 then  explain else '' end as fail_reason from " +
		"(select t.check_no,t.data_dt,t.check_type,t.tab_name,t.col_name,t.check_rule,sum(t.fail_count) fail_count,sum(t.total_count) total_count,t.fail_type,t.fail_source,t.addr, "
		+" t.data_source,t.busi_type,  r.explain from east_check_result_col t "
		+" left join east_check_result_explain r on t.check_no || '_' ||'" +data.data_dt.replace(/-/g,"")+ "'|| '_'|| '"+data.addr_ot+"'=r.rid where 1=1 ";

	if(_condition!=""){
		if(_condition!=""){
			_condition = _condition.replace('addr_ot', 'addr');
			_sql += "and" + _condition;
		}
	}
	_sql = _sql + " group by t.check_no,t.data_dt,t.check_type,t.tab_name,t.col_name,t.check_rule,t.fail_type,t.fail_source,t.addr,t.data_source,t.busi_type,r.explain) s";
	_sql = _sql + " order by fail_count desc";
	console.log("onErrorSummary:"+_sql);
	JSPFree.queryDataBySQL(d1_1_BillList, _sql);
	FreeUtil.resetToFirstPage(d1_1_BillList); //手工跳转到第一页
}

function onErrorSummary1(_condition) {
	var data = JSPFree.getBillQueryFormValue(d1_2_BillQuery);
	var _sql = "select s.* ," + "case when (case when total_sum is not null then (case when ledger_sum is not null then total_sum-ledger_sum else total_sum-0 end) else " +
		"(case when ledger_sum is not null then 0-ledger_sum else 0 end) end)<> 0 then explain  else '' end as differ_desc " +
		" from ("
	+ "select t.data_dt,t.check_rule,t.tab_name,t.check_key,t.subject_name,t.subject_no,sum(t.ledger_sum) ledger_sum,sum(t.total_sum) total_sum,sum(t.differ_sum)differ_sum,t.curr_cd,t.addr,t.busi_type,r.explain "
	+ " from east_check_result_sum t "
	+" left join east_check_result_explain r on t.subject_no || '_' ||t.curr_cd||'_'||'" +data.data_dt.replace(/-/g,"")+ "'|| '_'|| '"+data.addr_ot1+"'=r.rid where 1=1 ";

	if(_condition!=""){
		if(_condition!=""){
			_condition = _condition.replace('addr_ot1', 'addr');
			_sql += "and" + _condition;
		}
	}
	_sql = _sql + " group by t.data_dt,t.check_rule,t.tab_name,t.check_key,t.subject_name,t.subject_no,t.curr_cd,t.addr,t.busi_type, r.explain )s";
	_sql = _sql + " order by tab_name, subject_name";


	console.log("onErrorSummary1:"+_sql);
	JSPFree.queryDataBySQL(d1_2_BillList, _sql);
	FreeUtil.resetToFirstPage(d1_2_BillList); //手工跳转到第一页
}

//导入
function importData(){
	// 获取登录用户所在机构的
	JSPFree.openDialog("文件上传","/yujs/east/result/inputRuleResult.js", 500, 240, {type: '2'},function(_rtdata){
		if (_rtdata != null && _rtdata != "undefined") {
			JSPFree.refreshBillListCurrPage(d1_1_BillList);
			JSPFree.refreshBillListCurrPage(d1_2_BillList);
		}
	});
}

//导出
function exportData(){
	var d1Query = JSPFree.getBillQueryFormValue(d1_1_BillQuery);
	
	var _data_dt = d1Query.data_dt; // 第一个页签的日期
	var addr = d1Query.addr_ot; // 第一个页签的报送机构
	
	if (_data_dt == null || _data_dt == "") {
		JSPFree.alert("数据批次不能为空!");
		return;
	}
	if (addr == null || addr == "") {
		JSPFree.alert("地区不能为空!");
		return;
	}
	
	//获得excel第一个sheet页的列
	var jsy_cols1 = [];
	var itemVOs = d1_1_BillList.templetVO.templet_option_b;
	for(var i=0;i<itemVOs.length;i++){
		if("Y"==itemVOs[i].list_isshow){ // 如果列表显示!
			jsy_cols1.push(itemVOs[i].itemkey);
		}
	}
	//获得excel第二个sheet页的列。第二个sheet需要的sum_no字段在xml中已经去除，在DMO中配置列
	var _par = {
		addr : addr,
		data_dt : _data_dt
	};
	
	var jso_str = JSPFree.doClassMethodCall("com.yusys.east.common.GetBankNameAndDateDMO","getBankNameAndDate", _par);
	var str_data_dt = jso_str.dataDt; //获得文件名中的日期字段
	var str_bank_name = jso_str.addrName; //获得文件名中的银行名
	
	if ("Y" === bank_flag) {
		var _downloadName = str_bank_name + "-" + str_data_dt + "-" + "EAST检核结果核对情况表.xlsx";
		var jso_par = {
			FileName: "26-EastRuleResultCheck.xlsx", //指定excel模板文件名称
			DownloadName: _downloadName, //下载文件名称
			BankName: str_bank_name,
			SQL1: d1_1_BillList.CurrSQL3,
			SQL2: d1_2_BillList.CurrSQL3,
			Cols1: jsy_cols1,
			Ds: null,
			HeadRow1: "2", //表头占的行数
			HeadRow2: "3" //表头占的行数
		};
		JSPFree.downloadFile("com.yusys.east.business.report.service.ExportRuleResultCheckDMO", _downloadName, jso_par);
	} else if ("N" === bank_flag) {
		var _downloadName = str_bank_name + "-" + str_data_dt + "-" + "数据质量检核结果.xlsx";
		var jso_par = {
			FileName: "26-EastRuleResultCheck-nobank.xlsx", //指定excel模板文件名称
			DownloadName: _downloadName, //下载文件名称
			BankName: str_bank_name,
			SQL1: d1_1_BillList.CurrSQL3,
			SQL2: d1_2_BillList.CurrSQL3,
			Cols1: jsy_cols1,
			Ds: null,
			HeadRow1: "2", //表头占的行数
			HeadRow2: "3" //表头占的行数
		};
		JSPFree.downloadFile("com.yusys.east.business.report.service.ExportRuleResultCheckDMO", _downloadName, jso_par);
	}
}

//导出全部科目
function onDownloadAllSubject(){
	var d2Query = JSPFree.getBillQueryFormValue(d1_2_BillQuery);
	var _data_dt = d2Query.data_dt;
	var addr = d2Query.addr_ot1;

	if (_data_dt == null || _data_dt == "") {
		JSPFree.alert("数据批次不能为空!");
		return;
	}
	
	if (addr == null || addr == "") {
		JSPFree.alert("地区不能为空!");
		return;
	}

	var src = v_context + "/east/report/exportCSV?addr=" + addr + "&data_dt=" + _data_dt;
	var download = null;

	download = $('<iframe id="download" style="display: none;"/>');
	$('body').append(download);

	download.attr('src', src);
}

/**
 * 编辑
 * 
 * @return {[type]} [description]
 */
function update1(){
	var selectData = d1_1_BillList.datagrid('getSelected');
	var addr = JSPFree.getBillQueryFormValue(d1_1_BillQuery).addr_ot;
	if (selectData == null) {
		$.messager.alert('提示', '必须选择一条记录进行操作', 'warning');
		return;
	}

	JSPFree.openDialog("字段校验","/yujs/east/result/editResultColAddr.js", 900, 560, {checkNo:selectData.check_no,dataDt: selectData.data_dt,addr:addr,fail_reason:selectData.fail_reason},function(_rtdata){
		if (_rtdata == true) {
			$.messager.alert('提示', '保存成功！', 'info');
			JSPFree.refreshBillListCurrRow(d1_1_BillList);
		} else if (_rtdata == false) {
			$.messager.alert('提示', '保存失败！', 'info');
		}
	});
}

/**
 * 编辑
 * @return {[type]} [description]
 */
function update2(){
	var selectData = d1_2_BillList.datagrid('getSelected');
	var addr = JSPFree.getBillQueryFormValue(d1_2_BillQuery).addr_ot;
	if (selectData == null) {
		$.messager.alert('提示', '必须选择一条记录进行操作', 'warning');
		return;
	}
	
	JSPFree.openDialog("总分核对","/yujs/east/result/editResultSumAddr.js", 900, 560, {checkNo:selectData.check_no,dataDt: selectData.data_dt,addr:addr,differ_desc:selectData.differ_desc},function(_rtdata){
		if (_rtdata == true) {
			$.messager.alert('提示', '保存成功！', 'info');
			JSPFree.refreshBillListCurrRow(d1_2_BillList);
		} else if (_rtdata == false) {
			$.messager.alert('提示', '保存失败！', 'info');
		}
	});
}

/**
 * 导出：字段校验
 * @returns
 */
function download1(){
	var _data_dt = JSPFree.getBillQueryFormValue(d1_1_BillQuery).data_dt;
	if (d1_1_BillList.CurrSQL == null || "undefined" == d1_1_BillList.CurrSQL) {
		JSPFree.alert("导出失败！当前页面没有数据，请确认是否有数据或点击数据日期进行查询！");
		return;
	}
	var jsy_datas = JSPFree.getBillListAllDatas(d1_1_BillList);
	
	if(jsy_datas[0]==null || jsy_datas[0]==undefined){
		JSPFree.alert("导出失败！当前页面没有查询记录，请确认是否有数据或点击数据日期进行查询！");
		return;
	}

	var orgName = "";
	var jso_orgname = JSPFree.getHashVOs("SELECT org_nm FROM rpt_org_info where org_type='04' and is_org_report='Y' and org_no = '"+JSPFree.getBillQueryFormValue(d1_1_BillQuery).org_ot+"'");
	if(jso_orgname != null && jso_orgname.length > 0){
		orgName = jso_orgname[0].org_nm;
	}
	
	var _downloadName = "EAST数据字段校验结果表.xlsx";
	var jso_par = {
			FileName : "25-EastReportList1-haixia.xlsx", //指定excel模板文件名称
			DownloadName : _downloadName, //下载文件名称
			SQL : d1_1_BillList.CurrSQL,
			Ds : null,
			HeadRow : "3", //表头占的行数,
			Data_dt : _data_dt, //日期
			Org_nm : orgName //机构
		};
	JSPFree.downloadFile("com.yusys.east.business.report.service.ExportReportListAsExcelDMO_haixia1", _downloadName, jso_par); //
}

/**
 * 导出：总分核对
 * @returns
 */
function download2(){
	var _data_dt = JSPFree.getBillQueryFormValue(d1_2_BillQuery).data_dt;
	if (d1_2_BillList.CurrSQL == null || "undefined" == d1_2_BillList.CurrSQL) {
		JSPFree.alert("导出失败！当前页面没有数据，请确认是否有数据或点击数据日期进行查询！");
		return;
	}
	var jsy_datas = JSPFree.getBillListAllDatas(d1_2_BillList);
	
	if(jsy_datas[0]==null || jsy_datas[0]==undefined){
		JSPFree.alert("导出失败！当前页面没有查询记录，请确认是否有数据或点击数据日期进行查询！");
		return;
	}

	var orgName = "";
	var jso_orgname = JSPFree.getHashVOs("SELECT org_nm FROM rpt_org_info where org_type='04' and is_org_report='Y' and org_no = '"+JSPFree.getBillQueryFormValue(d1_1_BillQuery).org_ot1+"'");
	if(jso_orgname != null && jso_orgname.length > 0){
		orgName = jso_orgname[0].org_nm;
	}
	
	var _downloadName = "EAST数据总分核对结果表.xlsx";
	var jso_par = {
			FileName : "25-EastReportList2-haixia.xlsx", //指定excel模板文件名称
			DownloadName : _downloadName, //下载文件名称
			SQL : d1_2_BillList.CurrSQL,
			Ds : null,
			HeadRow : "3", //表头占的行数,
			Data_dt : _data_dt, //日期
			Org_nm : orgName //机构
		};
	JSPFree.downloadFile("com.yusys.east.business.report.service.ExportReportListAsExcelDMO_haixia2", _downloadName, jso_par); //
}
