-- 杜思佳 
-- 有以下存储过程：
-- 股东信息 proc_app_shareholder_info_dsj
-- 高管信息 proc_app_executive_info_dsj
-- 企业信息 proc_app_firm_info_dsj
-- 税控信息 proc_app_tax_ctrl_zjh

-- 股东信息
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
 INSERT INTO app_firm_info(report_id,
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
 SELECT a.applyId,
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
-- 税控信息
DROP PROCEDURE IF EXISTS proc_app_tax_ctrl_zjh; #proc_app_tax_ctrl_zjh
DELIMITER $$

CREATE PROCEDURE proc_app_tax_ctrl_zjh(
  IN apply_id VARCHAR(50), -- 申请编号
  IN credit_code VARCHAR(50), -- 统一社会信用代码
  IN org_code VARCHAR(50), -- 组织机构
  IN org_name VARCHAR(50), -- 组织名称
  IN tax_id VARCHAR(50), -- 纳税人识别号
  IN reg_no VARCHAR(50))  -- 工商注册号码
BEGIN

 INSERT INTO app_tax_ctrl(
     report_id,machine_type,machine_code,
     quota,machine_role,release_date,
     cert_date,sign_in_times,alter_times
     ) #app_tax_ctrl
 
 SELECT   apply_id,machine_type,machine_code,
     quota,machine_role,release_date,
     cert_date,sign_in_times,alter_times 

 FROM   tmp_tax_ctrl_info_dsj
 
 WHERE    tmp_tax_ctrl_info_dsj.apply_id = CONVERT(apply_id USING utf8) COLLATE utf8_unicode_ci ;

END$$


BEGIN

 INSERT INTO app_tax_ctrl(
     report_id,machine_type,machine_code,
     quota,machine_role,release_date,
     cert_date,sign_in_times,alter_times
     ) #app_tax_ctrl
 
 SELECT   apply_id,machine_type,machine_code,
     quota,machine_role,release_date,
     cert_date,sign_in_times,alter_times 

 FROM   tmp_tax_ctrl_info_dsj
 
 WHERE    tmp_tax_ctrl_info_dsj.apply_id = CONVERT(apply_id USING utf8) COLLATE utf8_unicode_ci ;

END$$

DELIMITER ;