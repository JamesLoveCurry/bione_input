//初始化界面,菜单配置路径是【/frs/yufreejs?js=/yujs/east/check_data_edit.js】
var rid = "";
var type = ""; // 新增Add、修改Edit
var template = ""; // 模板路径
var tabname = "";
var tabnameen = "";
var jso_listData = "";
var str_ds = "";
function AfterInit(){
	rid = jso_OpenPars.rid;
	type = jso_OpenPars.type;
	template = jso_OpenPars.templetcode;
	tabname = jso_OpenPars.tabname;
	tabnameen = jso_OpenPars.tabnameen;
	jso_par = jso_OpenPars.jso_par;
	str_ds = jso_OpenPars.str_ds;
	var jso_listData = self.jso_OpenPars2; // 列表中传入的数据
	if (type == "Add") {
		JSPFree.createBillCard("d1",template,["保存/onSave/icon-p21","取消/onCancel/icon-undo"],null);
	} else if (type == "Edit") {
		JSPFree.createBillCard("d1",template,["保存/onSave/icon-p21","取消/onCancel/icon-undo"],null);
		d1_BillCard.OldData = jso_listData;
		//赋值
		JSPFree.setBillCardValues(d1_BillCard, jso_listData);
	} else if (type == "View") {
		JSPFree.createBillCard("d1",template,["取消/onCancel/icon-undo"],null);
		d1_BillCard.OldData = jso_listData;
		//赋值
		JSPFree.setBillCardValues(d1_BillCard, jso_listData);
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
}
/**
 * 保存
 * @return {[type]} [description]
 */
var saveFlag;
function onSave(){
	var _rt = this.encryption();//脱敏处理
	if (typeof(_rt)!="undefined"){
		if (_rt.status=="OK"){
			//数据插入表单，更新数据
			JSPFree.setBillCardValues(d1_BillCard, _rt.data);
		}else {
			$.messager.alert("提示",_rt.msg);
			return ;
		}
	}
	// 是否进行单条数据检核操作
	var jso_check = JSPFree.doClassMethodCall("com.yusys.east.common.service.EastCommonBS",
		"getEastCheckProperties", {});
	var ischeck = jso_check.ischeck;
	if ("Y" == ischeck) {
		if (type == "Add" && !saveFlag) {
			JSPFree.setBillCardValues(d1_BillCard, {rid:FreeUtil.getUUIDFromServer()});
			var backValue = JSPFree.editTableCheckData(d1_BillCard, type, tabname, tabnameen.toUpperCase(), str_ds, "1");
			if (backValue == "" || "undifind" == backValue) {
				return;
			} else if (backValue == "OK") {
				var oldData = d1_BillCard.OldData;
				saveFlag = JSPFree.doBillCardInsert(d1_BillCard, null);
				//关闭窗口,并返回数据
				var jso_rt = getJsonResultParam(oldData);
				JSPFree.closeDialog(jso_rt);
			} else if (backValue == "Fail") {
				return;
			}
		} else {
			var backValue = JSPFree.editTableCheckData(d1_BillCard, type, tabname, tabnameen.toUpperCase(), str_ds, "1");
			if (backValue == "" || "undifind" == backValue) {
				return;
			} else if (backValue == "OK") {
				var oldData = d1_BillCard.OldData;
				saveFlag = JSPFree.doBillCardUpdate(d1_BillCard, null);
				//关闭窗口,并返回数据
				var jso_rt = getJsonResultParam(oldData);
				JSPFree.closeDialog(jso_rt);
			} else if (backValue == "Fail") {
				return;
			}
		}
	} else {
		if (type == "Add" && !saveFlag) {
			var oldData = d1_BillCard.OldData;
			JSPFree.setBillCardValues(d1_BillCard, {rid:FreeUtil.getUUIDFromServer()});
			saveFlag = JSPFree.doBillCardInsert(d1_BillCard, null);
			//关闭窗口,并返回数据
			var jso_rt = getJsonResultParam(oldData);
			JSPFree.closeDialog(jso_rt);
		} else {
			var oldData = d1_BillCard.OldData;
			saveFlag = JSPFree.doBillCardUpdate(d1_BillCard, null);
			//关闭窗口,并返回数据
			var jso_rt = getJsonResultParam(oldData);
			JSPFree.closeDialog(jso_rt);
		}
	}

}
function getJsonResultParam(oldData) {
	var jso_rt = {
		result: "校验并保存成功",
		jso_formData: JSPFree.getBillCardFormValue(d1_BillCard),
		templetcode: d1_BillCard.templetVO.templet_option.templetcode,
		templetVO: d1_BillCard.templetVO,
		OldData: oldData,
		type: type
	};
	return jso_rt;
}
/**
 * 取消
 * @return {[type]} [description]
 */
function onCancel(){
	JSPFree.closeDialog();
}

/**
 * 脱敏处理
 * @returns {JSON}{_rt}
 */
function encryption(){
	var jso_formData = JSPFree.getBillCardFormValue(d1_BillCard);
	//判断是否有脱敏前的字段
	// var needEncryption = Object.keys(jso_formData).some(key=>key.toLowerCase().includes("_orig"));
	var arr = Object.keys(jso_formData);
	var needEncryption = false;
	for (let i = 0; i < arr.length; i++) {
		if(arr[i].toLowerCase().includes("_orig")){
			needEncryption = true;
			break;
		}
	}
	if (needEncryption){
		//后台处理数据
		var _par={jso_formData:jso_formData,tab_name_en:tabnameen};
		var _rt = JSPFree.doClassMethodCall("com.yusys.east.common.Encryption","replaceData",_par);
		return _rt;
	}

}