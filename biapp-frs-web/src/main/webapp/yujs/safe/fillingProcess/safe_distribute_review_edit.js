/**
 * 
 * <pre>
 * Title: 【报表处理】-【报表复核】
 * Description: 报表复核：复核页面
 * </pre>
 * @author wangxy31 
 * @version 1.00.00
   @date 2020年6月22日
 */

var data = "";
var tabName = "";
var tabNameEn = "";
var reportType = "";
var taskId = "";

var str_ds = "";

	
function AfterInit() {
	reportType = jso_OpenPars.reportType;
	data = jso_OpenPars.data;
	tabName = data.tab_name;
	tabNameEn = data.tab_name_en;
	taskId = data.rid;
    str_ds = jso_OpenPars.ds;


    JSPFree.createSplitByBtn("d1", "上下", 500, ["通过/onConfirm", "退回/onBack"]);

    // 获取子表查询按钮
    // var childBtnStr = JSPFree.getChildTabBtn(tabNameEn, reportType);
    var str_className = "Class:com.yusys.safe.base.common.template.CommonBuilderTemplate.getTemplet('" + tabName + "','" + tabNameEn + "','" + reportType + "','" + str_LoginUserOrgNo + "')";
	JSPFree.createBillList("d1_A",str_className,null,{list_btns:"[icon-p41]查看/viewData;",isSwitchQuery:"N",autoquery:"Y",ishavebillquery:"N",list_ispagebar:"N",refWhereSQL:" xf_status is not null "});
	//审核时也要展示强制提交的数据，所以这行代码暂时注掉
	//JSPFree.queryDataByConditon2(d1_A_BillList, "task_id = '"+taskId+"' and xf_status = '" +SafeFreeUtil.getXfStatus().DOWN+ "'");
	JSPFree.queryDataByConditon2(d1_A_BillList, "task_id = '"+taskId+"' ");
}

/**
 * 通过
 */
var col_arr = []; 
function onConfirm(){
	JSPFree.confirm('提示', '你真的要通过该任务吗?', function(_isOK){
		if (_isOK){
			var taskIds = []; 
			taskIds.push(data.rid); // 任务id
			
			var jso_Pars = {taskIds:taskIds,status:SafeFreeUtil.getProcessChildStatus().COMPLETE,logType:SafeFreeUtil.getFillingReasonType().APPROVED,reason:"复核通过",userNo:str_LoginUserCode,reportType:reportType};
			var jsn_result = JSPFree.doClassMethodCall("com.yusys.safe.fillingProcess.service.SafeFillingProcessBS", "updateDataByTaskByTaskid", jso_Pars);
			if (jsn_result.msg == 'OK') {
				JSPFree.closeDialog("通过");
			}
		}
	});
}

/**
 * 退回
 * @returns
 */
function onBack() {
	JSPFree.confirm('提示', '你真的要退回该任务吗?', function(_isOK){
		if (_isOK) {
			// 获取【填报流程任务表原因记录】常量类
			var reasonTab = SafeFreeUtil.getTableNames().SAFE_FILLING_REASON;
			// 获取英文表名
			var jso_reason_data = JSPFree.doClassMethodCall(
					"com.yusys.safe.base.common.service.SafeCommonBS",
					"getTabNameByEn", {tab_name:reasonTab, report_type:reportType});
			var reasonTabNameEn = jso_reason_data.tab_name_en;
			var str_className = "Class:com.yusys.safe.base.common.template.CommonBuilderTemplate.getTemplet('" + reasonTab + "','" + reasonTabNameEn + "','" + reportType + "')";
			
			//弹出窗口,传入参数,然后接收返回值!
			JSPFree.openDialog("填写原因","/yujs/safe/fillingProcess/safe_distribute_detail_fillin_reason.js",600,300,
					{templetcode:str_className},function(_rtdata){
				if(_rtdata != null && _rtdata != undefined && null != _rtdata.reason && undefined != _rtdata.reason) {
					var taskIds = []; 
					taskIds.push(taskId);
					
					var jso_Pars = {taskIds:taskIds,status:SafeFreeUtil.getProcessChildStatus().RETURN,logType:SafeFreeUtil.getFillingReasonType().BACK,reason:_rtdata.reason,userNo:str_LoginUserCode,reportType:reportType};
					var jsn_result = JSPFree.doClassMethodCall("com.yusys.safe.fillingProcess.service.SafeFillingProcessBS", "updateDataByTaskByTaskid", jso_Pars);
					if (jsn_result.msg == 'OK') {
						JSPFree.closeDialog("退回");
					}
				}
			});
		}
	});
}

/**
 * 查看子表信息
 * @param tabNmEn 字表表名
 */
function childView(childTabNmEn, childTabNm, btnNm, typeFlag) {
    var json_rowdata = d1_A_BillList.datagrid('getSelected'); // 先得到数据

    if (json_rowdata == null || json_rowdata == undefined) {
        $.messager.alert('提示', '至少选择一条记录进行操作', 'warning');
        return;
    }
    var defaultVal = {tabname:childTabNm,tabnameen:childTabNmEn,typeFlag:typeFlag,str_ds:str_ds,report_type:reportType,parentTabNmEn:tabNameEn,parentRid:json_rowdata.rid,data_dt:json_rowdata.data_dt,org_no:json_rowdata.org_no,fromType:"N"};
    JSPFree.openDialog3("查看"+btnNm,"/yujs/safe/busidata/safe_childtab_info_list.js",null,null,defaultVal,function(_rtdata) {},true);

}


function viewData() {
    var json_data = d1_A_BillList.datagrid('getSelections');
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
    var jso_Pars = {json_data:json_data[0],tabNameEn:tabNameEn,tabName:tabName,reportType:reportType,strDs:str_ds,operType:'view'}
    JSPFree.openDialog2("编辑","/yujs/safe/fillingProcess/safe_distribute_detail_fillin_edit.js",1000,800,jso_Pars,function(_rtData){

    });
}
