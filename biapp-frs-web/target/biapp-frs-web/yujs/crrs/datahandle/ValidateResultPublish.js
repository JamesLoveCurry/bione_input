//校验结果分发,即从总行管理岗分给分行管理岗【ValidateResultPublish.js】
function AfterInit(){
	JSPFree.createTabb("d1",["确定性校验","一致性校验","提示性校验"]);
	var str_sqlCons = "";//查询所有状态
	JSPFree.createBillList("d1_1","/biapp-crrs/freexml/crrs/rule/crrs_result_sure_Submit.xml",null,{list_btns:"[icon-p47]发布/onPublish_1;$VIEW",querycontion:str_sqlCons,isSwitchQuery:"N"});  //确定性校验
	JSPFree.createBillList("d1_2","/biapp-crrs/freexml/crrs/rule/crrs_result_consistency_Submit.xml",null,{list_btns:"[icon-p47]发布/onPublish_2;$VIEW",querycontion:str_sqlCons,isSwitchQuery:"N"});  //一致性校验
	JSPFree.createBillList("d1_3","/biapp-crrs/freexml/crrs/rule/crrs_result_warn_Submit.xml",null,{list_btns:"[icon-p47]发布/onPublish_3;$VIEW",querycontion:str_sqlCons,isSwitchQuery:"N"});  //提示性校验

	//人工再次查询数据
	JSPFree.queryDataByConditon2(d1_1_BillList,null);
	JSPFree.queryDataByConditon2(d1_2_BillList,null);
	JSPFree.queryDataByConditon2(d1_3_BillList,null); 
}

//确定性校验-发布
function onPublish_1(){
	//var jsy_datas = JSPFree.getBillListSelectDatas(d1_1_BillList);
	JSPFree.confirm("提示","该操作会将所有“未发布”的结果发布，确认发布吗?",function(_isOK){
		if(_isOK){
		  doCommitUpdateDB(d1_1_BillList,"crrs_result_sure");
		}
	});
}


//一致性校验-发布
function onPublish_2(){
	//var jsy_datas = JSPFree.getBillListSelectDatas(d1_2_BillList);
	JSPFree.confirm("提示","该操作会将所有“未发布”的结果发布，确认发布吗?",function(_isOK){
		if(_isOK){
		  doCommitUpdateDB(d1_2_BillList,"crrs_result_consistency");
		}
	});
}

//提示性校验-发布
function onPublish_3(){
	//var jsy_datas = JSPFree.getBillListSelectDatas(d1_3_BillList);
	JSPFree.confirm("提示","该操作会将所有“未发布”的结果发布，确认发布吗?",function(_isOK){
		if(_isOK){
		  doCommitUpdateDB(d1_3_BillList,"crrs_result_warn");
		}
	});
}

function doCommitUpdateDB(billList,_tableName){

	var jso_par = {tablename:_tableName};
	var jso_rt = JSPFree.doClassMethodCall("com.yusys.crrs.rulevalidate.service.ValidateResultHandleBS","doResultPublish",jso_par);
	
	JSPFree.queryDataByConditon2(billList, null);  //刷新当前行
	JSPFree.alert(jso_rt.msg);  //提示成功!
}

