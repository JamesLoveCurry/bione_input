<%@ page language="java" pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<html>
<head>
<meta name="decorator" content="/template/template5.jsp">
<script type="text/javascript" src="${ctx}/js/color/jscolor.js"></script>
<script type="text/javascript">
	jQuery.validator.addMethod("greaterThan", function(value, element, params) {
		if (value == '' || value == null) {
			return true;
		}
		var ele = $("[name=" + params + "]");
		return value > ele.val() ? true : false;
	}, "结束日期应当大于开始日期");
	jQuery.validator.addMethod("validStartDate", function(value, element,
			params) {
		if (verStartDate != null) {
			return value >= verStartDate;
		}
		return false;
	}, "开始日期应该大于等于模板的开始时间");

	var groupicon = "${ctx}/images/classics/icons/communication.gif";
	var mainform;
	var verStartDate;//开始日期
	var isQuery = "${isQuery}";
	var rptCycle = '${rptCycle}';//报表生成周期
	
	$(function() {
		$.ajax({
			cache : false,
			async : false,
			url : "${ctx}/report/frame/valid/warn/validStartDate?cfgId=${cfgId}&d="
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
		//监管要求默认值否
		liger.get("isFrs").setValue("02");
		

		jQuery.metadata.setType("attr", "validate");
		BIONE.validate($("#mainform"));

		var buttons = [];
		buttons.push({
			text : '取消',
			onclick : function() {
				BIONE.closeDialog("warnAdd");
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
			inputWidth : 150,
			labelWidth : 120,
			fields : [{
				group : "预警校验属性",
				groupicon : groupicon,
				display : '指标单元格',
				name : 'indexNo',
				comboboxName : "cell",
				newline : true,
				type : 'select',
				options : {
					onBeforeOpen : function() {
						window.parent.BIONE
								.commonOpenDialog(
										"双击选择单元格",
										"cellChoose",
										950,
										500,
										'${ctx}/report/frame/valid/warn/showCellChoose?templateId=${templateId}&cfgId=${cfgId}');
						return false;
					}
				},
				validate : {
					required : true
				}
			}, {
				display : "预警类型",
				name : 'compareType',
				newline : false,
				type : 'select',
				options : {
					data : [ {
						"id" : "01",
						"text" : "环比"
					}, {
						"id" : "02",
						"text" : "同比"
					}, {
						"id" : "03",
						"text" : "较上日"
					}, {
						"id" : "04",
						"text" : "较月初"
					}, {
						"id" : "05",
						"text" : "较上月末"
					}, {
						"id" : "06",
						"text" : "较季初"
					}, {
						"id" : "07",
						"text" : "较上季末"
					}, {
						"id" : "08",
						"text" : "较年初"
					}, {
						"id" : "09",
						"text" : "较上年末"
					}],
					onBeforeSelect : function (value){
						if(value == "01"){
							//根据报表生成周期给比较值类型赋值
							switch (rptCycle) {
								case "01"://日
									$("#mainform input[name='compareValType']").val("01");//环比（上日）
									break;
								case "02"://月
									$("#mainform input[name='compareValType']").val("03");//环比（上月月末）
									break;
								case "03"://季
									$("#mainform input[name='compareValType']").val("06");//环比（上季季末）
									break;
								case "04"://年
									$("#mainform input[name='compareValType']").val("08");//环比（上年年末）
									break;
								case "12"://半年
									$("#mainform input[name='compareValType']").val("10");//环比（上半年末）
									break;
								default:
									$("#mainform input[name='compareValType']").val("");
									break;
							}
						}else if(value == "02"){
							//同比（日、月、季、半年、年）
							$("#mainform input[name='compareValType']").val("09");//同比（去年同期）
						}else if(value == "03"){
							$("#mainform input[name='compareValType']").val("01");//较上日
						}else if(value == "04"){
							$("#mainform input[name='compareValType']").val("02");//较月初
						}else if(value == "05"){
							$("#mainform input[name='compareValType']").val("03");//较上月末
						}else if(value == "06"){
							$("#mainform input[name='compareValType']").val("05");//较季初
						}else if(value == "07"){
							$("#mainform input[name='compareValType']").val("06");//较上季末
						}else if(value == "08"){
							$("#mainform input[name='compareValType']").val("07");//较年初
						}else if(value == "09"){
							$("#mainform input[name='compareValType']").val("08");//较上年末
						}
					}
				},
				validate : {
					required : true
				}
			}, {
				display : "预警波动下限",
				name : 'minusRangeVal',
				newline : true,
				type : 'float',
				validate : {
					required : true
				}
			}, {
				display : "预警波动上限",
				name : 'postiveRangeVal',
				newline : false,
				type : 'float',
				validate : {
					required : true
				}
			}, {
				display : "监管要求",
				name : 'isFrs',
				newline : true,
				type : 'select',
				options : {
					data : [ {
						"id" : "01",
						"text" : "是"
					}, {
						"id" : "02",
						"text" : "否"
					} ]
				}
			}, {
				display : '幅度类型',
				name : 'rangeType',
				newline : false,
				type : 'select',
				options : {
					value : '01',
					data : [{"id":"01","text":"数字"},{"id":"02","text":"百分比"}],
					onSelected : function(value) {
						if (value == "01") {
							$("#unit").parent().parent().parent().parent().show();
						} else {
							$("#unit").parent().parent().parent().parent().hide();
						}
					}
				}
			}, {
				display : '单位',
				name : 'unit',
				newline : true,
				type : 'select',
				options : {
					value : '04',
					data : [
						{"id":"01","text":"元"},
						{"id":"02","text":"百"},
						{"id":"03","text":"千"},
						{"id":"04","text":"万"},
						{"id":"05","text":"亿"}
					]
				}
			}, {
				display : "比较值类型",
				name : 'compareValType',
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
				display : "级别编号",
				name : "levelNum",
				type : "hidden"
			}, {
				display : "级别名称",
				name : "levelNm",
				type : "hidden"
			}, {
				display : "预警类型",
				name : "levelType",
				type : "hidden"
			}, {
				display : "提醒颜色",
				name : "remindColor",
				type : "hidden"
			}, {
				display : "通过条件",
				name : "isPassCond",
				type : "hidden"
			}, {
				display : '备注',
				name : 'remark',
				newline : true,
				type : 'textarea',
				validate : {
					maxlength : 250
				},
				width : 650,
				attr : {
					style : "resize:none; height:50px"
				}
			} ]
		});
		jQuery.metadata.setType("attr", "validate");

		if ("${checkId}") {//修改，反显数据
			$.ajax({
				cache : false,
				async : true,
				url : "${ctx}//report/frame/valid/warn/${checkId}?cfgId=${cfgId}&d="
						+ new Date().getTime(),
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
					if (!result)
						return;
					liger.get("cell").setValue(result.indexNo);
					liger.get("cell").setText(result.indexNm);
					liger.get("compareType").selectValue(result.compareType);
					$("#mainform input[name='minusRangeVal']").val(result.minusRangeVal);
					$("#mainform input[name='postiveRangeVal']").val(result.postiveRangeVal);
					liger.get("isFrs").setValue(result.isFrs);
					liger.get("rangeType").setValue(result.rangeType);
					liger.get("unit").setValue(result.unit);
					$("#mainform input[name='compareValType']").val(result.compareValType);
					$("#mainform input[name='levelNum']").val(result.levelNum);
					$("#mainform input[name='levelNm']").val(result.levelNm);
					$("#mainform input[name='levelType']").val(result.levelType);
					$("#mainform input[name='remindColor']").val(result.remindColor);
					$("#mainform input[name='isPassCond']").val(result.isPassCond);
					$("#mainform input[name=startDate]").val(result.startDate);
					$("#mainform input[name=endDate]").val(result.endDate);
					$("#mainform textarea[name=remark]").val(result.remark);
				},
				error : function(result, b) {
					BIONE.tip('发现系统错误 <BR>错误码：' + result.status);
				}
			});
		}else{//新增，给隐藏框赋默认值
			//幅度类型（默认为百分比02，数字01）
			// $("#mainform input[name='rangeType']").val("02");
			//提醒名称（区分预警、报警、自定义）
			$("#mainform input[name='levelNm']").val("预警");
			//警戒类型（默认为内置分级01，自定义分级02）
			$("#mainform input[name='levelType']").val("01");
			//提醒颜色（使用6位16进制的颜色代码声明颜色，默认红色）
			$("#mainform input[name='remindColor']").val("FF0000");
			//通过条件（默认通过1，否0）
			$("#mainform input[name='isPassCond']").val("1");
			//结束日期
			$("#mainform input[name='endDate']").val("29991231");
			//开始日期
			$("#mainform input[name='startDate']").val(verStartDate);
		}
	}
	function f_save() {
		jQuery.metadata.setType("attr", "validate");
		if ($("#mainform").valid()) {
			var data = {
				checkId : "${checkId}",
				rptTemplateId : "${templateId}",
				indexNo : $.ligerui.get("cell").getValue(),
				compareType : $.ligerui.get("compareType").getValue(),
				minusRangeVal : $.ligerui.get("minusRangeVal").getValue(),
				postiveRangeVal : $.ligerui.get("postiveRangeVal").getValue(),
				isFrs : $.ligerui.get("isFrs").getValue(),
				compareValType : $("#mainform input[name='compareValType']").val(),
				rangeType : $("#mainform input[name='rangeType']").val(),
				unit : $("#mainform input[name='unit']").val(),
				levelNum : $("#mainform input[name='levelNum']").val(),
				levelNm : $("#mainform input[name='levelNm']").val(),
				levelType : $("#mainform input[name='levelType']").val(),
				remindColor : $("#mainform input[name='remindColor']").val(),
				isPassCond : $("#mainform input[name='isPassCond']").val(),
				remark : $("#mainform textarea[name='remark']").val(),
				startDate : $("#mainform input[name='startDate']").val(),
			    endDate : $("#mainform input[name='endDate']").val()
			};
			if('01' == data.rangeType){
				if(data.unit == null || data.unit == ''){
					BIONE.tip("单位不能为空！");
					return;
				}
			}
			if(Number(data.minusRangeVal) > Number(data.postiveRangeVal)){
				BIONE.tip("最小比率应小于最大比率");
				return;
			}
			$.ajax({
				type : "POST",
				dataType : 'json',
				url : "${ctx}/report/frame/valid/warn/saveData",
				data : data,
				success : function() {
					window.parent.frames["warn"].reloadGrid()
					BIONE.closeDialog("warnAdd", "保存成功");
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
		<div id="tipAreaDiv" style="width: 99.8%;padding-top: 20px;padding-bottom: 20px;background-color: #FFFEE6;border: solid 1px #D0D0D0;">
			<div style="padding-left: 50px;">
				<div style="width: 24px; height: 16px; float: left; background-image: url('${ctx}/images/classics/icons/lightbulb.png'); background-attachment: scroll; background-repeat: no-repeat; background-position-x: 0%; background-position-y: 0%; background-size: auto; background-origin: padding-box; background-clip: border-box; background-color: transparent;"></div>
				<p>tips : 页面功能:&nbsp;&nbsp;预警波动在上下限之内属于正常，波动超出上下限进行预警提醒</p>
			</div>
		</div>
		<form name="mainform" method="post" id="mainform"></form>
	</div>
</body>
</html>