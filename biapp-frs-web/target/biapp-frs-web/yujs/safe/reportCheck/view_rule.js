/**
 *
 * <pre>
 * Title:查看校验规则
 * Description:
 * </pre>
 * @author miaokx
 * @version 1.00.00
 * @date 2020/6/10 14:53
 */
var tab_name = "";
var tab_name_en = "";
var str_subfix = "";
var str_className = "";
function AfterInit(){
    // 获取路径参数
    if (jso_OpenPars != '') {
        if(jso_OpenPars.type != null) {
            str_subfix = jso_OpenPars.type;
        }
    }
    var jso_par1 = {list_btns:"",isSwitchQuery:"N",autoquery:"Y",ishavebillquery:"Y",list_ispagebar:"Y"};
    // 获取【检核日志表】常量类
    tab_name = SafeFreeUtil.getTableNames().SAFE_CR_RULE;
    // 获取英文表名
    var jso_data = JSPFree.doClassMethodCall(
        "com.yusys.safe.base.common.service.SafeCommonBS",
        "getTabNameByEn", {tab_name:tab_name});
    tab_name_en = jso_data.tab_name_en;
    var str_className = "Class:com.yusys.safe.base.common.template.CommonBuilderTemplate.getTemplet('" + tab_name + "','" + tab_name_en + "','" + str_subfix + "')";
    JSPFree.createBillList("d1", str_className, null, jso_par1);
}

/**
 * 查看一条详细的规则
 * @param _this
 * @returns
 */
function onView(_this) {
	var dataset = _this.dataset;
	var index = dataset.rowindex;
	var rows = d1_BillList.datagrid("getRows");
	var row = rows[index];//index是行号

	var jso_OpenPars = {
		id : row.id,
		type_cd : row.type_cd,
		tab_name : tab_name,
		tab_name_en : tab_name_en
	};
	JSPFree.openDialog("规则查看", "/yujs/safe/ruleConfig/safeViewRuleDetail.js", 750, 450,
			jso_OpenPars, function(_rtdata) {
			});
}