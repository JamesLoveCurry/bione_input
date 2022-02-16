/**
 * 
 * <pre>
 * Title: 【报表处理】-【报表填报】
 * Description: 报表处理-报表填报：填报按钮的主页面
 * 列表展示子任务中的具体错误明细数据，选择一条错误明细数据，即可对数据进行填报。已填报的会放在“已填报页”
 * </pre>
 * @author miaokx
 * @version 1.00.00
   @date 2021年4月18日
 */

var tabName = "";
var taskId = "";
var _sql = "";
var _sql1 = "";
var _date = "";
var isLoadTabb_2 = false;
var maskUtil = "";
function AfterInit(){
	taskId = jso_OpenPars.taskId;
	tabName = jso_OpenPars.tabName;
	tabNameEn = jso_OpenPars.tabNameEn;
	dataDt = jso_OpenPars.dataDt;
	_date = getDate(jso_OpenPars.dataDt);

	maskUtil = FreeUtil.getMaskUtil();

	JSPFree.createSplitByBtn("d1","上下",430,["提交/submission","强制提交/forcesubmission"]);
	
	JSPFree.createTabb("d1_A", [ "填报", "已填报" ]);
	
	var str_className1 = "Class:com.yusys.imas.business.service.ImasModelTempletBuilderForDistribute.getTemplet('"+tabName+"','"+tabNameEn+"','"+str_LoginUserOrgNo+"','_R','"+taskId+"','0')";
	JSPFree.createBillList("d1_A_1",str_className1,null,{list_btns:"[icon-p41]编辑/updateDate(this);$VIEW;[icon-remove]强制删除/deleteData(this);",isSwitchQuery:"N",autoquery:"N",ishavebillquery:"N"});
	d1_A_1_BillList.pagerType="1"; //第二种分页类型
	JSPFree.queryDataByConditon(d1_A_1_BillList, null)
	FreeUtil.loadBillQueryData(d1_A_1_BillList, {data_dt:_date,tr_dt:_date});
	JSPFree.addTabbSelectChangedListener(d1_A_tabb,onSelect);
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
 * 监听事件，监听选择的页签
 * 当选择第一个或者第二个页签的时候，动态加载数据
 * @param _index
 * @param _title
 * @returns
 */
function onSelect(_index,_title) {
	var newIndex = _index+1;

	if(newIndex==1){
		JSPFree.queryDataByConditon(d1_A_1_BillList);
	} else if(newIndex==2){
		var str_className2 = "Class:com.yusys.imas.business.service.ImasModelTempletBuilderForAudit.getTemplet('"+tabName+"','"+tabNameEn+"','"+str_LoginUserOrgNo+"','_R','"+taskId+"','1')";
		JSPFree.createBillList("d1_A_2",str_className2,null,{list_btns:"[icon-p41]编辑/updateDate1(this);$VIEW;[icon-remove]强制删除/deleteData1(this);",isSwitchQuery:"N",autoquery:"Y",ishavebillquery:"N"});
		d1_A_2_BillList.pagerType="2"; //第二种分页类型
		FreeUtil.loadBillQueryData(d1_A_2_BillList, {data_dt:_date,tr_dt:_date});
		
		$.parser.parse('#d1_A_2');
	}
}
function deleteData() {
	var json_data = d1_A_1_BillList.datagrid('getSelections');
	if (json_data == null || json_data == undefined || json_data.length == 0) {
		$.messager.alert('提示', '必须选择一条记录进行操作', 'warning');
		return;
	}
	var ds = d1_A_1_BillList.templetVO.templet_option.ds;
	var _par = {rid: json_data[0].rid, tabName: tabNameEn, dsName: ds};
	JSPFree.confirm('提示', '强制删除会同步删除业务数据,是否确认删除?', function(_isOK){
		if (_isOK) {
			JSPFree.doClassMethodCall("com.yusys.imas.process.service.ImasDistributeFillinBS","forceDelete",_par);
			// 从界面上删除行
			var p = d1_A_1_BillList.datagrid('getPager');
			$(p).pagination('select');
			$.messager.alert('提示', '删除数据成功!', 'info');
		}
	})

}

function deleteData1() {
	var json_data = d1_A_2_BillList.datagrid('getSelections');
	if (json_data == null || json_data == undefined || json_data.length == 0) {
		$.messager.alert('提示', '必须选择一条记录进行操作', 'warning');
		return;
	}
	var ds = d1_A_2_BillList.templetVO.templet_option.ds;
	var _par = {rid: json_data[0].rid, tabName: tabNameEn, dsName: ds};
	JSPFree.confirm('提示', '强制删除会同步删除业务数据,是否确认删除?', function(_isOK){
		if (_isOK) {
			JSPFree.doClassMethodCall("com.yusys.imas.process.service.ImasDistributeFillinBS","forceDelete",_par);
			// 从界面上删除行
			var p = d1_A_2_BillList.datagrid('getPager');
			$(p).pagination('select');
			$.messager.alert('提示', '删除数据成功!', 'info');
		}
	})

}
/**
 * 待审核的编辑按钮
 * 选择一条错误明细数据，点击编辑按钮，对数据进行填报
 * @returns
 */
function updateDate(){
	var json_data = d1_A_1_BillList.datagrid('getSelections');
	if (json_data == null || json_data == undefined || json_data.length == 0) {
		$.messager.alert('提示', '必须选择一条记录进行操作', 'warning');
		return;
	}
	if (json_data.length>1) {
		$.messager.alert('提示', '只能选择一条记录进行操作', 'warning');
		return;
	}
	
	var jso_Pars = {json_data:json_data[0],tabNameEn:tabNameEn,tabName:tabName,dataDt:dataDt, rptOrgNo: json_data[0].rpt_org_no}
	JSPFree.openDialog2("编辑","/yujs/imas/fillingProcess/imas_distribute_detail_fillin_edit.js",1100,600,jso_Pars,function(_rtData){
		if (_rtData != null) {
			if ("dirclose" == _rtData.type) {
				return;
			}
			if (_rtData == true) {
				if (json_data[0].data_ources == ImasFreeUtil.getDataOurces().etl_process){//etl才处理
					if (json_data[0].data_modify == ImasFreeUtil.getDataModefy().data_maintenance){//如果维护界面，则其减一
						var _par={tab_name_en:tabNameEn,data_dt:json_data[0].data_dt,number:-1,source_type:ImasFreeUtil.getSourceType().data_maintenance,rpt_org_no:json_data[0].rpt_org_no};
						JSPFree.doClassMethodCall("com.yusys.imas.dataoperation.StatisticalOperation","statisticalOperationForForm",_par)
					}
					if (json_data[0].data_modify != ImasFreeUtil.getDataModefy().data_filling){//本身不是填报界面才更新
						var _par={tab_name_en:tabNameEn,data_dt:json_data[0].data_dt,number:1,source_type:ImasFreeUtil.getSourceType().data_filling,rpt_org_no:json_data[0].rpt_org_no};
						JSPFree.doClassMethodCall("com.yusys.imas.dataoperation.StatisticalOperation","statisticalOperationForForm",_par)
					}
				}
				$.messager.alert('提示', '校验并保存成功！', 'info');
				JSPFree.queryDataByConditon(d1_A_1_BillList);
			}
		}
	});
}


/**
 * 已审核的编辑按钮
 * 选择一条错误明细数据，点击编辑按钮，对数据进行填报
 * @returns
 */
function updateDate1(){
	var json_data = d1_A_2_BillList.datagrid('getSelections');
	if (json_data == null || json_data == undefined || json_data.length == 0) {
		$.messager.alert('提示', '必须选择一条记录进行操作', 'warning');
		return;
	}
	if (json_data.length>1) {
		$.messager.alert('提示', '只能选择一条记录进行操作', 'warning');
		return;
	}

	var jso_Pars = {json_data:json_data[0],tabNameEn:tabNameEn,tabName:tabName,dataDt:dataDt, rptOrgNo: json_data[0].rpt_org_no}
	JSPFree.openDialog2("编辑","/yujs/imas/fillingProcess/imas_distribute_detail_fillin_edit1.js",1100,600,jso_Pars,function(_rtData){
		if (_rtData != null) {
			if ("dirclose" == _rtData.type) {
				return;
			}
			if (_rtData == true) {
				$.messager.alert('提示', '校验并保存成功！', 'info');
				JSPFree.queryDataByConditon(d1_A_2_BillList);
			}
		}
	});
}

/**
 * 提交按钮
 * @returns
 */
function submission(){
	// 如果当前待编辑列表中有数据，则不能进行提交
	if (d1_A_1_BillList.datagrid('getData').total != 0) {
		$.messager.alert('提示', '当前列表存在待编辑数据，无法提交！', 'warning');
		return;
	}
	
	var jso_allrids=[]; 
	jso_allrids.push(taskId);

	maskUtil.mask();
	setTimeout(function () {
		// 修改数据状态：1：待审核
		var jsn_result = JSPFree.doClassMethodCall("com.yusys.imas.process.service.ImasCrProcessBSDMO", "updateDataByTaskByRids", {
			allrids: jso_allrids,
			status: '1',
			type: "1",
			userNo: str_LoginUserCode
		});
		maskUtil.unmask();
		if (jsn_result.msg == 'OK') {
			JSPFree.closeDialog("提交");
		}
	}, 100);
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
			JSPFree.openDialog("填写原因","/yujs/imas/fillingProcess/imas_distribute_detail_fillin_reason.js",600,300,
					{templetcode:"/biapp-imas/freexml/fillingProcess/imas_filling_process_remark.xml"},function(_rtdata){
				if(_rtdata != null && _rtdata != undefined && null != _rtdata.reason && undefined != _rtdata.reason){
					doUpdateBackStatus(d1_A_1_BillList,jso_allrids, _rtdata.reason);
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
function doUpdateBackStatus(billList,_rids,_reason){
	var jso_par = {allrids:_rids,type:'1',reason:_reason,userNo:str_LoginUserCode,unEditData:d1_A_1_BillList.datagrid('getData').total};
	maskUtil.mask();
	setTimeout(function () {
		var jsn_result = JSPFree.doClassMethodCall("com.yusys.imas.process.service.ImasCrProcessBSDMO", "doResultBack", jso_par);
		maskUtil.unmask();
		if (jsn_result.msg == 'OK') {
			JSPFree.closeDialog("强制提交");
		}
	},100);
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