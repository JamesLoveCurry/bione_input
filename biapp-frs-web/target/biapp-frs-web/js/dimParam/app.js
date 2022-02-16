/*!
 * 参数模版中设计器的视图对象
 * 
 */
define(['jquery', 'JSON2', 'Underscore', 'Backbone', 'BackboneUndo', 'template/model', 'dimParam/layout', 'template/convertor', 'dimParam/effects', 'template/util', 'template/validator'], function ($, JSON2, _, Backbone, BackboneUndo, Model, Layout, Convertor, Effects, Util, Validator) {
    
	// 设置jquery.format验证
	//$.metadata.setType("attr", "validate");
	
	window.AppTip = {};
	
	var treeUrl = "/report/frame/design/paramtmp/getTreeDimItems?dimTypeNo=";
	var selectUrl = "/report/frame/design/paramtmp/getSelectDimItems?dimTypeNo=";
	var orgUrl = "/report/frame/datashow/idx/orgTree";

    // 设计区的视图对象
    var AppView = Backbone.View.extend({

        el: $('#designerCanvas'),
        
        isIE : false,

        initialize: function () {
        	/*if(navigator.appName == "Microsoft Internet Explorer" && navigator.appVersion.match(/6./i)=="6."
        		|| navigator.appName == "Microsoft Internet Explorer" && navigator.appVersion.match(/7./i)=="7."){
        		this.isIE = true;
        	}*/
        	if(!+'\v1' && !'1'[0]){
        		this.isIE = true;
        		//alert("ie6或ie7");
        	} 
            _.bindAll(this, 'render');
            this.template = new Model.Template();
            this.componentes = this.template._getComponentes();
            /*// 创建undo管理对象，并开启纪录
            this.undoManager = new Backbone.UndoManager();
            this.undoManager.register(this.componentes);
            this.undoManager.startTracking();
            this.listenTo(this.componentes, 'remove', this.render);
            this.listenTo(this.componentes, 'reset', this.render);*/
            this.listenTo(this.componentes, 'add', this.render);
            this.listenTo(this.componentes, 'remove', this.render);
            _.bindAll(this, 'render', 'initData');
            // 设置全局对象，方便其他模块调用
            window.app = this;
            this.initData();
            this.render();
        },

        // 本系统修改参数模版的时候，根据当前弹出框上级查找paramJson中的值，初始化数据。        
        initData: function () {
        	if (window.$paramJson && window.$paramJson.length > 0 ) {
        		var models = JSON2.parse(window.$paramJson);
        		if(models != null && models.length > 0){
        			this.componentes.add(models);
        			
        		}
        		
        		
        	}
        	
        },

        render: function () {
            var g = this;
            // 先删除设计区的所有控件
            this.$el.children().remove();
            // 重新设置设计区的高度
            this.$el.height($('#center').height());
            // 判断如果没有添加任何控件，则需要特殊处理
//            //wjx20150531修改  防止用户去除所有的日期维度
//            leftTreeObj.setting.callback.beforeCheck = function( treeId, treeNode){
//            	if(treeNode.checked){
//            		for(var i in g.componentes.models){
//	            		if(g.componentes.models[i].attributes.name == treeNode.id){
//	            			if(!(g.componentes.models[i].attributes.name == "DATE" )||g.componentes.models[i].attributes.name == "MONTH"||
//	       						 g.componentes.models[i].attributes.name == "YEAR"||g.componentes.models[i].attributes.name == "QUARTER"){
//	            				 return true;
//	       				 	}
//	       				 	else{
//	       				 		var dnum=0;
//	       				 		for(var i in g.template.componentes.models){
//	       				 			if(g.template.componentes.models[i].attributes.name == "DATE")
//	       				 				dnum++;
//	       				 		}
//	       				 		if(dnum==1){
//	       				 			BIONE.tip("这是最后一个日期类型的控件，无法删除");
//	       				 			return false;
//	       				 			
//	       				 		}
//		       		            else{
//		       		            	return true;
//		       		            }
//	       				 	}
//	            		}
//            		}
//            	}
//            }
            //修改完毕
        	leftTreeObj.setting.callback.onCheck = function(event, treeId, treeNode){
            	if(treeNode.checked){
            		
            		if(treeNode.id == "DATE"){
            			g.template.addComponent("date", {display : treeNode.text, name : treeNode.id, required : "true", format : "yyyyMMdd"});
            		}
//            		//wjx20150529修改  添加了年、季、月三种日期维度
//            		else if(treeNode.id == "QUARTER"){
//            			g.template.addComponent("select", {display : treeNode.text, name : treeNode.id, required : "true", 
//            				datasource: "{\"options\":{\"data\":[{\"id\":\"01\",\"text\":\"第一季度\"},{\"id\":\"02\",\"text\":\"第二季度\"},{\"text\":\"第三季度\",\"id\":\"03\"},{\"text\":\"第四季度\",\"id\":\"04\"}]}}"
//            			});
//            		}else if(treeNode.id == "MONTH"){
//            			g.template.addComponent("select", {display : treeNode.text, name : treeNode.id, required : "true", 
//            				datasource: "{\"options\":{\"data\":[{\"id\":\"01\",\"text\":\"1月\"},{\"id\":\"02\",\"text\":\"2月\"},{\"id\":\"03\",\"text\":\"3月\"},{\"id\":\"04\",\"text\":\"4月\"},{\"id\":\"05\",\"text\":\"5月\"},{\"id\":\"06\",\"text\":\"6月\"},{\"id\":\"07\",\"text\":\"7月\"},{\"id\":\"08\",\"text\":\"8月\"},{\"id\":\"09\",\"text\":\"9月\"},{\"id\":\"10\",\"text\":\"10月\"},{\"id\":\"11\",\"text\":\"11月\"},{\"id\":\"12\",\"text\":\"12月\"}]}}"
//            			});
//            		}else if(treeNode.id == "YEAR"){
//            			g.template.addComponent("select", {display : treeNode.text, name : treeNode.id, required : "true", 
//            				datasource: "{\"options\":{\"url\":\"/report/frame/design/paramtmp/getYearItems\",\"ajaxType\":\"post\"}}"
//            			});
//            		}
//           		//修改完毕
            		else if(treeNode.id == "ORG"){
            		
            			g.template.addComponent("popup", {display : treeNode.text, name : treeNode.id,
            				dialogWidth : "400", dialogHeight : "500",
            				required : "false",
            				datasource: "{\"options\":{\"url\":\"" + orgUrl + "\",\"ajaxType\":\"post\"}}"});
            		
            		}else if(treeNode.id == "CURRENCY"){
            		
            			g.template.addComponent("select", {display : "币种", name : "CURRENCY",
                				required : "true",
                				datasource: "{\"options\":{\"url\":\"" + selectUrl + treeNode.id +"\",\"ajaxType\":\"post\"}}",
                				value: "CNY"
                		});
            		
            		}else if(treeNode.params.dimTypeStruct == "01"){
            		
            			g.template.addComponent("select", {display : treeNode.text.length > 10 ? (treeNode.text.substring(0,10) + "..."):(treeNode.text), name : treeNode.id,
            				required : "false",
            				datasource: "{\"options\":{\"url\":\"" + selectUrl + treeNode.id +"\",\"ajaxType\":\"post\"}}",
            				value: ""
            			});
            				
            		
            		}else if(treeNode.params.dimTypeStruct == "02"){
            		
            			g.template.addComponent("popup", {display : treeNode.text.length > 10 ? (treeNode.text.substring(0,10) + "..."):(treeNode.text), name : treeNode.id,
            				required : "false",
            				dialogWidth : "400", dialogHeight : "500",
            				datasource: "{\"options\":{\"url\":\"" + treeUrl + treeNode.id +"\",\"ajaxType\":\"post\"}}"});
            		}
            	}else{
            		for(var i in g.componentes.models){
            			if(g.componentes.models[i].attributes.name == treeNode.id){
	            			g.template.removeComponent(g.componentes.models[i]);
	        				break;
            			}
            		}
            	}
            };//end onCheck
            
            if (this.componentes.length == 0) {
            	//wjx20150529修改  添加了年、季、月三种日期维度 
//            	if(leftTreeObj.getNodeByParam("id", "YEAR")!=null&&leftTreeObj.getNodeByParam("id", "YEAR").checked){
//            		g.template.addComponent("select", {display : treeNode.text, name : treeNode.id, required : "true", 
//        				datasource: "{\"options\":{\"url\":\"/report/frame/design/paramtmp/getYearItems\",\"ajaxType\":\"post\"}}"
//        			})
//        		}
//            	if(leftTreeObj.getNodeByParam("id", "MONTH")!=null&&leftTreeObj.getNodeByParam("id", "MONTH").checked){
//            		g.template.addComponent("select", {display : treeNode.text, name : treeNode.id, required : "true", 
//        				datasource: "{\"options\":{\"data\":[{\"id\":\"01\",\"text\":\"1月\"},{\"id\":\"02\",\"text\":\"2月\"},{\"id\":\"03\",\"text\":\"3月\"},{\"id\":\"04\",\"text\":\"4月\"},{\"id\":\"05\",\"text\":\"5月\"},{\"id\":\"06\",\"text\":\"6月\"},{\"id\":\"07\",\"text\":\"7月\"},{\"id\":\"08\",\"text\":\"8月\"},{\"id\":\"09\",\"text\":\"9月\"},{\"id\":\"10\",\"text\":\"10月\"},{\"id\":\"11\",\"text\":\"11月\"},{\"id\":\"12\",\"text\":\"12月\"}]}}"
//        			});
//            	}
//            	if(leftTreeObj.getNodeByParam("id", "QUARTER")!=null&&leftTreeObj.getNodeByParam("id", "MONTH").checked){
//            		g.template.addComponent("select", {display : treeNode.text, name : treeNode.id, required : "true", 
//        				datasource: "{\"options\":{\"data\":[{\"id\":\"01\",\"text\":\"第一季度\"},{\"id\":\"02\",\"text\":\"第二季度\"},{\"text\":\"第三季度\",\"id\":\"03\"},{\"text\":\"第四季度\",\"id\":\"04\"}]}}"
//        			});
//            	}
            	if(leftTreeObj.getNodeByParam("id", "DATE")!=null&&leftTreeObj.getNodeByParam("id", "DATE").checked){
            		g.template.addComponent("date", {display : "数据日期", name : "DATE", required : "true", format : "yyyyMMdd"});
            	}
            	if(leftTreeObj.getNodeByParam("id", "ORG")!=null&&leftTreeObj.getNodeByParam("id", "ORG").checked){
            		g.template.addComponent("popup", {display : "机构", name : "ORG",
            			dialogWidth : "400", dialogHeight : "500",
            			required : "false",
            			datasource: "{\"options\":{\"url\":\"" + orgUrl + "\",\"ajaxType\":\"post\"}}"});
            	}
            	/*if(leftTreeObj.getNodeByParam("id", "CURRENCY")!=null&&leftTreeObj.getNodeByParam("id", "CURRENCY").checked){
            		g.template.addComponent("popup", {display : "币种", name : "CURRENCY",
            			dialogWidth : "400", dialogHeight : "500",
            			required : "true",
            			datasource: "{\"options\":{\"url\":\"" + treeUrl + "CURRENCY" +"\",\"ajaxType\":\"post\"}}"});
            	
            	}*/
            	//修改完毕
            	
            } else {
                //每两个为一组对组件添加换行属性 暂时有问题，先不添加 改为自动换行
            	/*for(var i in g.componentes.models){
        			if(i%2 == 0 && i != 0){
        				g.componentes.models[i].attributes.newline = "true";
        			}else{
        				g.componentes.models[i].attributes.newline = "false";
        			}
        		}*/
            	if(this.isIE){
            		for(var i in g.componentes.models){
            			if(i%2 == 0 && i != 0){
            				g.componentes.models[i].attributes.newline = "true";
            			}else{
            				g.componentes.models[i].attributes.newline = "false";
            			}
            		}
            		} 
            	// 在设计区添加转换完ligerForm的UI对象
                $('<div id="wraper" ></div>').appendTo(this.$el).ligerForm(Convertor.convert(this.componentes.toFormPorperties(), 'design'));
                // 设置ligerForm中的控件可以通过拖拽修改位置
                Effects.createLigerFormSortable( function (event, ui) {
                	
                    var model = g.template.getComponent(ui.item.find("[cid]").attr("cid"));
                    if(model === undefined) return;
                    var index = $(".l-fieldcontainer").index(ui.item);//必须要在删除前计算索引位置，否则会返回-1
                    var cloneModel = g.template.moveComponent(model);
                    g.template.removeComponent(model);
                    g.template.insertComponent(cloneModel, index);
                });
                // 设置每个参数的UI事件
                Effects.createLiEvent(this.$el, this.template);
                // 重新选择刷新界面前选择的参数
                Effects.selectHistoryLi(this.$el);
            }
            return this;
        }
    });

    return {
        AppView : AppView
    };
});