--基线版本20200430******************************************************
--新增指标常量函数公式(yangyf1)
update RPT_IDX_FORMULA_FUNC set FORMULA_DISPLAY = 'C：指标常量函数，使用方式:C(数字) 如：C(100),表示常量100' where FORMULA_ID = 'C';

/*20200421-添加指标类任务类型参数 maojin2*/
insert into BIONE_PARAM_TYPE_INFO (PARAM_TYPE_ID, LOGIC_SYS_NO, PARAM_TYPE_NO, PARAM_TYPE_NAME, UP_NO, REMARK) 
values ('4028884c71713dc50171727433dd0000', 'FRS', 'idxTaskType', '指标类报送任务类型', '0', '指标类报送任务类型');

insert into bione_param_info (PARAM_ID, LOGIC_SYS_NO, PARAM_TYPE_NO, PARAM_VALUE, PARAM_NAME, ORDER_NO, UP_NO, REMARK)
values ('6f8c752ff44e4a93b24c858b2ee4147c', 'FRS', 'idxTaskType', '02', '1104报送', 1, '0', '');

insert into bione_param_info (PARAM_ID, LOGIC_SYS_NO, PARAM_TYPE_NO, PARAM_VALUE, PARAM_NAME, ORDER_NO, UP_NO, REMARK)
values ('004bfbcbea374bb3a195e48d500e79d7', 'FRS', 'idxTaskType', '03', '人行大集中', 2, '0', '');

insert into bione_param_info (PARAM_ID, LOGIC_SYS_NO, PARAM_TYPE_NO, PARAM_VALUE, PARAM_NAME, ORDER_NO, UP_NO, REMARK)
values ('be5bb31c343f4b58a07872df45283377', 'FRS', 'idxTaskType', '05', '利率报备', 3, '0', '');

insert into bione_param_info (PARAM_ID, LOGIC_SYS_NO, PARAM_TYPE_NO, PARAM_VALUE, PARAM_NAME, ORDER_NO, UP_NO, REMARK)
values ('74c542253ea24cb2bd173927390818dc', 'FRS', 'idxTaskType', '06', '支付报送', 4, '0', '');


/*20200421-添加校验简报菜单 maojin2*/
insert into bione_func_info (FUNC_ID, MODULE_ID, FUNC_NAME, UP_ID, ORDER_NO, NAV_ICON, NAV_PATH, FUNC_STS, REMARK)
values ('b4199cf8562443edbee833c013ab94bd', '3b6169aa5dfb4292a18df0740570f2b9', '校验简报', '0', 4, 'icon-analyze', '/frs/validatereport/index', '1', '');

/*20200518-添加联系电话表达式*/
insert into RPT_SYS_VAR_INFO (VAR_ID, VAR_NO, VAR_NM, DEF_TYPE, VAR_TYPE, SOURCE_ID, VAR_VAL, REMARK)
values ('e2ddd456f2f64d19b252c6e2223q4sa4', 'phone_number', '联系电话', '02', '02', '1', 'select   phone_number   from  rpt_user_rel   where  1=1   and  rpt_num  = ''#rptNum#''   and   org_id   =  ''#orgNo#''', '');

/*20200519-添加已驳回状态*/
insert into bione_param_info (PARAM_ID, LOGIC_SYS_NO, PARAM_TYPE_NO, PARAM_VALUE, PARAM_NAME, ORDER_NO, UP_NO, REMARK)
values ('eea902ae4a6548bba9114eae28234234', 'FRS', 'rptSts', '10', '已驳回', 4, '0', '只用于显示');


--基线版本V3.2-20200615******************************************************
--改变授权对象类名
update BIONE_AUTH_OBJ_DEF set BEAN_NAME = 'roleAuthObjImpl' where OBJ_DEF_NO = 'AUTH_OBJ_ROLE';

--规范指标类报送流程状态码值
--（0-未提交，1-待复核，2-待审核，3-流程完成）
update BIONE_PARAM_INFO set PARAM_NAME = '未提交' where PARAM_NAME = '未归档';
update BIONE_PARAM_INFO set PARAM_NAME = '流程完成' where PARAM_NAME = '审核完成';

--基线版本V3.2-20200722******************************************************
/*20200722-新增精准扶贫、资管产品、理财登记、存款保险模块*/
/*模块SQL*/
insert into bione_module_info (MODULE_ID, MODULE_NO, MODULE_NAME, REMARK)
values ('0d8dc01578a040e28359ae1065dc1f23', 'poverty_report', '精准扶贫', '');

insert into bione_module_info (MODULE_ID, MODULE_NO, MODULE_NAME, REMARK)
values ('98c553b4f6cb45b0b40a5fbf5291a3c9', 'asset_report', '资管产品', '');

insert into bione_module_info (MODULE_ID, MODULE_NO, MODULE_NAME, REMARK)
values ('77f9bd9044d74251acf80ee4247a075e', 'finance_report', '理财登记', '');

insert into bione_module_info (MODULE_ID, MODULE_NO, MODULE_NAME, REMARK)
values ('24615d1fa9e44e75a78e1780d3c364e4', 'deposi_report', '存款保险', '');
/*功能菜单SQL*/
insert into bione_func_info (FUNC_ID, MODULE_ID, FUNC_NAME, UP_ID, ORDER_NO, NAV_ICON, NAV_PATH, FUNC_STS, REMARK)
values ('9711463c76514ebdb7a4fc04a3b13870', '0d8dc01578a040e28359ae1065dc1f23', '任务配置', '0', 1, 'icon-menu3', '/frs/rpttsk/publish/busi?orgType=13', '1', '');

insert into bione_func_info (FUNC_ID, MODULE_ID, FUNC_NAME, UP_ID, ORDER_NO, NAV_ICON, NAV_PATH, FUNC_STS, REMARK)
values ('d972053d76484b01945c3d6e0948d82c', '0d8dc01578a040e28359ae1065dc1f23', '报表填报', '0', 2, 'icon-endorse', '/rpt/frs/rptfill/fillRpt?orgType=13', '1', '');

insert into bione_func_info (FUNC_ID, MODULE_ID, FUNC_NAME, UP_ID, ORDER_NO, NAV_ICON, NAV_PATH, FUNC_STS, REMARK)
values ('442a740d84ad47719d1b1798b68f7c37', '0d8dc01578a040e28359ae1065dc1f23', '报表复核', '0', 3, 'icon-process', '/frs/rptsubmit/submit/busiIndex?orgType=13', '1', '');

insert into bione_func_info (FUNC_ID, MODULE_ID, FUNC_NAME, UP_ID, ORDER_NO, NAV_ICON, NAV_PATH, FUNC_STS, REMARK)
values ('c67204983a9545149077ea067026332d', '0d8dc01578a040e28359ae1065dc1f23', '报表审核', '0', 4, 'icon-customer', '/frs/rptsubmit/submit/busiApprIndex?orgType=13', '1', '');

insert into bione_func_info (FUNC_ID, MODULE_ID, FUNC_NAME, UP_ID, ORDER_NO, NAV_ICON, NAV_PATH, FUNC_STS, REMARK)
values ('e551128c850146669257258bb9e809a4', '0d8dc01578a040e28359ae1065dc1f23', '申请解锁', '0', 5, 'icon-password1', '/frs/rptfill/reject/busi?orgType=13', '1', '');

insert into bione_func_info (FUNC_ID, MODULE_ID, FUNC_NAME, UP_ID, ORDER_NO, NAV_ICON, NAV_PATH, FUNC_STS, REMARK)
values ('f860a63d16fc44c49a135c6e3c863de7', '0d8dc01578a040e28359ae1065dc1f23', '查看申请', '0', 6, 'icon-search-02', '/frs/rptfill/reject/rejectView?orgType=13', '1', '');

insert into bione_func_info (FUNC_ID, MODULE_ID, FUNC_NAME, UP_ID, ORDER_NO, NAV_ICON, NAV_PATH, FUNC_STS, REMARK)
values ('48819eecc6c94d5a9d64df76cd0e63ba', '0d8dc01578a040e28359ae1065dc1f23', '审批解锁', '0', 7, 'icon-clear', '/frs/rptfill/reject/approve?orgType=13', '1', '');

insert into bione_func_info (FUNC_ID, MODULE_ID, FUNC_NAME, UP_ID, ORDER_NO, NAV_ICON, NAV_PATH, FUNC_STS, REMARK)
values ('370afd827e7b4dd88b5a400fb13776a3', '0d8dc01578a040e28359ae1065dc1f23', '强制解锁', '0', 8, 'icon-clear', '/frs/rptfill/reject/force?orgType=13', '1', '');

insert into bione_func_info (FUNC_ID, MODULE_ID, FUNC_NAME, UP_ID, ORDER_NO, NAV_ICON, NAV_PATH, FUNC_STS, REMARK)
values ('2aa347459d524f4ca2741082f9437daa', '0d8dc01578a040e28359ae1065dc1f23', '报表导出', '0', 9, 'icon-screen', '/rpt/frs/rptexport/index?orgType=13', '1', '');

insert into bione_func_info (FUNC_ID, MODULE_ID, FUNC_NAME, UP_ID, ORDER_NO, NAV_ICON, NAV_PATH, FUNC_STS, REMARK)
values ('2df4ed8e2d1544799b5ab3fa9c29b146', '0d8dc01578a040e28359ae1065dc1f23', '任务管理', '0', 10, 'icon-menu3', '/bione/frs/task/index?orgType=13', '1', '');

insert into bione_func_info (FUNC_ID, MODULE_ID, FUNC_NAME, UP_ID, ORDER_NO, NAV_ICON, NAV_PATH, FUNC_STS, REMARK)
values ('e823c2c5b364375e9e4d058af2408b9e', '24615d1fa9e44e75a78e1780d3c364e4', '报表审核', '0', 4, 'icon-customer', '/frs/rptsubmit/submit/busiApprIndex?orgType=10', '1', '');

insert into bione_func_info (FUNC_ID, MODULE_ID, FUNC_NAME, UP_ID, ORDER_NO, NAV_ICON, NAV_PATH, FUNC_STS, REMARK)
values ('79a9deedd0633470b4eba941fcccf577', '24615d1fa9e44e75a78e1780d3c364e4', '任务配置', '0', 1, 'icon-menu3', '/frs/rpttsk/publish/busi?orgType=10', '1', '');

insert into bione_func_info (FUNC_ID, MODULE_ID, FUNC_NAME, UP_ID, ORDER_NO, NAV_ICON, NAV_PATH, FUNC_STS, REMARK)
values ('60b96a5aef1c3ffebab98018ce811280', '24615d1fa9e44e75a78e1780d3c364e4', '报表填报', '0', 2, 'icon-endorse', '/rpt/frs/rptfill/fillRpt?orgType=10', '1', '');

insert into bione_func_info (FUNC_ID, MODULE_ID, FUNC_NAME, UP_ID, ORDER_NO, NAV_ICON, NAV_PATH, FUNC_STS, REMARK)
values ('369899cdcf77347d93fdc11593fe948a', '24615d1fa9e44e75a78e1780d3c364e4', '报表复核', '0', 3, 'icon-process', '/frs/rptsubmit/submit/busiIndex?orgType=10', '1', '');

insert into bione_func_info (FUNC_ID, MODULE_ID, FUNC_NAME, UP_ID, ORDER_NO, NAV_ICON, NAV_PATH, FUNC_STS, REMARK)
values ('db8481183384328eb015ce7d0dfcb29b', '24615d1fa9e44e75a78e1780d3c364e4', '申请解锁', '0', 5, 'icon-password1', '/frs/rptfill/reject/busi?orgType=10', '1', '');

insert into bione_func_info (FUNC_ID, MODULE_ID, FUNC_NAME, UP_ID, ORDER_NO, NAV_ICON, NAV_PATH, FUNC_STS, REMARK)
values ('3f06b27b8e7d393799b3e473fa1005aa', '24615d1fa9e44e75a78e1780d3c364e4', '查看申请', '0', 6, 'icon-search-02', '/frs/rptfill/reject/rejectView?orgType=10', '1', '');

insert into bione_func_info (FUNC_ID, MODULE_ID, FUNC_NAME, UP_ID, ORDER_NO, NAV_ICON, NAV_PATH, FUNC_STS, REMARK)
values ('62e79532120e3884aaaef7b0ece66f7c', '24615d1fa9e44e75a78e1780d3c364e4', '审批解锁', '0', 7, 'icon-clear', '/frs/rptfill/reject/approve?orgType=10', '1', '');

insert into bione_func_info (FUNC_ID, MODULE_ID, FUNC_NAME, UP_ID, ORDER_NO, NAV_ICON, NAV_PATH, FUNC_STS, REMARK)
values ('bbe31434f30a3f029502dedac4f60058', '24615d1fa9e44e75a78e1780d3c364e4', '强制解锁', '0', 8, 'icon-clear', '/frs/rptfill/reject/force?orgType=10', '1', '');

insert into bione_func_info (FUNC_ID, MODULE_ID, FUNC_NAME, UP_ID, ORDER_NO, NAV_ICON, NAV_PATH, FUNC_STS, REMARK)
values ('4f199cabe3810fe2aa16d1b636170c8b', '24615d1fa9e44e75a78e1780d3c364e4', '报表导出', '0', 9, 'icon-screen', '/rpt/frs/rptexport/index?orgType=10', '1', '');

insert into bione_func_info (FUNC_ID, MODULE_ID, FUNC_NAME, UP_ID, ORDER_NO, NAV_ICON, NAV_PATH, FUNC_STS, REMARK)
values ('33bb8bcfe3663fc8ad63c1ba45b164a5', '24615d1fa9e44e75a78e1780d3c364e4', '任务管理', '0', 10, 'icon-menu3', '/bione/frs/task/index?orgType=10', '1', '');

insert into bione_func_info (FUNC_ID, MODULE_ID, FUNC_NAME, UP_ID, ORDER_NO, NAV_ICON, NAV_PATH, FUNC_STS, REMARK)
values ('1fb1c8f2eb9e3ea294e77d016450731d', '98c553b4f6cb45b0b40a5fbf5291a3c9', '任务配置', '0', 1, 'icon-menu3', '/frs/rpttsk/publish/busi?orgType=14', '1', '');

insert into bione_func_info (FUNC_ID, MODULE_ID, FUNC_NAME, UP_ID, ORDER_NO, NAV_ICON, NAV_PATH, FUNC_STS, REMARK)
values ('ae590c78d9e135809456a9314d22417b', '98c553b4f6cb45b0b40a5fbf5291a3c9', '报表填报', '0', 2, 'icon-endorse', '/rpt/frs/rptfill/fillRpt?orgType=14', '1', '');

insert into bione_func_info (FUNC_ID, MODULE_ID, FUNC_NAME, UP_ID, ORDER_NO, NAV_ICON, NAV_PATH, FUNC_STS, REMARK)
values ('333e22c9e90032219ed0a179f27cce63', '98c553b4f6cb45b0b40a5fbf5291a3c9', '报表复核', '0', 3, 'icon-process', '/frs/rptsubmit/submit/busiIndex?orgType=14', '1', '');

insert into bione_func_info (FUNC_ID, MODULE_ID, FUNC_NAME, UP_ID, ORDER_NO, NAV_ICON, NAV_PATH, FUNC_STS, REMARK)
values ('b720db2f6a96303dacdb50238bd92120', '98c553b4f6cb45b0b40a5fbf5291a3c9', '报表审核', '0', 4, 'icon-customer', '/frs/rptsubmit/submit/busiApprIndex?orgType=14', '1', '');

insert into bione_func_info (FUNC_ID, MODULE_ID, FUNC_NAME, UP_ID, ORDER_NO, NAV_ICON, NAV_PATH, FUNC_STS, REMARK)
values ('6a3d896ce16034fdad820795a2714cf4', '98c553b4f6cb45b0b40a5fbf5291a3c9', '申请解锁', '0', 5, 'icon-password1', '/frs/rptfill/reject/busi?orgType=14', '1', '');

insert into bione_func_info (FUNC_ID, MODULE_ID, FUNC_NAME, UP_ID, ORDER_NO, NAV_ICON, NAV_PATH, FUNC_STS, REMARK)
values ('5fc678214280317384c34908031d3268', '98c553b4f6cb45b0b40a5fbf5291a3c9', '查看申请', '0', 6, 'icon-search-02', '/frs/rptfill/reject/rejectView?orgType=14', '1', '');

insert into bione_func_info (FUNC_ID, MODULE_ID, FUNC_NAME, UP_ID, ORDER_NO, NAV_ICON, NAV_PATH, FUNC_STS, REMARK)
values ('80f40e5bae5e3d37bca6f4d34bbf485a', '98c553b4f6cb45b0b40a5fbf5291a3c9', '审批解锁', '0', 7, 'icon-clear', '/frs/rptfill/reject/approve?orgType=14', '1', '');

insert into bione_func_info (FUNC_ID, MODULE_ID, FUNC_NAME, UP_ID, ORDER_NO, NAV_ICON, NAV_PATH, FUNC_STS, REMARK)
values ('8647fa0ed62734ad864639800d581a68', '98c553b4f6cb45b0b40a5fbf5291a3c9', '强制解锁', '0', 8, 'icon-clear', '/frs/rptfill/reject/force?orgType=14', '1', '');

insert into bione_func_info (FUNC_ID, MODULE_ID, FUNC_NAME, UP_ID, ORDER_NO, NAV_ICON, NAV_PATH, FUNC_STS, REMARK)
values ('e6526b5b9d6b3cb68b8821e172bbe239', '98c553b4f6cb45b0b40a5fbf5291a3c9', '报表导出', '0', 9, 'icon-screen', '/rpt/frs/rptexport/index?orgType=14', '1', '');

insert into bione_func_info (FUNC_ID, MODULE_ID, FUNC_NAME, UP_ID, ORDER_NO, NAV_ICON, NAV_PATH, FUNC_STS, REMARK)
values ('0d16795b8b793a95947e6a2298ad4378', '98c553b4f6cb45b0b40a5fbf5291a3c9', '任务管理', '0', 10, 'icon-menu3', '/bione/frs/task/index?orgType=14', '1', '');

insert into bione_func_info (FUNC_ID, MODULE_ID, FUNC_NAME, UP_ID, ORDER_NO, NAV_ICON, NAV_PATH, FUNC_STS, REMARK)
values ('9f61858bf1c0381e8f5a1f3220361c6a', '77f9bd9044d74251acf80ee4247a075e', '任务配置', '0', 1, 'icon-menu3', '/frs/rpttsk/publish/busi?orgType=16', '1', '');

insert into bione_func_info (FUNC_ID, MODULE_ID, FUNC_NAME, UP_ID, ORDER_NO, NAV_ICON, NAV_PATH, FUNC_STS, REMARK)
values ('1ab67a17de1139dcb24f1c3794a6ea1a', '77f9bd9044d74251acf80ee4247a075e', '报表填报', '0', 2, 'icon-endorse', '/rpt/frs/rptfill/fillRpt?orgType=16', '1', '');

insert into bione_func_info (FUNC_ID, MODULE_ID, FUNC_NAME, UP_ID, ORDER_NO, NAV_ICON, NAV_PATH, FUNC_STS, REMARK)
values ('b11f03df60853bb5b669c4af5b5b4798', '77f9bd9044d74251acf80ee4247a075e', '报表复核', '0', 3, 'icon-process', '/frs/rptsubmit/submit/busiIndex?orgType=16', '1', '');

insert into bione_func_info (FUNC_ID, MODULE_ID, FUNC_NAME, UP_ID, ORDER_NO, NAV_ICON, NAV_PATH, FUNC_STS, REMARK)
values ('406755a9171934fda4947ba477819336', '77f9bd9044d74251acf80ee4247a075e', '报表审核', '0', 4, 'icon-customer', '/frs/rptsubmit/submit/busiApprIndex?orgType=16', '1', '');

insert into bione_func_info (FUNC_ID, MODULE_ID, FUNC_NAME, UP_ID, ORDER_NO, NAV_ICON, NAV_PATH, FUNC_STS, REMARK)
values ('eeb4bc55021d3875aaa3318c66ff709f', '77f9bd9044d74251acf80ee4247a075e', '申请解锁', '0', 5, 'icon-password1', '/frs/rptfill/reject/busi?orgType=16', '1', '');

insert into bione_func_info (FUNC_ID, MODULE_ID, FUNC_NAME, UP_ID, ORDER_NO, NAV_ICON, NAV_PATH, FUNC_STS, REMARK)
values ('584ce51cdb9732d5937f010a5b51ccf2', '77f9bd9044d74251acf80ee4247a075e', '查看申请', '0', 6, 'icon-search-02', '/frs/rptfill/reject/rejectView?orgType=16', '1', '');

insert into bione_func_info (FUNC_ID, MODULE_ID, FUNC_NAME, UP_ID, ORDER_NO, NAV_ICON, NAV_PATH, FUNC_STS, REMARK)
values ('bed95ac1812b3741947fcf3a0f0b8df2', '77f9bd9044d74251acf80ee4247a075e', '审批解锁', '0', 7, 'icon-clear', '/frs/rptfill/reject/approve?orgType=16', '1', '');

insert into bione_func_info (FUNC_ID, MODULE_ID, FUNC_NAME, UP_ID, ORDER_NO, NAV_ICON, NAV_PATH, FUNC_STS, REMARK)
values ('faaa1e33ddd03044882b81646529ecf5', '77f9bd9044d74251acf80ee4247a075e', '强制解锁', '0', 8, 'icon-clear', '/frs/rptfill/reject/force?orgType=16', '1', '');

insert into bione_func_info (FUNC_ID, MODULE_ID, FUNC_NAME, UP_ID, ORDER_NO, NAV_ICON, NAV_PATH, FUNC_STS, REMARK)
values ('caabcceb884c3c8e92293443ec494d75', '77f9bd9044d74251acf80ee4247a075e', '报表导出', '0', 9, 'icon-screen', '/rpt/frs/rptexport/index?orgType=16', '1', '');

insert into bione_func_info (FUNC_ID, MODULE_ID, FUNC_NAME, UP_ID, ORDER_NO, NAV_ICON, NAV_PATH, FUNC_STS, REMARK)
values ('c15caa5c3bf63191ad75324ae9e0072d', '77f9bd9044d74251acf80ee4247a075e', '任务管理', '0', 10, 'icon-menu3', '/bione/frs/task/index?orgType=16', '1', '');

/*制度版本*/
insert into FRS_SYSTEM_CFG (SYSTEM_VER_ID, BUSI_TYPE, SYSTEM_NAME, VER_START_DATE, VER_END_DATE, REMARK, CATALOG_ID)
values (2020, '16', '2020年制度', '20200101', '29991231', '', '16_2020');

insert into FRS_SYSTEM_CFG (SYSTEM_VER_ID, BUSI_TYPE, SYSTEM_NAME, VER_START_DATE, VER_END_DATE, REMARK, CATALOG_ID)
values (2020, '14', '2020年制度', '20200101', '29991231', '', '14_2020');

insert into FRS_SYSTEM_CFG (SYSTEM_VER_ID, BUSI_TYPE, SYSTEM_NAME, VER_START_DATE, VER_END_DATE, REMARK, CATALOG_ID)
values (2020, '13', '2020年制度', '20200101', '29991231', '', '13_2020');

insert into FRS_SYSTEM_CFG (SYSTEM_VER_ID, BUSI_TYPE, SYSTEM_NAME, VER_START_DATE, VER_END_DATE, REMARK, CATALOG_ID)
values (2020, '10', '2020年制度', '20200101', '29991231', '', '10_2020');

/*系统参数*/
insert into bione_param_info (PARAM_ID, LOGIC_SYS_NO, PARAM_TYPE_NO, PARAM_VALUE, PARAM_NAME, ORDER_NO, UP_NO, REMARK)
values ('24bbd61f3b8b4060ac960c9af5fa2c49', 'FRS', 'reportorgtype', '10', '存款保险', 11, '0', '');

insert into bione_param_info (PARAM_ID, LOGIC_SYS_NO, PARAM_TYPE_NO, PARAM_VALUE, PARAM_NAME, ORDER_NO, UP_NO, REMARK)
values ('35ba34dbe2c94a5b9402ed602bbca54c', 'FRS', 'reportorgtype', '13', '精准扶贫', 14, '0', '');

insert into bione_param_info (PARAM_ID, LOGIC_SYS_NO, PARAM_TYPE_NO, PARAM_VALUE, PARAM_NAME, ORDER_NO, UP_NO, REMARK)
values ('ff5695d578344cb2b7d11c4623d9b117', 'FRS', 'reportorgtype', '14', '资管产品', 16, '0', '');

insert into bione_param_info (PARAM_ID, LOGIC_SYS_NO, PARAM_TYPE_NO, PARAM_VALUE, PARAM_NAME, ORDER_NO, UP_NO, REMARK)
values ('c9b25b1c47a54446bb152e59f8614f1b', 'FRS', 'reportorgtype', '16', '理财登记', 18, '0', '');

insert into bione_param_info (PARAM_ID, LOGIC_SYS_NO, PARAM_TYPE_NO, PARAM_VALUE, PARAM_NAME, ORDER_NO, UP_NO, REMARK)
values ('2e218bb16a1d4e4fbd34751bf45be664', 'FRS', 'reportorgtype', '15', '理财与信托', 17, '0', '');

insert into bione_param_info (PARAM_ID, LOGIC_SYS_NO, PARAM_TYPE_NO, PARAM_VALUE, PARAM_NAME, ORDER_NO, UP_NO, REMARK)
values ('237cbbfa555149d4a212f0ebc53f7ad9', 'FRS', 'idxTaskType', '10', '存款保险', 6, '0', '');

insert into bione_param_info (PARAM_ID, LOGIC_SYS_NO, PARAM_TYPE_NO, PARAM_VALUE, PARAM_NAME, ORDER_NO, UP_NO, REMARK)
values ('a2239563f6f641f68f545b572cf41d16', 'FRS', 'idxTaskType', '13', '精准扶贫', 5, '0', '');

insert into bione_param_info (PARAM_ID, LOGIC_SYS_NO, PARAM_TYPE_NO, PARAM_VALUE, PARAM_NAME, ORDER_NO, UP_NO, REMARK)
values ('afe264f6bdb2471cad04772db4b05ead', 'FRS', 'idxTaskType', '14', '资管产品', 7, '0', '');

insert into bione_param_info (PARAM_ID, LOGIC_SYS_NO, PARAM_TYPE_NO, PARAM_VALUE, PARAM_NAME, ORDER_NO, UP_NO, REMARK)
values ('b9cfb7bc71a1428fbab1a96ffe401fa8', 'FRS', 'idxTaskType', '16', '理财登记', 8, '0', '');

insert into bione_param_info (PARAM_ID, LOGIC_SYS_NO, PARAM_TYPE_NO, PARAM_VALUE, PARAM_NAME, ORDER_NO, UP_NO, REMARK)
values ('8b00a88bcf3543c3af41bfa957f8cc94', 'FRS', 'socialCreditCode', '91510000204367525F', '社会信用代码', 1, '0', '');

insert into RPT_SYS_VAR_INFO (VAR_ID, VAR_NO, VAR_NM, DEF_TYPE, VAR_TYPE, SOURCE_ID, VAR_VAL, REMARK)
values ('a407f942061447edae67b8a00d3bc645', 'socialCreditCode', '社会信用代码', '02', '02', '1', 'SELECT param_value FROM bione_param_info where param_type_no = ''socialCreditCode'' and param_name = ''社会信用代码''', '');

insert into RPT_SYS_VAR_INFO (VAR_ID, VAR_NO, VAR_NM, DEF_TYPE, VAR_TYPE, SOURCE_ID, VAR_VAL, REMARK)
values ('94b9944a80af44b7b68eee73158403d4', 'financeOrgNo', '金融机构编码', '01', '01', '', '', '');


--基线版本V3.2-2020******************************************************
/*20200817-新增上旬公式*/
insert into RPT_IDX_FORMULA_FUNC (FORMULA_ID, FUNC_TYPE, FORMULA_NM, FORMULA_DISPLAY, REMARK) values ('LastTenEnd', '03', 'LastTenEnd()', '上旬指标值，使用方式LastTenEnd(I(''指标''))', '上旬指标值，使用方式LastTenEnd(I(''指标''))');
/*20200821-审批人改为复核人*/
update RPT_SYS_VAR_INFO set VAR_NM = '复核人' where VAR_NM = '审批人';

/*20200911-增加1104报送币种参数值*/
insert into BIONE_PARAM_TYPE_INFO (PARAM_TYPE_ID, LOGIC_SYS_NO, PARAM_TYPE_NO, PARAM_TYPE_NAME, UP_NO, REMARK)
values ('4028884c747b2cc001747bcb287c0000', 'FRS', 'currency1104', '1104报送币种', '0', '');

insert into bione_param_info (PARAM_ID, LOGIC_SYS_NO, PARAM_TYPE_NO, PARAM_VALUE, PARAM_NAME, ORDER_NO, UP_NO, REMARK)
values ('09232104af9f45dba7b9ed86dd5b5d00', 'FRS', 'currency1104', '001', '人民币', 1, '0', '');

insert into bione_param_info (PARAM_ID, LOGIC_SYS_NO, PARAM_TYPE_NO, PARAM_VALUE, PARAM_NAME, ORDER_NO, UP_NO, REMARK)
values ('533f9c6a449a476c91b71bc891afcf6c', 'FRS', 'currency1104', 'ARS', '阿根廷比索', 4, '0', '');

insert into bione_param_info (PARAM_ID, LOGIC_SYS_NO, PARAM_TYPE_NO, PARAM_VALUE, PARAM_NAME, ORDER_NO, UP_NO, REMARK)
values ('d20d07f2800e4b168e4e8862b7759c24', 'FRS', 'currency1104', 'AUD', '澳元', 5, '0', '');

insert into bione_param_info (PARAM_ID, LOGIC_SYS_NO, PARAM_TYPE_NO, PARAM_VALUE, PARAM_NAME, ORDER_NO, UP_NO, REMARK)
values ('316458b7f3214fe7b3e680137f7a2be8', 'FRS', 'currency1104', 'BRL', '巴西里尔', 6, '0', '');

insert into bione_param_info (PARAM_ID, LOGIC_SYS_NO, PARAM_TYPE_NO, PARAM_VALUE, PARAM_NAME, ORDER_NO, UP_NO, REMARK)
values ('17ca5d715f4945a396581c6050011bc2', 'FRS', 'currency1104', 'CAD', '加元', 9, '0', '');

insert into bione_param_info (PARAM_ID, LOGIC_SYS_NO, PARAM_TYPE_NO, PARAM_VALUE, PARAM_NAME, ORDER_NO, UP_NO, REMARK)
values ('8b45f4c806014436b7228f0f3bff648c', 'FRS', 'currency1104', 'CHF', '瑞士法郎', 15, '0', '');

insert into bione_param_info (PARAM_ID, LOGIC_SYS_NO, PARAM_TYPE_NO, PARAM_VALUE, PARAM_NAME, ORDER_NO, UP_NO, REMARK)
values ('02d25c8d4cee4c8f83198d19ee237c72', 'FRS', 'currency1104', 'EUR', '欧元', 3, '0', '');

insert into bione_param_info (PARAM_ID, LOGIC_SYS_NO, PARAM_TYPE_NO, PARAM_VALUE, PARAM_NAME, ORDER_NO, UP_NO, REMARK)
values ('ea5b0fa65bb74ff4bd3a2a7cb3d3888f', 'FRS', 'currency1104', 'GBP', '英镑', 21, '0', '');

insert into bione_param_info (PARAM_ID, LOGIC_SYS_NO, PARAM_TYPE_NO, PARAM_VALUE, PARAM_NAME, ORDER_NO, UP_NO, REMARK)
values ('205a94a87f5347adb5d3a0c1a108f9bb', 'FRS', 'currency1104', 'HKD', '港元', 7, '0', '');

insert into bione_param_info (PARAM_ID, LOGIC_SYS_NO, PARAM_TYPE_NO, PARAM_VALUE, PARAM_NAME, ORDER_NO, UP_NO, REMARK)
values ('60e9afd73b4f4064989f24d35f950df1', 'FRS', 'currency1104', 'IDR', '印尼盾', 20, '0', '');

insert into bione_param_info (PARAM_ID, LOGIC_SYS_NO, PARAM_TYPE_NO, PARAM_VALUE, PARAM_NAME, ORDER_NO, UP_NO, REMARK)
values ('1ebf5ce246984c6598da6421d45a0e00', 'FRS', 'currency1104', 'INR', '印度卢比', 19, '0', '');

insert into bione_param_info (PARAM_ID, LOGIC_SYS_NO, PARAM_TYPE_NO, PARAM_VALUE, PARAM_NAME, ORDER_NO, UP_NO, REMARK)
values ('6057cdeb23864caaa7d7368606c80e9f', 'FRS', 'currency1104', 'JPY', '日元', 13, '0', '');

insert into bione_param_info (PARAM_ID, LOGIC_SYS_NO, PARAM_TYPE_NO, PARAM_VALUE, PARAM_NAME, ORDER_NO, UP_NO, REMARK)
values ('3f0ee69248984f218df73f8e05fbe278', 'FRS', 'currency1104', 'KRW', '韩元', 8, '0', '');

insert into bione_param_info (PARAM_ID, LOGIC_SYS_NO, PARAM_TYPE_NO, PARAM_VALUE, PARAM_NAME, ORDER_NO, UP_NO, REMARK)
values ('cc4bea2e115f4fd2826251c58d947fca', 'FRS', 'currency1104', 'MXN', '墨西哥比索', 11, '0', '');

insert into bione_param_info (PARAM_ID, LOGIC_SYS_NO, PARAM_TYPE_NO, PARAM_VALUE, PARAM_NAME, ORDER_NO, UP_NO, REMARK)
values ('247eb3b1df73420a8909df11e93f81ce', 'FRS', 'currency1104', 'RUB', '卢布', 10, '0', '');

insert into bione_param_info (PARAM_ID, LOGIC_SYS_NO, PARAM_TYPE_NO, PARAM_VALUE, PARAM_NAME, ORDER_NO, UP_NO, REMARK)
values ('3b8335ffb87a4504a6640fa621d9fac9', 'FRS', 'currency1104', 'SAR', '沙特阿拉伯币', 16, '0', '');

insert into bione_param_info (PARAM_ID, LOGIC_SYS_NO, PARAM_TYPE_NO, PARAM_VALUE, PARAM_NAME, ORDER_NO, UP_NO, REMARK)
values ('a54649a6ca7c4040948b38d6a8c40728', 'FRS', 'currency1104', 'SEK', '瑞典克朗', 14, '0', '');

insert into bione_param_info (PARAM_ID, LOGIC_SYS_NO, PARAM_TYPE_NO, PARAM_VALUE, PARAM_NAME, ORDER_NO, UP_NO, REMARK)
values ('e6af303d968d498191b70235bddf45a7', 'FRS', 'currency1104', 'SGD', '新加坡元', 18, '0', '');

insert into bione_param_info (PARAM_ID, LOGIC_SYS_NO, PARAM_TYPE_NO, PARAM_VALUE, PARAM_NAME, ORDER_NO, UP_NO, REMARK)
values ('17b2ac9828e243f7b7c3d2dea801d78d', 'FRS', 'currency1104', 'TRL', '土耳其里拉', 1, '3b8335ffb87a4504a6640fa621d9fac9', '');

insert into bione_param_info (PARAM_ID, LOGIC_SYS_NO, PARAM_TYPE_NO, PARAM_VALUE, PARAM_NAME, ORDER_NO, UP_NO, REMARK)
values ('19883afdde5c44a69517f9a47a3be99d', 'FRS', 'currency1104', 'USD', '美元', 2, '0', '');

insert into bione_param_info (PARAM_ID, LOGIC_SYS_NO, PARAM_TYPE_NO, PARAM_VALUE, PARAM_NAME, ORDER_NO, UP_NO, REMARK)
values ('1635210d541b4e228fdb710f06d5a8eb', 'FRS', 'currency1104', 'ZAR', '南非兰特', 12, '0', '');

/*20201022-增加跨机构取数公式*/
insert into RPT_IDX_FORMULA_FUNC (FORMULA_ID, FUNC_TYPE, FORMULA_NM, FORMULA_DISPLAY, REMARK) values ('GetOrgData', '03', 'GetOrgData()', '指定机构的指标值，使用方式GetOrgData(I(''指标''),''数据来源机构编号'',''目标机构编号'')', '指定机构的指标值，使用方式GetOrgData(I(''指标''),''数据来源机构编号'',''目标机构编号'')');

