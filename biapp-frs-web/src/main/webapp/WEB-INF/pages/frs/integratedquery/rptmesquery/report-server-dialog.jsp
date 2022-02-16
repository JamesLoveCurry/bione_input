<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<head>
<meta name="decorator" content="/template/template5.jsp">
<script type="text/javascript">var grid, tabObj, canSelect = false, checkServerId, checkServerName;</script>
<script type="text/javascript">
	$(function() {
		initTab();		/* 初始化 Tab */
		initSearchForm();	/* 初始化查询表单 */
		initGrid();		/* 初始化服务器 GRID */
		changeBtn();	
		BIONE.addSearchButtons("#search", grid, "#searchbtn");	/* 生成搜索按钮 */
		$("div .searchtitle").hide();
	});
	/* 渲染 tab */
	function initTab() {
		$("#navtab1").ligerTab({
			changeHeightOnResize : true,
			contextmenu : true,
			onBeforeSelectTabItem : function(tabId) {
				return canSelect;
			}
		});
		tabObj = $("#navtab1").ligerGetTabManager();
	}
	/* 初始化查询表单 */
	function initSearchForm() {
	    $("#search").ligerForm({
			width : "97%",
			fields : [ {
				display : "服务器名称", name : "serverName", newline : true, type : "text", cssClass : "field",
				attr : {
					op : "like",
					field : "server.serverNm"
				}
			}]
	    });
	}
	/* 初始化服务器 GRID */
	function initGrid() {
		grid = $("#maingrid").ligerGrid({
			width : "99%",
			height : "87%",
			columns : [ {
				display : '服务器名称', name : 'serverNm', align : 'center', minWidth : '', width : '',isSort: true
			}, {
				display : '适配器类型', name : 'adapterNm', align : 'center', minWidth : '', width : '',sortname:'adapterId',isSort: true
			}, {
				display : '服务器描述', name : 'serverDesc', align : 'center', minWidth : '', width : '',isSort: true
			} ],
			checkbox : false,
			usePager : true,
			rownumbers : true,
			alternatingRow : true,//附加奇偶行效果行
			colDraggable : true,
			dataAction : 'server',//从后台获取数据
			method : 'post',
			url : "${ctx}/frs/rptmgr/server/list"
	    });
	}
	
	/* 初始化树 */
	function initTree(serverId) {
		showLoading("正在从服务器读取报表，可能需要几分钟，请耐心等待...");
		window['serverTree'] = $.fn.zTree.init($("#tree"),{
			async:{
				enable : true,
				url : "${ctx}/frs/integratedquery/rptmesquery/info/serverReportTree?serverId="+checkServerId,
				dataType : "json",
				type : "get"
			},
			data:{
				key:{
					name:"text"
				}
			},
			view:{
				selectedMulti: false,
				showLine: false
			},
			callback: {
				onNodeCreated: function(){
					BIONE.hideLoading();
				},
				onAsyncError: function(){
					BIONE.hideLoading();
					BIONE.tip("服务器异常，读取失败");
				}
			}
		});
		
		var tabHeight = $("#center").height();
		$("#content1").height(tabHeight -50);
	}
	/*改变底部显示的 按钮*/
	function changeBtn(nowTab) {
		$(".l-dialog-btn").remove();
		var btns = [{ 
			text : '取消', onclick : function() {BIONE.closeDialog("serverBox");}
		}];
		if (nowTab == "report") {
			btns.push({
				text : '选择', onclick : save, icon : 'save', operNo : 'save'
			}, {
				text : '上一步', onclick : toPrev, icon : 'prev', operNo : 'toPrev'
			}, {
				text : '刷新', onclick : flash, icon : 'flash', operNo : 'flash'
			});
		} else {
			btns.push({
				text : '下一步', onclick : toNext, icon : 'next', operNo : 'toNext'
			});
		}
		BIONE.addFormButtons(btns);
	}
	
	/*前一步*/
	toPrev = function (target) {
		canSelect = true;
		tabObj.selectTabItem("server");
		changeBtn("server");
		canSelect = false;
	};
	
	/*后一步*/
	toNext = function (target) {
		var rows = grid.getSelectedRows();
		if (rows.length == 1) {
			checkServerId = rows[0].serverId;
			checkServerName = rows[0].serverNm;
			canSelect = true;
			tabObj.selectTabItem("report");
			initTree();		/* 初始化树 */
			changeBtn("report");
			canSelect = false;
		} else {
			BIONE.tip("请选择一条记录！");
		}
	};
	
	/*刷新树形结构  与 报表服务器上的同步 */
	flash = function(){
		BIONE.ajax({url:"${ctx}/frs/integratedquery/rptmesquery/info/flashServerReport",type:"post", data : {data:checkServerId}},function(){
			serverTree.reAsyncChildNodes(null, "refresh");
			BIONE.tip("刷新成功！");
		});
	};
	
	/*保存*/ 
	save = function () {
		var nodes = serverTree.getSelectedNodes();
		if(nodes && nodes.length ==1 ) {
			if(nodes[0].data.resourceType != "folder"){
				var main = window.parent.selectedTab;
				 var c=main.jQuery.ligerui.get("serverIdBox");
				 c._changeValue(checkServerId, checkServerName);
// 				main.$("#basicform [name='serverSelName']").val(checkServerName);
// 				main.$("#basicform [name='serverId']").val(checkServerId);
				main.$("#basicform [name='rptName']").val(nodes[0].data.desc);
				main.$("#searchPath").val(nodes[0].data.id);
				main.$("#rptSrcPath").val(nodes[0].data.path);
				BIONE.closeDialog("serverBox");
			}else{
				BIONE.tip("请选择报表节点");
			}
		}else{
			BIONE.tip("请选择一个节点");
		}
	};
	
	function showLoading(message) {
		message = message || "正在加载中...";
		$('body').append("<div class='jloading'>" + message + "</div>");
		$(".jloading").css("left",'30%');
		$.ligerui.win.mask();
	};
	
</script>
</head>
<body>
	<div id="template.center">
		<div id="navtab1" style="width: 100%;">
			<div tabid="server" title="服务器选择" lselected="true">
				<div id="mainsearch">
				 	<div class="searchtitle">
						<img src="${ctx}/images/classics/icons/find.png" /> <span>搜索</span>
						<div class="togglebtn">&nbsp;</div>
					</div>
					<div id="searchbox" class="searchbox">
						<form id="formsearch">
							<div id="search"></div>
							<div class="l-clear"></div>
						</form>
						<div id="searchbtn" class="searchbtn"></div>
					</div>
				</div>
				<div id="maingrid" class="maingrid"></div>
			</div>
			<div tabid="report" title="报表选择">
				<div class="content" style="border: 1px 1px 1px solid #D6D6D6;overflow: auto;" id="content1">
					<div id="treeContainer"
						style="width: 100%;">
						<ul id="tree"
							style="font-size: 12; background-color: F1F1F1; width: 90%"
							class="ztree"></ul>
					</div>
				</div>
			</div>
		</div>
	</div>
</body>
</html>