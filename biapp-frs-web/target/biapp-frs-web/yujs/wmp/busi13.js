var str_ds = null;
var str_className = null;
var tab_name = "";
var tab_name_en = "";
function AfterInit(){
	str_ds = jso_OpenPars.ds;  //数据源
	tab_name = jso_OpenPars.tab_name;
	tab_name_en = jso_OpenPars.tab_name_en;
	str_className = "Class:com.yusys.wmp.business.service.FrsWmpModelTempletBuilder.getTemplet('" + jso_OpenPars.tab_name + "','" + jso_OpenPars.tab_name_en + "')";

	JSPFree.createBillList("d1",str_className,null,{list_btns:"[icon-add]新增/insert1;[icon-edit]编辑/update1;[icon-remove]删除/delete1;$VIEW;[icon-p69]导出/exports(this)",isSwitchQuery:"N",list_ischeckstyle:"Y",list_ismultisel:"Y"});
}

//新增
function insert1(){
	var _par = {templet:str_className,tabname:tab_name,tabnameen:tab_name_en,ds:str_ds};
	JSPFree.openDialog("新增","/yujs/wmp/addTabDetail.js",900,560,_par,function(_rtdata){
		if (_rtdata == "校验并保存成功") {
			JSPFree.alert("校验并保存成功");
		}
	},true);
}

/**
 * 修改
 * @return {[type]} [description]
 */
function update1(){
	var data = d1_BillList.datagrid('getSelections');
	if (data == null || data.length==0) {
		$.messager.alert('提示', '必须选择一条记录进行操作', 'warning');
		return;
	}else if (data.length>1) {
		$.messager.alert('提示', '只能选择一条记录进行操作', 'warning');
		return;
	}
	
	var row = data[0]; //拿到这一行的数据
	var str_date = row.stat_dt;
	var jso_data = JSPFree.getHashVOs("select status from frs_wmp_lock_data where data_dt='" + str_date + "' and status='锁定'");
	if(jso_data.length>0){  //如果的确有锁定,则提示不允许处理
		JSPFree.alert("日期[" + str_date + "]的数据已经被锁定,不能编辑!<br>请在【数据处理->数据锁定】中进行设置");
		return;
	}
	
	var _par = {
			data: row,
			templet: str_className,
			tabname: tab_name,
			tabnameen: tab_name_en,
			ds: str_ds
	};
	JSPFree.openDialog2("编辑","/yujs/wmp/editTabDetail.js",950,600,_par, function(_rtdata){
		if (_rtdata == "校验并保存成功") {
			JSPFree.alert("校验并保存成功");
			JSPFree.refreshBillListCurrRow(d1_BillList); // 刷新当前行
		}
	},true);
}

/**
 * 删除
 * @return {[type]} [description]
 */
function delete1(){
	var data = d1_BillList.datagrid('getSelections');
	if (data == null || data.length==0) {
		$.messager.alert('提示', '必须选择一条记录进行操作', 'warning');
		return;
	}
	
	var dates = []; //存放所有日期，重复日期存放一次
	for(var i=0; i<data.length; i++){
		if (data[i].stat_dt!=null && data[i].stat_dt!="" && dates.indexOf(data[i].stat_dt)==-1) {
			dates.push(data[i].stat_dt);
		}
	}
	
	var locks = []; //存放所有被锁定日期
	for(var i=0; i<dates.length; i++){
		var jso_data = JSPFree.getHashVOs("select status from frs_wmp_lock_data where data_dt='" + dates[i] + "' and status='锁定'");
		if(jso_data.length>0){
			locks.push(dates[i]);
		}
	}
	
	var str_lock_dates = "";
	if(locks.length>0){ //如果存在锁定日期
		for(var i=0; i<locks.length; i++){
			if(i==locks.length-1){
				str_lock_dates = str_lock_dates + locks[i];
			}else{
				str_lock_dates = str_lock_dates + locks[i] + ",";
			}
		}
		
		JSPFree.alert("日期[" + str_lock_dates + "]的数据已经被锁定,不能编辑!<br>请在【数据处理->数据锁定】中进行设置");
		return;
	}
	
	JSPFree.doBillListBatchDelete(d1_BillList);
}

/**
 * 导出
 * @returns
 */
function exports(){
	if (d1_BillList.CurrSQL3 == null || "undefined" == d1_BillList.CurrSQL3) {
		JSPFree.alert("当前无记录！");
		return;
	}
	
	// 如果超过50w数据,页面提示语
	var sql = d1_BillList.CurrSQL3;
	var new_sql = 'select count(*) c from ('+sql+') t';
	var jso_data = JSPFree.getHashVOsByDS(new_sql, str_ds);
	var c = jso_data[0].c;
	
	var data_dt = JSPFree.getBillQueryFormValue(d1_BillQuery).stat_dt;
	var src = v_context + "/wmp/business/data/exportCSV?org_no="+str_LoginUserOrgNo 
		+ "&data_dt=" + data_dt
		+ "&tab_name=" + tab_name
		+ "&tab_name_en=" + tab_name_en 
		+ "&strsql=" + encodeURIComponent(d1_BillList.CurrSQL3);
	
	var jso_rt = JSPFree.doClassMethodCall("com.yusys.wmp.business.service.FrsWmpBusinessBS", "getMaxDataNum",{});

	if (c > jso_rt.download_num) {
		JSPFree.alert("当前导出数据量较大，请稍后服务器上进行下载");
		var xmlhttp = new XMLHttpRequest();
		xmlhttp.open("GET", src);
		xmlhttp.send();
		
		return;
	}
	
	var download = $('<iframe id="download" style="display: none;"/>');
	$('body').append(download);
	
	download.attr('src', src);
}