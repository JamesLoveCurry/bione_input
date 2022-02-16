function AfterInit(){
  var str_templetcode = jso_OpenPars._templetcode;  //模板编码

  JSPFree.createBillList("d1",str_templetcode,["确定/onConfirm","取消/onCancel"],{isSwitchQuery:"N"});  //
}

//确定
function onConfirm(){
  var str_ismulti = jso_OpenPars.ismulti;  //是否多选
  if("Y"==str_ismulti) {  //如果在模板定义中设置了ismulti=Y,即多选,则把各列的值拼接
  	var jsy_data = JSPFree.getBillListSelectDatas(d1_BillList);
  	if(jsy_data==null || jsy_data.length<=0){
  		$.messager.alert('提示','必须选择一条数据!','info');
	  	return;
  	}

  	//先计算出所有key
  	var allKeys = [];
  	for(var _key in jsy_data[0]){
      allKeys.push(_key);
  	}
  	//console.log(allKeys);

  	//处理各个列,把各行的数据拼接
  	var jso_spandata = {};
  	for(var i=0;i<allKeys.length;i++){
  		var str_span="";
  		for(var j=0;j<jsy_data.length;j++){
  			var str_value = jsy_data[j][allKeys[i]];
  			if(typeof str_value!="undefined" && str_value!=null){
  				str_span = str_span + str_value + ";"  //拼接
  			}
  		}
  		jso_spandata[allKeys[i]] = str_span;  //
  	}
    JSPFree.closeDialog(jso_spandata);
  } else {  //单选
	  var jso_rowdata = JSPFree.getBillListSelectData(d1_BillList);  //取得选中行的数据!
	  if(jso_rowdata==null){
	  	$.messager.alert('提示','必须选择一条数据!','info');
	  	return;
	  }
	  JSPFree.closeDialog(jso_rowdata);
  }
}

//取消
function onCancel(){
  JSPFree.closeDialog();
}
