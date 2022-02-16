//初始化界面
var jso_cardData = null;
function AfterInit(){
	JSPFree.createBillCard("d1","east/generate_data_date_day",["确定/onConfirm","取消/onCancel"]);
}

//确认
function onConfirm(){
	jso_cardData = JSPFree.getBillCardFormValue(d1_BillCard);
	if(jso_cardData.data_dt_1==null || jso_cardData.data_dt_1==""){
		JSPFree.alert("采集日期不能为空!");
		return;
	}
	
	JSPFree.openDialogAndCloseMe2("生成所有表数据","/yujs/east/busiModel/generateDataDay_start.js",750,350,{"data_dt_1":jso_cardData.data_dt_1});
}

function onCancel(){
	JSPFree.closeDialog(null);
}