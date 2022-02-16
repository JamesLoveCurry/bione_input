<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<html>
<head>
<meta name="decorator" content="/template/template5.jsp">
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
</script>

<script type="text/javascript">
	var taskId="${taskId}";
	var filldate = 3;
	var taskInfo = '${taskInfo}';
	var procDef = '${procDef}';
	var moduleType = parent.moduleType;

	$(function() {
		if(taskInfo&&taskInfo!=null&&taskInfo!='')
			taskInfo=JSON2.parse('${taskInfo}') ;
			procDef=JSON2.parse('${procDef}') ;
		//初始化form
		$("#mainform").ligerForm({
			labelWidth : 100,
			inputWidth : 280,
			fields : [ {
				display : "任务名称",
				name : "taskNm",
				newline : true,
				type : "text",
				cssClass : "field",
				group : "基本信息", 
				groupicon : "${ctx}/images/classics/icons/communication.gif",
				validate : {
					required : true ,
					remote : {
						url : '${ctx}/frs/rpttsk/publish/checkTaskNam',  
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
				type:"text",
				cssClass:"field",
				newline : true,
				validate :{
					required : true,
					greaterThanNow : taskId == null ? true : false 
				}
			} ,{
				display:"失效日期",
				name:"invalidDate",
				type:"text",
				cssClass:"field",
				newline : false,
				options :{
					initValue: "2099-12-31",
					format : "yyyy-MM-dd"
				}, 
				validate :{
					required : true,
					greaterThan : "effectDate",
					messages : {
						greaterThan : "失效日期不能小于生效日期"
					}
				} 
			}, {
				display : "任务类型",
				name : "taskType",
				comboboxName:"taskTypeBox",
				newline : true,
				type : "select",
				cssClass : "field",
				options : {
					//01 行内补录02 银监会1104报表 03 人行报表
				    data : [  {
						text : '人行大集中',
						id : "03"
				    },  {
						text : '1104监管',
						id : '02'
				    }, {
						text : '利率报备',
						id : '01'
					},{
						text : '存款保险',
						id : '05'
					}],
					onSelected : function(value){
						if(value!=null && value!=""){
							$.ligerui.get('exeObjTypeBox').selectValue(value);
							
							window.frames.leftframe.initTree("rpt", value);
							window.frames.rightframe.initTree("org", value);
						}
					}
				}
			}, {
				display : "机构树类型",
				name : "exeObjType",
				comboboxName:"exeObjTypeBox",
				newline : false,
				type : "select",
				cssClass : "field",
				options : {
				    //监管机构类型： 1-保留，2-人行机构，3-1104机构
				    data : [ {
						text : '人行大集中',
						id : "03"
				    },{
						text : '1104监管',
						id : "02"
				    }, {
						text : '利率报备',
						id : '01'
				    },{
						text : '存款保险',
						id : '05'
					}],
				    onBeforeSelect:function(value){
				    	//01 行内补录02 银监会1104报表 03 人行报表
				    	var val = $("#mainform input[name='taskType']").val();
				    	if(val=="02"){//1104
				    		if(value!="02"){//总分行
				    			BIONE.tip("[1104监管]只能选[总分行机构]为下发机构!");
				    			return false;
				    		}
				    	}
				    	
				    	if(val=="01"){//人行报表
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
				    }
				}
			} ,{
				display : "下发报表",
				name : "tskobjId",
				newline : true,
				type : "text",
				cssClass : "field"
			}, {
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
				display : "数据日期",
				name:"dateOffsetAmount",
				comboboxName:"dateOffsetAmountBox",
				newline:false,
				type:"select",
				cssClass:"field",
				options :{
					data :[{
						text:"当前日期",
						id :"0"
					},{
						text:"当前日期前一天",
						id : "-1"
					},{
						text:"当前日期前两天",
						id : "-2"
					},{
						text:"当前日期前三天",
						id : "-3"
					},{
						text:"当前日期前四天",
						id : "-4"
					},{
						text:"当前日期前五天",
						id : "-5"
					}]
				}
			},{
				display : "填报人参与审批",
				name : "isApprove",
				comboboxName:"isApproveComboBox",
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
				    //01 日02 周03 旬04 月05 季06 半年07 年
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
					}]
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
						var vvv = $("#mainform input[name='isUse']").val();
						if(vvv == "N"){
							BIONE.tip("【是否启用触发】为【否】无需修改触发器");
							return false;
						}
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
		
		var aa =['taskNm','effectDate','invalidDate','taskDeadlineTime','taskDeadline'];
		for(var i =0;i<aa.length;i++){
			$("#"+aa[i]).attr("readonly", "true");
			$("#"+aa[i]).css("color","#666");
		} 	
		
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
		//初始化按钮
		var buttons = [];
		buttons.push({
		    text : '取消',
		    onclick : function() {
				BIONE.closeDialog("addNewTskWin", null);
		    }
		});
		BIONE.addFormButtons(buttons);
		
		$("#deadEnd").parent().html("天后").width(40);
		
		$.ligerui.get("taskTypeBox").setDisabled();//.attr("readOnly","true");
		$("#taskTypeBox").css("color","#666");
		$.ligerui.get("exeObjTypeBox").setDisabled();//.attr("readOnly","true");
		$("#exeObjTypeBox").css("color","#666");
		
	});
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
			$("#mainform input[name='logicDelNo']").val(tskInfo.logicDelNo);
			$.ligerui.get('isUseBox').selectValue(tskInfo.isUse);
			$.ligerui.get('procDefBox').selectValue(procDef.procDefKey);

			var bb =['taskStsBox','taskFreqBox','isUseBox','triggerIdBox'];
			for(var i = 0;i < bb.length;i++){
				$.ligerui.get(bb[i]).setDisabled();
				$("#"+bb[i]).css("color","#666");
			}
			$.ligerui.get('dateOffsetAmountBox').selectValue(tskInfo.dateOffsetAmount);
			$.ligerui.get('isApproveComboBox').selectValue(tskInfo.isApprove);
		}else{
			//设置表单默认值 
			$.ligerui.get('taskTypeBox').selectValue(moduleType);//人行
			$.ligerui.get('taskStsBox').selectValue("1");//任务状态 启用
			$.ligerui.get('taskFreqBox').selectValue("07");//频度  年 
			$("#mainform input[name='effectDate']").val(initdate());
			$("#mainform input[name='logicDelNo']").val("N");
			$.ligerui.get('isUseBox').selectValue("N");//是否启用触发
			$("#mainform input[name='taskDeadline']").val("3");
		}
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