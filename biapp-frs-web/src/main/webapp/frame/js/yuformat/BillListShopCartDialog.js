//列表购物车参照,即可以实现翻页多选.
var ary_cartkeys = [];   //购物车的字段
var ary_cartnames =[];  //名称
var ary_cartwidths = [];  //宽度
var iscomma = false; //是否逗号分隔
function AfterInit(){
  var str_templetcode = jso_OpenPars._templetcode;  //模板编码
  ary_cartkeys = jso_OpenPars.cartkeys;  //右边购物车中的列
  ary_cartwidths = jso_OpenPars.cartwidths;
  iscomma = jso_OpenPars.iscomma;
  JSPFree.createSplitByBtn("d1","左右",620,["确定/onConfirm","取消/onCancel"]);

  //创建左边的表
  JSPFree.createBillList("d1_A",str_templetcode,null,{isSwitchQuery:"N"});
  
  //计算出右边表格的列表与宽度
  var jsy_itemVOs = d1_A_BillList.templetVO.templet_option_b;
  for(var i=0;i<ary_cartkeys.length;i++){
  	var itemVO = FreeUtil.findItemVOByItemkey(jsy_itemVOs,ary_cartkeys[i]);
  	if(itemVO==null){
  		ary_cartnames.push(ary_cartkeys[i]);
  	}else{
        ary_cartnames.push(itemVO.itemname);
  	}
  }

  //创建右边的购物车表
  JSPFree.createBillListByItems("d1_B",str_templetcode,ary_cartkeys,ary_cartnames,ary_cartwidths);
  
  //双击左边表格数据,选择到右边!!
  JSPFree.addBillListDoubleClick(d1_A_BillList,function(_rowIndex,_rowData){
  	var str_key = _rowData[ary_cartkeys[0]];
  	var isHave = isAlreadyHaveData(str_key);
  	if(isHave){
  		FreeUtil.alert(ary_cartnames[0] + "为【"+str_key + "】数据已经有了!");
  		return;
  	}

    var jso_newdata = {};
    var li_now = Date.now(); 
	jso_newdata["_rownum"]=""+li_now;
	for(var i=0;i<ary_cartkeys.length;i++){
		jso_newdata[ary_cartkeys[i]] = _rowData[ary_cartkeys[i]];
	}
	d1_B_BillList.datagrid('appendRow',jso_newdata);
  });


  //双击左边表格数据,选择到右边!!
  JSPFree.addBillListDoubleClick(d1_B_BillList,function(_rowIndex,_rowData){
	d1_B_BillList.datagrid('deleteRow',_rowIndex);
  });

}

//检查是否在右边购物车中已有!
function isAlreadyHaveData(_value){
    var jsy_datas = JSPFree.getBillListAllDatas(d1_B_BillList);
    for(var i=0;i<jsy_datas.length;i++){
    	if(jsy_datas[i][ary_cartkeys[0]]==_value){
    		return true;
    	}
    }
	return false;
}

//确定
function onConfirm(){
	//购物车的所有数据
	var jsy_datas = JSPFree.getBillListAllDatas(d1_B_BillList);
	var jso_spandata = {};
	//处理各个列,把各行的数据拼接
	for(var i=0;i<ary_cartkeys.length;i++){
		var str_span="";
		for(var j=0;j<jsy_datas.length;j++){
			var str_value = jsy_datas[j][ary_cartkeys[i]];
			if(typeof str_value!="undefined" && str_value!=null){
		  if(j != jsy_datas.length-1){
			// 如果逗号拼接则用逗号拼接
			if (iscomma) {
				str_span = str_span + str_value + ","  //拼接
			} else {
				str_span = str_span + str_value + ";"  //拼接
			}
		  }
		  else{
			str_span = str_span + str_value;  //拼接
		  }
			}
		}
		jso_spandata[ary_cartkeys[i]] = str_span;  //
	}
	JSPFree.closeDialog(jso_spandata);
}

//取消
function onCancel(){
  JSPFree.closeDialog();
}
