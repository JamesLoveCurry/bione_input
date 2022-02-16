<%@ page language="java" pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<%@ page
	import="org.apache.shiro.web.filter.authc.FormAuthenticationFilter"%>
<html>
<meta name="decorator" content="/template/template5.jsp">
<head>
<script type="text/javascript" src="${ctx}/js/udip/remark/global.js"></script>
<script type="text/javascript" src="${ctx}/js/udip/remark/temple.js"></script>
<script type="text/javascript">

	var catalogId='${catalogId}';
	var catalogName='${catalogName}';
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
				display : "模板名称",
				name : "templeName",
				newline : true,
				type : "text",
				group : "模板基础信息",
				groupicon : groupicon,
				validate : {
					remote : {
						url : "${ctx}/rpt/input/temple/checkTempleName",
						type : 'post',
						async : false,
						data: {
							id: "${id}"
						}
					} ,
					messages : {
						remote:"模板名已存在，请检查！"
					},
					required : true,
					maxlength : 128 
				}
			},{
				display:"所属目录",
	        	name:'catalogName',
	        	newline:false,
	        	type:'popup',
	        	options : {
					openwin : true,
					valueFieldID : "catalogId",
					onButtonClick: openNewDilog
				},
				validate : {
					required : true,
					maxlength : 256
				}
			}/*,{
				display: '定义权限',
				type: 'select',
				name: '$defsrc',
				newline: true,
				options: {
					readonly:true,
					valueFieldID: 'defSrc',
					initValue: '01',
					data: [{
						text: '全行',
						id: '01'
					}]
				}
			}*/,{
				display : "负责人",
				name : "dutyUser",
				newline : false,
				type : "text",
				validate : {
					maxlength : 128 
				}
			}/*,{
				display : "所属单位",
				name : "dutyUserDept",
				newline : true,
				type : "text",
				validate : {
					maxlength : 256
				},
				options: {
					readonly:true
				}
			}*/,{
				display:"模板状态",
	        	name:'templeSts',
	        	newline:false,
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
				display : '机构',
				name : 'orgNoID',
				newline : true,
	 			type : 'select',
				attr : {
					field : 'orgNoID',
					op : "="
				},
	 			options : {
	 				onBeforeOpen: function() {
						BIONE.commonOpenIconDialog('选择机构', 'orgComBoBox',
								'${ctx}/rpt/input/temple/asyncOrgTree?multi=1', 'orgNoID');
	 				},
					hideOnLoseFocus : true,
					slide : false,
					selectBoxHeight : 1,
					selectBoxWidth : 1,
					resize : false,
					switchPageSizeApplyComboBox : false
	 			},
	 			width : '300'
			},{
				display : '用户',
				name : 'userID',
				newline : false,
	 			type : 'select',
				attr : {
					field : 'userID',
					op : "="
				},
	 			options : {
	 				onBeforeOpen: function() {
						BIONE.commonOpenIconDialog('选择用户', 'userComBoBox',
								'${ctx}/rpt/input/temple/asyncUserTree?multi=1&isyg=1', 'userID');
	 				},
					hideOnLoseFocus : true,
					slide : false,
					selectBoxHeight : 1,
					selectBoxWidth : 1,
					resize : false,
					switchPageSizeApplyComboBox : false
	 			},
	 			width : '300'
			},{
				display:"可新增",
	        	name:'allowAdd',
	        	newline:true,
	        	type:'checkbox'
			},{
				display:"可修改",
	        	name:'allowUpdate',
	        	newline:false,
	        	type:'checkbox'
			},{
				display:"可删除",
	        	name:'allowDelete',
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
					maxlength : 500
				}
			}],
			labelWidth : 90
		});
		$('#mainform ul > .l-fieldcontainer.l-fieldcontainer-first > ul > li[id^="mainform"] > div.l-checkbox-wrapper').parent().css('margin-top', '4px');
		liger.get('templeSts').selectValue(1);

		$.ligerui.get("userID").setDisabled();
		$("#mainform input[name='userID']").parent().parent().parent().hide();
		$.ligerui.get("orgNoID").setDisabled();
		$("#mainform input[name='orgNoID']").parent().parent().parent().hide();
		if("${id}") {
			$.ajax({
				url : "${ctx}/rpt/input/temple/findTempleInfo?templeId="+"${id}"+"&d="+new Date().getTime(),
				success : function(data) {
// 					var isCheck = $("#isCheck").ligerCheckBox({ disabled: false });
					var allowAdd = $("#allowAdd").ligerCheckBox({ disabled: false });
					var allowUpdate = $("#allowUpdate").ligerCheckBox({ disabled: false });
					var allowDelete = $("#allowDelete").ligerCheckBox({ disabled: false });
					//var allowInputHist = $("#allowInputHist").ligerCheckBox({ disabled: false });
					
					if(data.isCheck == null){
// 						isCheck.setValue(true);
						allowAdd.setValue(true);
						allowUpdate.setValue(true);
						allowDelete.setValue(true);
						$.ligerui.get("orgNoID").setDisabled();
						$("#mainform input[name='orgNoID']").parent().parent().parent().hide();
						$.ligerui.get("userID").setDisabled();
						$("#mainform input[name='userID']").parent().parent().parent().hide();
					}else{
// 						if(data.isCheck=='1'){
// 							isCheck.setValue(true);
// 						}else{
// 							isCheck.setValue(false);
// 						}
						if(data.allowAdd=='1'){
							allowAdd.setValue(true);
						}else{
							allowAdd.setValue(false);
						}
						if(data.allowUpdate=='1'){
							allowUpdate.setValue(true);
						}else{
							allowUpdate.setValue(false);
						}
						if(data.allowDelete=='1'){
							allowDelete.setValue(true);
						}else{
							allowDelete.setValue(false);
						}
						//if(data.allowInputHist=='1'){
						//	allowInputHist.setValue(true);
						//}else{
						//	allowInputHist.setValue(false);
						//}
						$("#mainform [name='templeId']").val(data.templeId);
						$("#mainform [name='templeName']").val(data.templeName);
						$("#mainform [name='remark']").val(data.remark);
						$("#mainform [name='dutyUser']").val(data.dutyUser);
						//$("#mainform [name='dutyUserDept']").val(data.dutyUserDept);
						$("#mainform input[name='catalogId']").val(data.catalogId);
						$("#mainform input[ligeruiId='catalogName']").val(data.catalogName);
						$.ligerui.get("templeSts").selectValue(data.templeSts);
						//liger.get('$unit').selectValue(data.unit);
						//$("#mainform input[name='$defsrc']").val(data.defSrc);
						//liger.get('$defsrc').setValue(data.defSrc);
						liger.get('orgNoID').setValue(data.defOrg);
						liger.get('orgNoID').setText(data.defOrgNm);
						liger.get('userID').setValue(data.defUser);
						liger.get('userID').setText(data.defUserNm);
						if(data.defSrc=="02"){
							
							$.ligerui.get("orgNoID").setEnabled();
							$("#mainform input[name='orgNoID']").parent().parent().parent().show();
							$.ligerui.get("userID").setDisabled();
							$("#mainform input[name='userID']").parent().parent().parent().hide();
						}else if(data.defSrc=="03"){
							$.ligerui.get("userID").setEnabled();
							$("#mainform input[name='userID']").parent().parent().parent().show();
							$.ligerui.get("orgNoID").setDisabled();
							$("#mainform input[name='orgNoID']").parent().parent().parent().hide();
						}else{
							$.ligerui.get("userID").setDisabled();
							$("#mainform input[name='userID']").parent().parent().parent().hide();
							$.ligerui.get("orgNoID").setDisabled();
							$("#mainform input[name='orgNoID']").parent().parent().parent().hide();
						}
						
						/* $.ajax({
							async : false,
							url : '${ctx}/udip/dir/findcatalogIdAndName/'+data.catalogId,
							success : function(data2) {
								
								$("#mainform input[name='catalogName']").val(data2.catalogName);
								$("#mainform input[name='catalogId']").val(data2.catalogId);
							}
						});  */
					}
					
					
				}
			});	
		}
		if(catalogId){
			$("#mainform input[name='catalogId']").val(catalogId);
			$("#mainform input[ligeruiId='catalogName']").val(catalogName);

		}

		$("#catalogName").ligerComboBox({
			onBeforeOpen : function() {
				if(!window.parent.lookType){
					openNewDilog();
				}
				return false;
			}
		});


		$("#templateSts").ligerComboBox({
			onBeforeOpen : function() {
				if(window.parent.lookType){
					return false;
				}
			}
		});
		jQuery.metadata.setType("attr", "validate");
		BIONE.validate(mainform);
		var buttons = [];
		buttons.push({
			text : '关闭',
			onclick : cancleCallBack
		});
		buttons.push({
			text : '下一步',
			onclick : next
		});
		
		if(!window.parent.lookType){
			buttons.push({
				text : '保存',
				onclick : save_objDef
			});
		}
		BIONE.addFormButtons(buttons,true);
		
		$("#dutyUser").click(function(){
			openUserview();
		})
	});
	function openNewDilog() {
		catalogName = $("#catalogName");
		catalogId = $("#catalogId");
		dialog = BIONE.commonOpenDialog('选择目录', "dirList", "700", "350","${ctx}/rpt/input/temple/templeCatalog/1");
	}
	function selectCata(text, val) {
		var pop = liger.get('catalogName');
		pop.setText(text);
		pop.setValue(val);
	}
	function next() {
		setTempleInfo();
		if (mainform.valid()) {
			parent.next('2','${id}');
		}
	}
	function setTempleInfo(){
		var isCheck="1";
/* 		var isCheck;
		if($('#isCheck').attr('checked')=="checked"){
			isCheck='1';
		}else{
			isCheck='0';
		} */
		var allowAdd;
		if($("#allowAdd").ligerCheckBox().getValue()){
			allowAdd='1';
		}else{
			allowAdd='0';
		}
		var allowUpdate;
		if($("#allowUpdate").ligerCheckBox().getValue()){
			allowUpdate='1';
		}else{
			allowUpdate='0';
		}
		var allowDelete;
		if($("#allowDelete").ligerCheckBox().getValue()){
			allowDelete='1';
		}else{
			allowDelete='0';
		}
		//var allowInputHist;
		//if($('#allowInputHist').attr('checked')=="checked"){
		//	allowInputHist='1';
		//}else{
		//	allowInputHist='0';
		//}
		var templeSts=$.ligerui.get("templeSts").getValue();
		var info = {"templeId" : "${id}",
				"templeName" : $.trim(document.getElementById('templeName').value),
				"dutyUser" : $.trim(document.getElementById('dutyUser').value),
				//"dutyUserDept" : $.trim(document.getElementById('dutyUserDept').value),
				"catalogId" : document.getElementById('catalogId').value,
				"templeSts" : templeSts,
				"isCheck" : isCheck,
				"allowAdd" : allowAdd,
				"allowUpdate" : allowUpdate,
				"allowDelete" : allowDelete,
				//"allowInputHist" : allowInputHist,
				"remark" : document.getElementById('remark').value,
				"dsId" : "",
				"tableName" : "",
				"orgColumn" : "",
				"paramStr" : "",
				//"unit": liger.get('$unit').getValue(),
				//"defSrc": liger.get('$defsrc').getValue(),
				"defSrc": '01',
				"defOrg" : ","+liger.get('orgNoID').getValue(),
				"defUser" : ","+liger.get('userID').getValue()
				
		};
		parent.setTempleInfo_tab1( info );
	}
	function cancleCallBack() {
		parent.closeDsetBox();
	}
	function save_objDef(){
		setTempleInfo();
		if (mainform.valid()) {
			parent.saveTempleInfo();
		}
	}
	function openUserview() {
		BIONE.commonOpenDialog('选择负责人', "userView", "700", "500","${ctx}/rpt/input/temple/templeSelUser");
	}
	/*function setDutyUser(username,deptname) {
		var userPop = liger.get('dutyUser');
		userPop.setValue(username);
		var deptPop = liger.get('dutyUserDept');
		deptPop.setValue(deptname);
	}*/
	//对输入信息的提示
	/* function check() {
		$("#templeName").focus(
				function() {
					checkLabelShow(TempleRemark.global.templeName);
					$("#checkLabelContainer").html(
							GlobalRemark.title
									+ TempleRemark.global.templeName);
				});
		$("#dutyUser").focus(
				function() {
					checkLabelShow(TempleRemark.global.dutyUser);
					$("#checkLabelContainer").html(
							GlobalRemark.title
									+ TempleRemark.global.dutyUser);
				});
		$("#dutyUserDept").focus(
				function() {
					checkLabelShow(TempleRemark.global.dutyUserDept);
					$("#checkLabelContainer").html(
							GlobalRemark.title
									+ TempleRemark.global.dutyUserDept);
				});
		$("#templateSts").change(
				function() {
					checkLabelShow(TempleRemark.global.templateSts);
					$("#checkLabelContainer").html(
							GlobalRemark.title
									+ TempleRemark.global.templateSts);
				});
		$("#isCheck").change(
				function() {
					checkLabelShow(TempleRemark.global.isCheck);
					$("#checkLabelContainer").html(
							GlobalRemark.title+ TempleRemark.global.isCheck 
									);
				});
		$("#allowAdd").change(
				function() {
					checkLabelShow(TempleRemark.global.allowAdd);
					$("#checkLabelContainer").html(
							GlobalRemark.title+ TempleRemark.global.allowAdd 
									);
				});
		$("#allowUpdate").change(
				function() {
					checkLabelShow(TempleRemark.global.allowUpdate);
					$("#checkLabelContainer").html(
							GlobalRemark.title+ TempleRemark.global.allowUpdate 
									);
				});
		$("#allowDelete").change(
				function() {
					checkLabelShow(TempleRemark.global.allowDelete);
					$("#checkLabelContainer").html(
							GlobalRemark.title+ TempleRemark.global.allowDelete 
									);
				});
		$("#allowInputHis").change(
				function() {
					checkLabelShow(TempleRemark.global.historyable);
					$("#checkLabelContainer").html(
							GlobalRemark.title+ TempleRemark.global.historyable 
									);
				});
		$("#remark").focus(
				function() {
// 					checkLabelShow(TempleRemark.global.remark);
					$("#checkLabelContainer").html(
							GlobalRemark.title
									+ TempleRemark.global.remark);
				});
	} */
</script>
</head>
<body>
<div id="template.center">
		<form id="mainform" action="" method="post"></form>
	</div>
</body>
</html>