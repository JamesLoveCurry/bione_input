//初始化界面
function AfterInit() {
	
	JSPFree.createBillCard("d1", "/biapp-east/freexml/triggerconf.xml",["确定/onConfirm/icon-p12","取消/onCancel/icon-undo"]); // 页签先左右分割
	var dom_root = document.getElementById("d1_form");
	var str_html = "<div class='easyui-layout'><a id='circle' href='#' style='margin-left:5px;width:60px;' class='easyui-linkbutton' >循环</a>"+
				   "<a id='dayBtn' href='#' style='margin-left:5px;width:60px;' class='easyui-linkbutton' >每天</a>"+
				   "<a id='weekBtn' href='#' style='margin-left:5px;width:60px;' class='easyui-linkbutton' >每周</a>"+
				   "<a id='monthBtn' href='#' style='margin-left:5px;width:60px;' class='easyui-linkbutton' >每月</a>"+
				   "<a id='yearBtn' href='#' style='margin-left:5px;width:60px;' class='easyui-linkbutton' >每年</a>"+
				   "<a id='quarterBtn' href='#' style='margin-left:5px;width:60px;' class='easyui-linkbutton' >每季</a></div>";
	dom_root.innerHTML=str_html+dom_root.innerHTML;

	$("#circle").click(function(){
		JSPFree.setBillCardValues(d1_BillCard,{triggerconf:"5"});
	});

	$("#dayBtn").click(function(){
		JSPFree.setBillCardValues(d1_BillCard,{triggerconf:"每天;13:50;"});
	});

	$("#weekBtn").click(function(){
		JSPFree.setBillCardValues(d1_BillCard,{triggerconf:"每周;3;13:50;"});
	});

	$("#monthBtn").click(function(){
		JSPFree.setBillCardValues(d1_BillCard,{triggerconf:"每月;360;13:50;"});
	});

	$("#yearBtn").click(function(){
		JSPFree.setBillCardValues(d1_BillCard,{triggerconf:"每年;360;13:50;"});
	});

	$("#quarterBtn").click(function(){
		JSPFree.setBillCardValues(d1_BillCard,{triggerconf:"每季;1;2;13:50"});
	});
}

function onConfirm(){
	var billCard = JSPFree.getBillCardFormValue(d1_BillCard);
	var triggerconf = "";
	for(var obj in billCard){
		triggerconf +=  billCard[obj];
	}
	JSPFree.closeDialog({triggerconf:triggerconf});
}

function onCancel(){
	JSPFree.closeDialog();
}