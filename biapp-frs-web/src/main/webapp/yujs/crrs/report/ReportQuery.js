//初始化界面,菜单配置路径是【/frs/yufreejs?js=/yujs/crrs/xxx/xxx.js】
var maskUtil = "";
var isLoadTabb_2 = false;
var isLoadTabb_3 = false;
var isLoadTabb_4 = false;
var isLoadTabb_5 = false;
var isLoadTabb_6 = false;
function AfterInit(){
	maskUtil = FreeUtil.getMaskUtil();
	
	JSPFree.createTabb("d1", [ "对公及同业客户统计表", "集团客户、供应链融资统计表", "单一法人客户统计表", "对公客户担保情况统计表", "个人贷款违约情况统计表", "个人违约贷款担保情况统计表" ],175);
	JSPFree.createBillList("d1_1", "/biapp-crrs/freexml/crrs/generateXml/query/query_trade_and_public_CODE1.xml",null,{isSwitchQuery:"N"}); // 对公及同业客户统计表
	
	d1_1_BillList.pagerType="1";
	var _sql = "select * from v_crrs_trade_and_public_cut where "+ getQueryCondition(str_LoginUserOrgNo) +" order by data_dt desc";
	JSPFree.queryDataBySQL(d1_1_BillList, _sql);
	JSPFree.billListBindCustQueryEvent(d1_1_BillList, onQuery1Event);
	
	JSPFree.addTabbSelectChangedListener(d1_tabb, onSelect);
}
function onSelect(_index,_title){
	var newIndex = _index+1;

	if(newIndex==2){
		if(!isLoadTabb_2){
			JSPFree.createBillList("d1_2", "/biapp-crrs/freexml/crrs/generateXml/query/query_group_client_CODE1.xml",null,{isSwitchQuery:"N"}); // 集团客户、供应链融资统计表
			
			$.parser.parse('#d1_2');
			isLoadTabb_2 = true;
			d1_2_BillList.pagerType="1";
			var _sql = "select * from crrs_group_group_client where "+ getQueryCondition(str_LoginUserOrgNo) +" order by data_dt desc";
			JSPFree.queryDataBySQL(d1_2_BillList, _sql);
			JSPFree.billListBindCustQueryEvent(d1_2_BillList, onQuery2Event);
		}
	}
	if(newIndex==3){
		if(!isLoadTabb_3){
			JSPFree.createBillList("d1_3", "/biapp-crrs/freexml/crrs/generateXml/query/query_single_customer_CODE1.xml",null,{isSwitchQuery:"N"}); // 单一法人客户统计表
			
			$.parser.parse('#d1_3');
			isLoadTabb_3 = true;
			d1_3_BillList.pagerType="1";
			var _sql = "select * from crrs_single_corporation where "+ getQueryCondition(str_LoginUserOrgNo) +" order by data_dt desc";
			JSPFree.queryDataBySQL(d1_3_BillList, _sql);
			JSPFree.billListBindCustQueryEvent(d1_3_BillList, onQuery3Event);
		}
	}

	if(newIndex==4){
		if(!isLoadTabb_4){
			JSPFree.createBillList("d1_4", "/biapp-crrs/freexml/crrs/generateXml/query/query_public_guaranteed_CODE1.xml",null,{isSwitchQuery:"N"}); // 对公客户担保情况统计表
			
			$.parser.parse('#d1_4');
			isLoadTabb_4 = true;
			d1_4_BillList.pagerType="1";
			var _sql = "select * from (select a.*,b.inst_no,b.inst_name from crrs_ent_guaranteed a " +
						"left join crrs_single_corporation b " +
						"on a.customer_code = b.customer_code " +
						"and a.data_dt = b.data_dt) where " + 
						getQueryCondition(str_LoginUserOrgNo) +
						" order by data_dt desc";
			JSPFree.queryDataBySQL(d1_4_BillList, _sql);
			JSPFree.billListBindCustQueryEvent(d1_4_BillList, onQuery4Event);
		}
	}

	if(newIndex==5){
		if(!isLoadTabb_5){
			JSPFree.createBillList("d1_5", "/biapp-crrs/freexml/crrs/generateXml/query/query_PersonViolation_CODE1.xml",null,{isSwitchQuery:"N"}); // 个人贷款违约情况统计表
			
			$.parser.parse('#d1_5');
			isLoadTabb_5 = true;
			d1_5_BillList.pagerType="1";
			var _sql = "select * from v_crrs_person_personal where "+ getQueryCondition(str_LoginUserOrgNo) +" order by data_dt desc";
			JSPFree.queryDataBySQL(d1_5_BillList, _sql);
			JSPFree.billListBindCustQueryEvent(d1_5_BillList, onQuery5Event);
		}
	}

	if(newIndex==6){
		if(!isLoadTabb_6){
			JSPFree.createBillList("d1_6", "/biapp-crrs/freexml/crrs/generateXml/query/query_personGuaranteed_CODE1.xml",null,{isSwitchQuery:"N"}); // 个人违约贷款担保情况统计表

			$.parser.parse('#d1_6');
			isLoadTabb_6 = true;
			d1_6_BillList.pagerType="1";
			var _sql = "select * from (select a.*,b.inst_no,b.inst_name from crrs_person_ent_guaranteed a " +
					"left join crrs_person_personal b " +
					"on a.customer_code = b.customer_code " +
					"and a.data_dt = b.data_dt) where " +
					getQueryCondition(str_LoginUserOrgNo) +
					" order by data_dt desc";
			JSPFree.queryDataBySQL(d1_6_BillList, _sql);
			JSPFree.billListBindCustQueryEvent(d1_6_BillList, onQuery6Event);
		}
	}
}

/**
 * 查询列表加载默认查询条件
 * @param code
 * @returns
 */
function getQueryCondition(code){
	var jso_rt = JSPFree.doClassMethodCall("com.yusys.crrs.customer.service.ValidateQueryCondition","getQueryCondition",{"_loginUserOrgNo" : code});
	var condition = "";
	if(jso_rt.msg == "ok"){
		condition = jso_rt.condition;
	}

	return condition;
}

/**
 * 选中查询面板后，根据报送机构号，进行查询
 * @param code
 * @returns
 */
function getQueryOrgCondition(code){
	var jso_rt = JSPFree.doClassMethodCall("com.yusys.crrs.customer.service.ValidateQueryInstNoCondition","getQueryCondition",{"_loginUserOrgNo" : code});
	var condition = "";
	if(jso_rt.msg == "ok"){
		condition = jso_rt.condition;
	}

	return condition;
}

function onQuery1Event(_condition) {	
	var _sql = "select * from v_crrs_trade_and_public_cut ";

	if(_condition!="") {
		if (_condition.indexOf('inst_no') != -1) {
			// 如果存在inst_no，要获取inst_no
			var inst_no = JSPFree.getBillQueryFormValue(d1_1_BillQuery).inst_no;
			var c = getQueryOrgCondition(inst_no);
			
			_condition = _condition.substring(0, _condition.indexOf('inst_no')) ;
			_condition = _condition + c;
		} else {
			var c = getQueryCondition(str_LoginUserOrgNo);
			_condition = _condition + " and " + c;
		}
		_sql = _sql + " where " + _condition + " order by data_dt desc";
	} else {
		var c = getQueryCondition(str_LoginUserOrgNo);
		_sql = _sql + " where " + c + " order by data_dt desc";
	}
	
	JSPFree.queryDataBySQL(d1_1_BillList, _sql);
	FreeUtil.resetToFirstPage(d1_1_BillList); //手工跳转到第一页
}

function onQuery2Event(_condition) {	
	var _sql = "select * from crrs_group_group_client ";

	if(_condition!="") {
		if (_condition.indexOf('inst_no') != -1) {
			// 如果存在inst_no，要获取inst_no
			var inst_no = JSPFree.getBillQueryFormValue(d1_2_BillQuery).inst_no;
			var c = getQueryOrgCondition(inst_no);
			
			_condition = _condition.substring(0, _condition.indexOf('inst_no')) ;
			_condition = _condition + c;
		} else {
			var c = getQueryCondition(str_LoginUserOrgNo);
			_condition = _condition + " and " + c;
		}
		_sql = _sql + " where " + _condition + " order by data_dt desc";
	} else {
		var c = getQueryCondition(str_LoginUserOrgNo);
		_sql = _sql + " where " + c + " order by data_dt desc";
	}
	
	JSPFree.queryDataBySQL(d1_2_BillList, _sql);
	FreeUtil.resetToFirstPage(d1_2_BillList); //手工跳转到第一页
}

function onQuery3Event(_condition) {	
	var _sql = "select * from crrs_single_corporation ";

	if(_condition!="") {
		if (_condition.indexOf('inst_no') != -1) {
			// 如果存在inst_no，要获取inst_no
			var inst_no = JSPFree.getBillQueryFormValue(d1_3_BillQuery).inst_no;
			var c = getQueryOrgCondition(inst_no);
			
			_condition = _condition.substring(0, _condition.indexOf('inst_no')) ;
			_condition = _condition + c;
		} else {
			var c = getQueryCondition(str_LoginUserOrgNo);
			_condition = _condition + " and " + c;
		}
		_sql = _sql + " where " + _condition + " order by data_dt desc";
	} else {
		var c = getQueryCondition(str_LoginUserOrgNo);
		_sql = _sql + " where " + c + " order by data_dt desc";
	}
	
	JSPFree.queryDataBySQL(d1_3_BillList, _sql);
	FreeUtil.resetToFirstPage(d1_3_BillList); //手工跳转到第一页
}

function onQuery4Event(_condition) {	
	var _sql = "select * from (select a.*,b.inst_no,b.inst_name from crrs_ent_guaranteed a " +
			"left join crrs_single_corporation b " +
			"on a.customer_code = b.customer_code " +
			"and a.data_dt = b.data_dt)  ";

	if(_condition!="") {
		if (_condition.indexOf('inst_no') != -1) {
			// 如果存在inst_no，要获取inst_no
			var inst_no = JSPFree.getBillQueryFormValue(d1_4_BillQuery).inst_no;
			var c = getQueryOrgCondition(inst_no);
			
			_condition = _condition.substring(0, _condition.indexOf('inst_no')) ;
			_condition = _condition + c;
		} else {
			var c = getQueryCondition(str_LoginUserOrgNo);
			_condition = _condition + " and " + c;
		}
		_sql = _sql + " where " + _condition + " order by data_dt desc";
	} else {
		var c = getQueryCondition(str_LoginUserOrgNo);
		_sql = _sql + " where " + c + " order by data_dt desc";
	}
	
	JSPFree.queryDataBySQL(d1_4_BillList, _sql);
	FreeUtil.resetToFirstPage(d1_4_BillList); //手工跳转到第一页
}

function onQuery5Event(_condition) {	
	var _sql = "select * from v_crrs_person_personal ";

	if(_condition!="") {
		if (_condition.indexOf('inst_no') != -1) {
			// 如果存在inst_no，要获取inst_no
			var inst_no = JSPFree.getBillQueryFormValue(d1_5_BillQuery).inst_no;
			var c = getQueryOrgCondition(inst_no);
			
			_condition = _condition.substring(0, _condition.indexOf('inst_no')) ;
			_condition = _condition + c;
		} else {
			var c = getQueryCondition(str_LoginUserOrgNo);
			_condition = _condition + " and " + c;
		}
		_sql = _sql + " where " + _condition + " order by data_dt desc";
	} else {
		var c = getQueryCondition(str_LoginUserOrgNo);
		_sql = _sql + " where " + c + " order by data_dt desc";
	}
	
	JSPFree.queryDataBySQL(d1_5_BillList, _sql);
	FreeUtil.resetToFirstPage(d1_5_BillList); //手工跳转到第一页
}

function onQuery6Event(_condition) {	
	var _sql = "select * from (select a.*,b.inst_no,b.inst_name from crrs_person_ent_guaranteed a " +
				"left join crrs_person_personal b " +
				"on a.customer_code = b.customer_code " +
				"and a.data_dt = b.data_dt) ";
	
	if(_condition!="") {
		if (_condition.indexOf('inst_no') != -1) {
			// 如果存在inst_no，要获取inst_no
			var inst_no = JSPFree.getBillQueryFormValue(d1_6_BillQuery).inst_no;
			var c = getQueryOrgCondition(inst_no);
			
			_condition = _condition.substring(0, _condition.indexOf('inst_no')) ;
			_condition = _condition + c;
		} else {
			var c = getQueryCondition(str_LoginUserOrgNo);
			_condition = _condition + " and " + c;
		}
		_sql = _sql + " where " + _condition + " order by data_dt desc";
	} else {
		var c = getQueryCondition(str_LoginUserOrgNo);
		_sql = _sql + " where " + c + " order by data_dt desc";
	}
	
	JSPFree.queryDataBySQL(d1_6_BillList, _sql);
	FreeUtil.resetToFirstPage(d1_6_BillList); //手工跳转到第一页
}

// 对公及同业客户统计表  - 授信信息
function creditInfo1(){
	var jso_OpenPars = d1_1_BillList.datagrid('getSelected');
	if (jso_OpenPars == null) {
		$.messager.alert('提示', '必须选择一条记录进行操作', 'warning');
		return;
	}

	var def = {customer_code:jso_OpenPars.customer_code,
				customer_type:jso_OpenPars.customer_type,
				data_dt:jso_OpenPars.data_dt};
	JSPFree.openDialog("对公及同业客户统计表","/yujs/crrs/report/query/TradeAndPublicCustomer.js",900,600,
			def,function(_rtdata){
	});
}

//集团客户、供应链融资统计表详细信息
function groupDetailFun(){
	var jso_OpenPars = d1_2_BillList.datagrid('getSelected');
	if (jso_OpenPars == null) {
		$.messager.alert('提示', '必须选择一条记录进行操作', 'warning');
		return;
	}
	
	var def = {customer_code:jso_OpenPars.customer_code,
				data_dt:jso_OpenPars.data_dt};
	JSPFree.openDialog("集团客户、供应链融资统计表","/yujs/crrs/report/query/GroupAndSupplyChain.js",960,600,
			def,function(_rtdata){
	});
}

// 单一法人客户统计表详细信息
function singleDetailFun(_this){
	var jso_OpenPars = d1_3_BillList.datagrid('getSelected');
	if (jso_OpenPars == null) {
		$.messager.alert('提示', '必须选择一条记录进行操作', 'warning');
		return;
	}

	var def = {customer_code:jso_OpenPars.customer_code,
				data_dt:jso_OpenPars.data_dt};
	JSPFree.openDialog("单一法人客户统计表","/yujs/crrs/report/query/SingleAndCustomer.js",900,600,
			def,function(_rtdata){
	});
}

// 对公客户担保情况统计表
function singleGuaranteed(_this){
	var jso_OpenPars = d1_4_BillList.datagrid('getSelected');
	if (jso_OpenPars == null) {
		$.messager.alert('提示', '必须选择一条记录进行操作', 'warning');
		return;
	}

	var def = {customer_code:jso_OpenPars.customer_code,
				data_dt:jso_OpenPars.data_dt};
	JSPFree.openDialog("对公客户担保情况统计表","/yujs/crrs/report/query/SingleCustomerGuaranteed.js",900,600,
			def,function(_rtdata){
	});
}

//个人贷款违约情况统计表 - 详细信息
function personViolation(){
	var jso_OpenPars = d1_5_BillList.datagrid('getSelected');
	if (jso_OpenPars == null) {
		$.messager.alert('提示', '必须选择一条记录进行操作', 'warning');
		return;
	}

	var def = {customer_code:jso_OpenPars.customer_code,
				data_dt:jso_OpenPars.data_dt};
	JSPFree.openDialog("个人贷款违约情况统计表","/yujs/crrs/report/query/PersonViolation.js",960,600,
			def,function(_rtdata){
	});
}

//个人违约贷款担保情况统计表 - 详细信息
function personGuaranteed(){
	var jso_OpenPars = d1_6_BillList.datagrid('getSelected');
	if (jso_OpenPars == null) {
		$.messager.alert('提示', '必须选择一条记录进行操作', 'warning');
		return;
	}

	var def = {customer_code:jso_OpenPars.customer_code,
				data_dt:jso_OpenPars.data_dt};
	JSPFree.openDialog("个人违约贷款担保情况统计表","/yujs/crrs/report/query/PersonGuaranteed.js",960,600,
			def,function(_rtdata){
	});
}

/**
 * 导出
 * @returns
 */
function download1(){
	download(d1_1_BillList, d1_1_BillQuery, "1")
}
function download2(){
	download(d1_2_BillList, d1_2_BillQuery, "2")
}
function download3(){
	download(d1_3_BillList, d1_3_BillQuery, "3")
}
function download4(){
	download(d1_4_BillList, d1_4_BillQuery, "4")
}
function download5(){
	download(d1_5_BillList, d1_5_BillQuery, "5")
}
function download6(){
	download(d1_6_BillList, d1_6_BillQuery, "6")
}

function download(_BillList, _BillQuery, type) {
	if (_BillList.CurrSQL3 == null || "undefined" == _BillList.CurrSQL3) {
		JSPFree.alert("当前无记录！");
		return;
	}

    var dataDt = JSPFree.getBillQueryFormValue(_BillQuery).data_dt;
    if (dataDt == null || dataDt == ""  || "undefined" == dataDt) {
    	JSPFree.alert("请选择日期！");
		return;
    }

    var instNo = JSPFree.getBillQueryFormValue(_BillQuery).inst_no;
    if (instNo == null || instNo == ""  || "undefined" == instNo) {
    	JSPFree.alert("请选择经办机构号！");
		return;
    }
    
    // 如果超过50w数据,页面提示语
    var sql = _BillList.CurrSQL3;
	var new_sql = 'select count(*) c from ('+sql+') t';
	var jso_data = JSPFree.getHashVOs(new_sql);
	var c = jso_data[0].c;
	var jso_rt = JSPFree.doClassMethodCall("com.yusys.crrs.batchExport.service.CrrsDownloadBS", "getDownloadNum",{});
	if (parseInt(c) > parseInt(jso_rt.downloadNum)) {
		JSPFree.confirm('提示', '当前导出数据量超过规定条数，将导出【'+jso_rt.downloadNum+'】条数数据，是否导出?', function(_isOK){
			if (_isOK) {
				maskUtil.mask();
				setTimeout(function () {
					var json = JSPFree.doClassMethodCall("com.yusys.crrs.batchExport.service.CrrsFxyjDownloadBS", "downloadExcel",{dataDt: dataDt, instNo: instNo, type: type});
					if (json.code='success') {
						var filepath = json.data;
						var src = v_context + "/crrs/fxyj/download/export?filepath=" + filepath + "&type=" + type;
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
		    var json = JSPFree.doClassMethodCall("com.yusys.crrs.batchExport.service.CrrsFxyjDownloadBS", "downloadExcel",{dataDt: dataDt, instNo: instNo, type: type});
			if (json.code='success') {
				var filepath = json.data;
				var src = v_context + "/crrs/fxyj/download/export?filepath=" + filepath + "&type=" + type;
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