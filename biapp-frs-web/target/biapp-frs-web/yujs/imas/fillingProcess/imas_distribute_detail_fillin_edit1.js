/**
 * 
 * <pre>
 * Title: 【报表处理】-【报表填报】
 * Description: 报表处理-报表填报：编辑页面
 * 选择一条错误明细数据，对该条数据进行弹窗展示，并允许编辑除了主键字段以外的字段
 * </pre>
 * @author liangzy5 
 * @version 1.00.00
   @date 2020年8月18日
 */

var tabName = "";
var tabNameEn = "";
var dataDt = "";
var rptOrgNo = "";
var json_data = "";
var str_ds = "";
function AfterInit() {
	tabName = jso_OpenPars2.tabName;
	tabNameEn = jso_OpenPars2.tabNameEn;
	dataDt = jso_OpenPars2.dataDt;
	rptOrgNo = jso_OpenPars2.rptOrgNo
	var str_className1 = "Class:com.yusys.imas.business.service.ImasModelTempletBuilderForDistribute.getTemplet('"+tabName+"','"+tabNameEn+"','"+str_LoginUserOrgNo+"','_R')";
	JSPFree.createBillCard("d1",str_className1,["保存/onSave/icon-p21","取消/onCancel/icon-undo","强制保存/onForceSave/icon-p21"],null);
	
	JSPFree.setBillCardValues(d1_BillCard,jso_OpenPars2.json_data);
	d1_BillCard.OldData = jso_OpenPars2.json_data;
	json_data = jso_OpenPars2.json_data;
}

/**
 * 页面加载完成之后，表单中的前端主键is_pk1设置为不可编辑，强制设置机构编号和数据日期不可编辑
 */
var col_arr = []; 
function AfterBodyLoad(){
	// 表单中的前端主键is_pk1设置为不可编辑
	var tabName = jso_OpenPars2.tabName;
	var jso_col = JSPFree.getHashVOs("select col_name_en from imas_cr_col where tab_name='"+tabName+"' and is_pk1='Y' order by col_no");
	if(jso_col != null && jso_col.length > 0){
		for (var i=0;i<jso_col.length;i++) {
			col_arr.push(jso_col[i].col_name_en);
			JSPFree.setBillCardItemEditable(d1_BillCard,jso_col[i].col_name_en.toLowerCase(),false);
		}
	}

	//JSPFree.setBillCardItemEditable(d1_BillCard,"data_dt",false);
	JSPFree.setBillCardItemEditable(d1_BillCard,"tr_dt",false);
	//JSPFree.setBillCardItemEditable(d1_BillCard,"org_no",false);
	JSPFree.setBillCardItemEditable(d1_BillCard,"rpt_org_no",false);
	JSPFree.setBillCardItemEditable(d1_BillCard,"dept_no",false);
	JSPFree.setBillCardItemEditable(d1_BillCard,"issued_no",false);
	var ds_name = JSPFree.getHashVOs("select ds_name from imas_cr_tab where tab_name='"+tabName+"'");
	if (ds_name != null && ds_name.length > 0) {
		str_ds = ds_name[0].ds_name;
	}
	//JSPFree.editTableCheckData(d1_BillCard, "Edit", tabName, tabNameEn.toUpperCase(), str_ds, ImasFreeUtil.reportType);
	// 根据报送机构获取当前错误表，然后查询出所有的错误规则。
	showErrorFiled(col_arr);
}

/**
 * 保存数据操作
 * @return {[type]} [description]
 */
var saveFlag = "";
function onSave(_from){
	var str_ds = getDsName();
	var jso_check = JSPFree.doClassMethodCall("com.yusys.imas.common.service.ImasCommonBS",
		"getImasCheckProperties", {});
	var ischeck = jso_check.ischeck;
	if ("Y" == ischeck) {
		var backValue = JSPFree.editTableCheckData(d1_BillCard, "Edit", tabName, tabNameEn.toUpperCase(),str_ds, ImasFreeUtil.reportType);
		if (backValue == "" || "undifind" == backValue) {
			return ;
		} else if (backValue == "OK") {
			JSPFree.setBillCardItemValue(d1_BillCard,"data_modify",ImasFreeUtil.getDataModefy().data_filling);
			saveFlag = JSPFree.doBillCardUpdate(d1_BillCard,null);

			if (saveFlag == true) {
				var newData = JSPFree.getBillCardFormValue(d1_BillCard)
				// 此处在更新一个数据表状态值
				var dd = {tabNameEn:jso_OpenPars2.tabNameEn,colArry:col_arr,status:'1',jsonData:jso_OpenPars2.json_data, newData: newData};
				var jsn_result = JSPFree.doClassMethodCall("com.yusys.imas.process.service.ImasCrProcessBSDMO", "updateTemplateTabs", dd);

				if (jsn_result.msg == 'OK') {
					JSPFree.closeDialog(true);
					if (_from==2)
						return jsn_result.msg;
				}
			}
		} else if (backValue == "Fail") {
			if (_from==2)
				return backValue;
			else return ;
		}
	} else {
		JSPFree.setBillCardItemValue(d1_BillCard,"data_modify",ImasFreeUtil.getDataModefy().data_filling);
		saveFlag = JSPFree.doBillCardUpdate(d1_BillCard,null);

		if (saveFlag == true) {
			var newData = JSPFree.getBillCardFormValue(d1_BillCard)
			// 此处在更新一个数据表状态值
			var dd = {tabNameEn:jso_OpenPars2.tabNameEn,colArry:col_arr,status:'1',jsonData:jso_OpenPars2.json_data, newData: newData};
			var jsn_result = JSPFree.doClassMethodCall("com.yusys.imas.process.service.ImasCrProcessBSDMO", "updateTemplateTabs", dd);

			if (jsn_result.msg == 'OK') {
				JSPFree.closeDialog(true);
			}
		}
	}


}

/**
 * 取消编辑操作，点击按钮，关闭窗口
 * @returns
 */
function onCancel() {
	JSPFree.closeDialog(null);
}

/**
 * 强制保存操作
 */
function onForceSave() {
	var backValue = this.onSave(2);
	if (backValue == "" || "undifind" == backValue) {
		return ;
	}else if (backValue == "Fail") {
		var dsName = getDsName();
		var _formData=JSPFree.doBillCardUpdateByCheck(d1_BillCard,null);
		var jso_rt = JSPFree.doClassMethodCall("com.yusys.imas.singlecheck.ImasSingleCheckBS", "forceSingleCheckData", {
			data: _formData,
			tabName: tabName,
			tabNameEn: tabNameEn,
			ds: dsName
		});
		if (jso_rt.status=="ok"){
			JSPFree.setBillCardItemValue(d1_BillCard,"data_modify",ImasFreeUtil.getDataModefy().data_filling);
			saveFlag = JSPFree.doBillCardUpdate(d1_BillCard, null);

			if (saveFlag == true) {
				var newData = JSPFree.getBillCardFormValue(d1_BillCard)
				JSPFree.confirm("提示", "您确认要强制提交吗?", function (_isOK) {
					if (_isOK) {
						//弹出窗口,传入参数,然后接收返回值!
						JSPFree.openDialog("填写原因", "/yujs/imas/fillingProcess/imas_distribute_detail_fillin_reason.js", 600, 300,
							{templetcode: "/biapp-imas/freexml/fillingProcess/imas_filling_process_remark.xml"}, function (_rtdata) {
								if (_rtdata != null && _rtdata != undefined && null != _rtdata.reason && undefined != _rtdata.reason) {
									// 此处在更新一个数据表状态值
									var dd = {
										tabNameEn: jso_OpenPars2.tabNameEn,
										colArry: col_arr,
										status: '1',
										jsonData: jso_OpenPars2.json_data,
										newData: newData,
										reason: _rtdata.reason
									};
									var jsn_result = JSPFree.doClassMethodCall("com.yusys.imas.process.service.ImasCrProcessBSDMO", "updateTemplateTabs", dd);

									if (jsn_result.msg == 'OK') {
										JSPFree.closeDialog(true);
									}
								}
							});
					}
				});
			}
		}else if(jso_rt.status=="error"){
			$.messager.alert("提示","存在不能强制保存的规则","warning")
			return ;
		}

	}
}
/**
 * 获取ds_name
 */
function getDsName(){
	var str_ds = "";
	var ds_name = JSPFree.getHashVOs("select ds_name from imas_cr_tab where tab_name= '"+tabName+"'");
	if (ds_name != null && ds_name.length > 0) {
		str_ds = ds_name[0].ds_name;
	}
	return str_ds;
}

/**
 *  展示错误信息
 */
function showErrorFiled() {
	var colArr = [];
	var jso_col = JSPFree.getHashVOs("select col_name_en from imas_cr_col where tab_name='"+tabName+"' order by col_no");
	if(jso_col != null && jso_col.length > 0){
		for (var i=0;i<jso_col.length;i++) {
			colArr.push(jso_col[i].col_name_en);
		}
	}
	var rid = jso_OpenPars2.json_data.rid;
	// 根据rid获取数据
	var oldDataJson = JSPFree.doClassMethodCall("com.yusys.imas.process.service.ImasCrProcessBSDMO", "getOldData", {rid: rid, tabNameEn: tabNameEn, dsName: str_ds});
	var oldData =  oldDataJson.data;
	var newData =  json_data;
	for (var i=0;i<colArr.length;i++) {
		var data = colArr[i].toLocaleLowerCase();
		if (oldData[data] !=null && oldData[data]!="" && oldData[data]!=undefined) {
			if ( oldData[data] != newData[data]) {
				JSPFree.setBillCardItemWarnMsg(d1_BillCard, colArr[i].toLowerCase(), "修改前数据:" + oldData[data]);
				JSPFree.setBillCardItemColor(d1_BillCard, colArr[i].toLowerCase(), "yellow");
			}
		} else {
			if (newData[data] !=null && newData[data]!="" && newData[data]!=undefined) {
				JSPFree.setBillCardItemWarnMsg(d1_BillCard, colArr[i].toLowerCase(), "修改前数据:\"\"");
				JSPFree.setBillCardItemColor(d1_BillCard, colArr[i].toLowerCase(), "yellow");
			}
		}

	}

}