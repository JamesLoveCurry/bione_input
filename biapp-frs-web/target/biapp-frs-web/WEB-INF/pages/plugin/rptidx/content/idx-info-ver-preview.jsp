<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<html>
<head>
<meta name="decorator" content="/template/template5.jsp">
<script type="text/javascript">
	//全局变量
	var grid;
	var indexNo  =  '${indexNo}';
	var defSrc  =  '${defSrc}';
	function deleteVerInfo(indexNo,indexVerId){
		if(grid.getData().length > 1){
			indexNo = indexNo +",";
			window.parent.$.ligerDialog.confirm('确实要删除当前指标的这个版本记录吗!',
					function(yes) {
						if (yes) {
							$.ajax({
								type : "POST",
								url : '${ctx}/report/frame/idx/idxInfoDel/' + indexNo+"?vid="+indexVerId,
								dataType : 'json',
								success : function(result) {
									if (result.msg) {
										if(result.msg=='1'){
											grid.loadData();
											BIONE.tip("删除成功");
											parent.frames["rptIdxCenterTabFrame"].grid.loadData();
										}else{
											BIONE.tip(result.msg);
										}
									} else {
										BIONE.tip("删除失败");
									}
								},
								beforeSend : function() {
				 					parent.BIONE.loading = true;
				 					parent.BIONE.showLoading("正在操作中...");
				 				},
				 				complete : function() {
				 					parent.BIONE.loading = false;
				 					parent.BIONE.hideLoading();
				 				}
							});
						}
					});
		}
		else{
			BIONE.tip("当前指标只有一个版本，无法删除");
		}
		
	}
	
	function showBusiInfo(indexNo,indexVerId,infoRights){
		dialog = window.parent.BIONE.commonOpenDialog("指标查看",
				"rptIdxInfoPreviewBox",$(parent.document).width(), $(parent.document).height(),
				"${ctx}/report/frame/idx/"+indexNo+"/show?d="+new Date().getTime()+"&indexVerId="+indexVerId+"&defSrc="+defSrc,null);
	}
	$(function() {
		var indxInfoObj = parent.indxInfoObj;
		//渲染grid
		ligerGrid();
		//渲染GRID
		function ligerGrid() {
			                   grid = $("#maingrid").ligerGrid({
			                    	toolbar : {},
			            			checkbox : false,
			            			columns : [{
			            				display : '版本号',
			            				name : 'id.indexVerId',
			            				width : "6%",
			            				align : 'center'
			            			},{
			            				display : '指标名称',
			            				name : 'indexNm',
			            				width : "16%",
			            				align : 'center'
			            			},  {
			            				display : '指标标识',
			            				name : 'id.indexNo',
			            				width : "20%",
			            				align : 'center'
			            			}, {
			            				display : '指标类型',
			            				name : 'indexType',
			            				width : "20%",
			            				align : 'center'
			            			}, {
			            				display : '有效期',
			            				name : 'startDate',
			            				width : "20%",
			            				align : 'center',
			            				render:function(row){
			            					if(row.startDate&&row.endDate){
			            						return    row.startDate+"--"+row.endDate;
			            					}
			            					return "";
			            				}
			            			}, {
			            				display : '操作',
			            				name : 'startDate',
			            				width : "14%",
			            				align : 'center',
			            				render:function(row){
			            					if("29991231" == row.endDate){
				            					return "<a href='javascript:void(0)'   onclick='showBusiInfo(\""+row.id.indexNo+"\",\""+row.id.indexVerId+"\",\""+row.infoRights+"\")'>查看</a>"+
			            						   "&nbsp<a href='javascript:void(0)'   onclick='deleteVerInfo(\""+row.id.indexNo+"\",\""+row.id.indexVerId+"\",\""+row.infoRights+"\")'>删除</a>";
			            					}
			            					return "<a href='javascript:void(0)'   onclick='showBusiInfo(\""+row.id.indexNo+"\",\""+row.id.indexVerId+"\",\""+row.infoRights+"\")'>查看</a>";
			            				}
			            			}],
			            			dataAction : 'server', //从后台获取数据
			            			usePager : false, //服务器分页
			            			alternatingRow : true, //附加奇偶行效果行
			            			colDraggable : true,
			            			url : "${ctx}/report/frame/idx/getIdxInfoVerList.json?indexNo="+indexNo,
			            			rownumbers : true,
			            			width : "99%"
							});
			//初始化按钮
			var btns = [ {
				text : "刷新",
				icon : "refresh",
				click : function() {
					grid.loadData();
				},
				operNo : "col_refresh"
			}];
			BIONE.loadToolbar(grid, btns, function() {}
			);
			grid.setHeight($("#center").height()-10);
		}

		//添加表单按钮
		var btns = [ {
			text : "关闭",
			onclick : function() {
				setTimeout('BIONE.closeDialog("idxInfoVerPreBox");',0);
			}
		}];
		BIONE.addFormButtons(btns);
	});
</script>
<title></title>
</head>
<body>
	<div id="template.center">
		<div id="maingrid"></div>
	</div>
</body>
</html>