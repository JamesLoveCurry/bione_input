//初始化界面,菜单配置路径是【/frs/yufreejs?js=/yujs/crrs/collection/addSingle.js】
function AfterInit(){
	JSPFree.createTabb("d1",["账户信息","姓名信息","地址信息","TIN信息"]);

	JSPFree.createBillCard("d1_1","/biapp-cams/freexml/cams/cams_indv_acct_CODE1.xml",["保存/onSave/icon-ok"],null);
	document.getElementById("d1_2").innerHTML="<div style=\"width:100%;text-align:center;\">请先维护个人账户信息</div>";
	document.getElementById("d1_3").innerHTML="<div style=\"width:100%;text-align:center;\">请先维护个人账户信息</div>";
	document.getElementById("d1_4").innerHTML="<div style=\"width:100%;text-align:center;\">请先维护个人账户信息</div>";
}

/**
 * 保存
 * @return {[type]} [description]
 */
function onSave(){
	JSPFree.setBillCardValues(d1_1_BillCard,{rid:FreeUtil.getUUIDFromServer(),status:"1",org_no:str_LoginUserOrgNo,user_name:str_LoginUserName,create_time:curentTime()}); // 初始化状态
	var flag = JSPFree.doBillCardInsert(d1_1_BillCard,null);
	if(flag){
		JSPFree.alert("校验并保存成功");
		var indv = JSPFree.getBillCardFormValue(d1_1_BillCard);
		JSPFree.openDialogAndCloseMe2("账户信息","/yujs/cams/collection/indv/editIndv.js",900,560,{indv:indv},true);
	}
	
//	JSPFree.setBillCardValues(d1_1_BillCard,{rid:FreeUtil.getUUIDFromServer()});
//	
//	// 单条数据校验
//	var backValue = JSPFree.editTableCheckData(d1_1_BillCard, "Add", "个人账户信息", "CAMS_INDV_ACCT","","4");
//	if (backValue == "" || "undifind" == backValue) {
//		return;
//	} else if (backValue == "OK") {
//		// 提示验证通过，并进行保存
//		var flag = JSPFree.doBillCardInsert(d1_1_BillCard,null);
//		if(flag){
//			JSPFree.alert("校验并保存成功");
//			var indv = JSPFree.getBillCardFormValue(d1_1_BillCard);
//			JSPFree.openDialogAndCloseMe2("基础信息","/yujs/cams/collection/indv/editIndv.js",900,560,{indv:indv},true);
//		}
//	} else if (backValue == "Fail") {
//		return;
//	}
}

function curentTime() { 
    var now = new Date();
    
    var year = now.getFullYear();       //年
    var month = now.getMonth() + 1;     //月
    var day = now.getDate();            //日
    
    var hh = now.getHours();            //时
    var mm = now.getMinutes();          //分
    var ss = now.getSeconds();           //秒
    
    var clock = year + "-";
    
    if(month < 10)
        clock += "0";
    
    clock += month + "-";
    
    if(day < 10)
        clock += "0";
        
    clock += day + " ";
    
    if(hh < 10)
        clock += "0";
        
    clock += hh + ":";
    if (mm < 10) clock += '0'; 
    clock += mm + ":"; 
     
    if (ss < 10) clock += '0'; 
    clock += ss; 
    
    return(clock); 
}