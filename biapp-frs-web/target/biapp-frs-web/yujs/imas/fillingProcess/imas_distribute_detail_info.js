/**
 * 
 * <pre>
 * Title: 【报表处理】-【报表下发】
 * Description: 报表下发：选择错误明细的页面
 * 此页面提供了报表下发的选择错误明细的功能，选择具体的错误明细数据，只有被选择了的明细数据才会被下发
 * </pre>
 * @author liangzy5 
 * @version 1.00.00
   @date 2020年8月11日
 */

var tabName = "";
var tabNameEn = "";
var _tabNameEn = ""; //不带下划线的英文表名
var taskId = "";
var dataDt = ""; //10位数据日期
var _date = ""; //8位数据日期
var _sql = "";
var status = "";
var dept_no = "";
var isLoadTabb_2 = false;
var orgNo = "";
function AfterInit(){
	taskId = jso_OpenPars.taskId;
	tabName = jso_OpenPars.tabName;
	tabNameEn = jso_OpenPars.tabNameEn;
	dept_no = jso_OpenPars.dept_no
	_tabNameEn = getTabNameEn(jso_OpenPars.tabNameEn);
	dataDt = jso_OpenPars.dataDt;
	status = jso_OpenPars.status;
	_date = getDate(jso_OpenPars.dataDt);
	orgNo = jso_OpenPars.orgNo;
	var jso_pars = {tabName: tabName,tabNameEn:tabNameEn,dataDt:dataDt, orgNo: orgNo};
	// 查看详情前，先确认表是否存在
	var jso_rt = JSPFree.doClassMethodCall("com.yusys.imas.business.service.ImasBusinessBS","checkTab",jso_pars);
	if (jso_rt.msg != "OK") {
		JSPFree.alert("当前明细表不存在，无法查看明细数据!");
		return;
	}
	
	JSPFree.createTabb("d1", [ "未选择", "已选择" ]);
	var str_className = "Class:com.yusys.imas.business.service.ImasModelTempletBuilderNoRpt.getTemplet('" + tabName + "','" + tabNameEn + "',''" + dataDt + ',' + "" + str_LoginUserOrgNo + "')";//
	JSPFree.createBillList("d1_1", str_className, null, {
		list_btns: "$VIEW;[icon-p69]添加/selectDownOn(this);[icon-p99]一键添加/selectAllDownOn",
		autoquery: "N",
		isSwitchQuery: "N",
		list_ischeckstyle: "Y",
		list_ismultisel: "Y",
		list_ispagebar: "Y",
		afterloadclass: ""
	}); //设置不需要自动查询
	// 使用第一种分页类型查询效率更高
	d1_1_BillList.pagerType = "1";
	_sql = getSql();
	FreeUtil.loadBillQueryData(d1_1_BillList, {data_dt: dataDt, tr_dt: dataDt});
	JSPFree.queryDataBySQL(d1_1_BillList, _sql);
	JSPFree.billListBindCustQueryEvent(d1_1_BillList, onDetailInfoErrorSummary);
	JSPFree.addTabbSelectChangedListener(d1_tabb, onSelect);
	
}

/**
 * 页面加载完成之后，如果下发任务的状态是处理中或者完成，则把添加按钮置灰，不允许再继续添加明细
 * @returns
 */
function AfterBodyLoad(){
	$("[name=data_dt]").each(function(){
	    $(this).prev().attr('disabled',"true");
	  });
	
	$("[name=tr_dt]").each(function(){
	    $(this).prev().attr('disabled',"true");
	  });
	
	if(status == '1' || status == '3'){
		JSPFree.setBillListBtnEnable(d1_1_BillList, "添加", false);
		JSPFree.setBillListBtnEnable(d1_1_BillList, "一键添加", false);
	}
}

/**
 * 获取sql语句，根据实际情况拼接sql语句，包含日期、机构等过滤条件
 * 存在多数据源时使用
 */
var col_arry = [];

/**
 * 获取sql语句，根据实际情况拼接sql语句，包含日期、机构等过滤条件
 * 
 */
function getSql() {
	var _sql = "";
	var col_arry1 = [];
	var jso_col = JSPFree.getHashVOs("select col_name_en from imas_cr_col where tab_name='" + tabName + "' and is_pk='Y' order by col_no");
	if (jso_col != null && jso_col.length > 0) {
		for (var i = 0; i < jso_col.length; i++) {
			col_arry.push(jso_col[i].col_name_en);
		}
	}
	
	var jso_col1 = JSPFree.getHashVOs("select col_name_en from imas_cr_col where tab_name='" + tabName + "'  order by col_no");
	if (jso_col1 != null && jso_col1.length > 0) {
		for (var i = 0; i < jso_col1.length; i++) {
			col_arry1.push(jso_col1[i].col_name_en);
		}
	}
	// 计算错误明细表：表名，如Z191031_IBANKDPST
	var result= JSPFree.doClassMethodCall("com.yusys.imas.common.template.ImasCommonTemplate","getZTable",{orgNo: orgNo, dataDt: dataDt, tableNameEn: tabNameEn});
	var tabNameEnZ = result.data;
	_sql = "select " + col_arry1.toString() + " from (select a.* from " + tabNameEn + " a,(select distinct rid from " + tabNameEnZ
		+ " where rid NOT IN (select rid from " + tabNameEn + "_R) ) b where 1=1";
	for (var j = 0; j < jso_col.length; j++) {
		_sql += " and a.rid=b.rid" ;
	}
	_sql += ") temp where 1=1 " + " and " + ImasFreeUtil.getDateColumn(tabName) + "='" + dataDt + "'";
	var jso_rt = JSPFree.doClassMethodCall("com.yusys.imas.common.service.ImasValidateQueryConditionBS", "getQueryConditionForBusi", {"_loginUserOrgNo": str_LoginUserOrgNo});
	
	if (jso_rt.msg == "ok") {
		_sql += " and " + jso_rt.condition;
	}
	if (dept_no) {
		_sql += " and dept_no='" + dept_no + "'";
	}
	return _sql;
}

function getSqlForSelectAllData() {
	var _sql = "";
	var col_arry1 = [];
	var jso_col = JSPFree.getHashVOs("select col_name_en from imas_cr_col where tab_name='" + tabName + "' and is_pk='Y' order by col_no");
	if (jso_col != null && jso_col.length > 0) {
		for (var i = 0; i < jso_col.length; i++) {
			col_arry.push(jso_col[i].col_name_en);
		}
	}
	
	var jso_col1 = JSPFree.getHashVOs("select col_name_en from imas_cr_col where tab_name='" + tabName + "'  order by col_no");
	if (jso_col1 != null && jso_col1.length > 0) {
		for (var i = 0; i < jso_col1.length; i++) {
			col_arry1.push(jso_col1[i].col_name_en);
		}
	}
	// 计算错误明细表：表名，如Z191031_IBANKDPST
	var result = JSPFree.doClassMethodCall("com.yusys.imas.common.template.ImasCommonTemplate","getZTable",{orgNo: orgNo, dataDt: dataDt, tableNameEn: tabNameEn});
	var tabNameEnZ = result.data;
	_sql = "select " + col_arry1.toString() + " from (select a.* from " + tabNameEn + " a,(select distinct rid from " + tabNameEnZ
		+ " where rid NOT IN (select rid from " + tabNameEn + "_R) ) b where 1=1";
	for (var j = 0; j < jso_col.length; j++) {
		_sql += " and a.rid=b.rid" ;
	}
	_sql += ") temp where 1=1 " + " and " + ImasFreeUtil.getDateColumn(tabName) + "='" + dataDt + "'";
	var jso_rt = JSPFree.doClassMethodCall("com.yusys.imas.common.service.ImasValidateQueryConditionBS", "getQueryConditionForBusi", {"_loginUserOrgNo": str_LoginUserOrgNo});
	
	if (jso_rt.msg == "ok") {
		_sql += " and " + jso_rt.condition;
	}
	if (dept_no) {
		_sql += " and dept_no='" + dept_no + "'";
	}
	return _sql;
}

/**
 * 获取点击查询时候的sql语句
 */
var sql = "";

function onDetailInfoErrorSummary(_condition) {
	if (_condition != "") {
		if (_condition.indexOf("data_dt") != -1) {
			var str = _condition.substring(_condition.indexOf('data_dt'), _condition.indexOf('data_dt') + 22);
			_condition = _condition.replace(new RegExp(str), " 1=1 ");
		} else if (_condition.indexOf("tr_dt") != -1) {
			var str = _condition.substring(_condition.indexOf('tr_dt'), _condition.indexOf('tr_dt') + 20);
			_condition = _condition.replace(new RegExp(str), " 1=1 ");
		}
		sql = _sql + " and" + _condition;
	}
	
	/*if (sql.lastIndexOf("and ") != -1) {
		sql = sql.substring(0, sql.lastIndexOf("and "));
	}*/
	JSPFree.queryDataBySQL(d1_1_BillList, sql);
	FreeUtil.resetToFirstPage(d1_1_BillList); //手工跳转到第一页
}

/**
 * 选中分发
 * 选择一条或多条明细数据，添加到_R表中去
 * @returns
 */
function selectDownOn() {
	var json_data = d1_1_BillList.datagrid('getSelections');
	if (json_data == null || json_data == undefined || json_data.length == 0) {
		$.messager.alert('提示', '必须至少选择一条记录进行操作', 'warning');
		return;
	}
	var jsn_result = JSPFree.doClassMethodCall("com.yusys.imas.process.service.ImasCrProcessBSDMO", "selectDataOn", {
		jsonData: json_data,
		taskId: taskId,
		tabNameEn: tabNameEn,
        deptNo: dept_no
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
 * 选择一条或多条明细数据，添加到_R表中去
 * @returns
 */
function selectAllDownOn() {
	var querySql = getSqlForSelectAllData();
	var jsn_result = JSPFree.doClassMethodCall("com.yusys.imas.process.service.ImasCrProcessBSDMO", "selectAllDataOn", {
		sql: querySql,
		taskId: taskId,
		tabNameEn: tabNameEn,
		dataDt: dataDt,
		deptNo: dept_no
	});
	if (jsn_result.msg == 'OK') {
		if (sql == "" || sql == null) {
			sql = _sql;
		}
		JSPFree.queryDataBySQL(d1_1_BillList, sql);
		FreeUtil.resetToFirstPage(d1_1_BillList); //手工跳转到第一页
		JSPFree.alert("一键添加完成!");
	}
}

/**
 * 取消分发
 * 选择一条或多条明细数据，从_R表中删除，从而取消分发
 * @returns
 */
function selectDownOff() {
	var json_data = d1_2_BillList.datagrid('getSelections');
	if (json_data == null || json_data == undefined || json_data.length == 0) {
		$.messager.alert('提示', '必须至少选择一条记录进行操作', 'warning');
		return;
	}

	var jsn_result = JSPFree.doClassMethodCall("com.yusys.imas.process.service.ImasCrProcessBSDMO", "selectDataOff", {jsonData:json_data,colArry:col_arry.toString(),tabNameEn:tabNameEn});
	if (jsn_result.msg == 'OK') {
		JSPFree.queryDataByConditon(d1_2_BillList);
	}
}

/**
 * 监听事件，监听选择的页签
 * 当选择第一个或者第二个页签的时候，动态加载数据
 * @param _index
 * @param _title
 * @returns
 */
function onSelect(_index,_title) {
	var newIndex = _index+1;

	if(newIndex==1){
		// 由于每次切换tab，都会重新查数据，导致很慢。所以先注释掉
		if (sql == "" || sql == null) {
			sql = _sql;
		}
		d1_1_BillList.pagerType = "1";
		JSPFree.queryDataBySQL(d1_1_BillList, sql);
		FreeUtil.resetToFirstPage(d1_1_BillList); //手工跳转到第一页
	} else if(newIndex==2){
		if(!isLoadTabb_2){
			var str_className2 = "Class:com.yusys.imas.business.service.ImasModelTempletBuilderForOrg.getTemplet('"+tabName+"','"+tabNameEn+"','"+str_LoginUserOrgNo+"','_R','"+taskId+"')";
			JSPFree.createBillList("d1_2",str_className2,null,{list_btns:"$VIEW;[icon-p69]移除/selectDownOff(this)",isSwitchQuery:"N",autoquery:"Y",ishavebillquery:"N", list_ischeckstyle:"Y", list_ismultisel:"Y", list_ispagebar:"Y"});
			FreeUtil.loadBillQueryData(d1_2_BillList, {data_dt:dataDt,tr_dt:dataDt});
			$.parser.parse('#d1_2');
//			isLoadTabb_2 = true;
			
			$("[name=data_dt]").each(function(){
			    $(this).prev().attr('disabled',"true");
			  });
			
			$("[name=tr_dt]").each(function(){
			    $(this).prev().attr('disabled',"true");
			  });
			
			if(status == '1' || status == '3'){
				JSPFree.setBillListBtnEnable(d1_2_BillList, "移除", false);
			}
		}
	}
}

/**
 * 去掉横杠，转换10位数据日期为8位
 * @param dataDt
 * @returns
 */
function getDate(dataDt) {
	var _date = dataDt.replace(/-/g, '');
	return _date;
}

/**
 * 去掉下划线，转换英文表名为一串英文字母
 * @param tabNameEn
 * @returns
 */
function getTabNameEn(tabNameEn) {
	var _tabNameEn = tabNameEn.replace(/_/g, '');
	return _tabNameEn;
}