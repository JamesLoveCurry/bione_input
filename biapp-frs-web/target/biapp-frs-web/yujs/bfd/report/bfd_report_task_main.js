/**
 * 
 * <pre>
 * Title: 【报送管理】-【报文生成】
 * Description: 报送管理-报文生成：主页面
 * 此页面提供了报送管理的相关操作，可以允许生成报文，删除报文任务，打包下载报文，查看生成报文的日志和查看报文任务
 * </pre>
 * @author liangzy5 
 * @version 1.00.00
   @date 2020年8月21日
 */

var tab_name = "";
var tab_name_en = "";
var org_no = "";
var org_class = "";
var org_nm = "";

function AfterInit() {
	//通过当前登录人所属内部机构获取报送机构号
	var jso_report_org = JSPFree.doClassMethodCall("com.yusys.bfd.common.service.BfdValidateQueryConditionBS","getReportOrgNoCondition",{_loginUserOrgNo:str_LoginUserOrgNo});
	if(jso_report_org.msg=="ok"){
		org_no = jso_report_org.data;
	}
	org_class = BfdFreeUtil.getOrgClass(org_no); //获取机构层级
	org_nm = BfdFreeUtil.getOrgNm(org_no); //获取机构名称
	
	JSPFree.createBillList("d1","/biapp-bfd/freexml/report/bfd_cr_report_CODE1.xml",null,{isSwitchQuery:"N",autoquery:"N"});
	JSPFree.queryDataByConditon2(d1_BillList, getCondition());
	
	FreeUtil.loadBillQueryData(d1_BillList, {org_no:org_no+"/"+org_nm});
}

/**
 * 页面加载结束后，对查询框中的机构下拉框进行处理
 * @returns
 */
function AfterBodyLoad(){
	// 判断是否是总行，如果是总行则不禁用，如果是其他则禁用
	if (org_class == BfdFreeUtil.getBfdOrgClass().zh) {
		JSPFree.setBillQueryItemEditable("org_no","下拉框",true);
	} else if (org_class == BfdFreeUtil.getBfdOrgClass().fh) {
		JSPFree.setBillQueryItemEditable("org_no","下拉框",false);
	} else {
		
	}
}

/**
 * 获取辖内机构号的过滤条件，作为sql的一部分
 * @returns
 */
function getCondition() {
	var condition = "";
	var jso_rt = JSPFree.doClassMethodCall("com.yusys.bfd.common.service.BfdValidateQueryConditionBS","getQueryCondition",{"_loginUserOrgNo" : str_LoginUserOrgNo});
	if(jso_rt.msg == "ok"){
		condition = jso_rt.condition;
	}
	
	return condition;
}

/**
 * 指标取数操作
 * 点击按钮，对三张季报指标类报表从大集中取数
 * @returns
 */
function onGetIdx() {
	JSPFree.openDialog("选择数据日期","/yujs/bfd/report/bfd_get_idx_choose_date.js",700,500,"",function(_rtdata){
		if (_rtdata != null && _rtdata != undefined && _rtdata.type != "dirclose") { //不是直接关闭窗口
            JSPFree.alert(_rtdata.msg);
        }
	});
}

/**
 * 生成报文操作
 * 若选中了页面上的记录，则认为是重新生成，判断报文任务状态，提示是否覆盖报文，即可生成报文
 * 若没有选择记录，则认为是选择日期生成报文
 * 点击按钮，选择一系列生成报文的参数，启动生成报文
 * @returns
 */
function onStartReport() {
	var isHaveAlreadyStart = false; //记录是否有状态为“完成”的报文任务
	
	var selectDatas = d1_BillList.datagrid('getSelections');
	//如果选中了记录，则判断日期是否是同一天，需要重新生成这些报文任务下的报文
	var jso_rids = new Array();
	if (selectDatas.length > 0) {
		for (var i=0; i<selectDatas.length; i++) {
			if (i > 0) {
				if(selectDatas[i].data_dt != selectDatas[i-1].data_dt) { //其实应该可以去掉这个限制，后面再考虑
					$.messager.alert('提示', '只能选择同一日期的记录进行操作', 'warning');
					return;
				}
			}
			
			if ("完成" == selectDatas[i].status) {
				isHaveAlreadyStart = true;
			}
			jso_rids.push(selectDatas[i].rid);
		}
		
		if (isHaveAlreadyStart) { //已经有完成的报文，需要提示
			JSPFree.confirm('提示', '当前选中的记录包含已生成的报文记录，是否继续启动，如继续启动，将覆盖原已生成文件?', function(_isOK){
				if (_isOK){
					JSPFree.openDialog2("启动选择的任务","/yujs/bfd/report/bfd_report_task_start.js",750,350,{allTaskIds: jso_rids,data_dt:selectDatas[0].data_dt});
				}
			});
		} else { //不提示
			JSPFree.openDialog2("启动选择的任务","/yujs/bfd/report/bfd_report_task_start.js",750,350,{allTaskIds: jso_rids,data_dt:selectDatas[0].data_dt});
		} 
	} else { //如果没有选中，则认为新增报文任务
		// step1：创建任务
		JSPFree.openDialog("生成报文","/yujs/bfd/report/bfd_report_task_create.js",700,500,{org_no:org_no,org_class:org_class,org_nm:org_nm},function(_rtdata){
			if (_rtdata != null) {
				if ("dirclose" == _rtdata.type) { //直接关闭窗口
					return;
				}
				JSPFree.queryDataByConditon2(d1_BillList,_rtdata.wheresql); // 立即查询数据
				// steep2：启动任务，超多参数！！str_org是机构号 970110,990100
				JSPFree.openDialog2("启动选择的任务","/yujs/bfd/report/bfd_report_task_start.js",750,350,
				{allTaskIds: _rtdata.rids, data_dt:_rtdata.data_dt,report_type:_rtdata.report_type,str_org:_rtdata.str_org});
			}
		});
	}
}

/**
 * 刷新操作
 * 点击刷新按钮，实现刷新操作
 * @returns
 */
function onRefresh() {
	JSPFree.queryDataByConditon2(d1_BillList,getCondition());
}

/**
 * 删除报文
 * 生成报文中的报文任务，不能删除
 * @returns
 */
function onDeleteReport(){
	var selectDatas = d1_BillList.datagrid('getSelections');
	if (selectDatas.length == 0) {
		$.messager.alert('提示', '至少选择一条记录进行操作', 'warning');
		return;
	}
	
	var tab_name = "";
	for(var i=0; i<selectDatas.length; i++){
		if (selectDatas[i].status.match("生成报文中")) {
			tab_name += selectDatas[i].tab_name + ","
		}
	}
	if (tab_name != null && tab_name != "") {
		$.messager.alert('提示', '包含处理中的任务，不能删除', 'warning');
	} else {
		// 执行删除(批量)
		//JSPFree.doBillListBatchDelete(d1_BillList);

		// 警告提醒是否真的删除?
		$.messager.confirm('提示', '你真的要删除选中的记录吗?', function(_isConfirm) {
			if (!_isConfirm) {
				return;
			}
			var jso_templetVO = d1_BillList.templetVO; // 模板对象
			var str_ds = jso_templetVO.templet_option.ds; // 数据源

			// 远程调用,真正删除数据库
			try {
				var str_id = [];
				var str_path = [];
				for(var i=0; i<selectDatas.length; i++){
					str_id.push(selectDatas[i].rid);
					if(selectDatas[i].filepath != "" && selectDatas[i].filepath.length !=0){
						str_path.push(selectDatas[i].filepath);
					}
				}
				var idStr = "('" + str_id.join("','") + "')";
				var pathStr = (str_path.length == 0) ? "" : str_path.join(",");
				JSPFree.doClassMethodCall("com.yusys.bfd.report.service.BfdCrReportBS", "del", {ds:str_ds, str_id:idStr, str_path:pathStr});
				JSPFree.queryDataByConditon2(d1_BillList,getCondition());
				$.messager.alert('提示', '删除数据成功!', 'info');
			} catch (_ex) {
				console.log(_ex);
				FreeUtil.openHtmlMsgBox("发生异常", 500, 250, _ex)
			}
		});
	}
}

/**
 * 打包下载报文
 * @returns
 */
function onZipAndDownload() {
	JSPFree.openDialog("打包压缩下载一个机构某一日期下的所有报文", "/yujs/bfd/report/bfd_report_task_choosedate.js", 400, 350, {org_no:org_no,org_class:org_class,org_nm:org_nm});
}

/**
 * 查看日志
 * @returns
 */
function onViewLog() {
	var selectDatas = d1_BillList.datagrid('getSelections');
	if (selectDatas.length == 0) {
		$.messager.alert('提示', '至少选择一条记录进行操作', 'info');
		return;
	} else if (selectDatas.length > 1) {
		$.messager.alert('提示', '只能选择一条记录进行操作', 'info');
		return;
	}

	var json_data = selectDatas[0];
	if (json_data == null) {
		$.messager.alert('提示', '必须选择一条记录进行操作', 'info');
	}

	JSPFree.openDialog("查看日志","/yujs/bfd/report/bfd_cr_report_viewlog.js",900,420,{rid:json_data.rid},function(_rtdata){
		//回调方法,立即查询数据
	});
}
/*
 * 下载一个文件
 */
function downLoadOne(_btn) {
	var dataset = _btn.dataset;
	var index = dataset.rowindex;
	var rows = d1_BillList.datagrid("getRows");
	var row = rows[index]; // index为行号
	var result = JSPFree.doClassMethodCall("com.yusys.bfd.report.service.BfdCrReportBS", "getZip", { rid:row.rid});
	if (result.code == 'success') {
		JSPFree.confirm('提示', '你真的要下载压缩文件么?这是一个非常耗时的操作,请谨慎操作!<br>建议使用Ftp工具下载或直接在服务器端拷贝!', function(_isOK){
			if (_isOK){
				var download=null;
				download = $('<iframe id="download" style="display: none;"/>');
				$('body').append(download);

				var src = v_context + "/bfd/report/downloadOne?rid="+row.rid ;
				download.attr('src', src);
			}
		});
	} else {
		$.messager.alert('提示', result.msg, 'info');
	}

}

/**
 * 导入报文
 */
function onImportReport() {
	JSPFree.openDialog("文件上传", "/yujs/bfd/report/bfd_report_import.js", 500, 300, {
	}, function (_rtdata) {
		if (_rtdata == "success") {
			JSPFree.alert("导入成功！");
		}
	});
}