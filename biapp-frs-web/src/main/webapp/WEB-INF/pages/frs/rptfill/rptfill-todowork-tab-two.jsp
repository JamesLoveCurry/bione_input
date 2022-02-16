<%@ page language="java" pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<html>
<meta name="decorator" content="/template/template14.jsp">
<head>
<style type="text/css">
.l-tab-links li.l-selected{background:url(${ctx}/css/classics/ligerUI/Gray/images/layout/tabs-item-selected-light.gif);}
.l-selected .l-tab-links-item-left{ background: url(${ctx}/css/classics/ligerUI/Gray/images/layout/tabs-item-left-selected-light.gif);}
.l-selected .l-tab-links-item-right{background:url(${ctx}/css/classics/ligerUI/Gray/images/layout/tabs-item-right-selected-light.gif) no-repeat left top}
</style>
<script type="text/javascript">
	var doFlag="";
	var tmp = {
		tabHeight: 0,
		initTabItem : new Array(),
		initOverTabItem : new Array(),
		urlTmp : ['${ctx}/rpt/frs/rptfill/showTab?path=rptfill-todowork-mychildworktree&parentFlag=${parentFlag}','${ctx}/rpt/frs/rptfill/showTab?path=rpt-todowork-child-list&orgTypes=02,03,01&parentFlag=${parentFlag}'],
		tabTmp : ['tsktab','listtab']  //与url顺序对应
	}
	tmp.init =  function(){
		this.tabObj =$("#tab").ligerTab({
			changeHeightOnResize : true,
			contextmenu : false,
			onBeforeAddTabItem   : function(tabObj) {
				tmp.initTabItem.push(tabObj.tabid);
			},
			onBeforeSelectTabItem : function(tabId){
				var pos = $.inArray(tabId,tmp.initTabItem) ;
				if(pos > -1){
					tmp.initTabItem.splice(pos,1);
					return false;
				}
				if($.inArray(tabId,tmp.initOverTabItem) > -1){
					return ;
				}else{
// 					BIONE.showLoading();
				}
				
			},
			onAfterSelectTabItem : function(tabId) {
				if($.inArray(tabId,tmp.initOverTabItem) > -1){
					return ;
				}else{
					tmp.loadFrame(tabId);			
				}
			}
		  
	});
	};
	tmp.loadFrame = function(tabId){
		var pos = $.inArray(tabId,this.tabTmp) ;
		if(pos < 0){
			BIONE.tip("tab配置有误");
			return;
		}
		var url;
		if(pos == 1){
			url = this.urlTmp[pos] + "&d=" + new Date().getTime();
		}else{
			url = this.urlTmp[pos] + "&d=" + new Date().getTime();
		}
		var frame = $('<iframe/>');
		frame.attr({
				id : tabId,
				frameborder : 0,
				src : url
		}).css({
			 overflow : "hidden",
			 height : tmp.tabHeight
		});
		$('div[tabId=' + tabId + ']').html(frame);
		this.initOverTabItem.push(tabId);
	
	}
	tmp.addTabItem = function(tabObj,tabId, tabText){
		tabObj.addTabItem({//添加标签tab页
			tabid : tabId,
			text : tabText,
			showClose : false,
			content : "<div id='" + tabId + "' style='height:" + tmp.tabHeight
					+ "px;width:100%;overflow: hidden;'></div>"
		});
	};
	tmp.changeTab = function(tabObj,tabId){
		tabObj.selectTabItem(tabId);
		if($.inArray(tabId,this.initOverTabItem) > -1){
			return ;
		}else{
			this.loadFrame(tabId);			
		}
	};
	$(function() {
		var $centerDom = $(document);
		tmp.tabHeight = $centerDom.height() - 4;
		tmp.init();
		tmp.addTabItem(tmp.tabObj,tmp.tabTmp[0],"任务监控");
		tmp.addTabItem(tmp.tabObj,tmp.tabTmp[1],"填报列表");
		tmp.changeTab(tmp.tabObj,tmp.tabTmp[0]);
	});
</script>

</head>
<body>
	<div id="template.center">
		<div id="tab" style="width: 100%; height:100%; overflow: hidden;"></div>
	</div>
</body>
</html>