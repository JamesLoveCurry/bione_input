function AfterInit() {
	JSPFree.createBillCard("d1", "/biapp-cams/freexml/cams/datagram/cams_datagram_task_create.xml", ["生成/onConfirm","取消/onCancel"]);
}

function onConfirm(){
	var jso_cardData = JSPFree.getBillCardFormValue(d1_BillCard);
	var data_dt = jso_cardData.data_dt;
	var due_diligence_ind = jso_cardData.due_diligence_ind;
	var report_size = 2000;//jso_cardData.report_size;
	var report_type = "CRS701";//新数据
	var rtype = "1";//正常报文
	
	if(!data_dt){
		JSPFree.alert("报文日期不能为空!");
		return;
	}
	
	var dataYear = data_dt.slice(0, 4);
	var yearEnd = dataYear + "-12-31";
	if(data_dt != yearEnd){
		JSPFree.alert("报文日期只能选择年底！");
		return;
	}
	
	if(!due_diligence_ind){
		JSPFree.alert("账户类型不能为空!");
		return;
	}
	
	$.ajax({
		cache : false,
		async : false,
		url : "cams/datagram/task/startReportTask",
		dataType : 'json',
		data : {
			dataDt : data_dt,
			dueDiligenceInd : due_diligence_ind,
			reportSize : report_size,
			reportType : report_type,
			rtype : rtype
		},
		type : "post",
		success : function(result) {
			if (result && result.state) {
				if(result.warning){
					window.parent.onRefresh(result.warning);
				}else{
					window.parent.onRefresh(result.msg);
				}
			}
			onCancel();
		},
		error : function() {
			JSPFree.alert("报文生成错误");
		}
	});
}

function onCancel(){
	JSPFree.closeDialog(null);
}