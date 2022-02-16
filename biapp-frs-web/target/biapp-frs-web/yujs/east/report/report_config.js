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

	if (_itemkey == "create_type") {
		var str_value = _jsoValue;
		if (str_value.value == "1") {
			JSPFree.setBillCardItemVisible(_billCard, "addr", false);
			JSPFree.setBillCardItemVisible(_billCard, "org_no", true);
			JSPFree.setBillCardItemVisible(_billCard, "org_name", true);
			JSPFree.setBillCardItemIsMust(_billCard,"org_no",true);
			JSPFree.setBillCardItemIsMust(_billCard,"org_name",true);
			JSPFree.setBillCardItemIsMust(_billCard,"addr",false);
		}else if(str_value.value == "2"){
			JSPFree.setBillCardItemVisible(_billCard, "addr", true);
			JSPFree.setBillCardItemIsMust(_billCard,"addr",true);
			JSPFree.setBillCardItemVisible(_billCard, "org_no", false);
			JSPFree.setBillCardItemVisible(_billCard, "org_name", false);
			JSPFree.setBillCardItemIsMust(_billCard,"org_no",false);
			JSPFree.setBillCardItemIsMust(_billCard,"org_name",false);
		}
	}

	if (_itemkey == "classname") {
		var str_value = _jsoValue;
		if (str_value.value.indexOf("ScheduledDelDownLoadFileTask") != -1) {
			JSPFree.setBillCardItemVisible(_billCard, "del_term", true);
			JSPFree.setBillCardItemIsMust(_billCard,"del_term",true);
		}else{
			JSPFree.setBillCardItemVisible(_billCard, "del_term", false);
			JSPFree.setBillCardItemIsMust(_billCard,"del_term",false);
		}
	}
}

function afterInsert(_billCard) {
	JSPFree.setBillCardItemVisible(_billCard, "addr", false);
	JSPFree.setBillCardItemVisible(_billCard, "org_no", false);
	JSPFree.setBillCardItemVisible(_billCard, "org_name", false);
	JSPFree.setBillCardItemVisible(_billCard, "autojobtrigger_name", false);
	JSPFree.setBillCardItemVisible(_billCard, "autojobtrigger_remark", false);
	JSPFree.setBillCardItemVisible(_billCard, "autojobtrigger", false);
	JSPFree.setBillCardItemVisible(_billCard, "del_term", false);
}

function afterUpdate(_billCard) {
	if (false == _billCard.OldData.isautojob) {
		JSPFree.setBillCardItemVisible(_billCard, "autojobtrigger_name", false);
		JSPFree.setBillCardItemVisible(_billCard, "autojobtrigger_remark", false);
		JSPFree.setBillCardItemVisible(_billCard, "autojobtrigger", false);
	}
	if (_billCard.OldData.classname.indexOf("ScheduledDelDownLoadFileTask") == -1) {
		JSPFree.setBillCardItemVisible(_billCard, "del_term", false);
		JSPFree.setBillCardItemIsMust(_billCard,"del_term",false);
	} else if (_billCard.OldData.classname.indexOf("ScheduledDelDownLoadFileTask") != -1) {
		JSPFree.setBillCardItemVisible(_billCard, "del_term", true);
		JSPFree.setBillCardItemIsMust(_billCard,"del_term",true);
	}

	if(false == _billCard.OldData.create_type.value=='2'){
		JSPFree.setBillCardItemVisible(_billCard, "org_no", false);
		JSPFree.setBillCardItemVisible(_billCard, "org_name", false);
	}else if(false == _billCard.OldData.create_type.value=='1'){
		JSPFree.setBillCardItemVisible(_billCard, "addr", false);
	}
}
