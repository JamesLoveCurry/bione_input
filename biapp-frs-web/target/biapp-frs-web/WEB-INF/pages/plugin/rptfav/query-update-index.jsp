<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<html>
<meta name="decorator" content="/template/template5.jsp">
<head>
<script type="text/javascript">
	var createTime = "${createTime}";
	var instanceId = "${instanceId}";
	var folderNm = "${folderNm}";
	var folderId = "${folderId}";
	
	$(function(){
		$("#instanceId").attr("value", instanceId);
		$("#mainform").ligerForm({
			inputWidth : 150,
			labelWidth : 90,
			fields:[{
				display:"目录",
				name:"menu",
				newline:true,
				type : "select",
				comboboxName : "choose",
				width:250,
				options:{
					onBeforeOpen:function(){
						BIONE.commonOpenDialog("目录","chooseMenu",400,200,"${ctx}/rpt/frame/index/update/come?folderId="+folderId,null);//他爹 -- 弹出目录树
						return false;
					}
				}				
			},{
				display:"查询名称",
				name:"queryNm",
				newline:true,
				type:"text",
				width:250
			}//,{
			//	display:"创建用户",
			//	name:"userName",
			//	newline:true,
			//	type:"text",
			//	width:250
			//},{
			//	display:"创建时间",
			//	name:"createTime",
			//	newline:true,
			//	type:"date",
			//	width:250,
			//	format:'yyyy-MM-dd hh:mm:ss'
			//}
			,{
				display:"备注",
				name:"remark",
				newline:true,
				type:"textarea",
				width:250				
			}]
		});
			liger.get("choose").setValue(folderId);
			liger.get("choose").setText(folderNm);
			$("#mainform [name=queryNm]").val(window.parent.queryNm);
			$("#mainform [name=remark]").val(window.parent.remark);
			$("#mainform [name='userName']").attr("readonly","true").removeAttr("validate").val(window.parent.userName).css('color','#666');
			$("#mainform [name='createTime']").attr("readonly","true").removeAttr("validate").val(createTime).css('color','#666');
	
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
				window.parent.parent.refreshTree();
				BIONE.closeDialogAndReloadParent("addCatalog","maingrid","保存成功");
			},function(){
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
		<form id="mainform" action="${ctx}/rpt/frame/index/update/edit" method="post">
			<input type="hidden" id="instanceId" name="instanceId">
		</form>
	</div>
</body>
</html>