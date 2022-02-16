<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<html>
<meta name="decorator" content="/template/template5.jsp">
<head>
<script type="text/javascript">
	var groupicon = "${ctx}/images/classics/icons/communication.gif";
	//币种
	var currData = []
	//创建表单结构 
	var mainform;
	$(function() {
		//初始化币种数据
		initCurrData();

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
					initValue:'境内汇总数据',
					data : [ {
						text : '法人',
						id : '法人'
					}, {
						text : '境内',
						id : '境内汇总数据'
					}, {
						text : '并表',
						id : '并表'
					}  ]
				},
				group : "导出配置信息",
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
					initValue: '001',
					initText: '人民币',
					data: currData
				},
				validate : {
					required : true
				}
			}]
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
			//导出配置信息
			var submitScope = $("#submitScope").val();
			var currency = $.ligerui.get("currencyBox").getText();
			var configParams = {
					submitScope : submitScope,
					currency : currency
			};
		
			//报表信息
			var rows = window.parent.manager.getSelectedRows();
			var argsArr = [];
			if(rows.length > 0){
		    	for(var i in rows){
		    		var orgName = rows[i].exeObjNm;
		    	    var rptId = rows[i].taskObjId;
		    	    var orgNo = rows[i].exeObjId;
		    	    var busiLineId = rows[i].lineId;
		    	    var dataDate = rows[i].dataDate;
			    	if(rptId != null && orgNo != null && dataDate != null){
			    	       var argsArr1 = [];
			    	       var args1 = {'DimNo':'ORG','Op':'=','Value':orgNo};
			    	       argsArr1.push(args1);
			    	       var args = {'orgNm':orgName,'rptId':rptId,'dataDate':dataDate,'busiLineId':busiLineId,'searchArgs':JSON2.stringify(argsArr1)};
			    	       argsArr.push(args);
		   	      	}else{
			   	       BIONE.tip("数据异常，请联系系统管理员");
			   	    }
		    	}
		    	if(argsArr.length > 0){
		    		BIONE.ajax({
	   	        	 	async : false,
	    	         	url : "${ctx}/rpt/frs/rptfill/rptdownloadList",
	    	         	dataType : 'json',
	    				data : {
	    					json : JSON2.stringify(argsArr),
    					 	configParams : JSON.stringify(configParams),
    					 	QueryInit : false,
    					 	moduleType : window.parent.moduleType
	    				},
		    	        type : 'post',
		    	        loading : '正在生成下载文件，请稍等...'
		    	    },
		    	    function (result){
		    	    	if(result.result){
		    	    		if("OK" == result.result){
		    	    			if(result.zipFilePath && result.folderinfoPath){
		    	    				 var src = '';
		    	    				 src = "${ctx}/rpt/frs/rptfill/downFile?&zipFilePath="+encodeURI(result.zipFilePath)+"&folderinfoPath=" + encodeURI(result.folderinfoPath) + "&d="+ new Date() + "&taskFillOperType=36&operType="+window.parent.rptOperType+"&taskInsId=" + rows[0].taskInstanceId;
		    	    				 window.parent.download.attr('src', src);
		    	    			}else{
		    	    				BIONE.tip("数据异常，请联系系统管理员");
		    	    			}
		    	    		}else{
		    	    			parent.BIONE.showError(result.msg);
		    	    		}
		    	    		BIONE.closeDialog("1104DownCfg");
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
	
	function initCurrData(){
		$.ajax({
			cache : false,
			async : false,
			url : "${ctx}/rpt/frs/rptexport/getReportCurrency",
			type : "get",
			dataType : "json",
			success : function(result) {
				if(result.currencyList){
					currData = result.currencyList;
				}
			}
		});
	}

	function cancle(){   
		BIONE.closeDialog("1104DownCfg");
	} 
</script>
</head>
<body>
	<div id="template.center">
		<form id="mainform" action="" method="post"></form>
	</div>
</body>
</html>