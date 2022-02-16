<%@ page language="java" pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<html>
<head>
<meta name="decorator" content="/template/template5.jsp">
<script type="text/javascript">
	jQuery.extend(jQuery.validator.messages, {
		greaterThan : "结束时间应当大于开始日期"
	});
	var groupicon = "${ctx}/images/classics/icons/communication.gif";
	var unit = "second";
	$(function() {
		$("#mainform").ligerForm({
			fields : [ {
				display : '触发器名称',
				name : 'triggerName',
				newline : true,
				type : 'text',
				group : "触发器信息",
				groupicon : groupicon,
				validate : {
					required : true,
					maxlength : 100,
				 	remote: {
						url:"${ctx}/bione/schedule/trigger/triggerNameValid"
					},
					messages:{
						remote : "名称已存在"
					}
				}
			}, {
				display : '定义类型',
				name : 'defType',
				newline : false,
				type : 'select',
				comboboxName: 'defTypeBox',
				options : {
					data : [ {
						text : '配置',
						id : "01"
					}, {
						text : '自定义',
						id : '02'
					} ],
					onSelected : function(val) {
						setCss();
					}
				}
			}, {
				display : '执行频率',
				name : 'execFreq',
				newline : true,
				type : 'select',
				comboboxName: 'execFreqBox',
				options : {
					data : [ {
						text : '每天',
						id : '11'
					}, {
						text : '周初',
						id : '21'
					}, {
						text : '旬初',
						id : '31'
					}, {
						text : '月初',
						id : '41'
					}, {
						text : '月末',
						id : '42'
					}, {
						text : '季初',
						id : '51'
					}, {
						text : '季末',
						id : '52'
					}, {
						text : '年初',
						id : '61'
					}, {
						text : '半年末',
						id : '62'
					}, {
						text : '年末',
						id : '63'
					} ],
					onSelected : serFreqValue
				}
			}, {
				display : '执行方式',
				name : "execType",
				newline : false,
				type : "select",
				comboboxName: 'execTypeBox',
				options : {
					data : [ {
						text : '执行时间',
						id : 'execTime'
					}, {
						text : '间隔时间',
						id : 'spaceTime'
					} ],
					onSelected : function(val) {
						setCss();
						setRemarkValue();
					}
				}

			}, {
				display : '执行时间',
				name : 'execTime',
				id : 'execTime',
				newline : true,
				type : "text",
				validate : {
					time : true,
					required : true
				}
			}, {
				display : '间隔时间',
				name : 'spaceTime',
				id : 'spaceTime',
				newline : true,
				type : 'spinner',
				validate : {
					required : true,
					digits : true ,
					maxlength : 3
				},
				options : {
					type : 'int', valueFieldID : 'typelengthId',isNegative:false 
				}
			}, {
				display : '单位',
				name : 'spaceUnit',
				newline : false,
				type : 'select',
				options : {
					initValue : 'second',
					data : [ {
						text : '秒',
						id : 'second'
					}, {
						text : '分',
						id : 'min'
					}, {
						text : '时',
						id : 'hour'
					} ],
					onSelected : function(val) {
						unit = val;
						setSpaceValue();
						setRemarkValue();
					}
				}
			}, {
				display : '开始时间',
				name : 'startTime',
				newline : true,
				id : 'startTime',
				validate : {
					required : true
				},
				type : 'date'
			}, {
				display : '结束时间',
				name : 'endTime',
				id : 'endTime',
				newline : false,
				type : 'date',
				validate : {
					greaterThan : "startTime"
				}
			}, {
				display : '克隆表达式',
				name : 'cron',
				width : '490',
				newline : true,
				type : 'text',
				validate : {
					cron : true,
					required : true
				}
			}, {
				display : '描述',
				name : 'remark',
				width : '493',
				newline : true,
				type : 'textarea',
				validate : {
					maxlength : 500
				},
				attr : {
					style : 'resize: none;'
				}
			}, {
				name : 'freqValue',
				type : 'hidden'
			}, {
				name : 'everydayFreqV',
				type : 'hidden'
			}, {//0809gaofneg修改
				name : 'execFreqValue',
				type : 'hidden'
			}, {//0809gaofneg修改
				name : 'unitValue',
				type : 'hidden'
			} ]
		});

		$("#mainform [name='freqValue']").val("* * ? *");
		
		var date = new Date();
		var yy = date.getFullYear();
		var Mm = ((date.getMonth() + 1) < 10) ? ('0' + (date.getMonth() + 1))
				: (date.getMonth() + 1);
		var dd = (date.getDate() < 10) ? ('0' + date.getDate()) : date
				.getDate();
		$("#mainform input[name=startTime]").val(yy + '-' + Mm + '-' + dd);

		$("#mainform input[name=spaceTime]").parent().parent().parent("ul")
				.hide().find("input").attr("disabled", "disabled");
		$("#mainform input[name=spaceUnit]").parent().parent().parent("ul")
				.hide().find("input").attr("disabled", "disabled");
		$("#mainform input[name='cron']").attr("readonly", "readonly").removeAttr("validate");//0809gaofneg修改

		$("#execTime").blur(setCronValue);

		$("#spaceTime").blur(setSpaceValue);
		jQuery.metadata.setType("attr", "validate");
		BIONE.validate($("#mainform"));
		var buttons = [];
		buttons.push({
			text : '取消',
			onclick : function() {
				BIONE.closeDialog("triggerAddWin");
			}
		});
		buttons.push({
			text : '保存',
			onclick : f_save
		});

		BIONE.addFormButtons(buttons);
		$.ligerui.get("execFreqBox").selectValue("11");
		$.ligerui.get("defTypeBox").selectValue("01");
		$.ligerui.get("execTypeBox").selectValue("execTime");
	});

	function setSpaceValue() {
		var val = $("#mainform input[name=defType]").val();
		if (val == "01") {
			var value = $("#mainform [name='spaceTime']").val();
			var v = "";
			if (unit == "second") {
				v = "0/" + value + " * * ";
				$("#mainform [name='unitValue']").val("秒");
			} else if (unit == "min") {
				v = "0 0/" + value + " * ";
				$("#mainform [name='unitValue']").val("分");
			} else {
				v = "0 0 0/" + value + " ";
				$("#mainform [name='unitValue']").val("小时");
			}
			setRemarkValue();
			$("#mainform [name='everydayFreqV']").val(v);
			$("#mainform [name='cron']").val(
					$("#mainform [name='everydayFreqV']").val()
							+ $("#mainform [name='freqValue']").val());
		}
		
	}

	function f_save() {
		BIONE.submitForm($("#mainform"), function() {
			BIONE.closeDialogAndReloadParent("triggerAddWin", "maingrid",
					"添加成功");
		}, function() {
			BIONE.closeDialog("triggerAddWin", "添加失败");
		});
	}
	function serFreqValue(value) {

		if (value == '11') {
			//每日
			$("#mainform [name='freqValue']").val("* * ? *");
			$("#mainform [name='execFreqValue']").val("每天");
		} else if (value == '21') {
			//周初（也就是周一）
			$("#mainform [name='freqValue']").val("? 1-12 2 *");
			$("#mainform [name='execFreqValue']").val("周初");
		} else if (value == '31') {
			//旬初
			$("#mainform [name='freqValue']").val("1,11,21 * ? *");
			$("#mainform [name='execFreqValue']").val("旬初");
		} else if (value == '41') {
			//月初
			$("#mainform [name='freqValue']").val("1 * ? *");
			$("#mainform [name='execFreqValue']").val("月初");
		} else if (value == '42') {
			//月末
			$("#mainform [name='freqValue']").val("L * ? *");
			$("#mainform [name='execFreqValue']").val("月末");
		} else if (value == '51') {
			//季初
			$("#mainform [name='freqValue']").val("1 1,4,7,10 ? *");
			$("#mainform [name='execFreqValue']").val("季初");
		} else if (value == '52') {
			//季末
			$("#mainform [name='freqValue']").val("L 3,6,9,12 ? *");
			$("#mainform [name='execFreqValue']").val("季末");
		} else if (value == '61') {
			//年初
			$("#mainform [name='freqValue']").val("1 1 ? *");
			$("#mainform [name='execFreqValue']").val("年初");
		} else if (value == '62') {
			//半年末
			$("#mainform [name='freqValue']").val("L 6 ? *");
			$("#mainform [name='execFreqValue']").val("半年末");
		} else if (value == '63') {
			//年末
			$("#mainform [name='freqValue']").val("L 12 ? *");
			$("#mainform [name='execFreqValue']").val("年末");
		}
		setRemarkValue();
		var val = $("#mainform input[name=defType]").val();
		if (val == "01") {
			$("#mainform [name='cron']").val(
					$("#mainform [name='everydayFreqV']").val()
							+ $("#mainform [name='freqValue']").val());
		}
		
	}

	function setCss() {
		var val = $("#mainform input[name=defType]").val();
		if (val == "01") {
			//$.ligerui.get("execFreqValue").setEnabled();
			$("#mainform input[name=execFreq]").parent("div").removeClass(
					"l-text-disabled").parent().parent("ul").show().find(
					"input").removeAttr("disabled").css("color", "black");//显示执行频率 
			$("#mainform input[name=execType]").parent("div").removeClass(
					"l-text-disabled").parent().parent("ul").show().find(
					"input").removeAttr("disabled").css("color", "black");//显示执行方式 

			var val_2 = $("#mainform input[name=execType]").val();
			if (val_2 == "execTime") {
				$("#mainform input[name=spaceUnit]").parent().parent().parent(
						"ul").hide().find("input").attr("disabled", "disabled");
				$("#mainform input[name=spaceTime]").parent().parent().parent(
						"ul").hide().find("input").attr("disabled", "disabled");
				$("#mainform input[name=execTime]").parent("div").removeClass(
						"l-text-disabled").parent().parent("ul").show().find(
						"input").removeAttr("disabled").css("color", "black");

				setCronValue();
			} else {
				$("#mainform input[name=spaceUnit]").parent("div").removeClass(
						"l-text-disabled").parent().parent("ul").show().find(
						"input").removeAttr("disabled").css("color", "black");
				$("#mainform input[name=spaceTime]").parent("div").removeClass(
						"l-text-disabled").parent().parent("ul").show().find(
						"input").removeAttr("disabled").css("color", "black");
				$("#mainform input[name=execTime]").parent().parent().parent(
						"ul").hide().find("input").attr("disabled", "disabled");
				setSpaceValue();
			}

			$("#mainform input[name=cron]").attr("readonly", "readonly")
											.removeAttr("validate")
											.parent("div").addClass("l-text-readonly");//0809gaofneg修改
		} else {
			//$.ligerui.get("execFreqValue").setDisabled();
			$("#mainform input[name=execFreq]").parent().parent().parent("ul")
					.hide().find("input").attr("disabled", "disabled");//隐藏执行频率
			$("#mainform input[name=execType]").parent().parent().parent("ul")
					.hide().find("input").attr("disabled", "disabled");//隐藏执行方式
			$("#mainform input[name=execTime]").parent().parent().parent("ul")
					.hide().find("input").attr("disabled", "disabled");//隐藏执行时间
			$("#mainform input[name=spaceTime]").parent().parent().parent("ul")
					.hide().find("input").attr("disabled", "disabled");//隐藏间隔时间
			$("#mainform input[name=spaceUnit]").parent().parent().parent("ul")
					.hide().find("input").attr("disabled", "disabled");//隐藏间隔单位                	 
			//克隆公式手动添加
			$("#mainform input[name=cron]").attr("validate","{cron : true,required : true}")
											.removeAttr("readonly")
											.parent("div").removeClass("l-text-readonly");
			//清空 克隆表达式 和 描述
			if (!"${id}") {
				$("#mainform input[name=cron]").val("");
				$("#mainform [name='remark']").val("");
			}

		}
		$("#mainform input[name=cron]").css("color", "black");
		//$("#mainform").valid();
	}
	function setCronValue() {
		var val = $("#mainform input[name=defType]").val();
		if (val == "01") {
			var value = $("#mainform [name='execTime']").val();
			var v = "";
			var dt = value.split(":");
			for ( var i = 0; i < dt.length; i++) {
				v += dt[dt.length - i - 1] + " ";
			}
			setRemarkValue();
			$("#mainform [name='everydayFreqV']").val(v);
			$("#mainform [name='cron']").val(
					$("#mainform [name='everydayFreqV']").val()
							+ $("#mainform [name='freqValue']").val());
		}
		
	}
	function setRemarkValue() {
		var val = $("#mainform input[name=defType]").val();
		if (val == "01") {
			var freq = $("#mainform [name='execFreqValue']").val();
			var last = "";
			var ifEveryDay = $("#mainform [name='execType']").val();
			if (ifEveryDay == 'execTime') {
				last = $("#mainform [name='execTime']").val();
			} else if (ifEveryDay == 'spaceTime') {
				last = "0点开始每隔" + $("#mainform [name='spaceTime']").val()
						+ $("#mainform [name='unitValue']").val();
			}
			$("#mainform [name='remark']").val(freq + last + "执行");
		}
		
	}
</script>
</head>
<body>
	<div id="template.center">
		<form name="mainform" method="post" id="mainform"
			action="${ctx}/bione/schedule/trigger"></form>
	</div>
</body>
</html>