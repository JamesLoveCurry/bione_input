<%@ page language="java" pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<html>
<meta name="decorator" content="/template/template5.jsp">
<head>
<script type="text/javascript">
	jQuery.validator.addMethod("startDateCheck", function(value, element) {
		var startDateVal = $("#startDate").val();
		var endDateVal = $("#endDate").val();
		if (startDateVal != "" && endDateVal != ""
				&& startDateVal >= endDateVal) {
			return false;
		} else {
			return true;
		}
	}, "启用时间应小于结束时间");
	jQuery.validator.addMethod("endDateCheck", function(value, element) {
		var startDateVal = $("#startDate").val();
		var endDateVal = $("#endDate").val();
		if (startDateVal != "" && endDateVal != ""
				&& startDateVal >= endDateVal) {
			return false;
		} else {
			return true;
		}
	}, "结束时间应大于启用时间");
	var frsSystemCfg = '${frsSystemCfg}';
	var readonly = false;
	if (frsSystemCfg) {
		frsSystemCfg = JSON2.parse(frsSystemCfg);
		readonly = true;
	}
	var mainform;
	
	$(function() {
		var groupicon = "${ctx}/images/classics/icons/communication.gif";
		mainform = $("#mainform").ligerForm({
			inputWidth : 160,
			labelWidth : 100,
			space : 30,
			align : "center",
			fields : [ {
				name : 'systemVerId',
				type : 'hidden'
			}, {
				group : "制度版本信息",
				groupicon : groupicon,
				display : "业务类型 ",
				name : "busiType",
				newline : false,
				width : 215,
				type : "select",
				comboboxName : "busi_type_box",
				width : 215,
				validate : {
					required : true,
					messages : {
						required : "请选择一业务类型"
					}
				},
				options : {
					readonly : readonly,
					url : "${ctx}/report/frame/datashow/idx/getIdxTaskType.json",
					onBeforeSelect : function(selectBusiType) {
						$("#systemName").rules("add",{remote:{
							url:"${ctx}/frs/system/cfg/checkSystemName",
							type : "post",
							data : {
								busiType : selectBusiType
							}
						},messages:{remote:"制度包名称已存在"}});
					}
				}
			}, {
				display : "制度版本名称",
				name : "systemName",
				newline : false,
				width : 215,
				type : "text",
				validate : {
					required : true,
					maxlength : 100
				}
			}, {
				display : '启用时间',
				name : 'verStartDate',
				id : 'verStartDate',
				newline : true,
				width : 215,
				type : 'date',
				validate : {
					startDateCheck : true,
					required : true
				},
				options : {
					format : "yyyyMMdd",
					readonly : readonly
				}
			}, {
				display : '结束时间',
				name : 'verEndDate',
				id : 'verEndDate',
				newline : false,
				width : 215,
				type : 'date',
				options : {
					format : "yyyyMMdd",
					readonly : true
				}
			}, {
				display : "备注 ",
				name : "remark",
				newline : true,
				type : "textarea",
				width : 543,
				validate : {
					maxlength : 500
				}
			} ]
		});

		var buttons = [];
		buttons.push({
			text : '取消',
			onclick : f_close
		});
		buttons.push({
			text : '保存',
			onclick : f_save
		});
		BIONE.addFormButtons(buttons);

		jQuery.metadata.setType("attr", "validate");
		BIONE.validate("#mainform");

		if (frsSystemCfg) {
			$("#systemVerId").val(frsSystemCfg.id.systemVerId);
			$("#systemName").val(frsSystemCfg.systemName);
			$.ligerui.get('busi_type_box')
					.selectValue(frsSystemCfg.id.busiType);
			$("#verStartDate").val(frsSystemCfg.verStartDate);
			$("#verEndDate").val(frsSystemCfg.verEndDate);
			$("#remark").val(frsSystemCfg.remark);
		}
	});

	function f_save() {
		BIONE.submitForm($("#mainform"), function(result) {
			if(result && result.mag){
				BIONE.tip(result.mag);
				return;
			}else{
				BIONE.tip("保存成功");
				window.parent.grid.reload();
				BIONE.closeDialog("addSystem");	
			}
		}, function() {
			BIONE.tip("保存失败");
		});
	}

	function f_close() {
		BIONE.closeDialog("addSystem");
	}
</script>

<title>制度版本管理</title>
</head>
<body>
	<div id="template.center">
		<form name="mainform" method="post" id="mainform"
			action="${ctx}/frs/system/cfg/save"></form>
	</div>
</body>
</html>