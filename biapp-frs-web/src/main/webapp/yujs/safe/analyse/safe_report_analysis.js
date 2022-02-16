/**
 * 
 * <pre>
 * Title:【统计与监控】-【报送情况统计】
 * 机构权限控制：总行统计全行的数据，分行统计辖内的数据
 * Description: 计算各个报表的总数，上报数，拦截数
 * SafeReportAnalysisBuilderTemplate.getTemplet() 参数是中文表名和type=1，主界面。后面会继续用到这个方法，是数据下钻的页面，传参数2
 * </pre>
 * @author liangzy5 
 * @version 1.00.00
 * @date 2020年6月29日
 */
var str_subfix = "";
var tab_name = "";
var str_className= "";
var org_no = "";
function AfterInit(){
	// 获取路径参数
	if (jso_OpenPars != '') {
		if(jso_OpenPars.type != null){
			str_subfix = jso_OpenPars.type; //获取后缀
		}
	}
	
	//通过当前登录人所属内部机构获取报送机构号
	var jso_report_org = JSPFree.doClassMethodCall("com.yusys.safe.base.common.service.SafeValidateQueryConditionBS","getReportOrgNoCondition",{_loginUserOrgNo:str_LoginUserOrgNo,report_type:str_subfix});
	if(jso_report_org.msg=="ok"){
		org_no = jso_report_org.data;
	}
	
	tab_name = SafeFreeUtil.getSafeAnalysisTab().report;
	str_className = "Class:com.yusys.safe.analysis.template.SafeReportAnalysisBuilderTemplate.getTemplet('" + tab_name + "','1')"; //传参中文表名+type=1，主界面
	JSPFree.createBillList("d1", str_className, null, {
		list_btns: "[icon-p69]导出/exportAnalysis(this);[icon-p31]查看分行统计/showDetail(this);",
		isSwitchQuery: "N"
	});
	
	JSPFree.billListBindCustQueryEvent(d1_BillList, onQuery); //绑定查询事件，即点击查询按钮触发此事件
}

/**
 * 点击查询按钮之后的触发事件，获取查询框的内容，拼接sql查询数据
 * 'data_dt' as data_dt的原因是把数据日期，机构号，业务类型也加到表单中，传到后台
 * 通过SafeReportAnalysisFormulaBS，计算此数据日期下的统计信息，例如总条数。
 * @param _condition 获取到的查询框的条件
 * @returns
 */
function onQuery(_condition){
	var data_dt = _condition.substring(_condition.indexOf('data_dt')+11, _condition.length-2); //数据日期是必输项，不需要判断非空
	var sql = "select tab_name,tab_name_en, '"+data_dt+"' as data_dt, '"+org_no+"' as org_no, " +"'"+str_subfix+
		"' as report_type from safe_cr_tab where report_code is not null and tab_belong='"+SafeFreeUtil.getSafeTabBelongto().business+"' and report_type like '%"+str_subfix+"%' ";
	if(_condition.indexOf('tab_name')!=-1){
		sql = sql +" and "+ _condition.substring(0, _condition.indexOf('and')); //如果表名like条件不为空，则把表名也加到过滤条件里面
	}
	sql = sql +" order by tab_no";
	JSPFree.queryDataBySQL(d1_BillList, sql); //查询数据
}

/**
 * 点击页面上面的【总数】，数据下钻
 * 下钻方式：根据点击的这条数据，查看这条数据对应的报表名称+子级机构的详细情况
 * 总行下钻分行的数据，分行下钻支行的数据
 */
function showDetail(_this){
	var json_data = d1_BillList.datagrid('getSelections');
	if (json_data.length == 0) {
		$.messager.alert('提示', '至少选择一条记录进行操作', 'warning');
		return;
	} else if (json_data.length > 1) {
		$.messager.alert('提示', '只能选择一条记录进行操作', 'warning');
		return;
	}
	var jso_par = {
			data_dt: json_data[0].data_dt,
			org_no: json_data[0].org_no,
			report_type: json_data[0].report_type,
			tab_name_en: json_data[0].tab_name_en,
			str_tab_name: tab_name
	}
	
	JSPFree.openDialog("详细信息", "/yujs/safe/analyse/safe_report_explore.js", 900, 560,jso_par, function(_rtdata) {
			});
}
/*
* 查看明细数据
 */
function onExplore(_this) {
	var rows = d1_BillList.datagrid("getRows");
	var row = rows[_this.dataset.rowindex];//index为行号
	var itemKey = _this.dataset.itemkey;
	var jso_par = {
		data_dt: row.data_dt,
		org_no: row.org_no,
		report_type: row.report_type,
		tab_name_en: row.tab_name_en,
		str_tab_name: row.tab_name
	}
	if (itemKey == 'report_count') {
		jso_par.data_status = SafeFreeUtil.getCheckStatus().CHECK;
	} else if (itemKey == 'block_count') {
		jso_par.data_status = SafeFreeUtil.getCheckStatus().NO_CHECK;
	}
	JSPFree.openDialog("查看详情","/yujs/safe/analyse/safe_report_analysis_view.js",1000,600,jso_par,function(_rtdata) {
	});
}
/**
 * 导出报送情况统计，先查询，后导出
 * 
 * @returns
 */
function exportAnalysis(_btn) {
	if (d1_BillList.CurrSQL == null || d1_BillList.CurrSQL == "") {
		JSPFree.alert("请先查询后导出！");
		return;
	}
	
	var data_dt = JSPFree.getBillQueryFormValue(d1_BillQuery).data_dt; // 查询框中的日期
	
	var _downloadName = str_subfix+"-报送情况统计-"+data_dt+".xlsx";
	
	var jso_par = {
		    "templetcode": str_className, //模板编码
		    "SQL" : d1_BillList.CurrSQL,  //实际SQL
		  };
	
	JSPFree.downloadFile("com.yusys.safe.analysis.service.SafeReportAnalysisExportBS", _downloadName, jso_par);
}