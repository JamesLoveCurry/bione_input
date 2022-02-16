//初始化界面
var tabName = "";
var tabNameEn = "";
var dataDt = "";
function AfterInit() {
	tabName = jso_OpenPars2.tabName;
	tabNameEn = jso_OpenPars2.tabNameEn;
	dataDt = jso_OpenPars2.dataDt;

	var str_className1 = "Class:com.yusys.wmp.business.model.service.WmpModelTempletBuilder.getTemplet('"+tabName+"','"+tabNameEn+"','"+str_LoginUserOrgNo+"','_R')";
	JSPFree.createBillCard("d1",str_className1,["保存/onSave/icon-p21","取消/onCancel/icon-undo"],null);
	
	JSPFree.setBillCardValues(d1_BillCard,jso_OpenPars2.json_data);
	d1_BillCard.OldData = jso_OpenPars2.json_data;
}

var col_arr = []; 
function AfterBodyLoad(){
	// 设置表单中的主键不可进行编辑
	var tabName = jso_OpenPars2.tabName;
	var jso_col = JSPFree.getHashVOs("select col_name_en from frs_wmp_cr_tab where tab_name='"+tabName+"' and is_pk='Y' order by col_no");
	if(jso_col != null && jso_col.length > 0){
		for (var i=0;i<jso_col.length;i++) {
			col_arr.push(jso_col[i].col_name_en);
			JSPFree.setBillCardItemEditable(d1_BillCard,jso_col[i].col_name_en.toLowerCase(),false);
		}
	}

	JSPFree.setBillCardItemEditable(d1_BillCard,"cjrq",false);
	JSPFree.setBillCardItemEditable(d1_BillCard,"kjrq",false);
	JSPFree.setBillCardItemEditable(d1_BillCard,"nbjgh",false);
}

/**
 * 保存
 * @return {[type]} [description]
 */
var saveFlag = "";
function onSave(){
	var str_ds = "";
	var ds_name = JSPFree.getHashVOs("select ds_name from frs_wmp_cr_tab where tab_name='"+tabName+"'");
	if (ds_name != null && ds_name.length > 1) {
		str_ds = ds_name[0].da_name;
	}
	
	var backValue = JSPFree.editTableCheckData(d1_BillCard, "Edit", tabName, tabNameEn.toUpperCase(),str_ds,"1");
	if (backValue == "" || "undifind" == backValue) {
		return;
	} else if (backValue == "OK") {
		saveFlag = JSPFree.doBillCardUpdate(d1_BillCard,null);
		
		if (saveFlag == true) {
			// 此处在更新一个数据表状态值
			var dd = {tabNameEn:jso_OpenPars2.tabNameEn,colArry:col_arr,status:'1',jsonData:jso_OpenPars2.json_data};
			var jsn_result = JSPFree.doClassMethodCall("com.yusys.wmp.process.service.WmpCrProcessBS", "updateTemplateTabs", dd);
			
			if (jsn_result.msg == 'OK') {
				JSPFree.closeDialog(true);
			}
		}
	} else if (backValue == "Fail") {
		return;
	}
}

function onCancel() {
	JSPFree.closeDialog(null);
}