//初始化界面,菜单配置路径是【/frs/yufreejs?js=/yujs/east/fillingProcess/distribute_main.js】
var rid = "";
var type = ""; // 新增Add、修改Edit
var template = ""; // 模板路径
var tabname = "";
var tabnameen = "";
var defaultValue = "";
function AfterInit(){
	rid = jso_OpenPars.rid;
	type = jso_OpenPars.type;
	template = jso_OpenPars.template;
	tabname = jso_OpenPars.tabname;
	tabnameen = jso_OpenPars.tabnameen;
	defaultValue = jso_OpenPars.defaultValue;
	
	JSPFree.createBillCard("d1",template,["保存/onSave/icon-p21","取消/onCancel/icon-undo"],null);
	
	if (type == "Add") {
		//先执行卡片默认值公式
		JSPFree.execBillCardDefaultValueFormula(d1_BillCard); //参数不带true，会去加载xml当中的defaultformula
		
		JSPFree.setBillCardValues(d1_BillCard, defaultValue);
	} else if (type == "Edit") {
		//赋值
		JSPFree.queryBillCardData(d1_BillCard, "rid = '"+rid+"'");
		JSPFree.setBillCardValues(d1_BillCard, defaultValue);
		
		//执行卡片默认值公式2，即修改时的defaultformula2
		JSPFree.execBillCardDefaultValueFormula(d1_BillCard,true); //参数带true，会去加载xml当中的defaultformula2，属于不同分支，所以不会把之前的UUID4冲掉
	}
}

/**
 * 保存
 * @return {[type]} [description]
 */
var saveFlag;
function onSave(){
	// 是否进行单条数据检核操作
	var jso_check = JSPFree.doClassMethodCall("com.yusys.crrs.collection.service.CrrsCollectionBS", "getCrrsCheckProperties", {});
	var ischeck = jso_check.ischeck;
	if ("Y" == ischeck) {
		if(type == "Add" && !saveFlag){
			var backValue = JSPFree.editTableCheckData(d1_BillCard, type, tabname, tabnameen.toUpperCase(),"","2");
			if (backValue == "" || "undifind" == backValue) {
				return;
			} else if (backValue == "OK") {
				saveFlag = JSPFree.doBillCardInsert(d1_BillCard,null);
				if(saveFlag){
					JSPFree.closeDialog("校验并保存成功");
				}
			} else if (backValue == "Fail") {
				return;
			}
		} else {
			var backValue = JSPFree.editTableCheckData(d1_BillCard, type, tabname, tabnameen.toUpperCase(),"","2");
			if (backValue == "" || "undifind" == backValue) {
				return;
			} else if (backValue == "OK") {
				saveFlag = JSPFree.doBillCardUpdate(d1_BillCard,null);
				if(saveFlag){
					JSPFree.closeDialog("校验并保存成功");
				}
			} else if (backValue == "Fail") {
				return;
			}
		}
	} else {
		if(type == "Add" && !saveFlag){
			saveFlag = JSPFree.doBillCardInsert(d1_BillCard,null);
			if(saveFlag){
				JSPFree.closeDialog("保存成功");
			}
		} else {
			saveFlag = JSPFree.doBillCardUpdate(d1_BillCard,null);
			if(saveFlag){
				JSPFree.closeDialog("保存成功");
			}
		}
	}
}

/**
 * 取消
 * @return {[type]} [description]
 */
function onCancel(){
	JSPFree.closeDialog();
}
