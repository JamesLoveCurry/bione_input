/**
 *
 * <pre>
 * Title:【报表管理】-【维护字段】-【监听is_limit】
 * Description:维护字段：监听is_limit
 * is_limit是【是否限定金额】，在这里监听is_limit的值是否发生变化
 * 若is_limit生效，则页面上面limit_value限定金额则变成必填项
 * 若is_limit不生效，则页面上面limit_value限定金额则不是必填项
 * </pre>
 * @author liangzy5
 * @version 1.00.00
 * @date 2020/6/15 14:03
 */
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
