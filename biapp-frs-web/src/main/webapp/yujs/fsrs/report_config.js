//函数
function onBillCardItemEdit(_billCard, _itemkey, _jsoValue) {
	if (_itemkey == "isautojob") {
		var str_value = _jsoValue;
		if (str_value == "Y") {
			JSPFree.setBillCardItemVisible(_billCard, "autojobtrigger_name", true);
			JSPFree.setBillCardItemVisible(_billCard, "autojobtrigger_remark", true);
			JSPFree.setBillCardItemVisible(_billCard, "autojobtrigger", true);
			JSPFree.setBillCardItemIsMust(_billCard,"autojobtrigger",true);//设置报送频率为必输项
		} else {
			JSPFree.setBillCardItemVisible(_billCard, "autojobtrigger_name", false);
			JSPFree.setBillCardItemVisible(_billCard, "autojobtrigger_remark", false);
			JSPFree.setBillCardItemVisible(_billCard,"autojobtrigger",false);//设置报送频率为非必输项
			JSPFree.setBillCardItemIsMust(_billCard,"autojobtrigger",false);//设置报送频率为必输项
			JSPFree.setBillCardItemClearValue(_billCard, "autojobtrigger");
			JSPFree.setBillCardItemClearValue(_billCard, "autojobtrigger_name");
			JSPFree.setBillCardItemClearValue(_billCard, "autojobtrigger_remark");
		}
	}
}

function afterInsert(_billCard) {
	JSPFree.setBillCardItemVisible(_billCard, "autojobtrigger_name", false);
	JSPFree.setBillCardItemVisible(_billCard, "autojobtrigger_remark", false);
	JSPFree.setBillCardItemVisible(_billCard, "autojobtrigger", false);
}

function afterUpdate(_billCard) {
	if (false == _billCard.OldData.isautojob) {
		JSPFree.setBillCardItemVisible(_billCard, "autojobtrigger_name", false);
		JSPFree.setBillCardItemVisible(_billCard, "autojobtrigger_remark", false);
		JSPFree.setBillCardItemVisible(_billCard, "autojobtrigger", false);
	}
}
