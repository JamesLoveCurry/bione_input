<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head> 
<%@ include file="/common/meta.jsp"%>
<meta name="decorator" content="/template/template7.jsp">

<script type="text/javascript">
    //var  preview  = '${preview}'; 
    var  isPublish  = '${isPublish}'; 
    var  indexNm  = '${indexNm}'; 
	var  leftTreeObj, items, currentNode, grid, tabObj;
	var  treeRootNo = '${treeRootNo}';  //参数在开始显示主界面时传入
	var  rootIcon = '${rootIcon}';
	$(function(){
		initTree();       //显示树
		tabObj = $("#tab").ligerTab({
			contextmenu : true
		});
		height = $(document).height() - 26;  //显示右侧tab下方的页面序号
		$("#treeSearchbar").hide();
		initCurrentTab("","","","","","");  //显示右侧grid内容
	});

	//初始化tree，只是用于显示树，同右侧的grid没有关系
	function initTree() {
		var url_ =  "${ctx}/rpt/frame/idx/limit/getAsyncTreeIdxShow.json?isShowIdx=1&t="
				+ new Date().getTime();
		if(isPublish){
			url_ =  "${ctx}/rpt/frame/idx/limit/getAsyncTreeIdxShow.json?isShowIdx=1&isPublish=1&t="
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
		
		curTabUri = "${ctx}/rpt/frame/idx/limit/idxLimitInfos?indexCatalogNo="+encodeURI(encodeURI(indexCatalogNo));
		
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
			curTabUri  = "${ctx}/rpt/frame/idx/limit/idxLimitInfos?indexCatalogNo="
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