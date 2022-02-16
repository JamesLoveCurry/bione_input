<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<html>
<head>
<%@ include file="/common/taglibs.jsp"%>
<meta name="decorator" content="/template/template5.jsp">
<script type="text/javascript">
	var btns =[]; 
	var currentNode;//当前点击节点
	var orgType = "${orgType}";
	var isOrgReport = '';
	$(function(){
		initTree();
		initBtn();
	});
	
	//初始化数
	function initTree() {
		settingAsync = {
			async : {
				type : 'get',
				enable : true,
				url : "${ctx}/frs/systemmanage/orgmanage/getTree?orgType=${orgType}&t="
						+ new Date().getTime(),
				autoParam : [ "upOrgNo", "orgNo", "orgNm" ],
				dataType : "json",
				dataFilter : function(treeId, parentNode, childNodes) {
					if (childNodes) {
						for ( var i = 0; i < childNodes.length; i++) {
							//如果选择了级联，那样下级节点就自动进行勾选
							if($('#level1')[0].checked && parentNode.checked){
								childNodes[i].checked = true;
							}
							childNodes[i].orgType = childNodes[i].params.orgType;
							childNodes[i].upOrgNo = childNodes[i].params.upOrgNo;
							childNodes[i].orgNm = childNodes[i].params.orgNm;
							childNodes[i].orgNo = childNodes[i].params.orgNo;
						}
					}
					return childNodes;
				}
			},
			data : {
				key : {
					name : "text"
				},
				simpleData:{
					enable:true,
					idKey: "id",
					pIdKey: "upId"
				}
			},
			view : {
				selectedMulti : false
			},
			check : {
			    chkStyle : 'checkbox',
			    enable : true,
			    chkboxType : {
					"Y" : "",
					"N" : ""
			    }
			},
			callback : {
				//onDblClick : zTreeOnDblClick,
				onClick : zTreeOnClick,
				onNodeCreated : function(event, treeId, treeNode) {
					if (treeNode.id == "ROOT" || treeNode.id == "0") {
						leftTreeObj.reAsyncChildNodes(treeNode, "refresh");
					}
				}
			}
		};
			
		settingSync = {
			data : {
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
			view : {
				selectedMulti : false
			},
			check : {
			    chkStyle : 'checkbox',
			    enable : true,
			    chkboxType : {
					"Y" : "",
					"N" : ""
			    }
			},
			callback : {
				//onDblClick : zTreeOnDblClick,
				onClick : zTreeOnClick,
				onNodeCreated : function(event, treeId, treeNode) {
				}
			}
		};
				
		//搜索
		$("#treeSearchIcon").bind("click", function(){
			setTree($("#treeSearchInput").val() != "");
		});
		$("#treeSearchInput").bind("keydown", function(e){
			if(e.keyCode == 13){
				setTree($("#treeSearchInput").val() != "");
			}
		});
		setTree(false);
	}
	
	function setTree(searchFlag){
		if(searchFlag || isOrgReport){
			leftTreeObj = $.fn.zTree.init($("#tree"), settingSync);
			loadTree("${ctx}/frs/systemmanage/orgmanage/searchOrgTree?orgType=${orgType}&t=" + new Date().getTime(), leftTreeObj, {orgNm : $("#treeSearchInput").val(), isOrgReport : isOrgReport});
		}else{
			leftTreeObj = $.fn.zTree.init($("#tree"), settingAsync);
		}
	}
	
	//加载树
	function loadTree(url, treeObj, data){
		 $.ajax({
			cache : false,
			async : true,
			url : url,
			type : "post",
			dataType : "json",
			data : data,
			beforeSend : function(){
			},
			complete : function(){
				BIONE.loading = false;
				BIONE.hideLoading();
			},
			success : function(result){
				var nodes = treeObj.getNodes();
				for(var i=0; i<nodes.length; i++){
					treeObj.removeNode(nodes[i], false);   //移除先前节点
				}
				if(result.length > 0){
					tmpNodes = result;   //用于级联取消选择操作
					treeObj.addNodes(null, result, false);
					treeObj.expandAll(true);
					treeObj.refresh();   //更新显示
				}
			},
			error : function(result, b){
			}
		 });
	}

	//树点击事件
	function zTreeOnClick(event, treeId, treeNode) {
		if (currentNode != treeNode) {
			currentNode = treeNode;
		}
	}
	

	//树双击击事件
	function zTreeOnDblClick(event, treeId, treeNode) {
		if(treeNode.params.nodetype == "dept"){
			var $w = window.parent.$window;
			$w.liger.get("defDept")._changeValue(treeNode.id,treeNode.text);
			BIONE.closeDialog("taskOrgWin");
		}
	}

	//初始化按钮
	function initBtn() {
		btns.push({
			text : "取消",
			onclick : function() {
				BIONE.closeDialog("taskOrgWin");
			}
		}, {
			text : "选择",
			onclick : function() {
				var nodes = leftTreeObj.getCheckedNodes(), orgNos = [], orgNms = [];
				if (nodes.length > 0) {
					if( $('input:radio:checked').val() == 'level1'){//如果选择级联获取机构，则再次获取已选机构的下级
						var otyp, oNs = "";
						$(nodes).each(function(i, n) {
							if((n.id != "0") && (n.params.orgNo != "0")){
								oNs = oNs + n.id +",";
								otyp=n.data.id.orgType;
							}
						});
						$.ajax({
							async : false,
							type : "POST",
							url : '${ctx}/frs/integratedquery/rptquery/orgChilds',
							dataType : "json",
							data : {
								oNs : oNs,
								orgType : otyp,
								isOrgReport : isOrgReport
							},
							success : function(result){
								var resorgs = result.orgNos;
								var resmrgs = result.orgNms;
								for(var i=0;i<resorgs.length;i++){
									orgNos.push(resorgs[i]);
									orgNms.push(resmrgs[i]);
								}
								
							},
							error : function(){
								BIONE.tip('下级机构获取失败');
							}
						}); 
						
					}else{
						$(nodes).each(function(i, n) {
							if(n.id != "0"){
								orgNos.push(n.id);
								orgNms.push(n.text);
							}
						});
					}
				}
				var c = window.parent.jQuery.ligerui.get("orgNm_sel");
				c._changeValue(orgNos.join(), orgNms.join());
				BIONE.closeDialog("taskOrgWin");
			}
		});
		BIONE.addFormButtons(btns);
	}
	function check(){ 
		if(!$('#level1')[0].checked){
			leftTreeObj.setting.check.chkboxType = {
					"Y" : "",
					"N" : ""
			    }
		}else{
			leftTreeObj.setting.check.chkboxType = {
				"Y" : "s",
				"N" : "s"
		    }
		}
	}
	
	function checkOrg(){
		var submitOrg1 = $("#submitOrg1").prop("checked");//判断选中状态
		var submitOrg2 = $("#submitOrg2").prop("checked");
		if(submitOrg1 && !submitOrg2){//是
			isOrgReport = 'Y';
		}else if(!submitOrg1 && submitOrg2){//否
			isOrgReport = 'N';
		}else{
			isOrgReport = '';
		}
		settingAsync.async.url = "${ctx}/frs/systemmanage/orgmanage/getTree?orgType=${orgType}&t=" + new Date().getTime() + "&isOrgReport=" + isOrgReport;
		setTree($("#treeSearchInput").val() != "");
		check();
	}
</script>
</head>
<body>
	<div id="template.center">
		<div class="content" style="border: 1px solid #C6C6C6; height: 98%">
			<div id="treeToolbar" style="border-bottom-width: 1px; border-bottom-style: solid; border-bottom-color: #D6D6D6; height: 24px; background: #F1F1F1">
				<div id="level" style="line-height: 24px; height: 24px; margin-left: 5px; float: left;">
					<span style="display: block; float: left;">是否级联： 是</span>
					<input type="radio" id="level1" name="level" value="level1" style="display: block; float: left; margin: 5px;" onclick=check() />
					<span style="display: block; float: left;">否 	</span>
					<input type="radio" id="level2" name="level" value="level2" style="display: block; float: left; margin: 5px;" onclick=check() checked="true" />
				</div>
				<div id="isSubmitOrg" style="line-height: 24px; height: 24px; margin-left: 50px; float: left;">
					<span style="display: block; float: left;">是否报送机构： 是</span>
					<input type="checkbox" id="submitOrg1" value="y" style="display: block; float: left; margin: 5px;" onclick=checkOrg() />
					<span style="display: block; float: left;">否 	</span>
					<input type="checkbox" id="submitOrg2" value="n" style="display: block; float: left; margin: 5px;" onclick=checkOrg() />
				</div>
			</div>
			<div id="treeSearchbar" style="width:99%; height:20px; margin-top:2px; padding-left:2px;">
				<ul>
					<li style="width:100%;text-align:left;">                      
						<div class="l-text-wrapper" style="width: 100%;">                         
							<div class="l-text l-text-date" style="width: 100%;">                    
								<input id="treeSearchInput" type="text" class="l-text-field"  style="width: 100%;right: 0px;padding-left: 0px;" />    
									<div class="l-trigger">                                                                      
										<a id="treeSearchIcon" class = "icon-search search-size" ></a>                                                   
									</div>
							</div>                                                                                                   
						</div>
					</li>
				</ul>                                                                                                         
			</div>
			<div id="treeContainer" style="width: 100%; height: 88%; overflow: auto; clear: both;">
				<ul id="tree" style="font-size: 12; background-color: F1F1F1; width: 90%" class="ztree"></ul>
			</div>
		</div>
	</div>
</body>
</html>