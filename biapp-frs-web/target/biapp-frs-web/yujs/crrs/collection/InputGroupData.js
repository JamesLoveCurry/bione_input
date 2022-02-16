//初始化界面,菜单配置路径是【/frs/yufreejs?js=/yujs/crrs/xxx/xxx.js】
var maskUtil = "";
function AfterInit() {
	maskUtil = FreeUtil.getMaskUtil();
	
	JSPFree.createBillList("d1","/biapp-crrs/freexml/crrs/group/crrs_group_group_client_CODE1.xml",null,{list_btns:"[icon-add]新增/insert1;[icon-edit]编辑/update1;[icon-remove]删除/delete1;[icon-p67]下载模板/downModel;[icon-p68]导入/importGroupFun;[icon-p67]导出/downSingleFun;[icon-p31]查看/onView"
		,querycontion:"type='1'",autocondition:"type='1'",list_ischeckstyle:"N",list_ismultisel:"N",ishavebillquery:"Y",autoquery:"N",isSwitchQuery:"N"});

	d1_BillList.pagerType="2";
	var _sql = "select * from crrs_group_group_client where type = '1' and "+ getQueryCondition(str_LoginUserOrgNo) +" order by data_dt desc";
	JSPFree.queryDataBySQL(d1_BillList, _sql);
	JSPFree.billListBindCustQueryEvent(d1_BillList, onQueryEvent);
}

//查询列表加载默认查询条件
function getQueryCondition(code){
	var jso_rt = JSPFree.doClassMethodCall("com.yusys.crrs.customer.service.ValidateQueryCondition","getQueryCondition",{"_loginUserOrgNo" : code});
	var condition = "";
	if(jso_rt.msg == "ok"){
		condition = jso_rt.condition;
	}

	return condition;
}

//选中查询面板后，根据报送机构号，进行查询
function getQueryIssuedNoCondition(code){
	var jso_rt = JSPFree.doClassMethodCall("com.yusys.crrs.customer.service.ValidateQueryIssuedNoCondition","getQueryCondition",{"_reportOrgNo" : code});
	var condition = "";
	if(jso_rt.msg == "ok"){
		condition = jso_rt.condition;
	}

	return condition;
}

function onQueryEvent(_condition) {	
	var _sql = "select * from crrs_group_group_client where type = '1' ";
debugger;
	if(_condition!="") {
		if (_condition.indexOf('issued_no') != -1) {
			// 如果存在issued_no，要获取issued_no
			var issued_no = JSPFree.getBillQueryFormValue(d1_BillQuery).issued_no;
			var c = getQueryIssuedNoCondition(issued_no);
			
			_condition = _condition.substring(0, _condition.indexOf('issued_no')) ;
			_condition = _condition + c;
		} else {
			var c = getQueryCondition(str_LoginUserOrgNo);
			_condition = _condition + " and " + c;
		}
		_sql = _sql + " and " + _condition + " order by data_dt desc";
	} else {
		var c = getQueryCondition(str_LoginUserOrgNo);
		_sql = _sql + " and " + c + " order by data_dt desc";
	}
	
	JSPFree.queryDataBySQL(d1_BillList, _sql);
	FreeUtil.resetToFirstPage(d1_BillList); //手工跳转到第一页
}

/**
 * 导入集团客户
 * @return {[type]} [description]
 */
function importGroupFun(){
	JSPFree.openDialog("文件上传","/yujs/crrs/collection/importGroup.js", 500, 240, null,function(_rtdata){
		if (_rtdata == true) {
        	JSPFree.alert("上传成功");
        	JSPFree.queryDataByConditon(d1_BillList,null);
		}
	});
}

/**
 * 模板下载
 * @returns
 */
function downModel(){
	var download=null;
	download = $('<iframe id="download" style="display: none;"/>');
	$('body').append(download);
	
	var src = v_context + "/crrs/common/downLoadModel?fileNm=22-GroupInfo.xls";
	download.attr('src', src);
}

/**
 * 导出数据
 * @returns
 */
function downSingleFun() {
	var type = "2";
	
	if (d1_BillList.CurrSQL3 == null || "undefined" == d1_BillList.CurrSQL3) {
		JSPFree.alert("当前无记录！");
		return;
	}

    var dataDt = JSPFree.getBillQueryFormValue(d1_BillQuery).data_dt;
    if (dataDt == null || dataDt == ""  || "undefined" == dataDt) {
    	JSPFree.alert("请选择日期！");
		return;
    }

    var instNo = JSPFree.getBillQueryFormValue(d1_BillQuery).issued_no;
    if (instNo == null || instNo == ""  || "undefined" == instNo) {
    	JSPFree.alert("请选择填报机构号！");
		return;
    }
    
    // 如果超过50w数据,页面提示语
    var sql = d1_BillList.CurrSQL3;
	var new_sql = 'select count(*) c from ('+sql+') t';
	var jso_data = JSPFree.getHashVOs(new_sql);
	var c = jso_data[0].c;
	var jso_rt = JSPFree.doClassMethodCall("com.yusys.crrs.batchExport.service.CrrsDownloadBS", "getDownloadNum",{});
	if (parseInt(c) > parseInt(jso_rt.downloadNum)) {
		JSPFree.confirm('提示', '当前导出数据量超过规定条数，将导出【'+jso_rt.downloadNum+'】条数数据，是否导出?', function(_isOK){
			if (_isOK) {
				maskUtil.mask();
				setTimeout(function () {
					var json = JSPFree.doClassMethodCall("com.yusys.crrs.batchExport.service.CrrsDownloadBS", "downloadExcel",{dataDt: dataDt, instNo: instNo, type: type});
					if (json.code='success') {
						var filepath = json.data;
						var src = v_context + "/crrs/download/export?filepath=" + filepath + "&dataDt=" + dataDt + "&type=" + type;
						var download = $('<iframe id="download" style="display: none;"/>');
						$('body').append(download);
						download.attr('src', src);
					} else {
						$.messager.alert('提示', json.msg, 'warning');
					}
					maskUtil.unmask();
				}, 100);
			}
		});
	} else {
		maskUtil.mask();
		setTimeout(function () {
		    var json = JSPFree.doClassMethodCall("com.yusys.crrs.batchExport.service.CrrsDownloadBS", "downloadExcel",{dataDt: dataDt, instNo: instNo, type: type});
			if (json.code='success') {
				var filepath = json.data;
				var src = v_context + "/crrs/download/export?filepath=" + filepath + "&dataDt=" + dataDt + "&type=" + type;
				var download = $('<iframe id="download" style="display: none;"/>');
				$('body').append(download);
				download.attr('src', src);
			} else {
				$.messager.alert('提示', json.msg, 'warning');
			}
			maskUtil.unmask();
		}, 100);
	}
}
/**
 * 新增
 * @return {[type]} [description]
 */
function insert1(){
	JSPFree.openDialog("基础信息","/yujs/crrs/collection/addGroup.js", 950,600, null,function(_rtdata){
		JSPFree.queryDataByConditon(d1_BillList,null);  //立即查询刷新数据
	},true);
}

/**
 * 修改
 * @return {[type]} [description]
 */
function update1(){
	var group = d1_BillList.datagrid('getSelected');
	if (group == null) {
		$.messager.alert('提示', '必须选择一条记录进行操作', 'warning');
		return;
	}
	JSPFree.openDialog2("基础信息","/yujs/crrs/collection/editGroup.js",950,600,{group:group},function(_rtdata){
		JSPFree.refreshBillListCurrPage(d1_BillList);
	},true);
}

/**
 * 查看规则
 * @return {[type]} [description]
 */
function viewRule(){
	var jso_par1 = {list_btns:"",isSwitchQuery:"N",autoquery:"Y",ishavebillquery:"N",list_ispagebar:"Y",
			autocondition:"tablename_en in ('CRRS_GROUP_GROUP_CLIENT','crrs_group_executives','crrs_group_members'," +
					"'crrs_group_actualcontroller','crrs_group_ffiliated_groups','crrs_ent_credit','crrs_ent_group_client') and ruletype='确定性校验'"};
	var jso_par2 = {list_btns:"",isSwitchQuery:"N",autoquery:"Y",ishavebillquery:"N",list_ispagebar:"Y",
			autocondition:"tablename_en in ('CRRS_GROUP_GROUP_CLIENT','crrs_group_executives','crrs_group_members'," +
					"'crrs_group_actualcontroller','crrs_group_ffiliated_groups','crrs_ent_credit','crrs_ent_group_client') and ruletype='一致性校验'"};
	var jso_par3 = {list_btns:"",isSwitchQuery:"N",autoquery:"Y",ishavebillquery:"N",list_ispagebar:"Y",
			autocondition:"tablename_en in ('CRRS_GROUP_GROUP_CLIENT','crrs_group_executives','crrs_group_members'," +
					"'crrs_group_actualcontroller','crrs_group_ffiliated_groups','crrs_ent_credit','crrs_ent_group_client') and ruletype='提示性校验'"};
	var jso_par ={jso_par1:jso_par1,jso_par2:jso_par2,jso_par3:jso_par3};
	JSPFree.openDialog2("基础数据","/yujs/crrs/collection/ViewRule.js",960,600,jso_par);
}

/**
 * 查看
 */
function onView(){
	var group = d1_BillList.datagrid('getSelected');
	if (group == null) {
		$.messager.alert('提示', '必须选择一条记录进行操作', 'warning');
		return;
	}
	FreeUtil.setClientEnv("group", group);

	JSPFree.openDialog2("基础数据","/yujs/crrs/dataquery/viewGroup.js",950,600,{group:group},true);
}

/**
 * 删除
 * @return {[type]} [description]
 */
function delete1(){
	var group = d1_BillList.datagrid('getSelected');
	if (group == null) {
		$.messager.alert('提示', '必须选择一条记录进行操作', 'warning');
		return;
	}

	if ("锁定" == group.status) {
		JSPFree.alert("当前数据已锁定，不能进行删除操作！");
		return;
	}
	
	JSPFree.confirm("提示", "你真的要删除选中的记录吗?", function(_isOK){
		if(_isOK){
			var jso_rt = JSPFree.doClassMethodCall("com.yusys.crrs.collection.service.CrrsCollectionBS", "deleteGroupData",{rid:group.rid,customer_code:group.customer_code,data_dt:group.data_dt});
			if(jso_rt.msg == "OK"){
				$.messager.show({title:'消息提示',msg: '删除成功',showType:'show'});
				JSPFree.queryDataByConditon(d1_BillList);  //立即查询刷新数据
			}
			else{
				JSPFree.alert(jso_rt.msg);
			}
		}
	});
}