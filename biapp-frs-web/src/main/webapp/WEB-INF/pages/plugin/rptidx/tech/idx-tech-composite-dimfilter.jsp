<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<html>
<meta name="decorator" content="/template/template5.jsp">
<head>
<style type="text/css">
.container {
	overflow: hidden;
}
.buttom {
	position: absolute;
	width: 100%;
	bottom: 0;
}
.container.tree {
	border-top: solid 1px #CCC;
	border-bottom: solid 1px #CCC;
	background-color: #FFF;
}
#treeContainer {
	height: 100%;
	overflow: auto;
}
</style>
<script type="text/javascript"
	src="${ctx}/plugin/js/dim/dimFormula.js"></script>
<script type="text/javascript">
$(function() {
	$("#container").height($("#center").height()-50);
	// 下拉
	var row = parent.alertDimValWin.row;
	$('#mode').ligerForm({
		fields : [ {
			display : "过滤类型",
			name : "filterMode",
			type : "select",
			width: '100',
			height:'10',
			options : {
				data : [ {
					id : '01',
					text : '包含'
				}],
				initValue: row.filterMode || '01'
			}
		} ]
	});
	
	// 按钮
	BIONE.addFormButtons([{
		text : '选择',
		onclick : function() {
			var row = parent.alertDimValWin.row;
			var tree = $.fn.zTree.getZTreeObj('tree');
			var nodes = tree.getCheckedNodes();
			var items = [];
			var itemArr = [];
			if(nodes.length>0){
				$.each(nodes, function(i, n) {
					itemArr.push(n.id);
				});
				
				var str = liger.get('filterMode').getText() + "( ";
				$.each(nodes, function(i, n) {
					if (i > 0) {
						str += ";";
					}
					str += n.text;
				});
				
				str = str + " " + nodes.length + "个过滤值)";
				
				parent.dimItemValArr = $.grep(parent.dimItemValArr, function(n) {
					return n.dimTypeNo !== '${dimTypeNo}';
				});
				
				parent.dimItemValArr.push({
					dimTypeNo: '${dimTypeNo}',
					value: str
				});
				row.itemArr = itemArr;
				row.filterMode = liger.get('filterMode').getValue();
				var baseWin=parent.parent.frames.base.contentWindow?parent.parent.frames.base.contentWindow:parent.parent.frames.base;
				var isSum =baseWin.liger.get('isSum').getValue();
				var dimTypeStruct = parent.alertDimValWin.row.dimTypeStruct;
				row.formulaInfo = dimFormula.dealFormula(tree, "${dimTypeNo}", isSum, dimTypeStruct, liger.get('filterMode').getValue(), itemArr);
			}
			else{
				parent.dimItemValArr = $.grep(parent.dimItemValArr, function(n) {
					return n.dimTypeNo !== '${dimTypeNo}';
				});
				parent.dimItemValArr.push({
					dimTypeNo: '${dimTypeNo}',
					value: null
				});
				row.itemArr = itemArr;
				row.formulaInfo = "";
			}
			var grid = parent.liger.get('maingrid');
			grid.reRender();
			grid.getData();
			BIONE.closeDialog('rptCmpoIdxSeltTree');
		}
	}]);
	
	
	// 树
	var setting = {
		check : {
			enable : true,
			chkStyle : "checkbox",
			chkboxType: { "Y": "", "N": "" }
		},
		data : {
			key : {
				name : "text"
			},
			simpleData : {
				enable : true,
				idKey : "id",
				pIdKey : "upId"
			}
		},
		view : {
			selectedMulti : false
		}
	};
	$.ajax({
		url: '${ctx}/report/frame/idx/getDimInfoTree?dimTypeNo=${dimTypeNo}&srcIndexNo=${srcIndexNo}',
		type: 'post',
		dataType: 'json',
		beforeSend: function(){
			BIONE.showLoading();
		},
		complete: function(){
			BIONE.hideLoading();
		},
		success: function(data) {
			var row = parent.alertDimValWin.row;
			$.each(data, function(i, n) {
				if (n.upId == null || n.upId == '0') {
					n.checked = false;//n.nocheck = true;
				};
				if (row.itemArr && $.inArray(n.id, row.itemArr) != -1) {
					n.checked = true;
				}
			});
			var tree = $.fn.zTree.init($("#tree"), setting, data);
			tree.expandNode(tree.getNodeByParam("level",0));
		}
	});
});
</script>
</head>
<body>
	<div id="template.center">
		<div>
			<form action="#" id="mode"></form>
		</div>
		<div id="container" class="container tree">
			<div id="treeContainer"
				style="width: 100%; overflow: auto; clear: both; background-color: #FFFFFF;">
				<ul id="tree"
					style="font-size: 12; background-color: #FFFFFF; width: 92%"
					class="ztree"></ul>
			</div>
		</div>
	</div>
</body>
</html>