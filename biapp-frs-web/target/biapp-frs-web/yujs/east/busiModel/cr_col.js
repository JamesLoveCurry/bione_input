function onBillCardItemEdit(_billCard, _itemkey, _jsoValue) {
	if (_itemkey == "is_limit") {
		console.log("字段【" + _itemkey + "】发生变化" + _jsoValue);

		var str_value = _jsoValue;
		if (str_value == "Y") {
			JSPFree.setBillCardItemVisible(_billCard, "limit_value", true);
			JSPFree.setBillCardItemIsMust(_billCard,"limit_value",true);//设置报送频率为必输项
		} else {
			JSPFree.setBillCardItemVisible(_billCard, "limit_value", false);
			JSPFree.setBillCardItemIsMust(_billCard,"limit_value",false);//设置报送频率为非必输项
		}
	}
}

function afterInsert(_billCard) {
	JSPFree.setBillCardItemVisible(_billCard, "limit_value", false);
}

function afterUpdate(_billCard) {
	if (false == _billCard.OldData.is_limit) {
		JSPFree.setBillCardItemVisible(_billCard, "limit_value", false);
	}
}
