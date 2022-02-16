//分发
function AfterInit(){
	JSPFree.createTabb("d1",["确定性校验","一致性校验","提示性校验"]);
	var str_LoginUserOrgNo = window.self.str_LoginUserOrgNo;
	var str_sqlCons = "";  //过滤条件
	
	JSPFree.createBillList("d1_1","/biapp-crrs/freexml/crrs/rule/crrs_result_sure_Submit.xml",null,{list_btns:"[icon-p51]分发/onDispatch_1;$VIEW",list_ischeckstyle:"Y",list_ismultisel:"Y",isSwitchQuery:"N"});  //确定性校验
	JSPFree.createBillList("d1_2","/biapp-crrs/freexml/crrs/rule/crrs_result_consistency_Submit.xml",null,{list_btns:"[icon-p51]分发/onDispatch_2;$VIEW",list_ischeckstyle:"Y",list_ismultisel:"Y",isSwitchQuery:"N"});  //一致性校验
	JSPFree.createBillList("d1_3","/biapp-crrs/freexml/crrs/rule/crrs_result_warn_Submit.xml",null,{list_btns:"[icon-p51]分发/onDispatch_3;$VIEW",list_ischeckstyle:"Y",list_ismultisel:"Y",isSwitchQuery:"N"});  //提示性校验

	//查询数据
	JSPFree.queryDataByConditon2(d1_1_BillList,getQueryCondition());
	JSPFree.queryDataByConditon2(d1_2_BillList,getQueryCondition());
	JSPFree.queryDataByConditon2(d1_3_BillList,getQueryCondition());
	
	//灵活查询加载初始化查询条件
	JSPFree.setBillListForceSQLWhere(d1_1_BillList,getQueryCondition());
	JSPFree.setBillListForceSQLWhere(d1_2_BillList,getQueryCondition());
	JSPFree.setBillListForceSQLWhere(d1_3_BillList,getQueryCondition());
}

//查询列表加载默认查询条件，用于灵活查询
function getQueryCondition(){
	var jso_rt = JSPFree.doClassMethodCall("com.yusys.crrs.rulevalidate.service.ValidateQueryCondition","getQueryCondition",{"_LoginUserOrgNo" : str_LoginUserOrgNo});
	var condition = "";
	if(jso_rt.msg == "ok"){
		condition = jso_rt.condition;
	}
	return condition;
}

function onDispatch_1(){
	var jsy_datas = JSPFree.getBillListSelectDatas(d1_1_BillList);
	if(jsy_datas==null || jsy_datas.length<=0){
		JSPFree.alert("至少选择一条数据！");
		return;
	}
	
	var jsy_rids = [];  //存储所有选中的主键
	var finishCount = 0;
	for(var i=0;i<jsy_datas.length;i++){
		if("完成" != jsy_datas[i]["result_status"]){
			jsy_rids.push(jsy_datas[i]["rid"]);
		}else{
			finishCount++;
		}
	}
	if(finishCount == jsy_datas.length){
		JSPFree.alert("您选择的数据全部是完成状态，<br/>请重新选择！");
		return;
	}
	//弹出窗口,传入参数,然后接收返回值!
	JSPFree.openDialog("选择客户经理","/frame/js/yuformat/ChoseUserDialog.js",900,600,null,function(_rtdata){
		if (_rtdata.type == "dirclose" || _rtdata == false) {
			return;
		}
		if(_rtdata){
			JSPFree.confirm("提示","您选中的客户经理是: "+ _rtdata.staff_name +"，<br>确定要分发吗?",function(_isOK){
				if(_isOK){
				  doCommitUpdateDB(d1_1_BillList,"crrs_result_sure",jsy_rids,_rtdata.inst_code,_rtdata.inst_name,_rtdata.staff_code,_rtdata.staff_name);
				}
			});
		}
	});
}

function onDispatch_2(){
	var jsy_datas = JSPFree.getBillListSelectDatas(d1_2_BillList);
	if(jsy_datas==null || jsy_datas.length<=0){
		JSPFree.alert("至少选择一条数据！");
		return;
	}
	
	var jsy_rids = [];  //存储所有选中的主键
	var finishCount = 0;
	for(var i=0;i<jsy_datas.length;i++){
		if("完成" != jsy_datas[i]["result_status"]){
			jsy_rids.push(jsy_datas[i]["rid"]);
		}else{
			finishCount++;
		}
	}
	if(finishCount == jsy_datas.length){
		JSPFree.alert("您选择的数据全部是完成状态，<br/>请重新选择！");
		return;
	}
	//弹出窗口,传入参数,然后接收返回值!
	JSPFree.openDialog("选择客户经理","/frame/js/yuformat/ChoseUserDialog.js",900,600,null,function(_rtdata){
		if (_rtdata.type == "dirclose" || _rtdata == false) {
			return;
		}
		if(_rtdata){
			JSPFree.confirm("提示","您选中的客户经理是: "+ _rtdata.staff_name +"，<br>确定要分发吗?",function(_isOK){
				if(_isOK){
				  doCommitUpdateDB(d1_2_BillList,"crrs_result_consistency",jsy_rids,_rtdata.inst_code,_rtdata.inst_name,_rtdata.staff_code,_rtdata.staff_name);
				}
			});
		}
	});
}

function onDispatch_3(){
	var jsy_datas = JSPFree.getBillListSelectDatas(d1_3_BillList);
	if(jsy_datas==null || jsy_datas.length<=0){
		JSPFree.alert("至少选择一条数据！");
		return;
	}
	
	var jsy_rids = [];  //存储所有选中的主键
	var finishCount = 0;
	for(var i=0;i<jsy_datas.length;i++){
		if("完成" != jsy_datas[i]["result_status"]){
			jsy_rids.push(jsy_datas[i]["rid"]);
		}else{
			finishCount++;
		}
	}
	if(finishCount == jsy_datas.length){
		JSPFree.alert("您选择的数据全部是完成状态，<br/>请重新选择！");
		return;
	}
	//弹出窗口,传入参数,然后接收返回值!
	JSPFree.openDialog("选择客户经理","/frame/js/yuformat/ChoseUserDialog.js",900,600,null,function(_rtdata){
		if (_rtdata.type == "dirclose" || _rtdata == false) {
			return;
		}
		if(_rtdata){
			JSPFree.confirm("提示","您选中的客户经理是: "+ _rtdata.staff_name +"，<br>确定要分发吗?",function(_isOK){
				if(_isOK){
				  doCommitUpdateDB(d1_3_BillList,"crrs_result_warn",jsy_rids,_rtdata.inst_code,_rtdata.inst_name,_rtdata.staff_code,_rtdata.staff_name);
				}
			});
		}
	});
}

//分发
function doCommitUpdateDB(_billList,_tableName,_rids,_inst_code,_inst_name,_staff_code,_staff_name){
	
	var jso_par = {tablename:_tableName,rids:_rids,inst_code:_inst_code,inst_name:_inst_name,staff_code:_staff_code,staff_name:_staff_name};
	var jso_rt = JSPFree.doClassMethodCall("com.yusys.crrs.rulevalidate.service.ValidateResultHandleBS","doResultPublish2",jso_par);
	
	//JSPFree.refreshBillListCurrRows(_billList);  //刷新当前行
	JSPFree.queryDataByConditon2(_billList, null);  //刷新当前行
	JSPFree.alert(jso_rt.msg);  //提示成功!
}
