/**
 *
 * <pre>
 * Title:【数据查询】【报表查询】
 * Description:报表查询:主页面
 * </pre>
 * @author wangxy31 
 * @version 1.00.00
   @date 2020年8月6日
 */
var tab_name = "";
var tab_name_en = "";
var str_subfix = "";
var str_className = "";

function AfterInit() {
	JSPFree.createSplit("d1","左右",235);
	var whereSql = '';
	var jsn_result = JSPFree.doClassMethodCall("com.yusys.imas.common.service.ImasGetReportBS", "getReportList", null);
	if (jsn_result.code == 'success') {
		whereSql = jsn_result.data;
	}
	JSPFree.createBillTree("d1_A","/biapp-imas/freexml/busiModel/imas_cr_tab_ref.xml", {refWhereSQL: whereSql});
	document.getElementById("d1_B").innerHTML="<div style='width:100%;text-align:center'><span style='font-size:16px;color:blue'>请选择左边表查询数据!</span></div>";
	
	JSPFree.bindBillTreeOnSelect(d1_A_BillTree,function(_node){
	  var isLeaf_1 = JSPFree.isBillTreeSelectNodeLeaf(d1_A_BillTree);
	  if (isLeaf_1) {  // 如果选中了叶子结点
	  		var jso_data = JSPFree.getBillTreeSelectData(d1_A_BillTree);
	  		var str_tab_name = jso_data.tab_name;
	  		var str_tab_name_en = jso_data.tab_name_en;
	  		var str_ds = jso_data.ds_name;
	  		
	  		// 判断选中的叶子节点，是否含有字段信息
	  		var jso_par = { str_tab_name:str_tab_name };
	  		var jso_data = JSPFree.doClassMethodCall("com.yusys.imas.business.service.ImasBusinessBS","checkTabCols",jso_par);
	  		var str_html = jso_data.msg;  // 返回的html
	  		if (str_html != null && str_html != "") {
	  			document.getElementById("d1_B").innerHTML="<div style='width:100%;text-align:center'><span style='font-size:16px;color:red'>"+jso_data.msg+"!</span></div>";
	  			
	  			return;
	  		}
		  	  
	  		var v_par = {"tab_name":str_tab_name,"tab_name_en":str_tab_name_en,"ds":str_ds};
	  		JSPFree.createOrReplaceIFrame("d1_B","/yujs/imas/hisdata/imas_his_data_list.js",v_par);
	  	}
	});
}