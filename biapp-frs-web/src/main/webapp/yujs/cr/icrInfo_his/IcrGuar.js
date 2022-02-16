//初始化界面,菜单配置路径是
function AfterInit(){
    JSPFree.createSplit("d1","上下",300);
    JSPFree.createBillList("d1_A","/biapp-cr/freexml/icrInfo_his/guar/IcrGuarInformation.xml");
    JSPFree.createTabb("d1_B",["其他信息","相关还款责任人","抵质押物信息","在保责任信息"]);
    JSPFree.createBillList("d1_B_1","/biapp-cr/freexml/icrInfo_his/guar/IcrGuarInformation2.xml");
    JSPFree.createBillList("d1_B_2","/biapp-cr/freexml/icrInfo_his/guar/IcrGuarInformation3.xml");
    JSPFree.createBillList("d1_B_3","/biapp-cr/freexml/icrInfo_his/guar/IcrGuarInformation4.xml");
    JSPFree.createBillList("d1_B_4","/biapp-cr/freexml/icrInfo_his/guar/IcrGuarInformation5.xml");
    JSPFree.bindSelectEvent(d1_A_BillList,onSelect);
}
//选择人员基础信息时触发的事件,即查询该人员的其他信息!
function onSelect(rowIndex, rowData){
    var acctcode = rowData["acctcode"];  //证件号码
    JSPFree.queryDataByConditon(d1_B_1_BillList,"ACCTCODE='" + acctcode + "'");
    JSPFree.setDefaultValues(d1_B_1_BillList,{"ACCTCODE":acctcode});
    JSPFree.queryDataByConditon(d1_B_2_BillList,"ACCTCODE='" + acctcode + "'");
    JSPFree.setDefaultValues(d1_B_2_BillList,{"ACCTCODE":acctcode});
    JSPFree.queryDataByConditon(d1_B_3_BillList,"ACCTCODE='" + acctcode + "'");
    JSPFree.setDefaultValues(d1_B_3_BillList,{"ACCTCODE":acctcode});
    JSPFree.queryDataByConditon(d1_B_4_BillList,"ACCTCODE='" + acctcode + "'");
    JSPFree.setDefaultValues(d1_B_4_BillList,{"ACCTCODE":acctcode});
}

function onExport(){
    var str_sql = d1_A_BillList.CurrSQL;
    if (str_sql == null) {
        JSPFree.alert("当前无记录！");
        return;
    }
    JSPFree.downloadExcelBySQL("个人担保基础段.xls", str_sql, "个人担保基础段sheet1", "信息记录类型,担保账户类型,账户标识码,信息报告日期,报告时点说明代码,债务人姓名,债务人证件类型,债务人证件号码,增量标识,业务管理机构代码");
}

/**
 * 模版下载
 * @author 墨殇九歌
 * 2020-3-20  09:50
 */
function downModel(){
    var download=null;
    download = $('<iframe id="download" style="display: none;"/>');
    $('body').append(download);

    var src = v_context + "/cr/common/downLoadModel?fileNm=29-PersionalInfo.xlsx";
    download.attr('src', src);
}

//导入
function onImport(){
    JSPFree.openDialog("文件上传","/yujs/cr/basic/importMData.js", 500, 240, null,function(_rtdata){
        JSPFree.queryDataByConditon(d1_A_BillList,null);  //立即查询刷新数据
    });
}
