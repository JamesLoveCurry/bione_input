//初始化界面,菜单配置路径是【/frs/yufreejs?js=/yujs/east/58table.js】
var str_org_no = "";

function AfterInit() {
    //通过当前登录人所属内部机构获取报送机构号
    var jso_report_org = JSPFree.doClassMethodCall("com.yusys.east.checkrule.rulesummary.service.ValidateQueryCondition", "getReportOrgNo", {
        _loginUserOrgNo: str_LoginUserOrgNo,
        report_type: "04"
    });
    if (jso_report_org.msg == "ok") {
        org_no = jso_report_org.data;
    }


    JSPFree.createBillCard("d1", "/biapp-east/freexml/east/report/east_cr_update_insert.xml", ["保存/onNext", "取消/onCancel"]);
}

//页面加载结束后
function AfterBodyLoad() {

    // 判断是否是总行，如果是总行则不禁用，如果是其他则禁用
    var org_class = "";
    var jso_org = JSPFree.getHashVOs("SELECT org_class FROM rpt_org_info where org_type='04' and org_no = '" + org_no + "'");
    if (jso_org != null && jso_org.length > 0) {
        org_class = jso_org[0].org_class;
    }
    if (org_class != '总行') {
        JSPFree.setBillQueryItemEditable("org_no", "下拉框", false);
    }
    JSPFree.setBillCardItemValue(d1_BillCard, "org_no", org_no)
}

function onNext() {
    var jso_cardData = JSPFree.getBillCardFormValue(d1_BillCard);
    var str_date = jso_cardData.data_dt;
    var orgNo = jso_cardData.org_no;
    if (str_date == null || str_date == "") {
        JSPFree.alert("采集日期不能为空!");
        return;
    }

    if (orgNo == null || orgNo == "") {
        JSPFree.alert("报送机构不能为空!");
        return;
    }
    var hashVOs = JSPFree.getHashVOs("select count(*) as count from EAST_CR_DATA_UPLOAD where org_no='"+orgNo+"' and data_dt='"+str_date+"'");
    if (hashVOs[0].count!=0) {
        JSPFree.alert("该机构已有当前数据日期，请重新选择!");
        return;
    }
    var _par = {data_dt: str_date, org_no: orgNo};
    var _rt = JSPFree.doClassMethodCall("com.yusys.east.business.report.service.EastCrDataUpload", "execInsert", _par);
    if (_rt.status == "success") {
        JSPFree.closeDialog("org_no = '"+orgNo+"' and data_dt='"+str_date+"'");
    } else {
        $.messager.alert("警告","新增数据失败","warning");
    }
}

function onCancel() {
    JSPFree.closeDialog();
}