/**
 * 
 * <pre>
 * Title: 【错误补录】
 * Description: 错误补录主页面
 * </pre>
 * @author wangxy31 
 * @version 1.00.00
   @date 2020年5月12日
 */

var tab_name = "";
var tab_name_en = "";
var str_subfix = "";

function AfterInit() {
	// 获取路径参数
	if (jso_OpenPars != '') {
		if(jso_OpenPars.type != null) {
			str_subfix = jso_OpenPars.type;
		}
	}
	// 获取【错误补录表】常量类
	tab_name = SafeFreeUtil.getTableNames().SAFE_RESULT_DATA;
	// 获取英文表名
	var jso_data = JSPFree.doClassMethodCall(
			"com.yusys.safe.base.common.service.SafeCommonBS",
			"getTabNameByEn", {tab_name:tab_name, report_type:str_subfix});
	tab_name_en = jso_data.tab_name_en;
	
	var str_className = "Class:com.yusys.safe.base.common.template.CommonBuilderTemplate.getTemplet('" + tab_name + "','" + tab_name_en + "','" + str_subfix + "','" + str_LoginUserOrgNo + "')";
	JSPFree.createBillList("d1", str_className, null, {list_btns:"$VIEW;[icon-p41]单一处理/singleHandler(this)",isSwitchQuery:"N"});
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
			pkvalue:selectData.pkvalue,report_type:str_subfix};
	if (SafeFreeUtil.getSafeResultDataStatus().status_1 == selectData.result_status) {
		JSPFree.openDialog(selectData.tablename,"/yujs/safe/errorRecord/error_record_handle.js", 900, 560, def,function(_rtdata) {
			if (_rtdata.type == "dirclose" || _rtdata == false) {
				return;
			}
			
			if (_rtdata == "OK") {
				JSPFree.alert("保存成功!<br>同时修改了对应的校验结果状态为【完成】!");
				JSPFree.queryDataByConditon(d1_BillList, "reportdate ='"+str_data_dt+"' order by tablename_en");  // 立即查询刷新数据
			}
		});
	} else {
		$.messager.alert('提示','该数据已完成，请选择其他数据进行处理!','warning');
	}
}