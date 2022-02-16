/**
 * 
 * <pre>
 * Title: 【报送管理】-【报文生成】
 * Description: 报送管理-报文生成-打包压缩下载：选择日期，机构和报送频率的页面
 * 此页面提供了报文下载的日期和机构选择操作，可以允许选择日期，机构和报送频率，打包压缩下载对应的报文
 * </pre>
 * @author liangzy5 
 * @version 1.00.00
   @date 2020年8月25日
 */
var _str_date = "";
var _str_org_no = ""; //970110,990100
var _str_report_type = "";
var org_class = "";
var org_nm = "";

function AfterInit() {
	if(jso_OpenPars!=null){ //如果是主界面过来的，参数是null，如果是从压缩界面回退回来的，则把日期和报送频率赋值
		_str_org_no = jso_OpenPars.org_no;
		org_class = jso_OpenPars.org_class;
		org_nm = jso_OpenPars.org_nm;
		
		_str_date = jso_OpenPars.data_dt;
		_str_report_type = jso_OpenPars.report_type;
	}
	
	JSPFree.createBillCard("d1","/biapp-bfd/freexml/report/bfd_report_choose.xml",["下一步/onNext","取消/onCancel"]);
}

/**
 * 页面初始化后，隐藏滚动框
 * @returns
 */
function AfterBodyLoad() {
	var dom_div = document.getElementById("d1_BillCardDiv");
	dom_div.style.overflow="hidden";  // 隐藏滚动框
	
	if(BfdFreeUtil.getBfdOrgClass().fh==org_class){
		// 如果是分行，则机构赋默认值并且置灰
		JSPFree.setBillCardItemValue(d1_BillCard, "org_no", _str_org_no+"/"+org_nm); //获取机构名称
		JSPFree.setBillCardItemEditable(d1_BillCard, "org_no", false);
	}else if(BfdFreeUtil.getBfdOrgClass().zh==org_class){
		JSPFree.setBillCardItemValue(d1_BillCard, "org_no", _str_org_no+"/"+org_nm);
	}
	
	if(_str_date!=null && _str_date!=""){
		JSPFree.setBillCardItemValue(d1_BillCard, "data_dt", _str_date);
	}
	if(_str_report_type!=null && _str_report_type!=""){
		JSPFree.setBillCardItemValue(d1_BillCard, "report_type", _str_report_type);
	}
}

/**
 * 下一步
 * @returns
 */
function onNext() {
	var jso_cardData = JSPFree.getBillCardFormValue(d1_BillCard);
	var str_org = jso_cardData.org_no;
	var str_date = jso_cardData.data_dt;
	var str_report_type = jso_cardData.report_type;
	
	if (str_org == null || str_org == "") {
        $.messager.alert('提示', '必须选择机构!', 'info');
        return;
    }
	if (str_date == null || str_date == "") {
		$.messager.alert('提示', '必须选择日期!', 'info');
		return;
	}
    if (str_report_type == null || str_report_type == "") {
        $.messager.alert('提示', '必须选择报送频率!', 'info');
        return;
    }
	
	var arr_org = str_org.split(";");
	var orgStr = ""; //970110,990100
	for(var i=0; i<arr_org.length; i++){
		orgStr = orgStr + arr_org[i].substring(0, arr_org[i].indexOf("/"))+",";
	}
	//去掉最后一个逗号
	if(arr_org.length>0){
		orgStr = orgStr.substring(0,orgStr.length-1);
	}
	
	JSPFree.openDialogAndCloseMe("打包压缩下载报文","/yujs/bfd/report/bfd_report_task_zip.js",780,300,{data_dt:str_date,org_no:orgStr,report_type:str_report_type,org_class:org_class,org_nm:org_nm});
}

/**
 * 取消
 * @returns
 */
function onCancel() {
	JSPFree.closeDialog();
}