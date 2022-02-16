<%@ page language="java" pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<%@ page
	import="org.apache.shiro.web.filter.authc.FormAuthenticationFilter"%>
<html>
<meta name="decorator" content="/template/template52.jsp">
<head>
<title>Insert title here</title>
<script type="text/javascript" src="${ctx}/js/udip/UDIP.js"></script>
<script type="text/javascript" src="${ctx}/js/udip/remark/table.js"></script>
<script type="text/javascript">
	var tabObj = null;
	var canSelect = false;
	var tableInfo, tableNameFlag = '';
	var isUpdateDsId;//标注用户是否修改了数据源
	var isFreshTableCol;//是否在主键索引页面重新加载表字段信息，true为重新加载，false为不加载
	var oldTableInfo;
	//初始化
	$(function() {
		initTab();
		function initTab() {
			var height = $(document).height() - 33;
			$("#tab").append('<div tabid="tab1" title="基本表信息" />');
			$("#tab").append('<div tabid="tab2" title="字段信息" />');
			$("#tab").append('<div tabid="tab3" title="主键索引" />');
			$("#tab").append('<div tabid="tab4" title="生成表" />');
			tabObj = $("#tab").ligerTab({
				contextmenu : false,
				onBeforeSelectTabItem : function() {
					return canSelect;
				}
			});
			loadFrame("tab1", "${ctx}/rpt/input/table/tab1", "tab1frame");
		}
	});

	//下一步
	function next(tabNum) {
		var tabid = "tab" + tabNum;
		var id = "tab" + tabNum + "frame";
		var src = "${ctx}/rpt/input/table/tab" + tabNum;
		if(tabNum=='3'||tabNum=='4')
			loadFrame(tabid, src, id,"1");
		else
			loadFrame(tabid, src, id);
		canSelect = true;
		tabObj.selectTabItem("tab" + tabNum);
		if ("${tableId}" != '') {
			if (tabNum == '4' || tabNum == '2' || tabNum == '3'
					|| tabNum == '1') {
				tabObj.selectTabItem("tab" + tabNum);
				//tabObj.reload("tab" + tabNum);
			}
		} else {
			if (tabNum == '4' || tabNum == '1') {
				tabObj.selectTabItem("tab" + tabNum);
				//tabObj.reload("tab" + tabNum);
			}
		}
		canSelect = false;
	}

	function loadFrame(tabId, src, id,refresh) {
		var height = $(document).height() - 100;
		var frame = $('<iframe/>');
		if ($('#' + id).attr('src')) {
			if(refresh&&refresh=="1")
				$('#' + id).attr("src" , src);
			return;
		}
		frame.attr({
			id : id,
			frameborder : 0,
			src : src
		}).css("height", height);
		$('div[tabId=' + tabId + ']').append(frame);
	}
	function setTableInfo_tab1(info) {
		tableInfo = info;
	}
	function setTableInfo_tab2(info) {
		tableInfo.tableColInfo = info;
	}
	function setUpdate(info) {
		tableInfo.update = info;
	}
	function setTableInfo_tab3(info) {
		tableInfo.tableIndexInfo = info;
	}
	function setTableName(info) {
		tableNameFlag = info;
	}
	function getTableColInfo() {
		return tableInfo.tableColInfo;
	}
	function getTableInfo() {
		return tableInfo;
	}
	var tableId = "${tableId}";
	function getTableId() {
		return tableId;
	}
	function clearTableId() {
		tableId='';
	}
	function getTableName() {
		return tableNameFlag;
	}
	function setIsUpdateDsId(dsId) {
		isUpdateDsId = dsId;
	}
	function getIsUpdateDsId() {
		return isUpdateDsId;
	}
	function setIsFreshTableCol(isFresh) {
		isFreshTableCol = isFresh;
	}
	function getIsFreshTableCol() {
		return isFreshTableCol;
	}
	function setOldTableInfo(tableInfo) {
		oldTableInfo = tableInfo;
	}
	function closeDsetBox() {
		BIONE.closeDialog("tableAddWin");
	}
	function saveTableInfo(res) {//新增表字段信息
		if (tableInfo == null || tableInfo.dsId == "" || tableInfo.tableName == "") {
			BIONE.tip("请填写数据来源和表名称。");
			return;
		}
		if (tableInfo.tableColInfo == null || tableInfo.tableColInfo == "") {
			BIONE.tip("请填写表字段信息。");
			return;
		}

		$.ajax({
			async : false,
			url : "${ctx}/rpt/input/table/saveTableInfo",
			dataType : 'text',
			data : tableInfo,
			type : "post",
			success : function(data) {
				parent.f_reload();
				setTableName(tableInfo.tableName);
				if (res != 'create') {
					top.BIONE.tip("保存成功！");
				} else {
					top.BIONE.tip("建补录表成功！");
				}
				closeDsetBox();
				tableId = data;
			},
			error : function(result, b) {
				if(result.responseText==''){

					parent.f_reload();
					setTableName(tableInfo.tableName);
					if (res != 'create') {
						top.BIONE.tip("保存成功！");
					} else {
						top.BIONE.tip("建补录表成功！");
					}
					closeDsetBox();
				}else
				BIONE.tip('发现系统错误 <BR>错误码：' + result.status);
			}
		});
	}

	function updateTableInfo() {//修改表字段信息
		var message = '';
		if (tableInfo == null || tableInfo.dsId == "" || tableInfo.tableName == "") {
			BIONE.tip("请填写数据来源和表名称。");
		}
		if (tableInfo.tableColInfo == null || tableInfo.tableColInfo == "") {
			BIONE.tip("请填写表字段信息。");
		}
		BIONE.ajax({
			type : "post",
			dataType : 'json',
			url : "${ctx}/rpt/input/table/updateTableInfo",
			data : tableInfo
		}, function(resultMap) {
			if (resultMap.result == '') {
				if (tableInfo.tableType == '1' || tableInfo.tableType == '2') {//如果该表为补录表才查看该表是否关联补录模板
					BIONE.ajax({
						type : "get",
						dataType : 'text',
						url : "${ctx}/rpt/input/table/checkTempAndDataLibByTableName.json",
						data : {
							"tableName" : oldTableInfo.tableName, "dsId" : oldTableInfo.dsId, "tableType" : tableInfo.tableType
						}
					}, function(result) {
						if (result != null && result.length > 0) {
							BIONE.tip(result);
						} else {
							BIONE.tip("保存成功！");
						}
					});
				} else {
					BIONE.tip("保存成功！");
				}
				parent.f_reload();
			} else {
				BIONE.commonOpenDialog('保存失败',"tableUpdateError","570","440", '${ctx}/rpt/input/table/tableUpdateError?result='
						+ encodeURIComponent(encodeURIComponent(resultMap.result)));
			}
		});
	}
</script>
</head>
<body>
</body>
</html>