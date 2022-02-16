<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<html>
<meta name="decorator" content="/template/template5_BS.jsp">
<head>
<script type="text/javascript">
	var dialog;
	var grid;
	var ruleId;
	var idData=[];
	
	$(function() {
	    initGrid();
		initButtons1();
	    initButtons();
	});
	
	function initGrid() {
		grid = $("#mainform").ligerGrid({
		columns : [ {
			display : '报警内容',
			name : 'warnCont',
			width : '25%',
			align : 'center',
			editor: { 
				type: 'text'
			}
		},{
			display : '报警方式',
			name : 'warnMethod',
			width : '15%',
			align : 'center',
			editor: { 
				type: 'select',
				isShowCheckBox: true, 
				isMultiSelect: true, 
				valueField: 'id', 
				textField: 'value',
				data : [{
						value : '站内信',
						id : "站内信"
				    },{
				    	value : '邮件',
						id : '邮件'
				    }, {
				    	value : '短信',
						id : '短信'
					}]
			}
		},{
			display:'开始值(>=)',
			name:'startVal',
			width:'15%',
			align:'left',
			editor: { 
				type: 'int'
			}
		}, {
			display : '结束值(<)',
			name : 'endVal',
			width : '15%',
			align:'left',
			editor: { 
				type: 'int'
			}
		}, {
			display : '单位',
			name : 'warnUnit',
			width : '20%',
			align : 'center',
			editor: { 
				type: 'select',
				isShowCheckBox: true, 
				isMultiSelect: true, 
				valueField: 'id', 
				textField: 'value',
				data : [{
						value : '百分比',
						id : "百分比"
				    },{
				    	value : '其他',
						id : '其他'
				    }]
			}
		}],
		checkbox : true,
		enabledEdit : true,
		rownumbers : true,
		isScroll : false,
		alternatingRow : true,//附加奇偶行效果行
		colDraggable : false,
		dataAction : 'server',//从后台获取数据
		method : 'post',
		url : "${ctx}/east/rules/warn/warnRule",
		parms : {
			ruleId : ${ruleId}
		},
		sortName : 'ruleId',//第一次默认排序的字段
		sortOrder : 'asc', //排序的方式 
		width : '100%',
		height : '100%',
		toolbar : {}
	})
	};
	
	//初始化按钮
    function initButtons1() {
		var btns = [{
            text : '添加',
            click : f_addRow,
            icon : 'fa-plus'
        }, {
            text : '删除',
            click : f_delete,
            icon : 'fa-trash-o'
        }];

		BIONE.loadToolbar(grid, btns, function() {});
		jQuery.metadata.setType("attr", "validate");
		BIONE.validate("#mainform");
	};
	
	function initButtons() {
		buttons = [];
		buttons.push({
			text : '取消',
			onclick : function(){
				window.BIONE.closeDialog("addWarn");
			} 
		}, {
			text : '完成',
			onclick : f_save
		});
		
		BIONE.addFormButtons(buttons);
		jQuery.metadata.setType("attr", "validate");
		BIONE.validate("#mainform");
	};
	
	// 保存
	function f_save(){
		var warnData = grid.data.Rows;
		var wData = [];
		for (var i = 0; i < warnData.length; i++) {
			var __status = warnData[i].__status;
			if ("delete" != __status) {
				wData.push(warnData[i]);
			}
		}
		$.ajax({
			cache : false,
			async : true,
			url : "${ctx}/east/rules/warn/save",
			dataType : 'json',
			type : "POST",
			data : {
				"ruleId" : ${ruleId},
				"warnData" : JSON.stringify(wData)
			},
			success : function(result) {
                if(result && result.addNo == "ok"){
                    BIONE.tip('保存成功');
                    grid.loadData();
                }
            },
            error : function(result, b) {
                BIONE.tip('保存错误 <BR>错误码：' + result.status);
            }
		});
	}
	
	// 增加
    function f_addRow() {
        grid.addRow({
            id : 0,
			name:'',
			type:'url',
			value:''
        });
    } 
	// 删除
    function f_delete() {
    	var rows = grid.getSelectedRows();
        if(rows.length>0) {
            grid.deleteSelectedRow();
        } else {
            BIONE.tip('请至少选择一条记录');
        }
    }
	
</script>
</head>
<body>
<div id="template.center" style="height: 100%">
		<form id="mainform" action="${ctx}/bione/admin/userauth/authLDAP/saveLdap" method="post"></form>
	</div>
</body>
</html>