<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<html>
<head>
<meta name="decorator" content="/template/template14.jsp">
<style type="text/css">
#center {
	overflow: auto;
}
</style>
<script type="text/javascript">
    //修改显示界面的展示
	var mainform;
	var id = "${orgId}";
	var orgInfo = parent.orgInfo;
	var upAddrNo =orgInfo.upAddrNo;//上级编号
	var addrName =orgInfo.addrName;//机构名称
	var addrNo = orgInfo.addrNo;//机构编号
	var field = [
			{
				name : 'addrLvl',
				type : 'hidden'
			},
			{
				display : "机构编号",
				name : "addrNo",
				newline : true,
				type : "text"
			},
			{
				display : "机构名称",
				name : "addrNm",
				newline : false,
				type : "text",
				validate : {
					required : true,
					maxlength : 100,
					remote : "${ctx}/bione/frs/addreginfo/checkCodeNm?oldAddrNm=" + encodeURI(encodeURI(addrName)),
							messages : {
						remote : "行政区域名称重复"
				  }
				}
			},
			{
				display : "上级机构编号 ",
				name : "upAddrNo",
				newline : true,
				type : 'hidden'
			},
			{
				display : "命名空间 ",
				name : "nameSpace",
				newline : true,
				type : 'hidden'
			}];

	//创建表单结构 
	function ligerFormNow() {
		mainform = $("#mainform").ligerForm({
			inputWidth : 180,
			labelWidth : 125,
			space : 40,
			fields : field
		});
		jQuery.metadata.setType("attr", "validate");
		BIONE.validate($("#mainform"));
	}
	$(function() {
		ligerFormNow();
		$("#mainform [name='addrNo']").val(addrNo).attr("readonly", "true");//机构编号
		$("#mainform [name='addrNo']").css('color','#333');
		$("#mainform [name='upAddrNo']").val(upAddrNo).attr("readonly", "true");//上级机构编号
		 if (addrName == null || addrName == "null") {
			addrName = "";
		} 		
		$("#mainform [name='addrNm']").val(addrName);//机构编号

	});

	function f_save() {
		var treeObj = window.parent.leftTreeObj;

		BIONE.submitForm($("#mainform"), function() {
			BIONE.tip("保存成功");
			if (upAddrNo == "0") {
				upAddrNo = "-1";
			}
			BIONE.tip("保存成功");
			BIONE.refreshAsyncTreeNodes(treeObj, "id", upAddrNo, "refresh");
		}, function() {
			BIONE.tip("保存失败");
		});
	};
</script>

<title>新增区域管理</title>
</head>
<body>
	<div id="template.center">
		<form name="mainform" method="post" id="mainform"
			action="${ctx}/bione/frs/addreginfo/update"></form>
	</div>
</body>
</html>