<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<html>
<meta name="decorator" content="/template/template22.jsp">
<head>
<script type="text/javascript">
	var currentNode={};
	var url;
	var leftTreeObj;
	var cenId;
	var srcRpt= "${ctx}/rpt/frame/rptfav/report";
	var srcIdx = "${ctx}/rpt/frame/rptfav/index";
	var height;
	var folderNm;
	
	$(function() {
		initTree();
		refreshTree();
		initTab(); //tab
	});
	
	function cascade() {
		$.ligerDialog.confirm('是否全部删除?(请注意，该操作将会删除此文件夹下的所有的文件夹和文件！)', function (flag) {
			if (flag) {
				$.ajax({
					url: '${ctx}/rpt/frame/rptfav/query/cascade',
					data: {
						folderId: currentNode.id
					},
					dataType: 'json',
					type: 'post',
					success: function (data) {
						loadTree("${ctx}/rpt/frame/rptfav/query/getTreeNode", leftTreeObj);
						BIONE.tip('文件夹' + currentNode.text + '下有所文件已删除！');
					}
				});
			}
		});
		
	}
	
	function refreshTree() {  //刷新树
		if (leftTreeObj) {
			loadTree("${ctx}/rpt/frame/rptfav/query/getTreeNode", leftTreeObj);
		}
	}
	function initTree() {
		var setting = {
			data : {
				key : {
					name : 'text'
				},
				simpleData : {
					enable : true,
					idKey : "id",
					pIdKey : "upId"
				}
			},
			callback : {
				onClick : zTreeOnClick
			}
		};
		leftTreeObj = $.fn.zTree.init($("#tree"), setting);
		
		//加载数据
		loadTree("${ctx}/rpt/frame/rptfav/query/getTreeNode", leftTreeObj);
		//增加节点按钮
		/* var menuconfig = {
				width :90,
				items : [ {
					icon : 'add',
					text : '新建',
					click : addCatalog
				},{
					line : true
				},{
					icon : 'modify',
					text : '修改',
					click : editCatalog
				},{
					line : true
				},{
					icon : 'delete',
					text : '删除',
					click : deleteCatalog
				}]
			};
		
		
		$("#treeToolbar").ligerTreeToolBar({
				items : [ {
					icon : 'config',
					text : '目录',
					menu : menuconfig
				}],
				treemenu : false
			}); */
			
		$("#treeToolbar").ligerTreeToolBar({
			width :90,
			items : [ {
				icon : 'add',
				text : '新建',
				click : addCatalog
			},{
				line : true
			},{
				icon : 'modify',
				text : '修改',
				click : editCatalog
			},{
				line : true
			},{
				icon : 'delete',
				text : '删除',
				click : deleteCatalog
			}]
		});
		
		$("#treeSearchIcon").bind("click",function(e){
			setTree($("#treeSearchInput").val() == "");
		});
		$("#treeSearchInput").bind("keydown",function(e){
			if(e.keyCode == 13){
				setTree($("#treeSearchInput").val() == "");
			}
		});
		
		function setTree(flag){
			if(flag){
				loadTree("${ctx}/rpt/frame/rptfav/query/getTreeNode",leftTreeObj);
				leftTreeObj = $.fn.zTree.init($("#tree"), setting,zTreeOnClick());
			}
			else{
				loadTree("${ctx}/rpt/frame/rptfav/query/getTreeNode?",leftTreeObj,{folderNm:$("#treeSearchInput").val()});
				leftTreeObj = $.fn.zTree.init($("#tree"), setting,zTreeOnClick());
			}
		}
	}
	
	function loadTree(url, component, data) {
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
				for ( var i = 0; i < num; i++) {
					component.removeNode(nodes[0], false);
				}
				if (result.length > 0) {
					component.addNodes(null, result, false);
					component.expandAll(true);
				}
			},
			error : function(result, b) {
				//BIONE.tip('发现系统错误 <BR>错误码：' + result.status);
			}
		});
	}
	
	function zTreeOnClick(event, treeId, treeNode,clickFlag){
		currentNode = treeNode;
		if(currentNode.id == '0'){
			srcRpt = "${ctx}/rpt/frame/rptfav/report?folderId="+currentNode.id+"&type=catalog";
			srcIdx = "${ctx}/rpt/frame/rptfav/index?folderId="+currentNode.id+"&type=catalog";
			tabObj.removeTabItem('index');
			tabObj.removeTabItem('report');
			window['indexflag']=false;
			window['reportflag']=false;
			tabObj.addTabItem({
				tabid : 'report',
				text : '我的报表',
				showClose : false,
				content : "<iframe src='' id='report' name='report' style='height: " + height + "px;' frameborder='0'></iframe>"
			});
			tabObj.addTabItem({
				tabid : 'index',
				text : '我的指标查询',
				showClose : false,
				content : "<iframe src='' id='index' name='index' style='height: " + height + "px;' frameborder='0'></iframe>"
			});
			window['indexflag']=true;
			window['reportflag']=true;
			$("#report").attr('src','');
			tabObj.selectTabItem('report');
		}
		if(currentNode.params.type == "catalog"){
			srcRpt = "${ctx}/rpt/frame/rptfav/report?folderId="+currentNode.id+"&type=catalog";
			srcIdx = "${ctx}/rpt/frame/rptfav/index?folderId="+currentNode.id+"&type=catalog";
			tabObj.removeTabItem('index');
			tabObj.removeTabItem('report');
			window['indexflag']=false;
			window['reportflag']=false;
			tabObj.addTabItem({
				tabid : 'report',
				text : '我的报表',
				showClose : false,
				content : "<iframe src='' id='report' name='report' style='height: " + height + "px;' frameborder='0'></iframe>"
			});
			tabObj.addTabItem({
				tabid : 'index',
				text : '我的指标查询',
				showClose : false,
				content : "<iframe src='' id='index' name='index' style='height: " + height + "px;' frameborder='0'></iframe>"
			});
			window['indexflag']=true;
			window['reportflag']=true;
			$("#report").attr('src','');
			
			tabObj.selectTabItem('report');
		}
		if(currentNode.params.type == "idx"){
 			window.parent.BIONE.commonOpenDialog("我的指标", "alertRptIndexs", window.parent.parent.parent.$("body").width()-10, window.parent.parent.parent.$("body").height()-10,"${ctx}/report/frame/datashow/idx/show?instanceId="+currentNode.id+"&mode="+'1');
		}
		if(currentNode.params.type == "report"){
			openReport();
		}
	}
	
	//tab 的url现在是写死的 重构的时候查询数据库 for循环出type类型 然后拼到url 以后优化再说
	initTab = function() {
		var tabChangeFlag = false; // tab 是否可切换的标识
		tabObj = $("#tab").ligerTab({
			changeHeightOnResize : true,
			contextmenu : false,
			onBeforeSelectTabItem : function(tabId) {
				if (!tabChangeFlag) {
					return false;
				};
			},
			onAfterSelectTabItem : function(tabId) {
					 if (tabId == 'report') {
						if ($("#report").attr('src') == ""&&window[tabId+'flag']==true) {
							$("#report").attr({src : srcRpt});
						};
						window[tabId+'flag']=false;
					 } 
					 if (tabId == 'index') {
						if ($("#index").attr('src') == ""&&window[tabId+'flag']==true) {
							$("#index").attr({src : srcIdx});
						};
						window[tabId+'flag']=false;
					 } 
			}
	   });
			
		height = $("#tab").height() - 20;
		tabObj.addTabItem({
			tabid : 'report',
			text : '我的报表',
			showClose : false,
			content : "<iframe src='' id='report' name='report' style='height: " + height + "px;' frameborder='0'></iframe>"
		});
		tabObj.addTabItem({
				tabid : 'index',
				text : '我的指标查询',
				showClose : false,
				content : "<iframe src='' id='index' name='index' style='height: " + height + "px;' frameborder='0'></iframe>"
			});
		tabChangeFlag = true;
		window['indexflag']=true;
		window['reportflag']=true;
		tabObj.selectTabItem('report');
	};
		
		function openReport() {
			$.ajax({
				   type: "POST",
				   url: "${ctx}/rpt/frame/rptfav/query/rptType",
				   data: {
						rptId: currentNode.id
					},
				   success: function(data){
					   if ('01' == data) {
							window.parent.BIONE.commonOpenDialog("外部报表", "alertRptIndexs", window.parent.parent.parent.$("body").width()-10, window.parent.parent.parent.$("body").height()-10,'${ctx}/report/frame/datashow/rpt/show/' + currentNode.id);
						} else if ('02' == data) {
							window.parent.BIONE.commonOpenDialog("平台报表", "alertRptIndexs", window.parent.parent.parent.$("body").width()-10, window.parent.parent.parent.$("body").height()-10,'${ctx}/rpt/rpt/rptplatshow/show?rptId=' + currentNode.id+"&rptNm="+encodeURI(encodeURI(currentNode.text)));
						}
				   }
				});
		}
		
		//新增
		function addCatalog() {
			if(!currentNode.id){
				BIONE.tip("请选择文件夹");
				return;
			}
			else{
				if(currentNode.id == "0"){
					url = "${ctx}/rpt/frame/rptfav/query/addCatalog?upFolderId=" + currentNode.id;
				}else{
					if(currentNode.params.type != "catalog"){
						BIONE.tip("请选择目录新增");
						return;
					}
					else
					{
						url = "${ctx}/rpt/frame/rptfav/query/addCatalog?upFolderId=" + currentNode.id;
					}
				}
				
			}
			BIONE.commonOpenSmallDialog("新增文件夹", "addCatalog", url); //他爹
		}
		
		//修改
		function editCatalog() {
			if(!currentNode.id){
				BIONE.tip("请选择文件夹");
				return;
			}else{
				if (currentNode.id == "0") {
					BIONE.tip("根目录不能修改");
					return;
				}else{
					if(currentNode.params.type != "catalog"){
						BIONE.tip("请选择目录修改");
						return;
					}
					if(!currentNode.upId){
						BIONE.tip("请选择文件夹");
						return;
					}else{
						url = "${ctx}/rpt/frame/rptfav/query/addCatalog?upFolderId=" + currentNode.upId+"&folderId="+currentNode.id+"&folderNm="+currentNode.text;
					}
				}
					BIONE.commonOpenSmallDialog("修改文件夹","addCatalog",url);
			}
		}

		//删除
		function deleteCatalog() {
			if(currentNode.params.type != "catalog"){
				BIONE.tip("请选择目录删除");
				return;
			}
			if(!currentNode){
				BIONE.tip("未选中文件夹!");
				return;
			}
			if( !currentNode.upId ){  //currentNode.id == "0" ||
				BIONE.tip("根目录不能被删除!");
				return;
			}
			$.ligerDialog.confirm("确定删除 [" + currentNode.text + "] ？",function(sun){
				if(sun){
					$.ajax({
						url : "${ctx}/rpt/frame/rptfav/query/delete?folderId="+currentNode.id,
						success:function(ming){
							if(ming == "" || ming.length == 0){
								BIONE.tip("删除成功!");
								loadTree("${ctx}/rpt/frame/rptfav/query/getTreeNode", leftTreeObj);
								return;
							}if(ming == "exit"){
								BIONE.tip("该文件夹下存在文件夹");
								cascade();
								return;
							}if(ming == "wenjian"){
								BIONE.tip("该文件夹下存在文件");
								cascade();
								return;
							}
						},error : function(XMLHttpRequest,
								textStatus, errorThrown) {
							BIONE.tip('删除失败,错误信息:'
									+ textStatus);
						}
					});
				}
			});
		}
		

</script>
</head>
<body>
	<div id="template.left.up.icon">
		<img src="${ctx}/images/classics/icons/application_side_tree.png" />
	</div>
	<div id="template.left.up">
		<span style="font-size: 12" id="left_up_span">目录</span>
	</div>
	<div id="template.left.down">
		<div id="btn"></div>
	</div>
	<div id="template.right" >
	<div id="tab"></div></div>
</body>
</html>