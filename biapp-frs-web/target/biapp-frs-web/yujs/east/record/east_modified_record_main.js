/**
 * 
 * <pre>
 * Title: 【配置管理】-【数据日志】
 * </pre>
 * @author miaokx
 * @version 1.00.00
   @date 2021年7月22日
 */
function AfterInit(){
	JSPFree.createBillList("d1","/biapp-east/freexml/east/record/east_modified_record.xml", null , null); //创建列表
}
/**
 * 查看修改前数据
 * @param _btn
 * @returns
 */
function onLookSourceData(_btn) {
	var str_cmd = _btn.dataset.itemvalue;
	FreeUtil.openHtmlMsgBox("查看修改前数据", 800, 400, "<span style='font-size:15px;color:#9B0000;'>" + str_cmd.replaceAll(',',',\r\n') + "</span>");
}
/**
 * 查看修改后数据
 * @param _btn
 * @returns
 */
function onLookModifiedData(_btn) {
	var str_cmd = _btn.dataset.itemvalue;
	FreeUtil.openHtmlMsgBox("查看修改后数据", 800, 400, "<span style='font-size:15px;color:#9B0000'>" + str_cmd.replaceAll(',',',\r\n') + "</span>");
}