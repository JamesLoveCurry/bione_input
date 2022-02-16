<%@ page language="java" pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
<head>
<meta name="decorator" content="/template/template5.jsp">
<style>
#left {
	float: left;
	width: 70%;
	border-width: 1px;
	border-style: solid;
	border-color: #D6D6D6;
}
#right {

	width: 29%;
	float: left;

}
#tree {
	background-color: #F1F1F1;
}
</style>
<script type="text/javascript">
	var userTreeObj = null;
	var userTreeObj1 = null;
	var userTreeObj2 = null;
	var grid = null;
	var treeObj = null;
	var treeurl = "";
	function templateshow() {
		$("#right").height($(document).height() - 49);
		$("#left").height($(document).height() - 53);
		$("#treeContainer").height($("#left").height()-88);
    }
    
    $(function() {
    	templateshow();
    	initLayout();
    	//initTree();
    	initGrid();
    	initData();
    	initBtn();
    });
    
 // 初始化页面布局
	var initLayout = function(){
		//初始化树查询按钮事件
		$("#treeSearchIcon").bind("click",function(e){
			loadTree(treeurl,treeObj,{searchNm : $("#treeSearchInput").val()});
		});
		$("#treeSearchInput").bind("keydown",function(e){
			// 按下回车键进行查询
			var code = e.keyCode;
			if(code == null
					|| typeof code == "undefined"
					|| code != "13"){
				return ;
			}
			loadTree(treeurl,treeObj,{searchNm : $("#treeSearchInput").val()});
		});
		$("#treeContainer").height($("#treeContainer").height()+30);
		// 初始化百叶窗
		$("#treeContainer").html('<div id="according"></div>');
		var  htmlArr =  [];
		htmlArr.push('<div title="用户列表"  class="l-scroll"><ul id="tree"   style="font-size: 12; background-color: #FFFFFF; width: 92%" class="ztree"></div>');
		htmlArr.push('<div title="最近分享人"  class="l-scroll"><ul id="tree1"   style="font-size: 12; background-color: #FFFFFF; width: 92%" class="ztree"></div>');
		htmlArr.push('<div title="常用分享人"  class="l-scroll"><ul id="tree2"   style="font-size: 12; background-color: #FFFFFF; width: 92%" class="ztree"></div>');
		$("#according").html(htmlArr.join(''));
		var height_ = $("#left").height()+1;
		$("#treeSearchbar").find("ul").css("margin-left","0px").css("margin-bottom","0px");
		//面板
        $("#according").ligerAccordion({ height: height_ - 60, speed: null });
        $(".l-accordion-content").css("height",height_ - 60);
        $(".l-scroll").css("height",height_ - 135 );
		$(".l-accordion-header-inner").css("color","#183152").css("font-size","11");
		$(".l-accordion-header").click(function(){
		    	var  ul  = $(this).find(".l-accordion-header-inner").parent().next().find("ul");
		    	var  id  = $(ul).attr("id");
		    	if(id == "tree"){
		    		treeObj  = userTreeObj;
		    		treeurl = "${ctx}/rpt/frame/rptmgr/share/userlist";
		    	} 
		    	else if(id == "tree1"){
		    		treeObj  = userTreeObj1;
		    		treeurl = "${ctx}/rpt/frame/rptmgr/share/userllist";
		    	} else if(id == "tree2"){
		    		treeObj = userTreeObj2;
		    		treeurl = "${ctx}/rpt/frame/rptmgr/share/usermlist";
		    	}
	            $(this).find(".l-accordion-header-inner").css("color","blue").parent().siblings(".l-accordion-header").find(".l-accordion-header-inner").css("color","#183152");
		 });
		userTreeObj = initTree("tree","${ctx}/rpt/frame/rptmgr/share/userlist");
		userTreeObj1 = initTree("tree1","${ctx}/rpt/frame/rptmgr/share/userllist");
		userTreeObj2 = initTree("tree2","${ctx}/rpt/frame/rptmgr/share/usermlist");
		treeObj  = userTreeObj;
		treeurl = "${ctx}/rpt/frame/rptmgr/share/userlist";
	}
 
 
    function initTree(treeId,url) {
    	var treeObj = null;
		var setting ={
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
		treeObj = $.fn.zTree.init($("#"+treeId), setting);
		loadTree(url,treeObj);
		return treeObj;
	}
    
    function zTreeOnClick(event, treeId, treeNode){
    	if(!getRow(treeNode.id)){
    		var row	={
    			userId : treeNode.id,
    			userName : treeNode.text
    	    };
    		grid.addRow(row);
    	}
    }
    
    function getRow(userId){
    	var data = grid.getData();
    	for(var i in  data){
    		if(data[i].userId == userId){
    			return true;
    		}
    	}
    	return false;
    }
    function initGrid(){
		grid = $("#maingrid").ligerGrid({
			checkbox : false,
			columns : [ {
				display : '用户名称',
				name : 'userName',
				width : "70%",
				align : 'center',
				isSort : false
			},{
				display : '操作',
				name : 'method',
				width : "20%",
				isSort : false,
				render : function(row,b,c){
					return "<a style='color:blue' onclick='f_delete(\"" + row.__id + "\")'>删除</a>"
		
				}
			} ],
			dataAction : 'local', //从后台获取数据
			usePager : false, //服务器分页
			alternatingRow : true, //附加奇偶行效果行
			colDraggable : true,
			data : {},
			rownumbers : false,
			width:'99%'
		});
		var rightHeight = $("#right").height();
		grid.setHeight(rightHeight - 8);
	}
    
    function initData(){
    	$.ajax({
			cache : false,
			async : true,
			url : "${ctx}/rpt/frame/idx/compgrp/getCompGrpIndex.json?d="
					+ new Date().getTime(),
			dataType : 'json',
			data : {
				indexNo : "${indexNo}"
			},
			type : "post",
			success : function(result) {
				grid.addRows(result.Rows);
			}
		});
    }
    
    function initBtn(){
    	var buttons = [];
    	buttons.push({
    	    text : '取消',
    	    onclick : function() {
    		BIONE.closeDialog("userinfoEdit");
    	    }
    	});
    	buttons.push({
    	    text : '保存',
    	    onclick : f_save
    	});

    	BIONE.addFormButtons(buttons);
    }
    
    function f_save(){
    	var data = grid.getData();
    	var p = window.parent.panel;
    	p.adds(data,false);
    	BIONE.closeDialog("userinfoEdit","配置成功",3000);
    }
    
    function f_delete(rowId){
    	grid.deleteRow(rowId);
    }
    
  	//加载树中数据
	function loadTree(url,component,data){
		$.ajax({
			cache : false,
			async : true,
			url : url,
			dataType : 'json',
			data:data,
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
					component.addNodes(null,result,false);
					component.expandAll(true);	
				}
			},
			error : function(result, b) {
				//BIONE.tip('发现系统错误 <BR>错误码：' + result.status);
			}
		});
	}
    
</script>
</head>
<body>
	<div id="template.center">
		<div id="left" style="background-color: #FFFFFF">
			<div id="lefttable" width="100%" border="0">
				<div width="100%"
					style="height:30px;background-image: url(${ctx}/css/classics/ligerUI/Gray/images/ui/gridbar.jpg);border-bottom:1px solid #D6D6D6;">
					<div width="8%"
						style="padding-left: 10px; float: left; position: relative; height: 20p; margin-top: 8px">
						<img src="${ctx }/images/classics/icons/application_side_tree.png" />
					</div>
					<div width="90%">
						<span
							style="font-size: 12; float: left; position: relative; line-height: 30px; padding-left: 2px">
							用户信息
						</span>
					</div>
				</div>
			</div>
			<div id="treeSearchbar" style="width:99%;margin-top:2px;padding-left:2px;">
				<ul>
					<li style="width:98%;text-align:left;">                      
						<div class="l-text-wrapper" style="width: 100%;">                         
							<div class="l-text l-text-date" style="width: 100%;">                    
								<input id="treeSearchInput" type="text" class="l-text-field"  style="width: 100%;" />    
								<div class="l-trigger">                                                                      
									<div id="treeSearchIcon" style="width:100%;height:100%;background:url(${ctx}/images/classics/icons/find.png) no-repeat 50% 50% transparent;"></div>                                                 
								</div>
							</div>                                                                                                   
						</div>
					</li>
				</ul>                                                                                                         
			</div>          
			<div id="treeContainer"
				style="width: 100%; overflow: auto; clear: both; background-color: #FFFFFF;">
				<ul id="tree"
					style="font-size: 12; background-color: #FFFFFF; width: 92%"
					class="ztree"></ul>
			</div>
		</div>
		<div id="right">
			<div class="content">
				<div id="maingrid" class="maingrid"></div>
			</div>
		</div>
	</div>
</body>
</html>