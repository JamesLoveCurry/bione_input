<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<html>
<meta name="decorator" content="/template/template1_BS.jsp">
<head>
<!-- 基础的JS和CSS文件 -->
<script type="text/javascript">
	var grid;
	var ids;
	var catalogId='${catalogId}';
	var preview='${preview}';
	var hascheckbox =  true;
	var dynamicWidth  ="30%";
	var   defDeptParamTypeNo = '${defDeptParamTypeNo}';
	var  searchCols = [ {
		name : "catalogId",
		type : "hidden",
		attr : {
			field : 'dim.catalogId',
			op : "="
		}
	}, {
		display : '维度名称',
		name : "dimTypeNm",
		newline : true,
		type : "text",
		cssClass : "field",
		attr : {
			field : 'dimTypeNm',
			op : "like"
		}
	} ];
	if(preview){
		hascheckbox  =  false;
		dynamicWidth =  "33%";
		searchCols.push({
			display : '定义部门',
			name : 'defDept',
			newline : false,
			type : "select",
			options : {
			   isMultiSelect:false,
			   cancelable :true,
				url : "${ctx}/report/frame/idx/defDeptList.json?defDeptParamTypeNo="+defDeptParamTypeNo
			},
			attr : {
				field : 'defDept',
				op : "like"
			}
		});
	}
	function initSearchForm() {
		//搜索表单应用ligerui样式
		$("#search").ligerForm({
			fields : searchCols
		});
	};
	function initGrid() {
		var  cols=  [{
			display : '维度名称',
			name : 'dimTypeNm',
			width : dynamicWidth,
			align : 'left'
		}, {
			display : '业务定义',
			name : 'busiDef',
			width : "60%",
			align : 'left'
		}];
		if(preview){
			cols=  [{
				display : '维度目录',
				name : 'catalogNm', 
				width : dynamicWidth,
				align : 'left'
			},{
				display : '维度名称',
				name : 'dimTypeNm',
				width : "45%",
				align : 'left',
				render:function(row){
							 return  "<a href='javascript:void(0)'      onclick='f_open_dim_item(\""+row.dimTypeNo+"\",\""+row.dimTypeNm+"\")'>"+row.dimTypeNm+"</a>";
				}
			},{
				display : '维度信息',
				name : 'dimTypeNo',
				width : "18%",
				align : 'center',
				render:function(row){
					return  "<a href='javascript:void(0)' class='indexNmA'     onclick='f_open_preview(\""+row.dimTypeNo+"\")'>查看</a>";
				}
			}];
		}
		grid = $("#maingrid").ligerGrid({
			toolbar : {},
			checkbox : hascheckbox,
			columns :cols,
			dataAction : 'server', //从后台获取数据
			usePager : true, //服务器分页
			alternatingRow : true, //附加奇偶行效果行
			colDraggable : true,
			url : "${ctx}/rpt/frame/dimCatalog/getTypeInfoList.json?catalogId="+catalogId,
			sortName : 'dimTypeNo',//第一次默认排序的字段
			sortOrder : 'asc', //排序的方式
			pageParmName : 'page',
			pagesizeParmName : 'pagesize',
			rownumbers : true,
			width : '100%',
			height : '99%'
		});
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
		},{
			icon : 'fa-book',
			text : '查看',
			click : f_search
		}];
		if(!preview){
			BIONE.loadToolbar(grid, toolBars, function() {});
		}
	}
	$(function() {	
		initSearchForm();
		initGrid();
		initToolBar();
		BIONE.addSearchButtons("#search", grid, "#searchbtn");
		$("input[name='catalogId']").val(catalogId);
	});
	function f_open_add() {
		if (catalogId) {
			var modelDialogUri = "${ctx}/rpt/frame/dimCatalog/dimType/new?catalogId=" + catalogId;
			parent.BIONE.commonOpenDialog("维度添加", "rptDimTypeInfoAddWin",$(parent.document).width(), $(parent.document).height(),modelDialogUri);
		} else {
            parent.BIONE.tip("请选择维度目录！");
		}
	}
	function f_open_update() {
		var row = grid.getCheckedRows();
		if (row && row.length == 1) {
			var dimTypeNo = row[0].dimTypeNo;
			var modelDialogUri = "${ctx}/rpt/frame/dimCatalog/dimType/update?dimTypeNo=" + dimTypeNo;
			parent.BIONE.commonOpenDialog("维度修改", "rptDimTypeInfoUpdateWin",$(parent.document).width(), $(parent.document).height(),modelDialogUri);
		} else {
            parent.BIONE.tip('请选择一行数据。');
		}
	}
	function f_open_preview(dimTypeNo) {
		var modelDialogUri = "${ctx}/rpt/frame/dimCatalog/dimType/update?dimTypeNo=" + dimTypeNo+"&flag=flag";
		parent.BIONE.commonOpenDialog("维度查看", "rptDimTypeInfoUpdateWin",$(parent.document).width(), $(parent.document).height(),modelDialogUri);
	}
	function  f_open_dim_item(dimTypeNo,dimTypeNm){
		parent.initCurrentTab("iteminfo",dimTypeNo,dimTypeNm);
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
			id += checkedRole[i].dimTypeNo + ",";
		}
		window.parent.$.ligerDialog.confirm('将进行级联删除操作，确实要删除这' + checkedRole.length + '条记录吗!',
				function(yes) {
					if (yes) {
						$.ajax({
							type : "POST",
							url : '${ctx}/rpt/frame/dimCatalog/dimType/' + id,
							dataType : 'json',
							success : function(result) {
								if (result.msg) {
									if(result.msg=='1'){
										window.parent.searchHandler();
										refreshIt();
                                        parent.BIONE.tip("删除成功");
									}else{
                                        parent.BIONE.tip(result.msg);
									}
								}else {
                                    parent.BIONE.tip("删除失败");
								}
							}
						});
					}
				});
	}
	
	function  dim_value_m(){
		var row = grid.getCheckedRows();
		if (row && row.length == 1) {
			var dimTypeNo = row[0].dimTypeNo;
			var modelDialogUri = "${ctx}/rpt/frame/dimCatalog/dimItem?dimTypeNo=" + dimTypeNo;
		    parent.BIONE.commonOpenLargeDialog("维度项管理界面", "rptDimItemInfoMWin",
					modelDialogUri);
		} else {
            parent.BIONE.tip('请选择一行数据。');
		}
	}
	function achieveIds() {
		ids = [];
		var rows = grid.getSelectedRows();
		for ( var i in rows) {
			ids.push(rows[i]);
		}
	}
	function changeCatalog(catalogId) {
		$("input[name='dimTypeNm']").val("");
		$("input[name='dimTypeEnNm']").val("");
		if (catalogId) {
			$("input[name='catalogId']").val(catalogId);
		} else {
			$("input[name='catalogId']").val("");
		}
		refreshIt();
	}
	function  refreshIt(){
		var  dimItemNo_ = $("input[name='dimTypeNm']").val();
		var  dimItemNm_ = $("input[name='dimTypeEnNm']").val();
		$(".l-btn-hasicon").click();
		$("input[name='dimTypeNm']").val(dimItemNo_);
		 $("input[name='dimTypeEnNm']").val(dimItemNm_);
		grid.loadData();//加载页面
	}
	 function  f_search(){
		 var rows = grid.getSelectedRows();
		 if (rows.length == 1) {
			 parent.BIONE.commonOpenDialog(tabTitleName ="维度["+rows[0].dimTypeNm+"]: 维度项", "rptDimItemInfoUpdateWin",$(parent.document).width(), $(parent.document).height(),
						"${ctx}/rpt/frame/dimCatalog/checkDim?&dimTypeNo=" +rows[0].dimTypeNo+"&flag=preview",null);
		 }else{
             parent.BIONE.tip("请选择一条记录");
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