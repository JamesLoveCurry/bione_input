<%@ page language="java" pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<html>
<head>
<meta name="decorator" content="/template/template8.jsp">
<script type="text/javascript">

	var rptId = "${rptId}";
	var grid;
	var rptUpgradeMap = new Map();
	
	$(function() {
		initRptUpgradeType();
		// 初始化列表
		initGrid();
	});
	
	function initGrid(){
		grid = $("#maingrid").ligerGrid({
			width : "100%",
			height : "99%",
			columns : [ {
				display : "版本号",
				name : "verId",
				width : "5%",
				align: "center",
				render : function(data , row , context , it){
					if(data.verId
							&& data.verId != ""){
						return "V"+data.verId;
					}
				}
			},{
				display : "版本名称",
				name : "sysTemName",
				width : "15%",
				align: "center"
			},{
				display : "升级概况",
				name : "templateNm",
				width : "10%",
				align: "center",
				render : function(data, row, context, it) {
					return rptUpgradeMap.get(context);
				}
			},{
				display : "版本开始时间",
				name : "verStartDate",
				width : "20%",
				align: "center",
				type : 'text',
				render : function(data , row , context , it){
					var date = data.verStartDate;
					if(date.length == 8){
						var str = date.substr(0,4) + "-" + date.substr(4,2) + "-" + date.substr(6,2);
						return str;
					}else{
						return date;
					}
				}
			},{
				display : "版本结束时间",
				name : "verEndDate",
				width : "20%",
				align: "center",
				type : 'text',
				render : function(data , row , context , it){
					var date = data.verEndDate;
					if(date.length == 8){
						var str = date.substr(0,4) + "-" + date.substr(4,2) + "-" + date.substr(6,2);
						return str;
					}else{
						return date;
					}
				}
			},{
				display:"操作",
				width:"20%",
				render : function(data , row , context , it){
 					if("29991231" == data.verEndDate){
						return "<a href='javascript:ver_detail("+row+");'>查看</a>"+
						   "&nbsp<a href='javascript:deleteVer("+row+");'>删除</a>";
					}
					return "<a href='javascript:ver_detail("+row+");'>查看</a>";
				}
			} ],
			checkbox : false,
			rownumbers : true,
			alternatingRow : true,//附加奇偶行效果行
			colDraggable : false,
			selectRowButtonOnly : true ,
			dataAction : 'server',//从后台获取数据
			method : 'post',
			url : "${ctx}/report/frame/design/cfg/rptVersionView.json",
			parms:{
				rptId : rptId
			}
		});
	}
	
	function ver_detail(row){
		var selRow  = null;
		if(typeof row != "undefined"
				&& row != null){
			selRow = grid.getRow(row);
			grid.select(row);
		}else{
			selRow = grid.getSelectedRow();
		}
		if(selRow != null){
			var modifyUrl = "${ctx}/report/frame/design/cfg/frsindex/edit/frame?datetime="+new Date().getTime();
			modifyUrl += ("&rptId="+selRow.rptId+"&templateId="+selRow.templateId+"&templateType="+selRow.templateType + "&verId="+selRow.verId + "&canEdit=N");
			top.BIONE.commonOpenDialog("报表维护" , "rptEdit" , $(top).width()*0.95 , $(top).height()*0.95 , modifyUrl);
		} 
	}
	
	function deleteVer(row){
		var selRow  = null;
		if(typeof row != "undefined"
				&& row != null){
			selRow = grid.getRow(row);
			grid.select(row);
		}else{
			selRow = grid.getSelectedRow();
		}
		if(selRow != null){
			if(grid.getData().length > 1){
				window.parent.$.ligerDialog.confirm('确实要删除当前报表的这个版本记录吗!',
						function(yes) {
							if (yes) {
								$.ajax({
									type : "POST",
									url : "${ctx}/report/frame/design/cfg/deleteVer?rptId="+selRow.rptId+"&templateId="+selRow.templateId+"&templateType="+selRow.templateType + "&verId="+selRow.verId,
									dataType : 'json',
									beforeSend : function() {
					 					parent.BIONE.loading = true;
					 					parent.BIONE.showLoading("正在操作中...");
					 				},
					 				complete : function() {
					 					parent.BIONE.loading = false;
					 					parent.BIONE.hideLoading();
					 				},
									success : function(result) {
										if (result.msg) {
											if(result.msg=='1'){
												grid.loadData();
												parent.mainTab.reload("topTab");
												BIONE.tip("删除成功");
											}else{
												BIONE.tip(result.msg);
											}
										} else {
											BIONE.tip("删除失败");
										}
									}
								});
							}
						});
			}else{
				BIONE.tip("当前报表只有一个版本，无法删除");
			}
		}
	}
	
	function initRptUpgradeType(){
		$.ajax({
			async :false,
			type : "POST",
			url : "${ctx}/report/frame/design/cfg/getRptUpgradeList",
			dataType : 'json',
			success: function(data) {
				if(data){
					for(var i = 0; i < data.length ; i++){
						rptUpgradeMap.set(data[i].id, data[i].text);
					}
				}
			},
			error: function() {
				top.BIONE.tip('保存失败');
			}
		});
	}
</script>
</head>
<body>
</body>
</html>