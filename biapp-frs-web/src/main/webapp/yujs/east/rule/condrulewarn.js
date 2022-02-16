/**
 * 前置条件/规则/预警
 * @returns
 */
function AfterInit(){
	JSPFree.createTabb("d1",["前置条件","规则基本信息","规则定义"]);  //创建多页签
	//JSPFree.createTabb("d1",["前置条件","规则基本信息","规则定义","预警级别"]); 
	
	//第1个页签
	JSPFree.createBillCard("d1_1","/biapp-east/freexml/east/rule/east_cr_rule_condition.xml");
	var str_sqlWhere = "id='"  + jso_OpenPars.id + "'";  //拼SQL条件
	JSPFree.queryBillCardData(d1_1_BillCard,str_sqlWhere);  //锁定规则表查询数据

	//第2个页签
	JSPFree.createBillCard("d1_2","/biapp-east/freexml/east/rule/east_cr_rule_baseInfo.xml");
	var str_sqlWhere = "id='"  + jso_OpenPars.id + "'";  //拼SQL条件
	JSPFree.queryBillCardData(d1_2_BillCard,str_sqlWhere);  //锁定规则表查询数据

	//第3个页签
	if(jso_OpenPars.type_cd == "空值" || jso_OpenPars.type_cd == "记录数"){
		table = "";
	}
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
	
	if(table == ""){
		var div_root = document.getElementById("d1_3");  //先取得根div
  		div_root.innerHTML="空值和记录数没有规则定义";
	}
	else if(table != "east_cr_rule_uprr" && table != "east_cr_rule_aggr"){
		//创建表单
		JSPFree.createBillCard("d1_3","/biapp-east/freexml/east/rule/"+table+"_CODE1.xml");
		var ruleId = jso_OpenPars.id;
		var _sqlWhere = "id='"+ruleId+"'";
		JSPFree.queryBillCardData(d1_3_BillCard,_sqlWhere);  //锁定规则表查询数据
	}
	else{
		JSPFree.createBillList("d1_3", "/biapp-east/freexml/east/rule/"+table+"_ref.xml");
		var id = jso_OpenPars.id;
		var str_sqlWhere = "id='"+id+"'";
		JSPFree.queryDataByConditon(d1_3_BillList, str_sqlWhere); // 锁定规则表查询数据
	}
	
	//第4个页签
	/*JSPFree.createBillList("d1_4","east_cr_warn_ref");
	var str_sqlWhere = "rule_id='"  + jso_OpenPars.id + "'";  //拼SQL条件
	JSPFree.queryDataByConditon(d1_4_BillList,str_sqlWhere);  //锁定规则表查询数据*/
}

//明细
function detail(){
	var jso_OpenPars = d1_3_BillList.datagrid('getSelected');
	if (jso_OpenPars == null) {
		$.messager.alert('提示', '必须选择一条记录进行操作', 'warning');
		return;
	}
	jso_OpenPars.table = table;
	JSPFree.openDialog("配置明细","/yujs/east/rule/configsubdetail2.js", 700, 700, jso_OpenPars,function(_rtdata){});
}
