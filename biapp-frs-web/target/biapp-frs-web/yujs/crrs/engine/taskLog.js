//初始化界面,菜单配置路径是【/frs/yufreejs?js=/yujs/east/58table.js】
//初始化界面
var taskId="";
function AfterInit(){
JSPFree.createTabb("d1",["按任务查看","日志总览"]);  //创建多页签

JSPFree.createSplit("d1_1","上下",350);  //第1个页签先左右分割

JSPFree.createBillList("d1_1_A","/biapp-crrs/freexml/crrs/engine/crrs_engine_task_forlog.xml");  //第1个页签上边
JSPFree.createBillList("d1_1_B","/biapp-crrs/freexml/crrs/engine/crrs_engine_log_CODE1.xml");  //第1个页签右边的下边
JSPFree.createBillList("d1_2","/biapp-crrs/freexml/crrs/engine/crrs_engine_log_task_CODE.xml");

//绑定表格选择事件,d1_1_A_BillList会根据命名规则已创建
JSPFree.bindSelectEvent(d1_1_A_BillList,function(rowIndex, rowData){
  var task_id = rowData.task_id;  //取得选中记录中的id值
  taskId=task_id;
  var str_sqlWhere = "task_id='"  + task_id + "'";  //拼SQL条件
  JSPFree.queryDataByConditon(d1_1_B_BillList,str_sqlWhere);  //锁定规则表查询数据
});

}

// 出报送数据
function download1() {
	var json_data = d1_1_A_BillList.datagrid('getSelected');
	if (json_data == null) {
		$.messager.alert('提示', '必须选择一条记录进行操作', 'warning');
	}
	
	// alert(JSON.stringify(json_data));
	
	var download=null;
	download = $('<iframe id="download" style="display: none;"/>');
	$('body').append(download);
	/*查询条件之检核任务*/
	var task_id = json_data.task_id;

	var src = v_context + "/crrs/engine/log/download?taskId="
			+ task_id;
	download.attr('src', src);
}

//出报送数据
function download2() {
	var download=null;
	download = $('<iframe id="download" style="display: none;"/>');
	$('body').append(download);
	/*查询条件之检核任务*/
	var task_name = JSPFree.getBillQueryFormValue(d1_2_BillQuery).task_name;
	/*查询条件之数据日期*/
	var data_dt = JSPFree.getBillQueryFormValue(d1_2_BillQuery).data_dt;
	var task_type = JSPFree.getBillQueryFormValue(d1_2_BillQuery).task_type;
	var rule_ids = JSPFree.getBillQueryFormValue(d1_2_BillQuery).rule_ids;
	var rule_name = JSPFree.getBillQueryFormValue(d1_2_BillQuery).rule_name;
	var tab_names = JSPFree.getBillQueryFormValue(d1_2_BillQuery).tab_names;

	var src = v_context + "/crrs/engine/log/downloadlog?taskName="
			+ task_name
			+ "&dataDt="
			+ data_dt
			+ "&taskType="
			+ task_type
			+ "&ruleIds="
			+ rule_ids
			+ "&ruleName="
			+ rule_name
			+ "&tabNames="
			+ tab_names;

	download.attr('src', src);
}

//点击查看命令
function onLookCommand(_btn){
  var str_cmd = _btn.dataset.itemvalue;
  FreeUtil.openHtmlMsgBox("查看实际命令",800,500,"<span style='font-size:14px;color:blue'>" + str_cmd + "</span>");
}