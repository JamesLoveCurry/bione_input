/*!
 * 参数模版中把模型转换为LigerForm对象的模块
 * 
 */
define(['jquery', 'JSON2', 'template/property', 'template/util', 'Ligerui'], function ($, JSON2, Property, Util) {
	
	// 根据不同视图转换值的工厂对象
	var ViewValueManager = ViewValueManager || {};

	ViewValueManager.value = ViewValueManager.value || {};
	
	ViewValueManager.DataSeter = ViewValueManager.DataSeter || {};

	// 执行下拉框SQL语句的公用接口
	var dbSelectExecutorUrl = Util.getContextPath() + "/report/frame/param/commonComboBox/executeSelectSQL.json";
	
	// 执行数据树SQL语句的公用接口
	var dbTreeExecutorUrl = Util.getContextPath() + "/report/frame/param/commonComboBox/executeTreeSQL.json";
	
	// 执行弹出树SQL语句的公用接口
	var dbPopupExecutorUrl = Util.getContextPath() + "/report/frame/param/commonComboBox/executePopupSQL.json";
	
	// 执行数据树SQL语句的公用接口
	var convertMacroUrl = Util.getContextPath() + "/report/frame/param/templates/convertMacro";
	
	// 转换为LigerForm配置对象
	function toLigerForm (componentes, viewType,options) {
		window.options=options;
		var ligerFormObj = {
			inputWidth: 200,
			labelWidth: 90,
			space: 40
		}, fields = [], i = 0, manager = ViewValueManager.factory(viewType);
		ViewValueManager.createCorrelatedIndexes(componentes);
		for (i = 0 ; i < componentes.length ; i++) {
			fields.push(manager._buildField(componentes[i], viewType));
		}
		ligerFormObj['fields'] = fields;
		ligerFormObj['onAfterSetFields'] = _onAfterSetFields;
		return ligerFormObj;
	};
	
	//表单完成以后注入方法
	function _onAfterSetFields () {
		resetDateValue();
	};
	
	//树设置默认值重新填写显示值
	function resetPopupValue() {
		$.each($.ligerui.managers, function (key, comp) {
			if ( comp.__getType() === 'PopupEdit' && comp.getValue() !== '' && comp.getText() === ''){
				var puuid = comp.valueField.attr('puuid'), parentValue = '', parentText = '';
				$('input[uuid=' + puuid + ']').each(function () {
					var parent = $.ligerui.get(Util.getLigeruiID($(this)));
					if (parent && parent.getValue) {
						parentValue = parent.getValue();
					}
					if (parent && parent.getText) {
						parentText = parent.getText();
					}
				});
				if (comp.getValue().indexOf('#{') >= 0) {
					$.ajax({
	                    type: 'post',
	                    url: convertMacroUrl,
	                    cache: false,
	                    async: false,
	                    data: {
	                    	macroValue : comp.getValue()
	                    },
	                    success: function (value){
	                    	comp.setValue(value);
	                    	ViewValueManager.DataSeter['popup'].setText(comp.valueField.attr('options'), value, comp, parentValue, parentText);
	                    }
	                });
				} else {
					ViewValueManager.DataSeter['popup'].setText(comp.valueField.attr('options'), comp.getValue(), comp, parentValue, parentText);
				}
			}
		});
		 if(options['afterLoadForm'] !=null&& typeof(options['afterLoadForm'])== "function"){
         	options.afterLoadForm();
         }
	};
	
	//根据日期框的宏，重新设置日期默认值
	//edit by fangjuan 2014-07-23 从哈尔滨v3版本中拷出来的版本
	function resetDateValue() {
    	var managers = liger.managers, mid = "", ids = [], g = this;
    	for (mid in managers) {
    		if (managers[mid].type === "DateEditor" && /#\{([^}]+)/.test(managers[mid].options.value)) {
    			ids.push(mid);
    		}
    	}
    	if (ids.length > 0) {
    		require({
    			paths : {
    				'moment' : 'moment/moment.min'
    			}
    		}, [ 'moment' ], function(moment) {
    				for (var i = 0; i < ids.length ; i++ ) {
    					var widget = managers[ids[i]];
    					var macro = null, pattern = null, cal = null;
    					if ((macros = widget.options.value.match(/#\{([^}]+)/g)) && (cal = moment())) {
    						for (var j = 0 ; j < macros.length ; j++) {
    							macro = macros[j].replace("#{", "");
    							if (macro === 'now') {
        							cal = moment();
        						} else if ((pattern = macro.match(/(\w+)([+-])(\d+)/))) {
        							if (pattern[2] === '+') {
        								cal.add(pattern[1], pattern[3]);
        							} else if (pattern[2] === '-') {
        								cal.subtract(pattern[1], pattern[3]);
        							}
        						} else if(macro.indexOf("(") >= 0 && macro.indexOf(")") >= 1 ) {
        							eval("cal." + macro);
        						}
    						}
    					}
    					if (cal) {
    						var format = widget.options.format ? widget.options.format : (widget.options.showTime === 'true' ? 'yyyy-MM-dd hh:mm' : 'yyyy-MM-dd');
    						format = format.replace(/y/g, "Y");
    						format = format.replace(/d/g, "D");
    						format = format.replace(/h/g, "H");
    						widget.setValue(cal.format(format));
    					}
    				}
    				resetPopupValue();
    		});
    	} else {
    		resetPopupValue();
    	}
    	
    };
	
	$.extend(ViewValueManager, {
		
		// 工厂方法，产生对应的对象
		factory : function (viewType) {
			if(typeof ViewValueManager.value[viewType] !== 'object'){
		      	throw{
		        	name: "Error",
		        	message: viewType + " doesn't exist "
		      	};
		    }
			return ViewValueManager.value[viewType];
		},
		
		//创建级联关系索引
		createCorrelatedIndexes : function (componentes) {
			var indexes = {}, i = 0;
			for (i = 0 ; i < componentes.length ; i++) {
				if (componentes[i]['datasource']) {
					var datasource = JSON2.parse(componentes[i]['datasource']), puuid = datasource['puuid'];
					if (puuid) {
						if (!indexes[puuid]) {
							indexes[puuid] = [];
						}
						indexes[puuid].push(componentes[i]);
					}
				}
			}
			this.indexes = indexes;
		},
		
		// 通用创建属性方法，会分别被子对象调用，
		// 参数viewType为视图类型（design或real）
		// 参数component为模型对象
		// 参数componentType为property中配置的当前控件field类型，widget为但前field主属性，options和attr为放到对应的子属性中
		// 参数type为field类型
		// 参数options为附加的属性对象
		_createAttres : function (viewType, component, componentType, type, options) {
			var i = 0, result = {}, attres = componentType[type], manager = ViewValueManager.factory(viewType);
			for (i = 0; attres && i < attres.length ; i++ ) {
				// 针对有下划线隔开的属性，进行特殊处理
				if (attres[i].indexOf('_') > 0) {
					var ps = attres[i].split("_"), subObj = ps[0], subObjAttr = ps[1];
					if(result[subObj] === undefined) result[subObj] = {};
					manager.setCommonFieldValue(result[subObj], subObjAttr, component[attres[i]]);
				} else {
					manager.setCommonFieldValue(result, attres[i], component[attres[i]]);
				}
			}
			manager.setOptions(result, options);
			return result;
		}
	});

	$.extend(ViewValueManager.value, {

		// 设计时调用的函数
		design : {

			// 创建LigerForm的field对象
			_buildField : function (component, viewType) {
				var componentType = Property.getComponentType(component['type']), field = null;
				field = ViewValueManager._createAttres(viewType, component, componentType, 'widget', {type : component["type"]});
				field["options"] = ViewValueManager._createAttres(viewType, component, componentType, 'options');
				field["attr"] = ViewValueManager._createAttres(viewType, component, componentType, 'attr', {cid : component["cid"], uuid : component["id"]});
				return field;
			},

			// 设置field的值
			setCommonFieldValue : function (field, key, value) {
				if (key === 'disabled') {
					field[key] = 'disabled';
					return;
				}
				if (value === undefined || value === null || value === 'null' || key === 'value' || key === 'data') {
					return;
				}
				if (value == "true") {
		            field[key] = true;
		        } else if (value == "false") {
		            field[key] = false;
		        } else {
		            field[key] = value;
		        }
			},

			// 设置附加的属性
			setOptions : function (field, options) {
				var key = null, value = null;
				for (key in options) {
					if (options.hasOwnProperty(key)) {
						value = options[key];
						if (key === 'type' && value === 'hidden') {
							field['display'] = '隐藏域';
							field[key] = 'text';
						} else {
							field[key] = value;
						}
					}
				}
			}
		},

		// 实际生产环境时调用的函数
		real : {
			
			bindCorrelatedEvent : function (options, component) {
				if (ViewValueManager.indexes[component['id']]) {
					var propes = this.eventBinder[component['type']];
					for (var eventName in propes) {
						options[eventName] = this[propes[eventName]];
					}
				}
			},

			// 创建LigerForm的field对象
			_buildField : function (component, viewType) {
				var componentType = Property.getComponentType(component['type']), field = null;
				field = ViewValueManager._createAttres(viewType, component, componentType, 'widget', {type : component["type"]});
				if ('select' === component['type']) {
					var attrs = this._getSelectExtendAtrres(component);
					field["options"] = ViewValueManager._createAttres(viewType, component, componentType, 'options', attrs['options']);
					field["attr"] = ViewValueManager._createAttres(viewType, component, componentType, 'attr', attrs['attr']);
				} else if ('combobox' === component['type']) {
					var attrs = this._getTreeExtendAtrres(component);
					field["options"] = ViewValueManager._createAttres(viewType, component, componentType, 'options', attrs['options']);
					field["attr"] = ViewValueManager._createAttres(viewType, component, componentType, 'attr', attrs['attr']);
				} else if ('popup' === component['type']) {
					var attrs = this._getPopupExtendAtrres(component);
					field["options"] = ViewValueManager._createAttres(viewType, component, componentType, 'options', attrs['options']);
					field["attr"] = ViewValueManager._createAttres(viewType, component, componentType, 'attr', attrs['attr']);
					field["attr"]["dialog"] = JSON2.stringify(ViewValueManager._createAttres(viewType, component, componentType, 'dialog', attrs['dialog'])).replace(/\"/g, "'");
					field["attr"]["tree"] = JSON2.stringify(ViewValueManager._createAttres(viewType, component, componentType, 'tree', attrs['tree'])).replace(/\"/g, "'");
				} else {
					field["options"] = ViewValueManager._createAttres(viewType, component, componentType, 'options');
					field["attr"] = ViewValueManager._createAttres(viewType, component, componentType, 'attr', {uuid : component["id"]});
				}
				this.bindCorrelatedEvent(field["options"], component);
				// 处理模型中验证配置信息
				var validateStr = component["validate"];
				if (validateStr !== undefined && validateStr !== null && validateStr !== "" && validateStr !== "{}") {
					field["validate"] = JSON2.parse(validateStr);
				}
				var requiredStr = component["required"];
				if (requiredStr !== undefined && requiredStr !== null && requiredStr !== "" && requiredStr === "true") {
					field["validate"] = { required : true };
				}
				return field;
			},
			
			// 工厂方法，产生对应的对象
			_getSeter : function (ltype) {
				if(typeof ViewValueManager.DataSeter[ltype] !== 'object'){
			      	throw{
			        	name: "Error",
			        	message: ltype + " doesn't exist "
			      	};
			    }
				return ViewValueManager.DataSeter[ltype];
			},

			// 绑定级联下拉框的onSelected事件，参数selectedValue上级下拉框的实际值，参数selectedText上级下拉框的显示值
			_onSelected: function (selectedValue, selectedText) {
				//edit by fangjuan 2014-07-22
				//解决级联下拉框未加载时，触发该事件出错的问题
				if(selectedValue  && !selectedValue == ""){
				//edit end
					var $parent = this.valueField, uuid = $parent.attr('uuid');
					if (!selectedValue && !selectedText) {
						return false;
					}
					$('input[puuid=' + uuid + ']').each(function () {
						var $this = $(this), ltype = $this.attr('ltype'), options = $this.attr('options'), ligeruiID = Util.getLigeruiID($this);
						if (ligeruiID === undefined) {
						//解决级联下拉框默认选中时子下拉框未初始化问题
							var intervalID = setInterval(function (){
								var ligeruiID = Util.getLigeruiID($this), ajaxState = $this.attr('ajaxState');
								if (ligeruiID && ajaxState) {
									$this.attr('ajaxState', true);
									clearInterval(intervalID);
									ViewValueManager.value.real._setDataFromCommon(options, ligeruiID, ltype, selectedValue, selectedText);
								}
							}, 500);
						} else {
							ViewValueManager.value.real._setDataFromCommon(options, ligeruiID, ltype, selectedValue, selectedText);
						}
					});
				}
			},
			
			_onChangeValue: function (value) {
				var $parent = this.inputText, uuid = $parent.attr('uuid');
				if (!value) {
					return false;
				}
				$('input[puuid=' + uuid + ']').each(function () {
					var $this = $(this), ltype = $this.attr('ltype'), options = $this.attr('options'), ligeruiID = Util.getLigeruiID($this);
					if (ligeruiID === undefined) {
						//解决级联下拉框默认选中时子下拉框未初始化问题
						var intervalID = setInterval(function (){
							var ligeruiID = Util.getLigeruiID($this);
							if (ligeruiID) {
								clearInterval(intervalID);
								ViewValueManager.value.real._setDataFromCommon(options, ligeruiID, ltype, value);
							}
						}, 500);
					} else {
						ViewValueManager.value.real._setDataFromCommon(options, ligeruiID, ltype, value);
					}
				});
			},
			
			// 设置子数据树中的data数据
			// 参合options为当前子下拉框的配置
			// 参数ligeruiID为当前子下拉框的ligeruiID
			// 参数ltype为当前要设置值的控件
			// 参数value上级控件的实际值
			// 参数text上级控件的显示值
			_setDataFromCommon : function (options, ligeruiID, ltype, value ,text) {
				var g = this, data = {};
				if (value) {
					data['value'] = value;
				} 
				if (text) {
					data['text'] = text;
				}
				if (typeof options === 'string') {
					//针对JSON2不支持单引号问题，需要转义，并保护sql语句中的单引号
					if (options.indexOf("'sql':") > 0) {
						//把sql部分配置单独取出来放到临时变量sql中，options转成JSON后再设置回去
						var sql = options.substring(options.indexOf("'sql':") + 7, options.length - 2);
						if(sql.indexOf("','format'") > 0) sql = sql.substring(0, sql.indexOf("','format'"));
						if(sql.indexOf("','db'") > 0) sql = sql.substring(0, sql.indexOf("','db'"));
						options = options.replace(sql, "");
						options = JSON2.parse(options.replace(/'/g, "\""));
						options['sql'] = sql;
					} else {
						options = JSON2.parse(options.replace(/'/g, "\""));
					}
				}
				if (options['data']) {
					var newData = new Array();
                    for (var i = 0; i < options['data'].length; i++)
                    {
                        if (options['data'][i].pid === value)
                        {
                            newData.push(options['data'][i]);
                        }
                    }
                    g._getSeter(ltype).setData(ligeruiID, newData);
				} else if (options['url']) {
					
					$.ajax({
	                    type: options['ajaxType'],
	                    url: Util.getContextPath() + "/" + options['url'],//edit by fangjuan 2014-07-22
	                    cache: false,
	                    dataType: 'json',
	                    data: data,
	                    success: function (data){
	                    	var ligerComp = $.ligerui.get(ligeruiID), lv = ligerComp.getValue(), lt = ligerComp.getText();
	                    	if (ltype === 'select' && lv !== '' && lt === '' && ligerComp.selectValue) {
		                    	g._getSeter(ltype).setData(ligeruiID, data);
		                    	$.each(data, function(index, item){
		                    		if (item['id'] === lv) {
		                    			//ligerComp.inputText.val(item['text']);
		                    			ligerComp.selectValue(item['id']);//edit by fangjuan 2014-12-16 由上面的改为现在的，解决级联时无法设置下拉框的实际值的bug
		                    		}
		                    	});
	                    	} else {
	                    		g._getSeter(ltype).setData(ligeruiID, data);
	                    	}
	                    }
	                });
				} else if (options['sql']) {
					data['sql'] = options['sql'];
					data['db'] = options['db'];
					$.ajax({
	                    type: "post",
	                    url: g._getSeter(ltype).getUrl(),
	                    cache: false,
	                    dataType: 'json',
	                    data: data,
	                    success: function (data){
	                    	var ligerComp = $.ligerui.get(ligeruiID), lv = ligerComp.getValue(), lt = ligerComp.getText();
	                    	if (ltype === 'select' && lv !== '' && lt === '' && ligerComp.selectValue) {
		                    	g._getSeter(ltype).setData(ligeruiID, data);
		                    	$.each(data, function(index, item){
		                    		if (item['id'] === lv) {
//		                    			 ligerComp.inputText.val(item['text']);
		                    			ligerComp.selectValue(item['id']);//edit by fangjuan 2014-12-16 由上面的改为现在的，解决级联时无法设置下拉框的实际值的bug
		                    		}
		                    	});
	                    	} else {
	                    		g._getSeter(ltype).setData(ligeruiID, data);
	                    	}
	                    }
	                });
				} else {
					// 如果为空或者不符合预定义的配置格式，什么都不做
				}
			},

			// 设置field的值
			setCommonFieldValue : function (field, key, value) {
				if (value === undefined || value === null || value === 'null' ) {
					return;
				}
				if (value == "true") {
		            field[key] = true;
		        } else if (value == "false") {
		            field[key] = false;
		        } else {
		            field[key] = value;
		        }
			},
			
			// 生成数据树特殊的属性
			_getTreeExtendAtrres : function (component) {
				var attres = {};
				attres['attr'] = {};
				attres['attr']['uuid'] = component["id"];
				attres['options'] = {};
				attres['options']['tree_parentIDFieldName'] = 'upId';
				if (component['tree_checkbox'] !== 'true') {
					attres['options']['treeLeafOnly'] = 'false';
				}
				if (component['datasource']) {
					var datasource = JSON2.parse(component['datasource']);
					if (datasource['puuid']) {
						attres['attr']['puuid'] = datasource['puuid'];
						//把双引号转义为单引号，把配置信息放到组件html属性中
						attres['attr']['options'] = JSON2.stringify(datasource['options']).replace(/\"/g, "'");
					} else {
						this._convertTreeOptions2Data(attres['options'], datasource['options']);
					}
				}
				return attres;
			},
			
			//生成弹出树的属性值
			_getPopupExtendAtrres : function (component) {
				var attres = {};
				attres['attr'] = {};
				attres['attr']['uuid'] = component["id"];
				attres['options'] = {};
				attres['options']['onButtonClick'] = this._onButtonClick;
				attres['tree'] = {};
				attres['tree']['parentIDFieldName'] = 'upId';
				attres['dialog'] = {};
				if (component['tree_checkbox'] !== 'true') {
					attres['tree']['treeLeafOnly'] = 'false';
				}
				if (component['datasource']) {
					var datasource = JSON2.parse(component['datasource']);
					if (datasource['puuid']) {
						attres['attr']['puuid'] = datasource['puuid'];
					}
					attres['attr']['options'] = JSON2.stringify(datasource['options']).replace(/\"/g, "'");
				}
				return attres;
			},
			
			_onButtonClick : function () {
				window['popuptree'] || (window['popuptree'] = {});
				var $this = $(this), id = $this.attr("id"), $ele = $.ligerui.get(id).valueField, puuid = $ele.attr("puuid"), options = $ele.attr("options"), dialog = $ele.attr('dialog'), dw = 220, dh = 300;
				if (!options) return false;
				if (puuid) {
					var flag = false;
					$('input[uuid=' + puuid + ']').each(function () {
						var $this = $(this), pv = $.ligerui.get(Util.getLigeruiID($this)).getValue();
						if (pv === null || pv === '') {
							flag = true;
							var t = $this.parentsUntil('ul').parent('ul').find('li:first').text();
							$.ligerDialog.tip({ title: '提示信息', content: '请先给上级控件<B>' + t.substring(0, t.length - 1) + '[' + $this.attr('name') + ']' + '</B>赋值'});
						}
					});
					if (flag) {
						return false;
					}
				}
				if (dialog) {
					var dialogConfig = JSON2.parse(dialog.replace(/'/g, "\""));
					if (dialogConfig['dialogWidth'] && dialogConfig['dialogWidth'] > 0) {
						dw = dialogConfig['dialogWidth'];
					}
					if (dialogConfig['dialogHeight'] && dialogConfig['dialogHeight'] > 0) {
						dh = dialogConfig['dialogHeight'];
					}
				}
				if (window['popuptree'][id]) {
					if (!puuid) {
						window['popuptree'][id].show();
						return true;
					}
					window['popuptree'][id].destroy();
				}
				window['popuptree'][id] = $.ligerDialog.open({
					id: 'popuptreeDialog',
					title: '请选择',
					url: Util.getContextPath() + '/report/frame/param/templates/popupTree?id=' + id,
					width: dw,
					height: dh,
					isResize: false,
					allowClose : false,
					onClosed: function () {
                        for (var id in window['popuptree']) {
                            if (window['popuptree'].hasOwnProperty(id) && window['popuptree'][id]) {
                            	window['popuptree'][id].destroy();
                                delete window['popuptree'][id];
                            }
                        }
                    }
				});
			},

			// 生成下拉框特殊的属性
			_getSelectExtendAtrres : function (component) {
				var attres = {};
				attres['attr'] = {};
				attres['options'] = {};
				attres['attr']['uuid'] = component["id"];
				if (component['datasource']) {
					var datasource = JSON2.parse(component['datasource']);
					if (datasource['puuid']) {
						attres['attr']['puuid'] = datasource['puuid'];
						//把双引号转义为单引号，把配置信息放到组件html属性中
						attres['attr']['options'] = JSON2.stringify(datasource['options']).replace(/\"/g, "'");
					} else {
						this._convertSelectOptions2Data(attres['options'], datasource['options']);
					}
				}
				return attres;
			},
			
			// 把树的配置转换为用于显示的数据
			_convertTreeOptions2Data : function (attresOptions, options) {
				if (typeof options === 'string') {
					options = JSON2.parse(options);
				}
				if (options['url']) {
					attresOptions['tree_ajaxType'] = options['ajaxType'];
					attresOptions['tree_url'] = Util.getContextPath() + "/" + options['url'];//edit by fangjuan 2014-07-22
				} else if (options['sql']) {
					attresOptions['tree_ajaxType'] = 'post';
					attresOptions['tree_url'] = dbTreeExecutorUrl;
					attresOptions['tree_parms'] = {sql : options['sql'], db : options['db']};
				} else {
					attresOptions['tree_data'] = [];
				}
			},

			// 把下拉框的配置转换为用于显示的数据
			_convertSelectOptions2Data : function (attresOptions, options) {
				if (typeof options === 'string') {
					options = JSON2.parse(options);
				}
				if (options['data']) {
					attresOptions['data'] = options['data'];
				} else if (options['url']) {
					attresOptions['ajaxType'] = options['ajaxType'];
					attresOptions['url'] = Util.getContextPath() + "/" + options['url'];//edit by fangjuan 2014-07-22
				} else if (options['sql']) {
					attresOptions['url'] = dbSelectExecutorUrl;
					attresOptions['parms'] = {sql : options['sql'], db : options['db']};
				} else {
					attresOptions['data'] = [];
				}
			},

			// 设置附加的属性
			setOptions : function (field, options) {
				var key = null;
				for (key in options) {
					if (options.hasOwnProperty(key) && options[key] !== undefined && options[key] !== null) {
						if (key.indexOf('_') > 0) {
							var ps = key.split("_"), subObj = ps[0], subObjAttr = ps[1];
							if(field[subObj] === undefined) field[subObj] = {};
							this.setCommonFieldValue(field[subObj], subObjAttr, options[key]);
						} else {
							this.setCommonFieldValue(field, key, options[key]);
						}
					}
				}
			},
			
			//事件的绑定配置
			eventBinder : {'select' : {'onSelected' : '_onSelected'}, 'date' : {'onChangeDate' : '_onChangeValue'}}
		}

	});
	
	$.extend(ViewValueManager.DataSeter, {
		//树
		combobox : {
			setData : function (ligeruiID, data) {
				$.ligerui.get(ligeruiID).clear();
            	$.ligerui.get(ligeruiID).getTree().clear();
            	$.ligerui.get(ligeruiID).getTree().setData(data);
			},
			
			getUrl : function () {
				return dbTreeExecutorUrl;
			}
		},
		
		popup : {
			setData : function (ligeruiID, data) {
				$.ligerui.get(ligeruiID).clear();
			},
			
			setText : function (options, value, comp, parentValue, parentText) {
				var g = this, data = {};
				if (parentValue) {
					data['value'] = parentValue;
				} 
				if (parentText) {
					data['text'] = parentText;
				}
				if (typeof options === 'string') {
					//针对JSON2不支持单引号问题，需要转义，并保护sql语句中的单引号
					if (options.indexOf("'sql':") > 0) {
						//把sql部分配置单独取出来放到临时变量sql中，options转成JSON后再设置回去
						var sql = options.substring(options.indexOf("'sql':") + 7, options.length - 2);
						if(sql.indexOf("','format'") > 0) sql = sql.substring(0, sql.indexOf("','format'"));
						if(sql.indexOf("','db'") > 0) sql = sql.substring(0, sql.indexOf("','db'"));
						options = options.replace(sql, "");
						options = JSON2.parse(options.replace(/'/g, "\""));
						options['sql'] = sql;
					} else {
						options = JSON2.parse(options.replace(/'/g, "\""));
					}
				}
				if (options['url']) {
					$.ajax({
		                type: options['ajaxType'],
		                url: Util.getContextPath() + "/" + options['url'],//edit by fangjuan 2014-07-22
		                cache: false,
		                dataType: 'json',
		                data: data,
		                success: function (data){
		                	comp.setText(g._findText(JSON2.stringify(data), value));
		                }
		            });
				} else if (options['sql']) {
					data['sql'] = options['sql'];
					data['db'] = options['db'];
					$.ajax({
		                type: "post",
		                url: dbPopupExecutorUrl,
		                cache: false,
		                dataType: 'json',
		                data: data,
		                success: function (data) {
		                	comp.setText(g._findText(JSON2.stringify(data), value));
		                }
		            });
				} else {
					// 如果为空或者不符合预定义的配置格式，什么都不做
				}
			},
			
			_findText : function(jsonStr, value){
				jsonStr = jsonStr.replace(/"params":\{[^\}]*\}/g, "");
            	var valueIndex = jsonStr.indexOf(value);
            	if (valueIndex < 0) {
            		return "";
            	}
            	var endIndex = jsonStr.indexOf('}', valueIndex);
            	var startIndex = jsonStr.lastIndexOf('{', valueIndex);
            	return jsonStr.substring(startIndex, endIndex+1).match(/"text":"([^"]+)/)[1];
			},
			
			getUrl : function () {
				return dbPopupExecutorUrl;
			}
		},
		
		select : {
			setData : function (ligeruiID, data) {
				$.ligerui.get(ligeruiID).clear();
				$.ligerui.get(ligeruiID).clearContent();
                $.ligerui.get(ligeruiID).setData(data);
			},
			
			getUrl : function () {
				return dbSelectExecutorUrl;
			}
		}
	});

	return {
		convert: toLigerForm
	};
});