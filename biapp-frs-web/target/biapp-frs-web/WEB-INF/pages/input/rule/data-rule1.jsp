<%@ page language="java" pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<%@ page
	import="org.apache.shiro.web.filter.authc.FormAuthenticationFilter"%>
<html>
<meta name="decorator" content="/template/template5.jsp">
<head>
<script type="text/javascript" src="${ctx}/js/udip/remark/global.js"></script>
<script type="text/javascript" src="${ctx}/js/udip/remark/rule.js"></script>
<script type="text/javascript">
	var groupicon = "${ctx}/images/classics/icons/communication.gif";
	var selection = [ {
		id : "",
		text : ""
	} ];
	var sysParams = [], minDate = '', maxDate = '';
	var dataRangerSelect = [ {
		id : "",
		text : "请选择"
	} ];
	var maxyear, maxmonth, maxday, minyear, minmonth, minday;
	//创建表单结构 
	var mainform;
	$(function() {
		$('#center').append('<div id="checkLabelContainer" type="hidden" style="margin-left: 7px;"></div>');
		$.ajax({
			async : false,
			url : '${ctx}/rpt/input/data/getColumnList/' + "${id}",
			success : function(data1) {
				selection = selection.concat(data1);
			}
		});
		$.ajax({
			async : false,
			url : '${ctx}/rpt/input/rule/getSysParams',
			success : function(data1) {
				sysParams = data1;
			}
		});
		$.ajax({
			async : false,
			url : '${ctx}/rpt/input/rule/getDataRangerSelection',
			success : function(data1) {
				dataRangerSelect = dataRangerSelect.concat(data1)
			}
		});

		mainform = $("#mainform");
		mainform.ligerForm({
			fields : [
					{
						name : "templeId",
						type : "hidden"
					},
					{
						name : "ruleId",
						type : "hidden"
					},
					{
						display : "规则名称<font color='red'>*</font>",
						name : "ruleNm",
						newline : true,
						type : "text",
						validate : {
							remote : {
								type : 'get',
								url : "${ctx}/rpt/input/rule/checkRuleName?d=" + new Date().getTime(),
								data : {
									ruleId : "${ruleId}",
									templeId : "${id}"
								}
							},
							messages : {
								remote : "规则名已存在，请检查！"
							},
							required : true,
							maxlength : 200
						}
					}, {
						display : "约束字段<font color='red'>*</font>",
						name : 'columnNameValue',
						newline : true,
						type : 'select',
						options : {
							valueFieldID : 'fieldNm',
							data : selection
						},
						validate : {
							required : true
						}
					}/* , {
						display : "时间区间",
						name : "dateRangeValue",
						newline : true,
						type : 'select',
						options : {
							valueFieldID : 'timeRange',
							data : dataRangerSelect,
							onSelected : function(val) {
								setCss(val);
							}
						}
					} */, {
						display : "值范围",
						name : 'value1',
						newline : true,
						width : 90,
						space : 1,
						type : 'select',
						options : {
							valueFieldID : 'value2',
							data : [ {
								text : '大于',
								id : '{'
							}, {
								text : '大于等于',
								id : '['
							} ]
						}
					}, {
						display : "",
						space : 1,
						width : 90,
						labelWidth : 5,
						name : "minVal",
						newline : false,
						type : "text",
						ligerui : {
							type : 'int'
						}
					}, {
						display : "",
						space : 1,
						width : 90,
						labelWidth : 5,
						name : 'value3',
						newline : false,
						type : 'select',
						options : {
							valueFieldID : 'value4',
							data : [ {
								text : '小于',
								id : '{'
							}, {
								text : '小于等于',
								id : '['
							} ]
						}
					}, {
						display : "",
						space : 1,
						width : 90,
						labelWidth : 5,
						name : "maxVal",
						newline : false,
						type : "text",
						ligerui : {
							type : 'int'
						}
					}, {
						display : "数值集合",
						name : "valueSet",
						newline : true,
						type : 'text',
						validate : {
							maxlength : 4000
						}
					}, {
						display : "提示信息<font color='red'>*</font>",
						name : "errorTip",
						newline : true,
						type : "textarea",
						width : 380,
						attr : {
							style : "resize: none;"
						},
						validate : {
							required : true,
							maxlength : 300
						}
					} ]
		});

		if ("${ruleId}") {
			$.ajax({
				url : "${ctx}/rpt/input/rule/findRuleInfo?ruleId=" + "${ruleId}&d=" + new Date().getTime(),
				success : function(data) {
					$("#mainform [name='ruleNm']").val(data.ruleNm);
					$("#mainform [name='ruleId']").val(data.ruleId);
					liger.get('columnNameValue')._changeValue(data.fieldNm, data.fieldNm);
					if (data.maxVal != null) {
						var max = data.maxVal.substring(1, data.maxVal.length - 1);
						$("#mainform [name='maxVal']").val(max);
						var s = liger.get('value3');
						if (data.maxVal.charAt(0) == '{') {
							s.setValue('{');
							s.setText('小于');
						} else {
							s.setValue('[');
							s.setText('小于等于');
						}
					}
					if (data.minVal != null) {
						var min = data.minVal.substring(1, data.minVal.length - 1);
						$("#mainform [name='minVal']").val(min);
						var l = liger.get('value1');
						if (data.minVal.charAt(0) == '{') {
							l.setValue('{');
							l.setText('大于');
						} else {
							l.setValue('[');
							l.setText('大于等于');
						}
					}

// 					if (data.timeRange != null) {
// 						$("#mainform [name='timeRange']").val(data.timeRange);
// 						// liger.get('dateRangeValue').selectValue(data.timeRange);
// 					}
					$("#mainform [name='valueSet']").val(data.valueSet);
					$("#mainform [name='errorTip']").val(data.errorTip);
				}
			});
		} else {
			$("#valueSet").ligerTextBox({
				nullText : '例如：1,2,3'
			});
		}
		jQuery.metadata.setType("attr", "validate");
		BIONE.validate(mainform);

		var buttons = [];
		buttons.push({
			text : '关闭',
			onclick : cancleCallBack
		});
		if (!"${lookType}") {
			buttons.push({
				text : '确定',
				onclick : save_objDef
			});
		}
		BIONE.addFormButtons(buttons);

		$("#mainform [name='templeId']").val("${id}");
		// liger.get('dateRangeValue').selectValue("");
		liger.get('columnNameValue').selectValue("");
		check();
	});

	function cancleCallBack() {
		if("${ruleId}"){
			BIONE.closeDialog("dataRules");
		}else{
			parent.BIONE.closeDialog("dataRules");
		}
	}
	function save_objDef() {
		var value2 = document.getElementById("value2").value;
		var value4 = document.getElementById("value4").value;
		var minValue = $.ligerui.get("minVal").getValue();
		var maxValue = $.ligerui.get("maxVal").getValue();
		// var dataRange = document.getElementById("timeRange").value;
		var min, max;
		var flag = false;//判断是否为日期类型，true为是，false为不是
		if (minValue != '' || maxValue != '') {
			if (minValue != '') {
				min = minValue.charAt(0);
				if (min == '@') {
					flag = true;
				}
			}
			if (maxValue != '') {
				max = maxValue.charAt(0);
				if (max == '@') {
					flag = true;
				}
			}
			if (maxValue != '' && minValue != '' && max != '@' && min != '@') {
				flag = false;
				if ($.isNumeric(maxValue) && $.isNumeric(minValue)) {
					if (parseFloat(maxValue) < parseFloat(minValue)) {
						BIONE.tip('最大值不能小于最小值。');
						return;
					}
				} else {
					if (maxValue < minValue) {
						BIONE.tip('最大值不能小于最小值。');
						return;
					}
				}
				if (value2 != null && value4 != null && (value2 == '{' || value4 == '{') && maxValue == minValue) {
					BIONE.tip('最大值不能等于最小值。');
					return;
				}
			}
		}
		if (minValue != '') {
			if (value2 == '') {
				BIONE.tip('请选择最小值的运算符');
				return;
			}

			if (flag == true) {
				if (min != '@') {
					BIONE.tip('最小值请输入日期类型');
					return;
				}
				if (minValue.length == 1) {
					BIONE.tip('最小值不能只输入@的值');
					return;
				}
				var k = 0;
				for ( var i = 0; i < sysParams.length; i++) {
					if (sysParams[i] == minValue) {
						break;
					}
					k++;
				}
				if (k == sysParams.length) {
					var reg = /^(-|\+)?\d+$/;//判断是否为整数
					minyear = minValue.substr(1, 4);
					var fgf1 = minValue.substr(5, 1);
					minmonth = minValue.substr(6, 2);
					var fgf2 = minValue.substr(8, 1);
					minday = minValue.substr(9);
					if (minValue.length == 11 && reg.test(minyear)
							&& reg.test(minmonth) && (1 <= minmonth * 1)
							&& (minmonth * 1 <= 12) && reg.test(minday)
							&& (1 <= minday * 1) && (minday * 1 <= 31)
							&& fgf1 == '-' && fgf2 == '-') {
						minDate = minValue;
					} else {
						BIONE.tip('最小值请输入正确的日期值');
						return;
					}
				}
			} else {
				flag = false;
				if (minValue != '' && isNaN(minValue)) {
					BIONE.tip('最小值请输入数值类型。');
					return;
				}
			}
		}
		if (maxValue != '') {
			if (value4 == '') {
				BIONE.tip('请选择最大值的运算符');
				return;
			}
			if (flag == true) {
				if (max != '@') {
					BIONE.tip('最大值请输入日期类型');
					return;
				}
				if (maxValue.length == 1) {
					BIONE.tip('最大值不能只输入@的值');
					return;
				}
				var k = 0;
				for ( var i = 0; i < sysParams.length; i++) {
					if (sysParams[i] == maxValue) {
						break;
					}
					k++;
				}
				if (k == sysParams.length) {
					var reg = /^(-|\+)?\d+$/;//判断是否为整数
					maxyear = maxValue.substr(1, 4);
					var fgf1 = maxValue.substr(5, 1);
					maxmonth = maxValue.substr(6, 2);
					var fgf2 = maxValue.substr(8, 1);
					maxday = maxValue.substr(9);
					if (maxValue.length == 11 && reg.test(maxyear)
							&& reg.test(maxmonth) && (1 <= maxmonth * 1)
							&& (maxmonth * 1 <= 12) && reg.test(maxday)
							&& (1 <= maxday * 1) && (maxday * 1 <= 31)
							&& fgf1 == '-' && fgf2 == '-') {
						maxDate = maxValue;
					} else {
						BIONE.tip('最大值请输入正确的日期值');
						return;
					}
				}
			} else {
				flag = false;
				if (maxValue != '' && isNaN(maxValue)) {
					BIONE.tip('最大值请输入数值类型。');
					return;
				}
			}
		}
		if (flag == true) {
			if (minDate != '' && minDate != '') {
				var maxDate = new Date();
				maxDate.setDate(maxday);
				maxDate.setMonth(maxmonth);
				maxDate.setYear(maxyear);
				var minDate = new Date();
				minDate.setDate(minday);
				minDate.setMonth(minmonth);
				minDate.setYear(minyear);
				if (minDate > maxDate) {
					BIONE.tip('最大值的日期不能小于最小值的日期');
					return;
				}
				var reg = /^(-|\+)?\d+$/;//判断是否为整数
				if (value2 != null && value4 != null
						&& (value2 == '{' || value4 == '{')
						&& maxyear == minyear && maxmonth == minmonth
						&& maxday == minday && reg.test(maxyear)
						&& reg.test(minyear)) {
					BIONE.tip('最大值的日期不能等于最小值的日期');
					return;
				}
			}
		}
		var valueSet = document.getElementById("valueSet").value;
		if ((minValue == '' && maxValue == '' && (valueSet == '' || valueSet == '例如：1,2,3'))/*  && dateRange == "" */) {
			BIONE.tip('请选择数值校验方式。');
			return;
		}
		// var dateRange = document.getElementById("timeRange").value;
		/* if (dateRange != "") {
			$.ligerui.get("minVal").setValue("");
			$.ligerui.get("maxVal").setValue("");
			$.ligerui.get("valueSet").setValue("");
		} */
		BIONE.submitForm($("#mainform"), function(text) {
			BIONE.tip("保存成功");
			if("${ruleId}"){
				parent.$.ligerui.get("maingrid").set('url',parent.getTempleUrl() + new Date().getTime());
				parent.$.ligerui.get("maingrid").setOptions({
					parms : []
				});
				parent.$.ligerui.get("maingrid").loadData();
				BIONE.closeDialog("dataRules");
			}else{
				parent.parent.$.ligerui.get("maingrid").set('url',parent.parent.getTempleUrl() + new Date().getTime());
				parent.parent.$.ligerui.get("maingrid").setOptions({
					parms : []
				});
				parent.parent.$.ligerui.get("maingrid").loadData();
				parent.BIONE.closeDialog("dataRules");
			}
		}, function() {
			BIONE.tip("保存失败");
		});
	}

	function setCss(val) {
		if (val == "") {
			$("#mainform input[name=value1]").parent().parent().parent()
					.parent("ul").show().find("input").removeAttr("disabled").css("color", "black");
			$("#mainform input[name=valueSet]").parent().parent().parent("ul").show().find("input").removeAttr("disabled");

		} else {
			$("#mainform input[name=value1]").parent().parent().parent()
					.parent("ul").hide().find("input").attr("disabled", "disabled").css("color", "black");
			$("#mainform input[name=valueSet]").parent().parent().parent("ul")
					.hide().find("input").attr("disabled", "disabled");

		}
	}
	//对输入信息的提示
	function check() {
// 		$("#dateRangeValue").change(
// 				function() {
// 					checkLabelShow(RuleRemark.rule1.dateRangeValue);
// 					$("#checkLabelContainer").html(GlobalRemark.title+ RuleRemark.rule1.dateRangeValue);
// 				});
		$("#ruleNm").focus(
			function() {
				checkLabelShow(RuleRemark.global.ruleName);
				$("#checkLabelContainer").html(GlobalRemark.title+ RuleRemark.global.ruleName);
			});
		$("#columnNameValue").focus(
				function() {
					checkLabelShow(RuleRemark.global.columnNameValue);
					$("#checkLabelContainer").html(GlobalRemark.title+ RuleRemark.global.columnNameValue);
				});
		$("#minVal").focus(
				function() {
					checkLabelShow(RuleRemark.rule1.minValue);
					$("#checkLabelContainer").html(GlobalRemark.title+ RuleRemark.rule1.minValue);
				});
		$("#maxVal").focus(
				function() {
					checkLabelShow(RuleRemark.rule1.maxValue);
					$("#checkLabelContainer").html(GlobalRemark.title+ RuleRemark.rule1.maxValue);
				});
		$("#valueSet").focus(
				function() {
					checkLabelShow(RuleRemark.rule1.valueSet);
					$("#checkLabelContainer").html(GlobalRemark.title+ RuleRemark.rule1.valueSet);
				});
		$("#errorTip").focus(
				function() {
					checkLabelShow(RuleRemark.global.errorTip);
					$("#checkLabelContainer").html(GlobalRemark.title+ RuleRemark.global.errorTip);
				});
		$("#value1").focus(
				function() {
					checkLabelShow(RuleRemark.rule1.value1);
					$("#checkLabelContainer").html(GlobalRemark.title+ RuleRemark.rule1.value1);
				});
		$("#value3").focus(
				function() {
					checkLabelShow(RuleRemark.rule1.value2);
					$("#checkLabelContainer").html(GlobalRemark.title+ RuleRemark.rule1.value2);
				});
	}
	//根据提示信息字符的长度，调整显示提示信息的高度,实现自动换行。
	function checkLabelShow(info) {
		var width = $(document).width();
		var infolength = info.length * 9.63;
		var centerheight = $("#center").height();
		var n = 0;
		if (infolength % width > 0) {
			n = parseInt(infolength / width) + 1;
		} else {
			n = parseInt(infolength / width);
		}
		var checkheight = 16.5 * n;
		$("#checkLabelContainer").show();
		$("#checkLabelContainer").height(checkheight);
	}
</script>
</head>
<body>
	<div id="template.center">
		<form id="mainform" action="${ctx}/rpt/input/rule/rule1-save"
			method="post"></form>
	</div>
</body>
</html>