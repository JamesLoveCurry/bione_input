/**
 * 
 * <pre>
 * Title:【统计与监控】-数据下钻页面，通过点击【总数】查看
 * 机构权限控制：总行下钻分行的数据，分行下钻支行的数据
 * Description: 计算各个报表的总数，上报数，拦截数
 * SafeReportAnalysisBuilderTemplate.getTemplet('2') 参数是2，数据下钻的页面
 * </pre>
 * @author liangzy5 
 * @version 1.00.00
 * @date 2020年6月29日
 */
var data_dt = null;
var org_no = null; //报送机构号
var report_type = null;
var tab_name_en = null;
var str_tab_name = null;
function AfterInit(){
	data_dt = jso_OpenPars.data_dt;
	org_no = jso_OpenPars.org_no;
	report_type = jso_OpenPars.report_type;
	tab_name_en = jso_OpenPars.tab_name_en;
	str_tab_name = jso_OpenPars.str_tab_name;
	
	var str_className = "Class:com.yusys.safe.analysis.template.SafeReportAnalysisBuilderTemplate.getTemplet('"+str_tab_name+"','2')"; //传参type=2，数据下钻的页面
	JSPFree.createBillList("d1",str_className,null,{ishavebillquery:"N",isSwitchQuery:"N"});
	JSPFree.queryDataBySQL(d1_BillList, getSql()); //查询数据
}

/*
 * 获取sql，这里必须先代入mgr_org_no用户所属机构去查org_no报送机构号，再通过机构号之前的层级关系，up_org_no上级机构号等于=org_no，获取此机构下的全部子级机构
 */
function getSql(){
	var sql = "select org_no, '"+data_dt+"' as data_dt, '"+report_type+"' as report_type," +"'"+tab_name_en
		+"' as tab_name_en from rpt_org_info where org_type='"+report_type+"' and up_org_no='"+org_no+"'";
	return sql;
}