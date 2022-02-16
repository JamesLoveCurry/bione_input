<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<html>
<meta name="decorator" content="/template/template17.jsp">
<head>
<style type="text/css">
.ztree li span.button.switch.level0 {
	visibility: hidden;
	width: 1px;
}

.ztree li ul.level0 {
	padding: 0;
	background: none;
}

#left {
	width: 49%;
}

#right {
	width: 50%;
}

.ztree li span.button.add {
	background-position: -144px 0;
	margin-left: 2px;
	margin-right: -1px;
	vertical-align: top;
}

.display-child {
	display: none;
}

.diy-btn {
	margin: 5px;
	padding: 0 20px;
	height: 30px;
	line-height: 30px;
	border: 1px solid #CCC;
	background-color: #FFF;
	text-align: center;
	float: left;
	cursor: pointer;
	border-radius: 2px;
	box-shadow: 0 1px 1px rgba(0,0,0,0.15);
}
</style>
<script type="text/javascript">
var node;
function Tree(url, setting, rootName) {
	this.url = url;
	this.rootName = rootName;
	this.setting = setting;
	this.tree = null;
}
$.extend(Tree.prototype, {
	build: function() {
		this.tree = $.fn.zTree.init($('#tree'), this.setting, [{
			id: '0',
			text: this.rootName,
			params : {
				type : "catalog"
			},
			open: true
		}]);
		this.load();
		return this;
	},
	load: function() {
		var tree = this.tree;
		try {
			if (!tree) {
				throw new Error('树对象不存在，请尝试重新加载页面');
				return;
			}
		} catch(e) {
			BIONE.tip(e.message);
		}
		$('#tree_loading').css('display', 'block');
		$.ajax({
			url: this.url,
			type: 'post',
			dataType: 'json',
			success: function(data) {
				$('#tree_loading').css('display', 'none');
				var root = tree.getNodeByParam('id', '0');
				tree.removeChildNodes(root);
				if (data) {
					tree.addNodes(root, data);
				}
			}
		});
	}
});
var Component = liger.core.Component;
function ExForm(wrap, opts) {
	ExForm.base.constructor.call(this, opts);
	this.wrap = wrap;
	this.init();
}
ExForm.ligerExtend(Component, {
	init: function() {
		var t = this, 
		o = t.options,
		w = t.wrap;
		w.empty(),
		id = liger.getId('form');
		this.id = id;
		w.append('<form id="'+ id +'" action="'+ o.url +'" method="post"></form>');
		$('#' + id).ligerForm(o.setting);
		BIONE.validate('#' + id);
	},
	submit: function(success, error) {
		BIONE.submitForm(this.form, success, error);
	},
	clear: function() {
		this.wrap.empty();
	}
});
$.fn.extend({
	exForm: function(opts) {
		return new ExForm(this, opts);
	}
});
$(function() {
	jQuery.metadata.setType("attr", "validate");
	$('#treeContainer').height($('#right').height());
	var setting = {
			view: {
				addHoverDom: addHoverDom,
				removeHoverDom: removeHoverDom,
				dblClickExpand: dblClickExpand,
				selectedMulti: false
			},
			edit: {
				enable: true,
				showRemoveBtn: false,
				showRenameBtn: false
			},
			callback: {
				onClick: function() {
					node = arguments[2];
					if(node.id == "0"){
						BIONE.tip("请选择非根节点！");
						return;
					}
					$('#form-bar-inner').empty();
					if ('report' == node.params.type) {
						$('#display').empty();
						return;
					}
					$('#display').exForm(report);
					$('#folderId').val(node.id);
					$('#remark').css("resize","none");
				}
			},
			data: {
				key: {
					name: 'text'
				},
				simpleData: {
					enable: true,
					idKey: 'id',
					pIdKey: 'upId',
					rootPId: '0'
				}
			}
		};
	var tree = new Tree('${ctx}/report/frame/datashow/idx/store/node.json', setting, '收藏').build();
	var report = {
		url: '${ctx}/report/frame/datashow/rpt/store/ins',
		setting: {
			labelWidth: 50,
			inputWidth: 200,
			fields: [{
				type: 'hidden',
				name: 'folderId'
			},{
				type: 'hidden',
				name: 'instanceId',
				attr: {
					value: '${instanceId}'
				}
			},{
				display: '名称',
				group: '收藏信息',
				groupicon : "${ctx}/images/classics/icons/communication.gif",
				type: 'text',
				name: 'queryNm',
				attr: {
					value: '${defaultName}'
				},
				validate: {
					remote : {
						url : "${ctx}/report/frame/datashow/idx/checkQueryNm?t=" + new Date().getTime(),
						type : 'post'
					},
					messages : {
						remote : "该名称已存在"
					},
					required : true,
					maxlength : 32
				}
			}, {
				display: '备注',
				type: 'textarea',
				name: 'remark',
				validate: {
					maxlength : 100
				}
			}],
			buttons: [{
 				text: '收藏',
 				click: function() {
 					if($('#display form:first').valid()){
 					var win = parent || window;
					var favour = win.favour;
 					if (favour) {
 						favour.ins = {
							queryNm: $('#queryNm').val() || '',
							remark: $('#remark').val() || '',
							busiType : favour.busiType
 						};
 						favour.folder = {
 							folderId: $('#folderId').val() || ''
 						};
 						$.ajax({
 							url: '${ctx}/report/frame/datashow/idx/store/save',
 							type: 'post',
 							data: {
 								query: JSON2.stringify(favour)
 							},
 							dataType: 'json',
 							success: function() {
 								if(window.parent.initStoreTree){
 									window.parent.initStoreTree();
 								}
 								BIONE.tip("保存成功!");
 								BIONE.closeDialog("store");
 							}
 						});
 					}
 				}else{
 					BIONE.showInvalid();
 				}
 				}
 			}]
		}
	};
	var folder = {
 		url: '${ctx}/rpt/rpt/rptplatshow/store/folder',
 		setting: {
 			labelWidth: 50,
 			inputWidth: 200,
 			fields: [{
 				type: 'hidden',
 				name: 'upFolderId',
 				attr: {
 					value: '0'
 				}
 			},{
 				display: '名称',
 				group: '文件夹信息',
 				groupicon : "${ctx}/images/classics/icons/communication.gif",
 				type: 'text',
 				name: 'folderNm',
 				validate: {
 					required : true,
 					maxlength : 32
 				}
 			}],
 			buttons: [{
 				text: '新增',
 				click: function() {
 					BIONE.submitForm($('#display form:first'), function() {
 						tree.load();
 						$('#display').empty();
 					});
 				}
 			}]
 		}
	};
// 	var report = $('#display-report').rightForm({
// 		url: '${ctx}/report/frame/datashow/rpt/store/ins',
// 		setting: {
// 			labelWidth: 50,
// 			fields: [{
// 				type: 'hidden',
// 				name: 'instanceId',
// 				attr: {
// 					value: '${instanceId}'
// 				}
// 			},{
// 				display: '名称',
// 				group: '报表信息',
// 				groupicon : "${ctx}/images/classics/icons/communication.gif",
// 				type: 'text',
// 				name: 'queryNm',
// 				attr: {
// 					value: '${defaultName}',
// 				},
// 				validate: {
// 					required : true,
// 					maxlength : 32
// 				}
// 			}, {
// 				display: '备注',
// 				type: 'textarea',
// 				name: 'remark',
// 				validate: {
// 					maxlength : 100
// 				}
// 			}]
// 		}
// 	});
// 	var folder = $('#display-folder').rightForm({
// 		url: '${ctx}/report/frame/datashow/rpt/store/folder',
// 		setting: {
// 			labelWidth: 50,
// 			fields: [{
// 				type: 'hidden',
// 				name: 'upFolderId',
// 				attr: {
// 					value: '0'
// 				}
// 			},{
// 				display: '名称',
// 				group: '文件夹信息',
// 				groupicon : "${ctx}/images/classics/icons/communication.gif",
// 				type: 'text',
// 				name: 'folderNm',
// 				validate: {
// 					required : true,
// 					maxlength : 32
// 				}
// 			}]
// 		}
// 	});
// 	var btns = [{
// 		text : '添加文件夹',
// 		onclick : function(){
// 			folder.submit(function() {
// 			});
// 		}
// 	},{
// 		text : '保存收藏',
// 		onclick : function(){}
// 	}];
	var newCount = 1;
	function addHoverDom(treeId, treeNode) {
		
		if (!treeNode.params || 'report' == treeNode.params.type) {
			return;
		}
		var sObj = $("#" + treeNode.tId + "_span");
		if (treeNode.editNameFlag || $("#addBtn_"+treeNode.id).length>0) return;
		var addStr = "<span class='button add' id='addBtn_" + treeNode.id
			+ "' title='添加文件夹' onfocus='this.blur();'></span>";
		sObj.after(addStr);
		var btn = $("#addBtn_"+treeNode.id);
		if (btn) btn.bind("click", treeNode, function(event){
			var form = $('#display').exForm(folder);
			$('#form-bar-inner').empty();
// 			BIONE.addFormButtons([btns[0]]);
			$('#upFolderId').val(event.data.id);
			return false;
		});
	};
	function removeHoverDom(treeId, treeNode) {
		$("#addBtn_"+treeNode.id).unbind().remove();
	};
	var dblClickExpand = function(treeId, treeNode) {
		return treeNode.level > 0;
	};
});
</script>
</head>
<body>
<div id='template.left'>
  <div id="treeContainer" style="width: 100%; overflow: auto; clear: both; background-color: #FFFFFF; position: relative; height: 100%;">
  	<div id='tree_loading' class='l-tab-loading' style='display:block;'></div>
    <ul id="tree" style="font-size: 12; background-color: #FFFFFF; width: 92%" class="ztree"></ul>
  </div>
</div>
<div id="template.right">
  <div style="position: relative; height: 100%; overflow: hidden;">
  	<div id="display">
  		<form id="xf" action="" method="post"></form>
  	</div>
  </div>
</div>
</body>
</html>