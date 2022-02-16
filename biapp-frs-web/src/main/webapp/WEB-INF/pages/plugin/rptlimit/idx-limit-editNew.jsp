<%@ page language="java" pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
<head>
<meta name="decorator" content="/template/template5.jsp">
<script type="text/javascript">
 	var parentCata = window.parent;
	var indexNo = parentCata.indexNo;
	var indexVerId = parentCata.indexVerId;
	var indexNm = parentCata.parent.curCatalogName;
	var orgNo = "";
	var orgNm = "";

	jQuery.validator.addMethod("numberReg", function(value, element) {
	    var packageCode = /(^[0]$)|(^[1-9][0-9]*$)/;
	    return this.optional(element) || (packageCode.test(value));
	}, "输入正确格式的正整数");
	
    var groupicon = "${ctx}/images/classics/icons/communication.gif";
    $(function() {
		$("#mainform").ligerForm({
		    fields : [ {
				name : "indexNo",
				type : "hidden"
			},{
				name : "indexVerId",
				type : "hidden"
			},{
				display : "指标名称",
				name : 'indexNm',
				newline : true,
				type : 'text',
				validate : {
			   		required : true
				}
		    },{
				display : '机构名称',
				name : 'orgNm',
				newline : false,
				type : 'popup',
				options: {
					cancelable: false,  //是否显示弹框的关闭小×
					valueFieldID: 'orgNo',
					onButtonClick: function() {
						var orgNo = parent.orgNo;
						BIONE.commonOpenDialog('机构目录', "orgNoSelect", 400, 300, 
							"${ctx}/rpt/frame/idx/limit/orgTree?editFlag=0");
					},
				},
				validate : {
			   		required : true ,
/*   			   		remote : {
	        			url : "${ctx}/rpt/frame/idx/limit/testOrgNo",
						type : "POST",
						data : {
							"indexNo" : indexNo,
							"indexVerId" : indexVerId
						}
	        		},
				    messages : {
						remote : "该指标对应的机构已存在"
				    }  */ 
				}
			},{
				display : '最高界限',
				name : 'upperLimit',
				newline : true,
				type : 'text',
				validate : {
			   		required : false,
			   		numberReg : true
				}
			},{
				display : '最低界限',
				name : 'lowerLimit',
				newline : false,
				type : 'text',
				validate : {
			   		required : false,
			   		numberReg : true
				}
			},{
				display : '警告界限',
				name : 'warningLimit',
				newline : true,
				type : 'text',
				validate : {
			   		required : false,
			   		numberReg : true
				}
			},{
				display : '警告模式',
				name : 'warningMode',
				newline : false,
				type : 'select',
				options : {
				    initValue : '0',
				    data : [ {
						text : '大于',
						id : "0"
				    }, {
						text : '小于',
						id : '1'
				    } ]
				}
		    } ]
		});
		
		$("#mainform input[name=indexNo]").val(indexNo);
		$("#mainform input[name=indexVerId]").val(indexVerId);
		$("#mainform input[name=indexNm]").val(indexNm);
		$("#mainform input[name=indexNm]").attr("readonly", "true");
		jQuery.metadata.setType("attr", "validate");  //表示从表单项的validate属性取得验证规则
		BIONE.validate($("#mainform"));
		
		var buttons = [];
		buttons.push({
		    text : '取消',
		    onclick : function() {
				BIONE.closeDialog("rptIdxLimitAddWin");
		    }
		});
		buttons.push({
		    text : '保存',
		    onclick : f_save
		});

		BIONE.addFormButtons(buttons);
    });
    
    function f_save() {
		BIONE.submitForm($("#mainform"), function() {
		    BIONE.closeDialogAndReloadParent("rptIdxLimitAddWin", "maingrid", "添加成功");
		}, function() {
		    BIONE.closeDialog("rptIdxLimitAddWin", "添加失败");
		});
     } 
    
/*   function f_save() {
    	$.ajax({
    		type : "post",
    		url : "${ctx}/rpt/frame/idx/limit/testOrgNo" ,
    		data : {
    			"indexNo" : indexNo ,
    			"indexVerId" : indexVerId ,
    			"orgNo" : function(){return liger.get('orgNm').getValue();}
    		},
    		dataType : 'json',
    		success : function(result) {
    			if (result.msg) {
    				if(result.msg=='0'){
    					BIONE.submitForm($("#mainform"), function() {
        				    BIONE.closeDialogAndReloadParent("rptIdxLimitAddWin", "maingrid", "添加成功");
        				}, function() {
        				    BIONE.closeDialog("rptIdxLimitAddWin", "添加失败");
        				});
    				}else if(result.msg=='1'){
    					BIONE.tip("添加失败，该指标对应的机构已存在");
    				}
    			} else {
    				BIONE.tip("添加失败");
    			}
    		}
    	}); 
    } */ 

</script>
</head>
<body>
	<div id="template.center">
		<form name="mainform" method="post" id="mainform"
			action="${ctx}/rpt/frame/idx/limit/add"></form>
	</div>
</body>
</html>