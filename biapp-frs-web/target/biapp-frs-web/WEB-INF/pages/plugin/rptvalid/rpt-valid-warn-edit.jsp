<%@ page language="java" pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<html>
<head>
<meta name="decorator" content="/template/template5.jsp">
<script type="text/javascript" src="${ctx}/js/color/jscolor.js"></script>
<script type="text/javascript">
jQuery.validator.addMethod("greaterThan", function(value, element, params) {
	if (value == '' || value == null) {
		// edit by caiqy,have no endtime , return
		return true;
	}
	var ele = $("[name=" + params + "]");
	return value > ele.val() ? true : false;
}, "结束日期应当大于开始日期");
jQuery.validator.addMethod("validStartDate", function(value, element, params) {
	if(verStartDate != null){
		return value >= verStartDate;
	}
	return false;
}, "开始日期应该大于等于模板的开始时间");

	var groupicon = "${ctx}/images/classics/icons/communication.gif";
	var maingrid;
	var map = {};
	var mainform;
	var verStartDate;
	var indexCatalogNo = '${indexCatalogNo}';
	var indexNo = '${indexNo}';
	var checkId = '${checkId}';
	var indexNm = '${indexNm}';
	var editFlag = '${editFlag}';
	
	
	$(function() {
		initForm();
		initGrid();
		jQuery.metadata.setType("attr", "validate");
		BIONE.validate($("#mainform"));
		var buttons = [];
		buttons.push({
		    text : '取消',
		    onclick : function() {
		    	window.parent.closeDsetBox(editFlag);
		    }
		});
		buttons.push({
		    text : '下一步',
		    onclick : f_cache
		}); 
		BIONE.addFormButtons(buttons);
		$(document).live("click", function(){
			jscolor.bind();
		});
		 $(".color").live("mousedown", function(){
			grid.endEdit();
			$("#" + this.id).focus();
			$("#" + this.id).trigger("click");
			
			
		}); 
		$(".color").live("change", function(){
			map[this.id] = this.value;
		});
	});
	function initForm() {
		mainform = $("#mainform")
				.ligerForm(
						{
							inputWidth : 250,
							fields : [{
										name : "indexNo",
										type : "hidden"
									},{
										name : "indexVerId",
										type : "hidden"
									},{
										group : "警示校验属性",
										groupicon : groupicon,
										display : "校验名称",
										name : 'checkNm',
										newline : true,
										type : 'text',
										validate : {
										    required : true,
										    remote:{
												url:"${ctx}/report/frame/rptvalid/warn/testSameCheckNm",
												type : "post",
												data : {
													checkId : "${checkId}"?"${checkId}":""
												}
											},
											messages:{remote:"该校验公式名称已存在"}
										}
									},{
										display : "对象名称",
										name : 'indexNm',
										newline : false,
										type : 'text',
										validate : {
											required : true
										}
									},{
										display : "比较值类型",
										name : 'compareValType',
										newline : true,
										type : 'select',
										options :{
											data : [{"id":"00","text":"常量"},{"id":"01","text":"上日"},
											        {"id":"02","text":"月初"},{"id":"03","text":"上月末"},
											        {"id":"04","text":"上月同期"},{"id":"05","text":"季初"},
											        {"id":"06","text":"上季末"},{"id":"07","text":"年初"},
											        {"id":"08","text":"上年末"},{"id":"09","text":"上年同期"}],
											onSelected : function(value) {
											if(value == '00'){ 
												if($.ligerui.get("rangeType")){
													$.ligerui.get("rangeType").selectValue("01");
													$.ligerui.get("rangeType").set('disabled', true);
												}
											}else{
												if($.ligerui.get("rangeType")){
												    $.ligerui.get("rangeType").set('disabled', false);
												}
											}
										}       
										},
										validate : {
											required : true
										}
									},{
										display : '幅度类型',
										name : 'rangeType',
										newline : false,
										type : 'select',
										options : {
											data : [{"id":"01","text":"数字"},{"id":"02","text":"百分比"}]
										},
										validate : {
											required : true
										}

									},{
										display : "开始日期",
										name : "startDate",
										newline : true,
										type : "date",
										options : {
											format : "yyyyMMdd"
										},
										validate : {
											required : true,
											validStartDate : false,
											messages : {
												required : "开始日期不能为空"
											}
										}
									} ,{
										display : '分级信息',
										name : 'levelInfo',
										newline : true,
										width : '650'
									}, {
										display : '备注',
										name : 'remark',
										newline : true,
										type : 'textarea',
										validate : {
											maxlength : 250
										},
										width : 650,
										attr : {
											style : "resize:none; height:50px"
										}
									} ]
						});
		$("#mainform input[name=indexNo]").val(indexNo);
		$("#mainform input[name=indexVerId]").val(indexVerId);
		$("#mainform input[name=indexNm]").val(indexNm);
		$("#mainform input[name=indexNm]").attr("readonly", "true");
		
		jQuery.metadata.setType("attr", "validate");
		BIONE.validate($("#mainform"));
		var tempLi = $("#levelInfo").parent().parent();
		var tipContent = [];
		tipContent
				.push('<li style="width: 100%"><div id="maingrid" name="levelInfoGrid" style="width: 100%;"></div></li>');
		tempLi.html(tipContent.join(''));
		
		if("${checkId}"){
			$.ajax({
				cache : false,
				async : true,
				url : "${ctx}/report/frame/rptvalid/warn/${checkId}?indexNo=${indexNo}&d="
						+ new Date().getTime(),
				dataType : 'json',
				type : "get",
				beforeSend : function() {
					BIONE.loading = true;
					BIONE.showLoading("正在加载数据中...");
				},
				complete : function() {
					BIONE.loading = false;
					BIONE.hideLoading();
				},
				success : function(result) {
					if (!result)
						return;
					$("#mainform input[name=checkNm]").val(result.checkNm);
					$("#mainform input[name=indexNo]").val(result.indexNo);
					$("#mainform input[name=indexNm]").val(result.indexNm);
					$("#mainform input[name=indexNm]").attr("readonly", "true");
					liger.get("rangeType").selectValue(result.rangeType);
					liger.get("compareValType").selectValue(result.compareValType);
					$("#mainform input[name=unit]").val(result.unit);
					$("#mainform input[name=startDate]").val(result.startDate);
					$("#mainform textarea[name=remark]").val(result.remark);
					
				},
				error : function(result, b) {
					BIONE.tip('发现系统错误 <BR>错误码：' + result.status);
				}
			});
		}
	}

	function f_cache(){
		parent.canSelect=true;
		parent.datasetObj.checkId = checkId;
		parent.datasetObj.indexNo = indexNo;
	    parent.datasetObj.saveData = f_save;
		parent.next(editFlag);
	}
	
	function f_save() {
		grid.endEdit();
		jQuery.metadata.setType("attr", "validate");
		if ($("#mainform").valid()) {
			var levelInfo = "";
			if (grid) {
					grid.endEdit();
					
					var rows = grid.getData(), saveData = [];
					var flag = false;
					for ( var i = 0, l = rows.length; i < l; i++) {
						if(!rows[i].levelNm || rows[i].levelNm == ""){
							BIONE.tip("第" + (i + 1) + "行的级别名称不能为空！");
							return;
						}
						if(rows[i].isPassCond == "1"){
							flag = true;
						}
						rows[i].remindColor = map[i];
						saveData.push(rows[i]);
					}
					if(flag == false){
						BIONE.tip("至少有一个警示级别需要通过条件!");
						return ;
					}
					levelInfo = JSON2.stringify(saveData);
				}
			
			var data = {
				checkId : "${checkId}",
				checkNm : $("#mainform input[name='checkNm']").val(),
				levelInfo : levelInfo,
				indexNo : $("#mainform input[name='indexNo']").val(),
				compareValType : $.ligerui.get("compareValType").getValue(),
				rangeType : $.ligerui.get("rangeType").getValue(),
				unit : $("#mainform input[name='unit']").val(),
				remark : $("#mainform textarea[name='remark']").val(),
				startDate : $("#mainform input[name='startDate']").val(),
				endDate : '29991231'
			};
				$.ajax({
					type : "POST",
					dataType : 'json',
					url : "${ctx}/report/frame/rptvalid/warn",
					data : data,
					success : function() {
						if("${checkId}"){
							window.parent.parent.frames["warn"].reloadGrid();
							BIONE.tip("修改成功");
						}else{
							window.parent.parent.reloadGrid();
							BIONE.tip("增加成功");
						}
						window.parent.closeDsetBox(editFlag);
					},
					error : function(msg) {
						BIONE.tip("保存失败");
					}
				});
			} else {
				BIONE.tip("存在字段验证不通过，请检查！");
			}
	}
	function initGrid() {
		var g = {
			width : "99%",
			height : "180",
			columns : [{
				display : "级别名称",
				name : "levelNm",
				width : "20%",
				editor : {
					type : 'text'
				},
				isSort : false
			},  {
				display : "警示类型",
				name : "levelType",
				width : '20%',
				render : function(row){
					if(row.levelType == "01"){
						return "内置分级";
					}else{
						return "自定义分级";
					}
				}
				
			}, {
				display : "提醒颜色",
				name : "remindColor",
				width : "15%",
				render : function(row) {
					return renderColor(row.__index, row.remindColor);
				},
				isSort : false
			} , {
				display : "正向幅度值",
				name : "postiveRangeVal",
				width : "15%",
				editor :{
					type : 'float'
				},
				isSort : false
			}, {
				display : "负向幅度值",
				name : "minusRangeVal",
				width : "15%",
				editor : {
					type : 'float'
				},
				isSort : false
			},{
				display : "通过条件",
				name : "isPassCond",
				width : "10%",
				editor : {
					type : 'select',
					data : [{"id":"1","text":"是"},{"id":"0","text":"否"}]
				},
				render : function(data){
					if(data.isPassCond == "1")
						return "是";
					if(data.isPassCond == "0")
						return "否";
				},
				isSort : false
			}],
			enabledEdit : true,
			clickToEdit : true,
			checkbox : false,
			usePager : false,
			isScroll : true,
			rownumbers : true,
			alternatingRow : true,//附加奇偶行效果行
			colDraggable : true,
			dataAction : 'server',//从后台获取数据
			method : 'post',
			url : "${ctx}/report/frame/rptvalid/warn/getLevelInfo.json?checkId=${checkId}",
			toolbar : {}
		};
		//渲染GRID
		grid = $("#maingrid").ligerGrid(g);
		
		var btns = [ {
			text : "添加",
			icon : "add",
			click : addNewRow
		}, {
			text : "删除",
			icon : "delete",
			click : delRow
		} ];
		BIONE.loadToolbar(grid, btns, function() {
		});
		$("div.l-panel-bwarp").css("border-bottom", "1px solid #C6C6C6");
	}
	
	function renderColor(id, color){
		if (!this.map[id]) {
			color || (color = "FFFFFF");
			this.map[id] = color;
		}
		return '<input id="'+id+'" type="text" class="color" style="width:90%;background-color:#'+this.map[id]+';" readonly="readonly" autocomplete="on" value="'+this.map[id]+'">';
	}

	function addNewRow() {
		grid.addEditRow();
	}
	function modifyRow() {
		var row = grid.getSelectedRow();
		if (!row) {
			alert('请选择行');
			return;
		}
		grid.beginEdit(row);
	}
	function delRow() {
		var rows = grid.getSelectedRows();
		if(rows[0].levelType == "01"){
			BIONE.tip("内置分级不允许删除!");
			return;
		}
		if(rows.length){
			for(var i=0;i<rows.length;i++){
				
				for(var j=rows[i].__index;j<grid.getData().length - 1;j++){
					map[j] = map[j+1];	
				}
				delete map[grid.getData().length - 1]; 
				
				grid.deleteRow(rows[i]);	
			}
		}
		grid.endEdit();
	}
</script>
</head>
<body>
	<div id="template.center">
		<form name="mainform" method="post" id="mainform">
		</form>
		<div style="width: 1px; height: 1px; overflow: hidden;">
			<input id="hiddenColorInput" />
		</div>
		<!-- <div id="maingrid"></div> -->

	</div>
</body>
</html>