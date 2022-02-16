//初始化界面,菜单配置路径是【/frs/yufreejs?js=/yujs/east/fillingProcess/distribute_examine.js】
var data = "";
var tabName = "";
var tabNameEn = "";
var str_className= "";
var str_classNameUpdate= "";
var str_classNameDelete= "";
var i = "N";
var str_ds="";
var str_classNameError="";
function AfterInit(){
	JSPFree.createSplitByBtn("d1","上下",650,["通过/onConfirm","退回/onBack"]);
	JSPFree.createTabb("d1_A", [ "新增&修改", "删除", "未处理" ]);
	data = jso_OpenPars.data;
	tabName = data.tab_name;
	tabNameEn = data.tab_name_en;
	dataDt = data.data_dt;
	org_no = data.org_no;
	rpt_org_no = data.rpt_org_no;
	distributeType = data.distribute_type;
	d = getDate(dataDt);

	str_className = "Class:com.yusys.east.business.model.service.EastModelTempleteBuilderExa.getTemplet('"+tabName+"','"+tabNameEn+"','"+rpt_org_no+"','" + org_no+ "','" + distributeType  + "','"+d+"','2')";
	JSPFree.createBillList("d1_A_1",str_className,null,{list_btns:"$VIEW",isSwitchQuery:"N",autoquery:"Y",ishavebillquery:"Y",list_ispagebar:"Y"});
	JSPFree.addTabbSelectChangedListener(d1_A_tabb,onSelect);
	str_ds = d1_A_1_BillList.templetVO.templet_option.ds;
	var result= JSPFree.doClassMethodCall("com.yusys.east.common.service.EastCommonBS","getErrorTableName",{orgNo: rpt_org_no, dataDt: d, tableNameEn: tabNameEn});
	tabNameEnZ = result.data;
	var isOrNot= JSPFree.doClassMethodCall("com.yusys.east.common.service.EastCommonBS","isOrNot",{dsName: str_ds, errTable: tabNameEnZ});
	if (!isOrNot.code == 'success') {
		i = "Y";
	}
}
function AfterBodyLoad(){
	hidden(d1_A_1_BillList);
}
// 通过
var col_arr = []; 
function onConfirm(){
	JSPFree.confirm('提示', '你真的要通过该任务吗?', function(_isOK){
		if (_isOK){
			var jso_allrids = [];
			jso_allrids.push(data.rid);
			var jso_templetVO = d1_A_1_BillList.templetVO; // 模板配置数据
			var array_items = jso_templetVO.templet_option_b; // 模板子表
			var colStr = "";
			for (var i = 0; i < array_items.length; i++) {
				var str_itemkey = array_items[i].itemkey;
				var listShow = array_items[i].list_isshow;
				if ("Y" == listShow) {
					colStr += str_itemkey + ',';
				}
			}
			colStr = colStr.substring(0, colStr.length - 1);
			// 修改数据状态：3：完成
			var jso_Pars = {allrids:jso_allrids,status:'3',type:'3',reason:"通过",userNo:str_LoginUserCode,oldStatus: data.status,tabName:tabName,
					tabNameEn:tabNameEn,distributeType:distributeType,colArry:colStr,orgNo: org_no, rptOrgNo:rpt_org_no, dataDt: dataDt, ds:str_ds};
			var jsn_result = JSPFree.doClassMethodCall("com.yusys.east.process.service.EastCrProcessBSDMO", "updateDataByTaskByRids1", jso_Pars);
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
			JSPFree.openDialog("填写原因","/yujs/east/fillingProcess/distribute_detail_fillin_reason.js",600,300,{templetcode:"/biapp-east/freexml/east/fillingProcess/east_filling_process_remark.xml"},function(_rtdata){
				if(_rtdata != null && _rtdata != undefined && null != _rtdata.reason && undefined != _rtdata.reason){
					// 修改数据状态：3：完成
					var jso_allrids = [];
					jso_allrids.push(data.rid);
					
					var jso_Pars = {allrids:jso_allrids,status:'2',type:'2',reason:_rtdata.reason,userNo:str_LoginUserCode};
					var jsn_result = JSPFree.doClassMethodCall("com.yusys.east.process.service.EastCrProcessBSDMO", "updateDataByTaskByRids2", jso_Pars);
					if (jsn_result.msg == 'OK') {
						JSPFree.closeDialog("退回");
					}
				}
			});
		}
	});
}
function getDate(dataDt) {
	var d = dataDt.replace(/-/g, '');
	return d;
}

/**
 *  点击sheet页切换
 * @param _index
 * @param _title
 */
var sql ="";
function onSelect(_index,_title) {
	var newIndex = _index+1;

	if(newIndex==1){
		JSPFree.queryDataByConditon(d1_A_1_BillList);
	}  else if(newIndex ==2 ){
		if (str_classNameDelete) {
			JSPFree.queryDataByConditon(d1_A_2_BillList);
		} else {
			str_classNameDelete = "Class:com.yusys.east.business.model.service.EastModelTempleteBuilderExa.getTemplet('"+tabName+"','"+tabNameEn+"','"+rpt_org_no+"','" + org_no+ "','" + distributeType  + "','"+ d+"','3')";
			JSPFree.createBillList("d1_A_2",str_classNameDelete,null,{list_btns:"$VIEW;",isSwitchQuery:"N",autoquery:"Y",list_ispagebar:"Y",ishavebillquery:"Y"});
			$.parser.parse('#d1_A_2');
		}
		hidden(d1_A_2_BillList);
	} else {
		if (i == 'N') {
			str_classNameError = "Class:com.yusys.east.business.model.service.EastModelTempleteBuilderFull.getTemplet('"+tabName+"','"+tabNameEn+"','"+rpt_org_no+"','" + org_no+ "','" + distributeType  + "','"+d+"','2')";
			JSPFree.createBillList("d1_A_3",str_classNameError,null,{list_btns:"$VIEW;",isSwitchQuery:"N",autoquery:"N",list_ispagebar:"Y",ishavebillquery:"Y"});
			$.parser.parse('#d1_A_3');
			// 由于每次切换tab，都会重新查数据，导致很慢。所以先注释掉
			sql = getSql(tabNameEnZ);
			d1_A_3_BillList.pagerType = "1";
			JSPFree.queryDataBySQL(d1_A_3_BillList, sql);
			JSPFree.billListBindCustQueryEvent(d1_A_3_BillList, onDetailInfoErrorSummary);
			FreeUtil.resetToFirstPage(d1_A_3_BillList); //手工跳转到第一页
			hidden(d1_A_3_BillList);
		}
	}
}

/**
 * 隐藏部分展示框
 */
function hidden(billList) {
	// 隐藏日历
	FreeUtil.loadBillQueryData(billList, {cjrq: dataDt,kjrq: dataDt, org_no: rpt_org_no});
	/*JSPFree.setBillQueryItemEditable("cjrq", "日历", false);
	JSPFree.setBillQueryItemEditable("kjrq", "日历", false);
	// 隐藏法人机构
	JSPFree.setBillQueryItemEditable("org_no", "自定义参照", false);*/
	// 如果是按照网点，则也隐藏网点
	if (distributeType == '2') {
		FreeUtil.loadBillQueryData(billList, {issued_no: org_no });
		// 隐藏网点机构
		//JSPFree.setBillQueryItemEditable("issued_no", "自定义参照", false);
	}
}

/**
 * 获取sql语句，根据实际情况拼接sql语句，包含日期、机构等过滤条件
 *
 */
function getSql(tableName) {
	var cond = "";
	cond += " and org_no='" + rpt_org_no + "'";
	if ("2" == (distributeType)) {
		cond  += " and issued_no='" + org_no + "'";
	}
	cond += " and cjrq='" + d + "'";
	var _sql = "select * from " + tableName + " z where not exists (select 1 from " + tabNameEn + "_r r where r.rid=z.rid and r.is_check='0'" + cond +")";

	return _sql + cond;
}

/**
 * 绑定查询按钮
 * @param _condition
 */
function onDetailInfoErrorSummary(_condition) {
	if (i == 'Y') {
		$.messager.alert('提示', '当前日期下该机构的数据未检核,无错误数据！', 'warning');
		return;
	}
	if (_condition != "") {
		sql = sql + " and" + _condition;
	}
	JSPFree.queryDataBySQL(d1_A_1_BillList, sql);
	FreeUtil.resetToFirstPage(d1_A_1_BillList); //手工跳转到第一页
}