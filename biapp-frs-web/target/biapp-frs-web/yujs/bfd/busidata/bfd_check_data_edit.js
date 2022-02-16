/**
 *
 * <pre>
 * Title:【报表处理】【报表数据维护】
 * Description:单条数据检核
 * </pre>
 * @author wangxy31
 * @version 1.00.00
 * @date 2020年8月6日
 */

var rid = "";
var type = ""; // 新增Add、修改Edit
var template = ""; // 模板路径
var tabname = "";
var tabnameen = "";
var jso_listData = "";
var str_ds = "";
var org_no = "";
var org_class = "";
var old_data = "";
function AfterInit() {
	type = jso_OpenPars.type;
	tabname = jso_OpenPars.tabname;
	tabnameen = jso_OpenPars.tabnameen;
	str_ds = jso_OpenPars.str_ds;
    org_no = jso_OpenPars.org_no;
    org_class = jso_OpenPars.org_class;

	var jso_listData = self.jso_OpenPars2; // 列表中传入的数据

	rid = jso_listData.rid;
	template = "Class:com.yusys.bfd.business.service.BfdModelTempletBuilder.getTemplet('" + tabname + "','" + tabnameen + "','" + str_LoginUserOrgNo + "')";
    
	JSPFree.createBillCard("d1",template,["保存/onSave/icon-p21","取消/onCancel/icon-undo"],null);
	JSPFree.execBillCardDefaultValueFormula(d1_BillCard);

	if (type == "Add") {
		JSPFree.createBillCard("d1",template,["保存/onSave/icon-p21","取消/onCancel/icon-undo"],null);
		JSPFree.execBillCardDefaultValueFormula(d1_BillCard);
	} else if (type == "Edit") {
		JSPFree.createBillCard("d1",template,["保存/onSave/icon-p21","取消/onCancel/icon-undo"],null);
		JSPFree.execBillCardDefaultValueFormula(d1_BillCard);

		d1_BillCard.OldData = jso_listData;
		// 赋值
		JSPFree.queryBillCardData(d1_BillCard, "rid = '"+rid+"'");
		old_data = JSPFree.getBillCardFormValue(d1_BillCard);
	} else if (type == "View") {
		JSPFree.createBillCard("d1",template,["取消/onCancel/icon-undo"],null);
		JSPFree.execBillCardDefaultValueFormula(d1_BillCard);
		
		d1_BillCard.OldData = jso_listData;
		// 赋值
		JSPFree.queryBillCardData(d1_BillCard, "rid = '"+rid+"'");
	}

}

/**
 * 页面初始化后
 * @returns
 */
function AfterBodyLoad() {
	// 表单所有字段设置帮助
	JSPFree.setBillCardLabelHelptip(d1_BillCard);
	// 如果是查看页面，要进行置灰，不可编辑
	if (type == "View") {
		JSPFree.setBillCardItemEditable(d1_BillCard, "*", false);
	}
	var billCardItemValue = JSPFree.getBillCardItemValue(d1_BillCard,"rpt_org_no");
	if (billCardItemValue==null || billCardItemValue =="" || typeof billCardItemValue == "undefined") {
		if (org_class == BfdFreeUtil.getBfdOrgClass().zh) {
			// 如果是总行，则进行赋值
			JSPFree.setBillCardItemEditable(d1_BillCard, "rpt_org_no", true);
			JSPFree.setBillCardItemValue(d1_BillCard, "rpt_org_no", org_no);
		} else if (org_class == BfdFreeUtil.getBfdOrgClass().fh) {
			// 如果是分行，则进行置灰，并赋值
			JSPFree.setBillCardItemEditable(d1_BillCard, "rpt_org_no", false);
			JSPFree.setBillCardItemValue(d1_BillCard, "rpt_org_no", org_no);
		}
	}

	if(tabnameen == "BFD_102_GRKHXX" || tabnameen == "BFD_201_CLGRDK" || tabnameen == "BFD_201_GRDKFS" || tabnameen == "BFD_202_CLGRCK" || tabnameen == "BFD_202_GRCKFS"){
		JSPFree.setBillCardItemEditable(d1_BillCard, "id_no", false);
	}
	if(tabnameen == "BFD_201_DBHTXX"){
		JSPFree.setBillCardItemEditable(d1_BillCard, "guar_id_no", false);
	}
	if(tabnameen == "BFD_201_CLWTDK" || tabnameen == "BFD_201_WTDKFS"){
		JSPFree.setBillCardItemEditable(d1_BillCard, "id_no", false);
		JSPFree.setBillCardItemEditable(d1_BillCard, "client_id_no", false);
	}
}

/**
 * 保存
 * 保存之前，先判断是否要进行单条数据检核操作
 * @return {[type]} [description]
 */
var saveFlag;
function onSave() {
	var data_dt = JSPFree.getBillCardItemValue(d1_BillCard, "data_dt");
	var org_no = JSPFree.getBillCardItemValue(d1_BillCard, "rpt_org_no");
	//判断当前数据是否被锁定
	var lock_data = JSPFree.doClassMethodCall("com.yusys.bfd.process.service.BfdLockData", "getStatus", {tab_name:tabname,data_dt:data_dt,org_no:org_no,type:'2'});
	if(lock_data.status == "锁定"){
		$.messager.alert('提示', '当前日期的表数据已被锁定，无法操作！', 'warning');
		return;
	}

	//证件号脱敏
	//个人客户信息、存量个人贷款、个人贷款发生额信息、存量个人存款信息、个人存款发生额信息
	if(tabnameen == "BFD_102_GRKHXX" || tabnameen == "BFD_201_CLGRDK" || tabnameen == "BFD_201_GRDKFS" || tabnameen == "BFD_202_CLGRCK" || tabnameen == "BFD_202_GRCKFS"){
		var orig_id_no = JSPFree.getBillCardItemValue(d1_BillCard, "orig_id_no");
		var id_type = JSPFree.getBillCardItemValue(d1_BillCard, "id_type");
		var md5 = JSPFree.doClassMethodCall("com.yusys.bfd.singlecheck.BfdSingleCheckBS", "md5", {id_no:orig_id_no,id_type:id_type});
		if(id_type != '' && id_type != 'A01'){
			JSPFree.setBillCardItemValue(d1_BillCard,"id_no", md5.id_no_md5);
		} else {
			JSPFree.setBillCardItemValue(d1_BillCard,"id_no", orig_id_no);
		}
	}

	//金融机构稳企业保就业信息
	if(tabnameen == "BFD_101_WQYBJY"){
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
	if(tabnameen == "BFD_201_DBHTXX"){
		var orig_id_no = JSPFree.getBillCardItemValue(d1_BillCard, "orig_guar_id_no");
		var id_type = JSPFree.getBillCardItemValue(d1_BillCard, "guar_id_type");
		var md5 = JSPFree.doClassMethodCall("com.yusys.bfd.singlecheck.BfdSingleCheckBS", "md5", {id_no:orig_id_no,id_type:id_type});
		if(id_type != '' && id_type != 'A01'){
			JSPFree.setBillCardItemValue(d1_BillCard,"guar_id_no", md5.id_no_md5);
		} else {
			JSPFree.setBillCardItemValue(d1_BillCard,"guar_id_no", orig_id_no);
		}
	}
	//存量委托贷款表、委托贷款发生额信息
	if(tabnameen == "BFD_201_CLWTDK" || tabnameen == "BFD_201_WTDKFS"){
		var orig_id_no = JSPFree.getBillCardItemValue(d1_BillCard, "orig_id_no");
		var orig_client_id_no = JSPFree.getBillCardItemValue(d1_BillCard, "orig_client_id_no");
		var id_type = JSPFree.getBillCardItemValue(d1_BillCard, "id_type");
		var client_id_type = JSPFree.getBillCardItemValue(d1_BillCard, "client_id_type");
		var md5 = JSPFree.doClassMethodCall("com.yusys.bfd.singlecheck.BfdSingleCheckBS", "md5", {id_no:orig_id_no,id_type:id_type});
		var client_md5 = JSPFree.doClassMethodCall("com.yusys.bfd.singlecheck.BfdSingleCheckBS", "md5", {id_no:orig_client_id_no,id_type:client_id_type});
		if(id_type != '' && id_type != 'A01'){
			JSPFree.setBillCardItemValue(d1_BillCard,"id_no", md5.id_no_md5);
		} else {
			JSPFree.setBillCardItemValue(d1_BillCard,"id_no", orig_id_no);
		}
		if(client_id_type != '' && client_id_type != 'A01'){
			JSPFree.setBillCardItemValue(d1_BillCard,"client_id_no", client_md5.id_no_md5);
		} else {
			JSPFree.setBillCardItemValue(d1_BillCard,"client_id_no", orig_client_id_no);
		}
	}

	// 是否进行单条数据检核操作
	var jso_check = JSPFree.doClassMethodCall("com.yusys.bfd.common.service.BfdCommonBS", 
			"getBfdCheckProperties", {});
	var ischeck = jso_check.ischeck;
	if ("Y" == ischeck) {
		if(type == "Add" && !saveFlag) {
			var backValue = JSPFree.editTableCheckData(d1_BillCard, type, tabname, tabnameen.toUpperCase(),str_ds,"BFD");
			if (backValue == "" || "undifind" == backValue) {
				return;
			} else if (backValue == "OK") {
				JSPFree.setBillCardValues(d1_BillCard, {rid:FreeUtil.getUUIDFromServer()});
				JSPFree.setBillCardValues(d1_BillCard, {data_ources: BfdFreeUtil.getDataOurces().page_entry});
				saveFlag = JSPFree.doBillCardInsert(d1_BillCard, null);
				if (saveFlag) {
					var _par={tab_name_en:tabnameen,data_dt:data_dt,number:1,source_type:BfdFreeUtil.getSourceType().page_entry,rpt_org_no:JSPFree.getBillCardItemValue(d1_BillCard, "rpt_org_no")};
					JSPFree.doClassMethodCall("com.yusys.bfd.dataoperation.BfdStatisticalOperation","statisticalOperationForForm",_par)
					var par = {modifiedType:BfdFreeUtil.getModifiedType().insert, newData: JSPFree.getBillCardFormValue(d1_BillCard), tabName: tabname,opeMod:BfdFreeUtil.getOpeMod().bbsjwh}
					JSPFree.doClassMethodCall("com.yusys.bfd.business.service.BfdBusinessBS","saveRecord",par)
					JSPFree.closeDialog("CHECK_OK");
				}
			} else if (backValue == "Fail") {
				return;
			}
		} else {
			var backValue = JSPFree.editTableCheckData(d1_BillCard, type, tabname, tabnameen.toUpperCase(),str_ds,"BFD");
			if (backValue == "" || "undifind" == backValue) {
				return;
			} else if (backValue == "OK") {
				var data_ources = JSPFree.getBillCardItemValue(d1_BillCard,'data_ources');
				var data_modify = JSPFree.getBillCardItemValue(d1_BillCard,'data_modify');
				var modifyFlag = false;

				if (data_ources==BfdFreeUtil.getDataOurces().etl_process && data_modify != BfdFreeUtil.getDataModefy().data_maintenance){
					JSPFree.setBillCardItemValue(d1_BillCard,'data_modify',BfdFreeUtil.getDataModefy().data_maintenance);
					modifyFlag = true;
				}
				d1_BillCard.templetVO.templet_option.pkname = "DATA_DT,RPT_ORG_NO,RID";
				saveFlag = JSPFree.doBillCardUpdate(d1_BillCard, null);
				if (saveFlag) {
					if (modifyFlag){
						var _par={tab_name_en:tabnameen,data_dt:data_dt,number:1,source_type:BfdFreeUtil.getSourceType().data_maintenance,rpt_org_no:JSPFree.getBillCardItemValue(d1_BillCard, "rpt_org_no")};
						JSPFree.doClassMethodCall("com.yusys.bfd.dataoperation.BfdStatisticalOperation","statisticalOperationForForm",_par)
					}
					var par = {modifiedType:BfdFreeUtil.getModifiedType().update,oldData: old_data, newData: JSPFree.getBillCardFormValue(d1_BillCard), tabName: tabname ,opeMod:BfdFreeUtil.getOpeMod().bbsjwh}
					JSPFree.doClassMethodCall("com.yusys.bfd.business.service.BfdBusinessBS","saveRecord",par)
					JSPFree.closeDialog("CHECK_OK");

				}
			} else if (backValue == "Fail") {
				return;
			}
		}
	} else {
		if(type == "Add" && !saveFlag) {
			JSPFree.setBillCardValues(d1_BillCard, {rid:FreeUtil.getUUIDFromServer()});
			JSPFree.setBillCardValues(d1_BillCard, {data_ources:BfdFreeUtil.getDataOurces().page_entry});
			saveFlag = JSPFree.doBillCardInsert(d1_BillCard, null);
			if (saveFlag) {
				var _par={tab_name_en:tabnameen,data_dt:data_dt,number:1,source_type:BfdFreeUtil.getSourceType().page_entry,rpt_org_no:JSPFree.getBillCardItemValue(d1_BillCard, "rpt_org_no")};
				JSPFree.doClassMethodCall("com.yusys.bfd.dataoperation.BfdStatisticalOperation","statisticalOperationForForm",_par)
				var par = {modifiedType:BfdFreeUtil.getModifiedType().insert, newData: JSPFree.getBillCardFormValue(d1_BillCard), tabName: tabname,opeMod:BfdFreeUtil.getOpeMod().bbsjwh}
				JSPFree.doClassMethodCall("com.yusys.bfd.business.service.BfdBusinessBS","saveRecord",par)
				JSPFree.closeDialog("OK");
			}
		} else {
			var data_ources = JSPFree.getBillCardItemValue(d1_BillCard,'data_ources');
			var data_modify = JSPFree.getBillCardItemValue(d1_BillCard,'data_modify');
			var modifyFlag = false;

			if (data_ources=="01" && data_modify !="01"){
				JSPFree.setBillCardItemValue(d1_BillCard,'data_modify',BfdFreeUtil.getDataModefy().data_maintenance);

				modifyFlag = true;
			}
			d1_BillCard.templetVO.templet_option.pkname = "DATA_DT,RPT_ORG_NO,RID";
			saveFlag = JSPFree.doBillCardUpdate(d1_BillCard, null);
			if (saveFlag) {
				if (modifyFlag){
					var _par={tab_name_en:tabnameen,data_dt:data_dt,number:1,source_type:BfdFreeUtil.getSourceType().data_maintenance,rpt_org_no:JSPFree.getBillCardItemValue(d1_BillCard, "rpt_org_no")};
					JSPFree.doClassMethodCall("com.yusys.bfd.dataoperation.BfdStatisticalOperation","statisticalOperationForForm",_par)
				}
				var par = {modifiedType:BfdFreeUtil.getModifiedType().update,oldData: old_data, newData: JSPFree.getBillCardFormValue(d1_BillCard),tabName: tabname,opeMod:BfdFreeUtil.getOpeMod().bbsjwh}
				JSPFree.doClassMethodCall("com.yusys.bfd.business.service.BfdBusinessBS","saveRecord",par)
				JSPFree.closeDialog("OK");
			}
		}
	}
}

/**
 * 取消
 * @return {[type]} [description]
 */
function onCancel() {
	JSPFree.closeDialog();
}