/**
 *
 * <pre>
 * Title:【数据查询】【报表查询】
 * Description:各个报表列表
 * </pre>
 * @author miaokx
 * @version 1.00.00
 * @date 2020/6/2 17:25
 */
var tab_name = "";
var tab_name_en = "";
var report_type = "";
var str_ds = "";
var str_className = "";

function AfterInit() {
    tab_name = jso_OpenPars.tab_name;
    tab_name_en = jso_OpenPars.tab_name_en;
    str_ds = jso_OpenPars.ds;
    report_type = jso_OpenPars.report_type;
    
    // 此处要对【单位基本情况表】进行特殊处理
    str_className = "Class:com.yusys.safe.base.common.template.CommonBuilderTemplate.getTemplet('" + tab_name + "','" + tab_name_en + "','" + report_type + "','" + str_LoginUserOrgNo + "')";
    // 根据主表id查询其相关字子表，返回子表按钮标识
     // var childBtnStr = JSPFree.getChildTabBtn(tab_name_en, report_type);

    // if (SafeFreeUtil.getTabNameEn() == tab_name_en) {
    // 	JSPFree.createBillList("d1", str_className, null, {list_btns:"[icon-p81]查看/view1;[icon-p81]开户|经办信息/childView;",isSwitchQuery:"N",autoquery:"N"});
    // } else {
    // 	JSPFree.createBillList("d1", str_className, null, {list_btns:"[icon-p81]查看/view1;",isSwitchQuery:"N",autoquery:"N"});
    // }
	JSPFree.createBillList("d1", str_className, null, {list_btns:"[icon-p81]查看/view1;",isSwitchQuery:"N",autoquery:"Y"});

}

/**
 * 查看
 * @returns
 */
function view1() {
	var json_rowdata = d1_BillList.datagrid('getSelected'); // 先得到数据
	
	if (json_rowdata == null || json_rowdata == undefined) {
		$.messager.alert('提示', '至少选择一条记录进行操作', 'warning');
		return;
	}

	var defaultVal = {templetcode:str_className,type:"View",tabname:tab_name,tabnameen:tab_name_en,str_ds:str_ds,report_type:report_type};
	defaultVal["_BillCardData"] = json_rowdata; // 列表数据,这样设置就是从前端取数
		
	JSPFree.openDialog3("查看","/yujs/safe/busidata/safe_check_data_edit.js",null,null,defaultVal,function(_rtdata) {
		
	},true);
}

/**
 * 查看子信息
 * @returns
 */
// function childView() {
// 	var json_rowdata = d1_BillList.datagrid('getSelected'); // 先得到数据
//
// 	if (json_rowdata == null || json_rowdata == undefined) {
// 		$.messager.alert('提示', '至少选择一条记录进行操作', 'warning');
// 		return;
// 	}
//
// 	var tabname = "单位金融机构标识码信息表";
// 	var tabnameen = "SAFE_CORP_FIN_INFO";
// 	var defaultVal = {tabname:tabname,tabnameen:tabnameen,str_ds:str_ds,report_type:report_type,custcode:json_rowdata.custcode,custname:json_rowdata.custname,data_dt:json_rowdata.data_dt};
// 	JSPFree.openDialog3("查看单位金融机构标识码信息","/yujs/safe/busidata/safe_corp_fin_info_list.js",null,null,defaultVal,function(_rtdata) {},true);
// }

/**
 * 查看字表信息
 * @param tabNmEn 字表表名
 */
function childView(childTabNmEn, childTabNm, btnNm, typeFlag) {
    var json_rowdata = d1_BillList.datagrid('getSelected'); // 先得到数据

    if (json_rowdata == null || json_rowdata == undefined) {
        $.messager.alert('提示', '至少选择一条记录进行操作', 'warning');
        return;
    }
    var defaultVal = {tabname:childTabNm,tabnameen:childTabNmEn,typeFlag:typeFlag,str_ds:str_ds,report_type:report_type,parentTabNmEn:tab_name_en,parentRid:json_rowdata.rid,data_dt:json_rowdata.data_dt,org_no:json_rowdata.org_no};
    JSPFree.openDialog3("查看"+btnNm+"信息","/yujs/safe/busidata/safe_childtab_info_list.js",null,null,defaultVal,function(_rtdata) {},true);

}