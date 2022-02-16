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
var dataDt = "";
var rptOrgNo = "";
function AfterInit() {
	type = jso_OpenPars.type;
	tabname = jso_OpenPars.tabname;
	tabnameen = jso_OpenPars.tabnameen;
	str_ds = jso_OpenPars.str_ds;
    org_no = jso_OpenPars.org_no;
    org_class = jso_OpenPars.org_class;

	var jso_listData = self.jso_OpenPars2; // 列表中传入的数据

	rid = jso_listData.rid;
	dataDt = jso_listData.data_dt;
	rptOrgNo = jso_listData.rpt_org_no;
	template = "Class:com.yusys.imas.business.service.ImasModelTempletBuilder.getTemplet('" + tabname + "','" + tabnameen + "','" + str_LoginUserOrgNo + "')";
    
	JSPFree.createBillCard("d1",template,["保存/onSave/icon-p21","取消/onCancel/icon-undo"],null);
	JSPFree.execBillCardDefaultValueFormula(d1_BillCard);

	if (type == "Add") {
		JSPFree.createBillCard("d1",template,["保存/onSave/icon-p21","取消/onCancel/icon-undo","重置/onReset/icon-reset1"],null);
		JSPFree.execBillCardDefaultValueFormula(d1_BillCard);
	} else if (type == "Edit") {
		JSPFree.createBillCard("d1",template,["保存/onSave/icon-p21","取消/onCancel/icon-undo","重置/onReset/icon-reset1"],null);
		JSPFree.execBillCardDefaultValueFormula(d1_BillCard);
		d1_BillCard.OldData = jso_listData;
		// 赋值
		JSPFree.queryBillCardData(d1_BillCard, " data_dt='" + dataDt + "' and rpt_org_no='"+rptOrgNo+"' and rid = '"+rid+"'");
		old_data = JSPFree.getBillCardFormValue(d1_BillCard);
	} else if (type == "View") {
		JSPFree.createBillCard("d1",template,["取消/onCancel/icon-undo"],null);
		JSPFree.execBillCardDefaultValueFormula(d1_BillCard);
		
		d1_BillCard.OldData = jso_listData;
		// 赋值
		JSPFree.queryBillCardData(d1_BillCard, " data_dt='" + dataDt + "' and rpt_org_no='"+rptOrgNo+"' and rid = '"+rid+"'");
	}

}

function onReset(){
	JSPFree.setBillCardItemClearValue(d1_BillCard, "*");
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
	if (billCardItemValue==null || billCardItemValue =="" || billCardItemValue == "undefined"){
		if (org_class == ImasFreeUtil.getImasOrgClass().zh || org_class == ImasFreeUtil.getImasOrgClass().fh) {
			// 如果是报送总行/报送分行，不进行置灰，并赋值
			JSPFree.setBillCardItemEditable(d1_BillCard, "rpt_org_no", true);
			JSPFree.setBillCardItemValue(d1_BillCard, "rpt_org_no", org_no);
		}
	}
	JSPFree.setBillCardItemEditable(d1_BillCard, "rid", false);
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
	var lock_data = JSPFree.doClassMethodCall("com.yusys.imas.process.service.ImasLockData", "getStatus", {tab_name:tabname,data_dt:data_dt,org_no:org_no,type:'2'});
	if(lock_data.status == "锁定"){
		$.messager.alert('提示', '当前日期的表数据已被锁定，无法操作！', 'warning');
		return;
	}

	// 是否进行单条数据检核操作
	var jso_check = JSPFree.doClassMethodCall("com.yusys.imas.common.service.ImasCommonBS",
			"getImasCheckProperties", {});
	var ischeck = jso_check.ischeck;
	if ("Y" == ischeck) {
		if(type == "Add" && !saveFlag) {
			var backValue = JSPFree.editTableCheckData(d1_BillCard, type, tabname, tabnameen.toUpperCase(),str_ds,"IMAS");
			if (backValue == "" || "undifind" == backValue) {
				return;
			} else if (backValue == "OK") {
				JSPFree.setBillCardValues(d1_BillCard, {rid:FreeUtil.getUUIDFromServer()});
				JSPFree.setBillCardValues(d1_BillCard, {data_ources: ImasFreeUtil.getDataOurces().page_entry});
				saveFlag = JSPFree.doBillCardInsert(d1_BillCard, null);
				if (saveFlag) {

					var _par={tab_name_en:tabnameen,data_dt:data_dt,number:1,source_type:ImasFreeUtil.getSourceType().page_entry,rpt_org_no:JSPFree.getBillCardItemValue(d1_BillCard, "rpt_org_no")};
					JSPFree.doClassMethodCall("com.yusys.imas.dataoperation.StatisticalOperation","statisticalOperationForForm",_par)
					var par = {modifiedType:ImasFreeUtil.getModifiedType().insert, newData: JSPFree.getBillCardFormValue(d1_BillCard), tabName: tabname,opeMod:ImasFreeUtil.getOpeMod().bbsjwh}
					JSPFree.doClassMethodCall("com.yusys.imas.business.service.ImasBusinessBS","saveRecord",par)
					JSPFree.closeDialog("CHECK_OK");
				}
			} else if (backValue == "Fail") {
				return;
			}
		} else {
			var backValue = JSPFree.editTableCheckData(d1_BillCard, type, tabname, tabnameen.toUpperCase(),str_ds,"IMAS");
			if (backValue == "" || "undifind" == backValue) {
				return;
			} else if (backValue == "OK") {
				var data_ources = JSPFree.getBillCardItemValue(d1_BillCard,'data_ources');
				var data_modify = JSPFree.getBillCardItemValue(d1_BillCard,'data_modify');
				var modifyFlag = false;

				if (data_ources==ImasFreeUtil.getDataOurces().etl_process && data_modify != ImasFreeUtil.getDataModefy().data_maintenance){
					JSPFree.setBillCardItemValue(d1_BillCard,'data_modify',ImasFreeUtil.getDataModefy().data_maintenance);
					modifyFlag = true;
				}
				d1_BillCard.templetVO.templet_option.pkname = "DATA_DT,RPT_ORG_NO,RID";
				saveFlag = JSPFree.doBillCardUpdate(d1_BillCard, null);
				if (saveFlag) {
					if (modifyFlag){
						var _par={tab_name_en:tabnameen,data_dt:data_dt,number:1,source_type:ImasFreeUtil.getSourceType().data_maintenance,rpt_org_no:JSPFree.getBillCardItemValue(d1_BillCard, "rpt_org_no")};
						JSPFree.doClassMethodCall("com.yusys.imas.dataoperation.StatisticalOperation","statisticalOperationForForm",_par)
					}
					var par = {modifiedType:ImasFreeUtil.getModifiedType().update,oldData: old_data, newData: JSPFree.getBillCardFormValue(d1_BillCard), tabName: tabname ,opeMod:ImasFreeUtil.getOpeMod().bbsjwh}
					JSPFree.doClassMethodCall("com.yusys.imas.business.service.ImasBusinessBS","saveRecord",par)
					JSPFree.closeDialog("CHECK_OK");

				}
			} else if (backValue == "Fail") {
				return;
			}
		}
	} else {
		if(type == "Add" && !saveFlag) {
			JSPFree.setBillCardValues(d1_BillCard, {rid:FreeUtil.getUUIDFromServer()});
			JSPFree.setBillCardValues(d1_BillCard, {data_ources:ImasFreeUtil.getDataOurces().page_entry});
			saveFlag = JSPFree.doBillCardInsert(d1_BillCard, null);
			if (saveFlag) {
				var _par={tab_name_en:tabnameen,data_dt:data_dt,number:1,source_type:ImasFreeUtil.getSourceType().page_entry,rpt_org_no:JSPFree.getBillCardItemValue(d1_BillCard, "rpt_org_no")};
				JSPFree.doClassMethodCall("com.yusys.imas.dataoperation.StatisticalOperation","statisticalOperationForForm",_par)
				var par = {modifiedType:ImasFreeUtil.getModifiedType().insert, newData: JSPFree.getBillCardFormValue(d1_BillCard), tabName: tabname,opeMod:ImasFreeUtil.getOpeMod().bbsjwh}
				JSPFree.doClassMethodCall("com.yusys.imas.business.service.ImasBusinessBS","saveRecord",par)
				JSPFree.closeDialog("OK");
			}
		} else {
			var data_ources = JSPFree.getBillCardItemValue(d1_BillCard,'data_ources');
			var data_modify = JSPFree.getBillCardItemValue(d1_BillCard,'data_modify');
			var modifyFlag = false;

			if (data_ources=="01" && data_modify !="01"){
				JSPFree.setBillCardItemValue(d1_BillCard,'data_modify',ImasFreeUtil.getDataModefy().data_maintenance);

				modifyFlag = true;
			}
			d1_BillCard.templetVO.templet_option.pkname = "DATA_DT,RPT_ORG_NO,RID";
			saveFlag = JSPFree.doBillCardUpdate(d1_BillCard, null);
			if (saveFlag) {
				if (modifyFlag){
					var _par={tab_name_en:tabnameen,data_dt:data_dt,number:1,source_type:ImasFreeUtil.getSourceType().data_maintenance,rpt_org_no:JSPFree.getBillCardItemValue(d1_BillCard, "rpt_org_no")};
					JSPFree.doClassMethodCall("com.yusys.imas.dataoperation.StatisticalOperation","statisticalOperationForForm",_par)
				}
				var par = {modifiedType:ImasFreeUtil.getModifiedType().update,oldData: old_data, newData: JSPFree.getBillCardFormValue(d1_BillCard),tabName: tabname,opeMod:ImasFreeUtil.getOpeMod().bbsjwh}
				JSPFree.doClassMethodCall("com.yusys.imas.business.service.ImasBusinessBS","saveRecord",par)
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