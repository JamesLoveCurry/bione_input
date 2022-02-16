//初始化界面,菜单配置路径是【/frs/yufreejs?js=/yujs/crrs/collection/addSingle.js】
var tabname = "";
var tabnameen = "";
var ds = "";
function AfterInit(){
	var str_className = jso_OpenPars.templet;
	
	tabname = jso_OpenPars.tabname;
	tabnameen = jso_OpenPars.tabnameen;
	ds = jso_OpenPars.ds;
	
	JSPFree.createBillCard("d1",str_className,["保存/onSave/icon-p21","取消/onCancel/icon-undo"],null);
	
	//先执行卡片默认值公式
	JSPFree.execBillCardDefaultValueFormula(d1_BillCard);
}

//加载完后
function AfterBodyLoad(){
	//设置提示，当itemname的长度大于等于6的时候，鼠标放上去，会提示全部的名称！！非常重要
	FreeUtil.setBillCardLabelHelptip(d1_BillCard); //必须写在最后一行
}

/**
 * 保存
 * @return {[type]} [description]
 */
var saveFlag;
function onSave(){
	var str_date = "";
	//若页面上定义了data_dt或者trans_date，那么这一项就肯定不等于undefined。
	var data_dt = JSPFree.getBillCardItemValue(d1_BillCard,"data_dt");
	var trans_date = JSPFree.getBillCardItemValue(d1_BillCard,"trans_date");
	if(data_dt!=undefined){ //如果定义了data_dt，则把data_dt的值赋给str_date
		str_date = data_dt;
	}else{ //如果定义了trans_date，则把trans_date的值赋给str_date
		str_date = trans_date;
	}
	
	//日期是必输项
	str_date = str_date.substring(0,4) + "-" + str_date.substring(4,6) + "-" + str_date.substring(6,8);
	
	var jso_rt = JSPFree.doClassMethodCall("com.yusys.pscs.lock.service.PscsLockBSDMO", "checkDateLock", {data_dt:str_date,last_date:getLastDayOfMonth(str_date)});
	if (jso_rt != null && jso_rt.status == "锁定") {
		JSPFree.alert("日期[" + str_date + "]的数据已经被锁定,不能保存!<br>请在【数据处理->数据锁定】中进行设置");
		return;
	}
	
	saveFlag = JSPFree.doBillCardInsert(d1_BillCard,null);
	if (saveFlag) {
		JSPFree.closeDialog("校验并保存成功");
	}
	
	//20200414 liangzy5 这里先不调用单条检核，因为还没有规则
	/*var backValue = JSPFree.editTableCheckData(d1_BillCard, "Add", tabname, tabnameen.toUpperCase(),ds,"5");
	if (backValue == "" || "undifind" == backValue) {
		return;
	} else if (backValue == "OK") {
		saveFlag = JSPFree.doBillCardInsert(d1_BillCard,null);
		JSPFree.closeDialog("校验并保存成功");
	} else if (backValue == "Fail") {
		return;
	}*/
}

/**
 * 参数是10位的数据日期
 * 获取月底日期，返回一个10位的数据日期
 */
function getLastDayOfMonth(data_dt){
    //录入日期
    var year = data_dt.substring(0,4);
    var month = data_dt.substring(5,7);
    
    var lastDay = new Date(year,month,0);
    
    return year+"-"+month+"-"+lastDay.getDate();
}

/**
 * 取消
 * @return {[type]} [description]
 */
function onCancel(){
	JSPFree.closeDialog();
}