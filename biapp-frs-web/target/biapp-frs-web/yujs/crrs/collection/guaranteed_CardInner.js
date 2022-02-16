//函数
function onBillCardItemEdit(_billCard,_itemkey,_jsoValue){
	if(_itemkey == "collateral_type"){
//		console.log("字段【"  + _itemkey + "】发生变化" + _jsoValue);	
//		var str_value = _jsoValue.value;
//		var jsy_data = JSPFree.getHashVOs("select pled_key id,pled_name name from crrs_bs_pledge_type_code where parent_pled_code= '"+str_value+"' order by pled_code ");
//		JSPFree.setBillCardItemClearValue(_billCard,"collateral_code");
//		JSPFree.setBillCardItemClearValue(_billCard,"collateral_name");
//		JSPFree.setBillCardItemClearValue(_billCard,"collateral_code_tmp");
//		JSPFree.setBillCardComboBoxData2(_billCard,"collateral_code_tmp",jsy_data);
	}
	else if (_itemkey == "collateral_code_tmp") {
		JSPFree.setBillCardValues(_billCard,{"collateral_code":_jsoValue.id})
		JSPFree.setBillCardValues(_billCard,{"collateral_name":_jsoValue.name})
	}
}

//function afterUpdate(_billCard){
//	var str_code = JSPFree.getBillCardItemValue(_billCard,"collateral_code");
//	if(str_code != null && str_code != ""){
//		var str_name = JSPFree.getBillCardItemValue(_billCard,"collateral_name");
//		var collateral_type = JSPFree.getBillCardItemValue(_billCard,"collateral_type");
//		
//		var jsy_data = JSPFree.getHashVOs("select pled_key id,pled_name name from crrs_bs_pledge_type_code where parent_pled_code= '"+collateral_type+"' order by pled_code ");
//		JSPFree.setBillCardComboBoxData2(_billCard,"collateral_code_tmp",jsy_data);
//		JSPFree.setBillCardItemValue(_billCard,"collateral_code_tmp",str_code);
//	}
//}