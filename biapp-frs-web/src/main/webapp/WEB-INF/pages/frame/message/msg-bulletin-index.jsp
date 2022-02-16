<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<html>
<head>
<meta name="decorator" content="/template/template1_BS.jsp">
<script type="text/javascript">
	var grid, btns, url, ids = [];

	$(init);

	/* 全局初始化 */
	function init() {
		url = "${ctx}/bione/msg/bulletin/list.json";
		initSearchForm();
		initGrid();
		initButtons();
		BIONE.addSearchButtons("#search", grid, "#searchbtn");
		$('#searchbtn div:last-child').bind('click', function() {
			$('#edDate').val('');
		});
	}

	function initSearchForm() {
		$("#search").ligerForm({
			fields : [ {
				display : "标题",
				name : "announcementTitle",
				newline : true,
				type : "text",
				cssClass : "field",
				attr : {
					op : "like",
					field : "msgAnno.announcementTitle"
				}
			}, {
				type: 'hidden',
				name: 'edDate',
				attr: {
					op: '<',
					vt: 'date',
					field: 'msgAnno.lastUpdateTime'
				}
			}, {
				display: "发布时间",
				name: 'date',
				newline: false,
				type: 'date',
				attr: {
					op: '>=',
					vt: 'date',
					field: 'msgAnno.lastUpdateTime'
				},
				options: {
					cancelable: false,
					onChangeDate: function(value) {
						var d = new Date((new Date(value) / 1000 + 86400) * 1000);
						var year = d.getFullYear();
						var month = d.getMonth() > 8 ? d.getMonth() + 1 : '0' + (d.getMonth() + 1);
						var day = d.getDate() > 9 ? d.getDate() : '0' + (d.getDate());
						$('#edDate').val('' + year + '-' + month + '-' + day);
					}
				}
			}, {
				display: '发布人',
				name: 'faburen',
				newline: false,
				type: 'text',
				attr : {
					op : "like",
					field : "u.userName"
				}
			}]
		});
	}
	function initGrid() {
		grid = $("#maingrid").ligerGrid({
			height : '100%',
			width : '100%',
			columns : [ {
				display : '标题',
				name : 'announcementTitle',
				align : 'center',
				width : '40%',
				isSort : true
			}, {
				display : '发布时间',
				name : 'effectiveDate',
				align : 'center',
				width : '15%',
				type : 'date',
				render : function(data){
					return BIONE.getFormatDate(data.lastUpdateTime, "yyyy-MM-dd hh:mm:ss");
				},
				format : 'yyyy-MM-dd hh:mm:ss',
				isSort : true
			}, {
				display : '发布人',
				name : 'lastUpdateUser',
				align : 'center',
				width : '10%',
				isSort : true
			}],
			dateFormat : "yy:MM:dd hh:mm:ss",
			checkbox : false,
			usePager : true,
			isScroll : false,
			rownumbers : true,
			alternatingRow : true,//附加奇偶行效果行
			colDraggable : true,
			dataAction : 'server',//从后台获取数据
			method : 'post',
			url : url,
			sortName : 'lastUpdateTime desc', //第一次默认排序的字段
			sortOrder : 'desc',
			toolbar : {}
		});
	}

	function initButtons() {
		btns = [{
			text : '浏览',
			click : msgDef_preview,
			icon : 'bluebook',
			operNo : 'msgDef_preview'
		}];
		BIONE.loadToolbar(grid, btns, function() {
		});
	}

	function MsgStsRender(rowdata) {
		if (rowdata.announcementSts == '1')
			return "草稿";
		if (rowdata.announcementSts == '2')
			return "已发布";
		if (rowdata.announcementSts == '0')
			return "已取消发布";
		else
			return rowdata.roleSts;
	}

	function msgDef_add(item) {
		BIONE.commonOpenDialog('新建公告', 'msgDefManage',
				$(document.body).width(), $(document.body).height(),
				'${ctx}/bione/msg/announcement/new');
	}
	function msgDef_modify(item) {
		// 发布状态的消息不允许修改；
		ids = [];
		var rows = grid.getSelectedRows();
		for ( var i in rows) {
			if (rows[i].announcementSts == "2") {
				alert("发布状态的公告不允许修改！");
				return false;
			} else {
				ids.push(rows[i].announcementId);
			}
		}
		if (ids.length == 1) {
			var buttons = [
					{
						text : '保存',
						onclick : function(item, dialog) {
							BIONE.submitForm($("#mainform",
									dialog.frame.window.document), function() {
								dialog.close();
								initGrid();
								parent.BIONE.tip('修改成功');
							}, function() {
								BIONE.tip('保存失败');
							});
						}
					}, {
						text : '关闭',
						onclick : function(item, dialog) {
							dialog.close();
						}
					} ];
			BIONE.commonOpenDialog('编辑公告', 'msgDefManage', $(document.body)
					.width(), $(document.body).height(),
					'${ctx}/bione/msg/announcement/' + ids[0] + '/edit',
					buttons);
		} else if (ids.length > 1) {
			BIONE.tip('只能选择一条记录');
		} else {
			BIONE.tip('请选择记录');
		}
	}

	function msgDef_delete(item) {
		// 判断：已发布状态的消息，不能删除；必须返回到编辑状态才能删除；
		ids = [];
		var rows = grid.getSelectedRows();
		for ( var i in rows) {
			if (rows[i].announcementSts == "2") {
				alert("发布状态的公告不允许删除！");
				return false;
			} else {
				ids.push(rows[i].announcementId);
			}
		}
		// 
		if (ids.length > 0) {
			$.ligerDialog.confirm('确实要删除这些记录吗?', function(yes) {
				if (yes) {
					var flag = false;
					$.ajax({
						async : false,
						type : "POST",
						url : "${ctx}/bione/msg/announcement/" + ids.join(','),
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
	function msgDef_publish() {
		achieveIds();
		chkfb = "";

		var rows = grid.getSelectedRows();
		for ( var i in rows) {
			if (rows[i].announcementSts == "2") {
				alert("选择记录中包含已发布记录");
				return;
			}
		}
		if (ids.length > 0) {
			$.ligerDialog.confirm(chkfb + '确实要发布这些记录吗?', function(yes) {
				if (yes) {
					var flag = false;
					$.ajax({
						async : false,
						type : "POST",
						url : "${ctx}/bione/msg/announcement/" + ids.join(',')
								+ "/publish",
						dataType : "script",
						success : function() {
							flag = true;
						}
					});
					if (flag == true) {
						BIONE.tip('发布成功');
						initGrid();
					} else {
						BIONE.tip('发布失败');
					}
				}
			});
		} else {
			BIONE.tip('请选择记录');
		}
	}
	function msgDef_unPublish() {
		achieveIds();
		chkfb = "";
		var rows = grid.getSelectedRows();
		for ( var i in rows) {
			if (rows[i].announcementSts == "1" || rows[i].announcementSts == "3"
					|| rows[i].announcementSts == "0") {
				chkfb = "选择记录中包含非发布记录，";
				break;
			}
		}
		if (ids.length > 0) {
			$.ligerDialog.confirm(chkfb + '确实要取消发布这些记录吗?', function(yes) {
				if (yes) {
					var flag = false;
					$.ajax({
						async : false,
						type : "POST",
						url : "${ctx}/bione/msg/announcement/" + ids.join(',')
								+ "/unPublish",
						dataType : "script",
						success : function() {
							flag = true;
						}
					});
					if (flag == true) {
						BIONE.tip('取消发布成功');
						initGrid();
					} else {
						BIONE.tip('取消发布失败');
					}
				}
			});
		} else {
			BIONE.tip('请选择记录');
		}
	}
	function msgDef_preview() {
		achieveIds();
		if (ids.length == 1) {
			BIONE.commonOpenDialog('公告浏览', 'msgDefManage', $(document.body)
					.width(), $(document.body).height(),
					'${ctx}/bione/msg/announcement/' + ids[0] + '/view');
		} else if (ids.length > 1) {
			BIONE.tip('只能选择一条记录');
		} else {
			BIONE.tip('请选择记录');
		}
	}
	// 获取选中的行
	function achieveIds() {
		ids = [];
		var rows = grid.getSelectedRows();
		for ( var i in rows) {
			ids.push(rows[i].announcementId);
		}
	}
</script>

</head>
<body>
</body>
</html>