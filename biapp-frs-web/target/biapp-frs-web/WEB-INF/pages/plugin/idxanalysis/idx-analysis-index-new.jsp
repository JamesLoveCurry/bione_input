<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<%@ include file="/common/jquery_load.jsp"%>
<%@ include file="/common/ligerUI_load.jsp"%>
<%@ include file="/common/zTree_load.jsp"%>
<link rel="stylesheet" href="${ctx}/js/familytree/familytree.css">
<script type="text/javascript" src="${ctx}/js/familytree/familytree.js"></script>
<html>
<head>


<script type="text/javascript">
	var _id = 0;
	$(function(){
		var ft = $("#familytree").familytree({
			async : true,
			data : [{
				id : '0',
				text : '测试'
			}],
			callback:{
				onAsync : function(id,dom,flag){
					_id++;
					if(flag === "children"){
						ft.addChild(id,{
							id: _id+"",
							text: "子测试"+_id
						})
					}
				},
				onClick : function(id,dom,data){
					alert(data.id+"-"+data.text);
				},
				onDblClick : function(id,dom,data){
					alert("db"+data.id+"-"+data.text);
				}
			}
		});		
	});
	

</script>
</head>
<body >
<div id="template.center">
	<div  id="familytree"  style="height:100%;width:100%;overflow:auto;"> 
		
	</div> 
</div>
</body>
</html>