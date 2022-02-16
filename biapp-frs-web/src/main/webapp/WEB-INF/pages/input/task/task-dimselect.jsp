<%@ page language="java" pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<html>
<meta name="decorator" content="/template/template8.jsp">
<head>
<!-- 基础的JS和CSS文件 -->
<script type="text/javascript">
    var indexNo_='${indexNo}';
    var indexVerId_='${indexVerId}';
	var dynamicWidth  ="38%";
	var grid;
	var ids;
	function initSearchForm() {
		//搜索表单应用ligerui样式
		var  fieldsColumn= [ {
			name : "indexNo",
			type : "hidden",
			attr : {
				field : 'indexNo',
				op : "="
			}
		}, {
			name : "indexVerId",
			type : "hidden",
			attr : {
				field : 'indexVerId',
				op : "="
			}
		},{
			display : '维度类型名称',
			name : 'dimTypeNm',
			newline : false,
			type : "text",
			cssClass : "field",
			attr : {
				field : 'dimItemNm',
				op : "like"
			}
		}];
		$("#search").ligerForm({
			fields :fieldsColumn
		});
	};
	function initGrid() {
		var dynamicColumns  =  [ {
				display :  '维度类型名称',
				name : 'dimTypeNm',
				id:'dimTypeNm',
				width : "28%",
				align : 'left'
			},{
				display : '业务定义',
				name : 'busiDef',
				width : dynamicWidth,
				align : 'left'
		}];
		
		
		var   gridJson  ={
				toolbar : {},
    			checkbox :true,
    			columns :dynamicColumns,
    			alternatingRow : true, //附加奇偶行效果行
				colDraggable : true,
				sortName : 'dimTypeNm',//第一次默认排序的字段
    			sortOrder : 'asc', //排序的方式
    			url : "${ctx}/report/input/idxinput/findDimItemListByIdxInfo.json?indexNo="+indexNo_+"&indexVerId="+indexVerId_+"&d=" + new Date().getTime(),
    			rownumbers : true,
    			width : '100%',
    			usePager : true,
    			pageParmName :  'page',
    			pagesizeParmName :  'pagesize',
				onCheckRow : function(checked, rowdata, rowindex,
						rowDomElement) {
					//只允许勾选一行
					if (checked) {
						var all = rowdata;						
					}
				},
		};
		grid = $("#maingrid").ligerGrid(gridJson);
	};
	$(function() {
		//initSearchForm();
		initGrid();
		//BIONE.addSearchButtons("#search", grid, "#searchbtn");
		//$("input[name='indexVerId']").val(indexVerId_);
		//$("input[name='indexNo']").val(indexNo_);
	});
	
	function f_open_preview(dimItemNo) {
			var modelDialogUri = "${ctx}/rpt/frame/dimCatalog/dimItem/update?dimItemNo=" + dimItemNo+"&dimTypeNo="+dimTypeNo+"&preview=preview";
			parent.BIONE.commonOpenDialog("维度项查看", "rptDimItemInfoUpdateWin",$(parent.document).width(), $(parent.document).height(),modelDialogUri);
	}
	function achieveIds() {
		ids = [];
		var rows = grid.getSelectedRows();
		for ( var i in rows) {
			ids.push(rows[i]);
		}
	}
</script>
</head>
<body>

	<div id="template.right.down">
		<div id="aaa">
			<div id="maingrid" style="margin-top: 60px;"></div>
		</div>
	</div>
</body>
</html>