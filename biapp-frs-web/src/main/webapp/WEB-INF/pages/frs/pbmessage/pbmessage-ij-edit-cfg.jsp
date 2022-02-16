<%@ page language="java" pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<html>
<meta name="decorator" content="/template/template5.jsp">
<head>
<script type="text/javascript">
    var verId = window.parent.verId;
    var groupicon = "${ctx}/images/classics/icons/communication.gif";
	$(function() {
		initOrgCodeForm();
		addButtons();
	});
	
	function initOrgCodeForm() {
		var width = $(window).width() - 178;
		$('#mainform').ligerForm({
			fields : [  {
				display : "数据属性",
				name : "dataAttr",
				newline : true,
				type:"select",
				group : "统一配置属性",
				groupicon : groupicon,
				options :{
					data:[{
						text : '不配置',
						id : "0"
					},{
						text : '余额',
						id : "1"
					},{
						text : '发生额',
						id : "2"	
					}]
				}
			},{
				display : "币种",
				name : "currtype",
				newline : true,
				type:"select",
				options :{
					data:[{
						text : '不配置',
						id : "0"
					},{
						text : '人民币(CNY0001)',
						id : "CNY0001"
					},{
						text : '美元(USD0002)',
						id : "USD0002"	
					},{
						text : '本外币(BWB0001)',
						id : "BWB0001"	
					}]
				}
			},{
				display : "报送范围", 
				name : "submitRange", 
				newline : true, 
				type : "select", 
		        comboboxName: "submitRangeBox",
		        options:{
		        	onBeforeOpen : selectRangeDialog
		        },
				validate : { 
					required : true
				}
			}]
		});
		$.ligerui.get("dataAttr").selectValue("0");
		$.ligerui.get("currtype").selectValue("0");
		$.ligerui.get("submitRangeBox").selectValue("0");
		jQuery.metadata.setType("attr", "validate");
		BIONE.validate($('#mainform'));
	}
	
	//弹出窗口
    function selectRangeDialog(options) {
    	var height = $(window).height() - 60;
		var width = $(window).width() - 90;
		$.ligerDialog.open({
			name:'addRangeWin',
			title : '报送范围选择',
			width : width,
			height : height,
			url : '${ctx}/frs/pbmessage/selectRangeDialog',
			buttons : [ {
				text : '确定',
				onclick : f_selectOK
			}, {
				text : '取消',
				onclick : f_selectCancel
			} ]
		});
		return false;
	}
	
  //保存按钮调用方法
	function f_selectOK(a,dialog){
		var data = dialog.frame.addToParent();
		if(data!=""){
			$("#mainform input[name='submitRange']").val(data.submitRangeCode);
			$("#mainform input[name='submitRangeBox']").val(data.submitRangeNm);
		}
		dialog.close();
	}
	//取消按钮调用方法
	function f_selectCancel(a,dialog){
		dialog.close();
	}
	function addButtons() {
		BIONE.addFormButtons([{
			text : '取消',
			width : '80px',
			onclick : cancelButton
		}, {
			text : '保存', 
			width : '80px',
			onclick : saveButton
		} ]);
	}
	
	//保存按钮
	function saveButton(){
		var rows = window.parent.rows;
		var grid = window.parent.grid;
		var dataAttr = $("#dataAttr").val();
		var currtype = $("#currtype").val();
		var submitRange = $.ligerui.get("submitRangeBox").getValue();
		var indexNos=[];
		if ($('#mainform').valid()) {
			for(var i in rows){
				if(dataAttr != "0"){
					grid.updateCell("dataAttr",dataAttr,rows[i])
				}
				if(currtype != "0"){
					grid.updateCell("currtype",currtype,rows[i])
				}
				grid.updateCell("dtrctNo",submitRange,rows[i])
				indexNos.push(rows[i].id.indexNo);
			}
			BIONE.closeDialogAndReloadParent("codeConfig", "maingrid", null);
		}
	}
	
	function cancelButton(){
		BIONE.closeDialog("codeConfig");
	}
</script>
</head>
<body style="width: 80%">
	<div id="template.center">
		<form name="mainform" method="post" id="mainform"></form>
	</div>
</body>
</html>