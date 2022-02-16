<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta name="decorator" content="/template/template9.jsp">
<title>Insert title here</title>
<script type="text/javascript">
	var type = "${type}";
	//var type = "02";
	var taskInsId = "${taskInsId}";
	var rptId = "${rptId}";
	var orgNo = "${orgNo}";
	var dataDate = "${dataDate}";
	var operType = "${operType}";
	var cellNo = "${cellNo}";
	var initFlag = false; 
	var tabObj = null;
	var readOnly = "${readOnly}";
	
	$(function(){	
		initTab();
		initFlag = true;
	});
	
	function initTab(){ 
		var height=$(document).height()-10;
		var grid_height=$(document).height()-30;
		tabObj = $("#tab").ligerTab({
			height: height,
			contextmenu : false,
			onAfterSelectTabItem: function(tabId) {
				if (tabId == 'remark_list') {
					if($('#remark_list').attr("src") == "" && initFlag)
						$('#remark_list').attr("src","${ctx}/rpt/frs/rptfill/showTab?path=rpt-todowork-remark-list&taskInsId="+taskInsId+"&type="+type+"&rptId="+rptId+"&orgNo="+orgNo+"&operType="+operType+"&dataDate="+dataDate+"&cellNo="+cellNo);
				}
			}
		});
		
		tabObj.addTabItem({
			tabid : "remark_info",
			content : "<iframe frameborder='0' name='remark_info' id='remark_info' style='height:" + height + "px;' src='${ctx}/rpt/frs/rptfill/showTab?path=rpt-todowork-remark-info&taskInsId="+taskInsId+"&type="+type+"&rptId="+rptId+"&orgNo="+orgNo+"&dataDate="+dataDate + "&operType=" + operType + "&taskFillOperType=28"+"&cellNo="+cellNo+"&readOnly="+readOnly+"'></iframe>",
			text : "备注填写",
			showClose : false
		});
		
		tabObj.selectTabItem("remark_info");
	}
</script>
</head>
<body>

</body>
</html>