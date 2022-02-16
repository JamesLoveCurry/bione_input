<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<html>
<head>
<meta name="decorator" content="/template/template11_BS.jsp">
<script type="text/javascript">
	var tabObj;
	var dialog;
	var grid;
	var grid1;
	var grid2;
	var typeCdVal;
	var tabNameVal;

    $(function() {
        initGrid1();
        initGrid2();
        initForm();
        initGrid();
    });

	//初始化form
    function initForm() {
		//搜索框初始化
		$("#search").ligerForm({
			fields : [{
				display : "所属数据类型",
				name : "tabName",
				newline : false,
				labelWidth : 140, 
				width : 150, 
				type : "select",
				cssClass : "field",
				comboboxName : "ruleTypeBox",
				options : {
					url : "${ctx}/east/rules/business/tab/tabList",
				},
				attr : {
					field : "tabName",
					op : "like"
				}
			}, {
			display : "校验类型",
			name : "typeCd",
			newline : false,
			labelWidth : 140, 
			width : 150, 
			type : "select",
			cssClass : "field",
			options : {
			    data : [{
					text : '空值校验',
					id : "空值"
			    },{
					text : '取值范围校验',
					id : '取值范围'
			    }, {
					text : '格式校验',
					id : '格式'
				}, {
					text : '表内逻辑校验',
					id : '表内逻辑'
				}, {
					text : '表间逻辑校验',
					id : '表间逻辑'
				}, {
					text : '总分核对校验',
					id : '总分核对'
				}, {
					text : '记录数校验',
					id : '记录数'
				}, {
					text : '脱敏校验',
					id : '脱敏'
				}, {
					text : '跨监管一致性校验',
					id : '跨监管一致性'
				}]
			},
			attr : {
				op : "=",
				field : "typeCd"
			}
		}]
		})
	};

	function initGrid() {
		grid = $("#maingrid").ligerGrid({
			columns : [ {
				display : '规则标识',
				name : 'ruleId',
				width : '10%',
				align : 'center'
			},{
				display : '规则名称',
				name : 'ruleName',
				width : '20%',
				align : 'center'
			},{
				display:'校验类型',
				name:'typeCd',
				width:'10%',
				align:'left',
				render : function(row){
					return row.typeCd + '校验';
				}
			}, {
				display : '所属数据类型',
				name : 'tabName',
				width : '10%',
				align : 'left'
			}, {
				display : '创建时间',
				name : 'createTm',
				width : '10%',
				align : 'center',
				type:"date",
				format:"yyyy-MM-dd hh:mm:ss"
			}, {
				display : '最后修改时间',
				name : 'updateTm',
				width : '10%',
				align : 'center',
				type:"date",
				format:"yyyy-MM-dd hh:mm:ss"
			}, {
				display : '最后修改人',
				name : 'updateUser',
				width : '10%',
				align : 'center'
			}, {
				display : '状态',
				name : 'ruleSts',
				align : 'center',
				width : '10%',
				sortname : "ruleSts",
				render : function(row){
					return renderHandler(row);
				}
			}],
			checkbox : true,
			rownumbers : true,
			isScroll : true,
			alternatingRow : true,//附加奇偶行效果行
			colDraggable : false,
			dataAction : 'server',//从后台获取数据
			method : 'post',
			url : "${ctx}/east/rules/checkRule/queryList",
			parms : {
				typeCd : typeCdVal,
				tabName : tabNameVal
			},
			sortName : 'typeCd',//第一次默认排序的字段
			sortOrder : 'desc', //排序的方式
			width : '100%',
			height : '100%',
			toolbar : {},
			onAfterShowData : function(){
				if (!$.browser.msie || parseInt($.browser.version, 10) >= 9) {
					if($(".rcswitcher").length > 0){
						$(".rcswitcher").rcSwitcher({
							onText: '启用',
							offText: '停用',
							height:16.5,
							autoFontSize : true
						}).on({
							'turnon.rcSwitcher': function( e, dataObj ){
								var id = dataObj.$input.attr("id");
								var rowId = dataObj.$input.attr("rowid");
								if(id && rowId){
									changeSts(id , 'Y' , rowId);
								}
						    },
						    'turnoff.rcSwitcher': function( e, dataObj ){
								var id = dataObj.$input.attr("id");
						    	var rowId = dataObj.$input.attr("rowid");
								if(id && rowId){
									changeSts(id , 'N' , rowId);
								}
						    }			
						});
					}
				}
			} 
		})
	};
	
	function initGrid1() {
		grid1 = $("#maingrid1").ligerGrid({
			columns : [ {
				display : '规则类型',
				name : 'typeName',
				width : '80%',
				align : 'center'
			}],
			checkbox : false,
			isScroll : true,
			rownumbers : true,
			usePager : false,
			dataAction : 'server',//从后台获取数据
			method : 'post',
			url : "${ctx}/east/rules/checkRule/type/queryList",
			width : '100%',
			height : '100%',
			sortName : 'typeId',//第一次默认排序的字段
			sortOrder : 'asc', //排序的方式
			toolbar : {}, 
			onSelectRow : onSelectRow,
			onUnSelectRow : onUnSelectRow
		})
	};
	
	function initGrid2() {
		grid2 = $("#maingrid2").ligerGrid({
			columns : [ {
				display : '表名',
				name : 'tabName',
				width : '80%',
				align : 'center'
			}],
			checkbox : false,
			isScroll : true,
			rownumbers : true,
			usePager : false,
			dataAction : 'server',//从后台获取数据
			method : 'post',
			url : "${ctx}/east/rules/business/tab/queryList",
			width : '100%',
			height : '100%',
			sortName : 'tabNo',//第一次默认排序的字段
			sortOrder : 'asc', //排序的方式
			toolbar : {}, 
			onSelectRow : onSelectRow,
			onUnSelectRow : onUnSelectRow
		})
	};

	
	function onSelectRow(rowdata, rowid, rowobj) {
		grid.set('parms', {
			typeCd : rowdata.typeName,
			tabName : rowdata.tabName
		});
		grid.loadData(); 
	}
	
	function onUnSelectRow(rowdata, rowid, rowobj) {
		grid.set('parms', {
		});
		grid.loadData(); 
	}

    // 状态显示,停/启用等
    function renderHandler(row) {
    	if ($.browser.msie && parseInt($.browser.version, 10) < 9) {
			if ("Y"== row.ruleSts){
				return "启用"
			} else {
				return "停用";
			}
		} else {
			var html = "<input class='rcswitcher' type='checkbox' name='check"+row.ruleId
					+"' id='"+row.ruleId+"' rowId='"+row.__id+"' ";
			if(row.ruleSts == "Y"){
				html += "checked";
			}
			html += " />";
			return html;
		}
    }
</script>
</head>
<body>
<div id="template.left">
    <div id="maingrid2" class="maingrid" style="width: 100%;height : 60%"></div>
    <div id="maingrid1" class="maingrid" style="width: 100%;height : 40%"></div>
</div>
</body>
</html>