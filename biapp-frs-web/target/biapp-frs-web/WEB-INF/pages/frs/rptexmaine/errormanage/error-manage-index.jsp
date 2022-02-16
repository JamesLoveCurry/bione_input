<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<html>
<head>
<meta name="decorator" content="/template/template1_BS.jsp">
<script type="text/javascript">
	var grid;
	var isSuperUser = '${isSuperUser}';
	var ids;
	$(function(){
		initSearchForm();
		initGrid();
		initButtons();
		/* if(isSuperUser){
			
			//$("#orgNmBox").parent().parent().parent().parent().parent().show();
		}else{
			//$("#orgNmBox").parent().parent().parent().parent().parent().hide();
		} */
		BIONE.addSearchButtons("#search", grid, "#searchbtn");
	});
	
	//搜索框
	function initSearchForm() {
		$("#search").ligerForm({
			fields : [{
				display : "报送模块",
				name : "moduleType",
				newline : false,
				type : "select",
				cssClass : "field",
				comboboxName : "moduleTypeBox",
				attr : {
					op : "=",
					field : "t1.moduleType"
				},
				options : {
					data : [ {
						text : '请选择',
						id : ''
					}, {
						text : '1104监管',
						id : '02'
					}, {
						text : '人行大集中',
						id : '03'
					}, {
						text : '利率报备',
						id : '05'
					} ],
					onSelected : function(value) {
						if ("" != value) {
							$.ajax({
								async : false,
								type : "post",
								url : "${ctx}/frs/rptexmaine/control/taskNmComBoBox.json?orgTypes=" + value + "&flag=1&d=" + new Date().getTime(),
								dataType : "json",
								success : function(taskData) {
									$.ligerui.get("taskNm_sel").setValue("");
									$.ligerui.get("taskNm_sel").setData(taskData);
								}
							});
						}
					}
				}
			}, {
				display : "报送任务",
				name : "taskId",
				newline : false,
				type : "select",
				cssClass : "field",
				labelWidth : '90',
				comboboxName : "taskNm_sel",
				attr : {
					op : "=",
					field : "t1.taskId"
				},
				options : {
					onBeforeOpen : function() {
						var moduleType = $.ligerui.get("moduleTypeBox").getValue();
						if ("" == moduleType) {
							$.ligerui.get("taskNm_sel").setValue("");
							$.ligerui.get("taskNm_sel").setData("");
							BIONE.tip("请选择报送模块");
						}
					},
					cancelable : true,
					dataFilter : true
				}
			}, {
				display : "报送机构",
				name : "orgNo",
				newline : false,
				type : "select",
				cssClass : "field",
				comboboxName : "orgNmBox",
				attr : {
					op : "=",
					field : "t3.orgNo"
				},
				options : {
					onBeforeOpen : function() {
						var moduleType = $.ligerui.get("moduleTypeBox").getValue();
						if (moduleType) {
							var height = $(window).height() - 120;
							var width = $(window).width() - 480;
							window.BIONE.commonOpenDialog(
											"机构树",
											"orgTreeWin",
											width,
											height,
											"${ctx}/frs/submitdivision/searchOrgTree?moduleType=" + moduleType,
											null);
							return false;
						} else {
							BIONE.tip("请选择报送模块！");
						}
					},
					cancelable : true
				}
			} ]
		});
	}
	//列表
	function initGrid() {
		grid = $("#maingrid").ligerGrid({
			InWindow : false,
			height : '99%',
			width : '100%',
			columns : [ {
				display : '报送模块',
				name : 'moduleType',
				width : '10%',
				align : 'center',
				render : function(rowdata, index, value) {
					if (value == "02")
						return "1104监管";
					if (value == "03")
						return "人行大集中";
					if (value == "05")
						return "利率报备";
					else
						return "未知类型";
				}
			}, {
				display : '报送任务',
				name : "taskNm",
				width : '15%',
				align : 'left'
			}, {
				display : '数据日期',
				name : "dataDate",
				width : '10%',
				align : 'center'
			}, {
				display : '报送机构',
				name : 'orgName',
				width : '10%',
				align : 'center'
			}, {
				display : '负责部门',
				name : 'deptName',
				width : '10%',
				align : 'center'
			}, {
				display : '错误概述',
				name : 'errorDesc',
				width : '20%',
				align : 'center'
			}, {
				display : '错误附件',
				name : 'errorFile',
				width : '20%',
				align : 'center',
				render : download
			}, {
				display : 'rid',
				name : "rid",
				hide : "true"
			}, {
				display : '任务编号',
				name : "taskId",
				hide : "true"
			}, {
				display : '机构编号',
				name : "orgNo",
				hide : "true"
			}, {
				display : '部门编号',
				name : "deptNo",
				hide : "true"
			}, {
				display : '创建时间',
				name : "createTime",
				hide : "true"
			}],
			checkbox : false,
			usePager : true,
			isScroll : true,
			rownumbers : true,
			alternatingRow : true,//附加奇偶行效果行
			colDraggable : true,
			dataAction : 'server',//从后台获取数据
			url : '${ctx}/frs/errormanage/queryErrorManageList',
			sortName : 'dataDate', //第一次默认排序的字段
			sortOrder : 'desc',
			toolbar : {}
		});
	}
	//管理员有导入按钮，普通用户只能查看
	function initButtons() {
		var btns = [ {
				text : '增加',
				click : f_open_add,
				icon : 'fa-plus'
			}, {
				text : '修改',
				click : f_open_update,
				icon : 'fa-pencil-square-o'
			}, {
				text : '删除',
				click : f_delete,
				icon : 'fa-trash-o'
			}, {
				text : '导入',
				click : importData,
				icon : 'fa-upload',
				operNo : 'importData'
			}, {
				text : '上传附件',
				click : uploadFile,
				icon : 'fa-upload',
				operNo : 'uploadFile'
			} ];
		BIONE.loadToolbar(grid, btns, function() {
		});
	}
	
	function importData(){
		var height = $(window).height();
		BIONE.commonOpenDialog("数据导入", "importWin", 600, 480,
		"${ctx}/report/frame/wizard?type=Error");
	}
	function reload(){
		initGrid();
	}

	//新增
	function f_open_add() {
		BIONE.commonOpenLargeDialog("新增", "errorAddWin", "${ctx}/frs/errormanage/new");
	}

	function f_open_update() {
		var rows = grid.getSelecteds();
		if(rows.length == 1){
			BIONE.commonOpenLargeDialog("修改", "errorModifyWin","${ctx}/frs/errormanage/" + rows[0].rid + "/edit");
		}else if(rows.length>1){
			BIONE.tip("只能选择一条记录！");
		}else{
			BIONE.tip("请选择一条记录！");
			return;
		}
	}
	function f_delete() {
		var rows = grid.getSelecteds();
		if(rows.length == 0){
			BIONE.tip('请选择一条记录！');
			return;
		}
		$.ligerDialog.confirm('确实要删除这' + rows.length + '条记录吗!',
				function(yes) {
					var length = rows.length;
					var ids = "";
					for ( var i = 0; i < length; i++) {
						if (ids != "") {
							ids += ",";
						}
						ids += rows[i].rid;
					}
					$.ajax({
						type : "POST",
						url : '${ctx}/frs/errormanage/deleteError',
						dataType : "json",
						data : {
							"ids" : ids
						},
						success : function(result) {
							if (result) {
								BIONE.tip('删除成功');
								grid.loadData();
							} else {
								BIONE.tip('删除失败,请联系管理员!');
							}
						}
					});
				});
	}
	
	//上传附件
	function uploadFile() {
		var selRow  = grid.getSelectedRows();
		if(selRow != null && selRow.length == 1){
			var rid = selRow[0].rid;
			var moduleType = selRow[0].moduleType;
			var taskId = selRow[0].taskId;
			var dataDate = selRow[0].dataDate;
			var orgNo = selRow[0].orgNo;
			BIONE.commonOpenDialog('附件上传','importFile',600,320,'${ctx}/frs/errormanage/uploadFile?rid='+rid+'&moduleType='+moduleType);
		}else{
			parent.BIONE.tip("请选择一条记录");
		}
	}
	
	//下载附件
	function download(rowdata) {
		if(rowdata.errorFile != null && rowdata.errorFile != ""){
			return "<a href='javascript:void(0)' class='link' onclick='downloadFile(\""+ rowdata.errorFile+"\",\"" + rowdata.moduleType+"\")'>"+ rowdata.errorFile + "</a>";
		}else{
			return rowdata.errorFile;
		}
	};
	
	function downloadFile(errorFile, moduleType){
		window.location.href = "${ctx}/frs/errormanage/downloadFile?fileName="+errorFile+"&moduleType="+moduleType;
	}
</script>

</head>
<body>
</body>
</html>