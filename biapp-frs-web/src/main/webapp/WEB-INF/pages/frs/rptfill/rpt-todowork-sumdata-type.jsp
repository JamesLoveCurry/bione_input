<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<html>
<meta name="decorator" content="/template/template14.jsp">
<head>
<script type="text/javascript">
	var mainform = null;
	var exhibition = "${exhibition}";
	$(function() {
		initForm();
	});
	
	function initForm() {
		$("#form").height($("#center").height() - 75);
		mainform = $("#basicform").ligerForm({
			fields : [{
				display : "汇总方式",
				name : "sumType",
				newline : false,
				type : "select",
				comboboxName : "sumTypeCombo",
				options : {
					initValue:'01',
					data : [ {
						text : '从最底层机构进行汇总',
						id : '01'
					}, {
						text : '只汇总下一级机构数据',
						id : '02' 
					}]
				}
			}]
		});
		initBtn();
	}
	
	function initBtn() {
		var btns = [ {
			text : '取消',
			onclick : function() {
				BIONE.closeDialog("sumDataType");
			}
		}, {
			text : '汇总',
			onclick : sumData
		} ];
		BIONE.addFormButtons(btns);
	}
	
	function sumData() {
		window.parent.base_SumType = $.ligerui.get("sumTypeCombo").getValue();
		if("list" == exhibition){//填报列表
			window.parent.tmp.oper_datasum();
		}else{//填报详细页
			window.parent.tmp.sumDataInfo();
		}
		BIONE.closeDialog("sumDataType");
	}
</script>
</head>
<body>
	<div id="template.center">
		<div id="form">
			<form id="basicform" action="" method="post"></form>
		</div>
		<div id="bottom">
			<div class="form-bar">
				<div class="form-bar-inner" style="padding-top: 10px; padding-right: 20px"></div>
			</div>
		</div>
	</div>
</body>
</html>