<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<html>
<head>
<%@ include file="/common/taglibs.jsp"%>
<meta name="decorator" content="/template/template5.jsp">
<script type="text/javascript">
 	var rptCode = "${rptCode}";
	var btns =[]; 
	var treeObj;
	$(function(){
		initTree();
		initBtn();
		//报表树搜索
		$("#treesearchtext").bind("keydown",function(e){
			if(e.keyCode == 13){
				var sn = $("#treesearchtext").val();
				searchReportCodeNodes(sn);
			}
		}).bind("focusout",function(e){
			var sn = $("#IdxInfoSearch").val();
			searchReportCodeNodes(sn);
		});
	});
	
	//初始化树
	function initTree() {
		window['rptCodeTree'] = $.fn.zTree.init($("#tree"),{
			check:{
				enable: true,
				chkStyle:"checkbox",
				chkboxType: { "Y": "s", "N": "ps" }
			},
			data:{
				key:{
					name : "text"
				},
				simpleData : {
					enable : true,
					idKey : "id",
					pIdKey : "upId"
				}
			},
			view:{
				selectedMulti: false,
				showLine: true
			}
		});
		
		
		//加载报表编码数据
		loadTree("${ctx}/frs/pbmessage/rptTreeList", rptCodeTree);
	}
	
	//加载树中数据
	function loadTree(url,component,data){
		$.ajax({
			cache : false,
			async : true,
			url : url,
			dataType : 'json',
			data : data,
			type : "post",
			beforeSend : function() {
				BIONE.loading = true;
				BIONE.showLoading("正在加载数据中...");
			},
			complete : function() {
				BIONE.loading = false;
				BIONE.hideLoading();
			},
			success : function(result) {
				var nodes = component.getNodes();
				var num = nodes.length;
				for(var i=0;i<num;i++){
					component.removeNode(nodes[0],false);
				}
				if(result.length>0){
					component.addNodes(nodes[0],result,false);
					component.expandAll(true);
					if("${rptCode}"){
						var checkNodes = "${rptCode}";
						var nodeArr = checkNodes.split(";");
						for(var i = 0; i < nodeArr.length; i++){
							var node = component.getNodeByParam("id", nodeArr[i], null);
							if(node){
								node.checked = true;
							}
						}
						rptCodeTree.refresh();
					}
				}
			},
			error : function(result, b) {
				//BIONE.tip('发现系统错误 <BR>错误码：' + result.status);
			}
		});
	}

	//初始化按钮
	function initBtn(){
		btns.push(
				{
					text : "取消",
					onclick : function(){
						BIONE.closeDialog("rptCodeWin");
					}
				},	
				{
					text : "选择",
					onclick : function() {
						var showNodes = "";
						var hasCataflag = true;
						var nodes = rptCodeTree.getCheckedNodes();
						for(var i = 0; i < nodes.length; i++){
							if(nodes[i].isRpt == "1"){
								showNodes += nodes[i].id + ";";
								hasCataflag = false;
							}
						}
						if(hasCataflag){
							BIONE.tip("请选择报表");
						}else{
							showNodes = showNodes.substring(0, showNodes.length - 1);
							var c = window.parent.jQuery.ligerui.get("rptCode_sel");
							c._changeValue(showNodes, showNodes);
							BIONE.closeDialog("rptCodeWin");
						}
					}
			 }
		);
		BIONE.addFormButtons(btns);
	}
	
	//报表编码树搜索
	function searchReportCodeNodes(sn){
		var searchValue = $("input[id='treesearchtext']").val();
		if(sn){
			var data = "nodeNo="+sn;
		}else{
			var data = "nodeNo="+searchValue;
		}
		
		loadTree("${ctx}/frs/pbmessage/rptTreeList", rptCodeTree, data);
	}
	
</script>
</head>
<body>
	<div id="template.center">
		<div class="content" style="border: 1px solid #C6C6C6; height: 98%">
			<div id="lefttable" width="90%" border="0">
				<div width="100%"
					style="height:30px;background-image: url(${ctx}/css/classics/ligerUI/Gray/images/ui/gridbar.jpg);border-bottom:1px solid #D6D6D6;">
					<div width="8%"
						style="padding-left: 10px; float: left; position: relative; height: 20p; margin-top: 8px">
						<img src="${ctx}/images/classics/icons/application_side_tree.png" />
					</div>
					<div width="30%">
						<span
							style="font-size: 12; float: left; position: relative; line-height: 30px; padding-left: 2px">
							<span id="treeTitle" style="font-size: 12">报表编码</span>
						</span>
					</div>
					<div width="60%"> 
						<div style="float: right; position: relative; padding-right: 3px; padding-top:4px;">
							<div width="70%" class="l-text" style="display: block; float: left; height: 17px; margin-top: 2px; width: 180px;">
								<input id="treesearchtext" name="treesearch" type="text" class="l-text l-text-field" style="float: left; height: 16px; width: 180px;" />
							</div>
							<div width="30%" style="display: block; float: left; margin-left: 8px; margin-top: 3px; margin-right: 3px;">
								<a id="treesearchbtn" href='javascript:searchReportCodeNodes()'><img src="${ctx}/images/classics/icons/find.png" /></a>
							</div>
						</div>
					</div>
				</div>
			</div>
			<div id="treeToolbar"
				style="border-bottom-width: 1px; border-bottom-style: solid; border-bottom-color: #D6D6D6;"></div>
			<div id="treeContainer"
				style="width: 100%; height: 90%; overflow: auto; clear: both;">
				<ul id="tree"
					style="font-size: 12; background-color: F1F1F1; width: 90%"
					class="ztree"></ul>
			</div>
		</div>
	</div>
</body>
</html>