//初始化界面,菜单配置路径是【/frs/yufreejs?js=/yujs/wmp/distribute_examine.js】
var data = "";
var tabName = "";
var tabNameEn = "";
function AfterInit(){
	JSPFree.createSplitByBtn("d1","上下",500,["通过/onConfirm","退回/onBack"]);
	
	data = jso_OpenPars.data;
	tabName = data.tab_name;
	tabNameEn = data.tab_name_en;
	var dataDt = data.data_dt;
	var taskId = data.rid;

	var str_className2 = "Class:com.yusys.wmp.business.model.service.WmpModelTempletBuilder.getTemplet('"+tabName+"','"+tabNameEn+"','"+str_LoginUserOrgNo+"','_R','"+taskId+"')";
	JSPFree.createBillList("d1_A",str_className2,null,{list_btns:"$VIEW",isSwitchQuery:"N",autoquery:"Y",ishavebillquery:"N",list_ispagebar:"N"});
}

// 通过
var col_arr = []; 
function onConfirm(){
	JSPFree.confirm('提示', '你真的要通过该任务吗?', function(_isOK){
		if (_isOK){
			var jso_allrids = [];
			jso_allrids.push(data.rid);
			
			var jso_col = JSPFree.getHashVOs("select col_name_en from frs_wmp_cr_col where tab_name='"+tabName+"' and is_pk='Y' order by col_no");
			if(jso_col != null && jso_col.length > 0){
				for (var i=0;i<jso_col.length;i++) {
					col_arr.push(jso_col[i].col_name_en);
				}
			}

			// 修改数据状态：3：完成
			var jso_Pars = {allrids:jso_allrids,status:'3',type:'3',reason:"通过",userNo:str_LoginUserCode,
					tabNameEn:tabNameEn,colArry:col_arr,jsonData:d1_A_BillList.datagrid('getData').rows};
			var jsn_result = JSPFree.doClassMethodCall("com.yusys.wmp.process.service.WmpCrProcessBS", "updateDataByTaskByRids1", jso_Pars);
			if (jsn_result.msg == 'OK') {
				JSPFree.closeDialog("通过");
			}
		}
	});
}

// 退回
function onBack(){
	JSPFree.confirm('提示', '你真的要退回该任务吗?', function(_isOK){
		if (_isOK){
			//弹出窗口,传入参数,然后接收返回值!
			JSPFree.openDialog("填写原因","/yujs/wmp/distribute_detail_fillin_reason.js",600,300,{templetcode:"/biapp-wmp/freexml/wmp/wmp_filling_process_remark.xml"},function(_rtdata){
				if(_rtdata != null && _rtdata != undefined && null != _rtdata.reason && undefined != _rtdata.reason){
					// 修改数据状态：3：完成
					var jso_allrids = [];
					jso_allrids.push(data.rid);
					
					var jso_Pars = {allrids:jso_allrids,status:'2',type:'2',reason:_rtdata.reason,userNo:str_LoginUserCode};
					var jsn_result = JSPFree.doClassMethodCall("com.yusys.wmp.process.service.WmpCrProcessBS", "updateDataByTaskByRids2", jso_Pars);
					if (jsn_result.msg == 'OK') {
						JSPFree.closeDialog("退回");
					}
				}
			});
		}
	});
}
