<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<html>
<head>
<meta name="decorator" content="/template/template5.jsp">
<script type="text/javascript">
	var grid, btns, url, ids = [], user = [];
	var userId;
    $(function() {
		templateinit();
		init();
		initLayout();
		initBtn();
    });
    
    function initBtn(){
		//添加按钮
		var btns = [{
			text : "取消",
			onclick : f_cancel
		},{
			text : "选择",
			onclick : f_select
		}];
		BIONE.addFormButtons(btns);
	}
	
    function f_select(){
    	var row = grid.getSelectedRow();
    	if(row){
    		var $w = window.parent.$window;
    		$w.liger.get("userId")._changeValue(row.userId,row.userName);
    		$w.liger.get("defDept")._changeValue(row.deptId,row.deptName);
    		$w.$("#defTel").val(row.tel);
    		$w.$("#defMail").val(row.email);
    		BIONE.closeDialog("selectWin");
    	}
    	else{
    		BIONE.tip("请选择一个用户");
    	}
    }
    
    function f_cancel(){
    	BIONE.closeDialog("selectWin");
    }
    function templateinit() {
		$(".l-dialog-btn").live('mouseover', function() {
		    $(this).addClass("l-dialog-btn-over");
		}).live('mouseout', function() {
		    $(this).removeClass("l-dialog-btn-over");
		});
		$(".l-dialog-tc .l-dialog-close").live('mouseover', function() {
		    $(this).addClass("l-dialog-close-over");
		}).live('mouseout', function() {
		    $(this).removeClass("l-dialog-close-over");
		});
		$(".searchtitle .togglebtn").live(
			'click',
			function() {
			    var searchbox = $(this).parent().nextAll(
				    "div.searchbox:first");
			    var centerHeight = $("#center").height();
			    if ($(this).hasClass("togglebtn-down")) {
				$(this).removeClass("togglebtn-down");
				searchbox.slideToggle('fast', function() {
	
				    if (grid) {
					grid.setHeight(centerHeight
						- $("#mainsearch").height() - 25);
				    }
				});
			    } else {
				$(this).addClass("togglebtn-down");
				searchbox.slideToggle('fast', function() {
	
				    if (grid) {
					grid.setHeight(centerHeight
						- $("#mainsearch").height() - 20);
				    }
				});
			   }
		});
    }
    function initLayout() {
		if (grid) {
		    var centerHeight = $("#center").height();
		    grid.setHeight(centerHeight - $("#mainsearch").height() - 25);
		}
    }

	function init() {
		url = "${ctx}/bione/admin/user/list.json?isSts = 1";
		initGrid();
		searchForm();
		BIONE.addSearchButtons("#search", grid, "#searchbtn");
	}

	//搜索表单应用ligerui样式
	function searchForm() {
		$("#search").ligerForm({
			fields : [ {
				display : "用户帐号",
				name : "userNo",
				newline : true,
				type : "text",
				cssClass : "field",
				attr : {
					op : "=",
					field : "usr.user_No"
				}
			}, {
				display : "用户名称",
				name : "userName",
				newline : false,
				type : "text",
				cssClass : "field",
				attr : {
					op : "like",
					field : "usr.user_Name"
				}
			}, {
				display : "机构名称",
				name : "orgNo",
				newline : true,
				type : "text",
				cssClass : "field",
				attr : {
					op : "like",
					field : "org.org_Name"
				}
			}, {
				display : "用户状态",
				name : "userSts",
				newline : false,
				type : "select",
				cssClass : "field",
				options : {
					data : [{id : '1', text : '启用'}, {id : '0', text : '停用'}]
				},
				attr : {
					op : "=",
					field : "usr.user_Sts"
				}
			} ]
		});

	}

	function initGrid() {

		grid = $("#maingrid").ligerGrid(
				{
					width : '98%',
					columns : [
							{
								display : '用户帐号',
								name : 'userNo',
								align : 'left',
								width : '13%',
								sortname : "usr.user_no"
							},
							{
								display : '用户名称',
								name : 'userName',
								align : 'center',
								width : '24%',
								sortname : "usr.user_name"
							},
							{
								display : '机构',
								name : 'orgName',
								align : 'center',
								width : '13%',
								sortname : "org.org_Name" ,
							},
							{
								display : '条线',
								name : 'deptName',
								align : 'center',
								width : '13%',
								sortname : "dept.dept_name" ,
							},
							{
								display : '邮箱',
								name : 'email',
								align : 'center',
								width : '14%',
								sortname : "usr.email"
							},
							{
								display : '电话',
								name : 'tel',
								align : 'center',
								width : '14%',
								sortname : "usr.tel"
							}],
					checkbox : false,
					usePager : true,
					isScroll : false,
					rownumbers : true,
					alternatingRow : true,//附加奇偶行效果行
					colDraggable : true,
					dataAction : 'server',//从后台获取数据
					method : 'post',
					url : url,
					sortName : 'user_No', //第一次默认排序的字段
					sortOrder : 'asc',
					onDblClickRow : function (row, rowindex, rowobj)
					{
						var $w = window.parent.$window;
			    		$w.liger.get("userId")._changeValue(row.userId,row.userName);
			    		$w.liger.get("defDept")._changeValue(row.deptId,row.deptName);
			    		$w.$("#defTel").val(row.tel);
			    		$w.$("#defMail").val(row.email);
			    		BIONE.closeDialog("selectWin");
					} 
				});
	}

	
</script>
</head>
<body>
	<div id = "template.center">
		<div id="mainsearch" style="width:99%;">
			<div class="searchtitle">
				<img src="${ctx}/images/classics/icons/find.png" /> <span>搜索</span>
				<div class="togglebtn">&nbsp;</div>
			</div>
			<div id="searchbox" class="searchbox">
				<form id="formsearch">
					<div id="search"></div>
					<div class="l-clear"></div>
				</form>
				<div id="searchbtn" class="searchbtn"></div>
			</div>
		</div>
		<div class="content">
			<div id="maingrid" class="maingrid"></div>
		</div>	
	</div>
</body>
</html>