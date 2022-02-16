/*!
 * 参数模版中参数属性定义文件和通用工具方法
 * 
 */
define(function(){

    // 缓存对象
    var componentType = {};

    var refreshStates = {};

    // 参数定义
	var templateProperties = [{
        componentId: 'text',
        componentName: '文本框',
        properties: [{
            id: 'display',
            name: '显示文字',
            def: '文本框',
            type: 'widget',
            isRefresh: true,
            editor: 'text'
        }, {
            id: 'name',
            name: '唯一标识',
            type: 'widget',
            isRefresh: false,
            editor: 'text',
            validate: {
                required: true
            }
        }, {
            id: 'width',
            name: '宽度',
            nullText: 180,
            type: 'widget',
            isRefresh: true,
            editor: 'text',
            validate: {
                required: true,
                digits: true
            }
        }, {
            id: 'newline',
            name: '换行',
            def: 'false',
            type: 'widget',
            isRefresh: true,
            editor: 'select',
            options: [{id: 'true', text: '是'}, {id: 'false', text: '否'}]
        }, {
            id: 'disabled',
            name: '禁用',
            def: 'null', //建议给布尔值类型的字段设置默认值
            type: 'options',
            isRefresh: false,
            editor: 'select',
            options: [{id: 'disabled', text: '是'}, {id: 'null', text: '否'}]
        }, {
            id: 'value',
            name: '默认值',
            type: 'options',
            isRefresh: false,
            editor: 'text'
        }, {
            id: 'validate',
            name: '验证',
            type: 'validate',
            isRefresh: false,
            editor: 'popup'
        }, {
            id: 'key',
            type: 'attr',
            name : '字段',
            show : false,
            isRefresh: false,
            editor: 'text'
        }]
    }, {
        componentId: 'hidden',
        componentName: '隐藏域',
        properties: [{
            id: 'name',
            name: '唯一标识',
            def: '隐藏域',
            type: 'widget',
            isRefresh: false,
            editor: 'text'
        }, {
            id: 'value',
            name: '默认值',
            type: 'options',
            isRefresh: false,
            editor: 'text'
        }, {
            id: 'key',
            type: 'attr',
            name : '字段',
            show : false,
            isRefresh: false,
            editor: 'text'
        }]
    }, {
        componentId: 'date',
        componentName: '日期框',
        properties: [{
            id: 'display',
            name: '显示文字',
            def: '日期框',
            type: 'widget',
            isRefresh: true,
            editor: 'text'
        },{
            id: 'name',
            name: '唯一标识',
            type: 'widget',
            isRefresh: false,
            editor: 'text',
            validate: {
                required: true
            }
        },{
            id: 'width',
            name: '宽度',
            nullText: 180,
            type: 'widget',
            isRefresh: true,
            editor: 'text',
            validate: {
                required: true,
                digits: true
            }
        },{
            id: 'newline',
            name: '换行',
            def: 'false',
            type: 'widget',
            isRefresh: true,
            editor: 'select',
            options: [{id: 'true', text: '是'}, {id: 'false', text: '否'}]
        },{
            id: 'disabled',
            name: '禁用',
            def: 'null', //建议给布尔值类型的字段设置默认值
            type: 'options',
            isRefresh: false,
            editor: 'select',
            options: [{id: 'disabled', text: '是'}, {id: 'null', text: '否'}]
        },{
            id: 'value',
            name: '默认值',
            type: 'options',
            isRefresh: false,
            editor: 'text'
        },{
            id: 'showTime',
            name: '显示时间',
            def: 'false',
            type: 'options',
            isRefresh: false,
            editor: 'select',
            options: [{id: 'true', text: '是'}, {id: 'false', text: '否'}]
        },{
            id: 'format',
            name: '格式化',
            nullText: 'yyyy-MM-dd hh:mm',
            type: 'options',
            isRefresh: false,
            editor: 'text'
        },{
            id: 'required',
            name: '必填验证',
            def: 'false', //建议给布尔值类型的字段设置默认值
            type: 'required',
            isRefresh: false,
            editor: 'select',
            options: [{id: 'true', text: '是'}, {id: 'false', text: '否'}]
        },{
            id: 'key',
            type: 'attr',
            name : '字段',
            show : false,
            isRefresh: false,
            editor: 'text'
        },{
        	id: 'showType',
        	type : 'options',
        	name : '显示方式',
        	show : false,
        	isRefresh : false,
        	editor : "select",
        	options: [{id: 'date', text: '日期'}, {id: 'month', text: '月份'}, {id: 'year', text: '年份'}]
        }]
    }, {
        componentId: 'select',
        componentName: '下拉框',
        properties: [{
            id: 'display',
            name: '显示文字',
            def: '下拉框',
            type: 'widget',
            isRefresh: true,
            editor: 'text'
        }, {
            id: 'name',
            name: '唯一标识',
            type: 'widget',
            isRefresh: false,
            editor: 'text',
            validate: {
                required: true
            }
        }, 
//        {
//            id: 'comboboxName',
//            name: '显示名称',
//            type: 'widget',
//            isRefresh: false,
//            editor: 'text',
//            validate: {
//                required: true
//            }
//        }, 
        // {//ligerForm会把name属性复制给valueFieldID当隐藏域的ID使用
        //     id: 'valueFieldID',
        //     name: '隐藏域的ID',
        //     type: 'options',
        //     isRefresh: false,
        //     editor: 'text'
        // }, 
        {
            id: 'width',
            name: '宽度',
            nullText: 180,
            type: 'widget',
            isRefresh: true,
            editor: 'text',
            validate: {
                required: true,
                digits: true
            }
        }, {
            id: 'newline',
            name: '换行',
            def: 'false',
            type: 'widget',
            isRefresh: true,
            editor: 'select',
            options: [{id: 'true', text: '是'}, {id: 'false', text: '否'}]
        }, {
            id: 'disabled',
            name: '禁用',
            def: 'null', //建议给布尔值类型的字段设置默认值
            type: 'options',
            isRefresh: false,
            editor: 'select',
            options: [{id: 'disabled', text: '是'}, {id: 'null', text: '否'}]
        }, {
            id: 'value',
            name: '默认值',
            type: 'options',
            isRefresh: false,
            editor: 'text'
        }, 
        {
            id: 'isMultiSelect',
            name: '支持复选',
            def: 'false', //建议给布尔值类型的字段设置默认值
            type: 'options',
            isRefresh: false,
            editor: 'select',
            options: [{id: 'true', text: '是'}, {id: 'false', text: '否'}]
        },
        {
            id: 'datasource',
            name: '数据源',
            type: 'options',
            isRefresh: false,
            editor: 'popup'
        }, {
            id: 'required',
            name: '必填验证',
            def: 'false', //建议给布尔值类型的字段设置默认值
            type: 'required',
            isRefresh: false,
            editor: 'select',
            options: [{id: 'true', text: '是'}, {id: 'false', text: '否'}]
        }, {
        	id: 'selectBoxWidth',
        	name: '下拉框宽度',
        	type: 'options',
        	editor: 'text',
        	nullText: 180,
        	validate: {
                digits: true
            }
        },{
        	id: 'selectBoxHeight',
        	name: '下拉框高度',
        	type: 'options',
        	nullText: 180,
        	editor: 'text',
        	validate: {
                digits: true
            }
        },{
            id: 'key',
            type: 'attr',
            name : '字段',
            show : false,
            isRefresh: false,
            editor: 'text'
        },{
            id: 'dataFilter',
            name: '联想过滤',
            def: "true",
            type: 'options',
            isRefresh: false,
            editor: 'select',
            options: [{id: 'true', text: '是'}, {id: 'false', text: '否'}]
        },{
        	id: 'dimTypeNo',
        	type : 'attr',
        	name : '维度类型编号',
        	show : true,
        	isRefresh : false,
        	editor : "text"
        }]
    }, {
        componentId: 'combobox',
        componentName: '数据树',
        properties: [{
            id: 'display',
            name: '显示文字',
            def: '数据树',
            type: 'widget',
            isRefresh: true,
            editor: 'text'
        }, {
            id: 'name',
            name: '唯一标识',
            type: 'widget',
            isRefresh: false,
            editor: 'text',
            validate: {
                required: true
            }
        }, 
//        {
//            id: 'comboboxName',
//            name: '显示名称',
//            type: 'widget',
//            isRefresh: false,
//            editor: 'text',
//            validate: {
//                required: true
//            }
//        }, 
        {
            id: 'width',
            name: '宽度',
            nullText: 180,
            type: 'widget',
            isRefresh: true,
            editor: 'text',
            validate: {
                required: true,
                digits: true
            }
        }, {
            id: 'newline',
            name: '换行',
            def: 'false',
            type: 'widget',
            isRefresh: true,
            editor: 'select',
            options: [{id: 'true', text: '是'}, {id: 'false', text: '否'}]
        }, {
            id: 'disabled',
            name: '禁用',
            def: 'null', //建议给布尔值类型的字段设置默认值
            type: 'options',
            isRefresh: false,
            editor: 'select',
            options: [{id: 'disabled', text: '是'}, {id: 'null', text: '否'}]
        }, 
        {
            id: 'tree_checkbox',
            name: '支持复选',
            def: 'true',
            type: 'options',
            isRefresh: false,
            editor: 'select',
            options: [{id: 'true', text: '是'}, {id: 'false', text: '否'}]
        }, {
            id: 'datasource',
            name: '数据源',
            type: 'options',
            isRefresh: false,
            editor: 'popup'
        }, {
            id: 'required',
            name: '必填验证',
            def: 'false', //建议给布尔值类型的字段设置默认值
            type: 'required',
            isRefresh: false,
            editor: 'select',
            options: [{id: 'true', text: '是'}, {id: 'false', text: '否'}]
        },{
	    	id: 'selectBoxWidth',
	    	name: '下拉框宽度',
	    	type: 'options',
	    	editor: 'text',
	    	nullText: 180,
	    	validate: {
	            digits: true
	        }
	    },{
	    	id: 'selectBoxHeight',
	    	name: '下拉框高度',
	    	type: 'options',
	    	nullText: 180,
	    	editor: 'text',
	    	validate: {
	            digits: true
	        }
	    },{
            id: 'key',
            type: 'attr',
            name : '字段',
            show : false,
            isRefresh: false,
            editor: 'text'
        },{
        	id: 'dimTypeNo',
        	type : 'attr',
        	name : '维度类型编号',
        	show : true,
        	isRefresh : false,
        	editor : "text"
        }]
    } , {
        componentId: 'popup',
        componentName: '弹出树',
        properties: [{
            id: 'display',
            name: '显示文字',
            def: '弹出树',
            type: 'widget',
            isRefresh: true,
            editor: 'text'
        }, {
            id: 'name',
            name: '唯一标识',
            type: 'widget',
            isRefresh: false,
            editor: 'text',
            validate: {
                required: true
            }
        }, {
            id: 'width',
            name: '文本框宽',
            nullText: 180,
            type: 'widget',
            isRefresh: true,
            editor: 'text',
            validate: {
                required: true,
                digits: true
            }
        }, {
            id: 'newline',
            name: '换行',
            def: 'false',
            type: 'widget',
            isRefresh: true,
            editor: 'select',
            options: [{id: 'true', text: '是'}, {id: 'false', text: '否'}]
        }, {
            id: 'disabled',
            name: '禁用',
            def: 'null', //建议给布尔值类型的字段设置默认值
            type: 'options',
            isRefresh: false,
            editor: 'select',
            options: [{id: 'disabled', text: '是'}, {id: 'null', text: '否'}]
        }, {
            id: 'value',
            name: '默认值',
            def: 'null',
            type: 'attr',
            isRefresh: false,
            editor: 'text',
            //options: [{id: 'null', text: '无'}, {id: '#{ORG_NO}', text: '用户所属机构'}, {id: '#{ORG_NO_FULL}', text: '机构编号(复合型)'}]
        }, {
            id: 'checkbox',
            name: '支持复选',
            def: 'true',
            type: 'tree',
            isRefresh: false,
            editor: 'select',
            options: [{id: 'true', text: '是'}, {id: 'false', text: '否'}]
        }, {
            id: 'datasource',
            name: '数据源',
            type: 'options',
            isRefresh: false,
            editor: 'popup'
        }, {
            id: 'required',
            name: '必填验证',
            def: 'false', //建议给布尔值类型的字段设置默认值
            type: 'required',
            isRefresh: false,
            editor: 'select',
            options: [{id: 'true', text: '是'}, {id: 'false', text: '否'}]
		}, {
			id : 'dialogWidth',
			name : '弹出框宽',
			nullText: 220,
			isRefresh : false,
			type: 'dialog',
            editor: 'text',
            validate: {
                required: true,
                digits: true
            }
		}, {
			id : 'dialogHeight',
			name : '弹出框高',
			nullText: 300,
			isRefresh : false,
			type: 'dialog',
            editor: 'text',
            validate: {
                required: true,
                digits: true
            }
		}, {
            id: 'key',
            type: 'attr',
            name : '字段',
            show : false,
            isRefresh: false,
            editor: 'text'
        },{
        	id: 'dimTypeNo',
        	type : 'attr',
        	name : '维度类型编号',
        	show : true,
        	isRefresh : false,
        	editor : "text"
        }]
    }, {
        componentId: 'daterange',
        componentName: '日期区间',
        properties: [{
            id: 'display',
            name: '显示文字',
            def: '日期区间',
            type: 'widget',
            isRefresh: true,
            editor: 'text'
        }, {
            id: 'name',
            name: '唯一标识',
            type: 'widget',
            editor: 'text',
            validate: {
                required: true
            }
        }, {
            id: 'width',
            name: '文本框宽',
            nullText: 180,
            type: 'widget',
            isRefresh: true,
            editor: 'text',
            validate: {
                required: true,
                digits: true
            }
        }, {
            id: 'newline',
            name: '换行',
            def: 'false',
            type: 'widget',
            isRefresh: true,
            editor: 'select',
            options: [{id: 'true', text: '是'}, {id: 'false', text: '否'}]
        }, {
            id: 'disabled',
            name: '禁用',
            def: 'null', //建议给布尔值类型的字段设置默认值
            type: 'options',
            isRefresh: false,
            editor: 'select',
            options: [{id: 'disabled', text: '是'}, {id: 'null', text: '否'}]
        },/* {
        	id : 'readonly',
        	type : 'options',
        	def : 'readonly',
        	show : false
        },*/ {
            id: 'startDateId',
            name: '开始时间',
            type: 'attr',
            show : false,
            isRefresh: false,
            editor: 'text',
            validate: {
                required: true
            }
        }, {
            id: 'endDateId',
            name: '结束时间',
            type: 'attr',
            show : false,
            isRefresh: false,
            editor: 'text',
            validate: {
                required: true
            }
        },{
            id: 'required',
            name: '必填验证',
            def: 'false', //建议给布尔值类型的字段设置默认值
            type: 'required',
            isRefresh: false,
            editor: 'select',
            options: [{id: 'true', text: '是'}, {id: 'false', text: '否'}]
		}, {
            id: 'key',
            type: 'attr',
            name : '字段',
            show : false,
            isRefresh: false,
            editor: 'text'
        }, {
        	id : 'dayRangeMax',
        	type : 'widget',
        	name : '最大范围',
        	nullText: 0,
            isRefresh: true,
            editor: 'text',
            validate: {
            	required : true,
                digits : true
            }
        }]
    },{
        componentId: 'dblin',
        componentName: '组合控件',
        properties: [{
            id: 'display',
            name: '显示文字',
            def: '组合控件',
            type: 'widget',
            isRefresh: true,
            editor: 'text'
        }, {
            id: 'name',
            name: '唯一标识',
            type: 'widget',
            isRefresh: false,
            editor: 'text',
            validate: {
                required: true
            }
        }, 
        {
            id: 'width',
            name: '宽度',
            nullText: 180,
            type: 'widget',
            isRefresh: true,
            editor: 'text',
            validate: {
                required: true,
                digits: true
            }
        }, {
            id: 'newline',
            name: '换行',
            def: 'false',
            type: 'widget',
            isRefresh: true,
            editor: 'select',
            options: [{id: 'true', text: '是'}, {id: 'false', text: '否'}]
        }, {
            id: 'disabled',
            name: '禁用',
            def: 'null', //建议给布尔值类型的字段设置默认值
            type: 'options',
            isRefresh: false,
            editor: 'select',
            options: [{id: 'disabled', text: '是'}, {id: 'null', text: '否'}]
        }, {
            id: 'value',
            name: '默认值',
            type: 'options',
            isRefresh: false,
            editor: 'text'
        }, 
        {
            id: 'isMultiSelect',
            name: '支持复选',
            def: 'false', //建议给布尔值类型的字段设置默认值
            type: 'options',
            isRefresh: false,
            editor: 'select',
            options: [{id: 'true', text: '是'}, {id: 'false', text: '否'}]
        },{
        	id : 'inputName',
        	type : 'widget',
        	show : false
        },{
            id: 'datasource',
            name: '数据源',
            type: 'options',
            isRefresh: false,
            editor: 'popup'
        }, {
            id: 'validate',
            name: '验证',
            type: 'validate',
            isRefresh: false,
            editor: 'popup'
        }, {
            id: 'key',
            type: 'attr',
            name : '字段',
            show : false,
            isRefresh: false,
            editor: 'text'
        },{
            id: 'oper',
            type: 'attr',
            name : '操作',
            show : false,
            isRefresh: false,
            editor: 'text'
        }]
    },{
        componentId: 'textrange',
        componentName: '文本区间',
        properties: [{
            id: 'display',
            name: '显示文字',
            def: '文本区间',
            type: 'widget',
            isRefresh: true,
            editor: 'text'
        }, {
            id: 'inputName',
            name: '区间名称',
            type: 'attr',
            editor: 'text',
            /*validate: {
                required: true
            },*/
            isRefresh: false,
            show : false
        },{
            id: 'name',
            name: '唯一标识',
            type: 'widget',
            isRefresh: false,
            editor: 'text',
            validate: {
                required: true
            }
        }, {
            id: 'width',
            name: '宽度',
            nullText: 180,
            type: 'widget',
            isRefresh: true,
            editor: 'text',
            validate: {
                required: true,
                digits: true
            }
        }, {
            id: 'newline',
            name: '换行',
            def: 'false',
            type: 'widget',
            isRefresh: true,
            editor: 'select',
            options: [{id: 'true', text: '是'}, {id: 'false', text: '否'}]
        }, {
            id: 'disabled',
            name: '禁用',
            def: 'null', //建议给布尔值类型的字段设置默认值
            type: 'options',
            isRefresh: false,
            editor: 'select',
            options: [{id: 'disabled', text: '是'}, {id: 'null', text: '否'}]
        }, {
            id: 'value',
            name: '默认值',
            type: 'options',
            isRefresh: false,
            editor: 'text'
        }, {
            id: 'validate',
            name: '验证',
            type: 'validate',
            isRefresh: false,
            editor: 'popup'
        }, {
            id: 'key',
            type: 'attr',
            name : '字段',
            show : false,
            isRefresh: false,
            editor: 'text'
        }]
    }];

    // 获取当前componentId类型的定义对象
    function getDefinition (componentId) {
        var i = 0;
        for (i = 0; i < templateProperties.length; i++) {
            if (templateProperties[i]['componentId'] === componentId) {
                return templateProperties[i];
            }
        }
    };

    // 返回指定componentId类型的属性field类型对象，格式如：{widget:[], html: []}
    function getComponentType (componentId) {
        var properties, p , i;
        if (componentType[componentId] === undefined) {
            properties = getDefinition(componentId)['properties'];
            p = {};
            for (i = 0 ; i < properties.length; i++) {
                if (p[properties[i]['type']] === undefined) {
                    p[properties[i]['type']] = [];
                }
                p[properties[i]['type']].push(properties[i]['id']);
            }
            componentType[componentId] = p;
        }   
        return componentType[componentId];
    };

    // 返回指定componentId类型的属性刷新状态对象，格式如{id: true, name: false}
    function getRefreshStates (componentId) {
        var properties, p , i;
        if (refreshStates[componentId] === undefined) {
            properties = getDefinition(componentId)['properties'];
            p = {};
            for (i = 0 ; i < properties.length; i++) {
                p[properties[i]['id']] = properties[i]['isRefresh'];
            }
            refreshStates[componentId] = p;
        }   
        return refreshStates[componentId];
    };

    return {
        getDefinition: getDefinition,
        getComponentType: getComponentType,
        getRefreshStates: getRefreshStates
    };
});