<%@ page language="java" contentType="text/html; charset=UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<%@ include file="/common/meta.jsp"%>
<%@ include file="/common/taglibs.jsp"%>
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<%@ include file="/common/jquery_load.jsp"%>
<%@ include file="/common/ligerUI_load.jsp"%>
<link rel="stylesheet" type="text/css"
	href="${ctx}/css/classics/template/template1.css" />
<script type="text/javascript">
	$(function() {
		templateshow();

	});
	function templateshow() {
		var windowHeight = $(window).height();
		var windowWidth = $(window).width();
		
		$("#center").height(windowHeight-50 );
		//$("#center").width(windowWidth -120 );
		//$("#center").width($(window).height()-310);
		//$("#center").width($(window).width() );
		var formheight=110;
		var centerHeight = $("#center").height();
// 		$("#mainform").height(formheight);
		//$("#nodeContent").width($("#center").width()-45);
		$("#nodeContent").height(windowHeight * 0.55);
		var bottomheight = 40;
		$("#bottom").height(bottomheight);
	}
</script>
<sitemesh:write property='head' />
<script type="text/javascript">
	$(function() {
		templateshow();
	});
</script>
</head>
<body>
	<div id="center" style="overflow:auto;">
		<form name="mainform" method="post" id="mainform"></form>
		<div id="nodeContent" style="height:auto;">
		  <div id='gridDiv'>
		      <div id='maingrid' class="maingrid"></div>
		  </div>
		  <div id='sqlDiv'>
			  <div id='sqlTip' style='display:none;'>
			  <font color="red">sql语句必须包含table定义对应的字段英文名,例如表格包含字段 cname ,sql语句为select col1 as cname from tbl。<br/>
			  	<!-- 去除过滤提示20190521 wf
			  	手动下发需要过滤SQL语句中务必包含FILTER_NM字段。<br/>自动下发以及后置依赖任务SQL中务必包含FILTER_NO字段。<br/>
				例如：SELECT COL2 AS FILTER_NM FROM TBL,SELECT COL3 AS FILTER_NO FROM TBL。<br/>
				 -->
				特殊字符：<br/>
				&nbsp;&nbsp;&nbsp;&nbsp;@{dataDate}:将被替换为数据日期。替换样例：'20180101'<br/>
				&nbsp;&nbsp;&nbsp;&nbsp;@{orgNo}:将被替换为机构编号。替换样例：'001'
				</font>
			  </div>
			  <form name="sqlform" method="post" id="sqlform" action=""></form>
		  </div>
		</div>
	</div>
	<div id="bottom">
		<div class="form-bar">
			<div class="form-bar-inner" style="padding-top: 5px"></div>
		</div>
	</div>
</body>
</html>