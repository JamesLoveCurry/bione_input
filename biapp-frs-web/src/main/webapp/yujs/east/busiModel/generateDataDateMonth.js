//初始化界面
var jso_cardData = null;
function AfterInit(){
	JSPFree.createBillCard("d1","east/generate_data_date_month",["确定/onConfirm","取消/onCancel"]);
}

//确认
function onConfirm(){
	jso_cardData = JSPFree.getBillCardFormValue(d1_BillCard);
	if(jso_cardData.data_dt_1==null || jso_cardData.data_dt_1==""){
		JSPFree.alert("采集日期不能为空!");
		return;
	}else{
		if(jso_cardData.data_dt_1.substring(0,7) == getCurrentMonth()){
			JSPFree.alert("请选择小于当前月的月末日期!");
			return;
		}else{
			if(jso_cardData.data_dt_1 != getLastMonthAndDay(jso_cardData.data_dt_1)){
				JSPFree.alert("请选择月末日期!");
				return;
			}
		}
	}
	
	JSPFree.openDialogAndCloseMe2("生成所有表数据","/yujs/east/busiModel/generateDataMonth_start.js",750,350,{"data_dt_1":jso_cardData.data_dt_1});
}

function onCancel(){
	JSPFree.closeDialog(null);
}
/**
 * 获取当前年月
 * @returns
 */
function getCurrentMonth(){
	var nowDate = new Date();
	var year = nowDate.getFullYear();
	var month = nowDate.getMonth()+1;
	if(month < 10){
		month = "0"+month;
	}
	return year+"-"+month;
}

/**
 * 获取月底日期
 */
function getLastMonthAndDay(data_dt_1){
    //录入日期
    var year = data_dt_1.substring(0,4);
    var month = data_dt_1.substring(5,7);
    
    var lastDay = new Date(year,month,0);
    
    return year+"-"+month+"-"+lastDay.getDate();
}