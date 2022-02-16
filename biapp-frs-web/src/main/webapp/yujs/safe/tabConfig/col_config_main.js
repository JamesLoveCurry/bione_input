/**
 *
 * <pre>
 * Title:【报表管理】-【维护字段】
 * Description:维护字段：主页面
 * 在此界面对报表的字段信息进行维护，包括字段的中文名称，英文名称等
 * </pre>
 * @author liangzy5
 * @version 1.00.00
 * @date 2020/6/15 14:03
 */

var str_tab_name = "";

function AfterInit() {
	str_tab_name = jso_OpenPars.tab_name;  // 表名

	// 获取"检核数据表"
	var tab_name = SafeFreeUtil.getTableNames().SAFE_CR_COL;
	// 获取英文表名
	var jso_data = JSPFree.doClassMethodCall("com.yusys.safe.base.common.service.SafeCommonBS", "getTabNameByEn", {tab_name:tab_name});
	var tab_name_en = jso_data.tab_name_en;
	// 创建列表
	var str_className = "Class:com.yusys.safe.tabmanage.template.ColConfigBuilderTemplate.getTemplet('"+tab_name+"','"+tab_name_en+"')";
	JSPFree.createBillList("d1",str_className,null,{list_btns:"$INSERT;$UPDATE;$DELETE;",isSwitchQuery:"N"});

    //设置列表的默认值对象 
	d1_BillList.DefaultValues={tab_name:str_tab_name, tab_name_en: tab_name_en};

	var str_sqlWhere = "tab_name='"  + str_tab_name + "'";  // 拼SQL条件
	JSPFree.queryDataByConditon(d1_BillList, str_sqlWhere);  // 锁定规则表查询数据
	
	JSPFree.setBillListForceSQLWhere(d1_BillList, str_sqlWhere);
}