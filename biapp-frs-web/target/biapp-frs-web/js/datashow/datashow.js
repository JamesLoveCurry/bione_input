(function($){
	// 基础类
	function Base(options) {
		this.options = options;
		this.events = {};
		this.build();
	}
	$.extend(Base.prototype, {
		// 初始化
		build: function() {
			var options = this.options,
			target = this;
			if (options) {
				var pn = "",
				name = "";
				$.each(options, function(k, v) {
					if (k.indexOf('on') == 0) {
						name = k.substr(2);
						pn = name.substr(0, 1).toLowerCase() + name.substr(1);
						target.bind(pn, v);
					}
				});
			}
		},
		// 绑定触发器
		bind: function(arg, handler) {
			if (typeof arg == "string") {
				var cal = this.events[arg];
				if (!cal) {
					cal = $.Callbacks();
					this.events[arg] = cal;
				}
				cal.add(handler);
			}
		},
		// 触发
		trigger: function(arg, data) {
			var cal = this.events[arg];
			if (cal) {
				cal.fire(data);
			}
		},
		// 解除绑定
		unbind: function(arg, handler) {
			var cal = this.events[arg];
			if (cal) {
				if (handler) {
					cal.remove(handler);
				} else {
					cal.empty();
				}
			}
		}
	});
	
	// 带TAB的扩展树
	function ExTab(options) {
		Base.call(this, options);
		this.init();
	}
	$.extend(ExTab.prototype, Base.prototype, {
		// 初始化
		init: function() {
			// 初始化参数
			var init = {
				tree: {},
				tabId: 'treeToolbar',
				treeId: 'tree',
				inputId: 'treeSearchInput',
				btnId: 'treeSearchIcon',
				trees: [],
				searchParam: 'param',
				delay: false
			};
			
			var opt = this.options = $.extend({}, init, this.options);
			var trees = this.options.trees;
			var tab = {};
			$.each(trees, function(i, n) {
				// 添加TAB
				tab = $('<div/>').attr({
					tabid: 'tree_tab_' + i,
					title: n.tabName
				}).css('height', '0px');
				$('#' + opt.tabId).append(tab);
			});
			var target = this;
			this.tab = $('#' + opt.tabId).ligerTab({
				onAfterSelectTabItem: function(targettabid) {
					var index = Number(targettabid.substring('tree_tab_'.length));
					if (index != NaN) {
						var setting = trees[index];
						target.buildTree(setting);
						target.loadTree();
						target.bind('clickTab');
					}
				}
			});
			if (trees && trees.length > 0) {
				this.buildTree(trees[0]);
				if (this.options.delay == false) {
					this.loadTree();
				}
			}
			
			// 绑定事件
			$('#' + opt.btnId).click(function() {
				target.searchTree();
			});
			$('#' + opt.inputId).bind('keydown', function(event) {
				if (event.keyCode == 13) {
					target.searchTree();
		 		}
			});
			this.trigger('init');
		},
		isAsync: function() {
			var isAsync = false;
			try {
				isAsync = this.tree.setting.async.enable;
			} catch (e) {
			}
			return isAsync;
		},
		filter: function() {
			var data = arguments[0];
			var setting = this.tree.setting;
			if ($.type(setting.filter) == 'function') {
				return setting.filter(data);
			}
			return data;
		},
		// 创建树
		buildTree: function(setting, target) {
			target = target || this;
			var roots = [];
			if (setting.root) {
				roots.push($.extend({
					id: '0',
					text: 'root',
					open: true,
					icon: ''
				}, setting.root));
			} else {
				roots = null;
			}
//			var isAsync = false;
//			try {
//				isAsync = this.tree.setting.async.enable;
//			} catch (e) {
//			}
//			if (isAsync == true) {
//				setting = $.extend(true, {
//					async: {
//						dataFilter: target.filter
//					},
//					callback: {
//						onAsyncSuccess: function() {
//							target.trigger('load');
//						}
//					}
//				}, setting);
//			}
			target.tree = $.fn.zTree.init($('#' + target.options.treeId), setting, roots);
		},
		// 搜索树
		searchTree: function(param) {
			var tree = this.tree;
			param = param || $('#' + this.options.inputId).val();
			this.loadTree(param);
		},
		// 加载数据
		loadTree: function(param) {
			var tree = this.tree;
			var target = this;
			var isAsync = false;
			try {
				isAsync = tree.setting.async.enable;
				tree.setting.async.otherParam.searchNm = param || '';
			} catch(e) {
			}
			var data = {};
			try {
				var paramNm = this.tree.setting.searchParam || "param";
				data[paramNm] = param;
			} catch(e) {
				data['param'] = param;
			}
			target.trigger('beforeLoad');
			if (isAsync == true) {
				target.buildTree(tree.setting);
			} else {
				$.ajax({
					url: tree.setting.url,
					type: 'post',
					data: data,
					dataType: 'json',
					success: function(data) {
						target.trigger('load');
						data = target.filter(data);
						var root = null;
						if (tree.setting.root) {
							root = tree.getNodeByParam('id', setting.root.id);
							tree.removeChildNodes(root);
						} else {
							target.buildTree(tree.setting);
						}
						if (data) {
							tree.addNodes(root, data);
						}
					}
				});
			}
		}
	});
	
	// 标签组件
	function ExLabel(target, options) {
		Base.call(this, options);
		this.target = target;
		this.data = {};
	}
	$.extend(ExLabel.prototype, Base.prototype, {
		getId: function() {
			return liger.getId('Label');
		},
		clear: function() {
			this.target.empty();
			this.data = {};
		},
		addBefore: function(label, param) {
			this.addLabel(param, label, true);
		},
		addAfter: function(label, param) {
			this.addLabel(param, label, false);
		},
		addLabel: function(param, target, isBefore) {
			if (!param) {
				return;
			}
			var data = $.extend({
				lId: this.getId()
			}, param);
			if (this.trigger('beforeAdd', data) == false) {
				return;
			}
			if (param instanceof Array == true) {
				for (var i = 0; i < param.length; i++) {
					this.addLabel(param[i]);
				}
				return;
			}
			var label = {
				content: $('<div class="label-item"/>').attr('lId', data.lId),
				text: $('<div class="text"/>')
			};
			label.text.text(data.text);
			label.content.append(label.text);
			if (param.showClose && param.showClose == true) {
				var item = this;
				label.acton = $('<div class="icon"/>').appendTo(label.content);
				label.acton.bind('click', data, function(event) {
					item.closeLabel(event.data.lId);
				});
			}
			data.label = label;
			this.data[data.lId] = data;
			if (target) {
				if (true == isBefore) {
					target.label.content.before(label.content);
				} else {
					target.label.content.after(label.content);
				}
			} else {
				label.content.appendTo(this.target);
			}
			this.trigger('afterAdd', data);
			return data;
		},
		getLabel: function() {
			var data = this.data;
			var result = [];
			var args = arguments;
			$.each(data, function(i, n) {
				if (args.length == 0 || (args.length == 1 && n.lId == args[0]) || (args.length == 2 && n[args[0]] && n[args[0]] == args[1])) {
					result.push(n);
				}
			});
			return result;
		},
		closeLabel: function(lId) {
			var label = this.data[lId];
			if (this.trigger('beforeClose', label) == false) {
				return;
			}
			
			if(this.options && this.options.beforeCloseLabel && this.options.beforeCloseLabel(label) == false)
				return false;
			
			if (label && label.label) {
				label.label.content.remove();
			}
			this.trigger('afterClose', label);
			delete this.data[lId];
		},
		removeLabel: function(lId) {
			var label = this.data[lId];
			if (label && label.label) {
				label.label.content.remove();
			}
			delete this.data[lId];
		},
		removeAllLable : function(){
			var base = this;
			$.each(this.data, function(i, n) {
				base.removeLabel(i);
			});
		}
	});
	
	// jQuery扩展
	$.extend({
		exTab: function(options) {
			return new ExTab(options);
		}
	});
	$.fn.extend({
		exLabel: function(options) {
			return new ExLabel(this, options);
		}
	});
})(jQuery);