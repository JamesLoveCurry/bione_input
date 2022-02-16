<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<html>
<meta name="decorator" content="/template/template14.jsp">
<head>
<script type="text/javascript">
	var reportManage=window.parent;
	var treeObj=null;
	function templateshow() {
		$("#center").height($(document).height() - 8);
		var centerHeight = $("#center").height();
		$("#treeContainer").height(
				centerHeight - $("#mainsearch").height()
						 - 8);
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
			}
		};
		
		leftTreeObj = $.fn.zTree.init($("#tree"), setting);
		//加载数据
		loadTree("${ctx}/frs/integratedquery/rptmesquery/info/getAuthRoleTree?rptId=${rptId}",leftTreeObj);
	}
	
	function loadTree(url, component, data) {
		$.ajax({
			cache : false,
			async : true,
			url : url,
			dataType : 'json',
			data : data,
			type : "POST",
			beforeSend : function() {
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
	function addSearchButtons(form, btnContainer) {
		if (!form)
			return;
		form = $(form);
		if (btnContainer) {
			BIONE.createButton({
				appendTo : btnContainer,
				text : '搜索',
				icon : 'search3',
				width : '50px',
				click : function() {
					loadTree("${ctx}/frs/integratedquery/rptmesquery/info/getAuthRoleTree?rptId=${rptId}",leftTreeObj,{roleNm: $("#roleName").val()});
				}
			});
			BIONE.createButton({
				appendTo : btnContainer,
				text : '重置',
				icon : 'refresh2',
				width : '50px',
				click : function() {
					$("#roleName").val("");
				}
			});
		}
	};
	function templateinit() {
		var height = $(document).height();
		$(".l-dialog-btn").live('mouseover', function() {
			$(this).addClass("l-dialog-btn-over");
		}).live('mouseout', function() {
			$(this).removeClass("l-dialog-btn-over");
		});
		$(".l-dialog-tc .l-dialog-close").live('mouseover', function() {
			$(this).addClass("l-dialog-close-over");
		}).live('mouseout', function() {
			$(this).removeClass("l-dialog-close-over");
		});
		$(".searchtitle .togglebtn").live('click',
				function() {
					var searchbox = $(this).parent().nextAll(
							"div.searchbox:first");
					var centerHeight = $("#center").height();
					if ($(this).hasClass("togglebtn-down")) {
						$(this).removeClass("togglebtn-down");
						searchbox.slideToggle('fast', function() {
							$("#treeContainer").height(
									centerHeight - $("#mainsearch").height()
											 - 8);
						});
					} else {
						$(this).addClass("togglebtn-down");
						searchbox.slideToggle('fast', function() {
							$("#treeContainer").height(centerHeight  - 30);
						});
					}	
				}
		);
	}
	
	function searchForm() {
		$("#search").ligerForm({
			fields : [ {
				display : "角色名称",
				name : "roleName",
				newline : true,
				type : "text",
				cssClass : "field"
			} ]
		});
		addSearchButtons("#search", "#searchbtn");
	}

	$(function() {
		window.parent.reportManage.reportAuthority = window;
		searchForm();
		templateshow();
		templateinit();
		initTree();
	})
</script>
</head>
<body>
	<div id="template.center">
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
		<div class="content" style="border: 1px solid #D6D6D6;">
			<div id="treeContainer"
				style="width: 100%; overflow: auto; clear: both; background-color: #FFFFFF;">
				<ul id="tree"
					style="font-size: 12; background-color: F1F1F1; width: 90%"
					class="ztree"></ul>
			</div>
		</div>
	</div>
</body>
</html>