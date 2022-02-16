//函数
var array = new Array(1);
var map = {};
map['id'] = "日报";
map['name'] = "日报";
var map1 = {};
map1['id'] = "月报";
map1['name'] = "月报";
array[0] = map;
array[1] = map1;
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
