<%@ page language="java" pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<html>
<meta name="decorator" content="/template/template14_BS.jsp">
<head>
<script type="text/javascript">
	var tmp = {
		tabHeight: 0,
		initTabItem : new Array(),
		initOverTabItem : new Array(),
		// 页面合并
// 		 urlTmp : ['${ctx}/frs/nrtmessage/nrtBaseAcct','${ctx}/frs/nrtmessage/nrtAcctPersonal','${ctx}/frs/nrtmessage/nrtAcctCor','${ctx}/frs/nrtmessage/nrtControinfo','${ctx}/frs/nrtmessage/nrtTask','${ctx}/frs/nrtmessage/nrtAcctInfo'],
// 		tabTmp : ['pisaIdxtab','inOuttab','tsktab','controtab','taskInfo','nrtaccinfo']   //与url顺序对应
		urlTmp : ['${ctx}/frs/nrtmessage/nrtAcctInfo','${ctx}/frs/nrtmessage/nrtTask'],
		tabTmp : ['nrtaccinfo','taskInfo']  //与url顺序对应
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
		var url = this.urlTmp[pos];
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
			searchbox : "<div id='" + tabId + "' style='height:" + 10
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
	//系统初始化入口
	$(function() {
		var $centerDom = $(document);		
		tmp.tabHeight = $centerDom.height() - 46
		tmp.init();
		// 页面合并
// 		tmp.addTabItem(tmp.tabObj,tmp.tabTmp[0],"账户基本信息");
// 		tmp.addTabItem(tmp.tabObj,tmp.tabTmp[1],"账户持有人对私");
// 		tmp.addTabItem(tmp.tabObj,tmp.tabTmp[2],"账户持有人对公");
// 		tmp.addTabItem(tmp.tabObj,tmp.tabTmp[3],"控制人信息");
// 		tmp.addTabItem(tmp.tabObj,tmp.tabTmp[4],"报文生成");
// 		tmp.addTabItem(tmp.tabObj,tmp.tabTmp[5],"非居民账户信息"); 
		tmp.addTabItem(tmp.tabObj,tmp.tabTmp[0],"非居民账户信息"); 
		tmp.addTabItem(tmp.tabObj,tmp.tabTmp[1],"报文生成");
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