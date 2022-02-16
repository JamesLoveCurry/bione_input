<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<html>
<meta name="decorator" content="/template/template5.jsp">
<head>
<style type="text/css">
#searchbox {
	width : auto;
}
.content {
	width : auto;
	height : auto;
}
#center {
	overflow: hidden;
}
</style>
<script type="text/javascript"> var app = { ctx : '${ctx}' }, grid = null; </script>
<script type="text/javascript" src="${ctx}/frs/js/rptquery/rptquery.js"></script>
<script type="text/javascript" src="${ctx}/frs/js/rptfill/TaskFill.js"></script>
<script type="text/javascript">
	$(function(){
		grid = $("#maingrid").ligerGrid({
			columns : [
				{
					display : '日期', name : 'dataDate', align : 'center', minWidth : '', width : '55%'
				}, {
					display : '操作', name : 'oper', align : 'center', minWidth : '', width : '45%',
					render : function(rowdata, index, value, column) {
						// return '<a name="' + column.name + '" class="oper" rowid="' + index + '" onclick="return false;">删除</a>';
						return "<a name='" + column.name + "' href='javascript:deleteRowByIndex(" + index + ")'>删除</a> ";
					}
				} ], 
			dateFormat : 'yyyyMMdd',
			checkbox : false,
			usePager : false,
			isScroll : false,
			alternatingRow : true, /* 附加奇偶行效果行 */
			colDraggable : true,	/* 是否允许表头拖拽 */
			dataAction : 'local',	/* 从后台获取数据 */
			method : 'post',
			sortName : 'dataDate', //第一次默认排序的字段
			sortOrder : 'desc',
			delayLoad : true,
			height : 'auto',
			width : 'auto'
		});
		// 加载已经选择的数据日期
		var c = window.parent.jQuery.ligerui.get("dataDate");
		var tmp = c.getValue();
		if (tmp) {
			var rowsdata = $.map(tmp.split(','), function(n) {
				return createRow(n);
			});
			grid.addRows(rowsdata);
		}
		// 注册删除 click 事件
		// $('.oper').live('click', function() {
		// 	var $this = $(this), name = $this.attr('name'), rowid = $this.attr('rowid');
		// 	grid.remove(grid.getRow(rowid));
		// });
		
	/* 	$('#search').ligerForm({
			fields : [ {
				display : "选择日期", name : "dataDate", newline : true, type : "date", cssClass : "field",
				options : {
					format: 'yyyyMMdd'
				}
			} ],
			space : '5'
		}); */
		$('#searchbox').hide();
		
		grid.setHeight($('#center').height() - $('#searchbox').height() - 6);
		var btns = [
			{ text : "取消", onclick : function() { BIONE.closeDialog("dateDialog"); } },	
			{ text : "保存", onclick : function() {
					var dateArr = $.map(grid.getData(), function(n) {
						return n.dataDate;
					});
					var c = parent.jQuery.ligerui.get("dataDate");
					/* c._changeValue(dateArr.join(), dateArr.join()); */
					parent.liger.get('dataDate').setValue(dateArr.join());
					parent.$('#dataDateHide').val(dateArr.join());
					BIONE.closeDialog("dateDialog");
				}
			}
		];
		BIONE.addFormButtons(btns);
		
		// 生成一条 grid 行数据
		function createRow(dataDate) {
			return $.extend({}, { dataDate : dataDate });
		}
	});

	//删除
	function deleteRowByIndex(rowid){
		grid.deleteRow(rowid);
	}
</script>
</head>
<body>
	<div id="template.center">
		<div id="searchbox" class="searchbox">
			<form id="formsearch">
				<div id="search"></div>
			</form>
		</div>
		<div class="content">
			<div id="maingrid" class="maingrid"></div>
		</div>
	</div>
</body>
</html>