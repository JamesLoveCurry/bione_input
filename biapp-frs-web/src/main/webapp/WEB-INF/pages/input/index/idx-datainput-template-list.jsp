<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<html>
<meta name="decorator" content="/template/template1_BS.jsp">
<head>
<style>
.indexStsA,.indexNmA{
     width:55%;
     cursor:pointer;
}
.stop{
    color:red;
}
</style>
<!-- 基础的JS和CSS文件 -->
<script type="text/javascript">
	var grid;
	var ids;
    var  templateName_  ='${DataInputName}';
	var catalogId_ ;
	var catalogName_ ;

	$(function() {	
		initBaseInfo();
		initSearchForm();
		initGrid();
		initToolBar();
		BIONE.addSearchButtons("#search", grid, "#searchbtn");
		$("input[name='templateName']").val(templateName_);
		
		loadData();
	});
	
	function loadData(){
		
		grid.loadData();
		
	}
	
	function initBaseInfo(){

		var catalog = parent.currentNode;
		if(catalog==null || typeof catalog=="undefined")
		{
			catalogId_ = "ROOT";
			catalogName_ = "ROOT";
		}
		else{
			catalogId_ = catalog.id;
			catalogName_ = catalog.text;
		}
	}
	function initSearchForm() {
		//搜索表单应用ligerui样式
		$("#search").ligerForm({
			fields : [{
				display : '模板名称',
				name : "templateNm",
				newline : true,
				type : "text",
				cssClass : "field",
				attr : {
					field : 'templateNm',
					op : "like"
				}
			}, {
				display : '模板类型',
				name : 'templateType',
				newline : false,
				type : "select",
				comboboxName:"templateTypebox",
				cssClass : "field",
				attr : {
					field : 'templateType',
					op : "="
				},
				options : {					
				    data : [{
						text : '--请选择--',
						id : ''
						}, {
						text : "指标补录",
						id : "01"
					    }, {
						text : "计划值补录",
						id : "02"
					    }]
				}
			}]
		});
	};
	
	function showTemplateInfoSingle(templateId,ralateToTask){
			
			var canEdit = ralateToTask=="未下发";
			dialog = window.parent.BIONE.commonOpenDialog("修改模板",
					"rptDataInputBox",$(parent.document).width(), $(parent.document).height(),
					"${ctx}/rpt/input/idxdatainput/idxInputFrame?templateId="+encodeURI(encodeURI(templateId))+"&canEdit="+canEdit, null);
	}
	
	function initGrid() {
		var  columnArr  =[{
			display : '模板名称',
			name : 'templateNm',
			width : "15%",
			align : 'left',
			render:function(row){
				return  "<a href='javascript:void(0)'   onclick='showTemplateInfoSingle(\""+row.templateId+"\",\""+row.ralateToTask+"\")'>"+row.templateNm+"</a>";
			}			
		},  {
			display : '模板类型',
			name : 'templateType',
			width : "15%",
			align : 'center',
			render:function(row){
				var tmpText = "";
				if(row.templateType=="01")
					return "指标补录";
				if(row.templateType=="02")
					return "指标计划值补录";
			}
		},  {
			display : '是否已经下发任务',
			name : 'ralateToTask',
			width : "15%",
			align : 'center'
		},  {
			display : '备注',
			name : 'remark',
			width : "45%",
			align : 'center'
		}];
		grid = $("#maingrid").ligerGrid({
			toolbar : {},
			checkbox : true,
			columns : columnArr,
			dataAction : 'server', //从后台获取数据
			usePager : true, //服务器分页
			alternatingRow : true, //附加奇偶行效果行
			colDraggable : true,
			url : "${ctx}/rpt/input/idxdatainput/getTemplateList.json?catalogId="+catalogId_,
			enabledSort:false,
            delayLoad:true,
			pageParmName : 'page',
			pagesizeParmName : 'pagesize',
			rownumbers : true,
			width : '100%'
		});
		grid.setHeight($("#center").height() - 40);
	};
	function initToolBar() {
		var toolBars = [ {
			text : '增加',
			click : f_open_add,
			icon : 'add'
		}, {
			text : '删除',
			click : f_delete,
			icon : 'delete'
		} ];
		BIONE.loadToolbar(grid, toolBars, function() {
		});
	}
	function f_open_add() {
			dialog = window.parent.BIONE.commonOpenDialog("新建模板",
					"rptDataInputBox",$(parent.document).width(), $(parent.document).height(),
					"${ctx}/rpt/input/idxdatainput/idxInputFrame?catalogId="
							+  encodeURI(encodeURI(catalogId_))+"&catalogName="+encodeURI(encodeURI(catalogName_))+"&canEdit=true" , null);
	}
	function showDataInputInfoSingle(DataInputId){
		dialog = window.parent.BIONE.commonOpenDialog("修改任务",
				"rptDataInputBox",$(parent.document).width(), $(parent.document).height(),
				"${ctx}/rpt/input/idxdatainput/idxInputFrame?DataInputId="+encodeURI(encodeURI(DataInputId)), null);
	}
	
	function f_canDelTemp(checkedDataInput){
		for(var i = 0 ;i < checkedDataInput.length;i++){
			if(checkedDataInput[i].ralateToTask=="已下发"){
				BIONE.tip("模型["+checkedDataInput[i].templateNm+"]已经下发,不能删除,请重新选择");
				return false;
			}
		}
		return true;
	}
	
	function f_delete() {
		var checkedDataInput = grid.getSelectedRows();
		if (checkedDataInput.length == 0) {
			BIONE.tip('请选择行');
			return;
		}
		if(!f_canDelTemp(checkedDataInput))
		return ;	
		
		window.parent.$.ligerDialog.confirm('将进行删除操作，确实要删除这' + checkedDataInput.length + '条记录吗!',
				function(yes) {
					if (yes) {
						var ids = "";
						var length = checkedDataInput.length;
						for ( var i = 0; i < length; i++) {
							ids = ids + checkedDataInput[i].templateId + ",";
						}
						$.ajax({
							type : "POST",
							url : '${ctx}/rpt/input/idxdatainput/delTemplate/' + ids,
							dataType : 'json',
							success : function(result) {
								if (result == true) {
									refreshIt();
									BIONE.tip("删除成功");
								} else {
									BIONE.tip("删除失败");
								}
							}
						});
					}
				});
	}
	function  refreshIt(){
		window.parent.refreshTree();
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