<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<html>
<meta name="decorator" content="/template/template14.jsp">
<head>
<style>
.checklabel{
	color: blue;
	cursor: pointer;
}
</style>
<script type="text/javascript">
	var flag = true;
	var filterInfos = [];
	var grid;
	$(function(){
		generateFilterInfo();
		initdom(flag);
		initGrid();
	});
	
	function generateFilterInfo(){
		filterInfos = [];
		var filterWindow = window.parent.document.getElementById("filter").contentWindow;
		if(filterWindow
				&& filterWindow.maintab
				&& filterWindow.maintab.getTabItemCount() > 0){
			var tabIdList = filterWindow.maintab.getTabidList();
			for(var i = 0 , l = tabIdList.length ; i < l ; i++){
				var tabIdTmp = tabIdList[i];
				var tabContent = filterWindow.document.getElementById(tabIdTmp+"frame").contentWindow;
				if(tabContent
						&& typeof tabContent.generateFilterInfo == "function"){
					var infosTmp = tabContent.generateFilterInfo();
					if(infosTmp){
						filterInfos.push(infosTmp);
					}
				}else if(filterWindow.checkedTypeVals
						&& typeof filterWindow.checkedTypeVals.length != "undefined"
						&& filterWindow.checkedTypeVals[tabIdTmp]){
					filterInfos.push(filterWindow.checkedTypeVals[tabIdTmp]);
				}
			}
		}
		if(filterInfos.length <= 0){
			flag = false;
		}else{
			flag = true;			
		}
	}
	
	function setData(){
		if(grid){
			if(filterInfos 
					&& filterInfos.length > 0){				
				grid.set("data",{
					Rows: filterInfos
				});
				return ;
			}
		}
	}

	function initdom(flag){
		if(flag === true){
			$("#maingrid").show();	
		}else{
			$("#maingrid").hide();
			$("#msg").parent().css("display" , "block");
			var msgLineheight = $("#center").height();
			var msgPadding = ($("#center").width() - $("#msg").width())/2;
			$("#msg").parent().height(msgLineheight);
			$("#msg").parent().css("line-height" , msgLineheight+"px");
			$("#msg").css("padding-left" , msgPadding);
		}
	}
	
	function refreshData(){
		generateFilterInfo();
		initdom(flag)
		setData();
	}
	
	function initGrid(){
		grid = $("#maingrid").ligerGrid({
			width : '99%',
			height: '99%',
			columns : [ {
				display : '维度类型',
				name : 'dimTypeNm',
				width : "20%",
				render : function(rowData){
					return "<a class='checklabel' href='javascript:dimTypeNoClick(\""+rowData.dimTypeNo+"\");' >"+rowData.dimTypeNm+"</a>";
				}
			}, {
				display : '维度过滤信息',
				name : 'filterText',
				width : "70%",
				render: function(a,b,c){
					var text = a.filterText;
					if(!text){
						var vals = a.checkedVal.split(",")
						text = vals.length+" 个值";
					}
					return (a.filterModelVal=="01"?"包含":"不包含") + text;
				}
			}],
			checkbox : false,
			data: null,
			usePager : false,
			isScroll : false,
			rownumbers : true,
			alternatingRow : true,//附加奇偶行效果行
			dataAction : 'loaction',//从后台获取数据
			usePager : false
		});
		$(".l-grid-header").hide();
		grid.setHeight($("#center").height()-10);
		if(flag === false){
			$("#maingrid").hide();
		}else{
			setData();
		}
	}
	
	function dimTypeNoClick(dimTypeNo){
		if(dimTypeNo
				&& window.parent.tabObj){
			window.parent.canSelectTab = true;
			window.parent.tabObj.selectTabItem("filter");
			var content = window.parent.document.getElementById("filter").contentWindow;
			if(content && content.maintab){
				content.maintab.selectTabItem(dimTypeNo);
			}
		}
	}
	
</script>
</head>
<body>
<div id="template.center">
	<div id="maingrid">
	</div>
	<div style="width:100%;display:none;">
		<font id="msg"><b>无过滤信息配置</b></font>
	</div>
</div>
</body>
</html>