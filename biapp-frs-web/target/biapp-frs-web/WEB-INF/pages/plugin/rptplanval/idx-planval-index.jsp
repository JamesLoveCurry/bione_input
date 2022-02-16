<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head> 
<%@ include file="/common/meta.jsp"%>
<meta name="decorator" content="/template/template7.jsp">

<script type="text/javascript">
   // var  preview  = '${preview}'; 
    var  isPublish  = '${isPublish}'; 
    var  indexNm  = '${indexNm}'; 
	var  leftTreeObj, items, currentNode, grid, tabObj;
	var  treeRootNo = '${treeRootNo}';  //参数在开始显示主界面时传入
	var  rootIcon = '${rootIcon}';
	var  searchObj = {exeFunction : "initTree",searchType : "idx"};//默认执行initTree方法
	var  refreshObj;
	$(function(){
		$("#treeToolbar").ligerTreeToolBar({
			items : [{
 				icon:'export',
 				text:'数据处理',
 				menu:{
 					width:90,
 					items:[{
 							icon:'export',
 							text:'导出数据',
 							click : idxPlanvalExport
 						},{
 							line:true
 						},{
 							icon:'import',
 							text:'导入数据',
 							click : idxPlanvalImport
 					}]
 				}
 			}]
		});
		initTree();       //显示树
		initExport();
		tabObj = $("#tab").ligerTab({
			contextmenu : true
		});
		height = $(document).height() - 26;  //显示右侧tab下方的页面序号
		$("#treeSearchbar").hide();
		initCurrentTab("","","","","","");  //显示右侧grid内容
		
		//增加一个搜索区域
		var searchArea = '<div id="treeSearchbar" style="width:99%;margin-top:2px;padding-left:2px;">' +
						'<ul><li style="width:98%;text-align:left;"><div class="l-text-wrapper" style="width: 100%;">' +
						'<div class="l-text l-text-date" style="width: 100%;"><input id="treeSearchInput" type="text" class="l-text-field"  style="width: 100%;" />' +
						'<div class="l-trigger"><div id="treeSearchIcon" style="width:100%;height:100%;background:url(${ctx}/images/classics/icons/find.png) no-repeat 50% 50% transparent;"></div>' +
						'</div></div></div></li></ul></div>'
		$("#treeToolbar").after(searchArea);
		//添加高级搜索按钮
		$(".l-trigger").css("right","26px");
		var innerHtml = '<i class="l-trigger" style="right:20px;width:1px;height:12px;border-left:1px dotted gray;margin-top:4px;"></i>'+
			'<div title="高级筛选" class="l-trigger" style="right:0px;"><div id="highSearchIcon" style="width:100%;height:100%;background:url(${ctx}/images/classics/icons/application_form.png) no-repeat 50% 50% transparent;"></div></div>';
		$(".l-trigger").after(innerHtml);
		//添加高级搜索弹框
		$("#highSearchIcon").click(function(){
			BIONE.commonOpenDialog("高级搜索","highSearch","600","250","${ctx}/report/frame/idx/highSearch?searchObj="+JSON2.stringify(searchObj));
		});
		
		initTreeSearch();//普通搜索
	});

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
	
	//初始化tree，只是用于显示树，同右侧的grid没有关系
	function initTree(searchNm,searchObj) {
		if(searchObj == undefined){
		var url_ =  "${ctx}/report/frame/idx/getAsyncTreeIdxShow.json?isShowIdx=1&isShowMeasure=0&showEmptyFolder=1&isAuth=1&t="
				+ new Date().getTime();
		if(isPublish){
			url_ =  "${ctx}/report/frame/idx/getAsyncTreeIdxShow.json?isShowIdx=1&isShowMeasure=0&isPublish=1&isPreview=1&isAuth=1&t="
				+ new Date().getTime();
		}
		var setting ={
 		   async:{
				enable:true,
				url:url_,
				autoParam:["nodeType", "id", "indexVerId"],   //将该三个参数提交,post方式
				dataType:"json",
				dataFilter:function(treeId, parentNode, childNodes){  //对 Ajax返回数据进行预处理
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
					name:"text"
				}
			},
			view:{
				selectedMulti:false
			},
			callback:{
				onClick : zTreeOnClick
			}
		};
		
		leftTreeObj = $.fn.zTree.init($("#tree"), setting, [ {
			id: '0',
			text: '全部',
			params: {"nodeType" : 'idxCatalog'},
			data: {"indexCatalogNo" : '0'},
			open: true,
			icon: '${ctx}'+rootIcon,
			isParent: true
		} ]);
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
		
		if ($.browser.msie && parseInt($.browser.version, 10) == 7) {
			// ie7 divs position:relative bug
			$(".l-treemenubar-item").css("z-index" , '999');
		}	
	}
	//模板导入
	function idxPlanvalImport(){
		BIONE.commonOpenDialog("指标计划值导入", "importWin", 600, 480,
				"${ctx}/report/frame/wizard?type=Idxplanval");
		
	}
       //模板导出
	function idxPlanvalExport(){
		BIONE.commonOpenDialog("指标计划值导出", "exportWin", 400, 480,
		"${ctx}/report/frame/wizard/exportWin?type=Idxplanval");
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
	
	//导入后刷新
	function reload(){
		refreshObj.reloadGrid();
	}
	
	//指标节点单击事件
	function zTreeOnClick(event, treeId, treeNode, clickFlag) {
		currentNode = treeNode;
		var catalogId = currentNode.data.indexCatalogNo;
		var type = currentNode.params.nodeType;
 		if(type=="idxInfo"){
            //节点为指标类型	
        	initCurrentTab(type, catalogId, currentNode.data.id.indexNo, currentNode.data.id.indexVerId, currentNode.data.indexNm, currentNode.data.infoRights);
		}else{
			if(clickFlag == "1") {
				if(treeRootNo == currentNode.id) {  //当前的节点id为根节点0
					initCurrentTab(type,"","","","",""); 
				} 
			}
		} 
	} 	
	
	//tab展示，显示右侧的grid内容
	function initCurrentTab(type, indexCatalogNo, indexNo, indexVerId, name, infoRights){
		
		curTabUri = "${ctx}/rpt/frame/idx/planval/idxPlanvalInfos?indexCatalogNo="+encodeURI(encodeURI(indexCatalogNo));
		
		tabTitleName = "指标";
		if(type != "idxInfo"){
			if(type != "" && indexNo){
			   tabTitleName ="指标目录["+name+"]: "+tabTitleName;
		    } 
		}else{
			tabTitleName ="指标["+name+"]";
		}
		if(type != "idxInfo"){  //用于显示目录下的指标
			tabObj.removeTabItem("rptIdxCenterTab");
			tabObj.addTabItem({
				tabid : "rptIdxCenterTab",
				text : tabTitleName,
				showClose : false,
				content : '<iframe frameborder="0" id="rptIdxCenterTabFrame" name="rptIdxCenterTabFrame" style="height:'+height+'px;" src='+curTabUri+' ></iframe>'
			});
			tabObj.selectTabItem("rptIdxCenterTab");
		}else{   //只显示具体指标		
			curTabUri  = "${ctx}/rpt/frame/idx/planval/idxPlanvalInfos?indexCatalogNo="
					+encodeURI(encodeURI(indexCatalogNo))+"&indexNo="+indexNo+"&indexVerId="+indexVerId;

			tabObj.removeTabItem("rptIdxCenterTab");
			tabObj.addTabItem({
				tabid : "rptIdxCenterTab",
				text : tabTitleName,
				showClose : false,
				content : '<iframe frameborder="0" id="rptIdxCenterTabFrame" name="rptIdxCenterTabFrame" style="height:'+height+'px;" src='+curTabUri+' ></iframe>'
			});
			tabObj.selectTabItem("rptIdxCenterTab");
		} 
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