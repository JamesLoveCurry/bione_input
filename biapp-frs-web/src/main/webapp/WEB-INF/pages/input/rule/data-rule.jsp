<%@ page language="java" pageEncoding="UTF-8"%>
<%@ page import="org.apache.shiro.web.filter.authc.FormAuthenticationFilter"%>

<%@ include file="/common/taglibs.jsp"%>
<%@ include file="/common/jquery_load.jsp"%>
<%@ include file="/common/ligerUI_load.jsp"%>
<html>
<head>
<script type="text/javascript">
	
	//初始化
	$(function() {
		ruleForm();
		changeFrame();
		$("#rule").height($(document).height()-$("#search").height());
	});
	function ruleForm() {
		var selection = [ {
			id : "",
			text : "请选择"
		} ];
		//设置规则类型下拉框的值
		$.ajax({
			async : false,
			url : '${ctx}/rpt/input/rule/dataRulesCombox',
			success : function(data) {
				selection = data;
			}
		});
		$("#search").ligerForm({
			fields : [ {
				display : "模板规则",
				name : "ruleType",
				comboboxName: "ruleNm",
				newline : true,
				type : 'select',
				options : {
					data : selection
				},
				attr : {
					field : 'temple.ruleType',
					op : "="
				}
			} ]
		});
	}
	function changeFrame(){
		var rule1 = "${ctx}/rpt/input/rule/rule1?id=${id}&d="+new Date().getTime();
		var rule2 = "${ctx}/rpt/input/rule/rule2?id=${id}&d="+new Date().getTime();
		//默认显示"数据值范围"
		$("#ruleNm").val('数据值范围');
		$("#rule").attr("src",rule1);
		
		var src;
		$("#ruleNm").change(function(){
			var rule = $("#ruleNm").val();
			if(!rule){
				src = "";
			}else if(rule=='数据值范围'){
				src = rule1;
			}else if(rule=='正则表达式'){
				src = rule2;
			}
			$("#rule").attr("src",src);
		});
		
	}
</script>
</head>
<body>
<div id="search"></div>
<div id="ruleDiv" style="width:100%">
	<iframe id="rule" frameborder="0px" width="100%"></iframe>
</div>
</body>
</html>