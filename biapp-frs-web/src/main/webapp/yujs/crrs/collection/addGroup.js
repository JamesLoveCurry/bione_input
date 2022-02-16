//初始化界面,菜单配置路径是【/frs/yufreejs?js=/yujs/crrs/collection/addSingle.js】
function AfterInit(){
	JSPFree.createTabb("d1",["基本信息","高管及重要联系人","成员名单","实际控制人","关联集团","授信信息","授信拆分信息"],[80,120,80,90,80,80,100]);

	JSPFree.createBillCard("d1_1","/biapp-crrs/freexml/crrs/group/crrs_group_group_client_CODE1.xml",["保存/onSave/icon-ok"],null);
	document.getElementById("d1_2").innerHTML="<div style=\"width:100%;text-align:center;\">请先维护集团基本信息</div>";
	document.getElementById("d1_3").innerHTML="<div style=\"width:100%;text-align:center;\">请先维护集团基本信息</div>";
	document.getElementById("d1_4").innerHTML="<div style=\"width:100%;text-align:center;\">请先维护集团基本信息</div>";
	document.getElementById("d1_5").innerHTML="<div style=\"width:100%;text-align:center;\">请先维护集团基本信息</div>";
	document.getElementById("d1_6").innerHTML="<div style=\"width:100%;text-align:center;\">请先维护集团基本信息</div>";
	document.getElementById("d1_7").innerHTML="<div style=\"width:100%;text-align:center;\">请先维护集团基本信息</div>";

	//赋值
	JSPFree.setBillCardValues(d1_1_BillCard,{customer_type:'1',type:'1',credit_type:'1'});
}

function AfterBodyLoad(){
	JSPFree.setBillCardItemEditable(d1_1_BillCard,"inst_no",false);
	FreeUtil.addBillCardItemEditListener(d1_1_BillCard,onEditChanged);
}

function onEditChanged(_billCard,_itemKey,_value){
	console.log("[" + _itemKey + "]");
	console.log(_value);
	if(_itemKey=="data_dt"){
		var str_date  = _value.newDateStr;  //
		var jso_data = JSPFree.getHashVOs("select status from crrs_lock_data where data_dt='" + str_date + "' and status='锁定'");
		if(jso_data.length>0){  //如果的确有锁定,则提示不允许处理
			JSPFree.alert("日期[" + _value.newDateStr + "]的数据已经被锁定,不能处理,保存会失败的!<br>请在【数据处理->数据锁定】中进行设置");
			return;  //
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
	
	JSPFree.setBillCardValues(d1_1_BillCard,{rid:FreeUtil.getUUIDFromServer()});

	// 是否进行单条数据检核操作
	var jso_check = JSPFree.doClassMethodCall("com.yusys.crrs.collection.service.CrrsCollectionBS", "getCrrsCheckProperties", {});
	var ischeck = jso_check.ischeck;
	if ("Y" == ischeck) {
		// 单条数据校验
		var backValue = JSPFree.editTableCheckData(d1_1_BillCard, "Add", "集团基本情况", "CRRS_GROUP_GROUP_CLIENT","","2");
		if (backValue == "" || "undifind" == backValue) {
			return;
		} else if (backValue == "OK") {
			// 提示验证通过，并进行保存
			var flag = JSPFree.doBillCardInsert(d1_1_BillCard,null);
			if(flag){
				JSPFree.alert("校验并保存成功");
				var group = JSPFree.getBillCardFormValue(d1_1_BillCard);
				JSPFree.openDialogAndCloseMe2("基础信息","/yujs/crrs/collection/editGroup.js",900,560,{group:group},true);
			}
		} else if (backValue == "Fail") {
			return;
		}
	} else {
		var flag = JSPFree.doBillCardInsert(d1_1_BillCard,null);
		if(flag){
			JSPFree.alert("保存成功");
			var group = JSPFree.getBillCardFormValue(d1_1_BillCard);
			JSPFree.openDialogAndCloseMe2("基础信息","/yujs/crrs/collection/editGroup.js",900,560,{group:group},true);
		}
	}
	
}