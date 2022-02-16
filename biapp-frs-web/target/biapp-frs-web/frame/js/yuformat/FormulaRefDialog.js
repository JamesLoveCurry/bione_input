var billList_formula;  //公式表格
var jso_CardData;  //卡片数据

function AfterInit(){
  var str_itemkey = jso_OpenPars.itemkey;  //字段名
  var str_dataxml = jso_OpenPars.dataxml;  //模板编码
  var str_colSQL = jso_OpenPars.colSQL;  //指定哪些列的SQL
  jso_CardData = jso_OpenPars2;  //表单中的实际值

  console.log(jso_CardData);
  console.log("有么?" + jso_CardData["mmm"]);

  JSPFree.createSplitByBtn("d1","上下",100,["确定/onConfirm","清空/onClearText","取消/onCancel"]);
  JSPFree.createBillCard("d1_A","/com/yusys/bione/plugin/yuformat/xml/FormulaRefBillCard.xml");

  //如果没有定义列的SQL,则下面直接是公式
  if(typeof str_colSQL == "undefined"){
    JSPFree.createBillList("d1_B","/com/yusys/bione/plugin/yuformat/xml/FormulaRefBillList.xml");  //
    billList_formula = d1_B_BillList;  //表格
  }else{
    JSPFree.createTabb("d1_B", ["常用公式","字段清单"], 120);  //创建多页签
    JSPFree.createBillList("d1_B_1","/com/yusys/bione/plugin/yuformat/xml/FormulaRefBillList.xml");  //
    JSPFree.createBillList("d1_B_2","/com/yusys/bione/plugin/yuformat/xml/FormulaRefBillList2.xml");  //
    str_colSQL = convertSQL(str_colSQL);  //把SQL中的变量替换一下!
    JSPFree.queryDataBySQL(d1_B_2_BillList, str_colSQL);  //查询数据
    billList_formula = d1_B_1_BillList;  //表格

    JSPFree.addBillListDoubleClick(d1_B_2_BillList,onDoubleClick2);  //表格双击事件
  }

  //隐藏卡片滚动条
  document.getElementById("d1_A_BillCardDiv").style.overflow="hidden";

  //查询数据
  JSPFree.queryDataByClass(billList_formula,"com.yusys.bione.plugin.yuformat.utils.YuFormatUtil2","getFormulaDemoData",{dataxml:str_dataxml},false);
  JSPFree.addBillListDoubleClick(billList_formula,onDoubleClick);  //表格双击事件

  //反向带入数据
  var str_initData = jso_CardData[str_itemkey];
  if(typeof str_initData != "undefined"){
    FreeUtil.loadBillCardData(d1_A_BillCard,{"formuladata":str_initData});
  }
}

//双击公式的逻辑
function onDoubleClick(){
  var jso_data = JSPFree.getBillListSelectData(billList_formula);  //
  setFormulaNewValue(jso_data["datademo"]);  //
}

//双击列表的逻辑
function onDoubleClick2(){
  var jso_data = JSPFree.getBillListSelectData(d1_B_2_BillList);  //
  setFormulaNewValue(jso_data["colname"]);  //
}

//设置公式新值
function setFormulaNewValue(_clickValue){
  var dom_area = FreeUtil.getBillCardItemCompent(d1_A_BillCard,"formuladata");  //取得多行文本框
  var str_value = dom_area.value;  //旧的值
  var li_begin = dom_area.selectionStart;  //光标起点
  var li_end = dom_area.selectionEnd;  //光标结束点

  //前的值是光标前后的数据加上点击传入的值
  var str_newValue = str_value.substring(0,li_begin) + _clickValue + str_value.substring(li_end,str_value.length);
  JSPFree.setBillCardItemValue(d1_A_BillCard,"formuladata",str_newValue);  //设置新值
}

//转换SQL
function convertSQL(_initSQL){
  var ary_items = FreeUtil.parseStrMacroKeys(_initSQL,"{","}");
  var str_newSQL = _initSQL;  //
  for(var i=0;i<ary_items.length;i++){
    var str_itemVaue = jso_CardData[ary_items[i]];  //先从页面上取,如果页面上有则使用之,否则从整个页面变量缓存取!!
    if(typeof str_itemVaue == "undefined"){
      str_itemVaue = FreeUtil.getClientEnv(ary_items[i]);  //从页面缓存取
    }

    str_newSQL = str_newSQL.replace("{" + ary_items[i] + "}",str_itemVaue);  //
  }
  return str_newSQL;  //返回最新结果
}

//确定
function onConfirm(){
  var str_newFormula = JSPFree.getBillCardItemValue(d1_A_BillCard, "formuladata");  //
  JSPFree.closeDialog({"rtdata":str_newFormula});
}

//清空数据
function onClearText(){
 JSPFree.setBillCardItemValue(d1_A_BillCard,"formuladata","");  //设置新值
}

//取消
function onCancel(){
  JSPFree.closeDialog();
}
