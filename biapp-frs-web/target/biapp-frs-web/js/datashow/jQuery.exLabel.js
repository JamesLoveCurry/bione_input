/**
 * create by kanglg on 2015.07.21
 * ver 0.2.2
 */
(function($){
	// 基础类
	function Base(options) {
		this.options = options;
		this.events = {};
		this.build();
	}
	// 数据绑定
	function DataBinder(object_id) {
		var pubSub = jQuery({});
		var data_attr = "bind-" + object_id, message = object_id + ":change";
		$(document).on("change", "[data-" + data_attr + "]", function(evt) {
			var $input = $(this);
			pubSub.trigger(message, [ $input.data(data_attr), $input.val() ]);
		});
		pubSub.on(message, function(evt, prop_name, new_val) {
			$("[data-" + data_attr + "=" + prop_name + "]").each(function() {
				var $bound = jQuery(this);
				if ($bound.is("input, textarea, select")) {
					$bound.val(new_val);
				} else {
					$bound.html(new_val);
				}
			});
		});
		return pubSub;
	}
	
	function getId(prev) {
        prev = prev || getId.managerIdPrev;
        var id = prev + (1000 + getId.managerCount);
        getId.managerCount++;
        return id;
	}
	getId.managerIdPrev = 'id';
	getId.managerCount = 0;
	
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
	// 标签组件
	function LabelItem(options, pad) {
		Base.call(this, options);
		this.pad = pad;
		$.extend(this, options);
		this._init();
		this._render();
	};
	LabelItem.prototype = new Base();
	$.extend(LabelItem.prototype, {
		_init: function() {
			var binder = this._binder = new DataBinder(this.lId);
			binder.on(this.lId + ":change", this, function(evt, attr_name, new_val, initiator) {
				if (!initiator) {
					evt.data.setText(new_val);
				}
			});
		},
		_render: function() {
			var t = this;
			var $dom = $('<div class="label-item"/>').attr({
				'lId': this.lId
			});
			var $text = $('<div class="text"/>');
			$text.text(this.text);
			$text.attr('data-bind-' + this.lId, 'text');
			$dom.append($text);
			this.content = $dom;
			$dom.bind('click', this, function() {
				t.trigger('click', t);
			});
			if (this.css) {
				this.setCss(this.css);
			}
			if (this.showClose && this.showClose == true) {
				$dom.addClass('close');
				var close = $('<div class="icon"></div>').appendTo($dom);
				close.bind('click', this, t.close);
			}
			if (this.isCheck != null) {
				var icon = $('<div class="icon"></div>').appendTo($dom);
				$dom.addClass('check');
				if (this.isCheck == false) {
					$dom.addClass('uncheck');
				}
				$dom.bind('click.check', this, t.check);
				$dom.css('cursor', 'pointer')
			}
		},
		close: function(e) {
			var l = e.data;
			l.trigger('close');
			l.pad.closeLabel(l.lId);
		},
		check: function(e) {
			var l = e.data;
			l.isCheck = !l.isCheck;
			var $dom = l.content;
			if (l.isCheck == true) {
				$dom.removeClass('uncheck');
			} else {
				$dom.addClass('uncheck');
			}
			l.trigger('check', [l.isCheck, l]);
		},
		setText: function(val) {
			this.text = val;
			this._binder.trigger(this.lId + ":change", [ 'text', val, this ] );
		},
		setCss: function(css) {
			this.css = css;
			this.content.css(css);
		}
	});
	function ExLabel(target, options) {
		Base.call(this, options);
		this.target = target;
		this.data = {};
		this._render();
	}
	$.extend(ExLabel.prototype, Base.prototype, {
		_render: function() {
			var tar = this.target;
			tar.addClass('labelPad');
		},
		getId: function() {
			return getId('Label');
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
			var label = new LabelItem(data, this);
			label.events = $.extend({}, this.events, label.events);
//			data.label = label;
			this.data[data.lId] = label;
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
				if (args.length == 0 || (args.length == 1 && n.lId == args[0]) || (args.length == 2 && n[args[0]] != null && n[args[0]] != undefined && n[args[0]] == args[1])) {
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
			if (label) {
				label.content.remove();
			}
			this.trigger('afterClose', label);
			delete this.data[lId];
		},
		removeLabel: function(lId) {
			var label = this.data[lId];
			if (label) {
				label.content.remove();
			}
			delete this.data[lId];
		},
		removeAllLable : function(){
			var base = this;
			$.each(base.data, function(i, n) {
				base.removeLabel(i);
			});
		}
	});
	
	// jQuery扩展
	$.fn.extend({
		exLabel: function(options) {
			return new ExLabel(this, options);
		}
	});
})(jQuery);