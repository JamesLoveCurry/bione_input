//插入POC假设数据(单条)
var jso_templetVO = null;
var jso_rowData = null;

function AfterInit(){
	jso_templetVO = jso_OpenPars2.templetVO;  //模板VO
	jso_rowData = jso_OpenPars2.rowData;

	var str_templetname =  jso_templetVO.templet_option.templetname;  //
	var str_table = jso_templetVO.templet_option.fromtable;  //


	JSPFree.createSplit("d1","左右",350);  //
	JSPFree.createBillList("d1_A","/biapp-crrs/freexml/crrs/rule/crrs_result_sure_POCDemo_cols.xml");
	JSPFree.createBillCard("d1_B","/biapp-crrs/freexml/crrs/rule/crrs_result_sure_POCDemo.xml",["新增校验失败/onSave"]);

	//设置值
	JSPFree.execBillCardDefaultValueFormula(d1_B_BillCard);
	
	JSPFree.setBillCardItemValue(d1_B_BillCard,"customercode",jso_rowData["customer_code"]);
	JSPFree.setBillCardItemValue(d1_B_BillCard,"customername",jso_rowData["customer_name"]);
	JSPFree.setBillCardItemValue(d1_B_BillCard,"tablename",str_templetname);
	JSPFree.setBillCardItemValue(d1_B_BillCard,"tablename_en",str_table);
	
	JSPFree.setBillCardItemValue(d1_B_BillCard,"pkcolname","rid");
	JSPFree.setBillCardItemValue(d1_B_BillCard,"pkvalue",jso_rowData["rid"]);

	//设置柜员号
	JSPFree.setBillCardItemValue(d1_B_BillCard,"staff_code",getStaff_code());
	
	//设置表格中的值
	var itemVOs = jso_templetVO.templet_option_b;
	var jsy_data = [];  //存储各字段的值
	for(var i=0;i<itemVOs.length;i++){
		var str_colname = itemVOs[i].itemkey;  //字段名
		var str_colname_cn = itemVOs[i].itemname;  //字段显示名(中文名)
		var str_card_isshow = itemVOs[i].card_isshow;  //卡片是否显示

		var str_colvalue = jso_rowData[str_colname];  //字段值

		if(str_colname=="rid" || str_colname=="customer_code" || str_colname=="customer_name"){
			continue;
		}

		//卡片不显示的，也不出来,曾经搞了一个，结果有记录但卡片不显示了!
		if("N"==str_card_isshow){
			continue;
		}

		jsy_data.push({colname:str_colname,colname_cn:str_colname_cn,colvalue:str_colvalue});
	}
	
	JSPFree.setBillListDatas(d1_A_BillList,jsy_data);  //设置列表
	JSPFree.bindSelectEvent(d1_A_BillList,onSelectListData);  //
}

//根据登录人员计算出柜员号
function getStaff_code(){
	var jsy_data = JSPFree.getHashVOs("select user_agname from bione_user_info where user_no='" + self["str_LoginUserCode"] + "'");
	if(jsy_data!=null && jsy_data.length>0){
		return jsy_data[0].user_agname;  //
	}else{
		return "";
	}
}

function AfterBodyLoad(){
	
}

function onSelectListData(_rowIndex, _rowData){
	var str_colname = _rowData["colname"]; //
	var str_colvalue = _rowData["colvalue"]; //
	JSPFree.setBillCardItemValue(d1_B_BillCard,"colname",str_colname);
	JSPFree.setBillCardItemValue(d1_B_BillCard,"colvalue",str_colvalue);
}


function onSave(){
	var str_colname = JSPFree.getBillCardItemValue(d1_B_BillCard,"colname"); //
	var str_problemcode = JSPFree.getBillCardItemValue(d1_B_BillCard,"problemcode"); //

	if(str_colname==null || str_colname==""){
		JSPFree.alert("必须选择一个列!!");
		return;
	}

	if(str_problemcode==null || str_problemcode==""){
		JSPFree.alert("必须选择问题编号!!");
		return;
	}

	var isOK = JSPFree.doBillCardInsert(d1_B_BillCard,null); 
	if(isOK){
		JSPFree.alert("新增一条校验失败记录成功!!<br>现在可以在校验结果中查看到该记录!",function(){
			JSPFree.closeDialog();  //直接关闭窗口!
		});
	}
}

