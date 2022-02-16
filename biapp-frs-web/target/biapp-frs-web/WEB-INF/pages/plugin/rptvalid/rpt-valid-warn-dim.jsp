<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<html>
<head>
<meta name="decorator" content="/template/template5.jsp">
<script type="text/javascript">
	//全局变量
	var checkId = '${checkId}';
	var indexNo = '${indexNo}';
	var editFlag = '${editFlag}';
	var leftTreeObj;
	$(function() {
		$("#center").prepend(
				'<div id="tipMainDiv"'
					+'style="width: 99.8%; margin: 0 auto; overflow: hidden; position: relative; border: 1px solid gray; padding-top: 1px; padding-bottom: 1px;">'
					+'<div id="tipContainerDiv"'
					   +'style="padding: 5px 2px; background: #fffee6; color: #8f5700;">'
					   +'<div id="tipAreaDiv">'
							+'tips : 数据日期与机构编号为指标共有维度，必选且不可更改<br />'
						+'</div>'
					+'</div>'
				+'</div>');
		//初始化tip
		$("#tipContainerDiv")
				.prepend(
						"<div style='width:24px;float:left;height:16px;background:url(${ctx}/images/classics/icons/comment_yellow.gif) no-repeat' />");
		initTree();
		//添加表单按钮
		var btns = [ {
			text : "保存",
			onclick : f_save
		}, {
			text : "上一步",
			onclick : parent.last
		} ];
		BIONE.addFormButtons(btns);
		
	});
	//保存
	function f_save() {
		window.parent.datasetObj.saveData();
		tree_save();
	}
	
	function initTree(){
		var url = "${ctx}/report/frame/rptvalid/warn/listDimTree.json";
		var data = {
				'checkId' : checkId,
				'indexNo' : indexNo
				   };
		var setting = {
				data:{
					key:{
						name:"text"
					},
					simpleData : {
						enable : true,
						idKey : "id",
						pIdKey : "upId"
						}
				},
				view : {
					selectedMulti : false
				},
				check : {
					enable : true,
					chkStyle : "checkbox",
					chkboxType : {
						"Y" : "s",
						"N" : "s"
					}
				},
				callback:{
					beforeCheck : function(treeId, treeNode){
						if(treeNode.id == "ORG" || treeNode.id == "DATE"){
							return false;
						}
					}
				}
			};
			leftTreeObj = $.fn.zTree.init($("#tree"), setting);
			loadTree(url,leftTreeObj,data);
	}
	
	function loadTree(url,leftTreeObj, data){
		$.ajax({
			cache : false,
			async : true,
			url : url,
			dataType : 'json',
			type : "post",
			data : data,
			success : function(result){
				var nodes = leftTreeObj.getNodes();
				var num = nodes.length;
				for(var i in result){
					if(result[i].data.judge =="1"){
						result[i].checked = true ;
					}else{
						result[i].checked = false ;
					}
				}
				for ( var i = 0; i < num; i++) {
					leftTreeObj.removeNode(nodes[0], false);
				}
				if (result.length > 0) {
					leftTreeObj.addNodes(null, result, false);
					leftTreeObj.expandAll(true);
					var baseNode = leftTreeObj.getNodes()[0];
					var id = baseNode.tId;
					$("#" + id).find("#" + id + "_check").remove();   //去除“全部”前的选择框 
				}
			},
			error:function(){
				//BIONE.tip("加载失败，请联系系统管理员");
			}
		});
	}
	function loadData(){
		var childNodes = leftTreeObj.getCheckedNodes(true);
		if(childNodes){
			for(var i = 0;i<childNodes.length;i++){
				childNodes[i].id = childNodes[i].id;
				childNodes[i].dimType = childNodes[i].data.dimType;
			}
		}
		return childNodes;
	}
	function tree_save(){
		var childNodes = loadData();
		var ids = "";
		var dimTypes = "";
		for(var i = 0;i<childNodes.length;i++){
			ids +=  childNodes[i].id + ",";
			dimTypes += childNodes[i].dimType + ",";
		}
		var _url="${ctx}/report/frame/rptvalid/warn/selectDim";
		if(ids != "" && dimTypes != ""){
			$.ajax({
				async:true,
				type:"POST",
				dataType:"json",
				url:_url,
				data:{
					'checkId' : checkId,
					'indexNo' : indexNo,
					'ids' : ids,
					'dimTypes' : dimTypes
					}
			});
		}
	}
	
</script>
<title>Insert title here</title>
</head>
<body>
	<div id="template.center">
		<div style="height: 2px;"></div>
		<div id="treeContainer"
			style="width: 100%; height: 90%; overflow: auto; clear: both; background-color: #FFFFFF;">
			<ul id="tree"
				style="font-size: 12; background-color: #FFFFFF; width: 92%"
				class="ztree"></ul>
		</div>
	</div>
</body>
</html>