/**
 * 
 * <pre>
 * Title: 检核结果数据表排名
 * Description: 【机构】按钮操作
 * </pre>
 * @author wangxy31 
 * @version 1.00.00
   @date 2020年8月12日
 */

var tabName = "";
var dataDt = "";
var orgNo = null;
var orgType = null;
var orgClass = null;
var busiType = "";

function AfterInit(){
	tabName = jso_OpenPars.tab_name;
	dataDt=jso_OpenPars.data_dt;
	orgNo = jso_OpenPars.org_no;
	orgType = jso_OpenPars.org_type;
	orgClass = jso_OpenPars.org_class;
	
	JSPFree.createBillList("d1","/biapp-bfd/freexml/resultsummary/bfd_result_tab_org_summary_ranking_v.xml");
	
	if (orgType == "Y") {
		if (orgClass == BfdFreeUtil.getBfdOrgClass().zh) {
			busiType = "1_" + orgNo;
		} else if (orgClass == BfdFreeUtil.getBfdOrgClass().fh) {
			busiType = "2_" + orgNo;
		}
	} else {
		busiType = "1_";
	}
	
	
	// 报送机构号转换成用户所属机构号
	var userNo = "";
	var jso_userorg = JSPFree.doClassMethodCall("com.yusys.bfd.common.service.BfdValidateQueryConditionBS", "getUserOrgNoCondition", {"orgNo" : orgNo});
	if (jso_userorg.msg == "ok") {
		userNo = jso_userorg.data;
	}
	
	var condition = "";
	var jso_org = JSPFree.doClassMethodCall("com.yusys.bfd.common.service.BfdValidateQueryConditionBS", "getQueryCondition", {"_loginUserOrgNo" : userNo});
	if (jso_org.msg == "ok") {
		condition = jso_org.condition;
	}
	
	var str_sqlWhere = "busi_type like '" + busiType + "%' and tab_name='"  + tabName + "' and data_dt='" + dataDt + "' and " + condition;  // 拼SQL条件
	JSPFree.queryDataByConditon(d1_BillList, str_sqlWhere);  // 锁定规则表查询数据
}

/**
 * 历史
 * @returns
 */
function historyFn(_btn) {
	var dataset = _btn.dataset;  // 数据都在这个map中
	var index = dataset.rowindex;
	var rows = d1_BillList.datagrid("getRows");
	var row = rows[index]; // index为行号
	
	var jso_OpenPars = {};
	jso_OpenPars.org_nm = row.org_nm;
	jso_OpenPars.tab_name = tabName;
	jso_OpenPars.data_dt = dataDt;
	jso_OpenPars.org_no = orgNo;
	jso_OpenPars.busi_type = busiType;
	
	JSPFree.openDialog(row.tab_name,"/yujs/bfd/resultsummary/bfd_check_result_tab_ranking_org_history.js", 700, 450, jso_OpenPars, function(_rtdata){});
}