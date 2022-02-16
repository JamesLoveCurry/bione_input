/**
 *
 * <pre>
 * Title:【报表管理】-【维护字段】
 * Description:维护字段：主页面
 * 在此界面对报表的字段信息进行维护，包括字段的中文名称，英文名称等
 * </pre>
 * @author mkx
 * @version 1.00.00
 * @date 2020/8/7 14:03
 */

var str_tab_name = "";
var str_tab_name_en = "";
function AfterInit() {
	str_tab_name = jso_OpenPars.tab_name;  // 表名
	str_tab_name_en = jso_OpenPars.tab_name_en; // 英文表名
	var whereSql = "tab_name = '" + str_tab_name + "'";
	JSPFree.createBillList("d1","/biapp-imas/freexml/tabConfig/imas_cr_col_code.xml",null,{list_btns:"$INSERT;$UPDATE;$DELETE;",isSwitchQuery:"N", refWhereSQL:whereSql});
	//设置列表的默认值对象
	d1_BillList.DefaultValues={tab_name: str_tab_name, tab_name_en: str_tab_name_en};
}