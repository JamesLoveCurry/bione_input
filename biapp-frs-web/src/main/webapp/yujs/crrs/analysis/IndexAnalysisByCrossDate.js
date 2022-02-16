//初始化界面,菜单配置路径是【/frs/yufreejs?js=/yujs/crrs/xxx/xxx.js】
function AfterInit(){
	JSPFree.createTabbByBtn("d1",  ["报表记录数统计", "报表指标项统计"], ["导出/onExportAll"], false);
	
	JSPFree.createBillList("d1_1","/biapp-crrs/freexml/crrs/summary/crrs_index_analyse_bydate_CODE1.xml",null,{autoquery:"N",isSwitchQuery:"N"});
	d1_1_BillList.pagerType="2"; //第二种分页类型
	var _sql = getSql();
	JSPFree.queryDataBySQL(d1_1_BillList, _sql);
	JSPFree.billListBindCustQueryEvent(d1_1_BillList, onIndexAnalyse1);
	
	JSPFree.createBillList("d1_2","/biapp-crrs/freexml/crrs/summary/crrs_index_analyse_bydate_CODE2.xml",null,{autoquery:"N",isSwitchQuery:"N"});
	d1_2_BillList.pagerType="2"; //第二种分页类型
	var _sql1 = getSql1();
	JSPFree.queryDataBySQL(d1_2_BillList, _sql1);
	JSPFree.billListBindCustQueryEvent(d1_2_BillList, onIndexAnalyse2);
}

function getSql() {	
	// 获取最新一期的日期
	var str_data_dt = ""; //20200101
	var jso_data = JSPFree.getHashVOs("SELECT max(data_dt) data_dt FROM crrs_analysis_data ");
	if(jso_data != null && jso_data.length > 0){
		str_data_dt = jso_data[0].data_dt;
	}
	
	FreeUtil.loadBillQueryData(d1_1_BillList, {data_dt:str_data_dt});
	
	//获取上月数据日期
	var jso_date = JSPFree.doClassMethodCall("com.yusys.crrs.monitor.service.CrrsAnalyseBSDMO", 
			"getLastMonthDate",{dataDt:str_data_dt});
	var lastMonthDate = jso_date.lastMonth;
	
	var _sql = "select ftmp.index_name,ftmp.index_position,ftmp.index_type,ftmp.index_range,ftmp.same_month_data,ftmp.data_dt,ftmp.last_month_data ," +
			" case when last_month_data = 0 then '上月数为0，请核实' else to_char(round((same_month_data-last_month_data)*100/last_month_data,2),'fm999999999999990.00')||'%' end as change_rate " +
			" from (select tmp.index_name,tmp.index_position,tmp.index_type,'['||nvl(tmp.index_down,0)||'%, '||nvl(tmp.index_up,0)||'%]' index_range ,tmp.same_month_data,tmp.data_dt,(select nvl(sum(ad.index_value),0) " +
			" from crrs_analysis_data ad where ad.index_name=tmp.index_name and ad.data_dt=tmp.last_data_dt) last_month_data from (select c.index_name,c.order_no,c.index_position,c.index_type,c.index_up,c.index_down,sum(d.index_value) " +
			" as same_month_data,d.data_dt,(select '"+lastMonthDate+"' from dual)as last_data_dt from " +
			" crrs_analysis_cfg c left join crrs_analysis_data d on c.index_name=d.index_name group by c.index_name,c.order_no,c.index_position,c.index_type,c.index_up,c.index_down,d.data_dt order by c.order_no) tmp " +
			" where 1=1 and data_dt = '"+str_data_dt+"' and tmp.index_type='报表记录数') ftmp";
	
	return _sql;
}

function getSql1() {	
	// 获取最新一期的日期
	var str_data_dt = ""; //20200101
	var jso_data = JSPFree.getHashVOs("SELECT max(data_dt) data_dt FROM crrs_analysis_data");
	if(jso_data != null && jso_data.length > 0){
		str_data_dt = jso_data[0].data_dt;
	}
	
	FreeUtil.loadBillQueryData(d1_2_BillList, {data_dt:str_data_dt});
	
	//获取上月数据日期
	var jso_date = JSPFree.doClassMethodCall("com.yusys.crrs.monitor.service.CrrsAnalyseBSDMO", 
			"getLastMonthDate",{dataDt:str_data_dt});
	var lastMonthDate = jso_date.lastMonth;
	
	var _sql = "select ftmp.index_name,ftmp.index_position,ftmp.index_type,ftmp.index_range,ftmp.same_month_data,ftmp.data_dt,ftmp.last_month_data ," +
			" case when last_month_data = 0 then '上月数为0，请核实' else to_char(round((same_month_data-last_month_data)*100/last_month_data,2),'fm999999999999990.00')||'%' end as change_rate " +
			" from (select tmp.index_name,tmp.index_position,tmp.index_type,'['||nvl(tmp.index_down,0)||'%, '||nvl(tmp.index_up,0)||'%]' index_range ,tmp.same_month_data,tmp.data_dt,(select nvl(sum(ad.index_value),0) " +
			" from crrs_analysis_data ad where ad.index_name=tmp.index_name and ad.data_dt=tmp.last_data_dt) last_month_data from (select c.index_name,c.order_no,c.index_position,c.index_type,c.index_up,c.index_down,sum(d.index_value) " +
			" as same_month_data,d.data_dt,(select '"+lastMonthDate+"' from dual)as last_data_dt from " +
			" crrs_analysis_cfg c left join crrs_analysis_data d on c.index_name=d.index_name group by c.index_name,c.order_no,c.index_position,c.index_type,c.index_up,c.index_down,d.data_dt order by c.order_no) tmp " +
			" where 1=1 and data_dt = '"+str_data_dt+"' and tmp.index_type='报表指标项') ftmp";
	
	return _sql;
}

function onIndexAnalyse1(_condition){
	var _data_dt = "";
	
	if ("" == _condition || null == _condition) {
		$.messager.alert('提示', '请选择数据日期', 'warning');
		return;
	}

	var jso_rt = JSPFree.doClassMethodCall("com.yusys.crrs.monitor.service.CrrsAnalyseBSDMO", 
			"getAnalyseSql",{condition:_condition, index_type:"报表记录数"});
	if(jso_rt.status == "FAIL"){
		JSPFree.alert(jso_rt.msg);
		return;
	}

	var _sql = jso_rt.sql;
	JSPFree.queryDataBySQL(d1_1_BillList, _sql);
}

function onIndexAnalyse2(_condition){
	var _data_dt = "";
	
	if ("" == _condition || null == _condition) {
		$.messager.alert('提示', '请选择数据日期', 'warning');
		return;
	}
	
	var jso_rt = JSPFree.doClassMethodCall("com.yusys.crrs.monitor.service.CrrsAnalyseBSDMO", 
			"getAnalyseSql",{condition:_condition, index_type:"报表指标项"});
		if(jso_rt.status == "FAIL"){
		JSPFree.alert(jso_rt.msg);
		return;
	}

	var _sql = jso_rt.sql;
	JSPFree.queryDataBySQL(d1_2_BillList, _sql);
}

function onExportAll(_condition){ //导出时，查看当前选中的页签，然后依据当前选中页签的数据日期进行导出
	var itemTab = d1_tabb.tabs('getSelected'); // 选中的页签
	var index = d1_tabb.tabs('getTabIndex', itemTab); // 选中的页签位置,第一个是0
	
	var _data_dt = "";
	if(index==0){ //第一个页签
		_data_dt = JSPFree.getBillQueryFormValue(d1_1_BillQuery).data_dt;
	} else { //第二个页签
		_data_dt = JSPFree.getBillQueryFormValue(d1_2_BillQuery).data_dt;
	}
	
	if (_data_dt == "") {
		$.messager.alert('提示', '请选择数据日期', 'warning');
		return;
	}
	
	//获取上月数据日期
	var jso_date = JSPFree.doClassMethodCall("com.yusys.crrs.monitor.service.CrrsAnalyseBSDMO", 
			"getLastMonthDate",{dataDt:_data_dt});
	var lastMonthDate = jso_date.lastMonth;
	
	var d1_sql = "select ftmp.data_dt,ftmp.index_name,ftmp.index_position,ftmp.last_month_data,ftmp.same_month_data," +
	" case when last_month_data = 0 then '上月数为0，请核实' else to_char(round((same_month_data-last_month_data)*100/last_month_data,2),'fm999999999999990.00')||'%' end as change_rate, ftmp.index_range " +
	" from (select tmp.index_name,tmp.index_position,tmp.index_type,'['||nvl(tmp.index_down,0)||'%, '||nvl(tmp.index_up,0)||'%]' index_range ,tmp.same_month_data,tmp.data_dt,(select nvl(sum(ad.index_value),0) " +
	" from crrs_analysis_data ad where ad.index_name=tmp.index_name and ad.data_dt=tmp.last_data_dt) last_month_data from (select c.index_name,c.order_no,c.index_position,c.index_type,c.index_up,c.index_down,sum(d.index_value) " +
	" as same_month_data,d.data_dt,(select '"+lastMonthDate+"' from dual)as last_data_dt from " +
	" crrs_analysis_cfg c left join crrs_analysis_data d on c.index_name=d.index_name group by c.index_name,c.order_no,c.index_position,c.index_type,c.index_up,c.index_down,d.data_dt order by c.order_no) tmp " +
	" where 1=1 and data_dt = '"+_data_dt+"' and tmp.index_type='报表记录数') ftmp";
	
	var d2_sql = "select ftmp.data_dt,ftmp.index_name,ftmp.index_position,ftmp.last_month_data,ftmp.same_month_data," +
	" case when last_month_data = 0 then '上月数为0，请核实' else to_char(round((same_month_data-last_month_data)*100/last_month_data,2),'fm999999999999990.00')||'%' end as change_rate, ftmp.index_range " +
	" from (select tmp.index_name,tmp.index_position,tmp.index_type,'['||nvl(tmp.index_down,0)||'%, '||nvl(tmp.index_up,0)||'%]' index_range ,tmp.same_month_data,tmp.data_dt,(select nvl(sum(ad.index_value),0) " +
	" from crrs_analysis_data ad where ad.index_name=tmp.index_name and ad.data_dt=tmp.last_data_dt) last_month_data from (select c.index_name,c.order_no,c.index_position,c.index_type,c.index_up,c.index_down,sum(d.index_value) " +
	" as same_month_data,d.data_dt,(select '"+lastMonthDate+"' from dual)as last_data_dt from " +
	" crrs_analysis_cfg c left join crrs_analysis_data d on c.index_name=d.index_name group by c.index_name,c.order_no,c.index_position,c.index_type,c.index_up,c.index_down,d.data_dt order by c.order_no) tmp " +
	" where 1=1 and data_dt = '"+_data_dt+"' and tmp.index_type='报表指标项') ftmp";

  JSPFree.downloadExcelBySQL("报送统计-"+_data_dt+".xls", d1_sql+";"+d2_sql, "报表记录数统计,报表指标项统计","数据日期,报表名称,报表序号,上月条数,本月条数,环比,正常阈值;数据日期,指标名称,指标位置,上月额度,本月额度,环比,正常阈值");
}