//监管提示性校验反馈导入
function AfterInit(){
	var str_sqlCons = "result_status2 in ('未分发')";  //过滤条件

	JSPFree.createBillList("d1","/biapp-crrs/freexml/crrs/rule/crrs_result_warn_FeedBack.xml",null,{list_btns:"[icon-p47]导入/onImport;$VIEW",querycontion:str_sqlCons});  //确定性校验
	
	JSPFree.queryDataByConditon2(d1_BillList,null);  //
}

//导入
function onImport(){
	var jsy_datas = JSPFree.getBillListSelectDatas(d1_BillList);
	if(jsy_datas==null || jsy_datas.length<=0){
		JSPFree.alert("必须选择一条数据！");
		return;
	}
	
	//做一个校验!
	var li_count = 0;
	var jsy_rids = [];  //存储所有选中的主键
	for(var i=0;i<jsy_datas.length;i++){
		var str_rid = jsy_datas[i]["rid"];
		var str_result_status = jsy_datas[i]["result_status2"];
		if("未分发"!=str_result_status){
			li_count++;
		}
		jsy_rids.push(str_rid);  //加入主键
	}

	if(li_count>0){
		JSPFree.alert("选中的数据中有【"+li_count+"】条状态不是[未分发]！");
		return;
	}
	JSPFree.confirm("提示","你真的要导入么?",function(_isOK){
		if(_isOK){
		  doCommitUpdateDB("crrs_result_warn","已导入",jsy_rids);
		}
	});
}

//提交数据库
function doCommitUpdateDB(_tableName,_newstatus,_rids){
	//表名、新的状态、所的id
	var jso_par = {tablename:_tableName,newstatus:_newstatus,rids:_rids};
	var jso_rt = JSPFree.doClassMethodCall("com.yusys.crrs.rulevalidate.service.ValidateResultHandleBS2","doResultPublish",jso_par);
	
	JSPFree.refreshBillListCurrRows(d1_BillList);  //刷新当前行
	JSPFree.alert(jso_rt.msg);  //提示成功!
}
