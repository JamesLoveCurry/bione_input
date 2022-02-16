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
		urlTmp : ['${ctx}/frs/nrtmessage/personAcctCheck','${ctx}/frs/nrtmessage/personalAcctCollect'],
		tabTmp : ['personAcctInfotab','personalAcctCollecttab']  //与url顺序对应
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
		tmp.addTabItem(tmp.tabObj,tmp.tabTmp[0],"个人账户信息查询");
		tmp.addTabItem(tmp.tabObj,tmp.tabTmp[1],"个人账户信息统计");
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