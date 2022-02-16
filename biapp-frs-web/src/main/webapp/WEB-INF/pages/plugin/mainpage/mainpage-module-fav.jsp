<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<%@ include file="/common/meta.jsp"%>
<%@ include file="/common/taglibs.jsp"%>
<%@ include file="/common/jquery_load.jsp"%>
<%@ include file="/common/ligerUI_load.jsp"%>
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<title>Insert title here</title>
<link rel="stylesheet" type="text/css"
	href="${ctx}/css/classics/mainpage/example.css" />
	<style >
	body,html{
		height:100%;
	}
	</style>
<script type="text/javascript">

	//更多
	function openMore(id)
	{	
		window.parent.BIONE.commonOpenFullDialog("我的收藏","favWin","${ctx}/rpt/frame/rptfav/query");
	}

	var customchartsApp = {
		tabObj : null,//选项卡对象
		tabHeight : 0,//tab高度
		tabIdRecord : "",//缓存tab页面ID
		newTabIdRecord : "",//缓存新增的tab页面ID
		tabNameList : [ "我的收藏"],//用于存储标签页名称

		/**
		 *初始化
		 **/
		init : function(tabnum) {
			customchartsApp.tabObj = $("#tab").ligerTab({
				//showSwitch : true,
				contextmenu : false,
				onBeforeAddTabItem : function(tabObj) {
					
					customchartsApp.newTabIdRecord += tabObj.tabid;
				},
				//选中时加载避免样式错误
				onAfterSelectTabItem : function(tabId) {
					var frame_src = "/rpt/frame/rptfav/report?state=1";
					if(tabId=="customchart-2"){//我的报表
						frame_src = "/rpt/frame/rptfav/index";
					}
					tabId && customchartsApp.loadFrame(tabId,frame_src);
				}
			});
			for ( var i = 1; i <= tabnum; i++) {
				var tabid = "customchart-" + i;
				customchartsApp.tabObj.addTabItem({
					tabid : tabid,
					text : this.tabNameList[i - 1],
					height : this.tabHeight,
					showClose : false
				});
			}
			
			$(".l-tab-links").find("ul").after("<li style='float:right;left: 0px;'><a class='more' onclick='customchartsApp.changeIndex()' style='color: #F47F56;'>更多</a></li>")

		},
		changeIndex: function(){
			window.parent.parent.changePage('${ctx}/rpt/frame/rptfav/query',null,"我的收藏");
		},
		/**
		 *加载处理项tab
		 **/
		loadFrame : function(tabid,src) {
			//如果该标签页是新增的标签页则跳过加载
			if (customchartsApp.newTabIdRecord.indexOf(tabid) > -1) {
				customchartsApp.newTabIdRecord = customchartsApp.newTabIdRecord
						.replace(tabid, "");
				return;
			}
			//如果缓存的标签页名称中包含该标签页ID，则不加载此标签页
			if (customchartsApp.tabIdRecord.indexOf(tabid) > -1) {
				return;
			} else {
				customchartsApp.tabIdRecord += tabid;
				customchartsApp.tabIdRecord += ",";
			}
			if ($('#' + tabid).attr('src')) {
				return;
			}
			var frame = $('<iframe/>');
			frame
					.attr(
							{
								id : tabid,
								frameborder : 0,
								src : "${ctx}"+src+"?state='1'&tabid="
										+ tabid + "&d=" + new Date()
							}).css("height", this.tabHeight-15).css("background-color","#FFFFFF");
			$('div[tabId=' + tabid + ']').append(frame);
		}
	};

	var tabnum = 1;
	$(function() {
		var contentHeight = $(document).height();
		$("#hisrpt_inbox").height(contentHeight - 2);
		$("#hisrpt_inboxcon").height(contentHeight - 35);
		$("#hisrpt_inlist").height(contentHeight - 35);
		$("#hisrpt_con").height(contentHeight - 35);
		$("#info").height(contentHeight - $(".in_box_titbg").height());
		
		
		/* var parentWidth = window.parent.$("#newsframe").width();
		$("#tab").width(parentWidth - 2);
		var parentHeight = window.parent.$("#newsframe").height();*/
		customchartsApp.tabHeight = contentHeight -30; 
		
		/* customchartsApp.init(tabnum);
		if (tabnum >= 1) {
			customchartsApp.tabObj.selectTabItem("customchart-1");
		} */
	});
</script>
</head>
<body>
	<div id="hisrpt_inbox" class="in_box">
		<div class="in_box_titbg">
			  <div class="in_box_tit"><span class="icon">我的收藏</span><span class="more"><a href="javascript:openMore()">更多</a></span></div>
		</div>
		<div id="box" class="in_box_con" style="width: 100%; overflow: hidden;">
			<iframe id="info"  name="info" frameborder="0" style="width : 98%;height: 90%;"src="${ctx}/rpt/frame/mainpage/myfav/frame"></iframe>
		</div> 
		</div>
</body>
</html>