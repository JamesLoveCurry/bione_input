<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<html>
<meta name="decorator" content="/template/template1_BS.jsp">
<head>
<!-- 基础的JS和CSS文件 -->
<script type="text/javascript">
    var grid, ids;

    $(function() {
		initSearchForm();
		initGrid();
		initToolBar();
		BIONE.addSearchButtons("#search", grid, "#searchbtn");
    });

    function initSearchForm() {
		$("#search").ligerForm({
		    fields : [ {
				display : '对象组标识',
				name : "objgrpNo",
				newline : true,
				type : "text",
				cssClass : "field",
				attr : {
				    field : 'objgrpNo',
				    op : "like"
				}
			}, {
				display : '对象组名称',
				name : 'objgrpName',
				newline : false,
				type : "text",
				cssClass : "field",
				attr : {
				    field : "objgrpName",
				    op : "like"
				}
		    } ]
		});
    }
    function initGrid() {
		grid = $("#maingrid").ligerGrid({
		    width : '100%',
		    toolbar : {},
		    checkbox : true,
		    columns : [ {
				display : '对象组标识',
				name : 'objgrpNo',
				width : "22%",
				align : 'left'
		    }, {
				display : '对象组名称',
				name : 'objgrpName',
				width : "23%",
				align : 'left'
		    }, {
				display : '对象组状态',
				name : 'objgrpSts',
				width : "24%",
				render : function(row){
					return renderHandler(row);
				}
		    }, {
				display : '最后修改时间',
				name : 'lastUpdateTime',
				width : "25%",
				type : 'date'
		    } ],
		    dataAction : 'server', //从后台获取数据
		    usePager : true, //服务器分页
		    alternatingRow : true, //附加奇偶行效果行
		    colDraggable : true,
		    url : "${ctx}/bione/admin/authObjgrp/list.json",
		    sortName : 'objgrpId',//第一次默认排序的字段
		    sortOrder : 'asc', //排序的方式
		    rownumbers : true,
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
								var objgrpId = dataObj.$input.attr("objgrpId");
								var rowId = dataObj.$input.attr("rowid");
								if(objgrpId
										&& rowId){
									changeSts(objgrpId , '1' , rowId);
								}
						    },
						    'turnoff.rcSwitcher': function( e, dataObj ){
						    	var objgrpId = dataObj.$input.attr("objgrpId");
						    	var rowId = dataObj.$input.attr("rowid");
								if(objgrpId
										&& rowId){
									changeSts(objgrpId , '0' , rowId);
								}
						    }			
						});
					}
				}
			}
		});

    }

    function QYBZRender(rowdata) {
		if (rowdata.objgrpSts == '1') {
		    return "启用";
		}
		if (rowdata.objgrpSts == '0') {
		    return "停用";
		} else {
		    return rowdata.isAudit;
		}
    }

    function initToolBar() {
		var toolbars = [ {
		    text : '增加',
		    click : authAdd,
		    icon : 'fa-plus'
		}, {
		    text : '修改',
		    click : authEdit,
		    icon : 'fa-pencil-square-o'
		}, {
		    text : '删除',
		    click : authDelete,
		    icon : 'fa-trash-o'
		}, {
		    text : '对象组关系维护',
		    click : authManage,
		    icon : 'fa-tasks'
		} ];
		BIONE.loadToolbar(grid, toolbars, function() {
		});
    }

    function authAdd() {
		BIONE.commonOpenLargeDialog("对象组添加", "objgrpAddWin",
			"${ctx}/bione/admin/authObjgrp/new");
    }

    function authEdit() {
		achieveIds();
		if (ids.length == 1) {
		    BIONE.commonOpenLargeDialog("对象组修改", "objgrpModifyWin",
			    "${ctx}/bione/admin/authObjgrp/" + ids[0].objgrpId
				    + "/edit");
		} else if (ids.length > 1) {
		    BIONE.tip("只能选择一行进行配置");
		} else {
		    BIONE.tip("请选择需要修改的对象组信息");
		    return;
		}
    }
    function authDelete() {
		var checkedRole = grid.getSelectedRows();
		if (checkedRole.length == 0) {
		    BIONE.tip('请选择行');
		    return;
		}
		;
		var id = "";
		for ( var i = 0; i < checkedRole.length; i++) {
		    id += checkedRole[i].objgrpId + ",";
		}
		$.ligerDialog.confirm(
				'确实要删除这' + checkedRole.length + '条记录吗!',
				function(yes) {
				    if (yes) {
					$
						.ajax({
						    url : '${ctx}/bione/admin/authObjgrp/destroyOwn.json?t='
							    + new Date().getTime(),
						    dataType : "json",
						    type : "post",
						    data : {
							"delIds" : id
						    },
						    success : function() {
							BIONE.tip('删除成功');
							grid.loadData();
						    }
						});
				    }
				});
    }

    function authManage() {
		var row = grid.getSelectedRows();
		if (row.length == 0) {
		    BIONE.tip('未选择任何记录');
		    return;
		}
		if (row.length > 1) {
		    BIONE.tip('请选择单记录');
		    return;
		}
		var id = row[0].objgrpId;
		var logicSysNo = row[0].logicSysNo;
		BIONE.commonOpenLargeDialog("对象组关系维护", "objgrpModifyWin",
			"${ctx}/bione/admin/objgrpRelManage/objgrpRelManage?objgrpId="
				+ id + "&logicSysNo=" + logicSysNo);
    }

    function achieveIds() {
		ids = [];
		var rows = grid.getSelectedRows();
		for ( var i in rows) {
		    ids.push(rows[i]);
		}
    }
    
 // 更改报表状态
	function changeSts(objgrpId , sts , rowId){
		$.ajax({
			cache : false,
			async : true,
			url : '${ctx}/bione/admin/authObjgrp/changeGrpSts',
			dataType : 'json',
			type : "post",
			data : {
				objgrpId : objgrpId,
				sts : sts
			},
			success : function(result){
				if(rowId
						&& grid){
					var row = grid.getRow(rowId);
					if(row){
						row.objgrpSts = sts;
					}
				}
			}
		});	
	}
	
	function renderHandler(row){
		if ($.browser.msie && parseInt($.browser.version, 10) < 9) {
			if (1== row.objgrpSts){
				return "启用"
			} else {
				return "停用";
			}
		}else{
			var html = "<input class='rcswitcher' type='checkbox' name='check"+row.objgrpId+"' objgrpId='"+row.objgrpId+"' rowId='"+row.__id+"' ";
			if(row.objgrpSts == "1"){
				html += "checked";
			}
			html += " />";
			return html;
		}
	}
</script>
</head>
<body>
	
</body>
</html>