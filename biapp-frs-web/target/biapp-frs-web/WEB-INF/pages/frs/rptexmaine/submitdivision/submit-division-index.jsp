<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<html>
<head>
<meta name="decorator" content="/template/template1_BS.jsp">
<script type="text/javascript">
	var grid;
	var isSuperUser = '${isSuperUser}';
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
					} ]
				}
			}, {
				display : "报表编号",
				name : "rptNum",
				newline : false,
				type : "text",
				attr : {
					op : "like",
					field : "t1.rptNum"
				}
			}, {
				display : "报表名称",
				name : "rptNm",
				newline : false,
				type : "text",
				attr : {
					op : "like",
					field : "t1.rptNm"
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
				display : '报表编号',
				name : "rptNum",
				width : '20%',
				align : 'center'
			}, {
				display : '报表名称',
				name : 'rptNm',
				width : '25%',
				align : 'left'
			}, {
				display : '报送机构',
				name : 'orgName',
				width : '20%',
				align : 'center'
			}, {
				display : '负责部门',
				name : 'deptName',
				width : '20%',
				align : 'center'
			}, {
				display : 'rid',
				name : "rid",
				hide : "true"
			},{
				display : '机构编号',
				name : "orgNo",
				hide : "true"
			}, {
				display : '部门编号',
				name : "deptNo",
				hide : "true"
			} ],
			checkbox : false,
			usePager : true,
			isScroll : true,
			rownumbers : true,
			alternatingRow : true,//附加奇偶行效果行
			colDraggable : true,
			dataAction : 'server',//从后台获取数据
			url : '${ctx}/frs/submitdivision/queryDivisionList',
			sortName : 'orgNo', //第一次默认排序的字段
			sortOrder : 'asc',
			toolbar : {}
		});
	}
	//管理员有导入按钮，普通用户只能查看
	function initButtons() {
		var btns = [ {
			text : '修改',
			click : f_open_update,
			icon : 'fa-pencil-square-o'
		},{
			text : '导入',
			click : importData,
			icon : 'fa-upload',
			operNo : 'importData'
		} ];
		BIONE.loadToolbar(grid, btns, function() {
		});
	}
	
	function importData(){
		var height = $(window).height();
		BIONE.commonOpenDialog("数据导入", "importWin", 600, 480,
		"${ctx}/report/frame/wizard?type=Division");
	}
	function reload(){
		initGrid();
	}
	
	function f_open_update() {
		var rows = grid.getSelecteds();
		if(rows.length == 1){
			BIONE.commonOpenLargeDialog("修改", "errorModifyWin","${ctx}/frs/submitdivision/" + rows[0].rid + "/edit");
		}else if(rows.length>1){
			BIONE.tip("只能选择一条记录！");
		}else{
			BIONE.tip("请选择一条记录！");
			return;
		}
	}
</script>

</head>
<body>
</body>
</html>