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
	var jso_report_org = JSPFree.doClassMethodCall("com.yusys.imas.common.service.ImasValidateQueryConditionBS","getReportOrgNoCondition",{_loginUserOrgNo:str_LoginUserOrgNo});
	if(jso_report_org.msg=="ok"){
		org_no = jso_report_org.data;
	}
	org_class = ImasFreeUtil.getOrgClass(org_no); //获取机构层级
	org_nm = ImasFreeUtil.getOrgNm(org_no); //获取机构名称
	
	JSPFree.createBillList("d1","/biapp-imas/freexml/report/imas_cr_report_CODE1.xml",null,{isSwitchQuery:"N",autoquery:"N"});
	JSPFree.queryDataByConditon2(d1_BillList, getCondition());
	
	// 如果当前机构为报送机构，则设置默认值，否则不设置值
	if (org_no) {
		var result = JSPFree.doClassMethodCall("com.yusys.imas.common.service.ImasValidateQueryConditionBS", "checkOrgIsReportOrg", {
			rptOrgNo: org_no,
			isSingle: false
		});
		if (result.result) {
			FreeUtil.loadBillQueryData(d1_BillList, {org_no: result.result});
		}
	}
}

/**
 * 页面加载结束后，对查询框中的机构下拉框进行处理
 * @returns
 */
function AfterBodyLoad(){
}

/**
 * 获取辖内机构号的过滤条件，作为sql的一部分
 * @returns
 */
function getCondition() {
	var condition = "";
	var jso_rt = JSPFree.doClassMethodCall("com.yusys.imas.common.service.ImasValidateQueryConditionBS","getQueryCondition",{"_loginUserOrgNo" : str_LoginUserOrgNo});
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
	JSPFree.openDialog("选择数据日期","/yujs/imas/report/imas_get_idx_choose_date.js",700,500,"",function(_rtdata){
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
			
			if ("报文已生成" == selectDatas[i].status) {
				isHaveAlreadyStart = true;
			}
			jso_rids.push(selectDatas[i].rid);
		}
		
		if (isHaveAlreadyStart) { //已经有完成的报文，需要提示
			JSPFree.confirm('提示', '当前选中的记录包含已生成的报文记录，是否继续启动，如继续启动，将覆盖原已生成文件?', function(_isOK){
				if (_isOK){
					JSPFree.openDialog2("启动选择的任务","/yujs/imas/report/imas_report_task_start.js",750,350,{allTaskIds: jso_rids,data_dt:selectDatas[0].data_dt});
				}
			});
		}else{ //不提示
			JSPFree.openDialog2("启动选择的任务","/yujs/imas/report/imas_report_task_start.js",750,350,{allTaskIds: jso_rids,data_dt:selectDatas[0].data_dt});
		} 
		
	} else { //如果没有选中，则认为新增报文任务
		// step1：创建任务
		JSPFree.openDialog("生成报文","/yujs/imas/report/imas_report_task_create.js",700,500,{org_no:org_no,org_class:org_class,org_nm:org_nm},function(_rtdata){
			if (_rtdata != null) {
				if ("dirclose" == _rtdata.type) { //直接关闭窗口
					return;
				}
				JSPFree.queryDataByConditon2(d1_BillList,_rtdata.wheresql); // 立即查询数据
				// steep2：启动任务，超多参数！！str_org是机构号 970110,990100
				JSPFree.openDialog2("启动选择的任务","/yujs/imas/report/imas_report_task_start.js",750,350,
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
		$.messager.confirm('提示', '删除会删除分片和生成的报文，你真的要删除选中的记录吗?', function(_isConfirm) {
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
				JSPFree.doClassMethodCall("com.yusys.imas.report.service.ImasCrReportBS", "del", {ds:str_ds, str_id:idStr, str_path:pathStr});
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
	JSPFree.openDialog("打包压缩下载一个机构某一日期下的所有报文", "/yujs/imas/report/imas_report_task_choosedate.js", 400, 350, {org_no:org_no,org_class:org_class,org_nm:org_nm});
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

	JSPFree.openDialog("查看日志","/yujs/imas/report/imas_cr_report_viewlog.js",900,420,{rid:json_data.rid},function(_rtdata){
		//回调方法,立即查询数据
	});
}

function onSeparate(){
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

	JSPFree.openDialog("查看日志","/yujs/imas/report/imas_cr_report_separate.js",900,420,{rid:json_data.rid, orgNo: json_data.org_no},function(_rtdata){
		//回调方法,立即查询数据
	});
}

/**
 * 直连上传报文
 */
function uploadFile() {
	var selectDatas = d1_BillList.datagrid('getSelections');
	if (selectDatas.length == 0) {
		// 如果不选择，则选择日期和报送频率全部上传。
		JSPFree.openDialog("上传一个机构某一日期下的所有报文", "/yujs/imas/report/imas_report_task_upload.js", 400, 350, {org_no:org_no,org_class:org_class,org_nm:org_nm},function(_rtdata){
			if (_rtdata.code == 'success') {
				$.messager.show({
					title : '消息提示',
					msg : '报文上传中,请稍候查看上传结果!',
					showType : 'show'
				});
			} else {
				$.messager.alert('提示', _rtdata.msg, 'info');
			}
		});
	} else {
		var jso_rids = new Array();
		for (var i=0; i<selectDatas.length; i++) {
			jso_rids.push(selectDatas[i].rid);
		}
		var jso_rt = JSPFree.doClassMethodCall("com.yusys.imas.report.service.ImasSendReportBS","uploadFile",{"allReportId": jso_rids, "loginUserOrgNo" : str_LoginUserOrgNo});
		if (jso_rt.code == 'success') {
			$.messager.show({
				title : '消息提示',
				msg : '报文上传中,请稍候查看上传结果!',
				showType : 'show'
			});
		} else {
			$.messager.alert('提示', jso_rt.msg, 'info');
		}
	}
}

/**
 * 强制提交
 */
function submitForced() {
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

	JSPFree.openDialog("强制提交","/yujs/imas/report/imas_report_task_import.js",600,360,{rid:json_data.rid},function(_rtdata){
		if (_rtdata != null && _rtdata != '' && _rtdata != undefined) {
			//回调方法,立即查询数据
			if (_rtdata == 'success') {
				$.messager.alert('提示', '强制提交成功!', 'info');
			} else {
				$.messager.alert('提示', _rtdata.msg, 'info');
			}
		}

	});
}