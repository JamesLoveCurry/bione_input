/**
 * 
 * <pre>
 * Title:【统计与监控】-【报表处理统计】
 * 机构权限控制：总行统计全行的数据，分行统计辖内的数据
 * Description: 计算各个报表的总数，已处理数，未处理数
 * </pre>
 * @author liangzy5 
 * @version 1.00.00
 * @date 2020年6月29日
 */
var str_subfix = "";
var tab_name = "";
var str_className = "";
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

	tab_name = SafeFreeUtil.getSafeAnalysisTab().handle;
	str_className = "Class:com.yusys.safe.analysis.template.SafeHandleAnalysisBuilderTemplate.getTemplet('"+tab_name+"')"; //传参中文表名
	JSPFree.createBillList("d1",str_className,null,{list_btns:"[icon-p31]查看/onView(this);[icon-p31]查看日志/onViewLog(this);[icon-p69]导出/exportAnalysis(this);",isSwitchQuery:"N"});
	
	JSPFree.billListBindCustQueryEvent(d1_BillList, onQuery); //绑定查询事件，即点击查询按钮触发此事件
}

/**
 * 点击查询按钮之后的触发事件，获取查询框的内容，拼接sql查询数据
 * 'data_dt' as data_dt的原因是把数据日期，机构号，业务类型也加到表单中，传到后台。rid是safe_filling_process报表下发表的任务主键id
 * 通过SafeHandleAnalysisFormulaBS，计算此数据日期下的统计信息，例如总条数。
 * @param _condition 获取到的查询框的条件
 * @returns
 */
function onQuery(_condition){
	var data_dt = _condition.substring(_condition.indexOf('data_dt')+11, _condition.length-2); //数据日期是必输项，不需要判断非空

	var jso_org = JSPFree.doClassMethodCall("com.yusys.safe.base.common.service.SafeValidateQueryConditionBS","getQueryCondition",{_loginUserOrgNo:str_LoginUserOrgNo,report_type:str_subfix});
	var condition = "";
	if(jso_org.msg=="ok"){
		condition=jso_org.condition;
	}
	if (condition!="1=1"){
		condition=condition.replace(/org_no/g,"f.org_no");
	}
	var sql = "select t.tab_name,t.tab_name_en, '"+data_dt+"' as data_dt, '"+org_no+"' as org_no, " +"'"+str_subfix+
		"' as report_type,f.rid,f.status,f.data_time from safe_cr_tab t left join safe_filling_process f on t.tab_name=f.tab_name " +
		"where  report_code is not null and t.tab_belong='"+SafeFreeUtil.getSafeTabBelongto().business+"' and t.report_type like '%"+str_subfix+"%' "+
		" and f.data_dt='"+data_dt+"' and "+condition;
	if(_condition.indexOf('tab_name')!=-1){
		sql = sql +" and "+ _condition.substring(0, _condition.indexOf('and')); //如果表名like条件不为空，则把表名也加到过滤条件里面
	}
	sql = sql +" order by tab_no";
	JSPFree.queryDataBySQL(d1_BillList, sql); //查询数据
}

/**
 * 查看详情，选择一条统计数据，查看辖内机构的明细数据
 */
function onView(_this){
	var json_data = d1_BillList.datagrid('getSelections');
	if (json_data == null || json_data == undefined || json_data.length == 0) {
		$.messager.alert('提示', '必须选择一条记录进行操作', 'warning');
		return;
	}

	var jso_Pars = {taskId:json_data[0].rid,tabNameEn:json_data[0].tab_name_en,tabName:json_data[0].tab_name,
			dataDt:json_data[0].data_dt,report_type:str_subfix};

	JSPFree.openDialog("查看详情","/yujs/safe/analyse/safe_handle_analysis_view.js",1000,600,jso_Pars,function(_rtdata) {
			});
}

/**
 * 查看日志，选择一条统计数据，查看这条数据的详细日志信息
 */
function onViewLog(_this){
	var json_data = d1_BillList.datagrid('getSelections');
	if (json_data == null || json_data == undefined || json_data.length == 0) {
		$.messager.alert('提示', '必须选择一条记录进行操作', 'warning');
		return;
	}
	
	var jso_par = {child_task_id:json_data[0].rid+"_"+org_no,report_type:str_subfix};
	JSPFree.openDialog("日志详情","/yujs/safe/fillingProcess/safe_distribute_process_detail.js",900,600,jso_par,function(_rtData){
		if (_rtData != null) {
			if ("dirclose" == _rtData.type) {
				return;
			}
		}
	});
}

/**
 * 导出报表处理统计，先查询，后导出
 * 
 * @returns
 */
function exportAnalysis(_btn) {
	if (d1_BillList.CurrSQL == null || d1_BillList.CurrSQL == "") {
		JSPFree.alert("请先查询后导出！");
		return;
	}
	
	var data_dt = JSPFree.getBillQueryFormValue(d1_BillQuery).data_dt; // 查询框中的日期
	
	var _downloadName = str_subfix+"-报表处理统计-"+data_dt+".xlsx";
	
	var jso_par = {
		    "templetcode": str_className, //模板编码
		    "SQL" : d1_BillList.CurrSQL,  //实际SQL
		  };
	
	JSPFree.downloadFile("com.yusys.safe.analysis.service.SafeHandleAnalysisExportBS", _downloadName, jso_par);
}

/**
 * 查询 总数、已处理数、未处理数的具体数据
 *
 */
function onExplore(_this){
	var rows = d1_BillList.datagrid("getRows");
	var row = rows[_this.dataset.rowindex];//index为行号
	//获取点击的列名
	var itemKey = _this.dataset.itemkey;


	var jso_par = {
		data_dt: row.data_dt,
		org_no: row.org_no,
		report_type: row.report_type,
		tab_name_en: row.tab_name_en,
		str_tab_name: row.tab_name,
		task_id: row.rid,
	}
	if ("handled_count"==itemKey){
		jso_par.xf_status= SafeFreeUtil.getHandledStatus().handled_count;
	}
	if ("nohandle_count"==itemKey){
		jso_par.xf_status= SafeFreeUtil.getHandledStatus().nohandled_count;
	}

	JSPFree.openDialog("详细信息", "/yujs/safe/analyse/safe_handle_explore.js", 900, 560,jso_par, function(_rtdata) {
	});
}