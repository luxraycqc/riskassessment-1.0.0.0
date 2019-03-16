# ETL使用方法：
## 新建一个存储过程

参考procedure.sql，所有实时处理的存储过程中的参数约定为以下六个：

```
  IN apply_id VARCHAR(50), -- 申请编号
  IN credit_code VARCHAR(50), -- 统一社会信用代码
  IN org_code VARCHAR(50), -- 组织机构
  IN org_name VARCHAR(50), -- 组织名称
  IN tax_id VARCHAR(50), -- 纳税人识别号
  IN reg_no VARCHAR(50))  -- 工商注册号码
```
所有批量处理的存储过程中的参数约定为以下两个：

```
 IN start_time VARCHAR(50), -- 开始时间
 IN end_time VARCHAR(50)) -- 结束时间
```
修改存储过程体，上述参数应该能覆盖绝大部分场景，覆盖不了的单做


## 存储过程的命名
以proc+目标表的表名为开头，结尾加上对应的操作，例如：
proc_table2_insert

## 存储过程配置
存储过程新建完成后，修改配置文件conf_etl.cfg，在 PROCEDURES一栏配置上新建的存储过程名称

```
# 数据库连接
[DATABASE]
CONN = {'HOSTNAME':'localhost','USERNAME':'root','PASSWORD':'123456','DATABASE':'test','CHARSET':'utf8'}
# 实时处理单个公司的数据
[ETL-REALTIME]
PROCEDURES = ['proctest1','proctest2']
# 批量处理多个公司的数据
[ETL-BATCH]
PROCEDURES = ['proctest3']
```
## 运行命令：


* 批量运行所有公司


```
python3 manager_main.py db batch -?
```
```
usage: db batch [-?] [-e END_TIME] [-s START_TIME]

optional arguments:
  -?, --help            show this help message and exit
  -e END_TIME, --end_time END_TIME
  -s START_TIME, --start_time START_TIME
```
后面可选参数有：

```
[-?] 帮助
[-e END_TIME] 结束时间
[-s START_TIME] 开始时间
```
例子：

```
python3 manager_main.py db batch -e 20180910
```
系统会自动运行配置文件中所配置的所有存储过程，查看运行日志是否成功。

* 实时计算单个公司

```
python3 manager_main.py db realtime -?
```
```
usage: db realtime [-?] [-r REG_NO] [-t TAX_ID] [-n ORG_NAME] [-o ORG_CODE]
                   [-c CREDIT_CODE] [-a APPLY_ID]

optional arguments:
  -?, --help            show this help message and exit
  -r REG_NO, --reg_no REG_NO
  -t TAX_ID, --tax_id TAX_ID
  -n ORG_NAME, --org_name ORG_NAME
  -o ORG_CODE, --org_code ORG_CODE
  -c CREDIT_CODE, --credit_code CREDIT_CODE
  -a APPLY_ID, --apply_id APPLY_ID
```
后面可选参数有：

```
[-?] 帮助
[-r REG_NO] 
[-t TAX_ID] 
[-n ORG_NAME] 
[-o ORG_CODE]
[-c CREDIT_CODE] 
[-a APPLY_ID]
```
例子：

```
python3 manager_main.py db realtime -n 北京大学
```
系统会自动运行配置文件中所配置的所有存储过程，查看运行日志是否成功。

# 查看日志 
```
tail /logs/call_proc-xxxx-xx-xx.log
```
可以看到当天运行的日志
#配置
1. 在cfg里配置数据库信息
2. 注意端口、用户权限

#需要启动pysnowflake服务
在命令行中输入```snowflake_start_server```
#在conf_db.cfg里修改数据库配置