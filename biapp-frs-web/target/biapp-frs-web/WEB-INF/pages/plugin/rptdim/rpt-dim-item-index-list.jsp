<%@ page language="java" pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<html>
<meta name="decorator" content="/template/template1_BS.jsp">
<head>
<!-- 基础的JS和CSS文件 -->
<script type="text/javascript">
    var dimTypeNo='${dimTypeNo}';
    var flag='${flag}';
    var isList='${isList}';
    var hascheckbox =  true;
	var dynamicWidth  ="45%";
	if(flag){
		hascheckbox  =  false;
		dynamicWidth =  "46%";
	}
	var grid;
	var ids;
	var catalogId;
	function initSearchForm() {
		//搜索表单应用ligerui样式
		var  fieldsColumn= [ {
			name : "dimTypeNo",
			type : "hidden",
			attr : {
				field : 'dimTypeNo',
				op : "="
			}
		}];
		if(!flag){
			fieldsColumn.push({
				display : '维度项标识',
				name : "dimItemNo",
				newline : true,
				type : "text",
				cssClass : "field",
				attr : {
					field : 'dimItemNo',
					op : "="
				}
			});
		}
		fieldsColumn.push({
				display : '维度项名称',
				name : 'dimItemNm',
				newline : false,
				type : "text",
				cssClass : "field",
				attr : {
					field : 'dimItemNm',
					op : "like"
				}
		});
		$("#search").ligerForm({
			fields :fieldsColumn
		});
	};
	function initGrid() {
		var displayText =  "维度项标识";
		if(flag){
			displayText =  "维度项名称";
		}
		var dynamicColumns  =  [ {
			display : displayText,
			name : 'dimItemNo',
			id:'dimItemNo',
			width : "40%",
			align : 'left',
			render:function(row){
				 if(flag){
					 return  "<a href='javascript:void(0)'       onclick='f_open_preview(\""+row.dimItemNo+"\")'>"+row.dimItemNm+"</a>";
				   }else{
					   return   row.dimItemNo;
				   }
			}
		}];
		if(flag){
				dynamicColumns.push({
					display : '业务定义',
					name : 'busiDef',
					width : dynamicWidth,
					align : 'left'
			});
		}else{
			dynamicColumns.push({
					display : '维度项名称',
					name : 'dimItemNm',
					width : dynamicWidth,
					align : 'left'
			});
		}
		var   gridJson  ={
				toolbar : {},
    			checkbox :hascheckbox,
    			columns :dynamicColumns,
    			dataAction : 'server', //从后台获取数据
    			alternatingRow : true, //附加奇偶行效果行
				colDraggable : true,
				sortName : 'dimItemNo',//第一次默认排序的字段
    			sortOrder : 'asc', //排序的方式
    			url : "${ctx}/rpt/frame/dimCatalog/findDimItemListByTypeNo.json?dimTypeNo="+dimTypeNo+"&d=" + new Date().getTime(),
    			rownumbers : true,
    			width : '99%'
		};
		if(isList){
			gridJson.usePager= true;
			gridJson.pageParmName= 'page';
			gridJson.pagesizeParmName= 'pagesize';
		}else{
			gridJson.usePager=false;
			gridJson.tree={ columnId: 'dimItemNo' };
			gridJson.autoCheckChildren=false;
        }
		grid = $("#maingrid").ligerGrid(gridJson);
		grid.setHeight($("#center").height() - 120);
	};
	function initToolBar() {
		var toolBars = [ {
			text : '增加',
			click : f_open_add,
			operNo : 'rpt-dim-add',
			icon : 'fa-plus'
		}, {
			text : '修改',
			click : f_open_update,
			operNo : 'rpt-dim-modify',
			icon : 'fa-pencil-square-o'
		}, {
			text : '删除',
			click : f_delete,
			operNo : 'rpt-dim-delete',
			icon : 'fa-trash-o'
		}];
		BIONE.loadToolbar(grid, toolBars, function() {});
	}
	$(function() {
		initSearchForm();
		initGrid();
		if(!flag){
		    initToolBar();
		}
		BIONE.addSearchButtons("#search", grid, "#searchbtn");
		$("input[name='dimTypeNo']").val(dimTypeNo);
	});
	function f_open_add() {
			var checkedRole = grid.getSelectedRows();
			if (checkedRole.length > 1) {
                parent.BIONE.tip("请选择1行数据");
				return;
			}
			var id = "";
			var length = checkedRole.length;
			if(length ==1){
				id = checkedRole[0].dimItemNo;
			}
			var modelDialogUri = "${ctx}/rpt/frame/dimCatalog/dimItem/new?dimItemNo=" + id+"&dimTypeNo="+dimTypeNo;
			parent.BIONE.commonOpenDialog("维度项添加", "rptDimItemInfoAddWin",$(parent.document).width(), $(parent.document).height(),modelDialogUri);
	}
	function f_open_update() {
		var row = grid.getCheckedRows();
		if (row && row.length == 1) {
			var dimItemNo = row[0].dimItemNo;
			var modelDialogUri = "${ctx}/rpt/frame/dimCatalog/dimItem/update?dimItemNo=" + dimItemNo+"&dimTypeNo="+dimTypeNo;
			parent.BIONE.commonOpenDialog("维度项修改", "rptDimItemInfoUpdateWin",$(parent.document).width(), $(parent.document).height(),modelDialogUri);
		} else {
            parent.BIONE.tip('请选择一行数据');
		}
	}
	function f_open_preview(dimItemNo) {
			var modelDialogUri = "${ctx}/rpt/frame/dimCatalog/dimItem/update?dimItemNo=" + dimItemNo+"&dimTypeNo="+dimTypeNo+"&preview=preview";
			parent.BIONE.commonOpenDialog("维度项查看", "rptDimItemInfoUpdateWin",$(parent.document).width(), $(parent.document).height(),modelDialogUri);
	}
	function achieveIds() {
		ids = [];
		var rows = grid.getSelectedRows();
		for ( var i in rows) {
			ids.push(rows[i]);
		}
	}
	function f_delete() {
		var checkedRole = grid.getSelectedRows();
		if (checkedRole.length == 0) {
            parent.BIONE.tip('请选择行');
			return;
		}
		var id = "";
		var length = checkedRole.length;
		for ( var i = 0; i < length; i++) {
			id += checkedRole[i].dimItemNo + ",";
		}
		window.parent.$.ligerDialog.confirm('将进行级联删除操作，确实要删除这' + checkedRole.length + '条记录吗!',
				function(yes) {
					if (yes) {
						$.ajax({
							type : "POST",
							url : '${ctx}/rpt/frame/dimCatalog/dimItem/' + id+"?dimTypeNo="+dimTypeNo,
							dataType : 'json',
							success : function(result) {
								if (result == true) {
									refreshIt();
                                    parent.BIONE.tip("删除成功");
								} else {
                                    parent.BIONE.tip("删除失败");
								}
							}
						});
					}
				});
	}
	function  refreshIt(){
		var  dimItemNo_ = $("input[name='dimItemNo']").val();
		var  dimItemNm_ = $("input[name='dimItemNm']").val();
		$(".l-btn-hasicon").click();
		$("input[name='dimItemNo']").val(dimItemNo_);
		 $("input[name='dimItemNm']").val(dimItemNm_);
		 grid.loadData();
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