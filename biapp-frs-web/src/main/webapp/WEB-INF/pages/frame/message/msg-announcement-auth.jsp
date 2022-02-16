<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<html>
<meta name="decorator" content="/template/template5.jsp">
<head>
<style type="text/css">
.menu {
	position: absolute;
	width: 100%;
	height: 30px;
	border-bottom: 1px solid #CCC;
	top: 31px;
	z-index: 9999;
	background-color: #F0F0F0;
}

.menu table {
	height: 30px;
	width: 100%;
}

.menu tr {
	vertical-align: middle;
}

.menu-item {
	margin-top: 31px;
	overflow: auto;
}
</style>
<script type="text/javascript">
	var tab;
	var trees = {};
	$(function() {
		$('.menu-item').height($('#center').height() - 62);
		// 创建按钮
		var cancel = function() {
			BIONE.closeDialog('author');
		};
		var ensure = function() {
			var t = null, nodes = [];
			var text = "";
			var returnFlag = true;
			$('.ztree').each(function() {
				var temp = $(this);
				t = $.fn.zTree.getZTreeObj(temp.attr('id'));
				var selNodes = t.getCheckedNodes();
				var selInfoNodes = [];
				for(var i in selNodes){
					if(selNodes[i].id != "0"){
						selInfoNodes.push(selNodes[i]);
					}
				}
				selNodes = selInfoNodes;
				if(selNodes
						&& selNodes.length > 0){
					returnFlag = false;
				}
				
				$.each(t.getCheckedNodes(), function(i, n) {
					if(n.id != "0"){
						nodes.push({
							objId: n.params.id,
							objDefNo: temp.attr('def')
						});
					}
				});
				var selected = [];
				var data = {};
				try {
					selected = $.parseJSON(parent.liger.get('authObj').getValue());
				} catch(e) {
				} finally {
					selected = selected || [];
				}
				var node = null;
				$.each(selected, function(i, n) {
					node = t.getNodesByParam('realId', n.objId);
					if (n.objDefNo == temp.attr('def') && node.length  == 0) {
						nodes.push(n);
					}
				});
			});
			if(returnFlag){
				BIONE.tip("请选择要授权的对象");
				return;
			}
			if(returnFlag === false){
				var p = parent || window;
				if (p.f_setAuth) {
					p.f_setAuth("共" + nodes.length + "个对象", nodes);
				}
				BIONE.closeDialog('author');
			}
		};
		BIONE.addFormButtons([{
			text : '取消',
			onclick : cancel
		}, {
			text : '确定',
			onclick : ensure
		}]);
		var temp = null;
		$('.ztree').each(function() {
			temp = $(this);
			var zt = $.fn.zTree.init(temp, {
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
				view : {
				    selectedMulti : false,
				    showLine : true
				},
				check : {
				    chkStyle : 'checkbox',
				    enable : true,
				    chkboxType : {
				    	"Y" : "",
						"N" : ""
				    }
				},
				async: {
					enable: true,
					url: "${ctx}/bione/admin/authObjUsrRel/getAuthObjTree.json",
					autoParam: ['id'],
					otherParam: {
						objDefNo: temp.attr('def')
					},
					dataFilter: function(id, pNode, cNodes) {
						var selected = [];
						var data = {};
						try {
							selected = $.parseJSON(parent.liger.get('authObj').getValue());
						} catch(e) {
						} finally {
							selected = selected || [];
						}
						var defNo = "";
						if(cNodes != null
								&& cNodes.length > 0){
							defNo = cNodes[0].params.objDefNo;
						}
						$.each(selected, function(i, n) {
							if (n.objDefNo == defNo) {
								data[n.objId] = n;
							}
						});
						$.each(cNodes, function(i, n) {
							if (n.params.cantClick && n.params.cantClick == '1') {
								n.nocheck = true;
							}
							if (data[n.params.id]) {
								n.checked = true;
							}
							n.realId = n.params.id;
						});
						// TODO 过滤已选择节点
						return cNodes;
					}
				}
			},
		    [ {
		    	id : "0",
				text : "授权对象树",
				icon : "${ctx}/images/classics/icons/house.png",
				isParent: true
		    } ]);
			var nodes = zt.getNodes();
			zt.reAsyncChildNodes(nodes[0], "refresh", false);
		});
		
		tab = $('#navtab').ligerTab();
		$('input:checkbox').ligerCheckBox();
		$('#cascade').prev('a').click(function() {
			var cas = liger.get('cascade');
			var chkboxType = {};
			if (cas.getValue() == true) {
				chkboxType = {
					"Y": "s",
					"N": "s"
				}
			} else {
				chkboxType = {
					"Y": "",
					"N": ""
				}
			}
			$('.ztree').each(function() {
				var temp = $(this);
				var tree = $.fn.zTree.getZTreeObj(temp.attr('id'));
				tree.setting.check.chkboxType = chkboxType;
			});
		});
	});
</script>
</head>
<body>
	<div id="template.center">
		<div id="menu" class="menu">
			<table>
				<tr>
					<td width="30" align="right"><input type="checkbox"
						name="cascade" id="cascade" /></td>
					<td align="left"><label for="cascade">级联</label></td>
				</tr>
			</table>
		</div>
		<div id="navtab" class="liger-tab">
			<c:forEach items="${authObjDefs}" var="item">
				<div tabid="${item['objDefNo'] }" title="${item['objDefName']}">
					<div class="menu-item">
						<div id="${item['objDefNo'] }_TREE" def="${item['objDefNo'] }" defNm="${item['objDefName']}"
							class="ztree"></div>
					</div>
				</div>
			</c:forEach>
		</div>
	</div>
</body>
</html>