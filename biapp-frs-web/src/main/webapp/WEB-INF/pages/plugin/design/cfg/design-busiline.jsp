<%@ page language="java" pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<html>
<meta name="decorator" content="/template/template5.jsp">
<head>
<script type="text/javascript">
	
	var grid ;
	var Design = window.parent.Design;
	var selBusiLine = "";
	
	$(function(){
		$("#center").css("overflow","hidden");
		initGrid();
		initButton();
	});
	
	function initGrid(){
		grid = $("#maingrid").ligerGrid({
			width : '99.8%',
			columns : [{
				display : '条线编号',
				name : 'lineId',
				align : 'center',
				width : '30%'
			},{
				display : '条线名称',
				name : 'lineNm',
				align : 'center',
				width : '58%'
			}],
			checkbox : true,
			usePager : false,
			rownumbers : true,
			alternatingRow : true,//附加奇偶行效果行
			dataAction : 'server',//从后台获取数据
			method : 'post',
			url : "${ctx}/report/frame/design/cfg/busiline/getLineList",
			onCheckRow : function(checked, rowdata, rowindex,
					rowDomElement) {
				//只允许勾选一行
				if (checked) {
					var all = grid.getData();
					$.each(all, function(i, n) {
						var x = grid.getRow(i);
						if (rowindex != x.__id) {
							grid.unselect(grid.getRow(i));
						}
					});
				}
			},
			onBeforeCheckAllRow : function() {
				return false;
			},
			onDblClickRow : function(data , rowid , rowdata){
				if(!window.parent.rptTab.isTabItemExist(data.lineId)){				
					var heightTmp = window.parent.$("#center").height() - 30;
					var frameHeightTmp = heightTmp - 30;
					window.parent.rptTab.addTabItem({
						tabid : data.lineId,
						text : data.lineNm,
						showClose : true,
						content : '<iframe frameborder="0" id="'+data.lineId+'" style="height:'+frameHeightTmp+'"></iframe>',
						height : heightTmp
					});
					window.parent.rptTab.moveTabItem(data.lineId , "_addTab");
				}else{
					window.parent.rptTab.selectTabItem(data.lineId);
				}
				BIONE.closeDialog("busiLineDialog");
			}
		});
		//隐藏全选按钮
		var oo = $(".l-grid-hd-cell-inner");
		if (oo.length > 0) {
			$(oo[1]).children(".l-grid-hd-cell-btn-checkbox").hide();
		}
	}
	
	function initButton(){
		var buttons = [];
		buttons.push({
			text : '取消',
			onclick : function(){
				BIONE.closeDialog("busiLineDialog");
			}
		});
		buttons.push({
			text : '添加',
			onclick : saveHandler
		});
		BIONE.addFormButtons(buttons);
	}
	
	// 保存方法
	function saveHandler(){
		if(grid){
			var selRow = grid.getSelectedRow();
			if(selRow != null
					&& typeof selRow == "object"){				
				if(!window.parent.rptTab.isTabItemExist(selRow.lineId)){				
					var heightTmp = window.parent.$("#center").height() - 30;
					var frameHeightTmp = heightTmp - 30;
					window.parent.rptTab.addTabItem({
						tabid : selRow.lineId,
						text : selRow.lineNm,
						showClose : true,
						content : '<iframe frameborder="0" id="'+selRow.lineId+'" style="height:'+frameHeightTmp+'"></iframe>',
						height : heightTmp
					});
					window.parent.rptTab.moveTabItem(selRow.lineId , "_addTab");
				}else{
					window.parent.rptTab.selectTabItem(selRow.lineId);
				}
				BIONE.closeDialog("busiLineDialog");
			}else{
				BIONE.tip("请选择要添加的业务条线");
			}
		}
	}
	
</script>
</head>
<body>
<div id="template.center">
	<div id="maingrid"  style="width:99.8%;"></div>
</div>
</body>
</html>