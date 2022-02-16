<%@ page language="java" pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<html>
<head>
<meta name="decorator" content="/template/template5.jsp">

<script type="text/javascript">
	var groupicon = "${ctx}/images/classics/icons/communication.gif";
	var id = "${id}";
	// 1-查看 2-复核 3-审核
	var type = "${type}";
	var infoType = "${infoType}";

	// 判断字符串是否为空
	function isNull(value) {
		if (value == null || value == '' || typeof value == 'undefined') {
			return true;
		}
		return false;
	}

	// 校验是否大写字母
	jQuery.validator.addMethod("checkUppercase1", function(value, element) {
		var uppercaseReg = /^[A-Z]+$/;
		return this.optional(element) || (uppercaseReg.test(value));
	}, "必须大写字母.");

	// 校验姓名（中文、<大写英文、空格>）
	jQuery.validator.addMethod("checkName", function(value, element) {
		var uppercaseReg = /^[A-Z\u4e00-\u9fa5\ \·]{1,60}$/;
		return this.optional(element) || (uppercaseReg.test(value));
	}, "名称格式不正确，例如：Jandey Liu, 张·三, 李四");

	// 校验日期
	jQuery.validator
			.addMethod(
					"checkBirthdate",
					function(value, element) {
						var birthdateReg = /^(\d{4})-(0\d{1}|1[0-2])-(0\d{1}|[12]\d{1}|3[01])$/;
						return this.optional(element)
								|| (birthdateReg.test(value));
					}, "YYYY-MM-DD");

	// 校验证件种类
	jQuery.validator.addMethod("checkIdentityTypeCd", function(value, element) {
		// 开户日期
		var openDt = $("#mainform [name='openDt']").val();
		// 核实结果
		var auditResultCd = $("#mainform [name='auditResultCd']").val();
		if (isNull(value) && ('2000-04-01' > openDt || '04' != auditResultCd)) {
			return false;
		}

		return true;
	}, "当开户日期在2000年4月1号之前(包括等于) 以及“核实结果”为“匿名”时，存款人身份证件种类可以为空");

	// 校验代理人证件种类
	jQuery.validator.addMethod("checkAgentIdentityTypeCd", function(value,
			element) {
		// 代理人姓名
		var agentName = $("#mainform [name='agentName']").val();

		if (!isNull(agentName) && isNull(value)) {

			return false;
		} else if (isNull(agentName) && !isNull(value)) {
			return false;
		}

		return true;
	}, "当代理人姓名为空时，代理身份证件种类可以为空");

	// 校验身份证号  2020-5-22 14:01:16
	jQuery.validator.addMethod("ckrlx", function(value, element) {
		var len = value.length;
		var typeCd = $("#mainform [name='depositIdentityTypeCd']").val();
		if (!isNull(typeCd) && isNull(value)) {
			return false;
		}
		var reg = /(^\d{15}$)|(^\d{18}$)|(^\d{17}(\d|X|x)$)/;
		return reg.test(value); //this.optional(element) || ((len==18||len==15)&&/^\d+$/.test(value));
	}, "证件类型不为空，证件号不为空；一代身份证:15位或18位；二代身份证:18位");

	// 校验代理人身份证号
	jQuery.validator.addMethod("checkAgentIdentityNum",
			function(value, element) {
				var len = value.length;
				var typeCd = $("#mainform [name='agentIdentityTypeCd']").val();
				if (!isNull(typeCd) && isNull(value)) {
					return false;
				}

				return this.optional(element)
						|| ((len == 18 || len == 15) && /^\d+$/.test(value));
			}, "代理人证件类型不为空，代理人证件号不为空；一代身份证:15位或18位；二代身份证:18位");

	// [校验代理人国籍]
	jQuery.validator.addMethod("checkAgentCountryCd", function(value, element) {
		var len = value.length;
		var typeCd = $("#mainform [name='agentIdentityTypeCd']").val();
		if (!isNull(typeCd) && isNull(value)) {
			return false;
		}
		return this.optional(element);// || ((len==18||len==15)&&/^\d+$/.test(value));
	}, "3位大写字母;代理人证件类型不为空，代理人国籍不为空");

	// 校验身份证件到期日
	jQuery.validator.addMethod("checkIdentityDueDt", function(value, element) {
		var len = value.length;
		var typeCd = $("#mainform [name='depositIdentityTypeCd']").val();
		if (!isNull(typeCd) && isNull(value)) {
			return false;
		}

		return this.optional(element) || (len <= 10)
				&& /^[\d\-]{10}$/.test(value);
	}, "yyyyMMdd;证件类型不为空，证件到期日不为空;多个之间用半角分号隔开");

	//发证机关所在地的地区代码
	jQuery.validator
			.addMethod(
					"fzqdm",
					function(value, element) {
						var len = value.length;
						// 证件类型
						var typeCd = $(
								"#mainform [name='depositIdentityTypeCd']")
								.val();
						if (!isNull(typeCd) && isNull(value)) {
							return false;
						}

						// 存款人类别
						var typeCd = $("#mainform [name='depositClassCd']")
								.val();
						if (!isNull(value) && !isNull(typeCd)) {
							var fzqdms;
							var depositClassCds;
							if (value.indexOf(':') != -1
									&& typeCd.indexOf(':') != -1) {
								fzqdms = value.split(':');
								depositClassCds = typeCd.split(':');
								for (var i = 0; i < fzqdms.length; i++) {
									if ('02' == depositClassCds[i]
											&& '1100' != fzqdms[i]) {
										return false;
									}
									if ('03' == depositClassCds[i]
											&& '6530' != fzqdms[i]) {
										return false;
									}
									if (('04' == depositClassCds[i] || '05' == depositClassCds[i])
											&& '1000' != fzqdms[i]) {
										return false;
									}
								}
							} else {
								fzqdms = value;
								depositClassCds = typeCd;
								if ('02' == depositClassCds && '1100' != fzqdms) {
									return false;
								}
								if ('03' == depositClassCds && '6530' != fzqdms) {
									return false;
								}
								if (('04' == depositClassCds || '05' == depositClassCds)
										&& '1000' != fzqdms) {
									return false;
								}
							}

						}

						return this.optional(element)
								|| ((len == 4) && /^\d+$/.test(value));
					},
					"地区代码长度:4位数字;存款人类别是军人时，地区代码为1100；存款人类别是武警时，地区代码为6530；存款人类别是香港、澳门、台湾地区居民和外国公民时，地区代码为1000。");

	// 校验存款人类别
	jQuery.validator.addMethod("checkDepositClassCd", function(value, element) {
		var len = value.length;
		var typeCd = $("#mainform [name='depositIdentityTypeCd']").val();
		if (!isNull(typeCd) && isNull(value)) {
			return false;
		}

		return true;
	}, "证件类型不为空，存款人类别不为空;多个之间用半角分号隔开");

	// 校验存款人国籍/地区
	jQuery.validator
			.addMethod(
					"checkDepositCountryCd",
					function(value, element) {
						var len = value.length;
						depositCountryCd
						var typeCd = $(
								"#mainform [name='depositIdentityTypeCd']")
								.val();

						if (!isNull(typeCd) && isNull(value)) {
							return false;
						}
						// 存款人类别
						var typeCd = $("#mainform [name='depositClassCd']")
								.val();
						var countryCds;
						var depositClassCds;
						if (value.indexOf(':') != -1
								&& typeCd.indexOf(':') != -1) {
							countryCds = value.split(':');
							depositClassCds = typeCd.split(':');
							for (var i = 0; i < countryCds.length; i++) {
								if ('05' == depositClassCds[i]
										&& ('CHN' == countryCds[i]
												|| 'HKG' == countryCds[i]
												|| 'MAC' == countryCds[i] || 'TWN' == countryCds[i])) {
									return false;
								}
								if ('04' == depositClassCds[i]
										&& 'HKG' != countryCds[i]
										&& 'MAC' != countryCds[i]
										&& 'TWN' != countryCds[i]) {
									return false;
								}
								if (('01' == depositClassCds[i]
										|| '02' == depositClassCds[i]
										|| '03' == depositClassCds[i] || '06' == depositClassCds[i])
										&& 'CHN' != countryCds[i]) {
									return false;
								}
							}
						} else {
							countryCds = value;
							depositClassCds = typeCd;

							if ('05' == depositClassCds
									&& ('CHN' == countryCds
											|| 'HKG' == countryCds
											|| 'MAC' == countryCds || 'TWN' == countryCds)) {
								return false;
							}
							if ('04' == depositClassCds && 'HKG' != countryCds
									&& 'MAC' != countryCds
									&& 'TWN' != countryCds) {
								return false;
							}
							if (('01' == depositClassCds
									|| '02' == depositClassCds
									|| '03' == depositClassCds || '06' == depositClassCds)
									&& 'CHN' != countryCds) {
								return false;
							}

						}

						return true;
					}, "3位大写字母;证件类型不为空，国籍/地区不为空;多个之间用半角分号隔开");

	//金融机构代码(字母和数字)
	jQuery.validator.addMethod("jrjgdm", function(value, element) {

		var len = value.length;
		return this.optional(element)
				|| ((len == 14) && /^[A-Za-z]\d+$/.test(value));
	}, "只能是字母和数字；编码长度:14位");

	//数字
	jQuery.validator.addMethod("shnum", function(value, element) {
		var len = value.length;
		return this.optional(element) || /^\d+$/.test(value);
	}, "只能是数字");

	//身份证类型相关
	jQuery.validator.addMethod("sfzlx", function(value, element) {

		var dtyp = $("#mainform [name='depositIdentityTypeCd']").val();
		var dcla = $("#mainform [name='depositClassCd']").val();

		return this.optional(element) || dcla.test(value);
	}, "");

	//账户类型校验
	jQuery.validator.addMethod("checkBankAcctTypeCd", function(value, element) {
		// 账户种类
		var bankAcctKindCd = $("#mainform [name='bankAcctKindCd']").val();
		if (('01' == bankAcctKindCd) && isNull(value)) {
			return false;
		}
		if (('02' == bankAcctKindCd || '03' == bankAcctKindCd)
				&& !isNull(value)) {
			return false;
		}

		return true;
	}, "当账户种类为01：借记结算账户时，账户类型不可为空;当账户种类为02贷记结算账户和03非结算账户时，账户类型必须为空。");

	//存款人身份证件种类校验
	jQuery.validator
			.addMethod(
					"checkDepositIdentityTypeCd",
					function(value, element) {
						// 存款人类别
						var dcla = $("#mainform [name='depositClassCd']").val();
						var dtyp = $("#mainform [name='depositIdentityTypeCd']")
								.val();

						// a.存款人类别是01:中国居民时，身份证件种类必须是下列之一01:第一代居民身份证；02:第二代居民身份证；
						// 03:临时身份证；04:中国护照; 05:户口簿；06:村民委员证明；07:学生证；20:其他；	

						// b.存款人类别是02:军人时，身份证件种类必须是下列之一08:军官证；09:离休干部荣誉证；10:军官退休证；
						// 11:文职干部退休证；12:军事学员证；14:士兵证; 20:其他；

						// c.存款人类别是03:武警时，身份证件种类必须是下列之一09:离休干部荣誉证；10:军官退休证；
						// 11:文职干部退休证；12:军事学员证；13:武警证, 14:士兵证；20:其他；

						// d.存款人类别是04:香港、澳门、台湾地区居民时，身份证件种类必须是15:港澳居民来往内地通行证；
						// 16:台湾居民来往大陆通行证通行证；20:其他；

						// e.存款人类别是05:外国公民时，身份证件种类必须是下列之一17:外国人永久居留证；18:边民出入境通行证；
						// 19:外国护照；20:其他。

						// f．存款人类别是06：定居国外的中国公民时，身份证件种类必须是04：中国护照。
						if (('01' == dcla)
								&& !('01' == dtyp || '02' == dtyp
										|| '03' == dtyp || '04' == dtyp
										|| '05' == dtyp || '06' == dtyp
										|| '07' == dtyp || '20' == dtyp)) {
							return false;
						} else if (('02' == dcla)
								&& !('08' == dtyp || '09' == dtyp
										|| '10' == dtyp || '11' == dtyp
										|| '12' == dtyp || '13' == dtyp
										|| '14' == dtyp || '20' == dtyp)) {
							return false;
						} else if (('03' == dcla)
								&& !('09' == dtyp || '10' == dtyp
										|| '11' == dtyp || '12' == dtyp
										|| '13' == dtyp || '14' == dtyp || '20' == dtyp)) {
							return false;
						} else if (('04' == dcla)
								&& !('15' == dtyp || '16' == dtyp || '20' == dtyp)) {
							return false;
						} else if (('05' == dcla)
								&& !('17' == dtyp || '18' == dtyp
										|| '19' == dtyp || '20' == dtyp)) {
							return false;
						} else if (('06' == dcla) && !'04' == dtyp) {
							return false;
						}

						return true;
					}, "证件类型和存款人类别不匹配");

			//卡到期日校验
			jQuery.validator.addMethod("checkCardDueDt", function(value,
					element) {
				// 如果不为空，则必须大于等于开户日期。
				// 当存款人身份证件种类为03临时身份证时，如果卡号不为空，则该卡号对应的卡到期日不能为空，具体日期由开户银行确定。
				// 当账户种类为02贷记结算账户时，如果卡号不为空，则该卡号对应的卡到期日不能为空。卡到期日应晚于或等于开户日期。
				// 身份证件类型
				var identityTypeCd = $(
						"#mainform [name='depositIdentityTypeCd']").val();
				// 账户种类
				var bankAcctKindCd = $("#mainform [name='bankAcctKindCd']")
						.val();
				// 卡号
				var cardCd = $("#mainform [name='cardCd']").val();
				if ('03' == identityTypeCd || '02' == bankAcctKindCd) {
					if (!isNull(cardCd) && isNull(value)) {
						return false;
					}
					if (!isNull(cardCd) && !isNull(value)) {
						var cardCds = cardCd.split(':');
						var cardDueDts = value.split(':');
						for (var i = 0; i < cardCds.length; i++) {
							if (!isNull(cardCds[i]) && isNull(cardDueDts[i])) {
								return false;
							}
						}
					}
				}
				return true;
			}, "证件种类为03临时身份证或账户种类为02贷记结算账户时，卡号不为空，卡到期日不能为空"),

			//账户介质校验
			jQuery.validator.addMethod("checkBankAcctMedia", function(value,
					element) {
				// 卡号
				var cardCd = $("#mainform [name='cardCd']").val();
				if (!isNull(cardCd) && isNull(value)) {
					return false;
				}
				if (!isNull(cardCd) && !isNull(value)) {
					var cardCds = cardCd.split(':');
					var acctMedias = value.split(':');
					for (var i = 0; i < cardCds.length; i++) {
						if (!isNull(cardCds[i]) && isNull(acctMedias[i])) {
							return false;
						}
					}
				}
				return true;
			}, "卡号不为空，则账户介质不可为空"),

			//卡状态校验
			jQuery.validator.addMethod("checkCardStatCd", function(value,
					element) {
				// 卡号
				var cardCd = $("#mainform [name='cardCd']").val();
				if (!isNull(cardCd) && isNull(value)) {
					return false;
				}
				if (!isNull(cardCd) && !isNull(value)) {
					var cardCds = cardCd.split(':');
					var cardStatCds = value.split(':');
					for (var i = 0; i < cardCds.length; i++) {
						if (!isNull(cardCds[i]) && isNull(cardStatCds[i])) {
							return false;
						}
					}
				}
				return true;
			}, "卡号不为空，则卡状态不可为空"),

			//绑定I类账户账号校验
			jQuery.validator
					.addMethod(
							"checkbindingiAcctNum",
							function(value, element) {
								// 当信息类型为01：开户时，如账户类型为02：II类或03：III类，如果开户渠道为01或04时，可以为空；
								// 如果开户渠道为01或04以外时，不可为空；当账户类型为01：I类或空时，必须为空。当信息类型为02：变更或03：销户时，此字段可为空。
								// 信息类型
								var infoTypeCd = $(
										"#mainform [name='infoTypeCd']").val();
								// 账户类型
								var bankAcctTypeCd = $(
										"#mainform [name='bankAcctTypeCd']")
										.val();
								// 开户渠道
								var openChannalCd = $(
										"#mainform [name='openChannalCd']")
										.val();

								if ('01' == infoTypeCd
										&& ('02' == bankAcctTypeCd || '03' == bankAcctTypeCd)
										&& !('01' == openChannalCd || '04' == openChannalCd)
										&& isNull(value)) {
									return false;
								}
								if (('01' == bankAcctTypeCd || isNull(bankAcctTypeCd))
										&& !isNull(value)) {
									return false;
								}
								return true;
							},
							"账户类型为01：I类或空时，必须为空;信息类型为01：开户时，如账户类型为02：II类或03：III类，如果开户渠道为02或03时，可以为空"),

			//绑定I类账户开户银行金融机构编码校验
			jQuery.validator
					.addMethod(
							"checkbindingIFinOrgCd",
							function(value, element) {
								// 当信息类型为01：开户时，如账户类型为02：II类或03：III类，如果开户渠道为01或04时，可以为空； 如果开户渠道为01或04以外时，不可为空；当账户类型为01：I类或空时，必须为空。
								// 当信息类型为02：变更或03：销户时，此字段可为空。
								// 信息类型
								var infoTypeCd = $(
										"#mainform [name='infoTypeCd']").val();
								// 账户类型
								var bankAcctTypeCd = $(
										"#mainform [name='bankAcctTypeCd']")
										.val();
								// 开户渠道
								var openChannalCd = $(
										"#mainform [name='openChannalCd']")
										.val();

								if ('01' == infoTypeCd
										&& ('02' == bankAcctTypeCd || '03' == bankAcctTypeCd)
										&& !('01' == openChannalCd || '04' == openChannalCd)
										&& isNull(value)) {
									return false;
								}
								if (('01' == bankAcctTypeCd || isNull(bankAcctTypeCd))
										&& !isNull(value)) {
									return false;
								}
								return true;
							},
							"账户类型为01：I类或空时，必须为空;信息类型为01：开户时，如账户类型为02：II类或03：III类，如果开户渠道为02或03时，可以为空"),

			// 开户日期校验 checkDateRanger
			jQuery.validator.addMethod("checkDateRanger", function(value,
					element) {
				var today = new Date();
				var yyyy = today.getFullYear();
				var mm = today.getMonth() + 1;
				var dd = today.getDate();
				var date = '' + yyyy + mm + dd;
				if (value > date) {
					return false;
				}
				return true;

			}, "不可为空;格式为yyyyMMdd,并且不能大于系统时间"),

			// 销户日期校验 checkExpireDt
			jQuery.validator.addMethod("checkExpireDt",
					function(value, element) {
						// 如果不为空，则必须大于等于开户日期，
						// 不可为空情形：当账户状态为02:销户时，销户日期不可为空；
						// 可为空情形：当账户状态为01正常或03未激活时，销户日期必须为空。
						// 开户日期
						var openDt = $("#mainform [name='openDt']").val();
						// 账户状态
						var acctStatCd = $("#mainform [name='acctStatCd']")
								.val();

						if (!isNull(value) && value < openDt) {

							return false;

						}
						if ('02' == acctStatCd && isNull(value)) {

							return false;
						}
						if (('01' == acctStatCd || '03' == acctStatCd)
								&& !isNull(value)) {

							return false;
						}

						return true;

					}, "1.格式为yyyyMMdd,并且不能大于系统时间 2.账户状态销户时，不能为空"),

			// 账户状态校验 
			jQuery.validator.addMethod("checkAcctStatCd", function(value,
					element) {
				// 信息类型  03 销户  账户状态 02 销户
				var infoTypeCd = $("#mainform [name='infoTypeCd']").val();
				var acctStatCd = $("#mainform [name='acctStatCd']").val();

				if ('03' == infoTypeCd && '02' != acctStatCd) {
					return false;
				}
				if (('01' == infoTypeCd || '02' == infoTypeCd)
						&& '02' == acctStatCd) {
					return false;
				}
				return true;

			}, "信息类型为01:开立或02:变更时，账户状态不能为02:销户;当信息类型为03:销户时，账户状态只能为02:销户"),

			// 无法核实原因校验 
			jQuery.validator.addMethod("checkNoAuditResultDesc",
					function(value, element) {
						// 核实结果
						var auditResultCd = $(
								"#mainform [name='auditResultCd']").val();

						if ('05' == auditResultCd && isNull(value)) {
							return false;
						}
						if ('05' != auditResultCd && !isNull(value)) {
							return false;
						}
						return true;

					}, "当核实结果为05:无法核实时，无法核实原因为必填，否则该字段应为空"),

			// 处置方法校验 
			jQuery.validator
					.addMethod(
							"checkDisposeMode",
							function(value, element) {
								// 核实结果
								var auditResultCd = $(
										"#mainform [name='auditResultCd']")
										.val();

								if ('02' == auditResultCd && !isNull(value)) {
									return false;
								}
								if (('01' == auditResultCd
										|| '03' == auditResultCd
										|| '04' == auditResultCd || '05' == auditResultCd)
										&& isNull(value)) {
									return false;
								}
								return true;

							},
							"当“核实结果”为01、03、04、05时，处置方法必填;当核实结果为02：真实，处置方式必须为空"),

			$(function() {
				initForm();
				initFormData();
				// checkUnion();
			});
	var selection = [ {
		text : "未作处理",
		id : '01'
	}, {
		text : "报送当地人民银行",
		id : '02'
	}, {
		text : "报送反洗钱监测中心",
		id : '03'
	}, {
		text : "报送当地公安机关",
		id : '04'
	}, {
		text : "中止交易",
		id : '05'
	}, {
		text : "关闭网银",
		id : '06'
	}, {
		text : '关闭网银',
		id : "06"
	}, {
		text : '关闭手机电话银行',
		id : "07"
	}, {
		text : '关闭ATM取现',
		id : "08"
	}, {
		text : '关闭ATM转账',
		id : "09"
	}, {
		text : '其他',
		id : "10"
	} ];
	var openSelect = [ {
		text : '网银',
		id : "01"
	}, {
		text : '手机银行',
		id : "02"
	}, {
		text : 'ATM转账或取现',
		id : "03"
	}, {
		text : 'POS',
		id : "04"
	}, {
		text : '其他',
		id : "05"
	} ]
	//字段
	function initForm() {
		$("#mainform").ligerForm({
			inputWidth : 150,
			labelWidth : 170,
			space : 20,
			fields : [ {
				group : "业务信息",
				groupicon : groupicon
			}, {
				display : "存款人姓名",//1
				name : "depositName",
				newline : false,
				type : "text",
				validate : {
					required : true,
					maxlength : 128,
					checkName : true
				}
			}, {
				display : '数据日期',
				name : 'statisticsDt',
				newline : false,
				type : 'text',
				cssClass : "field",
				options : {
					format : 'yyyy-MM-dd'
				},
				validate : {
					required : false,
					maxlength : 10
				}
			}, {
				display : "存款人身份证件种类",//_2
				name : "depositIdentityTypeCd",
				newline : false,
				type : "select",
				options : {
					data : [// { text : '-请选择-', id : ""}, 
					{
						text : '第一代居民身份证',
						id : "01"
					}, {
						text : '第二代居民身份证',
						id : "02"
					}, {
						text : '临时身份证',
						id : "03"
					}, {
						text : '中国护照',
						id : "04"
					}, {
						text : '户口簿',
						id : "05"
					}, {
						text : '村民委员会证明',
						id : "06"
					}, {
						text : '学生证',
						id : "07"
					}, {
						text : '军官证',
						id : "08"
					}, {
						text : '离休干部荣誉证',
						id : "09"
					}, {
						text : '军官退休证',
						id : "10"
					}, {
						text : '文职干部退休证',
						id : "11"
					}, {
						text : '军事学员证',
						id : "12"
					}, {
						text : '武警证',
						id : "13"
					}, {
						text : '士兵证',
						id : "14"
					}, {
						text : '港澳居民来往内地通行证',
						id : "15"
					}, {
						text : '台湾居民来往大陆通行证',
						id : "16"
					}, {
						text : '外国人永久居留证',
						id : "17"
					}, {
						text : '边民出入境通行证',
						id : "18"
					}, {
						text : '外国护照',
						id : "19"
					}, {
						text : '其它',
						id : "20"
					}, {
						text : '港澳居民居住证',
						id : "21"
					}, {
						text : '台湾居民居住证',
						id : "22"
					} ],
					cancelable : true
				},
				validate : {
					required : false,
					maxlength : 20,
					checkDepositIdentityTypeCd : true
				}
			}, {
				display : "存款人身份证件号码",//_3
				name : "depositIdentityNum",
				newline : false,
				type : "text",
				validate : {
					required : false,
					maxlength : 30,
					ckrlx : true
				}
			}, {
				display : "身份证件到期日",//_4
				name : "identityDueDt",
				newline : false,
				type : 'text',
				//options : { format : "yyyy-MM-dd"},
				validate : {
					required : false,
					maxlength : 40
				//checkIdentityDueDt : true
				}
			}, {
				display : "发证机关所在地的地区代码",//_5
				name : "issueOrgAreaCd",
				newline : false,
				type : "text",
				validate : {
					required : false,
					maxlength : 4,
					fzqdm : true
				}
			}, {
				display : "存款人类别",//6
				name : "depositClassCd",
				newline : false,
				type : "select",
				options : {
					data : [ //{ text : '-请选择-', id : ""},
					{
						text : '中国居民',
						id : "01"
					}, {
						text : '军人',
						id : "02"
					}, {
						text : '武警',
						id : "03"
					}, {
						text : '香港、澳门、台湾地区居民',
						id : "04"
					}, {
						text : '外国公民',
						id : "05"
					}, {
						text : '定居国外的中国公民',
						id : "06"
					} ],
					cancelable : true
				},
				validate : {
					required : false,
					maxlength : 22,
					checkDepositClassCd : true
				}
			}, {
				display : "存款人国籍(地区)",//7
				name : "depositCountryCd",
				newline : false,
				type : "text",
				validate : {
					required : false,
					maxlength : 3,
					checkUppercase1 : true,
					checkDepositCountryCd : true
				}
			}, {
				display : "存款人性别",//8
				name : "depositSexCd",
				newline : false,
				type : "select",
				options : {
					data : [ //{ text : '-请选择-', id : ""}, 
					{
						text : '男性',
						id : "01"
					}, {
						text : '女性',
						id : "02"
					}, {
						text : '未说明的性别',
						id : "09"
					} ],
					cancelable : true
				},
				validate : {
					required : true,
					maxlength : 12
				}
			}, {
				display : "存款人邮编",//9
				name : "depositPostCd",
				newline : false,
				type : "text",
				validate : {
					required : false,
					maxlength : 30,
					shnum : true
				}
			}, {
				display : "存款人地址",//10
				name : "depositAddr",
				newline : false,
				type : "text",
				validate : {
					required : false,
					maxlength : 90
				}
			}, {
				display : "存款人电话",//11
				name : "depositTel",
				newline : false,
				type : "text",
				validate : {
					required : false,
					maxlength : 15,
					shnum : true
				}
			}, {
				display : "代理人名称",//12
				name : "agentName",
				newline : false,
				type : "text",
				validate : {
					required : false,
					maxlength : 22,
					checkName : true
				}
			}, {
				display : "代理人身份证件种类",//13
				name : "agentIdentityTypeCd",
				newline : false,
				type : "select",
				options : {
					data : [ //{ text : '-请选择-', id : ""}, 
					{
						text : '第一代居民身份证',
						id : "01"
					}, {
						text : '第二代居民身份证',
						id : "02"
					}, {
						text : '临时身份证',
						id : "03"
					}, {
						text : '中国护照',
						id : "04"
					}, {
						text : '户口簿',
						id : "05"
					}, {
						text : '村民委员会证明',
						id : "06"
					}, {
						text : '学生证',
						id : "07"
					}, {
						text : '军官证',
						id : "08"
					}, {
						text : '离休干部荣誉证',
						id : "09"
					}, {
						text : '军官退休证',
						id : "10"
					}, {
						text : '文职干部退休证',
						id : "11"
					}, {
						text : '军事学员证',
						id : "12"
					}, {
						text : '武警证',
						id : "13"
					}, {
						text : '士兵证',
						id : "14"
					}, {
						text : '港澳居民来往内地通行证',
						id : "15"
					}, {
						text : '台湾居民来往大陆通行证',
						id : "16"
					}, {
						text : '外国人永久居留证',
						id : "17"
					}, {
						text : '边民出入境通行证',
						id : "18"
					}, {
						text : '外国护照',
						id : "19"
					}, {
						text : '其它',
						id : "20"
					}, {
						text : '港澳居民居住证',
						id : "21"
					}, {
						text : '台湾居民居住证',
						id : "22"
					} ],
					cancelable : true
				},
				validate : {
					required : false,
					maxlength : 20,
					checkAgentIdentityTypeCd : true
				}
			}, {
				display : "代理人身份证件号码",//14
				name : "agentIdentityNum",
				newline : false,
				type : "text",
				validate : {
					required : false,
					maxlength : 30,
					checkAgentIdentityNum : true
				}
			}, {
				display : "代理人国籍",//15
				name : "agentCountryCd",
				newline : false,
				type : "text",
				validate : {
					required : false,
					maxlength : 3,
					checkUppercase1 : true,
					checkAgentCountryCd : false
				}
			}, {
				display : "代理人电话",//16
				name : "agentTel",
				newline : false,
				type : "text",
				validate : {
					required : false,
					maxlength : 22,
					shnum : true
				}
			}, {
				display : "开户银行金融机构编码",//17
				name : "openBankFinOrgCd",
				newline : false,
				type : "text",
				labelWidth : '170',
				validate : {
					required : true,
					maxlength : 14,
					jrjgdm : true
				}
			}, {
				display : "账号（不可变更）",//18
				name : "bankAcctNum",
				newline : false,
				type : "text",
				validate : {
					required : true,
					maxlength : 32,
					shnum : true
				}
			}, {
				display : "账户种类",//19
				name : "bankAcctKindCd",
				newline : false,
				type : "select",
				options : {
					data : [ //{ text : '-请选择-', id : ""}, 
					{
						text : '借记结算账户',
						id : "01"
					}, {
						text : '贷记结算账户',
						id : "02"
					}, {
						text : '非结算账户',
						id : "03"
					}, {
						text : '其他',
						id : "04"
					} ],
					cancelable : true
				},
				onselected : function(value, text) {
					if (value == '02' && orgNo != '3000') {
						alert("您不能添加或修改贷记结算账户数据!");
					}
					if (value == '01' && orgNo == '3000') {
						alert("您不能添加或修改借记结算账户数据!");
					}
				},
				validate : {
					required : true,
					maxlength : 12
				}
			}, {
				display : "介质号",//20
				name : "cardCd",
				newline : false,
				type : "text",
				validate : {
					required : false,
					maxlength : 464,
					shnum : true
				}
			}, {
				display : "介质到期日",//21
				name : "cardDueDt",
				newline : false,
				type : 'text',
				cssClass : "field",
				//options:{ format:'yyyy-MM-dd' },
				validate : {
					required : false,
					maxlength : 134
				//checkCardDueDt : true
				}
			}, {
				display : "账户介质",//22
				name : "bankAcctMedia",
				newline : false,
				type : "select",
				options : {
					data : [ //{ text : '-请选择-', id : ""},
					{
						text : '银行卡',
						id : "01"
					}, {
						text : '存折',
						id : "02"
					}, {
						text : '存单',
						id : "03"
					}, {
						text : '手机',
						id : "04"
					}, {
						text : '无介质',
						id : "05"
					}, {
						text : '其他',
						id : "06"
					} ],
					cancelable : true
				},
				validate : {
					required : false,
					maxlength : 22,
					checkBankAcctMedia : true
				}
			}, {
				display : "介质注销日期",//23
				name : "expireCardDt",
				newline : false,
				type : 'date',
				cssClass : "field",
				options : {
					format : 'yyyy-MM-dd'
				},
				validate : {
					required : false,
					maxlength : 10
				}
			}, {
				display : "介质状态",//24
				name : "cardStatCd",
				newline : false,
				type : "select",
				options : {
					data : [ //{ text : '-请选择-', id : ""},
					{
						text : '正常',
						id : "01"
					}, {
						text : '销卡',
						id : "03"
					} ],
					cancelable : true
				},
				validate : {
					required : false,
					maxlength : 22,
					checkCardStatCd : true
				}
			}, {
				display : "账户类型",//25
				name : "bankAcctTypeCd",
				newline : false,
				type : "select",
				options : {
					data : [ //{ text : '-请选择-', id : ""},
					{
						text : 'Ⅰ类',
						id : "01"
					}, {
						text : 'Ⅱ类',
						id : "02"
					}, {
						text : 'Ⅲ类',
						id : "03"
					} ],
					cancelable : true
				},
				validate : {
					required : false,
					maxlength : 10,
					checkBankAcctTypeCd : true
				}
			}, {
				display : "Ⅱ、Ⅲ类户绑定账户账号",//26
				name : "bindingiAcctNum",
				newline : false,
				type : "text",
				validate : {
					required : false,
					maxlength : 32,
					checkbindingiAcctNum : true
				}
			}, {
				display : "II、III类户绑定账户开户银行金融机构编码",//27
				name : "bindingIFinOrgCd",
				newline : false,
				type : "text",
				validate : {
					required : false,
					maxlength : 22,
					checkbindingIFinOrgCd : true
				}
			}, {
				display : "开户日期",//28
				name : "openDt",
				newline : false,
				type : 'date',
				cssClass : "field",
				options : {
					format : 'yyyy-MM-dd'
				},
				validate : {
					required : true,
					maxlength : 10,
					checkDateRanger : true
				}
			}, {
				display : "销户日期(不可变)",//29
				name : "expireDt",
				newline : false,
				type : 'date',
				cssClass : "field",
				options : {
					format : 'yyyyMMdd'
				},
				validate : {
					required : false,
					maxlength : 10,
					checkExpireDt : true
				}
			}, {
				display : "账户状态",//30
				name : "acctStatCd",
				newline : false,
				type : "select",
				options : {
					data : [ //{ text : '-请选择-', id : ""},
					{
						text : '正常',
						id : "01"
					}, {
						text : '销户',
						id : "02"
					}, {
						text : '未激活',
						id : "03"
					} ],
					cancelable : true
				},
				validate : {
					required : true,
					maxlength : 22,
					checkAcctStatCd : true
				}
			}, {
				display : "币种",//31
				name : "currencyCd",
				newline : false,
				type : "select",
				options : {
					data : [ {
						text : '人民币',
						id : '01'
					} ]
				},
				validate : {
					required : true,
					maxlength : 6
				}
			}, {
				display : "是否为军人保障卡",//32
				name : "armySecurityCard",
				newline : false,
				type : "select",
				options : {
					data : [ //{ text : '-请选择-', id : ""},
					{
						text : '是',
						id : "01"
					}, {
						text : '不是',
						id : "02"
					} ],
					cancelable : true
				},
				validate : {
					required : true,
					maxlength : 20
				}
			}, {
				display : "是否为社会保障卡",//33
				name : "societySecurityCard",
				newline : false,
				type : "select",
				options : {
					data : [ //{ text : '-请选择-', id : ""},
					{
						text : '是',
						id : "01"
					}, {
						text : '不是',
						id : "02"
					} ],
					cancelable : true
				},
				validate : {
					required : true,
					maxlength : 20
				}
			}, {
				display : "核实结果",//34
				name : "auditResultCd",
				newline : false,
				type : "select",
				options : {
					data : [ //{ text : '-请选择-', id : ""},
					{
						text : '未核实',
						id : "01"
					}, {
						text : '真实',
						id : "02"
					}, {
						text : '假名',
						id : "03"
					}, {
						text : '匿名',
						id : "04"
					}, {
						text : '无法核实',
						id : "05"
					} ],
					cancelable : true
				},
				validate : {
					required : true,
					maxlength : 22
				}
			}, {
				display : "无法核实原因",//35
				name : "noAuditResultDesc",
				newline : false,
				type : "select",
				options : {
					data : [ //{ text : '-请选择-', id : ""},
					{
						text : '无法联系存款人',
						id : "01"
					}, {
						text : '存款人提供证明文件有疑义待进一步核实',
						id : "02"
					}, {
						text : '存款人在规定时间内无法提供相关证明',
						id : "03"
					}, {
						text : '存款人拒绝提供证明',
						id : "04"
					} ],
					cancelable : true
				},
				validate : {
					required : false,
					maxlength : 22,
					checkNoAuditResultDesc : true
				}
			}, {
				display : "处置方法",
				name : 'disposeMode',
				newline : document.body.scrollWidth < 1034,
				newline : false,
				type : 'select',
				options : {
					data : [ //{ text : '-请选择-', id : ""},
					{
						text : '未作处理',
						id : "01"
					}, {
						text : '报送当地人民银行',
						id : "02"
					}, {
						text : '报送反洗钱监测中心',
						id : "03"
					}, {
						text : '报送当地公安机关',
						id : "04"
					}, {
						text : '中止交易',
						id : "05"
					}, {
						text : '关闭网银',
						id : "06"
					}, {
						text : '关闭手机电话银行',
						id : "07"
					}, {
						text : '关闭ATM取现',
						id : "08"
					}, {
						text : '关闭ATM转账',
						id : "09"
					}, {
						text : '其他',
						id : "10"
					} ],
					cancelable : true
				}
			}, {
				display : "信息类型",//37
				name : "infoTypeCd",
				newline : false,
				type : "select",
				options : {
					data : [//{ text : '-请选择-', id : ""}, 
					{
						text : '开户',
						id : "01"
					}, {
						text : '变更',
						id : "02"
					}, {
						text : '销户',
						id : "03"
					} ],
					cancelable : true
				},
				validate : {
					required : true,
					maxlength : 6
				}
			}, {
				display : "开户渠道",//38
				name : "openChannalCd",
				newline : false,
				type : "select",
				options : {
					data : [ {
						text : '柜面',
						id : "01"
					}, {
						text : '互联网网页',
						id : "02"
					}, {
						text : 'APP客户端',
						id : "03"
					}, {
						text : '自助机具（人工参与审核）',
						id : "04"
					}, {
						text : '自助机具（无人工审核）',
						id : "05"
					}, {
						text : '第三方渠道',
						id : "06"
					}, {
						text : '其他',
						id : "07"
					} ],
					cancelable : true
				},
				validate : {
					required : true,
					maxlength : 22
				}
			}, {
				display : "备注",//39
				name : "remark",
				newline : true,
				type : "textarea",
				validate : {
					required : false,
				}
			}, {
				display : "打回类型",
				name : "reBackFlag",
				newline : false,
				type : "textarea",
				/*options : {  data : [//1 金电打回 2人行打回
				                     
				                     { text : '金电打回', id : "1"}, 
				                     { text : '人行打回', id : "2"}],
				                     cancelable  : true},*/
				validate : {
					required : false,
					maxlength : 12
				}
			},
			/*{
				display : "打回原因",//39
				name : "errorReason",  
				inputWidth: '120',
				
				type : "textarea",
				validate : {
				    required : false,
				}
			},*/
			{
				display : "开通的非柜面交易渠道",
				name : "openUntradeChannal",
				newline : false,
				type : "select",
				/*options : { data : [// { text : '-请选择-', id : ""},
				                     { text : '网银', id : "01"}, 
									 { text : '手机银行', id : "02"}, 
									 { text : 'ATM转账或取现', id : "03"}, 
									 { text : 'POS', id : "04"}, 
				                     { text : '其他', id : "05"}
									 
									 ],
				                     cancelable  : true},*/
				options : {
					split : ";",
					isShowCheckBox : true,
					isMultiSelect : true,
					valueFieldID : 'openUntradeChannal',
					data : $.extend({}, openSelect),
					onBeforeOpen : function() {
						liger.get('openUntradeChannal').setData(openSelect);
					}

				},
				validate : {
					required : false,
					maxlength : 12
				}
			}, {
				display : "是否为联名账户",//41
				name : "jointAccount",
				newline : false,
				type : "select",
				options : {
					data : [// { text : '-请选择-', id : ""},
					{
						text : '联名账户',
						id : "01"
					}, {
						text : '主、副卡账户',
						id : "02"
					} ],
					cancelable : true
				},
				validate : {
					required : false,
					maxlength : 12
				}
			}, {
				display : "开户地地区代码",//42
				name : "openAreaCode",
				newline : false,
				type : "text",
				validate : {
					required : false,
					maxlength : 22
				}
			}, {
				display : "预留字段4",//43
				name : "column4",
				newline : false,
				type : "text",
				validate : {
					required : false
				}
			}, {
				display : "预留字段5",//44
				name : "column5",
				newline : false,
				type : "text",
				validate : {
					required : false,
					maxlength : 22
				}
			}, {
				display : "校验状态",
				name : "validateFlag",
				newline : false,
				type : "select",
				options : {
					data : [// { text : '-请选择-', id : ""},
					{
						text : '未校验',
						id : "0"
					}, {
						text : '校验未通过',
						id : "1"
					}, {
						text : '校验通过',
						id : "2"
					} ],
					cancelable : true
				},
				validate : {
					required : false,
					maxlength : 12
				}
			}, {
				display : "审核状态",
				name : "auditStatus",
				newline : false,
				type : "select",
				options : {
					data : [// { text : '-请选择-', id : ""},

					{
						text : '待处理',
						id : "0"
					}, {
						text : '待复核',
						id : "1"
					}, {
						text : '待审核',
						id : "2"
					}, {
						text : '已审核',
						id : "3"
					}, {
						text : '复核不通过',
						id : "4"
					}, {
						text : '复核通过',
						id : "6"
					}, {
						text : '审核不通过',
						id : "5"
					} ],
					cancelable : true
				},
				validate : {
					required : false,
					maxlength : 12
				}
			}, {
				display : "机构号",//49
				name : "orgNo",
				newline : false,
				type : "text",
				validate : {
					required : false,
					maxlength : 22
				}
			}, {
				display : "录入人或修改人",//50
				name : "inputer",
				newline : false,
				type : "text",
				validate : {
					required : false,
					maxlength : 22
				}
			}, {
				display : "复核员",//51
				name : "reviewer",
				newline : false,
				type : "text",
				validate : {
					required : false,
					maxlength : 22
				}
			}, {
				display : "审核员",//52
				name : "assessor",
				newline : false,
				type : "text",
				validate : {
					required : false,
					maxlength : 22
				}
			}
			]
		});

		// 设置隐藏
		var aa = [ 'depositName', 'statisticsDt', 'depositIdentityTypeCd',
				'depositIdentityNum', 'identityDueDt', 'issueOrgAreaCd',
				'depositClassCd', 'depositCountryCd', 'depositSexCd',
				'depositPostCd', 'depositAddr', 'depositTel', 'agentName',
				'agentIdentityTypeCd', 'agentIdentityNum', 'agentCountryCd',
				'agentTel', 'openBankFinOrgCd', 'bankAcctNum',
				'bankAcctKindCd', 'cardCd', 'cardDueDt', 'bankAcctMedia',
				'expireCardDt', 'cardStatCd', 'bankAcctTypeCd',
				'bindingiAcctNum', 'bindingIFinOrgCd', 'openDt', 'expireDt',
				'acctStatCd', 'currencyCd', 'armySecurityCard',
				'societySecurityCard', 'auditResultCd', 'noAuditResultDesc',
				'disposeMode', 'infoTypeCd', 'openChannalCd', 'remark',
				'openUntradeChannal', 'jointAccount', 'openAreaCode',
				'column4', 'column5', 'validateFlag', 'auditStatus',
				'reBackFlag', 'orgNo', 'inputer', 'reviewer', 'assessor' ];// errorReason 
/*  		for (var i = 0; i < aa.length; i++) {
			$("#" + aa[i]).attr("readonly", "true");
			$("#" + aa[i]).css("color", "#666");
		}  */

		/* var bb = ['openDt','expireDt','acctStatCd','currencyCd'];
		for(var j =0;j<bb.length;j++){
			$("#"+bb[j]).attr("disabled", "disabled");
			//$("#"+bb[j]).css("color","#666");
		} */

		jQuery.metadata.setType("attr", "validate");
		BIONE.validate($("#mainform"));

		var buttons = [];

		// 查看按钮
		if (!isNull(type) && type == '1') {
			buttons.push({
				text : '返回',
				onclick : function() {
					BIONE.closeDialog("perFlowDealWin", null);
				}
			});
		}

		// 复核按钮
		if (!isNull(type) && type == '2') {
			buttons.push({
				text : '取消',
				onclick : function() {
					BIONE.closeDialog("perFlowDealWin", null);
				}
			});
			buttons.push({
				text : '复核通过',
				onclick : function() {
					$.ajax({
						async : false,
						type : "post",
						url : '${ctx}/frs/nrtmessage/reviewPersonFlow?&id='
								+ id + "&result=1",
						success : function(res) {
							BIONE.tip('复核成功');
							BIONE.closeDialogAndReloadParent("perFlowDealWin",
									"maingrid", "复核成功");
						},
						error : function(e) {
							BIONE.tip('复核失败');
						}
					});
				}
			});
			buttons.push({
				text : '复核退回',
				onclick : function() {
					$.ajax({
						async : false,
						type : "post",
						url : '${ctx}/frs/nrtmessage/reviewPersonFlow?&id='
								+ id + "&result=0",
						success : function(res) {
							BIONE.tip('复核成功');
							BIONE.closeDialogAndReloadParent("perFlowDealWin",
									"maingrid", "复核成功");
							//BIONE.closeDialog("perFlowDealWin", null);
							//grid.loadData();
						},
						error : function(e) {
							BIONE.tip('复核失败');
						}
					});
				}
			});
		}

		// 审核按钮
		if (!isNull(type) && type == '3') {
			buttons.push({
				text : '取消',
				onclick : function() {
					BIONE.closeDialog("perFlowDealWin", null);
				}
			});
			buttons.push({
				text : '审核通过',
				onclick : function() {
					$.ajax({
						async : false,
						type : "post",
						url : '${ctx}/frs/nrtmessage/assessPersonFlow?&id='
								+ id + "&result=1",
						success : function(res) {
							BIONE.tip('审核成功');
							BIONE.closeDialogAndReloadParent("perFlowDealWin",
									"maingrid", "处理成功");
						},
						error : function(e) {
							BIONE.tip('审核失败');
						}
					});
				}
			});
			buttons.push({
				text : '审核退回',
				onclick : function() {
					$.ajax({
						async : false,
						type : "post",
						url : '${ctx}/frs/nrtmessage/assessPersonFlow?&id='
								+ id + "&result=0",
						success : function(res) {
							BIONE.tip('回退成功');
							BIONE.closeDialogAndReloadParent("perFlowDealWin",
									"maingrid", "处理成功");
						},
						error : function(e) {
							BIONE.tip('回退失败');
						}
					});
				}
			});
		}

		BIONE.addFormButtons(buttons);
	}

	function initFormData() {
		if (id) {
			BIONE
					.loadForm(
							mainform,
							{
								url : "${ctx}/frs/nrtmessage/getPersnoFlowByValues?id=${id}&infoType=${infoType}"
							});
		}
	}

	//保存
	function f_save() {
		BIONE.submitForm($("#mainform"), function() {
			BIONE.closeDialogAndReloadParent("perFlowEidtWin", "maingrid",
					"添加成功");
		}, function() {
			BIONE.closeDialog("perFlowEidtWin", "添加失败");
		});
	}
</script>
</head>
<body>
	<div id="template.center">
		<form name="mainform" method="post" id="mainform"
			action="${ctx}/frs/nrtmessage/savePersonFlow"></form>
	</div>
</body>
</html>