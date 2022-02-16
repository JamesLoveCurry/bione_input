/**
 * YuFORMAT的一些重用方法,主要是用于Format系列功能..
 */

(function($) {
	//全局系统对象
	window['YuFORMAT'] = {};
	var v_docWidth = window.innerWidth;//当前页面的宽度
	var v_docHeight = window.innerHeight;//当前页面的高度
	
	//获得表格配置数据
	YuFORMAT.getGridOption = function(_templetcode,_compentid) {
		var v_pas = { _templetCode : _templetcode};
		var jso_gridoption = YuFORMAT.doClassMethodCall("com.yusys.bione.plugin.yuformat.web.GetGridOption","getGridOption",v_pas);  //
		jso_gridoption.compent_id = _compentid;  //设置控件的id名,后面做点击事件时需要!
		
		//处理绘制器..
		var v_templet_option = jso_gridoption.templet_option;  //模板主表配置
		var v_pkname = v_templet_option.pkname;//主键字段名
		var v_fromtab = v_templet_option.fromtable;//来源表格
		var ar_formoption_b = jso_gridoption.templet_option_b;  //模板子表配置,是个数组
		var ar_allcoulumns = jso_gridoption.columns;  //所有列,是个数组
		var str_gridid = jso_gridoption.compent_id;  //表格的id
		for(var i in ar_allcoulumns) {  
				var v_col = ar_allcoulumns[i]; //每一列的配置信息
				var str_colname = v_col.name;  //列名
				var v_option_b = YuFORMAT.getItemOption(ar_formoption_b,str_colname);  //
				var str_itemkey = v_option_b.itemkey;
				var str_itemname = v_option_b.itemname; 
				var str_itemtype = v_option_b.itemtype;
				var str_loadformula = v_option_b.loadformula;  //加载公式
				
				//如果列名为preview则设置成图片预览的超链接
				if(str_colname == "previewimg"){  //图片预览!
					v_col["render"] = function(rowdata, index, value){
						return "<a href='javascript:void(0)'   style='color:blue'   onclick='YuFORMAT.openUploadDialog(\""+v_fromtab+"\",\""+v_pkname+"\",\""+rowdata[v_pkname]+"\")'>预览图片</a>";
					}
				}
				
				if(str_itemtype=="勾选框"){
				   (function(str_itemkey,i){  //闭包执行,要动态传入字段名!
				        v_col.render = function(rowdata, _index, _value) {
							var v_html = _value; 
							if(_value=="Y"){
							   v_html = "<div style=\"padding:2px;\">" +
							   				"<input class='tgl tgl-flat' id='"+i+"checkboxid"+rowdata.__id+"'type='checkbox' checked='checked' onClick=YuFORMAT.clickGridRowCheckBox(this,'" + str_gridid + "'," + _index + ",'"  + str_itemkey + "') />" + 
							   				"<label class='tgl-btn' style='margin:auto;' for='"+i+"checkboxid"+rowdata.__id+"'></label>" +
							   			"</div>"; 
							}else{
							   v_html = "<div style=\"padding:2px;\">" +
							   				"<input class='tgl tgl-flat' id='"+i+"checkboxid"+rowdata.__id+"'type='checkbox' onClick=YuFORMAT.clickGridRowCheckBox(this,'" + str_gridid + "'," + _index + ",'"  + str_itemkey + "') />" +
							   				"<label class='tgl-btn' style='margin:auto;' for='"+i+"checkboxid"+rowdata.__id	+"'></label>" +
							   			"</div>";  
							}
							return v_html;
						};
				   })(str_itemkey,i);
				} else if(str_loadformula!=null && str_loadformula != ""){  //如果有加载公式 
					(function(str_itemkey,i){  //闭包执行,要动态传入字段名!
						 v_col.render = function(rowdata, _index, _value) {
							 var v_viewField = str_itemkey + "_◆view◆";
							 var v_viewText = rowdata[v_viewField];  //显示的字段值
							 if(v_viewText!=null){
							   return v_viewText;  //直接显示名称!
							 }else{
						       return _value;
							 }
						 }
					})(str_itemkey,i);
				}
		 }

		 //处理行背景色 
		jso_gridoption.rowAttrRender=function(rowdata,rowid){
			var str_bg = rowdata["◆rowbackground"];  //看行数据中是否有背景颜色?
			if(str_bg!=null && str_bg!="") {
				return "style='background:" + str_bg + ";'";  //如果设置了背景颜色,则设置
			}else{
				return "";
			}
		 };
		

		return jso_gridoption;  //返回数据对象!
	};
	
	
	
	//点击表格行中勾选框的效果!
	YuFORMAT.clickGridRowCheckBox = function(_checkbox,_grid_id,_index,_itemkey) {
		var ischecked = _checkbox.checked;
		var v_grid = window.winobj[_grid_id];  //从全局取得表格
		var str_table = v_grid.options.templet_option.savetable; 
		var str_pkname = v_grid.options.templet_option.pkname;
		var rowdata = v_grid.getRow(_index);   //选中行的数据
		var str_pkvalue = rowdata[str_pkname]; //主键值
		var str_sql = "update " + str_table + " set " + _itemkey + "='" + (ischecked?"Y":"N") + "' where " +  str_pkname + "='" +  str_pkvalue+ "'";  
		YuFORMAT.executeUpdateSQL(str_sql);  //执行SQL..
	};

	
	// 加载表格数据
	YuFORMAT.loadGridData = function(_grid, _gridOption, _where, _wherevalue){
		var v_templetcode = _gridOption.templet_option.templetcode;  //取得模板编码
		var v_pas = {_templetcode:v_templetcode, _where:_where, _wherevalue:_wherevalue };
		var jso_grid_data = YuFORMAT.doClassMethodCall("com.yusys.bione.plugin.yuformat.web.GetGridData","getGridData",v_pas); 
		//将查询条件添加到templet_option中
		_gridOption.templet_option["sql"] = jso_grid_data.SQL;//
		_grid.options.data["Rows"] = jso_grid_data.Data;  //设置数据!
		_grid.loadData();  //向表格中加载数据!
	};
	
	//初始化表后,处理按钮及监听事件等等....
	YuFORMAT.afterInitGrid = function (_grid, _gridOption){
		
		//如果定义了按钮，则设置按钮
		if(_gridOption.ListButton!=null){  
			var v_allBtns = _gridOption.ListButton;  //所有按钮!
			for(var i=0;i<v_allBtns.length;i++) {  //遍历所有按钮!
				var v_btnCode = v_allBtns[i].FT_BTNCODE;  //按钮编码
				var v_clickName = v_allBtns[i].clickName; //			
				if(v_clickName!=null && v_clickName!="") {  //如果定义了方法名
					(function(_grid, _gridOption,v_clickName){  //★★★★闭包
						 var v_function;
						 try{
						   v_function = window.eval(v_clickName);  //根据名称反射取得函数,取不到会抛异常!!
					     }catch(_ex){}
						 if(v_function && typeof v_function != "undefined"){  //如果的确定义了这个函数
							 v_allBtns[i].click = function(_event){ //执行这个函数
							 	 console.log("点击按钮将执行js文件【" + window.js_file + "】中的方法【" + v_clickName + "】...." );  //
								 v_function(_grid,_gridOption);  //★★★★★★关键★★★★★,使用闭包功能传入该函数!
							 };
						 }else{
							 v_allBtns[i].click = YuFORMAT.nofunction;  //
						 }
						 
					})(_grid, _gridOption,v_clickName);
				}	
			}
			BIONE.loadToolbar(_grid,_gridOption.ListButton,function(){});  //加载按钮栏
		}
		
		//加载右键菜单
		//菜单项
		//右键菜单对象
		
		var rightMenu = $.ligerMenu({ top: 100, left: 100, width: 120, 
				items : [
		                  {   text : "查看数据", 
		                	  click : function(){
		                	  		YuFORMAT.showData(_grid);//
		                  		} 
		                  },{
		                	  text : "查看模版配置",
		                	  click : function(){
		                		  YuFORMAT.showGridConf(_grid);//
		                	  }
		                  },
		                  {
		                	  text : "查看查询语句",
		                	  click : function(){
		                		  $.ligerDialog.open({
		                			  width : 900,
		                			  height : 55,
		                			  content : "查询语句是：\r\n  【"+_gridOption.templet_option.sql+"】"
		                		  });//
		                		  //BIONE.tip("查询语句是：\n  【"+_gridOption.templet_option.sql+"】");//
		                	  }
		                  },
		                  {
		                	  text : "取消", 
		                	  click : function(){
		                		  rightMenu.hide();//
		                	    }
		                  }
		                  ]
			});

		if(window.winobj && window.winobj.rightMenus){
			window.winobj.rightMenus.push(rightMenu);//
		}

		//绑定右键点击事件
		_grid.bind("contextmenu",function(parm, e){
			var v_rightMenus = window.winobj.rightMenus;//
			if(v_rightMenus != null && v_rightMenus.length >= 1){
				for(var i = 0; i < v_rightMenus.length; i++){
					v_rightMenus[i].hide();//
				}
			}
			
			rightMenu.show({ top: e.pageY, left: e.pageX })//       
			return false;//阻止系统右键菜单
		} );// 

	};
	
	
	/**
	 * 显示表格的配置信息
	 */
	YuFORMAT.showGridConf = function(_grid){

		  var v_tableTarget = "";//
		  v_tableTarget += "<div>";//
		  v_tableTarget += "<span style='font-size:16px; font-weight : bold; color: blue'>模版主表配置信息</span><br/>";//
		  v_tableTarget += "<table border='1px' cellpadding='5px' cellspacing='5px'>\n";//
		  v_tableTarget += "<tr style='background-color:blue'><td>配置项</td><td>配置内容</td></tr>";//
		  var index_row = 0;//
		  for(var i in _grid.options.templet_option){
			  var v_opt_inner = _grid.options.templet_option[i];//
			  if(index_row % 2 == 0){
				  v_tableTarget += "<tr>";//
			  }else{
				  v_tableTarget += "<tr style='background-color : silver'>";//
			  }
			  v_tableTarget += "<td>"+i+"</td><td>"+JSON.stringify(v_opt_inner)+"</td>";//
			  v_tableTarget += "</tr>";//
			  index_row++;//
		  }
		  v_tableTarget += "</table>";//
		  v_tableTarget += "<span style='font-size:16px; font-weight : bold; color: green'>模版子表配置信息</span><br/>";//
		  v_tableTarget += YuFORMAT.getDiaGridHtml(_grid.options.templet_option_b);//
		  v_tableTarget += "</div>";//
		  $.ligerDialog.open({
				width : 1050,
				height : 400,
				title : "查看模版配置",
				target : v_tableTarget
			});//
	}
	
	
	/**
	 * 查看表单数据
	 */
	YuFORMAT.showData = function (v_grid){
		var v_selRows = v_grid.getSelectedRows();//
		if(v_selRows == null || v_selRows.length <= 0){
			BIONE.tip("请选择至少一条数据...", 1500);//
			return;//
		}
		//获取选中记录详细信息的table的dom字符串
		var innerDom = YuFORMAT.getDiaGridHtml(v_selRows);//
		$.ligerDialog.open({
			width : 1000,
			height : 400,
			title : "查看表单数据",
			target : innerDom
		});// 
	};//
	
	/**
	 * 根据grid选择的记录构造记录详细信息的table
	 * 返回的是html文本内容
	 */
	YuFORMAT.getDiaGridHtml = function(v_selRows){
		//解决每行列数不一致的问题
		var v_colname_able = [];//表头列名
		//遍历所有行，不重复添加列名
		for(var i = 0; i < v_selRows.length; i++){
			var v_col_row = v_selRows[i];//每一行的数据
			for(var v_c in v_col_row){//该行所有列名
				if($.inArray(v_c, v_colname_able) < 0){//列名不在allColNames中
					v_colname_able.push(v_c);//不重复的列名进行添加
				}
			}
		}
		//cellspacing='1px' cellpadding='1px'
		//, border-collapse : separate, border-spacing : 10px'
		var v_tab_able = "";//table html内容
		v_tab_able += "<table width='100%' border='1px' cellpadding='10px' cellspacing='1px' style='border-collapse : separate;' >\n";//表格开始
		v_tab_able += "<tr style='background-color:#005100;'>\n";//行开始
		for(var i = 0; i < v_colname_able.length; i++){//遍历表头列名，添加表头
			var v_col_head = v_colname_able[i];//
			v_tab_able += "<th style='color:white;'>&nbsp;"+v_col_head+"&nbsp;</th>\n";//表头的每一列
		}
		v_tab_able += "</tr>\n";//行结束
		for(var k = 0; k < v_selRows.length; k++){//遍历所有行
			if(k % 2 == 0){
				v_tab_able += "<tr style='background-color:#eeeeee;'>\n";//行开始
			}else{
				v_tab_able += "<tr>\n";//行开始
			}
			var v_nextRow = v_selRows[k];//每一行的数据
			for(var i = 0; i < v_colname_able.length; i++){//按表头的列明添加列
				var v_col_head = v_colname_able[i];//列名
				var v_nextVal = v_nextRow[v_col_head];//当前行中对应列名的值
				//如果列内容为html格式的字符串，则需要进行处理
				if(v_nextVal != null &&  v_nextVal.toString().indexOf("<") >= 0){
					var eg_1 = new RegExp("<", "g");//匹配所有的<
					var eg_2 = new RegExp("</", "g");//匹配所有的</
					var eg_3 = new RegExp("/>", "g");//匹配所有的/>
					var eg_4 = new RegExp(">", "g");//匹配所有的>
					//注意替换的顺序
					v_nextVal = v_nextVal.toString().replace(eg_2, "【");//替换
					v_nextVal = v_nextVal.toString().replace(eg_1, "【");//替换
					v_nextVal = v_nextVal.toString().replace(eg_3, "】");//替换
					v_nextVal = v_nextVal.toString().replace(eg_4, "】");//替换
					//v_colVal = v_colVal.replace("<\g");//
				}
				
				v_tab_able += "<td>&nbsp;"+v_nextVal+"&nbsp;</td>\n";//添加每一列
			}
			v_tab_able += "</tr>\n";//行结束
		}
		v_tab_able += "</table>\n";//表格结束
		return v_tab_able;//innerDom
	}
	
	
	/**
	 *根据控件名称去获取控件相关的配置信息
	 */
	YuFORMAT.getFileOptionByName = function(_formoption, _filedname){
		var filedOption = {};	
		if(_formoption && _filedname){
			if(_formoption["fields"]){
				for(var f in _formoption["fields"]){
					var sglFiledOption = _formoption["fields"][f];
					if(sglFiledOption["name"] == _filedname){
						filedOption["filed"] = sglFiledOption;
					}
				}
			}
			if(_formoption["templet_option_b"]){
				for(var b in _formoption["templet_option_b"]){
					var sglTembOption = _formoption["templet_option_b"][b];
					if(sglTembOption["itemkey"] && sglTembOption["itemkey"] == _filedname){
						filedOption["TembOption"] = sglTembOption;
					}
				}
			}
		}
		return filedOption;
	}
		
	//取得表单上某个控件中的值..
	YuFORMAT.getFormItemValue = function(_formId,_filedname){
		var v_thisForm = YuFORMAT.getWinObj(window.self)[_formId];  //取得页面上本form!
		return YuFORMAT.getFormItemValue2(v_thisForm,_filedname);
	}

	//直接从一个LigerFrom中取得数据
	YuFORMAT.getFormItemValue2 = function(_vorm,_filedname){
		var v_data = _vorm.getData();  //取得表单上所有数据!
		for(_key in v_data){  //遍历所有值..
			if(_key==_filedname){
				return v_data[_key];  //
			}
		}
		return "";  //返回空值.
	}

	//取得文字
	YuFORMAT.getFormItemText = function(_form,_filedname){
		var v_item = _form.getEditor(_filedname);
		if(v_item && v_item.getText){
			return v_item.getText();
		}
	}

	
	//表单中某一项修改!
	YuFORMAT.onFormItemChanged = function(_formOption, _fieldName){
		if(typeof _formOption.forceForverCloseTrigger!="undefined" && _formOption.forceForverCloseTrigger=="Y"){  //有时需要强行永远关闭,这个变量不会被默认值公式更改..
			return;  //
		}

		if(typeof _formOption.isTriggerEvent=="undefined" || !_formOption.isTriggerEvent){  //如果没定义
			return;  //
		}
		
		//根据_fileName去配置项中找出对应的显示公式定义,比如如下:
		var str_showFormula = "";
		var fileOption = YuFORMAT.getFileOptionByName(_formOption, _fieldName);
		if(fileOption){
			if(fileOption["TembOption"]){
				if(fileOption["TembOption"]["showformula"]){
					str_showFormula = (fileOption["TembOption"]["showformula"]).toString();
					str_showFormula = str_showFormula.replace(/【/g, "");
					str_showFormula = str_showFormula.replace(/】/g, "");
				}
			}
		}
		//var str_showFormula = "{\"图表\":\"index_name;data_name;unit;is_show_unit;org_dim;currency_dim;text_info;is_wrap;\",\"标题2\":\"org_dim;currency_dim;\"}";
		if(str_showFormula) {  //如果定义了公式..
			var jso_showFormula = jQuery.parseJSON(str_showFormula);  //转成Json对象!
			
			YuFORMAT.setFormAllItemVisiable(_formOption,true);  //先把所有隐藏掉的统统显示..
			
			var v_itemValue = YuFORMAT.getFormItemValue(_formOption.formId,_fieldName);  //取得该控件中的值!
			var v_hiddenItems = jso_showFormula[v_itemValue];  //定义想要隐藏哪些字段...
			
			if(v_hiddenItems!=null){  //如果对上了
				YuFORMAT.setFormItemVisiable(null,v_hiddenItems,false); //隐藏.
			}
	    }
	}
	
	//设置表单上所有控件是否显示..
    YuFORMAT.setFormAllItemVisiable = function(_formOption,_visiable){
    	var v_allItems = YuFORMAT.getFormAllShowItems(_formOption);  //
    	for(var i=0;i<v_allItems.length;i++){  //遍历所有控件
    		YuFORMAT.setFormItemVisiable(null,v_allItems[i],_visiable);  //
    	}
	}

	//设置某个字段是否显示,name可以分号分割，比如:index_name;data_name;unit
	YuFORMAT.setFormItemVisiable = function (_form,_name,_visiable){
		//console.log("设置[" + _name + "][" + _visiable + "]");
		var v_array = _name.split(";");  //
		for(var i=0; i<v_array.length; i++) {
			var v_dom = null;
			if(_form){  //如果指定了从某个doc中取
			   //console.log(_form.element);
               v_dom = _form.element.querySelector("#" + v_array[i]);
			}else{
			   v_dom = document.getElementById(v_array[i]);
		    }
			if(typeof v_dom == "undefined" || v_dom==null){
				continue;  //
			}

			var v_parentDom = YuFORMAT.findFormItem(v_dom);  //
			if(typeof v_parentDom == "undefined" || v_parentDom==null){
				continue;  //
			}
			v_parentDom.hidden = !_visiable;  //
		}
	}

	//设置某一个控件是否启用.
	YuFORMAT.setFormItemEnabled = function(_form,_fieldName,_enabled){
		var v_item = _form.getEditor(_fieldName);
		var dom_item = v_item.element;  //取得控件的html元素
		//console.log(dom_item);
		if(_enabled){
			v_item.setEnabled();  //启用
			dom_item.style.color="#333333";  //
		}else{
			v_item.setDisabled();  //禁用
			dom_item.style.color="#7B7B7B";  //因为LigerForm默认不可编辑的颜色是非常灰色的,看不清楚,所以没办法只能强行修改
		}
	}

	//设置LigerForm某个字段的值
	YuFORMAT.setFormItemValue = function(_form,_fieldName,_value,_text){
		var v_item = _form.getEditor(_fieldName);
		if(v_item){
			if(v_item.setValue){
			   v_item.setValue(_value);
			} else {
				var v_html = v_item[0];  //直接取html控件!
				if(v_html.value!=null){  //隐藏域非要这么搞?也不知算不算ligerForm的Bug???
					v_html.value=_value;
				}
			}

			if(_text){
				v_item.setText(_text);
			}
		}
	}

	//重置所有数据
	YuFORMAT.reSetFormValue= function(_form){
		var array_items = _form.options.templet_option_b; //模板子表
		for(var i=0;i<array_items.length;i++){  //遍历模板子表
			var str_itemkey = array_items[i].itemkey;  //itemkey
			var v_item = _form.getEditor(str_itemkey);
			if(v_item){
			  if(v_item.setValue){
			    v_item.setValue("");
			  }

			  if(v_item.setText){
			    v_item.setText("");
			  }
		    }
		}
	}

	
	//取得表单上所有显示的控件名..
	YuFORMAT.getFormAllShowItems = function(_formOption){
		var v_nameArray = new Array();
		var v_formoption_b = _formOption.templet_option_b;  //模板子表配置
		for(i=0;i<v_formoption_b.length;i++){
			var v_itemKey = v_formoption_b[i].itemkey;  //
			var v_itemType = v_formoption_b[i].itemtype;  //
			if(v_itemType!="隐藏域"){
				v_nameArray.push(v_itemKey);  //
			}
		}
		return v_nameArray;  //
	}

	
	//递归往上寻找我需要的控件..
	YuFORMAT.findFormItem = function(_dom){
	 try{
		var dom_myParent = _dom.parentNode;  //父亲结点
		if(typeof dom_myParent == "undefined"){
			return null;  //
		}
		
		var v_name=dom_myParent.nodeName;  //结点名称
		if("BODY"==v_name.toUpperCase()){  //万一找到顶都没找到,则直接返回
		  return null;
		}

		if(dom_myParent.hasAttribute("fieldindex")){
			return dom_myParent;
		}

		return YuFORMAT.findFormItem(dom_myParent);
	  }catch(_ex){
		  return null;
	  }
	}

	//设置一个LigerForm中的某一项下拉框中的值为新的实际值，主要是要选用清空原来数据
	//一定要注意_dataArray的格式必须是id:"1",value:"标题1",text:"标题1",即是三个属性id/value/text
	YuFORMAT.resetComBoxValue = function(_form,_fieldName,_dataArray){
		_form.options.isTriggerEvent=false;
		var v_item = _form.getEditor(_fieldName);
		v_item.setData(_dataArray);
		//v_item.options.data=[];  //先清空数据
		//v_item.data=[]; //先清空数据
		//for(var i=0;i<_dataArray.length;i++){
			//v_item.addItem(_dataArray[i]);
		//}
		_form.options.isTriggerEvent=true;
	}

	//保存Form表单数据
	YuFORMAT.saveFormData = function(_form){
		//先获取当前form 的所有数据
		var formData = _form.getData(); //

		var str_must = "";
		var array_items = _form.options.templet_option_b; //模板子表
		for(var i=0;i<array_items.length;i++){  //遍历模板子表
			var str_itemkey = array_items[i].itemkey;  //itemkey
			var str_itemname = array_items[i].itemname;  //itemkey
			if(array_items[i].ismust=="必输项"){
				//YuFORMAT.tip(str_itemname + "是必须项!");
				if(formData[str_itemkey]==null || formData[str_itemkey]==""){
					var isHidden = YuFORMAT.isFormItemVisiable(_form,str_itemkey);
					if(!isHidden){  //如果没有隐藏,即显示着,并且又是必输项,则肯定要校验!,换句话说就是隐藏的东东可以跳过!
					   str_must = str_must + "【" + str_itemname + "】是必输项!<br>";
					}
				}
			}
		}
		
		if(str_must!=""){
			YuFORMAT.tip(str_must);
			return;
		}

		//_form.validate(); //校验
		if (!v_form.valid()){
	    	v_form.showInvalid();//显示校验信息
	       return;//
	   }

		//处理颜色控件,要在前面加上#,先暂时不搞,因为颜色选择器变化了好几次
		//for(var f in formData){
			//console.log("【classname】: "+$("#mainform input[name="+f+"]").attr("class"));//
			//var v_jsClass = $("#mainform input[name="+f+"]").attr("class"); //要改成使用JS的中取法.
			//if(v_jsClass != null && typeof v_jsClass != "undefine" && v_jsClass.indexOf("jscolor") >= 0){
				//formData[f] = "#" + formData[f];//
			//}
		//}
	
		//处理勾选框控件,要转换成Y/N
		var array_items = _form.options.templet_option_b; //模板子表
		for(var i=0;i<array_items.length;i++){  //遍历模板子表
			var str_itemkey = array_items[i].itemkey;  //itemkey
			var str_itemtype = array_items[i].itemtype;  //控件类型
			if("勾选框"==str_itemtype){  //如果是勾选框则进行转换
				var boolean_ischeckd = formData[str_itemkey]; //看控件是否勾选!
				if(boolean_ischeckd){  //如果是勾选上了
					formData[str_itemkey] = "Y"; //重新更改为Y
				}else{
					formData[str_itemkey] = "N";  //重新更改为N
				}
			}
		}

		//取得主键值
		var str_pkName = _form.options.templet_option.pkname;  //主键字段名
	    var str_pkValue = formData[str_pkName];  //主键值.
		var v_data = {pkvalue : str_pkValue, data : formData};//返回到后台的主键值和form数据
		
		//更新或新增数据
		var v_result = YuFORMAT.doClassMethodCall("com.yusys.bione.plugin.yuformat.web.GetFormData","saveFormData", v_data);
		//v_result.isSuccess;
		//v_result.resultMsg;
		//v_result.pkvalue;
		return v_result;
	}

	//判断某个控件是否隐藏?
	YuFORMAT.isFormItemVisiable = function(_form,_fieldName){
		var v_dom = _form.element.querySelector("#" + _fieldName);
		var v_parentDom = YuFORMAT.findFormItem(v_dom);  //
		if(v_parentDom){
			return v_parentDom.hidden;
		} else{
			return true;  //
		}
	}
	
	//获得表单配置数据
	YuFORMAT.getFormOption = function(_templetCode) {
		var v_pas = { _templetCode : _templetCode}; //取得模板编码
		var jso_formoption = YuFORMAT.doClassMethodCall("com.yusys.bione.plugin.yuformat.web.GetFormOption","getFormOption",v_pas);  //		
		jso_formoption.validate  = true;//设置表单提交时进行验证
		var v_formoption_b = jso_formoption.templet_option_b;  //设置表单的模板定义对象.  模板主表配置
		var fields_form = jso_formoption.fields; //所有字段列表!
		for(var i = 0;i < fields_form.length; i++){  //遍历所有字段
			var field = fields_form[i];//field
			var field_name = field.name;//名称
			var field_type = field.type; //类型
			if(field_type == "select"){  //如果是选择框,则监听选择变化事件!
				var field_editor = field.editor;//编辑器
				(function(jso_formoption,field_name){  //闭包执行
					field_editor.onSelected = function(_newvalue){
						YuFORMAT.onFormItemChanged(jso_formoption,field_name);  //
						var fn_itemEdit = window.onItemEdit;  
						if(fn_itemEdit){
							fn_itemEdit(field_name);
						}
					};
				})(jso_formoption,field_name);
			} else if(field_type=="popupedit"){  //如果弹出框
				var field_editor = field.editor;//编辑器
				(function(jso_formoption,field_name){  //闭包执行
					field_editor.onButtonClick = function(){
						YuFORMAT.onClickRefButton(jso_formoption,field_name);//点击参照按钮..
					};
				})(jso_formoption,field_name);
			}
		}
		//jso_formoption.isTriggerEvent = true;  //构造完后可以触发事件
		return jso_formoption;  //返回数据对象!
	};
	
	//打开图片上传下载dialog
	YuFORMAT.openUploadDialog = function(fromtable, pkname, pkvalue){
		var v_batchid = "bid_"+fromtable+"_"+pkname+"_"+pkvalue;
		var v_div = "<div width='800px' height='550px'><input class='l-dialog-btn-inner' type='text' id='imgfile' readonly /><br/><input class='l-dialog-btn-inner' type='button' value='上传图片' onclick='YuFORMAT.uploadImg(\"#imgshow\",\""+fromtable+"\",\""+pkname+"\",\""+pkvalue+"\")' /><input type='button' value='下载图片' onclick='YuFORMAT.downloadImg(\""+v_batchid+"\")' /><br/><div width='750px' height='325px'><img id='imgshow' src='"+v_context+"/frs/yuformat/downloadimg?type=Image&batchid="+v_batchid+"&fromtable="+fromtable+"&pkname="+pkname+"&pkvalue="+pkvalue+"' alt='当前记录没有关联图片，请上传图片后重新查看'></img></div></div>";
		$.ligerDialog.open({
			width : v_docWidth*0.95,
			height : v_docHeight*0.85,
			showMax : true,
			target : v_div
		}); 
	}
	
	
	
	YuFORMAT.uploadImg = function(divtar, fromtable, pkname, pkvalue){
		var v_batchid = "bid_"+fromtable+"_"+pkname+"_"+pkvalue;  //v_year+"-"+v_mon+"-"+v_day+"-"+v_hour+":"+v_min+":"+v_sec;
		var v_upDialog = BIONE.commonOpenDialog(
											"上传文件",
											"fileUpWin",
											580,
											340,
											v_context+'/frs/yuformat/uploadFile?type=Image&batchid='+v_batchid+'&fromtable='+fromtable+'&pkname='+pkname+'&pkvalue='+pkvalue,
											null,
											function(){
												YuFORMAT.repaintimg(divtar, v_batchid);
											});
	}
	
	YuFORMAT.repaintimg = function(divtar, batchid){
		//从数据库中读出刚刚存放的图片并显示在预览框中
		$(divtar).attr("src", v_context+'/frs/yuformat/downloadimg?type=Image&batchid='+batchid+'&t='+(new Date()).getTime());	
	}
	
	YuFORMAT.downloadImg = function(batchid){
		window.location.href = v_context+'/frs/yuformat/saveimg?batchid='+batchid+'&t='+(new Date()).getTime();
	}
	
	//处理表单后续信息,即增加按钮!
	YuFORMAT.afterInitForm = function(_formOption, _formId) {
		_formOption.formId = _formId;  //绑定!
		var v_templet_option = _formOption.templet_option;  //模板主表配置
		var v_templet_option_b = _formOption.templet_option_b;  //模板子表配置
		var str_templetcode = v_templet_option.templetcode;  //模板编码
		
		for(var b in v_templet_option_b){
			var v_itemoption = v_templet_option_b[b];//每个字段的配置信息
			var v_itemtype = v_itemoption.itemtype;//字段的类型:如图片预览  itemkey
			var v_itemkey = v_itemoption.itemkey;//字段的key  
			var v_itemname = v_itemoption.itemname;//字段的名称
			//添加气泡信息
			if(v_itemoption.helptip){
				//根据fieldindex=b来确定表单字段的dom 
				YuFORMAT.onShowPopMsg("li[fieldindex="+b+"]", v_itemoption.helptip);
			}
			
			if(v_itemtype == null || typeof v_itemtype == "undefine" || v_itemtype == ""){
				
			}else if(v_itemtype == "图片预览"){//如果是图片预览
				var v_batchid = "bid_"+v_templet_option.fromtable+"_"+v_templet_option.pkname+"_"+v_templet_option.pkvalue;//
				var v_imgDiv = "<li width='550px' height='450px'  fieldindex="+b+"><p margin-bottom='5px'>"+v_itemname+": </p><div class='preview' width='550px' height='450px' margin-bottom='5px' ><div class='mainbtn' padding-left='5px'><input type='button' value='上传图片' onclick='YuFORMAT.uploadImg(\"#img_preview\",\""+v_templet_option.fromtable+"\",\""+v_templet_option.pkname+"\",\""+v_templet_option.pkvalue+"\")'/><input type='button' value='下载图片'  onclick='YuFORMAT.downloadImg(\""+v_batchid+"\")' /></div><div class='mainimg' style='border : 1px solid gray; width:855px; height:355px;'><img id='img_preview' src='"+v_context+"/frs/yuformat/downloadimg?type=Image&batchid="+v_batchid+"&fromtable="+v_templet_option.fromtable+"&pkname="+v_templet_option.pkname+"&pkvalue="+v_templet_option.pkvalue+"' alt='当前记录没有关联图片，请上传图片后重新查看'></img></div></div></li>";
				$("li[fieldindex="+b+"]").replaceWith(v_imgDiv);
			}
		}
		//设置隐藏域，因为表单提时，后台只能接到表单里面的数据,所以要先设置下
		$(_formId + " input[name=FT_templetcode]").val(str_templetcode);  //模板编码
		$(_formId + " input[name=FT_savetable]").val(v_templet_option.savetable);  //保存的表名
		$(_formId + " input[name=FT_pkname]").val(v_templet_option.pkname);  //设置模板编码隐藏域
		
		var buttons = [];  //按钮数组
		buttons.push({
			text : '取消',
			onclick : function() {
				BIONE.closeDialog(str_templetcode); //关闭弹出窗口!
			}
		});
		buttons.push({
			text : '应用',
			onclick : YuFORMAT.f_save  //有的地方没这函数,所以改成了动态判断! 感觉设计不合理，以后考虑优化
		});
		buttons.push({
			text : '保存',
			onclick : YuFORMAT.f_confirm  //有的地方没这函数,所以改成了动态判断!感觉设计不合理，以后考虑优化
		});
		BIONE.addFormButtons(buttons); //在表单上加上按钮..	
		_formOption.isTriggerEvent = true; 
	};
	
	//保存,动态判断下有没有再执行
	YuFORMAT.f_save = function(){
		var fn = window.eval("f_save");
		if(fn){
			fn();
		}
	}

	//保存,动态判断下有没有再执行
    YuFORMAT.f_confirm = function(){
    	var fn = window.eval("f_confirm");
		if(fn){
			fn();
		}
	}


	/**
	 * 给表单字段添加气泡信息
	 * target   : dom
	 * msg       : 提示信息
	 */
	YuFORMAT.onShowPopMsg = function(target, msg){
		//给字段dom绑定鼠标经过事件
		$(target).live('mouseover', function(){
			$(target).ligerTip({content : msg}); //dom元素右侧弹出tip
		});
		
		//给字段dom绑定鼠标移出事件
		$(target).live('mouseout', function(){
			$(target).ligerHideTip(); //关闭tip
		});
	}
	
	//点击参照按钮的逻辑
	YuFORMAT.onClickRefButton = function(jso_formoption,_fieldName){
		var v_templet_option = jso_formoption.templet_option;  //模板主表配置
		var v_formoption_b = jso_formoption.templet_option_b;  //模板子表配置
		
		var str_template_code = v_templet_option.templetcode;//取得模版编码
		var v_itemOption = YuFORMAT.getItemOption(v_formoption_b,_fieldName);  //根据字段名取得模板子表中对应的详细配置!
		
		if (!v_itemOption || typeof v_itemOption == "undefined"){
			//console.log("从模板子表中没有找到对应项【" + _fieldName + "】");
			return;  //
		}

		var str_templet_bid = v_itemOption.templet_bid;//每一项form元素的templet_bid
		var item_type = v_itemOption.itemtype;//每一项form元素的控件类型
		var v_item_define = v_itemOption.itemdefine;//每一项form元素的item_define
		if(v_item_define){//如果存在
			var v_close_1 = v_item_define.search("【");//
			var v_close_2 = v_item_define.search("】");//
			if(v_close_1 == 0 && v_close_2 == v_item_define.length-1){
				v_item_define = v_item_define.substring(1, v_item_define.length-1);//
			}
			
		}
		var v_Define = JSON.parse(v_item_define);//控件定义!!
		
		if(item_type == "大文本框") {
			//如果是大文本框,将弹出一个带编辑框的dialog
			//var url = "${ctx}/frs/yuformat/openBigText?&templetcode="+code_template+"&templet_bid="+id_item+"&item_name="+item_name;//
			//BIONE.commonOpenDialog("编辑大文本框",v_name_dialog, 800,450,url);//itemname
		} else if(item_type == "列表参照"){  //如果是列表参照则弹出一个列表
			if(v_Define["类型"] != null && v_Define["类型"] == "SQL"){
				var v_editForm = YuFORMAT.getWinObj(window)[jso_formoption.formId];  //取得实际的Form
				var v_dataSQL ;//
				if(v_Define["SQL"] != null && v_Define["SQL"] != ""){
					v_dataSQL = v_Define["SQL"];//
				}else if(v_Define["sql"] != null && v_Define["sql"] != ""){
					v_dataSQL = v_Define["sql"];//
				}else if(v_Define["Sql"] != null && v_Define["Sql"] != ""){
					v_dataSQL = v_Define["Sql"];//
				}
				var v_textfield = v_Define["text位置"];//
				var v_valuefield = v_Define["value位置"];//
				var v_colwidth = v_Define["列宽"];//
				var v_name_dialog = "弹出列表参照--"+v_itemOption.itemname;//
				var v_name_backfun = "setPopEdit";//
				var v_name_field = _fieldName;//
				var li_width=500;
				var li_height=500;
				if(v_Define["窗口宽度"]){
					li_width = v_Define["窗口宽度"];
				}
				if(v_Define["窗口高度"]){
					li_height = v_Define["窗口高度"];
				}
				YuFORMAT.openSQLGridDialog(v_name_dialog, v_dataSQL, v_name_backfun, v_name_field, v_valuefield, v_textfield, v_colwidth, v_editForm,li_width,li_height, v_Define);//
				//YuFORMAT.openGridDialog(v_pars);
			}else if(v_Define["类型"] != null && v_Define["类型"] == "模板"){  //直接使用模板的列表参照!
				var v_code = v_Define["code"];//模板 编码
				var v_jsFilePath = v_Define["jsFilePath"];//模板所需js文件路径
				var v_btns = v_Define["allBtns"];//显示的按钮组
				var v_textitem = v_Define["text字段"];//返回的显示字段名称
				var v_valueitem = v_Define["value字段"];//返回的保存字段名称
				var v_name_backfun = "setPopEdit";//回调方法的函数名
				if(v_code == null || v_code == "" || typeof v_code == "undefine"){
					$.ligerDialog.error('表单字段【'+_fieldName+'】配置信息错误，缺少【code】');
					return;
				}
				if(v_textitem == null || v_textitem == "" || typeof v_textitem == "undefine" 
					|| v_valueitem == null || v_valueitem == "" || typeof v_valueitem == "undefine"){
					$.ligerDialog.error('表单字段【'+_fieldName+'】配置信息错误，缺少【v_textitem】或者【v_valueitem】');
					return;
				}
				var li_width=500;
				var li_height=500;
				if(v_Define["窗口宽度"]){
					li_width = v_Define["窗口宽度"];
				}
				if(v_Define["窗口高度"]){
					li_height = v_Define["窗口高度"];
				}
				YuFORMAT.openTemplateDialog(v_code, v_jsFilePath, v_btns, v_textitem, v_valueitem, v_name_backfun, _fieldName,li_width,li_height);
			}
		} else if(item_type == "树型参照"){
			var v_editForm = YuFORMAT.getWinObj(window)[jso_formoption.formId];  //取得实际的Form
			var v_dialogtitle = "树型参照[dialog_tree]--" + v_itemOption.itemname;  //标题!
			var v_pars = {dialogtitle:v_dialogtitle, editform:v_editForm,fieldName:_fieldName,define:v_Define}; //参数
			var li_width=500;
			var li_height=500;
			if(v_Define["窗口宽度"]){
				li_width = v_Define["窗口宽度"];
			}
			if(v_Define["窗口高度"]){
				li_height = v_Define["窗口高度"];
			}
			YuFORMAT.openTreeDialog(v_pars,li_width,li_height);  //弹出
		} else if(item_type == "树表参照"){
			YuFORMAT.tip("开发中...");  //不想再继续封装了,直接去掉这个功能了!
		} else if(item_type == "自定义参照"){
			var v_editForm = YuFORMAT.getWinObj(window)[jso_formoption.formId];  //取得实际的Form
			var v_dialogtitle = "自定义参照--" + v_itemOption.itemname;  //标题!
			
			var li_width=500;
			var li_height=500;
			if(v_Define["窗口宽度"]){
				li_width = v_Define["窗口宽度"];
			}
			if(v_Define["窗口高度"]){
				li_height = v_Define["窗口高度"];
			}
			var str_dialogName = "newDialog";
			if(v_Define.dialogName){
				str_dialogName = v_Define.dialogName;
			}
			var v_pars = {dialogtitle:v_dialogtitle, editform:v_editForm,fieldName:_fieldName,define:v_Define}; //参数

			var v_context = "/rpt-web";  //先暂时弄这个!
			if(v_Define.url.toLowerCase().indexOf(".jsp")>0){  //如果是直接跳到一个jsp
				YuFORMAT.openPathDialog(v_context,v_Define.url,v_pars,li_width,li_height);  //从弹出窗口中可以根据 var v_id = "${dialogid}"，可以获得数据
			}else{
			   var v_url = v_context + v_Define.url;  //拼路径
			   BIONE.commonOpenDialog("选择窗口",str_dialogName,li_width,li_height,v_url);  //弹出路径
			}
		} else{
			//console.log("【item_type】：null");//
		}
	}
	
	//新增的时候加载表单的默认值公式
	YuFORMAT.loadDefaultData = function(v_formoption){
		var v_templetcode = v_formoption.templet_option.templetcode;  //获取模版编码
		var v_pas = { _templetcode:v_templetcode}; //
		//后台获取默认值
		var jso_formdata = YuFORMAT.doClassMethodCall("com.yusys.bione.plugin.yuformat.web.GetFormData","getDefFormData",v_pas);  //取得表单数据!
		v_formoption.isTriggerEvent = false;
		YuFORMAT.setFormData(jso_formdata); //设置表单数据
		v_formoption.isTriggerEvent = true;
		
		//根据表单各控件数值设置加载显示公式
		if(v_formoption.fields){
			for(var f in v_formoption.fields){
				var fieldname = v_formoption.fields[f]["name"];
				if(fieldname){
					YuFORMAT.onFormItemChanged(v_formoption, fieldname);  //触发编辑公式
				}
			}
		}
	}
	
	
	
	//加载表单数据
	YuFORMAT.loadFormData = function(v_formoption,_pkvalue){
		var str_templetcode = v_formoption.templet_option.templetcode;  //
		var str_pkname = v_formoption.templet_option.pkname;  //	
		var jso_formdata = YuFORMAT.getFormData(str_templetcode, str_pkname, _pkvalue);  //
		v_formoption.templet_option["sql"] = jso_formdata.SQL;//
		
		v_formoption.isTriggerEvent = false;  //
		YuFORMAT.setFormData(jso_formdata.Data[0]); //设置表单数据,总感觉这个函数不严谨,至少它只认为页面上只有一个表单,如果有两个,且发生重名,就会有问题的
		v_formoption.isTriggerEvent = true;  //
		
		//根据表单各控件数值设置加载显示公式
		if(v_formoption.fields){
			for(var f in v_formoption.fields){
				var fieldname = v_formoption.fields[f]["name"];
				if(fieldname){
					YuFORMAT.onFormItemChanged(v_formoption, fieldname);
				}
			}
		}
	};
	
	//得到表单数据
	YuFORMAT.getFormData = function(_templetcode,_pkname,_pkvalue){
		var v_where = _pkname + "=?";  //条件
		var v_wherevalue = {_w1:_pkvalue};  //条件值
		var v_pas = { _templetcode:_templetcode, _where:v_where, _wherevalue:v_wherevalue  }; //取得模板编码
		var jso_formdata = YuFORMAT.doClassMethodCall("com.yusys.bione.plugin.yuformat.web.GetFormData","getFormData",v_pas);  //取得表单数据!
		return jso_formdata;  //
	};
	
	//在前台设置表单数据,要处理日期,按钮等控件的特殊情况..这个也是从BIONE.js中拷贝过来的.有点问题!!!
	YuFORMAT.setFormData = function (data) {
		// 根据返回的属性名，找到相应ID的表单元素，并赋值
		for ( var p in data) {  //
			if(p.indexOf("FT_")==0){  //如果是FT_开关的,则跳过,因为这是隐藏域..
				continue;
			}
			var ele = $("[name=" + p + "]"); //默认认为页面上只有一个表单,不会重名!!!
			// 针对复选框和单选框 处理
			if (ele.is(":checkbox,:radio")) {
				ele[0].checked = data[p] == "Y" ? true : false;
			} else if (ele.is(":text") && ele.attr("ltype") == "date") {
				if (data[p]) {
					var date = null;
					if (data[p].time) {
						date = new Date(data[p].time);
					} else {
						// edit by caiqy
						var tdate;
						if (typeof data[p] == 'string'
								&& data[p].indexOf('-') != -1
								&& data[p].length >= 10) {
							// if format as 'yyyy-MM-dd' or 'yyyy-mm-dd
							// hh:mm:ss'
							tdate = new Date(new Number(data[p]
									.substr(0, 4)), new Number(data[p]
									.substr(5, 2)) - 1, new Number(data[p]
									.substr(8, 2)));
						} else {
							tdate = new Date(data[p]);
						}
						date = new Date(tdate);
					}
					var yy = date.getFullYear();
					var Mm = ((date.getMonth() + 1) < 10) ? ('0' + (date
							.getMonth() + 1)) : (date.getMonth() + 1);
					var dd = (date.getDate() < 10) ? ('0' + date.getDate())
							: date.getDate();
					ele.val(yy + '-' + Mm + '-' + dd);
				}
			}else if (ele.attr("ltype") == "checkbox" || ele.attr("ltype") == "radio") {
				if(data[p] != null) {
					if(data[p] == "Y"){
						ele[0].checked = true;///
					}else{
						ele[0].checked = false;///
					}
					//ele[0].checked = (data[p] == "Y") ? true : false;
				}else{
					//从配置字段里取数，如果没有则默认为true
					ele[0].checked = true;//
				}
				
				
				
			} else if (ele.attr("ltype") == "radiolist" || ele.attr("ltype") == "checkboxlist") {
				$.ligerui.get(ele.attr("data-ligerid")).setValue(data[p]);
			}else if (ele.attr("ltype") == "popupedit") {  //如果是列表参照/树型参照等控件
				if(data[p] != null) {  
					ele.ligerPopupEdit().setValue(data[p]); //先设置实际值
					var v_viewText = data[p + "_◆view◆"];  //
					if(v_viewText!=null && (typeof v_viewText != 'undefined')){
					   ele.ligerPopupEdit().setText(v_viewText);//
					}else{
					   ele.ligerPopupEdit().setText(data[p]);//
				    }
				} else{
					ele.ligerPopupEdit().setValue("");//
					ele.ligerPopupEdit().setText("");//
				}
			}  else {
				var content_p = data[p];//
				if(typeof content_p == "String"){
					ele.val(data[p]);  //一般控件,直接设置数据!
				}else if(typeof content_p == "Object"){
					ele.val(JSON.stringify(data[p]));  //一般控件,直接设置数据!
				}
				ele.val(data[p]);  //一般控件,直接设置数据!
			}
		}
		// 下面是更新表单的样式
		var managers = $.ligerui.find($.ligerui.controls.Input);
		for ( var i = 0, l = managers.length; i < l; i++) {
			// 改变了表单的值，需要调用这个方法来更新ligerui样式
			var o = managers[i];
			o.updateStyle();
			if (o.inputText) {
				o.inputText.blur();
			}
			if (managers[i] instanceof $.ligerui.controls.TextBox)
				o.checkValue();
		}
		BIONE.hideLoading();
	};
	
	
	//新增单表数据
	YuFORMAT.onINSERT_OneGrid = function (_grid, _gridOption, _gridname) {
		var v_templetcode = _gridOption.templet_option.templetcode;  //模板编码
		var v_urlpar = "_templetcode=" + v_templetcode + "&_gridname=" + _gridname+"&_isadd=Y";  //
		
		if(_gridOption.templet_option.fkname){//外键名称
			v_urlpar = v_urlpar +"&_fkname="+_gridOption.templet_option.fkname;
		}
		if(_gridOption.templet_option.fkvalue){//外键值
			v_urlpar = v_urlpar +"&_fkvalue="+_gridOption.templet_option.fkvalue;
		}
		
		BIONE.commonOpenFullDialog("新增", v_templetcode, v_context + "/frs/yuformat/updategrid?" + v_urlpar);
	}
	
	//修改单表数据
	YuFORMAT.onUPDATE_OneGrid = function (_grid,_gridOption,_gridname) {
		var v_selVOs = _grid.getSelectedRows();  //得到选中行的记录!getSelectedRows
		if(v_selVOs==null || v_selVOs.length<=0){
			YuFORMAT.tip('必须选择一条【' + _gridOption.templet_option.templetname + '】进行操作!!');
			return;  //
		}
		
		if(v_selVOs.length > 1){
			YuFORMAT.tip("只能选择一条记录进行修改操作!<br>现在你选择了【" + v_selVOs.length+"】条!");
			return;  //
		}

		var v_selVO = v_selVOs[0]; //第一行数据
		var v_pkvalue = v_selVO[_gridOption.templet_option.pkname];  //主键值
		var v_templetcode = _gridOption.templet_option.templetcode;  //模板编码
		var v_urlpar = "_templetcode=" + v_templetcode + "&_pkvalue=" + v_pkvalue + "&_gridname=" + _gridname;  //
		BIONE.commonOpenFullDialog("修改", v_templetcode, v_context + "/frs/yuformat/updategrid?" + v_urlpar);
	}
	
	//删除单表数据
	YuFORMAT.onDELETE_OneGrid = function (_grid, _gridOption, _gridname) {
		var v_selVOs = _grid.getSelectedRows();  //得到选中行的记录!getSelectedRows
		if(v_selVOs==null || v_selVOs.length<=0){
			YuFORMAT.tip('必须选择一条记录进行操作!!');
			return;  //
		}
		if(v_selVOs.length > 1){
			YuFORMAT.tip("只能选择一条记录进行修改操作!<br>现在你选择了【" + v_selVOs.length+"】条!");
			return;  //
		}
		var v_selVO = v_selVOs[0]; //第一行数据
		$.ligerDialog.confirm('你真的要删除这条记录吗?', function (_yes) {
			if(_yes) {
				var v_templetcode = _gridOption.templet_option.templetcode;  //模板编码
				var v_tablename = _gridOption.templet_option.savetable;  //表名
				var v_pkname = _gridOption.templet_option.pkname;  //主键名
				var v_pkvalue = v_selVO[_gridOption.templet_option.pkname];  //主键值
				var v_pas = { _templetcode:v_templetcode,_tablename:v_tablename,_pkname:v_pkname, _pkvalue:v_pkvalue};  //
				YuFORMAT.remoteCall("/deletegrid", v_pas, function(_rt) {
					window.refreshData(_gridname);  //刷新一下数据
					YuFORMAT.tip(_rt.msg);  //提示结果
				},false);
			}
		}); 
	}
	
	//配置模板数据
	YuFORMAT.onCONFIG_TEMPLET = function(_grid, _gridOption){
		var gridCode = _gridOption.templet_option.templetcode;//
		if(gridCode == "rpt_YuFORMAT_formatframe" || gridCode == "rpt_YuFORMAT_formattemplet" || gridCode == "rpt_YuFORMAT_formattemplet_b"){
			BIONE.tip("不需要配置...", 2500);//
			return;//
		}
		//rpt_YuFORMAT_formattemplet
		BIONE.commonOpenFullDialog('配置', "rpt_YuFORMAT_formattemplet", v_context+'/frs/yuformat/configIndex?templetCode=rpt_YuFORMAT_formattemplet&templetid=' + _gridOption.templet_option.templetid);
	}
	
	//上移
	YuFORMAT.onMOVE_UP = function(_grid, _gridOption){
		YuFORMAT.onMOVE_UP_DOWN(_grid, _gridOption, "上移");  //
	};
	
	YuFORMAT.onMOVE_DOWN = function(_grid, _gridOption){
		YuFORMAT.onMOVE_UP_DOWN(_grid, _gridOption, "下移");  //
	};
	
	//上下移动
	YuFORMAT.onMOVE_UP_DOWN = function(_grid, _gridOption, _type){
		var jso_selRowData = _grid.getSelectedRow();  //选中行的数据!
		if(jso_selRowData == null){
			YuFORMAT.tip("请选择一行数据");
			return;
		}
		var row_index = jso_selRowData.__index + 1;  //第几行!因为从从零开始的,所以加一
		var array_gridData = _grid.options.data.Rows; //所有行的数据
		
		if(_type=="上移"){
			if(row_index == 1){  ////判断选中行是否是第一行
				YuFORMAT.tip("已经是第一行了");
				return;   //
			}
		}else if(_type=="下移"){
			if(row_index == array_gridData.length){  ////判断选中行是否是第一行
				YuFORMAT.tip("已经是最后一行了");
				return;   //
			}
		}

		var str_tablename = _gridOption.templet_option.savetable;  //表名
		var str_pkname = _gridOption.templet_option.pkname;  //主键名
		var needUpdate=[];  //需要添加的!		

		for(var i=1;i<= array_gridData.length;i++){  //遍历各行数据!
			var str_idvalue = array_gridData[i-1][str_pkname];  //主键值
			var li_seq = array_gridData[i-1].seq;  //排序字段值
			var li_newSeq;  //理论是应该正确的seq号!
			
			if(_type=="上移"){
				if(i == (row_index-1)){ //如果是上一行
					li_newSeq = row_index;  //应该是新的行号
				} else if(i == row_index) {  //如果是本行,应该是减一，因为要上移了!
					li_newSeq = row_index - 1;
				} else {
					li_newSeq = i;  //就是第几行!
				}
			}else if(_type=="下移"){
				if(i == (row_index)){ //如果是本行,则加上,因为要上移!
					li_newSeq = row_index + 1;  //
				} else if(i == row_index + 1) {  //如果是下一行
					li_newSeq = row_index;
				} else {
					li_newSeq = i;  //就是第几行!
				}
			}
			if(li_seq != li_newSeq){  //如果实际值与应该的理论值不等,则处理
				var v_update = {"_idvalue":str_idvalue, "_seq": li_newSeq};
				needUpdate.push(v_update);
				array_gridData[i-1].seq = li_newSeq;  //页面是设置新值!
			}
		}
		
		var v_pars = {_tablename:str_tablename, _pkname:str_pkname,_seqs:needUpdate};
		var jso_gridoption = YuFORMAT.doClassMethodCall("com.yusys.bione.plugin.yuformat.web.MoveUpDownSeq","moveUpDownSeq",v_pars); 
		
		if(_type=="上移"){
			_grid.up(jso_selRowData);  //上移
		} else if(_type=="下移"){
		   _grid.down(jso_selRowData);  //上移
	    }
	};
	
	//通用快速复制--单条、多条复制都走这里
	YuFORMAT.onCOPY_RECODER = function(_grid, _gridOption, _gridname){
		var rowDatas = _grid.getSelectedRows();
		if(rowDatas == null || rowDatas.length <= 0){
			YuFORMAT.tip('必须选择一条记录进行操作！！');
			return;  //
		}
		var from_table = _gridOption.templet_option.fromtable;
		var pk_name = _gridOption.templet_option.pkname;//获取主键名
		var pv_vals = [];
		for(var i=0;i<rowDatas.length;i++){
			pv_vals.push(rowDatas[i][pk_name]);
		}
		$.ligerDialog.prompt('请输入要复制的数量', function (conform, value){ 
			if(conform){
				if(parseInt(value.trim()) == value){//判断输入的是否是正整数
					//发起请求
					var v_pars = {quantity : value, fromTable : from_table, pk_name : pk_name, pk_values : pv_vals};
					var jso_rt = YuFORMAT.doClassMethodCall("com.yuchengtech.YuFORMAT.base.utils.YuFORMATBSUtil2","quickCopy",v_pars);  //以前杨跃放的位置在CopyPageAndPanelData中,这不对,移到通用工具类中!
					YuFORMAT.tip(jso_rt.message);  //
					window.refreshData(_gridname);
				}else{
					YuFORMAT.tip("请输入正整数！！");
				}
			}
		});
	}
	
	//通用万通的调用!反射!
	//比如var v_rt = YuFORMAT.doClassMethodCall("com.yuchengtech.YuFORMAT.base.utils.YuFORMATBSUtil2","testMethod",v_sql);
	//这个方法的好处是,不要再折腾Control中的映射了,对应参数名，如果改参数名或加参数,则要改一堆,还要去文本搜索寻找对应的路径,现在从入口可以直接看到是哪个类与方法!
	YuFORMAT.doClassMethodCall = function (_className, _methodName, _parJSO) {
		var v_parstr = JSON.stringify(_parJSO);  //将对象转换成字符串
		console.log("远程请求的参数【" + v_parstr + "】");
		var v_par = {_class:_className, _method:_methodName, _parstr : v_parstr};  //
		var v_returnData;  //返回值
		YuFORMAT.remoteCall("/doClassMethodCall", v_par, function(_rtData) {
			v_returnData = _rtData;  //
		},false);  //同步,必须做完才返回
		
		if(typeof v_returnData.Exception != "undefined" || v_returnData.Exception != null){
			throw("发生服务器端异常:<br>\n" + v_returnData.Exception);  //重抛服务器端异常
		}else{
		   return v_returnData;  //
		}
	};
	
	//显得一个提示框
	YuFORMAT.showMsgDialog = function(_msg,_width,_height){
		var v_html =  "<div><p>" + _msg + "</p></div>";//
		var v_width = 850;
		var v_height = 400;  //
		if(_width != null){
			v_width = _width;
		}
		if(_height != null){
			v_height = _height;
		}
		
		$.ligerDialog.open({width:v_width, height:v_height,target:v_html});
	};
	
	//创建一个弹出的DIV,然后可以在这个div中加内容,比如放置图表
	YuFORMAT.createModalDiv = function(_id,_width,_height,_left,_top) {
	    var dom_div = document.createElement("div");
		dom_div.setAttribute("id",_id);
		dom_div.style.position="absolute";
		dom_div.style.zindex="1000";
		dom_div.style.width=_width + "px";
		dom_div.style.height=_height + "px";
		dom_div.style.left=_left + "px";
		dom_div.style.top=_top + "px";
		dom_div.style.border="1px solid #AAAAAA";
	
		//dom_div.style.filter="Alpha(opacity=50)";
		//dom_div.style.opacity="0.7";  //50%透明
		//dom_div.innerHTML=""; 
		document.body.appendChild(dom_div);  //
		return dom_div;  //
    }

	//把一个异常输出来!
	YuFORMAT.showExDialog = function(_excep){
		if(typeof _excep == "string"){  //如果是字符串,即服务器端生成的!则直接提示
			YuFORMAT.showMsgDialog(_excep);
		} else { //如果是UI端生成的,则将堆栈输出,并根据at进行换行处理，看起来更清爽一点
			var array_stack = _excep.stack.split(" at ");
			var v_msg = "";
			for(var i=0; i<array_stack.length; i++){
				if(i==0){
					v_msg = v_msg + array_stack[i] + "<br>\n";
				} else{
					v_msg = v_msg + "at " +  array_stack[i] + "<br>\n";
				}
			}
			YuFORMAT.showMsgDialog(v_msg);
		}
	}
			
	//执直接执行一个或多条SQL
	YuFORMAT.executeUpdateSQL = function(_parSQLs){
		if(!(_parSQLs instanceof Array)){
			_parSQLs = [_parSQLs]; //
		}
		var v_sqls = [];  //要同时删除主子表的数据!!
		for(var i=0; i<_parSQLs.length; i++){
			v_sqls.push({ _sql : _parSQLs[i]});  //
		}
		var v_par = { _sqls : v_sqls };
		var jso_rt = YuFORMAT.doClassMethodCall("com.yuchengtech.YuFORMAT.base.utils.YuFORMATBSUtil2","executeSQL",v_par);  //
		return jso_rt;  //
	}
	
	

	/**
	 * 根据SQL语句弹出单表的dialog
	 * dialog_title   : 弹出框标题
	 * search_sql       : 查询语句
	 * back_fun  : 回调方法名称
	 * field_name     : 控件名称（如果是通过点击表单控件弹出dialog 如列表参照对应的field name）
	 * value_field     : 取值存值字段
	 * text_field     : 显示名称字段
	 * cols_width     : 表格各列的宽度
	 */
	YuFORMAT.openSQLGridDialog = function(v_dialog_title, v_search_sql, v_back_fun, v_field_name, v_value_field, v_text_field, v_cols_width,_editForm,_winWid,_winHei, defineOption){
		var v_pars = {dialog_type : "SQL", dialogtitle : v_dialog_title,  datasql : v_search_sql, callbackfn : v_back_fun, fieldname : v_field_name, valuefield : v_value_field, textfield : v_text_field, colwidth:v_cols_width, editform:_editForm, defineOption : defineOption };//第一步：将参数添加到winObjs中
		//弹出到通用的单列表dialog
		YuFORMAT.openClientDialog("dialog_grid.jsp", v_pars,_winWid,_winHei);
	}
	
	//找到某一条配置
	YuFORMAT.getItemOption = function(_options_b,_itemkey){
		for(var r in _options_b){
			if(_options_b[r].itemkey == _itemkey){  //找到对应的模板定义
				return _options_b[r];  //
			}
	    }
	};
	
	//根据模板编码打开窗口
	YuFORMAT.openTemplateDialog = function(v_code, v_jsFilePath, v_btns, v_textitem, v_valueitem, v_name_backfun, v_fieldName,_winWid,_winHei){
		var v_pars = {"code" : v_code, "jsFilePath" : v_jsFilePath, "btns" : v_btns, "textitem" : v_textitem, "valueitem" : v_valueitem, "backfun" : v_name_backfun, "fieldname" : v_fieldName};
		YuFORMAT.openClientDialog("formatdialog.jsp", v_pars,_winWid,_winHei);
	}
	
	
	//打开一个列表窗口
	YuFORMAT.openGridDialogByCode = function(v_dialogtitle, v_templetCode, v_callbackfn, v_rtfield, v_sqlwhere, v_dialog_type){
		var v_pars = {dialogtitle:v_dialogtitle, templetCode:v_templetCode, callbackfn:v_callbackfn, rtfield:v_rtfield, sqlwhere:v_sqlwhere, dialog_type:v_dialog_type};
		YuFORMAT.openClientDialog("dialog_grid.jsp", v_pars);
	}
	
	//打开两个列表窗口
	YuFORMAT.openGridGridDialogByCode = function(v_dialogtitle, v_templetCode1, v_templetCode2, v_callbackfn, v_rtfield, v_sqlwhere, v_dialog_type){
		var v_pars = {dialogtitle:v_dialogtitle, templetCode1:v_templetCode1, templetCode2:v_templetCode2, callbackfn:v_callbackfn, rtfield:v_rtfield, sqlwhere:v_sqlwhere, dialog_type:v_dialog_type};
		YuFORMAT.openClientDialog("dialog_gridgrid.jsp", v_pars);
	}
	
	//打开树表窗口
	YuFORMAT.openTreeGridDialog = function (v_par){
		YuFORMAT.openClientDialog("dialog_treegrid.jsp", v_par);
	}
	
	//打开树型窗口
	YuFORMAT.openTreeDialog = function(v_par,_width,_height){
		YuFORMAT.openClientDialog("dialog_tree.jsp", v_par,_width,_height);
	}
	
	
	//测试 用结构树弹出参照
	//fromtable    树机构表的表名
	//idtext	   id字段
	//upidtext	   upid字段
	//showtext     返回的显示字段
	//valuetext    返回的value字段
	YuFORMAT.openTestTreeDialog = function(fromtable, idtext, upidtext, nametext, showtext, valuetext){
		var v_pars = {"fromtable" : fromtable, "idtext" : idtext, "upidtext" : upidtext, "nametext" : nametext, "showtext" : showtext, "valuetext"  : valuetext};
		YuFORMAT.openClientDialog("dialog_testsingletree.jsp", v_pars);
	}
	
	//直接打开一个绝对路径的窗口
	//YuFORMAT.openPathDialog(window.v_context,"/report/YuFORMAT/base/tablelinktree.jsp",par_jso,800,600);
	YuFORMAT.openPathDialog = function (_context,_jspPath, v_par,_width,_height,_title) {  //
		var d = new Date();
		var v_currtime = "" + (d.getMonth() + 1) + d.getDate() + d.getHours() + d.getMinutes() + d.getSeconds() + d.getMilliseconds();  //精确到毫秒,一年内肯定不会重复
		var v_dialogid = "dialogid" + v_currtime;
		var v_winobj = YuFORMAT.getWinObj(window.self);  //找到winobj对象
		v_winobj[v_dialogid] =  v_par;  //在弹出新窗口之前先送入全局变量，然后在弹出窗口中再取得!
		var dailog_width = 850;
		var dailog_height = 500;
		if(_width){
			console.log((typeof _width));
			if((typeof _width)=="string" && _width.indexOf("%")>0){
				var v_per = _width.substring(0,_width.length-1);
				var all_winWidth = document.body.clientWidth;
				dailog_width = (all_winWidth*v_per)/100;
			}else{
				dailog_width = _width;
			}
		}
		if(_height){
			dailog_height = _height;
		}

		var str_title = "弹出窗口";//"弹出窗口-[" + _jspPath + "]";
		if(_title!=null){
			str_title = _title; 
		}
		BIONE.commonOpenDialog(str_title, v_dialogid, dailog_width,dailog_height, _context + "/frs/yuformat/openPathDialog?jspname=" + _jspPath + "&dialogid=" + v_dialogid);  //打开一个窗口
	}

	//打开表关联的树型展示窗口
	YuFORMAT.openTableLinkTreeJspDialog = function (_linkname,_width,_height) {  //
		var par_jso={linkname:_linkname};
		YuFORMAT.openPathDialog(window.v_context,"/report/YuFORMAT/base/tablelinktree.jsp",par_jso,_width,_height);  //打开窗口!
	}

	//根据dialogId,获得弹出窗口的入参
	//在弹出的jsp中,先获得 var v_id = "${dialogid}"; ，然后根据这个id得到入参!!
	YuFORMAT.getDialogParameter = function(_dialogId){
		var v_winobj = YuFORMAT.getWinObj(window.self);  //找到winobj对象
		var v_par = v_winobj[_dialogId]; 
		return v_par;
	}

	//打开一个客户端窗口,非常重要，就是不在server转参数了,而是在客户端存储
	//使用方法是,在js中直接调用此函数,打开一个jsp,然后传入复杂对象
	//然后在对应的jsp中,使用下面方法得到传入的参数,即平台会生成一个唯的id,然后会自动传给jsp,然后根据这个id从根页面的全局变量中取得打开窗口之前注册的参数!
	//var v_dialogid = '${dialogid}';
	//var v_winobj = YuFORMAT.getWinObj(window.self);
	//var v_dialogOption = v_winobj[v_dialogid]; 
	YuFORMAT.openClientDialog = function (_jspname, v_par,_width,_height) {  //
		var d = new Date();
		var v_currtime = "" + (d.getMonth() + 1) + d.getDate() + d.getHours() + d.getMinutes() + d.getSeconds() + d.getMilliseconds();  //精确到毫秒,一年内肯定不会重复
		var v_dialogid = "dialogid" + v_currtime;
		var v_winobj = YuFORMAT.getWinObj(window.self);  //找到winobj对象
		v_winobj[v_dialogid] =  v_par;  //在弹出新窗口之前先送入全局变量，然后在弹出窗口中再取得!
		var dailog_width = 850;
		var dailog_height = 500;
		if(_width){
			dailog_width = _width;
		}
		if(_height){
			dailog_height = _height;
		}
		BIONE.commonOpenDialog(v_par.dialogtitle, v_dialogid, dailog_width,dailog_height, v_context + "/frs/yuformat/openClientDialog?jspname=" + _jspname + "&dialogid=" + v_dialogid);  //打开一个窗口
	}
	
	//考虑到以后会弹出N层的窗口,但只有最顶层上存储变量,所以需要一个不断往上寻找的过程
	//也就是说，只有最顶层的jsp中才有winobj这个对象,然后其他任意层中的js代码都是通过这个函数来取,这样才不会乱!
	YuFORMAT.getWinObj = function(_window){
		if(typeof (_window.winobj) == "undefined"){  //如果定义了这个对象
			if(_window == _window.top) {  //如果已经到了顶层,则不继续往上找了,直接抛异常
				throw("找到顶层也没发现winobj对象");  //
			} else {
			    return YuFORMAT.getWinObj(_window.parent);  //继续往上一层找
			}
		}else{
			return _window.winobj;
		}
	}
	
	//获得登录人员相关信息,比如所属机构,用户名,服务器时间等..
	//_type参数是以后用来判断想取某种大类的信息，比如有时是取用户/机构相关的，有时是取系统内存,server时间等,为了统一处理，想合成一个函数算了! 如果都取则性能又慢，所以要有个类型参数
	YuFORMAT.getLoginUserInfo = function(_type){
		//var str_token = window.localStorage.getItem("token"); //
		//console.log("token=[" + str_token + "]");  //
		var v_pas = {type: _type};
		var jso_rt = YuFORMAT.doClassMethodCall("com.yuchengtech.YuFORMAT.base.utils.YuFORMATBSUtil2","getLoginUserInfo",v_pas);
		return jso_rt; //
	}


	//取得对象!
	YuFORMAT.getWinObjItemValue = function(_window,_key){
		var v_winobj = YuFORMAT.getWinObj(_window);
		return v_winobj[_key];
	}

	
	/**
	 * 远程访问公共方法（限制路径前缀为YuFORMAT/base/format）
	 * @param _actname 请求路径后缀
	 * @param _pars ajax请求参数
	 * @param _callback ajax请求成功回调函数，回调函数的参数为ajax请求返回的数据，（contronller中返回的data）
	 * @param _isAsync 同步/异步
	 */
	YuFORMAT.remoteCall = function (_actname, _pars,_callBack,_isAsync){
		var isAsync = false;
		if(_isAsync){
			isAsync = true;
		}
		$.ajax({
		  cache : false,
		  async : isAsync,
		  url : v_context + "/frs/yuformat" + _actname,
		  dataType : "json",
		  type : "post",
		  data : _pars,
		  success : _callBack,
		  error : function(result, b) {
		    BIONE.tip('发现系统错误 <BR>错误码：' + result.status);  //
		  }
		});
	}

	// 创建按钮
	YuFORMAT.createButton = function(options) {

		var p = $.extend({
			appendTo : $('body')
		}, options || {});

		if (p.operNo && top.window["protectedResOperNo"]) {

			if ($.inArray(p.operNo, top.window["protectedResOperNo"]) != -1) {
				// 说明改按钮收权限保护，需要判断当前登录用户是否具有权限
				if (!top.window["authorizedResOperNo"]
						|| $.inArray(p.operNo,
								top.window["authorizedResOperNo"]) == -1) {
					return;
				}
			}
		}
		// l-toolbar-item l-panel-btn l-toolbar-item-hasicon
		var hasIcon = false;
		if (p.icon) {
			hasIcon = true;
		}
		// var btn = $('<div class="l-btn l-btn-hasicon"><div
		// class="l-btn-l"></div><div class="l-btn-r"> </div>
		// <span></span></div>');
		var btnHtml = '<div class="l-btn';
		if (hasIcon) {
			btnHtml = btnHtml + ' l-btn-hasicon';
		}
		btnHtml += '"><div class="l-btn-l"></div><div class="l-btn-r"> </div> <span></span></div>';
		var btn = $(btnHtml);
		if (p.icon) {
			btn.append('<div class="l-icon l-icon-' + p.icon + '"> </div> ');
		}

		if (p.width) {
			btn.width(p.width);
		}
		if (p.click) {
			btn.click(p.click);
		}
		if (p.text) {
			$("span", btn).html(p.text);
		}
		if (typeof (p.appendTo) == "string") {
			var space = 30;
			var iconwidth = 0;
			var prewidth = 0;
			var num = $(p.appendTo + ' .l-btn').length;
			p.appendTo = $(p.appendTo);
			if (num != 0)
				prewidth = p.appendTo.width();
			if (hasIcon)
				iconwidth = 10;
			p.appendTo.width(prewidth + parseInt(p.width) + space + iconwidth);
		}

		btn.appendTo(p.appendTo);
	};

	//根据模板定义中,某一个dom中动态创建一个查询框! 点击查询按钮时生成生成SQL条件，然后回调传入的函数!
	//临时先这么搞，以后应该封装一个BillQueryPanel的JS类!使用闭包等功能，做一个强大的查询控件,可以定义查询控件类型(比如下拉框,参照)，还可定义查询语义生成方式，比如in(),like or,自定义类等等
	//总之，查询面板是一个非常重要的组件,应该既可以与表格控件自动绑在一起，也可以单独拿出来当个工具类用,它提供强大拼装SQL条件功能,会极大提高开发效率...
	YuFORMAT.buildSearchForm = function(_dom, _items, _searchFns){
		var str_html="";
		str_html = str_html + "<table border=\"0\" width=\"100%\">";
		var v_row = parseInt(_items.length/3);  //一行放3个
		var v_fix = _items.length%3; //
		var v_realRow = v_fix>0?v_row+1:v_row;  //实际行

		//按钮的申明,因为下面两处要用，所以事先定义下
		var str_btnhtml = "<input id=\"searchbtn\" type=\"button\" value=\" 查 询 \" style=\"width:85px;height:23px\">&nbsp;<input id=\"resetbtn\" type=\"button\" value=\" 重 置 \" style=\"width:80px;height:22px\">";

		//遍历各行
		for(var i=0;i<v_row;i++){
			str_html = str_html + "<tr style=\"height:30px\">";
			for(var j=0;j<3;j++) {  //3列
				var li_pos = i*3+j;  //坐标位置
				var str_itemkey = _items[li_pos].itemkey;
				var str_itemname = _items[li_pos].itemname;
				str_html = str_html + "<td style=\"text-align:right\">" + str_itemname + ":&nbsp;</td><td><input id=\""  + str_itemkey + "\" type=\"text\"></td>";
			}

			if(i==0){  //在第一行后面加上按钮
				str_html = str_html + "<td rowspan=\"" + v_realRow + "\">" + str_btnhtml + "</td>";
			}
			str_html = str_html + "</tr>";
		}

		//最后一行!
		if(v_fix>0){
			str_html = str_html + "<tr style=\"height:30px\">";
			for(var i=0;i<v_fix;i++){
				var li_pos = v_row*3+i;  //坐标位置
				var str_itemkey = _items[li_pos].itemkey;
				var str_itemname = _items[li_pos].itemname;
				str_html = str_html + "<td style=\"text-align:right\">" + str_itemname + ":&nbsp;</td><td><input id=\""  + str_itemkey + "\" type=\"text\"></td>";
			}
			for(var i=0;i<(3-v_fix);i++){
				str_html = str_html + "<td>&nbsp;</td><td>&nbsp;</td>";  //补下差额，否则表格错位不完整
			}

			if(v_row==0){  //如果只有0行,即只有余数,则前面是生不成按钮的，要在这里补上!
				str_html = str_html + "<td>" + str_btnhtml + "</td>";
			}
			str_html = str_html + "</tr>";
		}
		str_html = str_html + "</table>";
		//console.log(str_html);
		
		_dom.innerHTML=str_html;  //在Dom上打上去...

		var dom_searchbtn = _dom.querySelector("#searchbtn");  //找到查询按钮!
		dom_searchbtn.onclick=function(){  //按钮点击事件,自动拼好SQL条件,然后调用回调函数!
			var str_sqlCons = "";
			for(var i=0; i<_items.length; i++){
				var dom_item = _dom.querySelector("#" + _items[i].itemkey);  //找到这个文本框!
				var str_value = dom_item.value;  //
				if(str_value!=null && str_value.trim()!=""){
					str_sqlCons = str_sqlCons + "and " + _items[i].itemkey + " like '%" + str_value + "%' ";
				}
			}
			if(str_sqlCons==""){
				str_sqlCons = "9=9";
			}else{
				str_sqlCons = str_sqlCons.substring(4,str_sqlCons.length);
			}

			if(_searchFns){
			  _searchFns(str_sqlCons);
			}
		};

		var dom_resetbtn = _dom.querySelector("#resetbtn");  //重置
		dom_resetbtn.onclick=function(){
			for(var i=0; i<_items.length; i++){
				var dom_item = _dom.querySelector("#" + _items[i].itemkey);  //找到这个文本框!
				dom_item.value="";  //清空了
			}
		};
	}
	
	//查看表单数据
	YuFORMAT.onShowData = function(v_form,_formId){
		var innerTarget = "<div><div id='dialogGrid'></div></div>";//
		//console.log(v_form.options.fields);//
		var v_form_fields = v_form.options.fields;//
		var v_data_form = v_form.getData();//
		var data_diaGrid = [];//
		for(var v_f in v_form_fields){
			var v_field = v_form_fields[v_f];//
			var v_display_field = v_field.display;//
			var v_name_field = v_field.name;//
			var v_type_field = v_field.type;//
			var v_val_field = v_data_form[v_name_field];//
			var v_perRow = {};//
			v_perRow["ipt_display"] = v_display_field;//
			v_perRow["ipt_name"] = v_name_field;//
			v_perRow["ipt_type"] = v_type_field;//
			//console.log(typeof v_val_field);
			if(v_val_field != null && typeof v_val_field == "String" && v_val_field.indexOf("<") >= 0){
				var eg_1 = new RegExp("<", "g");//匹配所有的<
				var eg_2 = new RegExp("</", "g");//匹配所有的</
				var eg_3 = new RegExp("/>", "g");//匹配所有的/>
				var eg_4 = new RegExp(">", "g");//匹配所有的>
				//注意替换的顺序
				v_val_field = v_val_field.toString().replace(eg_2, "【");//替换
				v_val_field = v_val_field.toString().replace(eg_1, "【");//替换
				v_val_field = v_val_field.toString().replace(eg_3, "】");//替换
				v_val_field = v_val_field.toString().replace(eg_4, "】");//替换
			}
			//console.log(v_val_field);//
			v_perRow["ipt_val"] = v_val_field;//
			data_diaGrid.push(v_perRow);//
		}
		var v_target = $(innerTarget);//
		$.ligerDialog.open({
			width : 800,
			height : 400,
			title : "查看表单数据",
			target : v_target
		});// 

		var diaGrid = $("#" + _formId).ligerGrid({
			width : 770,
			height : 365,
			columns : [
			           { display : "显示名称", name : "ipt_display", align : "left" },
			           { display : "保存名称", name : "ipt_name", align : "left" },
			           { display : "控件数值", name : "ipt_val", align : "left" },
			           { display : "控件类型", name : "ipt_type", align : "left" }
			           ],
			data : { Rows : data_diaGrid },
			pageSize : 15,
			checkbox : false
		});
	}


	//templetcode fieldname templet_bid
	//先从后台获取到fieldName对应的主键
	YuFORMAT.editTempletField = function(_context,v_templetcode,v_fieldname){
		var v_pars = {_templetcode : v_templetcode, _fieldname : v_fieldname};
		var json_pkvalue = YuFORMAT.doClassMethodCall("com.yusys.bione.plugin.yuformat.web.GetFormData","getFieldPKvalue",v_pars);  //取得模板主键
		var v_templet_bid = json_pkvalue.templet_bid;//rpt_YuFORMAT_formattemplet_b主键
		var v_url = _context + "/frs/yuformat/editformfield?templetcode=" + v_templetcode + "&fieldname=" + v_fieldname + "&templet_bid=" + v_templet_bid;
		console.log("url:" + v_url);
		BIONE.commonOpenLargeDialog("编辑", "rpt_YuFORMAT_formattemplet_b", v_url);
	}

	//添加搜索重置按钮
	YuFORMAT.addSearchButtons = function(v_dataSql, liger_search, form, grid, btnContainer, searchClick) {
		if (!form)
			return;
		form = $(form);
		if (btnContainer) {
			YuFORMAT.createButton({
				appendTo : btnContainer,
				text : '搜索',
				icon : 'search3',
				width : '50px',
				click : function() {
					var sql = "";//
					for(var v in liger_search.options.fields){
						var field = liger_search.options.fields[v];//
						var name_field = field.name;//
						var val_field  = $.ligerui.get(name_field).getValue();//
						var op_attr_field = field.attr.op;//
						
						if(name_field != null && val_field != null && op_attr_field !=  null && name_field != "" && val_field != "" && op_attr_field != "" ){
							sql += " and "+name_field+" like '%"+val_field+"%'";//
						}else{
							//
						}
					}
					var full_sql = v_dataSql;//
					if(v_dataSql.indexOf("where") > 0){//如果已经存在where条件
						if(v_dataSql.indexOf("order") > 0){//如果已经存在order条件
							var v_order = v_dataSql.substring(v_dataSql.indexOf("order"), v_dataSql.length);//
							full_sql = v_dataSql.substring(0, v_dataSql.indexOf("order"))+sql+" "+v_order
						}else{
							full_sql = v_dataSql + sql;//
						}
					}else{//如果不存在where条件
						if(v_dataSql.indexOf("order") > 0){//如果已经存在order条件
							var v_order = v_dataSql.substring(v_dataSql.indexOf("order"), v_dataSql.length);//
							full_sql = v_dataSql.substring(0, v_dataSql.indexOf("order")) + " where 1=1"+sql+" "+v_order
						}else{
							full_sql = v_dataSql + " where 1=1" + sql;//
						}
					}
					searchClick(full_sql);//
				}
			});
			YuFORMAT
					.createButton({
						appendTo : btnContainer,
						text : '重置',
						icon : 'refresh2',
						width : '50px',
						click : function() {
							$(":input", form)
									.not(
											":submit, :reset,:hidden,:image,:button, [disabled]")
									.each(function() {
										$(this).val("");
									});
							$(":input[ltype=combobox]", form)
									.each(
											function() {
												var ligerid = $(this).attr(
														'data-ligerid'), ligerItem = $.ligerui
														.get(ligerid);// 需要配置comboboxName属性
												if (ligerid && ligerItem
														&& ligerItem.clear) {// ligerUI
													// 1.2
													// 以上才支持clear方法
													ligerItem.clear();
												} else {
													$(this).val("");
												}
											});
							$(":input[ltype=select]", form)
									.each(
											function() {
												var ligerid = $(this).attr(
														'data-ligerid'), ligerItem = $.ligerui
														.get(ligerid);// 需要配置comboboxName属性
												if (ligerid && ligerItem
														&& ligerItem.clear) {// ligerUI
													// 1.2
													// 以上才支持clear方法
													ligerItem.clear();
												} else {
													$(this).val("");
												}
											});
						}
					});
		}
		};
	
	
		YuFORMAT.tip = function(_message){
			$.ligerDialog.success(_message);
		};
		
		//弹出一段html，比如是个表格
		YuFORMAT.tipHtml = function(_title,_html,_width,_height){
			 $.ligerDialog.open({
				width : _width,
				height : _height,
				title : _title,
				target : _html
			});
		};


		//函数未定义
		YuFORMAT.nofunction = function(){
			alert("按钮没有定义逻辑!或定义的函数名没有对应到js文件中!");  //
		};
		
		
})(jQuery);
