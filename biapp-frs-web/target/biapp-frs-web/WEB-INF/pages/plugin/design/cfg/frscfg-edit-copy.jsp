<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<html>
<meta name="decorator" content="/template/template5.jsp">
<head>
<script type="text/javascript">
	// 复制为新报表时，copyRptId不为空。
	
	var groupicon = "${ctx}/images/classics/icons/communication.gif";
	
	var copyRptId = "${copyRptId}";
	var isCopy = copyRptId ? true : false;
	var defSrc = "${defSrc}";
	var templateType = "${templateType}"; 
	var catalogId = "${catalogId}";
	var rptId = "${rptId}";
	var templateId = "${templateId}";
	var verId = "${verId}";
	var templateUnit = "";
	var rptCycle = "${rptCycle}";
	var isUpt;   //是否数据修改
	
	var catalogNm;
	var defaultNm;
	var mainRptNm;
	var rptNum;             // 报表编号
	var verStartDate;       // 版本启用日期
	var reportCode = "";    // 上报编码
	var extType, busiType, submitMain, submitRange, isUpt, isReleaseSubmit;
	var rptSts, defDept, rptDesc, fillDesc, isMainRpt, mainRptId, tmpVersionId;
	var fixedLength, is_paging, importConfig;
	
	//正则表达式验证报表编码
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
	
	//获取所选报表记录更详细的相关信息
	if(rptId){
		// 修改
		var selRow = window.parent.document.getElementById("gridFrame").contentWindow.grid.getSelectedRow();
		if(selRow != null){
			rptNm = selRow.rptNm;
			templateUnit = selRow.templateUnit;
			catalogId = selRow.catalogId;
			catalogNm = selRow.catalogNm;
			rptCycle = selRow.rptCycle;
			verStartDate = selRow.verStartDate;
			rptNum = selRow.rptNum;
			extType = selRow.extType;
			busiType = selRow.busiType;
			submitMain = selRow.submitMain;
			submitRange = selRow.submitRange;
			reportCode = selRow.reportCode;
			isUpt = selRow.isUpt;
			isReleaseSubmit = selRow.isReleaseSubmit;
			rptSts = selRow.rptSts;
			defDept = selRow.defDept;
			rptDesc = selRow.rptDesc;
			fillDesc = selRow.fillDesc;
			isMainRpt = selRow.isMainRpt;
			mainRptId = selRow.mainRptId;
			mainRptNm = selRow.mainRptNm;
			tmpVersionId = selRow.tmpVersionId,
			fixedLength = selRow.fixedLength,
			isPaging = selRow.isPaging,
			importConfig = selRow.importConfig
		}
	}
	//创建表单结构 
	var mainform;
	
	$(function() {
		initForm();
		initButtons();
		initData();
		if("01" == templateType || "03" == templateType){//如果是明细类或综合类报表，显示“是否定长”和“是否分页”字段
			$("#fixedLength").parents("li").show();
			$("#fixedLength").rules("add" , {required : true});
			$("#isPaging").parents("li").show();
			$("#isPaging").rules("add" , {required : true});
			$("#importConfig").parents("li").show();
			$("#importConfig").rules("add" , {importConfig : true});
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
		}
	});
	
	function initForm(){
		mainform = $("#mainform");
		mainform.ligerForm({
			labelWidth : 105, sapce : 60,
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
							name : "extType",
							type : "hidden"
						}, {
							name : "mainRptId",
							type : "hidden"
						}, {
							name : "verStartDate",
							type : "hidden"
						}, {
							name : "verEndDate",
							type : "hidden"
						}, {
							name : "isMainRpt",
							type: "hidden"
						}, {
							group : "基础信息",
							groupicon : groupicon,
							display : "报表名称",
							name : "rptNm",
							newline : false,
							type : "text",
							width : 435,
							attr : {
								style : "resize: none;"
							},
							validate : {
								required : true,
								rptNm : true,
								maxlength : 100
							}
						}, {
							display : "报表编码",
							name : "rptNum",
							newline : true,
							type : "text",
							validate : {
								required : true,
								maxlength : 32,
								numReg : true
							}
						}, {
							display: '报表目录',
							name :'catalogNm',
							comboboxName:'catalogNmBox',
							type: 'select',
							newline: false,
							options : {
								onBeforeOpen : function() {
									window.parent.selectedTab=window;   //用于获取修改前的参数
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
							newline : true,
							type : "select",
							comboboxName : "templateTypeCombo",
							options : {
								initValue:'02',
								data : [ {
									text : '明细类',
									id : '01'
								}, {
									text : '单元格类',
									id : '02' 
								},{
									text : '综合类',
									id : '03'
								}],
								onBeforeSelect : function(templateType){
									window.parent.templateType = templateType;
									if("01" == templateType || "03" == templateType){//如果是明细类或综合类报表，显示“是否定长”和“是否分页”字段
										$("#fixedLength").parents("li").show();
										$("#fixedLength").rules("add" , {required : true});
										$("#isPaging").parents("li").show();
										$("#isPaging").rules("add" , {required : true});
										$("#importConfig").parents("li").show();
										$("#importConfig").rules("add" , {importConfig : true});
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
									}
								}
							},
							validate:{
								required: true
							}
						}, {
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
							}
						}, {
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
							}
						}, {
							group : "业务信息",
							groupicon : groupicon,
							display : "业务类型",
							name : "busiType",
							newline : true,
							type : "select",
							comboboxName : "busiTypeCombo",
							options : {
								url : "${ctx}/report/frame/datashow/idx/busiTypeList.json",
								onBeforeSelect : hideOrShowFields
							}
						}, {
							display : "制度表样版本",
							name : "tmpVersionId",
							newline : false,
							type : "text",
							validate : {
								maxlength : 100
							}
						}, {
							display : "上报编码",
							name : "reportCode",
							newline : true,
							type : "text",
							validate : {
								maxlength : 32
							}
						}, {
							display : "是否下发提交",
							name : "isReleaseSubmit",
							newline : false,
							type : "select",
							comboboxName : "isReleaseSubmitCombo",
							options : {
								initValue:'N',
								data : [ {
									text : '是',
									id : 'Y'
								}, {
									text : '否',
									id : 'N'
								} ]
							}
						}, {
							display : "报送主体",
							name : "submitMain",
							newline : true,
							type : "select",
							comboboxName : "submitMainCombo",
							options : {
								initValue:'1',
								data : [ {
									text : '总行',
									id : '1'
								}, {
									text : '分行',
									id : '2'
								} ]
							}
						}, {
							display : "报送范围",
							name : "submitRange",
							newline : false,
							type : "select",
							comboboxName : "submitRangeCombo",
							options : {
								initValue:'1',
								data : [ {
									text : '全行',
									id : '1'
								}, {
									text : '省级及以上',
									id : '2'
								}, {
									text : '市级及以上',
									id : '3'
								}, {
									text : '县级及以上',
									id : '4'
								}, {
									text : '法人',
									id : '5'
								} ]
							}
						}, {
							display : "是否数据修改",
							name : "isUpt",
							newline : true,
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
						}, {
							display : "定义部门",
							name : "defDept",
							newline : false,
							type : "text",
							validate : {
								maxlength : 100
							}
						}, {
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
							/*lcy 20190819 业务分库 增加代码*/
							display : "业务分库",
							name : "busiLibId",
							comboboxName : "busiLibIdCombo",
						    type : 'select',
						    newline : false,
						    options : {
						    	url : "${ctx}/rpt/frame/businesslib/getComboInfo.json?typeNo=busiLib"
						    }
						}, {
							display : "是否定长",
							name : "fixedLength",
							newline : true,
							type : "select",
							comboboxName : "fixedLengthCombo",
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
						}, {
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
						}, {
							display : "报表导入配置",
							name : "importConfig",
							type : 'text',
							newline : true
						}, {
							display : "业务定义",
							name : "rptDesc",
							newline : true,
							type : "textarea",
							width : 600,
							attr : {
								style : "resize: none;"
							},
							validate : {
								maxlength : 500
							}
						}, {
							display : "填报说明",
							name : "fillDesc",
							newline : true,
							type : "textarea",
							width : 600,
							attr : {
								style : "resize: none;"
							},
							validate : {
								maxlength : 500
							} 
						}
						
						
					]

		});
		
		$("#defSrc").val(defSrc ? defSrc : "");
		jQuery.metadata.setType("attr", "validate");
		BIONE.validate(mainform);

		var formHeight = $("#center").height() - 20;
		$(".ui-tabs-panel").height(formHeight - 50);
		
		// 添加相应校验
		$("#rptNm").rules("add",{remote:{
			url:"${ctx}/report/frame/design/cfg/checkRptNm",
			type : "post",
			data : {
				rptId : rptId
			}
		}, messages:{remote:"该报表名称已存在"}});
		$("#rptNum").rules("add",{remote:{
			url:"${ctx}/report/frame/design/cfg/checkRptNo?flag=02" ,
			type : "post"
		}, messages:{remote:"该报表编码已存在"}});
		// 报表名称相应样式
		$("#rptNm").bind("focusin" , function(){
			if($("#rptNm").attr("isNull") == "true"){
				$("#rptNm").removeClass("l-text-field-null").val("");
			}
		});
 		$("#rptNm").bind("focusout" , function(){
			if($("#rptNm").val() != null && $("#rptNm").val() != ""){
				$("#rptNm").attr("isNull","false");
			}else{
				$("#rptNm").addClass("l-text-field-null").val(defaultNm).attr("isNull","true");
			}
		}); 

		$.ligerui.get("templateTypeCombo").setDisabled();
		$("#templateTypeCombo").css("color", "black").attr("readOnly", "true").removeAttr("validate");
		$("#templateTypeCombo").parent().css("background", "#D0D0D0");
		$.ligerui.get("busiTypeCombo").setDisabled();
		$("#busiTypeCombo").css("color", "black").attr("readOnly", "true").removeAttr("validate");
		$("#busiTypeCombo").parent().css("background", "#D0D0D0");
		$.ligerui.get("verIdCombo").setDisabled();
		$("#verIdCombo").css("color", "black").attr("readOnly", "true").removeAttr("validate");
		$("#verIdCombo").parent().css("background", "#D0D0D0");
		$.ligerui.get("busiLibIdCombo").setDisabled();
		$("#busiLibIdCombo").css("color", "black").attr("readOnly", "true").removeAttr("validate");
		$("#busiLibIdCombo").parent().css("background", "#D0D0D0");
	}
	
	function initData(){
		// 缺省报表名称
		defaultNm = "平台报表"+(new Date().getTime());
		$("#rptNm").val(defaultNm);
		$("#rptNm").addClass("l-text-field-null").val(defaultNm).attr("isNull","true");
		if(rptId){
			// 报表名称（修改前报表名称）
			if(rptNm && rptNm != "" && isCopy === false){
				$("#rptNm").val(rptNm).attr("isNull","false").removeClass("l-text-field-null");
			}
			
			$("#tmpVersionId").val(tmpVersionId);
			$("#importConfig").val(importConfig);

			getSystemVer(busiType);
			
			// 报表编码
			$.ligerui.get("rptNum").setValue("");
			// 报表类型
			$.ligerui.get("templateTypeCombo").setValue(templateType);
			// 启用日期
			$.ligerui.get("verIdCombo").setValue(verId);
			getSystemDate(verId);
			// 报表周期
			$.ligerui.get("rptCycleCombo").setValue(rptCycle);
			// 报表单位
			$.ligerui.get("templateUnitCombo").setValue(templateUnit);
			
			//业务类型
			$.ligerui.get("busiTypeCombo").setValue(busiType);
			hideOrShowFields(busiType); 
			//报表上报编码
			if(reportCode){
				$.ligerui.get("reportCode").setValue(reportCode);
			}
			//报送主体
			if(submitMain){
				$.ligerui.get("submitMainCombo").setValue(submitMain);
			}
			//报送范围
			if(submitRange){
				$.ligerui.get("submitRangeCombo").setValue(submitRange);
			}
			//是否下发提交
			$.ligerui.get("isReleaseSubmitCombo").setValue(isReleaseSubmit);
			//是否数据修改
			$.ligerui.get("isUptCombo").setValue(isUpt);
			//是否主表
			$("#isMainRpt").val(isMainRpt);
			//定义部门
			if(defDept){
				$.ligerui.get("defDept").setValue(defDept);
			}
			// 报表状态
			if(rptSts){
				$.ligerui.get("rptStsCombo").setValue(rptSts);
			}
			//业务定义
			if(rptDesc){
				$("#rptDesc").val(rptDesc);
			}
			// 填报说明
			if(fillDesc){
				$("#fillDesc").val(fillDesc);
			}
			$.ligerui.get("fixedLengthCombo").setValue(fixedLength);
			$.ligerui.get("isPagingCombo").setValue(isPaging);
		}
		// 初始化目录信息
		$("#catalogNmBox").val(catalogNm);
		$("#catalogId").val(catalogId ? catalogId : "");
		
		//初始化对应主表信息
		$("#mainRptNm").val(mainRptNm);
		$("#mainRptNmCombo").val(mainRptNm);
		$("#mainRptId").val(mainRptId ? mainRptId : "");
		
	}
	
	//根据业务类型，确定是否显示上报编码、报送主体级报送范围等报文配置内容
	function hideOrShowFields(busiType){
		if(busiType == "03"){
			// 上报编码
			$("#reportCode").parents("li").show();
			$("#reportCode").rules("add" , {required : true});
			$("#reportCode").rules("add" , {maxlength : 32});
			// 报送主体
			$("#submitMain").parents("li").show();
			$("li:contains('报送主体')").show();
			$("#submitMain").rules("add" , {required : true});
			// 报送范围
			$("#submitRange").parents("li").show();
			$("li:contains('报送范围')").show();
			$("#submitRange").rules("add" , {required : true});
			// 制度表样版本
			$("#tmpVersionId").rules("remove");
			$("#tmpVersionId").parents("li").hide();
		}else if(busiType == "02"){
			// 上报编码
			$("#reportCode").parents("li").show();
			$("#reportCode").rules("add" , {maxlength : 32});
			// 报送主体
			$("#submitMain").parents("li").hide();
			$("li:contains('报送主体')").hide();
			// 报送范围
			$("#submitRange").parents("li").hide();
			$("li:contains('报送范围')").hide();

			// 制度表样版本
			$("#tmpVersionId").parents("li").show();
			$("li:contains('制度表样版本')").show();
		}else{
			// 上报编码
			$("#reportCode").parents("li").hide();
			$("#reportCode").rules("remove");
			$("#reportCode").val("");
			// 报送主体
			$("#submitMain").parents("li").hide();
			$("li:contains('报送主体')").hide();
			$("#submitMain").rules("remove");
			// 报送范围
			$("#submitRange").parents("li").hide();
			$("li:contains('报送范围')").hide();
			$("#submitRange").rules("remove");
		}
	}
	
	//根据是否属于主表,显示或隐藏对应主表名称输入框
	function hideOrShowMainRpt(isMainRpt){
		if(isMainRpt == "N"){
			// 对应主表名称
			$("#mainRptNm").parents("li").show();
			$("#mainRptNm").rules("add", {required : true});
		}else{
			$("#mainRptNm").parents("li").hide();
			$("#mainRptNm").rules("remove");
		}
	}
	
	function dialogBeforeCloseFunc() {
		// 刷新树
		var topWindow = window.parent;
		if(isUpt === false){
			//新增
			topWindow = window.parent.parent;
		}
		topWindow.searchHandler();
		if(topWindow.clickNodeType == topWindow.rptNodeType){
			// 若是报表，刷新tab的label
			topWindow.mainTab.setHeader("topTab","[报表]"+$("#rptNm").val());
		}		
		// 刷新列表
		topWindow.document.getElementById("gridFrame").contentWindow.grid.reload();
	}
	
	function initButtons(){
		var buttons = [];
		buttons.push({
			text : '取消',
			onclick : cancleHandler
		});
		buttons.push({
			text : '复制',
			onclick : function(){
				if(isCopy === false){
					// 正常保存
				}else{
					// 复制
					copyHandler();
				}
			}
		});
		BIONE.addFormButtons(buttons);
	}
	
	function copyHandler(){
		if(mainform.valid()){  //判断表单校验是否通过
			if(copyRptId){
				$("#copyRptId").val(copyRptId ? copyRptId : "");
				$("#rptId").val(rptId ? rptId : "");
				$("#templateId").val(templateId ? templateId : "");
				$("#verId").val(verId ? verId : "");
				$("#catalogId").val(catalogId ? catalogId : "");
				$("#extType").val(extType ? extType : "");
				if($("#isMainRpt").val() == "Y"){
					mainRptId = "";   //防止选择主表选项,但先前存在的mainRptId仍然存在
				}
				$("#mainRptId").val(mainRptId ? mainRptId : "");
				BIONE.submitForm($("#mainform"), function() {
					dialogBeforeCloseFunc();   // 刷新页面
					BIONE.closeDialog("baseEdit", "复制成功");
				}, function() {
					BIONE.closeDialog("baseEdit", "复制失败");
				});
			}
		}
	}
	
	function cancleHandler() {
		BIONE.closeDialog("baseEdit");
	}
	
	function getSystemDate(verId){
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
</script>
</head>
<body>
	<div id="template.center">
		<form id="mainform" action="${ctx}/report/frame/design/cfg/uptRpt"  method="post"></form>
	</div>
</body>
</html>