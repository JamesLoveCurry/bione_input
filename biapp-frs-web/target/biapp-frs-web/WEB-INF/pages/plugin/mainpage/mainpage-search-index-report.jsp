<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<%@ include file="/common/meta.jsp"%>
<%@ include file="/common/taglibs.jsp"%>
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<%@ include file="/common/jquery_load.jsp"%>
<%@ include file="/common/ligerUI_load.jsp"%>
<link rel="stylesheet" type="text/css"
	href="${ctx}/css/classics/mainpage/example.css" />
<script type="text/javascript">
	// 首页-指标查询-跳转
	function indxSearchButClick(){
		var data = encodeURI($("#indexNm").val());
		window.parent.parent.changePage("${ctx}/report/frame/idx/preview?indexNm="+data,"指标库管理","指标库管理>>指标字典查询");
		// window.parent.parent.changePage("${ctx}/report/frame/datashow/idx?search=" + data, "数据展现", "数据展现>>指标查询");
	}
	// 首页-报表查询-跳转
	function rptSearchButClick(){
		var data = encodeURI($("#rptName").val());
		window.parent.parent.changePage("${ctx}/rpt/frame/rptmgr/info/view?rptNm="+data,"报表管理","报表管理>>报表目录查询");
		// window.parent.parent.changePage("${ctx}/report/frame/datashow/rpt?search=" + data, "数据展现", "数据展现>>报表展现");
	}
	
	$(function() {
		var contentHeight = $(document).height();
		$("#hisrpt_inbox").height(contentHeight - 2);
		$("#info").height(contentHeight - $(".in_box_titbg").height());
	});
	
</script>
</head>
<body>
	<div id="hisrpt_inbox" class="in_box">
		<div class="in_box_titbg">
			  <div class="in_box_tit"><span class="icon">快速检索</span></div>
		</div>
		<div id="info" class="in_box_con" style="width: 100%; overflow: hidden;padding-bottom:10px;">
			<div id="searchInfo" style="margin-top:15px;margin-left:10px;width:80%;">
				<div style="position:relative;">
					指标检索：<input id="indexNm" type="text" class="l-text" style="width:65%;" />
					<div class="l-trigger">
						<div id="searchIndex" style="width:100%;height:100%;background:url(${ctx}/images/classics/icons/find.png) no-repeat 50% 50% transparent;" onclick="indxSearchButClick()"></div>
					</div>
				</div>
				<div style="margin-top:15px;position:relative;">
					报表检索：<input id="rptName" type="text" class="l-text" style="width:65%;" />
					<div class="l-trigger">
						<div id="searchReport" style="width:100%;height:100%;background:url(${ctx}/images/classics/icons/find.png) no-repeat 50% 50% transparent;" onclick="rptSearchButClick()"></div>
					</div>
				</div>
			</div> 
		</div>
	</div>
</body>
</html>