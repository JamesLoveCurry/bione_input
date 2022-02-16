//初始化界面,菜单配置路径是【/frs/yufreejs?js=/yujs/crrs/collection/addSingle.js】
var _TheArray = [
	["cams_indv_name_m", "/biapp-cams/freexml/cams/revise/cams_indv_name_m_CODE1.xml"],
	["cams_indv_addr_m", "/biapp-cams/freexml/cams/revise/cams_indv_addr_m_CODE1.xml"],
	["cams_indv_tin_m", "/biapp-cams/freexml/cams/revise/cams_indv_tin_m_CODE1.xml"],
	["cams_corp_name_m", "/biapp-cams/freexml/cams/revise/cams_corp_name_m_CODE1.xml"],
	["cams_corp_addr_m", "/biapp-cams/freexml/cams/revise/cams_corp_addr_m_CODE1.xml"],
	["cams_corp_tin_m", "/biapp-cams/freexml/cams/revise/cams_corp_tin_m_CODE1.xml"],
	["cams_corp_ctrl_m", "/biapp-cams/freexml/cams/revise/cams_corp_ctrl_m_CODE1.xml"],
]; //所有报送层子表和路径
var tablename_en = null;
var docrefid = null;

function AfterInit(){
	tablename_en = jso_OpenPars2.tablename_en;
	docrefid = jso_OpenPars2.docrefid;
	
	var arr_url = null; //遍历上面的数组，根据传过来的表名获取相应xml的路径
	for(var i=0;i<_TheArray.length;i++){
		if (tablename_en.toLowerCase() == _TheArray[i][0]) {
			arr_url = _TheArray[i][1];
		}
	}
	
	JSPFree.createBillList("d1",arr_url,null,{list_btns:"[icon-p41]编辑/onHandle;$VIEW;",isSwitchQuery:"N",autoquery:"N",list_ispagebar:"Y",ishavebillquery:"N"});
	//赋值
	JSPFree.queryDataBySQL(d1_BillList, getSql());
}

//获取sql
function getSql(){
	var sql = "select * from "+tablename_en+" where docrefid='"+docrefid+"'";
	return sql;
}

//编辑
function onHandle(){
	
	var selectData = d1_BillList.datagrid('getSelected');
	if (selectData == null) {
		$.messager.alert('提示', '必须选择一条记录进行操作', 'warning');
		return;
	}
	
	var def = {tablename_en:tablename_en,docrefid:selectData.docrefid,rid:selectData.rid};
	
	JSPFree.openDialog(selectData.tablename,"/yujs/cams/revise/receiptReviseHandleData.js",950,600,def,function(_rtdata){
		if (_rtdata.type == "dirclose" || _rtdata == false) {
			return;
		}
		
		if (_rtdata == "OK") {
			JSPFree.alert("保存成功!<br>同时修改了对应的回执结果状态为【完成】!");
			JSPFree.queryDataBySQL(d1_BillList, getSql());  //立即查询刷新数据
		}else if (_rtdata == "No") {
			JSPFree.alert("保存成功!");
			JSPFree.queryDataBySQL(d1_BillList, getSql());  //立即查询刷新数据
		}
	});
}