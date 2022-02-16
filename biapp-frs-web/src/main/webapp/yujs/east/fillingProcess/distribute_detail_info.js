var tabName = "";
var tabNameEn = "";
var taskId = "";
var dataDt = "";
var _sql = "";
var _sql1 = "";
var d = "";
var status = "";
var isLoadTabb_2 = false;
function AfterInit(){
	taskId = jso_OpenPars.taskId;
	tabName = jso_OpenPars.tabName;
	tabNameEn = jso_OpenPars.tabNameEn;
	dataDt = jso_OpenPars.dataDt;
	status = jso_OpenPars.status;
	d = getDate(jso_OpenPars.dataDt);
	
	var jso_pars = {tabName: tabName,tabNameEn:tabNameEn,dataDt:dataDt, orgNo:str_LoginUserOrgNo};
	// 查看详情前，先确认表是否存在
	var jso_rt = JSPFree.doClassMethodCall("com.yusys.east.checkresult.summary.service.FailDetailBS", 
			"checkTab",jso_pars);
	if (jso_rt.msg != "OK") {
		JSPFree.alert("当前明细表不存在，无法查看明细数据!");
		return;
	}
	
	JSPFree.createTabb("d1", [ "未选择", "已选择" ]);

	var str_className = "Class:com.yusys.east.business.model.service.East58ModelTempletBuilder2.getTemplet('"+tabName+"','"+tabNameEn+"','"+str_LoginUserOrgNo+"')";
	JSPFree.createBillList("d1_1", str_className, null, {
		list_btns: "$VIEW;[icon-p69]添加/selectDownOn(this);[icon-p99]一键添加/selectAllDownOn",
		isSwitchQuery: "N"
	});
	d1_1_BillList.pagerType="2"; //第二种分页类型
	FreeUtil.loadBillQueryData(d1_1_BillList, {kjrq:d,cjrq:d});
	var _sql = getSql();
	JSPFree.queryDataBySQL(d1_1_BillList, _sql);
	JSPFree.billListBindCustQueryEvent(d1_1_BillList, onErrorSummary);
	JSPFree.addTabbSelectChangedListener(d1_tabb,onSelect);
}

function AfterBodyLoad(){
	$("[name=cjrq]").each(function(){
	    $(this).prev().attr('disabled',"true");
	  });

	$("[name=kjrq]").each(function(){
	    $(this).prev().attr('disabled',"true");
	  });
	
	if(status == '1' || status == '3'){
		JSPFree.setBillListBtnEnable(d1_1_BillList, "添加", false);
		JSPFree.setBillListBtnEnable(d1_1_BillList, "一键添加", false);
	}
}

var col_arry = [];
function getSql() {
	col_arry = [];
	var col_arry1 = [];
	var jso_col = JSPFree.getHashVOs("select col_name_en from east_cr_col where tab_name='" + tabName + "' and is_pk='Y' order by col_no");
	if (jso_col != null && jso_col.length > 0) {
		for (var i = 0; i < jso_col.length; i++) {
			col_arry.push(jso_col[i].col_name_en);
		}
	}
	if ("内部科目对照表" != tabName && "持卡人基础情况表" != tabName) {
		if (col_arry.indexOf("NBJGH") != -1) {
			
		} else {
			col_arry.push("NBJGH");
		}
	}
	
	var jso_col1 = JSPFree.getHashVOs("select col_name_en from east_cr_col where tab_name='" + tabName + "' and is_export='Y' order by col_no");
	if (jso_col1 != null && jso_col1.length > 0) {
		for (var i = 0; i < jso_col1.length; i++) {
			col_arry1.push(jso_col1[i].col_name_en);
		}
	}
	// 计算错误明细表：表名
	var tabNameEnZ = "Z" + d.substring(2, d.length) + tabNameEn.substring(tabNameEn.lastIndexOf("_"), tabNameEn.length);
	// 计算错误明细表：表名
	var tabNameEnZ = '';
	var jso_rt = JSPFree.doClassMethodCall("com.yusys.east.process.service.EastCrProcessBSDMO",
		"getErrorDetailTableName", {"tabNameEn": tabNameEn, dataDt: dataDt,orgNo:str_LoginUserOrgNo});
	
	if (jso_rt.msg == "OK") {
		tabNameEnZ += jso_rt.data;
	}
	_sql = "select "+col_arry1.toString()+" from (select distinct a.* from "+tabNameEn+" a,(select "+col_arry.toString()+" from "+tabNameEnZ
		+" MINUS select "+col_arry.toString()+" from "+tabNameEn+"_R) b where 1=1";
	for (var j = 0; j < jso_col.length; j++) {
		_sql += " and a." + jso_col[j].col_name_en + "=b." + jso_col[j].col_name_en;
		_sql = "select " + col_arry1.toString() + " from (select distinct a.* from " + tabNameEn + " a,(select " + col_arry.toString() + " from " + tabNameEnZ
			+ " MINUS select " + col_arry.toString() + " from " + tabNameEn + "_R) b where 1=1";
		for (var j = 0; j < jso_col.length; j++) {
			_sql += " and a." + jso_col[j].col_name_en + "=b." + jso_col[j].col_name_en;
		}
		_sql += ") where 1=1";
		if (tabNameEn == "EAST4_201_ZZHJQKMB") {
			_sql += " and kjrq='" + d + "'";
		} else {
			_sql += " and cjrq='" + d + "'";
		}
		
		var jso_rt = JSPFree.doClassMethodCall("com.yusys.east.checkrule.rulesummary.service.ValidateQueryCondition1",
			"getQueryCondition", {"_loginUserOrgNo": str_LoginUserOrgNo});
		
		if (jso_rt.msg == "ok") {
			_sql += " and " + jso_rt.condition;
		}
		
		return _sql;
	}
}

var sql = "";
function onErrorSummary(_condition){
	if (_condition.indexOf("(") != -1) {
		_condition = _condition.substring(0,  _condition.indexOf("("));
	}
	if(_condition!=""){
		if (_condition.indexOf("cjrq") != -1) {
			var str = _condition.substring(_condition.indexOf('cjrq'), _condition.indexOf('cjrq')+17);
			_condition = _condition.replace(new RegExp(str), " 1=1 ");
		} else if (_condition.indexOf("kjrq") != -1) {
			var str = _condition.substring(_condition.indexOf('kjrq'), _condition.indexOf('kjrq')+17);
			_condition = _condition.replace(new RegExp(str), " 1=1 ");
		}
		sql = _sql + " and" + _condition;
	}
	
	if (sql.lastIndexOf("and ") != -1) {
		sql = sql.substring(0,sql.lastIndexOf("and "));
	}
	JSPFree.queryDataBySQL(d1_1_BillList, sql);
	FreeUtil.resetToFirstPage(d1_1_BillList); //手工跳转到第一页
}

// 选中分发
function selectDownOn() {
	var json_data = d1_1_BillList.datagrid('getSelections');
	if (json_data == null || json_data == undefined || json_data.length == 0) {
		$.messager.alert('提示', '必须至少选择一条记录进行操作', 'warning');
		return;
	}
	
	var jsn_result = JSPFree.doClassMethodCall("com.yusys.east.process.service.EastCrProcessBSDMO", "selectDataOn", {
		jsonData: json_data,
		taskId: taskId,
		tabNameEn: tabNameEn
	});
	if (jsn_result.msg == 'OK') {
		if (sql == "" || sql == null) {
			sql = _sql;
		}
		
		JSPFree.queryDataBySQL(d1_1_BillList, sql);
		FreeUtil.resetToFirstPage(d1_1_BillList); //手工跳转到第一页
	}
}

/**
 * 一键添加
 */
function selectAllDownOn() {
	var jsn_result = JSPFree.doClassMethodCall("com.yusys.east.process.service.EastCrProcessBSDMO", "selectAllDownOn", {
		sql: getSql(),
		taskId: taskId,
		tabNameEn: tabNameEn
	});
	if (jsn_result.msg == 'OK') {
		if (sql == "" || sql == null) {
			sql = _sql;
		}
		
		JSPFree.queryDataBySQL(d1_1_BillList, sql);
		FreeUtil.resetToFirstPage(d1_1_BillList); //手工跳转到第一页
	} else {
		$.messager.alert('提示', '一键添加明细出错', 'warning');
	}
}

//取消分发
function selectDownOff() {
	var json_data = d1_2_BillList.datagrid('getSelections');
	if (json_data == null || json_data == undefined || json_data.length == 0) {
		$.messager.alert('提示', '必须至少选择一条记录进行操作', 'warning');
		return;
	}

	var jsn_result = JSPFree.doClassMethodCall("com.yusys.east.process.service.EastCrProcessBSDMO", "selectDataOff", {jsonData:json_data,colArry:col_arry.toString(),tabNameEn:tabNameEn});
	if (jsn_result.msg == 'OK') {
		JSPFree.queryDataByConditon(d1_2_BillList);
	}
}

function onSelect(_index,_title) {
	var newIndex = _index+1;

	if(newIndex==1){
		// 由于每次切换tab，都会重新查数据，导致很慢。所以先注释掉
		/*if (sql == "" || sql == null) {
			sql = _sql;
		}
		
		JSPFree.queryDataBySQL(d1_1_BillList, sql);
		FreeUtil.resetToFirstPage(d1_1_BillList); //手工跳转到第一页
*/	} else if(newIndex==2){
		if(!isLoadTabb_2){
			var str_className2 = "Class:com.yusys.east.business.model.service.East58ModelTempletBuilder3.getTemplet('"+tabName+"','"+tabNameEn+"','"+str_LoginUserOrgNo+"','_R','"+taskId+"')";
			JSPFree.createBillList("d1_2",str_className2,null,{list_btns:"$VIEW;[icon-p69]移除/selectDownOff(this)",isSwitchQuery:"N",autoquery:"Y",ishavebillquery:"N"});
			FreeUtil.loadBillQueryData(d1_2_BillList, {kjrq:d,cjrq:d});
			
			$.parser.parse('#d1_2');
//			isLoadTabb_2 = true;
			
			$("[name=cjrq]").each(function(){
			    $(this).prev().attr('disabled',"true");
			  });

			$("[name=kjrq]").each(function(){
			    $(this).prev().attr('disabled',"true");
			  });
			
			if(status == '1' || status == '3'){
				JSPFree.setBillListBtnEnable(d1_2_BillList, "移除", false);
			}
		}
	}
}

function getDate(dataDt) {
	var d = dataDt.replace(/-/g, '');
	return d;
}
