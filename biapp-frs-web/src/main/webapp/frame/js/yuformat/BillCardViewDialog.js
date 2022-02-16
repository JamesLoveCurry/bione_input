//查看界面
function AfterInit() {
	var str_templetcode = jso_OpenPars.templetcode; // 模板编码
	var jso_listData = self.jso_OpenPars2; // 列表中传入的数据
	// console.log(jso_listData); //

	// 先创建卡片
	var btns = [ "取消/onClose/icon-undo" ];

	// 卡片前端的配置,即可以替换默认配置!
	JSPFree.createBillCard("d1", str_templetcode, btns); // 创建卡片表单

	// 直接设置卡片中的值,即从内存中传过来,而不是重新查询数据库
	JSPFree.setBillCardValues(d1_BillCard, jso_listData);
}

// 加载完后.
function AfterBodyLoad() {
	FreeUtil.setBillCardEditableForUpdate(d1_BillCard); //

	// 检查xml中有没有定义
	var str_divid = d1_BillCard.divid; // 卡片的divid

	// 处理表单编辑变化事件监听
	var str_fnName = "onBillCardItemEdit"; // 在创建表单时会创建这个函数
	if (typeof self[str_fnName] == "function") { // 如果的确定义了这个函数
		FreeUtil.addBillCardItemEditListener(d1_BillCard, self[str_fnName]); // 增加监听事件
	}

	// 处理After逻辑
	var str_fnName_after = "afterUpdate";
	if (typeof self[str_fnName_after] == "function") { // 如果的确定义了这个函数
		self[str_fnName_after](d1_BillCard); // 增加监听事件
	}

	JSPFree.setBillCardLabelHelptip(d1_BillCard); // 设置帮助说明 必须写在最后一行
}

// 直接关闭
function onClose() {
	JSPFree.closeDialog();
}