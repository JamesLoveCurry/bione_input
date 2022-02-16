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
var old_data = "";
function AfterInit() {
	tabName = jso_OpenPars2.tabName;
	tabNameEn = jso_OpenPars2.tabNameEn;
	dataDt = jso_OpenPars2.dataDt;

	var str_className1 = "Class:com.yusys.bfd.business.service.BfdModelTempletBuilderForDistribute.getTemplet('"+tabName+"','"+tabNameEn+"','"+str_LoginUserOrgNo+"','_R')";
	JSPFree.createBillCard("d1",str_className1,["保存/onSave/icon-p21","取消/onCancel/icon-undo","强制保存/onForceSave/icon-p21"],null);
	
	JSPFree.setBillCardValues(d1_BillCard,jso_OpenPars2.json_data);
	d1_BillCard.OldData = jso_OpenPars2.json_data;
	old_data = JSPFree.getBillCardFormValue(d1_BillCard);
}

/**
 * 页面加载完成之后，表单中的前端主键is_pk1设置为不可编辑，强制设置机构编号和数据日期不可编辑
 */
var col_arr = []; 
function AfterBodyLoad(){
	// 表单中的前端主键is_pk1设置为不可编辑
	var tabName = jso_OpenPars2.tabName;
	var jso_col = JSPFree.getHashVOs("select col_name_en from bfd_cr_col where tab_name='"+tabName+"' and is_pk1='Y' and (col_name_en != 'IS_NULL' and col_name_en != 'IS_NOT_NULL') order by col_no");
	if(jso_col != null && jso_col.length > 0){
		for (var i=0;i<jso_col.length;i++) {
			col_arr.push(jso_col[i].col_name_en);
			JSPFree.setBillCardItemEditable(d1_BillCard,jso_col[i].col_name_en.toLowerCase(),false);
		}
	}

	JSPFree.setBillCardItemEditable(d1_BillCard,"data_dt",false);
	JSPFree.setBillCardItemEditable(d1_BillCard,"tr_dt",false);
	JSPFree.setBillCardItemEditable(d1_BillCard,"org_no",false);
	JSPFree.setBillCardItemEditable(d1_BillCard,"rpt_org_no",false);
	var ds_name = JSPFree.getHashVOs("select ds_name from bfd_cr_tab where tab_name='"+tabName+"'");
	if (ds_name != null && ds_name.length > 0) {
		str_ds = ds_name[0].ds_name;
	}
	
	// 根据 bfd_cr_col 中 is_not_edit 字段，控制字段是否可以修改
	var isNotEditVOS = JSPFree.getHashVOs("select col_name_en from bfd_cr_col where tab_name='" + tabName + "' and is_not_edit='Y' and (col_name_en != 'IS_NULL' and col_name_en != 'IS_NOT_NULL')");
	if (isNotEditVOS != null && isNotEditVOS.length > 0) {
		for (var i = 0; i < isNotEditVOS.length; i++) {
			JSPFree.setBillCardItemEditable(d1_BillCard, isNotEditVOS[i].col_name_en.toLowerCase(), false);
		}
	}
	JSPFree.editTableCheckData(d1_BillCard, "Edit", tabName, tabNameEn.toUpperCase(),str_ds,"BFD");
}

/**
 * 保存数据操作
 * @return {[type]} [description]
 */
var saveFlag = "";
function onSave(_from){
	var str_ds = getDsName();
	md5Encryption();
	var jso_check = JSPFree.doClassMethodCall("com.yusys.bfd.common.service.BfdCommonBS",
		"getBfdCheckProperties", {});
	var ischeck = jso_check.ischeck;
	if ("Y" == ischeck) {
		var backValue = JSPFree.editTableCheckData(d1_BillCard, "Edit", tabName, tabNameEn.toUpperCase(),str_ds,"BFD");
		if (backValue == "" || "undifind" == backValue) {
			return ;
		} else if (backValue == "OK") {
			JSPFree.setBillCardItemValue(d1_BillCard,"data_modify",BfdFreeUtil.getDataModefy().data_filling);
			saveFlag = JSPFree.doBillCardUpdate(d1_BillCard,null);

			if (saveFlag == true) {
				var newData = JSPFree.getBillCardFormValue(d1_BillCard)
				var par = {modifiedType:BfdFreeUtil.getModifiedType().update,oldData: old_data, newData: newData, tabName: tabName,opeMod:BfdFreeUtil.getOpeMod().bbtb}
				JSPFree.doClassMethodCall("com.yusys.bfd.business.service.BfdBusinessBS","saveRecord",par)
				// 此处在更新一个数据表状态值
				var dd = {tabNameEn:jso_OpenPars2.tabNameEn,colArry:col_arr,status:'1',jsonData:jso_OpenPars2.json_data, newData: newData};
				var jsn_result = JSPFree.doClassMethodCall("com.yusys.bfd.process.service.BfdCrProcessBSDMO", "updateTemplateTabs", dd);

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
		JSPFree.setBillCardItemValue(d1_BillCard,"data_modify",BfdFreeUtil.getDataModefy().data_filling);
		saveFlag = JSPFree.doBillCardUpdate(d1_BillCard,null);

		if (saveFlag == true) {
			var newData = JSPFree.getBillCardFormValue(d1_BillCard)
			var par = {modifiedType:BfdFreeUtil.getModifiedType().update,oldData: old_data, newData: newData, tabName: tabName,opeMod:BfdFreeUtil.getOpeMod().bbtb}
			JSPFree.doClassMethodCall("com.yusys.bfd.business.service.BfdBusinessBS","saveRecord",par)
			// 此处在更新一个数据表状态值
			var dd = {tabNameEn:jso_OpenPars2.tabNameEn,colArry:col_arr,status:'1',jsonData:jso_OpenPars2.json_data, newData: newData};
			var jsn_result = JSPFree.doClassMethodCall("com.yusys.bfd.process.service.BfdCrProcessBSDMO", "updateTemplateTabs", dd);

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
		var jso_rt = JSPFree.doClassMethodCall("com.yusys.bfd.singlecheck.BfdSingleCheckBS", "forceSingleCheckData", {
			data: _formData,
			tabName: tabName,
			tabNameEn: tabNameEn,
			ds: dsName
		});
		if (jso_rt.status=="ok"){
			md5Encryption();
			JSPFree.setBillCardItemValue(d1_BillCard,"data_modify",BfdFreeUtil.getDataModefy().data_filling);
			saveFlag = JSPFree.doBillCardUpdate(d1_BillCard, null);

			if (saveFlag == true) {

				var newData = JSPFree.getBillCardFormValue(d1_BillCard)
				JSPFree.confirm("提示", "您确认要强制提交吗?", function (_isOK) {
					if (_isOK) {
						//弹出窗口,传入参数,然后接收返回值!
						JSPFree.openDialog("填写原因", "/yujs/bfd/fillingProcess/bfd_distribute_detail_fillin_reason.js", 600, 300,
							{templetcode: "/biapp-bfd/freexml/fillingProcess/bfd_filling_process_remark.xml"}, function (_rtdata) {
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
									var jsn_result = JSPFree.doClassMethodCall("com.yusys.bfd.process.service.BfdCrProcessBSDMO", "updateTemplateTabs", dd);

									if (jsn_result.msg == 'OK') {
										var par = {modifiedType:BfdFreeUtil.getModifiedType().update,oldData: old_data, newData: newData, tabName: tabName,opeMod:BfdFreeUtil.getOpeMod().bbtb}
										JSPFree.doClassMethodCall("com.yusys.bfd.business.service.BfdBusinessBS","saveRecord",par)

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
	var ds_name = JSPFree.getHashVOs("select ds_name from bfd_cr_tab where tab_name= '"+tabName+"'");
	if (ds_name != null && ds_name.length > 0) {
		str_ds = ds_name[0].ds_name;
	}
	return str_ds;
}

/**
 * 脱敏
 */
function md5Encryption() {
	//证件号脱敏
	//个人客户信息、存量个人贷款、个人贷款发生额信息、存量个人存款信息、个人存款发生额信息
	if (tabNameEn == "BFD_102_GRKHXX" || tabNameEn == "BFD_201_CLGRDK" || tabNameEn == "BFD_201_GRDKFS" || tabNameEn == "BFD_202_CLGRCK" || tabNameEn == "BFD_202_GRCKFS") {
		var orig_id_no = JSPFree.getBillCardItemValue(d1_BillCard, "orig_id_no");
		var id_type = JSPFree.getBillCardItemValue(d1_BillCard, "id_type");
		var md5 = JSPFree.doClassMethodCall("com.yusys.bfd.singlecheck.BfdSingleCheckBS", "md5", {
			id_no: orig_id_no,
			id_type: id_type
		});
		if (id_type != '' && id_type != 'A01') {
			JSPFree.setBillCardItemValue(d1_BillCard, "id_no", md5.id_no_md5);
		} else {
			JSPFree.setBillCardItemValue(d1_BillCard, "id_no", orig_id_no);
		}
	}
	//金融机构稳企业保就业信息
	if(tabNameEn == "BFD_101_WQYBJY"){
		var orig_id_no = JSPFree.getBillCardItemValue(d1_BillCard, "orig_ygzjdm");
		var id_type = JSPFree.getBillCardItemValue(d1_BillCard, "ygzjlx");
		var md5 = JSPFree.doClassMethodCall("com.yusys.bfd.singlecheck.BfdSingleCheckBS", "md5", {id_no:orig_id_no,id_type:id_type});
		if(id_type != '' && id_type != 'A01'){
			JSPFree.setBillCardItemValue(d1_BillCard,"ygzjdm", md5.id_no_md5);
		} else {
			JSPFree.setBillCardItemValue(d1_BillCard,"ygzjdm", orig_id_no);
		}
	}
	//担保合同信息
	if (tabNameEn == "BFD_201_DBHTXX") {
		var orig_id_no = JSPFree.getBillCardItemValue(d1_BillCard, "orig_guar_id_no");
		var id_type = JSPFree.getBillCardItemValue(d1_BillCard, "guar_id_type");
		var md5 = JSPFree.doClassMethodCall("com.yusys.bfd.singlecheck.BfdSingleCheckBS", "md5", {
			id_no: orig_id_no,
			id_type: id_type
		});
		if (id_type != '' && id_type != 'A01') {
			JSPFree.setBillCardItemValue(d1_BillCard, "guar_id_no", md5.id_no_md5);
		} else {
			JSPFree.setBillCardItemValue(d1_BillCard, "guar_id_no", orig_id_no);
		}
	}
	//存量委托贷款表、委托贷款发生额信息
	if (tabNameEn == "BFD_201_CLWTDK" || tabNameEn == "BFD_201_WTDKFS") {
		var orig_id_no = JSPFree.getBillCardItemValue(d1_BillCard, "orig_id_no");
		var orig_client_id_no = JSPFree.getBillCardItemValue(d1_BillCard, "orig_client_id_no");
		var id_type = JSPFree.getBillCardItemValue(d1_BillCard, "id_type");
		var client_id_type = JSPFree.getBillCardItemValue(d1_BillCard, "client_id_type");
		var md5 = JSPFree.doClassMethodCall("com.yusys.bfd.singlecheck.BfdSingleCheckBS", "md5", {
			id_no: orig_id_no,
			id_type: id_type
		});
		var client_md5 = JSPFree.doClassMethodCall("com.yusys.bfd.singlecheck.BfdSingleCheckBS", "md5", {
			id_no: orig_client_id_no,
			id_type: client_id_type
		});
		if (id_type != '' && id_type != 'A01') {
			JSPFree.setBillCardItemValue(d1_BillCard, "id_no", md5.id_no_md5);
		} else {
			JSPFree.setBillCardItemValue(d1_BillCard, "id_no", orig_id_no);
		}
		if (client_id_type != '' && client_id_type != 'A01') {
			JSPFree.setBillCardItemValue(d1_BillCard, "client_id_no", client_md5.id_no_md5);
		} else {
			JSPFree.setBillCardItemValue(d1_BillCard, "client_id_no", orig_client_id_no);
		}
	}
}

