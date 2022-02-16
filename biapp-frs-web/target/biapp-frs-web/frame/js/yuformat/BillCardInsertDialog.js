//修改数据【/frame/js/yuformat/UpdateBillList.js】
var jso_InsertOrUpdateRefSQLWhere = {};  //表单中参照的SQL过滤定义,可以动态引用传入的变量值

function AfterInit(){
	var str_templetcode = jso_OpenPars.templetcode;  //模板编码
	var str_ds = jso_OpenPars.ds;  //数据源
  var str_fromtable = jso_OpenPars.fromtable;  //查询表名
  var str_savetable = jso_OpenPars.savetable;  //保存表名

	jso_InsertOrUpdateRefSQLWhere = jso_OpenPars.InsertOrUpdateRefSQLWhere;  //表单参照有时需要一个特殊的过滤条件,从表单中传入

  //卡片配置VO
  var jso_cardConfig = {ds:str_ds,fromtable:str_fromtable,savetable:str_savetable};
	
  //先创建卡片
	JSPFree.createBillCard("d1",str_templetcode,["保存/onSaveAndClose/icon-p21","取消/onClose/icon-undo"],jso_cardConfig);  //创建卡片表单
	
	//先执行卡片默认值公式
	JSPFree.execBillCardDefaultValueFormula(d1_BillCard);

  //再把程序传过来的默认值设置上去
	JSPFree.setBillCardValues(d1_BillCard,jso_OpenPars.DefaultValues);
}

//加载完后.
function AfterBodyLoad(){
  //处理表单编辑变化事件监听
  var str_fnName = "onBillCardItemEdit"; //在创建表单时会创建这个函数
  if(typeof self[str_fnName] == "function") {  //如果的确定义了这个函数
     FreeUtil.addBillCardItemEditListener(d1_BillCard,self[str_fnName]);  //增加监听事件
  }

   //处理After逻辑
  var str_fnName_after = "afterInsert";
  if(typeof self[str_fnName_after] == "function") {  //如果的确定义了这个函数
     self[str_fnName_after](d1_BillCard);  //增加监听事件
  }

   FreeUtil.setBillCardLabelHelptip(d1_BillCard); //必须写在最后一行
}

//【保存】按钮逻辑
function onSaveData(){	
  try{
   var fun_custValidate = self["beforeSave"];  //xml中写的自定义校验

	 //有个标记是专门用来区分第一次insert与以后是update的
   if(typeof d1_BillCard.AlreadyInsert=="undefined"){
      if(JSPFree.doBillCardInsert(d1_BillCard,fun_custValidate)){
        $.messager.alert('提示','新增数据成功!','info');  
      }
   }else{
   	  if(JSPFree.doBillCardUpdate(d1_BillCard,fun_custValidate)){
        $.messager.alert('提示','保存数据成功!','info');  
      }
   }
  }catch(_ex){
  	console.log(_ex);
  	FreeUtil.openHtmlMsgBox2("发生异常",800,250,_ex)
  }
}

//【取消】按钮逻辑
function onSaveAndClose(){
try{
  var fun_custValidate = self["beforeSave"];  //xml中写的自定义校验

   //有个标记是专门用来区分第一次insert与以后是update的
   var isOK = true;
   if(typeof d1_BillCard.AlreadyInsert=="undefined"){
      isOK = JSPFree.doBillCardInsert(d1_BillCard,fun_custValidate); 
   }else{
     isOK = JSPFree.doBillCardUpdate(d1_BillCard,fun_custValidate);
   }
   
   if(isOK){
     var str_sqlWhere = JSPFree.getSQLWhereByPK(d1_BillCard.templetVO,d1_BillCard.OldData); 
     var jso_rt= { result:"ok",SQLWhere:str_sqlWhere};  //关闭窗口,并返回数据
     JSPFree.closeDialog(jso_rt);  //直接关闭窗口!
   }
}catch(_ex){
  console.log(_ex);
  FreeUtil.openHtmlMsgBox2("发生异常",900,400,_ex);
}
}

//直接关闭
function onClose(){
 JSPFree.closeDialog();
}
