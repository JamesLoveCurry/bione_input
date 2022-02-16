<%@ page language="java" pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<html>
<head>
<meta name="decorator" content="/template/template14.jsp">
<script type="text/javascript">
	var mainform;
	var cfgId = '${cfgId}';
	
	$(function() {
		window.parent.dataObj = window;
		initform();
		initData();
	});
	
	function initData(){
		$.ajax({
			url : '${ctx}/rpt/input/table/getSqlInfo?cfgId='+cfgId,
			dataType : 'json',
			success : function(data) {
				if (data )
					$("#mainform textarea[name='sqlText']").val(data.sql);
			},
			error : function(result, b) {
				BIONE.tip('发现系统错误 <BR>错误码：' + result.status);
			}
		});
	}
	
	function initform(){
		mainform = $("#mainform");
		mainform.ligerForm({
			fields : [{
				display : "SQL语句",
				name : 'sqlText',
				newline : true,
				type : 'textarea',
				width : 600,
				validate : {
					maxlength : 500,
					required : true
				}
			}]
		});
		$("#sqlText").css({ height:'100px'});
		jQuery.metadata.setType("attr", "validate");
		BIONE.validate(mainform);
	}
	function getData(){
		var sqlText = $("#sqlText").text();
		/**
		$.ajax({
			cache : false,
			async : true,
			url : "${ctx}/rpt/input/table/validateSql.json?d="+new Date(),
			dataType : 'json',
			type : "post",
			data : {
				tableId : tableId,
				sqlText : sqlText
			},
			success : function(result) {
				if(result.status=="-1")
				{
					BIONE.tip(result.msg);
					return "-1";
				}
				else
					return sqlText;
			}
		});
		*/
		return sqlText;
	}
</script>
</head>
<body>
	<div id='template.center' >
		<div><font color="red">SQL语句必须包含TABLE定义对应的字段英文名,例如表格包含字段 CNAME ,SQL语句为SELECT COL1 AS CNAME FROM TBL<br/>
		手动下发需要过滤SQL语句中务必包含FILTER_NM字段,自动下发以及后置依赖任务SQL中务必包含FILTER_NO字段.<br/>
		例如:SELECT COL2 AS FILTER_NM FROM TBL,SELECT COL3 AS FILTER_NO FROM TBL
		</font></div>
		<form name="mainform" method="post" id="mainform" action=""></form>
	</div>
</body>
</html>