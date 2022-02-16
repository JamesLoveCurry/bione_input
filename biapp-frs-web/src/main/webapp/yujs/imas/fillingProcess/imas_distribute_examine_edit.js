/**
 * 
 * <pre>
 * Title: 【报表处理】-【报表审核】
 * Description: 报表处理-报表审核：单条审核页面
 * 此页面提供了报表审核：单条审核的操作，可以允许用户审核通过或退回报表的填报任务
 * 若审核通过，则此任务的状态变更为完成，并将数据回灌到全量层和_M表。若审核不通过，则退回子任务，继续填报
 * </pre>
 * @author mkx
 * @version 1.00.00
   @date 2021年4月18日
 */

var data = "";
var tabName = "";
var tabNameEn = "";
var taskId = "";
var maskUtil = "";
function AfterInit(){
	JSPFree.createSplitByBtn("d1","上下",180,["通过/onConfirm","退回/onBack"]);
	data = jso_OpenPars.data;
	tabName = data.tab_name;
	tabNameEn = data.tab_name_en;
	var dataDt = data.data_dt;
	taskId = data.rid;
	maskUtil = FreeUtil.getMaskUtil();

	var str_className2 = "Class:com.yusys.imas.business.service.ImasModelTempletBuilderForAudit.getTemplet('"+tabName+"','"+tabNameEn+"','"+str_LoginUserOrgNo+"','_R','"+taskId+"','force_sub_sts')";
	JSPFree.createBillCard("d1_A", "/biapp-imas/freexml/fillingProcess/imas_filling_process_examin_detail.xml", null, null)
	JSPFree.createBillList("d1_B",str_className2,null,{list_btns:"[icon-p31]查看/onView(this);",isSwitchQuery:"N",autoquery:"Y",ishavebillquery:"N",list_ispagebar:"Y"});
}
function AfterBodyLoad(){
	var dom_div = document.getElementById("d1_A_BillCardDiv");
	dom_div.style.overflow="hidden";  //隐藏滚动框
	JSPFree.setBillCardItemValue(d1_A_BillCard, "total_num", data.total_num);
	JSPFree.setBillCardItemValue(d1_A_BillCard, "no_handle", data.no_handle);
	JSPFree.setBillCardItemValue(d1_A_BillCard, "pass_count", parseInt(data.total_num) -  parseInt(data.no_handle));
	var reason = d1_B_BillList.templetVO.templet_option.reason;
	if (reason) {
        JSPFree.setBillCardItemValue(d1_A_BillCard, "reason", reason);
    }
    JSPFree.setBillCardItemValue(d1_A_BillCard, "force_count", d1_B_BillList.templetVO.templet_option.forcecount);
}
/**
 * 审核通过操作
 * 点击通过按钮，审核通过该条填报任务
 */
var col_arr = []; 
function onConfirm(){
	var colStr = '';
	JSPFree.confirm('提示', '你真的要通过该任务吗?', function(_isOK){
		if (_isOK){
			var jso_allrids = [];
			jso_allrids.push(data.rid);
			var jso_col = JSPFree.getHashVOs("select col_name_en from imas_cr_col where tab_name='"+tabName+"' and is_export='Y'  order by col_no");
			if(jso_col != null && jso_col.length > 0){
				for (var i=0;i<jso_col.length;i++) {
					col_arr.push(jso_col[i].col_name_en);
					if (i == jso_col.length - 1) {
						colStr += jso_col[i].col_name_en ;
					} else {
						colStr += jso_col[i].col_name_en + ',';
					}

				}
			}

			// 修改数据状态：3：完成
			var jso_Pars = {allrids:jso_allrids,status:'3',type:'3',reason:"通过",userNo:str_LoginUserCode,
					tabNameEn:tabNameEn,colArry:colStr};
			maskUtil.mask();
			setTimeout(function () {
				var jsn_result = JSPFree.doClassMethodCall("com.yusys.imas.process.service.ImasCrProcessBSDMO", "updateDataByTaskByRids1", jso_Pars);
				maskUtil.unmask();
				if (jsn_result.msg == 'OK') {
					JSPFree.closeDialog("通过");
				}
			},100);
		}
	});
}

/**
 * 审核退回操作
 * 点击退回按钮，退回该条填报任务，退回后的任务将会继续填报流程
 * @returns
 */
function onBack(){
	JSPFree.confirm('提示', '你真的要退回该任务吗?', function(_isOK){
		if (_isOK){
			//弹出窗口,传入参数,然后接收返回值!
			JSPFree.openDialog("填写原因","/yujs/imas/fillingProcess/imas_distribute_detail_fillin_reason.js",600,300,{templetcode:"/biapp-imas/freexml/fillingProcess/imas_filling_process_remark.xml"},function(_rtdata){
				if(_rtdata != null && _rtdata != undefined && null != _rtdata.reason && undefined != _rtdata.reason){
					// 修改数据状态：3：完成
					var jso_allrids = [];
					jso_allrids.push(data.rid);
					
					var jso_Pars = {allrids:jso_allrids,status:'2',type:'2',reason:_rtdata.reason,userNo:str_LoginUserCode};
					maskUtil.mask();
					setTimeout(function () {
						var jsn_result = JSPFree.doClassMethodCall("com.yusys.imas.process.service.ImasCrProcessBSDMO", "updateDataByTaskByRids2", jso_Pars);
						maskUtil.unmask();
						if (jsn_result.msg == 'OK') {
							JSPFree.closeDialog("退回");
						}
					}, 100);
				}
			});
		}
	});
}
/**
 * 查看详情，选择一条统计数据
 */
function onView(_this){
	var json_data = d1_B_BillList.datagrid('getSelections');
	if (json_data == null || json_data == undefined || json_data.length == 0) {
		$.messager.alert('提示', '必须选择一条记录进行操作', 'warning');
		return;
	}
	var jso_Pars = {tabName: tabName, tabNameEn: tabNameEn, json_data: json_data[0]};

	JSPFree.openDialog2("查看详情","/yujs/imas/fillingProcess/imas_distribute_examine_detail.js",1000,600,jso_Pars,function(_rtdata) {
	});
}