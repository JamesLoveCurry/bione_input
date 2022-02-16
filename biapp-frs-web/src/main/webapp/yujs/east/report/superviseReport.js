//初始化界面,菜单配置路径是【/frs/yufreejs?js=/yujs/east/58table.js】
var fileName = "";
var org_class = "";
var org_no = "";
function AfterInit(){
	//通过当前登录人所属内部机构获取报送机构号
	var jso_report_org = JSPFree.doClassMethodCall("com.yusys.east.checkrule.rulesummary.service.ValidateQueryCondition","getReportOrgNo",{_loginUserOrgNo:str_LoginUserOrgNo,report_type:"04"});
	if(jso_report_org.msg=="ok"){
		org_no = jso_report_org.data;
	}
	
	JSPFree.createBillList("d1","/biapp-east/freexml/east/report/east_cr_report_CODE1.xml",null,{isSwitchQuery:"N",autoquery:"N"});
	JSPFree.queryDataByConditon2(d1_BillList, getCondition());
	JSPFree.billListBindCustQueryEvent(d1_BillList, queryCondition);
	// FreeUtil.loadBillQueryData(d1_BillList, {org_no:org_no});
	
	// 判断是否是总行，如果是总行则不禁用，如果是其他则禁用
	var jso_org = JSPFree.getHashVOs("SELECT org_no,org_class FROM rpt_org_info where org_type='04' and is_org_report='Y' and org_no = '"+org_no+"'");
	if(jso_org != null && jso_org.length > 0){
		org_class = jso_org[0].org_class;
	}
}

//页面加载结束后
function AfterBodyLoad(){
	// 判断是否是总行，如果是总行则不禁用，如果是其他则禁用
	if (org_class == '总行') {
		JSPFree.setBillQueryItemEditable("org_no","下拉框",true);
	} else if (org_class == '分行') {
		JSPFree.setBillQueryItemEditable("org_no","下拉框",false);
	} else {
		
	}
}

function queryCondition(_condition){
	var sql = "select * from east_cr_report where 1=1 ";
	var whereSql = getCondition();

	if(_condition){
		sql = sql+" and "+_condition;
	}else{
		sql = sql+" and "+whereSql;
	}
	d1_BillList.pagerType = "2";
	JSPFree.queryDataBySQL(d1_BillList, sql);
	FreeUtil.resetToFirstPage(d1_BillList);
}

function getCondition() {
	var condition = "";
	var jso_rt = JSPFree.doClassMethodCall("com.yusys.east.checkrule.rulesummary.service.ValidateQueryCondition","getQueryCondition",{"_loginUserOrgNo" : str_LoginUserOrgNo});
	if(jso_rt.msg == "ok"){
		condition = jso_rt.condition;
	}
	var whereSql = '';
	var jsn_result = JSPFree.doClassMethodCall("com.yusys.east.common.service.EastGetReportBS", "getReportList", null);
	if (jsn_result.code == 'success') {
		whereSql = jsn_result.data;
	}
	condition += " and " + whereSql;

	var jso_rt = JSPFree.doClassMethodCall("com.yusys.east.checkrule.rulesummary.service.ValidateQueryCondition","getAddrCondition",{"_loginUserOrgNo" : str_LoginUserOrgNo});
	if(jso_rt.code == "success"){
		condition = condition + jso_rt.data;
	}

	return condition;
}

//创建任务
function onCreateReport(){
  //alert("创建任务");
  var jso_par = {};  //参数
  JSPFree.openDialog("创建任务","/yujs/east/report/east_cr_report_create.js",800,500,jso_par,function(_rtdata){
	  if (_rtdata != null) { 
		  if ("dirclose" == _rtdata.type) {
				return;
		  }
		  JSPFree.queryDataByConditon(d1_BillList,_rtdata.wheresql);  //立即查询数据
		  $.messager.alert('提示', '创建成功!', 'warning');
	  }
  });
}

//删除（处理中，不能删除）
function onDeleteReport(){
	var selectDatas = d1_BillList.datagrid('getSelections');
	if (selectDatas.length == 0) {
		$.messager.alert('提示', '至少选择一条记录进行操作', 'warning');
		return;
	}
	
	var tab_name = "";
	for(var i=0; i<selectDatas.length; i++){
		if (selectDatas[i].status.match("处理中")) {
			tab_name += selectDatas[i].tab_name + ","
		}
	}
	if (tab_name != null && tab_name != "") {
		$.messager.alert('提示', '包含处理中的任务，不能删除', 'warning');
	} else {
		// 执行删除(批量)
		JSPFree.doBillListBatchDelete(d1_BillList);
	}
}

//启动任务
function onStartReport(){
	var selectDatas = d1_BillList.datagrid('getSelections');
	if (selectDatas.length == 0) {
		JSPFree.openDialog2("一键启动","/yujs/east/report/superviseReportDate.js",350,350,{currSQL:d1_BillList.CurrSQL},function(_rtdata){
		});
	} else{
		var isHaveAlreadyStart = false;
		var ary_taskids = [];  //所有rid数组
		for(var i=0; i<selectDatas.length; i++){
			// ②判断当前选中的任务，状态是否是已生成完的报文；如果是已生成，则弹出提示，是否要覆盖
			if ("完成" == selectDatas[i].status) {
				isHaveAlreadyStart = true;
			}
			ary_taskids.push(selectDatas[i].rid);
		}
		
		if (isHaveAlreadyStart) {
			JSPFree.confirm('提示', '当前选中的部分任务已生成报文，重新启动将覆盖已有报文文件?', function(_isOK){
				if (_isOK){
					JSPFree.openDialog2("启动选择的任务","/yujs/east/report/superviseReport_start.js",750,350,{allTaskIds:ary_taskids});
				}
			});
		} else {
			JSPFree.openDialog2("启动选择的任务","/yujs/east/report/superviseReport_start.js",750,350,{allTaskIds:ary_taskids});
		}
	}
}

// 一键启动
function onAllStartReport(){
	JSPFree.openDialog("一键启动","/yujs/east/report/superviseReportDate.js",350,350,{currSQL:d1_BillList.CurrSQL},function(_rtdata){
	});
}

/**
 * 打包下载报文
 * @returns
 */
function onZipAndDownload() {
	var selectDatas = d1_BillList.datagrid('getSelections');
	if (selectDatas.length == 0) {
		JSPFree.openDialog("打包压缩下载报文", "/yujs/east/report/superviseReport_choosedate.js", 400, 350, {org_no:org_no,org_class:org_class});
	}else{
		var dates = new Array();
		var orgs = new Array();
		var addrs = new Array();
		var str_date="";
		var orgStr="";
		var addrStr="";
		var rids = "";
		for (var i=0; i<selectDatas.length; i++) {
			str_date = selectDatas[0].data_dt;
			if(dates.indexOf(selectDatas[i].data_dt)==-1){
				dates.push(selectDatas[i].data_dt);
			}
			if(orgs.indexOf(selectDatas[i].org_no)==-1 && selectDatas[i].org_no){
				orgs.push(selectDatas[i].org_no);
			}
			if(orgStr.indexOf(selectDatas[i].org_no)==-1 && selectDatas[i].org_no){
				orgStr = orgStr + selectDatas[i].org_no;
			}
			if(addrs.indexOf(selectDatas[i].addr)==-1 && selectDatas[i].addr){
				addrs.push(selectDatas[i].addr);
			}

			if(addrStr.indexOf(selectDatas[i].addr)==-1 && selectDatas[i].addr){
				addrStr = addrStr + selectDatas[i].addr;
			}
			rids = rids + selectDatas[i].rid+",";
		}

		if(dates.length>1){
			$.messager.alert('提示', '请选择同日期数据进行打包下载操作', 'info');
			return;
		}

		if(orgs.length>1){
			$.messager.alert('提示', '请选择同机构数据进行打包下载操作', 'info');
			return;
		}

		var create_type = '1';
		if(addrs.length>0){
			create_type = '2';
		}

		JSPFree.openDialog("打包压缩下载报文","/yujs/east/report/superviseReport_zip.js",780,300,{rids:rids,data_dt:str_date,org_no:orgStr,create_type:create_type,addr:addrStr,org_class:org_class});
	}

}


//查看日志
function onViewLog(){
	var selectDatas = d1_BillList.datagrid('getSelections');
	if (selectDatas.length == 0) {
		$.messager.alert('提示', '至少选择一条记录进行操作', 'warning');
		return;
	} else if (selectDatas.length > 1) {
		$.messager.alert('提示', '只能选择一条记录进行操作', 'warning');
		return;
	}
	
	var json_data = selectDatas[0];
	if (json_data == null) {
		$.messager.alert('提示', '必须选择一条记录进行操作', 'warning');
	}
	
	JSPFree.openDialog("查看日志","/yujs/east/report/east_cr_report_viewlog.js",900,420,{rid:json_data.rid},function(_rtdata){
		//回调方法,立即查询数据
	});
}