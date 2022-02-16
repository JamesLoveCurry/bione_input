function onBillCardItemEdit(_billCard,_itemKey,_value){
	if(_itemKey=="staff_code"){//选择客户经理编号，显示经办机构
		var staff_code  = _value.staff_code;
		var jso_data = JSPFree.getHashVOs("SELECT a.nbjgh inst_no, c.org_nm inst_name  FROM crrs_bs_staff a left join rpt_org_info c on a.nbjgh = c.org_no where c.org_type = '08' and a.gyh ='" + staff_code + "'");
		if(jso_data.length > 0){
			JSPFree.setBillCardItemClearValue(_billCard,"inst_no");
			JSPFree.setBillCardItemValue(_billCard,"inst_no",jso_data[0].inst_no);
			JSPFree.setBillCardItemValue(_billCard,"inst_name",jso_data[0].inst_name);
		}
	}
}

//新增时的逻辑
function afterInsert(_billCard){
	var jso_cardData = JSPFree.getBillCardFormValue(_billCard);
	JSPFree.setBillCardItemEditable(_billCard,"inst_no",false);
	var str_dataType = jso_cardData["staff_code"];
	//选择客户经理编号，显示经办机构
	var staff_code  = jso_cardData["staff_code"];
	var jso_data = JSPFree.getHashVOs("SELECT a.nbjgh inst_no, c.org_nm inst_name  FROM crrs_bs_staff a left join rpt_org_info c on a.nbjgh = c.org_no where c.org_type = '08' and a.gyh ='" + staff_code + "'");
	if(jso_data.length > 0){
		JSPFree.setBillCardItemClearValue(_billCard,"inst_no");
		JSPFree.setBillCardItemValue(_billCard,"inst_no",jso_data[0].inst_no);
		JSPFree.setBillCardItemValue(_billCard,"inst_name",jso_data[0].inst_name);
	}
}

//修改时的逻辑
function afterUpdate(_billCard){
	var jso_cardData = JSPFree.getBillCardFormValue(_billCard);
	JSPFree.setBillCardItemEditable(_billCard,"inst_no",false);
	var str_dataType = jso_cardData["staff_code"];
	//选择客户经理编号，显示经办机构
	var staff_code  = jso_cardData["staff_code"];
	var jso_data = JSPFree.getHashVOs("SELECT a.nbjgh inst_no, c.org_nm inst_name  FROM crrs_bs_staff a left join rpt_org_info c on a.nbjgh = c.org_no where c.org_type = '08' and a.gyh ='" + staff_code + "'");
	if(jso_data.length > 0){
		JSPFree.setBillCardItemClearValue(_billCard,"inst_no");
		JSPFree.setBillCardItemValue(_billCard,"inst_no",jso_data[0].inst_no);
		JSPFree.setBillCardItemValue(_billCard,"inst_name",jso_data[0].inst_name);
	}
}
