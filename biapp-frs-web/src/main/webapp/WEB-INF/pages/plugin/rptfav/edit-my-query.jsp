<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<html>
<meta name="decorator" content="/template/template5.jsp">
<head>
<script type="text/javascript">
	var upFolderId = "${upFolderId}";
	var folderNm = "${folderNm}";
	var folderId = "${folderId}";
	$(function(){
		$("#upFolderId").attr("value", upFolderId);
		$("#folderId").attr("value", folderId);
		$("#mainform").ligerForm({
			inputWidth : 150,
			labelWidth : 90,
			fields:[{
				display:"文件夹名称",
				name:"folderNm",
				newline:true,
				type:"text",
				validate:{
					required:true,
					maxlength:100,
					remote:{url:"${ctx}/rpt/frame/rptfav/query/findFolderNm",
							type : "post",
						 	data : {
						 		upFolderId : upFolderId
						  }		
					},
					messages:{
						remote:"文件夹名称重复!"
					}
				}
			}]
		});
		
		if(folderNm != null && folderNm != ""){
			$("#mainform [name=folderNm]").val(window.parent.currentNode.text);
		}
	
		var btn = [];
		btn.push({
			text:'取消',
			onclick:cancelLine
		});
		btn.push({
			text:'保存',
			onclick:saveLine
		});
		
		BIONE.addFormButtons(btn);
		jQuery.metadata.setType("attr","validate");
		BIONE.validate($("#mainform"));
		
	});
	
	
	function saveLine(){
		BIONE.submitForm($("#mainform"),function(){
				window.parent.refreshTree();
				BIONE.closeDialogAndReloadParent("addCatalog","maingrid","保存成功");
			},function(){
				window.parent.refreshTree();
				BIONE.closeDialog("addCatalog", "保存失败");
			});
		}
	
	function cancelLine(){
		BIONE.closeDialog("addCatalog");
	}
</script>
</head>
<body>
	<div id="template.center">
		<form id="mainform" action="${ctx}/rpt/frame/rptfav/query/edit" method="post">
			<input type="hidden" id="upFolderId" name="upFolderId">
			<input type="hidden" id="folderId" name="folderId">
		</form>
	</div>
</body>
</html>