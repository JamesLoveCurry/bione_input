//业务数据查询【/frs/yufreejs?js=/yujs/east/busidataquery.js】
function AfterInit(){
	JSPFree.createSplit("d1","左右",240);
	JSPFree.createBillTree("d1_A","/biapp-pscs/freexml/pscs/datamaintain/pscs_cr_tab_ref.xml"); //
	document.getElementById("d1_B").innerHTML="<div style='width:100%;text-align:center'><span style='font-size:16px;color:blue'>请选择左边表查询数据!</span></div>";
	
	JSPFree.bindBillTreeOnSelect(d1_A_BillTree,function(_node){
	  var isLeaf_1 = JSPFree.isBillTreeSelectNodeLeaf(d1_A_BillTree);  //11张表是否选中的是叶子结点
	  if(isLeaf_1){  //如果选中了叶子结点
	  		var jso_data = JSPFree.getBillTreeSelectData(d1_A_BillTree);  //取得选中的数据
	  		var str_tab_name = jso_data.tab_name;
	  		var str_tab_name_en = jso_data.tab_name_en;
	  		var str_ds = jso_data.ds_name;
	  		
	  		// 判断选中的叶子节点，是否含有字段信息
	  		var jso_par = { str_tab_name:str_tab_name };
	  		var jso_data = JSPFree.doClassMethodCall("com.yusys.pscs.datamain.PscsCrReportBS","checkTabCols",jso_par); 
	  		var str_html = jso_data.msg;  //返回的html
	  		if (str_html != null && str_html != "") {
	  			document.getElementById("d1_B").innerHTML="<div style='width:100%;text-align:center'><span style='font-size:16px;color:red'>"+jso_data.msg+"!</span></div>";
	  			
	  			return;
	  		}
		  	  
	  		var v_par = {"tab_name":str_tab_name,"tab_name_en":str_tab_name_en,"ds":str_ds};
	  		JSPFree.createOrReplaceIFrame("d1_B","/yujs/pscs/datamaintain/busi11.js",v_par);
	  }
	});
}

