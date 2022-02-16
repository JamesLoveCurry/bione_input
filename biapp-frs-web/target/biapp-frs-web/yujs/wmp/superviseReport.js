//报文任务 初始化界面,菜单配置路径是【/frs/yufreejs?js=/yujs/wmp/superviseReport.js】
var fileName = "";
var org_class = "";
function AfterInit(){
	JSPFree.createBillList("d1","/biapp-wmp/freexml/wmp/wmp_cr_report_CODE1.xml",null,{isSwitchQuery:"N",autoquery:"N"});
	JSPFree.queryDataByConditon2(d1_BillList, getCondition());
	
	FreeUtil.loadBillQueryData(d1_BillList, {org_no:str_LoginUserOrgNo});
	
	// 判断是否是总行，如果是总行则不禁用，如果是其他则禁用
	var jso_org = JSPFree.getHashVOs("SELECT org_no,org_class FROM rpt_org_info where org_type='04' and is_org_report='Y' and org_no = '"+str_LoginUserOrgNo+"'");
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

function getCondition() {
	var condition = "";
	var jso_rt = JSPFree.doClassMethodCall("com.yusys.wmp.checkrule.rulesummary.service.ValidateQueryCondition","getQueryCondition",{"_loginUserOrgNo" : str_LoginUserOrgNo});
	if(jso_rt.msg == "ok"){
		condition = jso_rt.condition;
	}
	
	return condition;
}

//创建任务
function onCreateReport(){
  //alert("创建任务");
  var jso_par = {};  //参数
  JSPFree.openDialog("创建任务","/yujs/wmp/wmp_cr_report_create.js",800,500,jso_par,function(_rtdata){
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


//打包下载
function onZipAndDownload() {
	var str_org_no = JSPFree.getBillQueryFormValue(d1_BillQuery).org_no;
	if(str_org_no==""){
		$.messager.alert('提示', '请在查询框选择报送机构', 'warning');
		return;
	}
	
	var jso_report = JSPFree.getHashVOs("select count(*) c from east_cr_report where org_no='"+str_org_no+"'");
	var report = jso_report[0].c;
	if(report==0){
		$.messager.alert('提示', '请先创建选中机构的报文任务', 'warning');
		return;
	}
	
	var jso_data = JSPFree.getHashVOs("select count(*) c from east_cr_report where status not in ('完成') and org_no='"+str_org_no+"'");
	var count = jso_data[0].c;
	if(count>0){
		$.messager.alert('提示', '所选报送机构的报文未全部生成，请确保所有报文生成后再下载', 'warning');
		return;
	}
	
	JSPFree.openDialog("打包下载选中机构某一时间的所有报文","/yujs/east/report/superviseReport_choosedate.js",350,350,{org_no:str_org_no});
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