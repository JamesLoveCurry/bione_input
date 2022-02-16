<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<html>
<head>
<meta name="decorator" content="/template/template1_BS.jsp">
<script type="text/javascript">
	var grid;
	var renderData;
	// buttonFlag 显示按钮类型 1-支行 2-总行0000 3-总行3000 4-其他
	var buttonFlag="${buttonFlag}";
	
	$(function(){
		initRanderData();
		searchForm();
		initGrid();
		initButtons();
		initExport();
		BIONE.addSearchButtons("#search", grid, "#searchbtn");
	});

	//初始化数据码值数据
	function initRanderData(){
		//流向类型
		var paramTypeNos = "pisaFlowType"
		$.ajax({
			async : false,
			type : "post",
			url : '${ctx}/frs/pisamessage/randerDataByParamTypeNo.json',
			data: {
				typeNos : paramTypeNos
			},
			success : function(res){
				renderData = res;
			},
			error : function(e){
				BIONE.tip('初始化数据加载失败');
			}
		});
	}
	
	function searchForm() {
		$("#search").ligerForm({
			fields : [   {
				display : '数据日期',
				name : 'statisticsDt',
				newline : false,
				type : "date",
				cssClass : "field" ,
				options : {
					format : 'yyyyMMdd'
				},
				attr : {
					op : "like",
					field : "statisticsDt"
				}
			},   
			{
				display : "账号",
				name : "bankAcctNum",
				newline : false,
				type : "text",
				cssClass : "field",
				attr : {
					op : "like",
					field : "bankAcctNum"
				}
			},{
				display : '账户种类',
				name : 'bankAcctKindCd',
				newline : false,
				type : "select",
				cssClass : "field",
				options : {
					data : [{'text' : "借记结算账户" , 'id' : "01"},
					        {'text' : "贷记结算账户" , 'id' : "02"},
					        {'text' : "非结算账户" , 'id' : "03"}],
					cancelable  : true
				},
				attr : {
					op : "like",
					field : "bankAcctKindCd"
				}
			},{
				display : '审核状态',
				name : 'auditStatus',
				newline : false,
				type : "select",
				cssClass : "field",
				options : {
					data : [ 
					        {'text' : "待处理" , 'id' : "0"},
					        {'text' : "待复核" , 'id' : "1"},
					        {'text' : "待审核" , 'id' : "2"},
					        {'text' : "复核不通过" , 'id' : "4"},
					        {'text' : "审核不通过" , 'id' : "5"}, 
					        {'text' : "复核通过" , 'id' : "6"},
					        {'text' : "已审核" , 'id' : "3"} 
					         ],
					cancelable  : true
				},
				attr : {
					op : "like",
					field : "auditStatus"
				}
			}  ]
		});

	}

 
	/**
	**初始化Grid  宁夏 2020-4-27 09:53:052 重新校正字段及其码值 
	**/
	function initGrid() {
		grid = $("#maingrid").ligerGrid({
			InWindow : false,
			height : '99%',
			width : '100%',
			columns : [ {
				display : '数据日期',
				name : 'statisticsDt',
				width : '8%'
			},{
				display : '校验状态',
				name : 'validateFlag',
				width : '10%',
				 
				align: "center",
				render : function(rowData) {
					if (rowData.validateFlag == "0"){
						return "未校验"; 
					}else if (rowData.validateFlag == "1"){
						return "校验未通过";  
					}else if (rowData.validateFlag == "2"){
						return "校验通过"; 
					}else{
						return rowData.validateFlag; 
					} 
				}
			},{
				display : '审核状态',
				name : 'auditStatus',
				width : '10%',
				align: "center",
				render : function(rowData) {
					  if (rowData.auditStatus == "1"){
						return "待复核";  
					}else if (rowData.auditStatus == "2"){
						return "待审核"; 
					}else if (rowData.auditStatus == "3"){
						return "已审核"; 
					}else if (rowData.auditStatus == "0"){
						return "待处理"; 
					}else if (rowData.auditStatus == "4"){
						return "复核不通过"; 
					}else if (rowData.auditStatus == "5"){
						return "审核不通过"; 
					}else if (rowData.auditStatus == "6"){
						return "复核通过"; 
					}else{
						return rowData.auditStatus; 
					} 
				}   
			},{
				display : "打回标志", 
				name : "reBackFlag",
				width : "10%",
				align: "center"
			},
		  /*	{
				display : "打回原因", 
				name : "errorReason",
				width : "30%",
				align: "center"
			},*/
			
			{
				display : "账号（不可变更）",//18
				name : "bankAcctNum",
				width : "10%",
				align: "center"
			},{
				display : "存款人姓名", //1 账户属于联名账户
				name : "depositName",
				width : "10%",
				align: "center"
			} ,  
			{
				display : "机构号",
				name : "orgNo",	
				width : "6%",
				align: "center"
			},{
				display : "存款人身份证件种类",//2
				name : "depositIdentityTypeCd",
				width : "15%",
				align: "center",
				render : function(rowdata){
					if(rowdata.depositIdentityTypeCd=='01'){
						return "第一代居民身份证";
					}else if(rowdata.depositIdentityTypeCd=='02'){
						return "第二代居民身份证";
					}else if(rowdata.depositIdentityTypeCd=='03'){
						return "临时身份证";
					}else if(rowdata.depositIdentityTypeCd=='04'){
						return "中国护照";
					}else if(rowdata.depositIdentityTypeCd=='05'){
						return "户口簿";
					}else if(rowdata.depositIdentityTypeCd=='06'){
						return "村民委员会证明";
					}else if(rowdata.depositIdentityTypeCd=='07'){
						return "学生证";
					}else if(rowdata.depositIdentityTypeCd=='08'){
						return "军官证";
					}else if(rowdata.depositIdentityTypeCd=='09'){
						return "离休干部荣誉证";
					}else if(rowdata.depositIdentityTypeCd=='10'){
						return "军官退休证";
					}else if(rowdata.depositIdentityTypeCd=='11'){
						return "文职干部退休证";
					}else if(rowdata.depositIdentityTypeCd=='12'){
						return "军事学员证";
					}else if(rowdata.depositIdentityTypeCd=='13'){
						return "武警证";
					}else if(rowdata.depositIdentityTypeCd=='14'){
						return "士兵证";
					}else if(rowdata.depositIdentityTypeCd=='15'){
						return "港澳居民来往内地通行证";
					}else if(rowdata.depositIdentityTypeCd=='16'){
						return "台湾居民来往大陆通行证";
					}else if(rowdata.depositIdentityTypeCd=='17'){
						return "外国人永久居留证";
					}else if(rowdata.depositIdentityTypeCd=='18'){
						return "边民出入境通行证";
					}else if(rowdata.depositIdentityTypeCd=='19'){
						return "外国护照";
					}else if(rowdata.depositIdentityTypeCd=='20'){
						return "其它";
					}else if(rowdata.depositIdentityTypeCd=='21'){
						return "港澳居民居住证";
					}else if(rowdata.depositIdentityTypeCd=='22'){
						return "台湾居民居住证";
					}else{
						return rowdata.depositIdentityTypeCd;
					}
				}
			},{
				display : "存款人身份证件号码",//3
				name : "depositIdentityNum",
				width : "15%",
				align: "center"
			},{
				display : "身份证件到期日",//4
				name : "identityDueDt",
				width : "10%",
				align: "center"
			},{
				display : "发证机关所在地的地区代码",//5
				name : "issueOrgAreaCd",
				width : "20%",
				align: "center"
			},{
				display : "存款人类别",//6
				name : "depositClassCd",
				width : "10%",
				align: "center",
				render : function(rowdata){
					if(rowdata.depositClassCd=='01'){
						return "中国居民";
					}else if(rowdata.depositClassCd=='02'){
						return "军人";
					}else if(rowdata.depositClassCd=='03'){
						return "武警";
					}else if(rowdata.depositClassCd=='04'){
						return "香港、澳门、台湾地区居民";
					}else if(rowdata.depositClassCd=='05'){
						return "外国公民";
					}else if(rowdata.depositClassCd=='06'){
						return "定居国外的中国公民";
					}
				}
			},{
				display : "存款人国籍(地区)",//7
				name : "depositCountryCd",
				width : "10%",
				align: "center"
			},{
				display : "存款人性别",//8
				name : "depositSexCd",
				width : "6%",
				align: "center",
				render : function(rowdata){
					if(rowdata.depositSexCd=='01'){
						return "男";
					}else if(rowdata.depositSexCd=='02'){
						return "女";
					}else if(rowdata.depositSexCd=='09'){
						return "未说明的性别";
					} else{
						return rowdata.depositSexCd
					}
				}
			},{
				display : "存款人邮编",//9
				name : "depositPostCd",
				width : "6%",
				align: "center"
			},{
				display : "存款人地址",//10
				name : "depositAddr",
				width : "15%",
				align: "center"
			},{
				display : "存款人电话",//11
				name : "depositTel",
				width : "10%",
				align: "center"
			},{
				display : "代理人名称",//12
				name : "agentName",
				width : "10%",
				align: "center"
			},{
				display : "代理人身份证件种类",//13
				name : "agentIdentityTypeCd",
				width : "15%",
				align: "center",
				render : function(rowdata){
					if(rowdata.agentIdentityTypeCd=='01'){
						return "第一代居民身份证";
					}else if(rowdata.agentIdentityTypeCd=='02'){
						return "第二代居民身份证";
					}else if(rowdata.agentIdentityTypeCd=='03'){
						return "临时身份证";
					}else if(rowdata.agentIdentityTypeCd=='04'){
						return "中国护照";
					}else if(rowdata.agentIdentityTypeCd=='05'){
						return "户口簿";
					}else if(rowdata.agentIdentityTypeCd=='06'){
						return "村民委员会证明";
					}else if(rowdata.agentIdentityTypeCd=='07'){
						return "学生证";
					}else if(rowdata.agentIdentityTypeCd=='08'){
						return "军官证";
					}else if(rowdata.agentIdentityTypeCd=='09'){
						return "离休干部荣誉证";
					}else if(rowdata.agentIdentityTypeCd=='10'){
						return "军官退休证";
					}else if(rowdata.agentIdentityTypeCd=='11'){
						return "文职干部退休证";
					}else if(rowdata.agentIdentityTypeCd=='12'){
						return "军事学员证";
					}else if(rowdata.agentIdentityTypeCd=='13'){
						return "武警证";
					}else if(rowdata.agentIdentityTypeCd=='14'){
						return "士兵证";
					}else if(rowdata.agentIdentityTypeCd=='15'){
						return "港澳居民来往内地通行证";
					}else if(rowdata.agentIdentityTypeCd=='16'){
						return "台湾居民来往大陆通行证";
					}else if(rowdata.agentIdentityTypeCd=='17'){
						return "外国人永久居留证";
					}else if(rowdata.agentIdentityTypeCd=='18'){
						return "边民出入境通行证";
					}else if(rowdata.agentIdentityTypeCd=='19'){
						return "外国护照";
					}else if(rowdata.agentIdentityTypeCd=='20'){
						return "其它";
					}else if(rowdata.agentIdentityTypeCd=='21'){
						return "港澳居民居住证";
					}else if(rowdata.agentIdentityTypeCd=='22'){
						return "台湾居民居住证";
					}else{
						return rowdata.agentIdentityTypeCd;
					}
				}
			},{
				display : "代理人身份证件号码",//14
				name : "agentIdentityNum",
				width : "10%",
				align: "center"
			},{
				display : "代理人国籍",//15
				name : "agentCountryCd",
				width : "10%",
				align: "center"
			},{
				display : "代理人电话",//16
				name : "agentTel",
				width : "10%",
				align: "center"
			},{
				display : "开户银行金融机构编码",//17
				name : "openBankFinOrgCd",
				width : "10%",
				align: "center"
			},{
				display : "账户种类",
				name : "bankAcctKindCd",//19
				width : "10%",
				align: "center",
				render : function(rowdata){
						if(rowdata.bankAcctKindCd=='01'){
						return "借记结算账户";
					}else if(rowdata.bankAcctKindCd=='02'){
						return "贷记结算账户";
					}else if(rowdata.bankAcctKindCd=='03'){
						return "非结算账户";
					}else if (rowdata.bankAcctKindCd=='04'){
						return "其他";
					}else{
						return rowdata.bankAcctKindCd;
					}
				}
			},{
				display : "介质号",//20
				name : "cardCd",
				width : "10%",
				align: "center"
			},{
				display : "介质到期日",//21
				name : "cardDueDt",
				width : "8%",
				align: "center"
			},{
				display : "账户介质",//22
				name : "bankAcctMedia",
				width : "8%",
				align: "center",
				render : function(rowdata){
					/*if(rowdata.bankAcctMedia=='01'){
						return "银行卡";
					}else if(rowdata.bankAcctMedia=='02'){ 
						return "存折";
					}else if(rowdata.bankAcctMedia=='03'){
						return "存单";
					}else if(rowdata.bankAcctMedia=='04'){
						return "手机";
					}else if(rowdata.bankAcctMedia=='05'){
						return "无介质";
					}else if(rowdata.bankAcctMedia=='06'){
						return "其他";
					} */
					var allName=[];
					if(rowdata.bankAcctMedia!=null){ 
						var ids =rowdata.bankAcctMedia.split(";");   
						 for (var i = 0; i < ids.length; i++){
							  var num=ids[i]; 
					       if(num=='01'){
								  allName.push("银行卡");
							}else if(num=='02'){
								  allName.push("存折");
							}else if(num=='03'){
								  allName.push("存单");
							}else if(num='04'){
								  allName.push("手机");
							}else if(num=='05'){
								  allName.push("无介质"); 
							}else if(num=='06'){
								  allName.push("其他"); 
							}else{
								  return rowdata.bankAcctMedia;}
						} 
						return allName;
					}
					
				}
			},{
				display : "介质注销日期",//23
				name : "expireCardDt",
				width : "10%",
				align: "center"
			},{
				display : "介质状态",//24
				name : "cardStatCd",
				width : "8%",
				align: "center",
				render : function(rowdata){
					/*if(rowdata.cardStatCd=='01'){
						return "正常";
					}else if(rowdata.cardStatCd=='02'){
						return "销卡";
					}else{
						return rowdata.cardStatCd;
					}*/
					
					 var allName=[];
					if(rowdata.cardStatCd!=null){ 
						var ids =rowdata.cardStatCd.split(";");   
						 for (var i = 0; i < ids.length; i++){
							  var num=ids[i]; 
					       if(num=='01'){
								  allName.push("正常");
							}else if(num=='02'){
								  allName.push("销卡"); 
							}else{
								  return rowdata.cardStatCd;}
						} 
						return allName;
					}
					
				}
			},{
				display : "账户类型",//25
				name : "bankAcctTypeCd",
				width : "8%",
				align: "center",
				render : function(rowdata){
					if(rowdata.bankAcctTypeCd=='01'){
						return "Ⅰ类";
					}else if(rowdata.bankAcctTypeCd=='02'){
						return "Ⅱ类";
					}else if(rowdata.bankAcctTypeCd=='03'){
						return "Ⅲ类";
					}else{
						return rowdata.bankAcctTypeCd;
					}
				}
			},{
				display : "Ⅱ、Ⅲ类户绑定账户账号",//26
				name : "bindingiAcctNum",	
				width : "10%",
				align: "center"
			},{
				display : "II、III类户绑定账户开户银行金融机构编码",//27
				name : "bindingiFinOrgCd",
				width : "15%",
				align: "center"
			},{
				display : "开户日期",//28
				name : "openDt",
				width : "8%",
				align: "center"
			},{
				display : "销户日期(不可变)",//29
				name : "expireDt",
				width : "8%",
				align: "center"
			},{
				display : "账户状态",//30
				name : "acctStatCd",
				width : "8%",
				align: "center",
				render : function(rowdata){
					if(rowdata.acctStatCd=='01'){
						return "正常";
					}else if(rowdata.acctStatCd=='02'){
						return "销户";
					}else if(rowdata.acctStatCd=='03'){
						return "未激活";
					}else{
						return rowdata.acctStatCd;
					}
				}
			},{
				display : "币种",//31
				name : "currencyCd",
				width : "8%",
				align: "center",
				render :  function(rowdata){
					if(rowdata.currencyCd=='01'){
						return "人民币";
					}else{
						return rowdata.currencyCd;
					}
				}
			},{
				display : "是否为军人保障卡",//32
				name : "armySecurityCard",
				width : "8%",
				align: "center",
				render :  function(rowdata){
					if(rowdata.armySecurityCard=='01'){
						return "是";
					}else if(rowdata.armySecurityCard=='02'){
						return "不是";
					}else{
						return rowdata.armySecurityCard;
						
					}
				}
			},{
				display : "是否为社会保障卡",//33
				name : "societySecurityCard",
				width : "8%",
				align: "center",
				render :  function(rowdata){
					if(rowdata.societySecurityCard=='01'){
						return "是";
					}else if(rowdata.societySecurityCard=='02'){
						return "不是";
					}else{
						return rowdata.societySecurityCard;
						
					}
				}
			},{
				display : "核实结果",//34
				name : "auditResultCd",
				width : "8%",
				align: "center",
				render :  function(rowdata){
					if(rowdata.auditResultCd=='01'){
						return "未核实";
					}else if(rowdata.auditResultCd=='02'){
						return "真实";
					}else if(rowdata.auditResultCd=='03'){
						return "假名";
					}else if(rowdata.auditResultCd=='04'){
						return "匿名";
					}else if(rowdata.auditResultCd=='05'){
						return "无法核实";
					}else{
						return rowdata.auditResultCd;
						
					}
				}
			},{
				display : "无法核实原因",//35
				name : "noAuditResultDesc",
				width : "25%",
				align: "center",
				render :  function(rowdata){
					if(rowdata.noAuditResultDesc=='01'){
						return "无法联系存款人";
					}else if(rowdata.noAuditResultDesc=='02'){
						return "存款人提供证明文件有疑义待进一步核实";
					}else if(rowdata.noAuditResultDesc=='03'){
						return "存款人在规定时间内无法提供相关证明";
					}else if(rowdata.noAuditResultDesc=='04'){
						return "存款人拒绝提供证明";
					}else{
						return rowdata.noAuditResultDesc;
						
					}
				}
			},{
				display : "处置方法",//36
				name : "disposeMode",
				width : "50%",
				align: "center",
				render :  function(rowdata){
					var allName=[];
					if(rowdata.disposeMode!=null){ 
						var ids =rowdata.disposeMode.split(";");  
						/* $.each(ids,function(index,j){
							alert(j);
						}) */
						 for (var i = 0; i < ids.length; i++){
							  var num=ids[i]; 
					       if(num=='01'){
								  allName.push("未作处理");
							}else if(num=='02'){
								  allName.push("报送当地人民银行");
							}else if(num=='03'){
								  allName.push("报送反洗钱监测中心");
							}else if(num='04'){
								  allName.push("报送当地公安机关");
							}else if(num=='05'){
								  allName.push("中止交易");
							}else if(num=='06'){
								  allName.push("关闭网银");
							}else if(num=='07'){
								  allName.push("关闭手机电话银行");
							}else if(num=='08'){
								  allName.push("关闭ATM取现");
							}else if(num=='09'){
								  allName.push("关闭ATM转账");
							}else if(num=='10'){
								  allName.push("其他");
							}else{
								  return rowdata.disposeMode;}
						} 
						return allName;
					} 
					 
					/* if(rowdata.disposeMode=='01'){
						return "未作处理";
					}else if(rowdata.disposeMode=='02'){
						return "报送当地人民银行";
					}else if(rowdata.disposeMode=='03'){
						return "报送反洗钱监测中心";
					}else if(rowdata.disposeMode=='04'){
						return "报送当地公安机关";
					}else if(rowdata.disposeMode=='05'){
						return "中止交易";
					}else if(rowdata.disposeMode=='06'){
						return "关闭网银";
					}else if(rowdata.disposeMode=='07'){
						return "关闭手机电话银行";
					}else if(rowdata.disposeMode=='08'){
						return "关闭ATM取现";
					}else if(rowdata.disposeMode=='09'){
						return "关闭ATM转账";
					}else if(rowdata.disposeMode=='10'){
						return "其他";
					}else{
						return rowdata.disposeMode;} */
						
					
				}
			},{
				display : "信息类型",//37
				name : "infoTypeCd",
				width : "6%",
				align: "center",
				render : function(rowdata){
					if(rowdata.infoTypeCd=='01'){
						return "开户";
					}else if(rowdata.infoTypeCd=='02'){
						return "变更";
					}else if(rowdata.infoTypeCd=='03'){
						return "销户";
					}else{
						return rowdata.infoTypeCd;
						
					}
				}
			},{
				display : "开户渠道",//38
				name : "openChannalCd",
				width : "10%",
				align: "center",
				render :  function(rowdata){
					if(rowdata.openChannalCd=='01'){
						return "柜面";
					}else if(rowdata.openChannalCd=='02'){
						return "互联网网页";
					}else if(rowdata.openChannalCd=='03'){
						return "APP客户端";
					}else if(rowdata.openChannalCd=='04'){
						return "自助机具（人工参与审核）";
					}else if(rowdata.openChannalCd=='05'){
						return "自助机具（无人工审核）";
					}else if(rowdata.openChannalCd=='06'){
						return "第三方渠道";
					}else if(rowdata.openChannalCd=='07'){
						return "其他";
					}else{
						return rowdata.openChannalCd;
						
					}
				}
			},  
			{
				display : "备注",//39
				name : "remark",	
				width : "10%",
				align: "left"
			},
			{
				display : "开通的非柜面交易渠道", //40 //2020-1-18 09:30:40 常志伟增加字段
				name : "openUntradeChannal",	
				width : "10%",
				align: "left",
				render : function(rowdata){
				  var allName=[];
					if(rowdata.openUntradeChannal!=null){ 
						var ids =rowdata.openUntradeChannal.split(";");   
						 for (var i = 0; i < ids.length; i++){
							  var num=ids[i]; 
					       if(num=='01'){
								  allName.push("网银");
							}else if(num=='02'){
								  allName.push("手机银行");
							}else if(num=='03'){
								  allName.push("ATM转账或取现");
							}else if(num='04'){
								  allName.push("POS");
							}else if(num=='05'){
								  allName.push("其他"); 
							}else{
								  return rowdata.openUntradeChannal;}
						} 
						return allName;
					}
					/*if(rowdata.openUntradeChannal=='01'){
						return "网银";
					}else if(rowdata.openUntradeChannal=='02'){
						return "手机银行";
					}else if(rowdata.openUntradeChannal=='03'){
						return "ATM转账或取现";
					}else if(rowdata.openUntradeChannal=='04'){
						return "POS";
					}else if(rowdata.openUntradeChannal=='05'){
						return "其他";
					}else{
						return rowdata.openUntradeChannal;
					}*/
				}
			}  ,{
				display : "是否为联名账户",//41
				name : "jointAccount",	
				width : "10%",
				align: "center",
				render :  function(rowdata){
					if(rowdata.jointAccount=='01'){
						return "联名账户";
					}else if(rowdata.jointAccount=='02'){
						return "主、副卡账户";
					}else {
						return rowdata.jointAccount;
					}
				}
			},
			{
				display : "开户地地区代码",//42
				name : "openAreaCode",	
				width : "10%",
				align: "center"
			},
			
			 {
				display : "预留字段4",//43
				name : "column4",	
				width : "6%",
				align: "center"
			},{
				display : "预留字段5",//44
				name : "column5",	
				width : "6%",
				align: "center"
			},{
				display : "录入人或修改人",
				name : "inputer",	
				width : "10%",
				align: "center"
			},{
				display : "复核员",
				name : "reviewer",	
				width : "10%",
				align: "center"
			},{
				display : "审核员",
				name : "assessor",	
				width : "10%",
				align: "center"
			}   ],
			checkbox : true,
			usePager : true,
			isScroll : true,
			rownumbers : true,
			alternatingRow : true,//附加奇偶行效果行
			colDraggable : true,
			dataAction : 'server',//从后台获取数据
			method : 'post',
			url : '${ctx}/frs/nrtmessage/acctpersonlList',
			sortName : 'bankAcctNum', //第一次默认排序的字段
			sortOrder : 'asc',
			toolbar : {}
		});
	}

	function initButtons() { 
		//录入人员
		if(buttonFlag == "0"){
			var btns = [ {
				text : '新增',
				click : oper_add,
				icon : 'fa-plus',
				operNo : 'oper_add'
			}, {
				text : '修改',
				click : oper_modify,
				icon : 'fa-pencil-square-o',
				operNo : 'oper_modify'
			}, {
				text : '删除',
				click : oper_delete,
				icon : 'fa-trash-o',
				operNo : 'oper_delete'
			}, {
				text : '查看',
				click : oper_check,
				icon : 'fa-book',
				operNo : 'oper_check'
			},{
				text : '批量校验',
				click : oper_batchValidate,
				icon : 'fa-cogs',
				operNo : 'oper_batchValidate'
			}  ]; 
		}
		//复核人员
		if(buttonFlag == "1"){
			var btns = [  {
				text : '复核',
				click : oper_review,
				icon : 'fa-trash-o',
				operNo : 'oper_review'
			},{
				text : '查看',
				click : oper_check,
				icon : 'fa-book',
				operNo : 'oper_check'
			} ]; 
		} 
		// 总行0000
		if(buttonFlag == "2"){
			var btns = [{
				text : '审核',
				click : oper_assess,
				icon : 'fa-pencil-square-o',
				operNo : 'oper_assess'
			},{
				text : '查看',
				click : oper_check,
				icon : 'fa-book',
				operNo : 'oper_check'
			},{
				text : '调用生成报文脚本',
				click : oper_execShell,
				icon : 'icon-apply',
				operNo : 'oper_execShell'
			}];
		}
		// 总行3000
		if(buttonFlag == "3"){
			var btns = [ {
				text : '新增',
				click : oper_add,
				icon : 'fa-plus',
				operNo : 'oper_add'
			}, {
				text : '修改',
				click : oper_modify,
				icon : 'fa-pencil-square-o',
				operNo : 'oper_modify'
			}, {
				text : '删除',
				click : oper_delete,
				icon : 'fa-trash-o',
				operNo : 'oper_delete'
			},{
				text : '复核',
				click : oper_review,
				icon : 'fa-trash-o',
				operNo : 'oper_review'
			}, {
				text : '审核',
				click : oper_assess,
				icon : 'fa-pencil-square-o',
				operNo : 'oper_assess'
			},{
				text : '查看',
				click : oper_check,
				icon : 'fa-book',
				operNo : 'oper_check'
			}, {
				text : '批量校验',
				click : oper_batchValidate,
				icon : 'fa-cogs',
				operNo : 'oper_batchValidate'
			},{
				text : '调用生成报文脚本',
				click : oper_execShell,
				icon : 'icon-apply',
				operNo : 'oper_execShell'
			}];
		}
		
		if(buttonFlag == "4"){
			var btns = [{
				text : '查看',
				click : oper_check,
				icon : 'fa-book',
				operNo : 'oper_check'
			}];
		}
		BIONE.loadToolbar(grid, btns, function() {});
	}
	
	//码值转换
	function flowTypeRender(rowdata) {
		var renderVal = renderData.pisaFlowType[rowdata.flowType];
		return renderVal;
	}
	
	//新增数据
	function oper_add() {
		BIONE.commonOpenDialog('新增信息', 'perFlowEidtWin', 1500, 600, '${ctx}/frs/nrtmessage/persnonFlowEidt');
	}
	
	//修改属性
	function oper_modify() {
		var rows = grid.getSelectedRows();
		if (rows.length != 1) {
			BIONE.tip('请选择一条记录!');
			return;
		}
		BIONE.commonOpenDialog("修改信息", "perFlowEidtWin", 1500, 600, "${ctx}/frs/nrtmessage/persnonFlowEidt?id="+rows[0].bankAcctNum+"&infoType="+rows[0].infoTypeCd );
	}
	
	//查看信息
	function oper_check() {
		var rows = grid.getSelectedRows();
		if (rows.length != 1) {
			BIONE.tip('请选择一条记录!');
			return;
		}
		BIONE.commonOpenDialog("查看信息", "perFlowDealWin", 1500, 600, "${ctx}/frs/nrtmessage/persnonFlowDeal?id="+rows[0].bankAcctNum + "&type=1"+"&infoType="+rows[0].infoTypeCd);
	}
	
	//复核信息
	function oper_review() {
		var rows = grid.getSelectedRows();
		if (rows.length != 1) {
			BIONE.tip('请选择一条记录!');
			return;
		} 
		// 先判断当前用户是否有复核权限 
		var validateFlag=rows[0].validateFlag; 
		if(validateFlag!="2"){
		 	BIONE.tip("请校验数据!");
		 	return false;
		}     
		$.ajax({
				async : false,
				type : "post",
				url : '${ctx}/frs/nrtmessage/isReviewPower?id='+rows[0].bankAcctNum+"&infoType="+rows[0].infoTypeCd,
				success : function(res){
					if(res.isReview == '3'){
						BIONE.tip("请确认机构是否可以复核贷记账户!");
					}else if(res.isReview == '2'){
						BIONE.commonOpenLargeDialog("复核信息", "perFlowDealWin","${ctx}/frs/nrtmessage/persnonFlowDeal?id="+rows[0].bankAcctNum + "&type=2"+"&infoType="+rows[0].infoTypeCd);
					}else{
						BIONE.tip("请确认数据状态!");	
					}
				},
				error : function(e){
					BIONE.tip('操作失败');
				}
			});
	}
	
	//审核信息
	function oper_assess() {
		var rows = grid.getSelectedRows();
		if (rows.length != 1) {
			BIONE.tip('请选择一条记录!');
			return;
		}
		
		// 先判断当前用户是否有个人账户审核权限
		$.ajax({
				async : false,
				type : "post",
				url : '${ctx}/frs/nrtmessage/isAssessPower?id='+rows[0].bankAcctNum+"&infoType="+rows[0].infoTypeCd,
				success : function(res){
					if(res.isAssess == '0'){
						BIONE.tip("该数据不在待审核状态");
					}else if(res.isAssess == '1'){
						BIONE.commonOpenLargeDialog("审核信息", "perFlowDealWin","${ctx}/frs/nrtmessage/persnonFlowDeal?id="+rows[0].bankAcctNum + "&type=3"+"&infoType="+rows[0].infoTypeCd);
					}else{
						BIONE.tip("请确认数据状态!");	
					}
				},
				error : function(e){
					BIONE.tip('操作失败');
				}
			});
	}
	
	//批量删除 
	function oper_delete() {
		var ids = achieveIds();
		if(ids.length > 0){
			var idsStr = ids.join(',');
			$.ligerDialog.confirm('您确定删除这' + ids.length + '条记录吗？', function(yes) {
				if (yes) {
					$.ajax({
						async : false,
						type : "post",
						url : '${ctx}/frs/nrtmessage/deletePersonFlow?&ids=' + idsStr,
						success : function(res){
							BIONE.tip('删除成功');
							grid.loadData();
						},
						error : function(e){
							BIONE.tip('删除失败');
						}
					});
				}
			});	
		} else {
			BIONE.tip('请选择记录');
		}
	}
	
	// 数据批量校验
	function oper_batchValidate(){
		//BIONE.commonOpenDialog('个人账户数据批量校验', 'batchValidateWin', '350', '350','${ctx}/frs/nrtmessage/batchValidateView?module=nrtPersonalInfo');
	    var idsNo = achieveIds();
	     
		if(idsNo.length > 0){ 
		  var idsStr = idsNo.join(',');
		   var dataDate = $("#validateDate").val(); 
			$.ajax({
				cache : false,
				async : true,
				url : "${ctx}/frs/nrtmessage/batchValidate",
				dataType : 'json',
				type : "post",
				height:100,
				beforeSend: function(){
					BIONE.showLoading('数据正在校验中...');
				},
				data : {
					validateDate: dataDate, 
					idsNo : idsStr
				},
				complete: function(){
					BIONE.hideLoading();
				},
				success : function(result) { 
 					//重新加载 列表数据 
					 window.grid.loadData();
					BIONE.tip('  处理成功  ');
				},
				error : function(result, b) {
					BIONE.tip('发现系统错误 <BR>错误码：' + result.status);
				}
			}); 
	   }else {
			BIONE.tip('请选择记录');
		  }
	}
	
	//调用报文脚本
	function oper_execShell() { 
		$.ligerDialog.confirm('确定调用生成报文？', function(yes) {
			if (yes) {
				$.ajax({
					async : false,
					type : "post",
					url : '${ctx}/frs/nrtmessage/execRemoteShell',
					beforeSend: function(){
						BIONE.showLoading('正在执行远程调用脚本...');
					},
					complete: function(){
						BIONE.hideLoading();
					},
					success : function(ret){ 
					   if(ret=="failed"){
					   BIONE.tip('执行脚本失败!');
					   }else{
					   BIONE.tip('执行脚本成功!');
					   }
						
					},
					error : function(e){
						BIONE.tip('执行脚本失败!');
					}
				});
			}
		});	
	}
	//获取选中行的主键
	function achieveIds() {
		//过滤版本
		var ids = [];
		var verId = '';
		var rows = grid.getSelectedRows();
		for ( var i in rows) {
			ids.push(rows[i].bankAcctNum)
		}
		return ids;
	}
	
	function exportInfo(){
		BIONE.commonOpenDialog("流量流向配置导出", "exportWin", 600, 440,	"${ctx}/frs/pisamessage/exportExcel");
	}
	
	function importInfo(){
		BIONE.commonOpenDialog("流量流向配置导入", "importWin", 600, 440,	"${ctx}/report/frame/wizard?type=PisaIdx");
	}

	function exportData(fileName,taskName,dataDate){
		var src = '';
		src = "${ctx}/frs/pbmessage/manager/exportDataInfo?&filepath="+fileName+"&taskNm="+taskNm+"&d="+ new Date().getTime();
		downdload.attr('src', src);
	};

	function exportXls(fileName){
		var src = '';
		src = "${ctx}/frs/pbmessage/exportXls?filepath="+fileName+"&d="+ new Date().getTime();
		downdload.attr('src', src);
	};
	
	function initExport() {
		window["downdload"] = $('<iframe id="download"  style="display: none;"/>');
		$('body').append(downdload);
	};
</script>
</head>
<body>
</body>
</html>