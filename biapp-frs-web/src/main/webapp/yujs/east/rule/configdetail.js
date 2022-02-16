//初始化界面,菜单配置url路径是【/frs/yufreejs?js=/yujs/east/warningLevel.js】
var table = "";
var tab_name="";
function AfterInit(){
	tab_name=jso_OpenPars.tab_name;
	console.log(tab_name);
	if(jso_OpenPars.type_cd == "取值范围"){
		table = "east_cr_rule_range";
	}
	else if(jso_OpenPars.type_cd == "格式"){
		table = "east_cr_rule_fmt";
	}
	else if(jso_OpenPars.type_cd == "表内逻辑"){
		table = "east_cr_rule_logic_in";
	}
	else if(jso_OpenPars.type_cd == "表间逻辑"){
		table = "east_cr_rule_logic_btw";
	}
	else if(jso_OpenPars.type_cd == "脱敏"){
		table = "east_cr_rule_dss";
	}
	else if(jso_OpenPars.type_cd == "跨监管一致性"){
		table = "east_cr_rule_uprr";
	}
	else if(jso_OpenPars.type_cd == "跨监管一致性校验子表"){
		table = "east_cr_rule_uprr_sub";
	}
	else if(jso_OpenPars.type_cd == "总分核对"){
		table = "east_cr_rule_aggr";
	}
	if(table != "east_cr_rule_uprr" && table != "east_cr_rule_aggr"){
		//创建表单
		JSPFree.createBillCard("d1","/biapp-east/freexml/east/rule/"+table+"_CODE1.xml" ,["保存/onSaveData/icon-p21","取消/onCancel/icon-undo"]);
		
		var ruleId = jso_OpenPars.id;
		var _sqlWhere = "id='"+ruleId+"'";
		//先查一把
		var jso_par = {"tableName":table, "id":ruleId};
		JSPFree.doClassMethodCall("com.yusys.east.checkrule.detailmaintenance.service.EastCrRuleConfigDetailBS","configInit",jso_par); 

		var jso_data = JSPFree.queryBillCardData(d1_BillCard,_sqlWhere);  //锁定规则表查询数据
		d1_BillCard.OldData=jso_data;  //先把旧的数据存储下来!
		if(table=="east_cr_rule_logic_btw" || table=="east_cr_rule_logic_in"){
			JSPFree.setBillCardValues(d1_BillCard,{tab_name: tab_name});
		}
	}
	else{
		/*JSPFree.createBillList("d1", table+"_CODE1");
		var ruleId = jso_OpenPars.id;
		d1_BillList.DefaultValues={id : ruleId,tab_name : tab_name};
		var str_sqlWhere = "id='"+ruleId+"'";
		JSPFree.queryDataByConditon(d1_BillList, str_sqlWhere); // 锁定规则表查询数据
		if(JSPFree.getBillListAllDatas(d1_BillList).length ==0){
			$('#d1_BillList_Btn1').linkbutton('enable');
		}
		else{
			$('#d1_BillList_Btn1').linkbutton('disable');
		}*/

		//创建表单
		JSPFree.createBillCard("d1",table+"_CODE1" ,["保存/onSaveData/icon-p21","明细/detail/icon-p32","取消/onCancel/icon-undo"]);
		
		var ruleId = jso_OpenPars.id;
		var _sqlWhere = "id='"+ruleId+"'";
		//先查一把
		var jso_par = {"tableName":table, "id":ruleId};
		JSPFree.doClassMethodCall("com.yusys.east.checkrule.detailmaintenance.service.EastCrRuleConfigDetailBS","configInit",jso_par); 

		var jso_data = JSPFree.queryBillCardData(d1_BillCard,_sqlWhere);  //锁定规则表查询数据
		JSPFree.setBillCardValues(d1_BillCard,{tab_name: tab_name});
		d1_BillCard.OldData=jso_data;  //先把旧的数据存储下来!
	}
}

//页面加载结束后
function AfterBodyLoad(){
	JSPFree.setBillCardLabelHelptip(d1_BillCard);  //设置帮助说明
	if(table=="east_cr_rule_range"){ //如果是取值范围表单,则做表单变化监听
		FreeUtil.addBillCardItemEditListener(d1_BillCard,onCardItemEditForRangeDetail);

		//初始化值
		var jso_cardData = JSPFree.getBillCardFormValue(d1_BillCard);
		var str_type_cd = jso_cardData["type_cd"];
		//console.log("[" + str_type_cd + "]");  //是"",即空串
		if(typeof str_type_cd=="undefined" || str_type_cd==null || str_type_cd==""){
			//如果是第一次,没有值,则不做处理
		}else{
			changeItemByType(d1_BillCard,str_type_cd);  //根据初始值设置
		}
	}
}

//取值范围规则变化
function onCardItemEditForRangeDetail(_billCard,_itemkey,_jsoValue){
	if(_itemkey=="type_cd"){
		var str_value = _jsoValue.value;
		changeItemByType(_billCard,str_value);
	}
	else if(_itemkey=="multi_val"){
		var jso_OpenPars = JSPFree.getBillCardFormValue(_billCard);
		if(jso_OpenPars.multi_val == "N"){
			JSPFree.setBillCardValues(_billCard,{"sep":";"});
		}
		else{
			JSPFree.setBillCardValues(_billCard,{"sep":""});
		}
	}
}

//更新页面
function changeItemByType(_billCard,str_value){
	if(str_value=="1"){  //枚举
		JSPFree.setBillCardItemClearValue(_billCard,"min_range,min_val,max_range,max_val,dic_view");
		JSPFree.setBillCardItemVisible(_billCard,"min_range,min_val,max_range,max_val,dic_view",false);  //
		JSPFree.setBillCardItemVisible(_billCard,"multi_val,sep,range_cd",true);  //
	}else if(str_value=="2"){  //区间
		JSPFree.setBillCardItemClearValue(_billCard,"multi_val,sep,range_cd,dic_view");
		JSPFree.setBillCardItemVisible(_billCard,"multi_val,sep,range_cd,dic_view",false);  //
		JSPFree.setBillCardItemVisible(_billCard,"min_range,min_val,max_range,max_val",true);  //
	}else if(str_value=="3"){  //字典表
		JSPFree.setBillCardItemClearValue(_billCard,"multi_val,sep,range_cd,min_range,min_val,max_range,max_val");
		JSPFree.setBillCardItemVisible(_billCard,"multi_val,sep,range_cd,min_range,min_val,max_range,max_val",false);  //
		JSPFree.setBillCardItemVisible(_billCard,"dic_view",true);  //
	}
}

//明细
function detail(){
	var jso_OpenPars = JSPFree.getBillCardFormValue(d1_BillCard);
	jso_OpenPars.table = table;
	JSPFree.openDialog("配置明细","/yujs/east/rule/configsubdetail.js", 700, 700, jso_OpenPars,function(_rtdata){});
	
}
/**
 * 保存
 * @returns
 */
function onSaveData(){
	try {
		var result = JSPFree.doBillCardUpdate(d1_BillCard);
		if(result){
			JSPFree.closeDialog(true);
		}
	} catch (_ex) {
		FreeUtil.openHtmlMsgBox("发生异常", 500, 250, _ex)
	}
}
/**
 * 取消
 * @returns
 */
function onCancel(){
	JSPFree.closeDialog(false);
}