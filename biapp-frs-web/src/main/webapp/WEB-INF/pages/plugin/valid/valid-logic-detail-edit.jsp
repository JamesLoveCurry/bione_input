<%@ page language="java" pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<html>
<head>
<meta name="decorator" content="/template/template5.jsp">
<script type="text/javascript" src="${ctx}/js/color/jscolor.js"></script>
<style>
.singlebox,#allSelect{
  margin-top:5px;
}
.tipptitle{
  margin-left:24px;
  text-align:left
}

.tipp{
  margin-left:40px;
  text-align:left
}

#fdiv{
 margin-top:5% 
}
</style>
<script type="text/javascript">
	//输入框空格校验 校验
	jQuery.validator.addMethod("blankSpaceCheck", function(value, element) {
		if(value.indexOf(" ") > -1){
			return false;
		}
	  	return true;
	 }, "输入内容存在空格!");

	var groupicon = "${ctx}/images/classics/icons/communication.gif";
	var mainform;
	var verStartDate;//开始日期
	var isQuery = "${isQuery}";
	var templateId = "${templateId}";
	var operData = [{id:"<=",text:"<="},{id:"<",text:"<"},{id:"==",text:"=="},{id:">",text:">"},{id:">=",text:">="},{id:"!=",text:"!="},{id:"in",text:"in"},{id:"not in",text:"not in"}];
	$(function() {
		$.ajax({
			cache : false,
			async : false,
			url : "${ctx}/report/frame/valid/warn/validStartDate?cfgId=${templateId}&d="
					+ new Date().getTime(),
			dataType : 'json',
			type : "get",
			success : function(result) {
				verStartDate = result;
			},
			error : function(result, b) {
				BIONE.tip('发现系统错误 <BR>错误码：' + result.status);
			}
		});
		initForm();

		jQuery.metadata.setType("attr", "validate");
		BIONE.validate($("#mainform"));

		var buttons = [];
		buttons.push({
			text : '取消',
			onclick : function() {
				BIONE.closeDialog("logicAdd");
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
			inputWidth : 250,
			labelWidth: 100,
			fields : [{
				group : "校验规则",
				groupicon : groupicon,
				display : '校验类型',
				name : 'expType',
				newline : true,
				type : 'select',
				validate : {
					required : true
				},
				options : {
					initValue : "01",
					data : [ {
						text : '逻辑校验',
						id : "01"
					}, {
						text : '正则表达式校验',
						id : '02'
					}, {
						text : '字段比较校验',
						id : '03'
					}, {
						text : '系统重要性银行所有交易对手合计校验',
						id : '04'
					}, {
						text : '系统重要性银行各交易对手合计校验',
						id : '05'
					}, {
						text : '系统重要性银行字段比较校验',
						id : '06'
					}],
					onSelected : function(val) {
						setFormCss(val);
					}
				}
			}, {
				display : "表达式描述",
				name : "expressionDesc",
				newline : true,
				type : 'text'
			},{
				display : '左表达式',
				name : 'leftExpression',
				newline : true,
				type : 'text',
				validate : {
					required : true/* ,
					blankSpaceCheck : true,
					remote : {
						url : "${ctx}/rpt/frs/rptDetailValid/validateLeftExpression",
						type : "POST"
					},
					messages : {
						remote : "表达式不合法！"
					} */
				}
			},{
				display : '运算符',
				name : 'logicOperType',
				newline : false,
				type : 'select',
				options : {
					initValue : "==",
					data : operData
				}
			}, {
				display : "右表达式",
				name : 'rightExpression',
				newline : true,
				type : 'text',
				validate : {
					required : true,
					blankSpaceCheck : true/* ,
					remote : {
						url : "${ctx}/rpt/frs/rptDetailValid/validateRightExpression",
						type : "POST"
					},
					messages : {
						remote : "表达式不合法！"
					} */
				}
			}, {
				display : "容差值",
				name : 'floatVal',
				newline : false,
				type : 'number',
				validate : {
					required : true
				}
			},{
				display : '配置前置条件',
				name : 'isPre',
				newline : true,
				type : 'select',
				validate : {
					required : true
				},
				options : {
					initValue : "N",
					data : [ {
						text : '否',
						id : "N"
					}, {
						text : '是',
						id : 'Y'
					} ],
					onSelected : function(val) {
						setPreCss(val);
					}
				}
			}, {
				display : '前置左表达式',
				name : 'preLeftExpression',
				newline : true,
				type : 'text'
			},{
				display : '前置运算符',
				name : 'preOperType',
				newline : false,
				type : 'select',
				options : {
					initValue : "==",
					data : operData
				}
			}, {
				display : '前置右表达式',
				name : 'preRightExpression',
				newline : true,
				type : 'text'
			}, {
				display : '业务说明',
				name : 'busiExplain',
				newline : true,
				type : 'textarea',
				validate : {
					maxlength : 250
				},
				attr : {
					style : "resize:none; height:50px"
				}
			} , {
				display : "校验ID",
				name : 'checkId',
				type : "hidden"
			}, {
				display : "开始日期",
				name : "startDate",
				type : "hidden",
			}, {
				display : "结束日期",
				name : "endDate",
				type : "hidden",
			}, {
				display : "报表模板ID",
				name : "rptTemplateId",
				type : "hidden"
			}, {
				display : "公式类型",
				name : "checkType",
				type : "hidden"
			}, {
				display : "公式来源",
				name : "checkSrc",
				type : "hidden"
			}]
		});
		$("#mainform input[name=expressionDesc]").parent().parent().parent("ul").hide().find("input").attr("disabled", "disabled");
		jQuery.metadata.setType("attr", "validate");
		//修改，反显数据
		if ("${checkId}") {
			$.ajax({
				cache : false,
				async : true,
				url : "${ctx}/rpt/frs/rptDetailValid/getDetailData/${checkId}?&d=" + new Date().getTime(),
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
					if (!result){
						return;
					}
					$("#mainform input[name='leftExpression']").val(result.leftExpression);
					liger.get("logicOperType").setValue(result.logicOperType);
					liger.get("logicOperType").selectValue(result.logicOperType);
					$("#mainform input[name='rightExpression']").val(result.rightExpression);
					$("#mainform input[name='floatVal']").val(result.floatVal);
					$("#mainform textarea[name=busiExplain]").val(result.busiExplain);
					liger.get("expType").setValue(result.expType);
					//前置条件配置
					//liger.get("isPre").setText(result.isPre);
					liger.get("isPre").setValue(result.isPre);
					liger.get("isPre").selectValue(result.isPre);
					setPreCss(result.isPre);
					$("#mainform input[name='preLeftExpression']").val(result.preLeftExpression);
					liger.get("preOperType").setValue(result.preOperType);
					liger.get("preOperType").selectValue(result.preOperType);
					$("#mainform input[name='preRightExpression']").val(result.preRightExpression);
					
					$("#mainform input[name='checkId']").val(result.checkId);
					$("#mainform input[name=startDate]").val(result.startDate);
					$("#mainform input[name=endDate]").val(result.endDate);
					$("#mainform input[name='rptTemplateId']").val(result.rptTemplateId);
					$("#mainform input[name='expressionDesc']").val(result.expressionDesc);
					$("#mainform input[name='checkType']").val(result.checkType);
					$("#mainform input[name='checkSrc']").val(result.checkSrc);
				},
				error : function(result, b) {
					BIONE.tip('发现系统错误 <BR>错误码：' + result.status);
				}
			});
		}else{//新增，给隐藏框赋默认值
			//是否有前置条件（默认通过1，否0）
			$("#mainform input[name='rptTemplateId']").val(templateId);
			//结束日期
			$("#mainform input[name='endDate']").val("29991231");
			//开始日期
			$("#mainform input[name='startDate']").val(verStartDate);
			//隐藏 前置条件配置
			hiddenPreCfg();
		}
	}
	
	//是否有前置条件联动
 	function setPreCss(val) {
		if (val == "Y") {
			//显示 前置条件配置
			showPreCfg();
		} else {
			//隐藏 前置条件配置
			hiddenPreCfg();
		}
	}
 	//公式类型联动
 	function setFormCss(val) {
		if (val == "03") { // 字段比较
			$("#mainform input[name=rightExpression]").parent().parent().parent("ul").hide().find("input").attr("disabled", "disabled");
			$("#mainform input[name=floatVal]").parent().parent().parent("ul").hide().find("input").attr("disabled", "disabled");
			$("#mainform input[name=isPre]").parent().parent().parent("ul").hide().find("input").attr("disabled", "disabled");
			$("#mainform input[name=expressionDesc]").parent().parent().parent("ul").hide().find("input").attr("disabled", "disabled");
		
			$("#mainform input[name=leftExpression]").parent("div").removeClass("l-text-disabled").parent()
			.parent("ul").show().find("input").removeAttr("disabled").css("color", "black");
			$("#mainform input[name=leftExpression]").attr("validate","{required : true,blankSpaceCheck : true}");
		} else if(val == "04" || val == "05" || val == "06"){
			$("#mainform input[name=expressionDesc]").parent("div").removeClass("l-text-disabled").parent()
			.parent("ul").show().find("input").removeAttr("disabled").css("color", "black");
			$("#mainform input[name=expressionDesc]").attr("validate","{required : true,blankSpaceCheck : true}");
			
			$("#mainform input[name=rightExpression]").parent().parent().parent("ul").hide().find("input").attr("disabled", "disabled");
			$("#mainform input[name=isPre]").parent().parent().parent("ul").hide().find("input").attr("disabled", "disabled");
			$("#mainform input[name=leftExpression]").parent().parent().parent("ul").hide().find("input").attr("disabled", "disabled");
		}else{
			$("#mainform input[name=leftExpression]").parent("div").removeClass("l-text-disabled").parent()
			.parent("ul").show().find("input").removeAttr("disabled").css("color", "black");
			$("#mainform input[name=leftExpression]").attr("validate","{required : true,blankSpaceCheck : true}");
			$("#mainform input[name=rightExpression]").parent("div").removeClass("l-text-disabled").parent()
			.parent("ul").show().find("input").removeAttr("disabled").css("color", "black");
			$("#mainform input[name=floatVal]").parent("div").removeClass("l-text-disabled").parent()
					.parent("ul").show().find("input").removeAttr("disabled").css("color", "black");
			$("#mainform input[name=isPre]").parent("div").removeClass("l-text-disabled").parent()
					.parent("ul").show().find("input").removeAttr("disabled").css("color", "black");
			$("#mainform input[name=rightExpression]").attr("validate","{required : true,blankSpaceCheck : true}");
			$("#mainform input[name=floatVal]").attr("validate","{required : true}");
			$("#mainform input[name=isPre]").attr("validate","{required : true,blankSpaceCheck : true}");
			$("#mainform input[name=expressionDesc]").parent().parent().parent("ul").hide().find("input").attr("disabled", "disabled");
		}
	}
	//显示 前置条件 左表达式  前置运算符  右表达式
	function showPreCfg(){
		$("#mainform input[name=preLeftExpression]").parent("div").removeClass("l-text-disabled").parent()
				.parent("ul").show().find("input").removeAttr("disabled").css("color", "black");
		$("#mainform input[name=preOperType]").parent("div").removeClass("l-text-disabled").parent()
				.parent("ul").show().find("input").removeAttr("disabled").css("color", "black");
		$("#mainform input[name=preRightExpression]").parent("div").removeClass("l-text-disabled").parent()
				.parent("ul").show().find("input").removeAttr("disabled").css("color", "black");
		$("#mainform input[name=preLeftExpression]").attr("validate","{required : true,blankSpaceCheck : true}");
		$("#mainform input[name=preOperType]").attr("validate","{required : true}");
		$("#mainform input[name=preRightExpression]").attr("validate","{required : true,blankSpaceCheck : true}");
	}
	//隐藏 前置条件 左表达式  前置运算符  右表达式
	function hiddenPreCfg(){
		$("#mainform input[name=preLeftExpression]").parent().parent().parent("ul").hide().find("input").attr("disabled", "disabled");
		$("#mainform input[name=preOperType]").parent().parent().parent("ul").hide().find("input").attr("disabled", "disabled");
		$("#mainform input[name=preRightExpression]").parent().parent().parent("ul").hide().find("input").attr("disabled", "disabled");
	}
	
	function f_save() {
		jQuery.metadata.setType("attr", "validate");
		if ($("#mainform").valid()) {
			BIONE.submitForm($("#mainform"), function() {
				window.parent.frames["detail_logic"].reloadGrid();
				BIONE.closeDialog("logicAdd", "保存成功");
			}, function() {
				BIONE.tip("保存失败");
			});
		} else {
			BIONE.tip("存在字段验证不通过，请检查！");
		}
	}
</script>
</head>
<body>
	<div id="template.center">
		<div id="center" style="width: 100%;">
			<div id="cdiv" style="width: 99.8%;float: left;padding-top: 5px;">
				<form name="mainform" method="post" id="mainform" action="${ctx}/rpt/frs/rptDetailValid/saveDetailData"></form>
			</div>
			<div id="tipAreaDiv" style="width: 99.5%;float: left;padding-top: 10px;padding-bottom: 20px;background-color: #FFFEE6;border: solid 1px #D0D0D0;">
				<div style="padding-left: 2px;">
					<div style="width: 24px; height: 16px; float: left; background-image: url('${ctx}/images/classics/icons/lightbulb.png'); background-attachment: scroll; background-repeat: no-repeat; background-position-x: 0%; background-position-y: 0%; background-size: auto; background-origin: padding-box; background-clip: border-box; background-color: transparent;"></div>
					<p>表达式配置示例</p>
					<p class="tipptitle">1、单元格号 B10+C10</p>
					<p class="tipptitle">2、批量配置，[开始行号-结束行号]列公式，如 [10-15]B+C </p>
					<p class="tipptitle">3、报表间单元格号，报表编号:单元格号，如 D1:B10+D1:C10</p>
					<p class="tipptitle">3、报表间批量配置，[开始行号-结束行号]报表编号:列公式，如 [10-15]D1:B+D1:C</p>
					<p class="tipp"></p>
				</div>
			</div>
		</div>
	</div>
</body>
</html>