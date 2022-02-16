<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<html>
<meta name="decorator" content="/template/template14.jsp">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<script>
	var dataDate = "${dataDate}";
	var orgNo = "${orgNo}";
	var indexNo = "${indexNo}";
	var orgType = "${orgType}";
	var indexNm = "${indexNm}";
	var rptTemplateId = "${rptTemplateId}";
	var cellNo = "${cellNo}";
	var checkType = '${checkType}';

	var tmpStr = "";//校验状态

	$(function() {
		initForm();
		initGrid('N');

		BIONE.createButton({
			appendTo : "#mainform",
			text : '全部',
			icon : 'home_line',
			width : '55px',
			align : 'right',
			click : function() {
				$(".l-btn").css("background-color", "#3c8dbc");
				$(".icon-home_line").parent().css("background-color", "#44bc3c");
				tmpStr = "";
				initGrid(tmpStr);
			}
		});
		BIONE.createButton({
			appendTo : "#mainform",
			text : '通过',
			icon : 'succed',
			width : '55px',
			align : 'right',
			click : function() {
				$(".l-btn").css("background-color", "#3c8dbc");
				$(".icon-succed").parent().css("background-color", "#44bc3c");
				tmpStr = 'Y';
				initGrid(tmpStr);

			}
		});

		BIONE.createButton({
			appendTo : "#mainform",
			text : '未通过',
			icon : 'close',
			width : '55px',
			align : 'right',
			click : function() {
				$(".l-btn").css("background-color", "#3c8dbc");
				$(".icon-close").parent().css("background-color", "#44bc3c");
				tmpStr = 'N';
				initGrid(tmpStr);
			}
		});
		$("#mainform").width("800");
		$("<div style='width:300px; color: red; line-height: 29px; background-color: white; text-align:left;' class='l-btn l-btn-hasicon'>逻辑校验默认显示未通过的记录</div>").appendTo($("#mainform"));
		
		$(".icon-close").parent().css("background-color", "#44bc3c");
	});
	function initForm() {
		var mainform = $("#mainform").ligerForm({
			fields : [ {
				display : '校验状态',
				name : 'validType',
				newline : true,
				type : "hidden",
				attr : {
					readOnly : true
				}
			} ]
		});
		if (window.parent.tmp && window.parent.tmp.logicRs != null) {
			$("#validType").val(window.parent.tmp.logicRs);
		} else {
			$("#validType").val("未校验");
			//$("#griddiv").hide();
		}
	}

	function initGrid() {
		var grid = $("#griddiv").ligerGrid({
			height: '100%',
			width: '100%',
			columns: [
				{
					display: '校验公式',
					name: 'expressionShortDesc',
					width: "25%",
					align: 'left',
					render: function (a, b, c) {
						if (c != null) {
							c.replace(new RegExp("<", "gm"), "&lt;");
							var str = "";
							if(c.length > 40){
								for(var i=0; i<c.length;i++){
									str+=c[i];
									if(i != 0 && i%40 == 0){
										str+="<br/>";
									}
								}
							}else{
								str = c;
							}
							return str;
						} else {
							return c;
						}
					},
					isSort: true
				},
				{
					display: '校验数值',
					name: 'replaceExpression',
					width: "25%",
					align: 'left',
					render: function (a, b, c) {
				 		if (c != null) {
				 			c.replace(new RegExp("<", "gm"), "&lt;");
							var str = "";
							if(c.length > 40){
								for(var i=0; i<c.length;i++){
									str+=c[i];
									if(i != 0 && i%40 == 0){
										str+="<br/>";
									}
								}
							}else{
								str = c;
							}
							return str;
						} else {
							return c;
						}
					},
					isSort: true
				}, {
					display: '校验状态',
					name: 'verifytSts',
					width: "8%",
					align: 'center',
					render: VerifyStsRender,
					isSort: true
				}, {
					display: '容差值',
					name: 'floatVal',
					width: "7%",
					isSort: true,
					align: 'right',
					render: function (a, b, c) {
						if (a.dataUnit != null) {
							if (a.dataUnit == "01") {
								return c + "";
							} else if (a.dataUnit == "02") {
								return c / 100 + "百";
							} else if (a.dataUnit == "03") {
								return c / 1000 + "千";
							} else if (a.dataUnit == "04") {
								return c / 10000 + "万";
							} else if (a.dataUnit == "05") {
								return c / 100000000 + "亿";
							} else if (a.dataUnit == "06") {
								return c * 100 + "%";
							} else {
								return c + "";
							}
						} else {
							return c + "";
						}
					}
				}, {
					display: '差值',
					name: 'differVal',
					width: "8%",
					align: 'right',
					isSort: true,
					render: function (a, b, c) {
						return c + "";
					}
				},{
					display: '公式来源',
					name: 'checkSrc',
					width: "10%",
					align: 'center',
					render: function (a, b, c) {
						if (c) {
							if (c == '01') {
								return '监管制度';
							} else if (c == '02') {
								return '自定义';
							}
						} else {
							return '其他';
						}
					},
					isSort: true
				},{
					display: '公式说明',
					name: 'busiExplain',
					width: "19%",
					align: 'left',
					isSort: true
				},{
					display: '校验未通过说明',
					name: 'validDesc',
					width: "19%",
					align: 'left',
					isSort: true
				}],
			dataAction: 'server',
			url: "${ctx}/frs/verificationLogic/logicResult.json?dataDate="
					+ dataDate
					+ "&orgNo="
					+ orgNo
					+ "&indexNo="
					+ indexNo
					+ "&orgType="
					+ orgType
					+ "&rptTemplateId="
					+ rptTemplateId + "&cellNo=" + cellNo
					+ "&checkType="
					+ checkType,
			parms: {
				verifytSts: tmpStr
			},
			type: "post",
			checkbox: false,
			rownumbers: true,
			usePager: false,
			isScroll: false,
			alternatingRow: true
		});
		grid.setHeight($("#center").height() - $("#mainform").height() - 30);
	}

	//渲染校验状态
	VerifyStsRender = function(rowdata) {
		var verifytStsNm = "无数据";
		if (rowdata.verifytSts == 'Y'){
			verifytStsNm = "通过";
		}else if (rowdata.verifytSts == 'N'){
			verifytStsNm = "未通过";
			return "<div style='color: red;'>"+ verifytStsNm + "</div>";
		}
		return verifytStsNm;
	}
</script>
<title>总分校验</title>
</head>
<body>
	<div id="template.center">
		<form id="mainform"></form>
		<div id="griddiv"></div>
	</div>
</body>
</html>