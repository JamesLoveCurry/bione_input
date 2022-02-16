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
	/**
	实现新增功能
	*/
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
				type : "text",
				validate : {
					required : true,
					maxlength : 100/* ,
					remote : "${ctx}/bione/frs/addreginfo/checkCode",
					messages : {
						remote : "行政区域编号重复"
				  } */
				}
		},
			{
				display : "机构名称",
				name : "addrNm",
				newline : false,
				type : "text",
				validate : {
					required : true,
					maxlength : 100/* ,
					remote : "${ctx}/bione/frs/addreginfo/checkCodeNm?oldAddrNm=",
					messages : {
						remote : "行政区域名称重复"
				  } */
				}
			},
			{
				display : "上级机构编号 ",
				name : "upAddrNo",
				newline : true,
				type : "text"
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
		if( addrNo==null || addrNo =="" ){
			addrNo ='-1';
			upAddrNo="0";
			
		}
		$("#mainform [name='upAddrNo']").val(addrNo).attr("readonly", "true");//上级机构编号	
	});

	function f_save() {
		 var treeObj = window.parent.leftTreeObj;
		 if ($("#mainform").valid()) {
				$.ajax({
					cache : false,
					async : true,
					url : "${ctx}/bione/frs/addreginfo/validate",
					dataType : 'text',
					data : {
						addrNo:$("#addrNo").val(),
						addrNm:$("#addrNm").val()
					},
					type : "POST",
					success : function(result) {
						if(result=="null" || result.trim()==""){
							BIONE.submitForm($("#mainform"), function() {
								 if (addrNo == "-1") {
									 addrNo = "0";
								}
								upAddrNo = addrNo; //上級节点
								BIONE.refreshAsyncTreeNodes(treeObj, "id", upAddrNo, "refresh");
								BIONE.tip("保存成功");
							 }, function() {
								BIONE.tip("保存失败");
							}); 
						}
						else{
							BIONE.tip(result);
							return;
						}		
					},
					error : function(result, b) {
						//BIONE.tip('发现系统错误 <BR>错误码：' + result.status);
					}
				});
			} 
	};
</script>

<title>新增区域管理</title>
</head>
<body>
	<div id="template.center">
		<form name="mainform" method="post" id="mainform"
			action="${ctx}/bione/frs/addreginfo/save"></form>
	</div>
</body>
</html> 