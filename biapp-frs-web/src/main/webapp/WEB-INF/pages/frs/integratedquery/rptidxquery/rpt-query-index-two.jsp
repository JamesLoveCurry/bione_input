<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<html>
<meta name="decorator" content="/template/template21A.jsp">
<head>
<style type="text/css">
.searchtitle img {
	display: block;
	float: left;
}
.searchtitle span {
	display: block;
	float: left;
	margin-top: 1px;
	margin-left: 2px;
	line-height: 19px;
}
.l-tab-links {
/* 	position: absolute; */
}
</style>
<script type="text/javascript">
	var app = {
		ctx : '${ctx}',
		queryObj : '${queryObj}'? $.parseJSON('${queryObj}'):'',
		nodeType : '2'		
	};
</script>
<%@ include file="/common/spreadjs_load.jsp"%>
<script type="text/javascript">
var Spread;
$(function() {
	//app.rptQuery_execQuery(queryObj.querysItem, queryObj.rptInfo, queryObj.querysRel);
	//app.rptQuery_execQuery('${argsArr}','${rptInfo}', '${lineInfo}');
	
	require.config({
		baseUrl : "${ctx}/plugin/js/",
		paths:{
			"view" : "show/views/rptview"
		}
	});
	require(["view"] , function(view){

		var rptInfo = $.parseJSON('${rptInfo}');
		var orgNo = rptInfo.orgNo;
		var argsArr = [];
		var args1 = {'DimNo':'ORG','Op':'=','Value':orgNo};
		argsArr.push(args1);
		
		var rptId = rptInfo.rptId;
		var dataDate = rptInfo.dataDate;
		var lineId = "";
		var orgName = rptInfo.orgNm;
		var fileName = '';
		var settings = {
				targetHeight : ($("#content").height() - 28),
				ctx : "${ctx}",
				readOnly : true,
				cellDetail : true,
				toolbar : false,
				canUserEditFormula : false,
				initFromAjax : true,
				searchArgs : JSON2.stringify(argsArr),
				ajaxData:{
					rptId : rptId,
					dataDate : dataDate,
					busiLineId : lineId,
					fileName : fileName,
					orgNm : orgName
				}
		};
		View = view;
		var spread = view.init($('#spread') , settings);
		Spread = spread;
	});
}); 
</script>
</head>
<body>
</body>
</html>