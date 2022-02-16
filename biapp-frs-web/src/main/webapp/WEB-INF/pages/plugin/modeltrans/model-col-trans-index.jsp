<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<html>
<meta name="decorator" content="/template/template1.jsp">
<head>
<!-- 基础的JS和CSS文件 -->
<script type="text/javascript">
	var grid;
	$(function() {
		initSearchForm();
		initGrid();
		BIONE.loadToolbar(grid, [{
			text : '新增',
			click : add,
			icon : 'add'
		},{
			text : '修改',
			click : modify,
			icon : 'modify'
		},{
			text : '删除',
			click : remove,
			icon : 'delete'
		},{
			icon:'database',
			text:'数据处理',
			menu:{
				width:90,
				items:[{
					icon : 'export',
					text : '配置导出',
					click : configExport
				},{
					line : true
				},{
					icon : 'import',
					text : '配置导入',
					click : configImport
				}]
			}
		}]);
		BIONE.addSearchButtons("#search", grid, "#searchbtn");
	});
	
	function configExport(){
/* 		var type = 'Trans';
		var ids = "";
		var notice = "";
		if(rows.length == 0){
			notice = "全量导出，是否继续？";
		}else{
			notice = "勾选导出，是否继续？";
			var rows=grid.getSelectedRows()
			for ( var i = 0; i < rows.length; i++) {
				ids += rows[i].setId + ",";
			}
		}
		$.ligerDialog.confirm(notice, function(yes) {
			if (yes) {
				$.ajax({
					async:true,
					type:"POST",
					dataType:"json",
					url:"${ctx}/report/frame/wizard/download",
					data:{type:type},//,ids:themeId
					beforeSend : function(a, b, c) {
						BIONE.showLoading('正在导出数据中...');
					},
					success:function(data){
						BIONE.hideLoading();
						if(data.fileName==""){
							BIONE.tip('导出异常');
							return;
						}
						var src='${ctx}/report/frame/wizard/export?type='+type+'&fileName='+data.fileName;//ids='+id导出成功后的excell文件地址
						window["downdload"] = $('<iframe id="download"  style="display: none;"/>');//下载文件显示框
						$('body').append(downdload);//添加文件显示框在body的下方
						downdload.attr('src', src);//给下载文件显示框加上文件地址链接
					},
					error : function(result) {
						BIONE.hideLoading();
					}
				});
			}
		}); */
	}
	
	function configImport(){
		BIONE.commonOpenDialog("模型转换配置信息导入", "importWin", 600, 480,
		"${ctx}/report/frame/wizard?type=Trans");
	}
	
	function add(){
		BIONE.commonOpenDialog("新增转换配置","addTransDialog",950,500,
				"${ctx}/rpt/frame/modelcoltrans/addtrans");
	}
	
	function modify(){
		var rows = grid.getSelectedRows();
		if(rows.length == 0){
			BIONE.tip("请选择一条记录");
			return;
		}
		if(rows.length > 1){
			BIONE.tip("只能选择一条记录");
			return;
		}
		var setId = rows[0].setId;
		var srcSetId = rows[0].srcSetId;
		BIONE.commonOpenDialog("修改转换配置","editTransDialog",950,500,
		"${ctx}/rpt/frame/modelcoltrans/edittrans?setId="+setId+"&srcSetId="+srcSetId);
	}
	
	function remove(){
		var rows = grid.getSelectedRows();
		if(rows.length == 0){
			BIONE.tip("请至少选择一条记录");
			return;
		}
		var ids = [];
		for(var i in rows){
			ids.push(rows[i].setId);
		}
		$.ligerDialog.confirm('您确定删除选中记录么？', function(yes) {
			if (yes) {
				$.ajax({
				    url:"${ctx}/rpt/frame/modelcoltrans/deltrans",  //请求的url地址
				    dataType:"json",   //返回格式为json
				    async:false,
				    data:{"ids":ids.join(",")},    //参数值
				    type:"GET",   //请求方式
				    success:function(data){
				    	if(data.msg == "success"){
				    		grid.reload();
				    		BIONE.tip('删除成功');
				    	}else{
				    		BIONE.tip('删除失败');
				    	}
				    },
				    error:function(){
						BIONE.tip('请求出错');
				    }
				});
			}
		});
	}
	
	function initSearchForm(){
		$("#search").ligerForm({
			fields : [{
				display : '模型英文名',
				name : "tableEnNm",
				newline : true,
				type : "text",
				cssClass : "field",
				attr : {
					field : 'info.tableEnNm',
					op : "like"
				}
			},{
				display : '模型中文名',
				name : "setNm",
				newline : false,
				type : "text",
				cssClass : "field",
				attr : {
					field : 'info.setNm',
					op : "like"
				}
			}]
		});
	};
	
	function initGrid(){
		grid = $("#maingrid").ligerGrid({
			columns : [ {
				display : '模型英文名称',
				name : 'tableEnNm',
				width : "19%",
				align : 'left'
			},{
				display : '模型中文名称',
				name : 'setNm',
				width : "19%",
				align : 'left'
			},{
				display : '来源模型英文名称',
				name : 'srcTableEnNm',
				width : "19%",
				align : 'left'
			},{
				display : '来源模型中文名称',
				name : 'srcSetNm',
				width : "19%",
				align : 'left'
			},{
				display : '转换WHERE条件',
				name : 'srcDataFilterCond',
				width : "19%",
				align : 'left'
			}],
			checkbox : true,
			dataAction : 'server', //从后台获取数据
			usePager : true, //服务器分页
			alternatingRow : true, //附加奇偶行效果行
			colDraggable : true,
			url : "${ctx}/rpt/frame/modelcoltrans/list.json",
			sortName : 'info.tableEnNm',//第一次默认排序的字段
			sortOrder : 'desc', //排序的方式
			rownumbers : true,
			width:'100%',
			toolbar : {}
		});
	}
	
	function reload(){
		grid.reload();
	}
</script>
</head>
<body>
</body>
</html>