<%@ page language="java" pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<html>
<meta name="decorator" content="/template/template5.jsp">
<head>
<script type="text/javascript">
	var mainform;
	var grid;
	var catalogId,catalogNm,templateId;
	var ROOT_NO = '0';
	var canEdit = window.parent.canEdit;
	//授权资源根节点图标
	var auth_obj_root_icon = "${ctx}/images/classics/icons/house.png";
	var template = "";
	
	$(function() {
		initData();
		initBaseInfo();
		initDataOther();
		jQuery.metadata.setType("attr", "validate");
		BIONE.validate($("#mainform"));
	});
	function initBaseInfo(){
		var groupicon = "${ctx}/images/classics/icons/communication.gif";
		mainform = $("#mainform").ligerForm({
			inputWidth : 80,
			labelWidth : 120,
			space : 50,
			align : "left",
			fields : [  {
				group : "模板信息",
				groupicon : groupicon,
				display : "模板名称",
				name : "templateNm",
				newline : false,
				type : "text",
				width : 215,
				validate : {
					required : true,
					maxlength : 100,
					remote : {
						url : "${ctx}/rpt/input/idxdatainput/testTemplateNm",
						type : "POST",
						data : {
							"catalogId" : catalogId,
							'templateId' :  templateId
						}
					},
					messages : {
						remote : "相同路径下任务已存在"
					}
				},
        	attr:{
        		readOnly: canEdit!="true"
        	}
			}, {
				display : "模板类型",
				name : "templateType",
				newline : false,
				type:canEdit!="true"?"text":"select",
				comboboxName: "templateTypeSelect",
				options : {
					data : [ {
						id : '01',
						text : '指标补录'
					}, {
						id : '02',
						text : '指标计划值补录'
					}]
				},
				width : 215,
				validate : {
					required : true
				},
				attr : {
	        		readOnly: canEdit!="true"
					
				}
			},{
				display:"备注",
				name:"remark",
				newline:true,
				width:"600",
				validate:{
					maxlength:500
				},
				type:canEdit!="true"?"text":"textarea",
				attr : {
					style : "resize: none;",
	        		readOnly: canEdit!="true"
					
				}
			}]
		});
	}
	function initData(){
		window.parent.taskManage.detailManager = window;
		template = window.parent.templateVO.template;
		if(template == null){
			
			var selectedNodes = window.parent.parent.leftTreeObj.getSelectedNodes();
			if(selectedNodes==null||selectedNodes.length==0)
				catalogId = "ROOT";
			else{
				var node= selectedNodes[0];
				var nodeType = node.params.nodeType;
				if(nodeType=="template"){
					if(node.getParentNode()== null)
						catalogId="ROOT";
					else
						catalogId = node.getParentNode().id;
				}
				else if(nodeType=="catalog")
					catalogId = node.id;
			}
		}else{
			//templateId = template.templateId;
			//templateNm = template.templateNm;
			//templateType = template.templateType;
			catalogId = template.catalogId;
		}

		if(template!=null){
			templateId = template.templateId;
			catalogId = template.catalogId;
		}
	}
	
	function initDataOther(){
		if(template!=null){
			mainform.setData({
				remark : template.remark,
				templateNm:template.templateNm,
				templateType:canEdit!="true"?template.templateType=="01"?"指标补录":"指标计划值补录":template.templateType
            });
		}
	}
	
	
	function gatherData(){

		if($("#mainform").valid()){
			window.parent.templateVO.template = {
					templateId:templateId,
					templateNm:$("#mainform input[name='templateNm']").val(),
					templateType:$("#mainform input[name='templateType']").val(),
					remark:$("#remark").val(),
					"catalogId":catalogId
			}
			window.parent.operDetail();
		}else{
			window.parent.isNeedCheck = false;
		}
	}
</script>

<title>指标目录管理</title>
</head>
<body style="width: 80%">
	<div id="template.center">
		<form name="mainform" method="post" id="mainform">
		</form>
	</div>
</body>
</html>