var tablename = "";
var tablename_en = "";
var pkvalue = "";
var name = "";
function AfterInit(){
	tablename = jso_OpenPars2.tablename;
	tablename_en = jso_OpenPars2.tablename_en;
	pkvalue = jso_OpenPars2.pkvalue;
	name = jso_OpenPars2.name;

	var str_sqlCons = "result_status in ('已分发') and tablename_en = '"+ tablename_en +"' and pkvalue = '"+ pkvalue +"'";  //过滤条件
	
	JSPFree.createBillList("d1","/biapp-crrs/freexml/crrs/rule/"+name+"_Submit.xml",["关闭/onCancel/icon-p71","下一步/onNext/icon-p72"],{list_btns:"",querycontion:str_sqlCons,list_ischeckstyle:"N",list_ismultisel:"N",ishavebillquery:"N",list_ispagebar:"N"});

	JSPFree.queryDataByConditon2(d1_BillList,null);
}

/**
 * 下一步
 * @returns
 */
function onNext(){
	var jsy_rids = []; 
	var jsy_colnames = []; 
	var d1_BillList_array = d1_BillList.datagrid("getData").rows;
	if (d1_BillList_array.length == 0) {
		JSPFree.alert("数据错误，不能点击下一步");
		return;
	}
	for(var i=0;i<d1_BillList_array.length;i++){
		var rid = d1_BillList_array[i]["rid"];
		var colname = d1_BillList_array[i]["colname"];
		jsy_rids.push(rid);
		jsy_colnames.push(colname);
	}

	var arr = newArr(jsy_colnames);
	var _TheArray = new Array();
	for(var k=0;k<arr.length;k++) {
		var _TheArray_child = new Array();
		var aa = arr[k];
		var problemmsg = "";
		for(var i=0;i<d1_BillList_array.length;i++){
			if (aa == d1_BillList_array[i]["colname"]) {
				problemmsg = problemmsg + "\r\n" + "> " + d1_BillList_array[i]["problemmsg"];
			}
		}
		_TheArray_child.push(aa, problemmsg);
		_TheArray.push(_TheArray_child);
	}
	
	jso_OpenPars.pkvalue = pkvalue;
	jso_OpenPars.rids = jsy_rids;
	jso_OpenPars.colnames = jsy_colnames;
	jso_OpenPars.tablename_en = tablename_en;
	jso_OpenPars.name = name;
	jso_OpenPars.array = _TheArray;
	
	// 根据tabname获取到表中文名称
	JSPFree.openDialog(tablename,"/yujs/crrs/datahandle/ValidateResultHandleData.js", 900, 560, jso_OpenPars,function(_rtdata){JSPFree.refreshBillListCurrPage(d1_2_BillList);});
}

/**
 * 数组数据去重
 * @returns
 */
function newArr(arr){
    for(var i=0;i<arr.length;i++){
        for(var j=i+1;j<arr.length;j++) {
            if(arr[i]==arr[j]){
	            arr.splice(j,1);
	            j--;
            }
        }
    }
    return arr;
}

/**
 * 上一步（取消）
 * @returns
 */
function onCancel(){
	JSPFree.closeDialog();
}