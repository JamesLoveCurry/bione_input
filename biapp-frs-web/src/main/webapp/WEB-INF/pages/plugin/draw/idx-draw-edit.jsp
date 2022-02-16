<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<html>
<meta name="decorator" content="/template/template5.jsp">
<head>
<script type="text/javascript">
var ids = window.parent.indexNos;
$(function() {
	$("#mainform").ligerForm({
		fields : [ {
			display : "日期选择",
			name : "date",
			newline : true,
			type : "date",
			options: {
				format: 'yyyyMMdd'
			}
		}]
	});
	BIONE.addFormButtons([{
		text : '翻牌',
		onclick : function() {
			var dataDate = liger.get('date').getValue();
			if (!dataDate) {
				BIONE.tip("请选择日期");
				return;
			}
			rptDraw(dataDate);
		}
	}]);
});

function rptDraw(dataDate){
	var url = "${ctx}/rpt/idx/draw/idxDraw";
	$.ajax({
		cache : false,
		async : true,
		url : url,
		dataType : 'json',
		data:{
			"ids" : ids,
			"dt" : dataDate
		},
		type : "post",
		beforeSend : function() {
			BIONE.loading = true;
			BIONE.showLoading("正在加载数据中...");
		},
		complete : function() {
			BIONE.loading = false;
			BIONE.hideLoading();
		},
		success : function(result) {
			if(result.msg == 'ok'){
				BIONE.closeDialogAndReloadParent("editWin", "maingrid","翻牌成功");
			}else{
				BIONE.closeDialog("editWin", "翻牌失败");
			}
		},
		error : function(result, b) {
			//
		}
	});
}
</script>
</head>
<body>
	<div id="template.center">
		<form id="mainform" action="#"  method="post"></form>
	</div>
</body>
</html>