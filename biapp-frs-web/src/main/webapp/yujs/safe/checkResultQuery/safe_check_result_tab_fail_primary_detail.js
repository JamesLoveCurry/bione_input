var reportType;

function AfterInit() {

    reportType = jso_OpenPars.reportType;
    var parentTabnmEn = jso_OpenPars.parentTabnmEn;
    var parentTabName = jso_OpenPars.parentTabName;
    var parentData = jso_OpenPars.parentData;

    var str_className = "Class:com.yusys.safe.base.common.template.CommonBuilderTemplate.getTemplet('" + parentTabName + "','" + parentTabnmEn + "','" + reportType + "','" + str_LoginUserOrgNo + "')";


    JSPFree.createBillCard("d1", str_className);

    JSPFree.setBillCardValues(d1_BillCard,parentData);
    JSPFree.setBillCardItemEditable(d1_BillCard, "*", false);

}