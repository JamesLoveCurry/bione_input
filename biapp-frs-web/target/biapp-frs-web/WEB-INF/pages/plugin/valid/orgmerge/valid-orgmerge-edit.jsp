<%@ page language="java" pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<html>
<head>
<meta name="decorator" content="/template/template5.jsp">
<script type="text/javascript" src="${ctx}/js/color/jscolor.js"></script>
<script type="text/javascript">
	var groupicon = "${ctx}/images/classics/icons/communication.gif";
	var mainform;
	var sumTemplateId = "${templateId}";
	var isQuery = "${isQuery}";
	var checkId = "${checkId}";
	var startDate = "${startDate}";
	var branchTemplateId;
	$(function() {
		initForm();
		BIONE.validate($("#mainform"));
		var buttons = [];
		buttons.push({
			text : '取消',
			onclick : function() {
				BIONE.closeDialog("orgMergeAdd");
			}
		});
		buttons.push({
			text : '保存',
			onclick : f_save
		});
		if(!isQuery){
			BIONE.addFormButtons(buttons);
		}
	});
	function initForm() {
		mainform = $("#mainform").ligerForm({
			fields : [{
				group : "校验配置信息",
				groupicon : groupicon,
				display : '总行指标编号',
				name : 'sumIndexNo',
				comboboxName : "sumIndexNoCombo",
				newline : true,
				labelWidth : '120px',
				type : 'select',
				options : {
					onBeforeOpen : function() {
						window.parent.BIONE .commonOpenDialog("双击选择总行指标", "chooseIndex", 950, 500, 
								'${ctx}/report/frame/valid/orgmerge/chooseIndex?templateId=' + sumTemplateId +"&type=sum");
						return false;
					},
					cancelable:true
				},
				validate : {
					required : true
				}
			}, {
				display: '分行报表',
				name :'branchTemplateId',
				comboboxName:'branchTemplateIdCombo',
				type: 'select',
				labelWidth : '120px',
				newline: true,
				options : {
					onBeforeOpen : function() {
						BIONE.commonOpenDialog("报表选择", "rptIdxTreeWin", 480, 380, 
								'${ctx}/report/frame/valid/orgmerge/chooseRpt?busiType=02', null);
						return false;
					},
					cancelable:true
				},
				validate:{
					required: true
				}
			}, {
				display : '分行指标编号',
				name : 'branchIndexNo',
				comboboxName : "branchIndexNoCombo",
				newline : false,
				labelWidth : '120px',
				type : 'select',
				options : {
					onBeforeOpen : function() {
						//debugger;
						window.parent.BIONE .commonOpenDialog("双击选择分行指标", "chooseIndex", 950, 500, 
								'${ctx}/report/frame/valid/orgmerge/chooseIndex?templateId=' + branchTemplateId +"&type=branch");
						return false;
					},
					cancelable:true
				},
				validate : {
					required : true
				}
			}, {
				display : '校验描述',
				name : 'checkDesc',
				newline : true,
				type : 'textarea',
				labelWidth : '120px',
				validate : {
					maxlength : 2000
				},
				width : 550,
				attr : {
					style : "resize:none; height:50px"
				}
			}, {
				display : "开始日期",
				name : "startDate",
				type : "hidden",
			}, {
				display : "结束日期",
				name : "endDate",
				type : "hidden",
			}, {
				display : "校验id",
				name : "checkId",
				type : "hidden",
			} ]
		});
		jQuery.metadata.setType("attr", "validate");
		//debugger;
		if (checkId) {//修改，反显数据
			$.ajax({
				cache : false,
				async : true,
				url : "${ctx}//report/frame/valid/orgmerge/getFormData?checkId="+checkId+"&d="+ new Date().getTime(),
				dataType : 'json',
				type : "get",
				beforeSend : function() {
					BIONE.loading = true;
					BIONE.showLoading("正在加载数据中...");
				},
				complete : function() {
					BIONE.loading = false;
					BIONE.hideLoading();
				},
				success : function(result) {
					if (result){
						liger.get("sumIndexNoCombo").setValue(result.sumIndexNo);
						liger.get("sumIndexNoCombo").setText(result.sumIndexNo);
						liger.get("branchIndexNoCombo").setValue(result.branchIndexNo);
						liger.get("branchIndexNoCombo").setText(result.branchIndexNo);
						liger.get("branchTemplateIdCombo").setValue(result.branchTemplateId);
						liger.get("branchTemplateIdCombo").setText(result.branchRptNm);
						$("#checkDesc").val(result.checkDesc);
						$("#startDate").val(result.startDate);
						$("#endDate").val(result.endDate);
					}
				},
				error : function(result, b) {
					BIONE.tip('发现系统错误 <BR>错误码：' + result.status);
				}
			});
		}else{//新增，给隐藏框赋默认值
			//结束日期
			$("#mainform input[name='endDate']").val("29991231");
			//开始日期
			$("#mainform input[name='startDate']").val(startDate);
		}
	}
	function f_save() {
		if ($("#mainform").valid()) {
			var data = {
				checkId : checkId,
				sumTemplateId : sumTemplateId,
				sumIndexNo : $.ligerui.get("sumIndexNoCombo").getValue(),
				branchIndexNo : $.ligerui.get("branchIndexNoCombo").getValue(),
				branchTemplateId : $.ligerui.get("branchTemplateIdCombo").getValue(),
				checkDesc : $("#checkDesc").val(),
				startDate : $("#startDate").val(),
				endDate : $("#endDate").val()
			};
			$.ajax({
				type : "POST",
				dataType : 'json',
				url : "${ctx}/report/frame/valid/orgmerge/saveData",
				data : data,
				success : function() {
					window.parent.frames["orgMerge"].reloadGrid()
					BIONE.closeDialog("orgMergeAdd", "保存成功");
				},
				error : function(msg) {
				}
			});
		} else {
			BIONE.tip("存在字段验证不通过，请检查！");
		}
	}
</script>
</head>
<body>
	<div id="template.center">
		<form name="mainform" method="post" id="mainform"></form>
	</div>
</body>
</html>