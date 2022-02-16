var data_dt;
var tab_name;
var org_no;
var type;
var rid;
function AfterInit() {
	type = jso_OpenPars.type;
	org_no = jso_OpenPars.org_no;
	data_dt = jso_OpenPars.data_dt;
	tab_name = jso_OpenPars.tab_name;
	rid = jso_OpenPars.rid;
	var templet = "/biapp-imas/freexml/fillingProcess/imas_lock_data_lock.xml";
	if(type=='2'){
		templet = "/biapp-imas/freexml/fillingProcess/imas_lock_data_lock1.xml";
	}
    JSPFree.createBillCard("d1",templet,["确定/onConfirm","取消/onCancel"]);
}

function AfterBodyLoad() {
	var dom_div = document.getElementById("d1_BillCardDiv");
	dom_div.style.overflow="hidden"; // 隐藏滚动框
}

//创建任务
function onConfirm(){
    var jso_rt = null;

    var jso_cardData = JSPFree.getBillCardFormValue(d1_BillCard);
    if(jso_cardData.status==null || jso_cardData.status==""){
    	var msg = "加锁类型不能为空!";
    	if(type=='2'){
    		msg = "解锁类型不能为空!";
    	}
        JSPFree.alert(msg);
        return;
    }
    
    if(type=='1'){
    	jso_rt = JSPFree.doClassMethodCall("com.yusys.imas.process.service.ImasLockData", "startLockData", {rids:rid,status:jso_cardData.status});
    	jsoAlert(jso_rt);
    }else{
    	if(jso_cardData.status=='4' || jso_cardData.status=='5'){
    		var jso = JSPFree.doClassMethodCall("com.yusys.imas.process.service.ImasLockData", "checkAuditData", {rids:rid});
        	if(jso.msg=='ok'){
        		jso_rt = JSPFree.doClassMethodCall("com.yusys.imas.process.service.ImasLockData", "unLockData", {rids:rid,status:jso_cardData.status});
        		jsoAlert(jso_rt);
        	}else{
        		// 需要弹窗确认
        		JSPFree.confirm("提示","该表有正在填报过程中的数据，审批通过后会更新源表数据，建议报表填报完成后再对数据维护模块进行解锁，是否继续解锁？",function(_isOK){
        	        if(_isOK){
        	        	jso_rt = JSPFree.doClassMethodCall("com.yusys.imas.process.service.ImasLockData", "unLockData", {rids:rid,status:jso_cardData.status});
        	        	jsoAlert(jso_rt);
        	        }
        	    });
        	}
    	}else{
    		jso_rt = JSPFree.doClassMethodCall("com.yusys.imas.process.service.ImasLockData", "unLockData", {rids:rid,status:jso_cardData.status});
    		jsoAlert(jso_rt);
    	}
    }
}

function jsoAlert(jso_rt){
	if(jso_rt.status=='OK'){
		JSPFree.closeDialog(jso_rt);
	}else if(jso_rt.status=='FAIL'){
		JSPFree.closeDialog(jso_rt);
	}else{
		var msg = "维护未加锁";
		if(jso_rt.msg=='noSub'){
			msg = "填报未加锁";
		}
		JSPFree.alert(msg);
	}
}

function onCancel() {
	JSPFree.closeDialog();
}