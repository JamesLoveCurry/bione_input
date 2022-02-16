/**
 * 
 * <pre>
 * Title: 【错误补录】
 * Description: 错误补录主页面
 * </pre>
 * @author miaokx
 * @version 1.00.00
   @date 2020年8月24日
 */

//处理..
var str_data_dt = "";
var str_LoginUserCode = window.self.str_LoginUserCode;
function AfterInit(){
	JSPFree.createBillList("d1","/biapp-bfd/freexml/errorRecord/bfd_error_revise_code.xml",null,{isSwitchQuery:"N"});
}
/**
 * 单一处理
 * @returns
 */
function singleHandler() {
	var selectData = d1_BillList.datagrid('getSelected');
	if (selectData == null) {
		$.messager.alert('提示', '必须选择一条记录进行操作', 'warning');
		
		return;
	}
	// 此处不需要校验日期是否锁定
	// 注意：调用引擎执行任务时，锁定，引擎结束后，解锁
	
	var def = {tablename_en:selectData.tablename_en,
			tablename:selectData.tablename,pkcolname:selectData.pkcolname,
			pkvalue:selectData.pkvalue};
	if (SafeFreeUtil.getSafeResultDataStatus().status_1 == selectData.result_status) {
		JSPFree.openDialog(selectData.tablename,"/yujs/bfd/errorRecord/bfd_error_record_handle.js", 1000, 560, def,function(_rtdata) {
			if (_rtdata.type == "dirclose" || _rtdata == false) {
				return;
			}
			
			if (_rtdata == "OK") {
				JSPFree.alert("保存成功!<br>同时修改了对应的校验结果状态为【完成】!");
				JSPFree.queryDataByConditon(d1_BillList);  // 立即查询刷新数据
			}
		});
	} else {
		$.messager.alert('提示','该数据已完成，请选择其他数据进行处理!','warning');
	}
}