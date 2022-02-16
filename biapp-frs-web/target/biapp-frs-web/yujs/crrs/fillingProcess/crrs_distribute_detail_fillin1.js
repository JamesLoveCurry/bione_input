/**
 * 
 * <pre>
 * Title: 【报表处理】-【报表填报】
 * Description: 报表处理-报表填报：填报按钮的主页面
 * 列表展示子任务中的具体错误明细数据，选择一条错误明细数据，即可对数据进行填报。已填报的会放在“已填报页”
 * </pre>
 * @author wangxy31 
 * @version 1.00.00
   @date 2021年10月27日
 */

var tabName = "";
var tabNameEn = "";
var tabCode = "";
var taskId = "";
var _sql = "";
var _sql1 = "";
var _date = "";
var isLoadTabb_2 = false;
var org_no = "";
var st_date = "";
var condition1 = "";
var condition2 = "";
var maskUtil = "";

var isFromModel = ""; // 该字段表示是来源于填报页面（1），还是审核页面（2）
function AfterInit() {
	maskUtil = FreeUtil.getMaskUtil();

	isFromModel = jso_OpenPars.isFromModel;
	taskId = jso_OpenPars.taskId;
	tabName = jso_OpenPars.tabName;
	tabNameEn = jso_OpenPars.tabNameEn;
	tabCode = jso_OpenPars.tabCode;
	dataDt = jso_OpenPars.dataDt;
	org_no = jso_OpenPars.orgNo;
	st_date = "1,2";
	
	var list_btns1 = "";
	var list_btns2 = "";
	var list_btns3 = "";
	if (isFromModel == "2") {
		data = jso_OpenPars.data;
		tabName = data.tab_name;
		tabNameEn = data.tab_name_en;
		tabCode = data.tab_code;
		dataDt = data.data_dt;
		taskId = data.rid;
		org_no = data.org_no;
		_date = getDate(dataDt);
		
		var c = getQueryCondition();
		condition1 = c + " and customer_type = '"+ tabCode +"' and result_status in ('1', '2') and reportdate = '"+dataDt+"' and "+tabNameEn;
		condition2 = c + " and customer_type = '"+ tabCode +"' and result_status = '3' and reportdate = '"+dataDt+"' and "+tabNameEn;
		
		JSPFree.createSplitByBtn("d1","上下",430,["通过/onConfirm","退回/onBack"]);
		
		JSPFree.createTabb("d1_A", [ "待填报", "已填报" ]);
		JSPFree.createTabb("d1_A_1",["确定性校验","一致性校验","提示性校验"]);
		JSPFree.createTabb("d1_A_2",["确定性校验","一致性校验","提示性校验"]);
		
		list_btns1 = "$VIEW";
		list_btns2 = "$VIEW";
		list_btns3 = "$VIEW";
		list_btns4 = "$VIEW";
		list_btns5 = "$VIEW";
		list_btns6 = "$VIEW";
	} else {
		_date = getDate(jso_OpenPars.dataDt);
		
		var c = getQueryCondition();
		condition1 = c + " and customer_type = '"+ tabCode +"' and result_status in ('1', '2') and reportdate = '"+dataDt+"' and "+tabNameEn;
		condition2 = c + " and customer_type = '"+ tabCode +"' and result_status = '3' and reportdate = '"+dataDt+"' and "+tabNameEn;
		
		JSPFree.createSplitByBtn("d1","上下",430,["提交/submission","强制提交/forcesubmission"]);
		
		JSPFree.createTabb("d1_A", [ "待填报", "已填报" ]);
		JSPFree.createTabb("d1_A_1",["确定性校验","一致性校验","提示性校验"]);
		JSPFree.createTabb("d1_A_2",["确定性校验","一致性校验","提示性校验"]);
		
		list_btns1 = "[icon-p41]关联处理/onHandle_1;[icon-p41]单一处理/onHandle_1_1;$VIEW";
		list_btns2 = "[icon-p41]关联处理/onHandle_2;[icon-p41]单一处理/onHandle_2_1;$VIEW";
		list_btns3 = "[icon-p41]关联处理/onHandle_3;[icon-p41]单一处理/onHandle_3_1;$VIEW";
		
		list_btns4 = "[icon-p41]关联处理/onHandle_4;[icon-p41]单一处理/onHandle_4_1;$VIEW";
		list_btns5 = "[icon-p41]关联处理/onHandle_5;[icon-p41]单一处理/onHandle_5_1;$VIEW";
		list_btns6 = "[icon-p41]关联处理/onHandle_6;[icon-p41]单一处理/onHandle_6_1;$VIEW";
	}
	
	JSPFree.createBillList("d1_A_1_1","/biapp-crrs/freexml/crrs/ruleNew/crrs_result_sure_Submit_new_CODE1.xml",null,{list_btns:list_btns1,list_ischeckstyle:"N",list_ismultisel:"N",ishavebillquery:"Y",isSwitchQuery:"N",autoquery:"Y",querycontion: condition1,autocondition:condition1});  //确定性校验  [icon-remove]清空所有结果/dropAllData;
	JSPFree.createBillList("d1_A_1_2","/biapp-crrs/freexml/crrs/ruleNew/crrs_result_consistency_Submit_new_CODE1.xml",null,{list_btns:list_btns2,list_ischeckstyle:"N",list_ismultisel:"N",ishavebillquery:"Y",isSwitchQuery:"N",autoquery:"Y",querycontion: condition1,autocondition:condition1});  //一致性校验
	JSPFree.createBillList("d1_A_1_3","/biapp-crrs/freexml/crrs/ruleNew/crrs_result_warn_Submit_new_CODE1.xml",null,{list_btns:list_btns3,list_ischeckstyle:"N",list_ismultisel:"N",ishavebillquery:"Y",isSwitchQuery:"N",autoquery:"Y",querycontion: condition1,autocondition:condition1});  //提示性校验

	JSPFree.createBillList("d1_A_2_1","/biapp-crrs/freexml/crrs/ruleNew/crrs_result_sure_Submit_new_CODE1.xml",null,{list_btns:list_btns4,list_ischeckstyle:"N",list_ismultisel:"N",ishavebillquery:"Y",isSwitchQuery:"N",autoquery:"Y",querycontion: condition2,autocondition:condition2});  //确定性校验  [icon-remove]清空所有结果/dropAllData;
	JSPFree.createBillList("d1_A_2_2","/biapp-crrs/freexml/crrs/ruleNew/crrs_result_consistency_Submit_new_CODE1.xml",null,{list_btns:list_btns5,list_ischeckstyle:"N",list_ismultisel:"N",ishavebillquery:"Y",isSwitchQuery:"N",autoquery:"Y",querycontion: condition2,autocondition:condition2});  //一致性校验
	JSPFree.createBillList("d1_A_2_3","/biapp-crrs/freexml/crrs/ruleNew/crrs_result_warn_Submit_new_CODE1.xml",null,{list_btns:list_btns6,list_ischeckstyle:"N",list_ismultisel:"N",ishavebillquery:"Y",isSwitchQuery:"N",autoquery:"Y",querycontion: condition2,autocondition:condition2});  //提示性校验
}

function getQueryCondition() {
	var jso_rt = JSPFree.doClassMethodCall("com.yusys.crrs.customer.service.ValidateQueryIssuedNoCondition","getQueryCondition",{"_reportOrgNo" : org_no});
	var condition = "";
	if (jso_rt.msg == "ok") {
		condition = jso_rt.condition;
	}
	
	return condition;
}

/**
 * 页面加载完成之后
 * @returns
 */
function AfterBodyLoad(){
	$("[name=data_dt]").each(function(){
	    $(this).prev().attr('disabled',"true");
	  });
	
	$("[name=tr_dt]").each(function(){
	    $(this).prev().attr('disabled',"true");
	  });
}

/**
 * 提交按钮
 * @returns
 */
function submission(){
	// 如果当前待编辑列表中有数据，则不能进行提交
	if (d1_A_1_1_BillList.datagrid('getData').total != 0 
			|| d1_A_1_2_BillList.datagrid('getData').total != 0
			|| d1_A_1_3_BillList.datagrid('getData').total != 0) {
		$.messager.alert('提示', '当前列表存在待处理数据，无法提交！', 'warning');
		
		return;
	}
	
	var jso_allrids=[]; 
	jso_allrids.push(taskId);

	// 修改数据状态：1：待审核
	var jsn_result = JSPFree.doClassMethodCall("com.yusys.crrs.process.service.CrrsCrProcessBSDMO", "updateDataByTaskByRids", {allrids:jso_allrids,status:'1',type:"1",userNo:str_LoginUserCode});
	if (jsn_result.msg == 'OK') {
		JSPFree.closeDialog("提交");
	}
}

/**
 * 强制提交操作
 * 点击强制提交按钮，填写原因，并强制提交
 * @returns
 */
function forcesubmission(){
	var jso_allrids=[]; 
	jso_allrids.push(taskId);

	JSPFree.confirm("提示","您确认要强制提交吗?",function(_isOK){
		if(_isOK){
			//弹出窗口,传入参数,然后接收返回值!
			JSPFree.openDialog("填写原因","/yujs/crrs/fillingProcess/crrs_distribute_detail_fillin_reason.js",600,300,
					{templetcode:"/biapp-crrs/freexml/crrs/fillingProcess/crrs_filling_process_remark.xml"},function(_rtdata){
				if(_rtdata != null && _rtdata != undefined && null != _rtdata.reason && undefined != _rtdata.reason){
					doUpdateBackStatus(jso_allrids, _rtdata.reason);
				}
			});
		}
	});
}

/**
 * 强制提交操作，修改状态
 * @param billList
 * @param _rids
 * @param _reason
 * @returns
 */
function doUpdateBackStatus(_rids,_reason){
	var jso_par = {allrids:_rids,type:'1',reason:_reason,userNo:str_LoginUserCode};
	var jsn_result = JSPFree.doClassMethodCall("com.yusys.crrs.process.service.CrrsCrProcessBSDMO","doResultBack",jso_par);
	
	if (jsn_result.msg == 'OK') {
		JSPFree.closeDialog("强制提交");
	}
}
/**
 * 审核通过操作
 * 点击通过按钮，审核通过该条填报任务
 */

function onConfirm(){
	JSPFree.confirm('提示', '你真的要通过该任务吗?', function(_isOK){
		if (_isOK){
			var jso_allrids = [];
			jso_allrids.push(data.rid);
			
			// 修改数据状态：3：完成
			var jso_Pars = {allrids:jso_allrids,status:'3',type:'3',reason:"通过",userNo:str_LoginUserCode};
			maskUtil.mask();
			setTimeout(function () {
				var jsn_result = JSPFree.doClassMethodCall("com.yusys.crrs.process.service.CrrsCrProcessBSDMO", "updateDataByTaskByRids", jso_Pars);
				maskUtil.unmask();
				if (jsn_result.msg == 'OK') {
					JSPFree.closeDialog("通过");
				}
			},100);
		}
	});
}

/**
 * 审核退回操作
 * 点击退回按钮，退回该条填报任务，退回后的任务将会继续填报流程
 * @returns
 */
function onBack(){
	JSPFree.confirm('提示', '你真的要退回该任务吗?', function(_isOK){
		if (_isOK){
			//弹出窗口,传入参数,然后接收返回值!
			JSPFree.openDialog("填写原因","/yujs/crrs/fillingProcess/crrs_distribute_detail_fillin_reason.js",600,300,{templetcode:"/biapp-crrs/freexml/crrs/fillingProcess/crrs_filling_process_remark.xml"},function(_rtdata){
				if(_rtdata != null && _rtdata != undefined && null != _rtdata.reason && undefined != _rtdata.reason){
					var jso_allrids = [];
					jso_allrids.push(data.rid);
					var jso_Pars = {allrids:jso_allrids,status:'2',type:'2',reason:_rtdata.reason,userNo:str_LoginUserCode};
					maskUtil.mask();
					setTimeout(function () {
						var jsn_result = JSPFree.doClassMethodCall("com.yusys.crrs.process.service.CrrsCrProcessBSDMO", "updateDataByTaskByRids", jso_Pars);
						maskUtil.unmask();
						if (jsn_result.msg == 'OK') {
							JSPFree.closeDialog("退回");
						}
					}, 100);
				}
			});
		}
	});
}

/**
 * 去掉横杠，转换10位数据日期为8位
 * @param dataDt
 * @returns
 */
function getDate(dataDt) {
	var _date = dataDt.replace(/-/g, '');
	return _date;
}

/**
 * 确定性校验-处理
 * @returns
 */
function onHandle_1(){
	var jso_rowData = d1_A_1_1_BillList.datagrid('getSelected');
	if (jso_rowData == null) {
		JSPFree.alert('必须选择一条记录进行操作');
		return;
	}
	
	// 判断当前日期是否锁定
	var report_date = jso_rowData.reportdate;
	var str_data = report_date.replace(/-/g, '');
	var data = JSPFree.getHashVOs("select status from crrs_lock_data where data_dt = '"+str_data+"'");
	if (data != null && data.length > 0) {
		if ("锁定" == data[0].status) {
			JSPFree.alert('当前数据已锁定，请先对'+str_data+'数据日期进行解锁操作');
			return;
		}
	}
	
	//表名
	var str_rid = jso_rowData["rid"];  //rid
	var str_tablename_en = jso_rowData["tablename_en"];  //表名
	var str_customercode = jso_rowData["customercode"];  //客户编码
	var str_reportdate = jso_rowData["reportdate"];  //日期
	var str_pkvalue = jso_rowData["pkvalue"];  //记录主键

	//先远程调用,根据表名,
	var jso_getRootTablePar = {"rid":str_rid,"tablename_en":str_tablename_en,"customercode":str_customercode,"reportdate":str_reportdate,"pkvalue":str_pkvalue,result_tab:"crrs_result_sure",str_LoginUserCode : str_LoginUserCode};  //计算所属根主表的参数
	var jso_rootInfo = JSPFree.doClassMethodCall("com.yusys.crrs.datahandle.service.GetRootTableByErrorRecordDMONew","getRootTableName",jso_getRootTablePar);
	
	console.log(jso_rootInfo);

	var str_rootTableName = jso_rootInfo["roottable"];
	var str_rootcustomercode = jso_rootInfo["rootcustomercode"];
	var str_rootreportdate = jso_rootInfo["rootreportdate"];
	str_rootreportdate = str_rootreportdate.replace(/-/g, '');

	// 如果没有计算出主表与记录,则弹出提示并返回
	if(str_rootTableName=="" || str_rootcustomercode=="" || str_rootreportdate==""){
		JSPFree.alert("根据当前记录表名[" + str_tablename_en + "]与主键值计算主表与客户编码，数据日期时有一值为空[" + str_rootTableName + "][" + str_rootcustomercode + "][" + str_rootreportdate + "]"); //
		return;
	}

	//弹出不同的修改数据窗口
	var jso_par = {tab:"crrs_result_sure","customercode":str_rootcustomercode,"reportdate":str_rootreportdate,"ErrorCount":jso_rootInfo.ErrorCount,type:"1"};  //
	if(str_rootTableName.toLowerCase() =="crrs_single_corporation".toLowerCase()){
		JSPFree.openDialog2("修改数据-单一法人客户","/yujs/crrs/datahandleNew/HandleErrorDataForSingleNew.js",1100,600,jso_par,function(_rtdata){JSPFree.refreshBillListCurrPage(d1_A_1_1_BillList);},true);
	}else if(str_rootTableName.toLowerCase()=="crrs_group_group_client_1".toLowerCase()){
		JSPFree.openDialog2("修改数据-集团客户","/yujs/crrs/datahandleNew/HandleErrorDataForGroupNew.js",950,600,jso_par,function(_rtdata){JSPFree.refreshBillListCurrPage(d1_A_1_1_BillList);},true);
	}else if(str_rootTableName.toLowerCase()=="crrs_group_group_client_2".toLowerCase()){
		JSPFree.openDialog2("修改数据-供应链客户","/yujs/crrs/datahandleNew/HandleErrorDataForSupplychainNew.js",950,600,jso_par,function(_rtdata){JSPFree.refreshBillListCurrPage(d1_A_1_1_BillList);},true);
	}else if(str_rootTableName.toLowerCase()=="crrs_ent_trade_info".toLowerCase()){
		JSPFree.openDialog2("修改数据-同业客户","/yujs/crrs/datahandleNew/HandleErrorDataForSametradeNew.js",950,600,jso_par,function(_rtdata){JSPFree.refreshBillListCurrPage(d1_A_1_1_BillList);},true);
	}else if(str_rootTableName.toLowerCase()=="crrs_person_personal".toLowerCase()){
		JSPFree.openDialog2("修改数据--个人客户","/yujs/crrs/datahandleNew/HandleErrorDataForPersonNew.js",950,600,jso_par,function(_rtdata){JSPFree.refreshBillListCurrPage(d1_A_1_1_BillList);},true);
	}else{
		JSPFree.alert("根据当前结果的表名[" + str_tablename_en + "]计算出所属数据表名[" + str_rootTableName + "]不合法!");
	}
}

/**
 * 一致性校验-处理
 * @returns
 */
function onHandle_2(){
	var jso_rowData = d1_A_1_2_BillList.datagrid('getSelected');
	if (jso_rowData == null) {
		JSPFree.alert('必须选择一条记录进行操作');
		return;
	}
	
	// 判断当前日期是否锁定
	var report_date = jso_rowData.reportdate;
	var str_data = report_date.replace(/-/g, '');
	var data = JSPFree.getHashVOs("select status from crrs_lock_data where data_dt = '"+str_data+"'");
	if (data != null && data.length > 0) {
		if ("锁定" == data[0].status) {
			JSPFree.alert('当前数据已锁定，请先对'+str_data+'数据日期进行解锁操作');
			return;
		}
	}

	// 表名
	var str_rid = jso_rowData["rid"];  //rid
	var str_tablename_en = jso_rowData["tablename_en"];  //表名
	var str_customercode = jso_rowData["customercode"];  //客户编码
	var str_reportdate = jso_rowData["reportdate"];  //日期
	var str_pkvalue = jso_rowData["pkvalue"];  //记录主键

	//先远程调用,根据表名,
	var jso_getRootTablePar = {"rid":str_rid,"tablename_en":str_tablename_en,"customercode":str_customercode,"reportdate":str_reportdate,"pkvalue":str_pkvalue,result_tab:"crrs_result_consistency",str_LoginUserCode : str_LoginUserCode};  //计算所属根主表的参数
	var jso_rootInfo = JSPFree.doClassMethodCall("com.yusys.crrs.datahandle.service.GetRootTableByErrorRecordDMONew","getRootTableName",jso_getRootTablePar);
	
	console.log(jso_rootInfo);

	var str_rootTableName = jso_rootInfo["roottable"];
	var str_rootcustomercode = jso_rootInfo["rootcustomercode"];
	var str_rootreportdate = jso_rootInfo["rootreportdate"];
	str_rootreportdate = str_rootreportdate.replace(/-/g, '');

	// 如果没有计算出主表与记录,则弹出提示并返回
	if(str_rootTableName=="" || str_rootcustomercode=="" || str_rootreportdate==""){
		JSPFree.alert("根据当前记录表名[" + str_tablename_en + "]与主键值计算主表与客户编码，数据日期时有一值为空[" + str_rootTableName + "][" + str_rootcustomercode + "][" + str_rootreportdate + "]"); //
		return;
	}

	// 弹出不同的修改数据窗口
	var jso_par = {tab:"crrs_result_sure","customercode":str_rootcustomercode,"ErrorCount":jso_rootInfo.ErrorCount,type:"2"};  //
	if(str_rootTableName.toLowerCase() =="crrs_single_corporation".toLowerCase()){
		JSPFree.openDialog2("修改数据-单一法人客户","/yujs/crrs/datahandleNew/HandleErrorDataForSingleNew.js",1100,600,jso_par,function(_rtdata){JSPFree.refreshBillListCurrPage(d1_A_1_2_BillList);},true);
	}else if(str_rootTableName.toLowerCase()=="crrs_group_group_client_1".toLowerCase()){
		JSPFree.openDialog2("修改数据-集团客户","/yujs/crrs/datahandleNew/HandleErrorDataForGroupNew.js",950,600,jso_par,function(_rtdata){JSPFree.refreshBillListCurrPage(d1_A_1_2_BillList);},true);
	}else if(str_rootTableName.toLowerCase()=="crrs_group_group_client_2".toLowerCase()){
		JSPFree.openDialog2("修改数据-供应链客户","/yujs/crrs/datahandleNew/HandleErrorDataForSupplychainNew.js",950,600,jso_par,function(_rtdata){JSPFree.refreshBillListCurrPage(d1_A_1_2_BillList);},true);
	}else if(str_rootTableName.toLowerCase()=="crrs_ent_trade_info".toLowerCase()){
		JSPFree.openDialog2("修改数据-同业客户","/yujs/crrs/datahandleNew/HandleErrorDataForSametradeNew.js",950,600,jso_par,function(_rtdata){JSPFree.refreshBillListCurrPage(d1_A_1_2_BillList);},true);
	}else if(str_rootTableName.toLowerCase()=="crrs_person_personal".toLowerCase()){
		JSPFree.openDialog2("修改数据--个人客户","/yujs/crrs/datahandleNew/HandleErrorDataForPersonNew.js",950,600,jso_par,function(_rtdata){JSPFree.refreshBillListCurrPage(d1_A_1_2_BillList);},true);
	}else{
		JSPFree.alert("根据当前结果的表名[" + str_tablename_en + "]计算出所属数据表名[" + str_rootTableName + "]不合法!");
	}
}

/**
 * 提示性校验-处理
 * @returns
 */
function onHandle_3(){
	var jso_rowData = d1_A_1_3_BillList.datagrid('getSelected');
	if (jso_rowData == null) {
		JSPFree.alert('必须选择一条记录进行操作');
		return;
	}
	
	// 判断当前日期是否锁定
	var report_date = jso_rowData.reportdate;
	var str_data = report_date.replace(/-/g, '');
	var data = JSPFree.getHashVOs("select status from crrs_lock_data where data_dt = '"+str_data+"'");
	if (data != null && data.length > 0) {
		if ("锁定" == data[0].status) {
			JSPFree.alert('当前数据已锁定，请先对'+str_data+'数据日期进行解锁操作');
			return;
		}
	}
	
	//表名
	var str_rid = jso_rowData["rid"];  //rid
	var str_tablename_en = jso_rowData["tablename_en"];  //表名
	var str_customercode = jso_rowData["customercode"];  //客户编码
	var str_reportdate = jso_rowData["reportdate"];  //日期
	var str_pkvalue = jso_rowData["pkvalue"];  //记录主键

	//先远程调用,根据表名,
	var jso_getRootTablePar = {"rid":str_rid,"tablename_en":str_tablename_en,"customercode":str_customercode,"reportdate":str_reportdate,"pkvalue":str_pkvalue,result_tab:"crrs_result_warn",str_LoginUserCode : str_LoginUserCode};  //计算所属根主表的参数
	var jso_rootInfo = JSPFree.doClassMethodCall("com.yusys.crrs.datahandle.service.GetRootTableByErrorRecordDMONew","getRootTableName",jso_getRootTablePar);
	
	console.log(jso_rootInfo);

	var str_rootTableName = jso_rootInfo["roottable"];
	var str_rootcustomercode = jso_rootInfo["rootcustomercode"];
	var str_rootreportdate = jso_rootInfo["rootreportdate"];
	str_rootreportdate = str_rootreportdate.replace(/-/g, '');

	//如果没有计算出主表与记录,则弹出提示并返回
	if(str_rootTableName=="" || str_rootcustomercode=="" || str_rootreportdate==""){
		JSPFree.alert("根据当前记录表名[" + str_tablename_en + "]与主键值计算主表与客户编码，数据日期时有一值为空[" + str_rootTableName + "][" + str_rootcustomercode + "][" + str_rootreportdate + "]"); //
		return;
	}

	//弹出不同的修改数据窗口
	var jso_par = {tab:"crrs_result_sure","customercode":str_rootcustomercode,"ErrorCount":jso_rootInfo.ErrorCount,type:"3"};  //
	if(str_rootTableName.toLowerCase() =="crrs_single_corporation".toLowerCase()){
		JSPFree.openDialog2("修改数据-单一法人客户","/yujs/crrs/datahandleNew/HandleErrorDataForSingleNew.js",1100,600,jso_par,function(_rtdata){JSPFree.refreshBillListCurrPage(d1_A_1_3_BillList);},true);
	}else if(str_rootTableName.toLowerCase()=="crrs_group_group_client_1".toLowerCase()){
		JSPFree.openDialog2("修改数据-集团客户","/yujs/crrs/datahandleNew/HandleErrorDataForGroupNew.js",950,600,jso_par,function(_rtdata){JSPFree.refreshBillListCurrPage(d1_A_1_3_BillList);},true);
	}else if(str_rootTableName.toLowerCase()=="crrs_group_group_client_2".toLowerCase()){
		JSPFree.openDialog2("修改数据-供应链客户","/yujs/crrs/datahandleNew/HandleErrorDataForSupplychainNew.js",950,600,jso_par,function(_rtdata){JSPFree.refreshBillListCurrPage(d1_A_1_3_BillList);},true);
	}else if(str_rootTableName.toLowerCase()=="crrs_ent_trade_info".toLowerCase()){
		JSPFree.openDialog2("修改数据-同业客户","/yujs/crrs/datahandleNew/HandleErrorDataForSametradeNew.js",950,600,jso_par,function(_rtdata){JSPFree.refreshBillListCurrPage(d1_A_1_3_BillList);},true);
	}else if(str_rootTableName.toLowerCase()=="crrs_person_personal".toLowerCase()){
		JSPFree.openDialog2("修改数据--个人客户","/yujs/crrs/datahandleNew/HandleErrorDataForPersonNew.js",950,600,jso_par,function(_rtdata){JSPFree.refreshBillListCurrPage(d1_A_1_3_BillList);},true);
	}else{
		JSPFree.alert("根据当前结果的表名[" + str_tablename_en + "]计算出所属数据表名[" + str_rootTableName + "]不合法!");
	}
}

/**
 * 单一处理
 * @returns
 */
function onHandle_1_1(){
	var selectData = d1_A_1_1_BillList.datagrid('getSelected');
	if (selectData == null) {
		$.messager.alert('提示', '必须选择一条记录进行操作', 'warning');
		return;
	}
	
	// 判断当前日期是否锁定
	var report_date = selectData.reportdate;
	var str_data = report_date.replace(/-/g, '');
	var data = JSPFree.getHashVOs("select status from crrs_lock_data where data_dt = '"+str_data+"'");
	if (data != null && data.length > 0) {
		if ("锁定" == data[0].status) {
			JSPFree.alert('当前数据已锁定，请先对'+str_data+'数据日期进行解锁操作');
			return;
		}
	}

	if (st_date.indexOf(selectData.result_status) >= 0) {
		JSPFree.openDialog(selectData.tablename,"/yujs/crrs/datahandleNew/ValidateResultHandleDataNew.js", 900, 560, {tab:"crrs_result_sure",tabname:selectData.tabname,tablename_en:selectData.tablename_en,pkvalue:selectData.pkvalue,type:"1"},function(_rtdata){
			if (_rtdata.type == "dirclose" || _rtdata == false) {
				return;
			}
			
			if (_rtdata == "OK") {
				JSPFree.alert("保存成功!<br>同时修改了对应的校验结果状态为【完成】!");
				JSPFree.queryDataByConditon2(d1_A_1_1_BillList, null);
			}
			if (_rtdata.code != null && _rtdata.code != 'undefined' && _rtdata.code == '000') {
				JSPFree.alert("退回数据成功!");
				JSPFree.queryDataByConditon2(d1_A_1_1_BillList, null);
			}
		});
	} else {
		$.messager.alert('提示','该数据已处理，请选择其他数据进行处理!','warning');
	}
}

/**
 * 单一处理
 * @returns
 */
function onHandle_2_1(){
	var selectData = d1_A_1_2_BillList.datagrid('getSelected');
	if (selectData == null) {
		$.messager.alert('提示', '必须选择一条记录进行操作', 'warning');
		return;
	}
	
	// 判断当前日期是否锁定
	var report_date = selectData.reportdate;
	var str_data = report_date.replace(/-/g, '');
	var data = JSPFree.getHashVOs("select status from crrs_lock_data where data_dt = '"+str_data+"'");
	if (data != null && data.length > 0) {
		if ("锁定" == data[0].status) {
			JSPFree.alert('当前数据已锁定，请先对'+str_data+'数据日期进行解锁操作');
			return;
		}
	}
	
	if (st_date == selectData.result_status) {
		JSPFree.openDialog(selectData.tablename,"/yujs/crrs/datahandleNew/ValidateResultHandleDataNew.js", 900, 560, {tab:"crrs_result_consistency",tabname:selectData.tabname,tablename_en:selectData.tablename_en,pkvalue:selectData.pkvalue,type:"2"},function(_rtdata){
			if (_rtdata.type == "dirclose" || _rtdata == false) {
				return;
			}
			
			if (_rtdata == "OK") {
				JSPFree.alert("保存成功!<br>同时修改了对应的校验结果状态为【完成】!");
				JSPFree.queryDataByConditon2(d1_A_1_2_BillList, null);
			}
			if (_rtdata.code != null && _rtdata.code != 'undefined' && _rtdata.code == '000') {
				JSPFree.alert("退回数据成功!");
				JSPFree.queryDataByConditon2(d1_A_1_2_BillList, null);
			}
		});
	} else {
		$.messager.alert('提示','该数据已处理，请选择其他数据进行处理!','warning');
	}
}

/**
 * 单一处理
 * @returns
 */
function onHandle_3_1(){
	var selectData = d1_A_1_3_BillList.datagrid('getSelected');
	if (selectData == null) {
		$.messager.alert('提示', '必须选择一条记录进行操作', 'warning');
		return;
	}
	
	// 判断当前日期是否锁定
	var report_date = selectData.reportdate;
	var str_data = report_date.replace(/-/g, '');
	var data = JSPFree.getHashVOs("select status from crrs_lock_data where data_dt = '"+str_data+"'");
	if (data != null && data.length > 0) {
		if ("锁定" == data[0].status) {
			JSPFree.alert('当前数据已锁定，请先对'+str_data+'数据日期进行解锁操作');
			return;
		}
	}

	if (st_date == selectData.result_status) {
		JSPFree.openDialog(selectData.tablename,"/yujs/crrs/datahandleNew/ValidateResultHandleDataNew.js", 900, 560, {tab:"crrs_result_warn",tabname:selectData.tabname,tablename_en:selectData.tablename_en,pkvalue:selectData.pkvalue,type:"3"},function(_rtdata){
			if (_rtdata.type == "dirclose" || _rtdata == false) {
				return;
			}
			
			if (_rtdata == "OK") {
				JSPFree.alert("保存成功!<br>同时修改了对应的校验结果状态为【完成】!");
				JSPFree.queryDataByConditon2(d1_A_1_3_BillList, null);
			}
			if (_rtdata.code != null && _rtdata.code != 'undefined' && _rtdata.code == '000') {
				JSPFree.alert("退回数据成功!");
				JSPFree.queryDataByConditon2(d1_A_1_3_BillList, null);
			}
		});
	} else {
		$.messager.alert('提示','该数据已处理，请选择其他数据进行处理!','warning');
	}
}

///////////////////////////////////////////////////////////////////////
/**
 * 确定性校验-处理
 * @returns
 */
function onHandle_4(){
	var jso_rowData = d1_A_2_1_BillList.datagrid('getSelected');
	if (jso_rowData == null) {
		JSPFree.alert('必须选择一条记录进行操作');
		return;
	}
	
	// 判断当前日期是否锁定
	var report_date = jso_rowData.reportdate;
	var str_data = report_date.replace(/-/g, '');
	var data = JSPFree.getHashVOs("select status from crrs_lock_data where data_dt = '"+str_data+"'");
	if (data != null && data.length > 0) {
		if ("锁定" == data[0].status) {
			JSPFree.alert('当前数据已锁定，请先对'+str_data+'数据日期进行解锁操作');
			return;
		}
	}
	
	//表名
	var str_rid = jso_rowData["rid"];  //rid
	var str_tablename_en = jso_rowData["tablename_en"];  //表名
	var str_customercode = jso_rowData["customercode"];  //客户编码
	var str_reportdate = jso_rowData["reportdate"];  //日期
	var str_pkvalue = jso_rowData["pkvalue"];  //记录主键

	//先远程调用,根据表名,
	var jso_getRootTablePar = {"rid":str_rid,"tablename_en":str_tablename_en,"customercode":str_customercode,"reportdate":str_reportdate,"pkvalue":str_pkvalue,result_tab:"crrs_result_sure",str_LoginUserCode : str_LoginUserCode,str_source:"1"};  //计算所属根主表的参数
	var jso_rootInfo = JSPFree.doClassMethodCall("com.yusys.crrs.datahandle.service.GetRootTableByErrorRecordDMONew","getRootTableName",jso_getRootTablePar);
	
	console.log(jso_rootInfo);

	var str_rootTableName = jso_rootInfo["roottable"];
	var str_rootcustomercode = jso_rootInfo["rootcustomercode"];
	var str_rootreportdate = jso_rootInfo["rootreportdate"];
	str_rootreportdate = str_rootreportdate.replace(/-/g, '');

	// 如果没有计算出主表与记录,则弹出提示并返回
	if(str_rootTableName=="" || str_rootcustomercode=="" || str_rootreportdate==""){
		JSPFree.alert("根据当前记录表名[" + str_tablename_en + "]与主键值计算主表与客户编码，数据日期时有一值为空[" + str_rootTableName + "][" + str_rootcustomercode + "][" + str_rootreportdate + "]"); //
		return;
	}

	//弹出不同的修改数据窗口
	var jso_par = {tab:"crrs_result_sure","customercode":str_rootcustomercode,"reportdate":str_rootreportdate,"ErrorCount":jso_rootInfo.ErrorCount,type:"1",source:"1"};  //
	if(str_rootTableName.toLowerCase() =="crrs_single_corporation".toLowerCase()){
		JSPFree.openDialog2("修改数据-单一法人客户","/yujs/crrs/datahandleNew/HandleErrorDataForSingleNew.js",1100,600,jso_par,function(_rtdata){JSPFree.refreshBillListCurrPage(d1_A_2_1_BillList);},true);
	}else if(str_rootTableName.toLowerCase()=="crrs_group_group_client_1".toLowerCase()){
		JSPFree.openDialog2("修改数据-集团客户","/yujs/crrs/datahandleNew/HandleErrorDataForGroupNew.js",950,600,jso_par,function(_rtdata){JSPFree.refreshBillListCurrPage(d1_A_2_1_BillList);},true);
	}else if(str_rootTableName.toLowerCase()=="crrs_group_group_client_2".toLowerCase()){
		JSPFree.openDialog2("修改数据-供应链客户","/yujs/crrs/datahandleNew/HandleErrorDataForSupplychainNew.js",950,600,jso_par,function(_rtdata){JSPFree.refreshBillListCurrPage(d1_A_2_1_BillList);},true);
	}else if(str_rootTableName.toLowerCase()=="crrs_ent_trade_info".toLowerCase()){
		JSPFree.openDialog2("修改数据-同业客户","/yujs/crrs/datahandleNew/HandleErrorDataForSametradeNew.js",950,600,jso_par,function(_rtdata){JSPFree.refreshBillListCurrPage(d1_A_2_1_BillList);},true);
	}else if(str_rootTableName.toLowerCase()=="crrs_person_personal".toLowerCase()){
		JSPFree.openDialog2("修改数据--个人客户","/yujs/crrs/datahandleNew/HandleErrorDataForPersonNew.js",950,600,jso_par,function(_rtdata){JSPFree.refreshBillListCurrPage(d1_A_2_1_BillList);},true);
	}else{
		JSPFree.alert("根据当前结果的表名[" + str_tablename_en + "]计算出所属数据表名[" + str_rootTableName + "]不合法!");
	}
}

/**
 * 一致性校验-处理
 * @returns
 */
function onHandle_5(){
	var jso_rowData = d1_A_2_2_BillList.datagrid('getSelected');
	if (jso_rowData == null) {
		JSPFree.alert('必须选择一条记录进行操作');
		return;
	}
	
	// 判断当前日期是否锁定
	var report_date = jso_rowData.reportdate;
	var str_data = report_date.replace(/-/g, '');
	var data = JSPFree.getHashVOs("select status from crrs_lock_data where data_dt = '"+str_data+"'");
	if (data != null && data.length > 0) {
		if ("锁定" == data[0].status) {
			JSPFree.alert('当前数据已锁定，请先对'+str_data+'数据日期进行解锁操作');
			return;
		}
	}

	// 表名
	var str_rid = jso_rowData["rid"];  //rid
	var str_tablename_en = jso_rowData["tablename_en"];  //表名
	var str_customercode = jso_rowData["customercode"];  //客户编码
	var str_reportdate = jso_rowData["reportdate"];  //日期
	var str_pkvalue = jso_rowData["pkvalue"];  //记录主键

	//先远程调用,根据表名,
	var jso_getRootTablePar = {"rid":str_rid,"tablename_en":str_tablename_en,"customercode":str_customercode,"reportdate":str_reportdate,"pkvalue":str_pkvalue,result_tab:"crrs_result_consistency",str_LoginUserCode : str_LoginUserCode,str_source:"1"};  //计算所属根主表的参数
	var jso_rootInfo = JSPFree.doClassMethodCall("com.yusys.crrs.datahandle.service.GetRootTableByErrorRecordDMONew","getRootTableName",jso_getRootTablePar);
	
	console.log(jso_rootInfo);

	var str_rootTableName = jso_rootInfo["roottable"];
	var str_rootcustomercode = jso_rootInfo["rootcustomercode"];
	var str_rootreportdate = jso_rootInfo["rootreportdate"];
	str_rootreportdate = str_rootreportdate.replace(/-/g, '');

	// 如果没有计算出主表与记录,则弹出提示并返回
	if(str_rootTableName=="" || str_rootcustomercode=="" || str_rootreportdate==""){
		JSPFree.alert("根据当前记录表名[" + str_tablename_en + "]与主键值计算主表与客户编码，数据日期时有一值为空[" + str_rootTableName + "][" + str_rootcustomercode + "][" + str_rootreportdate + "]"); //
		return;
	}

	// 弹出不同的修改数据窗口
	var jso_par = {tab:"crrs_result_sure","customercode":str_rootcustomercode,"ErrorCount":jso_rootInfo.ErrorCount,type:"2",source:"1"};  //
	if(str_rootTableName.toLowerCase() =="crrs_single_corporation".toLowerCase()){
		JSPFree.openDialog2("修改数据-单一法人客户","/yujs/crrs/datahandleNew/HandleErrorDataForSingleNew.js",1100,600,jso_par,function(_rtdata){JSPFree.refreshBillListCurrPage(d1_A_2_2_BillList);},true);
	}else if(str_rootTableName.toLowerCase()=="crrs_group_group_client_1".toLowerCase()){
		JSPFree.openDialog2("修改数据-集团客户","/yujs/crrs/datahandleNew/HandleErrorDataForGroupNew.js",950,600,jso_par,function(_rtdata){JSPFree.refreshBillListCurrPage(d1_A_2_2_BillList);},true);
	}else if(str_rootTableName.toLowerCase()=="crrs_group_group_client_2".toLowerCase()){
		JSPFree.openDialog2("修改数据-供应链客户","/yujs/crrs/datahandleNew/HandleErrorDataForSupplychainNew.js",950,600,jso_par,function(_rtdata){JSPFree.refreshBillListCurrPage(d1_A_2_2_BillList);},true);
	}else if(str_rootTableName.toLowerCase()=="crrs_ent_trade_info".toLowerCase()){
		JSPFree.openDialog2("修改数据-同业客户","/yujs/crrs/datahandleNew/HandleErrorDataForSametradeNew.js",950,600,jso_par,function(_rtdata){JSPFree.refreshBillListCurrPage(d1_A_2_2_BillList);},true);
	}else if(str_rootTableName.toLowerCase()=="crrs_person_personal".toLowerCase()){
		JSPFree.openDialog2("修改数据--个人客户","/yujs/crrs/datahandleNew/HandleErrorDataForPersonNew.js",950,600,jso_par,function(_rtdata){JSPFree.refreshBillListCurrPage(d1_A_2_2_BillList);},true);
	}else{
		JSPFree.alert("根据当前结果的表名[" + str_tablename_en + "]计算出所属数据表名[" + str_rootTableName + "]不合法!");
	}
}

/**
 * 提示性校验-处理
 * @returns
 */
function onHandle_6(){
	var jso_rowData = d1_A_2_3_BillList.datagrid('getSelected');
	if (jso_rowData == null) {
		JSPFree.alert('必须选择一条记录进行操作');
		return;
	}
	
	// 判断当前日期是否锁定
	var report_date = jso_rowData.reportdate;
	var str_data = report_date.replace(/-/g, '');
	var data = JSPFree.getHashVOs("select status from crrs_lock_data where data_dt = '"+str_data+"'");
	if (data != null && data.length > 0) {
		if ("锁定" == data[0].status) {
			JSPFree.alert('当前数据已锁定，请先对'+str_data+'数据日期进行解锁操作');
			return;
		}
	}
	
	//表名
	var str_rid = jso_rowData["rid"];  //rid
	var str_tablename_en = jso_rowData["tablename_en"];  //表名
	var str_customercode = jso_rowData["customercode"];  //客户编码
	var str_reportdate = jso_rowData["reportdate"];  //日期
	var str_pkvalue = jso_rowData["pkvalue"];  //记录主键

	//先远程调用,根据表名,
	var jso_getRootTablePar = {"rid":str_rid,"tablename_en":str_tablename_en,"customercode":str_customercode,"reportdate":str_reportdate,"pkvalue":str_pkvalue,result_tab:"crrs_result_warn",str_LoginUserCode : str_LoginUserCode,str_source:"1"};  //计算所属根主表的参数
	var jso_rootInfo = JSPFree.doClassMethodCall("com.yusys.crrs.datahandle.service.GetRootTableByErrorRecordDMONew","getRootTableName",jso_getRootTablePar);
	
	console.log(jso_rootInfo);

	var str_rootTableName = jso_rootInfo["roottable"];
	var str_rootcustomercode = jso_rootInfo["rootcustomercode"];
	var str_rootreportdate = jso_rootInfo["rootreportdate"];
	str_rootreportdate = str_rootreportdate.replace(/-/g, '');

	//如果没有计算出主表与记录,则弹出提示并返回
	if(str_rootTableName=="" || str_rootcustomercode=="" || str_rootreportdate==""){
		JSPFree.alert("根据当前记录表名[" + str_tablename_en + "]与主键值计算主表与客户编码，数据日期时有一值为空[" + str_rootTableName + "][" + str_rootcustomercode + "][" + str_rootreportdate + "]"); //
		return;
	}

	//弹出不同的修改数据窗口
	var jso_par = {tab:"crrs_result_sure","customercode":str_rootcustomercode,"ErrorCount":jso_rootInfo.ErrorCount,type:"3",source:"1"};  //
	if(str_rootTableName.toLowerCase() =="crrs_single_corporation".toLowerCase()){
		JSPFree.openDialog2("修改数据-单一法人客户","/yujs/crrs/datahandleNew/HandleErrorDataForSingleNew.js",1100,600,jso_par,function(_rtdata){JSPFree.refreshBillListCurrPage(d1_A_2_3_BillList);},true);
	}else if(str_rootTableName.toLowerCase()=="crrs_group_group_client_1".toLowerCase()){
		JSPFree.openDialog2("修改数据-集团客户","/yujs/crrs/datahandleNew/HandleErrorDataForGroupNew.js",950,600,jso_par,function(_rtdata){JSPFree.refreshBillListCurrPage(d1_A_2_3_BillList);},true);
	}else if(str_rootTableName.toLowerCase()=="crrs_group_group_client_2".toLowerCase()){
		JSPFree.openDialog2("修改数据-供应链客户","/yujs/crrs/datahandleNew/HandleErrorDataForSupplychainNew.js",950,600,jso_par,function(_rtdata){JSPFree.refreshBillListCurrPage(d1_A_2_3_BillList);},true);
	}else if(str_rootTableName.toLowerCase()=="crrs_ent_trade_info".toLowerCase()){
		JSPFree.openDialog2("修改数据-同业客户","/yujs/crrs/datahandleNew/HandleErrorDataForSametradeNew.js",950,600,jso_par,function(_rtdata){JSPFree.refreshBillListCurrPage(d1_A_2_3_BillList);},true);
	}else if(str_rootTableName.toLowerCase()=="crrs_person_personal".toLowerCase()){
		JSPFree.openDialog2("修改数据--个人客户","/yujs/crrs/datahandleNew/HandleErrorDataForPersonNew.js",950,600,jso_par,function(_rtdata){JSPFree.refreshBillListCurrPage(d1_A_2_3_BillList);},true);
	}else{
		JSPFree.alert("根据当前结果的表名[" + str_tablename_en + "]计算出所属数据表名[" + str_rootTableName + "]不合法!");
	}
}

/**
 * 单一处理
 * @returns
 */
function onHandle_4_1(){
	var selectData = d1_A_2_1_BillList.datagrid('getSelected');
	if (selectData == null) {
		$.messager.alert('提示', '必须选择一条记录进行操作', 'warning');
		return;
	}
	
	// 判断当前日期是否锁定
	var report_date = selectData.reportdate;
	var str_data = report_date.replace(/-/g, '');
	var data = JSPFree.getHashVOs("select status from crrs_lock_data where data_dt = '"+str_data+"'");
	if (data != null && data.length > 0) {
		if ("锁定" == data[0].status) {
			JSPFree.alert('当前数据已锁定，请先对'+str_data+'数据日期进行解锁操作');
			return;
		}
	}
debugger;
	JSPFree.openDialog(selectData.tablename,"/yujs/crrs/datahandleNew/ValidateResultHandleDataNew.js", 900, 560, {tab:"crrs_result_sure",tabname:selectData.tabname,tablename_en:selectData.tablename_en,pkvalue:selectData.pkvalue,type:"1",source:"1"},function(_rtdata){
		if (_rtdata.type == "dirclose" || _rtdata == false) {
			return;
		}
		
		if (_rtdata == "OK") {
			JSPFree.alert("保存成功!<br>同时修改了对应的校验结果状态为【完成】!");
			JSPFree.queryDataByConditon2(d1_A_2_1_BillList, null);
		}
		if (_rtdata.code != null && _rtdata.code != 'undefined' && _rtdata.code == '000') {
			JSPFree.alert("退回数据成功!");
			JSPFree.queryDataByConditon2(d1_A_2_1_BillList, null);
		}
	});
}

/**
 * 单一处理
 * @returns
 */
function onHandle_5_1(){
	var selectData = d1_A_2_2_BillList.datagrid('getSelected');
	if (selectData == null) {
		$.messager.alert('提示', '必须选择一条记录进行操作', 'warning');
		return;
	}
	
	// 判断当前日期是否锁定
	var report_date = selectData.reportdate;
	var str_data = report_date.replace(/-/g, '');
	var data = JSPFree.getHashVOs("select status from crrs_lock_data where data_dt = '"+str_data+"'");
	if (data != null && data.length > 0) {
		if ("锁定" == data[0].status) {
			JSPFree.alert('当前数据已锁定，请先对'+str_data+'数据日期进行解锁操作');
			return;
		}
	}
	
	JSPFree.openDialog(selectData.tablename,"/yujs/crrs/datahandleNew/ValidateResultHandleDataNew.js", 900, 560, {tab:"crrs_result_consistency",tabname:selectData.tabname,tablename_en:selectData.tablename_en,pkvalue:selectData.pkvalue,type:"2",source:"1"},function(_rtdata){
		if (_rtdata.type == "dirclose" || _rtdata == false) {
			return;
		}
		
		if (_rtdata == "OK") {
			JSPFree.alert("保存成功!<br>同时修改了对应的校验结果状态为【完成】!");
			JSPFree.queryDataByConditon2(d1_A_2_2_BillList, null);
		}
		if (_rtdata.code != null && _rtdata.code != 'undefined' && _rtdata.code == '000') {
			JSPFree.alert("退回数据成功!");
			JSPFree.queryDataByConditon2(d1_A_2_2_BillList, null);
		}
	});
}

/**
 * 单一处理
 * @returns
 */
function onHandle_6_1(){
	var selectData = d1_A_2_3_BillList.datagrid('getSelected');
	if (selectData == null) {
		$.messager.alert('提示', '必须选择一条记录进行操作', 'warning');
		return;
	}
	
	// 判断当前日期是否锁定
	var report_date = selectData.reportdate;
	var str_data = report_date.replace(/-/g, '');
	var data = JSPFree.getHashVOs("select status from crrs_lock_data where data_dt = '"+str_data+"'");
	if (data != null && data.length > 0) {
		if ("锁定" == data[0].status) {
			JSPFree.alert('当前数据已锁定，请先对'+str_data+'数据日期进行解锁操作');
			return;
		}
	}

	JSPFree.openDialog(selectData.tablename,"/yujs/crrs/datahandleNew/ValidateResultHandleDataNew.js", 900, 560, {tab:"crrs_result_warn",tabname:selectData.tabname,tablename_en:selectData.tablename_en,pkvalue:selectData.pkvalue,type:"3",source:"1"},function(_rtdata){
		if (_rtdata.type == "dirclose" || _rtdata == false) {
			return;
		}
		
		if (_rtdata == "OK") {
			JSPFree.alert("保存成功!<br>同时修改了对应的校验结果状态为【完成】!");
			JSPFree.queryDataByConditon2(d1_A_2_3_BillList, null);
		}
		if (_rtdata.code != null && _rtdata.code != 'undefined' && _rtdata.code == '000') {
			JSPFree.alert("退回数据成功!");
			JSPFree.queryDataByConditon2(d1_A_2_3_BillList, null);
		}
	});
}

function dropAllData(){
	JSPFree.doClassMethodCall("com.yusys.crrs.datahandle.service.CreateDemoErrorDataDMO","dropAllData",null);
	JSPFree.alert("清除所有确定性校验结果成功!");
}