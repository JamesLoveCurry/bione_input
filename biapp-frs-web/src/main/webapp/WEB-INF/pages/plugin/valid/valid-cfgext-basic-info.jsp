<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<html>
<head>
<meta name="decorator" content="/template/template5.jsp" />
<script type="text/javascript">
	// 表单相关
	jQuery.validator.addMethod("greaterThan", function(value, element, params) {
		if (value == '' || value == null) {
			return true;
		}
		var ele = $("[name=" + params + "]");
		return value > ele.val() ? true : false;
	}, "结束日期应当大于开始日期");

	jQuery.validator.addMethod("validStartDate", function(value, element,
			params) {
		if (window.parent.verStartDate != null) {
			return value >= window.parent.verStartDate;
		}
		return false;
	}, "开始日期应该大于等于模板的开始时间");

	var mainform;
	var orgType = window.parent.busiType;
	var isOrgFilter = window.parent.validObj.isOrgFilter;
	
	$(function() {
		var groupicon = "${ctx}/images/classics/icons/communication.gif";
		var templateId = window.parent.validObj.templateId;
		var checkId = window.parent.validObj.checkId;
		var orgLevel = window.parent.validObj.orgLevel;
		
		var fields = [ {
			name : "templateId",
			type : "hidden"
		}, {
			name : "checkId",
			type : "hidden"
		}, {
			display : "序号",
			name : "serialNumber",
			newline : true,
			type : "text"
		}, {
			display : "容差值",
			name : "floatVal",
			newline : true,
			type : "number"
		}, {
			display : "精度",
			name : "dataPrecision",
			newline : true,
			type : "number"
		}, {
			display: "校验频度",
			name: "logicCheckCycle",
			newline: true,
			type: "select",
			options: {
				initValue:'01',
				data : [ {
					text : '日',
					id : '01'
				}, {
					text : '月',
					id : '02'
				}, {
					text : '季',
					id : '03'
				}, {
					text : '年',
					id : '04'
				}, {
					text : '周',
					id : '10'
				}, {
					text : '旬',
					id : '11'
				}, {
					text : '半年',
					id : '12'
				} ]
			}
		}, {
			display: "数据单位",
			name: "dataUnit",
			newline: true,
			type: "select",
			options: {
				initValue: '01',
				data: [{
					"id": "01",
					"text": "元"
				}, {
					"id": "02",
					"text": "百"
				}, {
					"id": "03",
					"text": "千"
				}, {
					"id": "04",
					"text": "万"
				}, {
					"id": "05",
					"text": "亿"
				}, {
					"id": "06",
					"text": "百分比"
				}]
			}
		}, {
			display : "是否预校验",
			name : "isPre",//edit by fangjuan 2014-07-22
			newline : true,
			type : "hidden",
			validate : {
				maxlength : 500
			},
			options : {
				initValue : '0',
				data : [ {
					"id" : "0",
					"text" : "否"
				}, {
					"id" : "1",
					"text" : "是"
				} ]
			},
			validate : {
				required : true,
				messages : {
					required : "是否预校验不能为空。"
				}
			}
		}, {
			display : "开始日期",
			name : "startDate",
			newline : true,
			type : "hidden"
		}, {
			display : "结束日期",
			name : "endDate",
			newline : true,
			type : "hidden"
		}, {
			display : "公式来源",
			name : "checkSrc",
			newline : true,
			type : "select",
			validate : {
				maxlength : 500
			},
			options : {
				initValue : '01',
				data : [ {
					"id" : "01",
					"text" : "监管制度"
				}, {
					"id" : "02",
					"text" : "自定义"
				} ]
			}
		}, {
			display : "是否机构过滤",
			name : "isOrgFilter",
			newline : true,
			type : "select",
			validate : {
				maxlength : 500
			},
			options : {
				initValue : 'N',
				onSelected : function(id, value) {
					isShowOrg(id);
				},
				data : [ {
					"id" : "N",
					"text" : "否"
				}, {
					"id" : "Y",
					"text" : "是"
				} ]
			}
		}, {
			display : "机构名称",
			name : "checkOrg",
			newline : true,
			type : "select",
			cssClass : "field",
			comboboxName : "orgNm_sel",
			options : {
				onBeforeOpen : function() {
					if (orgType) {
						var height = $(window).height() - 120;
						var width = $(window).width() - 480;
						window.BIONE.commonOpenDialog(
								"机构树",
								"taskOrgWin",
								width,
								height,
								"${ctx}/frs/integratedquery/rptquery/searchOrgSetInfo?orgType=" + orgType,
								null);
						return false;
					} else {
						BIONE.tip("请选择报送模块！");
					}
				},
				cancelable : true
			}
		}, {
			display : "业务说明",
			name : "busiExplain",//edit by fangjuan 2014-07-22
			newline : true,
			type : "textarea"
		} ];
		if (window.parent.validObj.logicOperType == "==") {
			fields[3] = {
				display : "容差值",
				name : "floatVal",
				newline : true,
				type : "number",
				validate : {
					required : true,
					messages : {
						required : "容差值不能为空。"
					}
				}
			};
		}
		//渲染表单
		mainform = $("#mainform");
		mainform.ligerForm({
			inputWidth : 250,
			labelWidth : 150,
			space : 30,
			fields : fields

		});

		jQuery.metadata.setType("attr", "validate");
		BIONE.validate("#mainform");
		
		//表单赋值
		$("#mainform input[name='templateId']").val("${templateId}");
		$("#mainform input[name='checkId']").val("${checkId}");

		if (checkId != null && !checkId == "") {
			//liger.get('isPre').setValue(window.parent.validObj.isPre);
			liger.get('dataUnit').setValue(window.parent.validObj.dataUnit);
			$("#mainform input[name='dataPrecision']").val(window.parent.validObj.dataPrecision);
			$("#mainform input[name='isSelfDef']").val(window.parent.validObj.isSelfDef);
			//$("#mainform input[name='isPre']").val(window.parent.validObj.isPre);
			$("#mainform input[name='floatVal']").val(window.parent.validObj.floatVal);
			$("#mainform input[name='serialNumber']").val(window.parent.validObj.serialNumber);
			$("#mainform input[name='startDate']").val(window.parent.verStartDate);
			$("#mainform input[name='endDate']").val(window.parent.validObj.endDate);
			$("#mainform textarea[name='busiExplain']").val(window.parent.validObj.busiExplain);
			$.ligerui.get("isOrgFilter").setValue(isOrgFilter ? isOrgFilter : "N");
			$.ligerui.get('orgNm_sel').setValue(window.parent.validObj.orgNo);
			$.ligerui.get('orgNm_sel').setText(window.parent.validObj.orgNm);
			$.ligerui.get("checkSrc").setValue(window.parent.validObj.checkSrc ? window.parent.validObj.checkSrc : "01");
			$.ligerui.get("logicCheckCycle").setValue(window.parent.validObj.logicCheckCycle ? window.parent.validObj.logicCheckCycle : "01");
		} else {
			$("#mainform input[name='floatVal']").val("200");
			$("#mainform input[name='endDate']").val("29991231");
			$("#mainform input[name='startDate']").val(window.parent.verStartDate);
		}

		//添加按钮
		var btns = [ {
			text : '上一步',
			onclick : function() {
				window.parent.last();
			}
		} ];
		if (!window.parent.isQuery) {
			btns.push({
				text : '保存',
				onclick : f_save
			});
		}
		BIONE.addFormButtons(btns);
		isShowOrg(isOrgFilter ? isOrgFilter : "N");
	});

	//控制是否显示机构过滤
	function isShowOrg(id) {
		if (id == "Y") {
			$("#orgNm_sel").parent().parent().parent().parent().show();
		} else {
			$("#orgNm_sel").parent().parent().parent().parent().hide();
		}
	}
	
	//保存
	function f_save() {
		jQuery.metadata.setType("attr", "validate");
		BIONE.validate("#mainform");
		var expressionShortDesc = window.parent.validObj.expressionShortDesc;
		if ($("#mainform").valid()) {
			var data = {
				checkSrc : $("#mainform input[name='checkSrc']").val(),
				isSelfDef : $("#mainform input[name='isSelfDef']").val(),
				isPre : "0",//目前产品没有预校验功能，先默认赋值0，也就是不进行预校验
				floatVal : $("#mainform input[name='floatVal']").val(),
				startDate : $("#mainform input[name='startDate']").val(),
				endDate : $("#mainform input[name='endDate']").val(),
				expressionDesc : window.parent.validObj.expressionDesc,
				expressionShortDesc : expressionShortDesc,
				leftExpression : window.parent.validObj.leftExpression,
				rightExpression : window.parent.validObj.rightExpression,
				logicOperType : window.parent.validObj.logicOperType,
				templateId : window.parent.validObj.templateId,
				checkId : window.parent.validObj.checkId,
				busiExplain : $("#mainform textarea[name='busiExplain']").val(),
				leftFormulaIndex : window.parent.validObj.leftFormulaIndex,
				rightFormulaIndex : window.parent.validObj.rightFormulaIndex,
				leftFormulaDs : window.parent.validObj.leftFormulaDs,
				rightFormulaDs : window.parent.validObj.rightFormulaDs,
				isOrgFilter : $("#mainform input[name='isOrgFilter']").val(),
				orgNo : $.ligerui.get('orgNm_sel').getValue(),
				isInnerCheck : window.parent.validObj.isInnerCheck,
				dataUnit : $("#mainform input[name='dataUnit']").val(),//新增容差值展示单位
				serialNumber : $("#mainform input[name='serialNumber']").val(),//新增序号
				dataPrecision : $("#mainform input[name='dataPrecision']").val(),//精度
				busiType : window.parent.validObj.busiType,
				logicCheckCycle : $("#mainform input[name='logicCheckCycle']").val()
			}
			if(data.isOrgFilter == 'Y'){
			    if(data.orgNo == null || data.orgNo == ''){
					BIONE.tip('请选择过滤机构!');
					return;
                }
            }

			$.ajax({
				cache : false,
				async : true,
				url : "${ctx}/report/frame/valid/logic",
				dataType : 'json',
				data : data,
				type : "post",
				beforeSend : function() {
					BIONE.loading = true;
					BIONE.showLoading("正在加载数据中...");
				},
				complete : function() {
					BIONE.loading = false;
					BIONE.hideLoading();
				},
				success : function(result) {
					BIONE.tip("保存成功");
					parent.parent.findIframeByTitle("校验设置")[0].contentWindow.frames.logic.reloadGrid();
					parent.BIONE.closeDialog("logicAdd");
				},
				error : function(result, b) {
					BIONE.tip('发现系统错误 <BR>错误码：' + result.status);
				}
			});
		}
	}
</script>
<title>Insert title here</title>
</head>
<body>
	<div id="template.center">
		<form id="mainform" action="${ctx}/report/frame/valid/logic"
			method="POST"></form>
	</div>
</body>
</html>