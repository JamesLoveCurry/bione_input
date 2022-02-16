//初始化界面,菜单配置路径是【/frs/yufreejs?js=/yujs/crrs/collection/addSingle.js】
var corp = null;
var defaultValue = "";
var isLoadTabb_2 = false;
var isLoadTabb_3 = false;
var isLoadTabb_4 = false;
var isLoadTabb_5 = false;
function AfterInit(){
	corp = jso_OpenPars2.corp;
	var jsy_btns = ["通过/onSaveSubmit/icon-ok","退回/onBack/icon-ok","查看日志/onLogs/icon-ok"];
	JSPFree.createTabbByBtn("d1",["账户信息","姓名信息","地址信息","TIN信息","控制人信息"],jsy_btns,false);

	JSPFree.createBillCard("d1_1","/biapp-cams/freexml/cams/cams_corp_acct_CODE1.xml");
	//赋值
	JSPFree.queryBillCardData(d1_1_BillCard, "rid = '"+corp.rid+"'");
	
	JSPFree.addTabbSelectChangedListener(d1_tabb, onSelect);
	defaultValue = {account_number:corp.account_number};
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
			JSPFree.createBillList("d1_2","/biapp-cams/freexml/cams/cams_corp_name_CODE1.xml",null,{ishavebillquery:"N",isSwitchQuery:"N",autoquery:"Y",autocondition:"account_number='"+corp.account_number+"'",querycontion:"account_number='"+corp.account_number+"'",list_btns:"$VIEW"});
			$.parser.parse('#d1_2');
			JSPFree.setDefaultValues(d1_2_BillList,defaultValue);
			isLoadTabb_2 = true;
		}
	}

	if(newIndex==3){
		if(!isLoadTabb_3){
			JSPFree.createBillList("d1_3","/biapp-cams/freexml/cams/cams_corp_addr_CODE1.xml",null,{ishavebillquery:"N",isSwitchQuery:"N",autoquery:"Y",autocondition:"account_number='"+corp.account_number+"'",querycontion:"account_number='"+corp.account_number+"'",list_btns:"$VIEW"});
			$.parser.parse('#d1_3');
			JSPFree.setDefaultValues(d1_3_BillList,defaultValue);
			isLoadTabb_3 = true;
		}
	}

	if(newIndex==4){
		if(!isLoadTabb_4){
			var def = {account_number:corp.account_number,in_type:"TIN"};
			JSPFree.createBillList("d1_4","/biapp-cams/freexml/cams/cams_corp_tin_CODE1.xml",null,{ishavebillquery:"N",isSwitchQuery:"N",autoquery:"Y",autocondition:"account_number='"+corp.account_number+"'",querycontion:"account_number='"+corp.account_number+"'",list_btns:"$VIEW"});
			$.parser.parse('#d1_4');
			JSPFree.setDefaultValues(d1_4_BillList,def);
			isLoadTabb_4 = true;
		}
	}

	if(newIndex==5){
		if(!isLoadTabb_5){
			JSPFree.createBillList("d1_5","/biapp-cams/freexml/cams/cams_corp_ctrl_CODE1.xml",null,{ishavebillquery:"N",isSwitchQuery:"N",autoquery:"Y",autocondition:"account_number='"+corp.account_number+"'",querycontion:"account_number='"+corp.account_number+"'",list_btns:"$VIEW"});
			$.parser.parse('#d1_5');
			JSPFree.setDefaultValues(d1_5_BillList,defaultValue);
			isLoadTabb_5 = true;
		}
	}
}

// 通过
function onSaveSubmit() {
	var def = {rid:corp.rid,user_no:str_LoginUserCode,data_type:"corp"};
	
	JSPFree.confirm('提示', '你确定将数据提交至审批岗?', function(_isOK){
		if (_isOK){
			var jsn_result = JSPFree.doClassMethodCall("com.yusys.cams.corp.service.CamsCorpBSDMO", "submitDataReview", def);
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
	var jso_par = {rid:corp.rid,user_no:str_LoginUserCode,data_type:"corp",reason:_reason};
	var jsn_result = JSPFree.doClassMethodCall("com.yusys.cams.corp.service.CamsCorpBSDMO","backDataReview",jso_par);
	
	if (jsn_result.msg == 'OK') {
		JSPFree.closeDialog("数据退回成功！");
	}
}

// 查看日志
function onLogs() {
	var jso_par = {data_id:corp.rid,data_type:"corp"};
	JSPFree.openDialog("处理日志信息","/yujs/cams/collection/processlogs.js",900,600,jso_par,function(_rtData){
		if (_rtData != null) {
			if ("dirclose" == _rtData.type) {
				return;
			}
		}
	});
}
