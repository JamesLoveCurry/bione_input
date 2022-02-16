<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<html>
<meta name="decorator" content="/template/template5.jsp">
<head>
<script type="text/javascript">
	//合法时间格式[**:**]验证
	jQuery.validator.addMethod("HourAndMinutTime", function(value, element, params) {
		if(value!=null&&value!=""){
			if (params) {
				var exp = new RegExp(/^(([0-1]\d)|2[0-3]):([0-5]\d)$/);
				return exp.test(value);
			}
		}else{
			return true;
		}
	}, "请输入格式为[##:##]的时间");
	
	jQuery.validator.addMethod("taskNm", function(value, element) {
	    var numReg = /^[0-9a-zA-Z\u2160-\u216B\u4e00-\u9fa5_-]*$/;//数字、字母、罗马数字、中文、下划线、横线
	    return this.optional(element) || (numReg.test(value));
	}, "任务名称命名不合法");

	jQuery.validator.addMethod("dateOffsetAmountFun", function(val, obj) {
		var value = parseInt(val);
		return (value | 0) === value
	}, "数据日期必须填整数");
</script>

<script type="text/javascript">
	var taskId="${taskId}";
	var filldate = 3;
	var taskInfo = '${taskInfo}';
	var procDef = '${procDef}';
	var isPublish = '${isPublish}';
	var check = parent.check;
	var moduleType = parent.moduleType;
	var newTaskInfo; //任务基本信息
	var tskobjs;     //报表-任务对象
	var exeobjs;     //机构-执行对象
	var isUse;
	var taskType = [];

	$(function() {
		if(taskInfo && taskInfo != null && taskInfo != ""){
			taskInfo=JSON2.parse('${taskInfo}') ;
		}
		if(procDef && procDef != null && procDef != ""){
			procDef=JSON2.parse('${procDef}') ;
		}
		initTaskData();
		initForm();
		initBtn();
		$("#mainform input[name='taskDeadlineTime']").hover(function (){
					$(this).ligerTip({content:'格式：hh:mm。例如：13:14（在13点14分触发）',x:10,y:10});
				},function (){
					$(this).ligerHideTip();
				}
		);
		$("#mainform input[name='dateOffsetAmount']").hover(function (){
					$(this).ligerTip({content:'0:当前日期 -1:当前日期前一天 9999:本月月末',x:10,y:10});
				},function (){
					$(this).ligerHideTip();
				}
		);
	});
	
	//初始化按钮
	function initBtn(){
		var buttons = [];
		buttons.push({
		    text : '取消',
		    onclick : function() {
				BIONE.closeDialog("addNewTskWin", null);
		    }
		});
		buttons.push({
		    text : '保存',
		    onclick : taskSave
		});
		BIONE.addFormButtons(buttons);
	}
	
	function initTaskData(){
		$.ajax({
		    async : false,
		    dataType : "json",
		    type : "post",
			url : "${ctx}/report/frame/datashow/idx/busiTypeList.json",
		    data : {
		    	moduleType : moduleType
		    },
		    success : function(result) {
		    	if(result){
		    		taskType = result;
		    	}
		    }			
		});
	}
	
	function initForm(){
		$("#mainform").ligerForm({
			labelWidth : 100,
			inputWidth : 280,
			fields : [{
				display : "任务名称",
				name : "taskNm",
				newline : true,
				type : "text",
				cssClass : "field",
				group : "基本信息", 
				groupicon : "${ctx}/images/classics/icons/communication.gif",
				validate : {
					required : true ,
					taskNm : true,
					remote : {
						url : '${ctx}/frs/rpttsk/publish/checkTaskName', 
						type : "GET",
						data : {
							taskId : taskId
						}
					},
					messages : {
						remote : "任务名称已存在"
					}
				}
			},{
				display:"任务状态",
				name : "taskSts",
				comboboxName:"taskStsBox",
				newline : false,
				type:"select",
				cssClass:"field",
				options:{
					data:[{
						text:"启用",
						id : "1"
					},{
						text:"停止",
						id : "0"
					}]
				}
			},{
				display:"生效日期",
				name:"effectDate",
				type:"date",
				cssClass:"field",
				newline : true,
				validate :{
					required : true,
					greaterThanNow : taskId == null ? true : false 
				}
			},{
				display:"失效日期",
				name:"invalidDate",
				type:"date",
				cssClass:"field",
				newline : false,
				options :{
					initValue: "2999-12-31",
					format : "yyyy-MM-dd"
				}, 
				validate :{
					required : true,
					greaterThan : "effectDate",
					messages : {
						greaterThan : "失效日期不能小于生效日期"
					}
				} 
			},{
				display : "任务类型",
				name : "taskType",
				comboboxName:"taskTypeBox",
				newline : true,
				type : "select",
				cssClass : "field",
				options : {
					cancelable :false,
					data : taskType,
					onSelected : function(value){
						if(value){  //根据任务类型确定执行机构类型
							$.ligerui.get('exeObjTypeBox').selectValue(value);
							window.frames.leftframe.initTree("rpt", value);
							window.frames.rightframe.initTree("org", value);
						}
					}
				}
			},{
				display : "机构树类型",
				name : "exeObjType",
				comboboxName:"exeObjTypeBox",
				newline : false,
				type : "select",
				cssClass : "field",
				options : {
				    cancelable :false,
				    data : taskType,
				    onBeforeSelect:function(value){
				    	var val = $("#mainform input[name='taskType']").val();
				    	if(val=="02"){//1104
				    		if(value!="02"){
				    			BIONE.tip("[1104监管]只能选[总分行机构]为下发机构!");
				    			return false;
				    		}
				    	}
				    	if(val=="01"){//利率报备
				    		if(value!="03"){//行政区域
				    			BIONE.tip("[利率报备]只能选[行政区域]为下发机构!");
				    			return false;
				    		}
				    	}
				    	if(val=="03"){
				    		if(value!="01"){//行政区域
				    			BIONE.tip("[人行大集中]只能选[行政区域]为下发机构!");
				    			return false;
				    		}
				    		//专用利率报备 只有总行(同1104机构)
				    	}
				    },
				    cancelable  : false
				}
			},{
				display : "下发报表",
				name : "tskobjId",
				newline : true,
				type : "text",
				cssClass : "field"
			},{
				display : "下发机构",
				name : "exeObjId",
				newline : false,
				type : "text",
				cssClass : "field"
			},{
				display : "填报期限",
				name:"taskDeadline",
				newline:true,
				type:"text",
				cssClass:"field"
			},{
				display : "填报人参与审批",
				name : "isApprove",
				comboboxName:"isApproveComboBox",
				newline : false,
				type:"select",
				cssClass:"field",
				options :{
					initValue : "是",
					data :[{
						text:"是",
						id :"是"
					},{
						text:"否",
						id : "否"
					}]
				}
			},{
				display : "是否填报下级",
				name : "isFill",
				comboboxName:"isFillComboBox",
				newline : true,
				type:"select",
				cssClass:"field",
				options :{
					initValue : "是",
					data :[{
						text:"是",
						id :"是"
					},{
						text:"否",
						id : "否"
					}]
				}
			},{
				display : "是否复核本级",
				name : "isCheck",
				comboboxName:"isCheckComboBox",
				newline : false,
				type:"select",
				cssClass:"field",
				options :{
					initValue : "是",
					data :[{
						text:"是",
						id :"是"
					},{
						text:"否",
						id : "否"
					}]
				}
			},{
				display : "是否审核本级",
				name : "isExamine",
				comboboxName:"isExamineComboBox",
				newline : true,
				type:"select",
				cssClass:"field",
				options :{
					initValue : "是",
					data :[{
						text:"是",
						id :"是"
					},{
						text:"否",
						id : "否"
					}]
				}
			},{
				display : "频度",
				name : "taskFreq",
				comboboxName:"taskFreqBox",
				newline : true,
				type : "select",
				cssClass : "field",
				group : "下发方式", 
				groupicon : "${ctx}/images/classics/icons/communication.gif",
				options : {
				    data : [ {
						text : '日',
						id : "01"
				    }, {
						text : '周',
						id : '02'
				    }, {
						text : '旬',
						id : '03'
					},{
						text : '月',
						id : "04"
				    }, {
						text : '季',
						id : '05'
				    }, {
						text : '半年',
						id : '06'
					}, {
						text : '年',
						id : '07'
					}]
				}
			},{
				display : "是否启用触发",
				name:"isUse",
				comboboxName:"isUseBox",
				newline:false,
				type:"select",
				cssClass:"field",
				options :{
					data :[{
						text:"是",
						id : "Y"
					},{
						text:"否",
						id :"N"
					}],
					onSelected : function(value, text){
						if(value == "N"){
							$.ligerui.get("triggerIdBox").setData("");
							$.ligerui.get("triggerIdBox").setText("");
							$.ligerui.get("triggerIdBox").setValue("");
							$.ligerui.get("triggerIdBox").setDisabled();
						}else if(value == "Y"){
							$.ligerui.get("triggerIdBox").setEnabled();
						}
					}
				}
			},{
				display : "触发器",
				name:"triggerId",
				comboboxName:"triggerIdBox",
				type:"select",
				cssClass:"field",
				newline:false,
				options :{
					onBeforeOpen:function(value){
						var triggerId =taskId?taskInfo.taskInfo.triggerId:null;
						$.ligerDialog.open({
							name:'addTriggerWin',
							title : '配置触发器',
							width :600,
							height : $(window).height()-40-10,
							url:'${ctx}/frs/rpttsk/publish/selectTriggerNew?triggerId='+triggerId,
							buttons : [ {
								text : '确定',
								onclick : f_selectOK
							}, {
								text : '取消',
								onclick : f_selectCancel
							} ]
						});
						return false;
					}
					
				}
			},{
				display : "数据日期",
				name:"dateOffsetAmount",
				newline:false,
				type:"text",
				cssClass:"field",
				validate: {
					dateOffsetAmountFun: true,
				}
			},{
				display: '期限时间',
				name:"taskDeadlineTime",
				newline:true,
				type:"text",
				validate : {
					HourAndMinutTime : true
				}
			},{
				name:"logicDelNo",
				type: "hidden" 
			},{
				display : "流程定义",
				name : "procDef",
				comboboxName:"procDefBox",
				newline : true,
				type : "select",
				cssClass : "field",
				group : "流程定义", 
				groupicon : "${ctx}/images/classics/icons/communication.gif",
				options : {
					url: '${ctx}/activiti/getAllModels',
					textField : 'NAME_',
					valueField : "KEY_",
				}
			}]
		
		});
		
		$("#deadEnd").parent().html("天后: ").width(40);
		
		//改变输入框的样式-----开始--------
		var tempLi_l = $("#tskobjId").parent().parent();
		var tipContent_l = [];
		var content_l = '<iframe frameborder="0" id="leftframe" name="leftframe" style="height:300px;width:280px;" src="${ctx}/frs/rpttsk/publish/showTreePage"></iframe>';
		tipContent_l.push(content_l);
		tempLi_l.html(tipContent_l.join(''));
		
		var tempLi_r = $("#exeObjId").parent().parent();
		var tipContent_r = [];
		var content_r = '<iframe frameborder="0" id="rightframe" name="rightframe" style="height:300px;width:280px;" src="${ctx}/frs/rpttsk/publish/showTreePage"></iframe>';
		tipContent_r.push(content_r);
		tempLi_r.html(tipContent_r.join(''));
		
		//改变输入框的样式-----结束--------
		
		jQuery.metadata.setType("attr", "validate");
		BIONE.validate($("#mainform"));
		
		$.ligerui.get("taskTypeBox").setDisabled();
		$("#taskTypeBox").css("color","#666");
		$.ligerui.get("exeObjTypeBox").setDisabled(); //机构类型
		$("#exeObjTypeBox").css("color","#666");
	}
	
	//初始化值
	function initData(){
		if(taskId){
			var tskInfo = taskInfo.taskInfo;
			$("#mainform input[name='taskNm']").val(tskInfo.taskNm);
			$.ligerui.get('exeObjTypeBox').selectValue(tskInfo.exeObjType);//行政区域
			$.ligerui.get('taskTypeBox').selectValue(tskInfo.taskType);//人行
			$.ligerui.get('taskStsBox').selectValue(tskInfo.taskSts);//任务状态 启用
			taskDeadlineStrSplit(tskInfo.taskDeadline);//填报期限
			$.ligerui.get('taskFreqBox').selectValue(tskInfo.taskFreq);//频度  年 
			$("#mainform input[name='effectDate']").val(formatDate(tskInfo.effectDate));
			$("#mainform input[name='invalidDate']").val(formatDate(tskInfo.invalidDate));
			$("#mainform input[name='triggerId']").val(tskInfo.triggerId);
			$("#mainform input[name='triggerIdBox']").val(tskInfo.triggerName);
			// $.ligerui.get('dateOffsetAmountBox').selectValue(tskInfo.dateOffsetAmount);
			$("#mainform input[name='dateOffsetAmount']").val(tskInfo.dateOffsetAmount);
			$("#mainform input[name='logicDelNo']").val(tskInfo.logicDelNo);
			$.ligerui.get('isUseBox').selectValue(tskInfo.isUse);
			//回显流程定义			
			$.ligerui.get('procDefBox').selectValue(procDef.procDefKey);
			//判断是否下发过
			if(isPublish>0 && procDef){
				$("#procDefBox").ligerComboBox({readonly: true});
			}
			$.ligerui.get('isApproveComboBox').selectValue(tskInfo.isApprove);
			$.ligerui.get('isFillComboBox').selectValue(tskInfo.isFill);
			$.ligerui.get('isCheckComboBox').selectValue(tskInfo.isCheck);
			$.ligerui.get('isExamineComboBox').selectValue(tskInfo.isExamine);
		}else{
			//设置表单默认值 
			$.ligerui.get('taskTypeBox').selectValue(moduleType); //任务类型
			$.ligerui.get('taskStsBox').selectValue("1");  //任务状态 启用
			$.ligerui.get('taskFreqBox').selectValue("07");  //频度  年 
			$("#mainform input[name='effectDate']").val(initdate());
			// $.ligerui.get('dateOffsetAmountBox').selectValue("-1");
			$("#mainform input[name='dateOffsetAmount']").val("-1");
			$("#mainform input[name='logicDelNo']").val("N");
			$.ligerui.get('isUseBox').selectValue("Y");          //是否启用触发
			$("#mainform input[name='taskDeadline']").val("3");  //填报期限 3天
		}
	}
	
	//保存按钮-任务保存方法
	function taskSave(){
		//长度校验
		var flag = true;
		var taskNmVal = $("#mainform input[name='taskNm']").val();
		if(taskNmVal.length>50){
			BIONE.tip("任务名称过长，不能超过50！");
			return;
		}
		// 基本校验
		if (!$("#mainform").valid()) {
			BIONE.tip("校验未通过！");
			return;
		}
		// 触发器校验 
		isUse = $("#mainform input[name='isUse']").val();
		var triggerId = $("#mainform input[name='triggerId']").val();
		if(isUse=="Y"&&triggerId==""){
			BIONE.tip("触发器已启用，请选择触发器！");
			return;
		}
		var taskType = $("#mainform input[name='taskType']").val();
		var exeObjType = $("#mainform input[name='exeObjType']").val();
		var taskDeadline = taskDeadlineStrJoin();
		var effectDate =formatDateStr($("#mainform input[name = 'effectDate']").val());
		var invalidDate = formatDateStr($("#mainform input[name = 'invalidDate']").val());
		var dateOffsetAmount = $("#mainform input[name = 'dateOffsetAmount']").val();
		// 1、 任务基本信息
		newTaskInfo = {
			taskId : taskId,
			taskNm : $("#mainform input[name='taskNm']").val(),
			taskSts : $("#mainform input[name='taskSts']").val(),
			taskType : taskType,
			exeObjType : exeObjType,
			taskDeadline : taskDeadline,
			taskFreq : $("#mainform input[name='taskFreq']").val(),
			triggerId : triggerId,
			isRelyData : "N",
			effectDate :effectDate,
			invalidDate : invalidDate,
			dateOffsetAmount :dateOffsetAmount,
			logicDelNo:$("#mainform input[name='logicDelNo']").val(),
			isUse : isUse,
			isApprove : $("#mainform input[name='isApprove']").val(),
			isFill : $("#mainform input[name='isFill']").val(),
			isCheck : $("#mainform input[name='isCheck']").val(),
			isExamine : $("#mainform input[name='isExamine']").val()
		};
		// 2、报表-任务对象 
		tskobjs = [];
		var rptCnt = 0;
		var leftTreeNodes = window.frames.leftframe.getCheckedRptNodes();
		if(leftTreeNodes.length>0){
			for ( var m = 0; m < leftTreeNodes.length; m++) {
				var node_l = leftTreeNodes[m];
				if(node_l.params.type=="rpt"){
					var tskObj = {
							id : {
								taskId : taskId,
								taskObjId : node_l.params.realId
							},
							taskObjType : taskType
					};
					tskobjs.push(tskObj);
					rptCnt = rptCnt +1;
				}
			}
		}
		if(rptCnt==0)
		{
			BIONE.tip("请至少选择一个下发报表");
			return;
		}
		// 流程定义
		var procDefKeyVal = $("#mainform input[name='procDef']").val();
		var procDefNameVal = $("#mainform input[name='procDefBox']").val();
		if(procDefKeyVal=="" || procDefNameVal==""){
			BIONE.tip("请选择流程定义！");
			return;
		};
		var procDefObj = {
			procDefKey : procDefKeyVal,
			procDefName: procDefNameVal
		};
		exeobjs = [];
		// 3、机构-执行对象
		var rightTreeNodes = window.frames.rightframe.getCheckedOrgNodes();  //通过方法getCheckedOrgNodes获取选择项
		if(rightTreeNodes.length>0){
			for ( var n = 0; n < rightTreeNodes.length; n++) {
				var node_r = rightTreeNodes[n];
				if((node_r.params.type=="org") && ("0" != node_r.params.orgNo)){
					var exeObj = {
							id : {
								taskId : taskId,
								exeObjId : node_r.params.orgNo,
								exeObjType : exeObjType,
								exeUpObjId : node_r.upId
							}
					};
					exeobjs.push(exeObj);
				}
			}
		}else{
			BIONE.tip("请至少选择一个下发机构");
			return;
		}
		if(exeobjs.length == 0){
			BIONE.tip("请至少选择一个下发机构");
			return;
		}
		var tskVO = {
		    	taskInfo : JSON2.stringify(newTaskInfo),
		    	tskobjs : JSON2.stringify(tskobjs),
		    	exeobjs : JSON2.stringify(exeobjs),
	    		useFlag : (isUse=="Y")?"true":"false",
   		    	procDef : JSON2.stringify(procDefObj)
		    };
		var d = $("#mainform input[name='taskDeadline']").val();
		if(d>0){
 			if(d%1==0){
			}else{
				BIONE.tip('填报期限输入格式有误，请检查后重新输入');
				return;
			}
		}else{
			BIONE.tip('填报期限输入格式有误，请检查后重新输入');
			return;
		}
		//保存
		$.ajax({
		    async : true,
		    dataType : "json",
		    type : "post",
			url : '${ctx}/frs/rpttsk/publish/saveTsk',
		    data : tskVO,
		    beforeSend : function() {
				BIONE.showLoading("正在加载数据中...");
		    },
		    success : function(result) {
		    	if(result.rs=="2"){
			    	BIONE.hideLoading();
					BIONE.tip('触发器时间和任务的开始结束时间冲突,请重新选择');
		    	}else if (result.rs=="1"){
			    	parent.grid.loadData();
			    	BIONE.hideLoading();
					parent.BIONE.tip('保存成功');
					BIONE.closeDialog("addNewTskWin");
		    	}else{
			    	BIONE.hideLoading();
			    	parent.BIONE.tip('任务保存失败,请联系管理员');
		    	}
		    },
			error : function(result, b) {
				parent.BIONE.tip('任务保存失败,请联系管理员');
				BIONE.closeDialog("addNewTskWin");
		    }

		});
		/* //判断报表+机构的任务实例是否已经存在
		$.ajax({
		    async : true,
		    dataType : "json",
		    type : "post",
			url : '${ctx}/frs/rpttsk/publish/isHaveTsk',
		    data : tskVO,
		    success : function(result) {
		    	if(result.isHaveTskIns=="YES"){
			    	BIONE.hideLoading();
					BIONE.tip('报表已在' + result.info +'任务中下发至您选择的机构,请重新配置任务');
		    	}else{
		    		
		    	}
		    }			
		}); */
	}
	
	//触发器配置页面的保存按钮调用方法
	function f_selectOK(a,dialog){
		var data = dialog.frame.addToParent();
		if(data!=""){
			$("#mainform input[name='triggerId']").val(data.triggerId);
			$("#mainform input[name='triggerIdBox']").val(data.triggerName);
		}
		dialog.close();
	}
	//触发器配置页面的取消按钮调用方法
	function f_selectCancel(a,dialog){
		dialog.close();
	}
	
	//填报期限拼接
	function taskDeadlineStrJoin(){
		var val = [];
		var d = $("#mainform input[name='taskDeadline']").val();
		val.push(d);
		var hAndM = $("#mainform input[name='taskDeadlineTime']").val();
		if(hAndM!=null&&hAndM!=""){
			var t = hAndM.split(":");
			val.push(t[0]);
			val.push(t[1]);
		}else{
			val.push("00");
			val.push("00");
		}
		return val.join("-");
	}
	//填报期限拆分
	function taskDeadlineStrSplit(val){
		if(val!=null&&val!=""){
			var valArray = val.split("-");
			$("#mainform input[name='taskDeadline']").val(valArray[0]);
			var deadEnd = "";
			if(valArray[1]=="00"&&valArray[2]=="00"){
			}else{
				deadEnd = valArray[1]+":"+valArray[2];
			}
			$("#mainform input[name='taskDeadlineTime']").val(deadEnd);
		}
	}
	//初始化当前日期
	function initdate() {
		var date = new Date();
		var y = date.getFullYear()+"";
		var m = (date.getMonth() + 1)+"";
		var d = date.getDate()+"";
		if(m.length==1){
			m = "0"+m;
		}
		if(d.length==1){
			d = "0"+d
		}
		var newDate = y+"-"+m+"-"+d;
		return newDate;
	}
	//格式化 20140808->2014-08-08
	function formatDate(val){
		var newDate = "";
		if(val.length>0){
			var y = val.substring(0,4);
			var m = val.substring(4,6);
			var d = val.substring(6,8);
			newDate = y+"-"+m+"-"+d;
		}
		return newDate;
	}
	//格式化 2014-08-08->20140808
	function formatDateStr(val){
		var newDate = "";
		if(val.length>0){
			var valArray = val.split("-");
			newDate = valArray[0]+valArray[1]+valArray[2];
		}
		return newDate;
	}
</script>
<title>Insert title here</title>
</head>
<body>
	<div id="template.center">
		<form name="mainform" method="post" id="mainform" action=""></form>
	</div>
</body>
</html>