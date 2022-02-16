/**
  * 
  * <pre>
  * Title: 【报送管理】-【报文策略】
  * Description: 报文策略：新增页面
  * </pre>
  * @author wangxy31 
  * @version 1.00.00
    @date 2020年5月22日
  */

var rid = "";
var template = ""; // 模板路径
var tabname = "";
var tabnameen = "";
var reporttype = "";

function AfterInit() {
	rid = jso_OpenPars.rid;
	type = jso_OpenPars.type;
	template = jso_OpenPars.templetcode;
	tabname = jso_OpenPars.tabname;
	tabnameen = jso_OpenPars.tabnameen;
	reporttype = jso_OpenPars.reporttype;

	JSPFree.createBillCard("d1", template, ["保存/onSave/icon-p21", "取消/onCancel/icon-undo"], null);
}

/**
 * 新增后，触发是否自动任务监听操作
 * 此处同report_config_safe.js
 * 为何两份？原因是新增时候是重写了方法
 * @param _billCard
 * @returns
 */
function AfterBodyLoad() {
	var str_fnName = "onBillCardItemEdit"; // 在创建表单时会创建这个函数
	if(typeof self[str_fnName] == "function") {  // 如果的确定义了这个函数
		FreeUtil.addBillCardItemEditListener(d1_BillCard, self[str_fnName]);  // 增加监听事件
	}

	// 处理After逻辑
	var str_fnName_after = "afterInsert";
	if(typeof self[str_fnName_after] == "function") {  // 如果的确定义了这个函数
		self[str_fnName_after](d1_BillCard);  // 增加监听事件
	}

	FreeUtil.setBillCardLabelHelptip(d1_BillCard); // 必须写在最后一行
}

/**
 * 新增后，监听
 * @param _billCard
 * @returns
 */
function afterInsert(_billCard) {
	JSPFree.setBillCardItemVisible(_billCard, "autojobtrigger_name", false);
	JSPFree.setBillCardItemVisible(_billCard, "autojobtrigger_remark", false);
	JSPFree.setBillCardItemVisible(_billCard, "autojobtrigger", false);
}

/**
 * 卡片编辑，触发
 * @param _billCard
 * @param _itemkey
 * @param _jsoValue
 * @returns
 */
function onBillCardItemEdit(_billCard, _itemkey, _jsoValue) {
	if (_itemkey == "isautojob") {
		var str_value = _jsoValue;
		if (str_value == "Y") {
			JSPFree.setBillCardItemVisible(_billCard, "autojobtrigger_name", true);
			JSPFree.setBillCardItemVisible(_billCard, "autojobtrigger_remark", true);
			JSPFree.setBillCardItemVisible(_billCard, "autojobtrigger", true);
			JSPFree.setBillCardItemIsMust(_billCard,"autojobtrigger",true); // 设置报送频率为必输项
		} else {
			JSPFree.setBillCardItemVisible(_billCard, "autojobtrigger_name", false);
			JSPFree.setBillCardItemVisible(_billCard, "autojobtrigger_remark", false);
			JSPFree.setBillCardItemVisible(_billCard,"autojobtrigger",false); // 设置报送频率为非必输项
			JSPFree.setBillCardItemIsMust(_billCard,"autojobtrigger",false); // 设置报送频率为必输项
			JSPFree.setBillCardItemClearValue(_billCard, "autojobtrigger");
			JSPFree.setBillCardItemClearValue(_billCard, "autojobtrigger_name");
			JSPFree.setBillCardItemClearValue(_billCard, "autojobtrigger_remark");
		}
	}
}

/**
 * 保存
 * @return {[type]} [description]
 */
function onSave() {
	JSPFree.setBillCardValues(d1_BillCard,{rid:FreeUtil.getUUIDFromServer(), report_type:reporttype});
	var flag = JSPFree.doBillCardInsert(d1_BillCard, null);
	if (flag) {
		JSPFree.closeDialog("保存成功");
	}
}

/**
 * 取消
 * @return {[type]} [description]
 */
function onCancel() {
	JSPFree.closeDialog();
}
