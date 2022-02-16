<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<html>
<meta name="decorator" content="/template/template5_BS.jsp">
<head>
<script type="text/javascript">
	var show="${show}";
	var mainform=null;
	var templateFlag=true;
	var groupicon = "${ctx}/images/classics/icons/communication.gif";
	
	function intiForm() {
		mainform = $("#basicform").ligerForm({
			fields : [{
				name: 'rptId',
				type: 'hidden'
			},{
				name: 'cfgId',
				type: 'hidden'
			},/* {
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
			}, */{
				display : "报表名称",
				name : "rptNm",
				newline : false,
				type : "text",
				validate : {
					required : true,
					maxlength : 100
				},
				group : "基础信息",
				groupicon : groupicon,
			} , {
				display: '报表目录',
				name :'catalogNm',
				comboboxName:'catalogNameBox',
				type: 'select',
				newline: true,
				options : {
					onBeforeOpen : function() {
						window.parent.selectedTab=window;
						window.parent.BIONE
								.commonOpenDialog(
										"报表目录选择",
										"catalogTreeWin",
										400,
										380,
										"${ctx}/report/frame/design/cfg/frsindex/edit/changeCatalog",
										null);
						return false;
					}//${ctx}/report/frame/design/cfg/frsindex/edit/changeCatalog
				},
				validate:{
					required: true
				}
			} ,{
				display : "启用日期",
				name : "verStartDate",
				newline : false,
				type : "date",
				options : {
					format : "yyyyMMdd"
				},
				validate : {
					required : true,
					messages : {
						required : "启用日期不能为空。"
					}
				}
			} , {
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
					},{
						text : '指标列表类',
						id : '04'
					}],
					onBeforeSelect : function(templateType){
						window.parent.templateType = templateType;
					}
				}
			},{
				display : "生成周期",
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
			},{
				display : "报表单位",
				name : "templateUnit",
				newline : true,
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
			}  , /*{
				display : "业务类型",
				name : "busiType",
				newline : true,
				group : "业务信息",
				groupicon : groupicon,
				type : "select",
				comboboxName : "serviceTypeCombo",
				options : {
					initValue:'03',
					data : [ {
						text : '1104报表',
						id : '02'
					}, {
						text : '人行大集中',
						id : '03' 
					} ],
					onBeforeSelect : hideOrShowFields
				}
			},{
				display : "上报编码",
				name : "reportCode",
				newline : false,
				type : "text",
				validate : {
					//required : true,
					//maxlength : 32
				}
			},{
				display : "是否下发提交",
				name : "isReleaseSubmit",
				newline : true,
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
			}, *//* {
				display : "定义部门",
				name : "defDept",
				newline : true,
				type : "text",
				validate : {
					maxlength : 100
				}
			}, */{
				display : "报表状态",
				name : "rptSts",
				newline : true,
				type : "select",
				comboboxName : "rptStsBox",
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
				width : 493,
				attr : {
					style : "resize: none;"
				},
				validate : {
					maxlength : 500
				}
			} ]
		});
		if("${show}"!=""){
			jQuery.metadata.setType("attr", "validate");
			BIONE.validate($("#basicform"));
		}
		
		$("#rptDesc").css("width","410");
		$("#rptDesc").css("resize","none");
		$("#rptNum").focus().blur();
		
		
		if (show != "") {
			$("input").css("color", "#333").attr("readOnly", "true")
			.removeAttr("validate");
			$.ligerui.get("catalogNameBox").setDisabled();
			$.ligerui.get("verStartDate").setDisabled();
			$.ligerui.get("templateTypeCombo").setDisabled();
			$.ligerui.get("rptCycleCombo").setDisabled();
			$.ligerui.get("templateUnitCombo").setDisabled();
			/* $.ligerui.get("serviceTypeCombo").setDisabled();
			$.ligerui.get("isReleaseSubmitCombo").setDisabled();
			$.ligerui.get("isUptCombo").setDisabled(); */
			$.ligerui.get("rptStsBox").setDisabled();
		}
	};
	
	function initButton(){
		var btns = [ {
			text : "取消", onclick : function() {
				if("${show}"=="1"){
					parent.currentNode=null;
					//2016-9
					parent.$("#tab").ligerGetTabManager().removeSelectedTabItem();
					BIONE.closeDialog("rptViewWin");
					return;
				}
				if("${show}"=="2"||"${show}"=="3"){
					BIONE.closeDialog("rptViewWin");
					return;
				}
				else{
					parent.currentNode=null;
					parent.removeInfo();
				}
			}
		} ];
		if("${show}"==""){
			btns.push( {
				text : "保存", onclick : save
			});
		}
		BIONE.addFormButtons(btns);
	}
	
	function hideOrShowFields(busiType){
		if(busiType == "03"){
			$("#reportCode").parents("li").show();
			$("#reportCode").rules("add" , {required : true});
			$("#reportCode").rules("add" , {maxlength : 32});
		}else{
			$("#reportCode").rules("remove");
			$("#reportCode").val("");
			$("#reportCode").parents("li").hide();
		}
	}
	
	function intiTab(){
		$("#tab").ligerTab({
			changeHeightOnResize : true,
			contextmenu : false,
			onAfterSelectTabItem : function(tabId) {
				if(tabId == 'template'){
					if(!templateFlag){///rpt/frame/rptmgr/info/template?
						content = "<iframe frameborder='0' id='templateFrame' name='templateFrame' style='height:100%;width:100%;' src='${ctx}/frs/integratedquery/rptmesquery/info/template?rptId=${rptId}'></iframe>";
						$("#templateFrame").html(content);
						templateFlag = true;
					}
				}
			}
		});
		tabObj = $("#tab").ligerGetTabManager();
		if("${show}"=="1"){
			addTabItem("template","模板信息","templateFrame",'templateFlag');
		}
		
	}
	
	function addTabItem(tabId,tabText,frameId,flag){
		// var $centerDom = $(document);
        var $centerDom = $(window.parent.document);
		framCenter = $centerDom.height() - 75;
		tabObj.addTabItem({//添加标签tab页
			tabid : tabId,
			text : tabText,
			showClose : false,
			content : "<div id='"+frameId+"' style='height:" + framCenter
					+ "px;width:100%;'></div>"
		}); 
		this[flag]=false;
	}
	function initData(){
		if ("${rptId}"!="") {
			$.ajax({
				async : true,
				cache : false,
				type : "POST",
				dataType : "JSON",
				contentType : "application/json",
				url : '${ctx}/frs/integratedquery/rptmesquery/info/getReportFrsInfo?rptId=${rptId}',
				beforeSend : function() {
					BIONE.showLoading();
				},
				success : function(data) {
					window.rptvo=data.rptvo;
					injectFormData(data.rptvo);
					$("#catalogId").val(data.rptvo.catalogId);
					$("#catalogNameBox").val(data.rptvo.catalogNm);
					//hideOrShowFields(data.rptvo.busiType);
					//设计器信息
					$.ajax({
						cache : false,
						async : false,
						url : '${ctx}/report/frame/design/cfg/getDesignInfoFrs',
						dataType : 'json',
						data : {
							templateId : rptvo.templateId,
							getChildTmps : true
						},
						type : "post",
						complete: function(){
							BIONE.hideLoading();
						},
						success : function(result){
							if(result){
								window.tmpInfo = result;
							}
						},
						error:function(){
							//BIONE.tip("数据加载异常，请联系系统管理员");
						}
					});
				},
				error : function(){
					BIONE.hideLoading();
				}
			});
			
		} 
	}
	/*注入form 数据*/
	function injectFormData(data) {
		for ( var p in data) {
			var ele = $("[name=" + p + "]");
			// 针对复选框和单选框 处理
			ele.val(data[p]);
		}
		// 下面是更新表单的样式
		var managers = $.ligerui.find($.ligerui.controls.Input);
		for ( var i = 0, l = managers.length; i < l; i++) {
			// 改变了表单的值，需要调用这个方法来更新ligerui样式
			var o = managers[i];
			o.updateStyle();
			if (managers[i] instanceof $.ligerui.controls.TextBox)
				o.checkValue();
		}
	};
	
	
	$(function(){
		intiTab();
		initButton();
		tabObj.selectTabItem("basic");
		intiForm();
		initData();
	});
	
</script>
</head>
<body>
	<div id='template.center'>
		<div id="tab" style="width: 100%; overflow: hidden;">
			<div tabid="basic" title="基本信息" lselected="true">
				<form id="basicform" action="${ctx}/report/mgr/reportInfo/saveReportInfo" method="post"></form>
			</div>
		</div>
	</div>
</body>
</html>