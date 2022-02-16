<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<html>
<head>
<meta name="decorator" content="/template/template1_BS.jsp">
<script type="text/javascript">
	var grid, btns, url, ids = [];
	
	$(function(){
		init();
	});

	/* 全局初始化 */
	function init() {
 		var msgState = "${msgSts}";
 		var msgNum = "${msgNum}";
		if (msgState == "1") {
			window.top.$("#msg span").attr('class', 'new');
			window.top.$("#msg span").html('<B>有新消息</B><a style="color:red">('+msgNum+')</a>');
		} else {
			window.top.$("#msg span").attr('class', 'old');
			window.top.$("#msg span").html("消息");
		}
		url = "${ctx}/bione/msgNoticeLog/list.json";
		initSearchForm();
		initGrid();
		initButtons();
		BIONE.addSearchButtons("#search", grid, "#searchbtn");
		
	}

	function initSearchForm() {
		$("#search").ligerForm({
			fields : [ {
				display : "标题",
				name : "msgTitle",
				newline : true,
				type : "text",
				cssClass : "field",
				attr : {
					op : "like",
					field : "log.msgTitle"
				}
			}, {
				display : "发送人",
				name : "sendUser",
				newline : false,
				type : "text",
				cssClass : "field",
				attr : {
					op : "like",
					field : "log.sendUser"
				}
			} , {
				display : "查看状态",
				name : "viewSts",
				newline : false,
				type : "select",
				
				cssClass : "field",
				attr : {
					op : "=",
					field : "log.viewSts"
				},
				options : {
					data : [
							{id : "0", text : "已读"},
							{id : "1", text : "未读"}
						]
				}
			}, {
				display : "消息状态",
				name : "msgType",
				newline : false,
				type : "select",
				cssClass : "field",
				attr : {
					op : "=",
					field : "log.msgType"
				},
				options : {
					data : [
							{id : "01", text : "分享"},
							{id : "02", text : "发布"},
							{id : "03", text : "下载"}
						]
				}
			}  ]
		});
	}
	
	function initGrid() {
		grid = $("#maingrid").ligerGrid({
			height : '100%',
			width : '100%',
			columns : [{
				display : "查看状态",
				name : "viewSts",
				align : 'center',
				width : '8%',
				render : function(data){
					if(data.viewSts == "1"){
						return "<a onclick='msg_detail(\""+data.msgId+"\")'><b>未读</b></a>";
					}else{
						return "已读";
					}
				}
				
			},{
				display : '标题',
				name : 'msgTitle',
				align : 'center',
				width : '35%',
			},{
				display : '消息类型',
				name : 'msgType',
				align : 'center',
				width : '10%',
				render : function(data){
					var value;
					if(data.msgType == "01"){
						value = "分享";
					}
					if(data.msgType == "02"){
						value = "发布";
					}
					if(data.msgType == "03"){
						value = "下载";
					}
					return value;
				}
			}, {
				display : '发送人',
				name : 'userName',
				align : 'center',
				width : '10%',
			}, {
				display : '发送时间',
				name : 'sendTime',
				align : 'center',
				width : '20%',
				type : "date",
				render : function(data){
					var string = BIONE.getFormatDate(data.sendTime, "yyyy-MM-dd hh:mm:ss")
					return string;
				}
			}],
			dateFormat : 'yyyy-MM-dd hh:mm:ss',
			checkbox : false,
			usePager : true,
			isScroll : false,
			rownumbers : true,
			alternatingRow : true,//附加奇偶行效果行
			colDraggable : true,
			dataAction : 'server',//从后台获取数据
			method : 'post',
			url : url,
			sortName : 'sendTime',
			sortOrder: 'desc',
			toolbar : {}
		});
	}

	function changeFontB(data, value){
		if(data.viewSts == "1"){
			return "<b>"+value+"</b>";
		}else{
			return value;
		}
	}
	function initButtons() {
		btns = [ {
			text : '查看',
			click :  msgAnno_view,
			icon : 'fa-book',
			operNo : 'msgAnno_view'
		}, {
			text : '全部标记为已读',
			click :  changeViewSts,
			icon : 'fa-envelope-open-o',
			operNo : 'changeViewSts'
		} , {
			text : '删除',
			click :  logDelete,
			icon : 'fa-trash-o',
			operNo : 'logDelete'
		} , {
			text : '删除所有已读消息',
			click :  logViewedDelete,
			icon : 'fa-trash-o',
			operNo : 'logViewedDelete'
		}  ];
		BIONE.loadToolbar(grid, btns, function() {
		});
	}
	function changeViewSts(item) {
		$.ajax({
			url : "${ctx}/bione/msgNoticeLog/changeViewSts",
			type : "get",
			success : function(){
				BIONE.tip("已标记为已读");
				grid.loadData();
				window.top.$("#msg span").attr('class', 'old');
				window.top.$("#msg span").html("消息");
			},
			error : function(){
				BIONE.tip("发生系统错误！");
			}
		});
	}
	function logViewedDelete(){
		
			$.ligerDialog.confirm('确实要删除多有已读消息吗?', function(yes) {
				if (yes) {
					var flag = false;
					$.ajax({
						async : false,
						type : "POST",
						url : "${ctx}/bione/msgNoticeLog/viewedDelete?date=" + new Date(),
						dataType : "script",
						success : function() {
							flag = true;
						}
					});
					if (flag == true) {
						BIONE.tip('删除成功');
						initGrid();
					} else {
						BIONE.tip('删除失败');
					}
				}
			});
	}
	function logDelete(){
		
		achieveIds();
		if (ids.length > 0) {
			$.ligerDialog.confirm('确实要删除这些记录吗?', function(yes) {
				if (yes) {
					var flag = false;
					$.ajax({
						async : false,
						type : "POST",
						url : "${ctx}/bione/msgNoticeLog?ids=" + ids.join(','),
						dataType : "script",
						success : function() {
							flag = true;
						}
					});
					if (flag == true) {
						BIONE.tip('删除成功');
						initGrid();
					} else {
						BIONE.tip('删除失败');
					}
				}
			});
		} else {
			BIONE.tip('请选择记录');
		}
	}
	function msgAnno_view(item) {
		achieveIds();
		if (ids.length == 1) {
 		 	var buttons = [
			{
				text : '关闭',
				onclick : function(item, dialog) {
					BIONE.submitForm($("#mainform",
							dialog.frame.window.document), function() {
						dialog.close();
						initGrid();
						dialog.close();
					}, function() {
						dialog.close();
					});
				}
			} ];  
			BIONE.commonOpenDialog('消息浏览', 'msgDefManage',
					$(document.body).width()-200, 
					$(document.body).height()-100, 
					'${ctx}/bione/msgNoticeLog/' + ids[0] + '/view', buttons);
		} 
		else if (ids.length > 1) {
			BIONE.tip('只能选择一条记录');
		} 
		else {
			BIONE.tip('请选择记录');
		}
	}
	//单机未读查看消息详情
	function msg_detail(msgId){
		var buttons = [
		   			{
		   				text : '关闭',
		   				onclick : function(item, dialog) {
		   					BIONE.submitForm($("#mainform",
		   							dialog.frame.window.document), function() {
		   						dialog.close();
		   						initGrid();
		   						dialog.close();
		   					}, function() {
		   						dialog.close();
		   					});
		   				}
		   			} ];  
		BIONE.commonOpenDialog('消息浏览', 'msgDefManage',
				$(document.body).width()-200, 
				$(document.body).height()-100, 
				'${ctx}/bione/msgNoticeLog/' + msgId + '/view', buttons);
	}
	// 获取选中的行
	function achieveIds() {
		ids = [];
		var rows = grid.getSelectedRows();
		for ( var i in rows) {
			ids.push(rows[i].msgId);
		}
	}
	
</script>

</head>
<body>
</body>
</html>