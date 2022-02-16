<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<html>
<head>
<meta name="decorator" content="/template/template1_BS.jsp">
<script type="text/javascript">
	// 全局变量
	var time = new Date();
	var date = new Date(time.getTime() - 4 * 24 * 3600 * 1000);
	var year=date.getFullYear();
	var month=date.getMonth()+1;
	var day = date.getDate();
	var nowmonth=time.getMonth()+1;
	var nowday=time.getDate();
	var nowyear=time.getFullYear();
	var lastfive=year+'-'+month+'-'+day;
	var nowtime=nowyear+'-'+nowmonth+'-'+nowday;	
	var grid;
	var nodeType;
	var realId;
	var dialogWidth = 1000;
	var dialogHeight = 500;
	var moduleType = '${moduleType}';
	var selRowNo;
	var pageName = "report-data-index"; 
	var orgTreeSkipUrl = "${ctx}/frs/rptfill/reject/searchOrgTree?orgType=" + moduleType;			
	$(function() {
		var parent = window.parent;
		// 初始化dialog高、宽
		dialogWidth = $(parent.window).width() * 0.95;
		dialogHeight = $(parent.window).height() - 30;
		//初始化
		ligerSearchForm();
		ligerGrid();
		initButtons();
		BIONE.addSearchButtons("#search", grid, "#searchbtn");

		//渲染查询表单
		function ligerSearchForm() {
			$("#search").ligerForm({
				fields : [{
					display : "信息类型",
					name : "INFO_TYPE_CD",
					newline :true,
					labelWidth : 140, 
					width : 150, 
					type : "select",
					cssClass : "field",
					comboboxName : "handSts_sel",
					attr : { op : "=", field : "info_type_cd"},
					options : {
						 data : [ 
						          { text : '--请选择--', id : ""},
						          { text : '开户', id : "01"},
						          { text : '变更', id : "02"},
						          { text : '销户', id : "03"}], 
						 cancelable  : true
				    }
				},{
					display : "一体机反馈结果",
					name : "MACHINE_FEEDBACK_STATUS",
					newline : false,
					labelWidth : 140, 
					width : 150, 
					type : "select",
					cssClass : "field",
					comboboxName : "handSts_sel",
					attr : { op : "=", field : "machine_feedback_status"},
					options : {
						 data : [ 
						         { text : '--请选择--', id : ""},
						          { text : '成功', id : "成功"},
						          { text : '失败', id : "失败"}], 
						 cancelable  : true
				    }
				},{
					display : "人行逻辑校验反馈结果",
					name : "PBOC_FEEDBACK_LG_STATUS",
					newline : false,
					labelWidth : 140, 
					width : 150, 
					type : "select",
					cssClass : "field",
					comboboxName : "handSts_sel",
					attr : { op : "=", field : "pboc_feedback_lg_status"},
					options : {
						 data : [ 
						         { text : '--请选择--', id : ""},
						          { text : '成功', id : "成功"},
						          { text : '失败', id : "失败"}], 
						 cancelable  : true
				    }
				},{
					display : "人行格式校验反馈结果",
					name : "PBOC_FEEDBACK_GS_STATUS",
					newline : true,
					labelWidth : 140, 
					width : 150, 
					type : "select",
					cssClass : "field",
					comboboxName : "handSts_sel",
					attr : { op : "=", field : "pboc_feedback_gs_status"},
					options : {
						 data : [ 
						         { text : '--请选择--', id : ""},
						          { text : '成功', id : "成功"},
						          { text : '失败', id : "失败"}], 
						 cancelable  : true
				    }
				},{
					display : "存款人身份证件号码",
					name : "DEPOSIT_IDENTITY_NUM",
					newline : false,
					labelWidth : 140, 
					width : 150, 
					type : "text",
					attr : {
						field : "deposit_identity_num",
						op : "="
					}
				},{
					display : "账号",
					name : "BANK_ACCT_NUM",
					newline : false,
					labelWidth : 140, 
					width : 150, 
					type : "text",
					attr : {
						field : "bank_acct_num",
						op : "="
					}
				},{
					display : "机构",
					name : "OPEN_COMBANK_ORG_ID",
					newline : true,
					labelWidth : 140, 
					width : 150,
					comboboxName:"org_tree",
					type : "select",
					attr : {
						field : "open_combank_org_id",
						op : "="
					},
					options:{
						   onBeforeOpen : function() {
						       var rptFillFlag = "detail";
						       var Dname = "org_tree";
						       var height = $(window).height() - 120;
						       var width = $(window).width() - 480;
						       window.BIONE.commonOpenDialog("机构树", "taskOrgWin", width, height, orgTreeSkipUrl+"&rptFillFlag="+rptFillFlag+"&Dname="+Dname, null);
						       return false;
						     }, 
						     cancelable  : true
					}
					},{
						display : "报送日期(从)",
						name : "STATISTICS_DT1",
						newline : false,
						labelWidth : 140, 
						width : 150, 
						type : "date",
						attr : {
							field : "to_char("+"statistics_dt"+",'yyyymmdd')",
							op : ">="
						},
						options : { format : "yyyyMMdd",
							        onChangeDate : function(){
							     	 	$.ligerui.get('handSts_sel1').selectValue(""); 
								        var num = $("#BANK_ACCT_NUM").val();
								        if(num==null||num==""){
								        	$("#STATISTICS_DT2").val($("#STATISTICS_DT1").val())	
								        }
								        var beginTime = $("#STATISTICS_DT1").val();
								          var endTime = $("#STATISTICS_DT2").val();
								        	
								          if(beginTime!=null && beginTime!='' && endTime!=null && endTime!=''){
												if(beginTime-endTime > 0){
													BIONE.tip("开始时间大于结束时间。请重新输入！");
												        }
											    }   											        
								   }
						}
					},{
						display : "报送日期(至)",
						name : "STATISTICS_DT2",
						newline : false,
						labelWidth : 140, 
						width : 150, 
						type : "date",
						attr : {
							field : "to_char("+"statistics_dt"+",'yyyymmdd')",
							op : "<="
						},
						options : { format : "yyyyMMdd",
							        onChangeDate : function(){
							     	 	$.ligerui.get('handSts_sel1').selectValue(""); 
							              var num2=$("#BANK_ACCT_NUM").val();
							              if(num2==null||num2==""){
							            	  $("#STATISTICS_DT1").val($("#STATISTICS_DT2").val())
							              }
							              var beginTime = $("#STATISTICS_DT1").val();
								          var endTime = $("#STATISTICS_DT2").val();
								        	
								          if(beginTime!=null && beginTime!='' && endTime!=null && endTime!=''){
												if(beginTime-endTime > 0){
													BIONE.tip("开始时间大于结束时间。请重新输入！");
												        }
											    }   			
							        }	        
						}
					},{
						display : "数据日期",
						name : "FINAL_SUBMIT_DATE",
						newline : true,
						labelWidth : 140, 
						width : 150, 
						type : "select",
						cssClass : "field",
						comboboxName : "handSts_sel1",
						attr : {  field : "to_char(statistics_dt,'yyyy-mm-dd')",
					      op : ">="
						},	
						options : {
							initValue : lastfive,
							data : [ 
							         { text : '前五天数据', id : lastfive}, 
							         { text : '全部', id : ""}],							       
							 cancelable  : true,
					    }
					}]
			});
		}
		
		
		//初始化按钮
		function initButtons() {
			var btns = [ {
				text : "数据下载",
				icon : "download",
				click : download,
				operNo : "download"
			}
			];
			BIONE.loadToolbar(grid, btns, function() {});
		}
		
		
		//数据下载
		function download(){
			download = $('<iframe id="download"  style="display: none;"/>');
	        $('body').append(download);
	        var data = queryConditionList();
	 	    var condition = data.condition;
			$.ajax({
				type : "POST",
				url : "${ctx}/frs/personal/personalAccount/getCount?pageName="+pageName+"&moduleType="+moduleType+"&condition="+condition,
				success : function(result) {
					if(result>10000){						
						$.ligerDialog.confirm("数据量太大,仅支持前一万条记录的下载,是否继续下载?", function(yes) { 
							if(yes){
						 	    var src = "${ctx}/frs/personal/personalAccount/downLoad?pageName="+pageName+"&moduleType="+moduleType+"&isDownload=Y&condition="+condition;
						 		download.attr('src', src);
							}
						})
					}else{
				 	    var src = "${ctx}/frs/personal/personalAccount/downLoad?pageName="+pageName+"&moduleType="+moduleType+"&isDownload=Y&condition="+condition;
				 		download.attr('src', src);
					}
				}
			});
		}
		
		//获取查询条件数据
	    function queryConditionList(){
	   	   var form = $('#formsearch');
	  	   var data = {};
	  	   var rule = BIONE.bulidFilterGroup(form);
	        if (rule.rules.length) {
	  			data.condition=JSON2.stringify(rule);
	  		}else{
	  			data.condition="";
	  		}
	        return data; 
	     }
		
		//渲染GRID
		function ligerGrid() {
			parent.rptGrid = grid = $("#maingrid").ligerGrid({
				width : "100%",
				height : "99%",
				columns : [ {
					display : "信息类型",
					name : "infoTypeCd",
					width : "10%",
					align: "center",
					render : function(rowdata){
						if(rowdata.infoTypeCd=='01'){
							return "开户";
						}else if(rowdata.infoTypeCd=='02'){
							return "变更";
						}else if(rowdata.infoTypeCd=='03'){
							return "销户";
						}
					}
				},{
					display : "报送日期",
					name : "statisticsDt",
					width : "10%",
					align: "center",
					render : function(rowdata){
						return rowdata.statisticsDt.substr(0,10);
					}
				},{
					display : "机构",
					name : "openCombankOrgName",
					width : "20%",
					align: "center"
				},{
					display : "一体机反馈结果",
					name : "machineFeedbackStatus",
					width : "20%",
					align: "center"
				},{
					display : "人行逻辑校验反馈结果",
					name : "pbocFeedbackLgStatus",
					width : "20%",
					align: "center"
				},{
					display : "人行格式校验反馈结果",
					name : "pbocFeedbackGsStatus",
					width : "20%",
					align: "center"
				},{
					display : "存款人姓名",
					name : "depositName",
					width : "10%",
					align: "center"
				},{
					display : "存款人身份证件种类",
					name : "depositIdentityTypeCd",
					width : "20%",
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
						}
					}
				},{
					display : "存款人身份证件号码",
					name : "depositIdentityNum",
					width : "20%",
					align: "center"
				},{
					display : "身份证件到期日",
					name : "identityDueDt",
					width : "10%",
					align: "center"
				},{
					display : "发证机关所在地的地区代码",
					name : "issueOrgAreaCd",
					width : "20%",
					align: "center"
				},{
					display : "存款人类别",
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
					display : "存款人邮编",
					name : "depositPostCd",
					width : "10%",
					align: "center"
				},{
					display : "存款人地址",
					name : "depositAddr",
					width : "40%",
					align: "center"
				},{
					display : "存款人电话",
					name : "depositTel",
					width : "20%",
					align: "center"
				},{
					display : "代理人名称",
					name : "agentName",
					width : "10%",
					align: "center"
				},{
					display : "代理人身份证件种类",
					name : "agentIdentityTypeCd",
					width : "20%",
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
						}
					}
				},{
					display : "代理人身份证件号码",
					name : "agentIdentityNum",
					width : "20%",
					align: "center"
				},{
					display : "代理人国籍",
					name : "agentCountryCd",
					width : "10%",
					align: "center"
				},{
					display : "代理人电话",
					name : "agentTel",
					width : "20%",
					align: "center"
				},{
					display : "开户银行金融机构编码",
					name : "openBankFinOrgCd",
					width : "20%",
					align: "center"
				},{
					display : "账号",
					name : "bankAcctNum",
					width : "20%",
					align: "center"
				},{
					display : "账户种类",
					name : "bankAcctKindCd",
					width : "10%",
					align: "center",
					render : function(rowdata){
 						if(rowdata.bankAcctKindCd=='01'){
							return "借记结算账户";
						}else if(rowdata.bankAcctKindCd=='02'){
							return "贷记结算账户";
						}else if(rowdata.bankAcctKindCd=='03'){
							return "非结算账户";
						}
					}
				},{
					display : "卡号",
					name : "cardCd",
					width : "20%",
					align: "center"
				},{
					display : "卡到期日",
					name : "cardDueDt",
					width : "10%",
					align: "center"
				},{
					display : "账户介质",
					name : "bankAcctMedia",
					width : "10%",
					align: "center",
					render : function(rowdata){
						if(rowdata.bankAcctMedia=='01'){
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
						}
					}
				},{
					display : "销卡日期",
					name : "expireCardDt",
					width : "20%",
					align: "center"
				},{
					display : "卡状态",
					name : "cardStatCd",
					width : "10%",
					align: "center",
					render : function(rowdata){
						if(rowdata.cardStatCd=='01'){
							return "正常";
						}else if(rowdata.cardStatCd=='02'){
							return "销卡";
						}
					}
				},{
					display : "账户类型",
					name : "bankAcctTypeCd",
					width : "10%",
					align: "center",
					render : function(rowdata){
						if(rowdata.bankAcctTypeCd=='01'){
							return "Ⅰ类";
						}else if(rowdata.bankAcctTypeCd=='02'){
							return "Ⅱ类";
						}else if(rowdata.bankAcctTypeCd=='03'){
							return "Ⅲ类";
						}
					}
				},{
					display : "绑定I类账户账号",
					name : "bindingiAcctNum",	
					width : "20%",
					align: "center"
				},{
					display : "绑定I类账户开户银行金融机构编码",
					name : "bindingiFinOrgCd",
					width : "20%",
					align: "center"
				},{
					display : "开户日期",
					name : "openDt",
					width : "10%",
					align: "center"
				},{
					display : "销户日期",
					name : "expireDt",
					width : "10%",
					align: "center"
				},{
					display : "账户状态",
					name : "acctStatCd",
					width : "10%",
					align: "center",
					render : function(rowdata){
						if(rowdata.acctStatCd=='01'){
							return "正常";
						}else if(rowdata.acctStatCd=='02'){
							return "销户";
						}else if(rowdata.acctStatCd=='03'){
							return "未激活";
						}
					}
				},{
					display : "币种",
					name : "currencyCd",
					width : "10%",
					align: "center",
					render :  function(rowdata){
						if(rowdata.currencyCd=='01'){
							return "人民币";
						}
					}
				},{
					display : "是否为军人保障卡",
					name : "armySecurityCard",
					width : "10%",
					align: "center",
					render :  function(rowdata){
						if(rowdata.armySecurityCard=='01'){
							return "是";
						}else if(rowdata.armySecurityCard=='02'){
							return "不是";
						}
					}
				},{
					display : "是否为社会保障卡",
					name : "societySecurityCard",
					width : "10%",
					align: "center",
					render :  function(rowdata){
						if(rowdata.societySecurityCard=='01'){
							return "是";
						}else if(rowdata.societySecurityCard=='02'){
							return "不是";
						}
					}
				},{
					display : "核实结果",
					name : "auditResultCd",
					width : "10%",
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
						}
					}
				},{
					display : "无法核实原因",
					name : "noAuditResultDesc",
					width : "20%",
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
						}
					}
				},{
					display : "处置方法",
					name : "disposeMode",
					width : "20%",
					align: "center",
					render :  function(rowdata){
						if(rowdata.disposeMode=='01'){
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
						}
					}
				},{
					display : "开户渠道",
					name : "openChannalCd",
					width : "20%",
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
							return "其他";
						}
					}
				} ],
				checkbox : false,
				userPager : true,
				rownumbers : true,
				alternatingRow : true,//附加奇偶行效果行
				colDraggable : false,
				isScroll : true,
				selectRowButtonOnly : true ,
				dataAction : 'server',//从后台获取数据
				method : 'post',
				url : "${ctx}/frs/personal/personalAccount/getReportList?pageName="+pageName+"&moduleType="+moduleType,
	            delayLoad:false
			});
		}

	});
	function test(id, text,Dname){
		$.ligerui.get(Dname).setValue(id);
	 	$.ligerui.get(Dname).setData(id);
	 	$.ligerui.get(Dname).setText(text);
	}
	

	
</script>
</head>
<body>
</body>
</html>