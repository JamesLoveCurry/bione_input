<%@ page language="java" pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<%@ page
	import="org.apache.shiro.web.filter.authc.FormAuthenticationFilter"%>
<html>
<meta name="decorator" content="/template/template5.jsp">
<head>
<script type="text/javascript">

	var groupicon = "${ctx}/images/classics/icons/communication.gif";
	//创建表单结构 
	var mainform;
	$(function() {
		mainform = $("#mainform");
		mainform.ligerForm({
			fields : [ 
			          {
			        	  name : "templeId",
			        	  type : "hidden"
			          },
			{
				display : "模板名称<font color='red'>*</font>",
				name : "templeName",
				newline : true,
				type : "text",
				group : "模板基础信息",
				groupicon : groupicon,
				validate : {
					remote : {
						url : "${ctx}/udip/temple/checkTempleName",
						type : 'get',
						async : true,
						data: {
							id: "${id}"
						}
					} ,
					messages : {
						remote:"模板名已存在，请检查！"
					},
					required : true,
					maxlength : 100
				}
			},{
				display:"所属目录ddd<font color='red'>*</font>",
	        	name:'dirName',
	        	newline:true,
	        	type:'select',
	        	options : {
	        		openwin : true,
					valueFieldID : "dirId"
				},
				validate : {
					required : true,
					maxlength : 256
				}
			},{
				display:"模板状态<font color='red'>*</font>",
	        	name:'stateValue',
	        	newline:true,
	        	type:'select',
	        	options:{
	        		valueFieldID:'state',
	        		data:[{
	        			text:'启用',
	        			id:'1'
	        		},{
	        			text:'停用',
	        			id:'0'
	        		}]
	        	},
				validate:{
					required:true
				}
			},{
				display:"是否审核",
	        	name:'authable',
	        	newline:true,
	        	type:'checkbox'
			},{
				display:"可新增",
	        	name:'addable',
	        	newline:false,
	        	type:'checkbox'
			},{
				display:"可修改",
	        	name:'updatable',
	        	newline:true,
	        	type:'checkbox'
			},{
				display:"可删除",
	        	name:'deletable',
	        	newline:false,
	        	type:'checkbox'
			},{
				display : "模板描述",
				name : "remark",
				newline : true,
				type : "textarea",
				width : 503,
				attr : {
					style : "resize: none;"
				},
				validate : {
					required:true,
					maxlength : 500
				}
			}]
		});
		if("${id}") {
			$.ajax({
				url : "${ctx}/udip/temple/findTempleInfo?templeId=${id}",
				success : function(data) {
					
					var authable = $("#authable").ligerCheckBox({ disabled: false });
					var addable = $("#addable").ligerCheckBox({ disabled: false });
					var updatable = $("#updatable").ligerCheckBox({ disabled: false });
					var deletable = $("#deletable").ligerCheckBox({ disabled: false });
					if(data.authable=='1'){
						authable.setValue(true);
					}else{
						authable.setValue(false);
					}
					if(data.addable=='1'){
						addable.setValue(true);
					}else{
						addable.setValue(false);
					}
					if(data.updatable=='1'){
						updatable.setValue(true);
					}else{
						updatable.setValue(false);
					}
					if(data.deletable=='1'){
						deletable.setValue(true);
					}else{
						deletable.setValue(false);
					}
					
					$("#mainform [name='templeId']").val(data.templeId);
					$("#mainform [name='templeName']").val(data.templeName);
					$("#mainform [name='remark']").val(data.remark);
					
					$("#mainform input[name='state']").val(data.state);
					if(data.state == "1"){
						$("#mainform [name='stateValue']").val("启用");
					}else{
						$("#mainform [name='stateValue']").val("停用");
					}
					$.ajax({
						async : false,
						url : '${ctx}/udip/dir/findDirIdAndName/'+data.dirId,
						success : function(data2) {
							
							$("#mainform input[name='dirName']").val(data2.dirName);
							$("#mainform input[name='dirId']").val(data2.dirId);
						}
					});
					
				}
			});	
		}
		$("#dirName").ligerComboBox({
			onBeforeOpen : function() {
				openNewDilog();
				return false;
			}
		});
		jQuery.metadata.setType("attr", "validate");
		BIONE.validate(mainform);
		var buttons = [];
		buttons.push({
			text : '取消',
			onclick : cancleCallBack
		});
		buttons.push({
			text : '下一步',
			onclick : next
		});
		buttons.push({
			text : '确定',
			onclick : save_objDef
		});
		BIONE.addFormButtons(buttons);

	});
	function openNewDilog() {
		dirName = $("#dirName");
		dirId = $("#dirId");
		dialog = BIONE.commonOpenDialog('选择目录', "dirList", "700", "350","${ctx}/udip/temple/templeDir/1");
		
	}
	
	function next() {
		parent.next('2','${id}');
	}
	function cancleCallBack() {
		parent.closeDsetBox();
	}
	function save_objDef() {
		BIONE.submitForm($("#mainform"), function(text) {
			BIONE.tip("保存成功");
		}, function() {
			BIONE.tip("保存失败");
		});
	}
	
	/**
	 * 获取光标的位置
	 * @param 控件对象
	 */
	function getCursorPosition(txb){
	    var slct = document.selection;
	    var rng = slct.createRange();
	    txb.select();
	    rng.setEndPoint("StartToStart", slct.createRange());
	    var psn = rng.text.length;
	    rng.collapse(false);
	    rng.select();
	    
	    return psn;
	}
	
	/**
	 * 插入指标或函数
	 */
	function insertText(text){
		var value=selectedInput.value;
		var posion = selectedInput.posion;
		selectedInput.focus();
		while(value.substr(posion,2).indexOf('\n')!=-1){//换行问题
			posion+=2;
		}
		value=value.substr(0,posion).concat(text,value.substr(posion,value.length));
		selectedInput.value = value;
	}
</script>
</head>
<body>
<div id="template.center">
		<form id="mainform" action="${ctx}/udip/temple/tab1-save-update" method="post"></form>
	</div>
</body>
</html>