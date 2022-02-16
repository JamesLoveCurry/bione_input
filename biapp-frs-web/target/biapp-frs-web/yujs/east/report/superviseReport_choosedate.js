//初始化界面,菜单配置路径是【/frs/yufreejs?js=/yujs/east/58table.js】
var str_org_no="";
var create_type="";
var addr = "";
var str_date = "";
var str_org_class = "";
var org_nm = "";
var rids = "";
function AfterInit(){
	str_org_no = jso_OpenPars.org_no;
	create_type = jso_OpenPars.create_type;
	addr = 	jso_OpenPars.addr;
	str_date = jso_OpenPars.data_dt;
	str_org_class = jso_OpenPars.org_class;
	org_nm = jso_OpenPars.org_nm;
	rids = jso_OpenPars.rids;
	JSPFree.createBillCard("d1","/biapp-east/freexml/east/report/supervise_report_download.xml",["下一步/onNext","取消/onCancel"]);
}

function AfterBodyLoad(){
	FreeUtil.addBillCardItemEditListener(d1_BillCard,onEditChanged);
	JSPFree.setBillCardItemVisible(d1_BillCard, "addr", false);
	JSPFree.setBillCardItemVisible(d1_BillCard, "org_no", false);
}

function onNext(){
	var jso_cardData = JSPFree.getBillCardFormValue(d1_BillCard);
	var str_date = jso_cardData.data_dt;
	var create_type = jso_cardData.create_type;
	var str_org_no = jso_cardData.org_no;
	var addr = jso_cardData.addr;
	if(str_date==null || str_date==""){
		JSPFree.alert("采集日期不能为空!");
		return;
	}
	if(create_type==null || create_type==""){
		JSPFree.alert("生成类型不能为空!");
		return;
	}else{
		if(create_type=='1'){
			if(str_org_no==null || str_org_no==""){
				JSPFree.alert("报送机构不能为空!");
				return;
			}
		}else if(create_type=='2'){
			if(addr==null || addr==""){
				JSPFree.alert("归属地不能为空!");
				return;
			}
		}
	}


	JSPFree.openDialogAndCloseMe("一键压缩打包下载【" + str_date + "】的所有报文","/yujs/east/report/superviseReport_zip.js",780,300,{data_dt:str_date,org_no:str_org_no,create_type:create_type,addr:addr});
}

function onCancel(){
	JSPFree.closeDialog();
}

function onEditChanged(_billCard, _itemkey, _jsoValue) {
	if (_itemkey == "create_type") {
		var str_value = _jsoValue;
		if (str_value.value == "1") {
			JSPFree.setBillCardItemVisible(_billCard, "addr", false);
			JSPFree.setBillCardItemVisible(_billCard, "org_no", true);
			JSPFree.setBillCardItemVisible(_billCard, "org_name", true);
			JSPFree.setBillCardItemIsMust(_billCard,"org_no",true);
			JSPFree.setBillCardItemIsMust(_billCard,"org_name",true);
			JSPFree.setBillCardItemIsMust(_billCard,"addr",false);
		}else if(str_value.value == "2"){
			JSPFree.setBillCardItemVisible(_billCard, "addr", true);
			JSPFree.setBillCardItemIsMust(_billCard,"addr",true);
			JSPFree.setBillCardItemVisible(_billCard, "org_no", false);
			JSPFree.setBillCardItemVisible(_billCard, "org_name", false);
			JSPFree.setBillCardItemIsMust(_billCard,"org_no",false);
			JSPFree.setBillCardItemIsMust(_billCard,"org_name",false);
		}
	}
}
