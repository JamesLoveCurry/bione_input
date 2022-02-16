//初始化界面,菜单配置路径是【/frs/yufreejs?js=/yujs/east/check_data_edit.js】
var rid = "";
var type = ""; // 新增Add、修改Edit
var template = ""; // 模板路径
var tabname = "";
var tabnameen = "";
var jso_listData = "";
var str_ds = "";
var disType = "";
var dataDt = "";
var orgNo = "";
var rptOrgNo ="";
var distributeType = "";
var old_data = "";
function AfterInit(){
	type = jso_OpenPars.type;
	template = jso_OpenPars.templetcode;
	tabname = jso_OpenPars.tabname;
	tabnameen = jso_OpenPars.tabnameen;
	jso_par = jso_OpenPars.jso_par;
	str_ds = jso_OpenPars.str_ds;
	disType = jso_OpenPars.disType;
	dataDt = jso_OpenPars.dataDt;
	orgNo = jso_OpenPars.orgNo;
	rptOrgNo = jso_OpenPars.rptOrgNo;
	distributeType = jso_OpenPars.distributeType;
	var jso_listData = self.jso_OpenPars2; // 列表中传入的数据
	JSPFree.createBillCard("d1",template,["保存/onSave/icon-p21","取消/onCancel/icon-undo"],null);
	d1_BillCard.OldData = jso_listData;
	rid = jso_listData.rid;
	//赋值
	JSPFree.setBillCardValues(d1_BillCard, jso_listData);
	old_data = JSPFree.getBillCardFormValue(d1_BillCard);
	d1_BillCard.templetVO.templet_option.ds = str_ds; // 数据源
}
/**
 * 页面初始化后
 * @returns
 */
function AfterBodyLoad() {
	// 表单所有字段设置帮助
	JSPFree.setBillCardLabelHelptip(d1_BillCard);
	if (disType == '1' || disType =='2') {
		showErrorFiled();
	}
	hidden();
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
	JSPFree.setBillCardItemValue(d1_BillCard, "proc_sts", "2");
	JSPFree.setBillCardItemValue(d1_BillCard, "is_check", "0");
	JSPFree.setBillCardItemValue(d1_BillCard, "is_issued_check", "0");
	// 是否进行单条数据检核操作
	var jso_check = JSPFree.doClassMethodCall("com.yusys.east.common.service.EastCommonBS",
		"getEastCheckProperties", {});
	var ischeck = jso_check.ischeck;
	debugger;
	if ("Y" == ischeck) {
		var backValue = JSPFree.editTableCheckData(d1_BillCard, type, tabname, tabnameen.toUpperCase(), str_ds, "1");
		if (backValue == "" || "undifind" == backValue) {
			return;
		} else if (backValue == "OK") {
			var oldData = d1_BillCard.OldData;
			var jsoData = JSPFree.getBillCardFormValue(d1_BillCard); // 取得新数据
			//
			var _rt = JSPFree.doClassMethodCall("com.yusys.east.process.service.EastCrProcessBSDMO","doUpdate",{rid: jsoData.rid, ds: str_ds, tabNameEn: tabnameen});
			if (_rt.code == 'success') {
				// 修改
				JSPFree.doBillCardUpdate(d1_BillCard, null);

				var par = {modifiedType:EastFreeUtil.getModifiedType().update,oldData:old_data, newData: JSPFree.getBillCardFormValue(d1_BillCard), tabName: tabname}
				JSPFree.doClassMethodCall("com.yusys.east.process.service.EastCrProcessBSDMO","saveRecord",par);
			} else {
				// 新增
				JSPFree.doBillCardInsert(d1_BillCard, null);

				var par = {modifiedType:EastFreeUtil.getModifiedType().update,oldData:old_data, newData: JSPFree.getBillCardFormValue(d1_BillCard), tabName: tabname}
				JSPFree.doClassMethodCall("com.yusys.east.process.service.EastCrProcessBSDMO","saveRecord",par);
			}
			//关闭窗口,并返回数据
			var jso_rt = getJsonResultParam(oldData);
			JSPFree.closeDialog(jso_rt);
		} else if (backValue == "Fail") {
			return;
		}
	} else {
		var oldData = d1_BillCard.OldData;
		var jsoData = JSPFree.getBillCardFormValue(d1_BillCard); // 取得新数据
		//
		var _rt = JSPFree.doClassMethodCall("com.yusys.east.process.service.EastCrProcessBSDMO","doUpdate",{rid: jsoData.rid, ds: str_ds, tabNameEn: tabnameen});
		if (_rt.code == 'success') {
			// 修改
			JSPFree.doBillCardUpdate(d1_BillCard, null);

			var par = {modifiedType:EastFreeUtil.getModifiedType().update,oldData:old_data, newData: JSPFree.getBillCardFormValue(d1_BillCard), tabName: tabname}
			JSPFree.doClassMethodCall("com.yusys.east.process.service.EastCrProcessBSDMO","saveRecord",par);
		} else {
			// 新增
			JSPFree.doBillCardInsert(d1_BillCard, null);
			var par = {modifiedType:EastFreeUtil.getModifiedType().update,oldData:old_data, newData: JSPFree.getBillCardFormValue(d1_BillCard), tabName: tabname}
			JSPFree.doClassMethodCall("com.yusys.east.process.service.EastCrProcessBSDMO","saveRecord",par);
		}
		//关闭窗口,并返回数据
		var jso_rt = getJsonResultParam(oldData);
		JSPFree.closeDialog(jso_rt);
	}

}
function getJsonResultParam(oldData) {
	var jso_rt = {
		result: "校验并保存成功"

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

/**
 *  展示错误信息
 */
function showErrorFiled() {
	var jsoRt = JSPFree.doClassMethodCall("com.yusys.east.process.service.EastCrProcessBSDMO", "showErrorFiled", {
		rptOrgNo: rptOrgNo,
		orgNo: orgNo,
		dataDt: dataDt,
		type: disType,
		tabNameEn: tabnameen,
		rid: rid,
		dsName: str_ds
	});
	var rules = jsoRt.data;
	var jso_data1 = JSPFree.getHashVOs("select col_name_en, col_name from east_cr_col where col_name in " +
		"(select col_name from east_cr_rule where tab_name = '"+tabname+"' AND (rule_sts ='Y' or rule_sts is null) " +
		"AND id IN ("+rules+")) and tab_name = '"+tabname+"'");

	var colnames = [];
	if (jso_data1 != null && jso_data1 != "" && jso_data1.length > 0) {
		for (var j=0;j<jso_data1.length;j++) {
			var colname = jso_data1[j].col_name;
			var colname_en = jso_data1[j].col_name_en;
			var jso_data2 = JSPFree.getHashVOs("select rule_name from east_cr_rule where (rule_sts='Y' or rule_sts is null) and tab_name = " +
				"'"+tabname+"' and col_name = '"+colname+"' and id in ("+rules+")");

			var problemmsg = "";
			if (jso_data2 != null && jso_data2 != "" && jso_data2.length>0) {
				for (var k=0;k<jso_data2.length;k++) {
					problemmsg = problemmsg + (k+1) + "." + jso_data2[k].rule_name + "<br>";
				}
			}
			JSPFree.setBillCardItemWarnMsg(d1_BillCard, colname_en.toLowerCase(), problemmsg);
			JSPFree.setBillCardItemColor(d1_BillCard, colname_en.toLowerCase(), "yellow");
		}
	}

}
/**
 * 隐藏部分展示框
 */
function hidden() {
	// 隐藏日历
	JSPFree.setBillCardValues(d1_BillCard, {cjrq: dataDt,kjrq: dataDt, org_no: rptOrgNo});
	JSPFree.setBillCardItemEditable(d1_BillCard,"cjrq",  false);
	JSPFree.setBillCardItemEditable(d1_BillCard,"kjrq",  false);
	// 隐藏法人机构
	JSPFree.setBillCardItemEditable(d1_BillCard,"org_no",  false);
	// 如果是按照网点，则也隐藏网点
	if (distributeType == '2') {
		JSPFree.setBillCardValues(d1_BillCard, {issued_no: orgNo });
		// 隐藏网点机构
		JSPFree.setBillCardItemEditable(d1_BillCard,"issued_no", false);
	}
}