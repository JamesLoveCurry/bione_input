var type = null;
//初始化界面
function AfterInit() {
	type = jso_OpenPars.type;
	if (type == "addr") {
		JSPFree.createBillCard("d1","east/engineTask/east_cr_task_rule_addr",["保存/onSaveAddr/icon-ok"],null);
	} else {
		JSPFree.createBillCard("d1","east/engineTask/east_cr_task_CODE2",["保存/onSave/icon-ok"],null);
	}
}

/**
 * 保存
 * @return {[type]} [description]
 */
function onSave(){
	var str_date = JSPFree.getBillCardItemValue(d1_BillCard,"data_dt");
	var str_busi_type = 1;
	var str_busi_org_no = JSPFree.getHashVOs("select trim(ORG_NO) c from rpt_org_info where  org_type='04' and UP_ORG_NO = '0'");
	var issued_no = JSPFree.getBillCardItemValue(d1_BillCard,"issued_no");
	var jso_org = JSPFree.getHashVOs("select trim(org_class) t from rpt_org_info where org_no='"+str_busi_org_no+"' and org_type='04' ");
	if (!issued_no) {
		if(jso_org != null && jso_org.length > 0){
			if(jso_org[0].t=="总行" ) {
				str_busi_type = "1";
			}else{
				str_busi_type = "2";
			}
		}
	} else {
		var jso_org_iss = JSPFree.getHashVOs("select trim(org_class) t from rpt_org_info where org_no='"+issued_no+"' and org_type='04' ");
		if(jso_org_iss != null && jso_org_iss.length > 0){
			if(issued_no[0].t=="总行" ) {
				str_busi_type = "1";
			}else{
				str_busi_type = "2";
			}
		}
	}
	
	/*//校验是否重复入库
	var jso_data = JSPFree.getHashVOs("select count(*) c from east_cr_task where task_type='规则任务' and data_dt='" + str_date + "' and busi_type='"+str_busi_type+"' and busi_org_no='"+str_busi_org_no[0].c+"'");

	if(jso_data != null && jso_data.length > 0 && jso_data[0].c > 0){
		JSPFree.alert("日期[" + str_date + "]的数据已存在，请重新选择！");
		return;
	}*/
	JSPFree.setBillCardValues(d1_BillCard,{task_id:FreeUtil.getUUIDFromServer(),task_type:'规则任务',status:'初始化',create_tm:getNowFormatDate,busi_type:str_busi_type});
	var flag = JSPFree.doBillCardInsert(d1_BillCard);
	if(flag){
		JSPFree.closeDialog(flag);
	}
}
/**
 * 保存
 * @return {[type]} [description]
 */
function onSaveAddr(){
	var str_busi_type = 1;
	JSPFree.setBillCardValues(d1_BillCard,{task_id:FreeUtil.getUUIDFromServer(),task_type:'地区规则任务',status:'初始化',create_tm:getNowFormatDate,busi_type:str_busi_type});
	var flag = JSPFree.doBillCardInsert(d1_BillCard);
	if(flag){
		JSPFree.closeDialog(flag);
	}
}
function getNowFormatDate() {
    var date = new Date();
    var seperator1 = "-";
    var seperator2 = ":";
    //外国的月份都是从0开始的，所以+1
    var month = date.getMonth() + 1;
    var strDate = date.getDate();
    //1-9月用0补位
    if(month >=1 && month <=9){
        month = "0" + month;
    }
    //1-9日用0补位
    if(strDate >=0 && strDate <= 9){
        strDate = "0" + strDate;
    }
    //获取当前时间 yyyy-MM-dd HH:mm:ss
    var currentdate = date.getFullYear() + seperator1 + month + seperator1 + strDate + " " +date.getHours() + seperator2 + date.getMinutes() + seperator2 + date.getSeconds();
    
    return currentdate;
}