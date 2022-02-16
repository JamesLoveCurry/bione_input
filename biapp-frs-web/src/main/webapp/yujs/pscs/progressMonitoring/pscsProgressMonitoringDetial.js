//数据校验
var reportdate = "";
var tablename = "";
var tablename_en = "";
var org_no = "";
var org_name = "";
function AfterInit(){
	reportdate = jso_OpenPars.reportdate;
	tablename = jso_OpenPars.tablename;
	tablename_en = jso_OpenPars.tablename_en;
	org_no = jso_OpenPars.org_no;
	org_name = jso_OpenPars.org_name;

	JSPFree.createBillList("d1","/biapp-pscs/freexml/pscs/progressMonitoring/pscs_result_data_monitoring1.xml",null,{isSwitchQuery:"N",autoquery:"N",ishavebillquery:"N"});
	d1_BillList.pagerType="2"; //第二种分页类型
	var _sql = getSql();
	JSPFree.queryDataBySQL(d1_BillList, _sql);
}

function getSql() {
	// 根据传进来的org_no，判断是总行，分行，支行
	// 如果是总行，查看分行汇总
	// 如果是分行，查看支行汇总
	// 如果是支行，查看自己
	var org_class = "";
	var jso_org = JSPFree.getHashVOs("select org_class from rpt_org_info where org_type = '11' and org_no = '"+ org_no +"'");
	if(jso_org != null && jso_org.length > 0){
		org_class = jso_org[0].org_class;
	}
	
	var org_arr = [];
	org_arr.push(org_no);
	if (org_class == "总行") {
		var jso_org_z = JSPFree.getHashVOs("select org_no from rpt_org_info where org_type = '11' and org_class = '分行'");
		if(jso_org_z != null && jso_org_z.length > 0){
			for (var i=0;i<jso_org_z.length;i++) {
				org_arr.push(jso_org_z[i].org_no);
			}
		}
	} else if (org_class == "分行") {
		var jso_org_f = JSPFree.getHashVOs("select org_no from rpt_org_info where org_type = '11' and up_org_no = '"+ org_no +"' and org_class = '支行'");
		if(jso_org_f != null && jso_org_f.length > 0){
			for (var i=0;i<jso_org_f.length;i++) {
				org_arr.push(jso_org_f[i].org_no);
			}
		}
	}



	var str_sql = "";
	if (org_arr != "" && org_arr != null && org_arr.length > 0) {
		for (var i=0;i<org_arr.length;i++) {
			// 根据org_no，获取名称
			var org_name = "";
			var jso_org = JSPFree.getHashVOs("select org_nm from rpt_org_info where org_type = '11' and org_no = '"+ org_arr[i] +"'");
			if(jso_org != null && jso_org.length > 0){
				org_name = jso_org[0].org_nm;
			}
			
			var _sql = "select reportdate,tablename,tablename_en,'"+org_name+"' as org_name,'"+org_arr[i]+"' as org_no,count(*) t from pscs_result_data where 1=1 ";
			if (tablename_en != "" && tablename_en != null) {
				_sql +=	" and tablename_en = '" + tablename_en + "'";
			}	
			if (reportdate != "" && reportdate != null) {
				_sql +=	" and reportdate = '" + reportdate + "'";
			}
			var condition = "";
			var jso_rt = JSPFree.doClassMethodCall("com.yusys.pscs.common.PscsOrgQueryCondition","getQueryCondition",{"_loginUserOrgNo" : org_arr[i]});
			if(jso_rt.msg == "ok"){
				condition = jso_rt.condition;
			}
			if (condition != "" && condition != null) {
				_sql +=	" and " + condition;
			}
			
			_sql +=	" group by reportdate,tablename,tablename_en ";
//			_sql +=	"order by reportdate,tablename,tablename_en ";
			_sql +=	" union all ";
			
			str_sql += _sql;
		}
	}
	
	if (str_sql != "" && str_sql.substring(str_sql.length-10, str_sql.length) == 'union all ') {
		str_sql = str_sql.substring(0, str_sql.length-10);
	}
	
	return str_sql;
}