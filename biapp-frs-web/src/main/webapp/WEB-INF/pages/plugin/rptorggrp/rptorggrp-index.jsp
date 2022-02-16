<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<html>
<head>
<meta name="decorator" content="/template/template1.jsp">
<script type="text/javascript">
	var dialog;

	$(function() {
		$("#search").ligerForm({
			fields : [{
				display : "机构集名称",
					name : "collectionNameId",
				newline : true,
				type : "text",
				attr : {
					field : "grpNm",
					op : "like"
				}
			}]
		});

		grid = $("#maingrid").ligerGrid({
			height : '100%',
			width : '100%',
			align : 'center',
			columns : [  {
				display : '机构集名称',
				name : 'grpNm',
				width : '40%',
				minWidth : 60
			}, {
				display : '创建机构',
				name : 'createOrgNm',
				width : '30%',
				minWidth : 60
			},{
				display : '是否启用',
				name : 'isUse',
				width : '20%',
				minWidth : 60,
				render : function(row){
					return renderHandler(row);
				}
			} 
			],
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
								var grpId = dataObj.$input.attr("grpId");
								var rowId = dataObj.$input.attr("rowId");
								if(grpId
										&& rowId){
									changeSts(grpId , 'test' , rowId);
								}
						    },
						    'turnoff.rcSwitcher': function( e, dataObj ){
						    	var grpId = dataObj.$input.attr("grpId");
						    	var rowId = dataObj.$input.attr("rowId");
								if(grpId
										&& rowId){
									changeSts(grpId , null , rowId);
								}
						    }			
						});
					}
				}
			},
			checkbox : true,
			usePager : true,
			isScroll : false,
			rownumbers : true,
			alternatingRow : true,//附加奇偶行效果行
			colDraggable : true,
			dataAction : 'server',//从后台获取数据
			url : "${ctx}/rpt/frame/rptorggrp/loaddata?orgId=1",
			usePager : true, //服务器分页
			sortName : 'b.orgNo ,a.grpNm',//第一次默认排序的字段
			sortOrder : 'asc', //排序的方式
			pageParmName : 'page',
			pagesizeParmName : 'pagesize',
			toolbar : {}
		});

		var btns = [ {
			text : '增加',
			click : create,
			icon : 'add',
			operNo : 'user_add'
		}, {
			text : '修改',
			click : edit,
			icon : 'modify',
			operNo : 'user_modify'
		}, {
			text : '删除',
			click : deleteBatch,
			icon : 'delete',
			operNo : 'user_delete'
						}
		,{
			text : '配置机构',
			click : config,
			icon : 'config'
		}
		];

		BIONE.loadToolbar(grid, btns, function() {
		});
		BIONE.addSearchButtons("#search", grid, "#searchbtn");
	});
	function create(){
		BIONE.commonOpenIconDialog("机构集信息录入", "new_set","${ctx}/rpt/frame/rptorggrp/newset", 'new_set');
	};
	
	function renderHandler(row){
		if ($.browser.msie && parseInt($.browser.version, 10) < 9) {
			if (row.isUse != null){
				return "启用"
			} else {
				return "停用";
			}
		}else{
			var html = "<input class='rcswitcher' type='checkbox' name='check"+row.grpId+"' grpId='"+row.grpId+"' rowId='"+row.__id+"' ";
			if(row.isUse !=null){
				html += "checked";
			}
			html += " />";
			return html;
		}
	}
	
	// 更改报表状态
	function changeSts(grpId , sts , rowId){
		$.ajax({
			cache : false,
			async : true,
			url : '${ctx}/rpt/frame/rptorggrp/changeGrpSts',
			dataType : 'json',
			type : "post",
			data : {
				grpId : grpId,
				sts : sts
			},
			success : function(result){
				if(rowId
						&& grid){
					var row = grid.getRow(rowId);
					if(row){
						row.isUse = sts;
					}
				}
			},
			error:function(){
			}
		});	
	}
	
	//修改记录方法
	function edit(){
		var rows=grid.getSelectedRows();
		if(rows.length==1){
			var groupId=rows[0].grpId;
			BIONE.commonOpenIconDialog("机构集信息修改", "new_set","${ctx}/rpt/frame/rptorggrp/editset?groupId="+groupId, 'edit_set');
		}
		else
			BIONE.tip("请选择一条记录");
	};
	
	//删除记录方法
	function deleteBatch(){
		var rows=grid.getSelectedRows();
		if(rows.length>=1){
			var ids="";
			for(var i=0;i<rows.length;i++){
				if(rows[i].createOrg == "${orgNo}")
					ids+=rows[i].grpId+",";
			}
			ids=ids.substring(0,ids.length-1);
			$.ligerDialog.confirm("您确定删除记录吗?",function(data){
				if(data){
					$.ajax({
						cache : false,
						async : true,
						url : "${ctx}/rpt/frame/rptorggrp/delgroup?groupId="+ids,
						type : 'POST',
						success : function(res){
							if(res){
								BIONE.tip("删除成功");
							}
							else{
								BIONE.tip("删除完毕，保留正在被其他机构启用的机构组");
							}
							grid.loadData();
						},
						error : function (result,b){
							BIONE.tip("删除失败"+result.status);
						}
					});
				}
			});
		}
		else{
			BIONE.tip("请选择需要删除的记录");
		}
	};

	function config(){
		var rows=grid.getSelectedRows();
		if(rows.length==1){
			var groupId=rows[0].grpId;
			var orgtype=rows[0].orgType;
			if(rows[0].createOrg =="${orgNo}")	
				BIONE.commonOpenDialog("配置机构信息","config_obj",320,400,"${ctx}/rpt/frame/rptorggrp/config?orgType="+orgtype+"&grpId="+groupId,"config_obj");
			else
				BIONE.tip("非本机构配置的机构组无法重新配置机构");
		}
		else
			BIONE.tip("请选择一条记录");
		}
</script>
</head>
<body>
</body>
</html>