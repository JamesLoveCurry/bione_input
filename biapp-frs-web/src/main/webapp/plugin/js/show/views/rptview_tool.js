/*
 * 报表excel公式计算JS文件  
 * author: yangyf
 * */

(function($) {
	function View(targetSelector, settings) {
		var viewObj = new Object();
		viewObj.spread = {};
		viewObj.spreadDom = {};
		viewObj.Utils = {};
		viewObj._Json = null;
		viewObj._timeoutHandler = null;
		viewObj._settings = {};
		viewObj._cellInfos = {};
		viewObj._validCellType = ["02" , "03" , "04" , "05"];
		viewObj._SelectionModule = {};
		viewObj._defaults = {
				searchArgs : null , // 查询条件 , 格式:{dim1:[val1,val2],dim2:[val1],...}(同查询引擎)
				targetHeight : null , // target总高度
				ctx : "" ,  // 所处jsp的全局contextpath 
				readOnly : true , // 是否只读模式(若是只读模式，单元格不可编辑 )
				contextMenu : false ,	//是否有右键菜单
				cellDetail : false	 ,  // 是否有单元格明细
				canUserEditFormula : true , // 是否可以编辑公式
				tabStripVisible : false , // 是否显示底部sheets' tab信息
				isloadSave : false,
				visibleCellNos : null , // 可见的单元格编号集合
				inValidMap : null , // 校验未通过的信息集合，格式: [{cellNo:单元格编号,remindColor:提醒颜色(6位)} , {...}]
				initFromAjax : true , // 自动ajax后台获取数据
				url : "/report/frame/tmp/view/getViewInfo?d="+new Date().getTime(),
				methodType : "POST",
				ajaxData : {} , // ajax参数，目前支持rptId:报表id；dataDate:数据日期 ;busiLineId 业务条线Id,fileName 文件
				onEnterCell : null , // 选中单元格事件
				onLeaveCell : null , // 移出单元格事件
				onCellDoubleClick : null ,  // 双击单元格事件
				onEditEnded : null ,// 编辑完成事件
				loadCompleted : null//加载完成回调函数
		};
		
		//初始化方法
		viewObj.init = function(targetSelector , settings){
			var target = null;
			if(targetSelector == null 
					|| (typeof targetSelector != "object"
						&& typeof targetSelector != "string")){
				return {};
			}
			if(typeof targetSelector == "string"){
				target = $(targetSelector);
			} else {
				target = targetSelector;
			}
			//参数处理
			this._settings = this._initSettings(settings);
			this._settingHandler(target, this._settings);	
		};

		// 初始化参数
		viewObj._initSettings = function(settings){
			var properties = this._defaults;
			if(settings
					&& typeof settings == "object"){
				for(var p in settings){
					if(p in properties){
						properties[p] = settings[p];
					}
				}
			}
			return properties;
		};

		viewObj._settingHandler = function(target , settings){
			if(this._settings.targetHeight != null
					&& this._settings.targetHeight != ""){
				target.height(this._settings.targetHeight);
			}
			this.spreadDom = this._initSpreadDoom(target);
			
			// 初始化spread上下文环境
			this.spread = new GC.Spread.Sheets.Workbook(this.spreadDom[0], {
		        sheetCount: 1,
		        newTabVisible: false,
		        tabStripVisible: this._settings.tabStripVisible,
		        highlightInvalidData: true,
		        allowCopyPasteExcelStyle: true,
		        allowContextMenu: false,
		        enableFormulaTextbox: true
		    });
	        // 允许单元格文字溢出
			this.spread.getActiveSheet().options.allowCellOverflow = true;
			// 初始化依赖组件
			this.resize(this.spread);
			this.ajaxJson(null,null,true);
		};
		
		// 初始化报表指标数据
		viewObj.ajaxJson = function(inValidMap,fileName,init,url){
			if(this._settings.initFromAjax === true
					&& this._settings.ctx != null
					&& this._settings.ctx != ""
					&& this._settings.url != null
					&& this._settings.url != ""){
				var ajaxData = this._settings.ajaxData;
				if(ajaxData == null){
					ajaxData = {};
				}
				ajaxData.readOnly = this._settings.readOnly;
				if(this._settings.searchArgs
						&& this._settings.searchArgs != ""){					
					ajaxData.searchArgs = this._settings.searchArgs;
				}
				if(this._settings.visibleCellNos
						&& this._settings.visibleCellNos != ""){
					ajaxData.visibleCellNos = this._settings.visibleCellNos;
				}
				if(inValidMap!=null){
					ajaxData.inValidMap = inValidMap;
					this._settings.inValidMap=inValidMap;
				}
				if(fileName!=null){
					ajaxData.fileName = fileName;
				}
				else if(this._settings.inValidMap
						&& this._settings.inValidMap != ""){
					ajaxData.inValidMap = this._settings.inValidMap;
				}
				if(url == null){
					url = this._settings.url;
				}
				$.ajax({
					cache : false,
					async : true,
					url : this._settings.ctx +url ,
					dataType : 'json',
					data : ajaxData,
					type : this._settings.methodType,
					beforeSend : function() {
						BIONE.showLoading();
					},
					success : function(result){
						if(result){
							var errorMsg = result.error;
							if(errorMsg != null
									&& errorMsg != ""
									&& typeof errorMsg != "undefined"){
								BIONE.tip(errorMsg);
							}else{
								var view_tool = $("#spread"+ result.rptId + result.dataDate + result.orgNo).data('view_tool');
								if(init!=null&&init==true){
									view_tool._Json = result.json;
								}
								var jsonStr = result.json;
								var cellInfo = result.cellInfo;
								var dsInfo = result.dsInfo;
								view_tool._initCellInfos(cellInfo , dsInfo);
								view_tool.formulaCell = result.formulaCell;
								view_tool.fromJSON(jsonStr);
								if(view_tool._settings.isloadSave){
									view_tool._saveData(view_tool._settings.ajaxData, view_tool);
								}
							}
						}
						BIONE.hideLoading();
					},
					error:function(){
						BIONE.hideLoading();
					}
				});
			}
		};
		
		viewObj.initEditCell = function(){
			var currSheet = this.spread.getActiveSheet();
			for(var posi in this._cellInfos){
				var cellTmp = this._cellInfos[posi];
				var rowCol = this.posiToRowCol(posi);
				if (cellTmp.type&&cellTmp.type!=null&cellTmp.type=="09"){
					currSheet.setValue(rowCol.row , rowCol.col,cellTmp.value);
					currSheet.getCell(rowCol.row , rowCol.col).value=cellTmp.value;
				}else if (cellTmp.type&&cellTmp.type!=null&cellTmp.type=="02"){
					currSheet.setValue(rowCol.row , rowCol.col,cellTmp.value);
					currSheet.getCell(rowCol.row , rowCol.col).value=cellTmp.value;
				}
					
			}
		};
		
		viewObj._initCellInfos = function(cellInfo , dsInfo){
			var tmp = {};
			if(cellInfo
					&& typeof cellInfo == "object"
					&& dsInfo 
					&& typeof dsInfo == "object"){		
				for(var posi in cellInfo){
					var cellTmp = cellInfo[posi];
					if(dsInfo[cellTmp.cellNo] != null
							&& typeof dsInfo[cellTmp.cellNo] != "undefined"
							&& jQuery.inArray(dsInfo[cellTmp.cellNo].type , this._validCellType) != -1){
						var value=cellTmp.value;
						var cellNo=cellTmp.cellNo;
						jQuery.extend(cellTmp , dsInfo[cellTmp.cellNo]);
						cellTmp.value=value;
						cellTmp.cellNo=cellNo;
						tmp[posi] = cellTmp;
					}else{
						var value=cellTmp.value;
						var cellNo=cellTmp.cellNo;
						jQuery.extend(cellTmp, dsInfo[cellTmp.cellNo]);
						cellTmp.value=value;
						cellTmp.cellNo=cellNo;
						tmp[posi] = cellTmp;
					}
				}
			}
			this._cellInfos = tmp;
		};

		//初始化设计器doom相关
		viewObj._initSpreadDoom = function(target){
			var spreadDom = target;
			var heightTmp = spreadDom.height();
			var cellDetailDom ;
			if(this._settings.cellDetail === true){
				cellDetailDom = "<div id='_spreadCellDetail' style='width=100%;'></div>";
			}
			if(cellDetailDom != null){
				var domInner = "";
				domInner += cellDetailDom ? cellDetailDom : "";
				domInner += "<div id='_spread' style='100%'></div>";
				target.append(domInner);
				spreadDom = $("#_spread");
				spreadDom.height(heightTmp);
			}
			return spreadDom;
		};

		viewObj.resize = function(spread){
			if(spread 
					&& typeof spread == "object"){
				if(this._settings.cellDetail === true){
					$("#_contextbox").width(this.spreadDom.width() - $("#_positionbox").width() - 10);
				}
				spread.refresh();
			}
		};

		viewObj.fromJSON = function(json){
			if(json){
				// 导入样式默认认为清除了所有的已配置报表指标
				this._Json = json;
				this._fromJSONHandler();
			}
		};

		viewObj._fromJSONHandler = function(){
			if(this._Json){
				var jsonFormat = this._Json;
				if(typeof jsonFormat == "string"){
					jsonFormat = JSON2.parse(jsonFormat);
				}

				this.spread.fromJSON(jsonFormat);
			}
		};

		viewObj._accMul = function(arg1,arg2){
			var m=0;
			s1=arg1.toString(),s2=arg2.toString();
			try{m+=s1.split(".")[1].length}catch(e){}
			try{m+=s2.split(".")[1].length}catch(e){}
			var a1,a2;
			var dot=s1.indexOf(".");
			if(dot>=0){
				a1=Number(s1.substring(0,dot)+s1.substring(dot+1,s1.length));
			}
			else{
				a1=Number(s1);
			}
			dot=s2.indexOf(".");
			if(dot>=0){
				a2=Number(s2.substring(0,dot)+s2.substring(dot+1,s2.length));
			}
			else{
				a2=Number(s2);
			}
			var s=(a1*a2).toString();
			if(0 == m){
				return s;
			}else{
				return s.substring(0,s.length-m)+"."+s.substring(s.length-m,s.length);
			}
		};

		viewObj.getFormulaCellInfo = function(){
			var forumulaCellValues=[];
			if(this._settings.readOnly === true){
				// 只读模式，不会发生值变化
				return forumulaCellValues;
			}
			if(this.formulaCell){
				var currSheet = this.spread.getActiveSheet();
				for( var i in this.formulaCell ){
					// 只有权限内可编辑单元格，才进行值变化与否分析
					var rowCol = this.posiToRowCol(this.formulaCell[i].cellNo);
					var newVal = currSheet.getCell(rowCol.row , rowCol.col).value();
					if(newVal==null||!newVal||newVal._error){
						newVal = "0";
					}
					var newValStr = $.trim(newVal)+"";
					var cell = {cellNo:this.formulaCell[i].cellNo,cellValue:newValStr ,unit:this.formulaCell[i].unit};
					forumulaCellValues.push(cell);
				}
			}
			return forumulaCellValues;
			
		};
		
		//构造发生了变化的报表指标信息
		viewObj.generateChangeInfo = function(isLeafNode){
			var changeInfo = {};
			var changeCells = [];
			if(this._settings.readOnly === true){
				// 只读模式，不会发生值变化
				return changeInfo;
			}
			if(this._cellInfos){
				var currSheet = this.spread.getActiveSheet();
				for(var posi in this._cellInfos){
					var cellTmp = this._cellInfos[posi];
					if(cellTmp.isUpt === "Y"
							&& cellTmp.isPri === "Y"){
						// 只有权限内可编辑单元格，才进行值变化与否分析
						var rowCol = this.posiToRowCol(posi);
						var newVal = currSheet.getCell(rowCol.row , rowCol.col).value();
						if(newVal==null||!newVal||newVal._error){
							if(cellTmp.type=="09"||cellTmp.extMode=="02")
							{
								newVal="";
							}else
								newVal = "0";
						}
						var flag=(this._settings.ajaxData.fileName!=null&&this._settings.ajaxData.fileName!="")?true:false;
						var newValStr = $.trim(newVal)+"";
						cellTmp.newValue =newValStr;
						cellTmp.flag=flag;
						cellTmp.posi=posi;
						//excel公式报表指标，如果是汇总的，只保存叶子节点数据
						if(!isLeafNode){//不是叶子节点
							if(cellTmp.type == "04"){//是excel公式
								if(cellTmp.isSum == "Y"){//是汇总的
									continue;
								}
							}
						}
						changeCells.push(cellTmp);
					}
				}
			}
			
			changeInfo = {
					cells : changeCells,
					searchArgs : this._settings.searchArgs,
					dataDate: this._settings.dataDate,
					isValid: true
			};
			for(var i in changeCells){
				var currSheet = this.spread.getActiveSheet();
				var rowCol = this.posiToRowCol(changeCells[i].cellNo);
				var valid = currSheet.isValid(rowCol.row, rowCol.col, changeCells[i].newvalue);
				if(!valid){
					changeInfo.isValid=false;
					break;
				}
			}
			return changeInfo;
		};

		//将形如 A3 的位置编码，转换成行号列号
		viewObj.posiToRowCol = function(posi){
			var row = "";
			var col = "";
			if(posi
					&& posi != ""){
				var numIndex = 0;
				for(var i = 0 , j = posi.length ; i < j ; i++){
					if(posi.charCodeAt(i) < 64){
						//数字
						numIndex = i;
						break;
					}
				}
				col = Number(this.to123(posi.substring(0,numIndex))) - 1;
				row = Number(posi.substring(numIndex , posi.length)) - 1;
			}
			return {
				row : row+"" ,
				col : col+""
			}
		};
		
		//字母转数字
		viewObj.to123 = function(num){
			var numReturn = "0";
			if(num != null
					&& typeof num == "string"){
				for(var i = 0 ; i < num.length ; i++){
					numReturn = Number(((numReturn*26)) + Number((num.charCodeAt(i) - 64)));
				}
			}
			if(numReturn == ""){
				numReturn = "0";
			}
			return numReturn;
		};
		
		//报表保存方法
		viewObj._saveData = function(ajaxData, view_tool){
			if(null == ajaxData){
				return;
			}
			var changeInfo = this.generateChangeInfo(true);
			var formulaCellInfo = this.getFormulaCellInfo();
			//进行excel公式初始数据保存
			$.ajax({
				async : true,
				url : this._settings.ctx + "/rpt/frs/rptfill/saveData",
				dataType : 'json',
				type : 'post',
				data : {
					cells : JSON2.stringify(changeInfo.cells),
					rptId : ajaxData.rptId,
					orgNo : ajaxData.orgNo,
					dataDate : ajaxData.dataDate,
					taskInsId : ajaxData.taskInsId,
					formulaCellInfo : JSON2.stringify(formulaCellInfo),
					searchArgs : changeInfo.searchArgs
				}, 
				success: function(result) {
					BIONE.tip("报表计算成功");
					if(view_tool._settings.loadCompleted != null
							&& typeof view_tool._settings.loadCompleted == "function"){
						view_tool._settings.loadCompleted();
					}
					$("#spread"+ ajaxData.rptId + ajaxData.dataDate + ajaxData.orgNo).remove();
				},
				error:function(){
					BIONE.tip("报表计算异常，请查看日志");
				}
			});
		}
		
		viewObj.init(targetSelector, settings);
		return viewObj;
	}
	


	$.fn.extend({
		view: function(arg0) {
			var view = new View($(this), arg0);
			this.data('view_tool', view);
	    }
	  });
})(jQuery);

