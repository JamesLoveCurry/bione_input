//初始化界面,菜单配置路径是【/frs/yufreejs?js=/yujs/crrs/collection/addSingle.js】
var _TheArray = [
	["cams_indv_name_m", "/biapp-cams/freexml/cams/revise/cams_indv_name_m_CODE1.xml"],
	["cams_indv_addr_m", "/biapp-cams/freexml/cams/revise/cams_indv_addr_m_CODE1.xml"],
	["cams_indv_tin_m", "/biapp-cams/freexml/cams/revise/cams_indv_tin_m_CODE1.xml"],
	["cams_corp_ctrl_m", "/biapp-cams/freexml/cams/revise/cams_corp_ctrl_m_CODE1.xml"],
];
var tablename_en = null;
var docrefid = null;
var rid = null;

function AfterInit(){
	
	tablename_en = jso_OpenPars2.tablename_en;
	docrefid = jso_OpenPars2.docrefid;
	rid = jso_OpenPars2.rid; //子表比主表多传一个参数，rid，用于在子表中确定唯一一条数据
	
	JSPFree.createBillList("d1","/biapp-cams/freexml/cams/revise/cams_corp_ctrl_m_CODE1.xml",null,{list_btns:"[icon-p41]编辑/onHandle;$VIEW;",isSwitchQuery:"N",autoquery:"N",list_ispagebar:"Y",ishavebillquery:"N"});
	//赋值
	JSPFree.queryDataBySQL(d1_BillList, getSql());
}

//获取sql
function getSql(){
	//tablename_en有可能是个人姓名，地址，tin或者控制人信息当中的任何一种
	//但是列表展示一定是展示控制人信息列表，所以表名固定
	var sql = "select * from cams_corp_ctrl_m where docrefid='"+docrefid+"'";
	return sql;
}

//编辑
function onHandle(){
	
	var selectData = d1_BillList.datagrid('getSelected');
	if (selectData == null) {
		$.messager.alert('提示', '必须选择一条记录进行操作', 'warning');
		return;
	}
	
	//传参的时候，把整一条控制人信息传过去
	var def = {tablename_en:tablename_en,docrefid:selectData.docrefid,rid:selectData.rid,ctrl:selectData};
	
	JSPFree.openDialog2(selectData.tablename,"/yujs/cams/revise/editCorpCtrlReceipt.js",950,600,def,function(_rtdata){
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