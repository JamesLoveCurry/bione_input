//初始化界面,菜单配置路径是【/frs/yufreejs?js=/yujs/east/58table.js】
function AfterInit() {
	JSPFree.createBillList("d1", "east/engineTask/batch_tab_east_cr_task_CODE");

	var nowdate = new Date();
	var y = nowdate.getFullYear();
	var m = nowdate.getMonth() + 1;
	var d = nowdate.getDate();
	if(m<10){
		m="0"+m;
	}
	if(d<10){
		d="0"+d;
	}
	var formatnowdate ="" + y +"-" +m+"-" +d;

	// 获取系统前一个月的时间
	nowdate.setMonth(nowdate.getMonth() - 1);
	var y = nowdate.getFullYear();
	var m = nowdate.getMonth() + 1;
	var d = nowdate.getDate();
	if(m<10){
		m="0"+m;
	}
	if(d<10){
		d="0"+d;
	}
	var formatwdate ="" + y +"-" +m+"-" +d;

	str_sqlWhere = "rule_name is null and data_dt>='" + formatwdate
			+ "' and data_dt <='" + formatnowdate + "'"; // 拼SQL条件
	JSPFree.queryDataByConditon(d1_BillList, str_sqlWhere); // 锁定规则表查询数据
}
function tabscreate() {

	var jso_par = {};
	// 获取时间
	var data_dt = JSPFree.getBillQueryFormValue(d1_BillQuery).data_dt;
	jso_par.data_dt = data_dt;
	if (null == data_dt || "" == data_dt) {
		jso_par.data_dt = getOneMonth();
	}
	var jso_data = JSPFree
			.doClassMethodCall(
					"com.yusys.east.checkrule.detailmaintenance.service.EastCrTaskBatchBS",
					"batchTabCreate", jso_par);
}

function getOneMonth() {
	// 【20190612】-【20190612】
	var nowdate = new Date();
	var y = nowdate.getFullYear();
	var m = nowdate.getMonth() + 1;
	var d = nowdate.getDate();
	// var formatnowdate = y + '-' + m + '-' + d;
	if(m<10){
		m="0"+m;
	}
	if(d<10){
		d="0"+d;
	}
	var formatnowdate = "" + y +"-" +m+"-" +d;

	// 获取系统前一个月的时间
	nowdate.setMonth(nowdate.getMonth() - 1);
	var y = nowdate.getFullYear();
	var m = nowdate.getMonth() + 1;
	var d = nowdate.getDate();
	if(m<10){
		m="0"+m;
	}
	if(d<10){
		d="0"+d;
	}
	
	var formatwdate ="" + y +"-" +m+"-" +d;
	var data_dt = "";
	data_dt="【"+formatwdate+"】-【"+formatnowdate+"】";
	
	return data_dt;
}
