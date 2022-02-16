<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<html>
<head>
<meta name="decorator" content="/template/template9.jsp">
<style>
.
</style>
<script type="text/javascript">
	var maintab=null;
	var id="${id}";
	var idx=null;
	var rpt=null;
	var dim=null;
	var type="${type}";
	var flag="${flag}";
	function addIdxTab(){
		addTabInfo("idx","指标");
	}
	function addRptTab(){
		addTabInfo("rpt","报表");
	}
	function addDimTab(){
		addTabInfo("dim","维度");
	}
	function reloadInfo(id){
		if(idx){
			idx.reloadGrid(id);
		}
		if(rpt){
			rpt.reloadGrid(id);
		}
		if(dim){
			dim.reloadGrid(id);
		}
	}
	function initTab(){
		maintab = $("#tab").ligerTab({
			contextmenu : false,
			onAfterSelectTabItem : function(tabId) {
				if(tabId == 'idx'){
					if($("#"+tabId+"frame").attr('src')==''){
						var url="${ctx}/rpt/frame/idx/idxanalysis/idxTab?id="+id+"&type=${type}&flag=${flag}&d="+new Date().getTime();
						$("#"+tabId+"frame").attr('src',url);
					}
				}
				if(tabId == 'rpt'){
					if($("#"+tabId+"frame").attr('src')==''){
						var url="${ctx}/rpt/frame/idx/idxanalysis/rptTab?id="+id+"&type=${type}&flag=${flag}&d="+new Date().getTime();
						$("#"+tabId+"frame").attr('src',url);
					}
				}
				if(tabId == 'dim'){
					if($("#"+tabId+"frame").attr('src')==''){
						var url="${ctx}/rpt/frame/idx/idxanalysis/dimTab?id="+id+"&type=${type}&flag=${flag}&d="+new Date().getTime();
						$("#"+tabId+"frame").attr('src',url);
					}
				}
			}
		});
		switch("${type}"){
			case 'idx':
				addIdxTab();
				if("${flag}"!="rel")
					addRptTab();
				maintab.selectTabItem('idx');
				break;
			case 'rpt':
				addIdxTab();
				addDimTab();
				maintab.selectTabItem('idx');
				break;
			case 'dim':
				addRptTab();
				maintab.selectTabItem('rpt');
				break;
			case 'model':
				addIdxTab();
				maintab.selectTabItem('idx');
				break;
		}
	}
	function addTabInfo(id, text) {
		maintab.addTabItem({
			tabid : id,
			text : text,
			showClose : false,
			content : "<div id='" + id + "' style='height:" + framCenter
					+ "px;width:100%;'></div>"
		});
		content = "<div id='"+id+"toolbar' style='height:30px;background:#fff;'></div><iframe frameborder='0' id='"
				+ id
				+ "frame' name='"
				+ id
				+ "frame' style='height:"+(framCenter-40)+"px;width:100%;' src=''></iframe>";
		$("#" + id).html(content);
		initToolbar(id+"toolbar");
	}
	
	
	
	function initToolbar(id) {
		$("#" + id).ligerToolBar({
			items : [ {
				text : '展开',
				click : function() {
					var tabId = maintab.getSelectedTabItemID();
					window.frames[tabId+"frame"].dupontTree.expandAll(true);
				},
				icon : 'up'
			}, {
				text : '折叠',
				click : function() {
					var tabId = maintab.getSelectedTabItemID();
					window.frames[tabId+"frame"].dupontTree.expandAll(false);
				},
				icon : 'down'
			}, {
				text : '导出',
				click : function() {
					var tabId = maintab.getSelectedTabItemID();
					window.frames[tabId+"frame"].f_export();
				},
				icon : 'export'
			} ]
		});
		
	}
	$(function() {
		parent.window["${flag}"] = window;
		framCenter = $(document).height() - 18;
		initTab();
	});
	
	
	
</script>
</head>
<body>

</body>
</html>