<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ include file="/common/meta.jsp"%>
<%@ include file="/common/taglibs.jsp"%>
<html>
<head>
<meta name="decorator" content="/template/template14.jsp">
<%@ include file="/common/spreadjs_load.jsp"%>
<script type="text/javascript">
var Design, spread;
var objTmp;
$(function() {
	initDesign();
});
var tmpId="${tmpId}";
var verId="${verId}";
// 初始化设计器
function initDesign(){
	require.config({
		baseUrl : "${ctx}/plugin/js/",
		paths:{
			"design" : "cfg/views/rptdesign"
		}
	});
	require(["design"] , function(ds){
		var settings = {
				ctx : "${ctx}",
				targetHeight : ($("#center").height() - 8) ,
				readOnly : true,
				cellDetail : false,
				toolbar : false,
				showType: 'busino',
				initFromAjax : true,
				ajaxData : {
					templateId : tmpId,
					verId : verId
				}
		};
		Design = ds;
		spread = ds.init($("#spread") , settings);
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