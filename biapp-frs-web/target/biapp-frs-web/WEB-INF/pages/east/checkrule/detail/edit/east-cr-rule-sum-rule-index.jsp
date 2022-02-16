<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<html>
<meta name="decorator" content="/template/template5_BS.jsp">
<head>
<script type="text/javascript">
	var dialog,grid,data,ruleId,typeCd;
	var mainform;
	
	$(function() {
		init();
		initForm();
        initGrid();
		initBtn();
	    initButtons();
	});
	
	function init() {
		ruleId = ${ruleId};
		typeCd = "${typeCd}";
	}
	
	function initForm(){
		mainform = $("#form1").ligerForm({
			labelWidth : 80,
			inputWidth : 240,
			fields : [{
				name : "dispCd",
				type : "hidden"
			}, {
				display : "规则标识",
				name : "ruleId",
				newline : true,
				type : "text",
				cssClass : "field",
				group : "基本信息", 
				groupicon : "${ctx}/images/classics/icons/communication.gif"
			},{
				display : "检验类型",
				name : "typeCd",
				newline : true,
				type : "text",
				cssClass : "field"
			},{
				display : "检验名称",
				name : "ruleName",
				newline : true,
				type : "text",
				cssClass : "field",
				validate : {
					required : true
				}
			},{
				display : "表名",
				name : "tabName",
				newline : true,
				type : "text",
				cssClass : "field"
			},{
				display : "字段名",
				name : "colName",
				newline : true,
				type : "text",
				cssClass : "field"
			},{
				display : "是否启用",
				name : "ruleSts",
				newline : true,
				type : "select",
				cssClass:"field",
				options:{
					data:[{
						text:"是",
						id : "Y"
					},{
						text:"否",
						id : "N"
					}]
				}
			},{
				display:"前置条件",
				name : "cond",
				newline : true,
				type:"textarea",
				cssClass:"field"
			},{
				display : "最大相对误差",
				name : "relative",
				newline : true,
				type : "number",
				cssClass : "field",
				group : "汇总型校验总表", 
				groupicon : "${ctx}/images/classics/icons/communication.gif"
			},{
				display : "分组中文字段名",
				name : "grpColName",
				newline : true,
				type : "text",
				cssClass : "field"
			},{
				display : "汇总计算表达式",
				name : "expr",
				newline : true,
				type : "text",
				cssClass : "field"
			}]
		});
		$("#form1 input[name=ruleId]").attr("readonly", "true").removeAttr("validate");
		$("#form1 input[name=typeCd]").attr("readonly", "true").removeAttr("validate");
		$("#form1 input[name=tabName]").attr("readonly", "true").removeAttr("validate");
		$("#form1 input[name=colName]").attr("readonly", "true").removeAttr("validate");
	}
	
	BIONE.loadForm(mainform, {
	    url : "${ctx}/east/rules/checkRule/sum/${ruleId}"
	});

	jQuery.metadata.setType("attr", "validate");
	BIONE.validate($("#form1"));

	var managers = $.ligerui.find($.ligerui.controls.Input);
	for ( var i = 0, l = managers.length; i < l; i++) {
	    //改变了表单的值，需要调用这个方法来更新ligerui样式
	    managers[i].updateStyle();
	}

	function initGrid() {
		grid = $("#maingrid").ligerGrid({
			columns : [ {
				display : '表名',
				name : 'tabName',
				width : '20%',
				align : 'center',
				editor: { 
					type: 'text'
				}
			},{
				display : '字段名',
				name : 'grpColName',
				width : '20%',
				align : 'center',
				editor: { 
					type: 'text'
				}
			},{
				display : '汇总计算表达式',
				name : 'wxpr',
				width : '20%',
				align : 'center',
				editor: { 
					type: 'text'
				}
			},{
				display : '前置条件表达式',
				name : 'cond',
				width : '20%',
				align : 'center',
				editor: { 
					type: 'text'
				}
			}],
			checkbox : true,
			enabledEdit : true,
			rownumbers : true,
			isScroll : true,
			alternatingRow : true,//附加奇偶行效果行
			colDraggable : false,
			dataAction : 'server',//从后台获取数据
			method : 'post',
			url : "${ctx}/east/rules/checkRule/sum/subList",
			parms : {
				ruleId : ${ruleId}
			},
			sortName : 'tabName',//第一次默认排序的字段
			sortOrder : 'asc', //排序的方式
			width : '100%',
			height : '100%',
			toolbar : {}
		})
	};
	
	//初始化按钮
	function initBtn(){
		var buttons = [];
		buttons.push({
		    text : '取消',
		    onclick : function() {
				BIONE.closeDialog("editRule", null);
		    }
		});
		buttons.push({
		    text : '保存',
		    onclick : save
		});
		BIONE.addFormButtons(buttons);
	}
	
	//初始化按钮
    function initButtons() {
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
	};
	
	//保存按钮-任务保存方法
	function save() {
		var formData = {};
		formData["ruleId"] = $("#form1 input[name=ruleId]").val();
		formData["typeCd"] = $("#form1 input[name=typeCd]").val();
		formData["tabName"] = $("#form1 input[name=tabName]").val();
		formData["colName"] = $("#form1 input[name=colName]").val();
		formData["dispCd"] = $("#form1 input[name=dispCd]").val();
		formData["ruleName"] = $("#form1 input[name=ruleName]").val();
		formData["ruleSts"] = $("#form1 input[name=ruleSts]").val();
		formData["cond"] = $("#form1 input[name=cond]").val();
		formData["relative"] = $("#form1 input[name=relative]").val();
		formData["grpColName"] = $("#form1 input[name=grpColName]").val();
		formData["expr"] = $("#form1 input[name=expr]").val();

		var subData = grid.data.Rows;
		var sData = [];
		for (var i = 0; i < subData.length; i++) {
			var __status = subData[i].__status;
			if ("delete" != __status) {
				sData.push(subData[i]);
			}
		}
		
		$.ajax({
			cache : false,
			async : true,
			url : "${ctx}/east/rules/checkRule/sum/save",
			dataType : 'json',
			type : "POST",
			data : {
				"ruleId" : ${ruleId},
				"formData" : JSON.stringify(formData),
				"subData" : JSON.stringify(sData)
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
		
		/* BIONE.submitForm($("#form1"), function() {
		    BIONE.closeDialogAndReloadParent("editRule", "maingrid", "保存成功");
		}, function() {
		    BIONE.closeDialog("editRule", "保存失败");
		}); */
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
	<div id="template.center">
		<div id="left" style="width:50%; height:100%; float:left;">
			<div id="form1" class="maingrid"></div>
		</div>
		<div id="right" style="width:50%; height:100%; float:left;">
			<div id="maingrid" class="maingrid"></div>
		</div>
	</div>
</body>
</html>