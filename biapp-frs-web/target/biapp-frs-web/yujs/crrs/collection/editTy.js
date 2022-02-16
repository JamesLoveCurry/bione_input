//初始化界面,菜单配置路径是【/frs/yufreejs?js=/yujs/crrs/collection/addSingle.js】
var tyData = null;
var defaultValue;
var isLoadTabb_2 = false;
var isLoadTabb_3 = false;
function AfterInit(){
	tyData = jso_OpenPars2.tyData;
	JSPFree.createTabb("d1",["基本信息","授信信息","业务明细"],[80,80,80]);

	JSPFree.createBillCard("d1_1","/biapp-crrs/freexml/crrs/ent/crrs_ent_trade_info_CODE1.xml",["保存/onSave/icon-ok","假设校验失败/onPOCDemoData1/icon-p50"],null);
	
	//赋值
	JSPFree.queryBillCardData(d1_1_BillCard, "rid = '"+tyData.rid+"'");

	defaultValue ={customer_code:tyData.customer_code,customer_name:tyData.customer_name,data_dt:tyData.data_dt,
			issued_no:tyData.issued_no};
	JSPFree.addTabbSelectChangedListener(d1_tabb,onSelect);
}

function onSelect(_index,_title){
	var newIndex = _index+1;
	
	if(newIndex==1){
		//是否显示假设校验失败按钮
		var jso_rt = FreeUtil.doClassMethodCall(FreeUtil.JSPBuilderClassName, "getIsPOC", null);
		if("Y" == jso_rt.isPOC){
			document.getElementById("d1_1_BillCardBtn2").style.display='';
		}else{
			document.getElementById("d1_1_BillCardBtn2").style.display='none';
		}
	}
	
	if(newIndex==2){
		if(!isLoadTabb_2){
			JSPFree.createBillList("d1_2","/biapp-crrs/freexml/crrs/ent/crrs_ent_credit_CODE4.xml",null,{isSwitchQuery:"N",autoquery:"Y",
				autocondition:"customer_code='"+tyData.customer_code+"' and data_dt='"+tyData.data_dt+"' and customer_type='3' and credit_type = '4'",querycontion:"customer_code='"+tyData.customer_code+"' and data_dt='"+tyData.data_dt+"' and customer_type='3' and credit_type = '4'"});
			$.parser.parse('#d1_2');
			JSPFree.setDefaultValues(d1_2_BillList,{customer_code:tyData.customer_code,customer_name:tyData.customer_name,data_dt:tyData.data_dt,credit_type:'4',customer_type:'3',issued_no:tyData.issued_no});
			isLoadTabb_2 = true;
			if ("锁定" == tyData.status) {
				JSPFree.setBillListBtnEnable(d1_2_BillList, "新增", false);
				JSPFree.setBillListBtnEnable(d1_2_BillList, "编辑", false);
				JSPFree.setBillListBtnEnable(d1_2_BillList, "删除", false);
				JSPFree.setBillListBtnTip(d1_2_BillList, "新增", "因为锁定，所以无法进行处理");
				JSPFree.setBillListBtnTip(d1_2_BillList, "编辑", "因为锁定，所以无法进行处理");
				JSPFree.setBillListBtnTip(d1_2_BillList, "删除", "因为锁定，所以无法进行处理");
			}
		}
		//是否显示假设校验失败按钮
		var jso_rt = FreeUtil.doClassMethodCall(FreeUtil.JSPBuilderClassName, "getIsPOC", null);
		JSPFree.showOrHideBtn(d1_2_BillList, "假设校验失败", jso_rt.isPOC);
	}

	if(newIndex==3){
		if(!isLoadTabb_3){
			JSPFree.createBillList("d1_3","/biapp-crrs/freexml/crrs/ent/crrs_ent_trade_customers_CODE1.xml",null,{isSwitchQuery:"N",autoquery:"Y",
				autocondition:"customer_code='"+tyData.customer_code+"' and data_dt='"+tyData.data_dt+"'",querycontion:"customer_code='"+tyData.customer_code+"' and data_dt='"+tyData.data_dt+"'"});
			$.parser.parse('#d1_3');
			JSPFree.setDefaultValues(d1_3_BillList,defaultValue);
			isLoadTabb_3 = true;
			if ("锁定" == tyData.status) {
				JSPFree.setBillListBtnEnable(d1_3_BillList, "新增", false);
				JSPFree.setBillListBtnEnable(d1_3_BillList, "编辑", false);
				JSPFree.setBillListBtnEnable(d1_3_BillList, "删除", false);
				JSPFree.setBillListBtnTip(d1_3_BillList, "新增", "因为锁定，所以无法进行处理");
				JSPFree.setBillListBtnTip(d1_3_BillList, "编辑", "因为锁定，所以无法进行处理");
				JSPFree.setBillListBtnTip(d1_3_BillList, "删除", "因为锁定，所以无法进行处理");
			}
		}
		//是否显示假设校验失败按钮
		var jso_rt = FreeUtil.doClassMethodCall(FreeUtil.JSPBuilderClassName, "getIsPOC", null);
		JSPFree.showOrHideBtn(d1_3_BillList, "假设校验失败", jso_rt.isPOC);
	}
}

function AfterBodyLoad(){
	JSPFree.setBillCardItemEditable(d1_1_BillCard,"inst_no",false);
	if ("锁定" == tyData.status) {
		JSPFree.setBillCardItemEditable(d1_1_BillCard,"*",false);
		$("#d1_1_BillCardBtn1").linkbutton('disable');  //按钮失效
		$("#d1_1_BillCardBtn1" ).attr("title","因为锁定，所以无法进行处理");
	} else {
		//添加监听事件放在禁用的前面，因为在添加监听事件的时候，会把日期的禁用放开！！
		FreeUtil.addBillCardItemEditListener(d1_1_BillCard,onEditChanged);
		JSPFree.setBillCardItemEditable(d1_1_BillCard,"customer_code",false);
		JSPFree.setBillCardItemEditable(d1_1_BillCard,"customer_name",false);
		JSPFree.setBillCardItemEditable(d1_1_BillCard,"data_dt",false);
	}
}

function onEditChanged(_billCard,_itemKey,_value){
	console.log("[" + _itemKey + "]");
	console.log(_value);
	if(_itemKey=="data_dt"){
		var str_date  = _value.newDateStr;
		var jso_data = JSPFree.getHashVOs("select status from crrs_lock_data where data_dt='" + str_date + "' and status='锁定'");
		if(jso_data.length>0){  //如果的确有锁定,则提示不允许处理
			JSPFree.alert("日期[" + _value.newDateStr + "]的数据已经被锁定,不能处理,保存会失败的!<br>请在【数据处理->数据锁定】中进行设置");
			return;
		}
	}
	if(_itemKey=="staff_code"){//选择客户经理编号，显示经办机构
		var staff_code  = _value.staff_code;
		var jso_data = JSPFree.getHashVOs("SELECT a.nbjgh inst_no, c.org_nm inst_name  FROM crrs_bs_staff a left join rpt_org_info c on a.nbjgh = c.org_no where c.org_type = '08' and a.gyh ='" + staff_code + "'");
		if(jso_data.length > 0){
			JSPFree.setBillCardItemClearValue(_billCard,"inst_no");
			JSPFree.setBillCardItemValue(_billCard,"inst_no",jso_data[0].inst_no);
			JSPFree.setBillCardItemValue(_billCard,"inst_name",jso_data[0].inst_name);
		}
	}
}

//新增：授信信息
function addData2() {
	var def = {customer_code:tyData.customer_code,customer_name:tyData.customer_name,data_dt:tyData.data_dt,credit_type:'4',customer_type:'3',issued_no:tyData.issued_no};
	var defaultVal = {type:"Add",template:"/biapp-crrs/freexml/crrs/ent/crrs_ent_credit_CODE4.xml",
			tabname:"授信情况",tabnameen:"CRRS_ENT_CREDIT",defaultValue:def};
	JSPFree.openDialog("授信信息","/yujs/crrs/collection/check_data_edit.js",900,560,defaultVal,function(_rtdata){
		if (_rtdata == "校验并保存成功") {
			JSPFree.alert("校验并保存成功");
		}
	},true);
}

//修改:授信信息
function editData2() {
	// 获取选中的数据的rid
	var json_data = d1_2_BillList.datagrid('getSelections');
	var rid = json_data[0].rid;
	var def = {customer_code:tyData.customer_code,customer_name:tyData.customer_name,data_dt:tyData.data_dt,credit_type:'4',customer_type:'3',issued_no:tyData.issued_no};
	var defaultVal = {rid:rid,type:"Edit",template:"/biapp-crrs/freexml/crrs/ent/crrs_ent_credit_CODE4.xml",
			tabname:"授信情况",tabnameen:"CRRS_ENT_CREDIT",defaultValue:def};
	JSPFree.openDialog("授信信息","/yujs/crrs/collection/check_data_edit.js",900,560,defaultVal,function(_rtdata){
		if (_rtdata == "校验并保存成功") {
			JSPFree.alert("校验并保存成功");
			JSPFree.refreshBillListCurrRow(d1_2_BillList); // 刷新当前行
		}
	},true);
}

//新增：业务明细
function addData3() {
	var defaultVal = {type:"Add",template:"/biapp-crrs/freexml/crrs/ent/crrs_ent_trade_customers_CODE1.xml",
			tabname:"同业客户业务明细",tabnameen:"CRRS_ENT_TRADE_CUSTOMERS",defaultValue:defaultValue};
	JSPFree.openDialog("业务明细","/yujs/crrs/collection/check_data_edit.js",900,560,defaultVal,function(_rtdata){
		if (_rtdata == "校验并保存成功") {
			JSPFree.alert("校验并保存成功");
		}
	},true);
}

//修改:业务明细
function editData3() {
	// 获取选中的数据的rid
	var json_data = d1_3_BillList.datagrid('getSelections');
	var rid = json_data[0].rid;
	var defaultVal = {rid:rid,type:"Edit",template:"/biapp-crrs/freexml/crrs/ent/crrs_ent_trade_customers_CODE1.xml",
			tabname:"同业客户业务明细",tabnameen:"CRRS_ENT_TRADE_CUSTOMERS",defaultValue:defaultValue};
	JSPFree.openDialog("业务明细","/yujs/crrs/collection/check_data_edit.js",900,560,defaultVal,function(_rtdata){
		if (_rtdata == "校验并保存成功") {
			JSPFree.alert("校验并保存成功");
			JSPFree.refreshBillListCurrRow(d1_3_BillList); // 刷新当前行
		}
	},true);
}

/**
 * 保存
 * @return {[type]} [description]
 */
function onSave(){
	var str_date = JSPFree.getBillCardItemValue(d1_1_BillCard,"data_dt");
	var jso_data = JSPFree.getHashVOs("select status from crrs_lock_data where data_dt='" + str_date + "' and status='锁定'");
	if(jso_data.length>0){  //如果的确有锁定,则提示不允许处理
		JSPFree.alert("日期[" + str_date + "]的数据已经被锁定,不能保存!<br>请在【数据处理->数据锁定】中进行设置");
		return;
	}
	
	// 是否进行单条数据检核操作
	var jso_check = JSPFree.doClassMethodCall("com.yusys.crrs.collection.service.CrrsCollectionBS", "getCrrsCheckProperties", {});
	var ischeck = jso_check.ischeck;
	if ("Y" == ischeck) {
		// 单条数据校验
		var backValue = JSPFree.editTableCheckData(d1_1_BillCard, "Edit", "同业客户基础信息", "CRRS_ENT_TRADE_INFO","","2");
		if (backValue == "" || "undifind" == backValue) {
			return;
		} else if (backValue == "OK") {
			// 提示验证通过，并进行保存
			var flag = JSPFree.doBillCardUpdate(d1_1_BillCard,null);
			if(flag){
				JSPFree.alert("校验并保存成功");
			}
		} else if (backValue == "Fail") {
			return;
		}
	} else {
		var flag = JSPFree.doBillCardUpdate(d1_1_BillCard,null);
		if(flag){
			JSPFree.alert("保存成功");
		}
	}
}

function onPOCDemoData1(){
	var jso_templetVO = d1_1_BillCard.templetVO;  //
	var str_templetcode = jso_templetVO.templet_option.templetcode;  //模板编码,后面根据这个模板编码创建卡片
	var str_templetname = jso_templetVO.templet_option.templetname;  //模板名称

	var jso_rowdata = JSPFree.getBillCardFormValue(d1_1_BillCard);

	//拼参数,把整个模板与数据都传入
	var jso_par={templetVO:jso_templetVO,rowData:jso_rowdata};

	//打开窗口
	JSPFree.openDialog2("创建POC假设校验失败数据-" + str_templetname,"/yujs/crrs/datahandle/HandleInsertPOCDemo.js",1100,560,jso_par,function(_rtData){

	},true);  //
}

function onPOCDemoData(_btn,_event){
	var billList = FreeUtil.getBtnBindBillList(_btn);  //根据按钮取得属于哪个列表
	var jso_templetVO = billList.templetVO;  //模板VO
	var str_templetcode = jso_templetVO.templet_option.templetcode;  //模板编码,后面根据这个模板编码创建卡片
	var str_templetname = jso_templetVO.templet_option.templetname;  //模板名称

	var jso_rowdata = JSPFree.getBillListSelectData(billList);  //
	if(jso_rowdata==null){
		JSPFree.alert("必须选择一条【" + str_templetname + "】记录!");
		return;
	}

	//拼参数,把整个模板与数据都传入
	var jso_par={templetVO:jso_templetVO,rowData:jso_rowdata};

	//打开窗口
	JSPFree.openDialog2("创建POC假设校验失败数据-" + str_templetname,"/yujs/crrs/datahandle/HandleInsertPOCDemo.js",1100,560,jso_par,function(_rtData){

	},true);  //
}