-- 陈积铨
    -- 销项发票统计分析
	-- 多条数据
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

-- 秦晓琳
    -- 主营商品分析
DROP PROCEDURE IF EXISTS proc_app_product_statistics_qxl; #proc_app_executive_info
DELIMITER $$
CREATE PROCEDURE proc_app_product_statistics_qxl(
  IN apply_id VARCHAR(50), -- 申请编号
  IN credit_code VARCHAR(50), -- 统一社会信用代码
  IN org_code VARCHAR(50), -- 组织机构
  IN org_name VARCHAR(50), -- 组织名称
  IN tax_id VARCHAR(50), -- 纳税人识别号
  IN reg_no VARCHAR(50) -- 工商注册号码
	) 
BEGIN
 INSERT INTO app_product_statistics(report_id,earliest_time,latest_time,invoice_num1,invoice_agg_amt1,
  invoice_agg_ratio1,invoice_num2,invoice_agg_amt2,invoice_agg_ratio2,product_cnt,concentration_ratio)
 SELECT a.APPLY_ID,a.last_invoice_date,a.latest_invoice_date,a.invoice_num_product,a.sales,NULL,NULL,NULL,NULL,NULL,NULL
 FROM mid_product_statistics_qxl a
WHERE (a.APPLY_ID = CONVERT(apply_id USING utf8) COLLATE utf8_unicode_ci);
END$$
DELIMITER ;

    -- 主营商品
	-- 多条数据
DROP PROCEDURE IF EXISTS proc_app_main_product_qxl; #proc_app_executive_info
DELIMITER $$
CREATE PROCEDURE proc_app_main_product_qxl(
  IN apply_id VARCHAR(50), -- 申请编号
  IN credit_code VARCHAR(50), -- 统一社会信用代码
  IN org_code VARCHAR(50), -- 组织机构
  IN org_name VARCHAR(50), -- 组织名称
  IN tax_id VARCHAR(50), -- 纳税人识别号
  IN reg_no VARCHAR(50) -- 工商注册号码
	) 
BEGIN
 INSERT INTO app_main_product(report_id,product_name,unit,sales,ratio)

 SELECT a.APPLY_ID,a.product_name,a.unit,a.product_sales,a.ratio
 FROM mid_main_product_top10_qxl a
  WHERE (a.APPLY_ID = CONVERT(apply_id USING utf8) COLLATE utf8_unicode_ci) ;

END$$
DELIMITER ;
--'001CA36CDEA049AAB1F3700A915FCF16'

--  杜思佳
--  股东信息
DROP PROCEDURE IF EXISTS proc_app_shareholder_info_dsj; #proc_app_executive_info
DELIMITER $$
CREATE PROCEDURE proc_app_shareholder_info_dsj(
  IN apply_id VARCHAR(50), -- 申请编号
  IN credit_code VARCHAR(50), -- 统一社会信用代码
  IN org_code VARCHAR(50), -- 组织机构
  IN org_name VARCHAR(50), -- 组织名称
  IN tax_id VARCHAR(50), -- 纳税人识别号
  IN reg_no VARCHAR(50) -- 工商注册号码
	) 

BEGIN
 INSERT INTO app_shareholder_info(report_id,shareholder_name,shareholder_type,id_type,id_num,hold_ratio,hold_quota) #新表

 SELECT a.applyid,a.shareholder_name,a.shareholder_type,a.id_type,a.id_num,a.hold_ratio,a.hold_quota

 FROM ods_shareholder_info_dsj a

 WHERE  applyid=CONVERT(apply_id USING utf8) COLLATE utf8_unicode_ci;

END$$
DELIMITER ;

--  高管信息
DROP PROCEDURE IF EXISTS proc_app_executive_info_dsj; #proc_app_executive_info
DELIMITER $$
CREATE PROCEDURE proc_app_executive_info_dsj(
  IN apply_id VARCHAR(50), -- 申请编号
  IN credit_code VARCHAR(50), -- 统一社会信用代码
  IN org_code VARCHAR(50), -- 组织机构
  IN org_name VARCHAR(50), -- 组织名称
  IN tax_id VARCHAR(50), -- 纳税人识别号
  IN reg_no VARCHAR(50) -- 工商注册号码
  ) 

BEGIN
 INSERT INTO app_executive_info(report_id,name,position) #新表
 SELECT a.applyid,a.name,a.position
 FROM ods_executive_info_dsj a
 WHERE  a.applyid = CONVERT(apply_id USING utf8) COLLATE utf8_unicode_ci;
END$$
DELIMITER ;

-- 企业信息
DROP PROCEDURE IF EXISTS proc_app_firm_info_dsj; #proc_app_executive_info;
DELIMITER $$
CREATE DEFINER=`root`@`localhost` PROCEDURE `proc_app_firm_info_dsj`(
  IN apply_id VARCHAR(50), -- 申请编号
  IN credit_code VARCHAR(50), -- 统一社会信用代码
  IN org_code VARCHAR(50), -- 组织机构
  IN org_name VARCHAR(50), -- 组织名称
  IN tax_id VARCHAR(50), -- 纳税人识别号
  IN reg_no VARCHAR(50) -- 工商注册号码
	) 
BEGIN
 INSERT INTO app_firm_info(apply_id,report_id,
						 name_of_firm,
 						 uni_social_credit_code,
						
						 organization_code,
						 taxpayer_id,
						 registered_no,
						 type_of_firm,
						 business_sector,
						 legal_representative,
						 business_status,
						 registered_capital,
						 registered_address,
						 business_address,
						 deposit_bank,
						 establish_date,
						 approval_date,
						 business_start,
						 business_end,
						 business_scope,
						 taxpayer_qualification
						 ) #新表
 SELECT a.applyId,a.`_id.$oid`,
						 a.organization_name,
 						 a.uni_social_credit_code,
					
						 a.organization_code,
						 a.taxpayer_id,
						 a.registered_no,
						 a.type_of_firm,
						 a.business_sector,
						 a.legal_representative,
						 a.business_status,
						 a.registered_capital,
						 a.registered_address,
						 a.business_address,
						 a.deposit_bank,
						 a.establish_date,
						 a.approval_date,
						 a.business_start,
						 a.business_end,
						 a.business_scope,
						 a.taxpayer_qualification
 FROM ods_firm_basic_info_dsj a

 WHERE a.applyId=CONVERT(apply_id USING utf8) COLLATE utf8_unicode_ci;
END$$
DELIMITER ;

-- 测试
call proc_app_executive_info_dsj('459582AF31E347B3BB5EE42F9556DFD7','a','b','c','d','e');
call proc_app_shareholder_info_dsj('459582AF31E347B3BB5EE42F9556DFD7','a','b','c','d','e');
call proc_app_main_product_qxl('001CA36CDEA049AAB1F3700A915FCF16','a','b','c','d','e');
--call proc_app_product_statistics_qxl('001CA36CDEA049AAB1F3700A915FCF16','a','b','c','d','e');
call proc_app_sell_invoice_statistics_cjq('001CA36CDEA049AAB1F3700A915FCF16','a','b','c','d','e');
call proc_app_firm_info_dsj('02EFBF1E4B5645619C02C818796C4F46','a','b','c','d','e');
