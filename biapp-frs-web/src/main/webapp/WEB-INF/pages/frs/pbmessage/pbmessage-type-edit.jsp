<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<html>
<meta name="decorator" content="/template/template5.jsp">
<head>
<script type="text/javascript">
	var groupicon = "${ctx}/images/classics/icons/communication.gif";
	var verId = window.parent.verId;
	//创建表单结构 
	var mainform;
	$(function() {
		mainform = $("#mainform");
		mainform.ligerForm({
			fields : [ 
				{
					name : "reportCodeCfgNo",
					type : "hidden"
				},{
					name : "verId",
					type : "hidden"
				 },
				 {
					display : "数据属性",
					name : "dataAttr",
					newline : true,
					type : "select",
					width : '200',
					group : "上报类型信息",
					groupicon : groupicon,
					validate : {
						required : true
					},
					comboboxName : "dataAttr_sel",
					options : {
						initValue:'1',
						data : [{
							text : '余额',
							id : "1"
						},{
							text : '发生额',
							id : "2"	
						}]
					}
				}, {
					display : "币种属性",
					name : "currtype",
					newline : true,
					type : "select",
					width : '200',
					validate : {
						required : true
					},
					comboboxName : "currtype_sel",
					options : {
						initValue:'CNY0001',
						data : [{
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
					name : "submitRangeCode", 
					width : '200',
					newline : true, 
					type : "select", 
			        comboboxName: "submitRangeCodeBox",
			        options:{
			        	onBeforeOpen : selectRangeDialog
			        },
					validate : { 
						required : true
					}
				}
			]
		});
		$("#mainform input[name='verId']").val(verId);
		jQuery.metadata.setType("attr", "validate");
		BIONE.validate(mainform);
		var buttons = [];
		buttons.push({
			text : '取消',
			onclick : cancle
		});
		buttons.push({
			text : '保存',
			onclick : save
		});
		BIONE.addFormButtons(buttons);

	});
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
			$("#mainform input[name='submitRangeCode']").val(data.submitRangeCode);
			$("#mainform input[name='submitRangeCodeBox']").val(data.submitRangeNm);
		}
		dialog.close();
	}
	//取消按钮调用方法
	function f_selectCancel(a,dialog){
		dialog.close();
	}
	
	//保存方法
	function save() {
		var reportCodeCfgNos = window.parent.reportCodeCfgNos;
		$.ajax({
			cache : false,
			async : true,
			url : "${ctx}/frs/pbmessage/savetype",
			dataType : 'json',
			type : "post",
			data : {
				reportCodeCfgNos : reportCodeCfgNos.join(","),
				dataAttr : $("#dataAttr").val(),
				currtype : $("#currtype").val(),
				submitRangeCode : $("#submitRangeCode").val(),
				verId:verId
			},
			success : function(result) {
				BIONE.closeDialogAndReloadParent("editType", "maingrid", "保存成功");
			},
			error : function(result, b) {
				BIONE.closeDialog("editType", "保存失败");
				//BIONE.tip('发现系统错误 <BR>错误码：' + result.status);
			}
		});
	}
	//取消方法
	function cancle() {
		BIONE.closeDialog("editType");
	}
</script>
</head>
<body>
	<div id="template.center">
		<form id="mainform" action="${ctx}/frs/pbmessage/savetype" method="post"></form>
	</div>
</body>
</html>