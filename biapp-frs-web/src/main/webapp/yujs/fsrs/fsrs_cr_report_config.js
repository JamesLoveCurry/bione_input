//初始化界面,菜单配置路径是【/frs/yufreejs?js=/yujs/east/east_cr_report_config.js】
function AfterInit(){
	JSPFree.createBillList("d1","/biapp-fsrs/freexml/fsrs/fsrs_cr_report_config_CODE1.xml",null,{isSwitchQuery:"N"});
}

function onBatchInsert(){
	JSPFree.openDialog("批量新增","/yujs/fsrs/fsrs_cr_report_config_inserts.js",580,500,null,function(_rtData){
		if (_rtData != null) {
			if ("dirclose" == _rtData.type) {
				return;
			}
			var str_sql = _rtData.whereSQL;  //返回的主键拼成的SQL
		       JSPFree.queryDataByConditon(d1_BillList,str_sql);
		       JSPFree.alert("批量新增数据成功!<br>当前页面数据是查询的新增数据!");
		}
	});
}