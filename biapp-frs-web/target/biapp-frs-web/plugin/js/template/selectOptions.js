/*!
 * 下拉框的数据源配置视图模块
 * 
 */
define(['jquery', 'JSON2', 'Underscore', 'Backbone', 'BackboneUndo', '../template/util','JqueryTemplate', 'Ligerui', 'LigeruiExpand', 'Codemirror', 'CodemirrorSql', 'SqlFormat'], function ($, JSON2, _, Backbone, BackboneUndo, Util) {

    // 自定义下拉框数据模型
	var DataOption = Backbone.Model.extend({
	});

    // 自定义下拉框数据模型集合
	var DataOptions = Backbone.Collection.extend({

        model: DataOption

	});

    // 自定义数据源视图
	var DataOptionsView = Backbone.View.extend({

		grid : null,

        type : "data",

		// undoManager : null,

		initialize: function () {
			_.bindAll(this, 'render', 'saveData', 'addNewRow', 'deleteRow', 'getOptionsData', 'getGridData');
			this.dataOptions = new DataOptions();
			// undoManager = new Backbone.UndoManager();
   //          undoManager.register(this.componentes);
   //          undoManager.startTracking();
		},

		render: function () {
			var g = this, pdata = null;
			this.el = $( "#dataOptions-template" ).tmpl();
            // 选择了上级下拉框
            if (this.parentId && this.parentId !== 'null') {
                pdata = g.getParentOptions(this.parentId);
                // 初始化列表
                grid = $(this.el).find("#maingrid").ligerGrid({
                    columns: [
                    { display: '上级选值', name: 'pid', width: 216,
                        editor: { type: 'select', data: pdata, valueColumnName: 'pid', displayColumnName: 'ptext'},
                        render: function (item) {
                            for (var i = 0 ; i < pdata.length ; i++) {
                                if (pdata[i].pid === item.pid){
                                    return pdata[i].ptext;
                                }
                            }
                            return item.pid;
                        }
                    },
                    { display: '实际值', name: 'id', width: 216,
                        editor: { type: 'text'}
                    },
                    { display: '显示值', name: 'text', width: 216,
                        editor: { type: 'text' }
                    }
                    ],
                    onSelectRow: function (rowdata, rowindex)
                    {
                        $("#txtrowindex").val(rowindex);
                    },
                    enabledEdit: true, clickToEdit: true, isScroll: false, tree: { columnName: 'text' }, usePager: false,
                    onAfterEdit: function (e){
                        var id = null;
                        // 对实际值字段进行唯一性验证
                        if (e.column.columnname === 'id'){
                            _.each(grid.getData(), function (obj, index){
                                var errorMessage = null;
                                id = e.column.columnname + '_' + index;
                                if (window.parent.OptionTip[id]) {
                                    window.parent.OptionTip[id].destroy();
                                    delete window.parent.OptionTip[id];
                                }
                                if (e.rowindex !== index && obj['id'] === e.value) {
                                    errorMessage = '实际值<B>' + e.value + '</B>重复了！';
                                }
                                if (errorMessage) {
                                    window.parent.OptionTip[id] = window.parent.$.ligerDialog.tip({ id: id, title: '提示信息', content: errorMessage});
                                }
                            });
                        }
                        var index = (parseInt($("span.pcontrol input").val()) - 1) * 10 + e.rowindex;
                        var option = g.dataOptions.at(index);
                        if (option) {
                            // 修改已有纪录
                            option.set(e.column.columnname, e.value);
                        } else {
                            // 新建纪录
                            option = new DataOption();
                            option.set(e.column.columnname, e.value);
                            g.dataOptions.add(option);
                        }
                    },
                    data: g.getGridData(),
                    width: '99.7%'
                });
            } else {
                // 初始化列表
    			grid = $(this.el).find("#maingrid").ligerGrid({
                    columns: [
                    { display: '实际值', name: 'id', width: 216,
                    	editor: { type: 'text'}
                    },
                    { display: '显示值', name: 'text', width: 216,
                        editor: { type: 'text' }
                    }
                    ],
                    onSelectRow: function (rowdata, rowindex)
                    {
                        $("#txtrowindex").val(rowindex);
                    },
                    enabledEdit: true, clickToEdit: true, isScroll: false, usePager: false,
                    onAfterEdit: function (e){
                    	var id = null;
                        // 对实际值字段进行唯一性验证
                    	if (e.column.columnname === 'id'){
                    		_.each(grid.getData(), function (obj, index){
                    			var errorMessage = null;
                    			id = e.column.columnname + '_' + index;
                    			if (window.parent.OptionTip[id]) {
                    				window.parent.OptionTip[id].destroy();
                    				delete window.parent.OptionTip[id];
                    			}
                    			if (e.rowindex !== index && obj['id'] === e.value) {
                    				errorMessage = '实际值<B>' + e.value + '</B>重复了！';
                    			}
                    			if (errorMessage) {
    	                			window.parent.OptionTip[id] = window.parent.$.ligerDialog.tip({ id: id, title: '提示信息', content: errorMessage});
                    			}
                    		});
                    	}
                        var index = (parseInt($("span.pcontrol input").val()) - 1) * 10 + e.rowindex;
                    	var option = g.dataOptions.at(index);
                    	if (option) {
                            // 修改已有纪录
    	                	option.set(e.column.columnname, e.value);
                    	} else {
                    		// 新建纪录
                            option = new DataOption();
                    		option.set(e.column.columnname, e.value);
                    		g.dataOptions.add(option);
                    	}
                    },
                    data: g.getGridData(),
                    width: '99.7%'
                });
                
            }
            grid.setHeight(270);
            // 初始化工具栏
			$(this.el).find("#gridToolbar").ligerToolBar({
        		items: [
        			{ text: '保存', click: g.saveData, icon:'save'},
	                { line:true },
	                { text: '关闭', click: g.closeDialog, icon:'right'},
	                { line:true },
	                { text: '增加', click: g.addNewRow, icon:'add'},
	                { line:true },
	                { text: '删除', click: g.deleteRow, icon:'delete'}
            	]
            });
            return this;
		},

        // 设置上级下拉框的唯一标识uuid
        setParentId: function (parentId) {
            this.parentId = parentId;
        },

        // 获取上级下拉框的自定义值列表
        getParentOptions: function (parentId) {
            var parentSelect = window.parent.app.template._getComponentesWhere({id : parentId});
            var datasource = parentSelect[0].get('datasource'), datasouceObj, data;
            if (datasource && (datasouceObj = JSON2.parse(datasource)) && (data = datasouceObj['options']['data'])) {
                return JSON2.parse(JSON2.stringify(data).replace(/id/g, "pid").replace(/text/g, "ptext"));
            }
            return {};
        },

        // 保存数据
		saveData: function () {
			// var data = {}, parentDatasource = window.parent.$.ligerui.get('datasource');
            // 针对未离开列表框中的编辑控件时，没有成功修改模型就点击保存时的特殊处理方法
            if ( $("#selectOptionsForm .l-grid-editor").length > 0 ) {
                $(document).trigger("click.grid");
            }

            var data = {}, puuid = undefined;
			data["options"] = {};
            if ((puuid = this.parentId) && puuid !=='null' ) {
                data["puuid"] = puuid;
            }
			data["options"]["data"] = this.getOptionsData();
            window.parent.$('#datasource').val(JSON2.stringify(data));
            // 保证参数模版的模型跟着修改
            window.parent.$('#datasource').trigger("change");
			this.closeDialog();
		},

        // 设置数据
		setData: function (data) {
			this.dataOptions.add(data['data']);
		},

		closeDialog: function () {
            var dialog = window.parent.OptionTip.datasourceDialog;
            dialog.close();
			// dialog.destroy();
		},

        // 在LigerGrid列表中添加一个空行
		addNewRow: function () {
            // this.dataOptions.add({text: ""});
            grid.addEditRow();
        }, 

        // 在LigerGrid列表中删除选中的行
        deleteRow: function () {
            var rows = grid.getSelectedRows(), i = 0, option = null ;
            for (i = 0 ; i < rows.length ; i++ ) {
                option = this.dataOptions.at(rows[i].__index);
                this.dataOptions.remove(option);
            }
             
            grid.deleteSelectedRow();
        },

        // 获取自定义数据源模型转换成的JSON对象
        getOptionsData: function () {
        	return this.dataOptions.toJSON();
        },

        // 获取列表数据
        getGridData: function () {
            return {Rows : this.getOptionsData()};
        }
	});

    // 远程数据源配置视图
	var RemoteOptionsView = Backbone.View.extend({
		
		type : "url",
		
		initialize: function () {
            _.bindAll(this, 'render', 'setParentId', 'saveData', 'closeDialog', 'destroy');
		},

        render: function (){
            var g = this;
            this.destroy();
            this.el = $('#remoteOptions-template').tmpl();
            $(this.el).ligerForm({inputWidth: 534});
            // 初始化提示信息
            $(this.el).find(".template-help").ligerTip();
            var ajaxType = $.ligerui.get('ajaxType');
            ajaxType.set('data', [{id: 'post', text: 'POST'}, {id: 'get', text: 'GET'}]);
            ajaxType.setValue('post');
            // ajaxType.set('width', 118);
            // 初始化工具栏
            $(this.el).find("#gridToolbar").ligerToolBar({
                items: [
                    { text: '保存', click: g.saveData, icon:'save'},
                    { line:true },
                    { text: '关闭', click: g.closeDialog, icon:'right'}
                ]
            });
            return this;
        },

        // 销毁ligeui对象
        destroy: function () {
            var ajaxType = null;
            if(ajaxType = $.ligerui.get('ajaxType')){
            	ajaxType.destroy();
            }
        },

        // 设置上级下拉框的唯一标识uuid
        setParentId: function (parentId) {
            this.parentId = parentId;
        },
        
        // 验证url
        validate: function (url) {
        	var errorMessage = null, id = 'options_url';
			if (window.parent.OptionTip[id]) {
				window.parent.OptionTip[id].destroy();
				delete window.parent.OptionTip[id];
			}
			if (url === undefined || url === null || url === "") {
				errorMessage = '请求地址不能为空！';
			}
			if (errorMessage) {
    			window.parent.OptionTip[id] = window.parent.$.ligerDialog.tip({ id: id, title: '提示信息', content: errorMessage});
    			return false;
			} else {
				return true;
			}
        },

        // 设置数据并改变视图
        setData: function (data) {
            var url = data['url'], ajaxType = data['ajaxType'];
            if (url) {
                $('#url').val(url);
            }
            if (ajaxType) {
                $.ligerui.get('ajaxType').setValue(ajaxType);
            }
        },

        // 保存数据
        saveData: function () {
        	var url = $('#url').val();
            // 保存前先进行验证
            if (this.validate(url)) {
            	var data = {}, puuid = undefined;
            	data["options"] = {};
                if ((puuid = this.parentId) && puuid !=='null' ) {
                    data["puuid"] = puuid;
                }
                data["options"]["url"] = url;
                data["options"]["ajaxType"] = $.ligerui.get('ajaxType').getValue();
                window.parent.$('#datasource').val(JSON2.stringify(data));
                window.parent.$('#datasource').trigger("change");
                this.closeDialog();
            }
        },

        closeDialog: function () {
            var dialog = window.parent.OptionTip.datasourceDialog;
            dialog.close();
            // dialog.destroy();
        }
	
	});

    // 数据库配置视图
    var DatabaseOptionsView = Backbone.View.extend({
        
        config : null,
        
        type : "db",
        
        // 是否进行过格式化的标识
        fmt : false,
        
        initialize: function () {
            _.bindAll(this, 'render', 'setData', 'saveData', 'destroy', 'formatSql', 'format', 'min');
        },

        render: function (){
            var g = this;
            this.destroy();
            this.el = $('#databaseOptions-template').tmpl();
            $(this.el).ligerForm({inputWidth: 330});
            // 初始化提示信息
            $(this.el).find(".template-help").ligerTip();
            this.db = $.ligerui.get('db');
            // 初始化数据库连接名称下拉框
            this.db.set('url', Util.getContextPath() + '/report/frame/param/commonComboBox/dataSources.json');
            // 初始化系统变量列表
            this.systemVariables = $(this.el).find("#systemVariables").ligerListBox({
                isShowCheckBox: false, isMultiSelect: false, width: 188, height:254,
                url: Util.getContextPath() + "/report/frame/param/commonComboBox/systemVariables.json",
                onSuccess : function () {
                	if (g.parentId && g.parentId !== 'null') {
                		this.addItems({id: "selectedValue", text: "上级实际值"});
                		this.addItems({id: "selectedText", text: "上级显示值"});
                	}
                	$("#systemVariables tr").dblclick(function () {
                		if (typeof g.editor.getSelection() === "string" && g.editor.getSelection().length > 0) {
                			g.editor.replaceSelection("#{" + $(this).attr("value") + "}");
                		} else {
                			g.editor.replaceRange("#{" + $(this).attr("value") + "}", g.editor.getCursor(), g.editor.getCursor());
                		}
                    	return false;
                    });
                }
            });
            // 初始化工具栏
            $(this.el).find("#gridToolbar").ligerToolBar({
                items: [
                    { text: '保存', click: g.saveData, icon:'save'},
                    { line:true },
                    { text: '关闭', click: g.closeDialog, icon:'right'},
                    { line:true },
                    { text: '格式化', click: g.formatSql, icon: 'attibutes'}
                ]
            });
            $('tbody:first').children().remove();
            $('tbody:first').append(this.el);
            // 初始化代码着色编辑器
            this.editor = CodeMirror.fromTextArea($('#sql').get(0), {
                mode: "text/x-sql",
                indentWithTabs: true,
                smartIndent: true,
                lineNumbers: true,
                matchBrackets : true,
                autofocus: true,
                theme: 'eclipse'
            });
        },

        // 销毁ligeui对象
        destroy: function () {
        	var db = null;
            if(db = $.ligerui.get('db')){
                db.destroy();
            }
        },

        // 设置上级下拉框的唯一标识uuid
        setParentId: function (parentId) {
            this.parentId = parentId;
        },

        // 设置数据并改变视图
        setData: function (data) {
            var db = data['db'], sql = data['sql'], format = data['format'];
            if (db) {
                $.ligerui.get('db').setValue(db);
            }
            if (sql) {
                if (format === true) {
                    // 设置格式化标识
                    this.fmt = true;
                    this.editor.setValue(this.format(sql));
                } else {
                    this.editor.setValue(sql);
                }
            }
        },
        
        // 验证sql语句
        validate: function (sql) {
        	var errorMessage = null, id = 'options_sql';
			if (window.parent.OptionTip[id]) {
				window.parent.OptionTip[id].destroy();
				delete window.parent.OptionTip[id];
			}
			if (sql === undefined || sql === null || sql === "") {
				errorMessage = 'SQL语句不能为空！';
			} else if (!new RegExp(/\sid/).test(sql.toLowerCase())) {
                errorMessage = 'SQL语句中缺少id字段！';
            } else if (!new RegExp(/\stext/).test(sql.toLowerCase())) {
                errorMessage = 'SQL语句中缺少text字段！';
            }
			if (errorMessage) {
    			window.parent.OptionTip[id] = window.parent.$.ligerDialog.tip({ id: id, title: '提示信息', content: errorMessage});
    			return false;
			} else {
				return true;
			}
        },

        // 保存数据
        saveData: function () {
        	var sql = this.fmt ? this.min(this.editor.getValue()) : this.editor.getValue();
        	// 保存前先进行验证
            if (this.validate(sql)) {
        		var data = {}, puuid = undefined;
        		data["options"] = {};
        		if ((puuid = this.parentId) && puuid !=='null' ) {
        			data["puuid"] = puuid;
        		}
        		data["options"]["db"] = $.ligerui.get('db').getValue();
        		data["options"]["sql"] = sql;
        		data["options"]["format"] = this.fmt;
        		window.parent.$('#datasource').val(JSON2.stringify(data));
        		window.parent.$('#datasource').trigger("change");
        		this.closeDialog();
        	}
        },
        
        // 设置格式化后的sql语句
        formatSql: function () {
            this.editor.setValue(this.format(this.editor.getValue()));
        },

        // 调用接口返回格式化后的sql语句
        format: function (sql) {
            if (sql === undefined || sql === null || sql === "") {
                return "";
            } else {
                this.fmt = true;
                return $.format(sql, {method: 'sql'});
            }
        },

        // 最小化sql语句
        min: function (sql) {
            return $.format(sql, {method: 'sqlmin'});
        },

        closeDialog: function () {
            var dialog = window.parent.OptionTip.datasourceDialog;
            dialog.close();
        }
    }); 

    // 数据树数据源设置视图
	var OptionsView = Backbone.View.extend({

		el : $('#selectOptionsDesigner'),

        _defaultOptionType : 'data',

        _deafaultCorrelated : 'null',

		initialize: function () {
            _.bindAll(this, 'render', 'renderTbody');
            var tmpl = $( "#selctOptionsForm-template" ).tmpl();
            $(this.el).html(tmpl);
            this.$('#selectOptionsForm').ligerForm({inputWidth: 190});
            this.optionsTypeSelect = $.ligerui.get('options_type');
            this.correlatedSelect = $.ligerui.get('correlated');
            this.render();
        },

        render: function () {
            var g = this, options = window.parent.$('#datasource').val(), config = null, op = null;
            // 初始化类型下拉框数据
            this.optionsTypeSelect.set('data', this.createTypeOptions());
            // 初始化上级下拉框数据
            this.correlatedSelect.set('data', this.createParentOptions());
            // 如果上级窗体中已有数据源设置
            if (options) {
        		config = JSON2.parse(options);
                op = config['options'];
                if (op['data']) {
                    this.optionsTypeSelect.selectValue('data');
                } else if (op['url']) {
                    this.optionsTypeSelect.selectValue('url');
                } else if (op['sql']) {
                    this.optionsTypeSelect.selectValue('sql');
                }
                if (config['puuid']) {
                    this.correlatedSelect.selectValue(config['puuid']);
                } else {
                    this.correlatedSelect.selectValue('null');
                }
                this.renderTbody(op);
            } else {
                this.optionsTypeSelect.selectValue(this._defaultOptionType);
                this.correlatedSelect.selectValue(this._deafaultCorrelated);
                this.renderTbody();
            }
            this.optionsTypeSelect.set('onSelected', function () {
                g.correlatedSelect.selectValue('null');
            });
            this.correlatedSelect.set('onSelected', function () {
                g.renderTbody();
            });
        },

        createTypeOptions: function () {
            return [{id: 'data', text: '自定义'}, {id: 'url', text: '超链接'}, {id: 'sql', text: '数据库'}];
        },

        // 创建上级下拉框的唯一标志对象
        createParentOptions: function () {
        	var selectComponentes = window.parent.app.template._getComponentesWhere({type : 'select'});
        	var selectedCid = window.parent.$('.li-selected :input[cid]').attr('cid');
        	var result = [];
        	result.push({id : "null", text : "无"});
        	_.each(selectComponentes, function (comp){
        		if (selectedCid !== comp.cid) {
        			result.push({id : comp.id, text : comp.get('name')});
        		}
        	});
        	return result;
        },

        renderTbody: function (data) {
        	var optionsType = this.optionsTypeSelect.getValue(), appendHtml = '';
        	if (optionsType === 'data') {
        		var dov = new DataOptionsView();
                if (data) {
                    dov.setData(data);
                }
                dov.setParentId(this.correlatedSelect.getValue());
        		appendHtml = dov.render().el;
                this.$('tbody').children().remove();
                this.$('tbody').append(appendHtml);
        	} else if (optionsType === 'url') {
                var rov = new RemoteOptionsView();
                rov.setParentId(this.correlatedSelect.getValue());
                appendHtml = rov.render().el;
                this.$('tbody').children().remove();
                this.$('tbody').append(appendHtml);
                if (data) {
                    rov.setData(data);
                }
        	} else if (optionsType === 'sql') {
                var dov = new DatabaseOptionsView();
                dov.setParentId(this.correlatedSelect.getValue());
                dov.render();
                if (data) {
                    dov.setData(data);
                }
        	}
        }
	});

	return {
		OptionsView : OptionsView
	};
});