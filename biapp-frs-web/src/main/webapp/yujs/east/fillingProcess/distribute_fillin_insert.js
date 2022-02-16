//初始化界面,菜单配置路径是【/frs/yufreejs?js=/yujs/east/fillingProcess/distribute_main.js】
var status = "";
function AfterInit(){
	JSPFree.createBillCard("d1","/biapp-east/freexml/east/fillingProcess/east_filling_process_child_CODE1.xml",["保存/onSave/icon-p21"],null);
}
//页面加载结束后
function AfterBodyLoad(){
	var jsn_result = JSPFree.doClassMethodCall("com.yusys.east.process.service.EastCrProcessBSDMO", "isHaveRptOrgNo", null);
	if (jsn_result.code != 'success') {
		JSPFree.setBillCardValues(d1_BillCard, {distribute_type: '2' });
		// 隐藏网点机构
		JSPFree.setBillCardItemEditable(d1_BillCard,"distribute_type", false);
	}
}
/**
 * 保存
 * @return {[type]} [description]
 */
function onSave(){
	var jso_cardData = JSPFree.getBillCardFormValue(d1_BillCard);
	if(jso_cardData.data_dt == null || jso_cardData.data_dt == ""){
		JSPFree.alert("数据日期，不能为空！");
		return;
	}
	if(jso_cardData.tab_name_en == null || jso_cardData.tab_name_en == ""){
		JSPFree.alert("表名，不能为空！");
		return;
	}
	if(jso_cardData.distribute_type == null || jso_cardData.distribute_type == ""){
		JSPFree.alert("下发分类，不能为空！");
		return;
	}
	if(jso_cardData.org_no == null || jso_cardData.org_no == ""){
		JSPFree.alert("填报机构，不能为空！");
		return;
	}
	var par = {dataDt: jso_cardData.data_dt, tabNameEn: jso_cardData.tab_name_en, distributeType: jso_cardData.distribute_type, orgNo: jso_cardData.org_no}
	var jsn_result = JSPFree.doClassMethodCall("com.yusys.east.process.service.EastCrProcessBSDMO", "insertFill", par);
	if (jsn_result.code == 'success') {
		JSPFree.closeDialog(jsn_result);  //关闭窗口,并有返回值
	} else {
		JSPFree.alert(jsn_result.msg);
	}
}

/**
 * 取消
 * @return {[type]} [description]
 */
function onCancel(){
	JSPFree.closeDialog();
}
