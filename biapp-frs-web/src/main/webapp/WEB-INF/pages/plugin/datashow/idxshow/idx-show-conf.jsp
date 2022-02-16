<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<html>
<meta name="decorator" content="/template/template5.jsp">
<head>
<style type="text/css">
#container {
	height: 100%;
}
#left {
	float: left;
	width: 40%;
}
#right {
	float: right;
	width: 60%;
}
.frame {
	height: 100%;
}
.exlist {
	border: 1px solid #CCC;
	background-color: white;
	overflow: auto;
	margin-right: 2px;
}
.exlist ul {
	margin: 0 2px;
}
.exlist li {
	padding: 0 2px;
	display: block;
	border-bottom: solid 1px #CCC;
	height: 30px;
	line-height: 30px;
	cursor: pointer;
}
.exlist li:hover {
	background-color: #F0F0F0;
}

.exlist li.check {
	background-color: #DEE4EC;;
}

.exlist .check div.checkbox {
	background-position: 0px -13px;
}

.exlist .checkbox {
	margin: 8px;
	height: 13px;
	float: left;
	width: 13px;
	background: url("${ctx}/images/classics/ligerui/checkbox.gif");
}

.exlist .text {
	float: left;
	height: 30px;
}

.exTree {
	border: 1px solid #CCC;
	background-color: white;
	overflow: hidden;
}

.exTree .exbtn {
	display: block;
	height: 22px;
	float: right;
	line-height: 22px;
	text-align: center;
}

.titlebar {
	height: 30px;
	background-color: #F1F1F1;
	line-height: 30px;
	padding-left: 5px;
	border-bottom: 1px solid #CCC;
}
</style>
<script type="text/javascript">
var Component = liger.core.Component;
$.fn.extend({
	exList: function(options){
		return new ExList(this, options);
	},
	exTree: function(options) {
		return new ExTree(this, options);
	}
});
function ExList(wrap, options) {
	ExList.base.constructor.call(this, options);
	this.wrap = wrap;
	this.data = {};
	this.checked = {};
	this._render();
	this._init();
}
ExList.ligerExtend(Component, {
	_init: function() {
		var base = this;
		$.each(this.options, function(i, n) {
			base.set(i, n);
			if ('list' == i) {
				base.add(n);
			}
		});
	},
	_render: function() {
		var wrap = this.wrap;
		var div = $('<div/>').addClass('exlist').width(this.options.width || 'auto')
			.height(this.options.height || wrap.height() - 4).append('<ul/>');
		wrap.append(div);
	},
	add: function(list) {
		var base = this;
		var wrap = this.wrap;
		if ($.isArray(list)) {
			$.each(list, function(i, n) {
				base.add(n);
			});
			return;
		}
		var id = liger.getId('li');
		var l = $.extend({
			text: '列表项',
			__id: id,
			isChecked: false
		}, list);
		l.__dom = $('<li/>');
		
		var checkbox = $('<div class="checkbox"></div>');
		l.__dom.append(checkbox);
		var text = $('<div class="text">'+l.text+'</div>').width(this.wrap.width() - 41);
		l.__dom.append(text);
		this.wrap.find('ul').append(l.__dom);
		this.data[id] = l;
		checkbox.bind('click', l, function(event) {
			if (event.data.isChecked == false) {
				base.check(event.data);
			} else {
				base.discheck(event.data);
			}
		});
		text.bind('click', l, function(event) {
			var flag = true;
			if (event.data.isChecked == false) {
				flag = false;
			}
			$.each(base.data, function(i, n) {
				if (n == event.data) {
					return true;
				}
				base.discheck(n);
			});
			base.check(event.data);
		});
	},
	getItem: function (id) {
		return this.data[id];
	},
	check: function(param) {
		var item = param || {};
		if (typeof param == 'string') {
			item = this.getItem(param);
		}
		if (item.isChecked == true) {
			return;
		} else {
			item.isChecked = true;
		}
		item.__dom.addClass('check');
		this.trigger('check', item);
	},
	discheck: function (param) {
		var item = param || {};
		if (typeof param == 'string') {
			item = this.getItem(param);
		}
		item.__dom.removeClass('check');
		item.isChecked = false;
		this.trigger('discheck', item);
	}
});
function ExTree(wrap, options) {
	ExTree.base.constructor.call(this, options);
	this.wrap = wrap;
	this._init();
	this._render();
}
ExTree.ligerExtend(Component, {
	_render: function() {
		var content = $('<div/>').width(this.options.width || 'auto')
			.height(this.options.height || this.wrap.height() - 4).addClass('exTree');
		$('<div/>').addClass('titlebar').text(this.options.title || '请选择：').appendTo(content);
		var tree = $('<div/>').height(content.height() - 41).css('overflow', 'auto').addClass('ztree').appendTo(content);
		this.wrap.append(content);
		this.tree = $.fn.zTree.init(tree, this.setting);
	},
	_init: function() {
		var self = this;
		this.setting = $.extend(true, {
			treeId: liger.getId('ztree'),
			view: {
				selectedMulti: false
			},
			check: {
				enable: true
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
		}, this.options && this.options.setting ? this.options.setting : {});
	},
	append: function(node) {
		return this.tree.addNodes(null, node);
	},
	remove: function(node) {
		this.tree.removeChildNodes(node);
		this.tree.removeNode(node);
	},
	getChecked: function() {
		var base = this;
		var nodes = this.tree.getCheckedNodes();
		var heap = {};
		var ar = [];
		$.each(nodes, function(i, n) {
			if (n.isParent == false) {
				if (!heap[n.parentTId]) {
					heap[n.parentTId] = [];
				}
				heap[n.parentTId].push(n.data);
			}
		});
		$.each(heap, function(i, n) {
			var node = base.tree.getNodeByTId(i);
			node.data.checkedItem = heap[i];
			ar.push(node.data);
		});
		return ar;
	}
});
$(function(){
	var exTree = $('#right').exTree({
		
	});
	var exList = $('#left').exList({
		onCheck: function(data) {
			var data = data.data;
			var id = liger.getId('node');
			var node = {
				id: id,
				upId: 0,
				text: data.dimTypeNm,
				data: data,
				isParent: true,
				children: []
			};
			$.each(data.dimItem, function(i, n) {
				node.children.push({
					id: liger.getId('node'),
					upId: id,
					text: n.dimItemNm,
					data: n,
					isParent: false
				});
			});
			data.node = exTree.append(node);
		},
		onDischeck: function(data) {
			if (data && data.data && data.data.node) {
				$.each(data.data.node, function(i, n) {
					exTree.remove(n);
				});
			}
		}
	});
	$.ajax({
		url: '${ctx}/report/frame/datashow/idx/conf/dim.json',
		type: 'get',
		data: {
			idxNo: '${idxNo}',
			verId: '${verId}'
		},
		dataType: 'json',
		success: function(data) {
			if (data) {
				var list = [];
				$.each(data, function(i, n) {
					var item = n.dimItem;
					var type = n.dimType;
					type.dimItem = item;
					list.push({
						text: type.dimTypeNm,
						data: type
					});
				});
				$('#loading').hide();
				exList.add(list);
			}
		}
	});
	BIONE.addFormButtons([{
		text : '确定',
		onclick : function(){
			parent.cfgRow.dimInfo = exTree.getChecked();
			BIONE.closeDialog('conf');
		}
	}]);
});
</script>
</head>
<body>
<div id="template.center">
	<div id='loading' class='l-tab-loading' style="display: block;"></div>
	<div id="container" >
		<div id="left" class="frame"></div>
		<div id="right" class="frame"></div>
	</div>
</div>
</body>
</html>