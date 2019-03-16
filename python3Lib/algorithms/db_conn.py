#-*- coding:utf-8 -*-
# author:chen
# datetime:2018/12/17 下午5:00
from sqlalchemy import create_engine
import configparser
import pandas as pd
import os


def DB_Engine(db_conn,db_type):
    # 数据库连接
    engine = create_engine(
        'mysql+mysqlconnector://%s:%s@%s:%s/%s' % (db_conn['USERNAME'], db_conn['PASSWORD'],
                                                   db_conn['HOSTNAME'], db_conn['PORT'], db_conn[db_type]))
    return engine

def fatchData_formCredit(db_conn, applyIds_str, extract_type):
    # 数据提取
    engine = DB_Engine(db_conn,"DATABASE_CREDIT")
    if extract_type == "months_output":
        form_table = "firm_months_output"
    elif extract_type == "downstream_output":
        form_table = "downstream_year_output"

    elif extract_type == "firm_info":
        form_table = "app_firm_info"

    else:
        form_table =""
    sqlstr = "select * from" + "`%s` where  apply_Id = '%s' " %(form_table, applyIds_str)

    extract_data = pd.read_sql(
        sqlstr,
        con=engine
    )
    return extract_data

def fatchData_fromTaxs(db_conn,applyIds_str,data_type):
    """
        函数说明：计算信用评分和授信额度
        输入：

        输出：
    """
    ## 企业数据 -- 来源于credit库
    firm_data = fatchData_formCredit(db_conn, applyIds_str, "firm_info")[["apply_date", "NSRSBH"]]

    #税务数据 -- 来源于源库 taxs
    config = configparser.RawConfigParser()
    config.read(
        os.path.join(os.path.abspath(os.path.dirname(os.getcwd()) + os.path.sep + "."),
                     'resources/conf_db.cfg'))

    dbBussiness_conn = eval(config.get('DATABASE', 'CONN_DBBUSSINESS'))
    engine_taxs = DB_Engine(dbBussiness_conn, "DATABASE_TAXS")

    if data_type == "downstream_year_output":
        sqlstr =(
            "SELECT DATE_FORMAT(kprq,'%Y') AS 'years',",
            "gfmc AS 'downstream_firm_buyer',",
		    "GROUP_CONCAT(DISTINCT gfsbh)  AS 'downstream_firm_buyer_ids',",
            "ROUND(SUM(CASE zfbz WHEN 'Y' THEN je ELSE 0 END),2) AS 'invalid_invoice_amt',",
            "ROUND(SUM(CASE zfbz WHEN 'N' THEN CASE WHEN je>0 THEN  je  END ELSE 0 END),2) AS 'valid_invoice_amt',",  #有效、且为正
            "ROUND(SUM(CASE WHEN je<0 THEN  CASE zfbz WHEN 'N' THEN je END ELSE 0 END),2) AS 'red_invoice_amt',",
            "ROUND(SUM(CASE zfbz WHEN 'N' THEN je ELSE 0 END),2) AS 'transaction_amount'" #营业收入：有效金额 + 红冲金额

            "FROM  `invoice_structure` "
            "WHERE xfsbh = '%s'"
            "AND kprq <= DATE_FORMAT('%s','%%Y-%%m-%%d')"
            "AND type = 'XXFP'"
            "GROUP BY downstream_firm_buyer,years;"%(firm_data["NSRSBH"].values[0],firm_data["apply_date"].values[0])
        #firm_data["NSRSBH"].values[0]
        )

    elif data_type == "downstream_output":
        sqlstr = "downstream_year_output"

    extract_data = pd.read_sql(
        " ".join(sqlstr),
        con=engine_taxs
    )


    return  extract_data


def export_data(db_conn, dataframe, export_type):

    engine = create_engine(
        'mysql+mysqlconnector://%s:%s@%s:%s/%s' % (db_conn['USERNAME'], db_conn['PASSWORD'],
                                                   db_conn['HOSTNAME'], db_conn['PORT'],db_conn['DATABASE']))
    if export_type == "score_result": #指定导入表名
        dataframe.to_sql('score_result',engine,index=False,if_exists="append")
