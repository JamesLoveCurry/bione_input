<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<html>
<meta name="decorator" content="/template/template5.jsp">
<head>
<script type="text/javascript">
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
		text : '运行',
		onclick : function() {
			var dataDate = liger.get('date').getValue();
			var data = parent.grid.getCheckedRows();
			var checkedRptIds = "";
			if (!dataDate) {
				BIONE.tip("请选择日期");
				return;
			}
			if (data.length == 0) {
				BIONE.tip("请至少选择一张报表");
				return;
			}
			$.each(data, function(i, n) {
				if (checkedRptIds.length > 0) {
					checkedRptIds+=",";
				}
				checkedRptIds+=n.rptId;
			});
			$.ajax({
			    async : false,//同步
				type : "POST",
				url : "${ctx}/report/frame/enginelog/rpt/isSelectedRptRunning.json",
				dataType : 'json',
				data : {
					checkedRptIds:   checkedRptIds    ,
					dataDate:          dataDate
				},
				success : function(result) {
					if(result.msg=="1"){
							$.ajax({
								async : false,//同步
				 				type : "POST",
				 				url : "${ctx}/report/frame/enginelog/rpt/saveOrUpdateEngineRptSts.json",
				 				dataType : 'json',
				 				data : {
				 					isReRunsal :       "N",
				 					checkedRptIds:   checkedRptIds,
				 					dataDate:          dataDate
				 				},
				 				success : function(rs) {
				 						BIONE.tip(rs.msg);
				 						BIONE.closeDialog("rptRun");
				 				},
				 				error : function(XMLHttpRequest, textStatus, errorThrown) {
				 					BIONE.tip('操作失败,错误信息:' + textStatus);
				 				}
				 			});
					}else if(result.msg=="0"){
						var   rptNames  =  result.rptNames;
						BIONE.tip('所选报表['+rptNames+']还未完成执行任务！');
					}		
				},
				error : function(XMLHttpRequest, textStatus, errorThrown) {
					BIONE.tip('获取数据失败,错误信息:' + textStatus);
				}
			});
		}
	}]);
});
</script>
</head>
<body>
	<div id="template.center">
		<form id="mainform" action="#"  method="post"></form>
	</div>
</body>
</html>