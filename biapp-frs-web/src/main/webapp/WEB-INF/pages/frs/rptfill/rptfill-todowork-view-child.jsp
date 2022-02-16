<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ include file="/common/meta.jsp"%>
<%@ include file="/common/taglibs.jsp"%>
<html>
<head>
<meta name="decorator" content="/template/template14.jsp">
<%@ include file="/common/spreadjs_load.jsp"%>
<script type="text/javascript">
var View, Spread;
$(function() {
	initDesign('${color}');
});

// 初始化设计器
function initDesign(color){
	require.config({
		baseUrl : "${ctx}/js/",
		paths:{
			"view" : "report/show/views/rptview"
		}
	});
	require(["view"] , function(view){
		var orgNo = "${orgNo}";
		var rptId = "${rptId}";
		var dataDate = "${dataDate}";
		var lineId = "${lineId}";
		var argsArr = [];
		var orgName = '';
		if("${orgNm}" != null){
			orgName = "${orgNm}";
		}
		var args1 = {'DimNo':'ORG','Op':'=','Value':orgNo};
		//var args2 = {'dimNo':'busiLineId','op':'=','value':lineId};
		argsArr.push(args1);
		//argsArr.push(args2);
//		var args = '["{"dimNo":"org","op":"=","value":"' + orgNo + '"}","{"dimNo":"busiLineId","op":"=","value":"' + lineId + '"}"]';
		var settings = {
				targetHeight : ($("#center").height() - 8),
				ctx : "${ctx}",
				readOnly : true,
				cellDetail : false,
				toolbar : false,
				canUserEditFormula : false,
				inValidMap : color,
				initFromAjax : true,
				searchArgs : JSON2.stringify(argsArr),
				ajaxData:{
					rptId : rptId,
					dataDate : dataDate,
					busiLineId : lineId,
					orgNm : orgName
				}
		};
		View = view;
		var spread = view.init($("#spread") , settings);
		Spread = spread;
	})
}

</script>
</head>
<body>
	<div id="template.center">
		<div id="spread" style="width:99.5%;border-bottom:1px solid #D0D0D0;"></div>
	</div>
</body>

</html>