/**
 * 
 * <pre>
 * Title: 【报表处理】-【报表审核】
 * Description: 报表处理-报表审核：审核查看页面
 * </pre>
 * @author wangxy31
 * @version 1.00.00
   @date 2021年9月22日
 */

var tabName = "";
var tabNameEn = "";
var str_ds = "";
var json_data = "";
function AfterInit() {
	tabName = jso_OpenPars2.tabName;
	tabNameEn = jso_OpenPars2.tabNameEn;
	var str_className1 = "Class:com.yusys.bfd.business.service.BfdModelTempletBuilderForDistribute.getTemplet('"+tabName+"','"+tabNameEn+"','"+str_LoginUserOrgNo+"','_R')";
	JSPFree.createBillCard("d1",str_className1,["取消/onCancel/icon-undo"],null);

	JSPFree.setBillCardValues(d1_BillCard,jso_OpenPars2.json_data);
	d1_BillCard.OldData = jso_OpenPars2.json_data;
	json_data = jso_OpenPars2.json_data;
}

/**
 * 页面加载完成之后，表单中的前端主键is_pk1设置为不可编辑，强制设置机构编号和数据日期不可编辑
 */
var col_arr = [];
function AfterBodyLoad(){
	var jso_col = JSPFree.getHashVOs("select col_name_en from bfd_cr_col where tab_name='"+tabName+"' order by col_no");
	if(jso_col != null && jso_col.length > 0){
		for (var i=0;i<jso_col.length;i++) {
			col_arr.push(jso_col[i].col_name_en);
			JSPFree.setBillCardItemEditable(d1_BillCard,jso_col[i].col_name_en.toLowerCase(),false);
		}
	}
	JSPFree.setBillCardItemEditable(d1_BillCard,"*",false);
	var ds_name = JSPFree.getHashVOs("select ds_name from bfd_cr_tab where tab_name='"+tabName+"'");
	if (ds_name != null && ds_name.length > 0) {
		str_ds = ds_name[0].ds_name;
	}
	// 根据报送机构获取当前错误表，然后查询出所有的错误规则。
	showUpdateFiled();
}

/**
 * 显示修改字段
 */
function showUpdateFiled() {
	var rid = jso_OpenPars2.json_data.rid;
	// 根据rid获取数据
	var oldDataJson = JSPFree.doClassMethodCall("com.yusys.bfd.process.service.BfdCrProcessBSDMO", "getOldData", {rid: rid, tabNameEn: tabNameEn, dsName: str_ds});
	var oldData =  oldDataJson.data;
	var newData =  json_data;
	for (var i=0;i<col_arr.length;i++) {
		var data = col_arr[i].toLocaleLowerCase();
		if (oldData[data] !=null && oldData[data]!="" && oldData[data]!=undefined) {
			if ( oldData[data] != newData[data]) {
				JSPFree.setBillCardItemWarnMsg(d1_BillCard, col_arr[i].toLowerCase(), "修改前数据:" + oldData[data]);
				JSPFree.setBillCardItemColor(d1_BillCard, col_arr[i].toLowerCase(), "yellow");
			}
		} else {
			if (newData[data] !=null && newData[data]!="" && newData[data]!=undefined) {
				JSPFree.setBillCardItemWarnMsg(d1_BillCard, col_arr[i].toLowerCase(), "修改前数据:\"\"");
				JSPFree.setBillCardItemColor(d1_BillCard, col_arr[i].toLowerCase(), "yellow");
			}
		}

	}
}

/**
 * 取消编辑操作，点击按钮，关闭窗口
 * @returns
 */
function onCancel() {
	JSPFree.closeDialog(null);
}