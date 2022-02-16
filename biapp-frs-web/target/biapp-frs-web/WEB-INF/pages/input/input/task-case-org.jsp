<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<html>
<head>
<meta name="decorator" content="/template/template8_1.jsp">
<script type="text/javascript" src="${ctx}/js/udip/UDIP.js"></script>
<script type="text/javascript">

var orgTree ;
$(function() {
	BIONE.showLoading("正在加载中...");
	var orgSetting={
		data : {
			keep : {
				parent : true
			},
			key : {
				name : "text"
			},
			simpleData : {
				enable : true,
				idKey : "id",
				pIdKey : "upId",
				rootPId : null
			}
		},
		check : {
			chkStyle : 'checkbox',
			enable : true,
			nocheckInherit : true,
			chkboxType : {
				"Y" : "",
				"N" : ""
			}
		},
		view : {
			selectedMulti : false,
			showLine : false
		}
	};
	
	orgTree = $.fn.zTree.init(eval($("#orgTree")),orgSetting);
	
	$.ajax({
		cache : false,
		async : false,
		url : "${ctx}/udip/task/roleandorg/orgList.json",
		success : function(result) {
			orgTree.addNodes(null, result.orgList, false);
			selectNode();
		},
		error : function(result, b) {
			BIONE.tip('发现系统错误 <BR>错误码：' + result.status);
		}
	});
	
	function selectNode(){
		$.ajax({
			async : true,
			type : 'get',
			datatype : 'json',
			url : "${ctx}/udip/task/roleandorg/getByCaseId.json?caseId="+"${caseId}",
			success : function(result) {
				var orgs = result.org;
				var all = orgTree.getCheckedNodes(false);
				$.each(orgs||[],function(i,node){
					var n = UDIP.findTreeNodeById(all,node.id);
					if(n)orgTree.checkNode(n,true,true,false);
				});
				BIONE.hideLoading();
			}
		});
		if ($("#orgTreeContainer") && $("#center")) {
			$("#orgTreeContainer").height($("#center").height() - 31);
		}
	}
	
	$("#template_center_combobox").ligerComboBox({  
        onSelected: selectRelation
    }); 
	
	BIONE.createButton({
		appendTo : "#bottom",
		text : '确定',
		icon : 'save',
		width : '50px',
		align :'right',
		click : function() {
			BIONE.showLoading("正在保存...");
			var orgs = orgTree.getCheckedNodes(true);
			var orgCode = [];
			
			$.each(orgs||[],function(i,node){
				orgCode.push(node.id);
			});
			$.ajax({
				async : true,
				type : 'post',
				url : "${ctx}/udip/task/roleandorg/saveCaseOrg.json",
				data :{
					caseId : "${caseId}",
					orgCode : orgCode.join(',')
				},
				success : function() {
					BIONE.hideLoading();
					BIONE.tip('保存成功!');
				},
				error : function(result, b) {
					BIONE.hideLoading();
					BIONE.tip('发现系统错误 <BR>错误码：' + result.status);
				}
			});
		}
	});
	
	BIONE.createButton({
		text : '关闭',
		width : '80px',
		appendTo : '#bottom',
		operNo : 'cancelButton',
		icon : 'delete',
		click : function(){
			BIONE.closeDialog("taskCaseOrg");
		}
	});
	
	function selectRelation(newvalue){
		if(newvalue=='0'){
			orgTree.setting.check.chkboxType['Y']='s';
			orgTree.setting.check.chkboxType['N']='s';
		}else{
			orgTree.setting.check.chkboxType['Y']='';
			orgTree.setting.check.chkboxType['N']='';
		}
	}
	
})

</script>
</head>
<body>
	<div id="template.center">
	    <div id="orgtable" width="100%" border="0">
			<div width="100%"
				style="height:30px;background-image: url(${ctx}/css/classics/ligerUI/Gray/images/ui/gridbar.jpg);border-bottom:1px solid #D6D6D6;">
				<div width="8%"
					style="padding-left: 10px; float: left; position: relative; height: 20p; margin-top: 8px">
					<sitemesh:write property='div.template.left.up.icon' />
				</div>
				<div width="30%">
					<span style="font-size: 12; float: left; position: relative; line-height: 30px; padding-left: 2px">
						<span style="font-size: 12">下发机构</span>
					</span>
				</div>
				<div width="60%"> 
					<div style="float: right; position: relative; padding-right: 3px; padding-top:4px;">
						<select name="template_center_combobox" id="template_center_combobox" >
							<option value="0">级联</option>
							<option value="1" selected="selected">不级联</option>
						</select> 
					</div>
				</div>
			</div>
		</div>
		<div id="orgTreeContainer"
			style="overflow: auto; clear: both; background-color: #FFFFFF;">
			<ul id="orgTree"
				style="font-size: 12; background-color: #FFFFFF; width: 96%"
				class="ztree"></ul>
		</div>
	</div>	
	<div id="bottom">
		<sitemesh:write property='div.template.bottom' />
	</div>
</body>
</html>