-- test1
DROP PROCEDURE IF EXISTS proctest1;
DELIMITER $$
CREATE PROCEDURE proctest1(
  IN apply_id VARCHAR(50), -- 申请编号
  IN credit_code VARCHAR(50), -- 统一社会信用代码
  IN org_code VARCHAR(50), -- 组织机构
  IN org_name VARCHAR(50), -- 组织名称
  IN tax_id VARCHAR(50), -- 纳税人识别号
  IN reg_no VARCHAR(50))  -- 工商注册号码
BEGIN
 INSERT INTO table2(cnt,name) SELECT count(1),name FROM table1
 WHERE name LIKE CONCAT('%', org_name, '%')
 GROUP BY name;
END$$
DELIMITER ;

-- test2
DROP PROCEDURE IF EXISTS proctest2;
DELIMITER $$
CREATE PROCEDURE proctest2(
  IN apply_id VARCHAR(50), -- 申请编号
  IN credit_code VARCHAR(50), -- 统一社会信用代码
  IN org_code VARCHAR(50), -- 组织机构
  IN org_name VARCHAR(50), -- 组织名称
  IN tax_id VARCHAR(50), -- 纳税人识别号
  IN reg_no VARCHAR(50))  -- 工商注册号码
BEGIN
 INSERT INTO table2(cnt,name) SELECT count(1),name FROM table1
 WHERE name LIKE CONCAT('%', org_name, '%')
 GROUP BY name;
END$$
DELIMITER ;



-- test3
DROP PROCEDURE IF EXISTS proctest3;
DELIMITER $$
CREATE PROCEDURE proctest3(
  IN start_time VARCHAR(50), -- 开始时间
  IN end_time VARCHAR(50)) -- 结束时间
BEGIN
 select *  FROM table2;
END$$
DELIMITER ;

--test4
DROP PROCEDURE IF EXISTS proctest4;
DELIMITER $$
CREATE PROCEDURE proctest4(
  IN apply_id VARCHAR(50), -- 申请编号
  IN credit_code VARCHAR(50), -- 统一社会信用代码
  IN org_code VARCHAR(50), -- 组织机构
  IN org_name VARCHAR(50), -- 组织名称
  IN tax_id VARCHAR(50), -- 纳税人识别号
  IN reg_no VARCHAR(50))  -- 工商注册号码
BEGIN
  INSERT INTO table2(
    name
  )
  values(
    apply_id
  );
END$$
DELIMITER ;

