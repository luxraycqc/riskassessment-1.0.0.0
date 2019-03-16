DROP PROCEDURE IF EXISTS proc_app_invoice_info_wy; #proc_app_invoice_info_wy
DELIMITER $$

CREATE PROCEDURE proc_app_invoice_info_wy(
  IN apply_id VARCHAR(50), -- 申请编号
  IN credit_code VARCHAR(50), -- 统一社会信用代码
  IN org_code VARCHAR(50), -- 组织机构
  IN org_name VARCHAR(50), -- 组织名称
  IN tax_id VARCHAR(50), -- 纳税人识别号
  IN reg_no VARCHAR(50))  -- 工商注册号码

BEGIN

 INSERT INTO app_invoice_info(report_id,
 			 taxpayer_name,taxpayer_id,
 			 earliest_date,latest_date,
 			 invoice_num) #app_invoice_info

  SELECT	GROUP_CONCAT(DISTINCT b.record_id),GROUP_CONCAT(DISTINCT b.financial_name),a.xfsbh,min(a.kprq),max(a.kprq),count(*) 
  
  FROM  	ods_invoice_structure a,ods_f_qyxx_1117 b
  
  WHERE 	b.business_no = a.xfsbh
  
  AND 		b.record_id <=> CONVERT(apply_id USING utf8) COLLATE utf8_unicode_ci)
  
  GROUP BY	a.xfsbh

END$$

DELIMITER ;