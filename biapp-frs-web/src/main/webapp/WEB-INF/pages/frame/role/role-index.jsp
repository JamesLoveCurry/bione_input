<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<html>
<meta name="decorator" content="/template/template1_BS.jsp">
<head>
<!-- 基础的JS和CSS文件 -->
<script type="text/javascript">
	var grid,ids;
	var busiTypeMap = new Map();
	var isSuperUser = '${isSuperUser}';
	var isManager = '${isManager}';
	var userId = '${userId}';

	$(function() {
		initBusiType();
		initSearchForm();
		initGrid();
		initToolBar();
		BIONE.addSearchButtons("#search", grid, "#searchbtn");
	});
	
	function initSearchForm(){
		$("#search").ligerForm({
			fields : [ {
				name : 'roleId',
				type : 'hidden'
			}, {
				display : '角色名称',
				name : "roleName",
				newline : true,
				type : "text",
				cssClass : "field",
				attr : {
					field : 'role.roleName',
					op : "like"
				}
			},{
				display : '平台角色',
				newline : false,
				cssClass : "field",
				name:'roleTypeJg',
				type:'select',
				options : {
					url : "${ctx}/bione/admin/role/getBioneRoleInfoExt",
					cancelable:true
				},
				attr : {
					field : 'role.roleTypeJg',
					op : "="
				}
			}, {
				display : '用户名称',
				newline : false,
				cssClass : "field",
				name:'userName',
				type:'text',
				attr : {
					field : 'user.userName',
					op : "like"
				}
			}
			]
		});
	};
	
	function initGrid(){
		grid = $("#maingrid").ligerGrid({
			toolbar : {},
			checkbox : true,
			columns : [{
				display : '平台角色',
				name : 'roleTypeJg',
				width : "16%",
				align : 'left',
				render: function(rowdata, index, value) {
					return busiTypeMap.get(value);
				}
			}, {
				display : '角色标识',
				name : 'roleNo',
				width : "16%",
				align : 'left'
			}, {
				display : '角色名称',
				name : 'roleName',
				width : "19%",
				align : 'left'
			}, {
				display : '用户名称',
				name : 'userName',
				width : "16%"
			}, {
				display : '角色状态',
				name : 'roleSts',
				width : "11%",
				render : function(row){
					return renderHandler(row);
				}
			}, {
				display : '最后修改时间',
				name : 'lastUpdateTime',
				width : "16%",
				type : 'date',
				format: 'yyyy-MM-dd hh:mm:ss'
			} ],
			dataAction : 'server', //从后台获取数据
			usePager : true, //服务器分页
			alternatingRow : true, //附加奇偶行效果行
			colDraggable : true,
			url : "${ctx}/bione/admin/role/list.json",
			sortName : 'lastUpdateTime',//第一次默认排序的字段
			sortOrder : 'desc', //排序的方式
			rownumbers : true,
			width:'100%',
			height : '99%',
			isScroll : true,
			onAfterShowData : function(){
				if (!$.browser.msie || parseInt($.browser.version, 10) >= 9) {
					if($(".rcswitcher").length > 0){
						$(".rcswitcher").rcSwitcher({
							onText: '启用',
							offText: '停用',
							height:16.5,
							autoFontSize : true
						}).on({
							'turnon.rcSwitcher': function( e, dataObj ){
								var roleId = dataObj.$input.attr("roleId");
								var rowId = dataObj.$input.attr("rowid");
								if(roleId
										&& rowId){
									changeSts(roleId , '1' , rowId);
								}
						    },
						    'turnoff.rcSwitcher': function( e, dataObj ){
						    	var roleId = dataObj.$input.attr("roleId");
						    	var rowId = dataObj.$input.attr("rowid");
								if(roleId
										&& rowId){
									changeSts(roleId , '0' , rowId);
								}
						    }			
						});
					}
				}
			}
		});
		grid.setHeight($("#center").height() - 115);
	}
	
	// 更改角色状态
	function changeSts(roleId , sts , rowId){
		$.ajax({
			cache : false,
			async : true,
			url : '${ctx}/bione/admin/role/changeRoleSts',
			dataType : 'json',
			type : "post",
			data : {
				roleId : roleId,
				sts : sts
			},
			beforeSend : function() {
			},
			success : function(result){
				if(rowId
						&& grid){
					var row = grid.getRow(rowId);
					if(row){
						row.roleSts = sts;
					}
				}
			}
		});	
	}
	
	function renderHandler(row){
		if ($.browser.msie && parseInt($.browser.version, 10) < 9) {
			if (1== row.roleSts){
				return "启用"
			} else {
				return "停用";
			}
		}else{
			var html = "<input class='rcswitcher' type='checkbox'  name='check"+row.roleId+"' roleId='"+row.roleId+"' rowId='"+row.__id+"' ";
			if(row.roleSts == "1"){
				html += "checked";
			}
			html += " />";
			return html;
		}
	}
	
	function initToolBar(){
		var btns = [];
		if("true" != isSuperUser && 'Y' != isManager){
			btns = [];
		}else{
			btns=[ {
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
			} ,{
                text : '角色授权用户关系',
                click : role_object_auth,
                icon : 'fa-pencil-square-o'
            },{
                text : '角色授权资源关系',
                click : role_object_resource,
                icon : 'fa-pencil-square-o'
            }];
		}
		BIONE.loadToolbar(grid, btns, function() { });
	}
	
	function QYBZRender(rowdata) {
		if (rowdata.roleSts == '1') {
			return "启用";
		}
		if (rowdata.roleSts == '0') {
			return "停用";
		} else {
			return rowdata.roleSts;
		}
	}

	//角色添加函数
	function f_open_add() {

		BIONE.commonOpenLargeDialog("角色添加", "roleAddWin",
				"${ctx}/bione/admin/role/new");

	}

	function f_open_update() {
		achieveIds();
		
		if(ids.length == 1){
			BIONE.commonOpenLargeDialog("角色修改", "roleModifyWin","${ctx}/bione/admin/role/" + ids[0].roleId + "/edit");
		}else if(ids.length>1){
			BIONE.tip("只能选择一行进行修改");
		}else{
			BIONE.tip("请选择需要修改的角色信息");
			return;
		}
		
		var row = grid.getSelectedRow();
		if (!row) {
			BIONE.tip('请选择行');
			return;
		}
	}

	//角色授权用户关系
    function role_object_auth(){
        achieveIds();
        if (ids.length == 1) {
            BIONE.commonOpenLargeDialog("查看角色授权用户关系", "roleModifyWin","${ctx}/bione/admin/role/" + ids[0].roleId + "/roleUserRel");
        } else if (ids.length > 1) {
            BIONE.tip('只能选择一条记录');
        } else {
            BIONE.tip('请选择记录');
        }
    }

    //角色授权资源关系
    function role_object_resource(){
        achieveIds();
        if (ids.length == 1) {
            BIONE.commonOpenLargeDialog("查看角色授权资源关系", "roleModifyWin","${ctx}/bione/admin/role/" + ids[0].roleId + "/roleResource");
        } else if (ids.length > 1) {
            BIONE.tip('只能选择一条记录');
        } else {
            BIONE.tip('请选择记录');
        }
    }
	
	function f_delete() {
		var selectedRow = grid.getSelecteds();
		if(selectedRow.length == 0){
			BIONE.tip('请选择行');
			return;
		}
		
		if (isSuperUser) {
			$.ligerDialog.confirm('确实要级联删除这' + selectedRow.length + '条记录吗!',
					function(yes) {
						// 先判断是否当前用户所创建的角色
						var length = selectedRow.length;
						var f_flag = false;
						for (var ii = 0; ii < length; ii++) {
							var lastUpdateUser = selectedRow[ii].lastUpdateUser;
							if (userId != lastUpdateUser) {
								f_flag = true;
							}
						}
						var type = false;
						if (yes) {
							type = true; // 级联删除
						}
						
						if (f_flag && type) {
							BIONE.tip('级联删除,只能选中当前用户创建的角色!');
							return;
						}

						deleteFun(length, selectedRow, type);
				});
			return;
		}
		
		$.ligerDialog.confirm('确实要删除这' + selectedRow.length + '条记录吗!',
				function(yes) {
					var length = selectedRow.length;
					if (yes) {
						deleteFun(length, selectedRow, false);
					}
				});
	}
	
	function deleteFun(length, selectedRow, type) {
		var ids = "";
		for ( var i = 0; i < length; i++) {
			if (ids != "") {
				ids += ",";
			}
			ids += selectedRow[i].roleId;
		}
		$.ajax({
			type : "POST",
			url : '${ctx}/bione/admin/role/destroyOwn.json',
			dataType : "json",
			type : "post",
			data : {
				"ids" : ids,
				"type" : type
			},
			success : function(result) {
				if (result) {
					var str = result.msg;
					if (str == "1") {
						BIONE.tip("当前选中角色被用户引用,不能删除!");
					} else if (str == "2") {
						BIONE.tip("当前选中角色与资源绑定，不能删除!");
					} else if (str == "0") {
						BIONE.tip('删除成功');
						grid.loadData();
					}
				} else {
					BIONE.tip('删除失败,请联系管理员!');
				}
			}
		});
	}
	
	function achieveIds() {
		ids = [];
		var rows = grid.getSelectedRows();
		for(var i in rows) {
			ids.push(rows[i]);
		}
	}
	
	
	//加载业务类型
	function initBusiType(){
		$.ajax({
			async :false,
			type : "POST",
			url : "${ctx}/report/frame/datashow/idx/busiTypeList.json",
			dataType : 'json',
			success: function(data) {
				if(data){
					for(var i = 0; i < data.length ; i++){
						busiTypeMap.set(data[i].id, data[i].text + "角色");
					}
				}
			},
			error: function() {
				top.BIONE.tip('加载失败');
			}
		});
	}
</script>
</head>
<body>
</body>
</html>