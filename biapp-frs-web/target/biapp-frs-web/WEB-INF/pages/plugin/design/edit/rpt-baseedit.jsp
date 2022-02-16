<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<html>
<meta name="decorator" content="/template/template5.jsp">
<head>
<script type="text/javascript">
	// 复制为新报表时，copyRptId不为空。
	var rptId = "${rptId}"
	var copyRptId = "${copyRptId}";
	var defSrc = "${defSrc}";
	var catalogId = "${catalogId}";
	var templateType = "${templateType}";
	var templateId = "${templateId}";
	var verId = "${verId}";
	var isCopy = copyRptId ? true : false;
	var groupicon = "${ctx}/images/classics/icons/communication.gif";
	var isUpt = rptId ? true : false;
	var defaultNum = "";
	var defaultNm = "";
	var defaultOrder = "";
	//创建表单结构 
	var mainform;
	
	$(function() {
		initForm();
		initButtons();
		initData();
	});
	
	function initForm(){
		mainform = $("#mainform");
		mainform.ligerForm({
			labelWidth : "90px",
			tab :{
				items : [
					{
						title : '基本信息',
						fields : [
						    {
						    	name : "copyRptId",
						    	type : "hidden"
						    }, {
								name : "rptId",
								type : "hidden"
							}, {
								name : "verId",
								type : "hidden"
							}, {
								name : "templateId",
								type : "hidden"
							}, {
								name : "catalogId",
								type : "hidden"
							}, {
								name : "defSrc",
								type : "hidden"
							},{
								name : "isCabin",
								type : "hidden"
							},{
								display : "报表编号",
								name : "rptNum",
								newline : true,
								type : "text",
								width : 420,
								validate : {
									required : true,
									specificSymbeol : true
								}
							},{
								display : "报表名称",
								name : "rptNm",
								newline : true,
								type : "textarea",
								width : 420,
								attr : {
									style : "resize: none;"
								}, 
								validate : {
									required : true,
									specificSymbeol : true,
									maxlength : 100
								}
							},{
								display : "启用日期",
								name : "verStartDate",
								newline : true,
								type : "date",
								options : {
									format : "yyyyMMdd"
								},
								validate : {
									messages : {
										required : "启用日期不能为空。"
									}
								}
							} , {
								display : "计算周期",
								name : "rptCycle",
								newline : false,
								type : "select",
								comboboxName : "rptCycleCombo",
								options : {
									initValue:'02',
									data : [ {
										text : '日',
										id : '01'
									}, {
										text : '月',
										id : '02' 
									}, {
										text : '季',
										id : '03' 
									},{
										text : "半年",
										id : "12"
									}, {
										text : '年',
										id : '04' 
									} ]
								},
								validate : {
							   		required : true
								}
							} , {
								display : "默认单位",
								name : "templateUnit",
								newline : true,
								type : "select",
								comboboxName : "templateUnitCombo",
								options : {
									initValue:'04',
									data : [ {
										text : '元(个)',
										id : '01'
									}, {
										text : '百',
										id : '02' 
									}, {
										text : '千',
										id : '03' 
									}, {
										text : '万',
										id : '04' 
									}, {
										text : '亿',
										id : '05' 
									}]
								}
							} , {
								display: '报表目录',
								name :'catalogNm',
								comboboxName:'catalogNmBox',
								type: 'select',
								newline: false,
								options : {
									onBeforeOpen : function() {
										window.parent.selectedTab=window;
										window.parent.BIONE
												.commonOpenDialog(
														"报表目录选择",
														"catalogTreeWin",
														400,
														380,
														'${ctx}/report/frame/design/cfg/catalogEdit?defSrc='+defSrc,
														null);
										return false;
									}
								},
								validate : {
							   		required : true
								}
							}, {
								display : "业务条线",
								name : "rptlineId",
							    type : 'select',
							    newline: true,
							    options : {
							    	url : "${ctx}/report/frame/design/cfg/getLineInfo"
							    },
							    validate : {
							    	required : true
							    }
							},{
								display : "排列顺序",
								name : "rankOrder",
							    type : 'number',
							    newline: false,
							    validate : {
							    	required : true,
							    	digits : true,
							    	maxlength : 5
							    }
							},{
								display : "自动调整列宽",
								name : "isAutoAdj",
								newline : true,
								type : "select",
								comboboxName : "isAutoAdjCombo",
								options : {
									initValue:'Y',
									data : [ {
										text : '是',
										id : 'Y'
									}, {
										text : '否',
										id : 'N' 
									}]
								}
							}
						]
					},{
						title : '业务信息',
							fields : [{
								display : "报表状态",
								name : "rptSts",
								newline : false,
								type : "select",
								comboboxName : "rptStsCombo",
								options : {
									initValue:'N',
									data : [ {
										text : '启用',
										id : 'Y'
									}, {
										text : '停用',
										id : 'N'
									} ]
								}
							},{
								display : "联系人",
								name : "userId",
								newline : true,
								type : defSrc=="01"?"select":"hidden",
								options : {
									onBeforeOpen : function() {
										var url = "${ctx}/report/frame/design/cfg/userEdit";
										window.parent.$window = window;
										dialog = window.parent.BIONE.commonOpenLargeDialog('请选择联系人',
														"selectWin",  url);
										return false;
									}
								}
							},{
								display : "主管部门",
								name : "defDept",
								newline : false,
								type : defSrc=="01"?"select":"hidden",
								options : {
									onBeforeOpen : function() {
										var url = "${ctx}/report/frame/design/cfg/deptEdit";
										window.parent.$window = window;
										dialog = window.parent.BIONE.commonOpenDialog('请选择主管部门',
														"selectWin", 400,500, url);
										return false;
									}
								}
							},{
								display : "联系人电话",
								name : "defTel",
								newline : true,
								type : defSrc=="01"?"text":"hidden",
								attr : {
									readOnly :true
								}
							},{
								display : "联系人邮箱",
								name : "defMail",
								newline : false,
								type : defSrc=="01"?"text":"hidden",
								attr : {
									readOnly :true
								}
							},{
								display : "报表描述",
								name : "rptDesc",
								newline : true,
								type : "textarea",
								width : 410,
								attr : {
									style : "resize: none;"
								},
								validate : {
									maxlength : 500
								}
							}
						]
					}         
				] }
		});
		
		jQuery.metadata.setType("attr", "validate");
		BIONE.validate(mainform);
		initFormStyle();
	}
	
	function initFormStyle(){
		$("#rptDesc").attr("resize","none");
		var formHeight = $("#center").height() - 20;
		$(".ui-tabs-panel").height(formHeight - 50);
		$("#rptNm").css("height" , "35px").css("resize" , "none");
		// 添加相应校验
		$("#rptNm").rules("add",{remote:{
			url:"${ctx}/report/frame/design/cfg/checkRptNm",
			type : "post",
			data : {
				rptId : isCopy?"":rptId,
				defSrc : defSrc
			}
		},messages:{remote:"该报表名称已存在"}});
		
		$("#rptNum").rules("add",{remote:{
			url:"${ctx}/report/frame/design/cfg/checkRptNum",
			type : "post",
			data : {
				rptId : isCopy?"":rptId,
				defSrc : "01"
			}
		},messages:{remote:"该报表编号已存在"}});
		if(!rptId){
			var prefix = "公共报表";
			if(defSrc != "01")
				prefix = "自定义报表";
			defaultNm = prefix +(new Date().getTime());
			$("#rptNm").val(defaultNm);
			$("#rptNm").addClass("l-text-field-null").attr("isNull","true");	
			// 报表序号相应样式
			$("#rankOrder").bind("focusin" , function(){
				if($("#rankOrder").attr("isNull") == "true"){
					$("#rankOrder").removeClass("l-text-field-null").val("");
				}
			});
			$("#rankOrder").bind("focusout" , function(){
				if($("#rankOrder").val() != null
						&& $("#rankOrder").val() != ""){
					$("#rankOrder").attr("isNull","false");
				}else{
					$("#rankOrder").addClass("l-text-field-null").val(defaultOrder).attr("isNull","true");
				}
			});
			
			// 报表名称相应样式
			$("#rptNm").bind("focusin" , function(){
				if($("#rptNm").attr("isNull") == "true"){
					$("#rptNm").removeClass("l-text-field-null").val("");
				}
			});
			$("#rptNm").bind("focusout" , function(){
				if($("#rptNm").val() != null
						&& $("#rptNm").val() != ""){
					$("#rptNm").attr("isNull","false");
				}else{
					$("#rptNm").addClass("l-text-field-null").val(defaultNm).attr("isNull","true");
				}
			});
			
			// 报表编号相应样式
			$("#rptNum").bind("focusin" , function(){
				if($("#rptNum").attr("isNull") == "true"){
					$("#rptNum").removeClass("l-text-field-null").val("");
				}
			});
			
			$("#rptNum").bind("focusout" , function(){
				if($("#rptNum").val() != null
						&& $("#rptNum").val() != ""){
					$("#rptNum").attr("isNull","false");
				}else{
					$("#rptNum").addClass("l-text-field-null").val(window.rptNum).attr("isNull","true");
				}
			});
		}
		
	}
	function initData(){
		$("#defSrc").val(defSrc ? defSrc : "");
		$.ajax({
			   cache : false,
			   async : true,
			   url: "${ctx}/report/frame/design/cfg/getRptBaseInfo?d="+new Date(),
			   data : {
				 rptId : rptId,
				 defSrc : defSrc,
				 catalogId : catalogId,
				 copyRptId : copyRptId
			   },
			   dataType : 'json',
			   type: "GET",  
			   beforeSend : function() {
					BIONE.loading = true;
					BIONE.showLoading("正在加载数据中...");
				},
				complete : function() {
					BIONE.loading = false;
					BIONE.hideLoading();
				},
			   success: function(data){
				  
				   window.rptNum = data.rptNum;
				   $("#rptNum").val(data.rptNum);
					if(data.rptNm
							&& data.rptNm != ""
							&& isCopy === false){
						$("#rptNm").val(data.rptNm).attr("isNull","false").removeClass("l-text-field-null");
					}
					if(rptId){
						verId = data.verId;
						templateId = data.templateId;
						$.ligerui.get("templateUnitCombo").selectValue(data.templateUnit);// 报表单位
						$.ligerui.get("rptCycleCombo").setValue(data.rptCycle);// 报表周期
						$.ligerui.get("verStartDate").setValue(data.verStartDate);// 启用时间
						$.ligerui.get("rptStsCombo").setValue(data.rptSts);// 报表状态
						$.ligerui.get("rankOrder").setValue(data.rankOrder);
						$.ligerui.get("isAutoAdjCombo").setValue(data.isAutoAdj);
						$.ligerui.get("rptlineId").setValue(data.lineId);
						if(data.isCabin)
							$("#isCabin").val(data.isCabin);
						else
							$("#isCabin").val("0");
						if(defSrc == "01"){
							$.ligerui.get("userId")._changeValue(data.userId,data.userName);
							$.ligerui.get("defDept")._changeValue(data.defDept,data.deptName);
							$.ligerui.get("defMail").setValue(data.email);
							$.ligerui.get("defTel").setValue(data.tel);
						}
						else{
							$("#userId").val(data.userId);
							$("#defDept").val(data.defDept);
							$("#defMail").val(data.email);
							$("#defTel").val(data.tel);
						}
						$.ligerui.get("catalogNmBox")._changeValue(data.catalogId,data.catalogNm);
						$("#catalogId").val(data.catalogId);						
					}
					else{
						defaultNum = data.rptNum;
						defaultOrder = data.rankOrder;
						$("#rptNum").addClass("l-text-field-null").attr("isNull","true");	
						$.ligerui.get("rankOrder").setValue(data.rankOrder);
						$("#isCabin").val(data.isCabin);
						if(defSrc == "01"){
							$.ligerui.get("userId")._changeValue(data.userId,data.userName);
							$.ligerui.get("defDept")._changeValue(data.defDept,data.deptName);
							$.ligerui.get("defMail").setValue(data.email);
							$.ligerui.get("defTel").setValue(data.tel);
						}
						else{
							$("#userId").val(data.userId);
							$("#defDept").val(data.defDept);
							$("#defMail").val(data.email);
							$("#defTel").val(data.tel);
						}
						$.ligerui.get("isAutoAdjCombo").setValue("N");
						$.ligerui.get("rptStsCombo").setValue("Y");// 报表状态
						$.ligerui.get("catalogNmBox")._changeValue(data.catalogId,data.catalogNm);
						var nowDate = new Date();
						var nowMonth = (nowDate.getMonth() + 1) + "";
						var nowDateStr = nowDate.getFullYear() + "" + (nowMonth.length > 1 ? nowMonth : ("0" + nowMonth)) + "" + ((nowDate.getDate()+"").length > 1 ? nowDate.getDate() : "0" + nowDate.getDate());
						$("#verStartDate").val(nowDateStr);
						$("#catalogId").val(data.catalogId);
					}
			   }
		}); 
	}
	function searchRankOrder(catalogId){
		$.ajax({
			cache : false,
			async : true,
			url : "${ctx}/report/frame/design/cfg/searchRankOrder",
			dataType : 'json',
			type : "get",
			data : {
				catalogId: catalogId
			},
			success : function(result){
				defaultOrder = result.rankOrder;
				$("#rankOrder").addClass("l-text-field-null").val(defaultOrder).attr("isNull","true");
			}
		});
	}
	
	
	function dialogBeforeCloseFunc() {
		// 刷新树
		var topWindow = window.parent;
		if(isUpt === false){
			//新增
			topWindow = window.parent.parent;
		}
		if(topWindow.searchHandler)
			topWindow.searchHandler();
		if(topWindow.clickNodeType == topWindow.rptNodeType){
			// 若是报表，刷新tab的label
			if(topWindow.mainTab && topWindow.mainTab.setHeader)
				topWindow.mainTab.setHeader("topTab","[报表]"+$("#rptNm").val());
		}	
		if(topWindow.document.getElementById("gridFrame"))
			// 刷新列表
			topWindow.document.getElementById("gridFrame").contentWindow.grid.reload();
	}
	
	function initButtons(){
		var buttons = [];
		buttons.push({
			text : '取消',
			onclick : cancleHandler
		});
		if(!rptId){			
			buttons.push({
				text : '保存并退出',
				onclick : function(){
					if(!mainform.valid()){
						BIONE.tip("字段校验失败！");
						return ;
					}
					dialogPresaveHandler(true);
				}
			});
		}
		buttons.push({
			text : '保存',
			onclick : function(){
				if(isCopy === false){
					if(!mainform.valid()){
						BIONE.tip("字段校验失败！");
						return ;
					}
					// 正常保存
					dialogPresaveHandler(false);
				}else{
					// 复制
					copyHandler();
				}
			}
		});
		BIONE.addFormButtons(buttons);
	}
	
	function dialogPresaveHandler(flag) {
		if(rptId){
			if(!mainform.valid()){
				return ;
			}
			// 修改
			$("#rptId").val(rptId ? rptId : "");
			$("#templateId").val(templateId ? templateId : "");
			$("#verId").val(verId ? verId : "");
			$("#catalogId").val(catalogId ? catalogId : "");
			BIONE.submitForm($("#mainform"), function() {
				// 刷新
				dialogBeforeCloseFunc();
				BIONE.closeDialog("baseEdit", "保存成功");
			}, function() {
				BIONE.closeDialog("baseEdit", "保存失败");
			});
		}else{
			// 新增
			// 保存具体的方法
			// 1.报表基本信息
			var saveObj = dialogPrepareDatas4Save();
			// 修改窗口title
			window.parent.parent.$(".l-dialog-title").html("【新增】"+saveObj.rptNm);
			// 2.设计器待保存信息
			var design4Save = window.parent.prepareDatas4Save();
			window.parent.baseInfo4Upt = saveObj;
			window.parent.saveHandler(saveObj , design4Save , flag);
		}
	}
	
	function copyHandler(){
		if(copyRptId){
			if(!mainform.valid()){
				BIONE.tip("字段校验失败！");
				return ;
			}
			$("#copyRptId").val(copyRptId ? copyRptId : "");
			$("#rptId").val(rptId ? rptId : "");
			$("#templateId").val(templateId ? templateId : "");
			$("#verId").val(verId ? verId : "");
			
			BIONE.submitForm($("#mainform"), function() {
				// 刷新
				dialogBeforeCloseFunc();
				BIONE.closeDialog("baseEdit", "保存成功");
			}, function() {
				BIONE.closeDialog("baseEdit", "保存失败");
			});
		}
	}
	
	function cancleHandler() {
		BIONE.closeDialog("baseEdit");
	}
	
	// 准备待保存数据
	function dialogPrepareDatas4Save(){
		var saveObj = null;
		if(mainform.valid()){
			saveObj = {
					rptId : rptId ? rptId : "",
					verId : verId ? verId : "",
					templateId : templateId ? templateId : "",
					templateType : templateType ? templateType : "",
					rptlineId : $("#rptlineId").val(),
					rptNm : $.trim($("#rptNm").val()) ? $("#rptNm").val() : "",
					rptNum : $.trim($("#rptNum").val()) ? $("#rptNum").val() : "",
					templateUnit : $.ligerui.get("templateUnitCombo").getValue(),
					rankOrder : $("#rankOrder").val() ? $("#rankOrder").val() : "",
					isUpt : "Y",
					rptSts : $.ligerui.get("rptStsCombo").getValue(),
					catalogId : catalogId ? catalogId : "" ,
					defDept : $("#defDept").val() ? $("#defDept").val() : "",
					userId : $("#userId").val() ? 	$("#userId").val() : "",
					defSrc : defSrc ? defSrc : "",
					rptDesc : $.trim($("#rptDesc").val()) ? $("#rptDesc").val() : "",
					verStartDate : $("#verStartDate").val(),
					rptCycle : $.ligerui.get("rptCycleCombo").getValue(),
					isAutoAdj :  $.ligerui.get("isAutoAdjCombo").getValue()
			};
		}
		return saveObj
	}
	
</script>
</head>
<body>
	<div id="template.center">
		<form id="mainform" action="${ctx}/report/frame/design/cfg/uptRpt"  method="post"></form>
	</div>
</body>
</html>