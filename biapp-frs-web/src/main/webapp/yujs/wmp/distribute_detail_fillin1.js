var tabName = "";
var taskId = "";
var _sql = "";
var _sql1 = "";
var d = "";
var isLoadTabb_2 = false;
function AfterInit(){
	taskId = jso_OpenPars.taskId;
	tabName = jso_OpenPars.tabName;
	tabNameEn = jso_OpenPars.tabNameEn;
	dataDt = jso_OpenPars.dataDt;
	
	d = getDate(jso_OpenPars.dataDt);
	
	JSPFree.createSplitByBtn("d1","上下",500,["提交/submission","强制提交/forcesubmission"]);
	
	JSPFree.createTabb("d1_A", [ "填报", "已填报" ]);
	
	var str_className1 = "Class:com.yusys.wmp.business.model.service.WmpModelTempletBuilder.getTemplet('"+tabName+"','"+tabNameEn+"','"+str_LoginUserOrgNo+"','_R','"+taskId+"','0')";
	JSPFree.createBillList("d1_A_1",str_className1,null,{list_btns:"[icon-p41]编辑/updateDate(this);$VIEW;",isSwitchQuery:"N",autoquery:"Y",list_ispagebar:"N",ishavebillquery:"N"});
	FreeUtil.loadBillQueryData(d1_A_1_BillList, {kjrq:d,cjrq:d});
	
	JSPFree.addTabbSelectChangedListener(d1_A_tabb,onSelect);
}

function AfterBodyLoad(){
	$("[name=cjrq]").each(function(){
	    $(this).prev().attr('disabled',"true");
	  });

	$("[name=kjrq]").each(function(){
	    $(this).prev().attr('disabled',"true");
	  });
}

function onSelect(_index,_title) {
	var newIndex = _index+1;

	if(newIndex==1){
		JSPFree.queryDataByConditon(d1_A_1_BillList);
	} else if(newIndex==2){
		var str_className2 = "Class:com.yusys.wmp.business.model.service.WmpModelTempletBuilder.getTemplet('"+tabName+"','"+tabNameEn+"','"+str_LoginUserOrgNo+"','_R','"+taskId+"','1')";
		JSPFree.createBillList("d1_A_2",str_className2,null,{list_btns:"[icon-p41]编辑/updateDate1(this);$VIEW;",isSwitchQuery:"N",autoquery:"Y",list_ispagebar:"N",ishavebillquery:"N"});
		FreeUtil.loadBillQueryData(d1_A_2_BillList, {kjrq:d,cjrq:d});
		
		$.parser.parse('#d1_A_2');
	}
}

// 待审核：编辑
function updateDate(){
	var json_data = d1_A_1_BillList.datagrid('getSelections');
	if (json_data == null || json_data == undefined || json_data.length == 0) {
		$.messager.alert('提示', '必须选择一条记录进行操作', 'warning');
		return;
	}
	if (json_data.length>1) {
		$.messager.alert('提示', '只能选择一条记录进行操作', 'warning');
		return;
	}

	var jso_Pars = {json_data:json_data[0],tabNameEn:tabNameEn,tabName:tabName,dataDt:dataDt}
	JSPFree.openDialog2("编辑","/yujs/wmp/distribute_detail_fillin_edit.js",1000,600,jso_Pars,function(_rtData){
		if (_rtData != null) {
			if ("dirclose" == _rtData.type) {
				return;
			}
			if (_rtData == true) {
				$.messager.alert('提示', '校验并保存成功！', 'info');
				JSPFree.queryDataByConditon(d1_A_1_BillList);
			}
		}
	});
}


//已审核：编辑
function updateDate1(){
	var json_data = d1_A_2_BillList.datagrid('getSelections');
	if (json_data == null || json_data == undefined || json_data.length == 0) {
		$.messager.alert('提示', '必须选择一条记录进行操作', 'warning');
		return;
	}
	if (json_data.length>1) {
		$.messager.alert('提示', '只能选择一条记录进行操作', 'warning');
		return;
	}

	var jso_Pars = {json_data:json_data[0],tabNameEn:tabNameEn,tabName:tabName,dataDt:dataDt}
	JSPFree.openDialog2("编辑","/yujs/wmp/distribute_detail_fillin_edit.js",1000,600,jso_Pars,function(_rtData){
		if (_rtData != null) {
			if ("dirclose" == _rtData.type) {
				return;
			}
			if (_rtData == true) {
				$.messager.alert('提示', '校验并保存成功！', 'info');
				JSPFree.queryDataByConditon(d1_A_2_BillList);
			}
		}
	});
}

//提交
function submission(){
	// 如果当前待编辑列表中有数据，则不能进行提交
	if (d1_A_1_BillList.datagrid('getData').total != 0) {
		$.messager.alert('提示', '当前列表存在待编辑数据，无法提交！', 'warning');
		return;
	}
	
	var jso_allrids=[]; 
	jso_allrids.push(taskId);

	// 修改数据状态：1：待审核
	var jsn_result = JSPFree.doClassMethodCall("com.yusys.wmp.process.service.WmpCrProcessBS", "updateDataByTaskByRids", {allrids:jso_allrids,status:'1',type:"1",userNo:str_LoginUserCode});
	if (jsn_result.msg == 'OK') {
		JSPFree.closeDialog("提交");
	}
}

//强制提交
function forcesubmission(){
	var jso_allrids=[]; 
	jso_allrids.push(taskId);

	JSPFree.confirm("提示","您确认要强制提交吗?",function(_isOK){
		if(_isOK){
			//弹出窗口,传入参数,然后接收返回值!
			JSPFree.openDialog("填写原因","/yujs/wmp/distribute_detail_fillin_reason.js",600,300,{templetcode:"/biapp-wmp/freexml/wmp/wmp_filling_process_remark.xml"},function(_rtdata){
				if(_rtdata != null && _rtdata != undefined && null != _rtdata.reason && undefined != _rtdata.reason){
					doUpdateBackStatus(d1_A_1_BillList,jso_allrids, _rtdata.reason);
				}
			});
		}
	});
}

//强制提交，修改状态
function doUpdateBackStatus(billList,_rids,_reason){
	var jso_par = {allrids:_rids,type:'1',reason:_reason,userNo:str_LoginUserCode};
	var jsn_result = JSPFree.doClassMethodCall("com.yusys.wmp.process.service.WmpCrProcessBS","doResultBack",jso_par);
	
	if (jsn_result.msg == 'OK') {
		JSPFree.closeDialog("强制提交");
	}
}

function getDate(dataDt) {
	var d = dataDt.replace(/-/g, '');
	return d;
}