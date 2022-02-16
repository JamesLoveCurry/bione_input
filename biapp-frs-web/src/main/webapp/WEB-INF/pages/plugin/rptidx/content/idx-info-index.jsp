<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head> 
<%@ include file="/common/meta.jsp"%>
<meta name="decorator" content="/template/template7_BS.jsp">
<script type="text/javascript">
    var  preview  ='${preview}'; 
    var  isPublish  ='${isPublish}'; 
    var  indexNm  ='${indexNm}'; 
	var leftTreeObj, items, currentNode,grid,tabObj;
	var  treeRootNo ='${treeRootNo}';
	var  rootIcon ='${rootIcon}';
	var searchObj = {exeFunction : "initTree",searchType : "idx"};//默认执行initTree方法
	$(function(){
		if(!preview){
			$("#treeToolbar").ligerTreeToolBar({
				items:[{
					icon:'menu',
					text:'目录',
					operNo : 'idx-info-menu',
					color :'#fcca54',
					menu:{
						width : 90,
						items : [{
							icon : 'add',
							text : '新建',
							click : addIdxCatalog
						},{
							icon : 'delete',
							text : '删除',
							click : deleteIdxCatalog
						},{
							icon : 'xg-01',
							text : '修改',
							click : updateIdxCatalog,
							color :'#fcca54'
						},{
							line : true
						},{
 							icon:'up',
 							text:'上移',
 							click : idx_up
 						},{
 							icon:'down',
 							text:'下移',
 							click : idx_down
 						}]
					}
				},{
					icon:'fontSize',
	 				text:'数据',
	 				operNo : 'idx-info-menu-impexp',
					menu:{
						width:90,
						items:[{
							icon:'database',
							text:'模板导出',
							click : idxExport
						},{
							icon : 'screening',
							text : '模板导入',
							click : idxImport
						}]
					}
				}]
				
			}); 
		}
		initTree();
		initExport();
		tabObj = $("#tab").ligerTab({
			contextmenu : true
		});
        // var $centerDom = $(document);
		var $centerDom = $(window.parent.document);
		height = $centerDom.height() - 150;
		$("#treeSearchbar").hide();
		initCurrentTab("","","","","","");
		
		//增加一个搜索区域
		var searchArea = '<div id="treeSearchbar" style="width:99%;margin-top:2px;padding-left:2px;">' +
						'<ul><li style="width:98%;text-align:left;"><div class="l-text-wrapper" style="width: 100%;">' +
						'<div class="l-text l-text-date" style="width: 100%;"><input id="treeSearchInput" type="text" class="l-text-field"  style="width: 75%;" />' +
						'<div class="l-trigger"><a id="treeSearchIcon" class = "icon-search font-size"></a>' +
						'</div></div></div></li></ul></div>'
		$("#treeToolbar").after(searchArea);
		//添加高级搜索按钮
		$(".l-trigger").css("right","26px");
		var innerHtml = '<i class="l-trigger" style="right:20px;width:1px;height:12px;border-left:1px dotted gray;margin-top:2px;"></i>'+
			'<div title="高级筛选" class="l-trigger" style="right:0px;"><a id="highSearchIcon" class = "icon-light_off font-size"></a></div>';
		$(".l-trigger").after(innerHtml);
		//添加高级搜索弹框
		$("#highSearchIcon").click(function(){
			BIONE.commonOpenDialog("高级搜索","highSearch","600","250","${ctx}/rpt/frame/rptSearch/highSearch?searchObj="+JSON2.stringify(searchObj));
		});
		
		initTreeSearch()//普通搜索
	});
	function idx_up(){
		var moveFlag = "";
		if($("#treeSearchInput").val()!= ""||searchObj.obj != undefined){
			BIONE.tip("筛选状态下不可移动");
			return;
		}
		if(leftTreeObj == null
				|| typeof leftTreeObj == "undefined"){
			return ;
		}
		var selNodes = leftTreeObj.getSelectedNodes();
		if(selNodes.length && selNodes.length > 0 && "idxCatalog" == selNodes[0].params.nodeType){	
			if(selNodes[0].isFirstNode){
				BIONE.tip("已到顶，无法上移!");
				return;
			}
			leftTreeObj.moveNode(selNodes[0].getPreNode(), selNodes[0], "prev");
			var treeNodes = selNodes[0].getParentNode();
			var selIds ="";
			var rankOrders ="";
			for(var i=0;i<treeNodes.children.length;i++){
				selIds += treeNodes.children[i].id+",";
				rankOrders += i+",";
			}
			if("idxCatalog" == selNodes[0].params.nodeType){
				moveFlag = "catalogNode";
			}
			commonMove(moveFlag,selIds,rankOrders);
		} else {
			BIONE.tip("请选择要移动的目录节点");
		}
	}
    function idx_down(){
    	var moveFlag = "";
    	if($("#treeSearchInput").val()!= ""||searchObj.obj != undefined){
			BIONE.tip("筛选状态下不可移动");
			return;
		}
		if(leftTreeObj == null
				|| typeof leftTreeObj == "undefined"){
			return ;
		}
		var selNodes = leftTreeObj.getSelectedNodes();
		if(selNodes.length && selNodes.length > 0 && "idxCatalog" == selNodes[0].params.nodeType){	
			if(selNodes[0].isLastNode){
				BIONE.tip("已到底，无法下移!");
				return;
			}
			leftTreeObj.moveNode(selNodes[0].getNextNode(), selNodes[0], "next");
			var treeNodes = selNodes[0].getParentNode();
			var selIds ="";
			var rankOrders ="";
			for(var i=0;i<treeNodes.children.length;i++){
				selIds += treeNodes.children[i].id+",";
				rankOrders += i+",";
			}
			if("idxCatalog" == selNodes[0].params.nodeType){
				moveFlag = "catalogNode";
			}
			commonMove(moveFlag,selIds,rankOrders);
		} else {
			BIONE.tip("请选择要移动的目录节点");
		}
	}
    function commonMove(moveFlag,selIds,rankOrders){
    	$.ajax({
			cache : false,
			async : false,
			url : "${ctx}/report/frame/idx/idxMove",
			dataType : 'json',
			data : {
				moveFlag : moveFlag,
				selIds :  selIds,
				rankOrders :  rankOrders
			},
			type : "POST",
			success : function(result){
				if(result != null
						&& typeof result != "undefined"){
					var flag = result.msg;
					if(flag == "ok" ){
						// 刷新树
					//	searchHandler();
						//leftTreeObj.reAsyncChildNodes(window.currentNode.getParentNode(), "refresh");	
						// 刷新tab
						//setDefaultTab();
					}else{
						if(result.msg
								&& typeof result.msg != "undefined"){
							BIONE.tip(result.msg);
						}else{								
							BIONE.tip("删除失败，请联系管理员");
						}
					}
				}
			},
			error:function(){
				BIONE.tip("删除失败，请联系系统管理员");
			}
		});
    }
	function initTreeSearch(){
		$("#treeSearchIcon").live('click',function(){// 树搜索
			initTree($('#treeSearchInput').val(),"");
		});
		$('#treeSearchInput').bind('keydown', function(event) {
			if (event.keyCode == 13) {
				initTree($('#treeSearchInput').val(),"");
			}
		});
	}
	
	var exportExcel=function(fileName,type){
		var src='';
		src='${ctx}/report/frame/wizard/export?type='+type+'&fileName='+fileName;
		downdload.attr('src', src);
	};
	
	var exportTmp=function(fileName){
		var src = '';
		src = "${ctx}/report/frame/idx/exportTmpInfo?filepath="+fileName+"&d="+ new Date().getTime();
		downdload.attr('src', src);
	};
	
	var initExport=function() {
		window["downdload"] = $('<iframe id="download"  style="display: none;"/>');
		$('body').append(downdload);
	};
	
	function  addIdxCatalog(){
		var id;
		if (!currentNode) {
			/* BIONE.tip("请选择父目录");
			return; */
			id = treeRootNo;
		} else if (currentNode.id == treeRootNo) {
			id = treeRootNo;
		} else {
			var  type  =   currentNode.params.nodeType;
			if(type=="idxInfo"){
				return;
			}
			id = currentNode.data.indexCatalogNo;
		}
		var modelDialogUri = "${ctx}/report/frame/idx/newCatalog?indexCatalogNo=" + id;
		BIONE.commonOpenDialog("指标目录添加", "rptIdxCatalogAddWin","660","320",modelDialogUri);		 
	}
    function  deleteIdxCatalog(){
    	if (!currentNode) {
			BIONE.tip("请选择目录");
			return;
		}
		var  type  =   currentNode.params.nodeType;
		if(type=="idxInfo"){
			return;
		}
		var id = currentNode.data.indexCatalogNo;
		if(id==treeRootNo){
			return;
		}		
		$.ajax({
			type : "POST",
			url : '${ctx}/report/frame/idx/isHasIdx.json?indexCatalogNo=' + id,
			success : function(result) {
				  if(result=="1"){
					  BIONE.tip("该指标目录下面包含指标，不能进行删除操作！");
					  return;
				  }else if(result=="0"){
					  $.ligerDialog.confirm('将执行级联删除操作，是否继续', '指标目录删除', function(yes) {
							if (yes) {
								if (id != null) {
									BIONE.ajax({
										type : 'POST',
										url : '${ctx}/report/frame/idx/idxCatalog/' + id
									}, function(result) {
										var returnStr = result.msg;
										if (returnStr == "0") {
											leftTreeObj.reAsyncChildNodes(currentNode
													.getParentNode(), "refresh");
			                                initCurrentTab();
											currentNode = null;
											BIONE.tip("删除成功");
										}else {
											BIONE.tip("删除失败");
										} 
									});
								}
							}
						});
				  }
			}
		});
	}
    function  updateIdxCatalog(){
    	var id;
		if (!currentNode) {
			BIONE.tip("请选择目录");
			return;
		} else if (currentNode.id == treeRootNo) {
			return;
		} else {
			var  type  =   currentNode.params.nodeType;
			if(type=="idxInfo"){
				return;
			}
			id = currentNode.data.indexCatalogNo;
		}
		var modelDialogUri = "${ctx}/report/frame/idx/initUpdateCatalog?indexCatalogNo=" + id;
		BIONE.commonOpenDialog("指标目录修改", "rptIdxCatalogUpdateWin","660","320",modelDialogUri);
    }
    
    
    var importReport = function (){
		BIONE.commonOpenDialog("导入数据","importIdx",600,380,"${ctx}/report/frame/idx/impIdx",null,
			function() {
				if(window.pathname!=null&&window.pathname!=""){
					$.ajax({
						cache : false,
						async : false,
						url : "${ctx}/report/frame/idx/deleteFile",
						dataType : 'json',
						data : {
							pathname: window.pathname
						},
						type : "POST"
					});
				}
			});
	};
	
	var exportReport = function(){			
		commonOpenSmallDialog("导出数据","exportIdxs","${ctx}/report/frame/idx/exportIdx");
	};
	
	//模板导入
	function idxImport(){
		BIONE.commonOpenDialog("指标信息模板导入", "importWin", 600, 480,
				"${ctx}/report/frame/wizard?type=Index");
		
	}
	
	//模板导出
	function idxExport(){
		BIONE.commonOpenDialog("指标信息模板导出", "exportWin", 600, 480,
		"${ctx}/report/frame/wizard/exportWin?type=Index");
	}
	
	function commonOpenSmallDialog(title, name,
			url, beforeClose) {
		var width = 450;
		var height = 600;
		var _dialog = $.ligerui.get(name);
		if (_dialog) {
			$.ligerui.remove(name);
		}
		_dialog = $.ligerDialog.open({
			height : height,
			width : width,
			url : url,
			name : name,
			id : name,
			title : title,
			isResize : false,
			isDrag : false,
			isHidden : false
		});
		if (beforeClose != null && typeof beforeClose == "function") {
			_dialog.beforeClose = beforeClose;
		}
		return _dialog;
	};
	
	function searchHandler(){
		//供子页面使用
		initTree();
		//leftTreeObj.reAsyncChildNodes(null,"refresh");
	}
	
	function initTree(searchNm,searchObj) {
		if(searchObj == undefined || searchNm == ''){
			var  url_ =  "${ctx}/report/frame/idx/getAsyncTreeIdxShow.json?isShowIdx=1&isShowMeasure=0&showEmptyFolder=1&isAuth=1&t="
				+ new Date().getTime();
			if(isPublish){
				url_ =  "${ctx}/report/frame/idx/getAsyncTreeIdxShow.json?isShowIdx=1&isShowMeasure=0&isPublish=1&isPreview=1&isAuth=1&t="
					+ new Date().getTime();
			}
			if("${preview}"!=""){
				url_ =  "${ctx}/report/frame/idx/getAsyncTreeIdxShow.json?isShowIdx=1&isShowMeasure=0&isPublish=1&isAuth=1&isPreview=1&t="
					+ new Date().getTime();
			}
			var setting ={
					async:{
						enable:true,
						url:url_,
						autoParam:["nodeType", "id", "indexVerId"],
						dataType:"json",
						dataFilter:function(treeId,parentNode,childNodes){
							if(childNodes){
								for(var i = 0;i<childNodes.length;i++){
									childNodes[i].nodeType = childNodes[i].params.nodeType;
									childNodes[i].indexVerId = childNodes[i].params.indexVerId;
								}
							}
							return childNodes;
						}
					},
					data:{
						key:{
							name:"text",
							title:"title"
						}
					},
					view:{
						selectedMulti:false
					},
					callback:{
						onClick : zTreeOnClick
					}
					

			};
			leftTreeObj = $.fn.zTree.init($("#tree"), setting,[ {
				id:  '0',
				text:  '全部',
				title: '全部',
				params:{"nodeType":'idxCatalog'},
				data:{"indexCatalogNo":'0'},
				open: true,
				icon: '${ctx}'+rootIcon,
				isParent:true
			}]);
		}else{
			var _url = "${ctx}/report/frame/idx/getSyncTree";
			var data = {'searchNm':searchNm ,'isShowIdx':'1','isShowDim':'0', 'isShowMeasure':'0','isPublish':'1', "isAuth" : "0", "showEmptyFolder":""};
			if(searchObj != null && searchObj != ""){
				_url = "${ctx}/report/frame/idx/getSyncTreePro";
				data = {'searchObj':JSON2.stringify(searchObj) ,'isShowIdx':'1','isShowDim':'0', 'isShowMeasure':'0','isPublish':'1', "isAuth" : "0", "showEmptyFolder":""};
			}
			setting ={
					data : {
						keep : {
							parent : true
						},
						key : {
							name : "text"
						},
						simpleData : {
							enable : true,
							idKey : "id",
							pIdKey : "upId",
							rootPId : null
						}
					},
					view:{
						selectedMulti:false
					},
					callback:{
						onClick : zTreeOnClick
					}
			};
			leftTreeObj = $.fn.zTree.init($("#tree"),setting,[]);
			BIONE.loadTree(_url,leftTreeObj,data,function(childNodes){
				if(childNodes){
					for(var i = 0;i<childNodes.length;i++){
						childNodes[i].nodeType = childNodes[i].params.nodeType;
						childNodes[i].indexVerId = childNodes[i].params.indexVerId;
					}
				}
				return childNodes;
			},false);
		}
	}
	
	//树点击事件
	function zTreeOnClick(event, treeId, treeNode, clickFlag) {
		currentNode = treeNode;
		var catalogId = currentNode.data.indexCatalogNo;
		var  type = currentNode.params.nodeType;
		if(type=="idxInfo"){
//          	自我展示	
        	initCurrentTab("idxInfo",currentNode.data.id.indexNo,currentNode.data.indexNm,currentNode.data.id.indexVerId,currentNode.data.indexCatalogNo,currentNode.data.infoRights);
		}else{
			if(clickFlag == "1") {
				if(treeRootNo == currentNode.id) {
					initCurrentTab("idxInfoList","","","","","");
				} else {
					initCurrentTab("idxInfoList",catalogId,currentNode.data.indexCatalogNm,"","","");
                }
			}
		}
		
	} 	
	function  initCurrentTab(type,id,name,versionId,indexCatalogNo,infoRights){
		if(preview){
		     curTabUri = "${ctx}/report/frame/idx/idxInfos?indexCatalogNo="+encodeURI(encodeURI(id))+"&preview="+preview+"&indexNm="+encodeURI(encodeURI(indexNm));
		}else{
		  curTabUri = "${ctx}/report/frame/idx/idxInfos?indexCatalogNo="+encodeURI(encodeURI(id));
		}
		tabTitleName = "指标"; 
		if(type!="idxInfo"){
			if(type!=""&&id){
			   tabTitleName ="指标目录["+name+"]: "+tabTitleName;
		    }  
		}else{
			tabTitleName ="指标["+name+"]";
		}
		if(type!="idxInfo"){
			tabObj.removeTabItem("rptIdxCenterTab");
			tabObj
			.addTabItem({
				tabid : "rptIdxCenterTab",
				text:tabTitleName,
				showClose : false,
				content : '<iframe frameborder="0" id="rptIdxCenterTabFrame" name="rptIdxCenterTabFrame" style="height:'+height+'px;" src='+curTabUri+' ></iframe>'
			});
			tabObj.selectTabItem("rptIdxCenterTab");
		}else{
			if(preview){
				curTabUri  = "${ctx}/report/frame/idx/"+id+"/show?d="+new Date().getTime();
				dialog =  BIONE.commonOpenDialog("指标查看",
							"rptIdxInfoPreviewBox",$(document).width()-50, $(document).height()-50,
							curTabUri, null);
			} else {
				curTabUri = "${ctx}/report/frame/idx/" + id + "/edit?indexVerId=" + versionId + "&indexCatalogNo="+encodeURI(encodeURI(indexCatalogNo));
    			dialog = BIONE.commonOpenDialog("修改指标",
					"idxEdit",$(document).width(), $(document).height(),curTabUri, null);
			}
		}   
	}
	
	function reload(){
		searchHandler();
	}
	
	
	
</script>

<title>指标信息</title>
</head>
<body>
	<div id="template.left.up.icon">
		<img src="${ctx}/images/classics/icons/application_side_tree.png" />
	</div>
	<div id="template.left.up">
		<span style="font-size: 12" id="left_up_span">指标目录</span>
	</div>
	<div id="template.left.down">
		<div id="btn"></div>
	</div>
	<div id="template.right">
	    <div id="tab" style="width: 100%; overflow: hidden;"></div>
	</div>
</body>
</html>