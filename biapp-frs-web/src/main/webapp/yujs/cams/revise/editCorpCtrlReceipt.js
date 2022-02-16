//初始化界面,菜单配置路径是【/frs/yufreejs?js=/yujs/crrs/collection/addSingle.js】
var _TheArray = [
	["cams_indv_name_m", "/biapp-cams/freexml/cams/revise/cams_indv_name_m_CODE1.xml", "姓名信息"],
	["cams_indv_addr_m", "/biapp-cams/freexml/cams/revise/cams_indv_addr_m_CODE1.xml", "地址信息"],
	["cams_indv_tin_m", "/biapp-cams/freexml/cams/revise/cams_indv_tin_m_CODE1.xml", "TIN信息"],
	["cams_corp_ctrl_m", "/biapp-cams/freexml/cams/revise/cams_corp_ctrl_m_CODE1.xml", "控制人信息"],
];
var str_sqlCons = null;
var tablename_en = null;
var docrefid = null;
var rid = null;
var arr_url = ""; //遍历上面的数组，根据传过来的表名获取相应xml的路径
var arr_name = ""; //遍历上面的数组，根据传过来的表名获取相应中文名

var ctrl = null;
var defaultValue = "";
var isLoadTabb_2 = false;

function AfterInit(){
	
	tablename_en = jso_OpenPars2.tablename_en;
	docrefid = jso_OpenPars2.docrefid;
	rid = jso_OpenPars2.rid; //子表比主表多传一个参数，rid，用于在子表中确定唯一一条数据
	
	ctrl = jso_OpenPars2.ctrl; //一条机构控制人信息
	
	str_sqlCons = "docrefid = '"+ docrefid +"' and rid = '"+rid+"' ";
	
	for(var i=0;i<_TheArray.length;i++){
		if (tablename_en.toLowerCase() == _TheArray[i][0]) {
			arr_url = _TheArray[i][1]; //xml路径
			arr_name = _TheArray[i][2]; //xml中文名，用于创建tab页
		}
	}
	
	//主表卡片
	var ary_CardBtn = ["保存/onSave/icon-ok","隐藏提示/onHiddenErr","显示提示/onShowErr"];
	
	//这个类不需要手动传参，它会获取当前卡片的数据。包括表名等，所以当控制人信息和地址信息同时发生错误的时候，
	//在控制人卡片上展示需要修改的地方，在展示的地址列表中，点击编辑，会展示地址信息需要修改的地方，非常强大
	var jso_CardConfig = {
			"afterloadclass": "com.yusys.cams.revise.service.CamsAfterErrorDataLoadClass2",
			"isafterloadsetcolor": "Y"
		};
	
	if(arr_name=="控制人信息"){
		JSPFree.createTabb("d1",["控制人信息"]); //控制人信息出错，则只创建控制人信息tab页
	}else{
		JSPFree.createTabb("d1",["控制人信息",arr_name]); //创建tab页，控制人信息和另外一种信息，比如地址
	}

	JSPFree.createBillCard("d1_1", "/biapp-cams/freexml/cams/revise/cams_corp_ctrl_m_CODE1.xml", ary_CardBtn, jso_CardConfig);
	
	//添加监听事件，选择其他页签触发
	JSPFree.addTabbSelectChangedListener(d1_tabb, onSelect);
}

function AfterBodyLoad(){
	//查询主表卡片数据
	JSPFree.queryBillCardData(d1_1_BillCard, str_sqlCons, "Y");
}

//隐藏提示
function onHiddenErr(){
	JSPFree.setBillCardItemWarnMsgVisible(d1_1_BillCard,false);
}

//显示提示
function onShowErr(){
	JSPFree.setBillCardItemWarnMsgVisible(d1_1_BillCard,true);
}

function onSelect(_index,_title){
	var newIndex = _index+1;
	
	if(newIndex==1){
		
	}
	
	if(newIndex==2){
		if(!isLoadTabb_2){
			if(arr_name=="姓名信息"){
				JSPFree.createBillList("d1_2",_TheArray[0][1],null,{list_btns:"[icon-p41]编辑/onHandle;$VIEW;",ishavebillquery:"N",isSwitchQuery:"N",autoquery:"Y",autocondition:"data_id='"+ctrl.rid+"' and data_type='2'",querycontion:"data_id='"+ctrl.rid+"' and data_type='2'"});
				$.parser.parse('#d1_2');
				isLoadTabb_2 = true;
			}else if(arr_name=="地址信息"){
				JSPFree.createBillList("d1_2",_TheArray[1][1],null,{list_btns:"[icon-p41]编辑/onHandle;$VIEW;",ishavebillquery:"N",isSwitchQuery:"N",autoquery:"Y",autocondition:"data_id='"+ctrl.rid+"' and data_type='2'",querycontion:"data_id='"+ctrl.rid+"' and data_type='2'"});
				$.parser.parse('#d1_2');
				isLoadTabb_2 = true;
			}else if(arr_name=="TIN信息"){
				JSPFree.createBillList("d1_2",_TheArray[2][1],null,{list_btns:"[icon-p41]编辑/onHandle;$VIEW;",ishavebillquery:"N",isSwitchQuery:"N",autoquery:"Y",autocondition:"data_id='"+ctrl.rid+"' and data_type='2'",querycontion:"data_id='"+ctrl.rid+"' and data_type='2'"});
				$.parser.parse('#d1_2');
				isLoadTabb_2 = true;
			}
		}
	}
}

/**
* 保存
* @return {[type]} [description]
*/
function onSave(){
	JSPFree.setBillCardItemValue(d1_1_BillCard,"revise_status","2"); //更新此条数据的状态为已处理
	var flag = JSPFree.doBillCardUpdate(d1_1_BillCard,null);
	if(flag){
		//页面处理保存逻辑分为3种，控制人信息有误，子表信息有误，或者两种同时有错误。
		//如果控制人信息有误，那就只需要更改控制人信息，当列表中控制人信息都处理了，本条回执数据的状态变更为完成
		if(arr_name=="控制人信息"){
			// 处理主数据状态
			var jso_rt = JSPFree.doClassMethodCall("com.yusys.cams.revise.service.CamsReviseDMO", 
					"updateResultData3",{tab:"cams_receipt_data",tablename_en:tablename_en,docrefid:docrefid});
			if("OK"==jso_rt.msg){
				JSPFree.closeDialog("OK");
			}else{
				JSPFree.closeDialog("No");
			}
		}else{ //如果是控制人子表也有错误，在保存的时候，需要去查找4张表中的信息是否都被处理了，如果是，那就把本条回执数据的状态变更为完成
			// 处理主数据状态
			var jso_rt = JSPFree.doClassMethodCall("com.yusys.cams.revise.service.CamsReviseDMO", 
					"updateResultData4",{tab:"cams_receipt_data",tablename_en:tablename_en,docrefid:docrefid});
			if("OK"==jso_rt.msg){
				JSPFree.closeDialog("OK");
			}else{
				JSPFree.closeDialog("No");
			}
		}
		
	}
}

//获取sql
function getSql(){
	var sql = "select * from "+tablename_en+" where data_id='"+ctrl.rid+"' and data_type='2'";
	return sql;
}

//编辑
function onHandle(){
	
	var selectData = d1_2_BillList.datagrid('getSelected');
	if (selectData == null) {
		$.messager.alert('提示', '必须选择一条记录进行操作', 'warning');
		return;
	}
	
	var def = {tablename_en:tablename_en,docrefid:docrefid,rid:selectData.rid,type:"2"};
	
	JSPFree.openDialog(selectData.tablename,"/yujs/cams/revise/receiptReviseHandleData.js",950,600,def,function(_rtdata){
		if (_rtdata.type == "dirclose" || _rtdata == false) {
			return;
		}
		
		if (_rtdata == "OK") {
			JSPFree.alert("保存成功!<br>同时修改了对应的回执结果状态为【完成】!");
			JSPFree.queryDataBySQL(d1_2_BillList, getSql());  //立即查询刷新数据
		}else if (_rtdata == "No") {
			JSPFree.alert("保存成功!");
			JSPFree.queryDataBySQL(d1_2_BillList, getSql());  //立即查询刷新数据
		}
	});
}