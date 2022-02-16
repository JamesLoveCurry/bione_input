<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<html>
<head>
<meta name="decorator" content="/template/template12.jsp">
<script type="text/javascript">
	var selectTreeObj, treeNode_;
	var orgNo = "${orgNo}";
	var editFlag = "${editFlag}";   //标识添加还是修改，0添加，1修改，确定是否对选择的机构进行校验
	
	$(function() {
		beforeTree();
		initTree();
		selectButton();
	});
	
	function beforeTree() {
		var setting = {
			data : {
				key : {
					name : 'text'
				},
				simpleData : {
					enable : true,
					idKey : "id",
					pIdKey : "upId"
				}
			},
			callback : {
				beforeClick : beforeClick,
				onClick : onClick
			},
			view : {
				selectedMulti : false,
				showLine : true,
				expandSpeed : "fast"
			},
			check : {
				enable : true,
				chkboxType: { "Y": "", "N": "" },  //设置没有父子关联
				chkStyle : "checkbox"
			} 
		};
		selectTreeObj = $.fn.zTree.init($("#tree"), setting);
	}

	function initTree() {
		$.ajax({
			cache : false,
			async : true,     //异步
			type : "post",
			url : "${ctx}/rpt/frame/idx/limit/listOrgTree.json",
			beforeSend : function() {
				BIONE.loading = true;
				BIONE.showLoading("正在加载数据中...");
			},
			complete : function() {
				BIONE.loading = false;
				BIONE.hideLoading();
			},
			success : function(result) {
				if (result) {
					for ( var i in result) {
						if (result[i].params.open) {
							result[i].open = true;
						} else {
							result[i].open = false;
						}
					}
				}
				selectTreeObj.addNodes(null, result, true);
				//selectTreeObj.expandAll(true);   //是否全部展开
				var baseNode = selectTreeObj.getNodes()[0];
				var id = baseNode.tId;
				$("#" + id).find(".chk#" + id + "_check").remove();   //去除“全部”前的选择框   
				//$("#" + id).find("#" + id + "_check").remove();
				showBack();    //编辑时选中已存在的机构
				templateShow();
			},
			error : function(result, b) {
				BIONE.tip('发现系统错误 <BR>错误码：' + result.status);
			}
		});
	}
	
	function showBack(){
		if(selectTreeObj.getNodeByParam("id",orgNo, null)){
    		selectTreeObj.checkNode(selectTreeObj.getNodeByParam("id",orgNo, null), true, true);
		}
	}
	
	//显示树机构对话框外形
	function templateShow() {
		var height = $(document).height();
		$("#center").height(height - 50);
		$("#content").height(height - 50);
		$("#treeContainer").height(height - 50);
	}
	
	function selectButton() {
		var buttons = [];
		buttons.push({
			text : '选择',
			onclick : selected
		});
		BIONE.addFormButtons(buttons);
	}

	function beforeClick(treeId, treeNode, clickFlag) {
		return false;   //返回 false，zTree 将不会选中节点，也无法触发 onClick 事件回调函数
	}

	function onClick(event, treeId, treeNode, clickFlag) {
		treeNode_ = treeNode;
	}

	//选择
	function selected() {
		// var radioId = $(".radio_true_full").attr("id");
		//var currentNode = selectTreeObj.getNodeByParam("tId", radioId);
		
		var checkNodes = selectTreeObj.getCheckedNodes(true);
		var len = checkNodes.length;
		var tmpNode = null;
		var strText = "";
		var strId = "";
		
		if (len > 0) {
			for(var i=0; i<len; i++){
				tmpNode = checkNodes[i];
				if(i == len-1){  //最后一个，不用在后方加逗号
					strText = strText + tmpNode.text;
					strId = strId + tmpNode.id;
				}else{
					strText = strText + tmpNode.text + ", ";
					strId = strId + tmpNode.id + ",";
				}
			}
		} else {
			BIONE.tip("请选择数据集目录");
			return;
		}
		
		var pop = parent.liger.get('orgNm');
		pop.setText(strText);
		pop.setValue(strId);
		
		if(editFlag == "0"){   //添加，需要校验指标对应的机构是否存在
			validate();
		}else if(editFlag == "1"){   //修改，不需校验
			BIONE.closeDialog("orgNoSelect");
		}
	}
	
	//机构校验
	function validate(){
		$.ajax({
    		type : "post",
    		async : false,   //默认为true，true时发送异步请求
    		url : "${ctx}/rpt/frame/idx/limit/testOrgNo",
    		data : {
    			"indexNo" : parent.indexNo ,
    			"indexVerId" : parent.indexVerId ,
    			"orgNo" : function(){return parent.liger.get('orgNm').getValue();}
    		},
    		dataType : 'json',
    		success : function(result) {   			
    			if(result == false){
    				BIONE.tip("该指标已有对应的机构，请确认");
					parent.liger.get('orgNm').setText("");
    				parent.liger.get('orgNm').setValue("");
    			} else{
    				BIONE.closeDialog("orgNoSelect");
    			}
    		}
    	}); 
	}
	
	
</script>
</head>
<body>
</body>
</html>