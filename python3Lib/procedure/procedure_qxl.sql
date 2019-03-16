
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

-- business info
DROP PROCEDURE IF EXISTS proc_app_business_info_qxl;
CREATE DEFINER=`rcqin`@`%` PROCEDURE `proc_app_business_info_qxl`(
  IN apply_id VARCHAR(50), -- 申请编号
  IN credit_code VARCHAR(50), -- 统一社会信用代码
  IN org_code VARCHAR(50), -- 组织机构
  IN org_name VARCHAR(50), -- 组织名称
  IN tax_id VARCHAR(50), -- 纳税人识别号
  IN reg_no VARCHAR(50))
BEGIN
 INSERT INTO app_business_info(report_id,province,city,trade_class1,trade_class2,last_year_sales,last_year_sales_rank,
  machine_type,machine_num,machine_collect_num)

 SELECT a.applyId,a.provinceName,a.cityName,a.hyzlmc,a.hymx,NULL,NULL,NULL,NULL,NULL
 FROM ods_bzbg_business_info_qxl a
WHERE a.applyId = CONVERT(apply_id USING utf8) COLLATE utf8_unicode_ci ;
END

-- 签到信息
DROP PROCEDURE IF EXISTS proc_app_sign_in_detail_qxl;
CREATE DEFINER=`rcqin`@`%` PROCEDURE `proc_app_sign_in_detail_qxl`(
  IN apply_id VARCHAR(50), -- 申请编号
  IN credit_code VARCHAR(50), -- 统一社会信用代码
  IN org_code VARCHAR(50), -- 组织机构
  IN org_name VARCHAR(50), -- 组织名称
  IN tax_id VARCHAR(50), -- 纳税人识别号
  IN reg_no VARCHAR(50))
BEGIN
 INSERT INTO app_sign_in_detail(report_id,machine_code,earliest_time,latest_time,ip_address,mac_address,times)
 SELECT a.applyId,a.machine_code,a.earliest_time,a.latest_time,a.ip_address,a.mac_address,a.times
 FROM tmp_sign_in_detail_qxl a
WHERE a.applyId = CONVERT(apply_id USING utf8) COLLATE utf8_unicode_ci ;
END