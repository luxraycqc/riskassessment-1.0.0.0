#-*- coding:utf-8 -*-
# author:chen
# datetime:2019/2/20 下午9:11

import os
import time
import sys

import numpy as np
import pandas as pd
pd.set_option('display.max_columns', None)
import configparser

from algorithms.db_conn import fatchData_fromTaxs,export_data

#00134F3002074C89A8D82E34AB194E66  2017-11-03 00:00:00 9132011309352163XP

if __name__ == '__main__':

    config = configparser.RawConfigParser()
    config.read(
        os.path.join(os.path.abspath(os.path.dirname(os.getcwd()) + os.path.sep + "."),
                     'resources/conf_db.cfg'))

    db_conn = eval(config.get('DATABASE', 'CONN_DBCREDIT'))
    APPLYID = "3823DC07E32A4B0ABACA60F045A7AF75"
    TaxDATA = fatchData_fromTaxs(db_conn, APPLYID,"downstream_year_output")
    TaxDATA["applyid"] = APPLYID

    try:
        export_data(db_conn,TaxDATA, "downstream_year_output")
    except Exception as e:
        print(e.with_traceback())  ### 异常报错 --- 待补充


