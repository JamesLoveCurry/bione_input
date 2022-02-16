/*!
 * 参数模版中的运行时展示模块
 * 
 */
define(['daterangepicker/moment','jquery', 'JSON2', 'Underscore', 'Backbone', '../template/model', '../template/convertor', 'daterangepicker/daterangepicker',
        'Ligerui', 'LigeruiExpand', 'Bione', 'Validate', 'ValidateMessage', 'ValidateExpand', 
        'Metadata'], 
        function (moment,$, JSON2, _, Backbone, Model, Convertor,daterangepicker) {
    //设置form.validate依赖的$.metadata属性attr
	$.metadata.setType("attr", "validate");
    
	var View = Backbone.View.extend({
		
        initialize: function ($target, options) {
        	_.bindAll(this, 'render');
            this.el = $target;
            this.template = new Model.Template();
            this.componentes = this.template._getComponentes();
            //把options传递过来的初始化数据传入对象列表
            this.componentes.add(options['data']);
            this.render();
            if(typeof options.loaded =="function"){
            	options.loaded();
            }
        },
        GetJson : function($target){
        	var jsonObject = [];
        	for(var i=0;i<this.properties.length;i++){
        		
        		var name = this.properties[i]['name'];
        		var key = $target.find("#" + name).attr("key");
        		if(!key){
        			key = name;
        		}
        		if((key == "ORG" || $target.find("#" + name).val() != "") && this.properties[i]['type'] != 'textrange'){
	        		if(this.properties[i]['type'] == 'daterange'){
	        			var name = this.properties[i]['name'];
	        			var date = $target.find("input[type=text][name="+name+"]").val().split("至");
						var begin = $.trim(date[0]).split("-").join("");
						var end = $.trim(date[1]).split("-").join("");
	        			jsonObject.push({ "name" : key, "begin" : ">=", "value" : begin});
	        			jsonObject.push({ "name" : key, "end" : "<=", "value" : end});
	        			
	        		}else if(this.properties[i]['type'] == 'dblin'){
	        			if($target.find("#" + this.properties[i]['inputName']).val() != ""){
							var op = $target.find("#" + name).val();
							var value = $target.find("#" + this.properties[i]['inputName']).val();
			        		jsonObject.push({ "name" : key, "op" : op, "value" :value});
	        			}
	        		}else if(this.properties[i]['type'] == 'date'){
	        			value = $target.find("input[type=text][name="+name+"]").val().split("-").join("");
	        			jsonObject.push({ "name" : key, "value" :value});
	        		}else{
	        			
	        			if(this.properties[i]['checkbox'] == "true" || this.properties[i]['isMultiSelect'] == "true" 
	        				|| this.properties[i]['tree_checkbox'] == "true"){
	        				value = $target.find("#" + name).val().split(";");
	        			}else{
	        				value = $target.find("#" + name).val();
	        			}
	        			jsonObject.push({ "name" : key, "value" :value});
	        			
	        		}
        		}else if(this.properties[i]['type'] == 'textrange'){
        			var begin = $target.find("#" + this.properties[i]['name']).val();
        			var end = $target.find("#" + this.properties[i]['inputName']).val();
        			if(begin != "" && end == ""){
        				jsonObject.push({ "name" : key, "op" : ">=", "value" :begin});
        			}else if(begin == "" && end != ""){
        				jsonObject.push({ "name" : key, "op" : "<=", "value" :end});
        			}else if(begin != "" && end != ""){
        				if(parseFloat(begin) <= parseFloat(end)){
        					jsonObject.push({ "name" : key, "op" : ">=", "value" :begin});
        					jsonObject.push({ "name" : key, "op" : "<=", "value" :end});
        				}else{
        					jsonObject.push({ "name" : key, "op" : "<=", "value" :begin});
        					jsonObject.push({ "name" : key, "op" : ">=", "value" :end});
        				}
        			}
        		}
        	}
        	return jsonObject;
        },
        GetParam : function($target){
        	var info = "";
        	for(var i=0;i<this.properties.length;i++){
        		var name = this.properties[i]['name'];
        		var key = $target.find("#" + name).attr("key");
        		if((key == "ORG" || $target.find("#" + name).val() != "") && this.properties[i]['type'] != 'textrange'){
	        		if(this.properties[i]['type'] == 'daterange'){
	        			var name = this.properties[i]['name'];
	        			var date = $target.find("input[type=text][name="+name+"]").val().split("至");
						var begin = date[0].trim().split("-").join("");
						var end = date[1].trim().split("-").join("");
						info += begin+"-"+end+"-";
	        			
	        		}else if(this.properties[i]['type'] == 'dblin'){
	        			if($target.find("#" + this.properties[i]['inputName']).val() != ""){
	        				var value = $target.find("#" + this.properties[i]['inputName']).val();
	        				info += value+"-";
	        			}
	        		}else if(this.properties[i]['type'] == 'date'){
	        			value = $target.find("#" + name).val().split("-").join("");
	        			info += value+"-";
	        		}else{
	        			if(this.properties[i]['checkbox'] == "true" || this.properties[i]['isMultiSelect'] == "true" 
	        				|| this.properties[i]['tree_checkbox'] == "true"){
	        				value = $target.find("#" + name).parent().find("input:first").val().split(";").join("-");
	        				info += value+"-";
	        			}else{
	        				value = $target.find("#" + name).parent().find("input:first").val();
	        				info += value+"-";
	        			}
	        			
	        		}
        		}else if(this.properties[i]['type'] == 'textrange'){
        			var begin = $target.find("#" + this.properties[i]['name']).val();
        			var end = $target.find("#" + this.properties[i]['inputName']).val();
        			if(begin != "" && end == ""){
        				info += begin+"-";
        			}else if(begin == "" && end != ""){
        				info += end+"-";
        			}else if(begin != "" && end != ""){
        				if(begin <= end){
        					info += begin+"-";
        					info += end+"-";
        				}else{
        					info += begin+"-";
        					info += end+"-";
        				}
        			}
        		}
        	}
        	return info.substring(0,info.length-1);
        },
        ResetForm : function($target){
        	for(var i=0;i<this.properties.length;i++){
        		var name = this.properties[i]['name'];
        		switch(this.properties[i]['type']){
		    		case 'text':$target.find("#" + name).val(this.properties[i]['value']);break;
		    		case 'hidden':break;
		    		case 'select':{
		    			//if(this.properties[i]['value'] && this.properties[i]['value'] != ""){
		    			$.ligerui.get(name).selectValue(this.properties[i]['value']);
		    			/*}else{
		    				if($.ligerui.get(name).data && $.ligerui.get(name).data.length > 0){
		    					$.ligerui.get(name).selectValue($.ligerui.get(name).data[0].id);
		    				}
		    			}*/
		    			break;
		    		}
		    		case 'date':$.ligerui.get(name).setValue(this.properties[i]['value']);break;
		    		case 'combobox':$.ligerui.get(name).selectValue(this.properties[i]['value']);break;
		    		case 'popup':{$.ligerui.get(name).setValue("");$.ligerui.get(name).setText("");}break;
		    		case 'daterange':{
		    			$.ligerui.get(name).unselect.trigger('click');
		    			break;
		    		}
		    		case 'dblin':{
		    			if(this.properties[i]['value'] && this.properties[i]['value'] != ""){
		    				$.ligerui.get(name).selectValue(this.properties[i]['value']);
		    			}else{
		    				if($.ligerui.get(name).data && $.ligerui.get(name).data.length > 0){
		    					$.ligerui.get(name).selectValue($.ligerui.get(name).data[0].id);
		    				}
		    			}
		    			$target.find("#" + this.properties[i]['inputName']).val("");
		    			break;
		    		}
		    		case 'textrange':{
		    			$target.find("#" + name).val("");
		    			$target.find("#" + this.properties[i]['inputName']).val("");
		    			break;
		    		}
        		
        	}
        }
        },
        render: function () {
        	this.properties = this.componentes.toFormPorperties();
        	/*for(var i in this.properties){
        		if(this.properties[i].type == "date" && (this.properties[i].value == null || this.properties[i].value == ""))
        			this.properties[i].value = "#"+"{day-1}";
        		if(this.properties[i].type == "popup" && (this.properties[i].value == null || this.properties[i].value == "" || this.properties[i].value == "null"))
        			this.properties[i].value = "#"+"{ORG_NO}";
        	}*/
            this.el.ligerForm(Convertor.convert(this.properties, 'real'));
            for(var i=0;i<this.properties.length;i++){
            	if(this.properties[i]['type'] == 'daterange'){
            		var value = this.properties[i]['value'];
            		var name = this.properties[i]['name'];
            		var dayRangeMax = parseInt(this.properties[i]['dayRangeMax']);
            		var startDate ="";
        			var endDate ="";
            		if(value){
            			var vlas = value.split(";");
            			for(var v in vlas){
            				if ((macros = vlas[v].match(/#\{([^}]+)/g)) && (cal = moment())) {
        						for (var j = 0 ; j < macros.length ; j++) {
        							macro = macros[j].replace("#{", "");
        							if (macro === 'now') {
            							cal = moment();
            						}else if (macro === 'lmonth') {
            							var date = new Date();
            							date.setDate(1);
            							date = new Date(date.getTime()-60*60*24*1000);
            							cal = moment(date);
            						}else if (macro === 'lquarter') {//上季度末
            							var date = new Date();
            							var month = date.getUTCMonth();
            							if(month == 0 || month == 1 || month == 2){
            								date.setFullYear(date.getFullYear()-1);
            								date.setMonth(11);
                							date.setDate(31);
            							}else if(month == 3 || month == 4 || month == 5){
            								date.setMonth(2);
                							date.setDate(31);
            							}else if(month == 6 || month == 7 || month == 8){
            								date.setMonth(5);
                							date.setDate(30);
            							}else if(month == 9 || month == 10 || month == 11){
            								date.setMonth(8);
                							date.setDate(30);
            							}
            							cal = moment(date);
            						}else if (macro === 'lyear') {
            							var date = new Date();
            							date.setFullYear(date.getFullYear()-1);
            							date.setMonth(11);
            							date.setDate(31);
            							cal = moment(date);
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
        						vlas[v] = cal.format("YYYY-MM-DD");
        					}
            			}
            			if(vlas.length==1){
            				if(vlas[0]!=""){
            					startDate = vlas[0];
            					endDate = vlas[0];
            				}
            			}
            			else if(vlas.length==2){
            				if(vlas[0]!="" && vlas[1]!=""){
            					if(vlas[1]>vlas[0]){
            						startDate = vlas[0];
            						endDate = vlas[1];
            					}
            					else{
            						startDate = vlas[1];
            						endDate = vlas[0];
            					}
            				}
            			}
            		}
            		
					
		            $.ligerui.get(name).setDisabled();
		            $.ligerui.get(name).unselect.click(function(){
		            	$(this).parent().find("input").trigger("change");
		            });
		            $.ligerui.get(name).link.click(function(){
		            	$(this).parent().find("input").trigger("click");
		            	return false;
		            });
		            $("#" + name).parent().removeClass("l-text-disabled");
		            $("#" + name).unbind("change");
		            var option = {
	            			"locale" :{
	            				format : "YYYY-MM-DD",
	            				separator: ' 至 ',
	            				monthNames: ["一月","二月","三月","四月","五月","六月","七月","八月","九月","十月","十一月","十二月"],
	            				daysOfWeek: ["日","一","二","三","四","五","六"]
	            			},
	            			"showDropdowns": true,
	            		    "autoApply": true,
	            		    "opens": "center"
	            		};
		            if(startDate != "" && endDate !=""){
		            	option.startDate = startDate;
		            	option.endDate = endDate;
		            }
		            else{
		            	$("#" + name).val("");
		            }
		            if(dayRangeMax>0){
		            	option.dateLimit= {
	            			   "days": parseInt(dayRangeMax)
	            		};
		            }
		            $("#" + name).daterangepicker(option, function(start, end, label){
            			
            		});	
            	}else if(this.properties[i]['type'] == 'dblin'){
            		var name = this.properties[i]['name'];
            		var value = this.properties[i]['value'];
            		//移除掉对下拉框的验证，将验证加在文本域上
            		$("input[ligeruiid='" + name + "']").removeAttr("validate");
            		var parent = $("input[ligeruiid='" + name + "']").parent();
            		var width = parent.width();
            		parent.find("input").width(width * 0.3);
            		parent.width(width * 0.3);
            		parent.css("float", "left");
            		var addInput = $("<div class='l-text' style='float:left;margin-left:5px;'>" +
            			"<input class='l-text-field' type='text' id='" + this.properties[i]['inputName'] + 
            			"' name='" + this.properties[i]['inputName'] + 
            			"' data-ligerid='" + name + "' validate='" +
            			this.properties[i]['validate']+"'/>" +
            			"<div class='l-text-l'></div><div class='l-text-r'></div></div>");
            		addInput.width(width * 0.7 - 7);
            		parent.after(addInput);
            		$("#"+this.properties[i]['inputName']).val(value);
            		$.ligerui.get(name).selectBox.css({ width: width * 0.3 ,resize:"none"});
            		$.ligerui.get(name).selectValue("=");
            		
            	}else if(this.properties[i]['type'] == 'textrange'){
            		var name = this.properties[i]['name'];
            		var value = this.properties[i]['value'];
            		var sNum = "";
            		var eNum = "";
            		if(value){
            			var vals = value.split(";");
                		if(vals.length == 1){
                			if(vals[0] == parseFloat(vals[0])){
                				sNum = vals[0];
                    			eNum = vals[0];
                			}
                		}
                		else if(vals.length == 2){
                			if(vals[0] == parseFloat(vals[0]) && vals[1] == parseFloat(vals[1])){
                				if(vals[0]<vals[1]){
                    				sNum = vals[0];
                        			eNum = vals[1];
                    			}
                    			else{
                    				sNum = vals[1];
                        			eNum = vals[0];
                    			}
                			}
                		}
            		}
            		var parent = $("input[ligeruiid='" + name + "']").parent();
            		var width = parent.width();
            		parent.find("input").width(width * 0.45);
            		parent.width(width * 0.45);
            		parent.css("float", "left");
            		
            		var addInput = $("<div class='l-text' style='float:left;'>" +
            			"<input class='l-text-field' type='text' id='" + this.properties[i]['inputName'] + 
            			"' name='" + this.properties[i]['inputName'] + 
            			"' data-ligerid='" + name + "' validate='" +
            			this.properties[i]['validate']+"'/>" +
            			"<div class='l-text-l'></div><div class='l-text-r'></div></div>");
            		addInput.width(width * 0.45);
            		parent.after(addInput);
            		addInput.find("input").width(width * 0.45);
            		var _add = $("<div style='float:left;text-align:center;width:"+(width * 0.1-3)+"px'>-</div>");
            		parent.after(_add);
            		$("#"+this.properties[i]['name']).val(sNum);
            		$("#"+this.properties[i]['inputName']).val(eNum);
            		
            	}
            }
            
            
            //使用BIONE的验证方法对ligerForm添加验证
            BIONE.validate(this.el);
            return this;
        }
    });

    return {View: View};
});