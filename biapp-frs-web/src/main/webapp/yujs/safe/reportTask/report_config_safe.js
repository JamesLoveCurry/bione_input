/**
 * 
 * <pre>
 * Title: 【报送管理】-【报文策略】
 * Description: 报文策略，表单页面，是否自定义任务，触发操作
 * </pre>
 * @author wangxy31 
 * @version 1.00.00
   @date 2020年5月26日
 */

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

/**
 * 保存之后，触发
 * @param _billCard
 * @returns
 */
function afterUpdate(_billCard) {
	if (false == _billCard.OldData.isautojob) {
		JSPFree.setBillCardItemVisible(_billCard, "autojobtrigger_name", false);
		JSPFree.setBillCardItemVisible(_billCard, "autojobtrigger_remark", false);
		JSPFree.setBillCardItemVisible(_billCard, "autojobtrigger", false);
	}
}
