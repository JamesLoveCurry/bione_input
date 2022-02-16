/**
 * 
 * <pre>
 * Title: 【报表处理】-【报表填报】
 * Description: 报表填报：填报页面
 * </pre>
 * @author wangxy31 
 * @version 1.00.00
   @date 2020年6月22日
 */

var tabName = "";
var taskId = "";
var _sql = "";
var _sql1 = "";
var report_type = "";
var isLoadTabb_1 = false;
var isLoadTabb_2 = false;
var str_className = null;



var tabNum = "1";
var str_ds = "";


function AfterInit() {
	taskId = jso_OpenPars.taskId;
	tabName = jso_OpenPars.tabName;
	tabNameEn = jso_OpenPars.tabNameEn;
	dataDt = jso_OpenPars.dataDt;
	report_type = jso_OpenPars.report_type;
    str_ds = jso_OpenPars.ds;


    // 根据主表id查询其相关字子表，返回子表按钮标识
    // childBtnStr = JSPFree.getChildTabBtn(tabNameEn, report_type);

    JSPFree.createSplitByBtn("d1","上下",520,["提交/submission","取消/onCancel"]);
	JSPFree.createTabb("d1_A", [ "填报", "已填报" ]);
	
	str_className = "Class:com.yusys.safe.business.template.BusinessBuilderTemplate.getTemplet('" + tabName + "','" + tabNameEn + "','" + report_type + "','" + str_LoginUserOrgNo + "')";
	JSPFree.createBillList("d1_A_1", str_className, null, {list_btns:"[icon-p41]编辑/updateDate(this);[icon-p41]查看/viewData;[icon-p21]批量填报/onSave(this);[icon-p21]一键填报/onSaveAll(this);" ,isSwitchQuery:"N",autoquery:"N",ishavebillquery:"N", ishavebillquery:"N",list_ischeckstyle:"Y",list_ismultisel:"Y"});
	
	FreeUtil.loadBillQueryData(d1_A_1_BillList, {data_dt:dataDt})
	JSPFree.queryDataByConditon2(d1_A_1_BillList, "task_id = '"+taskId+"' and xf_status = '"+SafeFreeUtil.getXfStatus().NO_DOWN+"' ");
	JSPFree.setBillListForceSQLWhere(d1_A_1_BillList,  "task_id = '"+taskId+"' and xf_status = '"+SafeFreeUtil.getXfStatus().NO_DOWN+"' ");
	
	JSPFree.addTabbSelectChangedListener(d1_A_tabb, onSelect);
}

/**
 * 页面初始化后
 * @returns
 */
function AfterBodyLoad(){
	JSPFree.setBillQueryItemEditable("data_dt", "日历", false);
}

/**
 * TAB页，触发操作
 * @param _index
 * @param _title
 * @returns
 */
function onSelect(_index,_title) {
	var newIndex = _index+1;

	if(newIndex==1){
        tabNum = "1";
		if(isLoadTabb_1) {
			JSPFree.queryDataByConditon2(d1_A_1_BillList);
			FreeUtil.resetToFirstPage(d1_A_1_BillList); // 手工跳转到第一页
		}
		isLoadTabb_1 = true;
	} else if(newIndex==2){
        tabNum = "2";
		JSPFree.createBillList("d1_A_2",str_className,null,{list_btns:"[icon-p41]编辑/updateDate1(this);[icon-p41]查看/viewData1;",isSwitchQuery:"N",autoquery:"N",ishavebillquery:"N", ishavebillquery:"N"});
		FreeUtil.loadBillQueryData(d1_A_2_BillList, {data_dt:dataDt});
		JSPFree.queryDataByConditon2(d1_A_2_BillList, "task_id = '"+taskId+"' and xf_status = '"+SafeFreeUtil.getXfStatus().DOWN+"'");
		JSPFree.setBillListForceSQLWhere(d1_A_2_BillList,  "task_id = '"+taskId+"' and xf_status = '"+SafeFreeUtil.getXfStatus().DOWN+"' ");
		
		$.parser.parse('#d1_A_2');
	}
}

/**
 * 待审核：编辑
 * @returns
 */
function updateDate() {
	var json_data = d1_A_1_BillList.datagrid('getSelections');
	if (json_data == null || json_data == undefined || json_data.length == 0) {
		$.messager.alert('提示', '必须选择一条记录进行操作', 'warning');
		return;
	}
	if (json_data.length>1) {
		$.messager.alert('提示', '只能选择一条记录进行操作', 'warning');
		return;
	}
	console.log(json_data[0]);
    console.log(json_data[0].org_no);
	var jso_Pars = {json_data:json_data[0],tabNameEn:tabNameEn,tabName:tabName,dataDt:dataDt,reportType:report_type,strDs:str_ds}
	JSPFree.openDialog2("编辑","/yujs/safe/fillingProcess/safe_distribute_detail_fillin_edit.js",1000,800,jso_Pars,function(_rtData){
		if (_rtData != null) {
			if ("dirclose" == _rtData.type) {
				return;
			}
			if (_rtData == "CHECK_OK") {
				$.messager.alert('提示', '校验并保存成功！', 'info');
				JSPFree.queryDataByConditon(d1_A_1_BillList);
			} else if (_rtData == "OK") {
				$.messager.alert('提示', '保存成功！', 'info');
				JSPFree.queryDataByConditon(d1_A_1_BillList);
			}
		}
	});
}

/**
 * 已审核：编辑
 * @returns
 */
function updateDate1() {
	var json_data = d1_A_2_BillList.datagrid('getSelections');
	if (json_data == null || json_data == undefined || json_data.length == 0) {
		$.messager.alert('提示', '必须选择一条记录进行操作', 'warning');
		return;
	}
	if (json_data.length>1) {
		$.messager.alert('提示', '只能选择一条记录进行操作', 'warning');
		return;
	}

	var jso_Pars = {json_data:json_data[0],tabNameEn:tabNameEn,tabName:tabName,dataDt:dataDt,reportType:report_type,strDs:str_ds}
	JSPFree.openDialog2("编辑","/yujs/safe/fillingProcess/safe_distribute_detail_fillin_edit.js",1000,600,jso_Pars,function(_rtData){
		if (_rtData != null) {
			if ("dirclose" == _rtData.type) {
				return;
			}
			if (_rtData == "CHECK_OK") {
				JSPFree.alert("校验并保存成功!");
				JSPFree.queryDataByConditon(d1_A_1_BillList);
			} else if (_rtData == "OK") {
				JSPFree.alert("保存成功!");
				JSPFree.queryDataByConditon(d1_A_1_BillList);
			}
		}
	});
}

function viewData() {
    var json_data = d1_A_1_BillList.datagrid('getSelections');
    if (json_data == null || json_data == undefined || json_data.length == 0) {
        $.messager.alert('提示', '必须选择一条记录进行操作', 'warning');
        return;
    }
    if (json_data.length>1) {
        $.messager.alert('提示', '只能选择一条记录进行操作', 'warning');
        return;
    }
    var jso_Pars = {json_data:json_data[0],tabNameEn:tabNameEn,tabName:tabName,dataDt:dataDt,reportType:report_type,strDs:str_ds,operType:'view'}
    JSPFree.openDialog2("编辑","/yujs/safe/fillingProcess/safe_distribute_detail_fillin_edit.js",1000,800,jso_Pars,function(_rtData){

    });
}

function viewData1() {
    var json_data = d1_A_2_BillList.datagrid('getSelections');
    if (json_data == null || json_data == undefined || json_data.length == 0) {
        $.messager.alert('提示', '必须选择一条记录进行操作', 'warning');
        return;
    }
    if (json_data.length>1) {
        $.messager.alert('提示', '只能选择一条记录进行操作', 'warning');
        return;
    }
    var jso_Pars = {json_data:json_data[0],tabNameEn:tabNameEn,tabName:tabName,dataDt:dataDt,reportType:report_type,strDs:str_ds,operType:'view'}
    JSPFree.openDialog2("编辑","/yujs/safe/fillingProcess/safe_distribute_detail_fillin_edit.js",1000,800,jso_Pars,function(_rtData){

    });
}

/**
 * 提交
 * @returns
 */
function submission() {
	// 如果当前待编辑列表中有数据，则强制提交
	if (d1_A_1_BillList.datagrid('getData').total != 0) {
		JSPFree.confirm("提示", "数据表" + tabName + "下有未完成填报数据，是否继续提交?", function (_isOK) {
			if (_isOK) {
				forcesubmission();
			}
		});
	} else {
		var taskIds = [];
		taskIds.push(taskId);
		// 修改数据状态：0分发1待复核2待审核3退回4完成
		var jsn_result = JSPFree.doClassMethodCall("com.yusys.safe.fillingProcess.service.SafeFillingProcessBS", "updateDataByTaskByTaskid",
			{
				taskIds: taskIds,
				status: SafeFreeUtil.getProcessChildStatus().REVIEWED,
				logType: SafeFreeUtil.getFillingReasonType().SUBMIT,
				userNo: str_LoginUserCode,
				reportType: report_type
			});
		if (jsn_result.msg == 'OK') {
			JSPFree.closeDialog("提交");
		}
	}
}

/**
 * 强制提交
 * @returns
 */
function forcesubmission(){
	// 获取【填报流程任务表原因记录】常量类
	var reasonTab = SafeFreeUtil.getTableNames().SAFE_FILLING_REASON;
	// 获取英文表名
	var jso_reason_data = JSPFree.doClassMethodCall(
			"com.yusys.safe.base.common.service.SafeCommonBS",
			"getTabNameByEn", {tab_name:reasonTab, report_type:report_type});
	var reasonTabNameEn = jso_reason_data.tab_name_en;
	var str_className = "Class:com.yusys.safe.base.common.template.CommonBuilderTemplate.getTemplet('" + reasonTab + "','" + reasonTabNameEn + "','" + report_type + "')";

	//弹出窗口,传入参数,然后接收返回值!
	JSPFree.openDialog("填写原因","/yujs/safe/fillingProcess/safe_distribute_detail_fillin_reason.js",600,300,
			{templetcode:str_className},function(_rtdata){
		if(_rtdata != null && _rtdata != undefined && null != _rtdata.reason && undefined != _rtdata.reason){
			var taskIds = [];
			taskIds.push(taskId);
			doUpdateBackStatus(d1_A_1_BillList, taskIds, _rtdata.reason);
		}
	});
}

/**
 * 强制提交，修改状态
 * @param billList
 * @param _rids
 * @param _reason
 * @returns
 */

function doUpdateBackStatus(billList,_taskrid,_reason){
	var jso_par = {taskIds:_taskrid,status:SafeFreeUtil.getProcessChildStatus().REVIEWED, logType:SafeFreeUtil.getFillingReasonType().SUBMIT, reason:_reason, userNo:str_LoginUserCode,reportType:report_type};
	var jsn_result = JSPFree.doClassMethodCall("com.yusys.safe.fillingProcess.service.SafeFillingProcessBS","updateDataByTaskByTaskid",jso_par);
	
	if (jsn_result.msg == 'OK') {
		JSPFree.closeDialog("强制提交");
	}
}

/**
 * 取消
 * @returns
 */
function onCancel(){
	JSPFree.closeDialog();
}

/**
 * 修改选中的数据为已填报
 */
function onSave(){
	var json_data = d1_A_1_BillList.datagrid('getSelections');
	if (json_data == null || json_data == undefined || json_data.length == 0) {
		$.messager.alert('提示', '必须选择一条记录进行操作', 'warning');
		return;
	}

	var whereSql="";
	if (json_data.length==1){
		whereSql="rid = '"+json_data[0].rid+"'";
	}else{
		var rids="rid in ('";
		for(var i in json_data){
			rids=rids+json_data[i].rid+"','";
		}
		whereSql=rids.substr(0,rids.length-2)+")";
	}
	
	var dd = {whereSql: whereSql, tabNameEn: tabNameEn, xfstatus: SafeFreeUtil.getHandledStatus().handled_count};
	var jsn_result = JSPFree.doClassMethodCall("com.yusys.safe.fillingProcess.service.SafeFillingProcessBS", "updateTabXfStatus", dd);

	if (jsn_result.msg == 'OK') {
		$.messager.alert('提示', '修改填报状态成功', 'info');
		JSPFree.queryDataByConditon2(d1_A_1_BillList);
	}
}
/**
 * 一键修改所有数据为已填报
 */
function onSaveAll(){
	if (d1_A_1_BillList.datagrid('getData').total == 0) {
		$.messager.alert('提示', '没有有未填报数据', 'warning');
		return;
	}

	var whereSql = "data_dt='"+dataDt+"' and task_id = '"+taskId+"' and xf_status = '"+SafeFreeUtil.getXfStatus().NO_DOWN+"'";
	//拼接模板中配置的条件
	var str_xmlCons = d1_A_1_BillList.templetVO.templet_option.querycontion;
	if (typeof str_xmlCons != "undefined" && str_xmlCons != null && str_xmlCons.trim() != "") {
		whereSql = whereSql + " and " + str_xmlCons;  //
	}
	
	var dd = {whereSql: whereSql, tabNameEn: tabNameEn, xfstatus: SafeFreeUtil.getHandledStatus().handled_count};
	var jsn_result = JSPFree.doClassMethodCall("com.yusys.safe.fillingProcess.service.SafeFillingProcessBS", "updateTabXfStatus", dd);

	if (jsn_result.msg == 'OK') {
		$.messager.alert('提示', '修改填报状态成功', 'info');
		JSPFree.queryDataByConditon2(d1_A_1_BillList);
	}
}

/**
 * 查看字表信息
 * @param tabNmEn 字表表名
 */
function childView(childTabNmEn, childTabNm, btnNm, typeFlag) {
    var json_rowdata;
	if (tabNum == "1") {
        json_rowdata = d1_A_1_BillList.datagrid('getSelected');// 填报tab页
    } else {
        json_rowdata = d1_A_2_BillList.datagrid('getSelected');// 已填报tab页
    }
    if (json_rowdata == null || json_rowdata == undefined) {
        $.messager.alert('提示', '至少选择一条记录进行操作', 'warning');
        return;
    }
    var defaultVal = {tabname:childTabNm,tabnameen:childTabNmEn,typeFlag:typeFlag,str_ds:str_ds,report_type:report_type,parentTabNmEn:tabNameEn,parentRid:json_rowdata.rid,data_dt:json_rowdata.data_dt,org_no:json_rowdata.org_no,fromType:"TB"};
    JSPFree.openDialog3("查看"+btnNm+"信息","/yujs/safe/busidata/safe_childtab_info_list.js",null,null,defaultVal,function(_rtdata) {},true);

}