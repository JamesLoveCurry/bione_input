load data
CHARACTERSET ZHS16GBK
append
into table acb_error_ytj
fields terminated by X'7C'
trailing nullcols 
(deposit_name CHAR(1024)     "trim(:Deposit_Name)"
,deposit_identity_type_cd    "trim(:deposit_identity_type_cd )"
,deposit_identity_num        "trim(:deposit_identity_num)"
,identity_due_dt             "trim(:identity_due_dt)"
,issue_org_area_cd           "trim(:issue_org_area_cd)"
,deposit_class_cd            "trim(:deposit_class_cd)"
,deposit_country_cd          "trim(:deposit_country_cd)"
,deposit_sex_cd              "trim(:deposit_sex_cd)"
,deposit_post_cd             "trim(:deposit_post_cd)"
,deposit_addr                "trim(:deposit_addr)"
,deposit_tel                 "trim(:deposit_tel)"
,agent_name                  "trim(:agent_name)"
,agent_identity_type_cd      "trim(:agent_identity_type_cd)"
,agent_identity_num          "trim(:agent_identity_num)"
,agent_country_cd            "trim(:agent_country_cd)"
,agent_tel                   "trim(:agent_tel)"
,open_bank_fin_org_cd        "trim(:open_bank_fin_org_cd)"
,bank_acct_num               "trim(:bank_acct_num)"
,bank_acct_kind_cd           "trim(:bank_acct_kind_cd)"
,card_cd CHAR(2018)                    "trim(:card_cd)"
,card_due_dt                 "trim(:card_due_dt)"
,bank_acct_media             "trim(:bank_acct_media)"
,expire_card_dt              "trim(:expire_card_dt)"
,card_stat_cd                "trim(:card_stat_cd)"
,bank_acct_type_cd           "trim(:bank_acct_type_cd)"
,binding_i_acct_num CHAR(2018)         "trim(:binding_i_acct_num)"
,binding_i_fin_org_cd        "trim(:binding_i_fin_org_cd)"
,open_dt                     "trim(:open_dt)"
,expire_dt                   "trim(:expire_dt)"
,acct_stat_cd                "trim(:acct_stat_cd)"
,currency_cd                 "trim(:currency_cd)"
,army_security_card          "trim(:army_security_card)"
,society_security_card       "trim(:society_security_card)"
,audit_result_cd             "trim(:audit_result_cd)"
,no_audit_result_desc        "trim(:no_audit_result_desc)"
,dispose_mode                "trim(:dispose_mode)"
,info_type_cd                "trim(:info_type_cd)"
,open_channal_cd             "trim(:open_channal_cd)"
,remark                      "trim(:remark)"
,column1                     "trim(:column1)"
,column2                     "trim(:column2)"
,column3                     "trim(:column3)"
,column4                     "trim(:column4)"
,column5                     "trim(:column5)"
,error_code                  "trim(:error_code)"
,STATISTICS_DT               "to_date(20200331,'yyyymmdd')"
)
