<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<html>
<head>
<meta name="decorator" content="/template/template1_BS.jsp">
<script type="text/javascript">
	var grid;
	var renderData;
	
	$(function(){
		/* initRanderData(); */
		searchForm();
		initGrid();
		initButtons();
		initExport();
		BIONE.addSearchButtons("#search", grid, "#searchbtn");
	});

	//初始化数据码值数据
	/* function initRanderData(){
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
	} */
	
	function searchForm() {
		$("#search").ligerForm({
			fields : [  {
				display : '数据日期（从）',
				name : 'datadate',
				newline : false,
				labelWidth : 120, 
				width : 150, 
				type : "date",
				cssClass : "field",
				 
				attr : {
					field : "to_char("+"datadate"+",'yyyymmdd')",
					op : ">="
				},
				options : { format : "yyyyMMdd",
			        onChangeDate : function(){
			     	 	$.ligerui.get('handSts_sel5').selectValue("");  
				        var beginTime = $("#datadate").val();
				          var endTime = $("#datadate2").val();
				        	
				          if(beginTime!=null && beginTime!='' && endTime!=null && endTime!=''){
								if(beginTime-endTime > 0){
									BIONE.tip("开始时间大于结束时间。请重新输入！");
								        }
							    }   			
				     }
		         }
			},{
				display : '（至）数据日期',
				name : 'datadate2',
				newline : false,
				labelWidth : 120, 
				width : 150, 
				type : "date",
				cssClass : "field", 
				attr : {
					field : "to_char("+"datadate"+",'yyyymmdd')",
					op : "<="
				},
				options : { format : "yyyyMMdd",
			        onChangeDate : function(){
			     	 	$.ligerui.get('handSts_sel6').selectValue(""); 
			              
			              var beginTime = $("#datadate").val();
				          var endTime = $("#datadate2").val();
				        	
				          if(beginTime!=null && beginTime!='' && endTime!=null && endTime!=''){
								if(beginTime-endTime > 0){
									BIONE.tip("开始时间大于结束时间。请重新输入！");
								        }
							    }   			
			        }	        
		        }   
			}]
		});

	}
	
	//初始化Grid
	function initGrid() {
		grid = $("#maingrid").ligerGrid({
			InWindow : false,
			height : '99%',
			width : '100%',
			columns : [ {
				display : "机构号",
				name : "openCombankOrgId",
				width : "6%", 
				align: "center" 
				
			},{
				display : '机构名称',
				name : 'openCombankOrgId',
				width : '8%' ,
				render : function(rowdata){ 
				     if(rowdata.openCombankOrgId=='0300'){ return "广场支行"; }
				 else if(rowdata.openCombankOrgId=='0400'){ return "东城支行"; }
				else if(rowdata.openCombankOrgId=='0500'){ return "商城支行"; }
				else if(rowdata.openCombankOrgId=='0600'){ return "北京路支行"; }
				else if(rowdata.openCombankOrgId=='0700'){ return "新华西街支行"; }
				else if(rowdata.openCombankOrgId=='0800'){ return "光华支行"; }
				else if(rowdata.openCombankOrgId=='0900'){ return "凤凰支行"; }
				else if(rowdata.openCombankOrgId=='1000'){ return "新城支行"; }
				else if(rowdata.openCombankOrgId=='1100'){ return "民生支行"; }
				else if(rowdata.openCombankOrgId=='1200'){ return "永康支行"; }
				else if(rowdata.openCombankOrgId=='1300'){ return "利民支行"; }
				else if(rowdata.openCombankOrgId=='1400'){ return "科技支行"; }
				else if(rowdata.openCombankOrgId=='1500'){ return "新市区支行"; }
				else if(rowdata.openCombankOrgId=='1600'){ return "开发区支行"; }
				else if(rowdata.openCombankOrgId=='1700'){ return "光明支行"; }
				else if(rowdata.openCombankOrgId=='1800'){ return "新世纪支行"; }
				else if(rowdata.openCombankOrgId=='1900'){ return "新华东街支行"; }
				else if(rowdata.openCombankOrgId=='2000'){ return "中山支行"; }
				else if(rowdata.openCombankOrgId=='2100'){ return "火车站支行"; }
				else if(rowdata.openCombankOrgId=='2200'){ return "丽景支行"; }
				else if(rowdata.openCombankOrgId=='2300'){ return "西塔支行"; }
				else if(rowdata.openCombankOrgId=='2400'){ return "湖滨支行"; }
				else if(rowdata.openCombankOrgId=='4000'){ return "鼓楼支行"; }
				else if(rowdata.openCombankOrgId=='4100'){ return "北塔支行"; }
				else if(rowdata.openCombankOrgId=='4300'){ return "阅海支行"; }
				else if(rowdata.openCombankOrgId=='4500'){ return "宝湖支行"; }
				else if(rowdata.openCombankOrgId=='4600'){ return "同心路支行"; }
				else if(rowdata.openCombankOrgId=='2700'){ return "贺兰支行"; }
				else if(rowdata.openCombankOrgId=='4200'){ return "永宁支行"; }
				else if(rowdata.openCombankOrgId=='4201'){ return "永宁宁和街支行"; }
				else if(rowdata.openCombankOrgId=='2900'){ return "灵武支行"; }
				else if(rowdata.openCombankOrgId=='4700'){ return "宁东支行"; }
				else if(rowdata.openCombankOrgId=='2602'){ return "石嘴山分行营业部"; }
				else if(rowdata.openCombankOrgId=='2600'){ return "大武口支行"; }
				else if(rowdata.openCombankOrgId=='3700'){ return "惠农支行"; }
				else if(rowdata.openCombankOrgId=='3900'){ return "平罗支行"; }
				else if(rowdata.openCombankOrgId=='2501'){ return "吴忠分行营业部"; }
				else if(rowdata.openCombankOrgId=='2502'){ return "利通支行"; }
				else if(rowdata.openCombankOrgId=='2508'){ return "红寺堡支行"; }
				else if(rowdata.openCombankOrgId=='2504'){ return "青铜峡支行"; }
				else if(rowdata.openCombankOrgId=='2507'){ return "盐池支行"; }
				else if(rowdata.openCombankOrgId=='5100'){ return "西安分行营业部"; }
				else if(rowdata.openCombankOrgId=='5101'){ return "西安经济开发区支行"; }
				else if(rowdata.openCombankOrgId=='5102'){ return "西安长安路支行"; }
				else if(rowdata.openCombankOrgId=='5103'){ return "西安大庆路支行"; }
				else if(rowdata.openCombankOrgId=='5104'){ return "西安雁塔路支行"; }
				else if(rowdata.openCombankOrgId=='5200'){ return "天津分行营业部"; }
				else if(rowdata.openCombankOrgId=='5201'){ return "天津北辰支行"; }
				else if(rowdata.openCombankOrgId=='5202'){ return "天津西青支行"; }
				else if(rowdata.openCombankOrgId=='5203'){ return "天津河西支行"; }
				else if(rowdata.openCombankOrgId=='5204'){ return "天津河东支行"; }
				else if(rowdata.openCombankOrgId=='6400000'){ return "宁夏区"; }
				else if(rowdata.openCombankOrgId=='9999'){ return "宁夏银行总行"; }
				else if(rowdata.openCombankOrgId=='6100000'){ return "陕西省"; }
				else if(rowdata.openCombankOrgId=='1200000'){ return "天津区"; }
				else if(rowdata.openCombankOrgId=='6401000'){ return "银川市"; }
				else if(rowdata.openCombankOrgId=='6402000'){ return "石嘴山市"; }
				else if(rowdata.openCombankOrgId=='6403000'){ return "吴忠市"; }
				else if(rowdata.openCombankOrgId=='6404000'){ return "固原市"; }
				else if(rowdata.openCombankOrgId=='6405000'){ return "中卫市"; }
				else if(rowdata.openCombankOrgId=='6101000'){ return "西安市"; }
				else if(rowdata.openCombankOrgId=='0100'){ return "总行营业部"; }
				else if(rowdata.openCombankOrgId=='0200'){ return "西城支行"; }
				else if(rowdata.openCombankOrgId=='2699'){ return "石嘴山分行"; }
				else if(rowdata.openCombankOrgId=='2599'){ return "吴忠分行"; }
				else if(rowdata.openCombankOrgId=='3600'){ return "固原支行"; }
				else if(rowdata.openCombankOrgId=='3199'){ return "中卫分行"; }
				else if(rowdata.openCombankOrgId=='2506'){ return "中卫分行营业部"; }
				else if(rowdata.openCombankOrgId=='2505'){ return "中宁支行"; }
				else if(rowdata.openCombankOrgId=='5199'){ return "西安分行"; }
				else if(rowdata.openCombankOrgId=='5299'){ return "天津分行"; }
				else if(rowdata.openCombankOrgId=='0000'){ return "总行清算中心"; }
				else if(rowdata.openCombankOrgId=='2800'){ return "总行办公室"; }
				else if(rowdata.openCombankOrgId=='2801'){ return "投资银行部"; }
				else if(rowdata.openCombankOrgId=='2802'){ return "运管中心"; }
				else if(rowdata.openCombankOrgId=='3000'){ return "电子银行部"; }
				else if(rowdata.openCombankOrgId=='3200'){ return "国际业务部"; }
				else if(rowdata.openCombankOrgId=='3300'){ return "客服中心"; }
				else if(rowdata.openCombankOrgId=='3400'){ return "票据中心"; }
				else if(rowdata.openCombankOrgId=='3500'){ return "现金调配中心"; }
				else if(rowdata.openCombankOrgId=='5105'){ return "西安西大街支行"; }
				else if(rowdata.openCombankOrgId=='3800'){ return "小企业信贷中心"; }
				else if(rowdata.openCombankOrgId=='3801'){ return "小企业信贷中心吴忠分中心"; }
				else if(rowdata.openCombankOrgId=='1201010'){ return "天津市和平区"; }
				else if(rowdata.openCombankOrgId=='1201020'){ return "天津市河东区"; }
				else if(rowdata.openCombankOrgId=='1201030'){ return "天津市河西区"; }
				else if(rowdata.openCombankOrgId=='1201110'){ return "天津市西青区"; }
				else if(rowdata.openCombankOrgId=='1201130'){ return "天津市北辰区"; }
				else if(rowdata.openCombankOrgId=='5294'){ return "天津分行小贷服务中心"; }
				else if(rowdata.openCombankOrgId=='6101010'){ return "西安市辖区"; }
				else if(rowdata.openCombankOrgId=='6401010'){ return "银川市辖区"; }
				else if(rowdata.openCombankOrgId=='6401210'){ return "永宁县地区"; }
				else if(rowdata.openCombankOrgId=='6401220'){ return "贺兰县地区"; }
				else if(rowdata.openCombankOrgId=='6401810'){ return "灵武市"; }
				else if(rowdata.openCombankOrgId=='3100'){ return "机构部"; }
				else if(rowdata.openCombankOrgId=='6402010'){ return "石嘴山市辖区"; }
				else if(rowdata.openCombankOrgId=='6402020'){ return "大武口区"; }
				else if(rowdata.openCombankOrgId=='6402050'){ return "惠农区"; }
				else if(rowdata.openCombankOrgId=='6402210'){ return "平罗县"; }
				else if(rowdata.openCombankOrgId=='6403010'){ return "吴忠市辖区"; }
				else if(rowdata.openCombankOrgId=='6403230'){ return "盐池县"; }
				else if(rowdata.openCombankOrgId=='6403810'){ return "青铜峡市"; }
				else if(rowdata.openCombankOrgId=='6404010'){ return "固原市辖区"; }
				else if(rowdata.openCombankOrgId=='6405010'){ return "中卫市辖区"; }
				else if(rowdata.openCombankOrgId=='6405210'){ return "中宁县"; }
				else if(rowdata.openCombankOrgId=='6401811'){ return "灵武支行汇总"; }
				else if(rowdata.openCombankOrgId=='6401012'){ return "新华西街支行管理机构"; }
				else if(rowdata.openCombankOrgId=='6401013'){ return "西城支行管理机构"; }
				else if(rowdata.openCombankOrgId=='6405011'){ return "中卫分行（沙波头区管理机构）"; }
				else if(rowdata.openCombankOrgId=='6401014'){ return "总行营业部管理机构"; }
				else if(rowdata.openCombankOrgId=='5106'){ return "西安沣东支行"; }
				else if(rowdata.openCombankOrgId=='0801'){ return "民族南街支行"; }
				else if(rowdata.openCombankOrgId=='4001'){ return "解放东街支行"; }
				else if(rowdata.openCombankOrgId=='3106'){ return "中卫鼓楼北街支行"; }
				else if(rowdata.openCombankOrgId=='2513'){ return "新村街支行"; }
				else if(rowdata.openCombankOrgId=='1801'){ return "银川海亮支行"; }
				else if(rowdata.openCombankOrgId=='2701'){ return "银川德胜支行"; }
				else if(rowdata.openCombankOrgId=='6404250'){ return "彭阳县"; }
				else if(rowdata.openCombankOrgId=='3603'){ return "彭阳支行"; }
				else if(rowdata.openCombankOrgId=='3601'){ return "固原文化街支行"; }
				else if(rowdata.openCombankOrgId=='0201'){ return "湖畔嘉苑支行"; }
				else if(rowdata.openCombankOrgId=='0701'){ return "长城花园支行"; }
				else if(rowdata.openCombankOrgId=='3101'){ return "中卫鼓楼北街小微支行"; }
				else if(rowdata.openCombankOrgId=='2301'){ return "河东机场支行"; }
				else if(rowdata.openCombankOrgId=='3103'){ return "海原支行"; }
				else if(rowdata.openCombankOrgId=='1601'){ return "银川宁安大街支行"; }
				else if(rowdata.openCombankOrgId=='3010'){ return "信用卡中心（直销团队"; }
				else if(rowdata.openCombankOrgId=='2510'){ return "金积支行"; }
				else if(rowdata.openCombankOrgId=='2509'){ return "同心支行"; }
				else if(rowdata.openCombankOrgId=='2511'){ return "龙海支行"; }
				else if(rowdata.openCombankOrgId=='6403011'){ return "吴忠分行营业部汇总"; }
				else if(rowdata.openCombankOrgId=='3102'){ return "中卫鼓楼南街支行"; }
				else if(rowdata.openCombankOrgId=='6403240'){ return "同心县地区"; }
				else if(rowdata.openCombankOrgId=='0101'){ return "兴庆府大院支行"; }
				else if(rowdata.openCombankOrgId=='0301'){ return "六盘山路支行"; }
				else if(rowdata.openCombankOrgId=='1101'){ return "文化西街支行"; }
				else if(rowdata.openCombankOrgId=='3701'){ return "惠安支行"; }
				else if(rowdata.openCombankOrgId=='6405220'){ return "海原县"; }
				else if(rowdata.openCombankOrgId=='1001'){ return "福州北街支行"; }
				else if(rowdata.openCombankOrgId=='1102'){ return "高尔夫支行"; }
				else if(rowdata.openCombankOrgId=='2512'){ return "裕民西路支行"; }
				else if(rowdata.openCombankOrgId=='3105'){ return "中卫鼓楼东街支行"; }
				else if(rowdata.openCombankOrgId=='0601'){ return "银川兴庆支行"; }
				else if(rowdata.openCombankOrgId=='0102'){ return "银川市民大厅支行"; }
				else if(rowdata.openCombankOrgId=='6404220'){ return "西吉县"; }
				else if(rowdata.openCombankOrgId=='3602'){ return "西吉支行"; }
				else if(rowdata.openCombankOrgId=='2901'){ return "灵武西湖支行"; }
				else if(rowdata.openCombankOrgId=='3104'){ return "中宁宁安东街支行"; }
				else if(rowdata.openCombankOrgId=='6401011'){ return "总行本部"; }
				else if(rowdata.openCombankOrgId=='3699'){ return "固原分行"; }
				else if(rowdata.openCombankOrgId=='3001'){ return "宁夏银行直销银行"; }
				else if(rowdata.openCombankOrgId=='3604'){ return "泾源支行"; }
				else if(rowdata.openCombankOrgId=='6404240'){ return "泾源县"; }  
				}
				
				
				
			},{
				display : '开户',
				columns : [
				           {display : "Ⅰ类户",
							name : "param1",
							width : "10%",
							columns : [
										{
											display : "普通账户",
											name : "openFcomm",
											width : "6%",
											align: "center"	
										},    
										{
											display :  "社保卡",
											name : "openFcard",
											width : "6%",
											align: "center"	
										},
										{
											display : "合计",
											name : "openFall",
											width : "6%",
											align: "center"	
										}
							           ],
							align: "center"
							},{
								display : "Ⅱ类户",
								name : "param2",
								width : "10%",
								columns : [
										{
											display : "普通账户",
											name : "openScomm",
											width : "6%",
											align: "center"	
										},
										{
											display : "社保卡",
											name : "openScard",
											width : "6%",
											align: "center"	
										},
										{
											display : "合计",
											name : "openSall",
											width : "6%",
											align: "center"	
										}
							           ],
								align: "center"	
							},{
								display : "Ⅲ类户",
								 name : "openTcomm",
								 width : "8%", 
								align: "center"	
							}]
			},{
				display : '销户',
				columns : [
				           {display : "Ⅰ类户",
							name : "param3",
							//width : "10%",
							columns : [
										{
											display : "普通账户",
											name : "closeFcomm",
											width : "6%",
											align: "center"	
										},
										{
											display : "社保卡",
											name : "closeFcard",
											width : "6%",
											align: "center"	
										},
										{
											display : "合计",
											name : "closeFall",
											width : "6%",
											align: "center"	
										}
							           ],
							align: "center"
							},{
								display : "Ⅱ类户",
								name : "param4",
								//width : "10%",
								columns : [
										{
											display : "普通账户",
											name : "closeScomm",
											width : "6%",
											align: "center"	
										},
										{
											display : "社保卡",
											name : "closeScard",
											width : "6%",
											align: "center"	
										},
										{
											display : "合计",
											name : "closeSall",
											width : "6%",
											align: "center"	
										}
							           ],
								align: "center"	
							},{
								display : "Ⅲ类户",
								 name : "closeTcomm",
								 width : "8%", 
								align: "center"	
							}]
			}          ],
			checkbox : false,
			//usePager : true,
			isScroll : true,
			//rownumbers : true,
			//alternatingRow : true,//附加奇偶行效果行
			//colDraggable : true,
			dataAction : 'server',//从后台获取数据
			method : 'post',
			url : '${ctx}/frs/nrtmessage/acctPersonalStatistic',
			sortName : 'OPEN_COMBANK_ORG_ID', //第一次默认排序的字段
			sortOrder : 'asc',
			toolbar : {}
		});
	}

	function initButtons() {
		var btns = [ {
			text : '导出Excel',
			click : exportInfo,
			icon : 'fa-download'
		} ];
		  BIONE.loadToolbar(grid, btns, function() {}); 
	}
	
	function exportInfo(){ 
		 var data = queryDownLoadList();
		 console.log(data);		
		 var condition = data.condition;
		 
		 var src = "${ctx}/frs/nrtmessage/flTaskList?otherCondition="+encodeURI(encodeURI(condition));
		 download.attr('src', src);
	}
	function initExport(){
		  download = $('<iframe id="download"  style="display: none;"/>');
		   $('body').append(download);  
		}
	function queryDownLoadList(){
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
	
	
	
	
</script>
</head>
<body>
</body>
</html>