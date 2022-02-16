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
	//创建表单结构 
	var mainform;
	$(function() {
		var selection = [{id:"",text:"请选择"}];
		$.ajax({
			async : false,
			url : '${ctx}/rpt/input/data/getColumnList/'+"${id}",
			success : function(data1) {
				selection = selection.concat(data1);
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
						url : "${ctx}/rpt/input/rule/checkRuleName",
						type : 'get',
						async : true,
						data: {
							ruleId: "${ruleId}",
							templeId : "${id}"
						}
					} ,
					messages : {
						remote:"规则名已存在，请检查！"
					},
					required : true,
					maxlength : 100
				}
			},{
				display:"约束字段<font color='red'>*</font>",
	        	name:'columnNameValue',
	        	newline:true,
	        	type:'select',
	        	
	        	options:{
	        		valueFieldID:'fieldNm',
	        		data:selection
	        	},
				validate:{
					required:true
				}
			},{
				display : "表达式<font color='red'>*</font>",
				name : "regex",
				newline : true,
				width : 350,
				type : "textarea",
				attr : {
					style : "resize: none;"
				},
				validate : {
					required:true,
					maxlength : 500
				}
			},{
				display : "提示信息<font color='red'>*</font>",
				name : "errorTip",
				newline : true,
				width : 350,
				type : "textarea",
				attr : {
					style : "resize: none;"
				},
				validate : {
					required : true,
					maxlength : 500
				}
			}]
		});
		if("${ruleId}") {
			$.ajax({
				async : true,
				url : "${ctx}/rpt/input/rule/findRuleInfo?ruleId=" + "${ruleId}&d="+new Date().getTime(),
				success : function(data) {
					$("#mainform [name='ruleNm']").val(data.ruleNm);
					$("#mainform [name='ruleId']").val(data.ruleId);
					liger.get('columnNameValue')._changeValue(data.fieldNm, data.fieldNm);
					$("#mainform [name='regex']").val(data.regex);
					$("#mainform [name='errorTip']").val(data.errorTip);
				}
			});	
		}
		jQuery.metadata.setType("attr", "validate");
		BIONE.validate(mainform);
		var buttons = [];
		buttons.push({
			text : '关闭',
			onclick : cancleCallBack
		});
		if(!"${lookType}"){
			buttons.push({
				text : '确定',
				onclick : save_objDef
			});
		}
		
		BIONE.addFormButtons(buttons);
		$("#mainform [name='templeId']").val("${id}");
		liger.get('columnNameValue')._changeValue('', '');
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
		BIONE.submitForm($("#mainform"), function(text) {
			BIONE.tip("保存成功");
			if("${ruleId}"){
				parent.$.ligerui.get("maingrid").set('url', parent.getTempleUrl()+new Date().getTime());
				parent.$.ligerui.get("maingrid").setOptions({ parms: []  }); 
				parent.$.ligerui.get("maingrid").loadData();
				BIONE.closeDialog("dataRules");
			}else{
				parent.parent.$.ligerui.get("maingrid").set('url', parent.parent.getTempleUrl()+new Date().getTime());
				parent.parent.$.ligerui.get("maingrid").setOptions({ parms: []  }); 
				parent.parent.$.ligerui.get("maingrid").loadData();
				parent.BIONE.closeDialog("dataRules");
			}
			
		}, function() {
			BIONE.tip("保存失败");
		});
	}
	//对输入信息的提示
	function check() {
		$("#ruleName").focus(
				function() {
					//checkLabelShow(RuleRemark.global.ruleName);
					$("#checkLabelContainer").html(
							GlobalRemark.title+ RuleRemark.global.ruleName 
									);
				});
		$("#errorTip").focus(
				function() {
					//checkLabelShow(RuleRemark.global.errorTip);
					$("#checkLabelContainer").html(
							GlobalRemark.title+ RuleRemark.global.errorTip 
									);
				});
		$("#regex").focus(
				function() {
					//checkLabelShow(RuleRemark.rule2.regex);
					$("#checkLabelContainer").html(
							GlobalRemark.title+ RuleRemark.rule2.regex 
									);
				});
	}
</script>
</head>
<body>
<div id="template.center">
		<form id="mainform" action="${ctx}/rpt/input/rule/rule2-save" method="post"></form>
	</div>
</body>
</html>