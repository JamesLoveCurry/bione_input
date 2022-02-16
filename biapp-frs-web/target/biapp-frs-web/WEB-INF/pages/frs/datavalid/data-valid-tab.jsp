<%@ page language="java" pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<html>
<meta name="decorator" content="/template/template14.jsp">
<head>
<script type="text/javascript">
	var tmp = {
		tabHeight: 0,
		initTabItem : new Array(),
		initOverTabItem : new Array(),
		urlTmp : ['${ctx}/rpt/frs/verificationLogic','${ctx}/rpt/frs/verificationTotal','${ctx}/rpt/frs/rptfill/mychildtask'],
		tabTmp : ['logictab','sumparttab','warntab']  //与url顺序对应
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
					//BIONE.showLoading();
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
		}
		var url = this.urlTmp[pos];
		var frame = $('<iframe/>');
		frame.attr({
				id : tabId,
				frameborder : 0,
				src : url+"?d=" + new Date().getTime()
		}).css({
			 overflow : "hidden",
			 height : tmp.tabHeight
		});
		$('div[tabId=' + tabId + ']').html(frame);
		this.initOverTabItem.push(tabId);
	
	};
	tmp.addTabItem = function(tabObj,tabId, tabText){
		tabObj.addTabItem({//添加标签tab页
			tabid : tabId,
			text : tabText,
			showClose : false,
			content : "<div id='" + tabId + "' style='height:" + tmp.tabHeight
					+ "px;width:100%;overflow: hidden;'></div>"
		});
	};
	tmp.changeTab = function(tabObj,tabId,loadFlag){
		tabObj.selectTabItem(tabId);
		if(!loadFlag){
			//不重新加载url
			if($.inArray(tabId,this.initOverTabItem) > -1){
				return ;
			}else{
				this.loadFrame(tabId);			
			}
		}else{
			this.loadFrame(tabId);		
		}
		
	};
	$(function() {
		var $centerDom = $(document);
		tmp.tabHeight = $centerDom.height() - 2;
		tmp.init();
		tmp.addTabItem(tmp.tabObj,tmp.tabTmp[0],"逻辑校验结果");
		tmp.addTabItem(tmp.tabObj,tmp.tabTmp[1],"总分校验结果");
		tmp.addTabItem(tmp.tabObj,tmp.tabTmp[2],"警戒校验结果");
		tmp.changeTab(tmp.tabObj,tmp.tabTmp[0]);
	});
</script>
</head>
<body>
	<div id="template.center">
		<div id="formButtom" style="width:100%;overflow:hidden"></div>
		<div id="tab" style="width: 100%; overflow: hidden;"></div>
	</div>
</body>
</html>