<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<html>
<head>
<meta name="decorator" content="/template/template1_BS.jsp">
<script type="text/javascript">
	var grid;
	var srCodes;
	$(function(){
		searchForm();
		initGrid();
		initButtons();
		BIONE.addSearchButtons("#search", grid, "#searchbtn");
	});

	function searchForm() {
		$("#search").ligerForm({
			fields : [ {
				display : "报送范围",
				name : "submitRangeNm",
				newline : true,
				type : "text",
				cssClass : "field",
				attr : {
					op : "like",
					field : "pbc.submitRangeNm"
				}
			} ]
		});
	}

	//初始化Grid
	function initGrid() {
		grid = $("#maingrid").ligerGrid({
			InWindow : false,
			height : '99%',
			width : '100%',
			columns : [ {
				display : '报送范围编号',
				name : 'submitRangeCode',
				width : '30%'
			},{
				display : '报送范围名称',
				name : 'submitRangeNm',
				width : '30%'
			}/**,{
				display : '版本编号',
				name : 'verId',
				width : '15%'
			}**/, {
				display : '上报机构',
				width : '15%',
				render : function(row){
					return "<a href='javascript:void(0)'  onclick='open_org_grid(\""+row.submitRangeCode+"\",\""+row.verId+"\")' >编辑</a>";
				}
			}  ],
			checkbox : true,
			usePager : true,
			isScroll : true,
			rownumbers : true,
			alternatingRow : true,//附加奇偶行效果行
			colDraggable : true,
			dataAction : 'server',//从后台获取数据
			method : 'post',
			url : '${ctx}/frs/pbmessage/rangeList',
			sortName : 'submitRangeNm', //第一次默认排序的字段
			sortOrder : 'asc',
			toolbar : {}
		});
	}

	//上报地区
	function open_org_grid(submitRangeCode,verId){
		var height = $(window).height() - 30;
		var width = $(window).width() - 60;
		BIONE.commonOpenDialog("上报地区","orgConfig",width,height,"${ctx}/frs/pbmessage/orgGrid?isView=0&submitRangeCode="+submitRangeCode+"&verId="+verId,null);
	}
	
	function initButtons() {
		var btns = [ {
			text : '新增',
			click : oper_add,
			icon : 'icon-add',
			operNo : 'oper_add'
		}, {
			text : '修改',
			click : oper_modify,
			icon : 'icon-modify',
			operNo : 'oper_modify'
		}, {
			text : '删除',
			click : oper_delete,
			icon : 'icon-delete',
			operNo : 'oper_delete'
		}];
		BIONE.loadToolbar(grid, btns, function() {});
	}
	
	function oper_add() {
		BIONE.commonOpenLargeDialog('新增报送范围', 'addRange', '${ctx}/frs/pbmessage/addRange');
	}
	
	function oper_modify() {
		var rows = grid.getSelectedRows();
		if (rows.length < 1) {
			BIONE.tip('请选择记录');
		} else if (rows.length > 1) {
			BIONE.tip('只能选择一条记录');
		} else {
			var rangeNo = rows[0].submitRangeNo;
			BIONE.commonOpenLargeDialog("修改报送范围", "editRange","${ctx}/frs/pbmessage/editRange?id="+rangeNo);
		}
	}
	//批量删除报文集
	function oper_delete() {
		var ids = achieveIds();
		var data = {
			ids : ids,
			codes : srCodes
		};
		if(ids.length > 0){
			$.ligerDialog.confirm('您确定删除这' + ids.length + "条记录吗？", function(yes) {
				if (yes) {
					$.ajax({
						async : false,
						type : "POST",
						url : '${ctx}/frs/pbmessage/delRange?ids=' + ids.join(',')+'&codes=' + srCodes.join(','),
						success : function(result){
							if(result.isDel){
								BIONE.tip("删除成功!");
								initGrid();
							}else{
								BIONE.tip(result.msg);
							}
							
						},
						error:function(e){
							BIONE.tip("删除失败，请联系系统管理员");
						}
					});
					
				}
			});	
		} else {
			BIONE.tip('请选择记录');
		}
	}

	//获取选中行的主键
	function achieveIds() {
		var submitRangeNos = [];
		var submitRangeCodes = [];
		var rows = grid.getSelectedRows();
		for ( var i in rows) {
			submitRangeNos.push(rows[i].submitRangeNo);
			submitRangeCodes.push(rows[i].submitRangeCode);
		}
		srCodes = submitRangeCodes;
		return submitRangeNos;
	}
	
</script>

</head>
<body>
</body>
</html>