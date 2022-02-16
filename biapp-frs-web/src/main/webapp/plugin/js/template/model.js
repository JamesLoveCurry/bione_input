/*!
 * 参数模版中模型定义模块
 * 
 */
define(['jquery', 'JSON2', 'Underscore', 'Backbone', 'UUID', '../template/property'], function ($, JSON2, _, Backbone, UUID, Property) {
    
    // 参数组件模型
    var Component = Backbone.Model.extend({

        config: null,

        // 纪录其属性刷新状态的对象
        refreshStates: {},

        initialize: function (options) {
            var type = options.type, i = 0;
            this.config = Property.getDefinition(type);
            this.refreshStates = Property.getRefreshStates(type);
            this.setInitDefaultValue();
            // 根据property文件定义的初始值对未初始化的属性进行初始化
            for (i = 0; i < this.config["properties"].length; i++) {
                if (this.get(this.config["properties"][i].id) === undefined && this.config["properties"][i].def != undefined) {
                    this.set(this.config["properties"][i].id, this.config["properties"][i].def);
                }
            }
        },

        // 设置初始值
        setInitDefaultValue: function(){
            if (!this.get("id")) {
            	this.set("id", UUID.v1());
            }
            if (this.get("name") === undefined) {
            	this.set("name", this.generateName());
            } else {
                // 如果已经有唯一标识，则纪录下来
            	Component.ids[this.cid] = this.get("name");
            }
            
            if((this.get("type")) == "daterange"){
            	this.set("startDateId", this.get("name") +　"_begin");
            	this.set("endDateId", this.get("name") + "_end");
            }
            if((this.get("type")) == 'dblin'){
            	this.set("inputName", this.get("name") +　"_text");
            }
            if((this.get("type")) == 'textrange'){
            	this.set("inputName", this.get("name") +　"_text");
            }
            
            
//            if ((this.get("type") === "select" || this.get("type") === "combobox") && this.get("comboboxName") === undefined) {
//            	this.set("comboboxName", this.get("name") + "Name");
//            }
        },
        
        // 重设初始值
        resetInitDefaultValue: function(){
            this.set("id", UUID.v1());
            this.set("name", this.generateName());
            if((this.get("type")) == "daterange"){
            	this.set("startDateId", this.get("name") +　"_begin");
            	this.set("endDateId", this.get("name") + "_end");
            }
            if((this.get("type")) == 'dblin'){
            	this.set("inputName", this.get("name") +　"_text");
            }
            if((this.get("type")) == 'textrange'){
            	this.set("inputName", this.get("name") +　"_text");
            }
//            if ((this.get("type") === "select" || this.get("type") === "combobox") && this.get("comboboxName") === undefined) {
//            	this.set("comboboxName", this.get("name") + "Name");
//            }
        },

        // 设置值的时候进行验证，如果不通过则返回提示信息
        validate: function (attrs) {
            if (attrs.name === "") {
                return "唯一标识不能为空！";
            }
            if (Component.ids[this.cid] !== attrs.name && _.contains(_.values(Component.ids), attrs.name)) {
                return "唯一标识设置为" + attrs.name + "时，导致唯一标识重复！";
            }
            if (window.AppTip[this.get("name")]) {
            	var tip = window.AppTip[this.get("name")];
            	delete window.AppTip[this.get("name")];
            	return tip;
            }
        },
        
        // 随即生成唯一标识
        generateName: function () {
        	var name = null;
        	do{
        		name = ("component" + Component.index++);
        	} while(_.contains(_.values(Component.ids), name));
        	Component.ids[this.cid] = name;
        	return name;
        },

        // 转换成属性对象
        toFormPorperties: function () {
            var obj = {};
            this.setDefaultValue(obj);
            this.setModelValue(obj);
            return obj;
        },

        // 设置特殊处理的值
        setDefaultValue: function (obj) {
            obj['type'] = this.config['componentId'];
            obj['display'] = this.config['componentName'];
            obj['cid'] = this.cid;
        },

        // 设置模型中的值
        setModelValue: function (obj) {
            var key = null, attrs = this.attributes;    
            for(key in attrs){
                obj[key] = attrs[key];
            }
        },

        // 根据property中的定义转换成LigerForm属性对象
        getAttrs: function () {
            var properties = this.config["properties"], attrs = [], i = 0, val;
            for(i = 0 ; i < properties.length ; i++){
            	if(false != properties[i]["show"]){
	                var attr = {};
	                attr["display"] = properties[i]["name"];
	                attr["name"] = properties[i]["id"];
	                attr["editor"] = properties[i]["editor"];
	                attr["nullText"] = properties[i]["nullText"] === undefined ? "" : properties[i]["nullText"];
	                attr["validate"] = JSON2.stringify(properties[i]["validate"] === undefined ? {} : properties[i]["validate"]);
	                
	                attr["options"] = properties[i]["options"];
	                this.resetOptions(attr["options"], this.get(properties[i]["id"]));
	                
	                this.setValue(attr, properties[i]["id"]);
	
	                attrs.push(attr);
            	}
            }
            return attrs;
        },

        // 遍历options类型对象，进行特殊值的处理，这里仅处理了下拉框的选中状态
        resetOptions: function (options, value) {
            if (options) {
                for(var i = 0 ; i < options.length ; i++ ){
                    var option = options[i];
                    if (option["id"] == value) {
                        option["selected"] = "selected";
                    } else {
                        option["selected"] = "";
                    }
                }
            }
        },

        // 设置值
        setValue: function (attr, id) {
            var val = this.get(id) || (id === 'display' && this.config['componentName']);
            if(val){
                attr["value"] = val;
            } else {
            	attr["value"] = "";
            }
        },

        // 判断是否需要刷新
        isRefresh: function (attr) {
            return this.refreshStates[attr];
        }
    }, {index: 1, ids: {}});

    // 参数组件集合
    var Componentes = Backbone.Collection.extend({

        model: Component,

        // 转换成属性对象集合
        toFormPorperties: function(options) {
            return this.map(function(model){ return model.toFormPorperties(options); });
        }

    });

    // 参数模版模型
    var Template = Backbone.Model.extend({

        initialize: function () {
            this.componentes = new Componentes();
        },

        // 获取参数组件集合
        _getComponentes: function () {
            return this.componentes;
        },

        // 根据筛选条件获取参数组件集合
        _getComponentesWhere: function (options) {
            return this.componentes.where(options);
        },

        // 获取筛选指定条件后的参数中的属性值集合
        _getComponentesWhereAndGet: function (options, attr) {
            var comp =  this.componentes.where(options);
            if (comp) {
                if (comp.length == 1) {
                    return comp.get(attr);
                } else {
                    var results = [], i = 0;
                    for (i = 0 ; i < comp.length ; i++) {
                        results.put(comp[i].get(attr));
                    }
                    return results;
                }
            } else {
                return undefined;
            }
        },

        // 新建一个参数对象
        createComponent: function (type, compAttr) {
            var c = new Component({"type": type});
            if (compAttr) {
                c.set(compAttr);
            }
            return c;
        },

        // 根据cid返回对应的参数对象
        getComponent: function (cid) {
            return this.componentes.get(cid);
        },

        // 添加一个参数对象到参数模版对象中
        addComponent: function (type, compAttr) {
            this.componentes.add(this.createComponent(type, compAttr));
        },

        // 克隆一个参数对象
        cloneComponent: function (component) {
        	var cloneComp = component.clone();
        	cloneComp.resetInitDefaultValue();
            return cloneComp;
        },
        
        //模板设计的时候，右侧拖拽可改变位置，但生成的唯一标识会改变，将其由cloneComponent改为moveComponent，保持ID和唯一标识不变
        //方娟 2014-12-08 修改
        moveComponent : function(component){
        	var cloneComp = component.clone();
        	return cloneComp;
        },

        // 返回指定参数对象的序号
        indexOfComponent: function (component) {
            return this.componentes.indexOf(component);
        },

        // 在指定的index位置插入一个参数对象到参数模版对象中
        insertComponent: function (component, index) {
            if(index < 0){
                throw{
                    name: "Error",
                    message: "insert " + component + " to " + index + " doesn't exist "
                };
            }
            this.componentes.add(component, {at: index});
        },

        // 删除指定的参数对象
        removeComponent: function (component) {
            delete Component.ids[component.cid];
            this.componentes.remove(component);
            this._removeChildSelectDatasource(component);
        },
        
        // 对级联下拉框，对当前下拉框的子下拉框的datasource属性至为空值
        _removeChildSelectDatasource : function (component) {
        	if (component.get('type') === 'select') {
				var id = component.id, selectes = this.componentes.where({type : 'select'}), i = 0, datasource = null;
				for (i = 0; i < selectes.length; i++) {
					datasource = selectes[i].get('datasource');
					if (datasource && JSON2.parse(datasource).puuid === id) {
						selectes[i].set('datasource', '');
					}
				}
			}
        },

        // 更新cid指定的参数对象属性
        updateComponent: function (cid, compAttr) {
            this.componentes.get(cid).reset(compAttr);
        }

    });

    return {
        Component: Component,
        Componentes : Componentes,
        Template : Template
    };
});