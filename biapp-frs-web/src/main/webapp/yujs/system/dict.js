function AfterInit(){
	JSPFree.createSpanByBtn("d1",["主界面(70)/listXmlFile","1.一键比较表结构(120)/onCompareAllTable","2.一键重建所有视图(120)/onReCompileAllView","A.生成初始化数据xml(145)/onBatchCreateXmlFirst","B.创建升级数据xml(120)/onBatchCreateXml","C.检测数据库差异(120)/onQueryDropTable"],true,true);
	listXmlFile();
}

//显示所有xml文件
function listXmlFile (){
	var jso_rt = JSPFree.doClassMethodCall("com.yusys.bione.plugin.database.DataBaseDMO","getXmlFileList",{});
	document.getElementById("d1_A").innerHTML=jso_rt.html;
}

//查询所有表名
function onQueryDropTable(){
	JSPFree.confirm("提示","这是反向检查数据库中有的表,而xml中没有定义的冗余/垃圾表,只是生成SQL,并没有实际执行!你确定进行此操作?",function(_isOK){
		if(_isOK){  //如果确定
			var jso_rt = JSPFree.doClassMethodCall("com.yusys.bione.plugin.database.DataBaseDMO","onQueryDropTable",{});
			document.getElementById("d1_A").innerHTML=jso_rt.html;
		}
	});
}

//查看表结构xml文件
function onLookTabHtml(_xmlFile){
	var jso_rt = JSPFree.doClassMethodCall("com.yusys.bione.plugin.database.DataBaseDMO","getOneXmlTable",{xmlfile:_xmlFile});
	document.getElementById("d1_A").innerHTML=jso_rt.html;
}

//查看视图结构的xml文件
function onLookViewHtml(_xmlFile){
	var jso_rt = JSPFree.doClassMethodCall("com.yusys.bione.plugin.database.DataBaseDMO","getOneXmlView",{xmlfile:_xmlFile});
	document.getElementById("d1_A").innerHTML=jso_rt.html;
}

//查看初始化数据的xml
function onLookInitDatawHtml(_xmlFile,_type){
	var jso_rt = JSPFree.doClassMethodCall("com.yusys.bione.plugin.database.DataBaseDMO","getOneInitDataXML",{xmlfile:_xmlFile,type:_type});
	document.getElementById("d1_A").innerHTML=jso_rt.html;
}

//一键比较所有表结构
function onCompareAllTable(){
	//选择数据库类型
	JSPFree.openDialog("选择数据库类型","/yujs/system/chooseDataBaseType.js",350,350,null,function(_rtdata){
		if(_rtdata){
			if(_rtdata.type != null && _rtdata.type == "dirclose"){
				return;
			}
			JSPFree.confirm("提示","你真的要比较所有模块的xml与数据库吗?<br>这是一个耗时操作,可能要1-2分钟!",function(_isOK){
				if(_isOK){
					//比较所有表结构
					var jso_rt = JSPFree.doClassMethodCall("com.yusys.bione.plugin.database.DataBaseDMO","compareAllXml",{database_type : _rtdata.database_type});
					document.getElementById("d1_A").innerHTML=jso_rt.html;
				}
			});
			
		}
	});
}

//比较一个xml文件
function onCompareOneXml(_xmlFile){
	//选择数据库类型
	JSPFree.openDialog("选择数据库类型","/yujs/system/chooseDataBaseType.js",350,350,null,function(_rtdata){
		if(_rtdata){
			if(_rtdata.type != null && _rtdata.type == "dirclose"){
				return;
			}
			JSPFree.confirm("提示","你真的要比较所有模块的xml与数据库吗?<br>这是一个耗时操作,可能要1-2分钟!",function(_isOK){
				if(_isOK){
					//比较表结构
					 var jso_rt = JSPFree.doClassMethodCall("com.yusys.bione.plugin.database.DataBaseDMO","compareOneXml",{xmlFile:_xmlFile,database_type : _rtdata.database_type});
					document.getElementById("d1_A").innerHTML=jso_rt.html;
				}
			});
			
		}
	});
}

//一键重新编译所有视图
function onReCompileAllView(){
	JSPFree.confirm("提示","您真的要重新编译所有模块的所视图么?请谨慎操作!",function(_isOK){
		if(_isOK){
			var jso_rt = JSPFree.doClassMethodCall("com.yusys.bione.plugin.database.DataBaseDMO","reCompileAllView",null);
			document.getElementById("d1_A").innerHTML=jso_rt.html;
			}
	});
}

//一键重新编译所有视图
function onReCompileOneXmlAllView(_xmlFile){
	JSPFree.confirm("提示","您真的要重新编译【" + _xmlFile + "】的所视图么?请谨慎操作!",function(_isOK){
		if(_isOK){
			var jso_rt = JSPFree.doClassMethodCall("com.yusys.bione.plugin.database.DataBaseDMO","reCompileOneXmlAllView",{xmlfile:_xmlFile});
			document.getElementById("d1_A").innerHTML=jso_rt.html;
			}
	});
}

//重建某个视图
function onReCompileOneView(_xmlFile,_viewName){
	var jso_rt = JSPFree.doClassMethodCall("com.yusys.bione.plugin.database.DataBaseDMO","reCompileOneView",{xmlfile:_xmlFile,viewname:_viewName});
	document.getElementById("d1_A").innerHTML=jso_rt.html;
}

//建表SQL
function onCreateSQLByOracle(_xmlFile,_tableName){
	var jso_rt = JSPFree.doClassMethodCall("com.yusys.bione.plugin.database.DataBaseDMO","buildCreateTableSQL",{xmlFile:_xmlFile,tableName:_tableName,dbtype:"ORACLE"});
	JSPFree.openHtmlMsgBox2("建表SQL",600,600,jso_rt.html);
}

//建表SQL
function onCreateSQLByDB2(_xmlFile,_tableName){
	var jso_rt = JSPFree.doClassMethodCall("com.yusys.bione.plugin.database.DataBaseDMO","buildCreateTableSQL",{xmlFile:_xmlFile,tableName:_tableName,dbtype:"DB2"});
	JSPFree.openHtmlMsgBox2("建表SQL",600,600,jso_rt.html);
}


function onCreateAllSQLByOracle(_xmlFile){
	var jso_rt = JSPFree.doClassMethodCall("com.yusys.bione.plugin.database.DataBaseDMO","buildAllCreateTableSQL",{xmlFile:_xmlFile,dbtype:"ORACLE"});
	document.getElementById("d1_A").innerHTML=jso_rt.html;
}

function onCreateAllSQLByDB2(_xmlFile){
	var jso_rt = JSPFree.doClassMethodCall("com.yusys.bione.plugin.database.DataBaseDMO","buildAllCreateTableSQL",{xmlFile:_xmlFile,dbtype:"DB2"});
	document.getElementById("d1_A").innerHTML=jso_rt.html;
}


function onCreateXmlFromDB(){
	var jso_rt = JSPFree.doClassMethodCall("com.yusys.bione.plugin.database.DataBaseDMO","getAllTableXML",{});
	document.getElementById("d1_A").innerHTML=jso_rt.html;
}


//一键导出所有建表SQL
function onExportAllCreateSQL(){
	//选择数据库类型
	JSPFree.openDialog("选择数据库类型","/yujs/system/chooseDataBaseType.js",350,350,null,function(_rtdata){
		if(_rtdata){
			if(_rtdata.type != null && _rtdata.type == "dirclose"){
				return;
			}
			//导出建表语句
			JSPFree.downloadFile("com.yusys.bione.plugin.database.ExportAllCreateSQLDMO","A_CreateTableSQL.txt",{database_type : _rtdata.database_type});
		}
	});
}

//一健导出所有初始化数据的SQL
function onExportAllInitDataSQL(){
    JSPFree.downloadFile("com.yusys.bione.plugin.database.ExportAllInitDataSQLDMO","B_AllInitSQL_1.txt",{type:"1"});
}

//导出所有视图定义
function onExportAllViewSQL(){
	JSPFree.downloadFile("com.yusys.bione.plugin.database.ExportAllViewSQLDMO","C_AllViewSQL.txt",{type:"1"});
}


//一键导出所有升级的SQL
function onExportAllUpgradeDataSQL(){
	JSPFree.downloadFile("com.yusys.bione.plugin.database.ExportAllInitDataSQLDMO","AllInitSQL_2.txt",{type:"2"});
}

//导出某一个模块的数据生成xml
function exportOneInitDataXmlDMO(_xmlfile,_type){
	JSPFree.prompt("提示","你真的要把【" + _xmlfile + "】中指定的表从数据库生成xml安装数据吗?<br>请选择生成的路径!",function(_path){
		if(_path){
		  var jso_rt = JSPFree.doClassMethodCall("com.yusys.bione.plugin.database.DataBaseDMO","exportOneInitDataXmlDMO",{"xmlfile":_xmlfile,"type":_type,"filepath":_path});
		  JSPFree.alert(jso_rt.msg);
		}
	});
}

//导出茉一个模块的数据生成xml
function exportOneInitDataSQLFromXmlDMO(_xmlfile,_type){
	JSPFree.confirm("提示","你真的要把该模块的xml数据转成SQL么?<br>转成SQL可以直接执行!",function(_isOK){
		if(_isOK){  //如果确定
			var str_filePrefix =  _xmlfile.substring(0,_xmlfile.indexOf("."));
			JSPFree.downloadFile("com.yusys.bione.plugin.database.ExportOneInitDataSQLFromXmlDMO",str_filePrefix + "-initSQL_" + _type + ".txt",{"xmlfile":_xmlfile,"type":_type});
		}
	});
}

//一键批量创建所有升级xml
function onBatchCreateXml(){
	JSPFree.prompt("提示","这是开发人员使用的,把数据库数据反向生成xml,业务人员无需使用!<br>请输入生成xml的文件路径(服务器端)!",function(_path){
		if(_path){
		  var jso_rt = JSPFree.doClassMethodCall("com.yusys.bione.plugin.database.DataBaseDMO","exportAllInitDataXmlDMO",{"type":"2","filepath":_path});
		  JSPFree.alert(jso_rt.msg);
		}
	});
}

//一键批量创建所有需要初始数据的xml
function onBatchCreateXmlFirst(){
	JSPFree.prompt("提示","这是开发人员使用的,把数据库数据反向生成xml,业务人员无需使用!<br>请输入生成xml的文件路径(服务器端)!",function(_path){
		if(_path){
		  var jso_rt = JSPFree.doClassMethodCall("com.yusys.bione.plugin.database.DataBaseDMO","exportAllInitDataXmlDMO",{"type":"1","filepath":_path});
		  JSPFree.alert(jso_rt.msg);
		}
	});
}

//一次把数据导出htnl看看
function onExportAllDBDataToHtml(){
  var jso_rt = JSPFree.doClassMethodCall("com.yusys.bione.plugin.database.DataBaseDMO","exportAllDBDataToHtml",null);
  JSPFree.alert(jso_rt.msg);
}