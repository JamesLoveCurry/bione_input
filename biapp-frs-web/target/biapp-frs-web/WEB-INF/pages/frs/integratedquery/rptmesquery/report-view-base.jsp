<%@ page language="java" pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<html>
<meta name="decorator" content="/template/template1_BS.jsp">
<head>
<!-- 基础的JS和CSS文件 -->
<script type="text/javascript">
    var dimTypeNo='${dimTypeNo}';
	var grid;
	var ids;
	var catalogId;
	function initSearchForm() {
		//搜索表单应用ligerui样式
		$("#search").ligerForm({
			fields : [ {
				display : '报表编号',
				name : "rptNo",
				newline : true,
				type : "text",
				attr : {
					field : 'rpt.rptNum',
					op : "="
				}
			}, {
				display : '报表名称',
				name : "rptNm",
				newline : false,
				type : "text",
				cssClass : "field",
				attr : {
					field : 'rpt.rptNm',
					op : "like"
				}
			} ,{
				display : '报表频率',
				name: 'rptCycle',
				comboboxName:'rptCycleBox',
				type: 'select',
				newline: true,
				options:{
					data: [{ 
						id: '01',
						text: '日' 
					},{
						id: '02',
						text: '月' 
					},{
						id: '03',
						text: '季' 
					},{
						id: '04',
						text: '年' 
					},{
						id: '10',
						text: '周' 
					},{
						id: '11',
						text: '旬' 
					},{
						id: '12',
						text: '半年' 
					}]
				},
				attr : {
					field : 'rpt.rptCycle',
					op : "="
				}
			} /* ,{
				display: '责任部门',
				name: 'dutyDept',
				type: 'text',
				newline: false,
				attr : {
					field : 'bank.dutyDept',
					op : "like"
				}
			} */]
		});
	};
	function alertRptIndex(rptId,rptNm,rptType,infoRights) {
		//if(infoRights=="Y")
			parent.addTabInfo(rptId,rptNm,rptType);
		//else
			//BIONE.tip("该用户无权限查看此报表");
	}
	
	function initGrid() {
		grid = $("#maingrid").ligerGrid({
			toolbar : {},
			checkbox :true,
			columns : [ /* {
				display : '报表编号',
				name : 'rptNum',
				width : "10%",
				align : 'center'
			}, */ {
				display : '报表名称',
				name : 'rptNm',
				width : "55%",
				align : 'left',
				render : function(row) {
					return "<a href='javascript:void(0)' class='link' onclick='alertRptIndex(\""+ row.rptId+ "\",\""+row.rptNm+"\",\""+row.rptType+"\",\""+row.infoRights+"\")'>"+ row.rptNm + "</a>";
				}
			},{
				display : '报表频率',
				name : 'rptCycle',
				width : "20%",
				align : 'center',
				render:function(a,b,c){
					switch(c){
					case '01':
						return '日';
					case '02':
						return '月';
					case '03':
						return '季';
					case '04':
						return '年';
					case '10':
						return '周';
					case '11':
						return '旬';
					case '12':
						return '半年';
					}
				}
			},/* {
				display : '责任部门',
				name : 'dutyDept',
				width : "15%",
				align : 'center'
			}, */{
				display : '报表状态',
				name : 'rptSts',
				width : "15%",
				align : 'center',
				render: function(a,b,c){
					if(c=="Y")
						return "启用";
					else
						return "停用";
				}
			}/* ,{
				display : '报表描述',
				name : 'rptDesc',
				width : "20%",
				align : 'center'
			} */],
			checkbox: false,
			dataAction : 'server', //从后台获取数据
			usePager : true, //服务器分页
			alternatingRow : true, //附加奇偶行效果行
			colDraggable : true,
			sortName: "rankOrder",
			url : "${ctx}/frs/integratedquery/rptmesquery/info/rptList",
			rownumbers : true,
			width : '100%',
			height : '99%',
			delayLoad :true
		});
		if(window.parent.rptNm!=null&&window.parent.rptNm!=""){
			$("#rptNm").val(window.parent.rptNm);
			grid.set('parme',{rptNm:window.parent.rptNm});
		}
		grid.loadData();
	};
	function initToolBar() {
		var toolBars = [ {
			text : '查看',
			click : f_open_view,
			icon : 'fa-book'
		}];
		if("${state}"!="catalog"){
			BIONE.loadToolbar(grid, toolBars, function() {
			});
		}
		else{
			$(".l-panel-topbar").hide();
		}
		
	}
	function checkRptPri(rptId){
		 
	}
	$(function() {
		parent.baseInfo=window;
		initSearchForm();
		initGrid();
		initToolBar();
		BIONE.addSearchButtons("#search", grid, "#searchbtn");
		$("input[name='dimTypeNo']").val(dimTypeNo);
		if($("#rptNm").val()!=""){
			var rule = BIONE.bulidFilterGroup("#search");
			if (rule.rules.length) {
				grid.setParm("condition",JSON2.stringify(rule));
				grid.setParm("newPage",1);
			} else {
				grid.setParm("condition","");
				grid.setParm('newPage', 1);
			}
			grid.loadData();
		}
	});
	function f_open_view() {
			var selectedRows = grid.getSelectedRows();
			if (selectedRows.length <= 0) {
				BIONE.tip("请选择一张报表");
				return;
			}
			if (selectedRows.length > 1) {
				BIONE.tip("请选择一张报表");
				return;
			}
			else{
				var rptId=selectedRows[0].rptId;
				var rptNm=selectedRows[0].rptNm;
				var rptType=selectedRows[0].rptType;
				//if(selectedRows[0].infoRights=="Y")
					parent.addTabInfo(rptId,rptNm,rptType);
				//else
					//BIONE.tip("该用户无权限查看此报表");
			}
			
	}
	
	function achieveIds() {
		ids = [];
		var rows = grid.getSelectedRows();
		for ( var i in rows) {
			ids.push(rows[i]);
		}
	}
	
</script>
</head>
<body>

	<div id="template.right.down">
		<div id="aaa">
			<div id="maingrid" style="margin-top: 60px;"></div>
		</div>
	</div>
</body>
</html>