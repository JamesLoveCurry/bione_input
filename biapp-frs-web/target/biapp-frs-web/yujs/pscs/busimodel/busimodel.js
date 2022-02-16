//初始化界面,菜单配置路径是【/frs/yufreejs?js=/yujs/east/58table.js】
function AfterInit(){
	JSPFree.createBillList("d1","/biapp-pscs/freexml/pscs/busimodel/pscs_cr_tab_CODE1.xml",null,{isSwitchQuery:"N"});
}

// 维护字段
function maintainCols() {
	var json_data = d1_BillList.datagrid('getSelections');
	if (json_data == null || json_data == undefined || json_data.length ==0) {
		$.messager.alert('提示', '必须选择一条记录进行操作', 'warning');
		return;
	}
	if (json_data.length>1) {
		$.messager.alert('提示', '只能选择一条记录进行操作', 'warning');
		return;
	}
	//弹出窗口,传入参数,然后接收返回值!
	JSPFree.openDialog("表字段","/yujs/pscs/busimodel/maintaincols.js",900,485,json_data[0],function(_rtdata){
		
	});
}

// 校验表结构
function checkCols() {
	var json_data = d1_BillList.datagrid('getSelections');
	if (json_data == null || json_data == undefined || json_data.length == 0) {
		$.messager.alert('提示', '必须选择一条记录进行操作', 'warning');
		return;
	}
	if (json_data.length>1) {
		$.messager.alert('提示', '只能选择一条记录进行操作', 'warning');
		return;
	}
	var jso_Pars = {tabNameEn:json_data[0].tab_name_en,tabName:json_data[0].tab_name};
	
	var jsn_result = JSPFree.doClassMethodCall("com.yusys.pscs.busimodel.PscsCrTabBS", "verificationData", jso_Pars);

	if(jsn_result.result=="false"){
		bottomRight(jsn_result.msg);
	} else{
		//弹出窗口,传入参数,然后接收返回值!
		var str_html = jsn_result.msg;
		JSPFree.openHtmlMsgBox("系统中未匹配上字段",900,560,str_html);
	}
}

//删除tab表，同时级联删除col表数据
function deleteTab(){
	var json_data = d1_BillList.datagrid('getSelections');
	if (json_data == null || json_data == undefined || json_data.length == 0) {
		$.messager.alert('提示', '必须至少选择一条记录进行操作', 'warning');
		return;
	}
	
	JSPFree.confirm('提示', '你真的要删除选中的记录吗?', function(_isOK){
		if (_isOK){
			var jso_alltabs=[]; //存放所有选中表名和英文表名的数组
			for(var i=0; i<json_data.length; i++){
				var jso_tab=[]; //存放一条表名和英文表名的数组
				jso_tab.push(json_data[i].tab_name_en);
				jso_tab.push(json_data[i].tab_name);
				jso_alltabs.push(jso_tab);
			}
			var jsn_result = JSPFree.doClassMethodCall("com.yusys.pscs.busimodel.PscsCrTabBS", "deleteColsByTabName", {alltabs:jso_alltabs});
			if (jsn_result.msg == 'OK') {
				$.messager.alert('提示', '删除数据成功!', 'info');
			}
			if (jsn_result.msg == 'OK') {
				JSPFree.queryDataByConditon(d1_BillList);
			}
		}
	});
}

function bottomRight(_msg){
	$.messager.show({
		title:'消息提示',
		msg:_msg,
		showType:'show'
	});
}

