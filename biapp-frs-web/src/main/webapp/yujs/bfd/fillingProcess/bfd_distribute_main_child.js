/**
 * 
 * <pre>
 * Title: 【报表处理】-【报表下发】
 * Description: 报表下发：查看子任务信息
 * 此页面提供了报表下发的查看子任务的操作，可以允许用户选择一条下发任务，查看这条任务对应的子任务情况
 * </pre>
 * @author liangzy5 
 * @version 1.00.00
   @date 2020年8月18日
 */

var task_id = "";
function AfterInit(){
	var jsoIsLine = JSPFree.doClassMethodCall("com.yusys.bfd.process.service.BfdCrProcessBSDMO", "getIsLineNo", {} );
	var templeteCode;
	if (jsoIsLine.msg == "OK") {
		templeteCode = "/biapp-bfd/freexml/fillingProcess/bfd_filling_process_child_CODE5.xml";
	} else {
		templeteCode = "/biapp-bfd/freexml/fillingProcess/bfd_filling_process_child_CODE2.xml";
	}
	JSPFree.createBillList("d1",templeteCode,
			null,{isSwitchQuery:"N",ishavebillquery:"N",autoquery:"N",list_btns:"[icon-p80]处理日志/processReasonDetail"});

	task_id = jso_OpenPars2.data.rid;
	JSPFree.queryDataByConditon2(d1_BillList, "task_id = '"+task_id+"'");
	JSPFree.setBillListForceSQLWhere(d1_BillList,"task_id = '"+task_id+"'");
}

/**
 * 查看处理日志
 * 选择一条子任务记录，点击按钮，即可查看这条子任务的处理日志
 * @returns
 */
function processReasonDetail(){
	var json_data = d1_BillList.datagrid('getSelections');
	if (json_data == null || json_data == undefined || json_data.length == 0) {
		$.messager.alert('提示', '必须选择一条记录进行操作', 'warning');
		return;
	}
	if (json_data.length>1) {
		$.messager.alert('提示', '只能选择一条记录进行操作', 'warning');
		return;
	}

	var jso_par = {child_task_id:json_data[0].rid};
	JSPFree.openDialog("处理日志信息","/yujs/bfd/fillingProcess/bfd_distribute_process_detail.js",700,400,jso_par,function(_rtData){
		if (_rtData != null) {
			if ("dirclose" == _rtData.type) {
				return;
			}
		}
	});
}