<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<html>
<head>
<meta name="decorator" content="/template/template9.jsp">
<script type="text/javascript">
	var maintab=null;
	var id="${id}";
	var type="${type}";
	var framCenter = 0;
	function addRelTab(type){
		addTabInfo("rel","血统分析");
	}
	function addInfTab(type){
		addTabInfo("inf","影响分析");
	}
	function initTab(){
		maintab = $("#tab").ligerTab({
			contextmenu : false,
			onAfterSelectTabItem : function(tabId) {
				if(tabId == 'rel'){
					if($("#"+tabId+"frame").attr('src')==''){
						var url="${ctx}/rpt/frame/idx/idxanalysis/relTab?id="+id+"&type="+type+"&flag=rel&d="+new Date().getTime();
						$("#"+tabId+"frame").attr('src',url);
					}
				}
				if(tabId == 'inf'){
					if($("#"+tabId+"frame").attr('src')==''){
						var url="${ctx}/rpt/frame/idx/idxanalysis/relTab?id="+id+"&type="+type+"&flag=inf&d="+new Date().getTime();
						$("#"+tabId+"frame").attr('src',url);
					}
				}
			}
		});
	}
	function addTabInfo(id, text) {
		maintab.addTabItem({
			tabid : id,
			text : text,
			showClose : false,
			content : "<div id='" + id + "' style='height:" + framCenter
					+ "px;width:100%;'></div>"
		});
		content = "<iframe frameborder='0' id='"
				+ id
				+ "frame' name='"
				+ id
				+ "frame' style='height:100%;width:100%;' src=''></iframe>";
		$("#" + id).html(content);
	}
	$(function() {
		framCenter = $(document).height() - 30;
		initTab();
		if(type=="idx"){
			addRelTab();
			addInfTab();
			maintab.selectTabItem('rel');
		}
		if(type=="rpt"){
			$("#center").html("<div id='" + id + "' style='height:" + framCenter
					+ "px;width:100%;'></div>");
			content = "<iframe frameborder='0' id='"
				+ id
				+ "frame' name='"
				+ id
				+ "frame' style='height:100%;width:100%;' src='${ctx}/rpt/frame/idx/idxanalysis/relTab?id="+id+"&type="+type+"&flag=rel&d="+new Date().getTime()+"'></iframe>";
			$("#" + id).html(content);
		}
		if(type=="dim"){
			$("#center").html("<div id='" + id + "' style='height:" + framCenter
					+ "px;width:100%;'></div>");
			content = "<iframe frameborder='0' id='"
				+ id
				+ "frame' name='"
				+ id
				+ "frame' style='height:100%;width:100%;' src='${ctx}/rpt/frame/idx/idxanalysis/relTab?id="+id+"&type="+type+"&flag=inf&d="+new Date().getTime()+"'></iframe>";
			$("#" + id).html(content);
		}
		if(type=="model"){
			$("#center").html("<div id='" + id + "' style='height:" + framCenter
					+ "px;width:100%;'></div>");
			content = "<iframe frameborder='0' id='"
				+ id
				+ "frame' name='"
				+ id
				+ "frame' style='height:100%;width:100%;' src='${ctx}/rpt/frame/idx/idxanalysis/relTab?id="+id+"&type="+type+"&flag=inf&d="+new Date().getTime()+"'></iframe>";
			$("#" + id).html(content);
		}
		
	});
</script>
</head>
<body>

</body>
</html>