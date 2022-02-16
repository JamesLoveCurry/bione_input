//修改时的逻辑
function afterInsert(_billCard){
	console.log("afterInsertAtXml....");
	//JSPFree.setBillCardItemEditable(_billCard,"datatype",false);
	//JSPFree.setBillCardItemEditable(_billCard,"tab_type",false);
	JSPFree.setBillCardItemColor(_billCard,"templetcode","green");
}

//修改时的逻辑
function afterUpdate(_billCard){
	console.log("afterUpdateAtXml....");
	var jso_cardData = JSPFree.getBillCardFormValue(_billCard);
	var str_dataType = jso_cardData["datatype"];
	console.log("数据类型:" + str_dataType);  //
	if(str_dataType=="状态类"){
		JSPFree.setBillCardItemEditable(_billCard,"datatype",false);
		JSPFree.setBillCardItemVisible(_billCard,"ds",false);
		JSPFree.setBillCardItemColor(_billCard,"templetcode","yellow");
	}else if(str_dataType=="明细类"){
		JSPFree.setBillCardItemEditable(_billCard,"tab_name",false);
		JSPFree.setBillCardItemVisible(_billCard,"tab_name_en",false);
		JSPFree.setBillCardItemColor(_billCard,"templetcode","cyan");
	}
}

//修改时的逻辑
function beforeSave(_billCard){
	var jso_cardData = JSPFree.getBillCardFormValue(_billCard);
	console.log(jso_cardData);
	JSPFree.alert("有个字段不通过!");
	return false;
}

//函数
function onBillCardItemEdit(_billCard,_itemkey,_jsoValue){
	console.log("字段【"  + _itemkey + "】发生变化" + _jsoValue);	
}