<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<html>
<meta name="decorator" content="/template/template1_BS.jsp">
<head>
<!-- 基础的JS和CSS文件 -->
<script type="text/javascript">
	var grid;
	var taskId='${taskId}';

	$(function() {
		initGrid();
		loadData();

	});
	
	function loadData(){
		grid.loadData();
		
	}
	function initGrid() {
		var  columnArr  =[{
			display : '最近补录时间',
			name : 'startTime',
			width : "15%",
			align : 'left',
			rowSpan:true,
			render: function (rowData){
				return rowData.startTime.substring(0,16);
			}
		}, {
			display : '部门',
			name : 'orgNm',
			width : "20%",
			align : 'left',
			rowSpan:true
		},  {
			display : '流程',
			name : 'taskNodeNm',
			width : "10%",
			align : 'center'
		},  {
			display : '下发类型',
			name : 'taskObjType',
			width : "10%",
			align : 'center',
			render : function(rowData) {
				if (rowData.taskObjType.trim() == "AUTH_OBJ_USER")
					return "用户";
				if (rowData.taskObjType.trim() == "AUTH_OBJ_ROLE")
					return "角色";
				return rowData.taskObjType;
			},
		},  {
			display : '下发对象',
			name : 'taskObjNm',
			width : "60%",
			align : 'center'
		}];
		grid = $("#maingrid").ligerGrid({
			checkbox : false,
			columns : columnArr,
			dataAction : 'server', //从后台获取数据
			usePager : false, //服务器分页
			alternatingRow : false, //附加奇偶行效果行
			colDraggable : false,
			<%--url : "${ctx}/rpt/input/task/getTaskDeptList.json?taskId="+taskId,--%>
			sortName : 'updateDate',//第一次默认排序的字段
			sortOrder : 'desc', //排序的方式
            delayLoad: true,
			pageParmName : 'page',
			pagesizeParmName : 'pagesize',
			rownumbers : false,
			width : '100%',	
			height : '99%'
		});
		f_reload();
	};

	function f_reload() {
		grid.setOptions({
			url : "${ctx}/rpt/input/task/getTaskDeptList.json?taskId="+taskId,
			dataAction : 'server',
			dataType : 'server'
		});
	}

	$.extend($.ligerui.controls.Grid.prototype, {
		_getHtmlFromData:function(data, frozen) {
			if (!data) return "";
			var g = this, p = this.options;
			var gridhtmlarr = [];
			var rowSpanData = {};
			var islast = false;
			for (var i = 0, l = data.length; i < l; i++)
			{
				islast = (i==data.length-1);
				var item = data[i];
				var rowid = item['__id'];
				if (!item) continue;
				gridhtmlarr.push('<tr');
				gridhtmlarr.push(' id="' + g._getRowDomId(item, frozen) + '"');
				gridhtmlarr.push(' class="l-grid-row'); //class start
				if (!frozen && g.enabledCheckbox() && p.isChecked && p.isChecked(item))
				{
					g.select(item);
					gridhtmlarr.push(' l-selected');
				}
				else if (g.isSelected(item))
				{
					gridhtmlarr.push(' l-selected');
				}
				else if (p.isSelected && p.isSelected(item))
				{
					g.select(item);
					gridhtmlarr.push(' l-selected');
				}
				if (item['__index'] % 2 == 1 && p.alternatingRow)
					gridhtmlarr.push(' l-grid-row-alt');
				//自定义css class
				if (p.rowClsRender)
				{
					var rowcls = p.rowClsRender(item, rowid);
					rowcls && gridhtmlarr.push(' ' + rowcls);
				}
				gridhtmlarr.push('" ');  //class end
				if (p.rowAttrRender) gridhtmlarr.push(p.rowAttrRender(item, rowid));
				if (p.tree && g.collapsedRows && g.collapsedRows.length)
				{
					var isHide = function ()
					{
						var pitem = g.getParent(item);
						while (pitem)
						{
							if ($.inArray(pitem, g.collapsedRows) != -1) return true;
							pitem = g.getParent(pitem);
						}
						return false;
					};
					if (isHide()) gridhtmlarr.push(' style="display:none;" ');
				}
				else if (p.tree && p.tree.isExtend)
				{
					var isHide = function ()
					{
						var pitem = g.getParent(item);
						while (pitem)
						{
							if (p.tree.isExtend(pitem) == false) return true;
							pitem = g.getParent(pitem);
						}
						return false;
					};
					if (isHide()) gridhtmlarr.push(' style="display:none;" ');
				}
				gridhtmlarr.push('>');
				$(g.columns).each(function (columnindex, column)
				{
					if (frozen != column.frozen) return;
					if(column.rowSpan){
						var key = column.name;
						var data = rowSpanData[key];
						var value = g._getCellContent(item, column);
						if(data){
							if(data.value==value){
								data.count = data.count+1;
								rowSpanData[key] = data;
								if(!islast){
									return true;
								}
							}else{
								if((data.domId&&data.count>1)){
									var rowSpanTd = 'id="'+data.domId+'"';
									$.each(gridhtmlarr,function(index,str){
										if(rowSpanTd==$.trim(str)){
											str = str+" rowspan="+data.count;
											gridhtmlarr[index] = str;
										}
									});
								}
								data = {value:value,count:1,domId:g._getCellDomId(item, this)};
								rowSpanData[key] = data;
							}
							if(islast){
								if((data.domId&&data.count>1)){
									var rowSpanTd = 'id="'+data.domId+'"';
									$.each(gridhtmlarr,function(index,str){
										if(rowSpanTd==$.trim(str)){
											str = str+" rowspan="+data.count;
											gridhtmlarr[index] = str;
										}
									});
									return true;
								}
							}
						}else{
							data = {value:value,count:1,domId:g._getCellDomId(item, this)};
							rowSpanData[key] = data;
						}
					}
					gridhtmlarr.push('<td');
					gridhtmlarr.push(' id="' + g._getCellDomId(item, this) + '"');
					//如果是行序号(系统列)
					if (this.isrownumber)
					{
						gridhtmlarr.push(' class="l-grid-row-cell l-grid-row-cell-rownumbers" style="width:' + this.width + 'px"><div class="l-grid-row-cell-inner"');
						if (p.fixedCellHeight)
							gridhtmlarr.push(' style = "height:' + p.rowHeight + 'px;" ');
						else
							gridhtmlarr.push(' style = "min-height:' + p.rowHeight + 'px;" ');
						gridhtmlarr.push('>' + (parseInt(item['__index']) + 1) + '</div></td>');
						return;
					}
					//如果是复选框(系统列)
					if (this.ischeckbox)
					{
						gridhtmlarr.push(' class="l-grid-row-cell l-grid-row-cell-checkbox" style="width:' + this.width + 'px"><div class="l-grid-row-cell-inner"');
						if (p.fixedCellHeight)
							gridhtmlarr.push(' style = "height:' + p.rowHeight + 'px;" ');
						else
							gridhtmlarr.push(' style = "min-height:' + p.rowHeight + 'px;" ');
						gridhtmlarr.push('>');
						gridhtmlarr.push('<span class="l-grid-row-cell-btn-checkbox"></span>');
						gridhtmlarr.push('</div></td>');
						return;
					}
					//如果是明细列(系统列)
					else if (this.isdetail)
					{
						gridhtmlarr.push(' class="l-grid-row-cell l-grid-row-cell-detail" style="width:' + this.width + 'px"><div class="l-grid-row-cell-inner"');
						if (p.fixedCellHeight)
							gridhtmlarr.push(' style = "height:' + p.rowHeight + 'px;" ');
						else
							gridhtmlarr.push(' style = "min-height:' + p.rowHeight + 'px;" ');
						gridhtmlarr.push('>');
						if (!p.isShowDetailToggle || p.isShowDetailToggle(item))
						{
							gridhtmlarr.push('<span class="l-grid-row-cell-detailbtn"></span>');
						}
						gridhtmlarr.push('</div></td>');
						return;
					}
					var colwidth = this._width;
					gridhtmlarr.push(' class="l-grid-row-cell ');
					if (g.changedCells[rowid + "_" + this['__id']]) gridhtmlarr.push("l-grid-row-cell-edited ");
					if (this.islast)
						gridhtmlarr.push('l-grid-row-cell-last ');
					gridhtmlarr.push('"');
					//if (this.columnname) gridhtmlarr.push('columnname="' + this.columnname + '"');
					gridhtmlarr.push(' style = "');
					gridhtmlarr.push('width:' + colwidth + 'px; ');
					if (column._hide)
					{
						gridhtmlarr.push('display:none;');
					}
					gridhtmlarr.push(' ">');
					gridhtmlarr.push(g._getCellHtml(item, column));
					gridhtmlarr.push('</td>');
				});
				gridhtmlarr.push('</tr>');
			}
			return gridhtmlarr.join('');
		}
	});


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