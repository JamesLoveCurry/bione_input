﻿/*
默认参数 扩展
 */

$.extend($.ligerDefaults.Grid, {
	checkbox : true,
	fixedCellHeight : false,
	frozen : false,
	async : true,
	allowUnSelectRow : true,
	onError : function(result, b) {
		// BIONE.tip('发现系统错误 ' + b);
	}
});

$.extend($.ligerDefaults.Tab, {
	contextmenu : false
});

/*
 * 表格 扩展
 */

$.extend($.ligerui.controls.Grid.prototype, {
	_initBuildHeader : function() {
		var g = this, p = this.options;
		if (p.title) {
			$(".l-panel-header-text", g.header).html(p.title);
			if (p.headerImg)
				g.header.append("<img src='" + p.headerImg + "' />").addClass(
						"l-panel-header-hasicon");
		} else {
			g.header.hide();
		}
		if (p.toolbar) {
			if ($.fn.ligerToolBar)
				g.toolbarManager = g.topbar.ligerToolBar(p.toolbar);
		} else {
			g.topbar.remove();
		}
	},
	addEditRow : function(rowdata) {
		var g = this;
		rowdata = g.add(rowdata);
		return g.beginEdit(rowdata);
	},
	getEditingRow : function() {
		var g = this;
		for ( var i = 0, l = g.rows.length; i < l; i++) {
			if (g.rows[i]._editing)
				return g.rows[i];
		}
		return null;
	},
	getChangedRows : function() {
		var g = this, changedRows = [];
		pushRows(g.getDeleted(), 'delete');
		pushRows(g.getUpdated(), 'update');
		pushRows(g.getAdded(), 'add');
		return changedRows;

		function pushRows(rows, status) {
			if (!rows || !rows instanceof Array)
				return;
			for ( var i = 0, l = rows.length; i < l; i++) {
				changedRows.push($.extend({}, rows[i], {
					__status : status
				}));
			}
		}

	}
});

/*
 * 表格格式化函数扩展
 */

// 扩展 percent 百分比 类型的格式化函数(0到1之间)
$.ligerDefaults.Grid.formatters['percent'] = function(value, column) {
	if (value < 0)
		value = 0;
	if (value > 1)
		value = 1;
	var precision = column.editor.precision || 0;
	return (value * 100).toFixed(precision) + "%";
};
/**
 * 扩展开关组件
 * modify by kanglg on 2015/7/1
 */
$.ligerDefaults.Grid.formatters['switcher'] = function(value, column, row) {
	var opt, on, off;
	opt = column.options;
	if (opt) {
		on = opt['onTurnOn'];
		off = opt['onTurnOff'];
	}
	if (!this.options.switcherEvent) {
		var switcherEvent = null;
		if ($.browser.msie && parseInt($.browser.version, 10) < 9) {
			switcherEvent = function(e) {
				var grid = e.data;
				var row = e.data.getRow($(this).attr('rowid'));
				if ('on' == $(this).attr('switch')) {
					if (off) {
						$(this).html('关闭');
						$(this).attr('switch', 'off');
						off($(this).attr('rowid'));
					}
				} else if ('off' == $(this).attr('switch')) {
					if (on) {
						$(this).html('启用');
						$(this).attr('switch', 'on');
						on($(this).attr('rowid'));
					}
				}
			};
			$('a.switcher').live('click', this, switcherEvent);
		} else {
			var o = $.extend({
				height: 16.5,
				autoFontSize : true
			}, opt);
			switcherEvent = function() {
				$(".rcswitcher").rcSwitcher(o).on({
					'turnon.rcSwitcher': function(e, dataObj) {
//						var rowId = dataObj.$input.attr("rowId");
						on(row);
					},
				    'turnoff.rcSwitcher': function(e, dataObj) {
//						var rowId = dataObj.$input.attr("rowId");
						off(row);
					}
				});
			};
			this.bind('afterShowData', switcherEvent);
		}
		this.options.switcherEvent = switcherEvent;
	}
//	if (!this.options.switcherEvent && !($.browser.msie && parseInt($.browser.version, 10) < 9)) {
//		var switcherEvent = function() {
//			$(".rcswitcher").rcSwitcher({
//				height:16.5,
//				autoFontSize : true
//			}).on({
//				'turnon.rcSwitcher': on,
//			    'turnoff.rcSwitcher': off
//			});
//		};
//		this.bind('afterShowData', switcherEvent);
//		this.options.switcherEvent = switcherEvent;
//	}
	// 开关判断
	var enable = ["true", "1", true, 1, "Y"];
	var disable = ["false", "0", false , 0, "N"];
	var turnOn = $.inArray(value, enable) != -1;
	var turnOff = $.inArray(value, disable) != -1;
	if ($.browser.msie && parseInt($.browser.version, 10) < 9) {
		if (turnOn) {
			return '<a class="switcher" rowid='+row.__id+' switch="on">启用</a>';
		} else {
			return '<a class="switcher" rowid='+row.__id+' switch="off">关闭</a>';
		}
	}else{
		var html = "<input class='rcswitcher' type='checkbox' name='check"+row.__id+"' rowId='"+row.__id+"' ";
		if(turnOn){
			html += "checked";
		}
		html += " />";
		return html;
	}
};

$.fn.extend({
	switcher: function(value, column) {
		
	}
});

// 扩展 numberbox 类型的格式化函数
$.ligerDefaults.Grid.formatters['numberbox'] = function(value, column) {
	var precision = column.editor.precision || 0;
	return value.toFixed(precision);
};
// 扩展currency类型的格式化函数
$.ligerDefaults.Grid.formatters['currency'] = function(num, column) {
	// num 当前的值
	// column 列信息
	if (!num)
		return "0.00";
	num = num.toString().replace(/\$|\,/g, '');
	if (isNaN(num))
		num = "0.00";
	sign = (num == (num = Math.abs(num)));
	num = Math.floor(num * 100 + 0.50000000001);
	cents = num % 100;
	num = Math.floor(num / 100).toString();
	if (cents < 10)
		cents = "0" + cents;
	for ( var i = 0; i < Math.floor((num.length - (1 + i)) / 3); i++)
		num = num.substring(0, num.length - (4 * i + 3)) + ','
				+ num.substring(num.length - (4 * i + 3));
	return "" + (((sign) ? '' : '-') + '' + num + '.' + cents);
};

/*
 * 表格编辑器
 */

// 扩展一个 百分比输入框 的编辑器(0到1之间)
$.ligerDefaults.Grid.editors['percent'] = {
	create : function(container, editParm) {
		var column = editParm.column;
		var precision = column.editor.precision || 0;
		var input = $("<input type='text' style='text-align:right' class='l-text' />");
		input.bind('keypress', function(e) {
			var keyCode = window.event ? e.keyCode : e.which;
			return keyCode >= 48 && keyCode <= 57 || keyCode == 46
					|| keyCode == 8;
		});
		input.bind('blur', function() {
			var showVal = input.val();
			showVal.replace('%', '');
			input.val(parseFloat(showVal).toFixed(precision));
		});
		container.append(input);
		return input;
	},
	getValue : function(input, editParm) {
		var showVal = input.val();
		showVal.replace('%', '');
		var value = parseFloat(showVal) * 0.01;
		if (value < 0)
			value = 0;
		if (value > 1)
			value = 1;
		return value;
	},
	setValue : function(input, value, editParm) {
		var column = editParm.column;
		var precision = column.editor.precision || 0;
		if (value < 0)
			value = 0;
		if (value > 1)
			value = 1;
		var showVal = (value * 100).toFixed(precision) + "%";
		input.val(showVal);
	},
	resize : function(input, width, height, editParm) {
		input.width(width).height(height);
	}
};

// 扩展一个 数字输入 的编辑器
$.ligerDefaults.Grid.editors['numberbox'] = {
	create : function(container, editParm) {
		var column = editParm.column;
		var precision = column.editor.precision;
		var input = $("<input type='text' style='text-align:right' class='l-text' />");
		input.bind('keypress', function(e) {
			var keyCode = window.event ? e.keyCode : e.which;
			return keyCode >= 48 && keyCode <= 57 || keyCode == 46
					|| keyCode == 8;
		});
		input.bind('blur', function() {
			var value = input.val();
			input.val(parseFloat(value).toFixed(precision));
		});
		container.append(input);
		return input;
	},
	getValue : function(input, editParm) {
		return parseFloat(input.val());
	},
	setValue : function(input, value, editParm) {
		var column = editParm.column;
		var precision = column.editor.precision;
		input.val(value.toFixed(precision));
	},
	resize : function(input, width, height, editParm) {
		input.width(width).height(height);
	}
};

$.ligerDefaults.Grid.editors['date'] = {
	create : function(container, editParm) {
		var column = editParm.column;
		var input = $("<input type='text'/>");
		container.append(input);
		var options = {};
		var ext = column.editor.p || column.editor.ext;
		if (ext) {
			var tmp = typeof (ext) == 'function' ? ext(editParm.record,
					editParm.rowindex, editParm.value, column) : ext;
			$.extend(options, tmp);
		}
		input.ligerDateEditor(options);
		return input;
	},
	getValue : function(input, editParm) {
		return input.liger('option', 'value');
	},
	setValue : function(input, value, editParm) {
		input.liger('option', 'value', value);
	},
	resize : function(input, width, height, editParm) {
		input.liger('option', 'width', width);
		input.liger('option', 'height', height);
	},
	destroy : function(input, editParm) {
		input.liger('destroy');
	}
};

$.ligerDefaults.Grid.editors['select'] = $.ligerDefaults.Grid.editors['combobox'] = {
	create : function(container, editParm, columnWidth) {
		var column = editParm.column;
		var input = $("<input type='text'/>");
		container.append(input);
		var options = {
			data : column.editor.data,
			slide : false,
			valueField : column.editor.valueField
					|| column.editor.valueColumnName,
			textField : column.editor.textField
					|| column.editor.displayColumnName,
			selectBoxWidth : false
		};
		if (columnWidth) {
			options.selectBoxWidth = columnWidth;
		}
		var ext = column.editor.p || column.editor.ext;
		if (ext) {
			var tmp = typeof (ext) == 'function' ? ext(editParm.record,
					editParm.rowindex, editParm.value, column) : ext;
			$.extend(options, tmp);
		}
		input.ligerComboBox(options);
		return input;
	},
	getValue : function(input, editParm) {
		return input.liger('option', 'value');
	},
	setValue : function(input, value, editParm) {
		input.liger('option', 'value', value);
	},
	resize : function(input, width, height, editParm) {
		input.liger('option', 'width', width - 1);
		input.liger('option', 'height', height - 2);
	},
	destroy : function(input, editParm) {
		input.liger('destroy');
	}
};

$.ligerDefaults.Grid.editors['int'] = $.ligerDefaults.Grid.editors['float'] = $.ligerDefaults.Grid.editors['spinner'] = {
	create : function(container, editParm) {
		var column = editParm.column;
		var input = $("<input type='text'/>");
		container.append(input);
		input.css({
			border : '#6E90BE'
		});
		var options = {
			type : column.editor.type == 'float' ? 'float' : 'int'
		};
		if (column.editor.minValue != undefined)
			options.minValue = column.editor.minValue;
		if (column.editor.maxValue != undefined)
			options.maxValue = column.editor.maxValue;
		input.ligerSpinner(options);
		return input;
	},
	getValue : function(input, editParm) {
		var column = editParm.column;
		var isInt = column.editor.type == "int";
		if (isInt)
			return parseInt(input.val(), 10);
		else
			return parseFloat(input.val());
	},
	setValue : function(input, value, editParm) {
		input.val(value);
	},
	resize : function(input, width, height, editParm) {
		input.liger('option', 'width', width);
		input.liger('option', 'height', height);
	},
	destroy : function(input, editParm) {
		input.liger('destroy');
	}
};

$.ligerDefaults.Grid.editors['string'] = $.ligerDefaults.Grid.editors['text'] = {
	create : function(container, editParm) {
		var input = $("<input type='text' class='l-text-editing'/>");
		if (typeof (editParm.column.validate) == "string") {
			input.attr("validate", editParm.column.validate);
		} else if (editParm.column.validate
				&& typeof (editParm.column.validate) == "object") {
			input.attr("validate", JSON2.stringify(editParm.column.validate));
		}
		if (editParm.grid) {
			var id = editParm.grid.id + "_editor_"
					+ editParm.grid.editorcounter++ + "_"
					+ new Date().getTime();
			input.attr("name", id).attr("id", id);
		}
		container.append(input);
		input.ligerTextBox();
		return input;
	},
	getValue : function(input, editParm) {
		return input.val();
	},
	setValue : function(input, value, editParm) {
		input.val(value);
	},
	resize : function(input, width, height, editParm) {
		input.liger('option', 'width', width - 2);
		input.liger('option', 'height', 19);
	},
	destroy : function(input, editParm) {
		input.liger('destroy');
	}
};

// 扩展 ligerGrid 的 搜索功能(高级自定义查询)
$.ligerui.controls.Grid.prototype.showFilter = function() {
	var g = this, p = this.options;
	if (g.winfilter) {
		g.winfilter.show();
		return;
	}
	var filtercontainer = $('<div id="' + g.id + '_filtercontainer"></div>')
			.width(380).height(120).hide();
	var fields = [];
	$(g.columns).each(
			function() {
				var o = {
					name : this.name,
					display : this.display
				};
				var isNumber = this.type == "int" || this.type == "number"
						|| this.type == "float";
				var isDate = this.type == "date";
				if (isNumber)
					o.type = "number";
				if (isDate)
					o.type = "date";
				if (this.editor) {
					o.editor = this.editor;
				}
				fields.push(o);
			});
	var filter = filtercontainer.ligerFilter({
		fields : fields
	});
	g.winfilter = $.ligerDialog.open({
		width : 420,
		height : 208,
		target : filtercontainer,
		isResize : true,
		top : 50,
		buttons : [ {
			text : '确定',
			onclick : function(item, dialog) {
				loadFilterData();
				dialog.hide();
			}
		}, {
			text : '取消',
			onclick : function(item, dialog) {
				dialog.hide();
			}
		} ]
	});

	var historyPanle = $('<div class="historypanle"><select class="selhistory"><option value="0">历史查询记录</options></select><input type="button" value="删除" class="deletehistory" /><input type="button" value="保存" class="savehistory" /></div>');
	filtercontainer.append(historyPanle);

	var historySelect = $(".selhistory", historyPanle).change(function() {
		if (this.value == "0")
			return;
		var rule = getHistoryRule(this.value);
		if (rule)
			filter.setData(rule);
	});

	$(".deletehistory", historyPanle).click(function() {
		if (historySelect.val() == "0")
			return;
		$.ligerDialog.confirm('确定删除吗', function(yes) {
			if (yes) {
				removeHistory(historySelect.val());
				reLoadHistory();
			}
		});
	});

	$(".savehistory", historyPanle).click(
			function() {
				$.ligerDialog.prompt('输入保存名字', JSON2.stringify(new Date())
						.replace(/["-\.:]/g, ''), false, function(yes, name) {
					if (yes && name) {
						addHistory(name);
						reLoadHistory();
						historySelect.val(name);
					}
				});
			});

	reLoadHistory();

	function getKey() {
		return encodeURIComponent(p.url.replace(/(.+)?view=/, ''));
	}

	function reLoadHistory() {
		historySelect.html('<option value="0">历史查询记录</options>');
		var key = getKey();
		var history = BIONE.cookies.get(key);
		if (history) {
			var data = JSON2.parse(history);
			$(data).each(
					function() {
						historySelect.append('<option value="' + this.name
								+ '">' + this.name + '</options>');
					});
		}
	}
	function removeHistory(name) {
		var key = getKey();
		var data;
		var history = BIONE.cookies.get(key);
		if (history) {
			data = JSON2.parse(history);
			for ( var i = 0, l = data.length; i < l; i++) {
				if (data[i].name == name) {
					data.splice(i, 1);
					BIONE.cookies.set(key, JSON2.stringify(data));
					return;
				}
			}
		}
	}

	function addHistory(name) {
		var key = getKey();
		var data;
		var history = BIONE.cookies.get(key);
		if (history) {
			data = JSON2.parse(history);
			data.push({
				name : name,
				value : filter.getData()
			});

		} else {
			data = [ {
				name : name,
				value : filter.getData()
			} ];
		}
		BIONE.cookies.set(key, JSON2.stringify(data));
	}

	function getHistoryRule(name) {
		var key = getKey();
		var history = BIONE.cookies.get(key);
		if (history) {
			var data = JSON2.parse(history);
			for ( var i = 0, l = data.length; i < l; i++) {
				if (data[i].name == name)
					return data[i].value;
			}
		}
		return null;
	}

	function loadFilterData() {
		var data = filter.getData();
		if (data && data.rules && data.rules.length) {
			g.set('parms', {
				where : JSON2.stringify(data)
			});
		} else {
			g.set('parms', {});
		}
		g.loadData();
	}
};

/*
 * 表单 扩展
 */

$.extend($.ligerui.controls.TextBox.prototype, {
	checkNotNull : function() {
		var g = this, p = this.options;
		if (p.nullText && !p.disabled) {
			if (!g.inputText.val()) {
				g.inputText.addClass("l-text-field-null").val(p.nullText);
			}
		}
	}
});

$.extend($.ligerui.controls.ComboBox.prototype, {
	_setHeight : function(value) {
		var g = this;
		if (value > 10) {
			g.wrapper.height(value);
			g.inputText.height(value);
			g.link.height(value);
			g.textwrapper.css({
				height : value
			});
		}
	}
});

// 扩展 DateEditor 的updateStyle方法
$.ligerui.controls.DateEditor.prototype.updateStyle = function() {
	var g = this, p = this.options;
	// Grid的date默认格式化函数就有对日期的处理
	var v = $.ligerDefaults.Grid.formatters['date'](g.inputText.val(), {
		format : p.format
	});
	g.inputText.val(v);
};

/*
 * 下拉框 combobox
 */

// 下拉框 加载文本值(有的时候在数据库只是返回了id值，并没有加载文本值，需要调用这个方法，远程获取)
$.ligerui.controls.ComboBox.prototype.loadText = function(options) {
	var g = this, p = this.options;
	options = $.extend({
		url : '../handler/select.ashx',
		view : null,
		idfield : null,
		textfield : null
	}, options || {});
	var value = options.value || g.getValue();
	var where = {
		op : 'and',
		rules : [ {
			field : options.idfield,
			op : 'equal',
			value : value
		} ]
	};
	$.ajax({
		cache : false,
		async : true,
		dataType : 'json',
		type : 'post',
		url : options.url,
		data : {
			view : options.view,
			idfield : options.idfield,
			textfield : options.textfield,
			where : JSON2.stringify(where)
		},
		success : function(data) {
			if (!data || !data.length)
				return;
			g._changeValue(data[0]['id'], data[0]['text']);
		}
	});
};
// sql编辑器sqlEditor
$.extend(liger.editors, {
	"sql": {
		control: "SqlEditor"
	}
});
$.ligerui.controls.SqlEditor = function (element, options)
{
    $.ligerui.controls.SqlEditor.base.constructor.call(this, element, options);
};
$.ligerui.controls.SqlEditor.ligerExtend($.ligerui.controls.Input, {
	__getType: function ()
    {
        return 'SqlEditor';
    },
    _init: function ()
    {
        $.ligerui.controls.SqlEditor.base._init.call(this);
    },
    _render: function (){
    	console.log('sql');
    },
    _setCss: function() {},
    _setCancelable: function() {},
    _setDisabled: function() {},
    _setWidth: function() {},
    _setHeight: function() {},
    _getText: function() {},
    _getValue: function() {},
    _setValue: function() {}
});
