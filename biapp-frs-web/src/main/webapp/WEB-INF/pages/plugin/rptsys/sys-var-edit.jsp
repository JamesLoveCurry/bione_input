<%@ page language="java" pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<html>
<meta name="decorator" content="/template/template5.jsp">
<head>
<script type="text/javascript">
	$(function() {
		 var groupicon = "${ctx}/images/classics/icons/communication.gif";
		 $("#mainform").ligerForm({
			align : 'center',
			fields : [{
				name:"varId",
				type:"hidden"
			},{
				name:"buttonType",
				type:"hidden"
			},{
				display : "变量标识",
				name : "varNo",
				newline : true,
				type : "text",
				group : "系统变量",
				groupicon : groupicon,
				validate : {
					required : true,
					specificSymbeol : true,//特殊符号验证
					maxlength : 32,
					remote: {
						url:"${ctx}/rpt/frame/rptsys/var/sysVarNoValid?d="+new Date()//变量标识唯一验证
					},
					messages:{
						remote : "变量标识重复"
					}
				}
			}, {
				display : "变量名称",
				name : "varNm",
				newline : false,
				type : "text",
				validate : {
					required : true,
					specificSymbeol : true,//特殊符号验证
					maxlength : 100
				}
			},{
				display : "变量类型",
				name : "varType",
				newline : true,
				type : "select",
				comboboxName:"varType_sel",
				options : {
					data : [ {
						text : '常量',
						id : "01"
					}, {
						text : 'SQL',
						id : '02'
					} ],
					onSelected : function(val){
						if(val=="01"){
							$(".l-dialog-btn").remove();
				    		if(buttons && buttons.length >= 3){
				    			buttons.pop();
				    		}
				    		BIONE.addFormButtons(buttons);
				    		//对数据源下拉框的隐藏
							if($("#mainform [name='sourceNm']").val()==""||$("#mainform [name='sourceNm']").val()==null){
								$("#mainform [name='sourceId']").val("------请选择------");
								$("#mainform [name='sourceNm']").val("------请选择------");
							}
							$("#mainform [name='sourceNm']").focusout();//隐藏前触发一次验证
							$("#dsName").parents("li").hide();
							$("li:contains('数据源名称')").hide();
							//对sql语句文本域的隐藏
							if($("#mainform [name='varValueSql']").val()==""||$("#mainform [name='varValueSql']").val()==null){
								$("#mainform [name='varValueSql']").val("------请添加------");
							}
							$("#mainform [name='varValueSql']").focusout();//隐藏前触发一次验证
							$("#varValueSql").parents("ul").hide();
							//$("li:contains('SQL语句')").hide();
							//对变量值的显示
							if($("#mainform [name='varVal']").val()=="------请添加------"){
								$("#mainform [name='varVal']").val("");
							}
							$("li:contains('数据源名称')").prev().show();//显示前一个空占地方的li
							$("#varVal").parents("li").show();
							$("li:contains('变量值')").show();
						}else{
							if($("#mainform [name='sourceNm']").val()=="------请选择------"){
								$("#mainform [name='sourceId']").val("");
								$("#mainform [name='sourceNm']").val("");
							}
							if($("#mainform [name='varValueSql']").val()=="------请添加------"){
								$("#mainform [name='varValueSql']").val("");
							}
							if(buttons && buttons.length < 3){
								$(".l-dialog-btn").remove();
					    		buttons.push({
					    			text : '试运行',
					    			onclick : testSave
					    		});
					    		BIONE.addFormButtons(buttons);
							}
				    		//对数据源下拉框的显示
							$("#sourceNm").parents("li").show();
							$("li:contains('数据源名称')").show();
							$("li:contains('数据源名称')").prev().hide();//隐藏前一个空占地方的li
							
							//对sql语句文本域的显示
							$("#varValueSql").parents("ul").show();
							//$("li:contains('SQL语句')").show();
							//对变量值的隐藏
							if($("#mainform [name='varVal']").val()==""||$("#mainform [name='varVal']").val()==null){
								$("#mainform [name='varVal']").val("------请添加------");
							}
							$("#mainform [name='varVal']").focusout();//隐藏前触发一次验证
							$("#varVal").parents("li").hide();
							$("li:contains('变量值')").hide();
						}
					}
				}
			},{
				display : "变量值",
				name : "varVal",
				newline : false,
				type : "text",
				validate : {
					required : true,
					maxlength : 500
				}
			},{
				display : "数据源名称",
				name : "sourceId",
				newline : false,
				type : "select",
				comboboxName : 'sourceNm',
				options : {
					url : "${ctx}/rpt/frame/dataset/dsList.json"
				},
				validate : {
					required : true
				}
			},{
				display : "SQL语句",
				name : "varValueSql",
				newline : true,
				type : "textarea",
				width : 493,
				attr : {
					style : "resize: none;"
				},
				validate : {
					required : true,
					maxlength : 500
				}
			}, {
				display : "备注",
				name : "remark",
				newline : true,
				type : "textarea",
				width : 493,
				attr : {
					style : "resize: none;"
				},
				validate : {
					maxlength : 500
				}
			} ]
		});

		jQuery.metadata.setType("attr", "validate");
		BIONE.validate("#mainform");
		$.ligerui.get('varType_sel').selectValue("01");
 		if ("${id}"){
			$("#mainform input[name='varNo']").css("color","#999").attr("readOnly","readOnly").removeAttr("validate");//修改时将变量标识设为只读	
			
			$
			.ajax({
				url : "${ctx}/rpt/frame/rptsys/var/getSysVarInfoById.json?d=" + new Date(),
				data : {
					varId : "${id}"
				},
				success : function(data) {
					$("#mainform input[name='varId']").val(data.varId);
					$("#mainform input[name='varNo']").val(data.varNo);
					$("#mainform input[name='varNm']").val(data.varNm);
					$.ligerui.get('varType_sel').selectValue(data.varType);
					if(data.varType=='02'){
						$("#mainform [name='sourceNm']").val(data.sourceNm);
						$("#mainform [name='sourceId']").val(data.sourceId);
						$("#mainform [name='varValueSql']").val(data.varVal);
					}else{
						$("#mainform input[name='varVal']").val(data.varVal);
					}
					$("#mainform [name='remark']").val(data.remark);
				}
			});		 
		} 

		var buttons = [];
		
		buttons.push({
			text:'取消',
			onclick:function(){
				BIONE.closeDialog("VarAdd");
			}
		});
		buttons.push({
			text : '保存',
			onclick : varSave
		});
		BIONE.addFormButtons(buttons);
		
		
 		
	});
	
	
	
	function varSave(){
		$("#mainform input[name='buttonType']").val("save");
		save();
	}
	
	function testSave(){
		var sql = $("#mainform [name='varValueSql']").val();
		if(sql.length >= 6){
			var sqlCheck = $.trim(sql);
			sqlCheck = sqlCheck.slice(0,6);
			if(sqlCheck.toLowerCase() == "select") {
				//测试返回值类型
				var SQL_TEST_SYS_SQLNULL = "01";//SQL语句为空
				var SQL_TEST_SYS_CLASSNOTFOUNDEXCEPTION = "02";//数据源/驱动配置异常
				var SQL_TEST_SYS_SQLEXCEPTION = "03";//SQL语法错误
				var SQL_TEST_SYS_SUCCESS = "04";//测试成功
				var SQL_TEST_SYS_CAHCE_ERROR="05";//缓存获取失败
				var SQL_TEST_SYS_COLUMN_ERROR = "06";//未明确列
				var SQL_TEST_SYS_ROW_ERROR = "07";//结果值不唯一

				$.ajax({
					cache : false,
					async : true,
					url : "${ctx}/bione/variable/sysvar/test",
					type : "post",
					data : {
						buttonType : "test",
						dsId : $("#mainform [name='sourceId']").val(),
						testSql : sql
					},
					beforeSend : function(a, b, c) {
						BIONE.showLoading('正在测试中...');
					},
					complete : function() {
						BIONE.hideLoading();
					},
					success : function(data) {
						if(data == SQL_TEST_SYS_SUCCESS)
							BIONE.tip("测试成功");
						if(data == SQL_TEST_SYS_SQLNULL)
							BIONE.tip("SQL语句为空");
						if(data == SQL_TEST_SYS_CLASSNOTFOUNDEXCEPTION)
							BIONE.tip("数据源/驱动配置异常");
						if(data == SQL_TEST_SYS_SQLEXCEPTION)
							BIONE.tip("SQL语法错误");
						if(data == SQL_TEST_SYS_CAHCE_ERROR)
							BIONE.tip("缓存获取失败");
						if(data == SQL_TEST_SYS_COLUMN_ERROR)
							BIONE.tip("未明确列");
						if(data == SQL_TEST_SYS_ROW_ERROR)
							BIONE.tip("结果值不唯一");
					}
				});
			}else{
				BIONE.tip("本处为查询语句，请以‘select’开头");
			}
		}else{
			BIONE.tip("本处为查询语句，请以‘select’开头");
		}
	}
	
	function save(){
		if($("#mainform [name='sourceNm']").val()==""){
			$("#mainform [name='sourceNm']").val($("#mainform [name='sourceId']").val());
		}
		var sql = $("#mainform [name='varValueSql']").val();
		if(sql!=null&&sql!=""&&sql !="------请添加------"){
			if(sql.length >= 6){
				var sqlCheck = $.trim(sql);
				sqlCheck = sqlCheck.slice(0,6);
				if(sqlCheck.toLowerCase() != "select"){
					BIONE.tip("本处为查询语句，请以‘select’开头");
					return;
				}
			}else{
				BIONE.tip("本处为查询语句，请以‘select’开头");
				return;
			}
		}
		//测试返回值类型
		var SQL_TEST_SYS_SQLNULL = "01";//SQL语句为空
		var SQL_TEST_SYS_CLASSNOTFOUNDEXCEPTION = "02";//数据源/驱动配置异常
		var SQL_TEST_SYS_SQLEXCEPTION = "03";//SQL语法错误
		var SQL_TEST_SYS_SUCCESS = "04";//测试成功
		var SQL_TEST_SYS_CAHCE_ERROR="05";//缓存获取失败
		var SQL_TEST_SYS_COLUMN_ERROR = "06";//未明确列
		var SQL_TEST_SYS_ROW_ERROR = "07";//结果值不唯一
		BIONE.submitForm($("#mainform"), function(data) {
			if(data.testFlag == null||typeof data.testFlag == "undefined"
					|| data.testFlag == SQL_TEST_SYS_SUCCESS) {
				window.parent.grid.loadData();
				BIONE.closeDialog("VarAdd","保存成功");
				return ;
			}
			if(data.testFlag == SQL_TEST_SYS_SQLNULL)
				BIONE.tip("SQL语句为空");
			if(data.testFlag == SQL_TEST_SYS_CLASSNOTFOUNDEXCEPTION)
				BIONE.tip("数据源/驱动配置异常");
			if(data.testFlag == SQL_TEST_SYS_SQLEXCEPTION)
				BIONE.tip("SQL语法错误");
			if(data.testFlag == SQL_TEST_SYS_CAHCE_ERROR)
				BIONE.tip("缓存获取失败");
			if(data.testFlag == SQL_TEST_SYS_COLUMN_ERROR)
				BIONE.tip("未明确列");
			if(data.testFlag == SQL_TEST_SYS_ROW_ERROR)
				BIONE.tip("结果值不唯一");
		}, function() {
			BIONE.closeDialog("VarAdd","保存失败");
		});
	}
</script>
</head>
<body>
<div id="template.center">
	<form name="mainform" id="mainform" action="${ctx}/rpt/frame/rptsys/var" method="post">
	</form>
</div>
</body>
</html>