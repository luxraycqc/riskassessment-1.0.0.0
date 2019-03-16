-- 陈积铨
    -- 销项发票统计分析 proc_app_sell_invoice_statistics_cjq
DROP PROCEDURE IF EXISTS proc_app_sell_invoice_statistics_cjq; #proc_app_executive_info
DELIMITER $$
CREATE PROCEDURE proc_app_sell_invoice_statistics_cjq(
  IN apply_id VARCHAR(50), -- 申请编号
  IN credit_code VARCHAR(50), -- 统一社会信用代码
  IN org_code VARCHAR(50), -- 组织机构
  IN org_name VARCHAR(50), -- 组织名称
  IN tax_id VARCHAR(50), -- 纳税人识别号
  IN reg_no VARCHAR(50) -- 工商注册号码
  ) 

BEGIN
 INSERT INTO app_sell_invoice_statistics(
			report_id,date,valid_invoice_amt,invalid_invoice_amt,
			red_invoice_amt,valid_invoice_cnt,invalid_invoice_cnt,
			red_invoice_cnt,
			rate_ratio0,rate_ratio3,rate_ratio5,rate_ratio6,
			rate_ratio11,rate_ratio13,rate_ratio17,rate_ratio_mix 
	) #新表
 SELECT     a.applyId,a.date,valid_invoice_amt,invalid_invoice_amt,
			red_invoice_amt,valid_invoice_cnt,invalid_invoice_cnt,
			red_invoice_cnt,
			rate_ratio_0,rate_ratio_3,rate_ratio_5,rate_ratio_6,
			rate_ratio_11,rate_ratio_13,rate_ratio_17,rate_ratio_mix 
FROM tmp_new_mid_sell_invoice_statistics_result a,ods_f_apply_1117 b

WHERE (b.APPLY_ID = CONVERT(apply_id USING utf8) COLLATE utf8_unicode_ci
AND a.date <= CONVERT(b.APPLY_TIME USING utf8) COLLATE utf8_unicode_ci
AND a.applyId = CONVERT(apply_id USING utf8) COLLATE utf8_unicode_ci);

END$$
DELIMITER ;