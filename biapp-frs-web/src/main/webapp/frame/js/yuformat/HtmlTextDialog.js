//这是一个纯用来显示一段html的窗口,因为之前的显示一个html提示框无法比父窗口更大,所以提供了新方法
//在FreeUtil.openHtmlMsgBox2()中调用此窗口!
function AfterInit(){
  var isText = jso_OpenPars.isText;
  var str_htmltext = jso_OpenPars2.htmltext;
  JSPFree.createSpanByBtn("d1",["确定/onCancel"]);

  if(typeof isText!="undefined" && isText){
  	document.getElementById("d1_A").innerText=str_htmltext;
  } else {
  	document.getElementById("d1_A").innerHTML=str_htmltext;
  }
}

//取消
function onCancel(){
  JSPFree.closeDialog();
}
