<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
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
				display : "报送范围",
				name : "submitScope",
				comboboxName: "submitScopeBox",
				newline : true,
				type : "select",
				cssClass : "field",
				options:{
					initValue:'境内',
					data : [ {
						text : '法人',
						id : '法人'
					}, {
						text : '境内',
						id : '境内'
					}, {
						text : '并表',
						id : '并表'
					}  ]
				},
				group : "报文配置信息",
				groupicon : groupicon,
				validate : {
					required : true
				}
			}, {
				display : "币种",
				name : "currency",
				comboboxName: "currencyBox",
				newline : true,
				type : "select",
				cssClass : "field",
				options : {
					initValue:'人民币',
					data : [ {
						text : '人民币',
						id : '人民币'
					} ]
				},
				validate : {
					required : true
				}
			}, {
				display : "数据单位",
				name : "unit",
				comboboxName: "unitBox",
				newline : true,
				type : "select",
				cssClass : "field",
				options : {
					initValue:'万元',
					data : [ {
						text : '万元',
						id : '万元'
					} ]
				},
				validate : {
					required : true
				}
			} ]
		});
		jQuery.metadata.setType("attr", "validate");
		BIONE.validate(mainform);
		var buttons = [];
		buttons.push({
			text : '取消',
			onclick : cancle
		});
		buttons.push({
			text : '导出',
			onclick : downloadXmlFile
		});
		BIONE.addFormButtons(buttons);

	});
	function downloadXmlFile() {
		
		if($('#mainform').valid()){
			//报文配置信息
			var submitScope = $("#submitScope").val();
			var currency = $("#currency").val(); 
			var unit = $("#unit").val();
			var configParams = {
					submitScope : submitScope,
					currency : currency,
					unit : unit
			};
		
			//报表信息
			var rows = window.parent.manager.getSelectedRows();
			var argsArr = [];
			if(rows.length > 0){
				for(var i=0; i<rows.length; i++){
					var rptId = rows[i].taskObjId;
					var rptNm = rows[i].taskObjNm;
					var orgNo = rows[i].exeObjId;
					var orgName = rows[i].exeObjNm;
					var dataDate = rows[i].dataDate;
					var taskInstanceId = rows[i].taskInstanceId;
					var busiLineId = rows[i].lineId;
					configParams["dataDate"] = dataDate;
		    	    if(rptId != null && orgNo != null && dataDate != null){
			    		//var args = {'rptNm':rptNm,'orgNo':orgNo,'rptId':rptId,'dataDate':dataDate, "taskInstanceId":taskInstanceId};
			    		var argsArr1 = [];
		    	       	var args1 = {'DimNo':'ORG','Op':'=','Value':orgNo};
		    	        argsArr1.push(args1);
		    	        var args = {'orgNm':orgName,'rptNm':rptNm,'rptId':rptId,'dataDate':dataDate,'busiLineId':busiLineId,"taskInstanceId":taskInstanceId,'searchArgs':JSON2.stringify(argsArr1)};
		    	        argsArr.push(args);
		   	      	}else{
			   	       BIONE.tip("数据异常，请联系系统管理员");
			   	    }
				}
				if(argsArr.length > 0){
		    		BIONE.ajax({
		    	         async : false,
		    	         url : "${ctx}/rpt/frs/rptexport/downloadXmlFile",
		    	         dataType : 'json',
		    	         type : 'post',
		    	         data : {
		    	        	 rptList : JSON.stringify(argsArr),
		    	        	 configParams : JSON.stringify(configParams)
		    	         },
		    	         loading : '正在生成下载文件，请稍等...'
		    	    },
		    	    function (result){
		    	    	if(result.result){
		    	    		if("OK" == result.result){
		    	    			if(result.filePath){
		    	    				 var src = '';
		    	    				 src = "${ctx}/rpt/frs/rptexport/downFile?&zipFilePath="+encodeURI(result.filePath);
		    	    				 window.parent.download.attr('src', src);
		    	    			}else{
		    	    				BIONE.tip("数据异常，请联系系统管理员");
		    	    			}
		    	    		}else{
		    	    			BIONE.tip(result.msg);
		    	    		}
		    	    		BIONE.closeDialog("createXmlWin");
		    	    	}else{
		    	    		BIONE.tip("数据异常，请联系系统管理员");
		    	    	}
		    	    });
		    	}else{
		    		BIONE.tip("数据异常，请联系系统管理员");
		    	}
			}else{
				BIONE.tip("请选择要导出的报表！");
			}
		}
	} 
	 function cancle(){                     
		BIONE.closeDialog("createXmlWin");
	} 
</script>
</head>
<body>
	<div id="template.center">
		<form id="mainform" action="" method="post"></form>
	</div>
</body>
</html>