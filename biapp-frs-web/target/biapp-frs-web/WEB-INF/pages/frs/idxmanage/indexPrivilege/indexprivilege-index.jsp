<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta name="decorator" content="/template/template9.jsp">
<title>Insert title here</title>
<script type="text/javascript">
$(function(){
	var flag = "${flag}";
	var height=$(document).height()-33;
	var navtab1 = window['tab'] = $("#tab").ligerTab({
		height: height,
		onAfterSelectTabItem: function(tabId) {
			if (tabId == 'roleindexId') {
				if($('#roleindexId').attr("src") == "" && initFlag)
					$('#roleindexId').attr("src","${ctx}/frs/idxmanage/indexPrivilege/roleindex?flag="+flag);
			}
		},
		contextmenu : false
	});
	var initFlag = false; 
	navtab1.addTabItem({
		tabid : "indexroleId",
		content : "<iframe frameborder='0' id='indexroleId' style='height:" + height + "px;' src='${ctx}/frs/idxmanage/indexPrivilege/indexrole?flag="+flag+"'></iframe>",
		text : "指标到角色",
		showClose : false
	});
	navtab1.addTabItem({
		tabid :"roleindexId" ,
		content : "<iframe  frameborder='0' id='roleindexId' style='height:" + height + "px;' src=''></iframe>",
		text : "角色到指标",
		showClose : false
	});
	initFlag = true;
	navtab1.selectTabItem("indexroleId");
	
});
</script>
</head>
<body>

</body>
</html>