//初始化界面,菜单配置路径是【/frs/yufreejs?js=/yujs/east/58table.js】
var fileName = "";

function AfterInit() {
    JSPFree.createBillList("d1", "/biapp-cr/freexml/filedatalist/filelist.xml", null, {isSwitchQuery: "N"});
}

//下载
function downfile() {
	const jsy_datas = JSPFree.getBillListSelectDatas(d1_BillList);
	// alert(JSON.stringify(jsy_datas))
    if (jsy_datas == null || jsy_datas.length <= 0) {
        JSPFree.alert("至少选择一条数据！");
        return;
    }
    // var filepath= JSPFree.doClassMethodCall("com.yusys.cr.fileexport.service.CrReportBSDMO","getEntFilePath");
    for(var i = 0; i < jsy_datas.length; i++){
    	let filename= jsy_datas[i]['filename'];
		let filepath = jsy_datas[i]['fileaddr'];
		filepath= filepath+'/'+filename;
		let download=null;
       download = $('<iframe id="download" style="display: none;"/>');
       $('body').append(download);
       alert(filepath);
		const src = v_context + "/cr/common/downLoadModel?fileNm="+filepath;
		download.attr('src', src);
    }
}

//导入
function importfile() {
    JSPFree.openDialog("文件上传", "/yujs/cr/filedata/importMData.js", 500, 240, null, function (_rtdata) {
        JSPFree.queryDataByConditon(d1_A_BillList, null);  //立即查询刷新数据
    });
}
