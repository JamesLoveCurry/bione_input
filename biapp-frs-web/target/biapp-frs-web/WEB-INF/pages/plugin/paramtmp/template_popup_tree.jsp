<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<html>
<head>
<%@ include file="/common/taglibs.jsp"%>
<meta name="decorator" content="/template/template5.jsp">
<style type="text/css">
table{
	font-size : 12px;
}
</style>
<title></title>
<script type="text/javascript">
	var tree=null;
	var url="";
	var id = "${id}";
	var valuelist =[];
	var textlist =[];
	var tree = null;
	var treeData = null;
	var treeInfo = null;
	var orgFlag = false;
	var cascade = 2;
	var url = "";
	var data = {};
	var checkEnable = false;
	var asyncFlag = false;
	var leftTreeObj = null;
	var $p = null;
	var key = "";
	$(function(){
		$p = parent.$.ligerui.get(id);
		$(".popupTreeContainer").height($(document).height() - 30);
		$(".popupTreeContainer").width($(document).width());
		initConfig();
		initLayout();
		initTree();
		initBtn();
		if(tree.checkbox!=null&&tree.checkbox==false){
		}
		else{
			check();
		}
	});
	
	function checkAll(flag){
		leftTreeObj.checkAllNodes(flag);
	}
	
	function checkAllNode(nodes){
		for(var i = 0; i < nodes.length; i++){
			for(var j = 0 ; j < valuelist.length; j++){
				if(valuelist[j] == nodes[i].id){
					 nodes[i].checked = true;
				}
			}
		}
	}
	function initTree(){
		var setting = {
			data : {
				key : {
					name : "text"
				},
				simpleData : {
					enable : true,
					idKey : "id",
					pIdKey : "upId"
				}
			},
			view : {
				selectedMulti : true
			},
			check : {
				chkboxType : {"Y":"s", "N":"s"},
				chkStyle : 'checkbox',
				enable : checkEnable
			},
			callback : {
				onCheck : zTreeOnCheck
			}
		}
		if(asyncFlag){
			setting.async = {
				enable : true,
				url : url,
				autoParam : [ "id" ],
				otherParam : data,
				dataType : "json",
				type : "post",
				dataFilter : function(treeId, parentNode, childNodes) {
					checkAllNode(childNodes);
					return childNodes;
				}
			};
		}
		leftTreeObj = $.fn.zTree.init($("#popupTree"), setting);
		if(!asyncFlag){
			BIONE.loadTree(url,leftTreeObj,data,function(node){
				checkAllNode(node);
				return node;
			},false);
		}
	}
	
	function zTreeOnCheck(event,treeId, item){
		var checked = item.getCheckStatus().checked;
		if(orgFlag){
			if(cascade==1){
				if (checked) {
					var node={};
					node.id=item.id;
					node.text=item.text;
					if(getIndex(node,valuelist)==-1)
						checkInfo(node,true);
					checkCascadeCheck(node.id,true);
				}
				else{
					var node={};
					node.id=item.id;
					node.text=item.text;
					var index=getIndex(node,valuelist);
					if(index>=0){
						checkInfo(node,false);
					}
					unCheckCascadeCheck(node.id,true);
				}
			}
			else if(cascade==2){
				if (checked) {
					var node={};
					node.id=item.id;
					node.text=item.text;
					if(getIndex(node,valuelist)==-1){
						checkInfo(node,true);
					}
				}
				else{
					var node={};
					node.id=item.id;
					node.text=item.text;
					var index=getIndex(node,valuelist);
					if(index>=0){
						checkInfo(node,false);
					}
				}
			}
		}
	}
	
	function initConfig(){
		var $ele = $p.valueField;
		tree = $ele.attr("tree"), options = $ele.attr("options");
		var key = $ele.attr("key");
		if(!key)
			key = "";
		if(key.split("|").length == 3){
			key = key.split("|")[2];
		}
		if($p.getValue()!="")
			valuelist = $p.getValue().split(";");
		if($p.getText()!="")
			textlist = $p.getText().split(";");
		var puuid = $ele.attr("puuid"), parentValue = '', parentText = '';
		tree = JSON2.parse(tree.replace(/'/g, "\""));
		if (puuid) {
			parent.$('input[uuid=' + puuid + ']').each(function () {
				var $this = $(this), ligeruiID = $this.attr('ligeruiid') || $this.attr('data-ligerid');
				var lp = parent.$.ligerui.get(ligeruiID);
				if (lp && lp.getValue) {
					parentValue = lp.getValue();
				}
				if (lp && lp.getText) {
					parentText = lp.getText();
				}
			});
		}
		//针对JSON2不支持单引号问题，需要转义，并保护sql语句中的单引号
		if (options.indexOf("'sql':") > 0) {
			var sql = options.substring(options.indexOf("'sql':") + 7, options.length - 2);
			if(sql.indexOf("','format'") > 0) sql = sql.substring(0, sql.indexOf("','format'"));
			if(sql.indexOf("','db'") > 0) sql = sql.substring(0, sql.indexOf("','db'"));
			options = options.replace(sql, "");
			options = JSON2.parse(options.replace(/'/g, "\""));
			url = "${ctx}/report/frame/param/commonComboBox/executePopupSQL.json";
			options['sql'] = sql;
			options['url'] = "/report/frame/param/commonComboBox/executePopupSQL.json";//edit by fangjuan 2014-07-23 
			options['parms'] = {
                sql : options['sql'],
                db : options['db'],
                value : parentValue,
                text : parentText
            };
			data = {
            	sql : options['sql'],
            	db : options['db'],
            	value : parentValue,
            	text : parentText
            };
		} else {
			
			options = JSON2.parse(options.replace(/'/g, "\""));
			if(typeof(options) == "string"){
				options = JSON2.parse(options);
			}
			url = "${ctx}/"+options['url'];
			if(options['url'] == "/report/frame/datashow/idx/orgTree"){
				key = "ORG";
				window.key = "ORG";
			}
			//key = options[url].indexOf("dimTypeNo");
			data = {
				value : parentValue,
				text : parentText
			};
			if(options.busiType){
				data.orgType = options.busiType;
			}
			options['parms'] = {
				value : parentValue,
            	text : parentText
            };
		}
		if(key=="ORG"){
			asyncFlag = true;
			data.srcCode = "02";
    		if(window.parent.srcCode){
    			data.orgType = window.parent.srcCode;
    		}
    	}
		$.extend(tree, options);
	}
	
	function initLayout(){
		if(tree.checkbox!=null&&tree.checkbox==false){
			checkEnable = false;
            $("#popupTreeContainer").height($(document).height()-$("#treeSearchbar").height()-65);
		}
        else{
        	checkEnable = true;
        	$("#searchInfo").css("float","left");
        	$("#popupTreeContainer").height($(document).height()-$("#treeSearchbar").height()-90);
        	var radio1 = '<div style="width:99%;float:left"><div >是否级联： 是 <input type="radio" id="level1"  name="level" value="level1" onclick="check()" checked="true"/> 否 <input type="radio" id="level2" name="level" value="level2" onclick="check()"  /></div></div>';
        	$("#center").prepend(radio1);
        	if(($p.options.attr.key=="ORG") || (key && $p.options.attr.key.split("|").length==3&&$p.options.attr.key.split("|")[2] =="ORG")||window.key == "ORG"){
        		orgFlag = true;
        		var orggrp = '<div style="margin-top: 8px; width:80px;float:left"><span>机构组选择：</span></div><div style="margin: 5px; margin-left: 80px;padding-left: 5px;"><input id="orggrp"></input></div>';
        		$("#center").prepend(orggrp);
	        	initCombo();
	        	initTreeInfo();
	        	$("#tree").height($(document).height()-$("#treeSearchbar").height()-210);
	        	$("#popupTreeContainer").height($("#popupTreeContainer").height() -40);
        	}
        	cascade = 1;
        }
		$(".form-bar-inner first").css("width", "10%").css("margin", "auto");
		$("#treeSearchIcon").bind("click",function(e){
			var searchName = $("#treeSearchInput").val();
			if(searchName!= ""){
				data.searchName = searchName;
				BIONE.loadTree(url,leftTreeObj,data,function(node){
					checkAllNode(node);
					return node;
				},false);
			} 
			else{
				leftTreeObj.reAsyncChildNodes(null, "refresh");
			}
		});
		$('#treeSearchInput').bind('keydown', function(event) {
			if(event.keyCode==13){
				var searchName = $("#treeSearchInput").val();
				if(searchName!= ""){
					data.searchName = searchName;
					BIONE.loadTree(url,leftTreeObj,data,function(node){
						checkAllNode(node);
					},false);
				} 
				else{
					leftTreeObj.reAsyncChildNodes(null, "refresh");
				}
			}
		});
		$("#tree").focus();
		$("#treeSearchInput").focus();
	}
	
	function initBtn(){
		//添加按钮
		var btns = [ {
			text : "取消",
			onclick : function() {
				//if (puuid) {
					try{
						parent.window['popuptree'][id].close();
					}
					catch(e){
						BIONE.closeDialog("orgWin");
					}
			}
		}, {
			text : "选择",
			onclick : function() {
				if(orgFlag){
					$p.setValue(valuelist.join(';'));
                	$p.setText(textlist.join(';'));
				}
				else{
					if (checkEnable) {
						var nodes = leftTreeObj.getCheckedNodes(true), value = [], text = [];
	                	$(nodes).each(function (i, node)
	                	{
	                    	value.push(node.id);
	                    	text.push(node.text);
	                	});
	                	$p.setValue(value.join(';'));
	                	$p.setText(text.join(';'));
					} else {
						var node = leftTreeObj.getSelectedNodes();
						if(node.length>0){
							$p.setValue(node[0].id);
		                	$p.setText(node[0].text);
						}
						else{
							BIONE.tip("请选择一个节点");
						}
					}
				}
				
					try{
						parent.window['popuptree'][id].close();
					}
					catch(e){
						BIONE.closeDialog("orgWin");
					}
			}
		} ];
		BIONE.addFormButtons(btns);
	}
	
	function checkNode(id,flag){
		var item = {
			id : id,
			text : treeData[id]
		};
		checkInfo(item,flag);		
	}
	
	function checkInfo(item,flag){
		if(flag){
			var i=$.inArray(item.id,valuelist);
			if(i<0){
				valuelist.push(item.id);
				textlist.push(item.text);
			}
		}
		else{
			var i=$.inArray(item.id,valuelist);
			if(i>=0){
				valuelist.splice(i, 1);	
				textlist.splice(i, 1);	
			}
		}
	}
	
	function check() {
		if(orgFlag){
			if ($("#level1")[0].checked == true){
				cascade = 1;
				leftTreeObj.setting.check.chkboxType = {
						"Y" : "s",
						"N" : "s"
				};
			}
			else{
				cascade = 2;
				leftTreeObj.setting.check.chkboxType = {
					"Y" : "",
					"N" : ""
				};
			}
		}
		else{
			if ($("#level1")[0].checked == true){
				leftTreeObj.setting.check.chkboxType = {
						"Y" : "s",
						"N" : "s"
				};
			}
			else{
				leftTreeObj.setting.check.chkboxType = {
					"Y" : "",
					"N" : ""
				};
			}
		}
	}
	
	function initCombo(){
		 $("#orggrp")
			.ligerComboBox(
					{
						url : "${ctx}/rpt/frame/rptorggrp/getorgGrpCombo?d="
								+ new Date().getTime(),
						ajaxType : "post",
						labelAligh : "center",
						slide : false,
						onSelected : function(val) {
							var data = {
									grpId : val
							};
							if(val != null && val != ""){
								$.ajax({
									cache : false,
									async : true,
									url : "${ctx}/rpt/frame/rptorggrp/getGrpOrgNos",
									type : 'POST',
									data : data,
									success : function (data){
										checkAll(false);
										for(var i in data){
											checkNode(data[i],true);
										}
									},
									error : function (result,b){
									
									}
								});
							}
						}
					});
	}
		
		function initTreeInfo(){
			var data = {};
			$.ajax({
				cache : false,
				// no async
				async : true,
				url :  "${ctx}/rpt/frame/rptorg/getAllOrgTree?orgType=01&d="+ new Date().getTime(),
				data : data,
				dataType : "json",
				type : "POST",
				success : function(result) {
					treeInfo = result.treeInfo;
					treeData = result.treeData;
				},
				error : function(result, b) {
				}
			});
		}
		
		function checkCascadeCheck(id,flag){
			var nodes=treeInfo[id];
			if(nodes!=null&&nodes.length>0){
				for(var i in nodes){
					var node={};
					node.id=nodes[i].id;
					node.text=nodes[i].text;
					if(getIndex(node,valuelist)==-1){
						checkNode(node.id,true);
					}
					if(flag)
						checkCascadeCheck(node.id,true);	
				}
			}
		}

		function unCheckCascadeCheck(id,flag){
			var nodes=treeInfo[id];
			if(nodes!=null&&nodes.length>0){
				for(var i in nodes){
					var node={};
					node.id=nodes[i].id;
					node.text=nodes[i].text;
					var index=getIndex(node,valuelist);
					if(index>=0){
						checkNode(node.id,false);
					}
					if(flag)
						unCheckCascadeCheck(node.id,true);
				}
			}
		}
		
		function getIndex(info,array){
			for(var i in array){
				if(array[i]==info.id){
					return i;
				}
			}
			return -1;
		}
	</script>
</head>
<body>
		<div id="template.center">
			<div id="treeSearchbar" style="width:98%;margin-top:2px;padding-left:2px;">
				<div id="searchInfo" class="l-text-wrapper" style="width: 98%;">                         
								<div class="l-text l-text-date" style="width: 98%;">                    
									<input id="treeSearchInput" type="text" class="l-text-field"  style="width: 98%;" />    
									<div class="l-trigger">                                                                      
										<div id="treeSearchIcon" style="width:100%;height:100%;background:url(${ctx}/images/classics/icons/find.png) no-repeat 50% 50% transparent;"></div>                                                 
									</div>
								</div>                                                                                                   
							</div>                                                                                                        
				</div>       
			<div id="popupTreeContainer"
					style="width: 99%;  height: 100%;overflow: auto; clear: both; ">
					<ul id="popupTree"
						style="font-size: 12; background-color: F1F1F1; width: 90%"
						class="ztree"></ul>
				</div>
		</div>
	</div>
</body>
</html>