<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<html>
<head>
<meta name="decorator" content="/template/template1_BS.jsp">
<script type="text/javascript">
	var grid, btns, url, ids = [], user = [];
	var userId;
	var isSuperUser = '${isSuperUser}';
	var isManager = '${isManager}';
    //刷新方法
    function reload() {
        grid.reload();
    }

	//初始化函数
	$(function() {
		url = "${ctx}/bione/admin/user/list.json";
		initGrid();
		searchForm();
		initButtons();
		BIONE.addSearchButtons("#search", grid, "#searchbtn");
	});  

	//搜索表单应用ligerui样式
	function searchForm() {
		$("#search").ligerForm({
			fields : [ {
				display : "用户帐号",
				name : "userNo",
				type : "text",
				cssClass : "field",
				attr : {
					op : "=",
					field : "usr.user_No"
				}
			}, {
				display : "用户名称",
				name : "userName",
				type : "text",
				newline : false,
				cssClass : "field",
				attr : {
					op : "like",
					field : "usr.user_Name"
				}
			}, {
				display : "机构名称",
				name : "orgNo",
				type : "text",
				newline : false,
				cssClass : "field",
				attr : {
					op : "like",
					field : "org.org_Name"
				}
			}, {
				display : "用户状态",
				name : "userSts",
				type : "select",
				newline : false,
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

	//初始化grid
	function initGrid() {
		grid = $("#maingrid").ligerGrid({
			width : '100%',
			height : '99%',
			columns : [
					{
						display : '用户帐号',
						name : 'userNo',
						align : 'left',
						width : '10%',
						sortname : "usr.user_no"
					},
					{
						display : '用户名称',
						name : 'userName',
						align : 'center',
						width : '14%',
						sortname : "usr.user_name"
					},
					{
						display : '机构',
						name : 'orgName',
						align : 'center',
						width : '13%',
						sortname : "org.org_Name"/* ,
						render : QYBZRenderOrg */
					},
					{
						display : '条线',
						name : 'deptName',
						align : 'center',
						width : '12%',
					},
					{
						display : '邮箱',
						name : 'email',
						align : 'center',
						width : '12%',
						sortname : "usr.email"
					},
                    {
                        display : '身份证号',
                        name : 'idCard',
                        align : 'center',
                        width : '12%'
                    },
                    {
						display : '是否管理者',
						name : 'isManager',
						align : 'center',
						width : '10%',
						sortname : "usr.is_manager",
						render : function(row){
							return isManagerRenderHandler(row);
						}
					},
					{
						display : '用户状态',
						name : 'userSts',
						align : 'center',
						width : '10%',
						sortname : "usr.user_sts",
						render : function(row){
							return renderHandler(row);
						}
					},
					{
						display : '最后密码修改时间',
						name : 'lastPwdUpdateTime',
						type : 'date',
						format : 'yyyy-MM-dd hh:mm:ss',
						align : 'center',
						width : '12%',
						sortname : "usr.last_pwd_update_time"
					} ],
			checkbox : true,
			usePager : true,
			isScroll : true,
			rownumbers : true,
			alternatingRow : true,//附加奇偶行效果行
			colDraggable : true,
			dataAction : 'server',//从后台获取数据
			method : 'post',
			url : url,
			sortName : 'user_No', //第一次默认排序的字段
			sortOrder : 'asc',
			toolbar : {} ,
			onAfterShowData : function(aa){
				if($("#userSts .rcswitcher").length > 0){
					$("#userSts .rcswitcher").rcSwitcher({
						onText: '启用',
						offText: '停用',
						height:16.5,
						autoFontSize : true
					}).on({
						'turnon.rcSwitcher': function( e, dataObj ){
							var userId = dataObj.$input.attr("userid");
							var rowId = dataObj.$input.attr("rowid");
							if(userId
									&& rowId){
								changeSts(userId , '1' , rowId);
							}
					    },
					    'turnoff.rcSwitcher': function( e, dataObj ){
					    	var userId = dataObj.$input.attr("userid");
					    	var rowId = dataObj.$input.attr("rowid");
							if(userId
									&& rowId){
								changeSts(userId , '0' , rowId);
							}
					    }			
					});
				}
				
				if($("#isManager .rcswitcher").length > 0){
					$("#isManager .rcswitcher").rcSwitcher({
						onText: '是',
						offText: '否',
						height:16.5,
						autoFontSize : true
					}).on({
						'turnon.rcSwitcher': function( e, dataObj ){
							var userId = dataObj.$input.attr("userid");
							var rowId = dataObj.$input.attr("rowid");
							if(userId && rowId){
								changeManager(userId , 'Y' , rowId);
							}
					    },
					    'turnoff.rcSwitcher': function( e, dataObj ){
					    	var userId = dataObj.$input.attr("userid");
					    	var rowId = dataObj.$input.attr("rowid");
							if(userId && rowId){
								changeManager(userId , 'N' , rowId);
							}
					    }			
					});
				}
			} 
		});
	}

	// 更改用户状态
	function changeSts(userId , sts , rowId){
		$.ajax({
			cache : false,
			async : true,
			url : '${ctx}/bione/admin/user/changeUserSts',
			dataType : 'json',
			type : "post",
			data : {
				userId : userId,
				sts : sts
			},
			success : function(result){
				if(rowId && grid){
					var row = grid.getRow(rowId);
					if(row){
						row.userSts = sts;
					}
				}
			}
		});	
	}
	
	//初始化按钮
	function initButtons() {
		if("true" != isSuperUser && 'Y' != isManager){
			btns = [];
		}else if("true" == isSuperUser){
			btns = [
			/*动态维护功能按钮*/
			{
				text : '增加',
				click : user_add_dynamic,
				icon : 'fa-plus',
				operNo : 'user_add'
			},{
				text : '修改',
				click : user_modify_dynamic,
				icon : 'fa-pencil-square-o',
				operNo : 'user_modify'
			},{
				text : '删除',
				click : user_delete_dynamic,
				icon : 'fa-trash-o',
				operNo : 'user_delete'
			},{
				text : '密码重置',
				click : user_pwd,
				icon : 'fa-pencil-square-o',
				operNo : 'user_pwd'
			},{
	            text : "用户导入导出",
	            icon : "fa-pencil-square-o",
	            operNo : "excel_modify",
	            menu : {
	                items : [ {
	                    icon : 'import',
	                    text : '用户导入',
	                    click : user_upload
	                }, {
	                    icon : 'export',
	                    text : '用户导出',
	                    click : user_download
	                } ]
	            }
	        },{
             	text : '用户授权对象关系',
             	click : user_object_auth,
             	icon : 'fa-pencil-square-o'
             },{
              	text : '授权对象资源分配',
              	click : user_object_resource,
              	icon : 'fa-pencil-square-o'
              	}/*,{
				text : '用户解锁',
				click : user_unlock,
				icon : 'lock',
				operNo : 'user_unlock'
			} */];
		}else{
			btns = [
			/*动态维护功能按钮*/
			{
				text : '增加',
				click : user_add_dynamic,
				icon : 'fa-plus',
				operNo : 'user_add'
			},{
				text : '修改',
				click : user_modify_dynamic,
				icon : 'fa-pencil-square-o',
				operNo : 'user_modify'
			},{
				text : '删除',
				click : user_delete_dynamic,
				icon : 'fa-trash-o',
				operNo : 'user_delete'
			},{
				text : '密码重置',
				click : user_pwd,
				icon : 'fa-pencil-square-o',
				operNo : 'user_pwd'
			},{
                text : '用户授权对象关系',
                click : user_object_auth,
                icon : 'fa-pencil-square-o'
            },{
                text : '授权对象资源分配',
                click : user_object_resource,
                icon : 'fa-pencil-square-o'
            }];
		}
		BIONE.loadToolbar(grid, btns, function() { });
	}
	
	// 获取机构名称
	function QYBZRenderOrg(rowdata) {
		return BIONE.paramTransformer(rowdata.orgNo,
				'${ctx}/bione/admin/user/getOrgName');
	}
	
	// 获取条线名称
	function QYBZRenderDept(rowdata) {
		return BIONE.paramTransformer(rowdata.deptNo, 
				'${ctx}/bione/admin/user/getDeptName');
	}
	
	// 状态显示,停/启用等
	function renderHandler(row){
		if ($.browser.msie && parseInt($.browser.version, 10) < 9) {
			if (1== row.userSts){
				return "启用"
			} else {
				return "停用";
			}
		}else{
			var html = "<div id='userSts'><input class='rcswitcher' type='checkbox' name='check"+row.userId+"' id='"+row.userId+"' userId='"+row.userId+"' rowId='"+row.__id+"' ";
			if(row.userSts == "1"){
				html += "checked";
			}
			html += " /></div>";
			return html;
		}
	}
	
	// 是否管理者,是/否等
	function isManagerRenderHandler(row){
		if ($.browser.msie && parseInt($.browser.version, 10) < 9) {
			if ('Y'== row.isManager){
				return "是"
			} else {
				return "否";
			}
		}else{
			var html = "<div id='isManager'><input class='rcswitcher' type='checkbox' name='check"+row.userId+"' id='"+row.userId+"' userId='"+row.userId+"' rowId='"+row.__id+"' ";
			if(row.isManager == "Y"){
				html += "checked";
			}
			html += " /></div>";
			return html;
		}
	}
	
	// 更改是否管理者
	function changeManager(userId , isManager , rowId){
		$.ajax({
			cache : false,
			async : true,
			url : '${ctx}/bione/admin/user/changeIsManager',
			dataType : 'json',
			type : "post",
			data : {
				userId : userId,
				isManager : isManager
			},
			success : function(result){
				if(rowId && grid){
					var row = grid.getRow(rowId);
					if(row){
						row.isManager = isManager;
					}
				}
			}
		});	
	}

	//重置用户密码，重置为123456
	function user_pwd(item) {
		achieveIds();
		if (ids.length == 1) {
			$.ligerDialog.confirm('确实要重置该用户的密码吗?', function(yes) {
				if (yes) {
					var flag = false;
					var msg = '密码重置失败！';
					$.ajax({
						async : false,
						type : "POST",
						url : "${ctx}/bione/admin/user/" + ids[0] + "/resetPwd",
						success : function(data) {
							if(data && data.msg=='S') {
								flag = true;
							} else if(data && data.msg!='F') {
								msg = data.msg;
							}
						}
					});
					if (flag == true) {
						BIONE.tip('密码重置成功！');
					} else {
						BIONE.tip(msg);
					}
				}
			});
		} else if (ids.length > 1) {
			BIONE.tip('只能选择一条记录');
		} else {
			BIONE.tip('请选择记录');
		}
	}
	
	//添加用户
	function user_add_dynamic(){
		BIONE.commonOpenLargeDialog('添加用户', 'userManage',
				'${ctx}/bione/admin/user/new');
	}
	
	//修改用户
	function user_modify_dynamic(){
		achieveIds();
		if (ids.length == 1) {
			if(user != "") {
				$.ligerDialog.error("用户: [ " + user.join(", ").toString() + " ] 不可修改!", "错误", function() {
					return false;
				});
			} else {
				userId = ids[0];
				BIONE.commonOpenLargeDialog('修改用户', 'userManage',
					'${ctx}/bione/admin/user/'+userId+'/edit');
			}
		} else if (ids.length > 1) {
			BIONE.tip('只能选择一条记录');
		} else {
			BIONE.tip('请选择记录');
		}
	}

	//删除用户
	function user_delete_dynamic(item) {
		achieveIds();
		if (ids.length > 0) {
			$.ligerDialog.confirm('确实要删除这些记录吗?', function(yes) {
				if(yes) {
					if (user != "") {
						$.ligerDialog.error("用户: [ "
								+ user.join(", ").toString() + " ] 不可删除!",
								"错误", function() {
									return false;
								});
					} else {
						$.ajax({
							async : false,
							type : "POST",
							url : "${ctx}/bione/admin/user/" + ids.join(","),
							success : function(result) {
								if (result) {
									var str = result.msg;
									if (str == "1") {
										BIONE.tip("当前选中用户与对象绑定,不能删除!");
									} else if (str == "2") {
										BIONE.tip("当前选中用户与资源绑定,不能删除!");
									} else if (str == "3") {
										BIONE.tip("当前选中用户是管理员,不能删除!");
									} else if (str == "0") {
										BIONE.tip('删除成功');
										grid.reload();
									}
								} else {
									BIONE.tip('删除失败,请联系管理员!');
								}
							}
						});
					}
				}
			});
		} else {
			BIONE.tip('请选择记录');
		}
	}

	//用户授权对象关系
    function user_object_auth(){
        achieveIds();
        if (ids.length == 1) {
            userId = ids[0];
            BIONE.commonOpenLargeDialog('查看用户与授权对象关系', 'roleModifyWin','${ctx}/bione/admin/user/'+userId+'/objectUserRel');
        } else if (ids.length > 1) {
            BIONE.tip('只能选择一条记录');
        } else {
            BIONE.tip('请选择记录');
        }
    }

    //授权对象资源分配
    function user_object_resource(){
        achieveIds();
        if (ids.length == 1) {
            userId = ids[0];
            BIONE.commonOpenDialog('查看授权对象资源分配', 'roleModifyWin', $("#center").width()-500,$("#center").height()-100,'${ctx}/bione/admin/user/'+userId+'/userResource');
        } else if (ids.length > 1) {
            BIONE.tip('只能选择一条记录');
        } else {
            BIONE.tip('请选择记录');
        }
    }
	
	//用户解锁
	function user_unlock() {
		achieveIds();
		if (ids.length == 1) {
			$.ligerDialog.confirm('确实要解锁该用户吗?', function(yes) {
				if (yes) {
					var flag = false;
					var msg = '解锁失败';
					$.ajax({
						async : false,
						type : "POST",
						url : "${ctx}/bione/admin/user/unlockUser?userId="+ids[0],
						success : function(data) {
							if(data && data.msg=='S') {
								flag = true;
							} else if(data && data.msg!='F') {
								msg = data.msg;
							}
						}
					});
					if (flag == true) {
						BIONE.tip('解锁成功');
						initGrid();
					} else {
						BIONE.tip(msg);
					}
				}
			});
		} else if (ids.length > 1) {
			BIONE.tip('只能选择一条记录');
		} else {
			BIONE.tip('请选择记录');
		}
	}

	//用户导入
	function user_upload() {
        BIONE.commonOpenDialog("数据导入", "importWin", 600, 480,"${ctx}/report/frame/wizard?type=User");
    }

	//用户导出
    function user_download() {
        var type = 'User';
        achieveIds();
        $.ajax({
            async: true,
            type: "POST",
            dataType: "json",
            url: "${ctx}/report/frame/wizard/download",
            data: {type: type, ids: ids.join(",")},
            beforeSend: function (a, b, c) {
                BIONE.showLoading('正在导出数据中...');
            },
            success: function (data) {
                BIONE.hideLoading();
                if (data.fileName == "") {
                    BIONE.tip('导出异常');
                    return;
                }
                var src = '${ctx}/report/frame/wizard/export?type=' + type + '&fileName=' + data.fileName;//导出成功后的excell文件地址
                window["downdload"] = $('<iframe id="download"  style="display: none;"/>');//下载文件显示框
                $('body').append(downdload);//添加文件显示框在body的下方
                downdload.attr('src', src);//给下载文件显示框加上文件地址链接
            },
            error: function (result) {
                BIONE.hideLoading();
            }
        });
    }

	//获取当前已勾选的用户记录
	function achieveIds() {
		ids = [];
		user = [];
		var rows = grid.getSelectedRows();
		$(rows).each(function() {
			ids.push(this.userId);
			if(this.isBuiltin == "1") 
				user.push(this.userNo);
		});
	}
</script>
</head>
<body>
</body>
</html>