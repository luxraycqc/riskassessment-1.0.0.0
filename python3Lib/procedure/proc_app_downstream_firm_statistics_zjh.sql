-- concentration_ratio数据缺失

DROP PROCEDURE IF EXISTS proc_app_downstream_firm_statistics_zjh; #proc_app_downstream_firm_statistics_zjh
DELIMITER $$

CREATE PROCEDURE proc_app_downstream_firm_statistics_zjh(
  IN apply_id VARCHAR(50), -- 申请编号
  IN credit_code VARCHAR(50), -- 统一社会信用代码
  IN org_code VARCHAR(50), -- 组织机构
  IN org_name VARCHAR(50), -- 组织名称
  IN tax_id VARCHAR(50), -- 纳税人识别号
  IN reg_no VARCHAR(50))  -- 工商注册号码

BEGIN

 INSERT INTO app_downstream_firm_statistics(
 			 report_id,downstream_firm_cnt,
 			 total_amt,avg_amt,concentration_ratio,
 			 upstream,stability
 			 ) #app_downstream_firm_statistics
 
 SELECT applyId,num_all_firms,sum_all_firms_transaction,sum_avr_firms_transaction,concentration_ratio,num_identical_firms,level_stability
 
 FROM md_firm_integrity_grant_transaction_statistics_cjq
 
 WHERE applyId <=> apply_id

 UNION ALL 
 
 SELECT applyId,num_all_firms,sum_all_firms_transaction,sum_avr_firms_transaction,concentration_ratio,num_identical_firms,level_stability
 
 FROM md_firm_integrity_grantNot_transaction_statistics_cjq
 
 WHERE applyId <=> apply_id;

END$$

DELIMITER ;