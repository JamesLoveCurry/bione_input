SELECT DEPOSIT_NAME AS "?????",DEPOSIT_ADDR AS ??,DEPOSIT_POST_CD AS ????,DEPOSIT_TEL AS ??,CASE WHEN DEPOSIT_IDENTITY_TYPE_CD IN ('01','02','03') THEN '1' WHEN DEPOSIT_IDENTITY_TYPE_CD IN ('05') THEN '2' WHEN DEPOSIT_IDENTITY_TYPE_CD IN ('08','09','10','11','12','14') THEN '3' WHEN DEPOSIT_IDENTITY_TYPE_CD IN ('13') THEN '4' WHEN DEPOSIT_IDENTITY_TYPE_CD IN ('04','19') THEN '7' WHEN DEPOSIT_IDENTITY_TYPE_CD IN ('15','16','21','22') THEN '8' ELSE '9' END AS ??????,DEPOSIT_IDENTITY_NUM AS ??????,'313871000007' AS ????????,BANK_ACCT_NUM AS ??,CASE WHEN BANK_ACCT_MEDIA = '01' THEN '1' WHEN BANK_ACCT_MEDIA = '02' THEN '2' WHEN BANK_ACCT_MEDIA = '03' THEN '5' ELSE '2' END AS ????,OPEN_DT AS ????,CARD_DUE_DT AS ????,'1' AS ???????,ISSUE_ORG_AREA_CD AS ??????????,CASE WHEN INFO_TYPE_CD='01' THEN '0' WHEN INFO_TYPE_CD='02' THEN '1' WHEN INFO_TYPE_CD='03' THEN '2' END AS ???? FROM ACB_INDIV_BANK_ACCT_SUMMIT FROM ACB_INDIV_BANK_ACCT_SUMMIT FROM ACB_INDIV_BANK_ACCT_SUMMIT WHERE to_char(STATISTICS_DT,'yyyymmdd')=$V_STATE