//初始化界面,菜单配置路径是【/frs/yufreejs?js=/yujs/crrs/collection/addSingle.js】
var indv = null;
var defaultValue = "";
var isLoadTabb_2 = false;
var isLoadTabb_3 = false;
var isLoadTabb_4 = false;
function AfterInit(){
	indv = jso_OpenPars2.indv;
	var jsy_btns = ["通过/onSaveSubmit/icon-ok","退回/onBack/icon-ok","查看日志/onLogs/icon-ok"];
	JSPFree.createTabbByBtn("d1",["账户信息","姓名信息","地址信息","TIN信息"],jsy_btns,false);

	JSPFree.createBillCard("d1_1","/biapp-cams/freexml/cams/cams_indv_acct_CODE1.xml");
	//赋值
	JSPFree.queryBillCardData(d1_1_BillCard, "rid = '"+indv.rid+"'");
	
	JSPFree.addTabbSelectChangedListener(d1_tabb, onSelect);
	defaultValue = {data_id:indv.rid,data_type:"1"};
}

function AfterBodyLoad(){
	JSPFree.setBillCardItemEditable(d1_1_BillCard,"*",false);
}

function onSelect(_index,_title){
	var newIndex = _index+1;
	
	if(newIndex==1){
		
	}
	
	if(newIndex==2){
		if(!isLoadTabb_2){
			var def = {data_id:indv.rid,data_type:"1",first_name:indv.first_name,last_name:indv.last_name};
			JSPFree.createBillList("d1_2","/biapp-cams/freexml/cams/cams_indv_name_CODE1.xml",null,{ishavebillquery:"N",isSwitchQuery:"N",autoquery:"Y",autocondition:"data_id='"+indv.rid+"' and data_type='1'",querycontion:"data_id='"+indv.rid+"' and data_type='1'",list_btns:"$VIEW"});
			$.parser.parse('#d1_2');
			JSPFree.setDefaultValues(d1_2_BillList,def);
			isLoadTabb_2 = true;
		}
	}

	if(newIndex==3){
		if(!isLoadTabb_3){
			JSPFree.createBillList("d1_3","/biapp-cams/freexml/cams/cams_indv_addr_CODE1.xml",null,{ishavebillquery:"N",isSwitchQuery:"N",autoquery:"Y",autocondition:"data_id='"+indv.rid+"' and data_type='1'",querycontion:"data_id='"+indv.rid+"' and data_type='1'",list_btns:"$VIEW"});
			$.parser.parse('#d1_3');
			JSPFree.setDefaultValues(d1_3_BillList,defaultValue);
			isLoadTabb_3 = true;
		}
	}

	if(newIndex==4){
		if(!isLoadTabb_4){
			var def = {data_id:indv.rid,data_type:"1",in_type:"TIN"};
			JSPFree.createBillList("d1_4","/biapp-cams/freexml/cams/cams_indv_tin_CODE1.xml",null,{ishavebillquery:"N",isSwitchQuery:"N",autoquery:"Y",autocondition:"data_id='"+indv.rid+"' and data_type='1'",querycontion:"data_id='"+indv.rid+"' and data_type='1'",list_btns:"$VIEW"});
			$.parser.parse('#d1_4');
			JSPFree.setDefaultValues(d1_4_BillList,def);
			isLoadTabb_4 = true;
		}
	}
}

// 通过
function onSaveSubmit() {
	var def = {rid:indv.rid,user_no:str_LoginUserCode,data_type:"indv"};
	
	JSPFree.confirm('提示', '你确定将数据提交至审批岗?', function(_isOK){
		if (_isOK){
			var jsn_result = JSPFree.doClassMethodCall("com.yusys.cams.indv.service.CamsIndvBSDMO", "submitDataReview", def);
			if (jsn_result.msg == 'OK') {
				JSPFree.closeDialog(true);
			}
		}
	});
}

//退回
function onBack() {
	JSPFree.confirm('提示', '你确定将数据退回?', function(_isOK){
		if(_isOK){
			//弹出窗口,传入参数,然后接收返回值!
			JSPFree.openDialog("填写原因","/yujs/cams/collection/busidatareview_reason.js",600,300,{templetcode:"/biapp-cams/freexml/cams/cams_data_log_remark.xml"},function(_rtdata){
				if(_rtdata != null && _rtdata != undefined && null != _rtdata.reason && undefined != _rtdata.reason){
					doUpdateBackStatus(_rtdata.reason);
				}
			});
		}
	});
}

//退回，修改状态
function doUpdateBackStatus(_reason){
	var jso_par = {rid:indv.rid,user_no:str_LoginUserCode,data_type:"indv",reason:_reason};
	var jsn_result = JSPFree.doClassMethodCall("com.yusys.cams.indv.service.CamsIndvBSDMO","backDataReview",jso_par);
	
	if (jsn_result.msg == 'OK') {
		JSPFree.closeDialog("数据退回成功！");
	}
}

// 查看日志
function onLogs() {
	var jso_par = {data_id:indv.rid,data_type:"indv"};
	JSPFree.openDialog("处理日志信息","/yujs/cams/collection/processlogs.js",900,600,jso_par,function(_rtData){
		if (_rtData != null) {
			if ("dirclose" == _rtData.type) {
				return;
			}
		}
	});
}
