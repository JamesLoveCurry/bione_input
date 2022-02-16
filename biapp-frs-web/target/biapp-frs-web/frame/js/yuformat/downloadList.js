/**
 *
 * <pre>
 * Title:【数据下载】
 * Description:数据下载:主页面
 * </pre>
 * @author miaokx
 * @version 1.00.00
   @date 2021年10月20日
 */
function AfterInit() {
	var whereSql = "1=1";
	var json = JSPFree.doClassMethodCall("com.yusys.bione.plugin.detail.download.service.DetailDownloadBS", "getWhereSql",{});
	if (json.code='success') {
		whereSql = json.data;
	}
	JSPFree.createBillList("d1", "/bione-plugin/xml/downloadList.xml", null, {
		isSwitchQuery: "N",
		orderbys: "create_time desc,tab_name_en",
		refWhereSQL: whereSql
	});
}

/**
 * 下载
 * @param _btn
 */
function download(_btn) {
	var dataset = _btn.dataset;
	var index = dataset.rowindex;
	var rows = d1_BillList.datagrid("getRows");
	var row = rows[index];//index为行号
	var status = row.status;
	if (status == '3') {
		JSPFree.confirm('提示', '是否确定下载？', function(_isOK) {
			if (_isOK) {
				var filepath = row.file_path;
				var src = v_context + "/detail/download/data/downloadDataNoDel?filepath=" + filepath;
				var download = $('<iframe id="download" style="display: none;"/>');
				$('body').append(download);
				download.attr('src', src);
			}
		})

	} else {
		$.messager.alert('提示', "只有生成成功的记录才可以下载!", 'warning');
	}

}

/**
 * 删除zip
 * @param _btn
 */
function deleteZip(_btn) {
	var dataset = _btn.dataset;
	var index = dataset.rowindex;
	var rows = d1_BillList.datagrid("getRows");
	var row = rows[index];//index为行号
	var rid = row.rid;
	var filepath = row.file_path;
	var json = JSPFree.doClassMethodCall("com.yusys.bione.plugin.detail.download.service.DetailDownloadBS", "getStatus",{rid: rid});
	if (json.code == 'success') {
		if (json.data == '2') {
			JSPFree.confirm('提示', '处理中的任务无法删除，是否强制删除？', function(_isOK) {
				if (_isOK) {
					var param = {rid: rid, filepath: filepath}
					var json = JSPFree.doClassMethodCall("com.yusys.bione.plugin.detail.download.service.DetailDownloadBS", "deleteZip",param);
					if (json.code == 'success') {
						$.messager.alert('提示', '删除成功', 'warning');
						JSPFree.queryDataByConditon(d1_BillList, null);  // 立即查询刷新数据
					} else {
						$.messager.alert('提示', json.msg, 'warning');
					}
				}
			})
			return;
		}
	}
	JSPFree.confirm('提示', '删除数据会将文件一起删除，是否删除?', function(_isOK) {
		if (_isOK) {
			var param = {rid: rid, filepath: filepath}
			var json = JSPFree.doClassMethodCall("com.yusys.bione.plugin.detail.download.service.DetailDownloadBS", "deleteZip",param);
			if (json.code == 'success') {
				$.messager.alert('提示', '删除成功', 'warning');
				JSPFree.queryDataByConditon(d1_BillList, null);  // 立即查询刷新数据
			} else {
				$.messager.alert('提示', json.msg, 'warning');
			}
		}
	})

}