//修改数据【/frame/js/yuformat/UpdateBillList.js】
var jso_InsertOrUpdateRefSQLWhere = {};  //表单中参照的SQL过滤定义,可以动态引用传入的变量值
var str_sqlwhere = "";  //SQL条件
var bo_isCanSave = true;  //是否可以保存,即【修改】与【查看】区别

var jso_data=null;

function AfterInit(){
	var str_templetcode = jso_OpenPars.templetcode;  //模板编码
	var str_ds = jso_OpenPars.ds;  //数据源
  	var str_fromtable = jso_OpenPars.fromtable;  //查询表名
  	var str_savetable = jso_OpenPars.savetable;  //保存表名

	bo_isCanSave =  jso_OpenPars.isCanSave;  //是否可以保存
	jso_InsertOrUpdateRefSQLWhere = jso_OpenPars.InsertOrUpdateRefSQLWhere;  //表单参照有时需要一个特殊的过滤条件,从表单中传入
	//先创建卡片
	var btns = [];
	if(bo_isCanSave){
        btns = ["保存/onSaveAndClose/icon-p21","取消/onClose/icon-undo"];
	}else{
        btns = ["取消/onClose/icon-undo"];
	}

  //卡片前端的配置,即可以替换默认配置!
  var jso_cardConfig = {ds:str_ds,fromtable:str_fromtable,savetable:str_savetable};
	JSPFree.createBillCard("d1",str_templetcode,btns,jso_cardConfig);  //创建卡片表单

	//SQL条件,即主键='主键值',即要根据这个查询
	str_sqlwhere = jso_OpenPars.SQLWhere;
	
  //加载数据
  jso_data = JSPFree.queryBillCardData(d1_BillCard,str_sqlwhere);  
    
  //执行卡片默认值公式2，即修改时的defaultformula2
  JSPFree.execBillCardDefaultValueFormula(d1_BillCard,true);
}

//加载完后.
function AfterBodyLoad(){
  if(bo_isCanSave){
    FreeUtil.setBillCardEditableForUpdate(d1_BillCard);  //
    
    //检查xml中有没有定义
    var str_divid = d1_BillCard.divid;  //卡片的divid
    
    //处理表单编辑变化事件监听
    var str_fnName = "onBillCardItemEdit"; //在创建表单时会创建这个函数
    if(typeof self[str_fnName] == "function") {  //如果的确定义了这个函数
    	 FreeUtil.addBillCardItemEditListener(d1_BillCard,self[str_fnName]);  //增加监听事件
    }

     //处理After逻辑
    var str_fnName_after = "afterUpdate";
    if(typeof self[str_fnName_after] == "function") {  //如果的确定义了这个函数
    	 self[str_fnName_after](d1_BillCard);  //增加监听事件
    }
  }else{
    FreeUtil.setBillCardItemEditable(d1_BillCard,"*",false);  //是否可编辑
  }

  JSPFree.setBillCardLabelHelptip(d1_BillCard);  //设置帮助说明  必须写在最后一行
}

//保存数据
function onSaveData(){
 try{
 	var fun_custValidate = self["beforeSave"];  //

    if(JSPFree.doBillCardUpdate(d1_BillCard,fun_custValidate)){
      JSPFree.alert('保存数据成功!');  
    }
 } catch(_ex){
   	console.log(_ex);
  	FreeUtil.openHtmlMsgBox2("发生异常",800,250,_ex)
 }
}

//保存并立即关闭
function onSaveAndClose(){
 try{
 	var fun_custValidate = self["beforeSave"];  //
	if(JSPFree.doBillCardUpdate(d1_BillCard,fun_custValidate)){
	   var str_sqlWhere = JSPFree.getSQLWhereByPK(d1_BillCard.templetVO,d1_BillCard.OldData); 
	   var jso_rt= { result:"ok",SQLWhere:str_sqlWhere};  //关闭窗口,并返回数据
	   JSPFree.closeDialog(jso_rt);  //直接关闭窗口!
	 }
 } catch(_ex){
   	console.log(_ex);  //排印至浏览器控制台
  	FreeUtil.openHtmlMsgBox2("发生异常",900,400,_ex);
 }
}

//直接关闭
function onClose(){
   JSPFree.closeDialog();
}