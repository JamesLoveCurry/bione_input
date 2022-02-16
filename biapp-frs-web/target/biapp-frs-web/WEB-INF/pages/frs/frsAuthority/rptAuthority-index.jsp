<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta name="decorator" content="/template/template9_BS.jsp">
<script type="text/javascript">
$(function(){
    var $centerDom = $(window.parent.document);
	var height=$centerDom.height()-120;
	var navtab1 = window['tab'] = $("#tab").ligerTab({
		height: height,
		onAfterSelectTabItem: function(tabId) {
			if (tabId == 'rptRoleId') {
				if($('#rptRoleId').attr("src") == "" && initFlag)
					$('#rptRoleId').attr("src","${ctx}/frs/submitConfig/frsAuthority/rptToRole");
			}
		},
		contextmenu : false
	});
	var initFlag = false; 
	navtab1.addTabItem({
		tabid : "roleRptId",
		content : "<iframe frameborder='0' id='roleRptId' style='height:" + height + "px;' src='${ctx}/frs/submitConfig/frsAuthority/roleToRpt'></iframe>",
		text : "角色到报表",
		showClose : false
	});
	navtab1.addTabItem({
		tabid :"rptRoleId",
		content : "<iframe  frameborder='0' id='rptRoleId' style='height:" + height + "px;' src=''></iframe>",
		text : "报表到角色",
		showClose : false
	});
	initFlag = true;
	navtab1.selectTabItem("roleRptId");
	
});
</script>
</head>
<body>

</body>
</html>