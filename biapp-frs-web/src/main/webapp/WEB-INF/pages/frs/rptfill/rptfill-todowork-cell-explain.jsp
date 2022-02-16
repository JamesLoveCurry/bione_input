<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ include file="/common/meta.jsp"%>
<html>
<head>
<meta name="decorator" content="/template/template14.jsp">
<%@ include file="/common/taglibs.jsp"%>
<script type="text/javascript">
	var dataDate = '${dataDate}';
	var orgNo = '${orgNo}';
	var rptIndexNo = '${rptIndexNo}';
	var cellNo = '${cellNo}';
	var rptTmpId = '${rptTmpId}';
</script>
<script type="text/javascript">
	var $content = $(window);
	var textareaWidth = $content.width();
	textareaWidth = (textareaWidth - 400) / 2;
	$(function() {
		initForm();
		initData();
	});

	function initForm() {
		var field = [ {
			display : "业务口径",
			name : "caliberExplain",
			newline : false,
			type : "textarea",
			width : textareaWidth
		}, {
			display : "技术口径",
			name : "caliberTechnology",
			newline : false,
			type : "textarea",
			width : textareaWidth
		} ];
		var mainform = $("#mainform").ligerForm({
			inputWidth : 750,
			labelWidth : 90,
			space : 40,
			fields : field
		});
	}

	function initData() {
		$.ajax({
			cache : false,
			async : true,
			url : "${ctx}/rpt/frs/rptfill/getCaliberExplain",
			data : {
				dataDate : dataDate,
				cellNo : cellNo,
				tmpId : rptTmpId
			},
			dataType : 'json',
			type : "post",
			beforeSend : function() {
				BIONE.loading = true;
				BIONE.showLoading("正在加载数据中...");
			},
			complete : function() {
				BIONE.loading = false;
				BIONE.hideLoading();
			},
			success : function(resultMap) {
				if (resultMap) {
					if(resultMap.caliberExplain){
						$("#mainform [name='caliberExplain']").val(
								resultMap.caliberExplain);
						$("#mainform [name='caliberExplain']").attr("readonly",
								"true").css("color", "black");
					}
					if(resultMap.caliberTechnology){
						$("#mainform [name='caliberTechnology']").val(
								resultMap.caliberTechnology);
						$("#mainform [name='caliberTechnology']").attr("readonly",
								"true").css("color", "black");
					}
				}
			},
			error : function(resultMap, b) {
				BIONE.tip('发现系统错误 <BR>错误码：' + resultMap.status);
			}
		});
	}
</script>
</head>
<body>
	<div id="template.center">
		<form name="mainform" method="post" id="mainform"></form>
	</div>
</body>
</html>