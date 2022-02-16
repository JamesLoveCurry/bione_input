//初始化界面,菜单配置路径是【/frs/yufreejs?js=/yujs/east/58table.js】
function AfterInit(){
		JSPFree.createBillCard("d1","/biapp-pscs/freexml/pscs/datalock/pscs_choose_date.xml",["确定/onConfirm","取消/onCancel"]);
}

// 创建任务
function onConfirm(){
	var jso_rt = null;
	
	var jso_cardData = JSPFree.getBillCardFormValue(d1_BillCard);
	var data_dt = jso_cardData.data_dt;
	var type = jso_cardData.type;
	
	if(data_dt==null || data_dt==""){
		JSPFree.alert("数据日期不能为空!");
		return;
	}
	
	if(type==null || type==""){
		JSPFree.alert("频度不能为空!");
		return;
	}
	
	if(type=="2"){ //如果是按月锁定，则必须选择月末日期
		if(data_dt!=getLastDayOfMonth(data_dt)){
			JSPFree.alert("按月锁定必须选择月末日期!");
			return;
		}
	}else{ //如果是按日锁定，先判断是否已经创建当月的锁定任务
		var jso_data = JSPFree.getHashVOs("select data_dt from pscs_lock_data where data_dt='" + getLastDayOfMonth(data_dt) + "' and type='2'");
		if(jso_data.length>0){
			JSPFree.alert("已经存在当月的按月锁定任务，请先删除再操作!");
			return;
		}
	}
	// 同一个日期不能被重复建任务
	var jso_dt = JSPFree.getHashVOs("select data_dt from pscs_lock_data where data_dt = '"+ data_dt +"'");
	if(jso_dt.length>0){
		JSPFree.alert("已经存在当前日期的锁定任务，请勿提交重复日期或者先删除后再操作!");
		return;
	}
	
	jso_rt = JSPFree.doClassMethodCall("com.yusys.pscs.lock.service.PscsLockBSDMO", "createLockData", {data_dt:jso_cardData.data_dt,type:jso_cardData.type,operator:str_LoginUserCode,operator_name:str_LoginUserName});

	JSPFree.closeDialog(jso_rt);
}

/**
 * 获取月底日期
 */
function getLastDayOfMonth(data_dt){
    //录入日期
    var year = data_dt.substring(0,4);
    var month = data_dt.substring(5,7);
    
    var lastDay = new Date(year,month,0);
    
    return year+"-"+month+"-"+lastDay.getDate();
}

function onCancel(){
	JSPFree.closeDialog(null);
}