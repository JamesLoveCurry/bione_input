<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<html>
<head>
<meta name="decorator" content="/template/template1_BS.jsp">
<!-- 基础的JS和CSS文件 -->
<script type="text/javascript">
	var grid;
	function initSearchForm() {
		$("#search").ligerForm({
			fields : [{
				display : '节点名称',
				name : "nodeNm",
				id : "nodeNm",
				newline : true,
				labelWidth : 100,
				width : 220,
				space : 30,
				type : "text",
				cssClass : "field",
				attr : {
					field : 'nodeNm',
					op : "like"
				}
			}, {
				display : '节点地址',
				name : "ipAddress",
				id : "ipAddress",
				newline : false,
				labelWidth : 100,
				width : 220,
				space : 30,
				type : "text",
				cssClass : "field",
				attr : {
					field : 'ipAddress',
					op : "like"
				}
			}, {
				display : '节点类型',
				name : "nodeTypeBox",
				newline : false,
				labelWidth : 100,
				width : 220,
				space : 30,
				type : "select",
				options : {
					data :[{
						"id"  :"01",
						"text":"计算节点"
						}, {
				        "id"  :"02",
				        "text":"查询节点"
				        }, {
				        "id"  :"03",
				        "text":"实时检核节点"
				        }, {
				        "id"  :"04",
				        "text":"实时机构汇总节点"
				        }, {
				        "id"  :"05",
				        "text":"批量检核节点"
				        }, {
				        "id"  :"06",
				        "text":"批量机构汇总节点"
				        }, {
					    "id"  :"07",
					    "text":"批量计算节点"
					    },{
					    "id"  :"11",
						"text":"引擎节点缓存1"
						},{
						"id"  :"12",
						"text":"引擎节点缓存2"
						}]
				},
				cssClass : "field",
				attr : {
					field : 'nodeType',
					op : "="
				}
			}]
		});
	};
	function initGrid() {
		grid = $("#maingrid").ligerGrid({
			toolbar : {},
			columns : [{
				display : '节点名称',
				name : 'nodeNm',
				width : "25%",
				align : 'center',
				render : function(row,index,val){
					return "<a style='color:blue' onclick='f_view(\""+row.nodeId+"\")'>"+val+"</a>"
				}
			}, {
				display : '节点类型',
				name : 'nodeType',
				width : "25%",
				align : 'center',
				render:function(row){
					switch(row.nodeType){
						case  "01":return "计算节点";
						case  "02":return "查询节点";
						case  "03":return "实时检核节点";
						case  "04":return "实时机构汇总节点";
						case  "05":return "批量检核节点";
						case  "06":return "批量机构汇总节点";
						case  "07":return "批量计算节点";
						case  "11":return "引擎节点缓存1";
						case  "12":return "引擎节点缓存2";
						default  :return "未知";
			        }
				}
			},  {
				display : '是否在线',
				name : 'nodeSts',
				width : "20%",
				align : 'center',
				type : "date",
				render:function(row){
					switch(row.nodeSts){
						case  "Y":return "在线";
						case  "N":return "离线";
					}
				}
			}],
			dataAction : 'server', 
			usePager : true, 
			url : "${ctx}/report/frame/engine/log/node/page",
			sortName : 'nodeNm',
			sortOrder : 'asc', 
			pageParmName : 'page',
			pagesizeParmName : 'pagesize',
			checkbox : true,
			rownumbers : true,
			width : '100%',
			height : '99%'
		});
	};
	function initToolBar() {
		var toolBars = [ {
			text : '增加',
			click : f_add,
			icon : 'fa-plus'
		},{
			text : '修改',
			click : f_update,
			icon : 'fa-pencil-square-o'
		},{
			text : '删除',
			click : f_delete,
			icon : 'fa-trash-o'
		}];
		BIONE.loadToolbar(grid, toolBars, function() {
		});
	    }
	$(function() {	
		initSearchForm();
		initGrid();
		initToolBar();
		BIONE.addSearchButtons("#search", grid, "#searchbtn");
	  });
	function f_add() {
		 BIONE.commonOpenDialog("新建引擎节点",
					"engineNodeDialog",600,350, 
					"${ctx}/report/frame/engine/log/node/new");
	}
	
	function f_update() {
		var rows = grid.getCheckedRows();
  		if(rows && rows.length < 1) {
  			BIONE.tip("请选择数据！");
  			return;
  		}else if(rows && rows.length == 1){
  			BIONE.commonOpenDialog("修改引擎节点",
  					"engineNodeDialog",600,350, 
  					"${ctx}/report/frame/engine/log/node/edit?nodeId="+rows[0].nodeId);
  		}else{
  			var nodeIds=rows[0].nodeId;
  			for(i=1;i<rows.length;i++){
  				nodeIds=nodeIds+","+rows[i].nodeId;//多个id用“，”拼接成一个字符串传给后台
  			}
  			BIONE.commonOpenDialog("批量修改引擎节点",
  					"engineNodeDialog",600,350, 
  					"${ctx}/report/frame/engine/log/node/multiple_edits?nodeIds="+nodeIds);
  		}
		
	}
	
	function f_view(nodeId){
		BIONE.commonOpenDialog("节点信息",
				"engineNodeDialog",700,440, 
				"${ctx}/report/frame/engine/log/node/view?nodeId="+nodeId);
	}

	function  f_delete(taskNo){
		var rows = grid.getCheckedRows();
  		if(rows && rows.length == 0) {
  			BIONE.tip("请选择一条数据！");
  			return;
  		}	
  		var id ="";
  		for(var i in rows){
  			id += rows[i].nodeId+",";
  		}
  		$.ligerDialog.confirm('您确定删除这条记录么？', function(yes) {
			if (yes) {
				$.ajax({
					cache : false,
					async : true,
					url : '${ctx}/report/frame/engine/log/node/' + id,
					type : "POST",
					beforeSend : function() {
						BIONE.loading = true;
						BIONE.showLoading("正在删除数据...");
					},
					complete : function() {
						BIONE.loading = false;
						BIONE.hideLoading();
					},
					success : function(data) {
						BIONE.tip('删除成功');
						grid.loadData();	
						
					},
					error : function(result, b) {
						BIONE.tip('发现系统错误 <BR>错误码：' + result.status);
					}
				});
			}
		});
	}
</script>
</head>
<body>
</body>
</html>