<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<html>
<meta name="decorator" content="/template/template5_BS.jsp">
<head>
<script type="text/javascript">
	var dialog,grid,data,ruleType,dataType;
	
	$(function() {
		init();
	    initGrid();
	    initButtons();
	});
	
	function init() {
		ruleType = window.parent.ruleType;
		dataType = window.parent.dataType;
	}
	
	function initGrid() {
		grid = $("#mainform").ligerGrid({
		columns : [ {
			display : '数据模型',
			name : 'tabName',
			width : '15%',
			align : 'center'
		},{
			display : '数据项',
			name : 'colNameEn',
			width : '15%',
			align : 'center'
		},{
			display:'数据项名称',
			name:'colName',
			width:'15%',
			align:'left'
		}, {
			display : '规则标识',
			name : 'id',
			width : '10%',
			align:'center',
			editor: { 
				type: 'int'
			}
		}, {
			display : '规则名称',
			name : 'ruleName',
			width : '15%',
			align : 'center',
			editor: { 
				type: 'text'
			}
		}, {
			display : '前置条件',
			name : 'cond',
			width : '10%',
			align : 'center',
			editor: { 
				type: 'text'
			}
		}],
	    checkbox:true,
		enabledEdit : true,
		rownumbers : true,
		isScroll : false,
		alternatingRow : true,//附加奇偶行效果行
		colDraggable : false,
		dataAction : 'server',//从后台获取数据
		method : 'post',
		url : "${ctx}/east/rules/checkRule/definitionRule",
		parms :{
			ruleType : ruleType,
			dataType : dataType
		},
		width : '100%',
		height : '100%',
		toolbar : {},
		isChecked: f_isChecked,
		onCheckRow : f_onCheckRow
	})
	};
	
	function f_onCheckRow(rowdata) {
		if (true == rowdata) {
			$.ligerDialog.alert('必填项：规则标识，规则名称');
		} else {
			$.ligerDialog.confirm('确定取消选中？',function (r) {
			});
		}
		
    }
	
	function f_isChecked(rowdata) {
		if (rowdata.id != null) 
			return true;
        return false;
    }
	
	function initButtons() {
		buttons = [];
		buttons.push({
			text : '取消',
			onclick : function(){
				window.parent.BIONE.closeDialog("addRule");
			} 
		}, {
			text : '重置',
			onclick : function(){
				grid.loadData();
			} 
		}, {
			text : '下一步',
			onclick : f_next
		});
		
		BIONE.addFormButtons(buttons);
		jQuery.metadata.setType("attr", "validate");
		BIONE.validate("#mainform");
	};

	// 下一步
	function f_next() {
		window.parent.selectRuleData = grid.selected;
		window.parent.tabObj.selectTabItem("warn");
	}
	
</script>
</head>
<body>
	<div id="template.center" style="height: 100%">
		<form id="mainform" action="${ctx}/bione/admin/userauth/authLDAP/saveLdap" method="post"></form>
	</div>
</body>
</html>