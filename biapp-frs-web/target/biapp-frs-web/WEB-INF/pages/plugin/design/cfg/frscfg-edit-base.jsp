<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<html>
<head>
<meta name="decorator" content="/template/template14.jsp">
<style type="text/css">
	.l-checkboxlist-table > tbody > tr > td{
		display: inline-block;
	}
</style>
<script type="text/javascript">

	var groupicon = "${ctx}/images/classics/icons/communication.gif";
	
	var catalogId = "" ;//报表目录id
	var catalogNm = "" ; //报表目录名称
	var rptId = "" ;//报表id
	var mainRptId = "" ;//主表ID
	var mainRptNm = "" ;//主表名称
	var defSrc = "";//定义来源
	var grid;//grid对象
	var mainform;
	
	var OPT_ADD_FLAG = "add";
	var OPT_DEL_FLAG = "delete";
	var cacheAttachsADD = [];//新增对象集合
	var cacheAttachsDEL = [];//删除对象集合
	
	var uptOldObj = {
		busiType : "",
		verStartDate : ""
	};
	
	jQuery.validator.addMethod("numReg", function(value, element) {
	    var numReg = /^[0-9a-zA-Z]*$/;
	    return this.optional(element) || (numReg.test(value));
	}, "编码格式不合法");
	
	jQuery.validator.addMethod("rptNm", function(value, element) {
	    var numReg = /^[0-9a-zA-Z\u2160-\u216B\u4e00-\u9fa5\uFF08\uFF09\uFF1A_-]*$/;//数字、字母、罗马数字、中文、中文括号、下划线、横线、冒号
	    return this.optional(element) || (numReg.test(value));
	}, "报表名称命名不合法");

	jQuery.validator.addMethod("importConfig", function(value, element) {
		return (value != null) && (value != '') && (value != 'undefined');
	}, "不定长配置为数字: 例如:2  定长配置为: 模型ID:长度 例如:MD_G15_1:10 多模型使用英文逗号分隔");

	$(function(){
		$("#center").css("overflow-y","scroll");
		// 初始化表单
		if(window.parent.baseInfo4Upt != null
				&& window.parent.baseInfo4Upt.catalogId
				&& window.parent.baseInfo4Upt.catalogNm){
			catalogId = window.parent.baseInfo4Upt.catalogId;
			catalogNm = window.parent.baseInfo4Upt.catalogNm;
			rptId = window.parent.baseInfo4Upt.rptId;
			mainRptId = window.parent.baseInfo4Upt.mainRptId;
			mainRptNm = window.parent.baseInfo4Upt.mainRptNm;
			defSrc = window.parent.baseInfo4Upt.defSrc;
		} else {
			catalogId = window.parent.selCatalogId;
			catalogNm = window.parent.selCatalogNm;
		}
		initForm();
		initGrid();
		initDefUser();
	});
	
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
			url: '${ctx}/report/frame/design/cfg/listTmpAttach?rptId=' + rptId,
			method: 'get'
		});
	}
	
	function initForm(){
		mainform = $("#baseform");
		mainform.ligerForm({
			inputWidth : 150, labelWidth : 105,
			fields : [ {
				name : "rptId" ,
				type : "hidden"
			},{
				name : "createTimeStr",
				type : "hidden"
			} ,{
				name : "templateId" ,
				type : "hidden"
			}, {
				name : "catalogId",
				type : "hidden"
			}, {
				name : "mainRptId",
				type : "hidden"
			}, {
				name : "verStartDate" ,
				type : "hidden"
			}, {
				name : "verEndDate" ,
				type : "hidden"
			}, {
				name : "isReleaseSubmit" ,
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
					rptNm : true,
					maxlength : 100
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
										'${ctx}/report/frame/design/cfg/frsindex/edit/changeCatalog',
										null);
						return false;
					}
				},
				validate:{
					required: true
				}
			}, {
				display : "报表类型",
				name : "templateType",
				newline : false,
				type : "select",
				comboboxName : "templateTypeCombo",
				options : {
					data : [
					    {
							text : '明细类',
							id : '01'
						}, {
							text : '单元格类',
							id : '02' 
						}, {
							text : '综合类',
							id : '03'
						} 
					],
					onBeforeSelect : function(templateType){
						window.parent.templateType = templateType;
						if("01" == templateType || "03" == templateType){//如果是明细类或综合类报表，显示“是否定长”和“是否分页”字段
							$("#fixedLength").parents("li").show();
							$("#fixedLength").rules("add" , {required : true});
							
							$("#isPaging").parents("li").show();
							$("#isPaging").rules("add" , {required : true});
							
							$("#importConfig").rules("add",{importConfig: true});
							$("#importConfig").parents("li").show();
							
							$("#sortSql").parents("li").show();
							$(".l-group-hasicon").each(function(index,element){
								if($(this).find("span").text() == "明细类报表特殊配置"){
									$(this).show();
								}
							});
						}else {
							$("#fixedLength").rules("remove");
							$("#fixedLength").val("");
							$("#fixedLength").parents("li").hide();
							$("#isPaging").rules("remove");
							$("#isPaging").val("");
							$("#isPaging").parents("li").hide();
							$("#importConfig").rules("remove");
							$("#importConfig").val("");
							$("#importConfig").parents("li").hide();
							$("#sortSql").parents("li").hide();
							$(".l-group-hasicon").each(function(index,element){
								if($(this).find("span").text() == "明细类报表特殊配置"){
									$(this).hide();
								}
							});
						}
					}
				},
				validate:{
					required: true
				}
			}, {
				display : "生成周期",
				name : "rptCycle",
				newline : true,
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
					}, {
						text : '年',
						id : '04' 
					}, {
						text : '周',
						id : '10' 
					}, {
						text : '旬',
						id : '11' 
					}, {
						text : '半年',
						id : '12' 
					} ]
				},
				validate:{
					required: true
				}
			},{
				display : "报表单位",
				name : "templateUnit",
				newline : false,
				type : "select",
				comboboxName : "templateUnitCombo",
				options : {
					initValue:'04',
					data : [ {
						text : '元',
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
				},
				validate:{
					required: true
				}
			},{
				display : "业务类型",
				name : "busiType",
				newline : false,
				type : "select",
				comboboxName : "serviceTypeCombo",
				options : {
					url : "${ctx}/report/frame/datashow/idx/getIdxTaskType.json",
					onBeforeSelect : hideOrShowFields
				},
				validate:{
					required: true
				}
			},{
				display : "报表版本",
				name : "verId",
				newline : false,
				type : "select",
				comboboxName : "verIdCombo",
				options : {
					data : null,
					onBeforeSelect : function(selectVerId){
						getSystemDate(selectVerId);
					}
				},
				validate : {
					required : true
				}
			},{
				display : "联系人",
				name : "defUser",
				newline : true,
				type : "select",
				options : {
					onBeforeOpen : function() {
						var url = "${ctx}/report/frame/idx/userEdit";
						dialog = BIONE.commonOpenLargeDialog('请选择联系人', "selectWin",  url);
						return false;
					},
					cancelable:true
				}
			},{
				display : "升级概况",
				name : "templateNm",
				newline : false,
				type : "select",
				comboboxName : "templateNmCombo",
				options : {
					url : "${ctx}/report/frame/design/cfg/getRptUpgradeList"
				}
			},{
				display : "是否数据修改",
				name : "isUpt",
				newline : false,
				type : "select",
				comboboxName : "isUptCombo",
				options : {
					initValue:'Y',
					data : [ {
						text : '是',
						id : 'Y'
					}, {
						text : '否',
						id : 'N' 
					} ]
				}
			},{
				display : "报表状态",
				name : "rptSts",
				newline : false,
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
				/*lcy 20190819 业务分库 增加代码*/
				display : "业务分库",
				name : "busiLibId",
			    type : 'select',
			    newline : true,
			    options : {
			    	url : "${ctx}/rpt/frame/businesslib/getComboInfo.json?typeNo=busiLib",
					cancelable: true
			    }
			},{
				display : "报表业务名称",
				name : "rptBusiNm",
			    type : 'text',
			    newline : false,
				options:{
					onMouseOver:function (){
						$("#rptBusiNm").parent().parent().parent().parent().append('<div class="wrapper">自定义报表导出的名称：如想要名称:“数据日期-机构编号-机构名称-报表编号-报表名称-金融机构编号.xls”，则填入{date}-{orgNo}-{orgNm}-{rptNum}-{rptNm}-{finNum}</div>');
					},
					onMouseOut:function (){
						$(".wrapper").remove();
					}
				}
			},{
				display : "是否跑Excel公式",
				name : "excelRun",
				newline : false,
				type : "select",
				comboboxName : "excelRunCombo",
				options : {
					initValue:'Y',
					data : [ {
						text : '是',
						id : 'Y'
					}, {
						text : '否',
						id : 'N'
					} ]
				}
			},{
				display : "报表报送对象",
				name : "reportObj",
				newline : true,
				type : "checkboxlist",
				width : 1000,
				options : {
					rowSize : 10,
					data : [ {
						id : '01',
						text : '银行'
					}, {
						id : '02',
						text : '农信'
					}, {
						id : '03',
						text : '农商'
					}, {
						id : '04',
						text : '消金'
					}, {
						id : '05',
						text : '财务公司'
					}, {
						id : '06',
						text : '信托'
					} ]
				}
			},{
				display : "业务定义",
				name : "rptDesc",
				newline : true,
				type : "textarea",
				width : 430,
				attr : {
					style : "resize: none;"
				},
				validate : {
					maxlength : 500
				}
			},{
				display : "填报说明",
				name : "fillDesc",
				newline : false,
				type : "textarea",
				width : 430,
				attr : {
					style : "resize: none;"
				}
			},{
				display : "制度表样版本",
				name : "tmpVersionId",
				newline : true,
				type : "text",
				group : "1104报送特殊配置",
				groupicon : groupicon,
				options:{
					onMouseOver : function(){
						$("#tmpVersionId").parent().parent().parent().parent().append('<div id="tmpVersionDesc" class="wrapper">银监表样属性中的自定义属性(ver)的值<div>');
					},
					onMouseOut : function(){
						$(".wrapper").remove();
					}
				}
			},{
				display : "制度表样编号",
				name : "repId",
				newline : false,
				type : "text",
				options:{
					onMouseOver : function(){
						$("#repId").parent().parent().parent().parent().append('<div class="wrapper">银监表样属性中的自定义属性(repId)的值<div>');
					},
					onMouseOut : function(){
						$(".wrapper").remove();
					}
				}
			},{
				display : "上报编码",
				name : "reportCode",
				newline : false,
				type : "text"
			},{
				display : "是否主表",
				name : "isMainRpt",
				newline: true,
				type : "hidden",
				comboboxName : "isMainRptCombo",
				options : {
					initValue:'Y',
					data : [ {
						text : '是',
						id : 'Y'
					}, {
						text : '否',
						id : 'N' 
					} ],
					onBeforeSelect : hideOrShowMainRpt
				}
			},{
				display : "对应主表名称",
				name : "mainRptNm",
				newline : false,
				type : "select",
				comboboxName : "mainRptNmCombo",
				options : {
					onBeforeOpen : function(){
						window.parent.selectedMainRpt = window;
						window.parent.BIONE.commonOpenDialog(
								"报表选择",
								"rptTreeWin",
								400,
								380,
								'${ctx}/report/frame/design/cfg/editRptTree',
								null);
						return false;
					}
				}
			},{
				display : "是否定长",
				name : "fixedLength",
				newline : true,
				type : "select",
				comboboxName : "fixedLengthCombo",
				group : "明细类报表特殊配置",
				groupicon : groupicon,
				options : {
					data : [ {
						text : '是',
						id : 'Y'
					}, {
						text : '否',
						id : 'N' 
					} ],
					onBeforeSelect : function(value){
						if("Y" == value){//如果是定长报表，不显示“是否分页”字段。
							$("#isPaging").rules("remove");
							$("#isPaging").val("N");
							$("#isPaging").parents("li").hide();
						}else {
							$("#isPaging").parents("li").show();
							$("#isPaging").rules("add" , {required : true});
						}
					}
				}
			},{
				display : "是否分页",
				name : "isPaging",
				newline : false,
				type : "select",
				comboboxName : "isPagingCombo",
				options : {
					data : [ {
						text : '是',
						id : 'Y'
					}, {
						text : '否',
						id : 'N' 
					} ]
				}
			},{
				display : "报表导入配置",
				name : "importConfig",
				type : 'text',
				newline : true,
				width : 720,
				options : {
					onMouseOver : function(){
						$("#importConfig").parent().parent().parent().parent().append('<div class="wrapper">不定长报表应配置为：表尾固定行数（例如:2 ）； 定长报表应配置为：模型ID:长度，多模型使用英文逗号分隔 （例如:MD_G15_1:10,MD_G15_2:10）<div>');
					},
					onMouseOut : function(){
						$(".wrapper").remove();
					}
				}
			},{
				display : "合计排序SQL",
				name : "sortSql",
				newline : true,
				type : "text",
				width : 720,
				attr : {
					style : "resize: none;"
				},
				validate : {
					maxlength : 500
				},
				options : {
					onMouseOver : function(){
						$("#sortSql").parent().parent().parent().parent().append('<div class="wrapper">配置规则：模型ID:排序字段公式，例如：{"MD_G1402":"(OT_RISK_EXP_AMT+PT_RISK_EXP_AMT+SP_RISK_EXP_AMT+NM_RISK_EXP_AMT+CC_RISK_EXP_AMT+TB_RISK_EXP_AMT)"}<div>');
					},
					onMouseOut : function(){
						$(".wrapper").remove();
					}
				}
			}]
		});
		jQuery.metadata.setType("attr", "validate");
		BIONE.validate(mainform);
		if(window.parent.baseInfo4Upt != null){
			// 修改时，数据初始化
			loadForm(window.parent.baseInfo4Upt);
			if("01" == window.parent.baseInfo4Upt.templateType || "03" == window.parent.baseInfo4Upt.templateType){//如果是明细类或综合类报表，显示“是否定长”和“是否分页”字段
				$("#fixedLength").parents("li").show();
				$("#fixedLength").rules("add" , {required : true});
				$("#isPaging").parents("li").show();
				$("#isPaging").rules("add" , {required : true});
				$("#importConfig").parents("li").show();
				$("#importConfig").rules("add" , {importConfig: true});
				$("#sortSql").parents("li").show();
				$(".l-group-hasicon").each(function(index,element){
					if($(this).find("span").text() == "明细类报表特殊配置"){
						$(this).show();
					}
				});
			}else {
				$("#fixedLength").rules("remove");
				$("#fixedLength").val("");
				$("#fixedLength").parents("li").hide();
				$("#isPaging").rules("remove");
				$("#isPaging").val("");
				$("#isPaging").parents("li").hide();
				$("#importConfig").rules("remove");
				$("#importConfig").val("");
				$("#importConfig").parents("li").hide();
				$("#sortSql").parents("li").hide();
				$(".l-group-hasicon").each(function(index,element){
					if($(this).find("span").text() == "明细类报表特殊配置"){
						$(this).hide();
					}
				});
			}
			if($("#fixedLength").val() == "Y"){
				$("#isPaging").rules("remove");
				$("#isPaging").val("N");
				$("#isPaging").parents("li").hide();
			}
			//设置报送对象的默认值
			$.ligerui.get("reportObj").setValue(window.parent.baseInfo4Upt.reportObj);

			hideOrShowFields(window.parent.baseInfo4Upt.busiType);
			hideOrShowMainRpt(window.parent.baseInfo4Upt.isMainRpt);    //用于隐藏或显示对应主表名称输入框
			$("#mainRptNmCombo").val(mainRptNm);	
			$("#rptNum").parent().css("background", "#D0D0D0");
			$("#baseform input[name=rptNum]").attr("readonly", "true").css("color", "black").css("background", "#D0D0D0").removeAttr("validate");
			$("#rptNm").rules("add",{remote:{
				url:"${ctx}/report/frame/design/cfg/checkRptNm",
				type : "post",
				data : {
					rptId : window.parent.baseInfo4Upt.rptId,
					defSrc : window.parent.baseInfo4Upt.defSrc
				}
			},messages:{remote:"该报表名称已存在"}});
			$.ligerui.get("templateTypeCombo").setDisabled();
			$("#templateTypeCombo").parent().css("background", "#D0D0D0");
			$("#templateTypeCombo").css("color", "#333").css("background", "#D0D0D0").attr("readOnly", "true").removeAttr("validate");
			$.ligerui.get("verIdCombo").setDisabled();
			$("#verIdCombo").parent().css("background", "#D0D0D0");
			$("#verIdCombo").css("color", "#333").css("background", "#D0D0D0").attr("readOnly", "true").removeAttr("validate");
			$.ligerui.get("serviceTypeCombo").setDisabled();
			$("#serviceTypeCombo").parent().css("background", "#D0D0D0");
			$("#serviceTypeCombo").css("color", "#333").attr("readOnly", "true").removeAttr("validate");
			jQuery.extend(uptOldObj , window.parent.baseInfo4Upt);
			window.parent.baseInfo4Upt = null;
		}else{
			hideOrShowMainRpt("Y");              //添加时设为主表
			$("#rptNm").rules("add",{remote:{
				url:"${ctx}/report/frame/design/cfg/checkRptNm",
				type : "post"
			},messages:{remote:"该报表名称已存在"}});
			$("#rptNum").rules("add",{remote:{
				url:"${ctx}/report/frame/design/cfg/checkRptNo?flag=01" ,
				type : "post"
			},messages:{remote:"该报表编码已存在"}});
			$("#reportCode").rules("add" , {maxlength : 32});
			$.ligerui.get("serviceTypeCombo").setValue("02");//初始化默认显示1104
			hideOrShowFields("02");//初始化默认显示1104
			$("#isMainRpt").val("Y");//目前已经去除主分表的概念
		}
		$("#catalogId").val(catalogId);
		$("#catalogNmBox").val(catalogNm);
	}
	
	function hideOrShowFields(busiType){
		getSystemVer(busiType);
		if(busiType == "03"){
			// 上报编码
			$("#reportCode").val("");
			$("#reportCode").parents("li").hide();
			// 制度表样版本
			$("#tmpVersionId").rules("remove");
			$("#tmpVersionId").parents("li").hide();
			$("li:contains('制度表样版本')").hide();
			$("#tmpVersionId").rules("remove");
			//制度报表编号
			$("#repId").rules("remove");
			$("#repId").parents("li").hide();
			$("li:contains('制度表样编号')").hide();
			$("#repId").rules("remove");
			$(".l-group-hasicon").each(function(index,element){
				if($(this).find("span").text() == "1104报送特殊配置"){
					$(this).hide();
				}
			});
		}else if(busiType == "02"){
			// 上报编码
			$("#reportCode").parents("li").show();
			$("#reportCode").rules("add" , {maxlength : 32});
			// 制度表样版本
			$("#tmpVersionId").parents("li").show();
			$("li:contains('制度表样版本')").show();
			$("#tmpVersionId").rules("add" , {required : true});
			// 制度报表编号
			$("#repId").parents("li").show();
			$("li:contains('制度表样编号')").show();
			$("#repId").rules("add" , {required : true});
			$(".l-group-hasicon").each(function(index,element){
				if($(this).find("span").text() == "1104报送特殊配置"){
					$(this).show();
				}
			});
		}else {
			// 上报编码
			$("#reportCode").val("");
			$("#reportCode").parents("li").hide();
			// 制度表样版本
			$("#tmpVersionId").rules("remove");
			$("#tmpVersionId").parents("li").hide();
			$("li:contains('制度表样版本')").hide();
			//制度报表编号
			$("#repId").rules("remove");
			$("#repId").parents("li").hide();
			$("li:contains('制度表样编号')").hide();
			$(".l-group-hasicon").each(function(index,element){
				if($(this).find("span").text() == "1104报送特殊配置"){
					$(this).hide();
				}
			});
		}
	}
	
	//根据是否属于主表,显示或隐藏对应主表名称输入框
	function hideOrShowMainRpt(isMainRpt){
		if(isMainRpt == "N"){
			// 对应主表名称
			$("#mainRptNm").parents("li").show();
			$("#mainRptNm").rules("add" , {required : true});
		}else{
			$("#mainRptNm").parents("li").hide();
			$("#mainRptNm").rules("remove");
		}
	}
	
	// 准备待保存数据
	function prepareDatas4Save(){
		var saveObj = null;
		if(mainform.valid()){
			if($("#isMainRpt").val() == "Y"){
				$("#mainRptId").val("");
			}
			saveObj = {
					rptId : $("#baseform input[name=rptId]").val(),
					verId : $.ligerui.get("verIdCombo").getValue(),
					templateId : $("#baseform input[name=templateId]").val(),
					rptNm : $("#baseform input[name=rptNm]").val() ? $.trim($("#baseform input[name=rptNm]").val()) : "",
					rptNum : $("#baseform input[name=rptNum]").val() ? $.trim($("#baseform input[name=rptNum]").val()) : "",
					templateType : $.ligerui.get("templateTypeCombo").getValue(),
					rptCycle : $.ligerui.get("rptCycleCombo").getValue(),
					templateUnit : $.ligerui.get("templateUnitCombo").getValue(),
					templateNm : $.ligerui.get("templateNmCombo").getValue(),
					busiType : $.ligerui.get("serviceTypeCombo").getValue(),
					isUpt : $.ligerui.get("isUptCombo").getValue(),
					defUser : $.ligerui.get("defUser").getValue(),
					isReleaseSubmit : "N",
					rptSts : $.ligerui.get("rptStsCombo").getValue(),
					rptDesc : $("#rptDesc").val() ? $("#rptDesc").val() : "",
					reportCode : $("#reportCode").val(),
					tmpVersionId : $("#tmpVersionId").val(),
					repId : $("#repId").val(),
					createTimeStr : $("#createTimeStr").val(),
					verStartDate : $("#verStartDate").val(),
					verEndDate : $("#verEndDate").val(),
					catalogId : $("#catalogId").val(),
					isMainRpt : $("#isMainRpt").val(),//根据光大新添加的
					mainRptId : $("#mainRptId").val() ? $("#mainRptId").val() : "",
					fillDesc : $("#fillDesc").val() ? $("#fillDesc").val() : "",
					//业务分库
					busiLibId : $("#busiLibId").val() ? $("#busiLibId").val() : "",
					rptBusiNm : $("#baseform input[name=rptBusiNm]").val(),
					addAttachs : $.toJSON(cacheAttachsADD),
					delAttachs : $.toJSON(cacheAttachsDEL),
					fixedLength : $("#fixedLength").val() ? $("#fixedLength").val() : "",
					isPaging : $("#isPaging").val() ? $("#isPaging").val() : "",
					sortSql : $("#sortSql").val(),
					importConfig : $("#importConfig").val(),
					excelRun : $.ligerui.get("excelRunCombo").getValue(),
				    reportObj : $.ligerui.get("reportObj").getValue()
			};
		}
		return saveObj;
	}
	
	function needToSaveDesign(){
		var verStartDate = $("#verStartDate").val();
		var busiType = $.ligerui.get("serviceTypeCombo").getValue();
		var flag = true;
		if(busiType == uptOldObj.busiType
				&& verStartDate == uptOldObj.verStartDate){
			// 业务类型和启用时间没发生变化，则不需要同步保存报表模板和指标
			flag = false;
		}
		return flag;
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
			if(o.inputText){
				o.inputText.blur();
			}
			if (managers[i] instanceof $.ligerui.controls.TextBox)
				o.checkValue();
		}
	}
	
	function attach_add() {
		BIONE.commonOpenDialog('上传文件', 'attachUpload', 562, 334, '${ctx}/bione/message/attach/newAttach?type=rptdesign');
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
	
	function afterAttachOperat(optType, attachInst) {
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
	
	function getSystemVer(busiType){
		if(busiType){
			$.ajax({
				cache : false,
				async : false,
				url : "${ctx}/report/frame/design/cfg/getSystemList",
				dataType : 'json',
				data : {
					busiType : busiType
				},
				type : "post",
				success : function(result){
					if(result){
						$.ligerui.get("verIdCombo").setData(result);
					}
				},
				error:function(){
					BIONE.tip("数据加载异常，请联系系统管理员");
				}
			});
		}
	}
	
	function getSystemDate(verId){
		var busiType = $.ligerui.get("serviceTypeCombo").getValue();
		if(verId && busiType){
			$.ajax({
				cache : false,
				async : false,
				url : "${ctx}/frs/system/cfg/getSystem",
				dataType : 'json',
				data : {
					busiType : busiType,
					verId : verId
				},
				type : "post",
				success : function(result){
					if(result && result.frsSystemCfg){
						var frsSystemCfg = result.frsSystemCfg;
						$("#verStartDate").val(frsSystemCfg.verStartDate);
						$("#verEndDate").val(frsSystemCfg.verEndDate);
					}
				},
				error:function(){
					BIONE.tip("数据加载异常，请联系系统管理员");
				}
			});
		}
	}
	
	//初始化联系人
	function initDefUser(){
		$.ajax({
			   cache : false,
			   async : true,
			   url: "${ctx}/report/frame/idx/inverse?d="+new Date(), 
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
				   liger.get('defUser')._changeValue(data.userId,data.userName);
			   }
			}); 
	}
</script>

</head>
<body>
<div id="template.center">
	<form name="baseform" method="post" id="baseform" action=""></form>
	<div id="attach_div"  style="width: 98%;height: 180px; text-align:left;">
		<div id="grid" ></div>
	</div>
</div>
</body>
</html>