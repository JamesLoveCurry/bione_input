<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<html>
<head>
<meta name="decorator" content="/template/template14.jsp">
<script type="text/javascript">

	var groupicon = "${ctx}/images/classics/icons/communication.gif";
	var mainRptId = "";
	var catalogId = "${catalogId}";
	var catalogNm = "${catalogNm}";
	var mainform;
	var grid;
	
	var uptOldObj = {
		busiType : ""
	};
	
	jQuery.validator.addMethod("numReg", function(value, element) {
	    var numReg = /^[0-9a-zA-z\\-]*$/;
	    return this.optional(element) || (numReg.test(value));
	}, "编码格式不合法");
	
	$(function(){
    	$("#attach_div").height(180);
		// 初始化表单
		initForm();
		initGrid();
		initCatalogNm();
		//初始化字段配置信息
		initEditInfo();
	});

	//初始化字段配置信息
	function initEditInfo(){
		if(window.parent.operFlag == "edit"){
			$.ajax({
				cache : false,
				async : true,
				url : "${ctx}/frs/submitConfig/detailrpt/getEditInfo?editType=1&rptId="+window.parent.rptId,
				dataType : 'json',
				type : "post",
				success : function(result){
					if(window.parent.checkedSearch.length<1&&window.parent.uncheckedSearch.length<1){
						for(i=0;i<result.checked.length;i++){
							window.parent.checkedSearch.push(result.checked[i]);
							window.parent.checkedSearchCN.push(result.checkedCN[i]);
						}
						for(i=0;i<result.unchecked.length;i++){
							window.parent.uncheckedSearch.push(result.unchecked[i]);
							window.parent.uncheckedSearchCN.push(result.uncheckedCN[i]);
						}
					}
				}
			})
			$.ajax({
				cache : false,
				async : true,
				url : "${ctx}/frs/submitConfig/detailrpt/getEditInfo?editType=2&rptId="+window.parent.rptId,
				dataType : 'json',
				type : "post",
				success : function(result){
					if(window.parent.checkedShow.length<1&&window.parent.uncheckedShow.length<1){
						for(i=0;i<result.checked.length;i++){
							window.parent.checkedShow.push(result.checked[i]);
							window.parent.checkedShowCN.push(result.checkedCN[i]);
						}
						for(i=0;i<result.unchecked.length;i++){
							window.parent.uncheckedShow.push(result.unchecked[i]);
							window.parent.uncheckedShowCN.push(result.uncheckedCN[i]);
						}
					}
				}
			})
		}
	}
	
	function initGrid() {
		grid = $('#grid').ligerGrid({
			title: '附件',
			headerImg: '${ctx}/images/classics/icons/attach.png',
			columns: [{
				display: '名称',
				name: 'attachName',
				align: 'left',
				width: 400
			}, {
				display: '类型',
				name: 'attachType',
				align: 'center',
				width: 100
			}],
			width: '100%',
			height: 170,
			toolbar: {
				items: [{
					text : '增加',
					click : attach_add,
					icon : 'add',
					operNo : 'attach_add'
				}, {
					text : '删除',
					click : attach_delete,
					icon : 'delete',
					operNo : 'attach_delete'
				}]
			},
			usePager: false,
			checkbox: false,
			dataAction: 'server',
			url: '${ctx}/report/frame/design/cfg/listTmpAttach?rptId='+window.parent.rptId,
			method: 'get'
		});
	}
	
	function initForm(){
		mainform = $("#baseform");
		mainform.ligerForm({
			inputWidth : 150, labelWidth : 105,
			fields : [ {
				name : "rptId"  ,
				type : "hidden" 
			}, {
				name : "createTimeStr",
				type : "hidden"
			}, {
				name : "templateId" ,
				type : "hidden"
			}, {
				name : "catalogId",
				type : "hidden"
			}, {
				name : "extType",
				type : "hidden"
			}, {
				display : "报表编码",
				name : "rptNum",
				newline : true,
				type : "text",
				group : "基础信息",
				groupicon : groupicon,
				validate : {
					required : true,
					maxlength : 32,
					numReg : true
				}
			} , {
				display : "报表名称",
				name : "rptNm",
				newline : false,
				type : "text",
				validate : {
					required : true,
					maxlength : 100
				}
			}, {
				display : "数据库表",
				name : "table",
				newline : true,
				type : "select",
				width : 435,
				comboboxName: "table_box",
				options : {
					onBeforeOpen : function() {
						if(!(window.parent.operFlag=="edit")){
							 var url = "${ctx}/rpt/frame/dataset/tablePage?dsId=580363699dd645a584119bd21b241282&datasetId=1104detail";
							dialog = window.parent.BIONE
									.commonOpenDialog('请选择物理表',
											"selectGrid", "700",
											"350", url);
						}
						return false; 
					}
				}
			}, {
				display: '报表目录',
				name :'catalogNm',
				type: 'text',
				newline: true
			}, {
				display : "报表类型",
				name : "templateType",
				newline : false,
				type : "select",
				comboboxName : "templateTypeCombo",
				options : {
					initValue:'05',
					data : [ {
						text : '汇总类',
						id : '00'
					},{
						text : '明细类',
						id : '01'
					},{
						text : '单元格类',
						id : '02'
					},{
						text : '综合类',
						id : '03'
					},{
						text : '指标列表类',
						id : '04'
					},{
						text : '查询明细类',
						id : '05'
					}]
				}
			}, {
				display : "业务类型",
				name : "busiType",
				newline : true,
				type : "select",
				comboboxName:"busiTypeBox",
				options : {
					data : [ {
						text : '1104监管',
						id : '02'
					},{
						text : '人行大集中',
						id : '03'
					}]
				}
			}, {
				display : "定义部门",
				name : "defDept",
				newline : false,
				type : "text",
				validate : {
					maxlength : 100
				}
			},{
				display : "报表状态",
				name : "rptSts",
				newline : true,
				type : "select",
				comboboxName : "rptStsCombo",
				options : {
					initValue:'Y',
					data : [ {
						text : '启用',
						id : 'Y'
					}, {
						text : '停用',
						id : 'N'
					} ]
				}
			},{
				display : "业务定义",
				name : "rptDesc",
				newline : true,
				type : "textarea",
				width : 800,
				attr : {
					style : "resize: none;"
				},
				validate : {
					maxlength : 500
				}
			}/* ,{
				display : "填报说明",
				name : "fillDesc",
				newline : true,
				type : "textarea",
				width : 800,
				attr : {
					style : "resize: none;"
				}
			} */]
		});
		$("#busiType").css("width","410");
		jQuery.metadata.setType("attr", "validate");
		BIONE.validate(mainform);
		if(window.parent.operFlag == "edit"){
			$("#baseform input[name=table]").attr("readonly", "true");
			//$.ligerui.get("table_box").attr("options","{}");
			$.ajax({
				cache : false,
				async : true,
				url : "${ctx}/frs/submitConfig/detailrpt/getBaseInfo?rptId="+window.parent.rptId,
				dataType : 'json',
				type : "post",
				success : function(result){
					$("#baseform input[name=rptNm]").val(result.rptNm);
					$("#baseform input[name=rptNum]").val(result.rptNum);
					$("#baseform input[name=rptId]").val(result.rptId);
					$("#createTimeStr").val(result.createTimeStr);
					$.ligerui.get('busiTypeBox').selectValue(result.busiType);
					$.ligerui.get('rptStsCombo').selectValue(result.rptSts);
					$.ligerui.get('templateTypeCombo').selectValue(result.rptType);
					$("#baseform input[name=templateId]").val(result.cfgId);
					$("#baseform input[name=catalogId]").val(result.catalogId);
					$("#baseform input[name=extType]").val(result.extType);
					$("#rptDesc").val(result.rptDesc);
				}
			})
			$.ajax({
				cache : false,
				async : true,
				url : "${ctx}/frs/submitConfig/detailrpt/getOtherBaseInfo?rptId="+window.parent.rptId,
				dataType : 'json',
				type : "post",
				success : function(result){
					$("#baseform input[name=defDept]").val(result.DEF_DEPT);
					//$("#fillDesc").val(result.createTimeStr);
					$("#baseform input[name=busiType]").val(result.DEF_DEPT);
					$("#table").val(result.TABLE_NAME_EN);
				 	$.ligerui.get("table_box").setData(result.TABLE_NAME_EN);
				 	$.ligerui.get("table_box").setText(result.TABLE_NAME_EN);
				 	window.parent.tableNameEn = result.TABLE_NAME_EN;
				}
			})
		}else{  //新增情况
			$("#rptNm").rules("add",{remote:{
				url: "${ctx}/report/frame/design/checkRptNm?flag=01",
				type : "post"
			},messages:{remote:"该报表名称已存在"}});
			$("#rptNum").rules("add",{remote:{
				//url:"${ctx}/report/frame/design/cfg/checkRptNo" ,
				url: "${ctx}/report/frame/design/checkRptNo?flag=01",
				type : "post"
			},messages:{remote:"该报表编码已存在"}});
		}
		$("#catalogId").val(catalogId);
		$("#baseform input[name=attachFileNm]").attr("readonly", "true");
	}
	
	function initCatalogNm(){
		$("#catalogNm").val(catalogNm);
		$("#catalogNm").attr("readonly", "true");
		$("#catalogId").val(catalogId);		
	}

	function attach_add() {
		//'${ctx}/bione/message/attach/newAttach'
		BIONE.commonOpenDialog('上传文件', 'attachUpload', 562, 334, '${ctx}/bione/message/attach/newAttach');
	}
	
	function attach_delete() {
		var row = liger.get('grid').getSelectedRow();
		if (row) {
			$.ligerDialog.confirm('确实要删除这些记录吗?', function(yes) {
				if (yes) {
					afterAttachOperat(OPT_DEL_FLAG, row.attachId);
				}
			});
		}
	}
	var OPT_ADD_FLAG = "add";
	var OPT_DEL_FLAG = "delete";
	var cacheAttachsADD = [];
	var cacheAttachsDEL = [];
	function afterAttachOperat(optType, attachInst) {
		//grid.addRows(attachInst);
		/* 新增或删除之后都调用此方法 */
		if (optType == OPT_ADD_FLAG) {
			cacheAttachsADD.push(attachInst);
			grid.addRows(attachInst);
		}
		else if (optType == OPT_DEL_FLAG) {
			// 删除存的只有id 
			var flag = false;
			if (cacheAttachsADD && cacheAttachsADD.length>0) {
				for (var _idx in cacheAttachsADD) {
					if (cacheAttachsADD[_idx].attachId == attachInst) {
						cacheAttachsADD.splice(_idx, 1);
						flag = true;
						break;
					}
				}
			}
			if (!flag) {
				cacheAttachsDEL.push(attachInst);
			}
			grid.deleteSelectedRow() ;
		}
	}

	
	// 准备待保存数据
	function prepareDatas4Save(){
		var saveObj = null;
		window.parent.rptNm = $("#baseform input[name=rptNm]").val();
		if(mainform.valid()){
			$("#mainRptId").val("");
			saveObj = {
					rptId : $("#baseform input[name=rptId]").val(),
					templateId : $("#baseform input[name=templateId]").val(),
					rptNm : $("#baseform input[name=rptNm]").val() ? $.trim($("#baseform input[name=rptNm]").val()) : "",
					rptNum : $("#baseform input[name=rptNum]").val() ? $.trim($("#baseform input[name=rptNum]").val()) : "",
					templateType : $.ligerui.get("templateTypeCombo").getValue(),
					defDept : $("#baseform input[name=defDept]").val(),
					busiType : $.ligerui.get("busiTypeBox").getValue(),
					rptSts : $.ligerui.get("rptStsCombo").getValue(),
                    rptNameEn : $.trim($.ligerui.get('table_box').getValue()),
					rptDesc : $("#rptDesc").val() ? $("#rptDesc").val() : "",
					//fillDesc : $("#fillDesc").val() ? $("#fillDesc").val() : "",
					mainRptId : $("#mainRptId").val() ? $("#mainRptId").val() : "",
					createTimeStr : $("#createTimeStr").val(),
					catalogId : $("#catalogId").val(),
					addAttachs : $.toJSON(cacheAttachsADD),
					delAttachs : $.toJSON(cacheAttachsDEL)
			};
		}
		return saveObj
	}
	
	function loadForm(data) {
		// 根据返回的属性名，找到相应ID的表单元素，并赋值
		for ( var p in data) {
			var ele = $("[name=" + p + "]");
			// 针对复选框和单选框 处理
			if (ele.is(":checkbox,:radio")) {
				ele[0].checked = data[p] ? true : false;
			} else if (ele.attr("ltype") == "radiolist"
					|| ele.attr("ltype") == "checkboxlist") {
				$.ligerui.get(ele.attr("data-ligerid")).setValue(data[p]);
			} else {
				ele.val(data[p]);
			}
		}
		// 下面是更新表单的样式
		var managers = $.ligerui.find($.ligerui.controls.Input);
		for ( var i = 0, l = managers.length; i < l; i++) {
			// 改变了表单的值，需要调用这个方法来更新ligerui样式
			var o = managers[i];
			o.updateStyle();
			o.inputText.blur();
			if (managers[i] instanceof $.ligerui.controls.TextBox)
				o.checkValue();
		}
	}
	
</script>

</head>
<body>
<div id="template.center">
	<form name="baseform" method="post" id="baseform" action="">
	</form>
	<div id="attach_div"  style="width: 98%;height=180; text-align:left;">
		<div id="grid" ></div>
	</div>
</div>
</body>
</html>