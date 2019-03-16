#!/usr/bin/env python
# -*- coding: utf8 -*-

from flask_script import Manager
from app import create_app
from db_tools import call_proc
#snowflake

DBMANAGER = Manager()
app = create_app()


# apply_id 申请编号
# credit_code 统一社会信用代码
# org_code 组织机构
# org_name 组织名称
# tax_id 纳税人识别号
# reg_no 工商注册号码
@DBMANAGER.option('-a', '--apply_id', dest='apply_id', default=None, required=False)
@DBMANAGER.option('-c', '--credit_code', dest='credit_code', default=None, required=False)
@DBMANAGER.option('-o', '--org_code', dest='org_code', default=None, required=False)
@DBMANAGER.option('-n', '--org_name', dest='org_name', default=None, required=False)
@DBMANAGER.option('-t', '--tax_id', dest='tax_id', default=None, required=False)
@DBMANAGER.option('-r', '--reg_no', dest='reg_no', default=None, required=False)
#@DBMANAGER.option('-id', '--idd', dest='idd', default=None, required=False)
def realtime(apply_id, credit_code, org_code, org_name, tax_id, reg_no):
    print(app.config.get("HELLO"))
    args = [apply_id, credit_code, org_code, org_name, tax_id, reg_no]
    call_proc(args, session='ETL-REALTIME')


# 批量处理所有公司，筛选条件为开始时间、结束时间
@DBMANAGER.option('-s', '--start_time', dest='start_time', default=None, required=False)
@DBMANAGER.option('-e', '--end_time', dest='end_time', default=None, required=False)
def batch(start_time, end_time):
    print(app.config.get("HELLO"))
    args = (start_time, end_time)
    call_proc(args, session='ETL-BATCH')


@DBMANAGER.command
def hello(name):
    print("hello", name)
