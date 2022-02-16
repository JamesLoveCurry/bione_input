<%@ page language="java" pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<html>
<meta name="decorator" content="/template/template8.jsp">
<head>
<script type="text/javascript">
	var grid;
	var rptId = "${rptId}";
	var dataDate = "${dataDate}";
	var orgNo = "${orgNo}";
	var selIdxs = top.selIdxs;

	$(function() {
		// 初始化列表
		initGrid();
		// 初始化toolbar
		//initToolbar();
		// 初始化按钮
		//initButtons();
	});
	
	// 初始化页面布局
	function initToolbar(){
		var toolbarHtml = 
			//'<ul style="width:160px; padding-top:4px;">                                                                                                                                                                           '+
			//'	<li style="width:160px;text-align:left;">                                                                                                                         '+
			'		<div class="l-text-wrapper" style="width: 160px;">                                                                                                    '+
			'			<div class="l-text l-text-date" style="width: 100%;">                                                                                               '+
			'				<input id="treeSearchInput" type="text" class="l-text-field"  style="width: 100%;" />                                              '+
			'				<div class="l-trigger">                                                                                                                                          '+
			'					<div id="treeSearchIcon" style="width:100%;height:100%;background:url(${ctx}/images/classics/icons/find.png)   '+
			'            no-repeat 50% 50% transparent;"></div>                                                                                                        '+
			'				</div>                                                                                                                                                                 '+
			'			</div>                                                                                                                                                                   '+
			'		</div>                                                                                                                                                                     '
			//'	</li>                                                                                                                                                                          '+
			//'</ul>                                                                                                                                                                          ';
		$(".l-panel-topbarinner .l-toolbar .l-panel-topbarinner-left").prepend(toolbarHtml);
	}
	
	// 初始化列表
	function initGrid(){
		var gridHeight = $("#center").height();
		grid = $("#maingrid").ligerGrid({
			width : "100%",
			height : gridHeight,
			columns : [ {
				display : "指标位置",
				name : "cellNo",
				width : "18%",
				align : "center"
			} , {
				display : "指标名称",
				name : "cellNm",
				width : "18%",
				align: "center",
				render : function(data){
					if(data.cellNm != null
							&& data.cellNm != ""){
						return data.cellNm;
					}else{						
						return "";
					}
				}
			} , {
				display : "业务编号",
				name : "busiNo",
				width : "16%",
				align: "center",
				render : function(data){
					if(data.busiNo != null
							&& data.busiNo != ""){
						return data.busiNo;
					}else{						
						return "";
					}
				}
			} , {
				display : "本期值",
				name : "newVal",
				width : "22%",
				align: "center",
				editor: { 
					type: 'text'
				}
			},{
				display : "上期值",
				name : "lastVal",
				width : "22%",
				align: "center"
			} ],
			checkbox : false,
			rownumbers : false,
			alternatingRow : true,//附加奇偶行效果行
			colDraggable : false,
			selectRowButtonOnly : true ,
			usePager : false ,
			dataAction : 'server',//从后台获取数据
			method : 'post',
			enabledEdit: true,
			url : "${ctx}/rpt/frs/rptfill/getCompareData",
			parms:{
				rptId : rptId,
				orgNo : orgNo,
				dataDate : dataDate,
				selIdxs : JSON2.stringify(selIdxs)
			},
			toolbar : {
				items: [{ 
					text: '保存', 
					click: f_save, 
					icon: 'save'
				},{
					line: true 
				},{ 
					text: '关闭', 
					click: f_close, 
					icon: 'delete' 
				}]
			},
			onBeforeEdit : function(e){
				if(e && e.record
					  && e.record.type == "04"){
					// excel公式单元格，不允许编辑
					return false;
				}
				return true;
			}
		});
	}
	
	// 初始化按钮
	function initButtons(){
		var buttons = [];
		buttons.push({
			text : '取消',
			onclick : f_close
		});
		buttons.push({
			text : '保存',
			onclick : f_save
		});
		BIONE.addFormButtons(buttons);
	}

	function f_save() {
		top.selIdxs = null;
		var data = grid.getUpdated();
		if(data
				&& data.length
				&& data.length > 0
				&& window.parent.View){
			var currSheet = window.parent.View.spread.getActiveSheet();
			for(var i = 0 , l = data.length ; i < l ; i++){
				if(!data[i].cellNo){
					continue;
				}
				var rowCol = window.parent.View.Utils.posiToRowCol(data[i].cellNo);
				var row = rowCol.row;
				var col = rowCol.col;
				if(!row || !col){
					continue;
				}
				currSheet.getCell(row , col).value(data[i].newVal);
			}
		}
		BIONE.closeDialog("compareWin");
	}

	function f_close() {
		top.selIdxs = null;
		BIONE.closeDialog("compareWin");
	}
	
</script>
</head>
<body>
</body>
</html>