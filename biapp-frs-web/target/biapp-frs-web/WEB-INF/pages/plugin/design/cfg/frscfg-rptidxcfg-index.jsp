<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<html>
<head>
<meta name="decorator" content="/template/template22_BS.jsp">
<script type="text/javascript">
	var searchIdxNm = '${searchIdxNm}';
	var indexTreeObj;
	var mainTab;
	var srcIndexNo;
	var srcIndexVerId;
	var srcTreeNode;
	var isFirstCfg = false;
	
	$(function() {
		initTool();
		indexIdxTree(searchIdxNm);
		initTab();
		initStyle();
	});
	
	//初始化样式
	function initStyle(){
        var $content = $(window);
        $("#right").height($content.height() - 10);
        $("#rightDiv").height($("#right").height() - 10);
        $("#left").height($content.height() - 10);
        $("#tab").height($content.height() - 10);
        var rightHeight = $("#right").height();
        var leftHeight = $("#left").height();
        var $treeContainer = $("#treeContainer");
        $treeContainer.height($("#left").height() - 40);
        $('.l-tab-content-item').append('<div id="background" class="l-tab-loading" style="display:block; background:url(${ctx}/images/classics/index/bg_center.jpg) no-repeat center center #ffffff;"></div>');
	}
	
	//初始化监听事件
	function initTool(){
		if(searchIdxNm){
			var regex = searchIdxNm.indexOf("{");
			if(regex != -1){
				var regex2 = /\{(.+?)\}/g;  // {} 大括号
				var idxNmArray = searchIdxNm.match(regex2);
				searchIdxNm = idxNmArray[0];
				searchIdxNm = searchIdxNm.substring(1, searchIdxNm.length - 1);
			}
			if("空指标" != searchIdxNm){
				$("#treeSearchInput").val(searchIdxNm);
			}else{
				searchIdxNm = "";
			}
		}else{//空白单元格，需要创建一个报表指标对象
			isFirstCfg = true;
		}
		//初始化树查询按钮事件
		$("#treeSearchIcon").bind("click",function(e){
			var searchName = $("#treeSearchInput").val();
			indexIdxTree(searchName);
		});
		$("#treeSearchInput").bind("keydown",function(e){
			// 按下回车键进行查询
			var code = e.keyCode;
			if(code == 13){
				var searchName = $("#treeSearchInput").val();
				indexIdxTree(searchName);
			}
		});
	}
	
	//初始化指标树
	function indexIdxTree(searchNm){
		var setting ={
				async:{
					enable:true,
					url:"${ctx}/report/frame/idx/getAsyncTreeIdxShow.json",
					autoParam:["nodeType", "id", "indexVerId"],   //将该三个参数提交,post方式
					otherParam:{'isShowIdx':'1','isShowDim':1, 'isShowMeasure':1,'isPublish':'1', "isAuth" : "1", "searchNm":searchNm},
					dataType:"json",
					dataFilter:function(treeId,parentNode,childNodes){
						if(childNodes){
							for(var i = 0;i<childNodes.length;i++){
								childNodes[i].nodeType = childNodes[i].params.nodeType;
								childNodes[i].indexVerId = childNodes[i].params.indexVerId;
								childNodes[i].defSrc = childNodes[i].params.defSrc ? childNodes[i].params.defSrc : "";
							}
						}
						return childNodes;
					}
				},
				data:{
					key:{
						name:"text"
					}
				},
				view:{
					selectedMulti:false
				},
				callback:{
					onClick : treeOnClick
				}
		};
		indexTreeObj = $.fn.zTree.init($("#tree"), setting);
	}
	
	//树点击事件
	var treeOnClick = function(event, treeId, treeNode){
		if(!treeId || !treeNode){
			return ;
		}
		var clickNodeType = treeNode.params.nodeType;
		var clickTreeId = treeNode.id;
		if(("idxInfo" == clickNodeType && (!treeNode.params.haveMeasure) )|| "measureInfo" == clickNodeType){
			srcIndexNo = clickTreeId;
			srcIndexVerId = treeNode.params.indexVerId;
			srcTreeNode = treeNode;
			initDimData('Y');
		}else{
			initDimData('N');
		}
	}
	
	// 初始化tab
	function initTab(){
		mainTab = $("#tab").ligerTab({
			contextmenu : false
		});
		mainTab.addTabItem({
			tabid : "dimTab",
			text : "维度过滤",
			showClose : false,
			content : "<iframe frameborder='0' id='gridFrame' style='height:95%;width:100%;' src=''></iframe>"
		});
	}
	
	//初始化维度信息页
	function initDimData(isIdxNode){
		if('Y' == isIdxNode){
			$("#gridFrame").attr({
				src : "${ctx}/report/frame/design/cfg/dbclk/module/idx?isRptIdxCfg=Y"
			});
			$("#background").hide();
		}else{
			$("#gridFrame").attr({
				src : ""
			});
			$("#background").show();
		}
	}
	
	//报表报表指标配置
	function saveRptIdx(){
    	var currSheet = window.parent.spread.getActiveSheet();
		var selections = currSheet.getSelections();
		var currSelection = selections[0];
		if(currSelection){
			window.parent.indexNodeDrop(currSelection.row, currSelection.col, srcTreeNode, currSheet);
			selecRptIdx();
		}
	}
	
	//将当前选择指标节点设置为父页面选择指标对象
	function selecRptIdx(){
    	var currSheet = window.parent.spread.getActiveSheet();
		var selections = currSheet.getSelections();
		var currSelection = selections[0];
		if(currSelection){
			var seq = currSheet.getTag(currSelection.row, currSelection.col, window.parent.GC.Spread.Sheets.SheetArea.viewport);
			var rptIdxTmp = window.parent.Design.rptIdxs[seq];
			window.parent.RptIdxInfo.initIdxKO(window.parent.selectionIdx , rptIdxTmp);
			window.parent.selectionIdx.seq(seq);
		}
	}
</script>
</head>
<body>
	<div id="template.left.up.icon">
		<img src="${ctx }/images/classics/icons/application_side_tree.png" />
	</div>
	<div id="template.left.up">
		<span style="font-size: 12">指标库</span>
	</div>
	<div id="template.right">
		<div id="tab" style="width: 100%; overflow: hidden;"></div>
	</div>
</body>
</html>