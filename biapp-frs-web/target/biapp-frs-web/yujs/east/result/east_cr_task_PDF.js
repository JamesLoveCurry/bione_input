//初始化界面,菜单配置路径是【/frs/yufreejs?js=/yujs/east/dataqualitypoc.js】
function AfterInit(){
	JSPFree.createBillList("d1","/biapp-east/freexml/east/result/east_data_quality_CODE.xml",null,{isSwitchQuery:"N"});
}

//点击查看数据质量报告
function onLookReport(_btn){
	var jso_OpenPars = {rid:_btn.textContent};
	JSPFree.openDialog("数据质量报告","/yujs/east/result/dataquanlityreport.js", 960, 560, jso_OpenPars,function(_rtdata){
		
	});
}

//导出word
function onExportWord(){
  var jso_data = JSPFree.getBillListSelectData(d1_BillList);
  if(jso_data==null){
  	JSPFree.alert("必须选择一条记录!");
  	return;
  }

  JSPFree.confirm("提示","您真的要导出质量报告吗?<br>这将是一个耗时操作,请谨慎操作!",function(_rt){
  	if(_rt){
  	  var jso_par = {date:jso_data.data_dt};  //
  	  JSPFree.downloadFile("com.yusys.east.checkresult.query.service.ExportWorfDMO","数据质量报告.doc",jso_par);
  	}
  });
}

//导出PDF
function onExportPDF(){
 var jso_data = JSPFree.getBillListSelectData(d1_BillList);
  if(jso_data==null){
  	JSPFree.alert("必须选择一条记录!");
  	return;
  }

  JSPFree.confirm("提示","您真的要导出质量报告吗?<br>这将是一个耗时操作,请谨慎操作!",function(_rt){
  	if(_rt){
  	  var jso_par = {date:jso_data.data_dt};  //
  	  JSPFree.downloadFile("com.yusys.east.checkresult.query.service.ExportPDFDMO","数据质量报告.pdf",jso_par);
  	}
  });
}